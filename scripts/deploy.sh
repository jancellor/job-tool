#!/bin/bash
set -e

# NOTE: This script should not be run manually.
# Deployment is automated via GitHub Actions (.github/workflows/deploy.yml)
# which triggers on push to the main branch.

# Configuration
PROJECT_ID="voltaic-bridge-292822"
REGION="europe-west1"
SERVICE_NAME="spring-boot-demo"
REPOSITORY="default"
IMAGE_TAG="main-SNAPSHOT"
LOCAL_IMAGE="${SERVICE_NAME}:${IMAGE_TAG}"
REMOTE_IMAGE="${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPOSITORY}/${SERVICE_NAME}:${IMAGE_TAG}"

echo "=== Building GraalVM native image ==="
./mvnw -Pnative spring-boot:build-image

echo ""
echo "=== Tagging image for Artifact Registry ==="
docker tag "${LOCAL_IMAGE}" "${REMOTE_IMAGE}"

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
echo "Service URL: https://${SERVICE_NAME}-1089738547069.${REGION}.run.app"
