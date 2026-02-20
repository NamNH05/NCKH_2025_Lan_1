# 🔧 10 - KHẮC PHỤC SỰ CỐ

---

## Docker Issues

### "Docker daemon is not running"

```
Windows: Mở Docker Desktop từ Start menu
Mac: docker desktop có thể chưa khởi động
Linux: systemctl start docker
```

### Containers không khởi động

```bash
# Kiểm tra logs
docker logs <container_name>

# Reset toàn bộ (xóa dữ liệu cũ)
docker-compose -f docker-compose-all-services.yml down -v
docker-compose -f docker-compose-all-services.yml up -d
```

---

## Port Issues

### "Port already in use"

**Nếu port 5432 (PostgreSQL) bận:**
```bash
# Windows
netstat -ano | findstr :5432
taskkill /PID <PID> /F

# Mac/Linux
lsof -i :5432
kill -9 <PID>
```

**Hoặc đổi port trong docker-compose:**
```yaml
ports:
  - "5433:5432"  # Dùng port 5433 thay vì 5432
```

---

## Maven Issues

### "Maven build failed"

```bash
# Clean & rebuild
mvn clean install

# Hoặc xóa .m2 cache
rm -rf ~/.m2/repository
mvn clean install
```

### "Cannot find Spring Boot main class"

- Kiểm tra `pom.xml` có `spring-boot-maven-plugin`
- Đảm bảo main class tồn tại

---

## npm Issues

### "npm: command not found"

- Node.js chưa cài hoặc PATH sai
- Download từ https://nodejs.org/

### "Cannot find module"

```bash
npm install
# Hoặc
npm install --legacy-peer-deps
```

---

## Database Connection Issues

### PostgreSQL connection refused

```bash
# Kiểm tra PostgreSQL chạy
docker ps | grep postgres

# Kiểm tra password
docker logs postgres-auth-db

# Test kết nối
psql -U auth_user -d nckh_auth -h localhost
```

### MySQL connection refused

```bash
# Kiểm tra MySQL chạy
docker ps | grep mysql

# Test kết nối
mysql -u product_user -p shopquanao
```

### SQL Server connection issues

```bash
# Kiểm tra SQL Server chạy
docker ps | grep sqlserver

# Logs
docker logs sqlserver-order-db
```

---

## JWT Token Issues

### "Invalid token" error

- Token hết hạn → Đăng nhập lại
- JWT secret không trùng giữa Auth Service & API Gateway
  - Kiểm tra `jwt.secret` trong `application.properties` (Auth Service)
  - Kiểm tra `JWT_SECRET` trong `api-gateway/.env`
  - Chúng phải **giống nhau**

---

## CORS Issues

### "CORS error" trong Frontend

```
Access to XMLHttpRequest from origin 'http://localhost:5173' 
has been blocked by CORS policy
```

**Cách fix:**
1. Kiểm tra `CORS_ORIGIN` trong `api-gateway/.env`
2. Phải là: `http://localhost:5173`
3. Restart API Gateway

---

## Services Won't Start

### "Connection refused"

- Kiểm tra databases chạy: `docker ps`
- Kiểm tra port không bận: `netstat -ano | findstr :5432`
- Kiểm tra credentials trong `application.properties`

### "Out of memory"

- Tăng memory cho Docker (Docker Desktop settings)
- Hoặc đóng ứng dụng khác để giải phóng RAM

---

## Frontend Blank Page

### Check browser console (F12)

- Xem error messages
- Kiểm tra API URL đúng trong `.env`
- Kiểm tra API Gateway chạy

---

## Quick Restart

```bash
# Dừng tất cả
docker-compose -f docker-compose-all-services.yml down

# Xóa volumes (nếu cần reset dữ liệu)
docker-compose -f docker-compose-all-services.yml down -v

# Khởi động lại
docker-compose -f docker-compose-all-services.yml up -d
```

---

**Nếu vẫn có vấn đề → Hãy xem logs chi tiết & Google error message**

