# PowerShell script for port forwarding services

# Print banner
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "   CRM Microservices - Port Forwarding Script         " -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan

# Function to start port forwarding
function Start-PortForward {
    param (
        [string]$ServiceName,
        [int]$LocalPort,
        [int]$RemotePort
    )
    
    Write-Host "Starting port forward for $ServiceName..." -ForegroundColor Green
    Write-Host "Local port: $LocalPort, Remote port: $RemotePort" -ForegroundColor Yellow
    
    # Start port forwarding in background
    $job = Start-Job -ScriptBlock {
        param($ServiceName, $LocalPort, $RemotePort)
        kubectl port-forward -n crm-system svc/$ServiceName $LocalPort`:$RemotePort
    } -ArgumentList $ServiceName, $LocalPort, $RemotePort
    
    return $job
}

# Start port forwarding for each service
$jobs = @()

# Gateway Server
$jobs += Start-PortForward -ServiceName "gateway-server" -LocalPort 8080 -RemotePort 8080
Write-Host "Gateway Server will be accessible at: http://localhost:8080" -ForegroundColor Green

# Discovery Server
$jobs += Start-PortForward -ServiceName "discovery-server" -LocalPort 8888 -RemotePort 8888
Write-Host "Discovery Server will be accessible at: http://localhost:8888" -ForegroundColor Green

# Grafana
$jobs += Start-PortForward -ServiceName "grafana" -LocalPort 3000 -RemotePort 3000
Write-Host "Grafana will be accessible at: http://localhost:3000" -ForegroundColor Green
Write-Host "Grafana credentials - Username: admin, Password: admin" -ForegroundColor Yellow

# Prometheus
$jobs += Start-PortForward -ServiceName "prometheus-server" -LocalPort 9090 -RemotePort 9090
Write-Host "Prometheus will be accessible at: http://localhost:9090" -ForegroundColor Green

Write-Host "`nPort forwarding started. Press Ctrl+C to stop all port forwards." -ForegroundColor Yellow
Write-Host "To stop port forwarding manually, run: Stop-Job -Job $jobs" -ForegroundColor Yellow

# Wait for all jobs
$jobs | Wait-Job 