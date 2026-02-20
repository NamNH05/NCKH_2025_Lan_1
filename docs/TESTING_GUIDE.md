# Hướng Dẫn Kiểm Thử - Testing Guide

## Mục Lục
1. [Tổng Quan](#tổng-quan)
2. [Unit Test](#unit-test)
3. [Component Test](#component-test)
4. [Integration Test](#integration-test)
5. [System Test](#system-test)
6. [Automated Testing (CI/CD)](#automated-testing-cicd)
7. [Best Practices](#best-practices)
8. [Tools & Technologies](#tools--technologies)

---

## Tổng Quan

### Chiến Lược Kiểm Thử Toàn Bộ

Dự án sử dụng **Test Pyramid** - Kiến trúc kiểm thử gồm 3 tầng:

```
        /\
       /  \      System & E2E Tests (Ít)
      /----\
     /      \    Integration Tests (Vừa)
    /--------\
   /          \  Unit Tests (Nhiều)
  /____________\
```

### Quy Tắc Chung
- **Unit Tests**: 70% - Nhanh, chi phí thấp, độ che phủ cao
- **Integration Tests**: 20% - Kiểm tra tích hợp giữa các thành phần
- **System/E2E Tests**: 10% - Kiểm tra toàn bộ hệ thống

### Mục Tiêu
- Đạt >= 80% code coverage cho unit tests
- Đạt >= 60% code coverage cho integration tests
- Tất cả critical features phải có E2E tests
- Tất cả tests phải chạy được tự động trong CI/CD pipeline

---

## Unit Test

### Định Nghĩa
Unit test là các test kiểm tra một đơn vị mã nhỏ nhất (function, method, class) độc lập, không phụ thuộc vào các thành phần khác.

### Đặc Điểm
- ✅ **Nhanh**: Chạy trong vài milliseconds
- ✅ **Độc lập**: Không phụ thuộc vào database, external APIs
- ✅ **Xác định**: Luôn cho kết quả như nhau
- ✅ **Dễ tìm lỗi**: Lỗi được xác định rõ vị trí

### Phạm Vi
- Service methods (Business Logic)
- Utility functions
- Helper methods
- Validation logic
- Data transformations

### Công Cụ & Dependencies

#### Backend (Java)
```xml
<!-- JUnit 5 (Jupiter) -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.9.3</version>
    <scope>test</scope>
</dependency>

<!-- Mockito - Mocking framework -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.3.1</version>
    <scope>test</scope>
</dependency>

<!-- Spring Boot Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- AssertJ - Fluent assertions -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.24.1</version>
    <scope>test</scope>
</dependency>
```

#### Frontend (JavaScript/Node.js)
```json
{
  "devDependencies": {
    "vitest": "^1.0.0",
    "chai": "^4.3.0",
    "@testing-library/vue": "^8.0.0",
    "sinon": "^17.0.0"
  }
}
```

### Cấu Trúc Thư Mục

#### Backend
```
src/
├── main/java/
│   └── com/nckh/
│       └── service/
│           ├── OrderService.java
│           └── ...
└── test/java/
    └── com/nckh/
        └── service/
            ├── OrderServiceTest.java
            └── ...
```

#### Frontend
```
src/
├── components/
│   ├── Button.vue
│   └── ...
└── __tests__/
    ├── components/
    │   ├── Button.spec.js
    │   └── ...
    └── utils/
        └── ...
```

### Ví Dụ Unit Test

#### Backend (Java - Spring)

**Service Class:**
```java
// src/main/java/com/nckh/service/OrderService.java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public Order createOrder(CreateOrderRequest request) {
        if (request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        Order order = new Order();
        order.setItems(request.getItems());
        order.setTotalPrice(calculateTotal(request.getItems()));
        order.setStatus("PENDING");
        
        return orderRepository.save(order);
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

**Unit Test:**
```java
// src/test/java/com/nckh/service/OrderServiceTest.java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private ProductService productService;
    
    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Should create order successfully when valid request is provided")
    void testCreateOrderSuccess() {
        // Arrange - Chuẩn bị dữ liệu
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(Arrays.asList(
            new OrderItem(1L, "PRODUCT1", new BigDecimal("100.00"), 2),
            new OrderItem(2L, "PRODUCT2", new BigDecimal("50.00"), 1)
        ));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setTotalPrice(new BigDecimal("250.00"));
        savedOrder.setStatus("PENDING");
        
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act - Thực hiện hành động
        Order result = orderService.createOrder(request);

        // Assert - Kiểm tra kết quả
        assertThat(result)
            .isNotNull()
            .hasFieldOrPropertyWithValue("status", "PENDING");
        
        assertThat(result.getTotalPrice())
            .isEqualByComparingTo(new BigDecimal("250.00"));
        
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when order items are empty")
    void testCreateOrderWithEmptyItems() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Order must have at least one item");
        
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should calculate total price correctly")
    void testCalculateTotalPrice() {
        // Arrange
        List<OrderItem> items = Arrays.asList(
            new OrderItem(1L, "PRODUCT1", new BigDecimal("100.00"), 2),
            new OrderItem(2L, "PRODUCT2", new BigDecimal("50.00"), 1)
        );

        // Act
        BigDecimal total = orderService.calculateTotal(items);

        // Assert
        assertThat(total).isEqualByComparingTo(new BigDecimal("250.00"));
    }
}
```

#### Frontend (JavaScript - Vue.js)

**Component:**
```javascript
// src/components/Button.vue
<template>
  <button 
    :class="['btn', `btn-${variant}`]"
    :disabled="disabled"
    @click="handleClick"
  >
    {{ label }}
  </button>
</template>

<script setup>
defineProps({
  label: String,
  variant: {
    type: String,
    default: 'primary'
  },
  disabled: Boolean
});

const emit = defineEmits(['click']);

const handleClick = () => {
  if (!disabled) {
    emit('click');
  }
};
</script>
```

**Unit Test:**
```javascript
// src/__tests__/components/Button.spec.js
import { describe, it, expect, vi } from 'vitest';
import { mount } from '@vue/test-utils';
import Button from '@/components/Button.vue';

describe('Button Component', () => {
  it('should render with correct label', () => {
    const wrapper = mount(Button, {
      props: {
        label: 'Click Me'
      }
    });

    expect(wrapper.text()).toContain('Click Me');
  });

  it('should emit click event when clicked', async () => {
    const wrapper = mount(Button, {
      props: {
        label: 'Submit'
      }
    });

    await wrapper.find('button').trigger('click');

    expect(wrapper.emitted('click')).toBeTruthy();
    expect(wrapper.emitted('click')).toHaveLength(1);
  });

  it('should not emit click event when disabled', async () => {
    const wrapper = mount(Button, {
      props: {
        label: 'Disabled',
        disabled: true
      }
    });

    await wrapper.find('button').trigger('click');

    expect(wrapper.emitted('click')).toBeFalsy();
  });

  it('should apply correct variant class', () => {
    const wrapper = mount(Button, {
      props: {
        label: 'Test',
        variant: 'danger'
      }
    });

    expect(wrapper.find('button').classes()).toContain('btn-danger');
  });
});
```

### Chạy Unit Tests

#### Backend
```bash
# Chạy tất cả unit tests
mvn test

# Chạy test cho service cụ thể
mvn test -Dtest=OrderServiceTest

# Chạy test với coverage
mvn test jacoco:report

# Xem coverage report
# Tại: target/site/jacoco/index.html
```

#### Frontend
```bash
# Chạy tất cả tests
npm run test

# Chạy test với watch mode
npm run test:watch

# Chạy test với coverage
npm run test:coverage
```

---

## Component Test

### Định Nghĩa
Component test kiểm tra một thành phần độc lập (Controller, Component, View) cùng với dependencies của nó, nhưng vẫn tách biệt với các phần khác của hệ thống.

### Đặc Điểm
- 🔍 **Kiểm tra logic thành phần**: State, props, events
- 🔌 **Mock external dependencies**: Gọi tới services khác, HTTP requests
- 🎯 **Tập trung vào behavior**: Cách thành phần hoạt động
- ⚡ **Nhanh hơn integration tests**: Không cần database thực

### Phạm Vi
- REST Controllers (Validation, Response formatting)
- Vue/React Components (Props validation, Events, State)
- Modal Dialogs
- Form Components
- Menu/Navigation Components

### Ví Dụ Component Test

#### Backend (Spring Controller)

**Controller:**
```java
// src/main/java/com/nckh/controller/OrderController.java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        OrderDTO dto = OrderDTO.from(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return ResponseEntity.ok(OrderDTO.from(order));
    }
}
```

**Component Test:**
```java
// src/test/java/com/nckh/controller/OrderControllerTest.java
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OrderService orderService;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should create order and return 201 status")
    void testCreateOrderReturns201() throws Exception {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(Arrays.asList(
            new OrderItem(1L, "PRODUCT1", new BigDecimal("100.00"), 2)
        ));

        Order order = new Order();
        order.setId(1L);
        order.setTotalPrice(new BigDecimal("200.00"));
        order.setStatus("PENDING");

        when(orderService.createOrder(any())).thenReturn(order);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.totalPrice").value(200.00));

        verify(orderService, times(1)).createOrder(any());
    }

    @Test
    @DisplayName("Should return 404 when order not found")
    void testGetOrderReturns404() throws Exception {
        // Arrange
        when(orderService.getOrderById(999L))
            .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/orders/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 for invalid request")
    void testCreateOrderWithInvalidRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
```

#### Frontend (Vue Component)

**Form Component:**
```vue
<!-- src/components/OrderForm.vue -->
<template>
  <form @submit.prevent="handleSubmit">
    <div class="form-group">
      <label>Số lượng</label>
      <input 
        v-model.number="form.quantity" 
        type="number" 
        min="1"
        required
      />
      <span v-if="errors.quantity" class="error">{{ errors.quantity }}</span>
    </div>
    
    <button type="submit" :disabled="isSubmitting">
      {{ isSubmitting ? 'Đang xử lý...' : 'Tạo đơn hàng' }}
    </button>
  </form>
</template>

<script setup>
import { ref, reactive } from 'vue';

const emit = defineEmits(['submit']);
const props = defineProps({
  onSubmit: Function
});

const form = reactive({ quantity: 1 });
const errors = reactive({});
const isSubmitting = ref(false);

const handleSubmit = async () => {
  errors.quantity = '';
  
  if (form.quantity < 1) {
    errors.quantity = 'Số lượng phải >= 1';
    return;
  }

  isSubmitting.value = true;
  try {
    await props.onSubmit?.(form);
    emit('submit', form);
  } finally {
    isSubmitting.value = false;
  }
};
</script>
```

**Component Test:**
```javascript
// src/__tests__/components/OrderForm.spec.js
import { describe, it, expect, vi } from 'vitest';
import { mount, flushPromises } from '@vue/test-utils';
import OrderForm from '@/components/OrderForm.vue';

describe('OrderForm Component', () => {
  it('should validate quantity is at least 1', async () => {
    const wrapper = mount(OrderForm, {
      props: {
        onSubmit: vi.fn()
      }
    });

    const input = wrapper.find('input[type="number"]');
    await input.setValue(0);
    await wrapper.find('form').trigger('submit');

    expect(wrapper.find('.error').exists()).toBe(true);
    expect(wrapper.find('.error').text()).toContain('Số lượng phải >= 1');
  });

  it('should emit submit event with form data', async () => {
    const mockSubmit = vi.fn();
    const wrapper = mount(OrderForm, {
      props: {
        onSubmit: mockSubmit
      }
    });

    const input = wrapper.find('input[type="number"]');
    await input.setValue(5);
    await wrapper.find('form').trigger('submit');
    await flushPromises();

    expect(mockSubmit).toHaveBeenCalledWith({ quantity: 5 });
    expect(wrapper.emitted('submit')).toBeTruthy();
  });

  it('should disable button while submitting', async () => {
    const mockSubmit = vi.fn(() => new Promise(resolve => setTimeout(resolve, 100)));
    const wrapper = mount(OrderForm, {
      props: {
        onSubmit: mockSubmit
      }
    });

    await wrapper.find('form').trigger('submit');
    
    expect(wrapper.find('button').attributes('disabled')).toBeDefined();
  });
});
```

### Chạy Component Tests

```bash
# Backend
mvn test -Dtest=*ControllerTest

# Frontend
npm run test -- components/
```

---

## Integration Test

### Định Nghĩa
Integration test kiểm tra tích hợp giữa các thành phần khác nhau: Services, Repositories, Database, External APIs.

### Đặc Điểm
- 🔗 **Kiểm tra tích hợp thực**: Kết nối database, caches, queues
- 🗄️ **Sử dụng test database**: Dữ liệu thực hoặc mô phỏng
- 📊 **Kiểm tra workflows**: Quy trình hoàn chỉnh từ request đến database
- ⏱️ **Chậm hơn unit tests**: Nhưng nhanh hơn E2E tests

### Phạm Vi
- Service layers tương tác với Repository/Database
- Multi-service workflows
- API requests qua HTTP với mock/real servers
- Transaction management
- Cache integration
- Message queue processing

### Ví Dụ Integration Test

#### Backend (Test với Test Database)

**Configuration:**
```java
// src/test/java/com/nckh/config/TestDatabaseConfig.java
@TestConfiguration
public class TestDatabaseConfig {
    
    @Bean
    public DataSource testDataSource() {
        return DataSourceBuilder.create()
            .driverClassName("org.h2.Driver")
            .url("jdbc:h2:mem:testdb")
            .username("sa")
            .password("")
            .build();
    }
}
```

**Integration Test:**
```java
// src/test/java/com/nckh/service/OrderServiceIntegrationTest.java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceIntegrationTest {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Chuẩn bị dữ liệu test
        Product product1 = new Product();
        product1.setName("PRODUCT1");
        product1.setPrice(new BigDecimal("100.00"));
        product1.setStock(100);
        productRepository.save(product1);
    }

    @Test
    @DisplayName("Should create order and persist to database")
    void testCreateOrderWithDatabase() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(Arrays.asList(
            new OrderItem(1L, "PRODUCT1", new BigDecimal("100.00"), 2)
        ));

        // Act
        Order savedOrder = orderService.createOrder(request);
        entityManager.flush();
        entityManager.clear();

        // Assert - Kiểm tra từ database
        Order retrievedOrder = orderRepository.findById(savedOrder.getId())
            .orElseThrow();
        
        assertThat(retrievedOrder)
            .isNotNull()
            .hasFieldOrPropertyWithValue("status", "PENDING");
        
        assertThat(retrievedOrder.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Should handle concurrent order creation")
    void testConcurrentOrderCreation() throws InterruptedException {
        // Arrange
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // Act
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    CreateOrderRequest request = new CreateOrderRequest();
                    request.setItems(Arrays.asList(
                        new OrderItem(1L, "PRODUCT1", new BigDecimal("100.00"), 1)
                    ));
                    orderService.createOrder(request);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Assert
        assertThat(orderRepository.count()).isEqualTo(threadCount);
    }

    @Test
    @DisplayName("Should rollback on exception")
    void testTransactionRollback() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThat(orderRepository.count()).isZero();
    }
}
```

**Test Containers (Sử dụng Docker):**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mssqlserver</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>
```

```java
// src/test/java/com/nckh/service/OrderServiceMSQLTest.java
@SpringBootTest
@Testcontainers
class OrderServiceMSQLTest {
    
    @Container
    static MSSQLServerContainer<?> mssqlContainer = 
        new MSSQLServerContainer<>()
            .withPassword("YourStrongPassword123!")
            .withDatabaseName("testdb");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mssqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mssqlContainer::getUsername);
        registry.add("spring.datasource.password", mssqlContainer::getPassword);
    }

    @Autowired
    private OrderService orderService;

    @Test
    void testWithRealMSSQL() {
        // Test với SQL Server thực
        CreateOrderRequest request = new CreateOrderRequest();
        // ... test logic
    }
}
```

#### Frontend (API Integration Test)

**API Service:**
```javascript
// src/services/orderApi.js
export const orderApi = {
  async createOrder(orderData) {
    const response = await fetch('/api/orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(orderData)
    });
    
    if (!response.ok) throw new Error('Failed to create order');
    return response.json();
  },

  async getOrder(id) {
    const response = await fetch(`/api/orders/${id}`);
    if (!response.ok) throw new Error('Order not found');
    return response.json();
  }
};
```

**Integration Test:**
```javascript
// src/__tests__/services/orderApi.spec.js
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { orderApi } from '@/services/orderApi';

describe('Order API Integration', () => {
  let fetchMock;

  beforeEach(() => {
    fetchMock = vi.fn();
    global.fetch = fetchMock;
  });

  it('should create order via API', async () => {
    // Arrange
    const mockResponse = {
      ok: true,
      json: async () => ({ id: 1, status: 'PENDING' })
    };
    fetchMock.mockResolvedValueOnce(mockResponse);

    const orderData = { quantity: 2, items: [] };

    // Act
    const result = await orderApi.createOrder(orderData);

    // Assert
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/orders',
      expect.objectContaining({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
      })
    );
    expect(result).toEqual({ id: 1, status: 'PENDING' });
  });

  it('should handle API errors', async () => {
    // Arrange
    const mockResponse = {
      ok: false,
      status: 400,
      json: async () => ({ error: 'Bad request' })
    };
    fetchMock.mockResolvedValueOnce(mockResponse);

    // Act & Assert
    await expect(orderApi.createOrder({}))
      .rejects
      .toThrow('Failed to create order');
  });
});
```

### Chạy Integration Tests

```bash
# Backend - Chỉ integration tests
mvn test -Dgroups="integration"

# Hoặc chạy với naming convention
mvn test -Dtest=*IntegrationTest

# Frontend
npm run test -- services/
```

---

## System Test

### Định Nghĩa
System test (End-to-End test) kiểm tra toàn bộ hệ thống từ UI đến backend, bao gồm tất cả các thành phần tích hợp với nhau.

### Đặc Điểm
- 🌍 **Kiểm tra từ người dùng**: Tương tác qua UI/API như user thực
- 🔄 **Kiểm tra workflow hoàn chỉnh**: Từ đầu đến cuối
- 🔌 **Tất cả services chạy**: Không mock, tất cả real
- ⏱️ **Chậm nhất**: Chạy ít nhất nhưng coverage cao nhất

### Phạm Vi
- User workflows (Login → Browse → Order → Checkout)
- Cross-service interactions
- UI testing (Button clicks, Form submissions)
- Data flow từ UI → Backend → Database
- Error scenarios từ end-to-end

### Công Cụ

#### Backend E2E
```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>
```

#### Frontend E2E
```json
{
  "devDependencies": {
    "playwright": "^1.40.0",
    "cypress": "^13.0.0"
  }
}
```

### Ví Dụ System Test

#### Backend (REST API E2E Test)

**Scenario-based E2E Test:**
```java
// src/test/java/com/nckh/e2e/OrderFlowE2ETest.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderFlowE2ETest {
    
    @LocalServerPort
    private int port;
    
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("Complete order workflow: Create → Retrieve → Update → Cancel")
    void testCompleteOrderWorkflow() {
        // Step 1: Tạo đơn hàng
        CreateOrderRequest createRequest = new CreateOrderRequest();
        createRequest.setItems(Arrays.asList(
            new OrderItem(1L, "PRODUCT1", new BigDecimal("100.00"), 2)
        ));

        Order createdOrder = given()
            .baseUri(baseUrl)
            .contentType(ContentType.JSON)
            .body(createRequest)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(201)
            .extract()
            .as(Order.class);

        Long orderId = createdOrder.getId();
        assertThat(orderId).isNotNull();

        // Step 2: Lấy chi tiết đơn hàng
        Order retrievedOrder = given()
            .baseUri(baseUrl)
            .when()
            .get("/api/orders/{id}", orderId)
            .then()
            .statusCode(200)
            .extract()
            .as(Order.class);

        assertThat(retrievedOrder.getStatus()).isEqualTo("PENDING");
        assertThat(retrievedOrder.getTotalPrice()).isEqualByComparingTo(new BigDecimal("200.00"));

        // Step 3: Cập nhật trạng thái đơn hàng
        given()
            .baseUri(baseUrl)
            .contentType(ContentType.JSON)
            .body(new UpdateOrderStatusRequest("CONFIRMED"))
            .when()
            .put("/api/orders/{id}/status", orderId)
            .then()
            .statusCode(200);

        // Step 4: Xác minh cập nhật
        Order updatedOrder = given()
            .baseUri(baseUrl)
            .when()
            .get("/api/orders/{id}", orderId)
            .then()
            .statusCode(200)
            .extract()
            .as(Order.class);

        assertThat(updatedOrder.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("Should handle order cancellation with refund")
    void testOrderCancellation() {
        // Tạo đơn hàng
        Order order = createTestOrder();

        // Hủy đơn hàng
        given()
            .baseUri(baseUrl)
            .when()
            .delete("/api/orders/{id}", order.getId())
            .then()
            .statusCode(200);

        // Xác minh status là CANCELLED
        Order cancelledOrder = given()
            .baseUri(baseUrl)
            .when()
            .get("/api/orders/{id}", order.getId())
            .then()
            .statusCode(200)
            .extract()
            .as(Order.class);

        assertThat(cancelledOrder.getStatus()).isEqualTo("CANCELLED");
    }

    private Order createTestOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(Arrays.asList(
            new OrderItem(1L, "PRODUCT1", new BigDecimal("100.00"), 1)
        ));

        return given()
            .baseUri(baseUrl)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(201)
            .extract()
            .as(Order.class);
    }
}
```

#### Frontend (Playwright E2E Test)

**Installation:**
```bash
npm install --save-dev @playwright/test
npx playwright install
```

**E2E Test:**
```javascript
// tests/e2e/order.spec.js
import { test, expect } from '@playwright/test';

test.describe('Order Flow E2E', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:5173'); // Vite dev server
  });

  test('Complete order flow: Browse → Add to cart → Checkout', async ({ page }) => {
    // Step 1: Đi tới trang sản phẩm
    await page.click('text=Products');
    await page.waitForLoadState('networkidle');

    // Step 2: Xem danh sách sản phẩm
    const productItems = await page.locator('[data-testid="product-item"]').count();
    expect(productItems).toBeGreaterThan(0);

    // Step 3: Thêm sản phẩm vào giỏ
    await page.click('[data-testid="product-item"] button:has-text("Add to Cart")');
    
    // Xác minh thông báo
    await expect(page.locator('.toast')).toContainText('Added to cart');

    // Step 4: Mở giỏ hàng
    await page.click('[data-testid="cart-button"]');
    
    // Step 5: Xác minh giỏ có sản phẩm
    await expect(page.locator('[data-testid="cart-item"]')).toHaveCount(1);

    // Step 6: Thanh toán
    await page.click('button:has-text("Checkout")');

    // Step 7: Điền thông tin giao hàng
    await page.fill('[name="address"]', '123 Main St');
    await page.fill('[name="phone"]', '0123456789');

    // Step 8: Xác nhận đơn
    await page.click('button:has-text("Confirm Order")');

    // Step 9: Xác minh thành công
    await expect(page.locator('[data-testid="order-success"]')).toBeVisible();
    const orderId = await page.locator('[data-testid="order-id"]').textContent();
    expect(orderId).toBeTruthy();
  });

  test('Should handle validation errors', async ({ page }) => {
    await page.click('text=Products');
    await page.click('[data-testid="product-item"] button:has-text("Add to Cart")');
    await page.click('[data-testid="cart-button"]');
    await page.click('button:has-text("Checkout")');

    // Để trống required fields và submit
    await page.click('button:has-text("Confirm Order")');

    // Xác minh error messages
    await expect(page.locator('.form-error')).toHaveCount(2);
  });

  test('Should handle network errors gracefully', async ({ page }) => {
    // Simulate network error
    await page.context().setOffline(true);
    
    await page.click('text=Products');
    
    // Expect error message
    await expect(page.locator('[data-testid="error-message"]')).toContainText('Network error');
    
    await page.context().setOffline(false);
  });
});
```

**Cypress Alternative (Giống nhau):**
```javascript
// cypress/e2e/order.cy.js
describe('Order Flow E2E', () => {
  beforeEach(() => {
    cy.visit('http://localhost:5173');
  });

  it('Should complete order workflow', () => {
    cy.contains('Products').click();
    cy.get('[data-testid="product-item"]').should('exist');
    
    cy.get('[data-testid="product-item"]')
      .first()
      .within(() => {
        cy.contains('button', 'Add to Cart').click();
      });
    
    cy.contains('.toast', 'Added to cart').should('be.visible');
    cy.get('[data-testid="cart-button"]').click();
    cy.get('[data-testid="cart-item"]').should('have.length', 1);
  });
});
```

### Chạy System Tests

```bash
# Backend - REST API E2E
mvn test -Dtest=*E2ETest

# Frontend - Playwright
npx playwright test

# Frontend - Cypress
npx cypress run

# Với browser UI
npx cypress open
```

---

## Automated Testing (CI/CD)

### Định Nghĩa
Automated Testing là quá trình chạy tất cả tests tự động khi có code commit/push, không cần con người can thiệp.

### Kiến Trúc CI/CD Pipeline

```
Git Push
   ↓
[Trigger] GitHub Actions / Jenkins
   ↓
[Checkout] Lấy code từ repo
   ↓
[Build] Compile code + resolve dependencies
   ↓
[Unit Tests] Chạy tất cả unit tests (70%)
   ↓
[Code Quality] SonarQube, Code Coverage
   ↓
[Component Tests] Controllers, Components
   ↓
[Integration Tests] Database + Services
   ↓
[System Tests] E2E, API workflows
   ↓
[Deploy Staging] Nếu tất cả pass
   ↓
[System Tests] Trên staging environment
   ↓
[Deploy Production] Sau approval thủ công
```

### GitHub Actions Configuration

#### Backend (Java) CI/CD

**File:** `.github/workflows/backend-ci.yml`

```yaml
name: Backend CI/CD

on:
  push:
    branches: [ main, develop ]
    paths:
      - 'backend/**'
      - '.github/workflows/backend-ci.yml'
  pull_request:
    branches: [ main, develop ]

jobs:
  # Job 1: Build & Unit Tests
  build:
    runs-on: ubuntu-latest
    
    services:
      # Test Database
      mssql:
        image: mcr.microsoft.com/mssql/server:2019-latest
        env:
          ACCEPT_EULA: Y
          SA_PASSWORD: YourPassword123!
        options: >-
          --health-cmd="/opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P 'YourPassword123!' -Q 'SELECT 1' || exit 1"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Build with Maven
        run: mvn clean package -DskipTests -f backend/pom.xml

      - name: Run Unit Tests
        run: mvn test -f backend/pom.xml
        
      - name: Generate Coverage Report
        run: mvn jacoco:report -f backend/pom.xml
      
      - name: Upload Coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./backend/target/site/jacoco/jacoco.xml
          flags: backend-unit
          fail_ci_if_error: false

  # Job 2: Code Quality
  quality:
    runs-on: ubuntu-latest
    needs: build
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: SonarQube Analysis
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: |
          mvn clean verify sonar:sonar \
            -Dsonar.projectKey=nckh-backend \
            -Dsonar.host.url=https://sonarcloud.io \
            -Dsonar.login=$SONAR_TOKEN \
            -f backend/pom.xml

  # Job 3: Integration Tests
  integration-tests:
    runs-on: ubuntu-latest
    needs: build
    
    services:
      mssql:
        image: mcr.microsoft.com/mssql/server:2019-latest
        env:
          ACCEPT_EULA: Y
          SA_PASSWORD: YourPassword123!
        options: >-
          --health-cmd="/opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P 'YourPassword123!' -Q 'SELECT 1' || exit 1"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Run Integration Tests
        run: mvn test -Dgroups="integration" -f backend/pom.xml

  # Job 4: E2E Tests
  e2e-tests:
    runs-on: ubuntu-latest
    needs: build
    
    services:
      mssql:
        image: mcr.microsoft.com/mssql/server:2019-latest
        env:
          ACCEPT_EULA: Y
          SA_PASSWORD: YourPassword123!
        options: >-
          --health-cmd="/opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P 'YourPassword123!' -Q 'SELECT 1' || exit 1"

    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Start Application
        run: |
          cd backend/audit-service/Nckh-Hi-p
          mvn spring-boot:run &
          sleep 20

      - name: Run E2E Tests
        run: mvn test -Dtest=*E2ETest -f backend/pom.xml

  # Job 5: Build Docker Image & Push
  docker:
    runs-on: ubuntu-latest
    needs: [build, integration-tests, e2e-tests]
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Build and Push Docker Image
        run: |
          echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
          docker build -t myrepo/nckh-backend:${{ github.sha }} backend/
          docker push myrepo/nckh-backend:${{ github.sha }}
```

#### Frontend (Vue) CI/CD

**File:** `.github/workflows/frontend-ci.yml`

```yaml
name: Frontend CI/CD

on:
  push:
    branches: [ main, develop ]
    paths:
      - 'frontend/**'
      - '.github/workflows/frontend-ci.yml'
  pull_request:
    branches: [ main, develop ]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    
    strategy:
      matrix:
        node-version: [18.x, 20.x]

    steps:
      - uses: actions/checkout@v3
      
      - name: Use Node.js ${{ matrix.node-version }}
        uses: actions/setup-node@v3
        with:
          node-version: ${{ matrix.node-version }}
          cache: 'npm'
          cache-dependency-path: frontend/web-client/package-lock.json

      - name: Install Dependencies
        run: npm ci
        working-directory: frontend/web-client

      - name: Build
        run: npm run build
        working-directory: frontend/web-client

      - name: Lint
        run: npm run lint
        working-directory: frontend/web-client

      - name: Run Unit Tests
        run: npm run test
        working-directory: frontend/web-client
        
      - name: Upload Coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./frontend/web-client/coverage/coverage-final.json
          flags: frontend-unit

  e2e-tests:
    runs-on: ubuntu-latest
    needs: build-and-test
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Use Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '20.x'
          cache: 'npm'
          cache-dependency-path: frontend/web-client/package-lock.json

      - name: Install Dependencies
        run: npm ci
        working-directory: frontend/web-client

      - name: Install Playwright Browsers
        run: npx playwright install --with-deps
        working-directory: frontend/web-client

      - name: Start Dev Server
        run: npm run dev &
        working-directory: frontend/web-client

      - name: Run E2E Tests
        run: npm run test:e2e
        working-directory: frontend/web-client

      - name: Upload Playwright Report
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: playwright-report
          path: frontend/web-client/playwright-report/
          retention-days: 30
```

### Jenkins Pipeline Configuration

**File:** `Jenkinsfile`

```groovy
pipeline {
    agent any
    
    options {
        timeout(time: 1, unit: 'HOURS')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Unit Tests') {
            parallel {
                stage('Backend Unit Tests') {
                    steps {
                        dir('backend') {
                            sh 'mvn test'
                        }
                    }
                }
                stage('Frontend Unit Tests') {
                    steps {
                        dir('frontend/web-client') {
                            sh 'npm install && npm run test'
                        }
                    }
                }
            }
        }

        stage('Code Quality') {
            steps {
                dir('backend') {
                    sh '''
                        mvn clean verify sonar:sonar \
                            -Dsonar.projectKey=nckh \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.login=${SONAR_TOKEN}
                    '''
                }
            }
        }

        stage('Integration Tests') {
            steps {
                dir('backend') {
                    sh 'mvn test -Dgroups="integration"'
                }
            }
        }

        stage('Build Docker Images') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    docker build -t nckh-backend:${BUILD_NUMBER} backend/
                    docker build -t nckh-frontend:${BUILD_NUMBER} frontend/web-client/
                '''
            }
        }

        stage('Deploy to Staging') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    docker-compose -f docker-compose.staging.yml pull
                    docker-compose -f docker-compose.staging.yml up -d
                    sleep 30
                '''
            }
        }

        stage('E2E Tests on Staging') {
            when {
                branch 'main'
            }
            steps {
                dir('backend') {
                    sh 'mvn test -Dtest=*E2ETest'
                }
            }
        }
    }

    post {
        always {
            // Lưu test results
            junit '**/target/surefire-reports/*.xml'
            publishHTML([
                reportDir: 'backend/target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'Code Coverage'
            ])
            
            // Archive logs
            archiveArtifacts artifacts: '**/logs/**', allowEmptyArchive: true
        }
        
        failure {
            // Gửi notification
            emailext(
                subject: "Build Failed: ${env.JOB_NAME}",
                body: "Build ${env.BUILD_NUMBER} failed. Check console logs at ${env.BUILD_URL}",
                to: '${DEFAULT_RECIPIENTS}'
            )
        }
        
        success {
            echo 'All tests passed! Ready for deployment.'
        }
    }
}
```

### Test Reporting & Metrics

#### Coverage Report Configuration

**Backend (Maven + JaCoCo):**
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>jacoco-check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <includesPattern>com.nckh.*</includesPattern>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Frontend (Vitest + c8):**
```javascript
// vite.config.js
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  test: {
    coverage: {
      provider: 'c8',
      reporter: ['text', 'json', 'html', 'lcov'],
      exclude: [
        'node_modules/',
        'src/__tests__/',
      ],
      lines: 80,
      branches: 80,
      functions: 80,
      statements: 80
    }
  }
});
```

---

## Postman Automated Testing

### Định Nghĩa
Postman Automated Testing là sử dụng Postman để viết và chạy tests cho APIs một cách tự động, có thể tích hợp vào CI/CD pipeline.

### Ưu Điểm
- ✅ **Giao diện người dùng thân thiện**: Dễ viết tests mà không cần code
- ✅ **Chạy collections**: Chạy nhiều APIs liên tiếp
- ✅ **Tích hợp CI/CD**: Chạy tự động qua Newman (CLI)
- ✅ **Mock Servers**: Dễ test với mock data
- ✅ **Shared Workspaces**: Team cộng tác
- ✅ **Automated Reports**: Tạo báo cáo chi tiết

### Cài Đặt & Cấu Hình

#### 1. Tải Postman
```bash
# Windows
# Download từ https://www.postman.com/downloads/

# hoặc sử dụng Chocolatey
choco install postman

# Mac
brew install postman

# Linux
snap install postman
```

#### 2. Cài Đặt Newman (CLI)
```bash
# npm install
npm install -g newman

# hoặc
npm install -g newman newman-reporter-html newman-reporter-json
```

#### 3. Kiểm tra cài đặt
```bash
postman --version
newman --version
```

### Cấu Trúc Test trong Postman

#### Request Structure
```
Request
  ├── Pre-request Script (Chuẩn bị)
  ├── Body (Dữ liệu gửi)
  └── Tests (Kiểm tra response)

Response
  ├── Status Code
  ├── Headers
  ├── Body
  └── Response Time
```

### Ví Dụ Thực Tế: Order Service Tests

#### 1. Tạo Collection "Order Service API"

**Collection Variables:**
```json
{
  "baseUrl": "http://localhost:8080/api",
  "orderId": "",
  "orderStatus": "PENDING"
}
```

#### 2. Request 1: Create Order

**Request:**
```
POST {{baseUrl}}/orders
```

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Body:**
```json
{
  "items": [
    {
      "productId": 1,
      "productName": "PRODUCT1",
      "price": 100.00,
      "quantity": 2
    },
    {
      "productId": 2,
      "productName": "PRODUCT2",
      "price": 50.00,
      "quantity": 1
    }
  ]
}
```

**Pre-request Script:**
```javascript
// Tạo timestamp
const timestamp = new Date().toISOString();
pm.environment.set("timestamp", timestamp);

// Log request info
console.log("Creating order at: " + timestamp);
```

**Tests:**
```javascript
// 1. Kiểm tra status code
pm.test("Status code should be 201 Created", function() {
    pm.response.to.have.status(201);
});

// 2. Kiểm tra response body
pm.test("Response should have id", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData.id).to.be.a('number');
    
    // Lưu orderId để dùng ở request sau
    pm.collectionVariables.set("orderId", jsonData.id);
});

// 3. Kiểm tra totalPrice
pm.test("Total price should be calculated correctly", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData.totalPrice).to.equal(250.00);
});

// 4. Kiểm tra status
pm.test("Order status should be PENDING", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData.status).to.equal("PENDING");
});

// 5. Kiểm tra response time
pm.test("Response time should be less than 1000ms", function() {
    pm.expect(pm.response.responseTime).to.be.below(1000);
});

// 6. Kiểm tra structure
pm.test("Response should have required fields", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.include.all.keys('id', 'items', 'totalPrice', 'status', 'createdAt');
});
```

#### 3. Request 2: Get Order by ID

**Request:**
```
GET {{baseUrl}}/orders/{{orderId}}
```

**Tests:**
```javascript
pm.test("Should retrieve order successfully", function() {
    pm.response.to.have.status(200);
    const jsonData = pm.response.json();
    pm.expect(jsonData.id).to.equal(Number(pm.collectionVariables.get("orderId")));
});

pm.test("Order details should match created order", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData.status).to.equal("PENDING");
    pm.expect(jsonData.totalPrice).to.equal(250.00);
    pm.expect(jsonData.items).to.have.lengthOf(2);
});

pm.test("Items should have correct data", function() {
    const jsonData = pm.response.json();
    const firstItem = jsonData.items[0];
    
    pm.expect(firstItem).to.include({
        productName: "PRODUCT1",
        quantity: 2,
        price: 100.00
    });
});
```

#### 4. Request 3: Update Order Status

**Request:**
```
PUT {{baseUrl}}/orders/{{orderId}}/status
```

**Body:**
```json
{
  "status": "CONFIRMED"
}
```

**Pre-request Script:**
```javascript
// Validate orderId exists
if (!pm.collectionVariables.get("orderId")) {
    throw new Error("orderId is required. Please run 'Create Order' request first.");
}

console.log("Updating order ID: " + pm.collectionVariables.get("orderId"));
```

**Tests:**
```javascript
pm.test("Should update order status to CONFIRMED", function() {
    pm.response.to.have.status(200);
    const jsonData = pm.response.json();
    pm.expect(jsonData.status).to.equal("CONFIRMED");
    
    // Lưu status mới
    pm.collectionVariables.set("orderStatus", jsonData.status);
});

pm.test("Other order fields should remain unchanged", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData.totalPrice).to.equal(250.00);
    pm.expect(jsonData.items).to.have.lengthOf(2);
});
```

#### 5. Request 4: Cancel Order

**Request:**
```
DELETE {{baseUrl}}/orders/{{orderId}}
```

**Tests:**
```javascript
pm.test("Should cancel order successfully", function() {
    pm.response.to.have.status(200);
});

pm.test("Cancelled order should have status CANCELLED", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData.status).to.equal("CANCELLED");
});

pm.test("Order should be marked as deleted", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData.deletedAt).to.exist;
});
```

#### 6. Request 5: Verify Order Cancelled

**Request:**
```
GET {{baseUrl}}/orders/{{orderId}}
```

**Tests:**
```javascript
pm.test("Cancelled order should return 404 or show as deleted", function() {
    // Tuỳ API implementation
    // Option 1: Return 404
    pm.expect([404, 200]).to.include(pm.response.code);
    
    // Option 2: Return with deletedAt
    if (pm.response.code === 200) {
        const jsonData = pm.response.json();
        pm.expect(jsonData.deletedAt).to.exist;
    }
});
```

### Loại Tests Phổ Biến

#### 1. Status Code Tests
```javascript
pm.test("Status code is 200", function() {
    pm.response.to.have.status(200);
});

pm.test("Status code is one of 200, 201, 204", function() {
    pm.expect(pm.response.code).to.be.oneOf([200, 201, 204]);
});
```

#### 2. Response Body Tests
```javascript
pm.test("Response has id field", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData.id).to.exist;
    pm.expect(jsonData.id).to.be.a('number');
});

pm.test("Response data type validation", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData.name).to.be.a('string');
    pm.expect(jsonData.price).to.be.a('number');
    pm.expect(jsonData.active).to.be.a('boolean');
});
```

#### 3. Response Headers Tests
```javascript
pm.test("Response should have content-type header", function() {
    pm.response.to.have.header("Content-Type");
});

pm.test("Content-Type should be application/json", function() {
    pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json");
});
```

#### 4. Response Time Tests
```javascript
pm.test("Response time should be less than 500ms", function() {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

pm.test("Response time should be within acceptable range", function() {
    pm.expect(pm.response.responseTime).to.be.above(50);
    pm.expect(pm.response.responseTime).to.be.below(1000);
});
```

#### 5. Array Tests
```javascript
pm.test("Response should have at least 1 item", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData.items).to.have.lengthOf.at.least(1);
});

pm.test("Items should have correct structure", function() {
    const jsonData = pm.response.json();
    jsonData.items.forEach(item => {
        pm.expect(item).to.have.all.keys('id', 'name', 'price');
    });
});
```

#### 6. Error Handling Tests
```javascript
pm.test("Should return 400 for invalid request", function() {
    pm.response.to.have.status(400);
    const jsonData = pm.response.json();
    pm.expect(jsonData.error).to.exist;
});

pm.test("Error response should have message", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData.message).to.be.a('string');
    pm.expect(jsonData.message).to.not.be.empty;
});
```

### Chạy Postman Collections

#### 1. Qua Postman UI
```
1. Mở Collection
2. Nhấn "Run" (play icon)
3. Chọn requests để chạy
4. Nhấn "Run Order Service API"
5. Xem kết quả trong "Results"
```

#### 2. Qua CLI (Newman)

**Chạy collection đơn giản:**
```bash
newman run "Order Service API.postman_collection.json"
```

**Chạy với environment variables:**
```bash
newman run "Order Service API.postman_collection.json" \
  -e "Production.postman_environment.json"
```

**Chạy với custom options:**
```bash
newman run "Order Service API.postman_collection.json" \
  -e "Production.postman_environment.json" \
  --iteration-count 3 \
  --delay-request 500 \
  --timeout-request 10000
```

**Tạo HTML Report:**
```bash
newman run "Order Service API.postman_collection.json" \
  -e "Production.postman_environment.json" \
  -r html,json,cli \
  --reporter-html-export "reports/postman-report.html"
```

#### 3. Export Collection & Environment

**Export từ Postman UI:**
```
1. Right-click Collection → Export
2. Chọn format "Collection v2.1"
3. Lưu file .json
4. Commit vào repo
```

### File Export Examples

**postman_collection.json:**
```json
{
  "info": {
    "name": "Order Service API",
    "description": "API tests for Order Service",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Create Order",
      "request": {
        "method": "POST",
        "url": {
          "raw": "{{baseUrl}}/orders",
          "host": ["{{baseUrl}}"],
          "path": ["orders"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\n  \"items\": [...]\n}"
        }
      },
      "response": []
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/api"
    }
  ]
}
```

**postman_environment.json:**
```json
{
  "id": "env-id",
  "name": "Development",
  "values": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/api",
      "type": "string",
      "enabled": true
    },
    {
      "key": "token",
      "value": "your-jwt-token",
      "type": "string",
      "enabled": true
    },
    {
      "key": "orderId",
      "value": "",
      "type": "string",
      "enabled": true
    }
  ]
}
```

### Tích Hợp CI/CD (GitHub Actions)

**File:** `.github/workflows/postman-tests.yml`

```yaml
name: Postman API Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  schedule:
    - cron: '0 0 * * *'  # Daily at midnight

jobs:
  postman-tests:
    runs-on: ubuntu-latest
    
    services:
      # Chạy backend service trong container
      backend:
        image: nckh-backend:latest
        ports:
          - 8080:8080
        env:
          DATABASE_URL: ${{ secrets.DATABASE_URL }}
        options: >-
          --health-cmd="curl -f http://localhost:8080/health || exit 1"
          --health-interval=10s
          --health-timeout=5s
          --health-retries=5

    steps:
      - uses: actions/checkout@v3
      
      - name: Install Newman
        run: npm install -g newman newman-reporter-html
      
      - name: Wait for backend to be ready
        run: |
          for i in {1..30}; do
            curl http://localhost:8080/health && break
            echo "Waiting for backend..."
            sleep 2
          done
      
      - name: Run Postman Tests - Development
        run: |
          newman run scripts/postman_collection.json \
            -e scripts/postman_environment_dev.json \
            -r html,json,cli \
            --reporter-html-export "reports/postman-dev-report.html" \
            --reporter-json-export "reports/postman-dev-report.json"
      
      - name: Run Postman Tests - Integration
        run: |
          newman run scripts/postman_collection.json \
            -e scripts/postman_environment_integration.json \
            --iteration-count 2 \
            --delay-request 500
      
      - name: Upload HTML Report
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: postman-reports
          path: reports/
          retention-days: 30
      
      - name: Publish Test Results
        if: always()
        uses: dorny/test-reporter@v1
        with:
          name: Postman Test Results
          path: 'reports/postman-*.json'
          reporter: 'java-junit'
      
      - name: Comment PR with Results
        if: github.event_name == 'pull_request'
        uses: actions/github-script@v7
        with:
          script: |
            const fs = require('fs');
            const report = JSON.parse(fs.readFileSync('reports/postman-dev-report.json', 'utf8'));
            const passed = report.run.stats.tests.passed;
            const failed = report.run.stats.tests.failed;
            
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: `## Postman Test Results\n✅ Passed: ${passed}\n❌ Failed: ${failed}`
            });
```

### Tích Hợp Jenkins

**Jenkinsfile snippet:**
```groovy
stage('Postman API Tests') {
    steps {
        echo 'Installing Newman...'
        sh 'npm install -g newman'
        
        echo 'Running Postman tests...'
        sh '''
            newman run scripts/postman_collection.json \
                -e scripts/postman_environment_dev.json \
                -r html,json,junit \
                --reporter-html-export "reports/postman-report.html" \
                --reporter-json-export "reports/postman-report.json" \
                --reporter-junit-export "reports/postman-report.xml"
        '''
    }
}

post {
    always {
        publishHTML([
            reportDir: 'reports',
            reportFiles: 'postman-report.html',
            reportName: 'Postman API Tests'
        ])
        
        junit 'reports/postman-report.xml'
    }
}
```

### Best Practices Postman

#### 1. Organization
```
Collection: Order Service API
├── Setup
│   └── Initialize Variables
├── Create Order
│   ├── Create Order Success
│   └── Create Order Validation Error
├── Get Order
│   ├── Get Order By ID
│   └── Get Non-existent Order (404)
├── Update Order
│   └── Update Status
└── Cleanup
    └── Delete Order
```

#### 2. Use Environment Variables
```javascript
// ✅ Good
{{baseUrl}}/orders
{{token}}

// ❌ Bad
http://localhost:8080/api/orders
hardcoded-token-123
```

#### 3. Assertions
```javascript
// ✅ Specific assertions
pm.test("Should return array of orders", function() {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
    pm.expect(jsonData).to.have.lengthOf.above(0);
});

// ❌ Vague assertions
pm.test("Response is ok", function() {
    pm.expect(true).to.be.true;
});
```

#### 4. Error Scenarios
```javascript
// Test success case
pm.test("Create order - Success", function() { ... });

// Test validation errors
pm.test("Create order - Empty items validation", function() { ... });

// Test not found
pm.test("Get order - Not found", function() { ... });

// Test unauthorized
pm.test("Unauthorized - Missing token", function() { ... });
```

### Ví Dụ Scripts Nâng Cao

#### Dynamic Data
```javascript
// Pre-request Script
const randomId = Math.floor(Math.random() * 10000);
pm.collectionVariables.set("orderId", randomId);

const timestamp = new Date().toISOString();
pm.collectionVariables.set("timestamp", timestamp);
```

#### Conditional Tests
```javascript
pm.test("Conditional test based on status", function() {
    const jsonData = pm.response.json();
    
    if (jsonData.status === "PENDING") {
        pm.expect(jsonData).to.have.property('expectedDeliveryDate');
    }
});
```

#### Loop through Array
```javascript
pm.test("Validate all items", function() {
    const jsonData = pm.response.json();
    
    pm.expect(jsonData.items).to.be.an('array');
    jsonData.items.forEach((item, index) => {
        pm.expect(item.id).to.be.a('number');
        pm.expect(item.quantity).to.be.above(0);
    });
});
```

#### Set Variable từ Response
```javascript
const jsonData = pm.response.json();

// Set environment variable
pm.environment.set("authToken", jsonData.token);

// Set collection variable
pm.collectionVariables.set("lastOrderId", jsonData.id);

// Set global variable
pm.globals.set("userId", jsonData.userId);
```

### Chạy Tests từ Dòng Lệnh

**Ví dụ hoàn chỉnh:**
```bash
# 1. Chạy basic
newman run Order\ Service\ API.postman_collection.json

# 2. Chạy với environment
newman run Order\ Service\ API.postman_collection.json \
  -e Development.postman_environment.json

# 3. Chạy từ URL (Postman Cloud)
newman run https://api.getpostman.com/collections/123456 \
  --api-key YOUR_POSTMAN_API_KEY

# 4. Chạy multiple iterations
newman run Order\ Service\ API.postman_collection.json \
  --iteration-count 5

# 5. Delay giữa requests
newman run Order\ Service\ API.postman_collection.json \
  --delay-request 500

# 6. Custom timeout
newman run Order\ Service\ API.postman_collection.json \
  --timeout-request 10000

# 7. Generate multiple reporters
newman run Order\ Service\ API.postman_collection.json \
  -r html,json,cli,junit \
  --reporter-html-export reports/report.html \
  --reporter-json-export reports/report.json \
  --reporter-junit-export reports/report.xml
```

---

## Best Practices

### 1. Test Naming Conventions

```
✅ Good:
- testShouldCreateOrderWithValidRequest()
- should_calculate_total_price_correctly()
- testOrderCannotBeCreatedWithEmptyItems()

❌ Bad:
- test1()
- testOrder()
- test()
```

### 2. Test Data Management

```java
// ✅ Good - Fixture/Builder Pattern
private Order createTestOrder(String status) {
    return Order.builder()
        .status(status)
        .totalPrice(new BigDecimal("100.00"))
        .build();
}

// ❌ Bad - Hardcoded data scattered in tests
Order order = new Order();
order.setId(1L);
order.setStatus("PENDING");
// ... nhiều dòng setup
```

### 3. AAA Pattern (Arrange-Act-Assert)

```java
@Test
void testExample() {
    // Arrange - Chuẩn bị dữ liệu
    CreateOrderRequest request = new CreateOrderRequest();
    request.setItems(...);
    
    // Act - Thực hiện hành động
    Order result = orderService.createOrder(request);
    
    // Assert - Kiểm tra kết quả
    assertThat(result).isNotNull();
}
```

### 4. Avoid Test Interdependence

```java
❌ Bad:
class OrderServiceTest {
    private static Order testOrder;
    
    @BeforeAll
    static void setup() {
        testOrder = createOrder(); // Tất cả tests dùng chung
    }
}

✅ Good:
class OrderServiceTest {
    @BeforeEach
    void setup() {
        // Mỗi test có dữ liệu riêng
        Order testOrder = createOrder();
    }
}
```

### 5. Mock External Dependencies

```java
✅ Good - Mock HTTP calls
@Mock
private RestTemplate restTemplate;

when(restTemplate.getForObject(anyString(), eq(String.class)))
    .thenReturn("mocked response");

❌ Bad - Thực hiện HTTP call trong test
new RestTemplate().getForObject("http://api.example.com/...");
```

### 6. Test Documentation

```java
@Test
@DisplayName("Should calculate order total price with 10% discount for orders over $1000")
void testCalculateTotalWithDiscount() {
    // Test logic
}
```

### 7. Isolate Test Environment

```yaml
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

### 8. Monitor & Report

```bash
# Tạo coverage report
mvn clean test jacoco:report

# Xem report
open target/site/jacoco/index.html

# SonarQube
mvn sonar:sonar -Dsonar.projectKey=nckh
```

---

## Tools & Technologies

### Recommended Stack

| Layer | Tool | Version | Purpose |
|-------|------|---------|---------|
| **Unit Test** | JUnit 5, Vitest | Latest | Test framework |
| **Mocking** | Mockito, Sinon | 5.x | Mock objects |
| **Assertions** | AssertJ, Chai | 3.x | Fluent assertions |
| **Integration** | Spring Test, TestContainers | Latest | Integration testing |
| **E2E** | Playwright, Cypress | 1.x | Browser automation |
| **Coverage** | JaCoCo, c8 | Latest | Code coverage |
| **CI/CD** | GitHub Actions, Jenkins | Latest | Automation |
| **Quality** | SonarQube | Latest | Code quality |

### Setup Commands

#### Backend Setup
```bash
# Clone & Install
git clone <repo>
cd backend/audit-service/Nckh-Hi-p
mvn clean install -DskipTests

# Run specific test
mvn test -Dtest=OrderServiceTest

# With coverage
mvn clean test jacoco:report
```

#### Frontend Setup
```bash
# Clone & Install
git clone <repo>
cd frontend/web-client
npm install

# Run tests
npm run test

# With coverage
npm run test:coverage

# E2E with Playwright
npm run test:e2e
```

---

## Quick Reference

### Unit Test Template

**Backend:**
```bash
mvn test -Dtest=ServiceNameTest
mvn test -f backend/pom.xml
```

**Frontend:**
```bash
npm run test -- ComponentName.spec.js
npm run test:watch
```

### Integration Test Template

**Backend:**
```bash
mvn test -Dgroups="integration"
mvn test -Dtest=*IntegrationTest
```

**Frontend:**
```bash
npm run test -- services/
```

### E2E Test Template

**Backend:**
```bash
mvn test -Dtest=*E2ETest
```

**Frontend:**
```bash
npx playwright test
npx cypress run
```

### Local Test Execution

```bash
# All tests
npm run test && mvn test

# Specific layer
npm run test:unit      # Frontend unit
npm run test:e2e       # Frontend E2E
mvn test -Dgroups="integration"  # Backend integration

# With coverage
npm run test:coverage
mvn clean test jacoco:report
```

---

## Khởi Động Nhanh

1. **Cài đặt dependencies**: `npm install` & `mvn install`
2. **Chạy unit tests**: `npm run test` & `mvn test`
3. **Kiểm tra coverage**: `npm run test:coverage` & `mvn jacoco:report`
4. **Chạy E2E tests**: `npx playwright test`
5. **Xem reports**: `open coverage/` & `open target/site/jacoco/`

---

**Lần cập nhật cuối**: Feb 3, 2026
**Version**: 1.0
**Maintainers**: Development Team
