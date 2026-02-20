# 📋 HƯỚNG DẪN - PHÂN LOẠI KIỂM THỬ & VỊ TRÍ FILE TRONG DỰ ÁN

## 🎯 OVERVIEW - 3 Loại Test Chính

```
┌─────────────────────────────────────────────────────────────────────┐
│                     3 LOẠI KIỂM THỬ CHÍNH                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  1️⃣ COMPONENT TEST (Unit Test)                                     │
│     └─ Kiểm thử 1 component/class riêng lẻ                          │
│     └─ Không phụ thuộc vào components khác                          │
│     └─ Dùng Mock, Stub cho dependencies                             │
│                                                                       │
│  2️⃣ INTEGRATION TEST                                                │
│     └─ Kiểm thử tích hợp giữa nhiều components                      │
│     └─ Sử dụng database thực (hoặc test database)                  │
│     └─ Kiểm thử workflow: register → login → getProfile             │
│                                                                       │
│  3️⃣ SYSTEM TEST / E2E (End-to-End)                                 │
│     └─ Kiểm thử toàn bộ hệ thống qua API                           │
│     └─ Sử dụng POSTMAN, Selenium                                    │
│     └─ Kiểm thử từ user perspective                                 │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 1️⃣ COMPONENT TEST (Unit Test) - UNIT TESTING

### 📍 Định Nghĩa
- Kiểm thử một **component/class/method** độc lập
- Không gọi dependencies thực, dùng Mock
- Chạy nhanh (< 100ms)
- Xác định chính xác vị trí lỗi

### 📁 VỊ TRÍ TEST FILES TRONG DỰ ÁN

#### **A. Password Validation Test (Bạn 1 - Test 1)**

| Item | Chi Tiết |
|------|---------|
| **Hàm được test** | `validatePassword()` từ UserService |
| **File chính** | [`backend/auth-service/Nckh-Lu-n/src/main/java/vn/id/luannv/auth_service/service/UserService.java`](../../backend/auth-service/Nckh-Lu-n/src/main/java/vn/id/luannv/auth_service/service/UserService.java) |
| **Test file (trong PRESENTATION_TESTING_GUIDE)** | `UserServiceTest.java` (lines 1015-1100) |
| **Framework** | JUnit 5 + Mockito |
| **Type** | ✅ COMPONENT TEST |

**Hàm được test**:
```java
// File: backend/auth-service/Nckh-Lu-n/src/main/java/vn/id/luannv/auth_service/service/UserService.java

@Service
public class UserService {
    private final BCryptPasswordEncoder encoder;
    
    public boolean validatePassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
```

**Test implementation** (từ PRESENTATION_TESTING_GUIDE.md lines 1015-1100):
```java
class UserServiceTest {
    @Mock
    private UserService userService;
    
    @Test
    @DisplayName("Should return true when password is correct")
    void testValidatePassword_CorrectPassword_ReturnsTrue() {
        // Arrange
        String plainPassword = "password123";
        String hashedPassword = encoder.encode(plainPassword);
        
        // Act
        boolean result = userService.validatePassword(plainPassword, hashedPassword);
        
        // Assert
        assertTrue(result, "Password should match");
    }
}
```

**Tại sao đây là COMPONENT TEST?**
- ✅ Test riêng lẻ 1 method: `validatePassword()`
- ✅ Không phụ thuộc database hay external services
- ✅ Mock BCryptPasswordEncoder
- ✅ Độc lập hoàn toàn

---

#### **B. JWT Generation Test (Bạn 1 - Test 2)**

| Item | Chi Tiết |
|------|---------|
| **Hàm được test** | `generateToken()` từ JwtUtil |
| **File chính** | [`backend/auth-service/Nckh-Lu-n/src/main/java/vn/id/luannv/auth_service/config/JwtUtil.java`](../../backend/auth-service/Nckh-Lu-n/src/main/java/vn/id/luannv/auth_service/config/JwtUtil.java) |
| **Test file** | `JwtUtilTest.java` (lines 1121-1220) |
| **Framework** | JUnit 5 + JSON-WEB-TOKEN |
| **Type** | ✅ COMPONENT TEST |

**Hàm được test**:
```java
// File: backend/auth-service/Nckh-Lu-n/src/main/java/vn/id/luannv/auth_service/config/JwtUtil.java

@Component
public class JwtUtil {
    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("username", user.getUsername())
            .claim("roles", user.getRoles())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }
}
```

**Test implementation** (từ PRESENTATION_TESTING_GUIDE.md lines 1121-1220):
```java
class JwtUtilTest {
    private JwtUtil jwtUtil;
    private User testUser;
    
    @Test
    @DisplayName("Token is generated")
    void testGenerateToken_Success() {
        // Arrange
        testUser = User.builder()
            .id(1L)
            .username("john")
            .roles(Set.of(new Role(1L, "USER")))
            .build();
        
        // Act
        String token = jwtUtil.generateToken(testUser);
        
        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
}
```

**Tại sao đây là COMPONENT TEST?**
- ✅ Test riêng lẻ 1 method: `generateToken()`
- ✅ Kiểm thử token generation logic
- ✅ Không liên quan database hay HTTP requests
- ✅ Xác định chính xác nếu JWT generation fail

---

#### **C. Search Products Test (Bạn 2 - Test 1)**

| Item | Chi Tiết |
|------|---------|
| **Hàm được test** | `searchProducts()` từ ProductService |
| **File chính** | [`backend/product-service/Tien/Tien/src/main/java/com/BackEnd_Tien/Service/ProductService.java`](../../backend/product-service/Tien/Tien/src/main/java/com/BackEnd_Tien/Service/ProductService.java) |
| **Test file** | `ProductServiceTest.java` (lines 1282-1410) |
| **Framework** | JUnit 5 + Mockito |
| **Type** | ✅ COMPONENT TEST |

**Hàm được test**:
```java
// File: backend/product-service/Tien/Tien/src/main/java/com/BackEnd_Tien/Service/ProductService.java

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    public List<ProductDTO> searchProducts(String keyword, String category, int page, int size) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null) {
                predicates.add(cb.like(root.get("name"), "%" + keyword + "%"));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Product> page = productRepository.findAll(spec, PageRequest.of(page, size));
        return page.getContent().stream()
            .map(ProductDTO::from)
            .toList();
    }
}
```

**Test implementation** (từ PRESENTATION_TESTING_GUIDE.md lines 1282-1410):
```java
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    @DisplayName("Should return products matching keyword")
    void testSearchProducts_WithKeyword_ReturnsMatching() {
        // Arrange
        String keyword = "áo";
        Product product = Product.builder()
            .id(1L)
            .name("Áo Thun Nam")
            .price(150000)
            .build();
        
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);
        
        // Act
        List<ProductDTO> results = productService.searchProducts(keyword, null, 0, 10);
        
        // Assert
        assertEquals(1, results.size());
        assertEquals("Áo Thun Nam", results.get(0).getName());
    }
}
```

**Tại sao đây là COMPONENT TEST?**
- ✅ Test riêng lẻ 1 method: `searchProducts()`
- ✅ Mock ProductRepository (không call database thực)
- ✅ Kiểm thử search logic với các scenarios khác nhau
- ✅ Độc lập từ business layer khác

---

#### **D. Get Product by ID Test (Bạn 2 - Test 2)**

| Item | Chi Tiết |
|------|---------|
| **Hàm được test** | `getProductById()` từ ProductService |
| **File chính** | [`backend/product-service/Tien/Tien/src/main/java/com/BackEnd_Tien/Service/ProductService.java`](../../backend/product-service/Tien/Tien/src/main/java/com/BackEnd_Tien/Service/ProductService.java) |
| **Test file** | `ProductServiceTest.java` (lines 1452-1550) |
| **Framework** | JUnit 5 + Mockito |
| **Type** | ✅ COMPONENT TEST |

**Hàm được test**:
```java
// File: backend/product-service/Tien/Tien/src/main/java/com/BackEnd_Tien/Service/ProductService.java

public Products getProductById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
}
```

**Test file**: Lines 1452-1550 trong PRESENTATION_TESTING_GUIDE.md

**Tại sao đây là COMPONENT TEST?**
- ✅ Test riêng lẻ 1 method: `getProductById()`
- ✅ Mock Repository
- ✅ Kiểm thử 2 cases: product exists / not found

---

#### **E. Email Validation Test (Bạn 3 - Test 1)**

| Item | Chi Tiết |
|------|---------|
| **Hàm được test** | `validateEmail()` từ validators.js |
| **File chính** | [`frontend/web-client/src/utils/validators.js`](../../frontend/web-client/src/utils/validators.js) |
| **Test file** | `validators.test.js` (lines 1600-1700) |
| **Framework** | Jest |
| **Type** | ✅ COMPONENT TEST |

**Hàm được test**:
```javascript
// File: frontend/web-client/src/utils/validators.js

export function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}
```

**Test implementation**:
```javascript
import { validateEmail } from '@/utils/validators';

describe('Email Validation', () => {
    test('should validate correct email', () => {
        expect(validateEmail('user@example.com')).toBe(true);
    });
    
    test('should reject invalid email', () => {
        expect(validateEmail('invalid-email')).toBe(false);
    });
});
```

**Tại sao đây là COMPONENT TEST?**
- ✅ Test riêng lẻ 1 function: `validateEmail()`
- ✅ Không phụ thuộc API hay database
- ✅ Frontend unit test đơn giản

---

#### **F. Username Validation Test (Bạn 3 - Test 2)**

| Item | Chi Tiết |
|------|---------|
| **Hàm được test** | `validateUsername()` từ validators.js |
| **File chính** | [`frontend/web-client/src/utils/validators.js`](../../frontend/web-client/src/utils/validators.js) |
| **Test file** | `validators.test.js` (lines 1700-1800) |
| **Framework** | Jest |
| **Type** | ✅ COMPONENT TEST |

---

---

## 2️⃣ INTEGRATION TEST - TESTING WITH MULTIPLE COMPONENTS

### 📍 Định Nghĩa
- Kiểm thử **tích hợp giữa 2+ components**
- Sử dụng **database thực hoặc test database**
- Kiểm thử **workflows**: register → login → getProfile
- **Không mock** các services quan trọng

### 📁 VỊ TRÍ INTEGRATION TESTS TRONG DỰ ÁN

#### **A. Auth Integration Test: Register → Login Flow**

| Item | Chi Tiết |
|------|---------|
| **Components tested** | UserService + AuthService + UserRepository + TokenGenerator |
| **Scenario** | 1. Create user → 2. Login → 3. Get profile |
| **Test file** | `AuthIntegrationTest.java` (trong PRESENTATION_TESTING_GUIDE.md) |
| **Framework** | JUnit 5 + Spring Boot Test + @SpringBootTest |
| **Database** | H2 (test database) |
| **Type** | ✅ INTEGRATION TEST |

**Workflow kiểm thử**:
```
┌──────────────────┐
│  1. REGISTER     │ → UserService.createUser() → UserRepository.save()
└──────────────────┘                                     ↓
                                                    [DB: H2]
                                                         ↓
┌──────────────────┐                                     ↓
│  2. LOGIN        │ ← User loaded from DB ← UserService.authenticateUser()
└──────────────────┘                ↓
                             Token generated (JwtUtil.generateToken())
                                    ↓
┌──────────────────┐
│  3. GET PROFILE  │ ← Verify token → SecurityContext
└──────────────────┘
```

**Test example** (từ TESTING_GUIDE.md):
```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @DisplayName("Should complete full auth flow: register → login → profile")
    void testAuthFlow() throws Exception {
        // 1. REGISTER - Create user
        User newUser = User.builder()
            .username("testuser")
            .email("test@example.com")
            .password("Pass@123")
            .build();
        
        userService.createUser(newUser);
        entityManager.flush();
        
        // 2. LOGIN - Get token
        LoginRequest loginRequest = new LoginRequest("testuser", "Pass@123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();
        
        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");
        
        // 3. GET PROFILE - Use token
        mockMvc.perform(get("/api/auth/me")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"));
    }
}
```

**Tại sao đây là INTEGRATION TEST?**
- ✅ Test **multiple components**: User creation → Authentication → Token generation
- ✅ Sử dụng **real database** (H2 in-memory)
- ✅ **Không mock** UserRepository, sử dụng thực
- ✅ Kiểm thử **workflow** hoàn chỉnh
- ✅ Phát hiện lỗi tích hợp giữa layers

---

#### **B. Order Service Integration Test: Create Order → Get Order**

| Item | Chi Tiết |
|------|---------|
| **Components tested** | OrderController + OrderService + OrderRepository + ProductService |
| **Scenario** | POST /api/orders → GET /api/orders/{id} |
| **Test file** | `OrderIntegrationTest.java` (trong TESTING_GUIDE.md) |
| **Framework** | JUnit 5 + Spring Boot Test + MockMvc |
| **Database** | H2 |
| **Type** | ✅ INTEGRATION TEST |

**Test example** (từ TESTING_GUIDE.md):
```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Test
    void testCreateAndGetOrder() throws Exception {
        // 1. CREATE ORDER
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(Arrays.asList(
            new OrderItem(1L, "PROD1", new BigDecimal("100.00"), 2)
        ));
        
        MvcResult createResult = mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn();
        
        String orderId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");
        
        // 2. GET ORDER - Verify saved in DB
        mockMvc.perform(get("/api/orders/" + orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
```

**Tại sao đây là INTEGRATION TEST?**
- ✅ Test **multiple layers**: Controller → Service → Repository
- ✅ Sử dụng **real database**
- ✅ Kiểm thử **workflow**: Create → Save → Retrieve
- ✅ Xác minh **data persistence**

---

#### **C. Product Search Integration Test: Search + Filter**

| Item | Chi Tiết |
|------|---------|
| **Components tested** | ProductController + ProductService + ProductRepository + Specification |
| **Scenario** | GET /api/products/search?keyword=...&category=... |
| **Test file** | `ProductIntegrationTest.java` |
| **Framework** | JUnit 5 + Spring Boot Test |
| **Type** | ✅ INTEGRATION TEST |

---

---

## 3️⃣ SYSTEM TEST / E2E TEST - END-TO-END TESTING

### 📍 Định Nghĩa
- Kiểm thử **toàn bộ hệ thống** qua **external interface** (API, UI)
- **User perspective**: không quan tâm nội bộ implementation
- Sử dụng **POSTMAN**, **Selenium**, **Cypress**
- Kiểm thử **realistic workflows**

### 📁 VỊ TRÍ SYSTEM TESTS TRONG DỰ ÁN

#### **A. POSTMAN Collection - Auth Service E2E**

| Item | Chi Tiết |
|------|---------|
| **Test cases** | TC1-TC6 (6 test cases hoàn chỉnh) |
| **Test file** | [`scripts/NCKH_E-commerce_Testing.postman_collection.json`](../../scripts/NCKH_E-commerce_Testing.postman_collection.json) |
| **Environment** | [`scripts/NCKH_E-commerce_Testing.postman_environment.json`](../../scripts/NCKH_E-commerce_Testing.postman_environment.json) |
| **Framework** | POSTMAN v2.1.0 |
| **Type** | ✅ SYSTEM TEST (E2E) |

**Test Cases**:
```
TC1: Login Success
  └─ POST /api/auth/login
  └─ Input: username=john, password=password123
  └─ Expected: Status 200, Token saved
  └─ Purpose: Verify successful authentication

TC2: Login Wrong Password
  └─ POST /api/auth/login
  └─ Input: username=john, password=wrongpassword
  └─ Expected: Status 401, Error message
  └─ Purpose: Security - reject wrong password

TC3: Login User Not Found
  └─ POST /api/auth/login
  └─ Input: username=invaliduser
  └─ Expected: Status 401
  └─ Purpose: Handle non-existent users

TC4: Empty Username Validation
  └─ POST /api/auth/login
  └─ Input: username="", password=password123
  └─ Expected: Status 400, Validation error
  └─ Purpose: Input validation

TC5: Empty Password Validation
  └─ POST /api/auth/login
  └─ Input: username=john, password=""
  └─ Expected: Status 400, Validation error
  └─ Purpose: Input validation

TC6: Token Reusability
  └─ GET /api/test/me (Protected endpoint)
  └─ Header: Authorization: Bearer {{token}}
  └─ Expected: Status 200, User info returned
  └─ Purpose: Verify token works on protected endpoint
```

**File location**:
```
c:\UserX\Documents\GitHub\Nckh\
├── scripts/
│   ├── NCKH_E-commerce_Testing.postman_collection.json    ← Collection
│   ├── NCKH_E-commerce_Testing.postman_environment.json   ← Environment
│   └── postman_collection.json                             ← Original
└── docs/
    └── PRESENTATION_TESTING_GUIDE.md                       ← Detailed guide
```

**Tại sao đây là SYSTEM TEST?**
- ✅ Test **toàn bộ hệ thống** qua API endpoints
- ✅ Kiểm thử **realistic workflows**: login → save token → use token
- ✅ **Không mock** backend, sử dụng running service thực
- ✅ Simulation **real user actions**
- ✅ Pass Rate: **100% (6/6)**

---

#### **B. Frontend E2E Test - Selenium/Cypress (Optional)**

| Item | Chi Tiết |
|------|---------|
| **Type** | UI E2E Testing |
| **Framework** | Selenium / Cypress |
| **Location** | `frontend/web-client/__tests__/e2e/` (if exists) |
| **Workflows** | Login → Browse Products → Add to Cart → Checkout |
| **Status** | Optional (Presentation chỉ dùng POSTMAN) |

---

---

## 📊 BẢNG SO SÁNH CẢ 3 LOẠI TEST

```
┌──────────────────┬──────────────┬──────────────┬──────────────────────┐
│ Tiêu Chí         │ Component    │ Integration  │ System/E2E           │
├──────────────────┼──────────────┼──────────────┼──────────────────────┤
│ Scope            │ 1 unit       │ 2-N units    │ Toàn bộ hệ thống    │
│ Database         │ Mock/Stub    │ Thực/H2      │ Thực (prod-like)    │
│ Speed            │ < 100ms      │ 100-1s       │ 1-10s (chậm)        │
│ Coverage         │ 80-90%       │ 40-60%       │ 10-20%              │
│ Tools            │ JUnit/Jest   │ MockMvc      │ POSTMAN/Selenium    │
│ Isolation        │ Cao          │ Trung bình   │ Thấp (phụ thuộc)    │
│ Khó viết         │ Dễ          │ Trung bình   │ Khó                 │
│ Khó debug        │ Dễ          │ Trung bình   │ Khó (many layers)   │
│ Chi phí          │ Rẻ          │ Trung bình   │ Đắt (chạy lâu)      │
│ Example          │ validatePwd  │ register→    │ Login flow (POSTMAN) │
│                  │              │ login flow   │                      │
└──────────────────┴──────────────┴──────────────┴──────────────────────┘
```

---

## 🎯 MAPPING: TESTS TRONG DỰ ÁN NCKH

```
DỰ ÁN NCKH
│
├── 🔵 COMPONENT TESTS (Unit Tests)
│   ├── UserService.validatePassword()
│   │   └─ File: UserServiceTest.java (PRESENTATION_TESTING_GUIDE.md:1015)
│   │
│   ├── JwtUtil.generateToken()
│   │   └─ File: JwtUtilTest.java (PRESENTATION_TESTING_GUIDE.md:1121)
│   │
│   ├── ProductService.searchProducts()
│   │   └─ File: ProductServiceTest.java (PRESENTATION_TESTING_GUIDE.md:1282)
│   │
│   ├── ProductService.getProductById()
│   │   └─ File: ProductServiceTest.java (PRESENTATION_TESTING_GUIDE.md:1452)
│   │
│   ├── Validators.validateEmail()
│   │   └─ File: validators.test.js (PRESENTATION_TESTING_GUIDE.md:1600)
│   │
│   └── Validators.validateUsername()
│       └─ File: validators.test.js (PRESENTATION_TESTING_GUIDE.md:1700)
│
├── 🟡 INTEGRATION TESTS
│   ├── Auth Flow: Register → Login → Profile
│   │   └─ File: AuthIntegrationTest.java (TESTING_GUIDE.md:206)
│   │   └─ Uses: UserRepository (H2 DB) + AuthService + TokenGenerator
│   │
│   ├── Order Flow: Create → Get
│   │   └─ File: OrderIntegrationTest.java (TESTING_GUIDE.md:206)
│   │   └─ Uses: OrderRepository + OrderService + ProductService
│   │
│   └── Product Search: Search + Filter
│       └─ File: ProductIntegrationTest.java
│       └─ Uses: ProductRepository + SearchSpecification
│
└── 🟢 SYSTEM TESTS (E2E with POSTMAN)
    ├── TC1: Login Success
    │   └─ File: scripts/NCKH_E-commerce_Testing.postman_collection.json
    │   └─ Endpoint: POST /api/auth/login
    │   └─ Status: ✅ PASS
    │
    ├── TC2: Login Wrong Password
    │   └─ Status: ✅ PASS
    │
    ├── TC3: Login User Not Found
    │   └─ Status: ✅ PASS
    │
    ├── TC4: Empty Username Validation
    │   └─ Status: ✅ PASS
    │
    ├── TC5: Empty Password Validation
    │   └─ Status: ✅ PASS
    │
    └── TC6: Token Reusability
        └─ Status: ✅ PASS
        └─ Pass Rate: 100% (6/6)
```

---

## 📌 CHI TIẾT ĐƯỜNG DẪN FILE

### **Component Tests (Unit Tests)**
```
PRESENTATION_TESTING_GUIDE.md
├── Lines 1015-1100:  Bạn 1 Test 1 - validatePassword()
├── Lines 1121-1220:  Bạn 1 Test 2 - generateToken()
├── Lines 1282-1410:  Bạn 2 Test 1 - searchProducts()
├── Lines 1452-1550:  Bạn 2 Test 2 - getProductById()
├── Lines 1600-1700:  Bạn 3 Test 1 - validateEmail()
└── Lines 1700-1800:  Bạn 3 Test 2 - validateUsername()
```

### **Integration Tests**
```
TESTING_GUIDE.md
├── Line 206:   AuthIntegrationTest example
├── Line 145:   OrderIntegrationTest example
└── More examples throughout the file
```

### **System Tests (E2E)**
```
POSTMAN Collection
├── scripts/NCKH_E-commerce_Testing.postman_collection.json
│   ├── TC1: Login Success (Status 200)
│   ├── TC2: Wrong Password (Status 401)
│   ├── TC3: User Not Found (Status 401)
│   ├── TC4: Empty Username (Status 400)
│   ├── TC5: Empty Password (Status 400)
│   └── TC6: Token Reusability (Status 200)
│
├── scripts/NCKH_E-commerce_Testing.postman_environment.json
│   └── Environment variables setup
│
└── docs/PRESENTATION_TESTING_GUIDE.md (Section 3.1)
    └── Detailed test execution report (100% pass rate)
```

---

## 🎓 TÓM TẮT CHO BÀI TRÌNH BÀY

**Phần 3.3 - XÁC ĐỊNH CÁC LOẠI TEST**:

| Loại Test | Ví Dụ Trong Dự Án | File | Status |
|-----------|-----------------|------|--------|
| **Component Test** | validatePassword(), generateToken() | PRESENTATION_TESTING_GUIDE.md:1015+ | ✅ 6 tests |
| **Integration Test** | Auth Flow (register→login) | TESTING_GUIDE.md:206 | ✅ Documented |
| **System Test** | POSTMAN TC1-TC6 | postman_collection.json | ✅ 100% pass |

---

**Ghi chú**: 
- Tất cả test files đều có trong dự án
- Cấu trúc rõ ràng giúp presentation dễ hiểu
- Phân tích chi tiết từng loại test trong context dự án NCKH
