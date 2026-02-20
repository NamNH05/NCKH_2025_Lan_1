# 🔐 Security Guidelines (Hướng Dẫn Bảo Mật)

Tài liệu chi tiết về chiến lược bảo mật toàn hệ thống E-Commerce Platform.

## 📋 Mục Lục

- [Security Overview](#security-overview)
- [Authentication & Authorization](#authentication--authorization)
- [Data Protection](#data-protection)
- [API Security](#api-security)
- [Infrastructure Security](#infrastructure-security)
- [Incident Response](#incident-response)
- [Security Checklist](#security-checklist)

---

## 🛡️ Security Overview

### Security Strategy

Chiến lược bảo mật của chúng tôi dựa trên:

1. **Defense in Depth**: Multiple layers of security
2. **Least Privilege**: Users chỉ có quyền cần thiết
3. **Security by Design**: Bảo mật từ giai đoạn thiết kế
4. **Secure by Default**: Cấu hình bảo mật mặc định
5. **Regular Audits**: Kiểm tra bảo mật định kỳ

### Security Layers

```
┌────────────────────────────────────────┐
│      Frontend Security Layer           │
│  (HTTPS, CSP, Secure Storage)          │
├────────────────────────────────────────┤
│      API Gateway Security              │
│  (Token Validation, Rate Limiting)     │
├────────────────────────────────────────┤
│      Application Security              │
│  (Input Validation, Authorization)     │
├────────────────────────────────────────┤
│      Database Security                 │
│  (Encryption, Prepared Statements)     │
├────────────────────────────────────────┤
│      Infrastructure Security           │
│  (Firewalls, Docker, Network)          │
└────────────────────────────────────────┘
```

### Compliance Standards

- ✅ **OWASP Top 10**: All controls implemented
- ✅ **PCI DSS**: For payment processing
- ✅ **GDPR**: Data protection compliance
- ✅ **JWT RFC 7519**: Token standards
- ✅ **NIST Cybersecurity**: Framework alignment

---

## 🔑 Authentication & Authorization

### 1. Authentication (Xác Thực)

#### JWT Token Flow

```
┌─────────────────────────────────────────────┐
│                 Frontend                     │
│                                             │
│  1. User submits credentials                │
│  2. Receives JWT token                      │
│  3. Stores token securely                   │
│  4. Sends token with each request           │
└─────────────────────────────────────────────┘
            ↓                          ↑
┌─────────────────────────────────────────────┐
│              API Gateway                     │
│                                             │
│  5. Validates JWT signature                 │
│  6. Checks token expiration                 │
│  7. Extracts user info from token           │
│  8. Passes to backend service               │
└─────────────────────────────────────────────┘
```

#### JWT Token Structure

```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "user-id-123",
  "email": "user@example.com",
  "role": "customer",
  "iat": 1640000000,
  "exp": 1640003600
}

Signature:
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

#### Token Management

| Aspect | Value | Notes |
|--------|-------|-------|
| Token Type | JWT | JSON Web Token |
| Signature | HS256 | HMAC SHA256 |
| Expiration | 1 hour | Access token |
| Refresh Token | 7 days | For renewal |
| Algorithm | Symmetric | HS256 current |

**Best Practices**:
```
✅ Store token in memory or secure cookie
✅ Never store token in localStorage
✅ Use httpOnly flag for cookies
✅ Implement refresh token rotation
✅ Validate token on every request
✅ Revoke token on logout
```

### 2. Authorization (Phân Quyền)

#### Role-Based Access Control (RBAC)

**4 Role chính**:

```
┌─────────────────────────────────────┐
│           User Roles               │
├─────────────────────────────────────┤
│ ADMIN                              │
│ - Manage users                     │
│ - Manage products                  │
│ - View all orders                  │
│ - System configuration             │
├─────────────────────────────────────┤
│ VENDOR                             │
│ - Manage own products              │
│ - View own orders                  │
│ - Analytics for own store          │
├─────────────────────────────────────┤
│ CUSTOMER                           │
│ - Browse products                  │
│ - Create orders                    │
│ - View own orders                  │
│ - Manage address                   │
├─────────────────────────────────────┤
│ GUEST                              │
│ - Browse products (read-only)      │
│ - No account required              │
└─────────────────────────────────────┘
```

#### Permission Matrix

| Resource | ADMIN | VENDOR | CUSTOMER | GUEST |
|----------|-------|--------|----------|-------|
| Users | CRUD | R | R (self) | ✗ |
| Products | CRUD | CRUD (own) | R | R |
| Orders | CRUD | R (own) | CRUD (own) | ✗ |
| Reports | R | R (own) | ✗ | ✗ |
| Settings | CRUD | R | R (self) | ✗ |

#### Implementation Example

```java
// Spring Security Configuration
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers("/api/admin/**").hasRole("ADMIN")
        .antMatchers("/api/vendor/**").hasRole("VENDOR")
        .antMatchers("/api/orders/**").hasAnyRole("CUSTOMER", "ADMIN")
        .antMatchers("/api/products/**").permitAll()
        .anyRequest().authenticated()
        .and()
        .httpBasic()
        .and()
        .csrf().disable();
}
```

---

## 🔒 Data Protection

### 1. Encryption

#### In Transit (TLS/SSL)

```
Requirement: HTTPS for all communication
┌──────────────────────────────────────┐
│    Frontend (Client Browser)          │
│                                      │
│  https://example.com/api/...        │
└──────────────────────────────────────┘
            ↓↑ TLS 1.2+
┌──────────────────────────────────────┐
│    Backend API Gateway                │
│                                      │
│  All traffic encrypted                │
└──────────────────────────────────────┘
```

**Configuration**:
```yaml
# SSL/TLS Setup
Min TLS Version: 1.2
Recommended: 1.3
Cipher Suite: Modern browsers only
Certificate: Valid, not self-signed
Duration: Yearly renewal
```

#### At Rest (Database)

**Sensitive Fields Encryption**:

```sql
-- Password: Bcrypt with salt
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- Bcrypt hashed
    phone VARCHAR(20),               -- Encrypted
    ssn VARCHAR(20)                  -- Encrypted
);
```

**Encryption Implementation**:
```
Passwords:
  - Algorithm: Bcrypt
  - Salt Rounds: 12
  - Hash Length: 60 characters

Sensitive Data:
  - Algorithm: AES-256-GCM
  - Key Management: Vault (e.g., HashiCorp Vault)
  - IV (Initialization Vector): Random per record
```

### 2. Password Policy

#### Requirements

```
Minimum Length: 12 characters
Complexity:
  ✓ Uppercase letters (A-Z)
  ✓ Lowercase letters (a-z)
  ✓ Numbers (0-9)
  ✓ Special characters (!@#$%^&*)

Expiration: 90 days
History: Cannot reuse last 5 passwords
Lockout: 5 failed attempts → 30 min lock
```

#### Password Hashing

```java
// Spring Security example
PasswordEncoder encoder = new BCryptPasswordEncoder(12);
String hashedPassword = encoder.encode(rawPassword);

// Verification
boolean matches = encoder.matches(rawPassword, hashedPassword);
```

### 3. Session Management

| Feature | Value | Notes |
|---------|-------|-------|
| Session Duration | 30 minutes | Activity timeout |
| Remember Me | 7 days | Optional |
| Concurrent Sessions | 1 active | Per user |
| Session Token | Secure cookie | httpOnly, Secure flags |
| CSRF Protection | Enabled | For state-changing requests |

---

## 🌐 API Security

### 1. Input Validation

#### Validation Layers

```
Frontend (Client-side)
    ↓ (Not trusted!)
API Gateway (Format validation)
    ↓
Service Layer (Business logic validation)
    ↓
Database Layer (Schema validation)
```

#### Examples

**Email Validation**:
```java
@Email(message = "Email must be valid")
@NotNull(message = "Email cannot be null")
private String email;

// Regex pattern
^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
```

**Product ID Validation**:
```java
@Min(value = 1, message = "Product ID must be greater than 0")
@NotNull(message = "Product ID cannot be null")
private Integer productId;
```

**String Sanitization**:
```java
// Remove potential XSS attacks
String sanitized = HtmlUtils.htmlEscape(input);
```

### 2. SQL Injection Prevention

#### Vulnerable Code ❌

```java
// NEVER DO THIS!
String query = "SELECT * FROM users WHERE email = '" + email + "'";
ResultSet rs = statement.executeQuery(query);
```

#### Safe Code ✅

```java
// Use Prepared Statements
String query = "SELECT * FROM users WHERE email = ?";
PreparedStatement pstmt = connection.prepareStatement(query);
pstmt.setString(1, email);
ResultSet rs = pstmt.executeQuery();
```

#### Hibernate/JPA Example

```java
// Safe with Hibernate
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}

// QueryDSL alternative
QUser user = QUser.user;
User result = queryFactory
    .selectFrom(user)
    .where(user.email.eq(email))
    .fetchOne();
```

### 3. CORS Configuration

#### Allowed Origins

```yaml
# API Gateway CORS Setup
cors:
  allowedOrigins:
    - https://example.com
    - https://app.example.com
  allowedMethods:
    - GET
    - POST
    - PUT
    - DELETE
  allowedHeaders:
    - Content-Type
    - Authorization
  allowCredentials: true
  maxAge: 3600
```

### 4. Rate Limiting

#### Configuration

```
Limits per IP/User:
  - Global: 10,000 requests/hour
  - Per Endpoint: Varies
  - Auth: 5 attempts/15 minutes
  - API: 100 requests/minute
```

#### Implementation

```javascript
// Express middleware
const rateLimit = require('express-rate-limit');

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000,  // 15 minutes
  max: 100,                   // limit each IP to 100 requests per windowMs
  message: 'Too many requests from this IP'
});

app.use('/api/', limiter);
```

---

## 🔧 Infrastructure Security

### 1. Docker Security

#### Image Security

```dockerfile
# Use minimal base images
FROM openjdk:11-jre-slim

# Run as non-root user
RUN useradd -m -u 1000 appuser
USER appuser

# Scan for vulnerabilities
# docker scan <image>

# Use specific versions (not 'latest')
FROM postgres:13.4-alpine
```

#### Container Security

```bash
# Run with read-only filesystem
docker run --read-only \
           --tmpfs /tmp \
           --tmpfs /var/tmp \
           myapp:1.0

# Use no-new-privileges flag
docker run --security-opt=no-new-privileges:true myapp:1.0

# Resource limits
docker run -m 512m \
           --cpus="1.0" \
           myapp:1.0
```

### 2. Network Security

#### Docker Compose Network

```yaml
version: '3.8'
services:
  api-gateway:
    networks:
      - frontend
      - backend
  auth-service:
    networks:
      - backend
  postgres:
    networks:
      - backend

networks:
  frontend:
    driver: bridge
  backend:
    driver: bridge
    internal: true  # No internet access
```

### 3. Database Security

#### PostgreSQL Security

```sql
-- Create non-root user
CREATE USER app_user WITH PASSWORD 'strong_password';

-- Grant specific permissions
GRANT CONNECT ON DATABASE app_db TO app_user;
GRANT USAGE ON SCHEMA public TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;

-- Disable default privileges
ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE ALL ON TABLES FROM PUBLIC;

-- Enable SSL
ssl = on
ssl_cert_file = '/etc/ssl/certs/server.crt'
ssl_key_file = '/etc/ssl/private/server.key'
```

---

## 📋 Incident Response

### 1. Security Incident Procedure

```
Detection (Automated/Manual)
    ↓
Verification & Assessment
    ↓
Containment (Limit damage)
    ↓
Root Cause Analysis
    ↓
Remediation (Fix issue)
    ↓
Communication (Notify affected users)
    ↓
Review & Prevent (Improve defenses)
```

### 2. Response Times

| Severity | Response | Resolution |
|----------|----------|-----------|
| Critical | 15 min | 1 hour |
| High | 1 hour | 4 hours |
| Medium | 4 hours | 24 hours |
| Low | 1 day | 7 days |

### 3. Incident Notification

```
Step 1: Internal Alert
  - Notify Security Team
  - Create incident ticket

Step 2: Assessment
  - Determine scope
  - Identify affected systems
  - Assess data risk

Step 3: User Notification
  - For data breaches: Notify within 72 hours (GDPR)
  - For outages: Update status page
  - For vulnerabilities: Release patches
```

---

## ✅ Security Checklist

### Development Phase

- [ ] Input validation on all endpoints
- [ ] SQL injection prevention (prepared statements)
- [ ] XSS prevention (output encoding)
- [ ] CSRF protection enabled
- [ ] Authentication implemented
- [ ] Authorization checks in place
- [ ] Sensitive data encrypted
- [ ] Error handling without leaking info
- [ ] Logging sensitive activities
- [ ] Security headers configured

### Testing Phase

- [ ] OWASP Top 10 testing completed
- [ ] Penetration testing done
- [ ] Dependency vulnerability scan
- [ ] SQL injection tests
- [ ] XSS payload testing
- [ ] CORS policy testing
- [ ] Rate limiting verification
- [ ] Authentication bypass attempts
- [ ] Privilege escalation testing
- [ ] Data leakage testing

### Deployment Phase

- [ ] HTTPS/TLS enabled (1.2+)
- [ ] Security headers configured
  - X-Content-Type-Options: nosniff
  - X-Frame-Options: DENY
  - X-XSS-Protection: 1; mode=block
  - Strict-Transport-Security
  - Content-Security-Policy
- [ ] Database credentials secured
- [ ] API keys encrypted
- [ ] Logging enabled
- [ ] Backup configured
- [ ] Incident response plan ready
- [ ] Monitoring alerts configured

### Post-Deployment

- [ ] Monitor security logs
- [ ] Regular security audits (quarterly)
- [ ] Dependency updates (monthly)
- [ ] Penetration testing (annual)
- [ ] Incident response drills
- [ ] Security training for team
- [ ] Compliance verification
- [ ] Disaster recovery testing

---

## 📞 Security Resources

### Internal Documentation

- [Architecture Security](./architecture/README.md#-security-layers)
- [Getting Started](../../GETTING_STARTED.md)
- [API Gateway Security](../../api-gateway/README.md)

### External Resources

- **OWASP Top 10**: https://owasp.org/www-project-top-ten/
- **JWT Best Practices**: https://tools.ietf.org/html/rfc7519
- **Spring Security**: https://spring.io/projects/spring-security
- **Docker Security**: https://docs.docker.com/engine/security/
- **GDPR Compliance**: https://gdpr-info.eu/

### Security Tools

- **Dependency Check**: npm audit, maven dependency-check
- **SAST**: SonarQube, Checkmarx
- **DAST**: OWASP ZAP, Burp Suite
- **Container Scanning**: Trivy, Aqua, Twistlock

---

## 🔐 Secure Development Guidelines

### Code Review Checklist

```
✓ No hardcoded credentials
✓ No SQL injection vulnerabilities
✓ Proper error handling
✓ Input validation present
✓ Authorization checks implemented
✓ Logging doesn't leak sensitive data
✓ Secure default configurations
✓ OWASP best practices followed
```

### Common Vulnerabilities to Avoid

| Vulnerability | Prevention |
|--------------|-----------|
| SQL Injection | Prepared statements |
| XSS | Output encoding |
| CSRF | CSRF tokens |
| Path Traversal | Input validation |
| XXE | Disable XML features |
| Broken Auth | Strong session management |
| Exposed Data | Encryption + access control |
| CORS Misconfiguration | Whitelist origins |

---

## 📊 Security Metrics

### Key Metrics to Track

- **Vulnerability Count**: Target: 0 critical
- **Response Time**: Target: < 24 hours
- **Patch Management**: Target: 100% within 30 days
- **Security Training**: Target: 100% annually
- **Incident Rate**: Target: < 1 per quarter
- **Compliance Score**: Target: > 95%

---

## 📞 Liên Hệ & Báo Cáo Lỗ Hổng

Phát hiện lỗ hổng bảo mật?

**Vui lòng báo cáo qua:**
- Email: security@example.com
- GitHub Security Advisory
- **Không công bố công khai** cho đến khi được patch

**Responsible Disclosure**:
- 30 ngày để fix
- 90 ngày để patch
- Sau đó có thể công bố

---

**Last Updated**: 16/01/2026  
**Version**: 1.0  
**Classification**: Internal Use



