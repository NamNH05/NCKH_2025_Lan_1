# 📚 DOCUMENTATION MASTER GUIDE

> **Tài liệu hướng dẫn toàn diện để khởi động dự án NCKH từ con số 0**

---

## 🎯 Bắt Đầu Nhanh (Quick Start)

**Dành cho người máy tính nhanh tay:**

```bash
# 1. Khởi động databases (Docker)
docker-compose -f docker-compose-all-services.yml up -d
sleep 60

# 2. Cài dependencies
cd backend/auth-service/Nckh-Lu-n && mvn clean install
cd ../../../api-gateway && npm install
cd ../frontend/web-client && npm install

# 3. Chạy services (4 terminals)
# Terminal 1: cd backend/auth-service/Nckh-Lu-n && mvn spring-boot:run
# Terminal 2: cd backend/product-service/Tien/Tien && mvn spring-boot:run
# Terminal 3: cd backend/order-service/Nckh-C-ng && mvn spring-boot:run
# Terminal 4: cd api-gateway && npm start
# Terminal 5: cd frontend/web-client && npm run dev

# 4. Mở browser: http://localhost:5173
```

**Tài khoản test:**
- Admin: `sysadmin` / `1234`
- User: `23810310082` / `123456`

---

## 📖 Tài Liệu Chi Tiết

### **Bắt Buộc Đọc Trước**
1. **[01-PROJECT_OVERVIEW.md](01-PROJECT_OVERVIEW.md)** ← Start từ đây!
   - Mô tả dự án làm gì
   - Kiến trúc hệ thống
   - Luồng tương tác

2. **[02-REQUIREMENTS.md](02-REQUIREMENTS.md)**
   - Cài Java, Maven, Node.js, Docker
   - Kiểm tra environment setup

3. **[03-CODE_STRUCTURE.md](03-CODE_STRUCTURE.md)**
   - Cấu trúc folder & file
   - Giải thích mỗi service
   - Thứ tự đọc code hợp lý

---

### **Setup & Configuration**
4. **[04-ENV_CONFIGURATION.md](04-ENV_CONFIGURATION.md)**
   - File .env là gì
   - Biến môi trường cần thiết
   - Cách tạo .env từ .env.example
   - Dev vs Production config (DevTunnel, localhost)

5. **[05-DATABASE_SETUP.md](05-DATABASE_SETUP.md)**
   - Database credentials
   - SQL init scripts location: **[/database/sql-scripts](../../database/sql-scripts/README.md)** ✅
   - Thủ công setup nếu Docker không hoạt động

6. **[06-DEPENDENCIES.md](06-DEPENDENCIES.md)**
   - Cài Maven dependencies
   - Cài npm packages
   - Mô tả từng dependency quan trọng

7. **[07-DOCKER_SETUP.md](07-DOCKER_SETUP.md)**
   - Docker concepts
   - docker-compose.yml giải thích
   - Docker commands

---

### **Chạy & Test Hệ Thống**
8. **[08-STARTUP_GUIDE.md](08-STARTUP_GUIDE.md)** ← Run theo đây!
   - Thứ tự khởi động services
   - Terminal layout
   - Verify mỗi bước
   - Startup script tự động

9. **[09-TESTING_VERIFICATION.md](09-TESTING_VERIFICATION.md)**
   - API test bằng Postman
   - Test scenarios
   - Health checks
   - Performance testing

10. **[10-TROUBLESHOOTING.md](10-TROUBLESHOOTING.md)** ← Khi có lỗi!
    - Lỗi thường gặp
    - Cách fix từng loại lỗi
    - Decision tree

---

## 🔗 File Mẫu (.env.example)

Tất cả `.env.example` files được tạo sẵn:

```
api-gateway/.env.example
frontend/web-client/.env.example
backend/auth-service/Nckh-Lu-n/src/main/resources/application.properties.example
backend/product-service/Tien/Tien/src/main/resources/application.properties.example
backend/order-service/Nckh-C-ng/src/main/resources/application.properties.example
backend/audit-service/Nckh-Hi-p/src/main/resources/application.properties.example
```

**Cách sử dụng:**
1. Copy `.env.example` → `.env` (từng service)
2. Điền mật khẩu thực tế vào `.env`
3. **KHÔNG commit `.env` file** (giữ bí mật!)

---

## 🎯 Thứ Tự Làm Từng Bước

### **Ngày 1: Preparation (1-2 giờ)**
- [ ] Đọc [01-PROJECT_OVERVIEW.md](01-PROJECT_OVERVIEW.md)
- [ ] Đọc [02-REQUIREMENTS.md](02-REQUIREMENTS.md)
- [ ] Cài Java 17, Maven, Node.js, Docker
- [ ] Kiểm tra: `java -version`, `mvn -version`, `node -v`, `docker -v`

### **Ngày 2: Setup (3-4 giờ)**
- [ ] Đọc [03-CODE_STRUCTURE.md](03-CODE_STRUCTURE.md)
- [ ] Đọc [04-ENV_CONFIGURATION.md](04-ENV_CONFIGURATION.md)
- [ ] Tạo .env files từ .env.example
- [ ] Đọc [05-DATABASE_SETUP.md](05-DATABASE_SETUP.md)
- [ ] Đọc [06-DEPENDENCIES.md](06-DEPENDENCIES.md)
- [ ] Cài dependencies: `mvn clean install`, `npm install`

### **Ngày 3: Startup (1-2 giờ)**
- [ ] Đọc [07-DOCKER_SETUP.md](07-DOCKER_SETUP.md)
- [ ] Khởi động Docker: `docker-compose up -d`
- [ ] Đọc [08-STARTUP_GUIDE.md](08-STARTUP_GUIDE.md)
- [ ] Chạy tất cả services
- [ ] Đọc [09-TESTING_VERIFICATION.md](09-TESTING_VERIFICATION.md)
- [ ] Test với Postman hoặc curl

### **Ngày 4+: Development**
- [ ] Đọc source code từng service
- [ ] Tạo task / bug fix / feature
- [ ] Dùng [10-TROUBLESHOOTING.md](10-TROUBLESHOOTING.md) khi cần
- [ ] Reference [09-TESTING_VERIFICATION.md](09-TESTING_VERIFICATION.md) để test

---

## 🎓 Learning Path

### **Beginner (Tuần 1)**
Chỉ cần biết:
- Dự án là gì (01)
- Cài environment (02)
- Cấu trúc folder (03)
- Chạy hệ thống (08)
- Test cơ bản (09)

### **Intermediate (Tuần 2-3)**
Cần hiểu:
- Từng service là gì (03)
- Config thế nào (04, 05)
- Dependency là gì (06)
- Docker hoạt động như thế nào (07)
- API test thực tế (09)

### **Advanced (Tuần 4+)**
Cần nắm vững:
- Từng service chi tiết (read source code)
- Spring Boot internals
- JWT & Security
- Database optimization
- Docker & Kubernetes
- Troubleshooting phức tạp (10)

---

## 🔍 Nhanh Tìm Giải Đáp

**"Mình muốn biết..."**

| Câu Hỏi | File |
|--------|------|
| ...dự án là gì? | [01-PROJECT_OVERVIEW.md](01-PROJECT_OVERVIEW.md) |
| ...cài gì trước? | [02-REQUIREMENTS.md](02-REQUIREMENTS.md) |
| ...file nào là gì? | [03-CODE_STRUCTURE.md](03-CODE_STRUCTURE.md) |
| ...biến môi trường? | [04-ENV_CONFIGURATION.md](04-ENV_CONFIGURATION.md) |
| ...database setup? | [05-DATABASE_SETUP.md](05-DATABASE_SETUP.md) |
| ...dependencies? | [06-DEPENDENCIES.md](06-DEPENDENCIES.md) |
| ...Docker là gì? | [07-DOCKER_SETUP.md](07-DOCKER_SETUP.md) |
| ...chạy thế nào? | [08-STARTUP_GUIDE.md](08-STARTUP_GUIDE.md) |
| ...test API? | [09-TESTING_VERIFICATION.md](09-TESTING_VERIFICATION.md) |
| ...lỗi X như thế nào? | [10-TROUBLESHOOTING.md](10-TROUBLESHOOTING.md) |
| ...tài liệu tất cả? | **File này** |

---

## 📂 Folder Structure Tài Liệu

```
docs/
├── setup/                              # Tất cả hướng dẫn setup
│   ├── 01-PROJECT_OVERVIEW.md         # Tổng quan (Start here!)
│   ├── 02-REQUIREMENTS.md             # Yêu cầu môi trường
│   ├── 03-CODE_STRUCTURE.md           # Cấu trúc source code
│   ├── 04-ENV_CONFIGURATION.md        # .env & config
│   ├── 05-DATABASE_SETUP.md           # Database & SQL
│   ├── 06-DEPENDENCIES.md             # Maven & npm
│   ├── 07-DOCKER_SETUP.md             # Docker & containers
│   ├── 08-STARTUP_GUIDE.md            # Chạy hệ thống
│   ├── 09-TESTING_VERIFICATION.md     # Test & verify
│   ├── 10-TROUBLESHOOTING.md          # Lỗi & fix
│   └── README.md                      # **File này**
├── architecture/                       # Sơ đồ kiến trúc
└── report/                             # Report từ sprint
```

---

## 🚀 Startup Scripts

### **Windows (start-all-databases.bat)**
```bash
docker-compose -f docker-compose-all-services.yml up -d
```

### **macOS/Linux (start-all-databases.sh)**
```bash
docker-compose -f docker-compose-all-services.yml up -d
```

Hoặc dùng script tự động:
- Windows: `start-project.bat` (trong `scripts/`)
- macOS/Linux: `start-project.sh`

---

## 📞 Support

**Khi gặp vấn đề:**

1. **Check logs**
   ```bash
   docker logs <container_name>
   # hoặc xem terminal output
   ```

2. **Search docs**
   - Dùng Ctrl+F để search từ khóa
   - Check [10-TROUBLESHOOTING.md](10-TROUBLESHOOTING.md)

3. **Try simple test**
   ```bash
   curl http://localhost:3000/health
   docker ps
   ```

4. **Ask team / Google**
   - Error message + "Spring Boot" / "Docker"
   - Stack Overflow
   - GitHub Issues

---

## ✅ Checklist: Hệ Thống Ready

Sau khi hoàn thành setup, verify:

- [ ] Java 17 installed: `java -version`
- [ ] Maven installed: `mvn -version`
- [ ] Node.js installed: `node -v`
- [ ] Docker running: `docker ps`
- [ ] Databases up: 4+ containers
- [ ] Auth Service running (port 8001)
- [ ] Product Service running (port 8003)
- [ ] Order Service running (port 8002)
- [ ] API Gateway running (port 3000)
- [ ] Frontend running (port 5173)
- [ ] Can login: http://localhost:5173
- [ ] No errors in logs

---

## 📊 File Sizes

| File | Kích Thước | Thời Gian Đọc |
|------|-----------|---------------|
| 01-PROJECT_OVERVIEW.md | ~5 KB | 10 phút |
| 02-REQUIREMENTS.md | ~15 KB | 20 phút |
| 03-CODE_STRUCTURE.md | ~20 KB | 30 phút |
| 04-ENV_CONFIGURATION.md | ~25 KB | 40 phút |
| 05-DATABASE_SETUP.md | ~20 KB | 30 phút |
| 06-DEPENDENCIES.md | ~20 KB | 30 phút |
| 07-DOCKER_SETUP.md | ~20 KB | 30 phút |
| 08-STARTUP_GUIDE.md | ~15 KB | 25 phút |
| 09-TESTING_VERIFICATION.md | ~15 KB | 25 phút |
| 10-TROUBLESHOOTING.md | ~25 KB | 40 phút |
| **TOTAL** | **~180 KB** | **~4 giờ đọc** |

---

## 🎉 Khi Thành Công

Sau khi setup xong, bạn sẽ có:

✅ **Development Environment**
- Java, Maven, Node.js, Docker installed
- All code dependencies installed
- Databases initialized

✅ **Running Services**
- 4 Java backend services (Auth, Product, Order, Audit)
- 1 Node.js API Gateway
- 1 React Frontend
- PostgreSQL, MySQL, SQL Server, Redis databases

✅ **Knowledge**
- Cấu trúc dự án
- Các services làm gì
- Cách kiến trúc microservices
- DevOps basics (Docker)

✅ **Next Steps**
- Đọc source code chi tiết
- Create your first feature
- Write tests
- Deploy to staging
- Contribute to project

---

## 🙌 Credits

Tài liệu này được tạo để:
- Giúp developer mới onboard nhanh
- Reduce setup time từ **1-2 ngày** xuống **4-6 giờ**
- Provide comprehensive documentation
- Reduce support burden

---

**Bắt đầu: Đọc [01-PROJECT_OVERVIEW.md](01-PROJECT_OVERVIEW.md) 👈**

Good luck! 🚀

