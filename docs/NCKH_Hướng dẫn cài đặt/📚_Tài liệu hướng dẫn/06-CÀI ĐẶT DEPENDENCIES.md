# 📚 06 - CÀI ĐẶT DEPENDENCIES

---

## Backend (Maven)

### Cài Đặt Toàn Bộ Backend

```bash
# Vào folder auth service
cd backend/auth-service/Nckh-Lu-n
mvn clean install

# Vào folder product service
cd ../../product-service/Tien/Tien
mvn clean install

# Vào folder order service
cd ../../order-service/Nckh-C-ng
mvn clean install

# Vào folder audit service
cd ../../audit-service/Nckh-Hi-p
mvn clean install
```

---

## Frontend (npm)

```bash
# Vào folder frontend
cd frontend/web-client

# Cài dependencies
npm install

# Hoặc dùng yarn
yarn install
```

---

## API Gateway (npm)

```bash
# Vào folder gateway
cd api-gateway

# Cài dependencies
npm install

# Hoặc dùng yarn
yarn install
```

---

## Main Dependencies

### Backend (Java Spring Boot)
- `spring-boot-starter-web` - Web framework
- `spring-boot-starter-data-jpa` - Database ORM
- `spring-boot-starter-security` - Authentication
- `spring-boot-starter-data-redis` - Redis cache
- Database drivers: `postgresql`, `mysql`, `mssql-jdbc`

### Frontend (React)
- `react` - UI framework
- `react-router-dom` - Routing
- `axios` - HTTP client
- `antd` - UI components

### API Gateway (Express)
- `express` - Web framework
- `jsonwebtoken` - JWT handling
- `cors` - CORS middleware
- `axios` - HTTP client

---

**👉 Tiếp theo: `07-DOCKER & CONTAINER.md`**

