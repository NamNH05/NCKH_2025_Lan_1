@echo off
REM Start Audit Service in a separate window
REM This batch file will start the Audit Service on port 8082

cd /d "C:\UserX\Documents\GitHub\Nckh\backend\audit-service\Nckh-Hi-p"

echo.
echo ====================================================
echo   Starting Audit Service (Port 8082)
echo ====================================================
echo.
echo Service will run in this window independently.
echo To stop: Close this window or press Ctrl+C
echo.

java -jar target/audit-0.0.1-SNAPSHOT.jar --server.port=8082

pause
