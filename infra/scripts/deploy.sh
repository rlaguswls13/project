#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_ROOT=$(cd -- "${SCRIPT_DIR}/../.." && pwd)

REMOTE_HOST=${REMOTE_HOST:?REMOTE_HOST is required}
REMOTE_USER=${REMOTE_USER:-azureuser}
SSH_KEY_PATH=${SSH_KEY_PATH:-~/.ssh/id_rsa}
DOCKER_WAIT_SECONDS=${DOCKER_WAIT_SECONDS:-300}

SSH_OPTS=(-i "${SSH_KEY_PATH}" -o StrictHostKeyChecking=no -o ConnectTimeout=10)

# Wait for cloud-init to finish installing Docker on a freshly provisioned VM.
ssh "${SSH_OPTS[@]}" "${REMOTE_USER}@${REMOTE_HOST}" \
  "timeout ${DOCKER_WAIT_SECONDS} bash -c 'until [ -f /opt/retrospective/.docker-ready ] && command -v docker >/dev/null 2>&1; do echo \"waiting for docker...\"; sleep 5; done'"

rsync -az --delete \
  -e "ssh ${SSH_OPTS[*]}" \
  "${REPO_ROOT}/backend" "${REPO_ROOT}/frontend" "${REPO_ROOT}/copilot-sdk-service" "${REPO_ROOT}/infra" \
  "${REMOTE_USER}@${REMOTE_HOST}:/opt/retrospective"

ssh "${SSH_OPTS[@]}" "${REMOTE_USER}@${REMOTE_HOST}" \
  'cd /opt/retrospective/infra && sudo docker compose up -d --build'