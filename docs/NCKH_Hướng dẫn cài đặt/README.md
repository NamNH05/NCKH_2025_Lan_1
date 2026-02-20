# 📖 NCKH - Hướng Dẫn Cài Đặt Đầy Đủ

> **Hướng dẫn toàn diện để khởi động dự án NCKH E-Commerce từ con số 0**

---

## 📂 Cấu Trúc Folder

```
NCKH_Hướng dẫn cài đặt/
│
├── 📚 Tài liệu hướng dẫn/
│   ├── 00-BẮT ĐẦU NHANH.md              ⭐ START HERE!
│   ├── 01-TỔNG QUAN DỰ ÁN.md
│   ├── 02-YÊU CẦU MÔI TRƯỜNG.md
│   ├── 03-CẤU TRÚC SOURCE CODE.md
│   ├── 04-CẤU HÌNH MÔI TRƯỜNG.md
│   ├── 05-DATABASE & SQL.md
│   ├── 06-CÀI ĐẶT DEPENDENCIES.md
│   ├── 07-DOCKER & CONTAINER.md
│   ├── 08-HƯỚNG DẪN KHỞI ĐỘNG.md
│   ├── 09-TEST & XÁC THỰC.md
│   └── 10-KHẮC PHỤC SỰ CỐ.md
│
├── 🔧 File cấu hình mẫu/
│   ├── api-gateway.env
│   ├── frontend.env
│   ├── auth-service.properties
│   ├── product-service.properties
│   ├── order-service.properties
│   └── audit-service.properties
│
├── 🚀 Script tự động/
│   ├── start-all-services.bat           (Windows)
│   ├── start-all-services.sh            (macOS/Linux)
│   └── README.md                         (Giải thích script)
│
├── 📋 Kiểm tra và xác nhận/
│   ├── SETUP_COMPLETION_CHECKLIST.md    (Verify setup hoàn tất)
│   ├── DOCUMENTATION_INDEX.md           (Chỉ mục tất cả docs)
│   └── SETUP_SUMMARY.md                 (Tóm tắt)
│
└── README.md                             (File này)
```

---

## ⚡ Bắt Đầu Nhanh Nhất (5 phút)

### Cho người vội:

1. **Mở folder**: `🚀_Script tự động/`
2. **Chạy script**:
   - **Windows**: Nhấp đôi `start-all-services.bat`
   - **macOS/Linux**: Mở terminal, chạy `./start-all-services.sh`
3. **Chờ 2-3 phút** cho services khởi động
4. **Mở trình duyệt**: http://localhost:5173
5. **Đăng nhập**:
   - Admin: `sysadmin` / `1234`
   - User: `23810310082` / `123456`

✅ **Xong!** Hệ thống đã sẵn sàng.

---

## 📚 Hướng Dẫn Chi Tiết (4 giờ học)

Nếu bạn muốn **hiểu rõ mọi thứ**, hãy đọc các tài liệu theo thứ tự:

### 👶 Cấp Beginner (2 giờ)
1. **00-BẮT ĐẦU NHANH.md** (5 phút)
   - Tổng quan 60 giây
   
2. **01-TỔNG QUAN DỰ ÁN.md** (30 phút)
   - NCKH là gì?
   - Kiến trúc hệ thống
   - Các thành phần chính
   
3. **02-YÊU CẦU MÔI TRƯỜNG.md** (45 phút)
   - Phần mềm cần cài
   - Cách cài từng phần
   - Kiểm tra yêu cầu

4. **08-HƯỚNG DẪN KHỞI ĐỘNG.md** (30 phút)
   - Chạy Docker
   - Chạy backend services
   - Chạy frontend

### 🚀 Cấp Intermediate (1 giờ)
5. **03-CẤU TRÚC SOURCE CODE.md** (30 phút)
   - Folder layout
   - Mỗi folder làm gì
   - Các file quan trọng

6. **04-CẤU HÌNH MÔI TRƯỜNG.md** (30 phút)
   - File .env
   - Biến môi trường
   - Config cho Dev/Prod

### 🎓 Cấp Advanced (1 giờ)
7. **05-DATABASE & SQL.md** (20 phút)
   - Database credentials
   - SQL init scripts
   - Schema explanation

8. **06-CÀI ĐẶT DEPENDENCIES.md** (20 phút)
   - Maven dependencies
   - npm packages
   - Cập nhật libraries

9. **07-DOCKER & CONTAINER.md** (20 phút)
   - Docker concepts
   - docker-compose
   - Container management

### 🔧 Troubleshooting
10. **09-TEST & XÁC THỰC.md** (30 phút)
    - Health checks
    - Postman testing
    - Test scenarios

11. **10-KHẮC PHỤC SỰ CỐ.md** (40 phút)
    - 30+ lỗi thường gặp
    - Cách diagnose
    - Giải pháp từng vấn đề

---

## 🎯 Dành cho các vai trò khác nhau

### 👨‍💼 Quản Lý / Scrum Master
- Đọc: **01-TỔNG QUAN DỰ ÁN.md**
- Thời gian: 30 phút
- Mục đích: Hiểu về architecture

### 👨‍💻 Developer (Backend - Java)
- Đọc: 01, 02, 03, 04, 05, 06, 08, 10
- Thời gian: 3 giờ
- Mục đích: Setup & hiểu codebase

### 👨‍💻 Developer (Frontend - React)
- Đọc: 01, 02, 03, 04, 08, 09, 10
- Thời gian: 2.5 giờ
- Mục đích: Setup & API testing

### 👨‍⚙️ DevOps / Infrastructure
- Đọc: 02, 05, 07, 08, 10
- Thời gian: 2 giờ
- Mục đích: Docker, DB, deployment

### 🧪 QA / Tester
- Đọc: 01, 04, 08, 09, 10
- Thời gian: 2 giờ
- Mục đích: Setup, API testing, troubleshooting

---

## 🎨 Hướng Dẫn Phổ Biến Nhất

### Tôi là developer mới, tôi cần gì?

**Bước 1** (5 phút): Đọc **00-BẮT ĐẦU NHANH.md**

**Bước 2** (30 phút): Cài phần mềm theo **02-YÊU CẦU MÔI TRƯỜNG.md**

**Bước 3** (5 phút): Chạy script trong **🚀_Script tự động/**

**Bước 4** (1 giờ): Đọc **01-TỔNG QUAN DỰ ÁN.md** + **03-CẤU TRÚC SOURCE CODE.md**

**Bước 5**: Bắt đầu làm việc!

### Tôi chạy script nhưng nó lỗi?

1. Mở **📋_Kiểm tra và xác nhận/SETUP_COMPLETION_CHECKLIST.md**
2. Kiểm tra danh sách yêu cầu
3. Nếu vẫn lỗi, xem **10-KHẮC PHỤC SỰ CỐ.md**

### Tôi cần biết tài liệu này đang nói về cái gì?

Mở **📋_Kiểm tra và xác nhận/DOCUMENTATION_INDEX.md** - nó có chỉ mục tất cả các mục.

---

## 📊 Thống Kê Tài Liệu

| Mục | File | Kích thước | Thời gian đọc |
|-----|------|-----------|----------------|
| Bắt đầu nhanh | 00-BẮT ĐẦU NHANH.md | 5 KB | 5 phút |
| Tổng quan | 01-TỔNG QUAN DỰ ÁN.md | 12 KB | 15 phút |
| Yêu cầu | 02-YÊU CẦU MÔI TRƯỜNG.md | 20 KB | 30 phút |
| Cấu trúc | 03-CẤU TRÚC SOURCE CODE.md | 15 KB | 20 phút |
| Config | 04-CẤU HÌNH MÔI TRƯỜNG.md | 18 KB | 25 phút |
| Database | 05-DATABASE & SQL.md | 22 KB | 30 phút |
| Dependencies | 06-CÀI ĐẶT DEPENDENCIES.md | 18 KB | 25 phút |
| Docker | 07-DOCKER & CONTAINER.md | 16 KB | 20 phút |
| Startup | 08-HƯỚNG DẪN KHỞI ĐỘNG.md | 20 KB | 30 phút |
| Testing | 09-TEST & XÁC THỰC.md | 17 KB | 25 phút |
| Troubleshooting | 10-KHẮC PHỤC SỰ CỐ.md | 35 KB | 45 phút |
| **TỔNG CỘNG** | | **~200 KB** | **~4.5 giờ** |

---

## 🔑 Thông Tin Quan Trọng

### Credentials (Mật Khẩu / Người Dùng)

| Service | Loại | Người dùng | Mật khẩu |
|---------|------|-----------|---------|
| **Frontend** | User | sysadmin | 1234 |
| **Frontend** | User | 23810310082 | 123456 |
| **PostgreSQL** | Database | auth_user | auth@123456 |
| **MySQL** | Database | product_user | product@123456 |
| **SQL Server** | Database | orderuser | Order@123 |

### Ports

| Service | Port | Ghi chú |
|---------|------|--------|
| Frontend (React) | 5173 | http://localhost:5173 |
| API Gateway | 3000 | http://localhost:3000 |
| Auth Service | 8001 | Backend service |
| Product Service | 8003 | Backend service |
| Order Service | 8002 | Backend service |
| Audit Service | 8004 | Backend service |
| PostgreSQL | 5432 | Auth & Audit database |
| MySQL | 3306 | Product database |
| SQL Server | 1433 | Order database |
| Redis | 6379 | Cache |
| Adminer | 8080 | Web UI for databases |

### Thư mục quan trọng

```
Backend Services:
  - backend/auth-service/Nckh-Lu-n/
  - backend/product-service/Tien/Tien/
  - backend/order-service/Nckh-C-ng/
  - backend/audit-service/Nckh-Hi-p/

Frontend:
  - frontend/web-client/

API Gateway:
  - api-gateway/

Databases:
  - docker-compose-all-services.yml
  - database/postgres-init.sql
  - database/mysql-init.sql
  - database/sqlserver-init.sql
```

---

## ❓ Câu Hỏi Thường Gặp

### Q: Mất bao lâu để setup?
**A**: 60-90 phút (nếu dùng script tự động)

### Q: Tôi có thể chạy trên macOS không?
**A**: Có, hoàn toàn hỗ trợ. Dùng `start-all-services.sh` thay vì `.bat`

### Q: Tôi phải cài cả 11 phần không?
**A**: Không, bạn có thể:
- Chỉ chạy script (5 phút)
- Hoặc đọc từng phần khi cần

### Q: Tôi không có Docker?
**A**: Xem **10-KHẮC PHỤC SỰ CỐ.md** phần "Docker Issues"

### Q: Làm sao để thêm credential mới?
**A**: Xem **05-DATABASE & SQL.md**

---

## 📞 Hỗ Trợ

| Vấn đề | Giải pháp |
|--------|----------|
| Không biết bắt đầu từ đâu | Đọc **00-BẮT ĐẦU NHANH.md** |
| Script không chạy | Xem **🚀_Script tự động/README.md** |
| Lỗi sau khi chạy | Mở **📋_Kiểm tra và xác nhận/SETUP_COMPLETION_CHECKLIST.md** |
| Lỗi cụ thể | Tìm kiếm trong **10-KHẮC PHỤC SỰ CỐ.md** |
| Cấu hình sai | Xem **04-CẤU HÌNH MÔI TRƯỜNG.md** |
| Database không kết nối | Xem **05-DATABASE & SQL.md** |

---

## 🚀 Các Bước Tiếp Theo

Sau khi setup xong:

1. ✅ Hệ thống chạy (5 phút)
2. ✅ Đăng nhập được (2 phút)
3. 📖 Đọc **01-TỔNG QUAN DỰ ÁN.md** - Hiểu architecture (30 phút)
4. 📖 Đọc **03-CẤU TRÚC SOURCE CODE.md** - Tìm hiểu codebase (30 phút)
5. 💻 Tạo feature đầu tiên của bạn
6. 🧪 Viết test
7. 📊 Deploy

---

## 📝 Ghi Chú

- Tất cả commands đều được test trên Windows 10/11
- Hỗ trợ macOS Monterey+, Ubuntu 20.04+
- Docker Desktop phải chạy trước khi bắt đầu
- Nếu lỗi, hãy kiểm tra **10-KHẮC PHỤC SỰ CỐ.md** trước

---

## 📅 Cập Nhật Lần Cuối

- **Ngày**: Tháng 1 năm 2026
- **Phiên bản**: 1.0
- **Kiểm tra bởi**: Development Team
- **Hỗ trợ đến**: Tháng 12 năm 2026

---

## ⭐ Lời Khuyên Cuối Cùng

> **Không vội, hãy đọc tài liệu từng phần một.**  
> **Mỗi phần giải thích một thứ cụ thể, đừng bỏ qua bất cứ cái gì.**

**Happy Coding! 🚀**

---

*Được tạo bởi Development Team*  
*Cho dự án NCKH E-Commerce*  
