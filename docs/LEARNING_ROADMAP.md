# 📚 ROADMAP KIẾN THỨC PHÁT TRIỂN DỰ ÁN NCKH

## 🎯 Tổng Quan

File này liệt kê **tất cả kiến thức, công nghệ, framework, và kỹ năng** cần thiết để phát triển và duy trì toàn bộ dự án NCKH từ đầu đến cuối.

---

## 📑 Mục Lục

1. [Kiến Thức Cơ Bản](#kiến-thức-cơ-bản)
2. [Frontend Development](#frontend-development)
3. [Backend Development](#backend-development)
4. [API Gateway & Microservices](#api-gateway--microservices)
5. [Cơ Sở Dữ Liệu](#cơ-sở-dữ-liệu)
6. [DevOps & Deployment](#devops--deployment)
7. [Bảo Mật](#bảo-mật)
8. [Công Cụ & IDE](#công-cụ--ide)
9. [Lộ Trình Học Tập](#lộ-trình-học-tập)
10. [Tài Liệu Tham Khảo](#tài-liệu-tham-khảo)

---

## 🔧 Kiến Thức Cơ Bản

### 1. **Git & Version Control**
- **Khái niệm**:
  - Repository (repo)
  - Commit, Push, Pull
  - Branch & Merge
  - Conflict resolution
  - .gitignore file

- **Lệnh cần biết**:
  ```bash
  git init              # Tạo repo mới
  git clone <url>       # Sao chép repo
  git add .             # Staging changes
  git commit -m "msg"   # Commit changes
  git push              # Đẩy lên remote
  git pull              # Kéo từ remote
  git branch <name>     # Tạo branch
  git checkout <branch> # Chuyển branch
  git merge <branch>    # Merge branch
  ```

- **Tài liệu**: https://git-scm.com/book/en

### 2. **Command Line/Terminal**
- **Windows PowerShell**:
  - Navigation: `cd`, `ls`, `pwd`
  - File operations: `mkdir`, `New-Item`, `Remove-Item`
  - Process management: `Get-Process`, `Stop-Process`
  - Environment variables: `$env:VAR_NAME`

- **Bash (Linux/Mac)**:
  - Basic commands: `cd`, `ls`, `cat`, `grep`
  - File operations: `touch`, `rm`, `cp`, `mv`
  - Permissions: `chmod`, `chown`
  - Pipes & redirection: `|`, `>`, `<`

### 3. **JSON & YAML**
- **JSON**:
  - Key-value pairs
  - Data types: string, number, boolean, array, object, null
  - Parsing & serialization
  - Nested objects

- **YAML** (for Docker):
  - Indentation-based syntax
  - Key-value pairs
  - Lists & dictionaries
  - Comments

### 4. **REST API Basics**
- **HTTP Methods**:
  - GET - Lấy dữ liệu
  - POST - Tạo dữ liệu
  - PUT - Cập nhật đầy đủ
  - PATCH - Cập nhật một phần
  - DELETE - Xóa dữ liệu

- **Status Codes**:
  - 200 OK
  - 201 Created
  - 400 Bad Request
  - 401 Unauthorized
  - 403 Forbidden
  - 404 Not Found
  - 500 Internal Server Error

- **Headers**:
  - Content-Type
  - Authorization
  - Accept
  - CORS headers

- **Request/Response Structure**:
  ```json
  // Request
  {
    "username": "user@example.com",
    "password": "password123"
  }

  // Response
  {
    "success": true,
    "data": {
      "id": 1,
      "name": "John"
    },
    "message": "Success"
  }
  ```

---

## 🎨 Frontend Development

### 1. **HTML Cơ Bản**
- **Cấu trúc**:
  - `<!DOCTYPE>`, `<html>`, `<head>`, `<body>`
  - Meta tags
  - Semantic HTML5 tags

- **Form Elements**:
  - `<input>`, `<select>`, `<textarea>`, `<button>`
  - Form validation
  - Submit & reset

- **DOM Manipulation**:
  - Selecting elements
  - Modifying content
  - Event handling

### 2. **CSS & Styling**
- **Selectors**:
  - Element, class, ID selectors
  - Pseudo-classes, pseudo-elements
  - Attribute selectors

- **Box Model**:
  - Margin, padding, border
  - Width, height
  - Display (block, inline, flex, grid)

- **Flexbox & Grid**:
  - Flex container & items
  - Grid layout
  - Responsive design

- **Responsive Design**:
  - Media queries
  - Viewport meta tag
  - Mobile-first approach

- **Tailwind CSS** (dự án sử dụng):
  - Utility-first CSS
  - Common classes: `flex`, `grid`, `p-4`, `text-center`, `bg-blue-500`
  - Responsive prefixes: `md:`, `lg:`, `xl:`
  - Dark mode

### 3. **JavaScript Essentials**
- **Cú Pháp Cơ Bản**:
  - Variables: `let`, `const`, `var`
  - Data types: string, number, boolean, object, array, null, undefined
  - Operators: arithmetic, comparison, logical
  - Control flow: `if`, `else`, `switch`, loops

- **Functions**:
  - Function declaration
  - Arrow functions: `() => {}`
  - Parameters & return values
  - Closures
  - Higher-order functions

- **Objects & Arrays**:
  - Object literals
  - Array methods: `map()`, `filter()`, `reduce()`, `forEach()`
  - Destructuring: `const { name } = user`
  - Spread operator: `...`

- **Async Programming**:
  - Callbacks
  - Promises: `.then()`, `.catch()`, `.finally()`
  - Async/Await: `async function`, `await`
  - Error handling: `try/catch`

- **DOM Manipulation**:
  - `document.querySelector()`
  - `document.getElementById()`
  - Event listeners
  - Event delegation

- **LocalStorage & SessionStorage**:
  - Storing data client-side
  - JWT token storage
  - Session management

### 4. **React 19**
- **Cơ Bản**:
  - JSX syntax
  - Functional components
  - Props
  - State management with `useState`

- **Hooks** (quan trọng):
  - `useState` - State management
  - `useEffect` - Side effects
  - `useContext` - Context API
  - `useReducer` - Complex state
  - `useCallback` - Memoization
  - `useMemo` - Performance optimization
  - `useRef` - Direct DOM access
  - `useNavigate` - Routing (React Router v7)

- **Component Lifecycle**:
  - Mounting
  - Updating
  - Unmounting
  - useEffect dependencies

- **Conditional Rendering**:
  - Ternary operator
  - && operator
  - if/else in functions

- **Lists & Keys**:
  - Rendering lists with `.map()`
  - Key importance
  - Unique identifiers

- **Forms in React**:
  - Controlled components
  - Uncontrolled components
  - Form submission
  - Validation

### 5. **React Router v7**
- **Cơ Bản**:
  - Route definition
  - Link & NavLink
  - useNavigate hook
  - URL parameters

- **Advanced**:
  - Dynamic routing
  - Route protection (PrivateRoute)
  - Nested routes
  - Query parameters
  - Lazy loading

### 6. **State Management**
- **Context API** (dự án sử dụng):
  - Creating context: `createContext()`
  - Provider component
  - useContext hook
  - Global state management

- **Alternative**: Redux (để biết để so sánh)
  - Actions
  - Reducers
  - Store
  - Dispatch

### 7. **HTTP Requests with Axios**
```javascript
// GET
axios.get('/api/products')
  .then(response => console.log(response.data))
  .catch(error => console.error(error));

// POST
axios.post('/api/orders', {
  items: [...],
  address: '...'
}, {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});

// PUT/DELETE
axios.put('/api/products/1', data)
axios.delete('/api/products/1')

// Interceptors
axios.interceptors.request.use(config => {
  config.headers.Authorization = `Bearer ${token}`;
  return config;
});
```

### 8. **Ant Design Components**
- **Common Components**:
  - Button, Input, Form
  - Table, List
  - Modal, Drawer
  - Menu, Dropdown
  - Pagination
  - Icons
  - Layout: Row, Col, Container

- **Form Components**:
  - Form.Item
  - Form.useForm()
  - Validation rules
  - Field state management

- **Theming**:
  - Color customization
  - Token configuration
  - Dark mode

### 9. **Build Tools - Vite**
- **Cơ Bản**:
  - Scaffolding: `npm create vite@latest`
  - Dev server: `npm run dev`
  - Production build: `npm run build`
  - Preview: `npm run preview`

- **Configuration**:
  - vite.config.js
  - Environment variables
  - Aliases
  - Plugins

- **Performance**:
  - Hot Module Replacement (HMR)
  - Code splitting
  - Tree shaking
  - Lazy loading

### 10. **ESLint & Code Quality**
- **Configuration**:
  - eslintrc files
  - Rules
  - Plugins

- **Common Rules**:
  - No unused variables
  - No console logs
  - Consistent code style
  - React-specific rules

---

## 🔧 Backend Development

### 1. **Java Fundamentals**
- **Cú Pháp Cơ Bản**:
  - Variables, data types
  - Operators, control flow
  - Functions/methods
  - Classes & objects
  - Inheritance, polymorphism
  - Interfaces, abstract classes

- **Collections**:
  - List, Set, Map
  - ArrayList, HashMap
  - Iteration

- **Exception Handling**:
  - try/catch/finally
  - Custom exceptions
  - Throwing exceptions

- **File I/O**:
  - Reading/writing files
  - Streams
  - Serialization

- **Concurrency** (nếu cần):
  - Threads
  - Synchronization
  - Thread pools

### 2. **Spring Framework**
- **Spring Boot** (Thường dùng):
  - Annotations: `@SpringBootApplication`, `@ComponentScan`
  - Auto-configuration
  - Starters (spring-boot-starter-web, etc.)

- **Dependency Injection (DI)**:
  - `@Autowired`
  - `@Component`, `@Service`, `@Repository`
  - `@Configuration`
  - Constructor injection (recommended)

- **Controllers**:
  - `@RestController`
  - `@RequestMapping`, `@GetMapping`, `@PostMapping`
  - `@PathVariable`, `@RequestParam`, `@RequestBody`
  - Response entities: `ResponseEntity<T>`

- **Services**:
  - Business logic layer
  - `@Service` annotation
  - Dependency injection

- **Repositories**:
  - Data access layer
  - Database operations

### 3. **Spring Data JPA**
- **Cơ Bản**:
  - Entity classes: `@Entity`, `@Table`, `@Id`
  - Relationships: `@OneToMany`, `@ManyToOne`, `@ManyToMany`
  - Cascading

- **Repository Interface**:
  - Extending `JpaRepository<T, ID>`
  - CRUD operations
  - Custom query methods

- **Query Methods**:
  ```java
  List<User> findByUsername(String username);
  List<Product> findByPriceGreaterThan(Double price);
  Page<Product> findByCategory(String category, Pageable pageable);
  ```

- **JPQL & Native SQL**:
  - `@Query` annotation
  - Named parameters
  - Dynamic queries

### 4. **Spring Security**
- **Authentication**:
  - User authentication
  - Password encoding: `BCryptPasswordEncoder`
  - Login/logout

- **Authorization**:
  - Role-based access control (RBAC)
  - `@PreAuthorize`
  - `@Secured`

- **JWT Token** (Optional, thường dùng với API Gateway):
  - Token generation
  - Token validation
  - JWT libraries

- **CORS & CSRF**:
  - CORS configuration
  - CSRF tokens

### 5. **REST API Development**
- **HTTP Methods**:
  - GET: Retrieve data
  - POST: Create data
  - PUT: Update full resource
  - PATCH: Partial update
  - DELETE: Remove data

- **Status Codes**:
  - 200 OK, 201 Created
  - 400 Bad Request, 401 Unauthorized
  - 404 Not Found, 500 Server Error

- **Error Handling**:
  - `@ExceptionHandler`
  - Custom error responses
  - Error logging

- **Validation**:
  - `@Valid`, `@Validated`
  - Constraint annotations: `@NotNull`, `@Size`, `@Email`
  - Custom validators

### 6. **Logging**
- **SLF4J & Logback**:
  - Logger setup: `private static final Logger logger = LoggerFactory.getLogger(Class.class);`
  - Log levels: DEBUG, INFO, WARN, ERROR
  - Configuration files

- **Best Practices**:
  - Log important operations
  - Log errors with stack traces
  - Use appropriate log levels
  - Avoid logging sensitive data

### 7. **Testing** (Quan trọng)
- **JUnit 5**:
  - `@Test` annotation
  - Assertions: `assertEquals`, `assertTrue`, etc.
  - Test fixtures: `@BeforeEach`, `@AfterEach`

- **Mockito**:
  - Mocking objects: `@Mock`, `Mockito.mock()`
  - Stubbing: `when().thenReturn()`
  - Verification: `verify()`

- **Spring Boot Test**:
  - `@SpringBootTest`
  - `@WebMvcTest` for controllers
  - `@DataJpaTest` for repositories
  - TestRestTemplate

### 8. **Maven**
- **pom.xml Structure**:
  - Group ID, Artifact ID, Version
  - Dependencies
  - Plugins
  - Repositories

- **Common Commands**:
  ```bash
  mvn clean install      # Build project
  mvn test              # Run tests
  mvn spring-boot:run   # Run Spring Boot app
  mvn package           # Create JAR
  ```

- **Dependency Management**:
  - Version management
  - Dependency resolution
  - Transitive dependencies

---

## 🌐 API Gateway & Microservices

### 1. **Node.js Basics**
- **Runtime**:
  - JavaScript execution outside browser
  - Event loop
  - Non-blocking I/O

- **NPM (Node Package Manager)**:
  - package.json
  - npm install, npm start
  - Semantic versioning
  - Scripts

### 2. **Express.js**
- **Cơ Bản**:
  - Creating server
  - Listening on port
  - Routing
  - Middleware

- **Middleware**:
  - Request/response processing
  - `app.use()`
  - Next function: `next()`
  - Error handling middleware

- **Routing**:
  - Route definitions
  - HTTP methods: GET, POST, PUT, DELETE
  - Route parameters: `:id`
  - Query strings: `req.query`
  - Request body: `req.body`

- **Response Methods**:
  ```javascript
  res.send(data)          // Send response
  res.json(data)          // Send JSON
  res.status(200).json({}) // With status
  res.redirect('/path')   // Redirect
  res.render('template')  // Render view
  ```

### 3. **JWT Authentication**
- **Token Structure**:
  - Header (algorithm, type)
  - Payload (claims, user data)
  - Signature (verification)

- **JWT Libraries**:
  - jsonwebtoken (Node.js)
  - jwt-decode (Client-side)

- **Implementation**:
  ```javascript
  // Create token
  const token = jwt.sign(
    { userId, role },
    JWT_SECRET,
    { expiresIn: '24h' }
  );

  // Verify token
  const decoded = jwt.verify(token, JWT_SECRET);
  ```

- **Best Practices**:
  - Use strong secret
  - Set expiration time
  - Refresh tokens (optional)
  - Secure storage (httpOnly cookies)

### 4. **CORS & Security**
- **CORS (Cross-Origin Resource Sharing)**:
  - Origin policy
  - Preflight requests
  - Allowed methods, headers
  - Credentials handling

- **Express CORS**:
  ```javascript
  const cors = require('cors');
  app.use(cors({
    origin: 'http://localhost:5173',
    credentials: true
  }));
  ```

- **Helmet.js**:
  - Security headers
  - XSS protection
  - Frame options
  - Content-type sniffing

### 5. **Rate Limiting**
```javascript
const rateLimit = require('express-rate-limit');
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100 // limit each IP to 100 requests per windowMs
});
app.use(limiter);
```

### 6. **Microservices Architecture**
- **Concepts**:
  - Service-oriented architecture
  - Service independence
  - Inter-service communication
  - API contracts

- **API Gateway Pattern**:
  - Single entry point
  - Request routing
  - Authentication/Authorization
  - Rate limiting

- **Service Discovery** (Optional):
  - Finding service instances
  - Load balancing
  - Health checks

---

## 💾 Cơ Sở Dữ Liệu

### 1. **SQL Fundamentals**
- **DDL (Data Definition Language)**:
  - CREATE TABLE
  - ALTER TABLE
  - DROP TABLE
  - CREATE INDEX

- **DML (Data Manipulation Language)**:
  - SELECT
  - INSERT
  - UPDATE
  - DELETE

- **Queries**:
  ```sql
  SELECT * FROM users WHERE id = 1;
  SELECT * FROM products ORDER BY price DESC;
  SELECT * FROM orders WHERE user_id = 1;
  SELECT COUNT(*) FROM users;
  ```

- **Joins**:
  - INNER JOIN
  - LEFT JOIN
  - RIGHT JOIN
  - FULL OUTER JOIN

- **Aggregation**:
  - GROUP BY
  - HAVING
  - COUNT, SUM, AVG, MAX, MIN

- **Transactions**:
  - BEGIN, COMMIT, ROLLBACK
  - ACID properties
  - Isolation levels

### 2. **PostgreSQL** (Auth Service)
- **Installation & Setup**:
  - Installation
  - Creating databases
  - Creating users & roles
  - Permissions

- **Data Types**:
  - Numeric: INTEGER, DECIMAL, FLOAT
  - String: VARCHAR, TEXT
  - DateTime: TIMESTAMP, DATE
  - Boolean: BOOLEAN
  - JSON: JSONB

- **Features**:
  - Serial sequences (auto-increment)
  - Foreign keys
  - Indexes
  - Full-text search

- **Connection**:
  ```
  psql -U username -d database_name -h localhost
  ```

### 3. **MySQL** (Product Service)
- **Installation & Setup**:
  - Installation
  - Starting service
  - Creating databases

- **SQL Differences**:
  - AUTO_INCREMENT (vs SERIAL in PostgreSQL)
  - LIMIT syntax
  - Data types variations

- **Engines**:
  - InnoDB (default, ACID)
  - MyISAM
  - Storage considerations

- **Connection**:
  ```
  mysql -u username -p database_name
  ```

### 4. **SQL Server** (Order Service)
- **T-SQL**:
  - SQL Server-specific SQL syntax
  - Stored procedures
  - Triggers
  - Views

- **Features**:
  - Management Studio
  - Data types
  - Indexes
  - Transactions

- **Connection Strings**:
  ```
  Data Source=localhost,1433;Initial Catalog=database;User Id=sa;Password=****
  ```

### 5. **Oracle** (Audit Service)
- **Basics**:
  - Schemas (users)
  - Tablespaces
  - Roles & privileges

- **Data Types**:
  - Numeric, VARCHAR2, DATE, CLOB
  - Constraints

- **Features**:
  - Sequences
  - Packages
  - Stored procedures

### 6. **Database Design**
- **ER Model** (Entity-Relationship):
  - Entities & attributes
  - Relationships
  - Cardinality

- **Normalization**:
  - 1NF, 2NF, 3NF
  - Reducing redundancy
  - Data integrity

- **Indexing**:
  - Primary keys
  - Foreign keys
  - Unique indexes
  - Performance optimization

- **Relationships**:
  - One-to-Many
  - Many-to-Many
  - One-to-One
  - Self-referencing

### 7. **Database Tools**
- **GUI Tools**:
  - pgAdmin (PostgreSQL)
  - MySQL Workbench
  - SQL Server Management Studio
  - DBeaver (Universal)

- **CLI Tools**:
  - psql, mysql, sqlplus commands
  - Scripting & automation

### 8. **Connection Pooling**
- **Why**: Performance optimization
- **Concepts**: Connection reuse, pool size
- **Implementation**: Via ORM (JPA), libraries

---

## 🐳 DevOps & Deployment

### 1. **Docker Basics**
- **Concepts**:
  - Containers vs VMs
  - Images vs Containers
  - Registry (Docker Hub, etc.)

- **Dockerfile**:
  ```dockerfile
  FROM openjdk:17-jdk-slim
  WORKDIR /app
  COPY target/app.jar app.jar
  ENTRYPOINT ["java", "-jar", "app.jar"]
  ```

- **Common Commands**:
  ```bash
  docker build -t image-name .        # Build image
  docker run -p 8080:8080 image-name  # Run container
  docker ps                           # List running containers
  docker logs container-id            # View logs
  docker stop container-id            # Stop container
  docker rm container-id              # Remove container
  ```

### 2. **Docker Compose**
- **docker-compose.yml**:
  ```yaml
  version: '3.8'
  services:
    web:
      build: .
      ports:
        - "8080:8080"
    db:
      image: postgres:15
      environment:
        POSTGRES_PASSWORD: password
  ```

- **Commands**:
  ```bash
  docker-compose up              # Start services
  docker-compose down            # Stop & remove
  docker-compose logs -f         # View logs
  docker-compose exec service bash # Execute command
  ```

### 3. **Environment Variables**
- **.env File**:
  ```
  DATABASE_URL=postgresql://user:pass@localhost/db
  JWT_SECRET=your-secret-key
  PORT=3000
  ```

- **Loading in Node.js**:
  ```javascript
  require('dotenv').config();
  const dbUrl = process.env.DATABASE_URL;
  ```

- **Loading in Java**:
  ```yaml
  # application.properties
  spring.datasource.url=${DB_URL}
  server.port=${PORT:8080}
  ```

### 4. **CI/CD Basics** (Optional but good to know)
- **GitHub Actions**:
  - Workflows
  - Triggers
  - Jobs & steps

- **Pipeline Stages**:
  - Build
  - Test
  - Deploy

- **Deployment Targets**:
  - Docker Registry
  - Cloud platforms (AWS, Azure, GCP)
  - Kubernetes

### 5. **Cloud Platforms** (Optional)
- **AWS**:
  - EC2 (Virtual machines)
  - RDS (Managed databases)
  - S3 (Object storage)

- **Azure**:
  - App Service
  - Azure SQL Database
  - Blob Storage

- **Google Cloud**:
  - Compute Engine
  - Cloud SQL
  - Cloud Storage

---

## 🔐 Bảo Mật

### 1. **Authentication vs Authorization**
- **Authentication**: Xác minh danh tính (Login)
- **Authorization**: Xác minh quyền hạn (Admin/User roles)

### 2. **Password Security**
- **Hashing Algorithms**:
  - bcrypt (recommended)
  - PBKDF2
  - scrypt

- **Implementation**:
  ```java
  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
  String hashedPassword = encoder.encode("plainPassword");
  boolean matches = encoder.matches("plainPassword", hashedPassword);
  ```

### 3. **HTTPS/TLS**
- **Concepts**:
  - Encryption
  - Certificate
  - Public/Private keys

- **Implementation**:
  - Self-signed certificates (dev)
  - Let's Encrypt (production)

### 4. **CSRF Protection**
- **Cross-Site Request Forgery**:
  - Token-based protection
  - Same-site cookies

- **Implementation**:
  - csurf middleware (Node.js)
  - Spring Security CSRF (Java)

### 5. **Input Validation & Sanitization**
- **Validation**:
  - Type checking
  - Length limits
  - Pattern matching

- **Sanitization**:
  - Remove dangerous characters
  - Escape special characters
  - Parameterized queries (prevent SQL injection)

### 6. **OWASP Top 10**
- **Injection** (SQL, Command)
- **Broken Authentication**
- **Sensitive Data Exposure**
- **XML External Entities (XXE)**
- **Broken Access Control**
- **Security Misconfiguration**
- **XSS (Cross-Site Scripting)**
- **Insecure Deserialization**
- **Using Components with Known Vulnerabilities**
- **Insufficient Logging & Monitoring**

### 7. **API Security**
- **API Key Authentication**
- **OAuth 2.0**
- **JWT Tokens**
- **Rate Limiting**
- **CORS Configuration**

---

## 🛠️ Công Cụ & IDE

### 1. **Development Environment**
- **IDE/Editors**:
  - Visual Studio Code (Frontend)
  - IntelliJ IDEA (Java Backend) - Recommended
  - Eclipse (Alternative Java IDE)

- **Extensions/Plugins**:
  - Prettier (Code formatter)
  - ESLint (JavaScript linter)
  - Spring Boot Extension Pack
  - Lombok annotation processor
  - Database tools

### 2. **Terminal & Shell**
- **PowerShell** (Windows):
  - Navigation
  - File operations
  - Process management
  - Environment variables

- **Git Bash / WSL** (Windows):
  - Linux-like environment
  - Better compatibility

### 3. **Version Control Platforms**
- **GitHub**:
  - Repository hosting
  - Pull requests
  - Issues
  - Actions (CI/CD)

### 4. **API Testing Tools**
- **Postman**:
  - API requests
  - Collections
  - Environment variables
  - Automated testing

- **cURL** (Command line):
  ```bash
  curl -X POST http://localhost:3000/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"user","password":"pass"}'
  ```

- **Thunder Client** (VS Code)

### 5. **Database Tools**
- **pgAdmin** (PostgreSQL)
- **MySQL Workbench**
- **SQL Server Management Studio**
- **DBeaver** (Universal)
- **Adminer** (Web-based)

### 6. **Monitoring & Logging**
- **Log Viewers**:
  - Tail (real-time logs)
  - Log aggregation tools

- **Performance Monitoring**:
  - DevTools (Browser)
  - Spring Boot Actuator (Metrics)
  - JProfiler (Java profiling)

---

## 📚 Lộ Trình Học Tập

### Phase 1: Nền Tảng (2-3 tuần)

**1. Kiến Thức Cơ Bản**
- [ ] Git & GitHub workflow
- [ ] Command line basics
- [ ] JSON & REST API concepts
- [ ] HTTP protocol

**2. Frontend Basics**
- [ ] HTML & semantic HTML
- [ ] CSS & Flexbox/Grid
- [ ] JavaScript fundamentals
- [ ] ES6+ features (arrow functions, destructuring, async/await)

**3. Backend Basics**
- [ ] Java fundamentals
- [ ] OOP concepts (classes, inheritance, interfaces)
- [ ] SQL basics & database design

**Tài Liệu**:
- HTML: https://developer.mozilla.org/en-US/docs/Web/HTML
- CSS: https://developer.mozilla.org/en-US/docs/Web/CSS
- JavaScript: https://javascript.info/
- Java: https://docs.oracle.com/javase/

---

### Phase 2: Frontend Development (3 tuần)

**1. React Fundamentals**
- [ ] Functional components
- [ ] JSX syntax
- [ ] Props & state management
- [ ] Hooks (useState, useEffect, useContext)
- [ ] Component lifecycle

**2. Advanced React**
- [ ] React Router v7
- [ ] Form handling & validation
- [ ] State management with Context API
- [ ] Performance optimization

**3. Styling & UI**
- [ ] Tailwind CSS
- [ ] Ant Design components
- [ ] Responsive design

**4. HTTP & API Integration**
- [ ] Axios setup & configuration
- [ ] Request/response handling
- [ ] Error handling
- [ ] JWT token management

**Tài Liệu**:
- React: https://react.dev/
- React Router: https://reactrouter.com/
- Ant Design: https://ant.design/
- Axios: https://axios-http.com/

---

### Phase 3: Backend Development (4 tuần)

**1. Spring Framework**
- [ ] Spring Boot basics
- [ ] Dependency injection
- [ ] Controllers & REST endpoints
- [ ] Exception handling
- [ ] Logging

**2. Spring Data & JPA**
- [ ] Entity modeling
- [ ] Relationships (OneToMany, ManyToMany)
- [ ] Repository pattern
- [ ] Query methods
- [ ] JPQL & native SQL

**3. Spring Security**
- [ ] Authentication (Login/Register)
- [ ] Authorization & role-based access
- [ ] Password hashing (bcrypt)
- [ ] CORS configuration

**4. Testing**
- [ ] Unit testing (JUnit 5)
- [ ] Mocking (Mockito)
- [ ] Integration testing
- [ ] Test coverage

**5. Maven & Build Tools**
- [ ] pom.xml structure
- [ ] Dependency management
- [ ] Build & packaging
- [ ] Plugins configuration

**Tài Liệu**:
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Spring Security: https://spring.io/projects/spring-security
- Maven: https://maven.apache.org/

---

### Phase 4: API Gateway & Microservices (2 tuần)

**1. Node.js & Express.js**
- [ ] Node.js runtime
- [ ] Express basics
- [ ] Middleware
- [ ] Routing

**2. JWT Authentication**
- [ ] Token generation
- [ ] Token verification
- [ ] Refresh tokens

**3. Security**
- [ ] CORS configuration
- [ ] Helmet security headers
- [ ] CSRF protection
- [ ] Rate limiting
- [ ] Input validation

**4. API Gateway Pattern**
- [ ] Request routing
- [ ] Centralized authentication
- [ ] Error handling
- [ ] Logging & monitoring

**Tài Liệu**:
- Node.js: https://nodejs.org/docs/
- Express: https://expressjs.com/
- JWT: https://jwt.io/
- Helmet: https://helmetjs.github.io/

---

### Phase 5: Database & Storage (2 tuần)

**1. SQL Fundamentals**
- [ ] SELECT queries
- [ ] INSERT, UPDATE, DELETE
- [ ] Joins & aggregation
- [ ] Transactions

**2. Database-Specific**
- [ ] PostgreSQL (Auth Service)
- [ ] MySQL (Product Service)
- [ ] SQL Server (Order Service)
- [ ] Oracle (Audit Service)

**3. Database Tools**
- [ ] GUI tools (pgAdmin, Workbench, etc.)
- [ ] CLI tools (psql, mysql, etc.)
- [ ] Scripting & automation

**4. Connection & ORM**
- [ ] Connection pooling
- [ ] Spring Data JPA
- [ ] Hibernate

**Tài Liệu**:
- SQL Tutorial: https://www.w3schools.com/sql/
- PostgreSQL: https://www.postgresql.org/docs/
- MySQL: https://dev.mysql.com/doc/
- SQL Server: https://learn.microsoft.com/en-us/sql/

---

### Phase 6: DevOps & Deployment (2 tuần)

**1. Docker**
- [ ] Docker concepts
- [ ] Dockerfile creation
- [ ] Docker image building
- [ ] Container running & management

**2. Docker Compose**
- [ ] docker-compose.yml syntax
- [ ] Multi-container setup
- [ ] Service networking
- [ ] Environment variables

**3. Environment Configuration**
- [ ] .env files
- [ ] Environment variable management
- [ ] Different environments (dev, test, prod)

**4. CI/CD Basics** (Optional)
- [ ] GitHub Actions
- [ ] Automated testing
- [ ] Automated deployment

**Tài Liệu**:
- Docker: https://docs.docker.com/
- Docker Compose: https://docs.docker.com/compose/

---

### Phase 7: Integration & Project Work (3-4 tuần)

**1. Full-Stack Integration**
- [ ] Connect frontend to API Gateway
- [ ] Connect API Gateway to backend services
- [ ] Connect backend to databases
- [ ] Test entire workflow

**2. Feature Development**
- [ ] Authentication flow
- [ ] Product listing & search
- [ ] Shopping cart
- [ ] Order management
- [ ] Admin dashboard

**3. Security & Hardening**
- [ ] HTTPS configuration
- [ ] Input validation
- [ ] Error handling
- [ ] Logging & monitoring

**4. Testing & QA**
- [ ] Unit tests
- [ ] Integration tests
- [ ] API testing (Postman)
- [ ] Manual testing
- [ ] Bug fixing

**5. Documentation**
- [ ] API documentation
- [ ] Code comments
- [ ] README files
- [ ] Deployment guide

---

### Phase 8: Advanced Topics (Optional)

**1. Performance Optimization**
- [ ] Caching strategies
- [ ] Database query optimization
- [ ] Frontend code splitting
- [ ] Lazy loading

**2. Advanced Security**
- [ ] OAuth 2.0
- [ ] API rate limiting strategies
- [ ] Security audits
- [ ] Vulnerability scanning

**3. Microservices Advanced**
- [ ] Service discovery
- [ ] Circuit breakers
- [ ] API versioning
- [ ] Backward compatibility

**4. Monitoring & Logging**
- [ ] Centralized logging
- [ ] Performance monitoring
- [ ] Error tracking
- [ ] Alerting systems

**5. Cloud Deployment**
- [ ] AWS/Azure/GCP basics
- [ ] Containerized deployment
- [ ] Kubernetes (optional)
- [ ] Serverless functions

---

## 🔗 Tài Liệu Tham Khảo

### Official Documentation
- **React**: https://react.dev/
- **Express.js**: https://expressjs.com/
- **Spring Boot**: https://spring.io/projects/spring-boot
- **Node.js**: https://nodejs.org/docs/

### Learning Resources
- **MDN Web Docs**: https://developer.mozilla.org/
- **Official Java Docs**: https://docs.oracle.com/javase/
- **TypeScript Handbook**: https://www.typescriptlang.org/docs/
- **SQL Tutorial**: https://www.w3schools.com/sql/

### Online Courses
- **Udemy**:
  - Complete JavaScript Course
  - React & Redux
  - Java Spring Boot
  - Docker Mastery

- **Coursera**:
  - Web Design Specialization
  - Java Programming
  - Cloud Computing

- **freeCodeCamp** (YouTube):
  - JavaScript Algorithms
  - React Course
  - Java Full Course
  - Docker & Kubernetes

### Books
- **JavaScript**:
  - "You Don't Know JS" by Kyle Simpson
  - "Eloquent JavaScript" by Marijn Haverbeke

- **Java**:
  - "Effective Java" by Joshua Bloch
  - "Spring in Action" by Craig Walls

- **Web Development**:
  - "Web Design with HTML, CSS, JavaScript and jQuery" by Jon Duckett
  - "The Pragmatic Programmer" by Andrew Hunt & David Thomas

### Communities
- **Stack Overflow**: https://stackoverflow.com/
- **GitHub Discussions**: https://github.com/
- **Reddit**:
  - r/learnprogramming
  - r/webdev
  - r/java

---

## ✅ Checklist Kỹ Năng

### Frontend Developer Checklist
- [ ] JavaScript fundamentals + ES6+
- [ ] React hooks & Context API
- [ ] React Router v7
- [ ] Axios & REST API calls
- [ ] Ant Design components
- [ ] Tailwind CSS
- [ ] Form validation
- [ ] State management
- [ ] Error handling
- [ ] JWT token management
- [ ] Responsive design
- [ ] Browser DevTools
- [ ] ESLint & code quality

### Backend Developer Checklist
- [ ] Java OOP fundamentals
- [ ] Spring Boot & Spring Security
- [ ] Spring Data JPA & Hibernate
- [ ] REST API design
- [ ] Database design & SQL
- [ ] Authentication & Authorization
- [ ] Exception handling & logging
- [ ] Unit testing & Mockito
- [ ] Maven build tool
- [ ] JWT token implementation
- [ ] Input validation & sanitization
- [ ] Performance optimization
- [ ] API documentation

### Full-Stack Developer Checklist
- [ ] Frontend + Backend checklists
- [ ] Microservices architecture
- [ ] API Gateway pattern
- [ ] Docker & containerization
- [ ] Database tools
- [ ] Git & version control
- [ ] REST API testing (Postman)
- [ ] CI/CD basics
- [ ] Security best practices
- [ ] Deployment & DevOps
- [ ] System design & scalability
- [ ] Monitoring & logging

---

## 📝 Tóm Tắt

Để phát triển toàn bộ dự án NCKH, bạn cần:

1. **8-12 tuần** để nắm vững các kiến thức cơ bản
2. **Kỹ năng Frontend**: React, JavaScript, HTML/CSS, Axios
3. **Kỹ năng Backend**: Java, Spring Boot, Spring Security, JPA
4. **Kỹ năng API Gateway**: Node.js, Express, JWT, security middleware
5. **Kỹ năng Database**: SQL, PostgreSQL, MySQL, SQL Server, Oracle
6. **Kỹ năng DevOps**: Docker, Docker Compose, Environment configuration
7. **Kỹ năng Soft**: Problem-solving, debugging, documentation, teamwork

**Khuyến nghị**: Học từ dễ đến khó, từ foundation đến specialized, và thực hành nhiều!

---

**Cập nhật**: February 2026
**Phiên bản**: 1.0
