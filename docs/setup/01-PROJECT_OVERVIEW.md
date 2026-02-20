# 📋 NCKH Project - Hướng Dẫn Setup Toàn Diện

**Dành cho: Developers mới tham gia dự án**
**Cập nhật lần cuối: Tháng 1 năm 2026**

---

## 🎯 Phần 1 – Tổng Quan Dự Án

### 1.1 Mô Tả Dự Án
**NCKH** là một **nền tảng thương mại điện tử đầy đủ** (Full-stack e-commerce platform) với các tính năng chính:

- **Quản lý sản phẩm**: Danh mục, thông tin sản phẩm, giá cả
- **Quản lý đơn hàng**: Giỏ hàng, thanh toán, trạng thái đơn hàng
- **Xác thực người dùng**: Đăng ký, đăng nhập, JWT token, phân quyền
- **Audit & Logging**: Ghi nhật ký hoạt động người dùng
- **API Gateway**: Xác thực JWT, phân luồng requests

**Kiến trúc**: Microservices (4 backend services độc lập)

---

### 1.2 Các Thành Phần Chính

#### 🔐 Backend Services (Java Spring Boot)

| Service | Cổng | Database | Mục Đích |
|---------|------|----------|---------|
| **Auth Service** | 8001 | PostgreSQL | Đăng ký, đăng nhập, JWT token, quản lý user |
| **Product Service** | 8003 | MySQL | Quản lý sản phẩm, danh mục, giá cả |
| **Order Service** | 8002 | SQL Server | Quản lý đơn hàng, giỏ hàng, thanh toán |
| **Audit Service** | 8004 | PostgreSQL | Ghi nhật ký hoạt động, audit trail |

#### 🌐 API Gateway (Node.js Express)
- **Port**: 3000
- **Mục đích**: Xác thực JWT, phân luồng requests đến các backend service

#### 💻 Frontend (React + Vite)
- **Port**: 5173
- **Mục đích**: Giao diện người dùng, dashboard, quản lý đơn hàng

#### 🗄️ Databases
- **PostgreSQL** (Port 5432): Auth & Audit Services
- **MySQL** (Port 3306): Product Service
- **SQL Server** (Port 1433): Order Service
- **Redis** (Port 6379): Cache

---

### 1.3 Luồng Tương Tác Hệ Thống

```
┌──────────────┐
│   Frontend   │ (React, Port 5173)
│   (Browser)  │
└──────┬───────┘
       │ HTTP/REST
       ▼
┌──────────────────────┐
│   API Gateway        │ (Express, Port 3000)
│   (JWT Verification) │
└──────┬───────────────┘
       │
   ┌───┴────────────────────────────────────┬──────────────────┐
   │                                        │                  │
   ▼                                        ▼                  ▼
┌─────────────┐                     ┌──────────────┐   ┌──────────────┐
│ Auth        │  (Port 8001)        │ Product      │   │ Order        │
│ Service     │──PostgreSQL         │ Service      │   │ Service      │
└─────────────┘  (Port 5432)        └──────────────┘   └──────────────┘
                                        MySQL             SQL Server
                                     (Port 3306)       (Port 1433)

┌────────────────────────────────────────────────────────────────────┐
│                    Audit Service (Port 8004)                       │
│                     – Ghi nhật ký tất cả hoạt động –              │
│                         PostgreSQL (5432)                          │
└────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────┐
│            Redis Cache (Port 6379) – Cache for all services         │
└──────────────────────────────────────────────────────────────────────┘
```

**Quy trình một request điển hình:**
1. Frontend gửi request đến API Gateway (http://localhost:3000)
2. API Gateway xác thực JWT token
3. API Gateway phân luồng request đến service tương ứng (Auth/Product/Order)
4. Service xử lý yêu cầu và query database tương ứng
5. Service trả về response cho API Gateway
6. API Gateway gửi response về Frontend

---

## 🔗 Đọc Thêm

Tài liệu chi tiết từng phần đặt trong folder `docs/setup/`:
- **02-REQUIREMENTS.md** - Yêu cầu môi trường
- **03-CODE_STRUCTURE.md** - Cấu trúc source code
- **04-ENV_CONFIGURATION.md** - Biến môi trường & config
- **05-DATABASE_SETUP.md** - Database & SQL
- **06-DEPENDENCIES.md** - Cài đặt dependencies
- **07-DOCKER_SETUP.md** - Docker & container
- **08-STARTUP_GUIDE.md** - Thứ tự chạy hệ thống
- **09-TESTING_VERIFICATION.md** - Test & verify
- **10-TROUBLESHOOTING.md** - Lỗi thường gặp

---

