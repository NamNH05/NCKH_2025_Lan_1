#!/bin/bash
set -e

# Start SQL Server
/opt/mssql/bin/sqlservr &
SERVER_PID=$!

# Give SQL Server time to initialize
for i in {1..30}; do
  if /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -d master -Q "SELECT 1" &>/dev/null; then
    echo "SQL Server is ready!"
    break
  fi
  echo "Waiting for SQL Server... ($i/30)"
  sleep 1
done

# Run initialization script
echo "Running initialization script..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -d master -i /docker-entrypoint-initdb.d/init-order-db.sql

echo "Initialization complete!"

# Keep container running by waiting for SQL Server process
wait $SERVER_PID
