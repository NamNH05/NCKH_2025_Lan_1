# Order Service - Docker & Database Setup

## Overview
This directory contains the **Order Service** with its own isolated Docker setup for SQL Server 2022.

## Project Structure
```
order-service/
├── docker-compose.yml              # SQL Server container for Order Service
├── Dockerfile                       # Order Service application image
├── pom.xml                         # Maven dependencies
├── src/
│   ├── main/
│   │   ├── java/com/example/order_service/
│   │   └── resources/application.properties
│   └── test/
├── database/
│   ├── sqlserver-init.sql         # Database initialization script
│   └── create-order-db.sql        # Database and table creation
└── README.md
```

## Quick Start

### Option 1: Standalone (Order Service Only)
Start only the SQL Server database for this service:

```bash
cd backend/order-service/Nckh-C-ng
docker-compose up -d
```

Verify SQL Server is running:
```bash
docker ps --filter "name=sqlserver"
```

Start the Order Service application:
```bash
mvn spring-boot:run
```

### Option 2: All Services Together
Use the root docker-compose file to start all services at once:

```bash
cd ../../..  # Go to project root
docker-compose -f docker-compose-all-services.yml up -d
./scripts/start-project.bat  # Windows
./scripts/start-project.sh   # Linux/Mac
```

## Database Configuration

### SQL Server Details
- **Container Name**: sqlserver-order-db
- **Port**: 1433
- **Username**: sa
- **Password**: SqlServer@2026
- **Database**: order_dB
- **Edition**: SQL Server 2022 Express

### Connection String
```
jdbc:sqlserver://localhost:1433;databaseName=order_dB;encrypt=true;trustServerCertificate=true;loginTimeout=30
```

### Tables
- `Cart` - Shopping cart storage
- `CartItem` - Individual items in cart
- `Orders` - Order records
- `OrderItem` - Order line items
- `Payment` - Payment transactions
- `Shipment` - Shipment tracking

## Management Commands

### Check Container Status
```bash
docker ps --filter "name=sqlserver"
docker logs sqlserver-order-db
```

### Connect to SQL Server
```bash
docker exec sqlserver-order-db /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "SqlServer@2026" -C
```

### Stop/Start Services
```bash
# Stop only SQL Server
docker-compose down

# Stop all services
docker-compose -f docker-compose-all-services.yml down
```

### View Database
```bash
docker exec sqlserver-order-db /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "SqlServer@2026" -C \
  -Q "SELECT name FROM sys.databases WHERE name = 'order_dB'"
```

## Application Properties
Location: `src/main/resources/application.properties`

Key settings:
- **Server Port**: 8091
- **DDL Auto**: none (schema pre-created)
- **Hibernate Dialect**: SQLServerDialect
- **Connection Timeout**: 30000ms
- **Max Pool Size**: 5
- **Defer Datasource Initialization**: true

## Troubleshooting

### SQL Server Won't Start
1. Check Docker is running: `docker ps`
2. Verify no port conflicts: `docker ps -a`
3. Remove old container: `docker rm -f sqlserver-order-db`
4. Restart: `docker-compose up -d`

### Connection Failed
1. Wait 30-45 seconds for SQL Server to initialize
2. Test connection: `docker logs sqlserver-order-db`
3. Verify database exists: See "Connect to SQL Server" section above
4. Check credentials in application.properties match docker-compose.yml

### Application Won't Start
1. Ensure SQL Server is healthy: `docker ps` (should show "healthy")
2. Wait 45+ seconds after docker-compose starts
3. Check logs: `docker logs sqlserver-order-db`
4. Verify database tables exist in order_dB
5. Run: `mvn clean spring-boot:run`

## Network Configuration

### Standalone Mode
- SQL Server accessible at: `localhost:1433`
- Order Service accessible at: `http://localhost:8091`

### All Services Mode
- Uses `app-network` bridge network for inter-service communication
- SQL Server accessible from Order Service: `sqlserver:1433` (Docker DNS)
- SQL Server accessible from host: `localhost:1433`

## Files Reference

| File | Purpose |
|------|---------|
| `docker-compose.yml` | Defines SQL Server container (standalone mode) |
| `database/sqlserver-init.sql` | Auto-initialization script (optional) |
| `database/create-order-db.sql` | Manual database creation script |
| `src/main/resources/application.properties` | Spring Boot configuration |
| `pom.xml` | Maven project definition with Spring Boot & JDBC |

## Next Steps

1. ✅ SQL Server running in Docker
2. ✅ Order Service compiles and starts
3. ⏳ Test API endpoints: `http://localhost:8091`
4. ⏳ Integrate with API Gateway (port 3000)
5. ⏳ Test full Order Service functionality

## Support

For issues or questions:
- Check logs: `docker logs sqlserver-order-db`
- Review application.properties settings
- Verify pom.xml has correct dependencies
- Ensure Java 17+ is installed: `java -version`
