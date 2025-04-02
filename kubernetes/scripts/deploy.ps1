# PowerShell script for deploying the application on Minikube

# Print banner
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "   CRM Microservices - Kubernetes Deployment Script    " -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "Config Server is configured to use:" -ForegroundColor Green
Write-Host "https://github.com/melih-vardar/crm-microservices-turkcell-final-project-configs" -ForegroundColor Yellow
Write-Host "=======================================================" -ForegroundColor Cyan

# Set base directory
$BASE_DIR = Join-Path $PSScriptRoot ".."

# Start Minikube if it's not running
$minikubeStatus = (minikube status)
if (-not ($minikubeStatus -match "Running")) {
    Write-Host "Starting Minikube..." -ForegroundColor Yellow
    minikube start --driver=docker --memory=4096 --cpus=2
}

# Set Docker environment to use Minikube's Docker daemon
Write-Host "Setting up Minikube Docker environment..." -ForegroundColor Yellow
Write-Host "Running: & minikube -p minikube docker-env --shell powershell | Invoke-Expression" -ForegroundColor Cyan
& minikube -p minikube docker-env --shell powershell | Invoke-Expression

# Check if we need to build images
$buildImages = $true
$response = Read-Host "Do you want to build Docker images? (y/n) [default: y]"
if ($response -eq "n") {
    $buildImages = $false
}

if ($buildImages) {
    # Check if build-images.ps1 exists and run it
    if (Test-Path (Join-Path $BASE_DIR "scripts/build-images.ps1")) {
        Write-Host "Building Docker images using build-images.ps1..." -ForegroundColor Green
        & (Join-Path $BASE_DIR "scripts/build-images.ps1")
    } else {
        Write-Host "build-images.ps1 not found. Please build your Docker images manually." -ForegroundColor Red
        $continue = Read-Host "Continue with deployment? (y/n) [default: y]"
        if ($continue -eq "n") {
            Write-Host "Deployment cancelled." -ForegroundColor Red
            exit
        }
    }
}

# Ask if user wants to open Minikube dashboard
$openDashboard = Read-Host "Do you want to open Minikube dashboard? (y/n) [default: y]"
if ($openDashboard -ne "n") {
    Write-Host "Opening Minikube dashboard..." -ForegroundColor Green
    Start-Job -ScriptBlock { minikube dashboard }
}

# Ask if user wants to proceed with Kubernetes deployments
$proceedK8s = Read-Host "Do you want to proceed with Kubernetes deployments? (y/n) [default: y]"
if ($proceedK8s -eq "n") {
    Write-Host "Skipping Kubernetes deployments..." -ForegroundColor Yellow
} else {
    # Apply namespace and storage
    Write-Host "Creating namespace and storage resources..." -ForegroundColor Green
    kubectl apply -f (Join-Path $BASE_DIR "base/namespaces/namespace.yaml")
    kubectl apply -f (Join-Path $BASE_DIR "base/storage/storage.yaml")

    # Apply databases
    Write-Host "Deploying databases..." -ForegroundColor Green
    kubectl apply -f (Join-Path $BASE_DIR "base/databases/databases.yaml")
    Write-Host "Waiting for database pods to start (30 seconds)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30

    # Apply Kafka
    Write-Host "Deploying Kafka..." -ForegroundColor Green
    kubectl apply -f (Join-Path $BASE_DIR "base/kafka/kafka.yaml")
    Write-Host "Waiting for Kafka to start (30 seconds)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30

    # Apply Monitoring (if files exist)
    $prometheusConfigExists = Test-Path (Join-Path $BASE_DIR "base/monitoring/prometheus-config.yaml")
    $monitoringExists = Test-Path (Join-Path $BASE_DIR "base/monitoring/monitoring.yaml")
    if ($prometheusConfigExists -and $monitoringExists) {
        Write-Host "Deploying monitoring services..." -ForegroundColor Green
        kubectl apply -f (Join-Path $BASE_DIR "base/monitoring/prometheus-config.yaml")
        kubectl apply -f (Join-Path $BASE_DIR "base/monitoring/monitoring.yaml")
        Write-Host "Waiting for monitoring to start (15 seconds)..." -ForegroundColor Yellow
        Start-Sleep -Seconds 15
    }

    # Apply Config Server FIRST and wait for it
    Write-Host "Deploying Config Server..." -ForegroundColor Green
    kubectl apply -f (Join-Path $BASE_DIR "base/infrastructure/infrastructure.yaml")
    Write-Host "Waiting for Config Server to start (90 seconds)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 90

    # Verify if Config Server is running
    $configServerPod = kubectl get pods -n crm-system -l app=config-server -o jsonpath="{.items[0].metadata.name}" 2>$null
    if (-not $configServerPod) {
        Write-Host "WARNING: Config Server pod not found! Continuing anyway..." -ForegroundColor Red
        
        # Config server çalışmıyorsa, yeniden build etme seçeneği sun
        $rebuildConfig = Read-Host "Would you like to rebuild and redeploy config-server? (y/n) [default: n]"
        if ($rebuildConfig -eq "y") {
            Write-Host "Rebuilding config-server image..." -ForegroundColor Yellow
            Push-Location (Join-Path $BASE_DIR "../config-server")
            docker build -t config-server:latest .
            Pop-Location
            
            Write-Host "Redeploying config-server..." -ForegroundColor Yellow
            kubectl delete deployment -n crm-system config-server
            kubectl apply -f (Join-Path $BASE_DIR "base/infrastructure/infrastructure.yaml")
            Write-Host "Waiting for Config Server to restart (90 seconds)..." -ForegroundColor Yellow
            Start-Sleep -Seconds 90
        }
    }

    # Apply microservices
    Write-Host "Deploying microservices..." -ForegroundColor Green
    kubectl apply -f (Join-Path $BASE_DIR "base/microservices/microservices.yaml")
    Write-Host "Waiting for microservices to start (30 seconds)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30

    # Display pod status
    Write-Host "Current pod status:" -ForegroundColor Green
    kubectl get pods -n crm-system

    # Display service access information
    Write-Host "=======================================================" -ForegroundColor Cyan
    Write-Host "   Deployment complete! Access services at:           " -ForegroundColor Green
    Write-Host "=======================================================" -ForegroundColor Cyan

    $minikubeIp = (minikube ip)
    Write-Host "Config Server: http://$minikubeIp:30070" -ForegroundColor Yellow
    Write-Host "Eureka Server: http://$minikubeIp:30888" -ForegroundColor Yellow
    Write-Host "Gateway Server: http://$minikubeIp:30080" -ForegroundColor Yellow
    Write-Host "User Service: http://$minikubeIp:30010" -ForegroundColor Yellow
    Write-Host "Customer Service: http://$minikubeIp:30020" -ForegroundColor Yellow
    Write-Host "Billing Service: http://$minikubeIp:30050" -ForegroundColor Yellow
    Write-Host "Analytics Service: http://$minikubeIp:30071" -ForegroundColor Yellow
    Write-Host "=======================================================" -ForegroundColor Cyan
}

# Ask if user wants to start port forwarding
$startPortForward = Read-Host "Do you want to start port forwarding? (y/n) [default: y]"
if ($startPortForward -ne "n") {
    Write-Host "Starting port forwarding..." -ForegroundColor Green
    & (Join-Path $BASE_DIR "scripts/port-forward.ps1")
} 