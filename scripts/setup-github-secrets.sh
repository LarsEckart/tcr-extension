#!/bin/bash
set -e

# Setup GitHub repository secrets for GPG signing
# This script uses the GitHub CLI to configure repository secrets

if ! command -v gh &> /dev/null; then
    echo "Error: GitHub CLI (gh) is not installed"
    echo "Install with: brew install gh"
    exit 1
fi

# Check if authenticated
if ! gh auth status &> /dev/null; then
    echo "Error: Not authenticated with GitHub CLI"
    echo "Run: gh auth login"
    exit 1
fi

# Source the variables from export-keys.sh
if [ ! -f "gpg-vars.sh" ]; then
    echo "Error: gpg-vars.sh not found. Run export-keys.sh first."
    exit 1
fi

source gpg-vars.sh

if [ -z "$GPG_KEY_ID" ]; then
    echo "Error: GPG_KEY_ID not set"
    exit 1
fi

echo "Setting up GitHub secrets for GPG signing..."

# Get the base64 encoded private key
PRIVATE_KEY_B64=$(gpg --export-secret-keys --armor "$GPG_KEY_ID" | base64 | tr -d '\n')

# Prompt for passphrase (don't echo to terminal)
echo -n "Enter GPG key passphrase: "
read -s PASSPHRASE
echo

# Set GitHub repository secrets
echo "Setting ORG_GRADLE_PROJECT_SIGNINGKEY secret..."
echo "$PRIVATE_KEY_B64" | gh secret set ORG_GRADLE_PROJECT_SIGNINGKEY

echo "Setting ORG_GRADLE_PROJECT_SIGNINGPASSWORD secret..."
echo "$PASSPHRASE" | gh secret set ORG_GRADLE_PROJECT_SIGNINGPASSWORD

echo "GitHub secrets configured successfully"
echo "Secrets set:"
echo "- ORG_GRADLE_PROJECT_SIGNINGKEY"
echo "- ORG_GRADLE_PROJECT_SIGNINGPASSWORD"

# Clear passphrase from memory
unset PASSPHRASE