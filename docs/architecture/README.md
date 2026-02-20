# 🏗️ Kiến Trúc Hệ Thống (System Architecture)

Tài liệu chi tiết về kiến trúc toàn bộ ứng dụng E-Commerce Platform.

## 📋 Mục Lục

- [Tổng Quan](#tổng-quan)
- [Kiến Trúc Tổng Thể](#kiến-trúc-tổng-thể)
- [Các Microservices](#các-microservices)
- [Công Nghệ Stack](#công-nghệ-stack)
- [Sơ Đồ Kiến Trúc](#sơ-đồ-kiến-trúc)
- [Luồng Dữ Liệu](#luồng-dữ-liệu)

---

## 🎯 Tổng Quan

Ứng dụng E-Commerce Platform sử dụng kiến trúc **Microservices** với các đặc điểm:

- **Độc lập**: Mỗi dịch vụ chạy độc lập, có thể deploy riêng
- **Khả năng mở rộng**: Dễ dàng thêm các dịch vụ mới
- **Độ tin cậy cao**: Lỗi trong một dịch vụ không ảnh hưởng các dịch vụ khác
- **Dễ bảo trì**: Mã nguồn được tổ chức theo nghiệp vụ (Domain-Driven)

### Nguyên Tắc Thiết Kế

1. **Single Responsibility**: Mỗi service chỉ làm một việc
2. **Loose Coupling**: Các service kết nối lỏng lẻo
3. **High Cohesion**: Chức năng liên quan được nhóm lại
4. **Stateless Services**: Không lưu trữ trạng thái
5. **Independent Deployment**: Deploy độc lập

---

## 🏛️ Kiến Trúc Tổng Thể

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend Layer                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │    Web Client (React/Vite)                           │  │
│  │    - Login Page, Product Listing, Shopping Cart     │  │
│  │    - Order Management, Admin Dashboard              │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            ↓
                    ┌──────────────────┐
                    │   API Gateway    │
                    │  (Express.js)    │
                    └──────────────────┘
                            ↓
        ┌───────────────────┼───────────────────┐
        ↓                   ↓                   ↓
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│  Auth Service    │ │  Order Service   │ │ Product Service  │
│  (Java/Spring)   │ │  (Java/Spring)   │ │  (Java/Spring)   │
└──────────────────┘ └──────────────────┘ └──────────────────┘
        ↓                   ↓                   ↓
    ┌────────┐          ┌────────┐         ┌────────┐
    │PostgreSQL          │MSSQL  │         │MySQL   │
    └────────┘          └────────┘         └────────┘

        ↑
        └─────────────────────┬─────────────────────┐
                              ↓
                      ┌──────────────────┐
                      │  Audit Service   │
                      │  (Java/Spring)   │
                      └──────────────────┘
                              ↓
                          ┌────────┐
                          │PostgreSQL
                          └────────┘
```

---

## 🔧 Các Microservices

### 1. **Auth Service** (Xác Thực & Phân Quyền)
- **Tư liệu**: [backend/auth-service/Nckh-Lu-n/README.md](../../backend/auth-service/Nckh-Lu-n/README.md)
- **Chức năng chính**:
  - Đăng ký / Đăng nhập người dùng
  - Quản lý JWT Token
  - Phân quyền (RBAC)
  - Quản lý hồ sơ người dùng
- **Database**: PostgreSQL
- **API Endpoints**:
  - `POST /api/auth/register` - Đăng ký
  - `POST /api/auth/login` - Đăng nhập
  - `POST /api/auth/logout` - Đăng xuất
  - `GET /api/auth/profile` - Lấy hồ sơ
  - `PUT /api/auth/profile` - Cập nhật hồ sơ

### 2. **Order Service** (Quản Lý Đơn Hàng)
- **Tư liệu**: [backend/order-service/Nckh-C-ng/README.md](../../backend/order-service/Nckh-C-ng/README.md)
- **Chức năng chính**:
  - Tạo / Quản lý đơn hàng
  - Theo dõi trạng thái đơn hàng
  - Lịch sử đơn hàng
  - Tính toán giá + VAT
- **Database**: MSSQL
- **API Endpoints**:
  - `POST /api/orders` - Tạo đơn hàng
  - `GET /api/orders/{id}` - Lấy chi tiết đơn
  - `GET /api/orders` - Danh sách đơn hàng
  - `PUT /api/orders/{id}` - Cập nhật đơn hàng
  - `DELETE /api/orders/{id}` - Xóa đơn hàng

### 3. **Product Service** (Quản Lý Sản Phẩm)
- **Tư liệu**: [backend/product-service/Tien/Tien/README.md](../../backend/product-service/Tien/Tien/README.md)
- **Chức năng chính**:
  - Quản lý danh mục sản phẩm
  - Quản lý thông tin sản phẩm
  - Quản lý tồn kho
  - Tìm kiếm & lọc sản phẩm
- **Database**: MySQL
- **API Endpoints**:
  - `GET /api/products` - Danh sách sản phẩm
  - `GET /api/products/{id}` - Chi tiết sản phẩm
  - `POST /api/products` - Tạo sản phẩm
  - `PUT /api/products/{id}` - Cập nhật sản phẩm
  - `DELETE /api/products/{id}` - Xóa sản phẩm
  - `GET /api/categories` - Danh sách danh mục

### 4. **Audit Service** (Ghi Nhận Hoạt Động)
- **Tư liệu**: [backend/audit-service/Nckh-Hi-p/README.md](../../backend/audit-service/Nckh-Hi-p/README.md)
- **Chức năng chính**:
  - Ghi nhận tất cả hoạt động hệ thống
  - Lưu trữ logs chi tiết
  - Hỗ trợ audit trail cho compliance
  - Phân tích hành vi người dùng
- **Database**: PostgreSQL
- **Không phục vụ request trực tiếp** - Nhận events từ các service khác

---

## 🌐 API Gateway

**Tư liệu**: [api-gateway/README.md](../../api-gateway/README.md)

- **Framework**: Express.js
- **Port**: 3000 (default)
- **Chức năng**:
  - Route request đến các service phù hợp
  - Xác thực token JWT
  - Logging & monitoring
  - Rate limiting
  - CORS handling
  - Error handling tập trung

**Cấu trúc routing**:
```
/api/auth       → Auth Service (port 8001)
/api/orders     → Order Service (port 8002)
/api/products   → Product Service (port 8003)
/api/audit      → Audit Service (port 8004)
```

---

## 💾 Công Nghệ Stack

### Backend
| Thành phần | Công nghệ | Phiên bản |
|-----------|-----------|---------|
| Runtime | Java | 11+ |
| Framework | Spring Boot | 3.x |
| Build Tool | Maven | 3.6+ |
| Database (Auth) | PostgreSQL | 13+ |
| Database (Order) | MSSQL | 2019+ |
| Database (Product) | MySQL | 8.0+ |
| API Gateway | Node.js / Express | 18+ |

### Frontend
| Thành phần | Công nghệ | Phiên bản |
|-----------|-----------|---------|
| Runtime | Node.js | 18+ |
| Framework | React | 18+ |
| Build Tool | Vite | 4.x |
| HTTP Client | Axios | 1.x |
| State Management | Context API | - |

### DevOps
| Thành phần | Công nghệ |
|-----------|-----------|
| Containerization | Docker |
| Orchestration | Docker Compose |
| CI/CD | Manual / Scripts |

---

## 🔄 Luồng Dữ Liệu

### 1. User Registration Flow
```
Frontend
   ↓
API Gateway (/api/auth/register)
   ↓
Auth Service
   ↓
PostgreSQL (Save user)
   ↓
Email Service (Optional - send verification)
   ↓
Response to Frontend
```

### 2. Product Search Flow
```
Frontend (Search query)
   ↓
API Gateway (/api/products?search=...)
   ↓
Product Service
   ↓
MySQL (Query products)
   ↓
Response with results
   ↓
Frontend (Display results)
```

### 3. Order Creation Flow
```
Frontend (Submit order)
   ↓
API Gateway (/api/orders)
   ↓
Order Service
   ↓
Validate with Auth Service (Check user)
   ↓
Check inventory with Product Service
   ↓
Create order in MSSQL
   ↓
Publish event to Audit Service
   ↓
Response to Frontend
```

---

## 📊 Database Design

### PostgreSQL (Auth Service)
```sql
-- Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(20),
    role VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- User Addresses
CREATE TABLE user_addresses (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    address VARCHAR(255),
    city VARCHAR(100),
    province VARCHAR(100),
    postal_code VARCHAR(20),
    is_default BOOLEAN
);
```

### MSSQL (Order Service)
```sql
-- Orders Table
CREATE TABLE Orders (
    OrderID INT PRIMARY KEY IDENTITY,
    UserID NVARCHAR(255),
    OrderDate DATETIME,
    TotalAmount DECIMAL(10,2),
    Status NVARCHAR(50),
    CreatedAt DATETIME
);

-- Order Items
CREATE TABLE OrderItems (
    ItemID INT PRIMARY KEY IDENTITY,
    OrderID INT REFERENCES Orders(OrderID),
    ProductID INT,
    Quantity INT,
    UnitPrice DECIMAL(10,2)
);
```

### MySQL (Product Service)
```sql
-- Categories
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Products
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT REFERENCES categories(id),
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2),
    stock INT,
    description TEXT,
    created_at TIMESTAMP
);
```

---

## 🚀 Deployment Architecture

```
┌─────────────────────────────────────────────┐
│         Docker Compose Environment          │
├─────────────────────────────────────────────┤
│                                             │
│  Frontend Container (Nginx/Vite)            │
│  Port: 5173                                 │
│                                             │
│  API Gateway Container (Node/Express)       │
│  Port: 3000                                 │
│                                             │
│  ┌─────────────────────────────────────┐  │
│  │    Microservices Containers         │  │
│  │                                     │  │
│  │  Auth Service      (Port: 8001)     │  │
│  │  Order Service     (Port: 8002)     │  │
│  │  Product Service   (Port: 8003)     │  │
│  │  Audit Service     (Port: 8004)     │  │
│  └─────────────────────────────────────┘  │
│                                             │
│  ┌─────────────────────────────────────┐  │
│  │    Database Containers              │  │
│  │                                     │  │
│  │  PostgreSQL        (Port: 5432)     │  │
│  │  MSSQL             (Port: 1433)     │  │
│  │  MySQL             (Port: 3306)     │  │
│  └─────────────────────────────────────┘  │
│                                             │
└─────────────────────────────────────────────┘
```

---

## 🔐 Security Layers

1. **API Gateway**: Token validation, Rate limiting
2. **Auth Service**: JWT generation, Password hashing
3. **Service-to-Service**: Internal validation
4. **Database**: Encrypted connections, Input validation
5. **Frontend**: HTTPS only, Secure cookies

---

## 📈 Scalability Considerations

### Horizontal Scaling
- Có thể deploy nhiều instance của mỗi service
- API Gateway có thể load balance giữa các instance
- Database replication cho high availability

### Vertical Scaling
- Tăng tài nguyên (CPU, Memory) cho các service
- Optimize database queries
- Caching layer (Redis) nếu cần

### Caching Strategy
- Frontend: Browser cache, localStorage
- Backend: Cache layer (Redis) nếu cần
- Database: Connection pooling

---

## 🔄 Service Communication

### Synchronous (REST API)
```
Service A → Service B
(HTTP REST - Blocking)
```

### Asynchronous (Events)
```
Service A → Event Bus → Service B
(Non-blocking, better for Audit)
```

---

## 📞 Tài Liệu Chi Tiết

- [Auth Service](../../backend/auth-service/Nckh-Lu-n/README.md)
- [Order Service](../../backend/order-service/Nckh-C-ng/README.md)
- [Product Service](../../backend/product-service/Tien/Tien/README.md)
- [Audit Service](../../backend/audit-service/Nckh-Hi-p/README.md)
- [API Gateway](../../api-gateway/README.md)

---

**Last Updated**: 16/01/2026  
**Version**: 1.0



