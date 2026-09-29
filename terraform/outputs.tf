# =========================================================================
# Outputs for Terraform AWS Infrastructure
# Project: Open Source Project Portal
# =========================================================================

output "vpc_id" {
  description = "The ID of the provisioned VPC"
  value       = aws_vpc.portal_vpc.id
}

output "public_subnet_ids" {
  description = "List of public subnet IDs"
  value       = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  description = "List of private subnet IDs"
  value       = aws_subnet.private[*].id
}

output "cluster_name" {
  description = "Name of the provisioned EKS Kubernetes cluster"
  value       = aws_eks_cluster.portal_cluster.name
}

output "cluster_endpoint" {
  description = "Endpoint URL for Amazon EKS control plane"
  value       = aws_eks_cluster.portal_cluster.endpoint
}

output "cluster_certificate_authority_data" {
  description = "Base64 encoded certificate data required to communicate with the cluster"
  value       = aws_eks_cluster.portal_cluster.certificate_authority[0].data
  sensitive   = true
}

output "kubeconfig_update_command" {
  description = "AWS CLI command to configure local kubectl to connect to the EKS cluster"
  value       = "aws eks --region ${var.aws_region} update-kubeconfig --name ${aws_eks_cluster.portal_cluster.name}"
}
