# 🔧 HƯỚNG DẪN CẤU HÌNH POSTMAN ENVIRONMENT

## 📋 Mục Đích
Set up environment variables để chạy collection kiểm thử Auth Service

---

## ✅ CÁCH 1: IMPORT ENVIRONMENT FILE (NHANH NHẤT)

### **Bước 1: Import file environment**
```
1. Mở Postman
2. Bấm vào "Environments" (bên trái)
3. Bấm "Import" 
4. Chọn file: NCKH_E-commerce_Testing.postman_environment.json
5. Bấm "Import"
```

### **Bước 2: Activate environment**
```
1. Ở góc phải trên, tìm dropdown (mặc định là "No Environment")
2. Chọn: "NCKH E-commerce Testing"
3. ✅ Xong! Environment đã active
```

### **Bước 3: Kiểm tra variables**
```
1. Bấm "Environments" (bên trái)
2. Chọn "NCKH E-commerce Testing"
3. Xem các variables:
   - auth_service_url = http://localhost:8080
   - token = (empty - sẽ tự fill sau)
   - username = (empty - sẽ tự fill sau)
```

---

## 🛠️ CÁCH 2: TẠO ENVIRONMENT THỦ CÔNG (CHI TIẾT)

### **Bước 1: Tạo Environment mới**
```
1. Mở Postman
2. Bấm biểu tượng "Environments" (bên trái thanh sidebar)
3. Bấm nút "+" hoặc "Create Environment"
4. Đặt tên: "NCKH E-commerce Testing"
5. Bấm "Create"
```

### **Bước 2: Add Variables**

#### **Variable 1: auth_service_url**
```
Name:         auth_service_url
Initial value: http://localhost:8080
Current value: http://localhost:8080
```

#### **Variable 2: token**
```
Name:         token
Initial value: (để trống)
Current value: (để trống - sẽ auto-fill sau khi login)
```

#### **Variable 3: username**
```
Name:         username
Initial value: (để trống)
Current value: (để trống - sẽ auto-fill sau khi login)
```

### **Bước 3: Save Environment**
```
1. Bấn nút "Save" / "Save as"
2. Xong!
```

### **Bước 4: Activate Environment**
```
1. Ở góc phải trên, tìm dropdown "No Environment"
2. Chọn "NCKH E-commerce Testing"
3. ✅ Environment đã active (xanh lá)
```

---

## 🚀 CÁCH CHẠY COLLECTION

### **Cách 1: Chạy từng request**
```
1. Mở Collection: NCKH E-commerce Testing
2. Click vào mỗi request
3. Bấn nút "Send"
4. Xem kết quả ở tab "Tests"
```

### **Cách 2: Chạy toàn bộ collection (Recommended)**
```
1. Bấn nút "Runner" (ở góc trái)
2. Chọn Collection: "NCKH E-commerce Testing"
3. Chọn Environment: "NCKH E-commerce Testing"
4. Bấn nút "Run NCKH E-commerce Testing"
5. Xem kết quả từng test case
```

---

## ⚙️ CẤU HÌNH CHI TIẾT CHO MỖI ENVIRONMENT

### **Development/Local Setup**
```json
{
  "auth_service_url": "http://localhost:8080",
  "token": "",
  "username": ""
}
```

### **Testing Server**
```json
{
  "auth_service_url": "http://192.168.1.100:8080",
  "token": "",
  "username": ""
}
```

### **Production** ⚠️ (Không recommend)
```json
{
  "auth_service_url": "https://api.nckh.com:8080",
  "token": "",
  "username": ""
}
```

---

## 🔍 KIỂM TRA ENVIRONMENT HOẠT ĐỘNG

### **Bước 1: Xem biến environment**
```
1. Mở một request bất kỳ
2. Ở phần "Body" hoặc "Params", bạn sẽ thấy {{auth_service_url}}
3. Bấm Ctrl+Shift+E (hoặc click mắt) để xem Preview
4. {{auth_service_url}} phải show là: http://localhost:8080
```

### **Bước 2: Test environment variables**
```
1. Tạo request mới: GET {{auth_service_url}}/api/auth/test
2. Bấn "Send"
3. Nếu biến được replace đúng, sẽ gọi: http://localhost:8080/api/auth/test
4. Nếu lỗi, check lại environment setup
```

---

## 🎯 CHECKLIST TRƯỚC KHI CHẠY

- [ ] Postman đã cài đặt
- [ ] Import file collection: NCKH_E-commerce_Testing.postman_collection.json
- [ ] Import file environment: NCKH_E-commerce_Testing.postman_environment.json
- [ ] Environment "NCKH E-commerce Testing" đã active (dropdown xanh lá)
- [ ] Auth Service đang chạy trên port 8080
- [ ] Database có user: john / password123
- [ ] Có thể ping: http://localhost:8080/api/auth/login

---

## 🚀 CHẠY COLLECTION

### **Run All Tests**
```
1. Bấn "Runner" → Chọn Collection
2. Chọn Environment: "NCKH E-commerce Testing"
3. Bấn "Run"
```

### **Kết Quả Mong Đợi**
```
TC1 - Login Success:        ✅ PASS
TC2 - Login Wrong Password: ✅ PASS
TC3 - Login User Not Found: ✅ PASS
TC4 - Login Empty Username: ✅ PASS
TC5 - Login Empty Password: ✅ PASS
TC6 - Validate Token:       ✅ PASS (sau TC1)

Pass Rate: 6/6 = 100% ✅
```

---

## 🐛 TROUBLESHOOTING

### **Lỗi 1: "Could not get any response"**
```
❌ Nguyên nhân: Auth Service không chạy
✅ Giải pháp:
   1. Mở terminal
   2. cd backend/auth-service/Nckh-Lu-n
   3. mvn spring-boot:run (hoặc java -jar app.jar)
   4. Chờ service start
   5. Thử lại request
```

### **Lỗi 2: "Unauthorized 401"**
```
❌ Nguyên nhân: Username/password sai
✅ Giải pháp:
   1. Check database có user "john" không
   2. Check password hash
   3. Hoặc tạo user mới:
      - username: testuser
      - password: test123
      - email: test@example.com
   4. Update request body với credentials mới
```

### **Lỗi 3: "{{auth_service_url}} không replace"**
```
❌ Nguyên nhân: Environment không active
✅ Giải pháp:
   1. Bấn dropdown (góc phải trên)
   2. Chọn "NCKH E-commerce Testing"
   3. Xem dropdown phải show xanh lá
   4. Thử lại
```

### **Lỗi 4: "TC6 skipped - No token found"**
```
❌ Nguyên nhân: Chạy TC6 trước TC1
✅ Giải pháp:
   1. Chạy TC1 trước (Login Success)
   2. TC1 sẽ auto-save token vào environment
   3. Sau đó chạy TC6
   4. Hoặc dùng Collection Runner (tự động chạy đúng thứ tự)
```

---

## 📊 SAU KHI CHẠY XONG

### **Export Report**
```
1. Ở Collection Runner, bấn "Export Results"
2. Chọn format: HTML hoặc JSON
3. Lưu file để báo cáo
```

### **Gửi report**
```
1. Copy file report
2. Dán vào báo cáo presentation
3. Screenshot kết quả
```

---

## 💡 TIPS

- **Auto-save token**: TC1 sẽ tự save token vào environment, TC6 có thể dùng ngay
- **Multiple environments**: Bạn có thể tạo nhiều environment cho Dev/Test/Prod
- **Reuse requests**: Có thể copy requests để test thêm cases khác
- **Monitor logs**: Xem tab "Console" để debug requests

---

**✅ Cập nhật**: February 2026
**📌 Version**: 1.0
