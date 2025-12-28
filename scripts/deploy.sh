#!/bin/bash
set -e

# NOTE: Deployment is automated via GitHub Actions (.github/workflows/deploy.yml).
# This script is retained for debugging deployment issues when needed.

# Configuration
PROJECT_ID="voltaic-bridge-292822"
REGION="europe-west1"
SERVICE_NAME="job-tool"
REPOSITORY="default"
IMAGE_TAG="main-SNAPSHOT"
REMOTE_IMAGE="${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPOSITORY}/${SERVICE_NAME}:${IMAGE_TAG}"

echo "=== Building Docker image ==="
docker build -t "${REMOTE_IMAGE}" .

echo ""
echo "=== Pushing image to Artifact Registry ==="
docker push "${REMOTE_IMAGE}"

echo ""
echo "=== Deploying to Cloud Run (updating image only) ==="
gcloud run deploy "${SERVICE_NAME}" \
  --image="${REMOTE_IMAGE}" \
  --region="${REGION}"

echo ""
echo "=== Deployment complete! ==="
