#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_ROOT=$(cd -- "${SCRIPT_DIR}/../.." && pwd)

REMOTE_HOST=${REMOTE_HOST:?REMOTE_HOST is required}
REMOTE_USER=${REMOTE_USER:-azureuser}
SSH_KEY_PATH=${SSH_KEY_PATH:-~/.ssh/id_rsa}

rsync -az --delete \
  -e "ssh -i ${SSH_KEY_PATH} -o StrictHostKeyChecking=no" \
  "${REPO_ROOT}/backend" "${REPO_ROOT}/frontend" "${REPO_ROOT}/infra" \
  "${REMOTE_USER}@${REMOTE_HOST}:/opt/retrospective"

ssh -i "${SSH_KEY_PATH}" -o StrictHostKeyChecking=no "${REMOTE_USER}@${REMOTE_HOST}" \
  'cd /opt/retrospective/infra && docker compose up -d --build'