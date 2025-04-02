#!/bin/bash

# Set the base directory
BASE_DIR=".."

# Function to build a service image
build_service() {
  SERVICE_NAME=$1
  echo -e "\033[0;32mBuilding ${SERVICE_NAME}...\033[0m"
  
  # Navigate to service directory
  SERVICE_DIR="${BASE_DIR}/${SERVICE_NAME}"
  
  if [ ! -d "$SERVICE_DIR" ]; then
    echo -e "\033[0;31mError: Directory for ${SERVICE_NAME} not found at ${SERVICE_DIR}\033[0m"
    return 1
  fi
  
  # Push to service directory
  pushd "$SERVICE_DIR" > /dev/null
  
  # Build the Docker image
  echo -e "\033[0;36mRunning: docker build -t ${SERVICE_NAME}:latest .\033[0m"
  docker build -t "${SERVICE_NAME}:latest" .
  
  # Return to original directory
  popd > /dev/null
}

# Make sure Minikube's Docker daemon is being used
echo -e "\033[0;33mSetting up Minikube Docker environment...\033[0m"
echo -e "\033[0;33mRun this command before continuing: eval \$(minikube docker-env)\033[0m"
echo -e "\033[0;33mVerify with: docker ps\033[0m"
echo -e "\033[0;33mPress any key to continue once you've run the command...\033[0m"
read -n 1 -s

# Build all services
build_service "config-server"
build_service "discovery-server"
build_service "gateway-server"
build_service "user-service"
build_service "customer-service"
build_service "billing-service"
build_service "analytics-service"

# List all built images for verification
echo -e "\033[0;32mBuilt images:\033[0m"
docker images | grep -E 'config-server|discovery-server|gateway-server|user-service|customer-service|billing-service|analytics-service'

echo -e "\033[0;32mAll images built successfully!\033[0m" 