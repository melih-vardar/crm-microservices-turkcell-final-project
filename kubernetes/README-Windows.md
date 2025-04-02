# CRM System - Windows Deployment Guide for Kubernetes

This guide explains how to deploy the CRM microservices system on Kubernetes using Minikube in Windows.

## Prerequisites

- [Minikube](https://minikube.sigs.k8s.io/docs/start/) - Install Windows version
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/) - Windows installation
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) - For Windows

## Step 1: Set Up Minikube

1. Open a PowerShell window as Administrator
2. Start Minikube:
   ```powershell
   minikube start --driver=docker --memory=4096 --cpus=2
   ```

## Step 2: Configure Docker to Use Minikube's Docker Daemon

Run this command in PowerShell:

```powershell
& minikube -p minikube docker-env --shell powershell | Invoke-Expression
```

Verify that Docker is connected to Minikube by running:

```powershell
docker ps
```

## Step 3: Build Docker Images for Your Services

We've created a PowerShell script to build all your service images:

```powershell
# Navigate to the kubernetes directory
cd kubernetes

# Run the build script
.\build-images.ps1
```

This script will:

1. Build Docker images for all your microservices
2. Tag them with the `latest` tag
3. Store them in Minikube's Docker registry

## Step 4: Deploy to Kubernetes

Use the PowerShell deployment script:

```powershell
.\deploy.ps1
```

This script will:

1. Apply all Kubernetes configurations in the correct order
2. Wait for the core infrastructure to be ready
3. Deploy all microservices

## Step 5: Access Your Application

1. Set up port forwarding for the Gateway service in a separate PowerShell window:

   ```powershell
   kubectl port-forward --namespace=crm-system service/gateway-server 8080:8080
   ```

2. Access your application at: http://localhost:8080

3. To view the Kubernetes dashboard:
   ```powershell
   minikube dashboard
   ```

## Troubleshooting

### Check Pod Status

```powershell
kubectl get pods -n crm-system
```

### View Pod Logs

```powershell
kubectl logs -n crm-system [pod-name]
```

### Describe Pod for Details

```powershell
kubectl describe pod -n crm-system [pod-name]
```

### Check if Images are Available

```powershell
docker images
```

### Restart a Failing Deployment

```powershell
kubectl rollout restart deployment -n crm-system [deployment-name]
```

### Delete and Recreate Everything

```powershell
kubectl delete namespace crm-system
kubectl apply -f namespace.yaml
# ... apply all other YAML files again
```

### Common Issues

1. **InvalidImageName errors**: Make sure your images are built correctly and available in Minikube's Docker daemon.

2. **Connection refused errors**: Ensure all services are running and healthy.

3. **Volume mount errors**: Check that PersistentVolumes are correctly provisioned.

4. **Service discovery issues**: Make sure DNS names are resolving correctly within the cluster.

## Cleaning Up

To delete all resources:

```powershell
kubectl delete namespace crm-system
```

To stop Minikube:

```powershell
minikube stop
```
