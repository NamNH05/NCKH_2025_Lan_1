# 🗄️ Phần 5 – Database Setup & SQL

---

## 5.1 Tổng Quan Databases

Dự án sử dụng **3 loại database** khác nhau:

| Database | Port | Services | Dữ Liệu Chứa |
|----------|------|----------|------------|
| **PostgreSQL** | 5432 | Auth, Audit | Users, roles, audit logs |
| **MySQL** | 3306 | Product | Products, categories |
| **SQL Server** | 1433 | Order | Orders, order items, payments |
| **Redis** | 6379 | All Services | Cache (sessions, product cache) |

---

## 5.2 Cách Khởi Tạo Databases

### ✅ **Phương Pháp 1: Docker (Khuyến Nghị)**

Đơn giản nhất, tự động tạo mọi thứ:

```bash
# Chạy docker-compose
docker-compose -f docker-compose-all-services.yml up -d

# Kiểm tra containers
docker ps

# Kiểm tra logs
docker logs postgres-auth-db
docker logs mysql-product-db
docker logs sqlserver-order-db
```

**Điều gì xảy ra:**
1. Docker tải images từ Docker Hub
2. Tạo 4 containers (PostgreSQL, MySQL, SQL Server, Redis)
3. Tự động chạy SQL init scripts từ `database/*.sql`
4. Tất cả databases được tạo & populated tự động

---

### ⚠️ **Phương Pháp 2: Thủ Công (Nếu Docker không hoạt động)**

#### **PostgreSQL (Manual)**

```bash
# 1. Kết nối PostgreSQL
psql -U postgres -h localhost

# 2. Tạo user
CREATE ROLE auth_user WITH LOGIN PASSWORD 'auth@123456';
ALTER ROLE auth_user CREATEDB;

# 3. Tạo database
CREATE DATABASE nckh_auth OWNER auth_user;

# 4. Kết nối database
psql -U auth_user -d nckh_auth -h localhost

# 5. Chạy init script
\i database/postgres-init.sql

# 6. Kiểm tra tables
\dt
```

#### **MySQL (Manual)**

```bash
# 1. Kết nối MySQL
mysql -u root -p

# 2. Nhập password root (default: root@123456)

# 3. Tạo user & database
CREATE USER 'product_user'@'localhost' IDENTIFIED BY 'product@123456';
CREATE DATABASE shopquanao CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON shopquanao.* TO 'product_user'@'localhost';
FLUSH PRIVILEGES;

# 4. Chạy init script
mysql -u product_user -p shopquanao < database/mysql-init.sql

# 5. Kiểm tra tables
USE shopquanao;
SHOW TABLES;
```

#### **SQL Server (Manual)**

```bash
# 1. Kết nối SQL Server
sqlcmd -S localhost -U sa -P SqlServer@2026

# 2. Tạo database
CREATE DATABASE order_dB;
GO

# 3. Tạo user
CREATE LOGIN orderuser WITH PASSWORD = 'Order@123';
USE order_dB;
CREATE USER orderuser FOR LOGIN orderuser;
GRANT CONTROL ON DATABASE::order_dB TO orderuser;
GO

# 4. Chạy init script
sqlcmd -S localhost -U sa -P SqlServer@2026 -d order_dB -i database/sqlserver-init.sql
```

---

## 5.3 Database Credentials

### 📍 **PostgreSQL**

```
Host: localhost
Port: 5432
Databases:
  - nckh_auth     (Auth & Audit)
  
Users:
  - postgres (superuser)
    Password: postgres
    
  - auth_user
    Password: auth@123456
    Permissions: CREATEDB, CREATEROLE
    
  - audit_user (nếu tạo riêng)
    Password: audit@123456
```

---

### 📍 **MySQL**

```
Host: localhost
Port: 3306
Databases:
  - shopquanao     (Product Service)
  
Users:
  - root (superuser)
    Password: root@123456
    
  - product_user
    Password: product@123456
    Database: shopquanao
    Permissions: SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER
```

---

### 📍 **SQL Server**

```
Server: localhost
Port: 1433
Databases:
  - order_dB      (Order Service)
  
Users:
  - sa (System Admin - superuser)
    Password: SqlServer@2026
    
  - orderuser
    Password: Order@123
    Database: order_dB
    Permissions: CONTROL
```

---

### 📍 **Redis**

```
Host: localhost
Port: 6379
Database: 0
Authentication: None (default)
```

---

## 5.4 Database Schema Scripts

Các scripts này được tự động chạy khi Docker containers khởi động.

### **database/postgres-init.sql** (cho Auth & Audit)

```sql
-- ============================================
-- PostgreSQL Init Script for NCKH Auth Service
-- ============================================

-- Ensure the default user/database are created
DO
$$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_user WHERE usename = 'auth_user') THEN
        CREATE ROLE auth_user WITH LOGIN PASSWORD 'auth@123456';
    END IF;
END
$$;

ALTER ROLE auth_user CREATEDB;

-- Create database if not exists (handled by Docker typically)
-- CREATE DATABASE nckh_auth OWNER auth_user;

-- Connect to nckh_auth database
-- Tables will be auto-created by Hibernate when app starts
-- But we can pre-create them here for consistency

-- Schema for Users
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    full_name VARCHAR(255),
    phone VARCHAR(20),
    role VARCHAR(50) DEFAULT 'USER',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Schema for Audit Logs
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    entity_id BIGINT,
    user_id BIGINT,
    changes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed sample user (will be overridden by bootstrap code)
INSERT INTO "user" (username, password, email, full_name, role)
VALUES ('sysadmin', 'hashed_password_here', 'sysadmin@system.local', 'System Admin', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_user_username ON "user" (username);
CREATE INDEX IF NOT EXISTS idx_user_email ON "user" (email);
CREATE INDEX IF NOT EXISTS idx_audit_user_id ON audit_log (user_id);
CREATE INDEX IF NOT EXISTS idx_audit_created_at ON audit_log (created_at);
```

---

### **database/mysql-init.sql** (cho Product Service)

```sql
-- ============================================
-- MySQL Init Script for NCKH Product Service
-- ============================================

-- Ensure database and user exist
CREATE DATABASE IF NOT EXISTS shopquanao CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'product_user'@'%' IDENTIFIED BY 'product@123456';
GRANT ALL PRIVILEGES ON shopquanao.* TO 'product_user'@'%';
FLUSH PRIVILEGES;

USE shopquanao;

-- Schema for Categories
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Schema for Products
CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description LONGTEXT,
    price DECIMAL(10, 2) NOT NULL,
    discount_price DECIMAL(10, 2),
    stock_quantity INT DEFAULT 0,
    image_url VARCHAR(500),
    sku VARCHAR(100) UNIQUE,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id),
    INDEX idx_category (category_id),
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Schema for Product Images
CREATE TABLE IF NOT EXISTS product_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500),
    display_order INT DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES product(id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed sample categories
INSERT INTO category (name, description) VALUES
('Áo thun', 'Áo thun nam nữ, in đơn sắc, họa tiết'),
('Quần', 'Quần jean, quần tây, quần short, jogger'),
('Áo khoác', 'Áo khoác nam nữ, hoodie, jacket'),
('Giày', 'Giày sneaker, giày thể thao, giày da'),
('Phụ kiện', 'Mũ, thắt lưng, túi xách, tất')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Seed sample products
INSERT INTO product (category_id, name, description, price, stock_quantity, status) VALUES
(1, 'Áo thun basic trắng', 'Áo thun basic màu trắng, chất liệu 100% cotton', 89000, 100, 'ACTIVE'),
(1, 'Áo thun đen classic', 'Áo thun đen, kinh điển, phù hợp mọi lứa tuổi', 99000, 80, 'ACTIVE'),
(2, 'Quần jean xanh', 'Quần jean skinny xanh đậm, form đẹp', 299000, 50, 'ACTIVE'),
(2, 'Quần tây ghi', 'Quần tây slim fit màu ghi, chất lượng cao', 349000, 30, 'ACTIVE'),
(3, 'Hoodie xám', 'Hoodie unisex màu xám, ấm áp, thời trang', 399000, 40, 'ACTIVE')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Create indexes
CREATE INDEX idx_created_at ON product(created_at);
CREATE INDEX idx_price ON product(price);
```

---

### **database/sqlserver-init.sql** (cho Order Service)

```sql
-- ============================================
-- SQL Server Init Script for NCKH Order Service
-- ============================================

-- Create database (must be done before connecting to it)
-- This would be done in a separate step:
-- CREATE DATABASE order_dB;
-- GO

-- Create login and user (run as SA)
-- IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'orderuser')
--     CREATE LOGIN orderuser WITH PASSWORD = 'Order@123';
-- GO

-- USE order_dB;
-- GO

-- IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'orderuser')
--     CREATE USER orderuser FOR LOGIN orderuser;
-- GO

-- GRANT CONTROL ON DATABASE::order_dB TO orderuser;
-- GO

-- Create tables
CREATE TABLE [Address] (
    Id BIGINT PRIMARY KEY IDENTITY(1,1),
    UserId BIGINT NOT NULL,
    FullName NVARCHAR(255),
    Phone NVARCHAR(20),
    Street NVARCHAR(255) NOT NULL,
    Ward NVARCHAR(255) NOT NULL,
    District NVARCHAR(255) NOT NULL,
    City NVARCHAR(255) NOT NULL,
    PostalCode NVARCHAR(20),
    IsDefault BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE [Orders] (
    Id BIGINT PRIMARY KEY IDENTITY(1,1),
    UserId BIGINT NOT NULL,
    OrderNumber NVARCHAR(50) UNIQUE NOT NULL,
    Status NVARCHAR(50) DEFAULT 'PENDING',
    TotalPrice DECIMAL(15, 2) NOT NULL,
    ShippingAddress NVARCHAR(500),
    ShippingMethod NVARCHAR(100),
    PaymentMethod NVARCHAR(100),
    PaymentStatus NVARCHAR(50) DEFAULT 'UNPAID',
    Notes NVARCHAR(500),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE [OrderItems] (
    Id BIGINT PRIMARY KEY IDENTITY(1,1),
    OrderId BIGINT NOT NULL,
    ProductId BIGINT NOT NULL,
    ProductName NVARCHAR(255),
    Price DECIMAL(10, 2),
    Quantity INT,
    Subtotal DECIMAL(15, 2),
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (OrderId) REFERENCES Orders(Id)
);

CREATE TABLE [Cart] (
    Id BIGINT PRIMARY KEY IDENTITY(1,1),
    UserId BIGINT NOT NULL UNIQUE,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE [CartItems] (
    Id BIGINT PRIMARY KEY IDENTITY(1,1),
    CartId BIGINT NOT NULL,
    ProductId BIGINT NOT NULL,
    ProductName NVARCHAR(255),
    Price DECIMAL(10, 2),
    Quantity INT,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (CartId) REFERENCES Cart(Id)
);

-- Create indexes
CREATE INDEX idx_orders_user_id ON [Orders](UserId);
CREATE INDEX idx_orders_status ON [Orders](Status);
CREATE INDEX idx_orders_created_at ON [Orders](CreatedAt);
CREATE INDEX idx_cart_user_id ON [Cart](UserId);
CREATE INDEX idx_address_user_id ON [Address](UserId);
```

---

## 5.5 Dữ Liệu Test (Sample Data)

### 📍 **Sample Users (Auth Service)**

```sql
-- PostgreSQL
INSERT INTO "user" (username, password, email, full_name, phone, role)
VALUES 
    ('sysadmin', '$2a$10$...(hashed)', 'sysadmin@system.local', 'System Admin', '0123456789', 'ADMIN'),
    ('user1', '$2a$10$...(hashed)', 'user1@example.com', 'Nguyễn Văn A', '0987654321', 'USER');
```

**Tài khoản test khả dụng:** (xem [09-TESTING_VERIFICATION.md](./09-TESTING_VERIFICATION.md))

---

## 5.6 Thứ Tự Migration (nếu có)

Dự án **sử dụng Hibernate DDL (auto)** chứ không dùng Flyway/Liquibase, nên:

**Quy trình:**
1. Database được tạo bằng Docker (hoặc thủ công)
2. Khi service Spring Boot khởi động → Hibernate tự động tạo/update tables
3. `spring.jpa.hibernate.ddl-auto=update` trong application.properties

**Điều này có nghĩa:**
- ✅ Không cần chạy migration thủ công
- ❌ Không nên dùng trong production (ddl-auto=update nguy hiểm)
- ⚠️ Để production → đổi thành `validate` (kiểm tra schema)

---

## 5.7 Kiểm Tra Databases

### ✅ **Verify Docker Containers Running**

```bash
docker ps

# Output mong đợi:
# postgres-auth-db    postgres:15
# mysql-product-db    mysql:8.0
# sqlserver-order-db  mssql/server:2022
# redis-cache         redis:7
# adminer             adminer:latest
```

### ✅ **Test PostgreSQL Connection**

```bash
# Dùng psql
psql -U auth_user -d nckh_auth -h localhost -c "SELECT 1;"

# Or dùng DBeaver: Connect với:
# Host: localhost
# Port: 5432
# Database: nckh_auth
# User: auth_user
# Password: auth@123456
```

### ✅ **Test MySQL Connection**

```bash
# Dùng mysql client
mysql -u product_user -p -h localhost -D shopquanao -c "SELECT 1;"

# Or dùng DBeaver
```

### ✅ **Test SQL Server Connection**

```bash
# Dùng sqlcmd
sqlcmd -S localhost -U orderuser -P Order@123 -d order_dB -Q "SELECT 1;"

# Or dùng DBeaver/SSMS
```

### ✅ **Test Redis Connection**

```bash
# Dùng redis-cli
redis-cli -h localhost ping
# Output mong đợi: PONG
```

---

## 5.8 Backup & Restore Databases

### **PostgreSQL Backup**

```bash
# Backup
pg_dump -U auth_user -h localhost nckh_auth > backup.sql

# Restore
psql -U auth_user -h localhost nckh_auth < backup.sql
```

### **MySQL Backup**

```bash
# Backup
mysqldump -u product_user -p -h localhost shopquanao > backup.sql

# Restore
mysql -u product_user -p -h localhost shopquanao < backup.sql
```

### **SQL Server Backup**

```sql
-- Backup
BACKUP DATABASE [order_dB] 
TO DISK = 'C:\backup\order_dB.bak'
WITH INIT, MEDIANAME = 'order_backup';

-- Restore
RESTORE DATABASE [order_dB]
FROM DISK = 'C:\backup\order_dB.bak'
WITH REPLACE;
```

---

## 5.9 Troubleshooting Database

| Vấn Đề | Nguyên Nhân | Giải Pháp |
|--------|-----------|---------|
| "Connection refused" | Database container chưa khởi động | `docker ps`, chờ container sẵn sàng |
| "Authentication failed" | Username/password sai | Kiểm tra credentials ở .env |
| "Database does not exist" | Chưa chạy init script | Chạy init script, hoặc restart docker |
| "Port already in use" | Port đang bị dùng | Thay port khác, hoặc kill process |

---

