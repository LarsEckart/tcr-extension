#!/bin/bash
set -e

# Upload GPG public key to multiple keyservers
# This script uploads the GPG public key to various keyservers for distribution

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

echo "Uploading GPG key $GPG_KEY_ID to keyservers..."

# Upload to multiple keyservers
keyservers=(
    "keyserver.ubuntu.com"
    "keys.openpgp.org"
)

for keyserver in "${keyservers[@]}"; do
    echo "Uploading to $keyserver..."
    if gpg --keyserver "$keyserver" --send-keys "$GPG_KEY_ID"; then
        echo "✓ Successfully uploaded to $keyserver"
    else
        echo "✗ Failed to upload to $keyserver"
    fi
done

echo "Key upload process completed for $GPG_KEY_ID"
