#!/bin/bash

# Poll Maven Central for junit-tcr-extensions version 0.0.5
# Shows macOS notification when version becomes available

VERSION="0.0.5"
URL="https://repo1.maven.org/maven2/com/larseckart/junit-tcr-extensions/"
INTERVAL=30

echo "Polling Maven Central for junit-tcr-extensions version $VERSION..."
echo "Checking every $INTERVAL seconds. Press Ctrl+C to stop."

while true; do
    # Check if version directory exists
    if curl -s --head "$URL$VERSION/" | head -n 1 | grep -q "200"; then
        echo "✅ Version $VERSION found on Maven Central!"
        
        # Show macOS notification
        osascript -e "display notification \"junit-tcr-extensions $VERSION is now available on Maven Central\" with title \"Maven Central Update\" sound name \"Glass\""
        
        exit 0
    else
        echo "$(date '+%Y-%m-%d %H:%M:%S') - Version $VERSION not yet available"
    fi
    
    sleep $INTERVAL
done