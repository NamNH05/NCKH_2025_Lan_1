# 🔐 Auth Service - Tài Liệu Chi Tiết

## Tổng Quan Service

### Tên Service
**Auth Service** (Dịch vụ Xác thực)

### Mục Đích Chính
Auth Service là dịch vụ vi mô chuyên biệt xử lý:
- **Đăng ký người dùng mới** - tạo tài khoản, validate dữ liệu
- **Đăng nhập** - xác minh username/password, cấp JWT token
- **Xác thực token** - kiểm tra token có hợp lệ hay hết hạn
- **Đăng xuất** - vô hiệu hóa token (blacklist)
- **Phân quyền người dùng** - gán role (USER/ADMIN)

### Service Này Giải Quyết Bài Toán Gì?
Trong một hệ thống e-commerce:
- Khách hàng cần có tài khoản để mua hàng
- Quản trị viên cần quyền riêng để quản lý sản phẩm
- Mỗi request cần kiểm tra người dùng hợp lệ (bằng JWT token)
- Khi đăng xuất, token cũ phải bị vô hiệu hóa ngay lập tức

### Service Này KHÔNG Làm Những Gì?
❌ Không quản lý sản phẩm (Product Service làm)  
❌ Không xử lý đơn hàng (Order Service làm)  
❌ Không lưu audit log trực tiếp (Audit Service làm)  
❌ Không gửi email (cần thêm Email Service)  
❌ Không reset mật khẩu (chỉ có login/register)

---

## 🏗️ Kiến Trúc Nội Bộ

### Các Layer (Tầng)

```
┌─────────────────────────────────────┐
│   Controller (AuthController)        │
│   - @PostMapping /register           │
│   - @PostMapping /login              │
│   - @PostMapping /validate           │
│   - @PostMapping /logout             │
└────────────┬────────────────────────┘
             │ gọi
┌────────────▼────────────────────────┐
│   Service Layer (AuthService)       │
│   - register(request)                │
│   - login(request)                   │
│   - validateToken(token)             │
│   - logout(token)                    │
└────────────┬────────────────────────┘
             │ gọi
┌────────────▼────────────────────────┐
│   Repository (UserRepository)        │
│   - findByUsername(username)         │
│   - save(user)                       │
│   - existsByUsername(username)       │
└────────────┬────────────────────────┘
             │ query
┌────────────▼────────────────────────┐
│   Database (PostgreSQL)              │
│   - users table                      │
│   - roles table                      │
│   - user_roles table                 │
└─────────────────────────────────────┘
```

### Luồng Request Chi Tiết

#### 1️⃣ **Register Flow**
```
POST /api/auth/register
  ├─ AuthController.register() nhận request
  ├─ Validate dữ liệu (@Valid)
  ├─ AuthService.register() kiểm tra username đã tồn tại?
  ├─ UserService.createUser() tạo user mới
  │   ├─ Hash password bằng BCrypt
  │   ├─ Set role = "USER"
  │   ├─ Set status = ACTIVE
  │   └─ Lưu vào database
  ├─ AuditClient ghi log: "User created"
  └─ Return UserResponse (id, username, email, fullName)
```

#### 2️⃣ **Login Flow**
```
POST /api/auth/login
  ├─ AuthController.login() nhận LoginRequest
  ├─ AuthService.login()
  │   ├─ Tìm user theo username
  │   ├─ So sánh password (BCrypt)
  │   ├─ Nếu sai → throw BusinessException
  │   ├─ Nếu đúng → JwtUtil.generateToken()
  │   │   ├─ Token chứa: userId, username, roles
  │   │   ├─ Hạn sử dụng: 24 giờ
  │   │   └─ Ký bằng secret key
  │   ├─ AuditClient ghi log: "User logged in"
  │   └─ Return AuthResponse (token, user, expiresIn)
```

#### 3️⃣ **Validate Token Flow**
```
POST /api/auth/validate
  Header: Authorization: Bearer <token>
  ├─ AuthController.validateToken()
  ├─ Tách token từ header (remove "Bearer ")
  ├─ AuthService.validateToken()
  │   ├─ JwtUtil.validateToken() kiểm tra:
  │   │   ├─ Chữ ký có đúng không?
  │   │   ├─ Token đã hết hạn?
  │   │   ├─ Token có trong blacklist?
  │   │   └─ Claims (userId, roles) có hợp lệ?
  │   └─ Return true/false
```

#### 4️⃣ **Logout Flow**
```
POST /api/auth/logout
  Header: Authorization: Bearer <token>
  ├─ AuthController.logout()
  ├─ AuthService.logout()
  │   ├─ BlacklistService.addToBlacklist(token)
  │   │   └─ Lưu token vào Redis cache
  │   │       (TTL = token expiry time)
  │   ├─ AuditClient ghi log: "User logged out"
  │   └─ Return "success"
```

---

## 🛠️ Công Nghệ Sử Dụng

### Ngôn Ngữ & Framework
| Thành Phần | Phiên Bản |
|-----------|---------|
| Java | 17+ |
| Spring Boot | 3.5.9 |
| Spring Security | 6.x |
| Spring Data JPA | 3.5.9 |

### Database
| Thành Phần | Chi Tiết |
|-----------|---------|
| Database | PostgreSQL 15 |
| Connection Pool | HikariCP |
| ORM | Hibernate (JPA) |

### Security
| Thành Phần | Mục Đích |
|-----------|---------|
| JWT (jjwt) | Token-based authentication |
| BCrypt | Password hashing |
| Spring Security | Authorization/Authentication |
| Redis | Token blacklist (logout) |

### Communication
| Thành Phần | Mục Đích |
|-----------|---------|
| REST API | Expose endpoints |
| HTTP Client (RestTemplate/Feign) | Gọi Audit Service |

---

## 📁 Cấu Trúc Thư Mục

```
src/main/java/vn/id/luannv/auth_service/
│
├── AuthServiceApplication.java           # Entry point của service
│
├── controller/
│   ├── AuthController.java               # REST endpoints: /api/auth
│   ├── UserController.java               # REST endpoints: /api/users
│   └── TestController.java               # Endpoints test (ping, me, etc)
│
├── service/
│   ├── AuthService.java                  # Logic: register, login, validate
│   ├── UserService.java                  # Logic: createUser, updateUser
│   └── BlacklistService.java             # Logic: add/check token blacklist
│
├── repository/
│   ├── UserRepository.java               # Query database users
│   └── RoleRepository.java               # Query database roles
│
├── entity/
│   ├── User.java                         # JPA Entity @Table("users")
│   ├── Role.java                         # JPA Entity @Table("roles")
│   └── UserStatus.java                   # Enum: ACTIVE, INACTIVE, BANNED
│
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java             # {username, password}
│   │   ├── RegisterRequest.java          # {username, email, password, fullName, phone}
│   │   └── CreateUserRequest.java        # Admin tạo user
│   │
│   └── response/
│       ├── AuthResponse.java             # {token, user, expiresIn}
│       └── UserResponse.java             # {id, username, email, fullName, phone, role}
│
├── config/
│   ├── JwtUtil.java                      # JWT token generation/validation
│   ├── SecurityConfig.java               # Spring Security configuration
│   ├── RestTemplateConfig.java           # HTTP client setup
│   └── AuditClientConfig.java            # Feign client to Audit Service
│
├── security/
│   ├── JwtAuthenticationFilter.java      # Filter: Extract token từ request
│   ├── JwtAuthenticationProvider.java    # Custom Authentication Provider
│   └── SecurityUtil.java                 # Helper: Get current user
│
├── exception/
│   ├── BusinessException.java            # Custom exception: (400, 401, 403)
│   ├── GlobalExceptionHandler.java       # @ControllerAdvice xử lý lỗi
│   └── TokenExpiredException.java        # Khi token hết hạn
│
├── client/
│   ├── AuditClient.java                  # Feign client call Audit Service
│   └── AuditLogRequest.java              # DTO for audit log
│
├── mapper/
│   ├── UserMapper.java                   # Convert Entity ↔ DTO
│   └── AuthMapper.java                   # Convert Auth data
│
└── resources/
    ├── application.yml                   # Config: port, database, JWT
    └── application-prod.yml              # Config production
```

---

## 🗄️ Database Schema

### Bảng `users`
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,           -- SHA256 hashed
    full_name VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(20),                       -- ACTIVE, INACTIVE, BANNED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_email ON users(email);
```

### Bảng `roles`
```sql
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,         -- USER, ADMIN, MODERATOR
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO roles (name) VALUES ('USER'), ('ADMIN');
```

### Bảng `user_roles` (Join Table)
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
```

### Quan Hệ Giữa Các Bảng
```
users (1) ──── (*) user_roles ──── (1) roles
  │                                       │
  ├─ 1 user có thể có 1+ role           └─ 1 role có thể được assign cho nhiều user
  │   (ví dụ: user vừa là USER vừa là ADMIN)
```

### Dữ Liệu Nhạy Cảm - Bảo Vệ Thế Nào?

| Dữ liệu | Cách Bảo Vệ |
|--------|-----------|
| Password | Mã hóa BCrypt (1-way hash) |
| Email | Yêu cầu unique, validate format |
| JWT Token | Ký bằng secret key (RS256) |
| Logout Token | Lưu blacklist trong Redis (TTL) |

---

## 🔌 API Endpoints

### 1️⃣ Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "fullName": "John Doe",
  "phone": "0912345678"
}
```

**Response (201 Created)**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "phone": "0912345678",
  "role": "[USER]",
  "status": "ACTIVE"
}
```

**Validation Rules**
- username: 3-50 ký tự, chỉ alphanumeric + underscore
- email: phải hợp lệ, unique
- password: 6+ ký tự
- fullName: 2-100 ký tự
- phone: format Việt Nam (0 hoặc +84 + 9-10 chữ số)

**Lỗi có thể**
- `400` - Username already exists
- `400` - Email already exists
- `400` - Invalid email format
- `500` - Database error

---

### 2️⃣ Login User
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123"
}
```

**Response (200 OK)**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "role": "[USER]"
  },
  "expiresIn": 86400
}
```

**Token Structure** (JWT)
```
Header: {
  "alg": "HS256",
  "typ": "JWT"
}

Payload: {
  "userId": 1,
  "username": "john_doe",
  "roles": ["USER"],
  "iat": 1705380000,
  "exp": 1705466400
}

Signature: HMACSHA256(secret_key)
```

**Lỗi có thể**
- `400` - Username not found
- `401` - Wrong password
- `403` - User account is banned
- `500` - Database error

---

### 3️⃣ Validate Token
```http
POST /api/auth/validate
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK)**
```json
{
  "valid": true
}
```

hoặc

```json
{
  "valid": false
}
```

**Kiểm tra gì?**
- Token signature hợp lệ?
- Token chưa hết hạn?
- Token có trong blacklist (logout)?

---

### 4️⃣ Logout
```http
POST /api/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK)**
```json
{
  "message": "Logout successfully"
}
```

---

### 5️⃣ Get Current User (Protected)
```http
GET /api/users/me
Authorization: Bearer <token>
```

**Response (200 OK)**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "[USER]"
}
```

---

### Phân Quyền (Role-Based)

| Endpoint | ADMIN | USER | Anonymous |
|----------|-------|------|-----------|
| POST /api/auth/register | ✅ | ✅ | ✅ |
| POST /api/auth/login | ✅ | ✅ | ✅ |
| POST /api/auth/validate | ✅ | ✅ | ❌ |
| POST /api/auth/logout | ✅ | ✅ | ❌ |
| GET /api/users | ✅ | ❌ | ❌ |
| PUT /api/users/{id} | ✅ | ❌ | ❌ |

---

## 🔒 Security

### Cách Xác Thực (Authentication)

1. **Request đầu tiên** - User login
   ```
   POST /api/auth/login
   Body: {username, password}
   ↓
   Backend: BCrypt.compare(password, hashed)
   ↓
   Response: JWT token + user info
   ```

2. **Request tiếp theo** - User gửi token
   ```
   GET /api/orders
   Header: Authorization: Bearer <token>
   ↓
   Backend: JwtUtil.validateToken(token)
   ↓
   Token hợp lệ → xử lý request
   Token lỗi → 401 Unauthorized
   ```

### Cách Phân Quyền (Authorization)

```java
@PreAuthorize("hasRole('ADMIN')")    // Chỉ ADMIN
public void deleteUser(Long id) { }

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // USER hoặc ADMIN
public void getProfile() { }

@PreAuthorize("isAuthenticated()")   // Bất kì ai đã login
public void logout() { }
```

### Middleware / Filter Bảo Mật

#### 1️⃣ **JwtAuthenticationFilter**
```
Request đến
  ↓
Filter kiểm tra header "Authorization: Bearer ..."
  ↓
Tách token, validate
  ↓
Đúng → set SecurityContext
  ↓
Sai → return 401 Unauthorized
```

#### 2️⃣ **SecurityConfig**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/register")
                    .permitAll()  // Cho phép anonymous
                .requestMatchers("/api/auth/logout", "/api/auth/validate")
                    .authenticated()  // Yêu cầu login
                .requestMatchers("/api/users/**")
                    .hasRole("ADMIN")  // Chỉ ADMIN
            )
            .addFilterBefore(jwtAuthFilter, ...)  // Thêm custom filter
            .exceptionHandling()
                .authenticationEntryPoint(...)  // Xử lý 401
                .accessDeniedHandler(...);      // Xử lý 403
    }
}
```

### Rủi Ro Bảo Mật - Đã Xử Lý

| Rủi Ro | Cách Xử Lý |
|-------|----------|
| **SQL Injection** | Dùng JPA PreparedStatement (tự động) |
| **Password yếu** | Validate: 6+ ký tự, recommend mạnh hơn |
| **Token bị lộ** | HTTPS, lưu HttpOnly cookie (nếu cần) |
| **Token bị đánh cắp** | Hạn thời gian: 24 giờ, logout blacklist |
| **Brute force login** | Có thể thêm rate limiting sau |
| **CORS attack** | @CrossOrigin configured |

---

## ⚙️ Cấu Hình & Environment

### File Cấu Hình - `application.yml`

```yaml
spring:
  application:
    name: auth-service
  
  # Database PostgreSQL
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_db
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  
  # JPA Hibernate
  jpa:
    hibernate:
      ddl-auto: validate          # không tự tạo table
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQL15Dialect
        show_sql: false
        format_sql: true
    open-in-view: false
  
  # Redis (cho token blacklist)
  redis:
    host: localhost
    port: 6379
    database: 0

# Server
server:
  port: 8081
  servlet:
    context-path: /

# JWT Config
jwt:
  secret: your-super-secret-key-min-32-chars-for-security
  expiration: 86400000              # 24 giờ (ms)
  refresh-expiration: 604800000     # 7 ngày (ms)

# Logging
logging:
  level:
    root: INFO
    vn.id.luannv.auth_service: DEBUG
```

### Environment Variables (Production)

```bash
# .env hoặc container env vars
DB_HOST=auth-postgres
DB_PORT=5432
DB_NAME=auth_db
DB_USER=postgres_user
DB_PASSWORD=secure_password_here

REDIS_HOST=redis-cache
REDIS_PORT=6379

JWT_SECRET=<USE_RANDOM_STRONG_SECRET_FROM_.ENV>
JWT_EXPIRATION=86400000

AUDIT_SERVICE_URL=http://audit-service:8082
PRODUCT_SERVICE_URL=http://product-service:8083
ORDER_SERVICE_URL=http://order-service:8084
```

### Chạy Local vs Production

| Điểm | Local | Production |
|-----|-------|-----------|
| Port | 8081 | 8081 (hoặc 80) |
| Database | localhost:5432 | postgres-service:5432 |
| HTTPS | ❌ HTTP | ✅ HTTPS (SSL/TLS) |
| JWT Secret | dev-secret | secure-random-key |
| CORS | localhost:* | domain.com |
| Logging | DEBUG | INFO/WARN |
| Docker | ❌ | ✅ container |

---

## 🚀 Cách Chạy Service

### 1️⃣ **Chạy bằng Maven (Local Development)**

**Điều kiện tiên quyết**
- Java 17+ installed
- PostgreSQL running (port 5432)
- Redis running (port 6379)

**Chạy**
```bash
cd backend/auth-service/Nckh-Lu-n

# Option 1: Build + Run
mvn clean package -DskipTests
java -jar target/auth-service-0.0.1-SNAPSHOT.jar

# Option 2: Direct run (không build jar)
mvn spring-boot:run

# Option 3: IDE (IntelliJ/VSCode)
- Mở AuthServiceApplication.java
- Click "Run" button
```

**Kiểm tra service chạy**
```bash
curl http://localhost:8081/api/test/ping
# Response: "OK - Auth Service is running"
```

---

### 2️⃣ **Chạy bằng Docker**

**Dockerfile (đã có sẵn trong folder service)**
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy compiled jar
COPY target/auth-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8081

# Run
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Docker Compose**
```yaml
version: '3.8'

services:
  auth-postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: auth_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - auth-postgres-data:/var/lib/postgresql/data

  auth-redis:
    image: redis:7
    ports:
      - "6379:6379"

  auth-service:
    build:
      context: .
      dockerfile: Dockerfile
    environment:
      - DB_HOST=auth-postgres
      - DB_NAME=auth_db
      - REDIS_HOST=auth-redis
    ports:
      - "8081:8081"
    depends_on:
      - auth-postgres
      - auth-redis
    networks:
      - auth-network

volumes:
  auth-postgres-data:

networks:
  auth-network:
```

**Chạy Docker Compose**
```bash
docker-compose -f docker-compose.yml up -d

# Check logs
docker-compose logs -f auth-service

# Stop
docker-compose down
```

---

## 🔗 Liên Kết Với Service Khác

### Service này gọi ai?

```
Auth Service
  ├─ Gọi → Audit Service
  │        POST /api/audits
  │        (ghi log: user login, register, logout)
  │
  └─ (có thể mở rộng) Email Service
           POST /api/emails/send
           (gửi email xác nhận, reset password)
```

### Service khác gọi service này?

```
Order Service
  ├─ Gọi → Auth Service (xác thực)
  │        GET /api/auth/validate
  │        (kiểm tra token user hợp lệ)
  │
Product Service
  ├─ Gọi → Auth Service (lấy user info)
           GET /api/users/{id}
           (kiểm tra role ADMIN)

API Gateway
  └─ Gọi → Auth Service
           POST /api/auth/login
           (forward request từ client)
```

### Giao Tiếp - REST hay Message Queue?

**Hiện tại**: REST API (synchronous)
```
Order Service → Auth Service
   HTTP GET /api/auth/validate
   ↓
   Auth Service xử lý ngay, return result
   ↓
   Order Service tiếp tục
```

**Nếu mở rộng**: Message Queue
```
User register → Audit Service (async)
   Message: { event: "USER_REGISTERED", userId: 1 }
   ↓
   Kafka/RabbitMQ buffer
   ↓
   Audit Service consume, ghi log
   (Order Service không phải chờ)
```

---

## ⚠️ Lỗi Thường Gặp & Cách Debug

### 1️⃣ **Lỗi Kết Nối Database**

**Error**
```
java.sql.SQLException: Connection refused to host: localhost:5432
```

**Nguyên Nhân**
- PostgreSQL chưa chạy
- Port sai (5432 vs 5433)
- Config database URL sai

**Cách Fix**
```bash
# 1. Kiểm tra PostgreSQL chạy không
psql -U postgres -h localhost

# 2. Kiểm tra port
netstat -an | grep 5432

# 3. Kiểm tra config application.yml
spring.datasource.url: jdbc:postgresql://localhost:5432/auth_db

# 4. Chạy PostgreSQL (nếu chưa)
docker run -d --name postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15

# 5. Tạo database
psql -U postgres -c "CREATE DATABASE auth_db;"
```

---

### 2️⃣ **Lỗi 401 Unauthorized**

**Error**
```
{
  "timestamp": "2026-01-16T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token is invalid or expired"
}
```

**Nguyên Nhân**
- Token không được gửi
- Token hết hạn
- Token bị sửa (fake token)
- Token đã logout (blacklist)

**Cách Fix**
```bash
# 1. Kiểm tra header
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
# phải có chữ "Bearer " ở đầu

# 2. Decode token xem có hợp lệ không
# Dùng jwt.io hoặc online JWT debugger

# 3. Kiểm tra token hết hạn
# exp field trong JWT phải > current timestamp

# 4. Kiểm tra Redis (logout check)
redis-cli
> GET blacklist:<token>
# Nếu tồn tại = token đã logout
```

---

### 3️⃣ **Lỗi 403 Forbidden**

**Error**
```
{
  "error": "Access Denied",
  "message": "User does not have ADMIN role"
}
```

**Nguyên Nhân**
- User là USER nhưng gọi endpoint ADMIN
- Role chưa được assign

**Cách Fix**
```bash
# 1. Kiểm tra user role trong database
SELECT u.username, r.name FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'john_doe';

# 2. Thêm role ADMIN (nếu cần)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'john_doe' AND r.name = 'ADMIN';

# 3. Hoặc thay đổi @PreAuthorize ở controller
# từ hasRole('ADMIN') → hasAnyRole('USER', 'ADMIN')
```

---

### 4️⃣ **Lỗi 500 - Internal Server Error**

**Error**
```
java.lang.NullPointerException: Cannot invoke "..." on null object
```

**Nguyên Nhân**
- Dependency injection lỗi
- Service không được autowire
- Repository trả về null

**Cách Fix**
```bash
# 1. Kiểm tra logs
tail -f target/auth-service.log

# 2. Debug bằng breakpoint (IDE)
# - Mở AuthServiceApplication.java
# - Set breakpoint ở line lỗi
# - Run với debug mode
# - Kiểm tra variable values

# 3. Kiểm tra dependency
# @Service, @Repository, @Component có không?
@Service
public class AuthService {
    
    @Autowired  // hoặc constructor injection
    private UserRepository userRepository;
}

# 4. Nếu vẫn lỗi, xóa target/ rebuild
mvn clean package
```

---

### 5️⃣ **Docker Error**

**Error**
```
docker: failed to solve with frontend dockerfile.v0
```

**Nguyên Nhân**
- Docker daemon chưa chạy
- Dockerfile syntax sai
- Build context sai

**Cách Fix**
```bash
# 1. Kiểm tra Docker
docker ps  # nếu lỗi = Docker chưa chạy
# Mở Docker Desktop hoặc chạy Docker daemon

# 2. Build image
docker build -t auth-service:latest .

# 3. Chạy container
docker run -p 8081:8081 auth-service:latest

# 4. Kiểm tra logs
docker logs <container_id>

# 5. Debug interactive
docker run -it auth-service:latest /bin/bash
```

---

## 📚 Tài Liệu Thêm

- **JWT**: https://jwt.io
- **Spring Security**: https://spring.io/projects/spring-security
- **PostgreSQL Docs**: https://www.postgresql.org/docs
- **Redis**: https://redis.io/docs
- **Docker**: https://docs.docker.com

---

## ✅ Checklist Cho Developer Mới

- [ ] Clone/pull code
- [ ] Install Java 17+
- [ ] Install PostgreSQL, tạo database `auth_db`
- [ ] Install Redis
- [ ] Chạy `mvn spring-boot:run`
- [ ] Test endpoint: `curl http://localhost:8081/api/test/ping`
- [ ] Đăng ký user: `POST /api/auth/register`
- [ ] Đăng nhập: `POST /api/auth/login`
- [ ] Lưu token từ login response
- [ ] Test protected endpoint: `POST /api/auth/validate` (với header Authorization)
- [ ] Đọc code: AuthController → AuthService → UserRepository
- [ ] Hiểu luồng security filter
- [ ] Tìm hiểu JWT token structure
- [ ] Chuẩn bị test unit/integration nếu cần

---

**Tài liệu được cập nhật**: 2026-01-16  
**Phiên bản Service**: 0.0.1-SNAPSHOT  
**Trạng thái**: ✅ Production Ready
