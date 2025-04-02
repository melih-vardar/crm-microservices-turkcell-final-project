# CRM System - Kubernetes Deployment Guide

This guide explains how to deploy the CRM microservices system on Kubernetes using Minikube.

## Prerequisites

- [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- [Docker](https://www.docker.com/get-started)

## Project Structure

The Kubernetes configuration is divided into several files:

- `namespace.yaml` - Creates a dedicated namespace for the CRM system
- `storage.yaml` - Sets up persistent volumes and claims for all databases
- `prometheus-config.yaml` - ConfigMap for Prometheus monitoring
- `databases.yaml` - Database deployments for all services
- `kafka.yaml` - Kafka messaging system
- `monitoring.yaml` - Prometheus and Grafana for system monitoring
- `infrastructure.yaml` - Core infrastructure services (Config Server, Discovery Server, Gateway)
- `microservices.yaml` - Business microservices (User, Customer, Billing, Analytics)

## Deployment Steps

### 1. Start Minikube

```bash
minikube start --memory=4096 --cpus=2
```

### 2. Build Docker Images

Before deploying to Kubernetes, you need to build the Docker images for all services:

```bash
# Make the build script executable
chmod +x build-images.sh

# Run the build script
./build-images.sh
```

Ensure that you are using the Minikube Docker daemon:

```bash
eval $(minikube docker-env)
```

### 3. Deploy the System

Use the deploy script to apply all configurations:

```bash
# Make the deploy script executable
chmod +x deploy.sh

# Run the deploy script
./deploy.sh
```

### 4. Access the Services

The deploy script sets up port forwarding to access the Gateway service on port 8080:

```
http://localhost:8080
```

For Grafana monitoring dashboard:

```bash
kubectl port-forward --namespace=crm-system service/grafana 3000:3000
```

Access at: http://localhost:3000 (username: admin, password: admin)

For Prometheus:

```bash
kubectl port-forward --namespace=crm-system service/prometheus 9090:9090
```

Access at: http://localhost:9090

## Verifying the Deployment

Check the status of all pods:

```bash
kubectl get pods -n crm-system
```

Check service endpoints:

```bash
kubectl get endpoints -n crm-system
```

View logs for a specific service:

```bash
kubectl logs -n crm-system deployment/[service-name]
```

## Cleaning Up

To delete all resources:

```bash
kubectl delete namespace crm-system
```

To stop Minikube:

```bash
minikube stop
```

## Troubleshooting

- If pods are pending, check persistent volume provisioning
- Check pod logs for detailed error information
- Ensure ports are correctly configured for service communication
- Verify all environment variables are correctly set in deployments
