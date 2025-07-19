#!/bin/bash
set -e

# Extend GPG key expiration and re-upload to keyservers
# This script extends the expiration date of a GPG key and updates keyservers

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

echo "Extending expiration for GPG key $GPG_KEY_ID..."

# Check current expiration
echo "Current key information:"
gpg --list-keys --keyid-format LONG "$GPG_KEY_ID"

# Create expect script for automated key expiration extension
cat > extend_key.exp << 'EOF'
#!/usr/bin/expect -f

set key_id [lindex $argv 0]
set years [lindex $argv 1]

spawn gpg --edit-key $key_id
expect "gpg>" { send "expire\r" }
expect "Change the expiration date for the primary key." { send "${years}y\r" }
expect "Is this correct?" { send "y\r" }
expect "gpg>" { send "save\r" }
expect eof
EOF

chmod +x extend_key.exp

# Default to 5 years if no argument provided
YEARS=${1:-5}

echo "Extending key expiration to $YEARS years..."

# Check if expect is available
if ! command -v expect &> /dev/null; then
    echo "Error: expect is not installed"
    echo "Install with: brew install expect"
    echo "Or run manually: gpg --edit-key $GPG_KEY_ID"
    echo "Then use: expire -> ${YEARS}y -> y -> save"
    exit 1
fi

# Run the expect script
if ./extend_key.exp "$GPG_KEY_ID" "$YEARS"; then
    echo "✓ Key expiration extended successfully"
    
    # Re-upload to keyservers
    echo "Re-uploading key to keyservers..."
    ./upload-keys.sh
    
    # Update gpg-vars.sh with new expiration info
    EXPIRY=$(gpg --list-keys --keyid-format LONG "$GPG_KEY_ID" | grep -A1 "pub" | tail -1 | grep -o "\[expires: [^]]*\]" | sed 's/\[expires: \(.*\)\]/\1/')
    echo "export GPG_EXPIRY=\"$EXPIRY\"" >> gpg-vars.sh
    
    echo "Key expiration extended and keyservers updated"
    echo "New expiration: $EXPIRY"
else
    echo "✗ Failed to extend key expiration"
fi

# Clean up
rm -f extend_key.exp