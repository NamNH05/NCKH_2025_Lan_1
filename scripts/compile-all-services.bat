@echo off
REM ============================================
REM Security Hardening - Compile All Services
REM ============================================

echo.
echo ========================================
echo Compiling Audit Service...
echo ========================================
cd /d e:\Nghien_cuu_kh\Nckh\backend\audit-service\Nckh-Hi-p
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Audit Service compilation failed
    exit /b 1
)

echo.
echo ========================================
echo Compiling Product Service...
echo ========================================
cd /d e:\Nghien_cuu_kh\Nckh\backend\product-service\Tien\Tien
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Product Service compilation failed
    exit /b 1
)

echo.
echo ========================================
echo Compiling Order Service...
echo ========================================
cd /d e:\Nghien_cuu_kh\Nckh\backend\order-service\Nckh-C-ng
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Order Service compilation failed
    exit /b 1
)

echo.
echo ========================================
echo ALL SERVICES COMPILED SUCCESSFULLY!
echo ========================================
echo.
echo Next steps:
echo 1. Build Docker images
echo 2. Run security-test.sh
echo 3. Deploy services
echo.
