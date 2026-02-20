# 🚀 08 - HƯỚNG DẪN KHỞI ĐỘNG

---

## Bước 1: Bắt Đầu Docker Containers

```bash
docker-compose -f docker-compose-all-services.yml up -d

# Kiểm tra
docker ps
```

Chờ 30 giây để databases ready.

---

## Bước 2: Cài Dependencies

### Backend Services

```bash
cd backend/auth-service/Nckh-Lu-n && mvn clean install && cd ../../../../
cd backend/product-service/Tien/Tien && mvn clean install && cd ../../../../
cd backend/order-service/Nckh-C-ng && mvn clean install && cd ../../../../
cd backend/audit-service/Nckh-Hi-p && mvn clean install && cd ../../../../
```

### Frontend & Gateway

```bash
cd api-gateway && npm install && cd ..
cd frontend/web-client && npm install && cd ../..
```

---

## Bước 3: Chạy Services (Mỗi Service Một Terminal)

### Terminal 1: Auth Service
```bash
cd backend/auth-service/Nckh-Lu-n
mvn spring-boot:run
# Chờ: "Started AuthServiceApplication"
```

### Terminal 2: Product Service
```bash
cd backend/product-service/Tien/Tien
mvn spring-boot:run
```

### Terminal 3: Order Service
```bash
cd backend/order-service/Nckh-C-ng
mvn spring-boot:run
```

### Terminal 4: Audit Service
```bash
cd backend/audit-service/Nckh-Hi-p
mvn spring-boot:run
```

### Terminal 5: API Gateway
```bash
cd api-gateway
npm start
```

### Terminal 6: Frontend
```bash
cd frontend/web-client
npm run dev
```

---

## Bước 4: Kiểm Tra

- **Frontend**: http://localhost:5173
- **API Gateway**: http://localhost:3000
- **Auth Service**: http://localhost:8001/health
- **Product Service**: http://localhost:8003/health
- **Order Service**: http://localhost:8002/health
- **Audit Service**: http://localhost:8004/health

---

## Bước 5: Đăng Nhập

**URL**: http://localhost:5173

| Tài Khoản | Username | Password |
|-----------|----------|----------|
| Admin | sysadmin | 1234 |
| User | 23810310082 | 123456 |

---

## 🎉 Xong!

Hệ thống đã chạy. Bạn có thể:
- Xem sản phẩm
- Thêm vào giỏ hàng
- Tạo đơn hàng
- Xem lịch sử

---

**👉 Tiếp theo: `09-TEST & XÁC THỰC.md`**

