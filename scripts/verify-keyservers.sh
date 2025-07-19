#!/bin/bash
set -e

# Verify GPG public key distribution across keyservers
# This script checks if the GPG public key is available on various keyservers

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

echo "Verifying GPG key $GPG_KEY_ID distribution across keyservers..."

# Keyservers to check
keyservers=(
    "keyserver.ubuntu.com"
    "keys.openpgp.org"
)

email="lars.eckart@hey.com"

for keyserver in "${keyservers[@]}"; do
    echo "Checking $keyserver..."
    
    # Try searching by key ID
    if gpg --keyserver "$keyserver" --search-keys "$GPG_KEY_ID" 2>/dev/null; then
        echo "✓ Key $GPG_KEY_ID found on $keyserver"
    else
        echo "✗ Key $GPG_KEY_ID not found on $keyserver"
    fi
    
    # Try searching by email
    if gpg --keyserver "$keyserver" --search-keys "$email" 2>/dev/null; then
        echo "✓ Email $email found on $keyserver"
    else
        echo "✗ Email $email not found on $keyserver"
    fi
    
    echo ""
done

echo "Keyserver verification completed"