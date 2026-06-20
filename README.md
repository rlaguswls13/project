# Activity Retrospective Monorepo

Monorepo for a Java Spring Boot backend, a Node.js/Next.js frontend, Azure VM deployment assets, and GitHub Actions CI/CD.

## Layout

- `backend/` Spring Boot REST API, H2, scheduler, and retrospective generation
- `copilot-sdk-service/` Node.js sidecar that calls GitHub Copilot SDK
- `frontend/` Next.js dashboard and settings pages
- `infra/` Azure CLI scripts, Docker Compose, and nginx config for VM deployment
- `.github/workflows/` CI and Azure VM deployment workflows

## Local Prerequisites

- Node.js 18+
- Java 17+
- Docker Desktop for local compose testing

## Next Steps

1. Configure `APP_GITHUB_TOKEN`, `APP_GITHUB_REPO`, and `APP_COPILOT_*` values in the environment.
2. Use `backend/.env.example` as a template for a local `backend/.env` file. The backend loads this file automatically.
3. Use `copilot-sdk-service/.env.example` as a template and set `COPILOT_GITHUB_TOKEN` (or `COPILOT_CLI_URL` for external headless CLI).
4. Run Copilot SDK unit tests from `backend/` with `gradle test` (or `./gradlew test` when wrapper is available).
5. Run backend, copilot sidecar, and frontend from their own directories during development.
6. Use the GitHub Actions deployment workflow to provision or reuse the Azure VM.

## Required Secrets

- `AZURE_CREDENTIALS`
- `AZURE_SUBSCRIPTION_ID`
- `AZURE_RESOURCE_GROUP`
- `AZURE_LOCATION`
- `VM_ADMIN_USERNAME`
- `VM_ADMIN_SSH_PUBLIC_KEY`
- `VM_ADMIN_SSH_PRIVATE_KEY`
- `APP_GITHUB_TOKEN`
- `COPILOT_GITHUB_TOKEN`