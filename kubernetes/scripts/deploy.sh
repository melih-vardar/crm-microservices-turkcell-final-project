#!/bin/bash

# Print banner
echo -e "\033[36m=======================================================\033[0m"
echo -e "\033[36m   CRM Microservices - Kubernetes Deployment Script    \033[0m"
echo -e "\033[36m=======================================================\033[0m"
echo -e "\033[32mConfig Server is configured to use:\033[0m"
echo -e "\033[33mhttps://github.com/melih-vardar/crm-microservices-turkcell-final-project-configs\033[0m"
echo -e "\033[36m=======================================================\033[0m"

# Set base directory
BASE_DIR="$(dirname "$0")/.."

# Start Minikube if it's not running
if ! minikube status | grep -q "Running"; then
  echo -e "\033[33mStarting Minikube...\033[0m"
  minikube start --driver=docker --memory=4096 --cpus=2
fi

# Set Docker environment to use Minikube's Docker daemon
echo -e "\033[33mSetting up Minikube Docker environment...\033[0m"
echo -e "\033[36mRunning: eval \$(minikube docker-env)\033[0m"
eval $(minikube docker-env)

# Check if we need to build images
buildImages=true
read -p "Do you want to build Docker images? (y/n) [default: y] " response
if [ "$response" = "n" ]; then
  buildImages=false
fi

if [ "$buildImages" = true ]; then
  # Check if build-images.sh exists and run it
  if [ -f "$BASE_DIR/scripts/build-images.sh" ]; then
    echo -e "\033[32mBuilding Docker images using build-images.sh...\033[0m"
    chmod +x "$BASE_DIR/scripts/build-images.sh"
    "$BASE_DIR/scripts/build-images.sh"
  else
    echo -e "\033[31mbuild-images.sh not found. Please build your Docker images manually.\033[0m"
    read -p "Continue with deployment? (y/n) [default: y] " continue
    if [ "$continue" = "n" ]; then
      echo -e "\033[31mDeployment cancelled.\033[0m"
      exit 1
    fi
  fi
fi

# Ask if user wants to open Minikube dashboard
read -p "Do you want to open Minikube dashboard? (y/n) [default: y] " openDashboard
if [ "$openDashboard" != "n" ]; then
  echo -e "\033[32mOpening Minikube dashboard...\033[0m"
  minikube dashboard &
fi

# Ask if user wants to proceed with Kubernetes deployments
read -p "Do you want to proceed with Kubernetes deployments? (y/n) [default: y] " proceedK8s
if [ "$proceedK8s" = "n" ]; then
  echo -e "\033[33mSkipping Kubernetes deployments...\033[0m"
else
  # Apply namespace and storage
  echo -e "\033[32mCreating namespace and storage resources...\033[0m"
  kubectl apply -f "$BASE_DIR/base/namespaces/namespace.yaml"
  kubectl apply -f "$BASE_DIR/base/storage/storage.yaml"

  # Apply databases
  echo -e "\033[32mDeploying databases...\033[0m"
  kubectl apply -f "$BASE_DIR/base/databases/databases.yaml"
  echo -e "\033[33mWaiting for database pods to start (30 seconds)...\033[0m"
  sleep 30

  # Apply Kafka
  echo -e "\033[32mDeploying Kafka...\033[0m"
  kubectl apply -f "$BASE_DIR/base/kafka/kafka.yaml"
  echo -e "\033[33mWaiting for Kafka to start (30 seconds)...\033[0m"
  sleep 30

  # Apply Monitoring (if files exist)
  if [ -f "$BASE_DIR/base/monitoring/prometheus-config.yaml" ] && [ -f "$BASE_DIR/base/monitoring/monitoring.yaml" ]; then
    echo -e "\033[32mDeploying monitoring services...\033[0m"
    kubectl apply -f "$BASE_DIR/base/monitoring/prometheus-config.yaml"
    kubectl apply -f "$BASE_DIR/base/monitoring/monitoring.yaml"
    echo -e "\033[33mWaiting for monitoring to start (15 seconds)...\033[0m"
    sleep 15
  fi

  # Apply Config Server FIRST and wait for it
  echo -e "\033[32mDeploying Config Server...\033[0m"
  kubectl apply -f "$BASE_DIR/base/infrastructure/infrastructure.yaml"
  echo -e "\033[33mWaiting for Config Server to start (90 seconds)...\033[0m"
  sleep 90

  # Verify if Config Server is running
  configServerPod=$(kubectl get pods -n crm-system -l app=config-server -o jsonpath="{.items[0].metadata.name}" 2>/dev/null)
  if [ -z "$configServerPod" ]; then
    echo -e "\033[31mWARNING: Config Server pod not found! Continuing anyway...\033[0m"
    
    # Config server not running, offer to rebuild
    read -p "Would you like to rebuild and redeploy config-server? (y/n) [default: n] " rebuildConfig
    if [ "$rebuildConfig" = "y" ]; then
      echo -e "\033[33mRebuilding config-server image...\033[0m"
      pushd "$BASE_DIR/../config-server"
      docker build -t config-server:latest .
      popd
      
      echo -e "\033[33mRedeploying config-server...\033[0m"
      kubectl delete deployment -n crm-system config-server
      kubectl apply -f "$BASE_DIR/base/infrastructure/infrastructure.yaml"
      echo -e "\033[33mWaiting for Config Server to restart (90 seconds)...\033[0m"
      sleep 90
    fi
  fi

  # Apply microservices
  echo -e "\033[32mDeploying microservices...\033[0m"
  kubectl apply -f "$BASE_DIR/base/microservices/microservices.yaml"
  echo -e "\033[33mWaiting for microservices to start (30 seconds)...\033[0m"
  sleep 30

  # Display pod status
  echo -e "\033[32mCurrent pod status:\033[0m"
  kubectl get pods -n crm-system

  # Display service access information
  echo -e "\033[36m=======================================================\033[0m"
  echo -e "\033[32m   Deployment complete! Access services at:           \033[0m"
  echo -e "\033[36m=======================================================\033[0m"

  minikubeIp=$(minikube ip)
  echo -e "\033[33mConfig Server: http://$minikubeIp:30070\033[0m"
  echo -e "\033[33mEureka Server: http://$minikubeIp:30888\033[0m"
  echo -e "\033[33mGateway Server: http://$minikubeIp:30080\033[0m"
  echo -e "\033[33mUser Service: http://$minikubeIp:30010\033[0m"
  echo -e "\033[33mCustomer Service: http://$minikubeIp:30020\033[0m"
  echo -e "\033[33mBilling Service: http://$minikubeIp:30050\033[0m"
  echo -e "\033[33mAnalytics Service: http://$minikubeIp:30071\033[0m"
  echo -e "\033[36m=======================================================\033[0m"
fi

# Ask if user wants to start port forwarding
read -p "Do you want to start port forwarding? (y/n) [default: y] " startPortForward
if [ "$startPortForward" != "n" ]; then
  echo -e "\033[32mStarting port forwarding...\033[0m"
  chmod +x "$BASE_DIR/scripts/port-forward.sh"
  "$BASE_DIR/scripts/port-forward.sh"
fi

echo -e "\033[36mFor troubleshooting, use: kubectl logs -n crm-system deployment/[service-name]\033[0m" 