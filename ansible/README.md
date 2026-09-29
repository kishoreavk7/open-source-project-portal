# Ansible Configuration Management - Open Source Project Portal

This directory contains Ansible playbooks and modular roles to automate the configuration of Linux servers (EC2 instances or bare-metal VMs) for the **Open Source Project Portal**.

## Directory Structure

```
ansible/
├── inventory.ini.example     # Sample inventory with master & worker node hosts
├── site.yml                  # Master playbook executing roles
├── group_vars/
│   └── all.yml               # Centralized configuration variables
├── roles/
│   ├── common/               # OS packages, swap disable, kernel modules, sysctl
│   ├── docker/               # Docker CE & Containerd installation with SystemdCgroup
│   └── kubernetes/           # Kubelet, Kubeadm, Kubectl official repository installation
└── README.md                 # Execution documentation
```

## Prerequisites

1. Install Ansible:
   ```bash
   pip install ansible
   ```
2. Setup SSH Key-based authentication to target hosts:
   ```bash
   ssh-copy-id -i ~/.ssh/id_rsa.pub ubuntu@<TARGET_HOST_IP>
   ```

## Setup & Execution

### 1. Create your Inventory
Copy the example inventory:
```bash
cp inventory.ini.example inventory.ini
```
Edit `inventory.ini` and specify your server public/private IP addresses and SSH private key path.

### 2. Verify Syntax
```bash
ansible-playbook -i inventory.ini site.yml --syntax-check
```

### 3. Dry Run (Check Mode)
```bash
ansible-playbook -i inventory.ini site.yml --check
```

### 4. Execute Playbook
Run the playbook with sudo/become privileges:
```bash
ansible-playbook -i inventory.ini site.yml
```

## Security Best Practices
- **Never commit private SSH keys** to Git.
- Store sensitive values (passwords, API tokens) using **Ansible Vault**:
  ```bash
  ansible-vault create group_vars/vault.yml
  ansible-playbook -i inventory.ini site.yml --ask-vault-pass
  ```
