# PowerShell script for building Docker images in Windows environment

# Print banner
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "   CRM Microservices - Docker Image Build Script      " -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan

# Set the base directory
$BASE_DIR = Join-Path $PSScriptRoot ".."

# Function to build a service image
function Build-ServiceImage {
    param (
        [string]$ServiceName
    )
    
    Write-Host "Building $ServiceName..." -ForegroundColor Green
    
    # Navigate to service directory
    $serviceDir = Join-Path -Path $BASE_DIR -ChildPath ".." | Join-Path -ChildPath $ServiceName
    
    if (-not (Test-Path $serviceDir)) {
        Write-Host "Error: Directory for $ServiceName not found at $serviceDir" -ForegroundColor Red
        return $false
    }
    
    # Check if Dockerfile exists
    if (-not (Test-Path (Join-Path $serviceDir "Dockerfile"))) {
        Write-Host "Error: Dockerfile not found in $serviceDir" -ForegroundColor Red
        return $false
    }
    
    Push-Location $serviceDir
    
    # Build the Docker image
    Write-Host "Running: docker build -t $($ServiceName):latest ." -ForegroundColor Cyan
    $buildResult = docker build -t "$($ServiceName):latest" .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: Failed to build $ServiceName" -ForegroundColor Red
        Pop-Location
        return $false
    }
    
    Pop-Location
    return $true
}

# Check if Minikube is running
$minikubeStatus = minikube status
if (-not ($minikubeStatus -match "Running")) {
    Write-Host "Error: Minikube is not running. Please start Minikube first." -ForegroundColor Red
    exit 1
}

# Set up Minikube Docker environment
Write-Host "Setting up Minikube Docker environment..." -ForegroundColor Yellow
& minikube -p minikube docker-env --shell powershell | Invoke-Expression

# Verify Docker environment
$dockerInfo = docker info
if (-not ($dockerInfo -match "minikube")) {
    Write-Host "Error: Not using Minikube's Docker daemon. Please run: & minikube -p minikube docker-env --shell powershell | Invoke-Expression" -ForegroundColor Red
    exit 1
}

# List of services to build
$services = @(
    "config-server",
    "discovery-server",
    "gateway-server",
    "user-service",
    "customer-service",
    "billing-service",
    "analytics-service"
)

# Build all services
$failedServices = @()
foreach ($service in $services) {
    if (-not (Build-ServiceImage -ServiceName $service)) {
        $failedServices += $service
    }
}

# Display results
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "   Build Summary:                                      " -ForegroundColor Green
Write-Host "=======================================================" -ForegroundColor Cyan

# List all built images for verification
Write-Host "Built images:" -ForegroundColor Green
docker images | Select-String -Pattern "config-server|discovery-server|gateway-server|user-service|customer-service|billing-service|analytics-service"

# Report any failures
if ($failedServices.Count -gt 0) {
    Write-Host "Failed to build the following services:" -ForegroundColor Red
    $failedServices | ForEach-Object { Write-Host $_ -ForegroundColor Red }
    exit 1
} else {
    Write-Host "All images built successfully!" -ForegroundColor Green
}

Write-Host "=======================================================" -ForegroundColor Cyan 