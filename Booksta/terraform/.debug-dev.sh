export ARM_SUBSCRIPTION_ID=""

export TF_VAR_application_name="booksta"
export TF_VAR_environment_name="dev"

export BACKEND_RESOURCE_GROUP="rg-terraform-state-${TF_VAR_environment_name}"
export BACKEND_STORAGE_ACCOUNT=""
export BACKEND_STORAGE_CONTAINER=""
export BACKEND_KEY="${TF_VAR_application_name}-${TF_VAR_environment_name}"

terraform init \
    -backend-config="resource_group_name=${BACKEND_RESOURCE_GROUP}" \
    -backend-config="storage_account_name=${BACKEND_STORAGE_ACCOUNT}" \
    -backend-config="container_name=${BACKEND_STORAGE_CONTAINER}" \
    -backend-config="key=${BACKEND_KEY}"

terraform $* -var-file ./envs/dev/dev.tfvars

rm -rf .terraform