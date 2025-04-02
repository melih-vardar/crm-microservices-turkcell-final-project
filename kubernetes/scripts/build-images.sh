#!/bin/bash

# Print banner
echo -e "\033[36m=======================================================\033[0m"
echo -e "\033[36m   CRM Microservices - Docker Image Build Script      \033[0m"
echo -e "\033[36m=======================================================\033[0m"

# Set the base directory
BASE_DIR="$(dirname "$0")/.."

# Function to build a service image
build_service() {
  SERVICE_NAME=$1
  echo -e "\033[0;32mBuilding ${SERVICE_NAME}...\033[0m"
  
  # Navigate to service directory
  SERVICE_DIR="${BASE_DIR}/../${SERVICE_NAME}"
  
  if [ ! -d "$SERVICE_DIR" ]; then
    echo -e "\033[0;31mError: Directory for ${SERVICE_NAME} not found at ${SERVICE_DIR}\033[0m"
    return 1
  fi
  
  # Push to service directory
  pushd "$SERVICE_DIR" > /dev/null
  
  # Build the Docker image
  echo -e "\033[0;36mRunning: docker build -t ${SERVICE_NAME}:latest .\033[0m"
  if ! docker build -t "${SERVICE_NAME}:latest" .; then
    echo -e "\033[0;31mError: Failed to build ${SERVICE_NAME}\033[0m"
    popd > /dev/null
    return 1
  fi
  
  # Return to original directory
  popd > /dev/null
  return 0
}

# Check if Minikube is running
if ! minikube status | grep -q "Running"; then
  echo -e "\033[0;31mError: Minikube is not running. Please start Minikube first.\033[0m"
  exit 1
fi

# Set up Minikube Docker environment
echo -e "\033[0;33mSetting up Minikube Docker environment...\033[0m"
eval $(minikube docker-env)

# Verify Docker environment
if ! docker info | grep -q "minikube"; then
  echo -e "\033[0;31mError: Not using Minikube's Docker daemon. Please run: eval \$(minikube docker-env)\033[0m"
  exit 1
fi

# List of services to build
SERVICES=(
  "config-server"
  "discovery-server"
  "gateway-server"
  "user-service"
  "customer-service"
  "billing-service"
  "analytics-service"
)

# Build all services
FAILED_SERVICES=()
for service in "${SERVICES[@]}"; do
  if ! build_service "$service"; then
    FAILED_SERVICES+=("$service")
  fi
done

# Display results
echo -e "\033[36m=======================================================\033[0m"
echo -e "\033[32mBuild Summary:\033[0m"
echo -e "\033[36m=======================================================\033[0m"

# List all built images for verification
echo -e "\033[0;32mBuilt images:\033[0m"
docker images | grep -E 'config-server|discovery-server|gateway-server|user-service|customer-service|billing-service|analytics-service'

# Report any failures
if [ ${#FAILED_SERVICES[@]} -ne 0 ]; then
  echo -e "\033[0;31mFailed to build the following services:\033[0m"
  printf '%s\n' "${FAILED_SERVICES[@]}"
  exit 1
else
  echo -e "\033[0;32mAll images built successfully!\033[0m"
fi

echo -e "\033[36m=======================================================\033[0m" 