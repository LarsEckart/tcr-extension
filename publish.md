# Manual Publishing to Maven Central

This document describes how to manually publish this library to Maven Central using the new Central Publishing Portal API.

## Background

As of 2024, Sonatype has migrated from the legacy OSSRH system to a new Central Publishing Portal. While there's no official Gradle plugin yet, we can publish manually using the Portal API.

## Prerequisites

### 1. Central Portal Account Setup
- Create account at [https://central.sonatype.com/](https://central.sonatype.com/)
- Generate a user token from your account settings
- Save the token securely (format: `username:token`)

### 2. GPG Key Setup
- Generate GPG key pair if not already available:
  ```bash
  gpg --gen-key
  ```
- Export public key to keyserver:
  ```bash
  gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
  ```
- Export private key for signing:
  ```bash
  gpg --export-secret-keys YOUR_KEY_ID > private.key
  ```

## Manual Publishing Process

### Step 1: Build Artifacts

Build all required artifacts using Gradle:

```bash
# Clean build
./gradlew clean

# Build main JAR, sources JAR, and javadoc JAR
./gradlew build

# Alternatively, build specific artifacts
./gradlew jar
./gradlew sourcesJar  
./gradlew javadocJar
```

**Required artifacts:**
- `build/libs/junit-tcr-extensions-0.0.4.jar` (main JAR)
- `build/libs/junit-tcr-extensions-0.0.4-sources.jar` (sources)
- `build/libs/junit-tcr-extensions-0.0.4-javadoc.jar` (javadoc)
- `build/publications/sonatype/pom-default.xml` (POM file)

### Step 2: Sign All Artifacts

Sign each artifact with GPG:

```bash
cd build/libs

# Sign main JAR
gpg --armor --detach-sign junit-tcr-extensions-0.0.4.jar

# Sign sources JAR  
gpg --armor --detach-sign junit-tcr-extensions-0.0.4-sources.jar

# Sign javadoc JAR
gpg --armor --detach-sign junit-tcr-extensions-0.0.4-javadoc.jar

# Sign POM (copy from publications directory first)
cp ../publications/sonatype/pom-default.xml junit-tcr-extensions-0.0.4.pom
gpg --armor --detach-sign junit-tcr-extensions-0.0.4.pom
```

This creates `.asc` signature files for each artifact.

### Step 3: Create Maven Directory Structure

Create the proper Maven repository directory structure:

```bash
mkdir -p central-bundle/com/larseckart/junit-tcr-extensions/0.0.4/
cd central-bundle/com/larseckart/junit-tcr-extensions/0.0.4/

# Copy all artifacts and signatures
cp ../../../../build/libs/junit-tcr-extensions-0.0.4.jar .
cp ../../../../build/libs/junit-tcr-extensions-0.0.4.jar.asc .
cp ../../../../build/libs/junit-tcr-extensions-0.0.4-sources.jar .
cp ../../../../build/libs/junit-tcr-extensions-0.0.4-sources.jar.asc .
cp ../../../../build/libs/junit-tcr-extensions-0.0.4-javadoc.jar .
cp ../../../../build/libs/junit-tcr-extensions-0.0.4-javadoc.jar.asc .
cp ../../../../build/libs/junit-tcr-extensions-0.0.4.pom .
cp ../../../../build/libs/junit-tcr-extensions-0.0.4.pom.asc .
```

### Step 4: Generate Checksums

Generate required checksum files:

```bash
# MD5 checksums
md5sum junit-tcr-extensions-0.0.4.jar > junit-tcr-extensions-0.0.4.jar.md5
md5sum junit-tcr-extensions-0.0.4-sources.jar > junit-tcr-extensions-0.0.4-sources.jar.md5
md5sum junit-tcr-extensions-0.0.4-javadoc.jar > junit-tcr-extensions-0.0.4-javadoc.jar.md5
md5sum junit-tcr-extensions-0.0.4.pom > junit-tcr-extensions-0.0.4.pom.md5

# SHA1 checksums  
sha1sum junit-tcr-extensions-0.0.4.jar > junit-tcr-extensions-0.0.4.jar.sha1
sha1sum junit-tcr-extensions-0.0.4-sources.jar > junit-tcr-extensions-0.0.4-sources.jar.sha1
sha1sum junit-tcr-extensions-0.0.4-javadoc.jar > junit-tcr-extensions-0.0.4-javadoc.jar.sha1
sha1sum junit-tcr-extensions-0.0.4.pom > junit-tcr-extensions-0.0.4.pom.sha1
```

### Step 5: Create Bundle ZIP

Create the deployment bundle:

```bash
cd ../../.. # Back to central-bundle directory
zip -r central-bundle.zip com/
```

**Final bundle structure:**
```
central-bundle.zip
└── com/
    └── larseckart/
        └── junit-tcr-extensions/
            └── 0.0.4/
                ├── junit-tcr-extensions-0.0.4.jar
                ├── junit-tcr-extensions-0.0.4.jar.asc
                ├── junit-tcr-extensions-0.0.4.jar.md5
                ├── junit-tcr-extensions-0.0.4.jar.sha1
                ├── junit-tcr-extensions-0.0.4-sources.jar
                ├── junit-tcr-extensions-0.0.4-sources.jar.asc
                ├── junit-tcr-extensions-0.0.4-sources.jar.md5
                ├── junit-tcr-extensions-0.0.4-sources.jar.sha1
                ├── junit-tcr-extensions-0.0.4-javadoc.jar
                ├── junit-tcr-extensions-0.0.4-javadoc.jar.asc
                ├── junit-tcr-extensions-0.0.4-javadoc.jar.md5
                ├── junit-tcr-extensions-0.0.4-javadoc.jar.sha1
                ├── junit-tcr-extensions-0.0.4.pom
                ├── junit-tcr-extensions-0.0.4.pom.asc
                ├── junit-tcr-extensions-0.0.4.pom.md5
                └── junit-tcr-extensions-0.0.4.pom.sha1
```

### Step 6: Upload to Central Portal

Upload the bundle using the Portal API:

```bash
# Base64 encode your token
echo -n "username:token" | base64

# Upload bundle
curl --request POST \
  --header 'Authorization: Bearer <base64-encoded-token>' \
  --form bundle=@central-bundle.zip \
  --form name="junit-tcr-extensions-0.0.4" \
  https://central.sonatype.com/api/v1/publisher/upload
```

**Response example:**
```json
{
  "deploymentId": "12345678-1234-1234-1234-123456789abc",
  "deploymentName": "junit-tcr-extensions-0.0.4",
  "deploymentState": "PENDING"
}
```

### Step 7: Check Deployment Status

Monitor the deployment validation:

```bash
curl --request POST \
  --header 'Authorization: Bearer <base64-encoded-token>' \
  --header 'Content-Type: application/json' \
  --data '{}' \
  https://central.sonatype.com/api/v1/publisher/status?id=<deploymentId>
```

Wait for `deploymentState` to become `VALIDATED`.

### Step 8: Publish Deployment

Once validated, publish the deployment:

```bash
curl --request POST \
  --header 'Authorization: Bearer <base64-encoded-token>' \
  --header 'Content-Type: application/json' \
  --data '{}' \
  https://central.sonatype.com/api/v1/publisher/deployment/<deploymentId>
```

The deployment will transition to `PUBLISHING` and then `PUBLISHED`.

## Automation Scripts

### Manual Publishing Script

For convenience, create a `manual-publish.sh` script:

```bash
#!/bin/bash
set -e

# Security: Disable command echoing when handling credentials
set +x

VERSION="0.0.4"
ARTIFACT="junit-tcr-extensions"
GROUP_PATH="com/larseckart"

# Validate required environment variables exist
if [[ -z "$MAVEN_CENTRAL_USERNAME" || -z "$MAVEN_CENTRAL_PASSWORD" ]]; then
    echo "ERROR: MAVEN_CENTRAL_USERNAME and MAVEN_CENTRAL_PASSWORD must be set"
    exit 1
fi

echo "Building artifacts..."
./gradlew clean build

echo "Setting up bundle directory..."
mkdir -p central-bundle/${GROUP_PATH}/${ARTIFACT}/${VERSION}/
cd central-bundle/${GROUP_PATH}/${ARTIFACT}/${VERSION}/

echo "Copying artifacts..."
cp ../../../../build/libs/${ARTIFACT}-${VERSION}.jar .
cp ../../../../build/libs/${ARTIFACT}-${VERSION}-sources.jar .
cp ../../../../build/libs/${ARTIFACT}-${VERSION}-javadoc.jar .
cp ../../../../build/publications/sonatype/pom-default.xml ${ARTIFACT}-${VERSION}.pom

echo "Signing artifacts..."
gpg --armor --detach-sign ${ARTIFACT}-${VERSION}.jar
gpg --armor --detach-sign ${ARTIFACT}-${VERSION}-sources.jar
gpg --armor --detach-sign ${ARTIFACT}-${VERSION}-javadoc.jar
gpg --armor --detach-sign ${ARTIFACT}-${VERSION}.pom

echo "Generating checksums..."
for file in *.jar *.pom; do
    md5sum "$file" > "$file.md5"
    sha1sum "$file" > "$file.sha1"
done

echo "Creating bundle..."
cd ../../..
zip -r central-bundle.zip com/

echo "Uploading to Maven Central Portal..."
# Base64 encode credentials securely (no echo)
AUTH_TOKEN=$(echo -n "${MAVEN_CENTRAL_USERNAME}:${MAVEN_CENTRAL_PASSWORD}" | base64)

# Upload with silent curl to avoid credential exposure
RESPONSE=$(curl --silent --request POST \
  --header "Authorization: Bearer ${AUTH_TOKEN}" \
  --form bundle=@central-bundle.zip \
  --form name="${ARTIFACT}-${VERSION}" \
  https://central.sonatype.com/api/v1/publisher/upload)

echo "Upload response: $RESPONSE"

# Extract deployment ID for status checking
DEPLOYMENT_ID=$(echo "$RESPONSE" | grep -o '"deploymentId":"[^"]*"' | cut -d'"' -f4)
echo "Deployment ID: $DEPLOYMENT_ID"

if [[ -n "$DEPLOYMENT_ID" ]]; then
    echo "Check status with:"
    echo "curl --silent --request POST --header 'Authorization: Bearer \$AUTH_TOKEN' --header 'Content-Type: application/json' --data '{}' https://central.sonatype.com/api/v1/publisher/status?id=$DEPLOYMENT_ID"
fi
```

### GitHub Actions Workflow Update

The existing GitHub Actions workflow in `.github/workflows/release.yml` can be updated to use the new Portal API. Here's the recommended configuration:

```yaml
name: Release to Maven Central Portal

on:
  workflow_dispatch:
    inputs:
      version:
        description: 'Version to release'
        required: true
        default: '0.0.4'

jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - name: Check out repo
        uses: actions/checkout@v4
        
      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: 'zulu'
          java-version: 11
          
      - name: Setup Gradle Build Action
        uses: gradle/gradle-build-action@v3
        with:
          gradle-home-cache-cleanup: true
          
      - name: Build artifacts
        run: ./gradlew clean build
        
      - name: Publish to Maven Central Portal
        env:
          MAVEN_CENTRAL_USERNAME: ${{ secrets.MAVEN_CENTRAL_USERNAME }}
          MAVEN_CENTRAL_PASSWORD: ${{ secrets.MAVEN_CENTRAL_PASSWORD }}
          ORG_GRADLE_PROJECT_signingKey: ${{ secrets.ORG_GRADLE_PROJECT_SIGNINGKEY }}
          ORG_GRADLE_PROJECT_signingPassword: ${{ secrets.ORG_GRADLE_PROJECT_SIGNINGPASSWORD }}
        run: |
          # Use the manual publishing script
          chmod +x manual-publish.sh
          ./manual-publish.sh
```

## Troubleshooting

### Common Issues

1. **GPG signing fails**: Ensure GPG key is properly configured and available
2. **Validation fails**: Check that all required metadata is present in POM
3. **Authentication fails**: Verify token is correctly base64 encoded
4. **Bundle structure wrong**: Ensure Maven directory structure is correct

### Validation Requirements

The Portal validates that your bundle contains:
- ✅ Valid POM with required metadata (name, description, URL, license, developers, SCM)
- ✅ Main JAR file
- ✅ Sources JAR file  
- ✅ Javadoc JAR file
- ✅ GPG signatures for all files
- ✅ Proper checksums (MD5, SHA1)

## Official Documentation

- [Central Publishing Portal Requirements](https://central.sonatype.org/publish/requirements/)
- [Portal API Documentation](https://central.sonatype.org/publish/publish-portal-api/)
- [Publishing Portal Overview](https://central.sonatype.org/publish/publish-portal-maven/)
- [Gradle Publishing (Community Solutions)](https://central.sonatype.org/publish/publish-portal-gradle/)

## GitHub Actions Integration

### GitHub Secrets Configuration

The following secrets are configured in the GitHub repository for automated publishing:

- `MAVEN_CENTRAL_USERNAME` - Username for Maven Central Portal
- `MAVEN_CENTRAL_PASSWORD` - Token for Maven Central Portal  
- `ORG_GRADLE_PROJECT_SIGNINGKEY` - GPG private key for signing
- `ORG_GRADLE_PROJECT_SIGNINGPASSWORD` - GPG key passphrase

### Environment Variables for Manual Use

For local manual publishing, use these environment variables:

```bash
# IMPORTANT: Never print these values or commit them to version control
export MAVEN_CENTRAL_USERNAME="your-username"
export MAVEN_CENTRAL_PASSWORD="your-token"
export GPG_PRIVATE_KEY="$(cat private.key | base64)"
export GPG_PASSPHRASE="your-passphrase"
```

### Security Notes

⚠️ **CRITICAL**: Never expose these credentials in logs, console output, or version control:
- Use `echo` commands carefully - avoid printing credential values
- Set bash scripts to `set +x` when handling credentials
- Use `--silent` or `--quiet` flags in curl commands when possible
- Store credentials in GitHub Secrets, not in code