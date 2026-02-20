# 📦 Phần 6 – Cài Đặt Dependencies

---

## 6.1 Tổng Quan

Dự án có **4 phần** cần cài dependencies:

| Phần | Công Cụ | File | Lệnh |
|------|---------|------|------|
| **Backend (Java)** | Maven | `pom.xml` | `mvn clean install` |
| **API Gateway** | npm | `package.json` | `npm install` |
| **Frontend** | npm | `package.json` | `npm install` |
| **Scripts** | Batch/Shell | `.bat` / `.sh` | Chạy trực tiếp |

---

## 6.2 Backend: Java Dependencies (Maven)

### 📍 **Mỗi service là một Maven project độc lập**

```
backend/
├── auth-service/Nckh-Lu-n/pom.xml
├── product-service/Tien/Tien/pom.xml
├── order-service/Nckh-C-ng/pom.xml
└── audit-service/Nckh-Hi-p/pom.xml
```

### ✅ **Cài từng service:**

```bash
# Auth Service
cd backend/auth-service/Nckh-Lu-n
mvn clean install

# Product Service
cd backend/product-service/Tien/Tien
mvn clean install

# Order Service
cd backend/order-service/Nckh-C-ng
mvn clean install

# Audit Service
cd backend/audit-service/Nckh-Hi-p
mvn clean install
```

### ✅ **Cài tất cả lúc 1 (from root)**

```bash
# Từ thư mục root dự án
cd e:\Nghien_cuu_kh\Nckh

# Cài tất cả
mvn clean install -DskipTests

# DskipTests = bỏ qua unit tests (tiết kiệm thời gian)
```

### 📊 **Dependencies Quan Trọng - Auth Service**

```xml
<!-- pom.xml của Auth Service -->

<!-- Spring Boot Web (REST API) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA (Database ORM) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Spring Security (Authentication & Authorization) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- JWT Token -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- Lombok (Reduce boilerplate code) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Redis Caching -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Springdoc OpenAPI (Swagger UI) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

**Mô tả:**

```
spring-boot-starter-web
  → Cho phép tạo REST API endpoints
  → Cung cấp embedded Tomcat server
  → Xử lý HTTP requests/responses

spring-boot-starter-data-jpa
  → JPA (Java Persistence API)
  → Hibernate ORM (Object-Relational Mapping)
  → Tự động map Java objects ↔ Database rows

spring-boot-starter-security
  → Spring Security framework
  → Xác thực & phân quyền người dùng
  → Bảo vệ endpoints

postgresql (driver)
  → Kết nối tới PostgreSQL database
  → Hibernate sử dụng driver này để thực thi SQL

jjwt (JSON Web Token)
  → Tạo & verify JWT tokens
  → Dùng cho authentication stateless

lombok
  → Giảm boilerplate code
  → @Data, @Getter, @Setter, @Builder annotations
  → Tự động generate getter/setter/constructor

spring-boot-starter-data-redis
  → Redis client
  → Cache data (sessions, products, etc.)
  → Cải thiện performance

springdoc-openapi (Swagger)
  → Auto-generate API documentation
  → Provides /swagger-ui.html endpoint
  → Dễ test API trong browser
```

---

### 📊 **Dependencies Khác**

**Product Service (MySQL):**
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Order Service (SQL Server):**
```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## 6.3 Frontend: Node.js Dependencies (npm)

### 📍 **Frontend directory**

```
frontend/web-client/package.json
```

### ✅ **Cài dependencies:**

```bash
cd frontend/web-client
npm install
```

### ✅ **Cài specific package:**

```bash
npm install --save <package_name>
npm install --save-dev <package_name>  # Dev dependency
```

### 📊 **Dependencies Quan Trọng**

```json
{
  "dependencies": {
    "react": "^19.2.0",              // React library
    "react-dom": "^19.2.0",          // React DOM rendering
    "react-router-dom": "^7.12.0",   // Client-side routing
    "axios": "^1.13.2",              // HTTP client (call API)
    "antd": "^6.1.1"                 // Ant Design UI components
  },
  "devDependencies": {
    "vite": "^7.2.4",                // Build tool (faster than webpack)
    "@vitejs/plugin-react": "^5.1.1", // React plugin for Vite
    "eslint": "^9.39.1",             // Code linter
    "tailwindcss": "^4.1.18"         // CSS framework (optional)
  }
}
```

**Mô tả:**

```
react
  → Core React library
  → Components, hooks, state management

react-router-dom
  → Client-side routing
  → <BrowserRouter>, <Route>, <Link> components
  → Tạo single-page application (SPA)

axios
  → HTTP client
  → Gọi API endpoints
  → Hỗ trợ interceptors (thêm JWT token, error handling)

antd (Ant Design)
  → UI component library
  → Button, Input, Modal, Table, Form, etc.
  → Giao diện professional, responsive

vite
  → Build tool & dev server
  → Nhanh hơn webpack (cùng tác giả)
  → Chỉ hỗ trợ modern browsers

@vitejs/plugin-react
  → React plugin for Vite
  → JSX transformation

eslint
  → Linter (check code quality)
  → Detect bugs, enforce coding style
  → Chạy với: npm run lint
```

---

### ✅ **npm scripts có sẵn:**

```bash
# Develop (local dev server with hot reload)
npm run dev
# Runs on http://localhost:5173

# Build for production
npm run build
# Creates optimized build in dist/ folder

# Lint code
npm run lint
# Check for code quality issues

# Preview production build
npm run preview
# Runs production build locally
```

---

## 6.4 API Gateway: Node.js Dependencies (npm)

### 📍 **API Gateway directory**

```
api-gateway/package.json
```

### ✅ **Cài dependencies:**

```bash
cd api-gateway
npm install
```

### 📊 **Dependencies Quan Trọng**

```json
{
  "dependencies": {
    "express": "^4.18.2",            // Web framework
    "cors": "^2.8.5",                // CORS middleware
    "axios": "^1.6.0",               // HTTP client
    "jsonwebtoken": "^9.0.0",        // JWT verification
    "dotenv": "^16.3.1"              // Load .env variables
  },
  "devDependencies": {
    "nodemon": "^3.0.2"              // Auto-restart on file changes
  }
}
```

**Mô tả:**

```
express
  → Web framework for Node.js
  → Định nghĩa routes, middleware, error handling
  → Lightweight, fast, popular

cors
  → CORS (Cross-Origin Resource Sharing) middleware
  → Cho phép frontend gọi API từ domain khác
  → Ngăn chặn unauthorized cross-origin requests

axios
  → HTTP client
  → Dùng để gọi backend services
  → Hỗ trợ promises, interceptors

jsonwebtoken
  → Verify JWT tokens
  → Kiểm tra token có valid không
  → Extract user info từ token

dotenv
  → Load environment variables từ .env file
  → require('dotenv').config()

nodemon (dev only)
  → Watch files for changes
  → Auto-restart server khi file thay đổi
  → Chỉ dùng khi develop, không dùng production
```

---

### ✅ **npm scripts có sẵn:**

```bash
# Development mode (with auto-restart via nodemon)
npm run dev

# Production mode
npm start
```

---

## 6.5 Root Project Dependencies (Optional)

```json
{
  "dependencies": {
    "axios": "^1.13.2"              // HTTP client for shared use
  },
  "devDependencies": {
    "@tailwindcss/postcss": "^4.1.18", // Tailwind CSS
    "autoprefixer": "^10.4.23"        // PostCSS autoprefixer
  }
}
```

---

## 6.6 Quy Trình Cài Đặt Toàn Bộ (Step by Step)

### **Step 1: Cài Java & Maven**

```bash
# Kiểm tra version
java -version
mvn -version
```

### **Step 2: Cài Node.js**

```bash
# Kiểm tra version
node --version
npm --version
```

### **Step 3: Cài Backend Dependencies**

```bash
# From root directory
cd e:\Nghien_cuu_kh\Nckh

# Cài tất cả backend services
mvn clean install -DskipTests

# Hoặc cài từng service
cd backend/auth-service/Nckh-Lu-n && mvn clean install
cd backend/product-service/Tien/Tien && mvn clean install
cd backend/order-service/Nckh-C-ng && mvn clean install
cd backend/audit-service/Nckh-Hi-p && mvn clean install
```

### **Step 4: Cài Frontend Dependencies**

```bash
cd frontend/web-client
npm install
```

### **Step 5: Cài API Gateway Dependencies**

```bash
cd api-gateway
npm install
```

### **Step 6: Verify Installations**

```bash
# Backend
mvn -v

# Node
node -v
npm -v

# Frontend
cd frontend/web-client
npm ls  # List installed packages

# Gateway
cd api-gateway
npm ls
```

---

## 6.7 Troubleshooting Dependencies

### ❌ **"Module not found" Error**

```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules & package-lock.json
rm -rf node_modules package-lock.json  # Linux/Mac
rmdir /s /q node_modules & del package-lock.json  # Windows

# Reinstall
npm install
```

---

### ❌ **"Maven build fails"**

```bash
# Clear Maven cache
mvn clean
rm -rf ~/.m2/repository  # Linux/Mac
rmdir /s %USERPROFILE%\.m2\repository  # Windows

# Reinstall
mvn clean install
```

---

### ❌ **"Java version mismatch"**

```bash
# Check Java version
java -version

# Check Maven Java version
mvn -version

# If mismatched, set JAVA_HOME
export JAVA_HOME=/path/to/jdk17  # Linux/Mac
set JAVA_HOME=C:\Program Files\Java\jdk-17  # Windows
```

---

## 6.8 Cập Nhật Dependencies

### **Frontend (npm):**

```bash
# Check outdated packages
npm outdated

# Update all packages
npm update

# Update specific package
npm update <package_name>

# Update to latest major version
npm install <package_name>@latest
```

### **Backend (Maven):**

```bash
# Check for available updates
mvn versions:display-dependency-updates

# Update plugin versions
mvn versions:display-plugin-updates

# Update to new version
# Edit pom.xml manually, then:
mvn clean install
```

---

## 6.9 Production Dependencies vs Dev Dependencies

### **Frontend:**

```json
{
  "dependencies": {
    "react": "19.2.0",        // Production - cần thiết
    "react-router-dom": "7.12.0" // Production - cần thiết
  },
  "devDependencies": {
    "vite": "7.2.4",          // Dev - chỉ dùng build time
    "eslint": "9.39.1"        // Dev - chỉ dùng linting
  }
}
```

**Khi build production:**
```bash
npm run build  # Chỉ đóng gói dependencies (không devDependencies)
```

### **Backend:**

Maven không phân biệt rõ, nhưng:
- Scope `runtime` = chỉ cần runtime (jdbc driver)
- Scope `compile` = cần build & runtime
- Scope `test` = chỉ cần test

---

## 6.10 Checklist Installation

- [ ] Java 17/21 installed & PATH configured
- [ ] Maven 3.9.0+ installed & PATH configured
- [ ] Node.js 18+ installed
- [ ] npm 9+ installed
- [ ] Backend dependencies installed (`mvn clean install`)
- [ ] Frontend dependencies installed (`npm install`)
- [ ] Gateway dependencies installed (`npm install`)
- [ ] All `node_modules` folders created
- [ ] No error messages during installation
- [ ] Can verify: `mvn -v`, `node -v`, `npm -v`

---

