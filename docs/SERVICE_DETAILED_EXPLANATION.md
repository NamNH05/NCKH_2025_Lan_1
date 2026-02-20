# 🔐 GIẢI THÍCH CHI TIẾT: AUTH SERVICE

## 📖 Mục Lục
1. [Tổng Quan](#tổng-quan)
2. [Cấu Trúc Thư Mục](#cấu-trúc-thư-mục)
3. [Các Thành Phần Chính](#các-thành-phần-chính)
4. [Entity & Database](#entity--database)
5. [Luồng Hoạt Động Chi Tiết](#luồng-hoạt-động-chi-tiết)
6. [Code Examples](#code-examples)
7. [Tương Tác với Các Services](#tương-tác-với-các-services)
8. [Debugging Tips](#debugging-tips)

---

## 🎯 Tổng Quan

### Auth Service là gì?
**Auth Service** là một **microservice độc lập** chuyên xử lý mọi thứ liên quan đến **xác thực (Authentication)** và **phân quyền (Authorization)** người dùng.

### Chức Năng Chính
```
┌─────────────────────────────────────┐
│      AUTH SERVICE (Port 8080)        │
├─────────────────────────────────────┤
│  ✅ Đăng ký (Register)               │
│     - Tạo tài khoản người dùng      │
│     - Hash password                  │
│     - Gán role mặc định             │
│                                     │
│  ✅ Đăng nhập (Login)                │
│     - Xác thực username/password    │
│     - Tạo JWT token                 │
│     - Trả về token cho client       │
│                                     │
│  ✅ Xác thực token (Validate)        │
│     - Kiểm tra token có hợp lệ      │
│     - Lấy thông tin user từ token   │
│                                     │
│  ✅ Đăng xuất (Logout)               │
│     - Vô hiệu hóa token (Blacklist) │
│     - Ngăn tái sử dụng token        │
│                                     │
│  ✅ Quản lý người dùng               │
│     - Lấy thông tin user            │
│     - Cập nhật profile              │
│     - Quản lý role (Admin)          │
│                                     │
│  ✅ Quản lý địa chỉ                  │
│     - Thêm/sửa/xóa địa chỉ         │
│     - Lưu địa chỉ tặng hàng        │
└─────────────────────────────────────┘
```

### Tại sao cần Auth Service riêng?
1. **Bảo mật tập trung** - Tất cả authentication logic ở một chỗ
2. **Reusable** - Các service khác có thể gọi để xác thực
3. **Scalable** - Có thể scale riêng mà không ảnh hưởng services khác
4. **Maintainable** - Dễ debug, test, update bảo mật

---

## 📁 Cấu Trúc Thư Mục

```
backend/auth-service/Nckh-Lu-n/
├── src/
│   └── main/
│       ├── java/vn/id/luannv/auth_service/
│       │   ├── AuthServiceApplication.java         ⭐ Entry point
│       │   │
│       │   ├── controller/
│       │   │   └── AuthController.java              ⭐ Nhận HTTP requests
│       │   │
│       │   ├── service/
│       │   │   ├── AuthService.java                 ⭐ Logic đăng nhập/đăng ký
│       │   │   ├── UserService.java                 ⭐ Logic quản lý user
│       │   │   ├── RoleService.java                 - Quản lý role
│       │   │   ├── AddressService.java              - Quản lý địa chỉ
│       │   │   └── BlacklistService.java            - Quản lý token blacklist
│       │   │
│       │   ├── repository/
│       │   │   ├── UserRepository.java              ⭐ Truy vấn database - User
│       │   │   ├── RoleRepository.java              - Truy vấn database - Role
│       │   │   ├── AddressRepository.java           - Truy vấn database - Address
│       │   │   └── BlacklistedTokenRepository.java  - Truy vấn database - Token
│       │   │
│       │   ├── entity/
│       │   │   ├── User.java                        ⭐ Entity - Người dùng
│       │   │   ├── Role.java                        - Entity - Vai trò
│       │   │   ├── UserRole.java                    - Entity - User-Role mapping
│       │   │   ├── Address.java                     - Entity - Địa chỉ
│       │   │   ├── BlacklistedToken.java            - Entity - Token vô hiệu
│       │   │   └── UserStatus.java                  - Enum - Trạng thái user
│       │   │
│       │   ├── dto/
│       │   │   ├── request/
│       │   │   │   ├── RegisterRequest.java         - Dữ liệu register từ client
│       │   │   │   └── LoginRequest.java            - Dữ liệu login từ client
│       │   │   ├── response/
│       │   │   │   ├── AuthResponse.java            - Response login/register
│       │   │   │   ├── UserResponse.java            - Response user info
│       │   │   │   └── AddressDTO.java              - Response địa chỉ
│       │   │   └── ...
│       │   │
│       │   ├── config/
│       │   │   ├── JwtUtil.java                     ⭐ Tạo & xác thực JWT
│       │   │   └── SecurityConfig.java              - Spring Security config
│       │   │
│       │   ├── client/
│       │   │   └── AuditClient.java                 - Gọi Audit Service
│       │   │
│       │   ├── exception/
│       │   │   ├── BusinessException.java           - Custom exception
│       │   │   └── GlobalHandlerException.java      - Global exception handler
│       │   │
│       │   ├── mapper/
│       │   │   └── UserMapper.java                  - Entity ↔ DTO conversion
│       │   │
│       │   └── util/
│       │       └── ...
│       │
│       └── resources/
│           ├── application.properties               ⭐ Config file
│           ├── application-dev.properties
│           └── application-prod.properties
│
├── pom.xml                           ⭐ Maven - Dependencies & build config
├── docker-compose.yml                - Docker config
├── Dockerfile                        - Docker image config
├── README.md                         - Tài liệu
└── mvnw / mvnw.cmd                   - Maven wrapper
```

---

## 🏗️ Các Thành Phần Chính

### 1️⃣ **AuthServiceApplication (Entry Point)**

```java
@SpringBootApplication
@EnableCaching
public class AuthServiceApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();  // Dùng để gọi các service khác
    }
}
```

**Giải thích**:
- `@SpringBootApplication` - Đánh dấu đây là Spring Boot application
- `@EnableCaching` - Bật tính năng cache
- `TimeZone.setDefault()` - Set timezone cho Vietnam
- `RestTemplate` - Dùng để gọi HTTP requests tới services khác (ví dụ Audit Service)

---

### 2️⃣ **AuthController (HTTP Endpoint)**

**Vị trí**: `controller/AuthController.java`

```
Đây là "cửa hàng" của Auth Service
- Nhận HTTP requests từ API Gateway
- Kiểm tra dữ liệu đầu vào
- Gọi AuthService xử lý
- Trả về HTTP response
```

**Các endpoint**:
```
POST   /api/auth/register         → Register người dùng
POST   /api/auth/login             → Login người dùng
POST   /api/auth/logout            → Logout (blacklist token)
GET    /api/auth/validate-token    → Kiểm tra token còn hợp lệ không
GET    /api/auth/user/:id          → Lấy thông tin user
PUT    /api/auth/user/:id          → Cập nhật user
DELETE /api/auth/user/:id          → Xóa user (Admin)
GET    /api/auth/users             → Lấy tất cả user (Admin)
```

---

### 3️⃣ **AuthService (Business Logic)**

**Vị trí**: `service/AuthService.java`

```
Đây là "trái tim" của xử lý xác thực
- Kiểm tra username đã tồn tại chưa?
- So sánh password
- Tạo JWT token
- Ghi log vào Audit Service
```

**Các phương thức chính**:

```java
public UserResponse register(RegisterRequest request) {
    // 1. Gọi UserService tạo user mới
    // 2. Ghi log vào Audit Service
    // 3. Trả về UserResponse
}

public AuthResponse login(LoginRequest request) {
    // 1. Tìm user theo username
    // 2. So sánh password
    // 3. Tạo JWT token
    // 4. Ghi log vào Audit Service
    // 5. Trả về token
}

public TokenValidationResponse validateToken(String token) {
    // 1. Kiểm tra token có hợp lệ không
    // 2. Kiểm tra token có bị blacklist không
    // 3. Trả về thông tin user trong token
}

public void logout(String token) {
    // 1. Thêm token vào blacklist
    // 2. Ngăn tái sử dụng token
}
```

---

### 4️⃣ **User Entity (Database Table)**

**Vị trí**: `entity/User.java`

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // ID tự động tăng
    
    @Column(nullable = false, unique = true)
    private String username;      // Tên đăng nhập (không trùng)
    
    @Column(nullable = false)
    private String password;      // Mật khẩu (đã hash)
    
    @Column(nullable = false, unique = true)
    private String email;         // Email (không trùng)
    
    private String fullName;      // Tên đầy đủ
    private String phone;         // Số điện thoại
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles")
    private Set<Role> roles;      // Vai trò (ADMIN, USER)
    
    @OneToMany(mappedBy = "user")
    private List<Address> addresses; // Địa chỉ
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;    // Trạng thái (ACTIVE, INACTIVE, BANNED)
}
```

**Giải thích**:
- `@Entity` - Đánh dấu đây là JPA entity (= table trong database)
- `@Id` - Primary key
- `@GeneratedValue` - Auto-increment ID
- `@Column(unique = true)` - Cột không được trùng
- `@ManyToMany` - Một user có nhiều roles, một role có nhiều users
- `@OneToMany` - Một user có nhiều addresses

**Bảng trong Database**:
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

### 5️⃣ **UserRepository (Database Query)**

**Vị trí**: `repository/UserRepository.java`

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Tìm user theo username
    Optional<User> findByUsername(String username);
    
    // Tìm user theo email
    Optional<User> findByEmail(String email);
    
    // Kiểm tra username đã tồn tại
    boolean existsByUsername(String username);
    
    // Kiểm tra email đã tồn tại
    boolean existsByEmail(String email);
    
    // Lấy tất cả users phân trang
    Page<User> findAll(Pageable pageable);
}
```

**Giải thích**:
- Spring JPA **tự động implement** những method này
- Không cần viết SQL, chỉ cần declare method name
- Spring sẽ parse method name và tạo SQL query

---

### 6️⃣ **JwtUtil (JWT Token Management)**

**Vị trí**: `config/JwtUtil.java`

```
Đây là "nhà máy sản xuất & kiểm tra token"
- Tạo JWT token mới
- Kiểm tra token có hợp lệ không
- Lấy thông tin user từ token
- Kiểm tra token hết hạn chưa
```

**Cách hoạt động**:

```
Token = Header.Payload.Signature

Header: {
  "alg": "HS256",      // Thuật toán ký
  "typ": "JWT"
}

Payload: {
  "sub": "username",    // Subject (ai sở hữu token)
  "userId": 123,        // Thông tin user
  "username": "john",
  "roles": ["USER"],
  "iat": 1234567890,    // Issued at (khi tạo)
  "exp": 1234567890     // Expiration (hết hạn)
}

Signature = HS256(Header + Payload, SECRET_KEY)
// Dùng để kiểm tra token không bị thay đổi
```

**Code**:
```java
public class JwtUtil {
    
    private static final String SECRET_KEY = "your-secret-key";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 hours
    
    // ✅ Tạo token mới
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("username", user.getUsername());
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getId().toString())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }
    
    // ✅ Kiểm tra token có hợp lệ không
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    // ✅ Lấy user ID từ token
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody();
        
        return Long.valueOf(claims.getSubject());
    }
    
    // ✅ Kiểm tra token hết hạn chưa
    public boolean isTokenExpired(String token) {
        return Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody()
            .getExpiration()
            .before(new Date());
    }
}
```

---

### 7️⃣ **Exception Handling (Xử Lý Lỗi)**

**Custom Exception**:
```java
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

**Global Exception Handler**:
```java
@RestControllerAdvice
public class GlobalHandlerException {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(400).body(Map.of(
            "success", false,
            "message", ex.getMessage()
        ));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return ResponseEntity.status(500).body(Map.of(
            "success", false,
            "message", "Internal server error"
        ));
    }
}
```

---

## 💾 Entity & Database

### User Entity Relationships

```
┌──────────────────────────────────┐
│         USER (id=1)              │
├──────────────────────────────────┤
│ id: 1                            │
│ username: "john"                 │
│ email: "john@example.com"        │
│ password: $2a$10$... (hash)      │
│ status: ACTIVE                   │
└──────────────────────────────────┘
           │
           ├─── ManyToMany ──→ ┌──────────────────────┐
           │                   │ ROLE (Many)          │
           │                   ├──────────────────────┤
           │                   │ id: 1                │
           │                   │ name: "USER"         │
           │                   │                      │
           │                   │ id: 2                │
           │                   │ name: "ADMIN"        │
           │                   └──────────────────────┘
           │
           └─── OneToMany ──→ ┌──────────────────────┐
                              │ ADDRESS (Many)       │
                              ├──────────────────────┤
                              │ id: 1                │
                              │ street: "123 Main St"│
                              │ city: "Hanoi"        │
                              │ user_id: 1          │
                              │                      │
                              │ id: 2                │
                              │ street: "456 Oak Ave"│
                              │ city: "HCMC"        │
                              │ user_id: 1          │
                              └──────────────────────┘
```

### Database Schema

```sql
-- Users table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

-- User-Role mapping (Many-to-Many)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Addresses table
CREATE TABLE addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    street VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Blacklisted tokens table
CREATE TABLE blacklisted_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token TEXT NOT NULL,
    user_id BIGINT,
    blacklist_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date TIMESTAMP
);
```

---

## 🔄 Luồng Hoạt Động Chi Tiết

### 📌 Luồng 1: REGISTER (Đăng Ký)

```
Frontend (Browser)
    │
    │ POST /api/auth/register
    │ Body: {
    │   "username": "john",
    │   "email": "john@example.com",
    │   "password": "password123",
    │   "fullName": "John Doe"
    │ }
    ▼
┌─────────────────────────────────────────────────┐
│  API Gateway (Port 3000)                        │
│  - Check CORS                                   │
│  - Validate input                               │
│  - Forward to Auth Service:8080                 │
└────────────┬────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  AuthController.register()                      │
│  - @PostMapping("/register")                    │
│  - Validate @Valid RegisterRequest              │
│  - Call authService.register()                  │
└────────────┬────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  AuthService.register()                         │
│  - Call userService.createUser()                │
│  - Try to log to Audit Service                  │
│  - Return UserResponse                          │
└────────────┬────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  UserService.createUser()                       │
│  1. Check if username already exists            │
│     → throw BusinessException if exists         │
│  2. Check if email already exists               │
│     → throw BusinessException if exists         │
│  3. Hash password using BCrypt                  │
│     password = BCrypt("password123")            │
│     = "$2a$10$..."                              │
│  4. Create new User entity                      │
│  5. Set role = "USER" (default)                 │
│  6. Set status = ACTIVE                         │
│  7. Call userRepository.save(user)              │
└────────────┬────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  UserRepository.save()                          │
│  - Spring Data JPA generates INSERT query:      │
│    INSERT INTO users                            │
│    (username, password, email, full_name,       │
│     status, created_at)                         │
│    VALUES (?, ?, ?, ?, ?, ?)                    │
│  - Insert into user_roles table                 │
│    INSERT INTO user_roles                       │
│    VALUES (user_id, role_id)                    │
└────────────┬────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  PostgreSQL Database                            │
│  Users Table:                                   │
│  ┌─────────────────────────────────────────┐   │
│  │ id | username | password | email | ...  │   │
│  ├─────────────────────────────────────────┤   │
│  │ 5  │ john     │ $2a$... │ j@.. │ ...   │   │
│  └─────────────────────────────────────────┘   │
│                                                 │
│  User_Roles Table:                              │
│  ┌──────────────────────┐                       │
│  │ user_id | role_id    │                       │
│  ├──────────────────────┤                       │
│  │ 5       │ 1 (USER)   │                       │
│  └──────────────────────┘                       │
└────────────┬────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  AuditClient (optional)                         │
│  - Call Audit Service:8082                      │
│  - Log: {                                       │
│      "action": "CREATE",                        │
│      "entity": "USER",                          │
│      "userId": 5,                               │
│      "timestamp": "2024-01-15T10:30:00"        │
│    }                                            │
└────────────┬────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  Return Response                                │
│  {                                              │
│    "success": true,                             │
│    "data": {                                    │
│      "id": 5,                                   │
│      "username": "john",                        │
│      "email": "john@example.com",               │
│      "fullName": "John Doe",                    │
│      "roles": ["USER"],                         │
│      "status": "ACTIVE"                         │
│    }                                            │
│  }                                              │
└─────────────────────────────────────────────────┘
```

---

### 📌 Luồng 2: LOGIN (Đăng Nhập)

```
Frontend
    │
    │ POST /api/auth/login
    │ Body: {
    │   "username": "john",
    │   "password": "password123"
    │ }
    ▼
API Gateway → AuthController → AuthService
             │
             ▼
┌─────────────────────────────────────────────────┐
│  AuthService.login()                            │
│  1. Find user by username                       │
│     → Optional<User> user =                     │
│         userRepository.findByUsername("john")   │
│  2. If not found                                │
│     → throw BusinessException("User not found") │
│  3. Compare password                            │
│     → plainPassword = "password123"             │
│     → hashedPassword = "$2a$..."                │
│     → passwordEncoder.matches(plain, hashed)   │
│        Using BCrypt algorithm to verify         │
│  4. If password doesn't match                   │
│     → throw BusinessException(...)              │
│  5. Check if user is active                     │
│     → if status != ACTIVE                       │
│        → throw BusinessException(...)           │
│  6. Generate JWT token                          │
│     → String token = jwtUtil.generateToken(user)│
│  7. Build response                              │
│     → AuthResponse with token                   │
│  8. Log to Audit Service                        │
│  9. Return response                             │
└─────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  JWT Token Generated                            │
│  Header:                                        │
│  {                                              │
│    "alg": "HS256",                              │
│    "typ": "JWT"                                 │
│  }                                              │
│                                                 │
│  Payload:                                       │
│  {                                              │
│    "sub": "5",         // user ID               │
│    "username": "john",                          │
│    "roles": ["USER"],                           │
│    "iat": 1705318200,  // issued at             │
│    "exp": 1705404600   // expires in 24h        │
│  }                                              │
│                                                 │
│  Signature:                                     │
│  HS256(                                         │
│    base64(header) + "." + base64(payload),      │
│    "your-secret-key"                            │
│  )                                              │
│  = "eyJhbGciOiJIUzI1NiJ9.eyJz..."              │
│                                                 │
│  Final Token:                                   │
│  eyJhbGciOiJIUzI1NiJ9.eyJzd2...                │
│  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^       │
│  (Dùng để authorize các requests sau)           │
└─────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  Return Response                                │
│  {                                              │
│    "success": true,                             │
│    "data": {                                    │
│      "id": 5,                                   │
│      "token": "eyJhbGci...",  // JWT            │
│      "username": "john",                        │
│      "roles": ["USER"],                         │
│      "expiresIn": 86400                         │
│    }                                            │
│  }                                              │
└─────────────────────────────────────────────────┘
             │
             ▼
Frontend (React)
    │
    │ Save token to localStorage
    │ localStorage.setItem("token", "eyJhbGci...")
    │
    ▼ Tất cả requests sau đều gửi token
    │
    │ GET /api/products
    │ Headers: {
    │   "Authorization": "Bearer eyJhbGci..."
    │ }
```

---

### 📌 Luồng 3: VALIDATE TOKEN

```
API Gateway nhận request từ client
    │
    ├─ Extract token từ Authorization header
    │ Authorization: "Bearer eyJhbGci..."
    │ token = "eyJhbGci..."
    │
    ▼
API Gateway gọi Auth Service
    │
    │ POST /api/auth/validate-token
    │ Body: { "token": "eyJhbGci..." }
    │
    ▼
┌─────────────────────────────────────────────────┐
│  AuthService.validateToken()                    │
│  1. Check if token in blacklist                 │
│     → blacklistService.isTokenBlacklisted()     │
│     → if yes, throw exception                   │
│  2. Validate token signature                    │
│     → jwtUtil.validateToken(token)              │
│     → HS256(payload, SECRET_KEY) == signature   │
│     → if invalid, throw exception               │
│  3. Check if token expired                      │
│     → exp claim vs current time                 │
│     → if expired, throw exception               │
│  4. Extract user info from token                │
│     → userId = token.sub                        │
│     → username = token.username                 │
│     → roles = token.roles                       │
│  5. Return TokenValidationResponse              │
└─────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  Response:                                      │
│  {                                              │
│    "valid": true,                               │
│    "userId": 5,                                 │
│    "username": "john",                          │
│    "roles": ["USER"],                           │
│    "expiresAt": 1705404600                      │
│  }                                              │
└─────────────────────────────────────────────────┘
             │
             ▼
API Gateway
    │
    ├─ If valid: Forward request to desired service
    │
    └─ If invalid: Return 401 Unauthorized error
```

---

### 📌 Luồng 4: LOGOUT

```
Frontend
    │
    │ POST /api/auth/logout
    │ Headers: { "Authorization": "Bearer token" }
    │
    ▼
API Gateway → AuthController → AuthService
             │
             ▼
┌─────────────────────────────────────────────────┐
│  AuthService.logout()                           │
│  1. Extract token from request                  │
│  2. Validate token                              │
│     → If invalid, throw exception               │
│  3. Get expiration time from token              │
│  4. Add token to blacklist                      │
│     → blacklistService.blacklistToken(token)    │
│     → Save to BlacklistedToken table            │
│  5. Return success response                     │
└────────────┬────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  BlacklistService.blacklistToken()              │
│  - Create BlacklistedToken entity               │
│  - Set expiry_date = token.exp                  │
│  - Save to database                             │
│  - Optional: Cache the token                    │
└────────────┬────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  Database - blacklisted_tokens table            │
│  ┌──────────────────────────────────────────┐   │
│  │ id │ token      │ user_id │ expiry_date  │   │
│  ├──────────────────────────────────────────┤   │
│  │ 1  │ eyJhbGci.. │ 5       │ 1705404600   │   │
│  └──────────────────────────────────────────┘   │
└────────────┬────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────┐
│  Return Response                                │
│  {                                              │
│    "success": true,                             │
│    "message": "Logged out successfully"         │
│  }                                              │
└─────────────────────────────────────────────────┘
             │
             ▼
Frontend (React)
    │
    ├─ Remove token from localStorage
    │ localStorage.removeItem("token")
    │
    └─ Redirect to login page
```

---

## 💻 Code Examples

### Example 1: Register Request

**Client Code (React)**:
```javascript
// frontend/web-client/src/api/authApi.js

import axios from 'axios';

const API_BASE = 'http://localhost:3000';

export const registerUser = async (username, email, password, fullName) => {
  try {
    const response = await axios.post(`${API_BASE}/api/auth/register`, {
      username,
      email,
      password,
      fullName
    });
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message;
  }
};
```

**Component (React)**:
```javascript
import { useState } from 'react';
import { registerUser } from '../api/authApi';

function RegisterPage() {
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    fullName: ''
  });
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const result = await registerUser(
        form.username,
        form.email,
        form.password,
        form.fullName
      );
      console.log('User registered:', result);
      // Redirect to login
      window.location.href = '/login';
    } catch (error) {
      console.error('Registration error:', error.message);
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Username"
        value={form.username}
        onChange={(e) => setForm({...form, username: e.target.value})}
      />
      <input
        type="email"
        placeholder="Email"
        value={form.email}
        onChange={(e) => setForm({...form, email: e.target.value})}
      />
      <input
        type="password"
        placeholder="Password"
        value={form.password}
        onChange={(e) => setForm({...form, password: e.target.value})}
      />
      <input
        type="text"
        placeholder="Full Name"
        value={form.fullName}
        onChange={(e) => setForm({...form, fullName: e.target.value})}
      />
      <button type="submit">Register</button>
    </form>
  );
}
```

**Server Code (Java)**:
```java
// backend/auth-service/src/main/java/vn/id/luannv/auth_service/controller/AuthController.java

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserResponse response = authService.register(request);
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "data", response
            ));
        } catch (BusinessException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", ex.getMessage()
            ));
        }
    }
}
```

---

### Example 2: Login Request

**Client (React)**:
```javascript
export const loginUser = async (username, password) => {
  try {
    const response = await axios.post(`${API_BASE}/api/auth/login`, {
      username,
      password
    });
    
    // Save token to localStorage
    const { token } = response.data.data;
    localStorage.setItem('token', token);
    
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message;
  }
};
```

**Server**:
```java
@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    try {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok().body(Map.of(
            "success", true,
            "data", response
        ));
    } catch (BusinessException ex) {
        return ResponseEntity.status(401).body(Map.of(
            "success", false,
            "message", ex.getMessage()
        ));
    }
}
```

---

### Example 3: Protected API Call

**Client (React)**:
```javascript
// axios instance with token
const axiosInstance = axios.create({
  baseURL: 'http://localhost:3000'
});

// Add token to every request
axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Get user profile (protected)
export const getUserProfile = async () => {
  try {
    const response = await axiosInstance.get('/api/auth/user/profile');
    return response.data;
  } catch (error) {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    throw error;
  }
};
```

**Server**:
```java
@GetMapping("/user/profile")
@PreAuthorize("isAuthenticated()")  // Require valid token
public ResponseEntity<?> getUserProfile(
    @RequestHeader("Authorization") String authHeader) {
    try {
        // Extract token from "Bearer token"
        String token = authHeader.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        User user = userService.getUserById(userId);
        return ResponseEntity.ok().body(Map.of(
            "success", true,
            "data", UserMapper.toDTO(user)
        ));
    } catch (Exception ex) {
        return ResponseEntity.status(401).body(Map.of(
            "success", false,
            "message", "Unauthorized"
        ));
    }
}
```

---

## 🔗 Tương Tác với Các Services

### 1. Gọi Audit Service

**Khi nào gọi**: Khi có action quan trọng (register, login, logout)

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuditClient auditClient;
    
    public AuthResponse login(LoginRequest request) {
        // ... validation ...
        
        // Log to Audit Service
        try {
            auditClient.logAudit(AuditLogRequest.builder()
                .sourceService("auth-service")
                .actionType("LOGIN")
                .entityName("USER")
                .entityId(user.getId().toString())
                .userId(user.getId().toString())
                .newValue(objectMapper.writeValueAsString(user))
                .ipAddress("0.0.0.0")
                .userAgent("auth-service")
                .build());
        } catch (Exception e) {
            logger.warn("Failed to audit login: {}", e.getMessage());
            // Log fail nhưng không ảnh hưởng đến flow chính
        }
        
        return buildAuthResponse(user);
    }
}
```

---

### 2. API Gateway Gọi Auth Service

**API Gateway Code** (Node.js/Express):
```javascript
// api-gateway/server.js

const verifyToken = (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1];
    
    // Auth routes không cần JWT
    if (req.path.includes('/auth/')) {
        return next();
    }
    
    if (!token) {
        return res.status(401).json({ message: 'No token' });
    }
    
    // Gọi Auth Service để validate token
    axios.post('http://localhost:8080/api/auth/validate-token', { token })
        .then(response => {
            req.user = response.data;
            next();
        })
        .catch(error => {
            res.status(401).json({ message: 'Invalid token' });
        });
};

app.use(verifyToken);
```

---

## 🐛 Debugging Tips

### 1. Check Logs

```bash
# Terminal chạy Auth Service
# Tìm dòng:
# [main] o.s.s.w.SecurityFilterChain
# [main] s.SpringApplication : Started...
# [main] s.o.m.a.AuthServiceApplication
```

---

### 2. Test with Postman

**Test Register**:
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "pass123",
  "fullName": "Test User"
}
```

**Test Login**:
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "pass123"
}

Response:
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "testuser",
    "roles": ["USER"]
  }
}
```

**Test Protected Endpoint**:
```
GET http://localhost:8080/api/auth/user/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

### 3. Check Database

```bash
# Connect to PostgreSQL
psql -U postgres -d auth_db

# View users
SELECT id, username, email, status FROM users;

# View roles
SELECT * FROM roles;

# View user_roles mapping
SELECT u.username, r.name 
FROM user_roles ur
JOIN users u ON ur.user_id = u.id
JOIN roles r ON ur.role_id = r.id;

# View blacklisted tokens
SELECT token, user_id, blacklist_date FROM blacklisted_tokens;
```

---

### 4. Common Errors & Solutions

| Lỗi | Nguyên Nhân | Giải Pháp |
|-----|-----------|---------|
| `User not found` | Username không tồn tại | Đăng ký user mới |
| `Invalid password` | Mật khẩu sai | Check lại mật khẩu |
| `User is not active` | User bị disable | Check status trong DB |
| `Invalid token` | Token hết hạn hoặc sai | Login lại |
| `Token expired` | Quá 24 giờ từ khi login | Login lại |
| `Connection refused` | Auth Service không chạy | `mvn spring-boot:run` |
| `Database error` | PostgreSQL không chạy | Start PostgreSQL service |

---

## 📊 Summary

| Thành Phần | Chức Năng | Công Nghệ |
|-----------|---------|---------|
| **AuthController** | HTTP endpoint | Spring MVC |
| **AuthService** | Business logic | Spring Service |
| **UserService** | User management | Spring Service |
| **UserRepository** | Database query | Spring Data JPA |
| **JwtUtil** | Token create/validate | jjwt library |
| **User Entity** | Database table | JPA |
| **PostgreSQL** | Persistent storage | Database |
| **AuditClient** | Call Audit Service | RestTemplate |

---

**Tóm lại**:
- Auth Service là "cổng vào" của hệ thống
- Nó quản lý toàn bộ xác thực & phân quyền
- Sử dụng JWT để trao đổi thông tin với client
- Tương tác với Audit Service để ghi log
- API Gateway phụ thuộc vào nó để xác thực requests

---

**Cập nhật**: February 2026
**Phiên bản**: 1.0
