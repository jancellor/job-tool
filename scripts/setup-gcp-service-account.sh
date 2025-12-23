#!/bin/bash
set -e

# NOTE: This script only needs to be run once during initial setup.
# It creates the GCP service account and GitHub secrets for CI/CD.

# Configuration
PROJECT_ID="voltaic-bridge-292822"
SA_NAME="github-actions-deployer"
SA_EMAIL="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
KEY_FILE="gcp-sa-key.json"

echo "=== Creating service account ==="
gcloud iam service-accounts create "${SA_NAME}" \
  --display-name="GitHub Actions Deployer" \
  --description="Service account for deploying from GitHub Actions to Cloud Run" \
  --project="${PROJECT_ID}"

echo ""
echo "=== Granting necessary permissions ==="

# Artifact Registry Writer - to push Docker images
gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/artifactregistry.writer"

# Cloud Run Admin - to deploy services
gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/run.admin"

# Service Account User - to act as the Cloud Run runtime service account
gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/iam.serviceAccountUser"

echo ""
echo "=== Creating and downloading service account key ==="
gcloud iam service-accounts keys create "${KEY_FILE}" \
  --iam-account="${SA_EMAIL}" \
  --project="${PROJECT_ID}"

echo ""
echo "=== Setup complete! ==="
echo ""
echo "Next steps:"
echo "1. Copy the contents of ${KEY_FILE} to add as a GitHub secret"
echo "2. Add the following secrets to your GitHub repository (Settings → Secrets and variables → Actions):"
echo "   - GCP_PROJECT_ID: ${PROJECT_ID}"
echo "   - GCP_SA_KEY: <paste the entire contents of ${KEY_FILE}>"
echo ""
echo "IMPORTANT: Keep ${KEY_FILE} secure and do not commit it to git!"
echo "Consider deleting it after adding to GitHub secrets."
