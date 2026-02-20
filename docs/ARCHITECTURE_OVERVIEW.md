# 🏗️ TỔNG QUAN KIẾN TRÚC DỰ ÁN NCKH

## 📋 Mục Lục
1. [Tổng Quan Hệ Thống](#tổng-quan-hệ-thống)
2. [Kiến Trúc Microservices](#kiến-trúc-microservices)
3. [Chi Tiết Các Thành Phần](#chi-tiết-các-thành-phần)
4. [Luồng Dữ Liệu & Tương Tác](#luồng-dữ-liệu--tương-tác)
5. [Công Nghệ Sử Dụng](#công-nghệ-sử-dụng)
6. [Sơ Đồ Kiến Trúc](#sơ-đồ-kiến-trúc)

---

## 🎯 Tổng Quan Hệ Thống

Dự án NCKH là một **nền tảng e-commerce Full-Stack** được xây dựng theo kiến trúc **Microservices**. Hệ thống bao gồm:

- **Frontend**: React SPA (Single Page Application) với Vite
- **API Gateway**: Node.js/Express - xác thực & định tuyến
- **Backend Services**: 4 microservices độc lập (Spring Boot Java)
- **Databases**: PostgreSQL, MySQL, SQL Server, Oracle

**Mục đích**: Cung cấp một nền tảng mua sắm hoàn chỉnh với quản lý sản phẩm, đơn hàng, xác thực người dùng, và ghi nhật ký kiểm toán.

---

## 🔗 Kiến Trúc Microservices

```
┌─────────────────────────────────────────────────────────────┐
│                    🌐 FRONTEND LAYER                         │
├─────────────────────────────────────────────────────────────┤
│  React + Vite (Port 5173)                                   │
│  - Web Client (web-client/)                                 │
│    └─ Trang chủ, sản phẩm, giỏ hàng, checkout, admin       │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/REST (Axios)
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  🚪 API GATEWAY (Port 3000)                  │
├─────────────────────────────────────────────────────────────┤
│  Express.js                                                 │
│  - JWT Verification                                         │
│  - CSRF Protection                                          │
│  - Rate Limiting                                            │
│  - Request Forwarding                                       │
│  - CORS Handling                                            │
└───┬───────────┬───────────┬───────────┬────────────────────┘
    │           │           │           │
    ▼           ▼           ▼           ▼
┌────────┬──────────────┬──────────────┬──────────┐
│        │              │              │          │
│ Auth   │ Product      │ Order        │ Audit    │
│Service │ Service      │ Service      │ Service  │
│        │              │              │          │
│8080    │ 8081         │ 8091         │ 8082     │
└────────┴──────────────┴──────────────┴──────────┘
│        │              │              │
▼        ▼              ▼              ▼
Postgre  MySQL          SQL Server    Oracle
SQL      (Port 3306)    (Port 1433)
(Port    
5432)    
```

---

## 📦 Chi Tiết Các Thành Phần

### 1️⃣ **FRONTEND - Web Client (React + Vite)**

**Vị trí**: `frontend/web-client/`

**Công Nghệ**:
- React 19.2
- Vite 7.2 (Build tool)
- React Router DOM 7.13 (Navigation)
- Ant Design 6.1 (UI Components)
- Axios 1.13 (HTTP Client)

**Cấu Trúc Thư Mục**:
```
web-client/
├── src/
│   ├── api/              # API Service calls (Axios config)
│   ├── components/       # React Components
│   │   ├── features/     # Feature-specific components
│   │   │   ├── admin/    # Admin Dashboard
│   │   │   ├── auth/     # Authentication
│   │   │   └── ...
│   │   └── common/       # Shared components
│   ├── context/          # React Context API (State management)
│   ├── pages/            # Page components
│   ├── routes/           # Routing config
│   ├── styles/           # Tailwind CSS
│   ├── utils/            # Helper functions
│   ├── App.jsx           # Root component
│   └── main.jsx          # Entry point
├── package.json
├── vite.config.js        # Vite configuration
├── index.html            # HTML template
└── eslint.config.js      # Linting rules
```

**Tính Năng Chính**:
- 🏠 Trang chủ với danh sách sản phẩm động
- 🔍 Tìm kiếm & lọc sản phẩm theo danh mục
- 🏷️ Danh mục: Áo nam, Áo nữ, Quần, Giày, Phụ kiện
- 👨‍💼 Admin Dashboard (CRUD sản phẩm)
- 🔐 Đăng nhập/Đăng ký
- 💳 Checkout & Thanh toán
- 📱 Responsive Design

---

### 2️⃣ **API GATEWAY - Express.js (Port 3000)**

**Vị trí**: `api-gateway/`

**Công Nghệ**:
- Express 4.18
- JWT (jsonwebtoken 9.0) - Token authentication
- CORS 2.8 - Cross-origin requests
- Helmet 8.1 - Security headers
- Express Rate Limit 8.2 - Rate limiting
- CSRF Protection (csurf 1.11) - CSRF token protection

**Trách Nhiệm**:
1. **Xác Thực JWT** - Kiểm tra token từ Frontend
2. **Định Tuyến Request** - Chuyển tiếp tới các backend services
3. **Bảo Mật**:
   - Helmet: Thêm security headers
   - CSRF: Chống CSRF attacks
   - Rate Limiting: Giới hạn requests
   - Input Validation: Kiểm tra dữ liệu đầu vào

**Cấu Trúc**:
```
api-gateway/
├── server.js              # Main server file
├── csrf-middleware.js     # CSRF protection setup
├── input-validation.js    # Request validation
├── rate-limit.js         # Rate limiting config
├── express.json          # Express config
├── package.json
├── .env                  # Environment variables
├── start-gateway.bat     # Windows startup script
└── README.md
```

**Endpoints**:
- `POST /auth/*` - Forward to Auth Service (no JWT required)
- `POST /products` - Create product (requires JWT)
- `GET /products*` - Get products
- `PUT /products/:id` - Update product (requires JWT + Admin role)
- `DELETE /products/:id` - Delete product (requires JWT + Admin role)
- `GET /orders*` - Get orders (requires JWT)
- `POST /orders` - Create order (requires JWT)
- `GET /audit*` - Get audit logs (requires JWT + Admin role)

---

### 3️⃣ **AUTH SERVICE - Spring Boot (Port 8080)**

**Vị trí**: `backend/auth-service/Nckh-Lu-n/`

**Công Nghệ**:
- Spring Boot 3.5.9
- Spring Security - Authentication & Authorization
- Spring Data JPA - ORM
- PostgreSQL - Database

**Trách Nhiệm**:
1. **Đăng ký người dùng** - Tạo tài khoản mới
2. **Đăng nhập** - Xác thực & cấp JWT token
3. **Quản lý người dùng** - CRUD user data
4. **JWT Token Generation** - Tạo token an toàn

**Endpoints Chính**:
```
POST   /api/auth/register          # Đăng ký
POST   /api/auth/login              # Đăng nhập
GET    /api/auth/validate-token     # Kiểm tra token
GET    /api/auth/user/:id           # Lấy thông tin user
PUT    /api/auth/user/:id           # Cập nhật user
DELETE /api/auth/user/:id           # Xóa user
```

**Cơ Sở Dữ Liệu**: PostgreSQL (Port 5432)
- Table: `users` - Thông tin người dùng
- Table: `roles` - Vai trò (Admin, User)

---

### 4️⃣ **PRODUCT SERVICE - Spring Boot (Port 8081)**

**Vị trí**: `backend/product-service/Tien/Tien/`

**Công Nghệ**:
- Spring Boot
- Spring Data JPA
- MySQL - Database

**Trách Nhiệm**:
1. **Quản lý sản phẩm** - CRUD operations
2. **Danh mục sản phẩm** - Phân loại sản phẩm
3. **Tìm kiếm & lọc** - Tìm kiếm theo từ khóa và danh mục
4. **Lấy dữ liệu** - API để frontend lấy sản phẩm

**Endpoints Chính**:
```
GET    /api/products                # Lấy tất cả sản phẩm
GET    /api/products/:id            # Lấy chi tiết sản phẩm
GET    /api/products/category/:type # Lọc theo danh mục
GET    /api/products/search?keyword # Tìm kiếm sản phẩm
POST   /api/products                # Tạo sản phẩm (Admin)
PUT    /api/products/:id            # Cập nhật sản phẩm (Admin)
DELETE /api/products/:id            # Xóa sản phẩm (Admin)
```

**Danh Mục Sản Phẩm**:
- Áo nam
- Áo nữ
- Quần
- Giày
- Phụ kiện

**Cơ Sở Dữ Liệu**: MySQL (Port 3306)
- Table: `products` - Thông tin sản phẩm
- Table: `categories` - Danh mục
- Fields: id, name, description, price, category, image_url, stock

---

### 5️⃣ **ORDER SERVICE - Spring Boot (Port 8091)**

**Vị trí**: `backend/order-service/Nckh-C-ng/`

**Công Nghệ**:
- Spring Boot 3.2.5
- Spring Data JPA
- Thymeleaf - Server-side rendering
- SQL Server (MSSQL) - Database

**Trách Nhiệm**:
1. **Giỏ hàng** - Thêm/xóa sản phẩm từ giỏ
2. **Thanh toán** - Xử lý checkout
3. **Đơn hàng** - CRUD order
4. **Vận chuyển** - Quản lý địa chỉ & vận chuyển

**Endpoints Chính**:
```
GET    /api/orders                  # Lấy các đơn hàng
GET    /api/orders/:id              # Chi tiết đơn hàng
POST   /api/orders                  # Tạo đơn hàng
PUT    /api/orders/:id              # Cập nhật đơn hàng
DELETE /api/orders/:id              # Xóa đơn hàng
GET    /api/cart/:userId            # Lấy giỏ hàng
POST   /api/cart/add                # Thêm vào giỏ
POST   /api/cart/remove             # Xóa từ giỏ
POST   /api/checkout                # Xử lý checkout
```

**Cơ Sở Dữ Liệu**: SQL Server (Port 1433)
- Table: `orders` - Thông tin đơn hàng
- Table: `order_items` - Chi tiết items
- Table: `cart` - Giỏ hàng
- Table: `cart_items` - Items trong giỏ

---

### 6️⃣ **AUDIT SERVICE - Spring Boot (Port 8082)**

**Vị trí**: `backend/audit-service/Nckh-Hi-p/`

**Công Nghệ**:
- Spring Boot 3.5.9
- Oracle Database

**Trách Nhiệm**:
1. **Ghi nhật ký hành động** - Đăng ký tất cả user actions
2. **Kiểm toán an toàn** - Theo dõi login, logout, access
3. **Lịch sử thay đổi** - Ghi lại thay đổi dữ liệu

**Endpoints Chính**:
```
GET    /api/audit-logs              # Lấy nhật ký
GET    /api/audit-logs/:id          # Chi tiết nhật ký
GET    /api/audit-logs/user/:userId # Nhật ký của user
POST   /api/audit-logs              # Tạo nhật ký (Internal)
```

**Cơ Sở Dữ Liệu**: Oracle
- Table: `audit_logs` - Nhật ký hoạt động
- Fields: id, userId, action, timestamp, details

---

## 🔄 Luồng Dữ Liệu & Tương Tác

### 📌 Luồng 1: Đăng Nhập

```
1. User nhập username/password
   ↓
2. Frontend (React) gửi POST /auth/login tới API Gateway
   ↓
3. API Gateway chuyển tiếp tới Auth Service (Port 8080)
   ↓
4. Auth Service:
   - Kiểm tra username/password trong PostgreSQL
   - Tạo JWT Token
   - Trả về token + user info
   ↓
5. API Gateway trả về response cho Frontend
   ↓
6. Frontend lưu token vào localStorage/sessionStorage
   ↓
7. Các request tiếp theo đều gửi token trong header:
   Authorization: Bearer <token>
   ↓
8. API Gateway xác minh token trước khi forward request
```

### 📌 Luồng 2: Lấy Danh Sách Sản Phẩm

```
1. Frontend gửi GET /api/products?category=men
   ↓
2. API Gateway:
   - Nhận request
   - (Có thể kiểm tra JWT nếu cần)
   - Forward tới Product Service (Port 8081)
   ↓
3. Product Service:
   - Query MySQL với category filter
   - Trả về danh sách sản phẩm + giá + hình ảnh
   ↓
4. API Gateway chuyển response về Frontend
   ↓
5. Frontend render danh sách sản phẩm
```

### 📌 Luồng 3: Tạo Đơn Hàng (Checkout)

```
1. User click "Thanh toán"
   ↓
2. Frontend gửi POST /api/checkout
   Header: Authorization: Bearer <token>
   Body: { items: [...], shippingAddress: {...} }
   ↓
3. API Gateway:
   - Xác minh JWT token
   - Forward tới Order Service (Port 8091)
   ↓
4. Order Service:
   - Tạo record trong SQL Server (orders table)
   - Tạo order_items từ items
   - Cập nhật stock sản phẩm (call Product Service)
   - Trả về order ID + confirmation
   ↓
5. API Gateway trả về response
   ↓
6. Audit Service (nếu được trigger):
   - Ghi nhật ký: "User XYZ created order ABC"
   ↓
7. Frontend hiển thị confirmation message
```

### 📌 Luồng 4: Ghi Nhật Ký Kiểm Toán

```
Khi một action quan trọng xảy ra:
1. Backend Service (Auth/Order/Product) 
   → Call Audit Service API
   ↓
2. Audit Service ghi vào Oracle Database
   Fields: userId, action, timestamp, details, ipAddress
   ↓
3. Admin có thể:
   - GET /api/audit-logs (xem tất cả)
   - GET /api/audit-logs/user/:userId (xem nhật ký của user)
```

---

## 🛠️ Công Nghệ Sử Dụng

### Frontend
| Công Nghệ | Phiên Bản | Mục Đích |
|-----------|----------|---------|
| React | 19.2 | UI Framework |
| Vite | 7.2 | Build tool & dev server |
| React Router | 7.13 | Routing |
| Ant Design | 6.1 | UI Component library |
| Axios | 1.13 | HTTP client |
| Tailwind CSS | 4.1 | Styling |
| ESLint | 9.39 | Code linting |

### API Gateway
| Công Nghệ | Phiên Bản | Mục Đích |
|-----------|----------|---------|
| Express | 4.18 | Web framework |
| Node.js | Latest | Runtime |
| JWT | 9.0 | Token auth |
| Cors | 2.8 | CORS handling |
| Helmet | 8.1 | Security headers |
| Rate Limit | 8.2 | Rate limiting |
| CSRF | 1.11 | CSRF protection |

### Backend Services
| Công Nghệ | Phiên Bản | Mục Đích |
|-----------|----------|---------|
| Spring Boot | 3.2-3.5 | Framework |
| Java | 17 | Language |
| Spring Security | Latest | Authentication |
| Spring Data JPA | Latest | ORM |
| Maven | Latest | Build tool |
| Lombok | Latest | Code generation |

### Databases
| Database | Port | Mục Đích |
|----------|------|---------|
| PostgreSQL | 5432 | Auth Service |
| MySQL | 3306 | Product Service |
| SQL Server | 1433 | Order Service |
| Oracle | Custom | Audit Service |

---

## 🏛️ Sơ Đồ Kiến Trúc Chi Tiết

### A. Architecture Overview
```
┌───────────────────────────────────────────────────────────────────┐
│                          BROWSER (User)                            │
└────────────────────────────┬────────────────────────────────────────┘
                             │ HTTP/HTTPS
                             ▼
┌───────────────────────────────────────────────────────────────────┐
│  FRONTEND: React + Vite (localhost:5173)                          │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │ Pages:                                                      │  │
│  │ - Home                  - Cart                              │  │
│  │ - Product List          - Checkout                          │  │
│  │ - Product Detail        - Order History                     │  │
│  │ - Admin Dashboard       - User Profile                      │  │
│  │ - Login/Register                                            │  │
│  └─────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────┘
                             │ REST API (Axios)
                             │ Authorization: Bearer <JWT>
                             ▼
┌───────────────────────────────────────────────────────────────────┐
│   API GATEWAY: Express.js (localhost:3000)                        │
│   ┌──────────────────────────────────────────────────────────┐   │
│   │ Middleware:                                              │   │
│   │ 1. CORS Handler        4. JWT Verifier                   │   │
│   │ 2. Rate Limiter        5. Input Validator                │   │
│   │ 3. CSRF Protector      6. Error Handler                  │   │
│   └──────────────────────────────────────────────────────────┘   │
└──────┬──────────┬──────────┬──────────┬──────────────────────────┘
       │          │          │          │
       ▼          ▼          ▼          ▼
   ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
   │ Auth   │ │Product │ │ Order  │ │ Audit  │
   │Service │ │Service │ │Service │ │Service │
   │:8080   │ │:8081   │ │:8091   │ │:8082   │
   └────┬───┘ └───┬────┘ └───┬────┘ └───┬────┘
        │         │          │          │
        ▼         ▼          ▼          ▼
   ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
   │Postgre │ │ MySQL  │ │SQL Srv │ │ Oracle │
   │ SQL    │ │:3306   │ │:1433   │ │        │
   │:5432   │ └────────┘ └────────┘ └────────┘
   └────────┘
```

### B. Request Flow - Chi Tiết
```
REQUEST: POST /api/products
         (Create new product)

┌─────────────────────────────────────────────────────────────┐
│ Frontend (React)                                            │
│ - User nhập sản phẩm mới                                   │
│ - Click "Tạo"                                              │
└────────────┬────────────────────────────────────────────────┘
             │ axios.post('/api/products', productData, 
             │            { headers: { Authorization: ...}})
             ▼
┌─────────────────────────────────────────────────────────────┐
│ API Gateway (Port 3000)                                    │
│ 1. Check CORS origin                                       │
│ 2. Verify JWT token                                        │
│ 3. Check rate limit                                        │
│ 4. Validate input (CSRF token)                             │
│ 5. Forward POST to Product Service:8081                    │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│ Product Service (Port 8081)                                │
│ 1. Validate request body                                   │
│ 2. Check user authorization (JWT claims)                   │
│ 3. Create product record in MySQL                          │
│ 4. Generate response { id, name, price, ... }              │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│ MySQL Database (Port 3306)                                 │
│ INSERT INTO products (name, price, category, ...)          │
│ VALUES (...)                                               │
└────────────┬────────────────────────────────────────────────┘
             │ Success
             ▼
┌─────────────────────────────────────────────────────────────┐
│ Audit Service (Optional trigger)                           │
│ POST /api/audit-logs                                       │
│ { userId, action: "CREATE_PRODUCT", details: {...} }       │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼ Response: { id, name, price, ... }
┌─────────────────────────────────────────────────────────────┐
│ API Gateway                                                │
│ Return response to Frontend                                │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│ Frontend (React)                                            │
│ - Display success message                                  │
│ - Refresh product list                                     │
│ - Redirect to product detail                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔐 Bảo Mật

### Authentication Flow
```
1. User Login
   └─→ POST /auth/login
       └─→ Auth Service validates credentials
           └─→ Creates JWT Token
               └─→ Returns { token, user_id, role }

2. Store Token
   └─→ Frontend stores in localStorage
       └─→ Included in Authorization header

3. Request with Token
   └─→ Every API request includes:
       Authorization: Bearer <JWT_TOKEN>

4. API Gateway Validation
   └─→ Extracts token from header
       └─→ Verifies signature with JWT_SECRET
           └─→ Checks expiration time
               └─→ Extracts user_id & role
                   └─→ Attaches to request for backend

5. Backend Verification
   └─→ Spring Security checks token claims
       └─→ Validates user authority
           └─→ Allows/Denies operation based on role
```

### Authorization by Role
```
Role: ADMIN
├─ Create Products
├─ Update Products
├─ Delete Products
├─ View All Audit Logs
├─ Manage Users
└─ View All Orders

Role: USER
├─ View Products
├─ Search Products
├─ Create Orders
├─ View Own Orders
├─ View Own Profile
└─ Cannot modify/delete products
```

### Security Features
1. **CSRF Protection** (csurf middleware)
   - Token validation on POST/PUT/DELETE
2. **Rate Limiting** (express-rate-limit)
   - Max 100 requests per 15 minutes
3. **Helmet Security Headers**
   - X-Frame-Options
   - X-Content-Type-Options
   - Content-Security-Policy
4. **Input Validation**
   - Sanitize user inputs
   - Validate data types
   - Check field lengths
5. **CORS Configuration**
   - Allow only trusted origins
   - Restrict to localhost:5173

---

## 📊 Cơ Sở Dữ Liệu

### PostgreSQL (Auth Service)
```sql
-- Users table
users (
  id INT PRIMARY KEY,
  username VARCHAR(100) UNIQUE,
  password VARCHAR(255),
  email VARCHAR(100),
  full_name VARCHAR(100),
  role VARCHAR(20),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)

-- Roles table
roles (
  id INT PRIMARY KEY,
  name VARCHAR(50),
  permissions JSON
)
```

### MySQL (Product Service)
```sql
-- Products table
products (
  id INT PRIMARY KEY,
  name VARCHAR(255),
  description TEXT,
  price DECIMAL(10,2),
  category VARCHAR(50),
  image_url VARCHAR(500),
  stock INT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)

-- Categories
categories: (Áo nam, Áo nữ, Quần, Giày, Phụ kiện)
```

### SQL Server (Order Service)
```sql
-- Orders table
orders (
  id INT PRIMARY KEY,
  user_id INT,
  total_amount DECIMAL(10,2),
  status VARCHAR(20),
  shipping_address VARCHAR(500),
  created_at DATETIME,
  updated_at DATETIME
)

-- Order Items table
order_items (
  id INT PRIMARY KEY,
  order_id INT,
  product_id INT,
  quantity INT,
  unit_price DECIMAL(10,2),
  subtotal DECIMAL(10,2)
)

-- Cart table
cart (
  id INT PRIMARY KEY,
  user_id INT,
  created_at DATETIME
)

-- Cart Items
cart_items (
  id INT PRIMARY KEY,
  cart_id INT,
  product_id INT,
  quantity INT
)
```

### Oracle (Audit Service)
```sql
-- Audit Logs table
audit_logs (
  id INT PRIMARY KEY,
  user_id INT,
  action VARCHAR(100),
  entity_type VARCHAR(50),
  entity_id INT,
  old_value CLOB,
  new_value CLOB,
  timestamp TIMESTAMP,
  ip_address VARCHAR(50),
  status VARCHAR(20)
)
```

---

## 🚀 Startup Sequence

### Local Development
```
1. Start Databases
   - PostgreSQL (5432)
   - MySQL (3306)
   - SQL Server (1433)
   - Oracle (custom)

2. Start Backend Services (separate terminals)
   Terminal 1: cd backend/auth-service/Nckh-Lu-n && mvn spring-boot:run
   Terminal 2: cd backend/product-service && mvn spring-boot:run
   Terminal 3: cd backend/order-service/Nckh-C-ng && mvn spring-boot:run
   Terminal 4: cd backend/audit-service/Nckh-Hi-p && mvn spring-boot:run

3. Start API Gateway
   Terminal 5: cd api-gateway && npm install && npm start

4. Start Frontend
   Terminal 6: cd frontend/web-client && npm install && npm run dev

5. Access Application
   http://localhost:5173
```

### Docker Compose
```bash
# Each service has docker-compose.yml
cd backend/[service-name]/[folder]
docker-compose up
```

---

## 📈 Scalability & Deployment

### Horizontal Scaling
- Mỗi microservice có thể được scale độc lập
- Sử dụng load balancer trước API Gateway
- Database replication cho high availability

### CI/CD Pipeline (Recommended)
```
Code Push (GitHub)
  ↓
GitHub Actions
  ├─ Run Tests
  ├─ Build Docker Images
  ├─ Push to Docker Registry
  └─ Deploy to Kubernetes/Cloud
```

### Production Deployment
- Frontend: AWS S3 + CloudFront hoặc Azure Static Web Apps
- API Gateway: AWS EC2/ECS hoặc Azure Container Instances
- Backend Services: Kubernetes hoặc Docker Swarm
- Databases: Cloud-managed (RDS, Azure Database, etc.)

---

## 🔗 Tài Liệu Liên Quan

- [Backend Services Documentation](../setup/README.md)
- [API Gateway Readme](../../api-gateway/README.md)
- [Frontend Readme](../../frontend/web-client/README.md)
- [Database Setup](../../database/sql-scripts/README.md)
- [Getting Started Guide](../../GETTING_STARTED.md)
- [Troubleshooting Guide](../setup/10-TROUBLESHOOTING.md)

---

## 📞 Liên Hệ & Hỗ Trợ

Nếu có vấn đề hoặc câu hỏi về kiến trúc:
1. Kiểm tra README files của từng service
2. Xem logs từ terminals
3. Kiểm tra port availability
4. Kiểm tra database connections
5. Xem .env files có cấu hình đúng không

---

**Cập nhật lần cuối**: 2024
**Phiên bản**: 1.0
