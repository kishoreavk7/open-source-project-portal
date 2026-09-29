# Project #91: Open Source Project Portal

> **A Production-Grade, Cloud-Native DevOps Showcase Project**  
> Built with Java 21, Spring Boot 3, Docker, Jenkins CI/CD, Terraform, AWS, Ansible, Kubernetes, and Ingress.

---

## Table of Contents

1. [Project Title](#1-project-title)
2. [Project Objective](#2-project-objective)
3. [Features](#3-features)
4. [Architecture Diagram (ASCII)](#4-architecture-diagram-in-textascii)
5. [Technology Stack](#5-technology-stack)
6. [Local Setup (VS Code)](#6-local-setup-vs-code)
7. [Maven Commands](#7-maven-commands)
8. [Testing & Mocking](#8-testing--mocking)
9. [Docker Setup](#9-docker-setup)
10. [Docker Registry Integration](#10-docker-registry-integration)
11. [Jenkins CI/CD Pipeline](#11-jenkins-cicd-pipeline)
12. [GitHub to Jenkins Webhook & SCM Polling](#12-github-to-jenkins-webhook--scm-polling)
13. [Terraform Infrastructure as Code](#13-terraform-infrastructure-as-code)
14. [AWS Infrastructure Setup](#14-aws-infrastructure-setup)
15. [Ansible Configuration Management](#15-ansible-configuration-management)
16. [Kubernetes Orchestration](#16-kubernetes-orchestration)
17. [Ingress Networking](#17-ingress-networking)
18. [Scaling with Horizontal Pod Autoscaler (HPA)](#18-scaling-with-horizontal-pod-autoscaler-hpa)
19. [Zero-Downtime Rolling Updates](#19-zero-downtime-rolling-updates)
20. [Self-Healing Architecture](#20-self-healing-architecture)
21. [Troubleshooting Guide](#21-troubleshooting-guide)
22. [Git Branching Strategy](#22-git-branching-strategy)
23. [College Viva Examination Guide (Q&A)](#23-college-viva-explanation-guide)

---

## 1. Project Title
**PROJECT #91 — OPEN SOURCE PROJECT PORTAL**

---

## 2. Project Objective
The objective of this project is to architect, develop, containerize, and deploy an enterprise-pattern **Open Source Project Portal** web application through an end-to-end automated DevOps pipeline:
```
Developer Commit ➔ GitHub ➔ Jenkins CI ➔ Maven Build/Test ➔ Docker Image ➔ Container Registry ➔ Terraform ➔ AWS Infrastructure ➔ Ansible ➔ Kubernetes ➔ Ingress
```
The application dynamically fetches real-time engagement telemetry (stargazers, forks, open issues) from the GitHub REST API and serves installation instructions, while the underlying infrastructure demonstrates zero-downtime deployment, self-healing, autoscaling, and cloud security.

---

## 3. Features
- **Dynamic GitHub Metrics**: Live statistics (Stars, Forks, Open Issues) dynamically retrieved from the GitHub REST API via Spring `RestClient`.
- **Fault-Tolerant & Graceful Fallback**: If the GitHub API is rate-limited, offline, or experiencing downtime, the portal catches exceptions and falls back to safe cached defaults instead of crashing.
- **Microservice Health Probes**: Native Spring Boot Actuator endpoints (`/actuator/health`, `/actuator/info`) and custom `/api/health` JSON probe for Kubernetes liveness/readiness monitoring.
- **Responsive Web UI**: Built with server-side Thymeleaf, modern CSS styling, clean cards, and zero heavy external CDN dependencies.
- **Multi-Stage Containerization**: Minimal image size using Eclipse Temurin JRE 21 and unprivileged non-root execution (`appuser:appgroup`).
- **Automated Declarative CI/CD**: 7-stage Jenkinsfile supporting cross-platform agent execution (Linux / Windows).
- **Repeatable Cloud Infrastructure**: Modular Terraform provisioning an AWS VPC, public/private subnets across multiple AZs, NAT Gateway, Security Groups, IAM Roles, and Amazon EKS cluster.
- **Automated Host Configuration**: Modular Ansible roles configuring Docker, Containerd (`SystemdCgroup`), kernel networking parameters, and Kubernetes tools (`kubeadm`, `kubelet`, `kubectl`).
- **High-Availability Kubernetes Deployment**: ConfigMap externalization, ClusterIP Service, Ingress routing, CPU-based HPA autoscaling, zero-downtime rolling updates, and automated self-healing.

---

## 4. Architecture Diagram in Text/ASCII

```
+-----------------------------------------------------------------------------------------------+
|                                      DEV-OPS WORKFLOW PIPELINE                                |
+-----------------------------------------------------------------------------------------------+

  [ Developer ]
        |
        |  1. Author Code & Commit
        v
  [ Git Feature Branch: feature/github-metrics ]
        |
        |  2. Pull Request & Merge
        v
  [ Git Develop Branch ] ---> [ Git Main Branch (Production) ]
        |
        |  3. GitHub Webhook / SCM Poll (http://<jenkins>:8081/github-webhook/)
        v
+-----------------------------------------------------------------------------------------------+
|                                          JENKINS CI SERVER                                    |
|                                                                                               |
|  [Stage 1: Checkout] ➔ [Stage 2: Maven Compile] ➔ [Stage 3: Maven Test] ➔ [Stage 4: Package]  |
|                                                                               |               |
|  [Stage 7: Push Image]  [Stage 6: Registry Login]  [Stage 5: Docker Build]                 |
+-----------------------------------------------------------------------------------------------+
        |
        |  4. Push Container Image
        v
  [ Docker Hub Registry: kishoreavk7/open-source-project-portal:latest ]
        |
        +-----------------------------------+
        |                                   |
        v                                   v
+---------------------------------+  +----------------------------------------------------------+
|      TERRAFORM (AWS IaC)        |  |               ANSIBLE CONFIGURATION MANAGEMENT           |
|                                 |  |                                                          |
|  - Dedicated VPC (10.0.0.0/16)  |  |  - roles/common: Kernel modules (overlay, br_netfilter)  |
|  - Public & Private Subnets     |  |  - roles/docker: Docker CE + containerd (SystemdCgroup)  |
|  - Internet & NAT Gateways      |  |  - roles/kubernetes: kubeadm, kubelet, kubectl setup     |
|  - AWS EKS Managed Cluster      |  +----------------------------------------------------------+
+---------------------------------+                                 |
        |                                                           |
        +-----------------------------+-----------------------------+
                                      |
                                      v
+-----------------------------------------------------------------------------------------------+
|                                  KUBERNETES CLUSTER (EKS / EC2)                               |
|                                                                                               |
|        [ Internet Traffic ]                                                                   |
|                 |                                                                             |
|                 v                                                                             |
|       [ Ingress Controller ] (portal.local / Path: /)                                         |
|                 |                                                                             |
|                 v                                                                             |
|       [ Service: ClusterIP ] (open-source-portal-service:8080)                                |
|                 |                                                                             |
|       +---------+---------+ (Load Balances Traffic)                                           |
|       |                   |                                                                   |
|       v                   v                                                                   |
|   [ Pod Replica 1 ]   [ Pod Replica 2 ]  <--- Autoscaled by HPA (2 to 5 Pods)                 |
|   - Port 8080         - Port 8080                                                             |
|   - Spring Boot App   - Spring Boot App                                                       |
|   - Liveness Probe    - Liveness Probe    (Monitors /actuator/health)                         |
|   - Readiness Probe   - Readiness Probe   (Monitors /actuator/health)                         |
|   - Non-Root User     - Non-Root User                                                         |
|       |                   |                                                                   |
|       +---------+---------+                                                                   |
|                 |                                                                             |
|                 v (Dynamic REST Call with Fallback)                                           |
|       [ GitHub REST API (api.github.com/repos/kishoreavk7/open-source-project-portal) ]       |
+-----------------------------------------------------------------------------------------------+
```

---

## 5. Technology Stack

| Layer | Component | Version / Details | Purpose |
| :--- | :--- | :--- | :--- |
| **Application** | Java | OpenJDK 21 LTS | Core programming language |
| **Application** | Spring Boot | 3.5.x | Modern web framework & inversion of control |
| **Application** | Spring Web | Spring 6 | REST routing and HTTP request handling |
| **Application** | Spring RestClient | Spring 6 | Declarative synchronous HTTP client for GitHub API |
| **Application** | Thymeleaf | Starter | Server-side templating engine for responsive UI |
| **Application** | Spring Actuator | Starter | Operational telemetry and health probe endpoints |
| **Build Tool** | Apache Maven | 3.9.x (Wrapper) | Dependency management, build lifecycle & packaging |
| **Testing** | JUnit 5 + Mockito | JUnit Platform | Unit tests, MockMvc controller tests, graceful fallback testing |
| **VCS** | Git & GitHub | Git 2.x | Source code management, feature branching, pull requests |
| **Container** | Docker | Multi-stage | Build & runtime separation, non-root user execution |
| **Registry** | Docker Hub | Configurable | Centralized container image storage & versioning |
| **CI/CD** | Jenkins | Declarative Pipeline | End-to-end automated continuous integration & delivery |
| **IaC** | Terraform | HashiCorp v1.16+ | Repeatable declarative AWS cloud provisioning |
| **Cloud Provider** | Amazon Web Services | VPC, Subnets, EKS, IAM, IGW, NAT | Cloud infrastructure hosting |
| **Configuration** | Ansible | 2.15+ | Automated OS, Docker, and Kubernetes package management |
| **Orchestrator** | Kubernetes | 1.30+ | Container orchestration, self-healing, rolling updates |
| **Scaling** | Kubernetes HPA | Autoscaling v2 | Horizontal pod autoscaling based on CPU threshold |
| **Networking** | Ingress NGINX | Networking v1 | HTTP routing and reverse proxy |

---

## 6. Local Setup (VS Code)

### Prerequisites
1. **Java Development Kit (JDK 21)** installed.
2. **VS Code** with the *Extension Pack for Java* (optional but recommended).

### Windows Setup & Execution
Open PowerShell or Command Prompt in the project folder:
```powershell
# Navigate into project directory
cd open-source-project-portal

# Clean old artifacts and package the application
.\mvnw.cmd clean package

# Run all unit and integration tests
.\mvnw.cmd test

# Start the Spring Boot application locally
.\mvnw.cmd spring-boot:run
```

### Linux / macOS Setup & Execution
```bash
# Make Maven wrapper executable
chmod +x mvnw

# Package and run
./mvnw clean package
./mvnw test
./mvnw spring-boot:run
```

### Accessing Local Application
- **Web Application Portal**: [http://localhost:8080](http://localhost:8080)
- **Actuator Health Endpoint**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Custom Health JSON API**: [http://localhost:8080/api/health](http://localhost:8080/api/health)

---

## 7. Maven Commands

| Command (Windows) | Command (Linux/macOS) | Description |
| :--- | :--- | :--- |
| `.\mvnw.cmd clean` | `./mvnw clean` | Deletes the `target/` directory |
| `.\mvnw.cmd compile` | `./mvnw compile` | Compiles Java source files in `src/main/java` |
| `.\mvnw.cmd test` | `./mvnw test` | Executes all JUnit 5 test suites |
| `.\mvnw.cmd clean package` | `./mvnw clean package` | Packages compiled code into executable JAR |
| `.\mvnw.cmd spring-boot:run` | `./mvnw spring-boot:run` | Launches the web application on port 8080 |

---

## 8. Testing & Mocking

All unit and integration tests are isolated from external dependencies. The test suite does **not** make real calls to GitHub:
- **`OpenSourceProjectPortalApplicationTests`**: Asserts the complete Spring ApplicationContext loads cleanly.
- **`HomeControllerTest`**: Uses Spring `@WebMvcTest` and `@MockitoBean` to mock `GitHubService`. Verifies HTTP 200, view resolution (`index`), and model attributes.
- **`GitHubServiceTest`**: Validates fallback metric generation and asserts that `getRepositoryDetails()` fails gracefully when external network calls fail.
- **`HealthControllerTest`**: Verifies the `/api/health` endpoint returns JSON with status `"UP"`.

Run tests:
```powershell
.\mvnw.cmd test
```

---

## 9. Docker Setup

### Multi-Stage Dockerfile Highlights
- **Stage 1 (Builder)**: Uses `maven:3.9.6-eclipse-temurin-21-alpine` to build the application and download dependencies offline.
- **Stage 2 (Runner)**: Uses `eclipse-temurin:21-jre-alpine` (under 180MB).
- **Security Compliance**: Runs under an unprivileged user `appuser:appgroup` instead of root.

### Building & Running with Docker
```bash
# 1. Build the Docker container image
docker build -t open-source-project-portal .

# 2. Run container in background on port 8080
docker run -d -p 8080:8080 --name portal open-source-project-portal

# 3. Check container logs
docker logs -f portal

# 4. Stop and remove container
docker stop portal && docker rm portal
```

### Running with Docker Compose
```bash
# Launch container service
docker-compose up -d --build

# View logs
docker-compose logs -f

# Teardown
docker-compose down
```

---

## 10. Docker Registry Integration

The Docker image name and registry are fully configurable via environment variables:
- `DOCKER_REGISTRY` (default: `docker.io`)
- `DOCKER_USERNAME` (e.g., `kishoreavk7`)
- `DOCKER_IMAGE` (default: `open-source-project-portal`)
- `DOCKER_TAG` (e.g., `latest` or Jenkins build number `${BUILD_NUMBER}`)

### Manual Tag and Push:
```bash
# Log in to Docker Hub
docker login -u YOUR_DOCKER_USERNAME

# Tag the local image
docker tag open-source-project-portal:latest YOUR_DOCKER_USERNAME/open-source-project-portal:latest

# Push to Docker Hub
docker push YOUR_DOCKER_USERNAME/open-source-project-portal:latest
```

---

## 11. Jenkins CI/CD Pipeline

The included `Jenkinsfile` provides a 7-stage declarative pipeline:

```
[1. Checkout] ➔ [2. Maven Build] ➔ [3. Maven Test] ➔ [4. Package] ➔ [5. Docker Build] ➔ [6. Docker Login] ➔ [7. Docker Push]
```

### Pipeline Configuration in Jenkins
1. Open Jenkins Dashboard ➔ **New Item** ➔ Name: `open-source-project-portal` ➔ Choose **Pipeline**.
2. Under **Pipeline Definition**, select **Pipeline script from SCM**.
3. SCM: **Git**
4. Repository URL: `https://github.com/kishoreavk7/open-source-project-portal.git`
5. Branches to build: `*/develop` or `*/main`
6. Script Path: `Jenkinsfile`
7. In Jenkins Credentials Store, add a **Username with password** credential:
   - ID: `dockerhub-credentials`
   - Username: *Your Docker Hub username*
   - Password: *Your Docker Hub Personal Access Token*

---

## 12. GitHub to Jenkins Webhook & SCM Polling

### Approach A: Public Domain / Production Webhook
When Jenkins has a publicly accessible IP or reverse proxy:
1. In your GitHub repository: **Settings** ➔ **Webhooks** ➔ **Add webhook**.
2. Payload URL: `http://<YOUR_JENKINS_PUBLIC_IP>:8081/github-webhook/`
3. Content type: `application/json`
4. Which events? Select **Just the push event**.
5. Save webhook. Any Git push will trigger Jenkins immediately.

### Approach B: Local College Demonstration (SCM Polling)
> [!IMPORTANT]
> A Jenkins server running on `localhost` cannot receive webhooks directly from public GitHub servers without an external tunnel (like ngrok). For a college viva demonstration, use **Poll SCM**:

1. In the Jenkins Job Configuration, navigate to **Build Triggers**.
2. Check **Poll SCM**.
3. Schedule: `H/2 * * * *` (checks GitHub for new commits every 2 minutes).
4. Alternatively, use the manual **Build Now** button in Jenkins during your presentation.

---

## 13. Terraform Infrastructure as Code

Located in `terraform/`:
- `provider.tf`: AWS provider with default tags.
- `variables.tf`: Region (`ap-south-1`), CIDRs, instance types.
- `main.tf`: VPC, 2 public subnets, 2 private subnets, IGW, NAT Gateway, route tables, Security Groups, IAM Roles, and EKS Cluster.
- `outputs.tf`: VPC ID, EKS endpoint, kubeconfig update command.
- `terraform.tfvars.example`: Example parameters.

### Terraform Execution Steps
```bash
cd terraform

# 1. Initialize provider plugins
terraform init

# 2. Format and validate
terraform fmt
terraform validate

# 3. Plan infrastructure
terraform plan

# 4. Provision AWS resources
terraform apply -auto-approve

# 5. Teardown when done
terraform destroy -auto-approve
```

---

## 14. AWS Infrastructure Setup

### Security & IAM Principle of Least Privilege
- **Worker Nodes in Private Subnets**: Compute instances run isolated from the public internet.
- **NAT Gateway**: Permits private nodes to pull container images and OS security patches without exposing inbound ports.
- **IAM Policies Attached to Node Role**:
  - `AmazonEKSWorkerNodePolicy`
  - `AmazonEKS_CNI_Policy`
  - `AmazonEC2ContainerRegistryReadOnly`

---

## 15. Ansible Configuration Management

Located in `ansible/`:
- `roles/common`: Installs prerequisite packages, disables swap, configures `overlay` and `br_netfilter` kernel modules, and enables `net.ipv4.ip_forward`.
- `roles/docker`: Installs Docker Engine & Containerd, generates `/etc/containerd/config.toml` with `SystemdCgroup = true`.
- `roles/kubernetes`: Installs official `kubelet`, `kubeadm`, and `kubectl` packages with version locks.

### Running Ansible Playbook
```bash
cd ansible
cp inventory.ini.example inventory.ini
ansible-playbook -i inventory.ini site.yml --syntax-check
ansible-playbook -i inventory.ini site.yml
```

---

## 16. Kubernetes Orchestration

Located in `k8s/`:
- `namespace.yaml`: Dedicated namespace `open-source-portal`.
- `configmap.yaml`: App environment variables.
- `deployment.yaml`: 2 replicas, rolling updates (`maxUnavailable: 0`, `maxSurge: 1`), resource limits, Actuator liveness and readiness probes.
- `service.yaml`: ClusterIP service routing to port 8080.
- `ingress.yaml`: Ingress routing rule for `portal.local`.
- `hpa.yaml`: Horizontal Pod Autoscaler scaling from 2 to 5 pods based on 70% CPU target.

### Apply All Kubernetes Manifests
```bash
kubectl apply -f k8s/
```

Verify deployment:
```bash
kubectl get all -n open-source-portal
```

---

## 17. Ingress Networking

The ingress resource (`k8s/ingress.yaml`) intercepts external HTTP traffic and routes it to `open-source-portal-service:8080`:
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: open-source-portal-ingress
  namespace: open-source-portal
spec:
  ingressClassName: nginx
  rules:
    - host: portal.local
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: open-source-portal-service
                port:
                  number: 8080
```
Map `portal.local` to your Ingress controller IP or `127.0.0.1` in your hosts file (`/etc/hosts` or `C:\Windows\System32\drivers\etc\hosts`).

---

## 18. Scaling with Horizontal Pod Autoscaler (HPA)

HPA monitors pod CPU metrics and scales dynamically:
```bash
# Check HPA status
kubectl get hpa -n open-source-portal
```
When CPU exceeds 70%, HPA spawns up to 5 pods. When traffic normalizes, it scales back down to 2 replicas after the stabilization window (300 seconds).

---

## 19. Zero-Downtime Rolling Updates

Rolling updates ensure high availability during releases:
```bash
# Update container image to a new version
kubectl set image deployment/open-source-project-portal portal=kishoreavk7/open-source-project-portal:v2 -n open-source-portal

# Track rollout progress
kubectl rollout status deployment/open-source-project-portal -n open-source-portal
```
With `maxUnavailable: 0`, Kubernetes will **never** terminate an existing healthy pod until a new pod passes its readiness probe (`/actuator/health`).

---

## 20. Self-Healing Architecture

Kubernetes constantly reconciles desired state vs actual state:
```bash
# 1. Inspect running pods
kubectl get pods -n open-source-portal

# 2. Simulate container crash or failure by killing a pod
kubectl delete pod <POD_NAME> -n open-source-portal

# 3. Watch Kubernetes instantly spawn a replacement pod
kubectl get pods -n open-source-portal -w
```

---

## 21. Troubleshooting Guide

| Issue | Root Cause | Solution |
| :--- | :--- | :--- |
| **Port 8080 already in use** | Another local process is listening on 8080 | Change `server.port=8081` in `application.properties` or kill the existing process (`netstat -ano \| findstr :8080`) |
| **GitHub 403 Forbidden** | GitHub API rate limit for unauthenticated IP | The portal automatically activates **Graceful Fallback Mode**; you can also configure a GitHub token if needed |
| **Docker permission denied** | Remote user is not in `docker` group | Run `sudo usermod -aG docker $USER` and reload shell session |
| **Kubelet not starting** | Linux system swap is enabled | Run `sudo swapoff -a` and comment out swap lines in `/etc/fstab` (handled automatically by Ansible `common` role) |
| **Readiness probe failing** | Application takes longer to warm up | Increase `initialDelaySeconds` in `k8s/deployment.yaml` from 25 to 40 seconds |

---

## 22. Git Branching Strategy

This project follows the **GitFlow** branching strategy:

```
feature/github-metrics  ➔  develop  ➔  main (Production)
```

1. **`main`**: Production-ready, stable releases.
2. **`develop`**: Integration and staging branch where features are aggregated and tested.
3. **`feature/*`**: Short-lived feature branches for authoring new enhancements (e.g., `feature/github-metrics`).

### Merge Sequence:
```bash
# 1. Commit and push on feature branch
git checkout feature/github-metrics
git add .
git commit -m "Implement GitHub metrics telemetry with graceful fallback"
git push origin feature/github-metrics

# 2. Merge into develop branch
git checkout develop
git merge feature/github-metrics
git push origin develop

# 3. Merge into main branch for release
git checkout main
git merge develop
git push origin main
```

---

## 23. College Viva Explanation Guide

### Frequently Asked Viva Questions & Answers

#### Q1: What is the purpose of this project?
> **Answer**: It is an Open Source Project Portal built with Spring Boot 3 and Java 21 that dynamically pulls engagement telemetry from GitHub and serves project guides. It demonstrates an automated DevOps delivery pipeline from source code commit through CI (Jenkins), Containerization (Docker), Infrastructure as Code (Terraform), Configuration Management (Ansible), to Orchestration (Kubernetes).

#### Q2: How does the application handle GitHub API rate limiting?
> **Answer**: In `GitHubService.java`, the HTTP call via Spring `RestClient` is wrapped in a `try-catch` block. If GitHub returns a 403 (rate limit), 404, or network timeout, the application logs a warning and returns default fallback metrics (`createFallbackMetrics()`) with `api_status = FALLBACK_MODE`. This ensures the web page fails gracefully instead of crashing.

#### Q3: Why is a multi-stage Docker build used?
> **Answer**: Multi-stage builds separate the build environment from the runtime environment. Stage 1 includes the full Maven toolchain and JDK to compile code and build the JAR. Stage 2 only includes a minimal, lightweight JRE 21 runtime image (under 180MB) and the single JAR file. This minimizes image size, speeds up deployments, and drastically reduces security vulnerabilities by excluding source code and build tools from production.

#### Q4: Why is the container run as a non-root user?
> **Answer**: In Dockerfile, we create `appuser:appgroup` and set `USER appuser:appgroup`. Running containers as root is a major security risk because container breakouts could give the attacker root privileges on the underlying host kernel.

#### Q5: What is the difference between Terraform and Ansible in this pipeline?
> **Answer**: **Terraform** is an Infrastructure as Code (IaC) tool designed for provisioning cloud resources (VPC, Subnets, EKS Cluster, Security Groups, IAM Roles). **Ansible** is a Configuration Management tool used to configure the provisioned servers (installing Docker, configuring containerd `SystemdCgroup`, setting up kubeadm and kubelet).

#### Q6: How do Kubernetes Liveness and Readiness probes differ?
> **Answer**:
> - **Readiness Probe**: Checks if the container is ready to receive traffic (e.g., Spring context initialized). If it fails, Kubernetes stops sending traffic via the Service load balancer to that pod.
> - **Liveness Probe**: Checks if the application is alive and healthy. If it fails (e.g., deadlocked or unresponsive), Kubernetes terminates and restarts the container to restore health.
> Both probes point to Spring Boot Actuator's `/actuator/health` endpoint.

#### Q7: How does Kubernetes achieve zero downtime during a deployment update?
> **Answer**: Through the `RollingUpdate` strategy configured with `maxUnavailable: 0` and `maxSurge: 1`. Kubernetes spins up one new pod with the new container image version, waits for its readiness probe to report healthy, and only then terminates one old pod. At no point is the application unavailable to incoming requests.

#### Q8: How did you test the application offline?
> **Answer**: In `HomeControllerTest.java`, we used `@WebMvcTest` and mocked `GitHubService` using `@MockitoBean`. This allows full unit and integration testing of HTTP routing, Thymeleaf rendering, and model attributes without requiring an active internet connection or hitting GitHub rate limits.
