# Activity Retrospective Monorepo

Monorepo for a Java Spring Boot backend, a Node.js/Next.js frontend, Azure VM deployment assets, and GitHub Actions CI/CD.

## Layout

- `backend/` Spring Boot REST API, H2, scheduler, and retrospective generation
- `frontend/` Next.js dashboard and settings pages
- `infra/` Azure CLI scripts, Docker Compose, and nginx config for VM deployment
- `.github/workflows/` CI and Azure VM deployment workflows

## Local Prerequisites

- Node.js 18+
- Java 17+
- Docker Desktop for local compose testing

## Next Steps

1. Configure `APP_GITHUB_TOKEN`, `APP_GITHUB_REPO`, and `APP_COPILOT_*` values in the environment.
2. Run the backend and frontend from their own directories during development.
3. Use the GitHub Actions deployment workflow to provision or reuse the Azure VM.

## Required Secrets

- `AZURE_CREDENTIALS`
- `AZURE_SUBSCRIPTION_ID`
- `AZURE_RESOURCE_GROUP`
- `AZURE_LOCATION`
- `VM_ADMIN_USERNAME`
- `VM_ADMIN_SSH_PUBLIC_KEY`
- `VM_ADMIN_SSH_PRIVATE_KEY`
- `APP_GITHUB_TOKEN`
- `APP_COPILOT_API_KEY`