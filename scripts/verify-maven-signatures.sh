#!/bin/bash
set -e

# Verify Maven Central artifact signatures
# This script downloads artifacts and verifies their GPG signatures

if [ $# -lt 1 ]; then
    echo "Usage: $0 <version>"
    echo "Example: $0 0.0.4"
    exit 1
fi

VERSION=$1
ARTIFACT_BASE="junit-tcr-extensions-$VERSION"
MAVEN_BASE_URL="https://repo1.maven.org/maven2/com/larseckart/junit-tcr-extensions/$VERSION"

echo "Verifying Maven Central signatures for version $VERSION..."

# Create temp directory for downloads
TEMP_DIR=$(mktemp -d)
cd "$TEMP_DIR"

echo "Working directory: $TEMP_DIR"

# Download artifacts and signatures
artifacts=(
    "$ARTIFACT_BASE.jar"
    "$ARTIFACT_BASE-sources.jar"
    "$ARTIFACT_BASE-javadoc.jar"
    "$ARTIFACT_BASE.pom"
)

echo "Downloading artifacts and signatures..."
for artifact in "${artifacts[@]}"; do
    echo "Downloading $artifact..."
    wget -q "$MAVEN_BASE_URL/$artifact" || echo "Failed to download $artifact"
    wget -q "$MAVEN_BASE_URL/$artifact.asc" || echo "Failed to download $artifact.asc"
done

# Source key variables if available
if [ -f "../gpg-vars.sh" ]; then
    source ../gpg-vars.sh
    KEY_ID=$GPG_KEY_ID
else
    echo "gpg-vars.sh not found, using default key ID"
    KEY_ID="YOUR_KEY_ID"
fi

# Import public key from keyserver
echo "Importing public key from keyserver..."
gpg --keyserver keyserver.ubuntu.com --recv-keys "$KEY_ID" || echo "Failed to import key"

# Verify signatures
echo "Verifying signatures..."
for artifact in "${artifacts[@]}"; do
    if [ -f "$artifact" ] && [ -f "$artifact.asc" ]; then
        echo "Verifying $artifact..."
        if gpg --verify "$artifact.asc" "$artifact" 2>/dev/null; then
            echo "✓ $artifact signature valid"
        else
            echo "✗ $artifact signature invalid"
        fi
    else
        echo "✗ $artifact or signature missing"
    fi
done

# Clean up
cd ..
rm -rf "$TEMP_DIR"

echo "Maven Central signature verification completed"