# 🚀 Phần 8 – Thứ Tự Chạy Hệ Thống (Startup Guide)

---

## 8.1 Tổng Quan Quy Trình

**Thứ tự bắt buộc:**
1. **Docker + Databases** (PostgreSQL, MySQL, SQL Server, Redis)
2. **Backend Services** (Auth → Product → Order → Audit)
3. **API Gateway** (Node.js Express)
4. **Frontend** (React Vite)

**Tại sao thứ tự này?**
- Backend cần database sẵn sàng
- Gateway cần backend services sẵn sàng
- Frontend cần gateway sẵn sàng

---

## 8.2 Bước 1: Khởi Động Databases (Docker)

### ⏱️ **Thời gian: ~2-3 phút**

```bash
# Từ root directory
cd e:\Nghien_cuu_kh\Nckh

# Khởi động tất cả containers
docker-compose -f docker-compose-all-services.yml up -d

# Chờ containers healthy (khoảng 1-2 phút)
docker-compose -f docker-compose-all-services.yml ps
```

**Kiểm tra:**
```bash
# Tất cả containers phải có status "Up"
docker ps

# Output mong đợi:
# postgres-auth-db     postgres:15     Up 2 minutes
# mysql-product-db     mysql:8.0       Up 1 minute
# sqlserver-order-db   mssql/server    Up 50 seconds
# redis-cache          redis:7         Up 1 minute
# adminer              adminer         Up 1 minute
```

**Troubleshooting:**
```bash
# Xem logs nếu container crash
docker logs postgres-auth-db
docker logs mysql-product-db

# Restart nếu cần
docker restart postgres-auth-db
```

---

## 8.3 Bước 2: Cài Dependencies (nếu chưa cài)

### ⏱️ **Thời gian: ~5-10 phút**

**Backend (Maven):**
```bash
cd backend/auth-service/Nckh-Lu-n
mvn clean install -DskipTests

cd ../../../backend/product-service/Tien/Tien
mvn clean install -DskipTests

cd ../../../backend/order-service/Nckh-C-ng
mvn clean install -DskipTests

cd ../../../backend/audit-service/Nckh-Hi-p
mvn clean install -DskipTests
```

**Frontend:**
```bash
cd frontend/web-client
npm install
```

**Gateway:**
```bash
cd api-gateway
npm install
```

---

## 8.4 Bước 3: Khởi Động Backend Services

### ⏱️ **Thời gian: ~2-3 phút cho tất cả services**

**Sử dụng 4 Terminals (hoặc 4 Tab PowerShell):**

#### **Terminal 1 – Auth Service (Port 8001)**

```powershell
cd backend/auth-service/Nckh-Lu-n
mvn spring-boot:run

# Hoặc chạy từ IDE (Run button)
# Expected output:
# Started AuthServiceApplication in X.XXX seconds
# Server running on: http://localhost:8001
```

**Đợi tới khi thấy:**
```
AuthServiceApplication started successfully
Listening on port 8001
```

---

#### **Terminal 2 – Product Service (Port 8003)**

```powershell
cd backend/product-service/Tien/Tien
mvn spring-boot:run

# Expected output:
# Started TienApplication in X.XXX seconds
# Server running on: http://localhost:8003
```

---

#### **Terminal 3 – Order Service (Port 8002)**

```powershell
cd backend/order-service/Nckh-C-ng
mvn spring-boot:run

# Expected output:
# Started OrderServiceApplication in X.XXX seconds
# Server running on: http://localhost:8002
```

---

#### **Terminal 4 – Audit Service (Port 8004)**

```powershell
cd backend/audit-service/Nckh-Hi-p
mvn spring-boot:run

# Expected output:
# Started AuditServiceApplication in X.XXX seconds
# Server running on: http://localhost:8004
```

---

### ✅ **Verify Backend Services Running**

```bash
# Test endpoint sẽ chạy chậm lần đầu vì Hibernate tạo tables
# Chờ khoảng 30-60 giây sau khi service khởi động

# Auth Service (có thể login)
curl http://localhost:8001/swagger-ui.html

# Product Service
curl http://localhost:8003/swagger-ui.html

# Order Service
curl http://localhost:8002/swagger-ui.html

# Audit Service
curl http://localhost:8004/swagger-ui.html
```

---

## 8.5 Bước 4: Khởi Động API Gateway

### ⏱️ **Thời gian: ~30 giây**

**Terminal 5:**

```powershell
cd api-gateway

# Cách 1: Dùng npm start
npm start

# Cách 2: Dùng npm run dev (auto-restart với nodemon)
npm run dev

# Expected output:
# API Gateway running on port 3000
# Server listening at http://localhost:3000
```

**Verify:**
```bash
curl http://localhost:3000/health

# Output mong đợi:
# {"status":"API Gateway running","timestamp":"2024-01-16T..."}
```

---

## 8.6 Bước 5: Khởi Động Frontend

### ⏱️ **Thời gian: ~1-2 phút**

**Terminal 6:**

```powershell
cd frontend/web-client
npm run dev

# Expected output:
# VITE v7.2.4 ready in XXX ms
# ➜ Local: http://localhost:5173/
# ➜ press h to show help
```

**Mở browser:**
```
http://localhost:5173
```

---

## 8.7 Kiểm Tra Toàn Hệ Thống

### ✅ **Services Health Check**

```bash
# API Gateway
curl http://localhost:3000/health
# → {"status":"API Gateway running"}

# Auth Service
curl http://localhost:8001/health
# → {"status":"UP"} hoặc similar

# Product Service
curl http://localhost:8003/health
# → {"status":"UP"}

# Order Service
curl http://localhost:8002/health
# → {"status":"UP"}
```

### ✅ **Database Connections**

```bash
# Test từ Adminer (web UI)
http://localhost:8080

# Hoặc từ DBeaver:
# PostgreSQL: localhost:5432 (nckh_auth)
# MySQL: localhost:3306 (shopquanao)
# SQL Server: localhost:1433 (order_dB)
```

### ✅ **Frontend**

```
http://localhost:5173
# Có thể thấy login page
```

---

## 8.8 Test Đăng Nhập (Quick Verification)

### 🔓 **Tài Khoản Test**

```
Admin:
  Username: sysadmin
  Password: 1234

User:
  Username: 23810310082
  Password: 123456
```

### 📝 **Bước Test:**

1. **Mở browser:** http://localhost:5173
2. **Nhập username & password** (một trong tài khoản trên)
3. **Nhấn Login**
4. **Verify:** Có redirect tới dashboard/home page

---

## 8.9 Terminal Layout Lý Tưởng

```
┌─────────────────────────────────────────────────┐
│ Terminal 1: Auth Service (Port 8001)            │
├─────────────────────────────────────────────────┤
│ Terminal 2: Product Service (Port 8003)         │
├─────────────────────────────────────────────────┤
│ Terminal 3: Order Service (Port 8002)           │
├─────────────────────────────────────────────────┤
│ Terminal 4: Audit Service (Port 8004)           │
├─────────────────────────────────────────────────┤
│ Terminal 5: API Gateway (Port 3000)             │
├─────────────────────────────────────────────────┤
│ Terminal 6: Frontend Dev (Port 5173)            │
├─────────────────────────────────────────────────┤
│ Terminal 7: Docker (docker ps)                  │
└─────────────────────────────────────────────────┘
```

**Using VS Code:**
- Mở 6 Terminals cùng lúc
- Terminal → Run Task → "start all services" (sẽ tạo sau)

---

## 8.10 Startup Script (Automation)

### **Windows (start-all-services.bat)**

```batch
@echo off
color 0A
echo.
echo ===== NCKH PROJECT - START ALL SERVICES =====
echo.
echo Step 1: Starting Docker containers...
docker-compose -f docker-compose-all-services.yml up -d
echo.
echo Waiting 30 seconds for databases to be ready...
timeout /t 30

echo.
echo Step 2: Starting backend services in 30 seconds...
echo   Terminal 1: Auth Service (Port 8001)
echo   Terminal 2: Product Service (Port 8003)
echo   Terminal 3: Order Service (Port 8002)
echo   Terminal 4: Audit Service (Port 8004)
echo.
pause

REM Start services in new windows (requires separate terminals)
start "Auth Service" cmd /k "cd backend\auth-service\Nckh-Lu-n && mvn spring-boot:run"
start "Product Service" cmd /k "cd backend\product-service\Tien\Tien && mvn spring-boot:run"
start "Order Service" cmd /k "cd backend\order-service\Nckh-C-ng && mvn spring-boot:run"
start "Audit Service" cmd /k "cd backend\audit-service\Nckh-Hi-p && mvn spring-boot:run"

echo.
echo Waiting 60 seconds for backend services...
timeout /t 60

echo.
echo Step 3: Starting API Gateway...
start "API Gateway" cmd /k "cd api-gateway && npm start"

echo.
echo Waiting 10 seconds for API Gateway...
timeout /t 10

echo.
echo Step 4: Starting Frontend...
start "Frontend" cmd /k "cd frontend\web-client && npm run dev"

echo.
echo ===== ALL SERVICES STARTED =====
echo.
echo Open browser: http://localhost:5173
echo.
pause
```

**Sử dụng:**
```bash
.\start-all-services.bat
```

---

## 8.11 Shutdown

**Dừng toàn bộ hệ thống:**

```bash
# 1. Dừng Backend Services (Ctrl+C trong 4 terminals)
# 2. Dừng API Gateway (Ctrl+C)
# 3. Dừng Frontend (Ctrl+C)
# 4. Dừng Docker
docker-compose -f docker-compose-all-services.yml down
```

---

## 8.12 Troubleshooting Startup

| Vấn Đề | Nguyên Nhân | Giải Pháp |
|--------|-----------|---------|
| "Connection refused: 5432" | PostgreSQL chưa ready | Chờ 30s-1min để database khởi động |
| "Port 8001 already in use" | Service khác dùng port | `lsof -i :8001` → kill process |
| "AuthServiceApplication failed" | Database error | Kiểm tra `docker logs postgres-auth-db` |
| "Cannot GET /api/auth/login" | Gateway không forward | Kiểm tra `api-gateway/server.js` BACKEND_URL |
| "CORS error" | Frontend CORS setting | Kiểm tra `api-gateway/.env` CORS_ORIGIN |
| "500 error from API" | Backend crash | Xem logs từ backend service |

---

## 8.13 Verify Checklist

- [ ] Docker containers running (`docker ps`)
- [ ] Auth Service running (port 8001)
- [ ] Product Service running (port 8003)
- [ ] Order Service running (port 8002)
- [ ] Audit Service running (port 8004)
- [ ] API Gateway running (port 3000)
- [ ] Frontend running (port 5173)
- [ ] Can access frontend (http://localhost:5173)
- [ ] Can login with sysadmin / 1234
- [ ] No errors in logs
- [ ] API Gateway health check returns 200
- [ ] Database connections established

---

