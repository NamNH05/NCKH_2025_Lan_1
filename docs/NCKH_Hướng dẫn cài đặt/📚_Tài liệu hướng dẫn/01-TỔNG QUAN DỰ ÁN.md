# 📋 01 - TỔNG QUAN DỰ ÁN

Dự án **NCKH** là một nền tảng **E-Commerce đầy đủ** với:
- ✅ Quản lý sản phẩm (Product Service)
- ✅ Quản lý đơn hàng (Order Service)  
- ✅ Xác thực người dùng (Auth Service)
- ✅ Ghi nhật ký hoạt động (Audit Service)
- ✅ API Gateway để xác thực
- ✅ Frontend React đầu tiên

**Kiến trúc**: Microservices (4 backend services độc lập)

---

## Các Thành Phần

| Thành Phần | Port | Tech | Database |
|-----------|------|------|----------|
| Auth | 8001 | Java Spring Boot | PostgreSQL 5432 |
| Product | 8003 | Java Spring Boot | MySQL 3306 |
| Order | 8002 | Java Spring Boot | SQL Server 1433 |
| Audit | 8004 | Java Spring Boot | PostgreSQL 5432 |
| Gateway | 3000 | Node.js Express | - |
| Frontend | 5173 | React + Vite | - |

---

## Luồng Tương Tác

```
Frontend (React, 5173)
        ↓ HTTP
API Gateway (Express, 3000)
        ↓
Auth(8001) + Product(8003) + Order(8002) + Audit(8004)
        ↓
PostgreSQL + MySQL + SQL Server + Redis
```

---

## Thông Tin Đăng Nhập

| Người Dùng | Username | Password |
|-----------|----------|----------|
| Admin | sysadmin | 1234 |
| User | 23810310082 | 123456 |

---

**👉 Tiếp theo: Đọc `02-YÊU CẦU MÔI TRƯỜNG.md`**

