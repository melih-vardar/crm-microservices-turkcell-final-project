# PowerShell script for building Docker images in Windows environment

# Set the base directory
$BASE_DIR = ".."

# Function to build a service image
function Build-ServiceImage {
    param (
        [string]$ServiceName
    )
    
    Write-Host "Building $ServiceName..." -ForegroundColor Green
    
    # Navigate to service directory
    $serviceDir = Join-Path -Path $BASE_DIR -ChildPath $ServiceName
    
    if (-not (Test-Path $serviceDir)) {
        Write-Host "Error: Directory for $ServiceName not found at $serviceDir" -ForegroundColor Red
        return
    }
    
    Push-Location $serviceDir
    
    # Build the Docker image
    Write-Host "Running: docker build -t $($ServiceName):latest ." -ForegroundColor Cyan
    docker build -t "$($ServiceName):latest" .
    
    # Return to original directory
    Pop-Location
}

# Make sure Minikube's Docker daemon is being used
Write-Host "Setting up Minikube Docker environment..." -ForegroundColor Yellow
Write-Host "Run this command before continuing: & minikube -p minikube docker-env --shell powershell | Invoke-Expression" -ForegroundColor Yellow
Write-Host "Verify with: docker ps" -ForegroundColor Yellow
Write-Host "Press any key to continue once you've run the command..." -ForegroundColor Yellow
$null = $host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

# Build all services
Build-ServiceImage -ServiceName "config-server"
Build-ServiceImage -ServiceName "discovery-server"
Build-ServiceImage -ServiceName "gateway-server"
Build-ServiceImage -ServiceName "user-service"
Build-ServiceImage -ServiceName "customer-service"
Build-ServiceImage -ServiceName "billing-service"
Build-ServiceImage -ServiceName "analytics-service"

# List all built images for verification
Write-Host "Built images:" -ForegroundColor Green
docker images | Select-String -Pattern "config-server|discovery-server|gateway-server|user-service|customer-service|billing-service|analytics-service"

Write-Host "All images built successfully!" -ForegroundColor Green 