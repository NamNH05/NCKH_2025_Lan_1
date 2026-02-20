# 🗄️ Database Configuration & Initialization

> **Centralized database schema and initialization scripts for all microservices**

## Overview

This directory contains all SQL initialization scripts for the entire project's database infrastructure.

### Databases

| Database | Type | Service | Tables |
|----------|------|---------|--------|
| **auth_db** | PostgreSQL | Auth & User Services | auth_users, roles, tokens, user_profiles, user_addresses, user_preferences |
| **shopquanao** | MySQL | Product Service | products, categories, stock_history, product_images |
| **order_dB** | SQL Server | Order Service | Cart, CartItem, Orders, OrderItem, Payment, Shipment |
| **Audit DB** | Oracle | Audit Service | audit_logs |

## Quick Start

All SQL initialization scripts are **centralized in `/database/sql-scripts`**:

```bash
# View all available scripts
ls database/sql-scripts/
```

See [sql-scripts/README.md](sql-scripts/README.md) for:
- ✅ Complete initialization instructions
- ✅ Database connection details
- ✅ Execution order and prerequisites
- ✅ Troubleshooting guide

## Database Setup Methods

### Method 1: Docker (Recommended)

All databases are automatically initialized with Docker:

```bash
docker-compose -f docker-compose-all-services.yml up -d
```

Services wait for databases to be ready before starting:
- PostgreSQL (5432) - Auth & User services
- MySQL (3306) - Product service
- SQL Server (1433) - Order service
- Oracle (1521) - Audit service

### Method 2: Manual Setup

If Docker is unavailable or you need manual control:

1. **PostgreSQL** (Auth & User)
   ```bash
   psql -U postgres -f database/sql-scripts/04-auth-user-service-postgres.sql
   ```

2. **MySQL** (Product)
   ```bash
   mysql -u root -p < database/sql-scripts/03-product-service-mysql.sql
   ```

3. **SQL Server** (Order)
   ```sql
   -- In SQL Server Management Studio
   :r database/sql-scripts/01-order-service-sqlserver.sql
   ```

4. **Oracle** (Audit)
   ```sql
   -- In SQL*Plus
   @database/sql-scripts/05-audit-service-oracle.sql
   ```

## Latest Updates

### Recent Changes ✅

1. **Database Schema Verification**
   - All 4 services verified with correct table structures
   - SQL Server Orders table: Added `item_details` and `shipping_fee` columns

2. **SQL Scripts Centralization**
   - All init files moved to `/database/sql-scripts/`
   - Single source of truth for database schemas
   - Numbered files for clear execution order

3. **Checkout Flow Integration**
   - Orders table now stores: address, itemDetails, shippingFee, shipping_fee DECIMAL
   - Supports full OrderDTO response from backend
   - Enables address capture and persistence

4. **Category Filtering Support**
   - MySQL products table uses `category` field
   - Backend: ProductService.getGroupProducts() uses `findByCategoryContaining()`
   - Frontend: Supports Vietnamese category names (Áo nam, Áo nữ, Quần, Giày, Phụ kiện)

## Environment Variables

Database credentials are configured in `.env` files for each service:

```env
# PostgreSQL (Auth Service)
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password
POSTGRES_DB=auth_db

# MySQL (Product Service)
MYSQL_USER=root
MYSQL_PASSWORD=root
MYSQL_DATABASE=shopquanao

# SQL Server (Order Service)
SA_PASSWORD=YourStrong@Pass123
MSSQL_DB=order_dB

# Oracle (Audit Service)
ORACLE_SID=FREEPDB1
ORACLE_PWD=password
```

## Backup & Recovery

### Backing Up

```bash
# PostgreSQL
pg_dump -U postgres auth_db > backup_auth_db.sql

# MySQL
mysqldump -u root -p shopquanao > backup_shopquanao.sql

# SQL Server
sqlcmd -S localhost -E -Q "BACKUP DATABASE order_dB TO DISK='order_dB.bak'"

# Oracle
exp audit_user/password FILE=audit_backup.dmp
```

### Restoring

```bash
# PostgreSQL
psql -U postgres auth_db < backup_auth_db.sql

# MySQL
mysql -u root -p shopquanao < backup_shopquanao.sql

# SQL Server
sqlcmd -S localhost -E -Q "RESTORE DATABASE order_dB FROM DISK='order_dB.bak'"

# Oracle
imp audit_user/password FILE=audit_backup.dmp
```

## Monitoring & Verification

### Verify Databases Are Running

```bash
# PostgreSQL
psql -U postgres -c "SELECT version();"

# MySQL
mysql -u root -p -e "SELECT VERSION();"

# SQL Server
sqlcmd -S localhost -Q "SELECT @@VERSION"

# Oracle
sqlplus sys/password@FREEPDB1 "SELECT * FROM v\$version WHERE ROWNUM=1;"
```

### Check Tables

```bash
# PostgreSQL
psql -U postgres -d auth_db -c "\dt"

# MySQL
mysql -u root -p shopquanao -e "SHOW TABLES;"

# SQL Server
sqlcmd -S localhost -Q "USE order_dB; SELECT * FROM INFORMATION_SCHEMA.TABLES"

# Oracle
sqlplus audit_user/password@FREEPDB1 "SELECT table_name FROM user_tables;"
```

## Troubleshooting

### Database Connection Failed

1. **Verify Docker containers are running**
   ```bash
   docker ps | grep -E "postgres|mysql|mssql|oracle"
   ```

2. **Check logs for initialization errors**
   ```bash
   docker logs <container_name>
   ```

3. **Verify port mappings**
   - PostgreSQL: 5432
   - MySQL: 3306
   - SQL Server: 1433
   - Oracle: 1521

### Table Not Found After Script Execution

1. Check script execution output for errors
2. Verify correct database is selected
3. Manually run script with error output:
   ```bash
   psql -U postgres -d auth_db -f database/sql-scripts/04-auth-user-service-postgres.sql 2>&1 | tee output.log
   ```

### Initialization Script Fails

1. Review `sql-scripts/README.md` for prerequisites
2. Check database user permissions
3. Verify SQL syntax compatibility with your database version

## Related Documentation

- **SQL Scripts Guide**: [sql-scripts/README.md](sql-scripts/README.md)
- **Docker Setup**: [../docs/setup/07-DOCKER_SETUP.md](../docs/setup/07-DOCKER_SETUP.md)
- **Environment Configuration**: [../docs/setup/04-ENV_CONFIGURATION.md](../docs/setup/04-ENV_CONFIGURATION.md)
- **Troubleshooting**: [../docs/setup/10-TROUBLESHOOTING.md](../docs/setup/10-TROUBLESHOOTING.md)

---

**Last Updated**: January 2026  
**Status**: ✅ All databases verified and operational
