# 📦 02 - YÊU CẦU MÔI TRƯỜNG

Phần mềm bắt buộc cần cài:

## ✅ Bắt Buộc Cài Đặt

| Phần Mềm | Phiên Bản | Kiểm Tra |
|---------|----------|---------|
| **Java (JDK)** | 17 hoặc 21 | `java -version` |
| **Maven** | 3.9+ | `mvn -version` |
| **Node.js** | 18+ LTS | `node -version` |
| **npm** | 9+ | `npm -version` |
| **Docker** | 20.10+ | `docker --version` |
| **Docker Compose** | 2.0+ | `docker compose version` |

---

## 🔧 Cách Cài (Windows)

```powershell
# Java
# Download từ https://www.oracle.com/java/technologies/downloads/

# Maven
# Download từ https://maven.apache.org/
# Hoặc: choco install maven

# Node.js
# Download từ https://nodejs.org/ (chọn LTS)
# Hoặc: choco install nodejs

# Docker
# Download Docker Desktop từ https://www.docker.com/products/docker-desktop
```

---

## ⚠️ Quy Tắc Quan Trọng

1. **Bắt buộc**: Cài Java, Maven, Node.js, Docker
2. **Optional**: Git, Postman, VS Code, DBeaver
3. **Docker phải chạy** trước khi bắt đầu

---

## ✓ Kiểm Tra

Chạy lệnh này để verify tất cả:

```bash
java -version
mvn -version
node --version
npm --version
docker --version
docker compose version
```

Nếu tất cả không báo lỗi → **Bạn đã sẵn sàng!**

---

**👉 Tiếp theo: Chạy `🚀_Script tự động/start-all-services.bat` (Windows) hoặc `.sh` (Mac/Linux)**

