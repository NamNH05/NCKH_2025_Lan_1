#!/bin/bash

# Run database initialization  
docker exec sqlserver-order-db /opt/mssql-tools18/bin/sqlcmd -S . -U sa -P "P@ssw0rd2026" -i /init-order-db.sql

echo "Database initialization complete!"
