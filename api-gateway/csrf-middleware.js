/**
 * CSRF Protection Middleware for Express
 * Implements synchronizer token pattern with HttpOnly cookies
 * 
 * @requires cookie-parser
 * @requires csurf
 */

const cookieParser = require('cookie-parser');
const csrf = require('csurf');

/**
 * Setup CSRF protection for the application
 * @param {Express} app - Express application instance
 */
function setupCSRFProtection(app) {
  // Parse cookies from request
  // Note: cookieParser can work with or without secret, but secret is recommended for production
  app.use(cookieParser());

  // CSRF protection middleware with HttpOnly cookies
  const csrfProtection = csrf({
    cookie: {
      httpOnly: true,                              // Cannot be accessed by JavaScript
      secure: process.env.NODE_ENV === 'production', // HTTPS only in production
      sameSite: 'strict',                          // CSRF protection against cross-site requests
      maxAge: 3600000                              // 1 hour
    }
  });

  // Route to get CSRF token
  app.get('/api/csrf-token', csrfProtection, (req, res) => {
    res.json({
      csrfToken: req.csrfToken(),
      expiresIn: 3600
    });
  });

  // Apply CSRF protection to all state-changing operations
  // EXCEPT auth routes (login, register) which don't have CSRF token yet
  app.use((req, res, next) => {
    // Skip CSRF for auth routes - users don't have tokens yet
    const csrfExemptPaths = [
      '/api/auth/login',
      '/api/auth/register',
      '/api/auth/forgot-password',
      '/api/auth/reset-password'
    ];
    
    if (csrfExemptPaths.some(path => req.path.includes(path))) {
      return next();
    }
    
    if (['POST', 'PUT', 'DELETE', 'PATCH'].includes(req.method)) {
      csrfProtection(req, res, next);
    } else {
      next();
    }
  });

  // CSRF error handler
  app.use((err, req, res, next) => {
    if (err.code === 'EBADCSRFTOKEN') {
      // CSRF token errors
      console.error('CSRF token validation failed:', {
        path: req.path,
        method: req.method,
        ip: req.ip,
        userAgent: req.get('user-agent')
      });

      res.status(403).json({
        error: 'CSRF token validation failed',
        message: 'Invalid or missing CSRF token'
      });
    } else {
      // Pass other errors to default handler
      next(err);
    }
  });
}

module.exports = setupCSRFProtection;
