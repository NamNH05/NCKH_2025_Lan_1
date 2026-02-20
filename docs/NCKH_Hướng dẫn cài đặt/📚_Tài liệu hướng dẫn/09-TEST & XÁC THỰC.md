# 🧪 09 - TEST & XÁC THỰC

---

## Health Checks

### Kiểm Tra Tất Cả Services

```bash
# Auth Service
curl http://localhost:8001/health

# Product Service
curl http://localhost:8003/health

# Order Service
curl http://localhost:8002/health

# Audit Service
curl http://localhost:8004/health

# API Gateway
curl http://localhost:3000/health
```

Nếu response là `{"status":"UP"}` → Service hoạt động.

---

## API Testing (Postman)

### 1. Đăng Nhập (Get Token)

```
Method: POST
URL: http://localhost:3000/api/auth/login
Body (JSON):
{
  "username": "sysadmin",
  "password": "1234"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "username": "sysadmin",
    "email": "sysadmin@system.local"
  }
}
```

Lưu `token` để dùng trong requests tiếp theo.

---

### 2. Get Products

```
Method: GET
URL: http://localhost:3000/api/products
Header:
  Authorization: Bearer <token_từ_step_1>
```

---

### 3. Get Orders

```
Method: GET
URL: http://localhost:3000/api/orders
Header:
  Authorization: Bearer <token_từ_step_1>
```

---

## Swagger UI (Tài Liệu API Tự Động)

Xem API documentation tại:

- Auth: http://localhost:8001/swagger-ui.html
- Product: http://localhost:8003/swagger-ui.html
- Order: http://localhost:8002/swagger-ui.html
- Audit: http://localhost:8004/swagger-ui.html

---

## Database Access

### Adminer Web UI
```
URL: http://localhost:8080
```

Chọn database:
- Server: postgresql (hoặc mysql, sqlserver)
- User: auth_user (hoặc product_user, orderuser)
- Password: Credentials tương ứng

---

## Frontend Testing

1. Mở http://localhost:5173
2. Đăng nhập bằng `sysadmin / 1234`
3. Xem sản phẩm
4. Thêm vào giỏ hàng
5. Thanh toán
6. Xem đơn hàng

---

**👉 Tiếp theo: `10-KHẮC PHỤC SỰ CỐ.md`**

