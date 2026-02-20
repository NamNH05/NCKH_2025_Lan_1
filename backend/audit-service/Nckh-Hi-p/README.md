# 📊 Audit Service - Tài Liệu Chi Tiết

## Tổng Quan Service

### Tên Service
**Audit Service** (Dịch vụ Ghi Nhật Ký Kiểm Toán)

### Mục Đích Chính
Audit Service ghi lại tất cả hành động quan trọng trong hệ thống:
- **Tạo người dùng** - Khi user đăng ký/admin tạo user
- **Đăng nhập** - Khi user login (IP address, time)
- **Thay đổi dữ liệu** - Khi sản phẩm/đơn hàng bị modify
- **Xóa dữ liệu** - Khi có dữ liệu bị xóa
- **Lỗi bảo mật** - Khi có cố gắng truy cập trái phép

### Service Này Giải Quyết Bài Toán Gì?
Trong một e-commerce lớn:
- Cần biết **ai** đã làm **gì** vào **lúc nào**
- Cần **backup dữ liệu** - nếu xóa nhầm có thể khôi phục
- Cần **kiểm tra an toàn** - phát hiện hack, truy cập bất thường
- Cần **compliance** - báo cáo cho quý/năm
- Cần **debug issues** - tìm lý do tại sao dữ liệu thay đổi

### Service Này KHÔNG Làm Những Gì?
❌ Không xóa dữ liệu (chỉ ghi nhận)  
❌ Không thay đổi dữ liệu gốc  
❌ Không tự động backup (chỉ ghi log)  
❌ Không phát email cảnh báo (có thể mở rộng)

---

## 🏗️ Kiến Trúc Nội Bộ

### Các Layer

```
┌────────────────────────────────┐
│   Controller (AuditController) │
│   GET /api/audits              │
│   POST /api/audits (internal)  │
└────────────┬────────────────────┘
             │ gọi
┌────────────▼────────────────────┐
│   Service (AuditService)        │
│   - logAction()                 │
│   - getAuditLogs()              │
│   - searchByUser()              │
└────────────┬────────────────────┘
             │ gọi
┌────────────▼────────────────────┐
│   Repository (AuditLogRepository)│
│   - findAll()                   │
│   - save()                      │
│   - findByUserId()              │
└────────────┬────────────────────┘
             │ query
┌────────────▼────────────────────┐
│   Database (PostgreSQL)         │
│   - audit_logs table            │
└────────────────────────────────┘
```

### Luồng Request

```
Auth Service gọi:
  POST /api/audits
  {
    "sourceService": "auth-service",
    "actionType": "CREATE",
    "entityName": "USER",
    "entityId": "1",
    "userId": "1",
    "oldValue": null,
    "newValue": "{username: john_doe, ...}",
    "ipAddress": "192.168.1.1",
    "userAgent": "Chrome/..."
  }
    ↓
AuditController.logAudit()
    ↓
AuditService.logAction()
    ├─ Validate request
    ├─ Enhance data (timestamp, source, etc)
    └─ Save to database
    ↓
Database: INSERT into audit_logs(...)
```

---

## 🛠️ Công Nghệ Sử Dụng

| Thành Phần | Chi Tiết |
|-----------|---------|
| **Ngôn Ngữ** | Java 17+ |
| **Framework** | Spring Boot 3.x |
| **Database** | PostgreSQL 15 |
| **Messaging** | REST API (Sync) |

---

## 🗄️ Database Schema

### Bảng `audit_logs`
```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_service VARCHAR(50),           -- "auth-service", "product-service"
    action_type VARCHAR(20),              -- CREATE, READ, UPDATE, DELETE
    entity_name VARCHAR(100),             -- "USER", "PRODUCT", "ORDER"
    entity_id VARCHAR(100),               -- id of affected entity
    user_id VARCHAR(100),                 -- who did this
    old_value TEXT,                       -- previous value (JSON)
    new_value TEXT,                       -- new value (JSON)
    ip_address VARCHAR(50),               -- client IP
    user_agent VARCHAR(500),              -- browser info
    status VARCHAR(20),                   -- SUCCESS, FAILURE
    error_message VARCHAR(500),           -- if failed
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_id ON audit_logs(user_id);
CREATE INDEX idx_entity_name ON audit_logs(entity_name);
CREATE INDEX idx_created_at ON audit_logs(created_at DESC);
```

---

## 🔌 API Endpoints

### 1️⃣ **Ghi Audit Log** (Internal - chỉ backend gọi)
```http
POST /api/audits
Authorization: Bearer <service-token>
Content-Type: application/json

{
  "sourceService": "auth-service",
  "actionType": "CREATE",
  "entityName": "USER",
  "entityId": "1",
  "userId": "1",
  "oldValue": null,
  "newValue": "{\"username\":\"john_doe\",\"email\":\"john@example.com\"}",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0..."
}
```

**Response (201 Created)**
```json
{
  "id": 1,
  "sourceService": "auth-service",
  "actionType": "CREATE",
  "entityName": "USER",
  "createdAt": "2026-01-16T10:30:00"
}
```

---

### 2️⃣ **Lấy Danh Sách Audit Logs** (Admin only)
```http
GET /api/audits?entityName=USER&days=7
Authorization: Bearer <token>
```

**Response (200 OK)**
```json
[
  {
    "id": 1,
    "sourceService": "auth-service",
    "actionType": "CREATE",
    "entityName": "USER",
    "entityId": "1",
    "userId": "1",
    "newValue": "{...}",
    "ipAddress": "192.168.1.100",
    "createdAt": "2026-01-16T10:30:00"
  }
]
```

**Query Parameters**
- `entityName` - Filter by entity type
- `actionType` - Filter by action (CREATE, UPDATE, DELETE)
- `userId` - Filter by user
- `days` - Last N days (default 30)
- `page` - Pagination

---

## ⚙️ Cấu Hình

### `application.yml`
```yaml
spring:
  application:
    name: audit-service
  
  datasource:
    url: jdbc:postgresql://localhost:5432/audit_db
    username: postgres
    password: postgres
  
  jpa:
    hibernate:
      ddl-auto: validate

server:
  port: 8082

# Service-to-service auth (optional)
audit:
  enabled: true
  retention-days: 365
```

---

## 🚀 Cách Chạy

### Local
```bash
cd backend/audit-service/Nckh-Hi-p
mvn spring-boot:run
```

### Docker
```bash
docker run -p 8082:8082 \
  -e DB_HOST=postgres \
  audit-service:latest
```

---

## 🔗 Liên Kết Với Service Khác

### Ai gọi Audit Service?
```
Auth Service
  └─ POST /api/audits
     (log user creation, login)

Product Service
  └─ POST /api/audits
     (log product changes)

Order Service
  └─ POST /api/audits
     (log order creation, status changes)
```

### Implementation Example
```java
// Trong AuthService.java
@Service
public class AuthService {
    
    @Autowired
    private AuditClient auditClient;
    
    public void register(RegisterRequest request) {
        User user = userService.createUser(request);
        
        // Log to Audit Service
        try {
            auditClient.logAudit(AuditLogRequest.builder()
                .sourceService("auth-service")
                .actionType("CREATE")
                .entityName("USER")
                .entityId(user.getId().toString())
                .userId(user.getId().toString())
                .newValue(objectMapper.writeValueAsString(user))
                .ipAddress(getClientIp())
                .userAgent(getUserAgent())
                .build());
        } catch (Exception e) {
            logger.warn("Failed to log audit: {}", e.getMessage());
        }
    }
}
```

---

## ✅ Checklist

- [ ] Clone repository
- [ ] PostgreSQL chạy, tạo database `audit_db`
- [ ] `mvn spring-boot:run`
- [ ] Test: `curl http://localhost:8082/api/audits`
- [ ] Đọc code: AuditController → AuditService → AuditLogRepository
- [ ] Hiểu cấu trúc Audit Log
- [ ] Kiểm tra database sau khi user login
- [ ] Verify log entry được tạo

---

**Updated**: 2026-01-16  
**Status**: ✅ Production Ready
