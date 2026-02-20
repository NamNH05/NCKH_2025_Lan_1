const express = require('express');
const cors = require('cors');
const axios = require('axios');
const jwt = require('jsonwebtoken');
const setupCSRFProtection = require('./csrf-middleware');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;
const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:8080';
const JWT_SECRET = process.env.JWT_SECRET;

// Middleware
app.use(cors({
  origin: ['http://localhost:5173', 'http://127.0.0.1:5173', 'http://localhost:5174', 'http://127.0.0.1:5174'],
  credentials: true
}));
app.use(express.json());

// Setup CSRF Protection
setupCSRFProtection(app);

// Middleware để log requests
app.use((req, res, next) => {
  console.log(`${new Date().toISOString()} - ${req.method} ${req.path}`);
  if (Object.keys(req.query).length > 0) {
    console.log('  Query Params:', req.query);
  }
  next();
});

// JWT Verification Middleware
const verifyToken = (req, res, next) => {
  const token = req.headers.authorization?.split(' ')[1]; // Extract token from "Bearer token"
  
  // Auth routes không cần JWT
  if (req.path.includes('/auth/')) {
    return next();
  }
  
  // Product routes không cần JWT (public access)
  if (req.path.startsWith('/api/products')) {
    return next();
  }
  
  // Các routes khác cần JWT
  if (!token) {
    return res.status(401).json({ message: 'No token provided' });
  }

  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    req.user = decoded;
    console.log('Token verified for user:', decoded.username);
  } catch (err) {
    console.log('Token verification failed:', err.message);
    return res.status(401).json({ message: 'Invalid token' });
  }
  
  next();
};

app.use(verifyToken);

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'API Gateway running', timestamp: new Date() });
});

// Auth routes - Register
app.post('/api/auth/register', async (req, res) => {
  try {
    console.log('Forwarding register request to backend');
    const response = await axios.post(`${BACKEND_URL}/api/auth/register`, req.body);
    res.json(response.data);
  } catch (error) {
    console.error('Register error:', error.response?.data || error.message);
    res.status(error.response?.status || 500).json({
      message: error.response?.data?.message || 'Registration failed',
      error: error.response?.data || error.message
    });
  }
});

// Auth routes - Login
app.post('/api/auth/login', async (req, res) => {
  try {
    console.log('Forwarding login request to backend');
    const response = await axios.post(`${BACKEND_URL}/api/auth/login`, req.body);
    
    // Backend returns: { id, token, username, email, fullName, phone, role, status }
    const { id, token, username, email, fullName, phone, role, status } = response.data;
    
    // Forward the response directly with id included
    res.json({
      id,
      token,
      username,
      email,
      fullName,
      phone,
      role,
      status,
      message: 'Login successful'
    });
  } catch (error) {
    console.error('Login error:', error.response?.data || error.message);
    res.status(error.response?.status || 401).json({
      message: error.response?.data?.message || 'Invalid credentials',
      error: error.response?.data || error.message
    });
  }
});

// Auth routes - Validate Token
app.post('/api/auth/validate', async (req, res) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    
    if (!token) {
      return res.status(401).json({ message: 'No token provided' });
    }

    const decoded = jwt.verify(token, JWT_SECRET);
    res.json({
      valid: true,
      user: decoded,
      message: 'Token is valid'
    });
  } catch (error) {
    console.error('Token validation error:', error.message);
    res.status(401).json({
      valid: false,
      message: error.message
    });
  }
});

// Generic route forwarder with intelligent routing
const forwardRequest = async (req, res, targetUrl, stripApiPrefix = true) => {
  try {
    // Get the path without query string
    let path = req.path; // /api/products/purchase/1
    console.log(`[DEBUG-1] Original req.path: ${path}`);
    console.log(`[DEBUG-1] Original req.query: ${JSON.stringify(req.query)}`);
    console.log(`[DEBUG-1] stripApiPrefix: ${stripApiPrefix}`);
    
    // Strip /api if needed
    if (stripApiPrefix && path.startsWith('/api')) {
      path = path.substring(4); // Remove '/api'
      console.log(`[DEBUG-2] After stripping /api: ${path}`);
    }
    
    const config = {
      headers: {
        'Content-Type': 'application/json'
      }
    };
    
    // Add query params if present
    if (Object.keys(req.query).length > 0) {
      config.params = req.query;
      console.log(`[DEBUG-3] Added params: ${JSON.stringify(req.query)}`);
    }
    
    // Forward Authorization header if present
    if (req.headers.authorization) {
      config.headers.Authorization = req.headers.authorization;
    }
    
    const fullUrl = `${targetUrl}${path}`;
    console.log(`[FORWARD] ${req.method} ${req.url} -> ${fullUrl}`);
    
    let response;
    switch (req.method.toUpperCase()) {
      case 'GET':
        response = await axios.get(fullUrl, config);
        break;
      case 'POST':
        console.log(`[POST-DEBUG] Sending POST to: ${fullUrl}`);
        console.log(`[POST-DEBUG] Body:`, JSON.stringify(req.body));
        console.log(`[POST-DEBUG] Params:`, config.params);
        response = await axios.post(fullUrl, req.body, config);
        console.log(`[POST-RESPONSE] Response from ${fullUrl}:`, JSON.stringify(response.data));
        break;
      case 'PUT':
        response = await axios.put(fullUrl, req.body, config);
        break;
      case 'DELETE':
        response = await axios.delete(fullUrl, config);
        break;
      default:
        return res.status(405).json({ message: 'Method not allowed' });
    }
    
    console.log(`[SUCCESS] ${req.method} ${req.url} - Status: ${response.status}`);
    res.json(response.data);
  } catch (error) {
    console.error(`[ERROR] ${req.method} ${req.path}:`, {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message
    });
    res.status(error.response?.status || 500).json({
      message: 'Internal server error',
      error: {
        code: error.response?.status || 500,
        message: error.response?.data?.message || error.message
      }
    });
  }
};

// Product Service Routes - Keep /api prefix since product service expects it
// Match all GET /api/products* requests (with any sub-path)
app.get('/api/products*', async (req, res) => {
  const PRODUCT_SERVICE_URL = process.env.PRODUCT_SERVICE_URL || 'http://localhost:8081';
  return forwardRequest(req, res, PRODUCT_SERVICE_URL, false); // Don't strip /api
});

app.post('/api/products*', async (req, res) => {
  const PRODUCT_SERVICE_URL = process.env.PRODUCT_SERVICE_URL || 'http://localhost:8081';
  return forwardRequest(req, res, PRODUCT_SERVICE_URL, false); // Don't strip /api
});

app.put('/api/products*', async (req, res) => {
  const PRODUCT_SERVICE_URL = process.env.PRODUCT_SERVICE_URL || 'http://localhost:8081';
  return forwardRequest(req, res, PRODUCT_SERVICE_URL, false); // Don't strip /api
});

app.delete('/api/products*', async (req, res) => {
  const PRODUCT_SERVICE_URL = process.env.PRODUCT_SERVICE_URL || 'http://localhost:8081';
  return forwardRequest(req, res, PRODUCT_SERVICE_URL, false); // Don't strip /api
});

// Order Service Routes - Keep /api prefix since order service expects it
// Match all /api/orders* requests (with any sub-path)
app.get('/api/orders*', async (req, res) => {
  const ORDER_SERVICE_URL = process.env.ORDER_SERVICE_URL || 'http://localhost:8091';
  return forwardRequest(req, res, ORDER_SERVICE_URL, false); // Don't strip /api
});

app.post('/api/orders*', async (req, res) => {
  const ORDER_SERVICE_URL = process.env.ORDER_SERVICE_URL || 'http://localhost:8091';
  return forwardRequest(req, res, ORDER_SERVICE_URL, false); // Don't strip /api
});

app.put('/api/orders*', async (req, res) => {
  const ORDER_SERVICE_URL = process.env.ORDER_SERVICE_URL || 'http://localhost:8091';
  return forwardRequest(req, res, ORDER_SERVICE_URL, false); // Don't strip /api
});

app.delete('/api/orders*', async (req, res) => {
  const ORDER_SERVICE_URL = process.env.ORDER_SERVICE_URL || 'http://localhost:8091';
  return forwardRequest(req, res, ORDER_SERVICE_URL, false); // Don't strip /api
});

// Cart Service Routes - Keep /api prefix (carts are in order-service too)
// Match all /api/carts* requests (with any sub-path)
app.get('/api/carts*', async (req, res) => {
  const ORDER_SERVICE_URL = process.env.ORDER_SERVICE_URL || 'http://localhost:8091';
  return forwardRequest(req, res, ORDER_SERVICE_URL, false); // Don't strip /api
});

app.post('/api/carts*', async (req, res) => {
  const ORDER_SERVICE_URL = process.env.ORDER_SERVICE_URL || 'http://localhost:8091';
  return forwardRequest(req, res, ORDER_SERVICE_URL, false); // Don't strip /api
});

app.put('/api/carts*', async (req, res) => {
  const ORDER_SERVICE_URL = process.env.ORDER_SERVICE_URL || 'http://localhost:8091';
  return forwardRequest(req, res, ORDER_SERVICE_URL, false); // Don't strip /api
});

app.delete('/api/carts*', async (req, res) => {
  const ORDER_SERVICE_URL = process.env.ORDER_SERVICE_URL || 'http://localhost:8091';
  return forwardRequest(req, res, ORDER_SERVICE_URL, false); // Don't strip /api
});

// ===== AUDIT SERVICE ROUTES =====
app.post('/api/v1/audits*', async (req, res) => {
  const AUDIT_SERVICE_URL = process.env.AUDIT_SERVICE_URL || 'http://localhost:8082';
  return forwardRequest(req, res, AUDIT_SERVICE_URL, false);
});

app.get('/api/v1/audits*', async (req, res) => {
  const AUDIT_SERVICE_URL = process.env.AUDIT_SERVICE_URL || 'http://localhost:8082';
  return forwardRequest(req, res, AUDIT_SERVICE_URL, false);
});

// ===== REVENUE ROUTES (Order Service) =====
app.get('/api/v1/revenue*', async (req, res) => {
  const ORDER_SERVICE_URL = process.env.ORDER_SERVICE_URL || 'http://localhost:8091';
  return forwardRequest(req, res, ORDER_SERVICE_URL, false);
});

// Generic routes for other /api/* requests - catch-all (DO NOT strip /api for backend)
app.all('/api/*', async (req, res) => {
  return forwardRequest(req, res, BACKEND_URL, false); // Don't strip /api - backend expects it
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({ message: 'Route not found', path: req.path });
});

// Error handler
app.use((err, req, res, next) => {
  console.error('Error:', err);
  res.status(500).json({
    message: 'Internal server error',
    error: err.message
  });
});

// Start server
app.listen(PORT, () => {
  console.log(`
╔══════════════════════════════════════════╗
║        API Gateway Started               ║
║   Port: ${PORT}                              ║
║   Backend: ${BACKEND_URL}              ║
║   Product Service: ${process.env.PRODUCT_SERVICE_URL || 'http://localhost:8081'}     ║
╚══════════════════════════════════════════╝
  `);
});

module.exports = app;
