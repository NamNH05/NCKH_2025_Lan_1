# 🗄️ 05 - DATABASE & SQL

---

## 3 Databases

| Database | Port | Services | Dùng Cho |
|----------|------|----------|---------|
| PostgreSQL | 5432 | Auth, Audit | Users, roles, logs |
| MySQL | 3306 | Product | Sản phẩm, danh mục |
| SQL Server | 1433 | Order | Đơn hàng, thanh toán |

---

## Khởi Tạo Databases

### ✅ Cách 1: Docker (Tự Động)

```bash
docker-compose -f docker-compose-all-services.yml up -d
docker ps  # Kiểm tra containers
```

**Điều gì xảy ra:**
- Docker tạo 4 containers (PostgreSQL, MySQL, SQL Server, Redis)
- Tự động chạy SQL init scripts
- Tất cả databases được tạo & populated

---

## Credentials

### PostgreSQL (Auth & Audit)
```
Host: localhost
Port: 5432
Database: nckh_auth
User: auth_user
Password: auth@123456
```

### MySQL (Product)
```
Host: localhost
Port: 3306
Database: shopquanao
User: product_user
Password: product@123456
```

### SQL Server (Order)
```
Server: localhost
Port: 1433
Database: order_dB
User: orderuser
Password: Order@123
```

---

## Kiểm Tra Kết Nối

### PostgreSQL
```bash
psql -U auth_user -d nckh_auth -h localhost
# Nhập password: auth@123456
```

### MySQL
```bash
mysql -u product_user -p shopquanao
# Nhập password: product@123456
```

### SQL Server
```bash
sqlcmd -S localhost -U orderuser -P Order@123 -d order_dB
```

---

## Init Scripts

Nằm trong folder `database/`:
- `postgres-init.sql` - Tạo tables cho Auth & Audit
- `mysql-init.sql` - Tạo tables cho Product
- `sqlserver-init.sql` - Tạo tables cho Order

Scripts này tự động chạy khi Docker containers khởi động.

---

**👉 Tiếp theo: `06-CÀI ĐẶT DEPENDENCIES.md`**

