#!/bin/bash

# Print banner
echo -e "\033[36m=======================================================\033[0m"
echo -e "\033[36m   CRM Microservices - Port Forwarding Script         \033[0m"
echo -e "\033[36m=======================================================\033[0m"

# Function to start port forwarding
start_port_forward() {
    local service_name=$1
    local local_port=$2
    local remote_port=$3
    
    echo -e "\033[32mStarting port forward for ${service_name}...\033[0m"
    echo -e "\033[33mLocal port: ${local_port}, Remote port: ${remote_port}\033[0m"
    
    # Start port forwarding in background
    kubectl port-forward -n crm-system svc/${service_name} ${local_port}:${remote_port} &
    echo $! # Return the process ID
}

# Start port forwarding for each service
pids=()

# Gateway Server
pids+=($(start_port_forward "gateway-server" 8080 8080))
echo -e "\033[32mGateway Server will be accessible at: http://localhost:8080\033[0m"

# Discovery Server
pids+=($(start_port_forward "discovery-server" 8888 8888))
echo -e "\033[32mDiscovery Server will be accessible at: http://localhost:8888\033[0m"

# Grafana
pids+=($(start_port_forward "grafana" 3000 3000))
echo -e "\033[32mGrafana will be accessible at: http://localhost:3000\033[0m"
echo -e "\033[33mGrafana credentials - Username: admin, Password: admin\033[0m"

# Prometheus
pids+=($(start_port_forward "prometheus-server" 9090 9090))
echo -e "\033[32mPrometheus will be accessible at: http://localhost:9090\033[0m"

echo -e "\n\033[33mPort forwarding started. Press Ctrl+C to stop all port forwards.\033[0m"
echo -e "\033[33mTo stop port forwarding manually, run: kill ${pids[*]}\033[0m"

# Wait for all background processes
wait 