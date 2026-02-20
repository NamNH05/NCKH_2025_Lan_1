# 🔧 04 - CẤU HÌNH MÔI TRƯỜNG

---

## Biến Môi Trường Quan Trọng

### API Gateway (.env)

```
PORT=3000
JWT_SECRET=caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a2df52c
BACKEND_URL_AUTH=http://localhost:8001
BACKEND_URL_PRODUCT=http://localhost:8003
BACKEND_URL_ORDER=http://localhost:8002
CORS_ORIGIN=http://localhost:5173
NODE_ENV=development
```

### Auth Service (application.properties)

```properties
server.port=8001
spring.datasource.url=jdbc:postgresql://localhost:5432/nckh_auth
spring.datasource.username=auth_user
spring.datasource.password=auth@123456
jwt.secret=caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a2df52c
bootstrap.admin.username=sysadmin
bootstrap.admin.password=1234
```

### Product Service (application.properties)

```properties
server.port=8003
spring.datasource.url=jdbc:mysql://localhost:3306/shopquanao
spring.datasource.username=product_user
spring.datasource.password=product@123456
```

### Order Service (application.properties)

```properties
server.port=8002
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=order_dB
spring.datasource.username=orderuser
spring.datasource.password=Order@123
```

### Frontend (.env)

```
VITE_API_URL=http://localhost:3000
VITE_TIMEOUT=30000
```

---

## 🔐 Database Credentials

| Database | Host | Port | User | Password |
|----------|------|------|------|----------|
| PostgreSQL | localhost | 5432 | auth_user | auth@123456 |
| MySQL | localhost | 3306 | product_user | product@123456 |
| SQL Server | localhost | 1433 | orderuser | Order@123 |
| Redis | localhost | 6379 | (none) | - |

---

## ⚠️ Quy Tắc

- ❌ **KHÔNG commit .env** (bảo mật mật khẩu)
- ✅ **COMMIT .env.example** (template)
- Copy `.env.example` → `.env` rồi điền mật khẩu

---

**👉 Tiếp theo: `05-DATABASE & SQL.md`**

