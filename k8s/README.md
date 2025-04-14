# Kubernetes Deployment for CRM Microservices

This directory contains Kubernetes manifests for deploying the CRM Microservices architecture to a Kubernetes cluster.

## Architecture Overview

The architecture consists of:

- **Core Infrastructure Services**:

  - Config Server
  - Discovery Server (Eureka)
  - API Gateway
  - Kafka Message Broker
  - Redis Cache

- **Data Services**:

  - PostgreSQL databases (one per service)
  - MongoDB (for Customer Support Service)

- **Microservices**:

  - User Service
  - Customer Service
  - Billing Service
  - Contract Service
  - Plan Service
  - Customer Support Service
  - Analytics Service
  - Notification Service

- **Monitoring**:
  - Prometheus
  - Grafana

## Prerequisites

- Kubernetes cluster (Minikube, Docker Desktop, or cloud provider)
- `kubectl` CLI installed and configured
- Docker Hub account for storing container images

## Configuration Repository

The configuration for all microservices is stored in a separate Git repository. The Config Server connects to this repository to provide centralized configuration management:

- **Configuration Repository**: [https://github.com/melih-vardar/crm-microservices-turkcell-final-project-configs](https://github.com/melih-vardar/crm-microservices-turkcell-final-project-configs)

This repository contains environment-specific configuration files (dev, prod, local) for each microservice in the system.

## Deployment Instructions

### Manual Deployment

1. Create the namespace and infrastructure:

   ```bash
   kubectl apply -f 00-namespace.yaml
   kubectl apply -f 01-configmaps.yaml
   kubectl apply -f 02-storage.yaml
   kubectl apply -f 03-databases.yaml
   kubectl apply -f 04-kafka.yaml
   kubectl apply -f 05-monitoring.yaml
   kubectl apply -f 07-config-maps.yaml
   ```

2. Deploy core services and wait for them to be ready:

   ```bash
   kubectl apply -f 06-core-services.yaml

   # Wait for core services to be ready
   kubectl get pods -n crm-system -l app=config-server -w
   kubectl get pods -n crm-system -l app=discovery-server -w
   ```

3. Deploy microservices:

   ```bash
   kubectl apply -f 08-microservices.yaml
   ```

### GitHub Actions Deployment

This project includes a GitHub Actions workflow for automated CI/CD. To use it:

1. Add the following secrets to your GitHub repository:

   - `DOCKERHUB_USERNAME`: Your Docker Hub username
   - `DOCKERHUB_TOKEN`: Your Docker Hub access token
   - `KUBECONFIG`: The base64-encoded contents of your kubeconfig file

2. Push changes to the main branch to trigger the workflow, or manually trigger it from the Actions tab.

## Accessing the Services

- **API Gateway**: Acts as the single entry point for all services.

  ```bash
  # Get the external IP of the gateway
  kubectl get ingress gateway-ingress -n crm-system
  ```

- **Monitoring**:

  ```bash
  # Port-forward for local access
  kubectl port-forward svc/prometheus -n crm-system 9090:9090
  kubectl port-forward svc/grafana -n crm-system 3000:3000
  ```

- **Eureka Dashboard**:
  ```bash
  kubectl port-forward svc/discovery-server -n crm-system 8888:8888
  ```

## Troubleshooting

1. Check pod status:

   ```bash
   kubectl get pods -n crm-system
   ```

2. View logs for a specific service:

   ```bash
   kubectl logs -f deployment/[service-name] -n crm-system
   ```

3. Describe a pod for detailed information:
   ```bash
   kubectl describe pod [pod-name] -n crm-system
   ```

## Configuration

Service configurations are managed through ConfigMaps and are aligned with the Spring Cloud Config Server.

- For production environments, sensitive information should be moved to Kubernetes Secrets.
- Database credentials should be secured with proper secret management.

## Scaling

To scale a service, use:

```bash
kubectl scale deployment [service-name] -n crm-system --replicas=[number]
```

## Clean Up

To remove all resources:

```bash
kubectl delete namespace crm-system
```
