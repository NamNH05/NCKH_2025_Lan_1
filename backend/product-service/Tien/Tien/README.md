# 🏬 Product Service - Tài Liệu Chi Tiết

## Tổng Quan Service

### Tên Service
**Product Service** (Dịch vụ Quản Lý Sản Phẩm)

### Mục Đích Chính
Product Service quản lý toàn bộ thông tin sản phẩm:
- **Tạo sản phẩm** - Admin thêm sản phẩm mới
- **Cập nhật sản phẩm** - Sửa giá, mô tả, ảnh
- **Xóa sản phẩm** - Tắt bán hoặc xóa hoàn toàn
- **Danh sách sản phẩm** - Lấy toàn bộ hoặc lọc theo danh mục
- **Chi tiết sản phẩm** - Thông tin chi tiết 1 sản phẩm
- **Danh mục** - Quản lý phân loại sản phẩm (Áo nam, Áo nữ, Quần, Giày, Phụ kiện) ✅
- **Tìm kiếm** - Tìm sản phẩm theo keyword
- **Lọc theo danh mục** - Backend returns products by category field ✅

### Service Này Giải Quyết Bài Toán Gì?
Trong một e-commerce:
- Khách hàng cần **xem danh sách** sản phẩm để chọn mua
- Admin cần **quản lý inventory** - thêm/sửa/xóa sản phẩm
- Cần **tìm kiếm nhanh** - người dùng nhập keyword
- Cần **phân loại** - dễ dàng navigate by category
- Cần **quản lý giá** - có khuyến mãi, giá cũ/mới

### Service Này KHÔNG Làm Những Gì?
❌ Không xử lý thanh toán (Payment Service)  
❌ Không quản lý đơn hàng (Order Service)  
❌ Không xác thực người dùng (Auth Service)  
❌ Không gửi email (Notification Service)  
❌ Không upload hình ảnh thực (chỉ lưu URL)

---

## 🏗️ Kiến Trúc Nội Bộ

### Các Layer

```
┌───────────────────────────────────┐
│   Controller (ProductController)  │
│   GET /api/products               │
│   GET /api/products/{id}          │
│   POST /api/products (admin)      │
│   PUT /api/products/{id} (admin)  │
│   DELETE /api/products/{id} (admin)
└────────────┬──────────────────────┘
             │ gọi
┌────────────▼──────────────────────┐
│   Service (ProductService)        │
│   - getProducts()                 │
│   - createProduct()               │
│   - updateProduct()               │
│   - deleteProduct()               │
└────────────┬──────────────────────┘
             │ gọi
┌────────────▼──────────────────────┐
│   Repository (ProductRepository)  │
│   - findAll()                     │
│   - save()                        │
│   - findById()                    │
│   - delete()                      │
└────────────┬──────────────────────┘
             │ query
┌────────────▼──────────────────────┐
│   Database (MySQL)                │
│   - products table                │
│   - categories table              │
└───────────────────────────────────┘
```

### Luồng Request - Lấy Danh Sách Sản Phẩm

```
GET /api/products?category=áo&page=1&limit=20
    ↓
ProductController.getProducts()
    ├─ Parse query params
    ├─ ProductService.searchProducts()
    │   ├─ Build filter:
    │   │   ├─ category = "áo"
    │   │   ├─ page = 1, limit = 20
    │   │   └─ active = true
    │   │
    │   ├─ ProductRepository.findByCategoryAndActive()
    │   ├─ Calculate pagination
    │   │   ├─ offset = (page-1) * limit = 0
    │   │   ├─ Query LIMIT 20 OFFSET 0
    │   │   └─ totalCount = COUNT(*)
    │   │
    │   └─ Return ProductResponse[]
    │
└─ Response: {
     data: [...20 products...],
     pagination: { page: 1, limit: 20, total: 150, totalPages: 8 }
   }
```

### Luồng Request - Tạo Sản Phẩm (Admin)

```
POST /api/products
Authorization: Bearer <admin-token>
{
  "name": "Áo thun cotton",
  "description": "...",
  "price": 150000,
  "oldPrice": 200000,
  "category": "áo",
  "image": "https://...",
  "stock": 100
}
    ↓
ProductController.createProduct()
    ├─ Validate @PreAuthorize("hasRole('ADMIN')")
    ├─ ProductService.createProduct()
    │   ├─ Validate input (name, price, etc)
    │   ├─ Check category exists
    │   ├─ Build Product entity
    │   ├─ ProductRepository.save()
    │   ├─ Log to Audit Service
    │   └─ Return ProductResponse
    │
└─ Response (201): { id, name, price, ... }
```

---

## 🛠️ Công Nghệ Sử Dụng

| Thành Phần | Chi Tiết |
|-----------|---------|
| **Ngôn Ngữ** | Java 17+ |
| **Framework** | Spring Boot 3.3.4 |
| **Database** | MySQL 8.0 |
| **ORM** | JPA Hibernate |
| **Search** | SQL queries (LIKE, FILTER) |

---

## 📁 Cấu Trúc Thư Mục

```
backend/product-service/Tien/Tien/src/main/java/com/BackEnd_Tien/Tien/
│
├── controller/
│   └── ProductController.java       # REST endpoints
│
├── service/
│   └── ProductService.java          # Business logic
│
├── repository/
│   └── ProductRepository.java       # Database queries
│
├── entity/
│   ├── Product.java                 # @Entity @Table("products")
│   └── Category.java                # @Entity @Table("categories")
│
├── dto/
│   ├── request/
│   │   ├── CreateProductRequest.java
│   │   └── UpdateProductRequest.java
│   │
│   └── response/
│       └── ProductResponse.java
│
├── exception/
│   ├── ProductNotFoundException.java
│   └── GlobalExceptionHandler.java
│
└── TienApplication.java             # Entry point
```

---

## 🗄️ Database Schema

### Bảng `categories`
```sql
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,    -- "áo", "quần", "giày", "phụ kiện"
    description VARCHAR(500),
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO categories (name, description) VALUES
('áo', 'Áo thun, áo sơ mi, áo khoác'),
('quần', 'Quần jean, quần kaki, quần short'),
('giày', 'Giày thể thao, giày da, dép'),
('phụ kiện', 'Mũ, túi, dây nịt');
```

### Bảng `products`
```sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    old_price DECIMAL(10, 2),              -- giá cũ (trước khuyến mãi)
    category_id BIGINT NOT NULL,
    image_url VARCHAR(500),
    stock INT DEFAULT 0,                   -- số lượng trong kho
    is_active BOOLEAN DEFAULT TRUE,        -- ẩn hay hiện
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE INDEX idx_category_id ON products(category_id);
CREATE INDEX idx_is_active ON products(is_active);
CREATE INDEX idx_name ON products(name);
```

### Quan Hệ
```
categories (1) ──── (*) products
  │
  └─ 1 category có nhiều products
     ví dụ: category "áo" chứa 50 sản phẩm
```

---

## 🔌 API Endpoints

### 1️⃣ **Lấy Danh Sách Sản Phẩm**
```http
GET /api/products?category=áo&page=1&limit=20&search=cotton
```

**Response (200 OK)**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Áo thun cotton",
      "description": "...",
      "price": 150000,
      "oldPrice": 200000,
      "discount": 25,
      "category": "áo",
      "imageUrl": "https://...",
      "stock": 100,
      "isActive": true,
      "createdAt": "2026-01-16T10:30:00"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 150,
    "totalPages": 8
  }
}
```

**Query Parameters**
- `category` - Filter by category
- `search` - Search by name/description
- `page` - Page number (default 1)
- `limit` - Items per page (default 20)
- `sortBy` - Sort field (price, createdAt, etc)
- `order` - asc or desc

---

### 2️⃣ **Lấy Chi Tiết Sản Phẩm**
```http
GET /api/products/{productId}
```

**Response (200 OK)**
```json
{
  "id": 1,
  "name": "Áo thun cotton",
  "description": "Áo thun chất lượng cao, thoáng mát...",
  "price": 150000,
  "oldPrice": 200000,
  "discount": 25,
  "category": {
    "id": 1,
    "name": "áo"
  },
  "imageUrl": "https://...",
  "stock": 100,
  "isActive": true,
  "createdAt": "2026-01-16T10:30:00"
}
```

**Lỗi có thể**
- `404` - Product not found
- `410` - Product is inactive (soft deleted)

---

### 3️⃣ **Tạo Sản Phẩm** (Admin only)
```http
POST /api/products
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "name": "Áo thun cotton mới",
  "description": "Mô tả sản phẩm...",
  "price": 150000,
  "oldPrice": 200000,
  "categoryId": 1,
  "imageUrl": "https://...",
  "stock": 100
}
```

**Response (201 Created)**
```json
{
  "id": 101,
  "name": "Áo thun cotton mới",
  "price": 150000,
  "category": "áo",
  "createdAt": "2026-01-16T11:00:00"
}
```

**Validation**
- `name` - 3-255 characters
- `price` - > 0
- `categoryId` - must exist
- `imageUrl` - valid URL

**Lỗi có thể**
- `400` - Invalid input data
- `401` - Unauthorized (not admin)
- `404` - Category not found

---

### 4️⃣ **Cập Nhật Sản Phẩm** (Admin only)
```http
PUT /api/products/{productId}
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "name": "Áo thun cotton (updated)",
  "price": 160000,
  "stock": 95
}
```

**Response (200 OK)**
```json
{
  "id": 1,
  "name": "Áo thun cotton (updated)",
  "price": 160000,
  "stock": 95,
  "updatedAt": "2026-01-16T12:00:00"
}
```

---

### 5️⃣ **Xóa Sản Phẩm** (Admin only)
```http
DELETE /api/products/{productId}
Authorization: Bearer <admin-token>
```

**Response (200 OK)**
```json
{
  "message": "Product deleted successfully"
}
```

**Note**: Có thể soft delete (set isActive=false) hoặc hard delete

---

### 6️⃣ **Lấy Danh Mục**
```http
GET /api/categories
```

**Response (200 OK)**
```json
[
  {
    "id": 1,
    "name": "áo",
    "description": "...",
    "imageUrl": "https://..."
  }
]
```

---

## ⚙️ Cấu Hình

### `application.yml`
```yaml
spring:
  application:
    name: product-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/product_db
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

server:
  port: 8083
  servlet:
    context-path: /
```

---

## 🚀 Cách Chạy

### Local
```bash
cd backend/product-service/Tien/Tien
mvn clean package -DskipTests
mvn spring-boot:run
```

### Docker
```bash
docker build -t product-service:latest .
docker run -p 8083:8083 product-service:latest
```

---

## 🔗 Liên Kết Với Service Khác

### Product Service gọi ai?
```
Product Service
  └─ (optional) Gọi Auth Service
               GET /api/auth/validate (kiểm tra admin)
               
  └─ (optional) Gọi Audit Service
               POST /api/audits (log product changes)
```

### Ai gọi Product Service?
```
Frontend
  └─ GET /api/products (list products)
     GET /api/products/{id} (detail)

Order Service (future)
  └─ GET /api/products/{id} (kiểm tra giá khi checkout)

Admin Dashboard
  └─ POST /api/products (create)
     PUT /api/products/{id} (update)
     DELETE /api/products/{id} (delete)
```

---

## ⚠️ Lỗi Thường Gặp

### 1️⃣ **500 - Database Error**
```
Table 'product_db.products' doesn't exist
```

**Fix**
```bash
mysql -u root -p product_db < init-db.sql
```

---

### 2️⃣ **404 - Product Not Found**
```
GET /api/products/999
```

**Fix**
```bash
# Kiểm tra database
SELECT * FROM products WHERE id = 999;

# Nếu không có, tạo sản phẩm test
INSERT INTO products (name, price, category_id, stock)
VALUES ('Test Product', 100000, 1, 10);
```

---

### 3️⃣ **403 - Forbidden (Not Admin)**
```
POST /api/products (với user account)
```

**Fix**
```bash
# Kiểm tra token có role ADMIN không
# Hoặc login lại với admin account
```

---

## ✅ Checklist

- [ ] Clone repo
- [ ] MySQL chạy, tạo database
- [ ] `mvn spring-boot:run`
- [ ] Test: `curl http://localhost:8083/api/products`
- [ ] Lấy danh sách sản phẩm
- [ ] Lấy chi tiết sản phẩm
- [ ] Tạo sản phẩm (admin)
- [ ] Cập nhật sản phẩm (admin)
- [ ] Xóa sản phẩm (admin)
- [ ] Kiểm tra database
- [ ] Hiểu ProductService logic

---

**Updated**: 2026-01-16  
**Framework**: Spring Boot 3.3.4  
**Database**: MySQL 8.0  
**Status**: ✅ Production Ready
