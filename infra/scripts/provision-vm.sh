#!/usr/bin/env bash
set -euo pipefail

RESOURCE_GROUP=${RESOURCE_GROUP:-retrospective-rg}
LOCATION=${LOCATION:-koreacentral}
VM_NAME=${VM_NAME:-retrospective-vm}
ADMIN_USERNAME=${ADMIN_USERNAME:-azureuser}
SSH_PUBLIC_KEY_PATH=${SSH_PUBLIC_KEY_PATH:-~/.ssh/id_rsa.pub}
IMAGE=${IMAGE:-Ubuntu2204}

if ! az group show --name "$RESOURCE_GROUP" >/dev/null 2>&1; then
  az group create --name "$RESOURCE_GROUP" --location "$LOCATION"
fi

if ! az vm show --resource-group "$RESOURCE_GROUP" --name "$VM_NAME" >/dev/null 2>&1; then
  az vm create \
    --resource-group "$RESOURCE_GROUP" \
    --name "$VM_NAME" \
    --image "$IMAGE" \
    --admin-username "$ADMIN_USERNAME" \
    --ssh-key-values "$SSH_PUBLIC_KEY_PATH" \
    --size Standard_B2s \
    --public-ip-sku Standard \
    --nsg-rule SSH \
    --tags app=retrospective
fi

az vm open-port --resource-group "$RESOURCE_GROUP" --name "$VM_NAME" --port 80
az vm open-port --resource-group "$RESOURCE_GROUP" --name "$VM_NAME" --port 443