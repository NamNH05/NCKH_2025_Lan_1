# 📁 Phần 3 – Cấu Trúc Source Code

---

## 3.1 Cấu Trúc Thư Mục Tổng Thể

```
NCKH/
├── 📄 README.md                          # File chính của dự án
├── 📄 GETTING_STARTED.md                 # Hướng dẫn bắt đầu nhanh
├── 📄 DOCUMENTATION_SUMMARY.md           # Tóm tắt tất cả tài liệu
├── 📄 docker-compose-all-services.yml    # Docker Compose toàn bộ
├── 📄 package.json                       # Dependencies root (axios, tailwind)
│
├── 📁 docs/                              # Tài liệu dự án
│   ├── setup/                            # Hướng dẫn setup
│   │   ├── 01-PROJECT_OVERVIEW.md
│   │   ├── 02-REQUIREMENTS.md
│   │   ├── 03-CODE_STRUCTURE.md
│   │   ├── 04-ENV_CONFIGURATION.md
│   │   ├── 05-DATABASE_SETUP.md
│   │   ├── 06-DEPENDENCIES.md
│   │   ├── 07-DOCKER_SETUP.md
│   │   ├── 08-STARTUP_GUIDE.md
│   │   ├── 09-TESTING_VERIFICATION.md
│   │   └── 10-TROUBLESHOOTING.md
│   ├── architecture/                     # Tài liệu kiến trúc
│   └── report/                           # Report từ các sprint
│
├── 📁 database/                          # Scripts khởi tạo database
│   ├── postgres-init.sql                 # PostgreSQL init (Auth & Audit)
│   ├── mysql-init.sql                    # MySQL init (Product)
│   ├── sqlserver-init.sql                # SQL Server init (Order)
│   └── README.md
│
├── 📁 api-gateway/                       # API Gateway (Node.js Express)
│   ├── 📄 server.js                      # File chính
│   ├── 📄 package.json                   # Dependencies
│   ├── 📄 .env                           # Biến môi trường (DO NOT COMMIT)
│   ├── 📄 .env.example                   # Mẫu file env
│   ├── 📄 README.md                      # Hướng dẫn gateway
│   └── 📁 routes/ (nếu có)               # Route handlers
│
├── 📁 backend/
│   │
│   ├── 📁 auth-service/                  # Service xác thực (Java Spring Boot)
│   │   └── Nckh-Lu-n/
│   │       ├── pom.xml                   # Maven dependencies
│   │       ├── src/
│   │       │   └── main/
│   │       │       ├── java/vn/id/luannv/
│   │       │       │   ├── AuthServiceApplication.java  # Main class
│   │       │       │   ├── config/                      # Configuration
│   │       │       │   ├── controller/                  # REST endpoints
│   │       │       │   ├── service/                     # Business logic
│   │       │       │   ├── repository/                  # Database access
│   │       │       │   └── entity/                      # JPA entities
│   │       │       └── resources/
│   │       │           ├── application.properties       # Config
│   │       │           └── application-prod.properties  # Prod config
│   │       └── README.md                # Hướng dẫn Auth Service
│   │
│   ├── 📁 product-service/               # Service quản lý sản phẩm (Java Spring Boot)
│   │   └── Tien/Tien/
│   │       ├── pom.xml
│   │       ├── src/
│   │       │   └── main/
│   │       │       ├── java/com/BackEnd_Tien/
│   │       │       │   ├── TienApplication.java
│   │       │       │   ├── config/
│   │       │       │   ├── controller/
│   │       │       │   ├── service/
│   │       │       │   ├── repository/
│   │       │       │   └── entity/
│   │       │       └── resources/
│   │       │           ├── application.properties
│   │       │           └── application-prod.properties
│   │       └── README.md
│   │
│   ├── 📁 order-service/                 # Service quản lý đơn hàng (Java Spring Boot)
│   │   └── Nckh-C-ng/
│   │       ├── pom.xml
│   │       ├── src/
│   │       │   └── main/
│   │       │       ├── java/com/example/order_service/
│   │       │       │   ├── OrderServiceApplication.java
│   │       │       │   ├── config/
│   │       │       │   ├── controller/
│   │       │       │   ├── service/
│   │       │       │   ├── repository/
│   │       │       │   └── entity/
│   │       │       └── resources/
│   │       │           ├── application.properties
│   │       │           └── application-prod.properties
│   │       └── README.md
│   │
│   └── 📁 audit-service/                 # Service audit logging (Java Spring Boot)
│       └── Nckh-Hi-p/
│           ├── pom.xml
│           ├── src/
│           │   └── main/
│           │       ├── java/com/example/
│           │       │   ├── AuditServiceApplication.java
│           │       │   ├── config/
│           │       │   ├── controller/
│           │       │   ├── service/
│           │       │   ├── repository/
│           │       │   └── entity/
│           │       └── resources/
│           │           └── application.properties
│           └── README.md
│
├── 📁 frontend/
│   └── 📁 web-client/                    # Frontend React + Vite
│       ├── 📄 index.html                 # HTML entry point
│       ├── 📄 package.json               # Dependencies
│       ├── 📄 vite.config.js             # Vite configuration
│       ├── 📄 eslint.config.js           # ESLint configuration
│       ├── 📄 .env                       # Biến môi trường
│       ├── 📄 .env.example               # Mẫu env file
│       ├── 📄 README.md                  # Hướng dẫn frontend
│       │
│       └── 📁 src/                       # Source code React
│           ├── main.jsx                  # Entry point React
│           ├── App.jsx                   # Root component
│           │
│           ├── 📁 api/                   # API integration
│           │   ├── auth.api.js           # Auth endpoints
│           │   ├── product.api.js        # Product endpoints
│           │   ├── order.api.js          # Order endpoints
│           │   ├── user.api.js           # User endpoints
│           │   ├── address.api.js        # Address endpoints
│           │   ├── revenue.api.js        # Revenue endpoints
│           │   └── axiosClient.js        # Axios configuration
│           │
│           ├── 📁 components/            # React components
│           │   ├── features/             # Feature components
│           │   └── ui/                   # Reusable UI components
│           │
│           ├── 📁 context/               # React Context API
│           │   └── (State management)
│           │
│           ├── 📁 pages/                 # Page components
│           │   ├── HomePage
│           │   ├── LoginPage
│           │   ├── ProductPage
│           │   ├── OrderPage
│           │   └── ...
│           │
│           ├── 📁 routes/                # React Router setup
│           ├── 📁 styles/                # CSS/Tailwind styles
│           ├── 📁 assets/                # Images, fonts, etc.
│           └── 📁 utils/                 # Utility functions
│
└── 📁 scripts/                           # Batch/Shell scripts
    └── start-project.bat                 # Start toàn bộ hệ thống
```

---

## 3.2 Giải Thích Chi Tiết Mỗi Folder Lớn

### 📁 **backend/** - Backend Services
**Mục đích**: Xử lý business logic, database operations, xác thực

**4 Services độc lập:**

#### 1️⃣ **auth-service** (Nckh-Lu-n)
```
Mục đích: Xác thực & Ủy quyền
Port: 8001
Database: PostgreSQL (nckh_auth)
Chủ yếu làm:
  - Đăng ký người dùng
  - Đăng nhập (generate JWT token)
  - Refresh token
  - Xác thực người dùng
  - Quản lý role & permission
```

#### 2️⃣ **product-service** (Tien/Tien)
```
Mục đích: Quản lý sản phẩm
Port: 8003
Database: MySQL (shopquanao)
Chủ yếu làm:
  - CRUD sản phẩm (Create, Read, Update, Delete)
  - Quản lý danh mục (category)
  - Quản lý giá cả, kho hàng
  - Tìm kiếm & filter sản phẩm
```

#### 3️⃣ **order-service** (Nckh-C-ng)
```
Mục đích: Quản lý đơn hàng & thanh toán
Port: 8002
Database: SQL Server (order_dB)
Chủ yếu làm:
  - Quản lý giỏ hàng
  - Tạo & cập nhật đơn hàng
  - Thanh toán
  - Theo dõi trạng thái đơn
  - Quản lý shipping
```

#### 4️⃣ **audit-service** (Nckh-Hi-p)
```
Mục đích: Ghi nhật ký hoạt động
Port: 8004
Database: PostgreSQL (nckh_auth)
Chủ yếu làm:
  - Ghi nhật ký (log) mọi hoạt động
  - Audit trail (ai làm gì, khi nào)
  - Xem lịch sử thay đổi dữ liệu
```

**Cấu trúc Java Spring Boot (chung cho mọi service):**
```
src/main/java/
├── {package}/
│   ├── {ServiceName}Application.java     # Main class, Spring Boot entry point
│   ├── config/
│   │   ├── SecurityConfig.java           # Spring Security, JWT config
│   │   ├── DatabaseConfig.java           # Database connection
│   │   └── CacheConfig.java              # Redis cache setup
│   ├── controller/
│   │   ├── AuthController.java           # REST endpoints (@RestController)
│   │   ├── ProductController.java
│   │   └── OrderController.java
│   ├── service/
│   │   ├── AuthService.java              # Business logic
│   │   ├── ProductService.java
│   │   └── OrderService.java
│   ├── repository/
│   │   ├── UserRepository.java           # Database access (JPA)
│   │   ├── ProductRepository.java
│   │   └── OrderRepository.java
│   ├── entity/
│   │   ├── User.java                     # JPA entity (@Entity)
│   │   ├── Product.java
│   │   └── Order.java
│   ├── dto/
│   │   ├── LoginRequest.java             # Data Transfer Object
│   │   ├── LoginResponse.java
│   │   └── ProductDTO.java
│   └── exception/
│       └── CustomException.java          # Exception handling

src/main/resources/
├── application.properties                # Dev config
├── application-prod.properties           # Prod config
└── application-docker.properties         # Docker config
```

---

### 📁 **frontend/** - Frontend React
**Mục đích**: Giao diện người dùng

```
src/
├── api/                        # Gọi API backend qua axios
│   ├── axiosClient.js         # Base axios instance + interceptors
│   ├── auth.api.js            # POST /login, /register, /logout
│   ├── product.api.js         # GET products, GET product/:id
│   ├── order.api.js           # POST order, GET orders
│   └── ...
│
├── components/                 # Reusable React components
│   ├── ui/                     # Button, Input, Modal, etc. (từ Ant Design)
│   └── features/               # Feature-specific: ProductCard, OrderTable
│
├── context/                    # React Context API
│   ├── AuthContext.jsx         # Lưu user info, JWT token
│   ├── CartContext.jsx         # Lưu giỏ hàng
│   └── ProductContext.jsx      # Lưu danh sách sản phẩm
│
├── pages/                      # Trang chính
│   ├── HomePage.jsx            # Trang chủ, danh sách sản phẩm
│   ├── LoginPage.jsx           # Đăng nhập
│   ├── RegisterPage.jsx        # Đăng ký
│   ├── ProductDetailPage.jsx   # Chi tiết sản phẩm
│   ├── CartPage.jsx            # Giỏ hàng
│   ├── CheckoutPage.jsx        # Thanh toán
│   ├── OrderPage.jsx           # Danh sách đơn hàng
│   └── DashboardPage.jsx       # Dashboard admin
│
├── routes/                     # React Router v7 setup
│   └── index.jsx               # Define tất cả routes
│
├── styles/                     # CSS/Tailwind
│   ├── index.css               # Global styles
│   └── components/             # Component-specific styles
│
├── utils/                      # Utility functions
│   ├── validators.js           # Validate email, phone, etc.
│   ├── formatters.js           # Format currency, date, etc.
│   └── constants.js            # Constants, enums
│
├── main.jsx                    # React entry point (index.js)
└── App.jsx                     # Root component
```

---

### 📁 **api-gateway/** - API Gateway
**Mục đích**: Entry point duy nhất cho frontend, JWT verification, request routing

```
server.js                        # Express server chính
├── Middleware JWT verification  # Kiểm tra token hợp lệ
├── Route forwarding             # Chuyển request đến service tương ứng
└── Error handling               # Handle errors từ backend

package.json                     # Dependencies
├── express              # Web framework
├── jsonwebtoken (jwt)   # JWT verification
├── axios               # HTTP client (gọi backend)
├── cors                # CORS handling
└── dotenv              # Biến môi trường

.env                    # Variables (DO NOT COMMIT)
├── PORT=3000
├── JWT_SECRET
├── BACKEND_URL_AUTH=http://localhost:8001
├── BACKEND_URL_PRODUCT=http://localhost:8003
├── BACKEND_URL_ORDER=http://localhost:8002
└── ...
```

---

### 📁 **database/** - Scripts khởi tạo database
**Mục đích**: SQL scripts để khởi tạo tables, users, sample data

```
postgres-init.sql          # PostgreSQL: CREATE databases, users, tables cho Auth & Audit
mysql-init.sql             # MySQL: CREATE database, users, tables cho Product
sqlserver-init.sql         # SQL Server: CREATE database, users, tables cho Order
README.md                  # Hướng dẫn thủ công
```

---

## 3.3 Thứ Tự Đọc Code Hợp Lý Cho Người Mới

### 📚 Tuần Đầu (Tìm Hiểu Tổng Quan)

**Ngày 1:**
1. Đọc [docs/setup/01-PROJECT_OVERVIEW.md](./01-PROJECT_OVERVIEW.md) - Hiểu tổng quan
2. Đọc [docs/setup/03-CODE_STRUCTURE.md](./03-CODE_STRUCTURE.md) - File này
3. Xem docker-compose-all-services.yml - Hiểu infrastructure

**Ngày 2-3:**
1. Đọc [README.md ở root](../../README.md)
2. Đọc từng service README:
   - [api-gateway/README.md](../../api-gateway/README.md)
   - [backend/auth-service/Nckh-Lu-n/README.md](../../backend/auth-service/Nckh-Lu-n/README.md)
   - [backend/product-service/Tien/Tien/README.md](../../backend/product-service/Tien/Tien/README.md)
   - [backend/order-service/Nckh-C-ng/README.md](../../backend/order-service/Nckh-C-ng/README.md)

**Ngày 4-5:**
1. Chuẩn bị môi trường: [02-REQUIREMENTS.md](./02-REQUIREMENTS.md)
2. Cài dependencies: [06-DEPENDENCIES.md](./06-DEPENDENCIES.md)
3. Setup databases: [05-DATABASE_SETUP.md](./05-DATABASE_SETUP.md)

---

### 💻 Tuần Thứ 2 (Chạy & Test)

**Ngày 1-2:**
1. [08-STARTUP_GUIDE.md](./08-STARTUP_GUIDE.md) - Chạy toàn bộ hệ thống
2. [09-TESTING_VERIFICATION.md](./09-TESTING_VERIFICATION.md) - Verify mọi thứ hoạt động

**Ngày 3-5:**
1. Đọc source code từng service
   - **Auth Service**: Xem AuthController → AuthService → AuthRepository
   - **Product Service**: Xem ProductController → ProductService → ProductRepository
   - **Order Service**: Xem OrderController → OrderService → OrderRepository
2. Chạy Postman để test từng endpoint
3. Xem logs & debug

---

### 🎯 Tuần Thứ 3+ (Deep Dive)

**Tuỳ theo task:**
- **Sửa bug**: Xem file error liên quan, trace code, tìm root cause
- **Thêm feature**: Xem existing implementation, thêm controller → service → repository
- **Tối ưu**: Xem logs, identify bottleneck
- **Deploy**: Xem [07-DOCKER_SETUP.md](./07-DOCKER_SETUP.md)

---

## 3.4 File Quan Trọng Phải Biết

| File | Mục Đích | Ưu Tiên |
|------|---------|--------|
| `docker-compose-all-services.yml` | Khởi động toàn bộ | 🔴 |
| `api-gateway/server.js` | Entry point requests | 🔴 |
| `backend/auth-service/.../application.properties` | Auth config | 🔴 |
| `frontend/web-client/src/api/axiosClient.js` | API client setup | 🔴 |
| `database/postgres-init.sql` | Database schema | 🟡 |
| `frontend/web-client/src/App.jsx` | Frontend entry point | 🟡 |
| `backend/.../controller/*Controller.java` | REST endpoints | 🟡 |
| `.env` files | Environment config | 🟡 |

🔴 = Critical (phải biết)  
🟡 = Important (nên biết)

---

## 3.5 Công Cụ Hữu Ích Để Navigate Code

**VS Code Extensions:**
- **Extension Pack for Java** - Debug Java code, step-by-step
- **REST Client** - Test API trực tiếp trong VS Code
- **Thunder Client** - Alternative Postman
- **Docker** - Manage containers

**Commands:**
```bash
# Tìm kiếm file
Ctrl+P (VS Code)
file: app.properties

# Tìm kiếm từ khóa
Ctrl+Shift+F (VS Code)
search: "javax.persistence"

# Jump to definition
Ctrl+Click (Mouse)

# Find references
Ctrl+K Ctrl+R (VS Code)
```

---

