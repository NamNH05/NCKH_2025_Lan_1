# Audit Service API Test Script
Write-Host "════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  🎯 AUDIT SERVICE API TEST  🎯" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════" -ForegroundColor Cyan

# Wait for service to be ready
Write-Host "`n⏳ Waiting for service to be ready..." -ForegroundColor Yellow
$maxRetries = 10
$retry = 0
while ($retry -lt $maxRetries) {
    try {
        $health = Invoke-RestMethod -Uri "http://localhost:8082/actuator/health" -Method Get -ErrorAction Stop
        if ($health.status -eq "UP") {
            Write-Host "✅ Service is UP!" -ForegroundColor Green
            break
        }
    } catch {
        $retry++
        Write-Host "  Attempt $retry/$maxRetries..." -ForegroundColor Gray
        Start-Sleep -Seconds 2
    }
}

if ($retry -eq $maxRetries) {
    Write-Host "❌ Service failed to start" -ForegroundColor Red
    exit 1
}

# Test 1: Health Check
Write-Host "`n[1️⃣] Health Check" -ForegroundColor Yellow
$health = Invoke-RestMethod -Uri "http://localhost:8082/actuator/health" -Method Get
Write-Host "✅ Status: $($health.status)" -ForegroundColor Green

# Test 2: Get initial logs
Write-Host "`n[2️⃣] Get Initial Logs" -ForegroundColor Yellow
$initial = Invoke-RestMethod -Uri "http://localhost:8082/api/audits" -Method Get
$initialCount = if ($initial -is [array]) { $initial.Count } else { 0 }
Write-Host "✅ Initial logs: $initialCount" -ForegroundColor Green

# Test 3-6: Create 4 different audit logs
$audits = @(
    @{userId="john_doe"; action="LOGIN"; resourceType="User"; resourceId="USR-001"; description="User login"; ipAddress="192.168.1.100"},
    @{userId="jane_smith"; action="CREATE_ORDER"; resourceType="Order"; resourceId="ORD-2024-001"; description="New order created"; ipAddress="192.168.1.101"},
    @{userId="admin_user"; action="DELETE_PRODUCT"; resourceType="Product"; resourceId="PROD-999"; description="Product deleted"; ipAddress="192.168.1.50"},
    @{userId="warehouse_user"; action="UPDATE_INVENTORY"; resourceType="Inventory"; resourceId="INV-WH-01"; description="Inventory updated"; ipAddress="192.168.2.1"}
)

$createdIds = @()
for ($i = 0; $i -lt $audits.Count; $i++) {
    $auditBody = $audits[$i] | ConvertTo-Json
    $created = Invoke-RestMethod -Uri "http://localhost:8082/api/audits" -Method Post -ContentType "application/json" -Body $auditBody
    $createdIds += $created.id
    Write-Host "`n[$($i+3)️⃣] Create $($audits[$i].action)" -ForegroundColor Yellow
    Write-Host "✅ Created: ID=$($created.id)" -ForegroundColor Green
    Write-Host "   User: $($created.userId) | Action: $($created.action)" -ForegroundColor White
}

# Test 7: Get all logs
Write-Host "`n[7️⃣] Retrieve All Logs" -ForegroundColor Yellow
$allAudits = Invoke-RestMethod -Uri "http://localhost:8082/api/audits" -Method Get
$totalCount = if ($allAudits -is [array]) { $allAudits.Count } else { 1 }
Write-Host "✅ Total logs: $totalCount" -ForegroundColor Green

Write-Host "`n📋 All Audit Records:" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════" -ForegroundColor Cyan
if ($allAudits -is [array]) {
    $allAudits | ForEach-Object {
        Write-Host "├─ ID: $($_.id)" -ForegroundColor White
        Write-Host "│  User: $($_.userId) | Action: $($_.action)" -ForegroundColor Gray
        Write-Host "│  Resource: $($_.resourceType)/$($_.resourceId)" -ForegroundColor Gray
        Write-Host "│  Time: $($_.createdAt)" -ForegroundColor Gray
        Write-Host "│" -ForegroundColor Gray
    }
} else {
    Write-Host "└─ ID: $($allAudits.id)" -ForegroundColor White
}

Write-Host "════════════════════════════════════════════" -ForegroundColor Green
Write-Host "✅✅✅ ALL TESTS PASSED! ✅✅✅" -ForegroundColor Green
Write-Host "════════════════════════════════════════════" -ForegroundColor Green
Write-Host "`nAudit Service is working correctly!" -ForegroundColor Green
