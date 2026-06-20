# Infra

Azure VM provisioning and deployment assets for the retrospective app.

## Files

- `scripts/provision-vm.sh` creates or updates the Ubuntu VM and opens ports 80/443
- `scripts/deploy.sh` syncs the monorepo app assets and runs Docker Compose on the VM
- `docker-compose.yml` runs backend, frontend, and nginx locally or on the VM
- `nginx/nginx.conf` reverse proxies `/api` to the backend and `/` to the frontend