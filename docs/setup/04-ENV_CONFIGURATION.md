# 🔧 Phần 4 – Biến Môi Trường & Configuration

---

## 4.1 Tổng Quan Biến Môi Trường

Các biến môi trường (.env files) chứa **thông tin nhạy cảm** (password, secret keys) và **cài đặt khác nhau** cho từng environment (local/dev/prod).

**🔒 Quy tắc quan trọng:**
- ❌ **KHÔNG bao giờ commit .env file** (mật khẩu lộ)
- ✅ **LUÔN commit .env.example** (mẫu không có mật khẩu thực)
- ✅ **Copy .env.example → .env** và điền mật khẩu thực tế

---

## 4.2 Liệt Kê Tất Cả Biến Môi Trường

### 📍 **API Gateway (.env)**

**File vị trí**: `api-gateway/.env`

| Biến | Giá Trị Mặc Định | Ý Nghĩa | Bắt Buộc |
|------|-----------------|--------|----------|
| `PORT` | 3000 | Port chạy API Gateway | ✅ |
| `JWT_SECRET` | (tùy ý, dài) | Secret key để verify JWT | ✅ |
| `BACKEND_URL_AUTH` | http://localhost:8001 | URL Auth Service | ✅ |
| `BACKEND_URL_PRODUCT` | http://localhost:8003 | URL Product Service | ✅ |
| `BACKEND_URL_ORDER` | http://localhost:8002 | URL Order Service | ✅ |
| `BACKEND_URL_AUDIT` | http://localhost:8004 | URL Audit Service | ⚠️ |
| `CORS_ORIGIN` | http://localhost:5173 | Frontend URL (CORS) | ✅ |
| `NODE_ENV` | development | Environment | ✅ |

**Mô tả chi tiết:**

```
PORT=3000
  → Port mà API Gateway lắng nghe
  → Client (Frontend) sẽ gọi http://localhost:3000
  → Nếu port 3000 đang bận, đổi sang port khác (e.g., 3001)

JWT_SECRET=your_super_secret_key_here_minimum_32_characters_long
  → Secret key để verify JWT token
  → Phải dài ít nhất 32 ký tự
  → Phải giống nhau với Auth Service (trong application.properties)
  → Nếu khác nhau → token sẽ không verify được
  → Ví dụ: caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a2df52c

BACKEND_URL_AUTH=http://localhost:8001
  → URL của Auth Service
  → Khi frontend gọi /api/auth/* → gateway sẽ forward đến http://localhost:8001/api/auth/*

BACKEND_URL_PRODUCT=http://localhost:8003
  → URL của Product Service
  → Khi frontend gọi /api/products/* → gateway sẽ forward đến http://localhost:8003/api/products/*

BACKEND_URL_ORDER=http://localhost:8002
  → URL của Order Service
  → Khi frontend gọi /api/orders/* → gateway sẽ forward đến http://localhost:8002/api/orders/*

CORS_ORIGIN=http://localhost:5173
  → Frontend domain được phép gọi API
  → Ngăn chặn Cross-Origin attacks
  → Nếu frontend chạy trên port khác → thêm domain đó
  → Ví dụ: http://localhost:3000,http://localhost:5173

NODE_ENV=development
  → Environment: development hoặc production
  → Ảnh hưởng tới logging, error messages
```

---

### 🐘 **Auth Service (application.properties)**

**File vị trí**: `backend/auth-service/Nckh-Lu-n/src/main/resources/application.properties`

| Biến | Giá Trị Mặc Định | Ý Nghĩa | Bắt Buộc |
|------|-----------------|--------|----------|
| `server.port` | 8001 | Port chạy Auth Service | ✅ |
| `spring.datasource.url` | jdbc:postgresql://localhost:5432/nckh_auth | PostgreSQL URL | ✅ |
| `spring.datasource.username` | auth_user | DB username | ✅ |
| `spring.datasource.password` | auth@123456 | DB password | ✅ |
| `spring.jpa.hibernate.ddl-auto` | update | Auto create/update tables | ✅ |
| `spring.data.redis.host` | localhost | Redis host | ⚠️ |
| `spring.data.redis.port` | 6379 | Redis port | ⚠️ |
| `jwt.secret` | caef38e2f... | JWT secret key | ✅ |
| `jwt.expiration` | 86400000 | JWT token lifetime (ms) | ✅ |
| `bootstrap.admin.username` | sysadmin | Default admin username | ✅ |
| `bootstrap.admin.password` | 1234 | Default admin password | ✅ |

**Mô tả chi tiết:**

```
spring.datasource.url=jdbc:postgresql://localhost:5432/nckh_auth?&timezone=Asia/Ho_Chi_Minh
  → PostgreSQL connection string
  → localhost:5432 = PostgreSQL server
  → nckh_auth = database name
  → timezone=Asia/Ho_Chi_Minh = timezone (Vietnam)
  → Nếu PostgreSQL chạy trên máy khác → thay localhost bằng IP

spring.datasource.username=auth_user
  → PostgreSQL username
  → Phải trùng với user tạo trong database

spring.datasource.password=auth@123456
  → PostgreSQL password
  → Giữ bí mật!

spring.jpa.hibernate.ddl-auto=update
  → Auto-create/update database tables từ @Entity classes
  → Giá trị: none, validate, update, create, create-drop
  → update = tự động tạo table nếu chưa có, không xóa
  → create = luôn tạo mới (nguy hiểm, mất dữ liệu cũ)

jwt.secret=caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a2df52c
  → JWT secret key (phải giống với API Gateway)
  → Dùng để ký token khi user đăng nhập

jwt.expiration=86400000
  → Token lifetime = 86400000 ms = 24 giờ
  → Sau 24 giờ, token hết hạn, user phải login lại

bootstrap.admin.username=sysadmin
  → Tài khoản admin mặc định
  → Được tạo tự động khi service khởi động lần đầu

bootstrap.admin.password=1234
  → Mật khẩu admin mặc định
  → ⚠️ ĐỔI SAU KHI VÀO PRODUCTION
```

---

### 🛒 **Product Service (application.properties)**

**File vị trí**: `backend/product-service/Tien/Tien/src/main/resources/application.properties`

(Chưa tìm thấy, tạo file mẫu sau)

| Biến | Giá Trị Mặc Định | Ý Nghĩa |
|------|-----------------|--------|
| `server.port` | 8003 | Port chạy Product Service |
| `spring.datasource.url` | jdbc:mysql://localhost:3306/shopquanao | MySQL URL |
| `spring.datasource.username` | product_user | DB username |
| `spring.datasource.password` | product@123456 | DB password |
| `spring.jpa.hibernate.ddl-auto` | update | Auto create/update tables |

---

### 📦 **Order Service (application.properties)**

**File vị trí**: `backend/order-service/Nckh-C-ng/src/main/resources/application.properties`

| Biến | Giá Trị Mặc Định | Ý Nghĩa |
|------|-----------------|--------|
| `server.port` | 8091 (hoặc 8002) | Port chạy Order Service |
| `spring.datasource.url` | jdbc:sqlserver://localhost:1433;databaseName=order_dB | SQL Server URL |
| `spring.datasource.username` | orderuser | DB username |
| `spring.datasource.password` | Order@123 | DB password |
| `spring.jpa.hibernate.ddl-auto` | update | Auto create/update tables |

---

### 📊 **Audit Service (application.properties)**

**File vị trí**: `backend/audit-service/Nckh-Hi-p/src/main/resources/application.properties`

(Chưa tìm thấy, tạo file mẫu sau)

| Biến | Giá Trị Mặc Định | Ý Nghĩa |
|------|-----------------|--------|
| `server.port` | 8004 | Port chạy Audit Service |
| `spring.datasource.url` | jdbc:postgresql://localhost:5432/nckh_auth | PostgreSQL URL |
| `spring.datasource.username` | auth_user | DB username |
| `spring.datasource.password` | auth@123456 | DB password |
| `spring.jpa.hibernate.ddl-auto` | update | Auto create/update tables |

---

### ⚛️ **Frontend (.env)**

**File vị trí**: `frontend/web-client/.env`

| Biến | Giá Trị Mặc Định | Ý Nghĩa |
|------|-----------------|--------|
| `VITE_API_URL` | http://localhost:3000 | API Gateway URL |
| `VITE_TIMEOUT` | 30000 | HTTP request timeout (ms) |

**Mô tả:**

```
VITE_API_URL=http://localhost:3000
  → URL của API Gateway
  → Tất cả API calls từ frontend sẽ đi qua URL này
  → Ví dụ: VITE_API_URL/api/auth/login

VITE_TIMEOUT=30000
  → HTTP request timeout = 30 giây
  → Nếu server không trả response trong 30s → abort request
```

---

## 4.3 File Mẫu (.env.example)

### 📄 **api-gateway/.env.example**

```bash
# API Gateway Configuration
PORT=3000
NODE_ENV=development

# JWT Secret (minimum 32 characters)
JWT_SECRET=caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a2df52c

# Backend Services URLs
BACKEND_URL_AUTH=http://localhost:8001
BACKEND_URL_PRODUCT=http://localhost:8003
BACKEND_URL_ORDER=http://localhost:8002
BACKEND_URL_AUDIT=http://localhost:8004

# CORS Configuration
CORS_ORIGIN=http://localhost:5173,http://127.0.0.1:5173
```

---

### 📄 **frontend/web-client/.env.example**

```bash
# Frontend Configuration
VITE_API_URL=http://localhost:3000
VITE_TIMEOUT=30000
```

---

### 📄 **backend/auth-service/Nckh-Lu-n/application.properties.example**

```properties
# Server Configuration
server.port=8001
server.address=0.0.0.0

# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/nckh_auth?timezone=Asia/Ho_Chi_Minh
spring.datasource.username=auth_user
spring.datasource.password=auth@123456
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Redis Configuration (for caching)
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis

# JWT Configuration
jwt.secret=caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a2df52c
jwt.expiration=86400000

# Bootstrap Admin User
bootstrap.admin.username=sysadmin
bootstrap.admin.password=1234
bootstrap.admin.email=sysadmin@system.local

# Logging
logging.level.root=INFO
logging.level.vn.id.luannv=DEBUG
```

---

## 4.4 Phân Biệt Local, Dev, Production

### 🔴 **LOCAL** (Máy tính của bạn)

**Dùng khi**: Develop & test trên máy cá nhân

**Đặc điểm:**
- Databases chạy trong Docker containers
- Không cần HTTPS, JWT expiration ngắn
- Logging verbose (debug output)
- CORS cho phép localhost

**File config:**
- `api-gateway/.env` (mình tạo)
- `application.properties` (hiện tại)

**Giá trị mẫu:**
```
# api-gateway/.env
PORT=3000
JWT_SECRET=dev_key_12345...

# application.properties
server.port=8001
spring.datasource.url=jdbc:postgresql://localhost:5432/nckh_auth
spring.jpa.hibernate.ddl-auto=update
logging.level.root=DEBUG
```

---

### 🟡 **DEV** (Development server - shared)

**Dùng khi**: Team share code, test tính năng mới

**Đặc điểm:**
- Databases trên dev server
- HTTPS bắt buộc
- JWT expiration vừa phải (6-12 giờ)
- CORS cho phép dev domain

**File config:**
- `application-dev.properties` (cần tạo)

**Giá trị mẫu:**
```properties
# application-dev.properties
server.port=8001
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
spring.datasource.url=jdbc:postgresql://dev-server.company.com:5432/nckh_auth
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=INFO
```

---

### 🟢 **PRODUCTION** (Production server - live)

**Dùng khi**: Deploy cho users thực tế

**Đặc điểm:**
- Databases trên prod server (secured)
- HTTPS bắt buộc, mật khẩu mạnh
- JWT expiration ngắn (1-4 giờ)
- CORS chỉ cho allowed domains
- Monitoring & alerting
- Backup hàng ngày

**File config:**
- `application-prod.properties` (cần tạo)

**Giá trị mẫu:**
```properties
# application-prod.properties
server.port=8001
server.ssl.enabled=true
server.ssl.key-store=/etc/secrets/keystore.p12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}

spring.datasource.url=jdbc:postgresql://prod-db.company.com:5432/nckh_auth
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate

jwt.expiration=14400000

logging.level.root=WARN
logging.file.name=/var/log/nckh/auth-service.log

spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

---

## 4.5 Cách Sử Dụng File Application Properties

### 🔄 Chuyển Environment

**Bước 1:** Tạo file `application-{profile}.properties`

```
application.properties           # Default (local)
application-dev.properties       # Dev environment
application-prod.properties      # Production
application-docker.properties    # Docker environment
```

**Bước 2:** Specify profile khi chạy

**Maven:**
```bash
# Chạy với dev profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Chạy với prod profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

**Java:**
```bash
java -Dspring.profiles.active=dev -jar app.jar
java -Dspring.profiles.active=prod -jar app.jar
```

**application.properties (set default):**
```properties
spring.profiles.active=dev
```

---

## 4.6 Variables Thứ 2: Environment Variables (OS)

Ngoài file .env, bạn cũng có thể set environment variables trên OS:

**Windows (PowerShell):**
```powershell
$env:PORT=3000
$env:JWT_SECRET="your_secret_key"
```

**Windows (Batch):**
```batch
set PORT=3000
set JWT_SECRET=your_secret_key
```

**Linux/macOS:**
```bash
export PORT=3000
export JWT_SECRET="your_secret_key"
```

**Ưu điểm**: Không cần .env file, direct set OS environment
**Nhược điểm**: Khó quản lý, không portable

---

## 4.7 Checklist Cấu Hình

- [ ] Copy `.env.example` → `.env` (tất cả services)
- [ ] Điền mật khẩu thực tế vào `.env`
- [ ] Kiểm tra `application.properties` tại từng backend service
- [ ] Xác nhận JWT_SECRET giống nhau ở API Gateway & Auth Service
- [ ] Kiểm tra database URLs trỏ đúng
- [ ] Test kết nối database trước khi chạy
- [ ] **NEVER commit .env file** (thêm vào .gitignore)

---

