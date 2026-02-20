@echo off
REM ============================================
REM SQL Database Initialization Script Runner
REM Windows PowerShell Version
REM ============================================
REM This script automatically runs all SQL initialization files
REM in the correct order for all microservices

echo ========================================
echo SQL Database Initialization Script
echo ========================================
echo.

setlocal enabledelayedexpansion

REM Get script directory
set SCRIPT_DIR=%~dp0
echo Script directory: %SCRIPT_DIR%
echo.

REM ============================================
REM 1. SQL Server - Order Service
REM ============================================
echo [1/4] Initializing SQL Server (Order Service)...
echo -----------------------------------------------

if exist "%SCRIPT_DIR%01-order-service-sqlserver.sql" (
    echo Running: 01-order-service-sqlserver.sql
    sqlcmd -S localhost -E -i "%SCRIPT_DIR%01-order-service-sqlserver.sql"
    if errorlevel 1 (
        echo [ERROR] SQL Server init failed!
        echo Make sure SQL Server is running on localhost
        echo.
    ) else (
        echo [OK] SQL Server initialized successfully
    )
) else (
    echo [WARN] 01-order-service-sqlserver.sql not found
)
echo.

REM ============================================
REM 2. MySQL - Product Service
REM ============================================
echo [2/4] Initializing MySQL (Product Service)...
echo -----------------------------------------------

if exist "%SCRIPT_DIR%03-product-service-mysql.sql" (
    echo Running: 03-product-service-mysql.sql
    mysql -u root -p < "%SCRIPT_DIR%03-product-service-mysql.sql"
    if errorlevel 1 (
        echo [ERROR] MySQL init failed!
        echo Make sure MySQL is running on localhost:3306
        echo Default user: root, password: root
        echo.
    ) else (
        echo [OK] MySQL initialized successfully
    )
) else (
    echo [WARN] 03-product-service-mysql.sql not found
)
echo.

REM ============================================
REM 3. PostgreSQL - Auth & User Services
REM ============================================
echo [3/4] Initializing PostgreSQL (Auth ^& User Services)...
echo -----------------------------------------------

if exist "%SCRIPT_DIR%04-auth-user-service-postgres.sql" (
    echo Running: 04-auth-user-service-postgres.sql
    psql -U postgres -f "%SCRIPT_DIR%04-auth-user-service-postgres.sql"
    if errorlevel 1 (
        echo [ERROR] PostgreSQL init failed!
        echo Make sure PostgreSQL is running on localhost:5432
        echo Default user: postgres, password: password
        echo.
    ) else (
        echo [OK] PostgreSQL initialized successfully
    )
) else (
    echo [WARN] 04-auth-user-service-postgres.sql not found
)
echo.

REM ============================================
REM 4. Oracle - Audit Service
REM ============================================
echo [4/4] Initializing Oracle (Audit Service)...
echo -----------------------------------------------

if exist "%SCRIPT_DIR%05-audit-service-oracle.sql" (
    echo Running: 05-audit-service-oracle.sql
    echo [INFO] Oracle setup requires manual execution
    echo Run in SQL*Plus:
    echo   sqlplus sys/password@FREEPDB1 as sysdba
    echo   @"%SCRIPT_DIR%05-audit-service-oracle.sql"
    echo.
) else (
    echo [WARN] 05-audit-service-oracle.sql not found
)
echo.

REM ============================================
REM Summary
REM ============================================
echo ========================================
echo Database Initialization Complete!
echo ========================================
echo.
echo Next steps:
echo 1. Verify all databases are initialized
echo 2. Check for any error messages above
echo 3. Run: docker ps (to verify containers)
echo 4. Start microservices
echo.

pause
