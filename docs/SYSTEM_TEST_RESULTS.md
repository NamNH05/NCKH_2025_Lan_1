# SYSTEM TEST: KIỂM THỬ HỆ THỐNG E-COMMERCE
## Báo Cáo Kết Quả End-to-End Testing

**Ngày:** 06/02/2026  
**Trạng thái:** ✅ **HOÀN THÀNH - 100% PASS**

---

## I. GIỚI THIỆU VỀ SYSTEM TEST

**System Test (End-to-End)** là loại kiểm thử toàn diện, kiểm tra toàn bộ hệ thống microservices hoạt động cùng nhau như một tổng thể.

### Phạm vi kiểm thử:

| Thành phần | Chi tiết |
|-----------|----------|
| **Auth Service** | Port 8080 - PostgreSQL - Xác thực người dùng |
| **Product Service** | Port 8081 - MySQL - Quản lý sản phẩm |
| **Order Service** | Port 8091 - SQL Server - Quản lý đơn hàng |
| **Cơ sở dữ liệu** | 3 database khác nhau hoạt động đồng thời |
| **Luồng dữ liệu** | Xác minh dữ liệu chảy chính xác giữa các services |

---

## II. KỊCH BẢN KIỂM THỬ (5 Scenarios)

1. **ST1: Auth Service** - Đăng ký → Đăng nhập → Xem profile (3 test cases)
2. **ST2: Product Service** - Duyệt sản phẩm → Xem chi tiết (2 test cases)
3. **ST3: Order Service** - Quản lý đơn hàng → Tạo đơn → Theo dõi (3 test cases)
4. **ST4: Cross-Service** - Tích hợp giữa các services (3 test cases)
5. **ST5: Security** - Kiểm tra bảo mật JWT & authentication (2 test cases)

---

## III. GIỚI THIỆU CÁC TEST CASE

### ST1: Auth Service (3 test cases)

**Mục đích:** Kiểm tra toàn bộ luồng xác thực từ đăng ký đến truy cập tài nguyên protected

| # | Test Case | Hành động | Kỳ vọng |
|---|-----------|----------|--------|
| 1 | Register New User | POST /api/auth/register với dữ liệu user mới | User được tạo trong PostgreSQL, HTTP 201 |
| 2 | Login | POST /api/auth/login với username/password | JWT token được tạo, HTTP 200 |
| 3 | Protected Endpoint | GET /api/test/me với JWT token | Trả về user info, HTTP 200 |

**Luồng kiểm thử:** Register → Login (nhận token) → Dùng token truy cập protected endpoint

---

### ST2: Product Service (2 test cases)

**Mục đích:** Kiểm tra tính năng duyệt và xem chi tiết sản phẩm

| # | Test Case | Hành động | Kỳ vọng |
|---|-----------|----------|--------|
| 1 | Get All Products | GET /api/products | Trả về danh sách sản phẩm từ MySQL, HTTP 200 |
| 2 | Get Product Details | GET /api/products/{id} | Trả về chi tiết sản phẩm đầy đủ, HTTP 200 |

**Luồng kiểm thử:** Lấy danh sách → Chọn sản phẩm → Xem chi tiết

---

### ST3: Order Service (3 test cases)

**Mục đích:** Kiểm tra quá trình tạo và theo dõi đơn hàng

| # | Test Case | Hành động | Kỳ vọng |
|---|-----------|----------|--------|
| 1 | Get All Orders | GET /api/orders | Trả về danh sách đơn hàng, HTTP 200 |
| 2 | Create Order | POST /api/orders/checkout với cart items | Tạo đơn hàng mới trong SQL Server, HTTP 201 |
| 3 | Get Order Details | GET /api/orders/{id} | Trả về chi tiết đơn hàng + items, HTTP 200 |

**Luồng kiểm thử:** Tạo đơn → Lưu order ID → Lấy chi tiết đơn hàng vừa tạo

---

### ST4: Cross-Service Integration (3 test cases)

**Mục đích:** Kiểm tra tích hợp giữa 3 services - dữ liệu và token chảy chính xác

| # | Test Case | Hành động | Kỳ vọng |
|---|-----------|----------|--------|
| 1 | Fresh Token | Login lại để lấy token mới | Token được tạo, HTTP 200 |
| 2 | Token Cross-Service | Dùng Auth token trên Product Service | Token được chấp nhận, HTTP 200 |
| 3 | Data Cross-Service | Sử dụng product data trong Order | Dữ liệu sản phẩm chảy chính xác sang order |

**Luồng kiểm thử:** Auth → Token → Sử dụng trên các services khác → Dữ liệu consistent

---

### ST5: Security Tests (2 test cases)

**Mục đích:** Kiểm tra bảo mật - xác minh token validation và authorization

| # | Test Case | Hành động | Kỳ vọng |
|---|-----------|----------|--------|
| 1 | Invalid Token | Gửi request với token sai | HTTP 401 Unauthorized |
| 2 | Missing Token | Truy cập protected endpoint không token | HTTP 401 Unauthorized |

**Luồng kiểm thử:** Gửi request sai → Xác minh rejection → Không có data leak

---

## IV. KẾT QUẢ CHẠY TEST CHI TIẾT

### 3.1 Kết quả từng scenario:

**✅ ST1 - AUTH SERVICE** (Xác thực người dùng)

| Test | Kết quả | HTTP | Chi tiết |
|------|---------|------|----------|
| ST1.1 Register | ✅ PASS | 201 | Người dùng tạo trong PostgreSQL |
| ST1.2 Login | ✅ PASS | 200 | JWT token (223 ký tự) |
| ST1.3 Protected | ✅ PASS | 200 | Token chấp nhận trên protected endpoint |

**🎯 Kết quả: ✅ AUTH FLOW HOÀN THÀNH**

---

**✅ ST2 - PRODUCT SERVICE** (Quản lý sản phẩm)

| Test | Kết quả | HTTP | Chi tiết |
|------|---------|------|----------|
| ST2.1 All Products | ✅ PASS | 200 | Lấy 5+ sản phẩm từ MySQL |
| ST2.2 Product Details | ✅ PASS | 200 | Chi tiết sản phẩm đầy đủ |

**🎯 Kết quả: ✅ PRODUCT SERVICE HOÀN THÀNH**

---

**✅ ST3 - ORDER SERVICE** (Quản lý đơn hàng)

| Test | Kết quả | HTTP | Chi tiết |
|------|---------|------|----------|
| ST3.1 Get Orders | ✅ PASS | 200 | Endpoint accessible |
| ST3.2 Create Order | ✅ PASS | 201 | Đơn hàng tạo trong SQL Server |
| ST3.3 Get Order | ✅ PASS | 200 | Chi tiết với items & timestamps |

**🎯 Kết quả: ✅ ORDER SERVICE HOÀN THÀNH**

---

**✅ ST4 - CROSS-SERVICE INTEGRATION** (Tích hợp services)

| Test | Kết quả | Chi tiết |
|------|---------|----------|
| ST4.1 Fresh Token | ✅ PASS | JWT token được tạo mới |
| ST4.2 Token Cross-Service | ✅ PASS | Token từ Auth hoạt động trên Product |
| ST4.3 Data Flow | ✅ PASS | Dữ liệu sản phẩm chảy sang Order |

**🎯 Kết quả: ✅ CROSS-SERVICE HOÀN THÀNH**

---

**✅ ST5 - SECURITY TESTS** (Bảo mật)

| Test | Kết quả | HTTP | Chi tiết |
|------|---------|------|----------|
| ST5.1 Invalid Token | ✅ PASS | 401 | Token sai định dạng bị từ chối |
| ST5.2 Missing Token | ✅ PASS | 401 | Truy cập không token bị từ chối |

**🎯 Kết quả: ✅ SECURITY VERIFIED**

---

### 3.2 Tổng hợp kết quả toàn bộ:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    SYSTEM TEST SUMMARY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  📊 Test Scenarios:            5 ✅
  📊 Total API Requests:       13/13 ✅
  📊 Total Assertions:         35+ ✅
  
  ✅ PASS:                     13/13 (100%)
  ❌ FAIL:                      0
  
  Pass Rate:                   100%
  Execution Time:              ~45-60 seconds

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

| Service | Test Cases | Pass | Fail | Rate |
|---------|-----------|------|------|------|
| Auth Service | 3 | 3 | 0 | 100% |
| Product Service | 2 | 2 | 0 | 100% |
| Order Service | 3 | 3 | 0 | 100% |
| Cross-Service | 3 | 3 | 0 | 100% |
| Security | 2 | 2 | 0 | 100% |
| **TOTAL** | **13** | **13** | **0** | **100%** |
