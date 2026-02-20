# ✅ Phần 9 – Testing & Verification

---

## 9.1 Tổng Quan Testing

**3 cấp độ test:**
1. **Connectivity Test** - Services hoạt động không?
2. **API Test** - API endpoints trả về response đúng?
3. **Integration Test** - Các services tương tác đúng?

---

## 9.2 Connectivity Test

### ✅ **Test Docker Containers**

```bash
# List all containers
docker ps

# Expected output (tất cả phải "Up"):
# postgres-auth-db      postgres:15     Up 5 minutes
# mysql-product-db      mysql:8.0       Up 5 minutes
# sqlserver-order-db    mssql/server    Up 5 minutes
# redis-cache           redis:7         Up 5 minutes
```

### ✅ **Test Database Connections**

```bash
# PostgreSQL
psql -U auth_user -d nckh_auth -h localhost -c "SELECT 1;"
# Expected: 1

# MySQL
mysql -u product_user -p -h localhost -D shopquanao -c "SELECT 1;"
# Password: product@123456
# Expected: 1

# SQL Server (nếu có sqlcmd)
sqlcmd -S localhost -U sa -P SqlServer@2026 -Q "SELECT 1;"
# Expected: 1

# Redis
redis-cli -h localhost ping
# Expected: PONG
```

---

### ✅ **Test Backend Services (Health Check)**

```bash
# Auth Service
curl http://localhost:8001/health
# Expected: {"status":"UP"} hoặc tương tự

# Product Service
curl http://localhost:8003/health

# Order Service
curl http://localhost:8002/health

# Audit Service
curl http://localhost:8004/health

# API Gateway
curl http://localhost:3000/health
# Expected: {"status":"API Gateway running"}
```

---

### ✅ **Test Frontend Access**

```bash
# Mở browser
http://localhost:5173

# Expected: Login page hoặc home page load successfully
```

---

## 9.3 API Testing (Postman)

### 📍 **Tài Khoản Test**

```
Admin:
  Username: sysadmin
  Password: 1234
  
User:
  Username: 23810310082
  Password: 123456
```

### 🔓 **1. Test Login API**

**Endpoint:**
```
POST http://localhost:3000/api/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "sysadmin",
  "password": "1234"
}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "sysadmin",
  "email": "sysadmin@system.local",
  "fullName": "System Admin",
  "role": "ADMIN",
  "status": "ACTIVE"
}
```

**Verify:**
- ✅ Status code = 200
- ✅ Response có token field
- ✅ Role = ADMIN hoặc USER

---

### 📦 **2. Test Product API**

**Get All Products:**
```
GET http://localhost:3000/api/products
Accept: application/json
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Áo thun basic trắng",
    "price": 89000,
    "category": "Áo thun",
    "stock": 100,
    "status": "ACTIVE"
  },
  ...
]
```

---

### 🛒 **3. Test Order API (Requires JWT)**

**Get User Orders:**
```
GET http://localhost:3000/api/orders
Authorization: Bearer {TOKEN}
Accept: application/json
```

**Expected Response (200 OK):**
```json
{
  "orders": [
    {
      "id": 1,
      "orderNumber": "ORD-001",
      "totalPrice": 289000,
      "status": "PENDING",
      "createdAt": "2024-01-16T10:00:00Z"
    }
  ]
}
```

---

### 👤 **4. Test User API (Requires JWT)**

**Get User Profile:**
```
GET http://localhost:3000/api/auth/me
Authorization: Bearer {TOKEN}
Accept: application/json
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "username": "sysadmin",
  "email": "sysadmin@system.local",
  "fullName": "System Admin",
  "role": "ADMIN"
}
```

---

## 9.4 Testing Scenarios

### 📋 **Scenario 1: Complete User Journey**

```
1. Người dùng đăng ký
   POST /api/auth/register
   {"username": "newuser", "password": "password123", "email": "user@example.com"}
   → Expected: 201 Created

2. Đăng nhập
   POST /api/auth/login
   {"username": "newuser", "password": "password123"}
   → Expected: 200, nhận JWT token

3. Lấy danh sách sản phẩm
   GET /api/products
   → Expected: 200, danh sách sản phẩm

4. Xem chi tiết sản phẩm
   GET /api/products/{id}
   → Expected: 200, chi tiết sản phẩm

5. Thêm vào giỏ hàng
   POST /api/cart/items
   Authorization: Bearer {token}
   {"productId": 1, "quantity": 2}
   → Expected: 200, item added

6. Tạo đơn hàng
   POST /api/orders
   Authorization: Bearer {token}
   {"items": [{...}], "shippingAddress": "..."}
   → Expected: 201, order created

7. Xem chi tiết đơn hàng
   GET /api/orders/{id}
   Authorization: Bearer {token}
   → Expected: 200, order details
```

---

### 🔓 **Scenario 2: Admin Operations**

```
1. Đăng nhập với tài khoản admin
   POST /api/auth/login
   {"username": "sysadmin", "password": "1234"}
   → Expected: 200, admin token

2. Tạo sản phẩm mới
   POST /api/products
   Authorization: Bearer {admin_token}
   {"name": "New Product", "price": 100000, ...}
   → Expected: 201

3. Cập nhật sản phẩm
   PUT /api/products/{id}
   Authorization: Bearer {admin_token}
   {"name": "Updated Product", "price": 120000}
   → Expected: 200

4. Xem tất cả đơn hàng
   GET /api/orders/all
   Authorization: Bearer {admin_token}
   → Expected: 200, list all orders

5. Cập nhật trạng thái đơn hàng
   PATCH /api/orders/{id}/status
   Authorization: Bearer {admin_token}
   {"status": "SHIPPED"}
   → Expected: 200
```

---

## 9.5 Error Scenarios

### ❌ **Test Error Cases**

**1. Invalid Credentials:**
```
POST /api/auth/login
{"username": "wrong_user", "password": "wrong_pass"}
→ Expected: 401 Unauthorized
→ Response: {"message": "Invalid username or password"}
```

**2. Missing JWT Token:**
```
GET /api/orders
(no Authorization header)
→ Expected: 401 Unauthorized
→ Response: {"message": "No token provided"}
```

**3. Invalid JWT Token:**
```
GET /api/orders
Authorization: Bearer invalid_token_123
→ Expected: 401 Unauthorized
→ Response: {"message": "Invalid token"}
```

**4. Product Not Found:**
```
GET /api/products/99999
→ Expected: 404 Not Found
→ Response: {"message": "Product not found"}
```

**5. Insufficient Stock:**
```
POST /api/cart/items
{"productId": 1, "quantity": 1000}
→ Expected: 400 Bad Request
→ Response: {"message": "Insufficient stock"}
```

---

## 9.6 Performance Test

### ⚡ **Load Testing (Simple)**

```bash
# Sử dụng Apache Bench (ab)
ab -n 100 -c 10 http://localhost:3000/api/products

# n = 100 requests
# c = 10 concurrent
# Expected: < 2 seconds, 0 failed
```

---

### ⏱️ **Response Time Baseline**

| Endpoint | Expected Time | Acceptable |
|----------|---------------|---------  |
| GET /api/products | < 200ms | < 500ms |
| POST /api/auth/login | < 500ms | < 1000ms |
| POST /api/orders | < 1000ms | < 2000ms |
| GET /api/orders | < 500ms | < 1000ms |

---

## 9.7 Postman Collection (Template)

**Tạo file: `docs/postman/NCKH.postman_collection.json`**

```json
{
  "info": {
    "name": "NCKH E-Commerce",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "url": "{{base_url}}/api/auth/login",
            "body": {
              "raw": "{\n  \"username\": \"sysadmin\",\n  \"password\": \"1234\"\n}"
            }
          }
        },
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "url": "{{base_url}}/api/auth/register",
            "body": {
              "raw": "{\n  \"username\": \"newuser\",\n  \"password\": \"password123\",\n  \"email\": \"user@example.com\"\n}"
            }
          }
        }
      ]
    },
    {
      "name": "Products",
      "item": [
        {
          "name": "Get All Products",
          "request": {
            "method": "GET",
            "url": "{{base_url}}/api/products"
          }
        },
        {
          "name": "Get Product by ID",
          "request": {
            "method": "GET",
            "url": "{{base_url}}/api/products/1"
          }
        }
      ]
    },
    {
      "name": "Orders",
      "item": [
        {
          "name": "Create Order",
          "request": {
            "method": "POST",
            "url": "{{base_url}}/api/orders",
            "header": [
              {"key": "Authorization", "value": "Bearer {{jwt_token}}"}
            ],
            "body": {
              "raw": "{\n  \"items\": [{\"productId\": 1, \"quantity\": 2}],\n  \"shippingAddress\": \"123 Main St\"\n}"
            }
          }
        },
        {
          "name": "Get My Orders",
          "request": {
            "method": "GET",
            "url": "{{base_url}}/api/orders",
            "header": [
              {"key": "Authorization", "value": "Bearer {{jwt_token}}"}
            ]
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:3000"
    },
    {
      "key": "jwt_token",
      "value": ""
    }
  ]
}
```

---

## 9.8 Manual Testing Checklist

- [ ] **Database connectivity**
  - [ ] PostgreSQL connects
  - [ ] MySQL connects
  - [ ] SQL Server connects
  - [ ] Redis connects

- [ ] **Backend Services**
  - [ ] Auth Service startup
  - [ ] Product Service startup
  - [ ] Order Service startup
  - [ ] Audit Service startup

- [ ] **API Gateway**
  - [ ] Gateway starts
  - [ ] Health check responds

- [ ] **Frontend**
  - [ ] Frontend loads
  - [ ] Login page appears

- [ ] **Authentication**
  - [ ] Login with admin works
  - [ ] Login with user works
  - [ ] JWT token returned
  - [ ] Invalid credentials rejected

- [ ] **Products**
  - [ ] Product list loads
  - [ ] Product detail loads
  - [ ] Search works
  - [ ] Filter works

- [ ] **Orders**
  - [ ] Can create order
  - [ ] Can view orders
  - [ ] Order status updates

- [ ] **Error Handling**
  - [ ] Invalid token rejected
  - [ ] Missing required fields handled
  - [ ] Database errors handled gracefully

---

## 9.9 Logs & Debugging

### 📝 **View Logs**

```bash
# Backend service logs (trong terminal service chạy)
# Xem "Tomcat started" message

# Gateway logs (trong terminal gateway chạy)
# Xem "API Gateway running on port 3000"

# Frontend logs (trong terminal frontend chạy)
# Xem "VITE ready in XXX ms"

# Docker logs
docker logs -f postgres-auth-db
docker logs -f mysql-product-db
```

### 🐛 **Debug Mode (Backend)**

**Thêm vào application.properties:**
```properties
logging.level.root=DEBUG
logging.level.vn.id.luannv=DEBUG
```

---

## 9.10 Verification Dashboard

Sau khi khởi động, bạn có thể check status tại:

| URL | Tool | Mục Đích |
|-----|------|---------|
| http://localhost:8001/swagger-ui.html | Swagger UI | Auth Service API docs |
| http://localhost:8003/swagger-ui.html | Swagger UI | Product Service API docs |
| http://localhost:8002/swagger-ui.html | Swagger UI | Order Service API docs |
| http://localhost:8004/swagger-ui.html | Swagger UI | Audit Service API docs |
| http://localhost:8080 | Adminer | Database management |
| http://localhost:5173 | Browser | Frontend |
| http://localhost:3000 | Browser | Gateway health check |

---

