# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java library providing JUnit 5 extensions for **Test-Commit-Revert (TCR)** workflow - Kent Beck's approach where tests either pass and commit, or fail and revert. 
The library automates git operations that would otherwise be manual ceremony.

**Key Details:**
- **Artifact**: `com.larseckart:junit-tcr-extensions:0.0.4`
- **Java Version**: 17+
- **Published**: Maven Central via GitHub Actions

## Common Commands

```bash
# Run tests
./gradlew test

# Run tests on Java 17
./gradlew testsOn17

# Build project (includes sources and javadoc)
./gradlew build

# Publish to Maven Central (via GitHub Actions)
gh workflow run release.yml
```

## Core Architecture

### Extension Hierarchy
All extensions implement `TestExecutionExceptionHandler` and `AfterAllCallback` with shared git operations but differentiated behavior:

| Extension | Behavior | Use Case |
|-----------|----------|----------|
| `TestCommitRevertExtension` | Full revert (all changes) | Standard TCR - revert everything on failure |
| `TestCommitRevertMainExtension` | Revert only `src/main/` | Keep test changes, revert production code |
| `SilentTestCommitRevertMainExtension` | Silent commits with "working" message | Fast TCR without interactive prompts |
| `FastTestCommitRevertMainExtension` | macOS-optimized with AppleScript | Faster commit prompts on macOS |
| `CommitOnGreenExtension` | Commit on green, no revert | Commit when tests pass, do nothing on failure |
| `SilentCommitOnGreenExtension` | Silent commit on green | Commit with "work in progress" message |

### Key Components

1. **Git Operations** (`getRootFolder()`, `runOnConsole()`): Git directory discovery, process execution, and stream handling
2. **Commit Message Handling**: 
   - `ArlosGitNotationPrompt`/`ArlosGitNotation2Prompt`: Swing UI with Arlo's git notation
   - `FastTestCommitRevertMainExtension`: macOS AppleScript dialog
   - Silent extensions: Hardcoded commit messages
3. **Test Lifecycle Integration**: Extensions track failures via `TestExecutionExceptionHandler` and execute git operations in `AfterAllCallback`

### TCR Workflow

1. **Test Execution**: JUnit runs tests with extension attached
2. **Exception Handling**: `handleTestExecutionException()` tracks failures  
3. **After All Tests**: `afterAll()` executes git operations based on test results
4. **Git Operations**: Find git root, check status, execute commands
5. **Commit Messages**: Interactive or silent commit message generation

## Testing Approach

- **Self-Dogfooding**: Tests use the TCR extension themselves (`@ExtendWith(TestCommitRevertExtension.class)`)
- **Approval Testing**: UI components tested with ApprovalTests (macOS only)
- **Dependencies**: JUnit 5, ApprovalTests for UI testing

## Project Structure

- `build.gradle.kts` - Gradle build file (Kotlin DSL)
- `settings.gradle.kts` - Gradle settings file
- `src/main/java/com/github/larseckart/tcr/` - Main extension implementations
- `src/test/java/com/larseckart/tcr/` - Self-dogfooding tests
- Build via Gradle with Java 11 toolchain (Azul Zulu)
- Published to Maven Central via GitHub Actions with PGP signing

## Known Limitations

- No multi-module Gradle support
- Significant code duplication across extensions  
- No suite-level extension declaration
- Limited error handling for git operations
