# GPG Signing for Maven Central

This document outlines the GPG key management strategy for publishing this library to Maven Central via GitHub Actions.

## Strategy

- **No local GPG key required** - all signing happens in GitHub Actions
- **Key storage**: 1Password for secure backup and GitHub Secrets for CI
- **Long-lived key**: 5+ year expiration for library stability
- **Public key distribution**: Multiple keyservers for reliability

## Initial Setup

### 1. Generate GPG Key

Generate the key in a temporary environment (can be local, then delete):

```bash
# Generate new key
gpg --full-generate-key

# Choose:
# - Key type: (1) RSA and RSA (default)
# - Key size: 4096
# - Expiration: 5y (5 years)
# - Real name: Lars Eckart
# - Email: lars.eckart@hey.com
# - Passphrase: [strong passphrase]
```

### 2. Export Keys

Run the export script:

```bash
./scripts/export-keys.sh
```

### 3. Store in 1Password

After running the export script, create a secure note with these values:

**From CLI output:**
- **Private key** (base64 encoded) - copy the long base64 string printed to terminal
- **Public key** (armor format) - copy the ASCII-armored block starting with `-----BEGIN PGP PUBLIC KEY BLOCK-----`
- **Passphrase** - the passphrase you set when generating the key

**From gpg-vars.sh file:**
- **Key ID** - value of `GPG_KEY_ID` 
- **Fingerprint** - value of `GPG_FINGERPRINT`

**From gpg output:**
- **Expiration date** - run `gpg --list-keys $GPG_KEY_ID` to see expiration

### 4. Upload Public Key to Keyservers

Run the upload script:

```bash
./scripts/upload-keys.sh
```

### 5. Configure GitHub Secrets

Run the GitHub secrets setup script:

```bash
./scripts/setup-github-secrets.sh
```

Alternatively, manually add in repository settings > Secrets and variables > Actions:
- `ORG_GRADLE_PROJECT_SIGNINGKEY`: Base64 encoded private key
- `ORG_GRADLE_PROJECT_SIGNINGPASSWORD`: Key passphrase

### 6. Clean Up Local Environment

```bash
# Source variables if needed
source gpg-vars.sh

# Delete local keys (optional - they're safely stored in 1Password)
gpg --delete-secret-keys $GPG_KEY_ID
gpg --delete-keys $GPG_KEY_ID

# Remove temporary file
rm gpg-vars.sh
```

## Verification

### Verify Public Key Distribution

Run the keyserver verification script:

```bash
./scripts/verify-keyservers.sh
```

### Verify GitHub Actions Setup

The release workflow automatically:
1. Imports the private key from secrets
2. Signs all artifacts during publish
3. Uploads signed artifacts to Maven Central

### Verify Maven Central Signatures

Run the Maven signature verification script:

```bash
./scripts/verify-maven-signatures.sh VERSION
```

Example:
```bash
./scripts/verify-maven-signatures.sh 0.0.4
```

## Troubleshooting

### "Invalid signature for file" Error

This usually means:
1. Public key isn't available on keyservers
2. Key ID mismatch between local and GitHub Actions
3. Expired key

**Solution**: Verify public key is uploaded to keyservers and matches the private key in GitHub secrets.

### Key Expiration

Run the key expiration extension script:

```bash
./scripts/extend-key-expiration.sh [YEARS]
```

Example (extends for 5 years):
```bash
./scripts/extend-key-expiration.sh 5
```

This will:
1. Extend the key expiration
2. Re-upload to keyservers
3. Update 1Password with new expiration
4. Update GitHub secrets if needed

## Current Key Information

- **Key ID**: 8C5D6176349A7D94
- **Fingerprint**: 6BC6C2203FAD7B24C17654F98C5D6176349A7D94
- **Expires**: 2030-07-18
- **Email**: lars.eckart@hey.com

## Scripts

All GPG operations are automated with scripts in the `scripts/` directory:

- `export-keys.sh` - Export GPG keys and save variables
- `upload-keys.sh` - Upload public key to keyservers
- `verify-keyservers.sh` - Verify key distribution
- `verify-maven-signatures.sh` - Verify Maven Central signatures
- `setup-github-secrets.sh` - Configure GitHub repository secrets
- `extend-key-expiration.sh` - Extend key expiration and re-upload
