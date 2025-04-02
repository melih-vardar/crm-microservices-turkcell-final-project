# PowerShell script for deploying the application on Minikube

# Print banner
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "   CRM Microservices - Kubernetes Deployment Script    " -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "Config Server is configured to use:" -ForegroundColor Green
Write-Host "https://github.com/melih-vardar/crm-microservices-turkcell-final-project-configs" -ForegroundColor Yellow
Write-Host "=======================================================" -ForegroundColor Cyan

# Kubernetes dağıtımı yapılıp yapılmayacağını sorma
$skipKubernetes = Read-Host "Do you want to skip Kubernetes deployment and go directly to port forwarding? (y/n) [default: n]"
if ($skipKubernetes -eq "y") {
    Write-Host "Skipping Kubernetes deployment, proceeding directly to port forwarding..." -ForegroundColor Yellow
}
else {
    # Start Minikube if it's not running
    $minikubeStatus = (minikube status)
    if (-not ($minikubeStatus -match "Running")) {
        Write-Host "Starting Minikube..." -ForegroundColor Yellow
        minikube start --driver=docker --memory=4096 --cpus=2
    }

    # Set Docker environment to use Minikube's Docker daemon
    Write-Host "Setting Docker environment to use Minikube's Docker daemon..." -ForegroundColor Yellow
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
        if (Test-Path "build-images.ps1") {
            Write-Host "Building Docker images using build-images.ps1..." -ForegroundColor Green
            & .\build-images.ps1
        } else {
            Write-Host "build-images.ps1 not found. Please build your Docker images manually." -ForegroundColor Red
            $continue = Read-Host "Continue with deployment? (y/n) [default: y]"
            if ($continue -eq "n") {
                Write-Host "Deployment cancelled." -ForegroundColor Red
                exit
            }
        }
    }

    # Apply namespace and storage
    Write-Host "Creating namespace and storage resources..." -ForegroundColor Green
    kubectl apply -f namespace.yaml
    kubectl apply -f storage.yaml

    # Apply databases
    Write-Host "Deploying databases..." -ForegroundColor Green
    kubectl apply -f databases.yaml
    Write-Host "Waiting for database pods to start (30 seconds)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30

    # Apply Kafka
    Write-Host "Deploying Kafka..." -ForegroundColor Green
    kubectl apply -f kafka.yaml
    Write-Host "Waiting for Kafka to start (30 seconds)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30

    # Apply Monitoring (if files exist)
    $prometheusConfigExists = Test-Path "prometheus-config.yaml"
    $monitoringExists = Test-Path "monitoring.yaml"
    if ($prometheusConfigExists -and $monitoringExists) {
        Write-Host "Deploying monitoring services..." -ForegroundColor Green
        kubectl apply -f prometheus-config.yaml
        kubectl apply -f monitoring.yaml
        Write-Host "Waiting for monitoring to start (15 seconds)..." -ForegroundColor Yellow
        Start-Sleep -Seconds 15
    }

    # Apply Config Server FIRST and wait for it
    Write-Host "Deploying Config Server..." -ForegroundColor Green
    kubectl apply -f infrastructure.yaml
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
            Push-Location ..\config-server
            docker build -t config-server:latest .
            Pop-Location
            
            Write-Host "Redeploying config-server..." -ForegroundColor Yellow
            kubectl delete deployment -n crm-system config-server
            kubectl apply -f infrastructure.yaml
            Write-Host "Waiting for Config Server to restart (90 seconds)..." -ForegroundColor Yellow
            Start-Sleep -Seconds 90
        }
    }

    # Deploy microservices
    Write-Host "Deploying microservices..." -ForegroundColor Green
    kubectl apply -f microservices.yaml

    # Wait for all deployments to become ready
    Write-Host "Waiting for all services to start (may take a few minutes)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 60

    # Display pod status
    Write-Host "Current pod status:" -ForegroundColor Green
    kubectl get pods -n crm-system

    # Display service access information
    Write-Host "=======================================================" -ForegroundColor Cyan
    Write-Host "   Deployment complete! Access services at:           " -ForegroundColor Green
    Write-Host "=======================================================" -ForegroundColor Cyan

    $minikubeIp = minikube ip
    Write-Host "Config Server: http://$minikubeIp:30070" -ForegroundColor Yellow
    Write-Host "Eureka Server: http://$minikubeIp:30888" -ForegroundColor Yellow
    Write-Host "Gateway Server: http://$minikubeIp:30080" -ForegroundColor Yellow
    Write-Host "User Service: http://$minikubeIp:30010" -ForegroundColor Yellow
    Write-Host "Customer Service: http://$minikubeIp:30020" -ForegroundColor Yellow
    Write-Host "Billing Service: http://$minikubeIp:30050" -ForegroundColor Yellow
    Write-Host "Analytics Service: http://$minikubeIp:30071" -ForegroundColor Yellow
    Write-Host "=======================================================" -ForegroundColor Cyan
}

# Port forwarding sorgulama
$portForward = Read-Host "Do you want to set up port forwarding for Gateway and Discovery servers? (y/n) [default: y]"
if (-not ($portForward -eq "n")) {
    Write-Host "Setting up port forwarding..." -ForegroundColor Green
    Write-Host "Gateway Server will be accessible at: http://localhost:8080" -ForegroundColor Yellow
    Write-Host "Discovery Server will be accessible at: http://localhost:8888" -ForegroundColor Yellow
    
    # Yeni PowerShell pencereleri açarak port forwarding başlatma
    Start-Process powershell -ArgumentList "-NoExit -Command kubectl port-forward --namespace=crm-system service/gateway-server 8080:8080"
    Start-Process powershell -ArgumentList "-NoExit -Command kubectl port-forward --namespace=crm-system service/discovery-server 8888:8888"
    
    Write-Host "Port forwarding started in separate windows. Do not close those windows." -ForegroundColor Green
}

# Prometheus ve Grafana için port forwarding sorgulama
$monitoringForward = Read-Host "Do you want to set up port forwarding for Prometheus and Grafana? (y/n) [default: y]"
if (-not ($monitoringForward -eq "n")) {
    Write-Host "Setting up port forwarding for monitoring..." -ForegroundColor Green
    Write-Host "Prometheus will be accessible at: http://localhost:9090" -ForegroundColor Yellow
    Write-Host "Grafana will be accessible at: http://localhost:3000" -ForegroundColor Yellow
    
    # Yeni PowerShell pencereleri açarak port forwarding başlatma
    Start-Process powershell -ArgumentList "-NoExit -Command kubectl port-forward --namespace=crm-system service/prometheus 9090:9090"
    Start-Process powershell -ArgumentList "-NoExit -Command kubectl port-forward --namespace=crm-system service/grafana 3000:3000"
    
    Write-Host "Monitoring port forwarding started in separate windows. Do not close those windows." -ForegroundColor Green
    Write-Host "Grafana credentials - Username: admin, Password: admin" -ForegroundColor Yellow
}

# Open minikube dashboard
$openDashboard = Read-Host "Do you want to open Minikube dashboard? (y/n) [default: y]"
if (-not ($openDashboard -eq "n")) {
    Write-Host "Opening Minikube dashboard..." -ForegroundColor Green
    Start-Process powershell -ArgumentList "-Command minikube dashboard"
}

Write-Host "For troubleshooting, use: kubectl logs -n crm-system deployment/[service-name]" -ForegroundColor Cyan 