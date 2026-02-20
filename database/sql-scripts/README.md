# Database Initialization Scripts

This folder contains all SQL initialization scripts for the entire project, organized by service and database type.

## 🚀 Quick Start - Automated Setup

### Option 1: PowerShell (Windows - Recommended)
```powershell
# Run all database initializations automatically
.\run-all-init.ps1

# Or run specific databases only
.\run-all-init.ps1 -OnlyMySQL
.\run-all-init.ps1 -OnlySQLServer
.\run-all-init.ps1 -OnlyPostgreSQL

# Skip specific databases
.\run-all-init.ps1 -SkipOracle
```

### Option 2: Batch File (Windows)
```cmd
run-all-init.bat
```

### Option 3: Shell Script (Linux/macOS)
```bash
chmod +x run-all-init.sh
./run-all-init.sh
```

---

## Structure

- **run-all-init.ps1** - PowerShell automation script (Windows) ✅
- **run-all-init.bat** - Batch file automation (Windows) ✅
- **run-all-init.sh** - Bash script automation (Linux/macOS) ✅
- **01-order-service-sqlserver.sql** - SQL Server database schema for Order Service
- **02-order-service-migration.sql** - SQL Server migration to add new columns (item_details, shipping_fee)
- **03-product-service-mysql.sql** - MySQL database schema for Product Service
- **04-auth-user-service-postgres.sql** - PostgreSQL schema for Auth & User Services
- **05-audit-service-oracle.sql** - Oracle database schema for Audit Service

## Service Mapping

| Service | Database | Type | File |
|---------|----------|------|------|
| Order Service | order_dB | SQL Server | 01 |
| Product Service | shopquanao | MySQL | 03 |
| Auth Service | auth_db | PostgreSQL | 04 |
| User Service | auth_db | PostgreSQL | 04 |
| Audit Service | Audit DB | Oracle | 05 |

## Manual Execution Order

If automation scripts don't work, run manually:

### 1. SQL Server (Order Service):
   ```sql
   -- Execute in SQL Server Management Studio
   01-order-service-sqlserver.sql
   ```

### 2. MySQL (Product Service):
   ```bash
   mysql -u root -p < 03-product-service-mysql.sql
   ```

### 3. PostgreSQL (Auth & User Services):
   ```bash
   psql -U postgres -f 04-auth-user-service-postgres.sql
   ```

### 4. Oracle (Audit Service):
   ```sql
   -- Execute in SQL*Plus or SQL Developer
   @05-audit-service-oracle.sql
   ```

### Database Connection Details

#### SQL Server
- Database: `order_dB`
- Default Port: 1433
- Tables: Cart, CartItem, Orders, OrderItem, Payment, Shipment

#### MySQL
- Database: `shopquanao`
- Default Port: 3306
- Tables: products, categories, stock_history, product_images

#### PostgreSQL
- Database: `auth_db`
- Default Port: 5432
- Tables: auth_users, roles, user_roles, tokens, user_profiles, user_addresses, user_preferences

#### Oracle
- Database: Audit DB (FREEPDB1 for Free/XE)
- Default Port: 1521
- Tables: audit_logs

## Important Notes

### SQL Server
- Scripts automatically create the database if it doesn't exist
- Uses IDENTITY for auto-increment IDs
- All tables have indexes for performance optimization
- Orders table includes new columns: `item_details`, `shipping_fee`

### MySQL
- Scripts automatically create the database if it doesn't exist
- Uses AUTO_INCREMENT for IDs
- Configured with UTF-8 support (utf8mb4)
- Includes sample categories and products

### PostgreSQL
- Includes default ADMIN role and user (password: Admin@123456 - hashed)
- Uses BIGSERIAL for auto-increment
- Support for JSON fields (JSONB) for flexible permissions storage
- Default admin user: `admin` / `admin@example.com`

### Oracle
- Requires audit_user creation before running script
- Uses SEQUENCE for ID generation
- Supports CLOB for large text fields (old_value, new_value)
- Requires Oracle Free/XE or equivalent

## Troubleshooting

### SQL Server
- If database creation fails, ensure you have appropriate permissions
- For migration script: if columns already exist, script will skip gracefully

### MySQL
- Ensure MySQL service is running
- Default user: root, password as configured in your environment

### PostgreSQL
- Ensure PostgreSQL service is running
- Create auth_db database first if not exists
- Connection string: `postgresql://user:password@localhost:5432/auth_db`

### Oracle
- Ensure Oracle instance is running and FREEPDB1 exists
- Run scripts as SYSDBA or have appropriate privileges
- Check NLS_LANG environment variable for character set issues

## Maintenance

### Backing up Databases

```bash
# SQL Server
sqlcmd -S localhost -E -Q "BACKUP DATABASE order_dB TO DISK='backup.bak'"

# MySQL
mysqldump -u root -p shopquanao > shopquanao_backup.sql

# PostgreSQL
pg_dump -U postgres auth_db > auth_db_backup.sql

# Oracle
exp audit_user/password FILE=audit_backup.dmp
```

### Resetting Databases

To completely reset a database, drop it and re-run the initialization script:

```sql
-- SQL Server
DROP DATABASE order_dB;
GO
-- Then re-run 01 and 02

-- MySQL
DROP DATABASE shopquanao;
-- Then re-run 03

-- PostgreSQL
DROP DATABASE auth_db;
-- Then re-run 04

-- Oracle
DROP USER audit_user CASCADE;
-- Then re-run 05
```

## Additional Resources

- [SQL Server Documentation](https://docs.microsoft.com/sql/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Oracle Documentation](https://docs.oracle.com/)

---
**Last Updated**: 2024
**Version**: 1.0
