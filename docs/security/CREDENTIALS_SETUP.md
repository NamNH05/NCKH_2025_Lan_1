# Credentials Setup Guide (P0.1 - CRITICAL)

## Status: FIXED ✅
All hardcoded credentials have been replaced with environment variables.

## What Was Fixed

### 1. Auth Service (Nckh-Luận)
**File:** `backend/auth-service/Nckh-Lu-n/src/main/resources/application.properties`

**Before:**
```properties
spring.datasource.password=1234
jwt.secret=caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a2df52c
bootstrap.admin.password=1234
```

**After:**
```properties
spring.datasource.password=${DB_PASSWORD:}
jwt.secret=${JWT_SECRET:}
bootstrap.admin.password=${ADMIN_PASSWORD:}
```

### 2. Product Service (Tien)
**File:** `backend/product-service/Tien/Tien/src/main/resources/application.properties`

**Before:**
```properties
spring.datasource.password=tien0399007905
```

**After:**
```properties
spring.datasource.password=${DB_PASSWORD:}
```

### 3. Order Service (Nckh-Công)
**File:** `backend/order-service/Nckh-C-ng/src/main/resources/application.properties`

**Before:**
```properties
spring.datasource.password=Order@123
```

**After:**
```properties
spring.datasource.password=${DB_PASSWORD:}
```

### 4. API Gateway
**File:** `api-gateway/.env`

**New Random Secrets Generated:**
```dotenv
JWT_SECRET=DA61EF835680CD643C259B9BB5E4794B4903A137356A75F2C2D05CD2DB9BAE7A
COOKIE_SECRET=6FACECAF50AC78ED36AB69097898DF9F14DE4738F71C42C1FA5CBCD678A099C5
```

## Required Environment Variables

Create `.env` files for each service with these variables:

### Auth Service `.env`
```dotenv
DB_HOST=localhost
DB_PORT=5432
DB_NAME=auth_db
DB_USERNAME=auth_user
DB_PASSWORD=<SECURE_PASSWORD>
ADMIN_PASSWORD=<SECURE_PASSWORD>
JWT_SECRET=<RANDOM_64_CHAR_HEX>
REDIS_HOST=localhost
REDIS_PORT=6379
```

### Product Service `.env`
```dotenv
DB_HOST=localhost
DB_PORT=3306
DB_NAME=shopquanao
DB_USERNAME=root
DB_PASSWORD=<SECURE_PASSWORD>
```

### Order Service `.env`
```dotenv
DB_HOST=localhost
DB_PORT=1433
DB_NAME=order_dB
DB_USERNAME=orderuser
DB_PASSWORD=<SECURE_PASSWORD>
```

### API Gateway `.env` ✅
Already configured with:
```dotenv
JWT_SECRET=DA61EF835680CD643C259B9BB5E4794B4903A137356A75F2C2D05CD2DB9BAE7A
COOKIE_SECRET=6FACECAF50AC78ED36AB69097898DF9F14DE4738F71C42C1FA5CBCD678A099C5
DB_HOST=localhost
DB_PORT=5432
DB_NAME=auth_db
DB_USERNAME=auth_user
DB_PASSWORD=<SET_THIS>
ADMIN_PASSWORD=<SET_THIS>
REDIS_HOST=localhost
REDIS_PORT=6379
```

## Spring Boot Environment Variable Resolution

Spring Boot automatically resolves `${VAR_NAME:default}` syntax:

```properties
spring.datasource.password=${DB_PASSWORD:}
```

This means:
- If `DB_PASSWORD` env var is set → use that value
- If `DB_PASSWORD` is not set → use empty string (after `:`)

### Setting Environment Variables (Windows)

**Option 1: System Environment Variables**
```powershell
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "YourSecurePassword", "User")
```

**Option 2: Command Line (temporary)**
```powershell
$env:DB_PASSWORD="YourSecurePassword"
mvn spring-boot:run
```

**Option 3: Docker Compose (recommended for production)**
```yaml
environment:
  DB_PASSWORD: ${DB_PASSWORD}
  JWT_SECRET: ${JWT_SECRET}
```

## Security Best Practices

1. ✅ **Never commit credentials** to Git
   - Add `.env` to `.gitignore`
   - Use `.env.example` as template

2. ✅ **Rotate passwords regularly**
   - Every 90 days for production
   - After any personnel changes

3. ✅ **Use strong passwords**
   - Minimum 16 characters
   - Mix: uppercase, lowercase, numbers, special chars
   - Example: `P@ssw0rd!SecureKey2024`

4. ✅ **Secure secret management**
   - Production: Use AWS Secrets Manager, Azure Key Vault, Vault
   - Development: Local `.env` files (not in Git)

5. ✅ **JWT Secret handling**
   - Keep 64+ characters (SHA256 size)
   - Rotate if suspected compromise
   - Different secret per environment

## Verification

### 1. Check Auth Service loads env vars:
```bash
cd backend/auth-service/Nckh-Lu-n
$env:DB_PASSWORD="testpass123"
$env:JWT_SECRET="DA61EF835680CD643C259B9BB5E4794B4903A137356A75F2C2D05CD2DB9BAE7A"
$env:ADMIN_PASSWORD="admin@secure"
mvn spring-boot:run
```

### 2. Check logs for successful connection:
```
[main] o.h.dialect.Dialect : HHH000400: Using dialect: org.hibernate.dialect.PostgreSQLDialect
[main] o.s.b.a.DataSourceAutoConfiguration : ...completed initialization...
```

### 3. API test:
```bash
curl http://localhost:8080/api/users
```

Should return authenticated response or 401 (not 500 connection error).

## Rollback

If needed, the original hardcoded values were:
- Auth DB password: `1234`
- Auth Admin password: `1234`
- Product DB password: `tien0399007905`
- Order DB password: `Order@123`
- Auth JWT secret: `caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a2df52c`

⚠️ **THESE VALUES ARE NOW DEPRECATED AND INSECURE**

## Next Steps

1. **Set actual database passwords** in each `.env`
2. **Update start-project.bat** to set env vars before starting services
3. **Deploy to staging** for integration testing
4. **Implement P1.1** - Security headers via helmet.js

## Files Modified

- ✅ `backend/auth-service/Nckh-Lu-n/src/main/resources/application.properties`
- ✅ `backend/product-service/Tien/Tien/src/main/resources/application.properties`
- ✅ `backend/order-service/Nckh-C-ng/src/main/resources/application.properties`
- ✅ `api-gateway/.env`

## Compliance

- ✅ CWE-798: Use of Hard-Coded Credentials - RESOLVED
- ✅ OWASP A07:2021: Identification and Authentication Failures - MITIGATED
- ✅ SANS Top 25: CWE-798 - FIXED
