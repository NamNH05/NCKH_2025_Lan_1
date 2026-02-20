# 📦 Phần 2 – Yêu Cầu Môi Trường

---

## 2.1 Hệ Điều Hành (OS)

### Khuyến Nghị
- **Windows 10/11 (Pro/Enterprise)** - Hỗ trợ tốt nhất Docker Desktop
- **macOS 11+** (Intel hoặc Apple Silicon)
- **Linux** (Ubuntu 20.04 LTS trở lên)

### Yêu Cầu Tối Thiểu
- Tối thiểu **8GB RAM**, khuyến khích **16GB**
- **50GB** dung lượng ổ cứng trống
- Quyền **Admin/Root** để cài Docker

---

## 2.2 Các Phần Mềm Cần Cài Đặt

### ✅ Bắt Buộc

#### 1. **Java Development Kit (JDK)**
- **Phiên bản yêu cầu**: Java 17 hoặc Java 21
- **Tại sao**: Backend services dùng Spring Boot

**Cách cài (Windows):**
```powershell
# Cách 1: Download từ Oracle (Recommended)
# https://www.oracle.com/java/technologies/downloads/

# Cách 2: Dùng Chocolatey
choco install openjdk17 -y

# Cách 3: Dùng Windows Subsystem for Linux (WSL)
```

**Kiểm tra cài đặt:**
```bash
java -version
javac -version
```

**Kết quả mong đợi:**
```
java version "17.0.x" hoặc "21.0.x"
Java(TM) SE Runtime Environment (build 17.0.x)
```

---

#### 2. **Maven**
- **Phiên bản yêu cầu**: Maven 3.9.0+
- **Tại sao**: Dùng để build và cài dependencies cho backend

**Cách cài (Windows):**
```powershell
# Cách 1: Download từ https://maven.apache.org/
# Giải nén vào C:\Apache\maven-3.9.x
# Thêm vào PATH: C:\Apache\maven-3.9.x\bin

# Cách 2: Dùng Chocolatey
choco install maven -y

# Cách 3: Dùng Windows Package Manager
winget install Apache.Maven
```

**Kiểm tra cài đặt:**
```bash
mvn -version
```

**Kết quả mong đợi:**
```
Apache Maven 3.9.x
```

---

#### 3. **Node.js**
- **Phiên bản yêu cầu**: Node 18.0+ (hoặc LTS mới nhất)
- **Tại sao**: Dùng cho API Gateway (Express) và Frontend (React)

**Cách cài (Windows):**
```powershell
# Cách 1: Download từ https://nodejs.org/
# Chọn LTS (Long Term Support)

# Cách 2: Dùng Chocolatey
choco install nodejs -y

# Cách 3: Dùng Windows Package Manager
winget install OpenJS.NodeJS.LTS
```

**Kiểm tra cài đặt:**
```bash
node --version
npm --version
npx --version
```

**Kết quả mong đợi:**
```
v18.19.0 (hoặc cao hơn)
9.8.1 (hoặc cao hơn)
```

---

#### 4. **Docker & Docker Compose**
- **Docker phiên bản**: 20.10+ (khuyến khích 25.0+)
- **Docker Compose phiên bản**: 2.0+
- **Tại sao**: Chạy PostgreSQL, MySQL, SQL Server, Redis dưới dạng container

**Cách cài (Windows):**
```powershell
# Cách 1: Docker Desktop (Recommended)
# Download từ https://www.docker.com/products/docker-desktop
# Cài đặt và bật Docker Desktop

# Cách 2: Dùng Chocolatey
choco install docker-desktop -y

# Cách 3: Dùng Windows Package Manager
winget install Docker.DockerDesktop
```

**Kiểm tra cài đặt:**
```bash
docker --version
docker compose version
```

**Kết quả mong đợi:**
```
Docker version 25.0.x
Docker Compose version 2.x.x
```

**Bật Docker Service:**
```powershell
# Trên Windows: Mở Docker Desktop từ Start menu
# Chờ khoảng 30 giây để Docker daemon khởi động

# Kiểm tra Docker đang chạy:
docker ps
```

---

### 📚 Tùy Chọn (Highly Recommended)

#### 5. **Visual Studio Code**
- **Download**: https://code.visualstudio.com/
- **Extensions cần cài**:
  - Extension Pack for Java (Microsoft)
  - Python (Microsoft)
  - REST Client (Huachao Mao)
  - Docker (Microsoft)

---

#### 6. **Postman** hoặc **Insomnia** (API Testing)
- **Postman**: https://www.postman.com/downloads/
- **Dùng để**: Test API endpoints
- **Import file**: `docs/postman/NCKH.postman_collection.json` (sẽ được tạo sau)

---

#### 7. **Git**
- **Download**: https://git-scm.com/
- **Dùng để**: Clone project từ GitHub

**Kiểm tra cài đặt:**
```bash
git --version
```

---

#### 8. **DBeaver** hoặc **pgAdmin** (Database Management)

**DBeaver:**
- Download: https://dbeaver.io/download/
- Hỗ trợ: PostgreSQL, MySQL, SQL Server

**pgAdmin (cho PostgreSQL):**
- Download: https://www.pgadmin.org/download/

---

## 2.3 Bảng Tóm Tắt Phiên Bản Yêu Cầu

| Phần Mềm | Phiên Bản Yêu Cầu | Mục Đích | Bắt Buộc |
|----------|-------------------|---------|----------|
| **Java (JDK)** | 17 hoặc 21 | Spring Boot Backend | ✅ |
| **Maven** | 3.9.0+ | Build & Dependencies | ✅ |
| **Node.js** | 18.0+ LTS | Gateway & Frontend | ✅ |
| **npm** | 9.0+ | Package Manager | ✅ |
| **Docker** | 20.10+ | Container Runtime | ✅ |
| **Docker Compose** | 2.0+ | Multi-container | ✅ |
| **Git** | 2.30+ | Version Control | ⚠️ |
| **Postman** | Latest | API Testing | ⚠️ |
| **VS Code** | Latest | IDE | ⚠️ |
| **DBeaver** | Latest | DB Management | ⚠️ |

✅ = Bắt buộc  
⚠️ = Tùy chọn nhưng khuyến nghị

---

## 2.4 Kiểm Tra Toàn Bộ Yêu Cầu

Tạo file `check-requirements.bat` (Windows) hoặc `check-requirements.sh` (macOS/Linux):

**Windows (check-requirements.bat):**
```batch
@echo off
color 0A
echo ===== CHECKING SYSTEM REQUIREMENTS =====
echo.

echo 1. Java version:
java -version
echo.

echo 2. Maven version:
mvn -version
echo.

echo 3. Node.js version:
node --version
npm --version
echo.

echo 4. Docker version:
docker --version
docker compose version
echo.

echo 5. Git version:
git --version
echo.

echo ===== CHECK COMPLETE =====
pause
```

**Chạy:**
```powershell
.\check-requirements.bat
```

---

## 2.5 Cài Đặt Từng Bước (Hướng Dẫn Chi Tiết)

### Bước 1: Cài Java (Windows)

```powershell
# 1. Download JDK 17 từ Oracle
#    https://www.oracle.com/java/technologies/downloads/#java17

# 2. Chạy installer, chọn:
#    - Install location: C:\Program Files\Java\jdk-17
#    - Chọn "Set JAVA_HOME automatically"

# 3. Kiểm tra:
java -version

# Nếu error, thêm vào PATH:
# Mở System Environment Variables:
#   1. Right-click "This PC" > Properties
#   2. Advanced system settings
#   3. Environment Variables
#   4. Thêm JAVA_HOME = C:\Program Files\Java\jdk-17
#   5. Thêm C:\Program Files\Java\jdk-17\bin vào PATH
```

### Bước 2: Cài Maven (Windows)

```powershell
# 1. Download Maven từ https://maven.apache.org/download.cgi
#    Chọn apache-maven-3.9.x-bin.zip

# 2. Giải nén vào C:\Apache\maven-3.9.x

# 3. Thêm vào PATH:
#    Path = C:\Apache\maven-3.9.x\bin

# 4. Kiểm tra:
mvn -version
```

### Bước 3: Cài Node.js (Windows)

```powershell
# 1. Download từ https://nodejs.org/ (LTS)
# 2. Chạy installer, chọn "Add to PATH"
# 3. Kiểm tra:
node --version
npm --version

# 4. Cập nhật npm:
npm install -g npm@latest
```

### Bước 4: Cài Docker (Windows)

```powershell
# 1. Download Docker Desktop từ https://www.docker.com/products/docker-desktop
# 2. Chạy installer
# 3. Chọn "Install required Windows components for WSL 2"
# 4. Khởi động lại máy
# 5. Mở Docker Desktop
# 6. Kiểm tra:
docker --version
docker compose version
```

---

## 2.6 Xử Lý Vấn Đề Thường Gặp

**Q: "Java is not recognized as an internal or external command"**
- A: Thêm C:\Program Files\Java\jdk-17\bin vào PATH

**Q: "mvn is not recognized"**
- A: Thêm C:\Apache\maven-3.9.x\bin vào PATH

**Q: "Docker daemon is not running"**
- A: Mở Docker Desktop từ Start menu, chờ nó khởi động

**Q: "Port 3000 already in use"**
- A: Xem phần **10-TROUBLESHOOTING.md**

---

