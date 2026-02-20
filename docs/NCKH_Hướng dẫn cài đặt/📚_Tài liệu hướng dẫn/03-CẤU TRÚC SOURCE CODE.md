# 🗂️ 03 - CẤU TRÚC SOURCE CODE

---

## Cấu Trúc Folder Chính

```
NCKH/
├── api-gateway/              → API Gateway (Port 3000)
├── backend/
│   ├── auth-service/        → Auth (Port 8001, PostgreSQL)
│   ├── product-service/     → Product (Port 8003, MySQL)
│   ├── order-service/       → Order (Port 8002, SQL Server)
│   └── audit-service/       → Audit (Port 8004, PostgreSQL)
├── frontend/
│   └── web-client/          → Frontend React (Port 5173)
└── database/                 → SQL scripts khởi tạo
```

---

## 4 Backend Services

### 1️⃣ Auth Service (Port 8001)
- Mục đích: Đăng ký, đăng nhập, JWT token
- Database: PostgreSQL
- Folder: `backend/auth-service/Nckh-Lu-n/`

### 2️⃣ Product Service (Port 8003)
- Mục đích: Quản lý sản phẩm, danh mục
- Database: MySQL
- Folder: `backend/product-service/Tien/Tien/`

### 3️⃣ Order Service (Port 8002)
- Mục đích: Quản lý đơn hàng, thanh toán
- Database: SQL Server
- Folder: `backend/order-service/Nckh-C-ng/`

### 4️⃣ Audit Service (Port 8004)
- Mục đích: Ghi nhật ký hoạt động
- Database: PostgreSQL
- Folder: `backend/audit-service/Nckh-Hi-p/`

---

## API Gateway

- Mục đích: Xác thực JWT, phân luồng requests
- Framework: Node.js Express
- Port: 3000
- Folder: `api-gateway/`

---

## Frontend

- Mục đích: Giao diện người dùng
- Framework: React + Vite + Ant Design
- Port: 5173
- Folder: `frontend/web-client/`

---

## Cấu Trúc Java Service (Ví Dụ: Auth)

```
auth-service/Nckh-Lu-n/
├── pom.xml                   → Maven dependencies
├── src/main/
│   ├── java/
│   │   └── vn/id/luannv/
│   │       ├── controller/   → REST APIs
│   │       ├── service/      → Business logic
│   │       ├── repository/   → Database access
│   │       ├── entity/       → JPA models
│   │       └── config/       → Security, Database config
│   └── resources/
│       └── application.properties  → Config (DB, JWT, etc.)
└── target/                   → Compiled files (không cần commit)
```

---

## File Quan Trọng

| File | Mục Đích |
|------|---------|
| `pom.xml` | Maven dependencies |
| `application.properties` | Database & service config |
| `Controller.java` | REST API endpoints |
| `Service.java` | Business logic |
| `Repository.java` | Database queries |
| `Entity.java` | Database models (@Entity) |

---

**👉 Tiếp theo: `04-CẤU HÌNH MÔI TRƯỜNG.md`**

