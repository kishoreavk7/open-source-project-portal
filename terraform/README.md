# Terraform AWS Infrastructure - Open Source Project Portal

This folder contains Terraform configuration to provision repeatable, version-controlled AWS infrastructure for the **Open Source Project Portal**.

## Target Architecture

```
AWS Cloud (Region: ap-south-1 / configurable)
 └── Dedicated VPC (10.0.0.0/16)
      ├── Internet Gateway (for public routing)
      ├── Public Subnets (2 AZs - for Load Balancers & Ingress)
      ├── NAT Gateway & Elastic IP (for private outbound internet access)
      ├── Private Subnets (2 AZs - for EKS Worker Nodes)
      ├── Security Groups (Control Plane SG + Node SG)
      ├── IAM Roles & Policies (EKSClusterPolicy, EKSWorkerNodePolicy, etc.)
      └── Amazon EKS Cluster (Kubernetes 1.30)
           └── Managed Node Group (2 x t3.medium EC2 instances)
```

## Prerequisites

1. Install [Terraform CLI](https://developer.hashicorp.com/terraform/downloads) (>= 1.5.0).
2. Install [AWS CLI v2](https://aws.amazon.com/cli/).
3. Configure AWS credentials securely:
   ```bash
   aws configure
   ```
   *(Never store AWS Access Keys or Secret Keys in `.tf` or `.tfvars` files!)*

## Commands to Run

### 1. Initialize Terraform Providers
Downloads the AWS provider and initializes the local backend:
```bash
cd terraform
terraform init
```

### 2. Format and Validate Code
```bash
terraform fmt
terraform validate
```

### 3. Review Execution Plan
```bash
terraform plan
```

### 4. Provision AWS Infrastructure
```bash
terraform apply -auto-approve
```

### 5. Configure Local Kubectl to Connect to EKS
After `terraform apply` finishes, run the output command:
```bash
aws eks --region ap-south-1 update-kubeconfig --name open-source-portal-eks
kubectl get nodes
```

### 6. Clean Up / Destroy Infrastructure (Avoid AWS Charges)
When done with testing or demonstration:
```bash
terraform destroy -auto-approve
```

## Security Best Practices
- State files (`*.tfstate`, `*.tfstate.*`) and `.terraform/` are strictly ignored by `.gitignore`.
- EKS worker nodes run inside **private subnets** with no direct public IP addresses.
- Outbound egress is routed securely through a NAT Gateway.
- All AWS credentials are read from local AWS CLI profile or environment variables.
