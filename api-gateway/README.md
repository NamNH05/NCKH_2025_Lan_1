# 🌐 API Gateway - Tài Liệu Chi Tiết

## Tổng Quan Service

### Tên Service
**API Gateway** (Cổng API Trung Tâm)

### Mục Đích Chính
API Gateway là điểm vào duy nhất cho tất cả requests từ Frontend:
- **Xác thực JWT** - Kiểm tra token hợp lệ
- **Phân luồng requests** - Gửi đến service đúng
- **CORS Support** - Cho phép Frontend kết nối
- **Token Validation** - Verify token hợp lệ
- **Request Logging** - Ghi nhập tất cả requests
- **Error Handling** - Xử lý lỗi từ backend

### Service Này Giải Quyết Bài Toán Gì?
Trong microservices architecture:
- Frontend cần **một endpoint duy nhất** (không cần biết địa chỉ từng service)
- Cần **kiểm tra JWT** trước khi forward request
- Cần **CORS support** để trình duyệt cho phép requests
- Cần **log tất cả requests** cho debugging
- Cần **timeout handling** khi backend chậm
- Cần **forward requests** đến service phù hợp

### Service Này KHÔNG Làm Những Gì?
❌ Không xử lý business logic  
❌ Không lưu trữ dữ liệu  
❌ Không xác thực người dùng (chỉ verify token)  
❌ Không thay đổi request/response body  
❌ Không cache responses

---

## 🏗️ Kiến Trúc Nội Bộ

### Cấu Trúc Luồng Request

```
┌─────────────────────────────────────────────────────────────┐
│                       Frontend (React)                       │
│                    Axios Instance                            │
│              + Auto Add Authorization Header                 │
└───────────────────────────┬─────────────────────────────────┘
                            │
                    POST /api/auth/login
                    GET /api/products
                    POST /api/orders/checkout
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              API Gateway (Express.js:3000)                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. CORS Middleware                                          │
│     ✓ Allow Origin: http://localhost:5173                   │
│     ✓ Credentials: true                                      │
│                                                               │
│  2. JSON Parser                                              │
│     ✓ Parse request body as JSON                            │
│                                                               │
│  3. Request Logger                                           │
│     ✓ Log timestamp, method, path, query params            │
│                                                               │
│  4. JWT Verification Middleware                             │
│     ├─ Public Routes (bypass JWT)                           │
│     │  ├─ POST /api/auth/register                          │
│     │  ├─ POST /api/auth/login                             │
│     │  └─ GET /api/products (no auth required)             │
│     │                                                        │
│     └─ Protected Routes (require JWT)                       │
│        ├─ POST /api/orders/*                               │
│        ├─ GET /api/orders/*                                │
│        └─ Other routes                                      │
│                                                               │
│  5. Service Router                                           │
│     ├─ /api/auth/* → Auth Service (8080)                   │
│     ├─ /api/products/* → Product Service (8081)            │
│     ├─ /api/orders/* → Order Service (8091)                │
│     └─ /api/v1/audits/* → Audit Service (8082)            │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
           ┌────────────────┼────────────────┐
           │                │                │
    Auth Service      Product Service  Order Service
    (8080)            (8081)           (8091)
    PostgreSQL        MySQL            MySQL
```

### Chi Tiết Từng Middleware

#### 1️⃣ CORS Middleware
Cho phép Frontend kết nối:
```javascript
cors({
  origin: [
    'http://localhost:5173',  // Vite dev server
    'http://127.0.0.1:5173',
    'http://localhost:5174',  // Fallback port
    'http://127.0.0.1:5174'
  ],
  credentials: true
})
```

#### 2️⃣ JWT Verification Middleware
```javascript
const verifyToken = (req, res, next) => {
  // Extract token from "Authorization: Bearer token"
  const token = req.headers.authorization?.split(' ')[1];
  
  // Public routes (không cần JWT)
  if (req.path.includes('/auth/')) return next();
  if (req.path.startsWith('/api/products')) return next();
  
  // Protected routes (cần JWT)
  if (!token) {
    return res.status(401).json({ message: 'No token provided' });
  }
  
  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    req.user = decoded;  // Attach user info to request
    next();
  } catch (err) {
    return res.status(401).json({ message: 'Invalid token' });
  }
}
```

#### 3️⃣ Request Forwarder
```javascript
const forwardRequest = async (req, res, targetUrl) => {
  const config = {
    headers: {
      'Content-Type': 'application/json'
    }
  };
  
  // Forward query params
  if (Object.keys(req.query).length > 0) {
    config.params = req.query;
  }
  
  // Forward Authorization header
  if (req.headers.authorization) {
    config.headers.Authorization = req.headers.authorization;
  }
  
  // Forward request based on HTTP method
  try {
    let response;
    switch (req.method) {
      case 'GET': response = await axios.get(targetUrl, config); break;
      case 'POST': response = await axios.post(targetUrl, req.body, config); break;
      case 'PUT': response = await axios.put(targetUrl, req.body, config); break;
      case 'DELETE': response = await axios.delete(targetUrl, config); break;
    }
    res.json(response.data);
  } catch (error) {
    res.status(error.response?.status || 500).json({
      message: 'Internal server error',
      error: error.response?.data
    });
  }
}
```

---

## 🛠️ Công Nghệ Sử Dụng

| Công Nghệ | Phiên Bản | Mục Đích |
|-----------|-----------|---------|
| **Node.js** | 18+ | JavaScript runtime |
| **Express.js** | 4.18.2 | Web framework |
| **Axios** | 1.x | HTTP client (forward requests) |
| **CORS** | 2.8.x | Cross-Origin Resource Sharing |
| **JWT** | 9.x | Token verification |
| **dotenv** | 16.x | Environment variables |

---

## 📁 Cấu Trúc Thư Mục

```
api-gateway/
├── server.js               # Main application
├── package.json            # Dependencies
├── package-lock.json       # Lock file
├── .env                    # Environment config
├── .env.example           # Template
├── express.json           # Express config (optional)
├── start-gateway.bat      # Windows startup script
├── node_modules/          # Dependencies (after npm install)
└── README.md              # This file
```

### File Chính: server.js
```javascript
const express = require('express');
const cors = require('cors');
const axios = require('axios');
const jwt = require('jsonwebtoken');

// 1. CORS Middleware
app.use(cors({
  origin: ['http://localhost:5173', ...],
  credentials: true
}));

// 2. JSON Parser
app.use(express.json());

// 3. Request Logger
app.use((req, res, next) => {
  console.log(`${new Date().toISOString()} - ${req.method} ${req.path}`);
  next();
});

// 4. JWT Verifier
app.use(verifyToken);

// 5. Auth Routes (register, login, validate)
app.post('/api/auth/register', ...);
app.post('/api/auth/login', ...);
app.post('/api/auth/validate', ...);

// 6. Service Routers (forward to specific services)
app.get('/api/products*', ...);    // → Product Service
app.get('/api/orders*', ...);      // → Order Service
app.post('/api/v1/audits*', ...);  // → Audit Service

// 7. Error Handler
app.use((err, req, res, next) => {...});

// 8. Start Server
app.listen(3000, ...);
```

---

## ⚙️ Environment Configuration

### File `.env` (tạo trong `api-gateway/`)

```bash
# Gateway Port
PORT=3000

# Backend Services URLs
BACKEND_URL=http://localhost:8080        # Auth Service default
PRODUCT_SERVICE_URL=http://localhost:8081
ORDER_SERVICE_URL=http://localhost:8091
AUDIT_SERVICE_URL=http://localhost:8082

# JWT Secret (phải trùng với Auth Service)
JWT_SECRET=caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a2df52c
```

### ⚠️ Important: JWT_SECRET PHẢI GIỐNG Auth Service
Nếu khác sẽ không thể verify token!

```
Auth Service: JWT_SECRET=xyz
Gateway: JWT_SECRET=xyz     ✅ Giống → OK
Gateway: JWT_SECRET=abc     ❌ Khác → Token verify fail
```

---

## 📡 API Routes

### 🔐 Authentication Routes (PUBLIC)

#### 1. Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123",
  "fullName": "User Name",
  "phone": "0123456789"
}
```

**Response (201):**
```json
{
  "id": 1,
  "username": "user123",
  "email": "user@example.com",
  "fullName": "User Name",
  "phone": "0123456789",
  "role": "USER",
  "status": "ACTIVE"
}
```

#### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user123",
  "password": "password123"
}
```

**Response (200):**
```json
{
  "id": 1,
  "username": "user123",
  "email": "user@example.com",
  "fullName": "User Name",
  "phone": "0123456789",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "USER",
  "status": "ACTIVE"
}
```

#### 3. Validate Token
```http
POST /api/auth/validate
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200):**
```json
{
  "valid": true,
  "user": {
    "userId": 1,
    "username": "user123",
    "email": "user@example.com"
  }
}
```

---

### 📦 Product Routes (PUBLIC - NO AUTH)

#### Get All Products
```http
GET /api/products?page=1&size=10&keyword=áo&categoryId=1
```

#### Get Product Details
```http
GET /api/products/{productId}
```

#### Create Product (ADMIN ONLY)
```http
POST /api/products
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "T-shirt",
  "price": 99.99,
  "oldPrice": 149.99,
  "categoryId": 1,
  "stock": 100
}
```

#### Update Product (ADMIN ONLY)
```http
PUT /api/products/{productId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "T-shirt Updated",
  "price": 89.99
}
```

#### Delete Product (ADMIN ONLY)
```http
DELETE /api/products/{productId}
Authorization: Bearer <token>
```

---

### 🛒 Order Routes (REQUIRES AUTH)

#### Get User Orders
```http
GET /api/orders/{userId}
Authorization: Bearer <token>
```

#### Add to Cart
```http
POST /api/orders/{userId}/items
Authorization: Bearer <token>
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2,
  "price": 99.99
}
```

#### Checkout
```http
POST /api/orders/checkout
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 99.99
    }
  ],
  "totalAmount": 199.98
}
```

---

### 📊 Audit Routes (INTERNAL)

#### Log Action
```http
POST /api/v1/audits
Authorization: Bearer <token>
Content-Type: application/json

{
  "sourceService": "auth-service",
  "actionType": "CREATE",
  "entityName": "User",
  "entityId": "1",
  "userId": "1",
  "ipAddress": "192.168.1.1",
  "userAgent": "Mozilla/5.0..."
}
```

---

## 🔒 Security

### JWT Token Structure

```json
{
  "userId": 1,
  "username": "user123",
  "email": "user@example.com",
  "iat": 1704988800,    // Issued at
  "exp": 1705075200     // Expires at
}
```

### Token Lifespan
- **Issued**: Khi user login
- **Valid**: 24 giờ (có thể config trong Auth Service)
- **Invalid**: Hết hạn hoặc logout

### Authorization Header Format
```
Authorization: Bearer <token>

Ví dụ:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoic3lzYWRtaW4iLCJlbWFpbCI6ImFkbWluQGV4YW1wbGUuY29tIiwiaWF0IjoxNzA0OTg4ODAwLCJleHAiOjE3MDUwNzUyMDB9.xxxxx
```

### Public vs Protected Routes

#### Public (Không cần JWT)
- ✅ `POST /api/auth/register`
- ✅ `POST /api/auth/login`
- ✅ `GET /api/products`
- ✅ `GET /api/products/{id}`

#### Protected (Cần JWT)
- ❌ `POST /api/products` (admin)
- ❌ `PUT /api/products/{id}` (admin)
- ❌ `DELETE /api/products/{id}` (admin)
- ❌ `POST /api/orders/*` (user)
- ❌ `GET /api/orders/{userId}` (user)

---

## 🚀 Cách Chạy

### 1. Installation
```bash
cd api-gateway
npm install
```

### 2. Environment Setup
```bash
# Copy from .env.example (nếu có)
cp .env.example .env

# Hoặc tạo .env mới
echo PORT=3000 > .env
echo BACKEND_URL=http://localhost:8080 >> .env
echo JWT_SECRET=caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a7df52c >> .env
```

### 3. Development Mode
```bash
# Watch mode with auto-restart
npm run dev

# Hoặc
npm start
```

### 4. Production Mode
```bash
# Just start
npm start
```

### 5. Windows Batch Script
```bash
# Double-click start-gateway.bat
start-gateway.bat
```

### 6. Verify Gateway Running
```bash
curl http://localhost:3000/health

# Expected response:
# { "status": "API Gateway running", "timestamp": "2026-01-11T..." }
```

---

## 🔗 Liên Kết Với Services Khác

### Service Map

| Route | Service | Port | Xử Lý |
|-------|---------|------|-------|
| `/api/auth/*` | Auth Service | 8080 | JWT generation, user login/register |
| `/api/products*` | Product Service | 8081 | Catalog, search, filter |
| `/api/orders*` | Order Service | 8091 | Cart, checkout, order management |
| `/api/carts*` | Order Service | 8091 | Shopping cart operations |
| `/api/v1/audits*` | Audit Service | 8082 | Action logging |

### Request Flow Example: Login

```
Frontend (React)
  │
  └─ POST /api/auth/login
       │ username: "user123"
       │ password: "password123"
       │
       ▼
     Gateway (3000)
       │
       ├─ Verify JWT? → Skip (auth route)
       │
       └─ Forward to Auth Service (8080)
           │
           └─ POST /api/auth/login
                │ Check credentials
                │ Hash password
                │ Generate JWT
                │
                ▼
              Response: { token, id, username, ... }
       │
       ▼
     Gateway Returns Response
       │
       ▼
Frontend Stores Token in localStorage
```

### Inter-Service Communication

Gateway **KHÔNG call other services directly**. Gateway chỉ **forward requests** từ Frontend.

Nếu backend services cần nói chuyện nhau:
- Auth Service → gọi Audit Service để log user login
- Product Service → có thể gọi Audit Service để log product changes

Điều này được xử lý **trong backend service**, không qua gateway.

---

## 🐛 Lỗi Thường Gặp

### 1. CORS Error: Access-Control-Allow-Origin

**Lỗi:**
```
Access to XMLHttpRequest at 'http://localhost:3000/api/products' 
from origin 'http://localhost:5173' has been blocked by CORS policy
```

**Nguyên nhân:**
- Frontend port không trong CORS whitelist
- CORS middleware không được kích hoạt

**Fix:**
```javascript
app.use(cors({
  origin: ['http://localhost:5173'],  // Add your port
  credentials: true
}));
```

---

### 2. 401 Unauthorized: Invalid Token

**Lỗi:**
```json
{
  "message": "Invalid token",
  "error": "jwt malformed"
}
```

**Nguyên nhân:**
- Token hết hạn
- JWT_SECRET khác với Auth Service
- Token format sai

**Fix:**
```bash
# 1. Check JWT_SECRET in .env
echo JWT_SECRET=...

# 2. Verify token from Auth Service login response
# 3. Ensure Bearer prefix in header
Authorization: Bearer eyJhbGc...
```

---

### 3. 503 Service Unavailable

**Lỗi:**
```json
{
  "message": "Internal server error",
  "error": {
    "code": 503,
    "message": "connect ECONNREFUSED 127.0.0.1:8080"
  }
}
```

**Nguyên nhân:**
- Backend service không running
- BACKEND_URL sai
- Port không khớp

**Fix:**
```bash
# 1. Check if Auth Service is running on port 8080
netstat -ano | findstr :8080

# 2. Verify .env URLs
cat .env

# 3. Start Auth Service first
cd backend/auth-service/Nckh-Lu-n
mvn spring-boot:run
```

---

### 4. 404 Route Not Found

**Lỗi:**
```json
{
  "message": "Route not found",
  "path": "/api/unknown"
}
```

**Nguyên nhân:**
- Route không tồn tại
- Typo trong endpoint

**Fix:**
- Check [API Routes](#-api-routes) section
- Verify endpoint spelling

---

### 5. Network Timeout

**Lỗi:**
```
Error: timeout of 5000ms exceeded
```

**Nguyên nhân:**
- Backend service quá chậm
- Query param tìm kiếm data lớn
- Database lock

**Fix:**
```bash
# 1. Check backend service logs
tail -f backend/auth-service/*/log.txt

# 2. Increase timeout (if needed in axios)
axios.defaults.timeout = 10000;  // 10 seconds
```

---

## 📋 Developer Checklist

Khi làm việc với API Gateway:

### Setup
- [ ] Node.js 18+ installed
- [ ] `npm install` completed
- [ ] `.env` file created
- [ ] JWT_SECRET set correctly
- [ ] BACKEND_URL points to running service

### Development
- [ ] `npm start` or `npm run dev` started
- [ ] Gateway listening on port 3000
- [ ] `/health` endpoint returns 200
- [ ] Can reach backend services

### Testing
- [ ] Register new user works
- [ ] Login returns JWT token
- [ ] Token validation endpoint works
- [ ] Protected routes require Authorization header
- [ ] CORS works for frontend requests

### Debugging
- [ ] Check gateway logs for request details
- [ ] Verify .env variables are correct
- [ ] Test with curl or Postman
- [ ] Check backend service is running
- [ ] Verify JWT_SECRET matches

### Production
- [ ] Remove console.logs (optional)
- [ ] Set NODE_ENV=production
- [ ] Test all routes before deploy
- [ ] Ensure all service URLs are correct
- [ ] Check firewall rules for port 3000
- [ ] Monitor gateway logs

---

## 📚 Tài Liệu Khác

- [Auth Service Documentation](../auth-service/Nckh-Lu-n/README.md)
- [Product Service Documentation](../product-service/Tien/Tien/README.md)
- [Order Service Documentation](../order-service/Nckh-C-ng/README.md)
- [Frontend Web Client Documentation](../../frontend/web-client/README.md)
