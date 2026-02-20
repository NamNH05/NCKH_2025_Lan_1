@echo off
cd /d e:\Nghien_cuu_kh\Nckh\api-gateway
echo Installing npm dependencies...
call npm install
echo.
echo Starting API Gateway...
call npm start
pause
