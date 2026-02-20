# 🧪 HƯỚNG DẪN CHẠY CÁC LOẠI TEST TRONG DỰ ÁN NCKH

## 📋 OVERVIEW

```
3 LOẠI TEST TRONG DỰ ÁN
│
├── 1️⃣ COMPONENT TEST (Unit Test) - 6 tests
│   ├── Backend Java (3 tests)
│   └── Frontend JavaScript (2 tests)
│
├── 2️⃣ INTEGRATION TEST (tích hợp) - 5 tests
│   └── Auth Service (register→login→profile)
│
└── 3️⃣ SYSTEM TEST (E2E - POSTMAN) - 6 tests ✅ 100% PASS
    └── Auth Service Login API (complete workflows)
```

---

## 1️⃣ COMPONENT TEST - BACKEND (Java - JUnit 5)

### **Test File 1: UserServiceTest.java (Bạn 1 - Test 1)**

**📁 File Path**:
```
backend/auth-service/Nckh-Lu-n/src/test/java/vn/id/luannv/auth_service/service/UserServiceTest.java
```

**🎯 Mục đích**: Kiểm thử hàm `validatePassword()` - Bạn 1 Test 1

**5️⃣ Test Cases**:
1. TC1: Correct Password ✅
2. TC2: Wrong Password ✅
3. TC3: Empty Password ✅
4. TC4: Special Characters Password ✅
5. TC5: Null Password Exception ✅

**▶️ Cách chạy**:

```bash
# Option 1: Chạy toàn bộ test class
cd backend/auth-service/Nckh-Lu-n
mvn test -Dtest=UserServiceTest

# Option 2: Chạy 1 test method cụ thể
mvn test -Dtest=UserServiceTest#testValidatePassword_CorrectPassword_ReturnsTrue

# Option 3: Chạy và xem output chi tiết
mvn test -Dtest=UserServiceTest -X
```

**✅ Expected Output**:
```
[INFO] Running vn.id.luannv.auth_service.service.UserServiceTest
✅ TC1 PASS: Correct password validated
✅ TC2 PASS: Wrong password rejected
✅ TC3 PASS: Empty password rejected
✅ TC4 PASS: Special character password validated
✅ TC5 PASS: Null password throws exception
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.234 s
[INFO] BUILD SUCCESS
```

---

### **Test File 2: JwtUtilTest.java (Bạn 1 - Test 2)**

**📁 File Path**:
```
backend/auth-service/Nckh-Lu-n/src/test/java/vn/id/luannv/auth_service/config/JwtUtilTest.java
```

**🎯 Mục đích**: Kiểm thử hàm `generateToken()` - Bạn 1 Test 2

**5️⃣ Test Cases**:
1. TC1: Token Generation Success ✅
2. TC2: Token Structure (3 parts) ✅
3. TC3: Token Claims Correctness ✅
4. TC4: Token Expiration ✅
5. TC5: Different Users Different Tokens ✅

**▶️ Cách chạy**:

```bash
cd backend/auth-service/Nckh-Lu-n
mvn test -Dtest=JwtUtilTest

# Chạy test cụ thể
mvn test -Dtest=JwtUtilTest#testGenerateToken_Success
```

**✅ Expected Output**:
```
[INFO] Running vn.id.luannv.auth_service.config.JwtUtilTest
✅ TC1 PASS: Token generated successfully
Token preview: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIx...
✅ TC2 PASS: Token has correct structure
✅ TC3 PASS: Token claims are correct
  Subject: 1
  Username: john
✅ TC4 PASS: Token has valid expiration
  Expires at: Wed Feb 06 2026 00:00:00 GMT+0700
✅ TC5 PASS: Different users produce different tokens
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.156 s
[INFO] BUILD SUCCESS
```

---

### **Test File 3: ProductServiceTest.java (Bạn 2 - Tests 1&2)**

**📁 File Path**:
```
backend/product-service/Tien/Tien/src/test/java/com/BackEnd_Tien/Service/ProductServiceTest.java
```

**🎯 Mục đích**: 
- TC1: Search Products (Bạn 2 - Test 1)
- TC2: Get Product by ID (Bạn 2 - Test 2)

**5️⃣ Test Cases**:
1. TC1: Search by Keyword ✅
2. TC2: Search by Category ✅
3. TC3: Empty Search Results ✅
4. TC4: Case Insensitive Search ✅
5. TC5: Combined Filters ✅

**▶️ Cách chạy**:

```bash
cd backend/product-service/Tien/Tien
mvn test -Dtest=ProductServiceTest

# Chạy test cụ thể
mvn test -Dtest=ProductServiceTest#testSearchProducts_WithKeyword_ReturnsMatching
```

**✅ Expected Output**:
```
[INFO] Running com.BackEnd_Tien.Service.ProductServiceTest
✅ TC1 PASS: Search by keyword found 2 products
✅ TC2 PASS: Category search found 2 products
✅ TC3 PASS: No results returned for non-existent keyword
✅ TC4 PASS: Case insensitive search works
✅ TC5 PASS: Combined filters work correctly
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.178 s
[INFO] BUILD SUCCESS
```

---

## 2️⃣ COMPONENT TEST - FRONTEND (JavaScript - Jest)

### **Test File: validators.test.js (Bạn 3 - Tests 1&2)**

**📁 File Path**:
```
frontend/web-client/src/__tests__/utils/validators.test.js
```

**🎯 Mục đích**:
- TC1-5: Email Validation (Bạn 3 - Test 1)
- TC1-6: Username Validation (Bạn 3 - Test 2)

**11️⃣ Test Cases**:

**Email Validation**:
1. TC1: Valid Email Format ✅
2. TC2: Email without @ ✅
3. TC3: Email without domain ✅
4. TC4: Email with spaces ✅
5. TC5: Empty email ✅

**Username Validation**:
1. TC1: Valid Username Format ✅
2. TC2: Username too short ✅
3. TC3: Username too long ✅
4. TC4: Special characters ✅
5. TC5: Spaces in username ✅
6. TC6: Empty username ✅

**▶️ Cách chạy**:

```bash
# Option 1: Chạy toàn bộ validators test
cd frontend/web-client
npm test validators.test.js

# Option 2: Chạy test cụ thể
npm test -- --testNamePattern="Email Validation"
npm test -- --testNamePattern="Username Validation"

# Option 3: Chạy trong watch mode
npm test validators.test.js -- --watch

# Option 4: Chạy với coverage
npm test validators.test.js -- --coverage
```

**✅ Expected Output**:
```
PASS  src/__tests__/utils/validators.test.js

Email Validation
  ✓ should validate correct email format (5ms)
    ✅ TC1 PASS: Valid emails accepted
  ✓ should reject email without @ (2ms)
    ✅ TC2 PASS: Emails without @ rejected
  ✓ should reject email without domain (1ms)
    ✅ TC3 PASS: Emails without domain rejected
  ✓ should reject email with spaces (1ms)
    ✅ TC4 PASS: Emails with spaces rejected
  ✓ should reject empty email (1ms)
    ✅ TC5 PASS: Empty email rejected

Username Validation
  ✓ should validate correct username format (3ms)
    ✅ TC1 PASS: Valid usernames accepted
  ✓ should reject username shorter than 3 characters (1ms)
    ✅ TC2 PASS: Short usernames rejected
  ✓ should reject username longer than 20 characters (1ms)
    ✅ TC3 PASS: Long usernames rejected
  ✓ should reject username with special characters (2ms)
    ✅ TC4 PASS: Usernames with special chars rejected
  ✓ should reject username with spaces (1ms)
    ✅ TC5 PASS: Usernames with spaces rejected
  ✓ should reject empty username (1ms)
    ✅ TC6 PASS: Empty username rejected

Test Suites: 1 passed, 1 total
Tests:       11 passed, 11 total
Snapshots:   0 total
Time:        2.345 s
```

---

## 3️⃣ INTEGRATION TEST - Java with MockMvc

### **Test File: AuthIntegrationTest.java**

**📁 File Path**:
```
backend/auth-service/Nckh-Lu-n/src/test/java/vn/id/luannv/auth_service/AuthIntegrationTest.java
```

**🎯 Mục đích**: Kiểm thử tích hợp Auth workflow (login → token → protected endpoint)

**5️⃣ Test Cases**:
1. TC1: Complete Auth Flow (login→profile) ✅
2. TC2: Wrong Password Login ✅
3. TC3: Empty Username Validation ✅
4. TC4: No Token Access Denied ✅
5. TC5: Invalid Token Rejection ✅

**▶️ Cách chạy**:

```bash
cd backend/auth-service/Nckh-Lu-n
mvn test -Dtest=AuthIntegrationTest

# Chạy test cụ thể
mvn test -Dtest=AuthIntegrationTest#testAuthFlow_LoginAndAccessProfile

# Chạy với output chi tiết
mvn test -Dtest=AuthIntegrationTest -X
```

**✅ Expected Output**:
```
[INFO] Running vn.id.luannv.auth_service.AuthIntegrationTest
✅ Step 1 PASS: Login successful
  Token obtained (length: 185)
✅ Step 2 PASS: Protected endpoint accessed with token
✅ INTEGRATION TEST COMPLETE: Auth flow works end-to-end
✅ TC2 PASS: Wrong password rejected
✅ TC3 PASS: Empty username rejected
✅ TC4 PASS: Protected endpoint denied without token
✅ TC5 PASS: Invalid token rejected
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.567 s
[INFO] BUILD SUCCESS
```

---

## 4️⃣ SYSTEM TEST - POSTMAN (E2E - Already Complete) ✅

### **POSTMAN Collection: Auth Service E2E**

**📁 File Path**:
```
scripts/NCKH_E-commerce_Testing.postman_collection.json
scripts/NCKH_E-commerce_Testing.postman_environment.json
```

**🎯 Mục đích**: End-to-End testing của Auth Service Login API

**6️⃣ Test Cases** (✅ 100% PASS):
1. TC1: Login Success → 200 ✅
2. TC2: Wrong Password → 401 ✅
3. TC3: User Not Found → 401 ✅
4. TC4: Empty Username → 400 ✅
5. TC5: Empty Password → 400 ✅
6. TC6: Token Reusability → 200 ✅

**▶️ Cách chạy**:

```bash
# Option 1: Chạy toàn bộ collection
postman collection run \
  scripts/NCKH_E-commerce_Testing.postman_collection.json \
  -e scripts/NCKH_E-commerce_Testing.postman_environment.json

# Option 2: Chạy qua POSTMAN GUI
# - Mở POSTMAN
# - Import collection: scripts/NCKH_E-commerce_Testing.postman_collection.json
# - Import environment: scripts/NCKH_E-commerce_Testing.postman_environment.json
# - Click "Run Collection" button
# - Chạy lần lượt TC1 → TC6

# Option 3: Chạy với newman CLI
npm install -g newman
newman run scripts/NCKH_E-commerce_Testing.postman_collection.json \
  -e scripts/NCKH_E-commerce_Testing.postman_environment.json
```

**✅ Expected Output**:
```
POSTMAN Collection Run
═════════════════════════════════════════

TC1 - Login Success
  Status: ✅ 200 OK
  Assertions: ✅ 6/6 pass
  Token Saved: ✅ YES

TC2 - Login Wrong Password
  Status: ✅ 401 Unauthorized
  Assertions: ✅ 4/4 pass

TC3 - Login User Not Found
  Status: ✅ 401 Unauthorized
  Assertions: ✅ 3/3 pass

TC4 - Login Empty Username
  Status: ✅ 400 Bad Request
  Assertions: ✅ 3/3 pass

TC5 - Login Empty Password
  Status: ✅ 400 Bad Request
  Assertions: ✅ 3/3 pass

TC6 - Validate Token
  Status: ✅ 200 OK
  Assertions: ✅ 5/5 pass
  Token Verified: ✅ YES

═════════════════════════════════════════
📊 SUMMARY
  Total Tests: 6
  ✅ Passed: 6 (100%)
  ❌ Failed: 0 (0%)
  ⏭️  Skipped: 0

  Total Execution Time: 656ms
  Average Response: 109ms
═════════════════════════════════════════
```

---

## 📊 TÓMLỘC CHẠY TẤT CẢ TEST

### **Chạy tất cả Component Tests (Backend)**

```bash
# Auth Service Tests
cd backend/auth-service/Nckh-Lu-n
mvn test -Dtest=UserServiceTest,JwtUtilTest

# Product Service Tests
cd ../../product-service/Tien/Tien
mvn test -Dtest=ProductServiceTest

# Integration Tests
cd ../../auth-service/Nckh-Lu-n
mvn test -Dtest=AuthIntegrationTest
```

### **Chạy tất cả Frontend Tests**

```bash
cd frontend/web-client
npm test validators.test.js
```

### **Chạy tất cả System Tests (POSTMAN)**

```bash
# CLI
newman run scripts/NCKH_E-commerce_Testing.postman_collection.json \
  -e scripts/NCKH_E-commerce_Testing.postman_environment.json

# Hoặc trong POSTMAN GUI
# Collection Runner → Run Collection
```

---

## 🎓 CHI TIẾT FILE TRONG BÁO CÁO

| Loại Test | File | Tests | Status | Cách Chạy |
|-----------|------|-------|--------|-----------|
| **Component** | UserServiceTest.java | 5 | ✅ Ready | `mvn test -Dtest=UserServiceTest` |
| **Component** | JwtUtilTest.java | 5 | ✅ Ready | `mvn test -Dtest=JwtUtilTest` |
| **Component** | ProductServiceTest.java | 5 | ✅ Ready | `mvn test -Dtest=ProductServiceTest` |
| **Component** | validators.test.js | 11 | ✅ Ready | `npm test validators.test.js` |
| **Integration** | AuthIntegrationTest.java | 5 | ✅ Ready | `mvn test -Dtest=AuthIntegrationTest` |
| **System/E2E** | POSTMAN Collection | 6 | ✅ 100% PASS | `newman run ...` hoặc POSTMAN GUI |

---

## 📌 LƯU Ý QUAN TRỌNG

1. **Cần chạy Auth Service trước**:
   ```bash
   cd backend/auth-service/Nckh-Lu-n
   mvn spring-boot:run
   ```

2. **Database**: 
   - Component Tests dùng mock/in-memory
   - Integration Tests dùng H2 (test DB)
   - System Tests dùng running service

3. **Thứ tự chạy**:
   - ✅ Component Tests (nhanh, độc lập)
   - ✅ Integration Tests (chậm hơn, xài DB)
   - ✅ System Tests (chạy cuối, xài service thực)

4. **Báo cáo**:
   - Screenshot kết quả chạy test
   - Log output từ terminal
   - Thêm vào slide presentation

---

## 🚀 QUICK START

```bash
# 1. Clone project
cd c:\UserX\Documents\GitHub\Nckh

# 2. Chạy Auth Service
cd backend/auth-service/Nckh-Lu-n
mvn spring-boot:run

# 3. Trong terminal khác - Chạy tests
# Component Tests
mvn test -Dtest=UserServiceTest
mvn test -Dtest=JwtUtilTest

# Integration Tests
mvn test -Dtest=AuthIntegrationTest

# Frontend Tests
cd ../../../frontend/web-client
npm test validators.test.js

# POSTMAN Tests
cd ../../../scripts
newman run NCKH_E-commerce_Testing.postman_collection.json \
  -e NCKH_E-commerce_Testing.postman_environment.json
```

---

**✅ Tất cả test files đã sẵn sàng để chạy!**
