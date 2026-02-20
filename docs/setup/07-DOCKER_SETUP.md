# 🐳 Phần 7 – Docker & Container Setup

---

## 7.1 Tổng Quan Docker

Docker cho phép chạy databases trong **containers** (lightweight virtual machines) thay vì cài direct trên máy.

**Lợi ích:**
- ✅ Không cần cài PostgreSQL, MySQL, SQL Server trên máy
- ✅ Isolate từ hệ thống
- ✅ Dễ cleanup (xóa container, mọi thứ đi)
- ✅ Portable (chạy được trên Windows/Mac/Linux)
- ✅ Dùng docker-compose để orchestrate multiple containers

---

## 7.2 Docker Concepts

| Khái Niệm | Ý Nghĩa |
|-----------|--------|
| **Image** | Template (e.g., `postgres:15-alpine`) |
| **Container** | Running instance của image |
| **Port Mapping** | `5432:5432` = container port 5432 → host port 5432 |
| **Volume** | Persistent storage (dữ liệu không bị xóa) |
| **Network** | Containers có thể gọi nhau qua network |

---

## 7.3 File: docker-compose-all-services.yml

**Vị trí:** `docker-compose-all-services.yml` (ở root project)

**Chức năng:** Định nghĩa tất cả containers (databases, cache)

```yaml
version: '3.8'

services:
  # PostgreSQL - Auth & Audit Services
  postgres-auth-db:
    image: postgres:15-alpine
    container_name: postgres-auth-db
    environment:
      POSTGRES_USER: auth_user
      POSTGRES_PASSWORD: auth@123456
      POSTGRES_DB: nckh_auth
    ports:
      - "5432:5432"
    volumes:
      - postgres_auth_data:/var/lib/postgresql/data
      - ./database/postgres-init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - microservices-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U auth_user"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  # MySQL - Product Service
  mysql-product-db:
    image: mysql:8.0-alpine
    container_name: mysql-product-db
    environment:
      MYSQL_ROOT_PASSWORD: root@123456
      MYSQL_DATABASE: shopquanao
      MYSQL_USER: product_user
      MYSQL_PASSWORD: product@123456
    ports:
      - "3306:3306"
    volumes:
      - mysql_product_data:/var/lib/mysql
      - ./database/mysql-init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - microservices-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-u", "product_user", "-pproduct@123456"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  # SQL Server - Order Service
  sqlserver-order-db:
    image: mcr.microsoft.com/mssql/server:2022-latest
    container_name: sqlserver-order-db
    environment:
      SA_PASSWORD: "SqlServer@2026"
      ACCEPT_EULA: "Y"
      MSSQL_PID: "Developer"
    ports:
      - "1433:1433"
    volumes:
      - sqlserver_order_data:/var/opt/mssql
    networks:
      - microservices-network
    healthcheck:
      test: ["CMD", "/opt/mssql-tools18/bin/sqlcmd", "-S", "localhost", "-U", "sa", "-P", "SqlServer@2026", "-Q", "SELECT 1"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  # Redis - Cache
  redis-cache:
    image: redis:7-alpine
    container_name: redis-cache
    command: redis-server --appendonly yes
    ports:
      - "6379:6379"
    volumes:
      - redis_cache_data:/data
    networks:
      - microservices-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

volumes:
  postgres_auth_data:
  mysql_product_data:
  sqlserver_order_data:
  redis_cache_data:

networks:
  microservices-network:
    driver: bridge
```

**Mô tả các field quan trọng:**

```yaml
services:
  postgres-auth-db:           # Tên service (dùng trong network)
    image: postgres:15-alpine # Image từ Docker Hub
    container_name: ...       # Tên container
    
    environment:              # Biến môi trường
      POSTGRES_USER: auth_user
      POSTGRES_PASSWORD: auth@123456
      POSTGRES_DB: nckh_auth
      
    ports:                    # Port mapping
      - "5432:5432"          # Host port : Container port
      
    volumes:                  # Bind mounts & volumes
      - postgres_auth_data:/var/lib/postgresql/data  # Persistent storage
      - ./database/postgres-init.sql:/docker-entrypoint-initdb.d/init.sql  # Init script
      
    networks:                 # Networks to join
      - microservices-network
      
    healthcheck:              # Check if container is healthy
      test: ["CMD-SHELL", "pg_isready -U auth_user"]
      interval: 10s           # Check every 10s
      timeout: 5s             # Wait 5s for response
      retries: 5              # Fail after 5 retries
      
    restart: unless-stopped   # Auto-restart if crashes

volumes:                      # Define named volumes
  postgres_auth_data:        # Persistent storage for PostgreSQL

networks:                     # Define networks
  microservices-network:      # Allow containers to communicate
    driver: bridge
```

---

## 7.4 Dockerfile untuk Services (Optional)

Nếu muốn container các backend services (Java), tạo Dockerfiles:

### **backend/auth-service/Nckh-Lu-n/Dockerfile**

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/auth-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### **frontend/web-client/Dockerfile**

```dockerfile
# Build stage
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

---

## 7.5 Lệnh Docker Cơ Bản

### ✅ **Start All Containers**

```bash
# Từ root directory (nơi docker-compose-all-services.yml)
docker-compose -f docker-compose-all-services.yml up -d

# Flags:
# -d = detached (chạy background)
# -f = specify compose file
```

### ✅ **View Running Containers**

```bash
docker ps

# Output:
# CONTAINER ID  IMAGE              STATUS
# abc123...     postgres:15        Up 2 minutes
# def456...     mysql:8.0          Up 2 minutes
# ...
```

### ✅ **View Logs**

```bash
# All containers
docker-compose -f docker-compose-all-services.yml logs

# Specific container
docker logs postgres-auth-db

# Follow logs (realtime)
docker logs -f postgres-auth-db

# Last 50 lines
docker logs --tail 50 postgres-auth-db
```

### ✅ **Stop Containers**

```bash
# Stop all
docker-compose -f docker-compose-all-services.yml down

# Stop specific
docker stop postgres-auth-db

# Stop all
docker stop $(docker ps -q)
```

### ✅ **Restart Containers**

```bash
# Restart all
docker-compose -f docker-compose-all-services.yml restart

# Restart specific
docker restart postgres-auth-db
```

### ✅ **Remove Containers (⚠️ Careful!)**

```bash
# Remove all containers
docker-compose -f docker-compose-all-services.yml down

# Remove specific
docker rm postgres-auth-db  # Must be stopped first

# Remove & delete volumes (CAREFUL - data loss!)
docker-compose -f docker-compose-all-services.yml down -v
```

### ✅ **Inspect Container**

```bash
# View detailed info
docker inspect postgres-auth-db

# View environment variables
docker inspect -f '{{json .Config.Env}}' postgres-auth-db
```

### ✅ **Execute Command Inside Container**

```bash
# Interactive shell
docker exec -it postgres-auth-db /bin/sh

# Run command
docker exec postgres-auth-db psql -U auth_user -d nckh_auth -c "SELECT 1;"
```

---

## 7.6 Health Checks & Dependencies

Trong docker-compose, `healthcheck` xác nhận container sẵn sàng:

```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U auth_user"]
  interval: 10s      # Check every 10 seconds
  timeout: 5s        # Wait max 5 seconds
  retries: 5         # Try 5 times before mark unhealthy
  start_period: 40s  # Wait 40s before first check
```

Để chờ một service khác ready:

```yaml
services:
  my-app:
    depends_on:
      postgres-auth-db:
        condition: service_healthy  # Wait until healthy
```

---

## 7.7 Network Communication

Services trong network có thể gọi nhau dùng container name:

```yaml
services:
  postgres-auth-db:      # Container name
    container_name: postgres-auth-db
    networks:
      - microservices-network

  app:
    networks:
      - microservices-network
    # Từ app, có thể gọi: postgres-auth-db:5432
```

Ở Java:
```properties
# Thay localhost bằng container name khi chạy trong Docker
spring.datasource.url=jdbc:postgresql://postgres-auth-db:5432/nckh_auth
```

---

## 7.8 Volumes: Persistent Storage

```yaml
volumes:
  postgres_auth_data:            # Named volume
    - postgres_auth_data:/var/lib/postgresql/data

  bind_mount:                    # Bind mount (host directory)
    - ./database:/app/database
```

**Named Volume:**
- Managed by Docker
- Data persists even after container removal
- View: `docker volume ls`
- Cleanup: `docker volume prune`

**Bind Mount:**
- Direct link to host directory
- Changes visible immediately
- Good for development

---

## 7.9 Scaling & Performance

### **Increase Connection Pool**

```yaml
mysql-product-db:
  environment:
    MYSQL_MAX_CONNECTIONS: 100
```

### **Limit Resources**

```yaml
postgres-auth-db:
  deploy:
    resources:
      limits:
        cpus: '0.5'       # 50% of 1 CPU
        memory: 512M      # 512 MB RAM
      reservations:
        cpus: '0.25'
        memory: 256M
```

### **Auto-restart Policy**

```yaml
restart: unless-stopped  # Restart unless explicitly stopped
restart: always          # Always restart
restart: on-failure      # Restart on crash
```

---

## 7.10 Troubleshooting Docker

### ❌ **"Port already in use"**

```bash
# Find process using port
lsof -i :5432           # Mac/Linux
netstat -ano | findstr :5432  # Windows

# Kill process
kill -9 <PID>           # Mac/Linux
taskkill /PID <PID>     # Windows

# Or change port in docker-compose
ports:
  - "5433:5432"  # Map to different port
```

---

### ❌ **"Connection refused"**

```bash
# Check if container running
docker ps

# Check logs
docker logs postgres-auth-db

# Restart container
docker restart postgres-auth-db

# Check health
docker inspect postgres-auth-db | grep -A 10 Health
```

---

### ❌ **"Volume permission denied"**

```bash
# Fix permissions (Linux)
sudo chown -R $(id -u):$(id -g) /path/to/volume

# Or run Docker as root
sudo docker-compose up -d
```

---

## 7.11 Docker Best Practices

- ✅ Use specific image tags (not `latest`)
- ✅ Run containers as non-root user (when possible)
- ✅ Use .dockerignore (similar to .gitignore)
- ✅ Keep images small (use alpine)
- ✅ Don't store secrets in images (use env vars)
- ✅ Use health checks
- ✅ Set memory limits
- ✅ Use named volumes for persistent data
- ❌ Don't use `docker run` in production (use orchestration)
- ❌ Don't run `sudo docker` (setup Docker user group)

---

## 7.12 Checklist Docker Setup

- [ ] Docker Desktop installed & running
- [ ] docker-compose-all-services.yml exists
- [ ] All volumes defined
- [ ] All networks defined
- [ ] All health checks configured
- [ ] Ports don't conflict
- [ ] Init scripts exist (postgres-init.sql, etc.)
- [ ] Can run: `docker-compose -f docker-compose-all-services.yml up -d`
- [ ] All containers running: `docker ps` shows 4+ containers
- [ ] Can connect to databases

---

