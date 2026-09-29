# =========================================================================
# AWS Provider Configuration
# Project: Open Source Project Portal
# =========================================================================

provider "aws" {
  region = var.aws_region

  # Default tags automatically applied to all provisioned resources
  default_tags {
    tags = {
      Project     = "OpenSourceProjectPortal"
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}
