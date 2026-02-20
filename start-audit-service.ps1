# Start Audit Service in a separate PowerShell window
# This script opens a new window and starts the Audit Service independently

$auditServicePath = "C:\UserX\Documents\GitHub\Nckh\backend\audit-service\Nckh-Hi-p"
$jarFile = "$auditServicePath\target\audit-0.0.1-SNAPSHOT.jar"

# Check if JAR file exists
if (-Not (Test-Path $jarFile)) {
    Write-Host "❌ JAR file not found at: $jarFile" -ForegroundColor Red
    Write-Host "Please build the project first: mvn clean package -DskipTests" -ForegroundColor Yellow
    exit 1
}

Write-Host "Starting Audit Service in a new window..." -ForegroundColor Green
Write-Host "Service will run on: http://localhost:8082" -ForegroundColor Cyan
Write-Host ""

# Start service in a new PowerShell window
Start-Process powershell -ArgumentList @"
    `$host.UI.RawUI.WindowTitle = 'Audit Service (Port 8082) - Running'
    cd '$auditServicePath'
    Write-Host '==============================================' -ForegroundColor Cyan
    Write-Host '  🎯 Audit Service Running on Port 8082' -ForegroundColor Cyan
    Write-Host '==============================================' -ForegroundColor Cyan
    Write-Host ''
    Write-Host 'Health check: http://localhost:8082/actuator/health' -ForegroundColor Yellow
    Write-Host 'API Endpoint: http://localhost:8082/api/audits' -ForegroundColor Yellow
    Write-Host ''
    Write-Host 'Press Ctrl+C to stop the service' -ForegroundColor Gray
    Write-Host ''
    java -jar target/audit-0.0.1-SNAPSHOT.jar --server.port=8082 --logging.level.root=INFO
"@

Write-Host "✅ Service started in new window!" -ForegroundColor Green
Write-Host "You can now test the API in another terminal." -ForegroundColor Cyan
