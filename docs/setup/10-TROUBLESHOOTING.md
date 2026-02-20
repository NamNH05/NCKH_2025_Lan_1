# 🔧 Phần 10 – Troubleshooting & Lỗi Thường Gặp

---

## 10.1 Lỗi Môi Trường (Environment)

### ❌ **"Java is not recognized"**

**Nguyên nhân:** Java không có trong PATH

**Giải pháp:**
```powershell
# 1. Check if Java installed
java -version

# 2. If error, set JAVA_HOME
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
$env:Path += ";C:\Program Files\Java\jdk-17\bin"

# 3. Verify
java -version
```

**Permanent fix (Windows):**
1. Right-click "This PC" → Properties
2. Advanced system settings → Environment Variables
3. New System Variable:
   - Name: `JAVA_HOME`
   - Value: `C:\Program Files\Java\jdk-17`
4. Edit PATH → Add: `C:\Program Files\Java\jdk-17\bin`
5. Restart PowerShell/CMD

---

### ❌ **"mvn is not recognized"**

**Nguyên nhân:** Maven không có trong PATH

**Giải pháp:**
```powershell
# 1. Set MAVEN_HOME
$env:MAVEN_HOME="C:\Apache\maven-3.9.0"
$env:Path += ";C:\Apache\maven-3.9.0\bin"

# 2. Verify
mvn -v
```

---

### ❌ **"node is not recognized"**

**Nguyên nhân:** Node.js chưa cài hoặc không trong PATH

**Giải pháp:**
```powershell
# 1. Reinstall Node.js từ https://nodejs.org/
# 2. Verify
node -v
npm -v

# 3. Nếu vẫn error, thêm vào PATH:
# C:\Program Files\nodejs
```

---

### ❌ **"Docker daemon is not running"**

**Nguyên nhân:** Docker Desktop chưa start

**Giải pháp:**
```powershell
# 1. Mở Docker Desktop từ Start menu
# 2. Chờ icon Docker hiện bình thường (không loading)
# 3. Verify
docker ps

# 4. Nếu vẫn error, restart Docker
# Click tray icon → Restart
```

---

## 10.2 Lỗi Port (Port Issues)

### ❌ **"Port 3000 already in use"**

**Nguyên nhân:** Có process khác dùng port 3000

**Giải pháp:**

**Windows:**
```powershell
# 1. Tìm process dùng port 3000
netstat -ano | findstr :3000

# 2. Kill process
taskkill /PID <PID> /F

# Hoặc: Đổi port trong api-gateway/.env
PORT=3001
```

**Linux/Mac:**
```bash
# 1. Tìm process
lsof -i :3000

# 2. Kill process
kill -9 <PID>

# Hoặc: Đổi port
export PORT=3001
npm start
```

---

### ❌ **"Port 5432 (PostgreSQL) already in use"**

**Giải pháp:**
```bash
# 1. Kill process or use different port in docker-compose
# Trong docker-compose-all-services.yml:
ports:
  - "5433:5432"  # Map to different port

# 2. Update application.properties
spring.datasource.url=jdbc:postgresql://localhost:5433/nckh_auth

# 3. Restart containers
docker-compose -f docker-compose-all-services.yml down
docker-compose -f docker-compose-all-services.yml up -d
```

---

### ❌ **"Port 8001 already in use"**

**Giải pháp:**
```bash
# 1. Change in pom.xml or application.properties
server.port=8005

# 2. Restart service
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8005"
```

---

## 10.3 Lỗi Database Connection

### ❌ **"Connection refused: localhost:5432"**

**Nguyên nhân:** PostgreSQL container không running hoặc chưa ready

**Giải pháp:**
```bash
# 1. Check container status
docker ps | grep postgres

# 2. Check logs
docker logs postgres-auth-db

# 3. Wait for container to be healthy
docker inspect postgres-auth-db | grep -A 10 Health

# 4. Restart if needed
docker restart postgres-auth-db

# 5. Wait 30 seconds, try again
sleep 30
curl http://localhost:8001/health
```

---

### ❌ **"Access denied for user 'product_user'@'localhost'"**

**Nguyên nhân:** MySQL username/password sai hoặc user chưa được create

**Giải pháp:**
```bash
# 1. Check MySQL logs
docker logs mysql-product-db

# 2. Verify credentials
# Mở MySQL client:
mysql -u root -p -h localhost
# Password: root@123456

# 3. Check user exists
SELECT User FROM mysql.user;

# 4. If not exists, create:
CREATE USER 'product_user'@'%' IDENTIFIED BY 'product@123456';
GRANT ALL PRIVILEGES ON shopquanao.* TO 'product_user'@'%';
FLUSH PRIVILEGES;

# 5. Restart container
docker restart mysql-product-db
```

---

### ❌ **"Cannot connect to SQL Server"**

**Nguyên nhân:** SQL Server container chưa ready

**Giải pháp:**
```bash
# 1. Check container
docker ps | grep sqlserver

# 2. Check logs (may take 60+ seconds to start)
docker logs sqlserver-order-db

# 3. Wait longer (SQL Server takes longer)
sleep 90
docker inspect sqlserver-order-db | grep -A 5 Health

# 4. Verify connection
sqlcmd -S localhost -U sa -P SqlServer@2026 -Q "SELECT 1;"
```

---

### ❌ **"No suitable driver found"**

**Nguyên nhân:** Database driver không cài (Maven không download)

**Giải pháp:**
```bash
# 1. Clear Maven cache
mvn clean

# 2. Verify pom.xml có driver dependency
# Should have: postgresql, mysql-connector-j, mssql-jdbc

# 3. Reinstall
mvn clean install -U

# -U = force update of snapshots
```

---

## 10.4 Lỗi JWT & Authentication

### ❌ **"Invalid token"**

**Nguyên nhân:** JWT secret key không match

**Giải pháp:**
```bash
# 1. Check JWT secret ở 2 chỗ:
# api-gateway/.env
JWT_SECRET=caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a2df52c

# backend/auth-service/Nckh-Lu-n/application.properties
jwt.secret=caef38e2f3667de7631b24840629c0aa60ef53f76a7c3e66d5edd0218a2df52c

# 2. Phải giống nhau! Nếu khác → update
# 3. Restart services
```

---

### ❌ **"Token expired"**

**Nguyên nhân:** JWT token hết hạn (24 giờ)

**Giải pháp:**
```bash
# 1. Login lại để lấy token mới
POST /api/auth/login

# 2. Hoặc tăng JWT expiration (tạm thời dev)
# application.properties
jwt.expiration=604800000  # 7 days

# 3. Restart Auth Service
```

---

### ❌ **"No token provided"**

**Nguyên nhân:** Frontend không gửi JWT token trong Authorization header

**Giải pháp:**
```javascript
// frontend/web-client/src/api/axiosClient.js
// Thêm JWT token vào headers:

const axiosClient = axios.create({
  baseURL: process.env.VITE_API_URL,
});

axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default axiosClient;
```

---

## 10.5 Lỗi CORS

### ❌ **"Access to XMLHttpRequest has been blocked by CORS policy"**

**Nguyên nhân:** CORS origin không được phép

**Giải pháp:**

```bash
# api-gateway/.env
CORS_ORIGIN=http://localhost:5173,http://127.0.0.1:5173

# Nếu frontend chạy trên port khác:
CORS_ORIGIN=http://localhost:3001,http://localhost:5173
```

**api-gateway/server.js:**
```javascript
app.use(cors({
  origin: process.env.CORS_ORIGIN.split(','),
  credentials: true
}));
```

**Restart API Gateway:**
```bash
npm start
```

---

### ❌ **"Preflight request failed"**

**Nguyên nhân:** OPTIONS method không được allowed

**Giải pháp:**
```javascript
// api-gateway/server.js
app.options('*', cors());  // Enable preflight for all routes
```

---

## 10.6 Lỗi Dependencies

### ❌ **"Cannot find module 'express'"**

**Nguyên nhân:** npm dependencies chưa installed

**Giải pháp:**
```bash
cd api-gateway
npm install

# Hoặc install specific package
npm install express
```

---

### ❌ **"Class not found: org.springframework.boot.*...."**

**Nguyên nhân:** Maven dependency chưa download

**Giải pháp:**
```bash
mvn clean install -U
# -U = force update

# Hoặc clear cache
mvn clean
rm -rf ~/.m2/repository
mvn clean install
```

---

### ❌ **"npm ERR! code ERESOLVE"**

**Nguyên nhân:** npm dependency conflict

**Giải pháp:**
```bash
# Force install (bypass version checking)
npm install --legacy-peer-deps

# Hoặc update npm
npm install -g npm@latest
npm install
```

---

## 10.7 Lỗi Docker

### ❌ **"docker: command not found"**

**Giải pháp:**
```bash
# Reinstall Docker Desktop từ https://www.docker.com/products/docker-desktop
# Hoặc dùng Docker CLI riêng
```

---

### ❌ **"Cannot connect to Docker daemon"**

**Giải pháp:**
```bash
# 1. Mở Docker Desktop
# 2. Chờ daemon khởi động (có thể mất 30-60 seconds)
# 3. Verify
docker ps

# Hoặc: Restart Docker daemon
# Linux:
sudo systemctl restart docker

# Mac:
# Mở Docker app → Restart
```

---

### ❌ **"Volume permission denied"**

**Nguyên nhân:** Docker container không có quyền access volume

**Giải pháp:**

**Linux:**
```bash
# Fix permissions
sudo chown -R 1000:1000 /path/to/volume

# Or run as root
sudo docker-compose up -d
```

**Windows:**
```bash
# Usually not an issue, but try:
docker restart <container_name>
```

---

### ❌ **"Disk space low" warning**

**Nguyên nhân:** Docker images/containers chiếm nhiều dung lượng

**Giải pháp:**
```bash
# Remove unused images
docker image prune -a

# Remove unused containers
docker container prune

# Remove unused volumes
docker volume prune

# Clean all at once
docker system prune -a --volumes
```

---

## 10.8 Lỗi Build & Compile

### ❌ **"Build failure: Java 17 not found"**

**Giải pháp:**
```bash
# Set JAVA_HOME
export JAVA_HOME=/path/to/jdk17

# Or tell Maven explicitly
mvn clean install -Dmaven.compiler.source=17 -Dmaven.compiler.target=17
```

---

### ❌ **"Package not found" error**

**Giải pháp:**
```bash
# Clear cache
mvn clean

# Update repositories
mvn clean install -U

# Or manually: Edit pom.xml, check version numbers
```

---

## 10.9 Lỗi Runtime

### ❌ **"NullPointerException in AuthService"**

**Nguyên nhân:** Null object được access

**Giải pháp:**
```bash
# 1. Check logs để tìm stack trace
# 2. Verify database có data
# 3. Add null checks trong code
// if (user != null) { ... }
```

---

### ❌ **"Out of memory" error**

**Nguyên nhân:** Heap memory đầy

**Giải pháp:**
```bash
# Tăng heap size
export JAVA_OPTS="-Xmx1024m"
mvn spring-boot:run

# Hoặc trong pom.xml
<jvmArguments>-Xmx1024m -Xms512m</jvmArguments>
```

---

## 10.10 Performance Issues

### ❌ **"API response slow (> 5 seconds)"**

**Nguyên nhân:** 
- Database chậm
- Network lag
- Business logic vô cùng

**Giải pháp:**
```bash
# 1. Check database
SELECT COUNT(*) FROM product;  # Nhiều records?

# 2. Add indexes
CREATE INDEX idx_product_name ON product(name);

# 3. Enable caching (Redis)
# api-gateway/.env
REDIS_CACHE=true

# 4. Optimize query
# Dùng SELECT fields instead của SELECT *
```

---

### ❌ **"Memory leak - RAM slowly increasing"**

**Giải pháp:**
```bash
# 1. Check for open connections
# application.properties
spring.datasource.hikari.maximum-pool-size=20

# 2. Monitor with jconsole
jconsole

# 3. Force garbage collection
# Usually automatic, but can log in code
System.gc();
```

---

## 10.11 Quick Restart All

Nếu rối hết, làm lại từ đầu:

```bash
# 1. Stop everything
docker-compose -f docker-compose-all-services.yml down -v
# (press Ctrl+C in other terminals)

# 2. Clean build
mvn clean

# 3. Remove node_modules
rm -rf api-gateway/node_modules frontend/web-client/node_modules

# 4. Restart
docker-compose -f docker-compose-all-services.yml up -d
sleep 30

# Install dependencies
cd api-gateway && npm install
cd ../frontend/web-client && npm install

# Build backend
mvn clean install -DskipTests

# Restart services
# (từng terminal như bình thường)
```

---

## 10.12 Support Resources

**Khi cần giúp đỡ:**

1. **Check logs first**
   ```bash
   docker logs <container_name>
   cat logs/application.log  # if exists
   ```

2. **Search error message online**
   - Google: "Spring Boot error X"
   - Stack Overflow
   - GitHub Issues

3. **Try simple cases**
   - Test curl trước (không dùng frontend)
   - Test single service trước (không test toàn hệ thống)

4. **Document findings**
   - Screenshot error
   - Full logs
   - Steps to reproduce
   - System info (Java version, OS, etc.)

---

## 10.13 Troubleshooting Decision Tree

```
System not starting?
├─ Docker containers not running?
│  └─ docker ps, docker logs
├─ Backend services won't start?
│  ├─ Check database connection
│  ├─ Check port availability
│  └─ Check logs for errors
├─ Gateway not working?
│  ├─ Check backend URLs in .env
│  └─ Check CORS settings
├─ Frontend not loading?
│  ├─ Check VITE_API_URL
│  └─ Check browser console

Login not working?
├─ Database has users?
│  └─ Check database directly
├─ Password hashed correctly?
│  └─ Check password encryption
├─ JWT token invalid?
│  └─ Check JWT secret match
└─ CORS blocking request?
   └─ Check gateway CORS_ORIGIN

API call returns 500?
├─ Check backend logs
├─ Check database connection
├─ Check request format (JSON valid?)
└─ Check required parameters sent

Slow performance?
├─ Check database indexes
├─ Check network latency
├─ Check CPU/Memory usage
└─ Add caching (Redis)
```

---

