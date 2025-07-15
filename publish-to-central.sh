#!/bin/bash
set -e

# Maven Central Portal Publishing Script
# This script builds and publishes the TCR extension to Maven Central Portal

# Configuration
VERSION="${1:-0.0.4}"
ARTIFACT="junit-tcr-extensions"
GROUP_PATH="com/larseckart"

# Check if we're running in CI
if [[ "${CI:-false}" == "true" ]]; then
    echo "Running in CI environment"
    SKIP_UPLOAD=false
else
    echo "Running locally - will skip actual upload"
    SKIP_UPLOAD=true
fi

# Clean up any existing bundle artifacts
echo "Cleaning up existing bundle artifacts..."
rm -rf central-bundle central-bundle.zip

echo "Publishing version: $VERSION"
echo "Skip upload: $SKIP_UPLOAD"

# Validate required environment variables for CI
if [[ "$SKIP_UPLOAD" == "false" ]]; then
    if [[ -z "$MAVEN_CENTRAL_USERNAME" || -z "$MAVEN_CENTRAL_PASSWORD" ]]; then
        echo "ERROR: MAVEN_CENTRAL_USERNAME and MAVEN_CENTRAL_PASSWORD must be set for CI publishing"
        exit 1
    fi
    if [[ -z "$ORG_GRADLE_PROJECT_signingKey" || -z "$ORG_GRADLE_PROJECT_signingPassword" ]]; then
        echo "ERROR: ORG_GRADLE_PROJECT_signingKey and ORG_GRADLE_PROJECT_signingPassword must be set for CI publishing"
        exit 1
    fi
fi

# Build artifacts
echo "Building artifacts..."
./gradlew clean build generatePomFileForSonatypePublication

# Debug - List build output
echo "Listing build/libs directory:"
ls -la build/libs/

echo "Listing build/publications directory:"
if [[ -d "build/publications/" ]]; then
    find build/publications/ -name "*.xml" | head -5
else
    echo "build/publications/ directory not found"
fi

# Verify all required files exist
echo "Verifying required files exist..."
if [[ ! -f "build/libs/${ARTIFACT}-${VERSION}.jar" ]]; then
    echo "ERROR: Main JAR not found: build/libs/${ARTIFACT}-${VERSION}.jar"
    exit 1
fi
if [[ ! -f "build/libs/${ARTIFACT}-${VERSION}-sources.jar" ]]; then
    echo "ERROR: Sources JAR not found: build/libs/${ARTIFACT}-${VERSION}-sources.jar"
    exit 1
fi
if [[ ! -f "build/libs/${ARTIFACT}-${VERSION}-javadoc.jar" ]]; then
    echo "ERROR: Javadoc JAR not found: build/libs/${ARTIFACT}-${VERSION}-javadoc.jar"
    exit 1
fi
if [[ ! -f "build/publications/sonatype/pom-default.xml" ]]; then
    echo "ERROR: POM file not found: build/publications/sonatype/pom-default.xml"
    exit 1
fi
echo "All required files found!"

# Setup bundle directory
echo "Setting up bundle directory..."
rm -rf central-bundle
mkdir -p central-bundle/${GROUP_PATH}/${ARTIFACT}/${VERSION}/
cd central-bundle/${GROUP_PATH}/${ARTIFACT}/${VERSION}/
ORIGINAL_DIR=$(pwd | sed "s|/central-bundle.*|/central-bundle|")

# Copy artifacts
echo "Copying artifacts..."
cp ../../../../../build/libs/${ARTIFACT}-${VERSION}.jar .
cp ../../../../../build/libs/${ARTIFACT}-${VERSION}-sources.jar .
cp ../../../../../build/libs/${ARTIFACT}-${VERSION}-javadoc.jar .
cp ../../../../../build/publications/sonatype/pom-default.xml ${ARTIFACT}-${VERSION}.pom

# List copied files
echo "Copied files:"
ls -la

# Sign artifacts (only if we have signing keys)
if [[ -n "$ORG_GRADLE_PROJECT_signingKey" && -n "$ORG_GRADLE_PROJECT_signingPassword" ]]; then
    echo "Signing artifacts..."
    gpg --batch --yes --armor --detach-sign --pinentry-mode loopback --passphrase "${ORG_GRADLE_PROJECT_signingPassword}" ${ARTIFACT}-${VERSION}.jar
    gpg --batch --yes --armor --detach-sign --pinentry-mode loopback --passphrase "${ORG_GRADLE_PROJECT_signingPassword}" ${ARTIFACT}-${VERSION}-sources.jar
    gpg --batch --yes --armor --detach-sign --pinentry-mode loopback --passphrase "${ORG_GRADLE_PROJECT_signingPassword}" ${ARTIFACT}-${VERSION}-javadoc.jar
    gpg --batch --yes --armor --detach-sign --pinentry-mode loopback --passphrase "${ORG_GRADLE_PROJECT_signingPassword}" ${ARTIFACT}-${VERSION}.pom
    
    echo "Signed files:"
    ls -la *.asc
else
    echo "Skipping signing - no GPG keys provided"
fi

# Generate checksums
echo "Generating checksums..."
for file in *.jar *.pom; do
    md5sum "$file" > "$file.md5"
    sha1sum "$file" > "$file.sha1"
done

echo "Generated checksums:"
ls -la *.md5 *.sha1

# Create bundle
echo "Creating bundle..."
BUNDLE_ROOT=$(pwd | sed 's|/central-bundle/.*||')/central-bundle
cd "$BUNDLE_ROOT"
zip -r ../central-bundle.zip com/
cd ..

echo "Bundle created:"
ls -la central-bundle.zip

# Upload to Maven Central Portal (only in CI)
if [[ "$SKIP_UPLOAD" == "false" ]]; then
    echo "Uploading to Maven Central Portal..."
    # Disable command echoing for credentials
    set +x
    AUTH_TOKEN=$(echo -n "${MAVEN_CENTRAL_USERNAME}:${MAVEN_CENTRAL_PASSWORD}" | base64)
    set -x
    
    RESPONSE=$(curl --silent --request POST \
      --header "Authorization: Bearer ${AUTH_TOKEN}" \
      --form bundle=@central-bundle.zip \
      --form name="${ARTIFACT}-${VERSION}" \
      https://central.sonatype.com/api/v1/publisher/upload)
    
    echo "Upload response: $RESPONSE"
    
    # Check if response is a UUID (deployment ID) or JSON
    if [[ "$RESPONSE" =~ ^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$ ]]; then
        DEPLOYMENT_ID="$RESPONSE"
    else
        # Try to extract from JSON format
        DEPLOYMENT_ID=$(echo "$RESPONSE" | grep -o '"deploymentId":"[^"]*"' | cut -d'"' -f4)
    fi
    echo "Deployment ID: $DEPLOYMENT_ID"
    
    if [[ -n "$DEPLOYMENT_ID" ]]; then
        echo "✅ Upload successful!"
        echo "Check deployment status at: https://central.sonatype.com/publishing/deployments"
        echo "Deployment ID: $DEPLOYMENT_ID"
    else
        echo "❌ Upload failed"
        exit 1
    fi
else
    echo "Skipping upload - running locally"
    echo "Bundle ready for upload: central-bundle.zip"
    echo "Contents:"
    unzip -l central-bundle.zip
fi

echo "Script completed successfully!"