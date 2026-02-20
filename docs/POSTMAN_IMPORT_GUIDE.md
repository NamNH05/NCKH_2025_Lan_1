# Hướng Dẫn Import & Sử Dụng Postman Collection

## 📥 Cách Import Collection

### Cách 1: Import File Trực Tiếp (Dễ nhất)

1. **Mở Postman**
2. **Nhấn nút "Import"** (góc trái, phần "Collections")
3. **Chọn tab "Upload Files"**
4. **Chọn file**: `Nckh-Order-Service-API.postman_collection.json`
5. **Nhấn "Import"**

### Cách 2: Kéo Thả (Drag & Drop)

1. **Mở Postman**
2. **Kéo file `Nckh-Order-Service-API.postman_collection.json` vào**
3. **Thả và confirm import**

### Cách 3: Từ URL (Nếu file ở trên server)

```
Import → Paste Raw Text
Dán toàn bộ nội dung file JSON
```

---

## ⚙️ Cài Đặt Environment

### Import Environment File

1. **Nhấn icon Settings** (ngoài cùng bên phải)
2. **Chọn "Environments"**
3. **Nhấn "Import"**
4. **Chọn file**: `Nckh-Development.postman_environment.json`
5. **Chọn environment "NCKH Development"** trong dropdown (góc trên phải)

---

## 🚀 Chạy Tests

### Lần Đầu Tiên

#### Bước 1: Khởi động Backend
```bash
# Order Service
cd backend/audit-service/Nckh-Hi-p
mvn spring-boot:run

# Hoặc từ terminal khác
# Auth Service
cd backend/auth-service/Nckh-Lu-n
mvn spring-boot:run

# Product Service
cd backend/product-service/Tien
mvn spring-boot:run
```

#### Bước 2: Test Health Check
1. Mở Collection "NCKH Order Service API Tests"
2. Chạy request "Health Check" (Setup & Authentication folder)
3. Xác minh response status 200

#### Bước 3: Login
1. Chạy request "Login User"
2. Xác minh response có `token`
3. Token sẽ **tự động lưu** vào variable `{{token}}`

### Chạy Unit Tests

```
Setup & Authentication
├── ✅ Health Check
└── ✅ Login User

Order Service - Unit Tests
├── ✅ Create Order - Success
├── ✅ Create Order - Validation Error (Empty Items)
└── ✅ Create Order - Invalid Data Type
```

**Cách chạy:**
1. Expand folder "Order Service - Unit Tests"
2. Right-click → "Run Folder"
3. Xem Results tab

### Chạy Component Tests

```
Order Service - Component Tests
├── ✅ Get Order by ID - Success
├── ✅ Get Order - Not Found
└── ✅ Get All Orders
```

**Cách chạy:**
1. Chạy "Create Order - Success" trước (để có `orderId`)
2. Chạy "Get Order by ID - Success"
3. Chạy "Get Order - Not Found" (với ID 999999)
4. Chạy "Get All Orders"

### Chạy Integration Tests

```
Order Service - Integration Tests
├── ✅ Update Order Status - PENDING to CONFIRMED
├── ✅ Update Order Status - CONFIRMED to SHIPPED
└── ✅ Update Order Status - Invalid Transition
```

**Cách chạy:**
1. Chạy "Create Order - Success" trước
2. Chạy "Update Order Status - PENDING to CONFIRMED"
3. Chạy "Update Order Status - CONFIRMED to SHIPPED"
4. Chạy "Update Order Status - Invalid Transition"

### Chạy System (E2E) Tests

```
Order Service - System (E2E) Tests
├── ✅ Complete Order Workflow
├── ✅ Cancel Order - Success
└── ✅ Verify Cancelled Order
```

**Cách chạy (Thứ tự quan trọng):**
1. ✅ Setup & Authentication → Login User
2. ✅ Order Service - Unit Tests → Create Order - Success
3. ✅ Order Service - Integration Tests → Update Order Status (×2)
4. ✅ Order Service - System Tests → Complete Order Workflow
5. ✅ Order Service - System Tests → Cancel Order - Success
6. ✅ Order Service - System Tests → Verify Cancelled Order

### Chạy Tất Cả Tests

**Qua Runner:**
1. Nhấn **"Run"** button (play icon) ở Collection
2. Chọn requests trong order:
   - Setup & Authentication (Health Check, Login)
   - Unit Tests
   - Component Tests
   - Integration Tests
   - System Tests
3. Nhấn **"Run Order Service API Tests"**

---

## 📊 Xem Kết Quả Tests

### Qua Postman UI

1. **After Running Folder/Collection**
2. **"Results" tab hiện ra** với chi tiết:
   - ✅ Passed tests (xanh)
   - ❌ Failed tests (đỏ)
   - ⏱️ Response time
   - 📋 Request/Response data

### Qua Newman (CLI)

```bash
# Basic run
newman run scripts/Nckh-Order-Service-API.postman_collection.json \
  -e scripts/Nckh-Development.postman_environment.json

# Generate HTML Report
newman run scripts/Nckh-Order-Service-API.postman_collection.json \
  -e scripts/Nckh-Development.postman_environment.json \
  -r html,json,cli \
  --reporter-html-export reports/postman-report.html

# Chạy lặp lại nhiều lần
newman run scripts/Nckh-Order-Service-API.postman_collection.json \
  -e scripts/Nckh-Development.postman_environment.json \
  --iteration-count 3

# Delay giữa requests (ms)
newman run scripts/Nckh-Order-Service-API.postman_collection.json \
  -e scripts/Nckh-Development.postman_environment.json \
  --delay-request 500
```

---

## 🔑 Variables & Data Flow

### Collection Variables (Tự động cập nhật)

| Variable | Được set bởi | Mục đích |
|----------|-------------|---------|
| `baseUrl` | Environment | Base URL của API |
| `token` | Login User request | Auth token cho requests |
| `userId` | Login User request | User ID |
| `orderId` | Create Order request | Order ID để dùng sau |
| `orderStatus` | Update Status request | Trạng thái order hiện tại |
| `requestTimestamp` | Create Order pre-request | Timestamp request |

### Cách Variables Tự Động Lưu

**Pre-request Script:**
```javascript
// Lưu ngay trước request
pm.collectionVariables.set("orderId", jsonData.id);
```

**Test Script:**
```javascript
// Lưu từ response
let jsonData = pm.response.json();
pm.collectionVariables.set("orderId", jsonData.id);
```

---

## 🧪 Ví Dụ: Chạy Toàn Bộ Workflow

### Scenario: Tạo → Lấy → Cập nhật → Hủy Đơn Hàng

**Bước 1: Setup**
```
Health Check ✓ (API chạy OK)
Login User ✓ (Lấy token)
```

**Bước 2: Unit Tests**
```
Create Order - Success ✓
  → orderId = 123
  → totalPrice = 250.00

Create Order - Validation Error ✓
Create Order - Invalid Data Type ✓
```

**Bước 3: Component Tests**
```
Get Order by ID - Success ✓
  → Retrieve order ID 123

Get Order - Not Found ✓
  → Try ID 999999

Get All Orders ✓
  → List all orders
```

**Bước 4: Integration Tests**
```
Update to CONFIRMED ✓
  → Status: PENDING → CONFIRMED

Update to SHIPPED ✓
  → Status: CONFIRMED → SHIPPED

Invalid Transition ✓
  → Reject invalid status
```

**Bước 5: E2E Tests**
```
Complete Workflow ✓
  → Xác minh tất cả steps

Cancel Order ✓
  → Status: SHIPPED → CANCELLED

Verify Cancelled ✓
  → Order không lấy được hoặc có deletedAt
```

---

## 🐛 Troubleshooting

### Problem: "orderId is required"

**Nguyên nhân**: Chưa chạy "Create Order - Success" request

**Giải pháp**:
1. Chạy "Create Order - Success" trước
2. Xác minh response có `id` field
3. Rồi chạy các requests khác

### Problem: "Authorization" Error

**Nguyên nhân**: Chưa login hoặc token hết hạn

**Giải pháp**:
1. Chạy "Login User" request
2. Xác minh response có `token`
3. Thử lại request

### Problem: Connection Refused

**Nguyên nhân**: Backend chưa chạy

**Giải pháp**:
```bash
# Khởi động backend
cd backend/audit-service/Nckh-Hi-p
mvn spring-boot:run

# Hoặc check xem service chạy trên port nào
# Sau đó update baseUrl trong environment
```

### Problem: "Invalid JSON" Error

**Nguyên nhân**: Request body format sai

**Giải pháp**:
1. Xác minh JSON syntax (mở JSON validator)
2. Kiểm tra quotes, commas, brackets
3. Copy body từ documentation lại

### Problem: Tests Fail Sau Import

**Nguyên nhân**: Environment variables chưa config

**Giải pháp**:
1. Chọn environment "NCKH Development" (dropdown góc trên phải)
2. Xác minh `baseUrl` đúng
3. Chạy "Login User" để lấy token

---

## 📝 Test Report Template

Sau khi chạy tests, tạo report:

```bash
newman run scripts/Nckh-Order-Service-API.postman_collection.json \
  -e scripts/Nckh-Development.postman_environment.json \
  -r html \
  --reporter-html-export "Test-Report-$(date +%Y%m%d-%H%M%S).html"
```

Report sẽ lưu tại: `Test-Report-20260203-120000.html`

---

## 🔄 Tích Hợp CI/CD

### GitHub Actions

```bash
# Cài Newman
npm install -g newman

# Chạy tests
newman run scripts/Nckh-Order-Service-API.postman_collection.json \
  -e scripts/Nckh-Development.postman_environment.json \
  -r json \
  --reporter-json-export results.json

# Check results
cat results.json
```

### Jenkins

```groovy
stage('Postman Tests') {
    steps {
        sh 'npm install -g newman'
        sh '''
            newman run scripts/Nckh-Order-Service-API.postman_collection.json \
                -e scripts/Nckh-Development.postman_environment.json \
                -r junit \
                --reporter-junit-export results.xml
        '''
    }
}
```

---

## 💡 Best Practices

1. **Luôn chạy Login trước** - Lấy token mới
2. **Kiểm tra orderId** - Trước khi chạy GET/PUT/DELETE
3. **Dùng Variables** - Không hardcode URLs, tokens
4. **Test theo thứ tự** - Unit → Component → Integration → E2E
5. **Xem error messages** - Học từ failures
6. **Backup collection** - Git commit thường xuyên
7. **Update documentation** - Khi thêm requests mới

---

## 📚 File Tham Khảo

- **Collection**: `scripts/Nckh-Order-Service-API.postman_collection.json`
- **Environment**: `scripts/Nckh-Development.postman_environment.json`
- **Guide**: `docs/TESTING_GUIDE.md`
- **API Docs**: Xem trong Postman (Documentation tab)

---

**Lần cập nhật**: Feb 3, 2026  
**Version**: 1.0  
**Status**: Ready to use ✅
