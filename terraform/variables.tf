# =========================================================================
# Input Variables for Terraform AWS Infrastructure
# Project: Open Source Project Portal
# =========================================================================

variable "aws_region" {
  description = "Target AWS Region for deployment"
  type        = string
  default     = "ap-south-1"
}

variable "environment" {
  description = "Deployment environment name (e.g. dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "cluster_name" {
  description = "Name of the AWS EKS Kubernetes Cluster"
  type        = string
  default     = "open-source-portal-eks"
}

variable "vpc_cidr" {
  description = "CIDR block for the dedicated VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets (across at least 2 Availability Zones)"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets hosting worker nodes"
  type        = list(string)
  default     = ["10.0.10.0/24", "10.0.20.0/24"]
}

variable "instance_types" {
  description = "EC2 instance types for EKS worker nodes"
  type        = list(string)
  default     = ["t3.medium"]
}

variable "desired_node_count" {
  description = "Desired number of worker nodes"
  type        = number
  default     = 2
}

variable "min_node_count" {
  description = "Minimum number of worker nodes in AutoScaling group"
  type        = number
  default     = 1
}

variable "max_node_count" {
  description = "Maximum number of worker nodes in AutoScaling group"
  type        = number
  default     = 3
}
