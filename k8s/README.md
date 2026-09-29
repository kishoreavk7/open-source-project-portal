# Kubernetes Orchestration - Open Source Project Portal

This directory contains production-grade Kubernetes resource manifests for deploying, networking, autoscaling, and managing the **Open Source Project Portal**.

## Manifests Overview

| Manifest | Kind | Purpose |
| :--- | :--- | :--- |
| `namespace.yaml` | `Namespace` | Isolates project resources in `open-source-portal` |
| `configmap.yaml` | `ConfigMap` | Externalizes Spring Boot application configuration |
| `deployment.yaml`| `Deployment` | Manages 2+ replicas with zero-downtime rolling update & health probes |
| `service.yaml`   | `Service` | Internal `ClusterIP` network abstraction exposing port 8080 |
| `ingress.yaml`   | `Ingress` | External HTTP ingress routing (NGINX Ingress Controller) |
| `hpa.yaml`       | `HorizontalPodAutoscaler` | Dynamic autoscaling between 2 and 5 pods based on CPU load |

---

## Deployment Steps

### 1. Apply Manifests
Deploy all manifests to your Kubernetes cluster (Minikube, Kind, or AWS EKS):
```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml
```

Or apply all at once:
```bash
kubectl apply -f k8s/
```

### 2. Verify Pod and Service Status
```bash
kubectl get all -n open-source-portal
```

### 3. Verify Health Probes
```bash
kubectl describe pod <pod-name> -n open-source-portal
```
Look for `Liveness` and `Readiness` probes pointing to `/actuator/health`.

---

## DevOps Demonstrations for College Viva

### A. Demonstration of Self-Healing
Kubernetes ensures the desired state (2 replicas) is constantly maintained. If a pod crashes or is terminated:
```bash
# 1. List currently running pods
kubectl get pods -n open-source-portal

# 2. Simulate node failure or container crash by deleting one pod
kubectl delete pod <POD_NAME> -n open-source-portal

# 3. Immediately watch Kubernetes create a replacement pod
kubectl get pods -n open-source-portal -w
```
*Viva Explanation: The ReplicaSet controller detected that current replicas (1) was less than desired replicas (2), immediately spawning a replacement pod to restore the desired state.*

### B. Demonstration of Zero-Downtime Rolling Update
When a new container version is built and pushed:
```bash
# Update the deployment image
kubectl set image deployment/open-source-project-portal portal=kishoreavk7/open-source-project-portal:v2 -n open-source-portal

# Monitor the rollout status in real-time
kubectl rollout status deployment/open-source-project-portal -n open-source-portal

# View rollout history
kubectl rollout history deployment/open-source-project-portal -n open-source-portal
```
*Viva Explanation: Because `maxUnavailable: 0` and `maxSurge: 1`, Kubernetes spins up new version pods and waits for their readiness probe (`/actuator/health`) to pass before terminating old pods, ensuring uninterrupted zero-downtime availability.*

### C. Demonstration of Horizontal Pod Autoscaling (HPA)
```bash
kubectl get hpa -n open-source-portal
```
*Viva Explanation: When average CPU load exceeds 70%, the HPA automatically triggers the deployment to scale up to 5 replicas.*
