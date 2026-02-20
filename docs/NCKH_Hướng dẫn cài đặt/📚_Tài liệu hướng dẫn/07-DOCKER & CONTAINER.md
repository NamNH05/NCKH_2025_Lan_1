# 🐳 07 - DOCKER & CONTAINER

---

## Docker Là Gì?

Docker giúp **chạy applications trong containers** - như những chiếc hộp độc lập, mỗi hộp có:
- OS, libraries, dependencies
- Ứng dụng của bạn
- Tất cả đều có sẵn, không cần cài trên máy

---

## Docker Compose

File `docker-compose-all-services.yml` **định nghĩa tất cả containers**:

```yaml
services:
  postgres-auth-db:    # PostgreSQL container
    image: postgres:15
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_USER=auth_user
      - POSTGRES_PASSWORD=auth@123456

  mysql-product-db:    # MySQL container
    image: mysql:8.0
    ports:
      - "3306:3306"

  sqlserver-order-db:  # SQL Server container
    image: mcr.microsoft.com/mssql/server:2022-latest
    ports:
      - "1433:1433"

  redis-cache:         # Redis container
    image: redis:7
    ports:
      - "6379:6379"
```

---

## Các Lệnh Cơ Bản

### Khởi Động Tất Cả Containers

```bash
docker-compose -f docker-compose-all-services.yml up -d
```

- `-f` = file path
- `up` = khởi động
- `-d` = chạy ở background (detach)

### Kiểm Tra Containers

```bash
docker ps  # Xem containers đang chạy
docker ps -a  # Xem tất cả containers (kể cả bị stop)
```

### Xem Logs

```bash
docker logs postgres-auth-db  # Xem logs PostgreSQL
docker logs mysql-product-db
docker logs sqlserver-order-db
```

### Dừng Containers

```bash
docker-compose -f docker-compose-all-services.yml down
```

### Xóa Volumes (Xóa Tất Cả Dữ Liệu)

```bash
docker-compose -f docker-compose-all-services.yml down -v
```

---

## Docker Desktop

- Trên Windows: Mở ứng dụng **Docker Desktop**
- Phải chạy trước khi dùng docker commands
- Có thể kiểm tra containers trong giao diện

---

**👉 Tiếp theo: `08-HƯỚNG DẪN KHỞI ĐỘNG.md`**

