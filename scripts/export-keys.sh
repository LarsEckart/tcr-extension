#!/bin/bash
set -e

# Export GPG keys for Maven Central publishing
# This script extracts the most recently created GPG key and exports it in formats needed for GitHub Actions

echo "Exporting GPG keys for Maven Central..."

# Use the most recently created key
KEY_ID=$(gpg --list-secret-keys --keyid-format LONG --with-colons | grep "^sec" | sort -t: -k6 -n | tail -1 | cut -d: -f5)

if [ -z "$KEY_ID" ]; then
    echo "Error: No GPG secret keys found"
    exit 1
fi

echo "Found key ID: $KEY_ID"

# Export private key (base64 encoded for GitHub)
PRIVATE_KEY_B64=$(gpg --export-secret-keys --armor $KEY_ID | base64 | tr -d '\n')
echo "Private key (base64):"
echo $PRIVATE_KEY_B64

# Export public key
PUBLIC_KEY=$(gpg --export --armor $KEY_ID)
echo "Public key:"
echo "$PUBLIC_KEY"

# Get fingerprint for documentation
FINGERPRINT=$(gpg --fingerprint $KEY_ID | sed -n '2p' | tr -d ' ')
echo "Fingerprint: $FINGERPRINT"

# Save to variables for later use
echo "export GPG_KEY_ID=$KEY_ID" > gpg-vars.sh
echo "export GPG_FINGERPRINT=$FINGERPRINT" >> gpg-vars.sh
chmod +x gpg-vars.sh

echo "Keys exported successfully. Variables saved to gpg-vars.sh"