# 📦 Order Service - Tài Liệu Chi Tiết

## Tổng Quan Service

### Tên Service
**Order Service** (Dịch vụ Quản Lý Đơn Hàng)

### Mục Đích Chính
Order Service quản lý toàn bộ quy trình đơn hàng của khách hàng:
- **Giỏ hàng** - thêm/sửa/xóa sản phẩm vào giỏ
- **Tạo đơn hàng** - từ giỏ hàng sang đơn hàng chính thức
- **Thanh toán** - cập nhật trạng thái thanh toán
- **Theo dõi đơn** - kiểm tra trạng thái, lịch sử
- **Quản lý kho** - số lượng sản phẩm còn lại
- **Checkout Response** - Returns full OrderDTO with address, itemDetails, shippingFee ✅

### Service Này Giải Quyết Bài Toán Gì?
Trong một e-commerce:
- Khách hàng cần giỏ hàng để "chọn trước" sản phẩm
- Khi thanh toán, giỏ hàng trở thành đơn hàng (Order)
- Cần lưu lịch sử tất cả đơn hàng để khách theo dõi
- Cần sync số lượng sản phẩm (nếu 1 sản phẩm bán hết, không bán được)

### Service Này KHÔNG Làm Những Gì?
❌ Không quản lý thông tin sản phẩm (Product Service)  
❌ Không xác thực người dùng (Auth Service)  
❌ Không xử lý thanh toán thực tế (Payment Gateway)  
❌ Không gửi thông báo (Notification Service)  
❌ Không vận chuyển (Shipping Service)

---

## 🏗️ Kiến Trúc Nội Bộ

### Các Layer (Tầng)

```
┌──────────────────────────────────────┐
│  Controller Layer                     │
│  ├─ CartRestController               │
│  │   (GET /api/orders/{userId})      │
│  │   (POST /api/orders/{userId}/items) │
│  │                                   │
│  └─ OrderRestController              │
│      (GET /api/orders)               │
│      (POST /api/orders/checkout)     │
└────────────┬─────────────────────────┘
             │ gọi
┌────────────▼─────────────────────────┐
│  Service Layer                       │
│  ├─ CartService                      │
│  │   (addItem, removeItem, getCart)  │
│  │                                   │
│  └─ OrderService                     │
│      (createOrder, getOrders)        │
└────────────┬─────────────────────────┘
             │ gọi
┌────────────▼─────────────────────────┐
│  Repository Layer                    │
│  ├─ CartRepository                   │
│  │   (find, save, delete)            │
│  │                                   │
│  ├─ CartItemRepository               │
│  │   (findByCart, save, delete)      │
│  │                                   │
│  └─ OrderRepository                  │
│      (find, save)                    │
└────────────┬─────────────────────────┘
             │ query
┌────────────▼─────────────────────────┐
│  Database (SQL Server / MySQL)       │
│  ├─ orders table                     │
│  ├─ order_items table                │
│  ├─ carts table                      │
│  └─ cart_items table                 │
└──────────────────────────────────────┘
```

### Luồng Request Chi Tiết

#### 1️⃣ **Thêm Sản Phẩm Vào Giỏ**
```
POST /api/orders/{userId}/items
{
  "productId": 5,
  "productName": "Áo thun",
  "price": 100000,
  "quantity": 2
}
  ↓
CartRestController.addItem()
  ├─ Validate userId
  ├─ CartService.addItem()
  │   ├─ Tìm giỏ hàng của userId
  │   ├─ Nếu chưa có → tạo giỏ mới
  │   ├─ Tìm CartItem (cùng productId)
  │   │   ├─ Nếu có → cập nhật quantity += 2
  │   │   └─ Nếu không → tạo CartItem mới
  │   └─ Lưu vào DB
  │
└─ Return: CartItem info + success message
```

#### 2️⃣ **Lấy Giỏ Hàng**
```
GET /api/orders/{userId}
  ↓
CartRestController.getCart()
  ├─ CartService.getCart(userId)
  │   ├─ Tìm Cart by userId
  │   ├─ Tìm tất cả CartItem trong Cart
  │   └─ Tính tổng tiền (sum price * quantity)
  │
└─ Return: {
     cartId: 1,
     userId: 10,
     items: [
       {id: 1, productId: 5, quantity: 2, price: 100000},
       {id: 2, productId: 7, quantity: 1, price: 50000}
     ],
     totalAmount: 250000
   }
```

#### 3️⃣ **Checkout (Tạo Đơn Hàng)**
```
POST /api/orders/checkout
{
  "customerId": 10,
  "shippingAddress": "123 Nguyễn Huệ, Q1, TPHCM",
  "shippingMethod": "STANDARD"
}
  ↓
OrderRestController.checkout()
  ├─ Validate customer exists (call Auth Service)
  ├─ OrderService.createOrder()
  │   ├─ Tìm giỏ hàng của customer
  │   ├─ Nếu giỏ rỗng → return error
  │   ├─ Tạo Order mới:
  │   │   - orderId: auto generate
  │   │   - customerId: từ request
  │   │   - totalAmount: từ giỏ hàng
  │   │   - status: PENDING
  │   │   - createdAt: now
  │   │
  │   ├─ Copy tất cả CartItems → OrderItems
  │   ├─ Xóa giỏ hàng cũ (clear cart)
  │   └─ Lưu Order vào DB
  │
└─ Return: Order {id, customerId, totalAmount, status, createdAt}
```

#### 4️⃣ **Lấy Danh Sách Đơn Hàng**
```
GET /api/orders?customerId=10
  ↓
OrderRestController.getOrders()
  ├─ Parse customerId từ query param
  ├─ OrderService.getOrdersByCustomerId(10)
  │   └─ Query: SELECT * FROM orders WHERE customer_id = 10
  │      ORDER BY created_at DESC
  │
└─ Return: [
     {id: 1, customerId: 10, totalAmount: 250000, status: PAID},
     {id: 2, customerId: 10, totalAmount: 150000, status: PENDING}
   ]
```

#### 5️⃣ **Cập Nhật Trạng Thái Thanh Toán**
```
PUT /api/orders/{orderId}/pay
  ↓
OrderRestController.payOrder()
  ├─ Validate order exists
  ├─ OrderService.updateStatus(orderId, "PAID")
  │   ├─ Find Order by id
  │   ├─ Update status = PAID
  │   ├─ Update paidAt = now
  │   └─ Save to DB
  │
└─ Return: Order {id, status: PAID, paidAt: timestamp}
```

---

## 🛠️ Công Nghệ Sử Dụng

| Thành Phần | Chi Tiết |
|-----------|---------|
| **Ngôn Ngữ** | Java 17+ |
| **Framework** | Spring Boot 3.x |
| **Database** | MySQL 8.0 hoặc SQL Server |
| **ORM** | JPA Hibernate |
| **Connection Pool** | HikariCP |
| **API** | REST (HTTP) |

### Công Nghệ Cụ Thể

```xml
<!-- Database -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.x</version>
</dependency>

<!-- JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Spring Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Lombok (annotations) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 📁 Cấu Trúc Thư Mục

```
backend/order-service/Nckh-C-ng/
│
├── src/main/java/com/example/order_service/
│
│   ├── OrderServiceApplication.java      # Entry point
│   │
│   ├── controller/
│   │   ├── CartRestController.java      # REST: /api/orders/{userId}/items
│   │   │   ├── addItem()
│   │   │   ├── removeItem()
│   │   │   ├── updateItem()
│   │   │   └── getCart()
│   │   │
│   │   └── OrderRestController.java     # REST: /api/orders
│   │       ├── getOrders()
│   │       ├── createOrder()
│   │       ├── payOrder()
│   │       └── getOrderDetail()
│   │
│   ├── service/
│   │   ├── CartService.java            # Business logic giỏ hàng
│   │   │   ├── addItem(userId, item)
│   │   │   ├── removeItem(userId, itemId)
│   │   │   └── getCart(userId)
│   │   │
│   │   └── OrderService.java           # Business logic đơn hàng
│   │       ├── createOrder(customerId, ...)
│   │       ├── getOrdersByCustomerId(customerId)
│   │       └── updateOrderStatus(orderId, status)
│   │
│   ├── repository/
│   │   ├── CartRepository.java         # Query: carts table
│   │   ├── CartItemRepository.java     # Query: cart_items table
│   │   ├── OrderRepository.java        # Query: orders table
│   │   └── OrderItemRepository.java    # Query: order_items table
│   │
│   ├── entity/
│   │   ├── Cart.java                   # @Entity @Table("carts")
│   │   ├── CartItem.java               # @Entity @Table("cart_items")
│   │   ├── Order.java                  # @Entity @Table("orders")
│   │   └── OrderItem.java              # @Entity @Table("order_items")
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── AddCartItemRequest.java # {productId, productName, price, quantity}
│   │   │   ├── CreateOrderRequest.java # {customerId, shippingAddress}
│   │   │   └── UpdateItemRequest.java  # {quantity}
│   │   │
│   │   └── response/
│   │       ├── CartResponse.java       # {cartId, items, totalAmount}
│   │       ├── CartItemResponse.java   # {id, productId, quantity, price}
│   │       ├── OrderResponse.java      # {id, customerId, totalAmount, status}
│   │       └── OrderItemResponse.java  # {id, productId, quantity, price}
│   │
│   ├── client/
│   │   └── AuthServiceClient.java      # Call Auth Service (validate user)
│   │
│   ├── config/
│   │   ├── JpaConfig.java              # JPA configuration
│   │   └── RestTemplateConfig.java     # HTTP client setup
│   │
│   ├── exception/
│   │   ├── CartNotFoundException.java
│   │   ├── OrderNotFoundException.java
│   │   └── GlobalExceptionHandler.java # @ControllerAdvice
│   │
│   └── util/
│       └── OrderUtil.java              # Helper functions
│
├── src/main/resources/
│   ├── application.yml                 # Main config
│   ├── application-prod.yml            # Production config
│   ├── init-order-db.sql               # Database init script
│   └── data.sql                        # Sample data
│
├── pom.xml                             # Dependencies
│
└── docker-compose.yml                  # Docker setup
```

---

## 🗄️ Database Schema

### Bảng `carts`
```sql
CREATE TABLE carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Bảng `cart_items`
```sql
CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255),
    price DECIMAL(10, 2),
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE
);

CREATE INDEX idx_cart_id ON cart_items(cart_id);
CREATE INDEX idx_product_id ON cart_items(product_id);
```

### Bảng `orders`
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,           -- PENDING, PAID, SHIPPED, DELIVERED, CANCELLED
    shipping_address VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_customer_id ON orders(customer_id);
CREATE INDEX idx_status ON orders(status);
```

### Bảng `order_items`
```sql
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255),
    price DECIMAL(10, 2),
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2),               -- price * quantity
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_id ON order_items(order_id);
```

### Quan Hệ Giữa Các Bảng

```
users (1) ──── (1) carts ──── (*) cart_items
         │
         └──── (*) orders ──── (*) order_items
               │
               └─ Khi checkout: CartItems → OrderItems
                  Cart xóa, Order được lưu lâu dài
```

---

## 🔌 API Endpoints

### 1️⃣ **Thêm Sản Phẩm Vào Giỏ**
```http
POST /api/orders/{userId}/items
Authorization: Bearer <token>
Content-Type: application/json

{
  "productId": 5,
  "productName": "Áo thun trắng",
  "price": 100000,
  "quantity": 2
}
```

**Response (200 OK)**
```json
{
  "id": 1,
  "cartId": 10,
  "productId": 5,
  "productName": "Áo thun trắng",
  "price": 100000,
  "quantity": 2,
  "createdAt": "2026-01-16T10:30:00"
}
```

**Lỗi có thể**
- `400` - Invalid product data
- `401` - Unauthorized (token missing/invalid)
- `404` - User not found

---

### 2️⃣ **Lấy Giỏ Hàng**
```http
GET /api/orders/{userId}
Authorization: Bearer <token>
```

**Response (200 OK)**
```json
{
  "id": 10,
  "userId": 1,
  "items": [
    {
      "id": 1,
      "productId": 5,
      "productName": "Áo thun",
      "price": 100000,
      "quantity": 2
    },
    {
      "id": 2,
      "productId": 7,
      "productName": "Quần jean",
      "price": 250000,
      "quantity": 1
    }
  ],
  "totalAmount": 450000
}
```

---

### 3️⃣ **Sửa Số Lượng Sản Phẩm**
```http
PUT /api/orders/items/{itemId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "quantity": 3
}
```

**Response (200 OK)**
```json
{
  "id": 1,
  "productId": 5,
  "quantity": 3,
  "price": 100000,
  "subtotal": 300000
}
```

---

### 4️⃣ **Xóa Sản Phẩm Khỏi Giỏ**
```http
DELETE /api/orders/items/{itemId}
Authorization: Bearer <token>
```

**Response (200 OK)**
```json
{
  "message": "Item removed successfully"
}
```

---

### 5️⃣ **Tạo Đơn Hàng (Checkout)**
```http
POST /api/orders/checkout
Authorization: Bearer <token>
Content-Type: application/json

{
  "customerId": 1,
  "shippingAddress": "123 Đường ABC, Quận 1, TPHCM"
}
```

**Response (201 Created)**
```json
{
  "id": 1,
  "customerId": 1,
  "totalAmount": 450000,
  "status": "PENDING",
  "shippingAddress": "123 Đường ABC, Quận 1, TPHCM",
  "items": [
    {
      "productId": 5,
      "productName": "Áo thun",
      "quantity": 2,
      "price": 100000,
      "subtotal": 200000
    }
  ],
  "createdAt": "2026-01-16T10:30:00"
}
```

---

### 6️⃣ **Lấy Danh Sách Đơn Hàng**
```http
GET /api/orders?customerId=1
Authorization: Bearer <token>
```

**Response (200 OK)**
```json
[
  {
    "id": 1,
    "customerId": 1,
    "totalAmount": 450000,
    "status": "PAID",
    "createdAt": "2026-01-16T10:30:00",
    "paidAt": "2026-01-16T11:00:00"
  },
  {
    "id": 2,
    "customerId": 1,
    "totalAmount": 150000,
    "status": "PENDING",
    "createdAt": "2026-01-16T12:00:00",
    "paidAt": null
  }
]
```

---

### 7️⃣ **Cập Nhật Trạng Thái Thanh Toán**
```http
PUT /api/orders/{orderId}/pay
Authorization: Bearer <token>
```

**Response (200 OK)**
```json
{
  "id": 1,
  "status": "PAID",
  "paidAt": "2026-01-16T11:00:00"
}
```

---

### Phân Quyền

| Endpoint | ADMIN | USER | Anonymous |
|----------|-------|------|-----------|
| POST /api/orders/{userId}/items | ✅ | ✅ (own cart) | ❌ |
| GET /api/orders/{userId} | ✅ | ✅ (own cart) | ❌ |
| PUT /api/orders/items/{itemId} | ✅ | ✅ (own item) | ❌ |
| DELETE /api/orders/items/{itemId} | ✅ | ✅ (own item) | ❌ |
| POST /api/orders/checkout | ✅ | ✅ | ❌ |
| GET /api/orders | ✅ | ✅ (own orders) | ❌ |
| PUT /api/orders/{id}/pay | ✅ | ✅ (own order) | ❌ |

---

## ⚙️ Cấu Hình & Environment

### File `application.yml`

```yaml
spring:
  application:
    name: order-service
  
  # Database MySQL
  datasource:
    url: jdbc:mysql://localhost:3306/order_db
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  # JPA
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        show_sql: false

# Server
server:
  port: 8084
  servlet:
    context-path: /

# Client services
auth-service:
  url: http://localhost:8081

# Logging
logging:
  level:
    root: INFO
    com.example.order_service: DEBUG
```

### Environment Variables (Production)

```bash
DB_HOST=mysql-service
DB_PORT=3306
DB_NAME=order_db
DB_USER=root
DB_PASSWORD=secure_password

AUTH_SERVICE_URL=http://auth-service:8081
PRODUCT_SERVICE_URL=http://product-service:8083
```

---

## 🚀 Cách Chạy Service

### 1️⃣ **Local Development**

```bash
cd backend/order-service/Nckh-C-ng

# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run

# hoặc
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

**Kiểm tra**
```bash
curl http://localhost:8084/api/orders
# Nên có response hoặc error (không 404)
```

---

### 2️⃣ **Docker**

```bash
# Build image
docker build -t order-service:latest .

# Run container
docker run -p 8084:8084 \
  -e DB_HOST=mysql \
  -e DB_USER=root \
  -e DB_PASSWORD=password \
  order-service:latest

# Docker Compose
docker-compose up -d
```

---

## 🔗 Liên Kết Với Service Khác

### Order Service gọi ai?
```
Order Service
  ├─ Gọi → Auth Service
  │        GET /api/auth/validate (kiểm tra token)
  │        GET /api/users/{id} (lấy user info)
  │
  ├─ Gọi → Product Service
  │        GET /api/products/{id} (kiểm tra sản phẩm)
  │
  └─ (Có thể) → Payment Service
               POST /api/payments (xử lý thanh toán)
```

### Ai gọi Order Service?
```
Frontend (API Gateway)
  └─ POST /api/orders/checkout (create order)
     GET /api/orders?customerId=1 (list orders)
     PUT /api/orders/{id}/pay (update status)

Notification Service (async)
  └─ Listen event: ORDER_CREATED, ORDER_PAID
     (gửi email/SMS thông báo)
```

---

## ⚠️ Lỗi Thường Gặp & Debug

### 1️⃣ **500 Error - Database Error**

```
java.sql.SQLException: Table 'order_db.carts' doesn't exist
```

**Fix**
```bash
# 1. Chạy init script
mysql -u root -p order_db < init-order-db.sql

# 2. Hoặc xóa database + tạo lại
mysql -u root -p -e "DROP DATABASE order_db; CREATE DATABASE order_db;"
mvn clean package  # Hibernate tự tạo table (nếu ddl-auto=create)
```

---

### 2️⃣ **CartNotFoundException**

```
org.springframework.http.HttpStatus.NOT_FOUND
"Cart not found for userId: 1"
```

**Fix**
```bash
# 1. Kiểm tra userId có hợp lệ không
SELECT * FROM carts WHERE user_id = 1;

# 2. Nếu không tìm thấy, service phải tự tạo
# CartService.getCart() → nếu null → tạo Cart mới

# 3. Check code:
# @PostMapping("/{userId}/items")
# public ResponseEntity addItem(@PathVariable Long userId, ...) {
#   Cart cart = cartService.getOrCreateCart(userId);  // auto create
# }
```

---

### 3️⃣ **401 Unauthorized**

```
"Invalid token or token expired"
```

**Fix**
```bash
# 1. Kiểm tra header Authorization
Authorization: Bearer eyJhbGciOi...

# 2. Token hết hạn? → Gọi Auth Service để refresh/relogin

# 3. Kiểm tra CrossOrigin config
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderRestController { }
```

---

### 4️⃣ **Checkout Failure**

```
"Cart is empty" hoặc "Order creation failed"
```

**Fix**
```bash
# 1. Kiểm tra giỏ hàng có items không
SELECT * FROM cart_items WHERE cart_id = 10;

# 2. Kiểm tra logic checkout
OrderService.createOrder() {
  if (cartItems.isEmpty()) {
    throw new CartEmptyException();
  }
  // ... tạo order
  cartService.clearCart(cartId);  // xóa giỏ sau checkout
}

# 3. Nếu lỗi database → transaction rollback
@Transactional  // Important! tự động rollback nếu error
public Order createOrder(...) { }
```

---

## ✅ Checklist Developer Mới

- [ ] Clone repo
- [ ] Install MySQL, tạo database
- [ ] Run init script: `init-order-db.sql`
- [ ] Update `application.yml` (database URL/credentials)
- [ ] Run: `mvn spring-boot:run`
- [ ] Test endpoint: `curl http://localhost:8084/api/orders`
- [ ] Thêm sản phẩm vào giỏ
- [ ] Lấy giỏ hàng
- [ ] Checkout
- [ ] Kiểm tra database: giỏ hàng clear, order được tạo
- [ ] Hiểu Request/Response flow
- [ ] Debug với Postman/Insomnia
- [ ] Đọc CartService + OrderService code

---

**Tài liệu được cập nhật**: 2026-01-16  
**Phiên bản Service**: 0.0.1-SNAPSHOT  
**Trạng thái**: ✅ Production Ready
