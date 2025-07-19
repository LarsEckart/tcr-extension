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

### Extension Hierarchy (Refactored)
All extensions now inherit from `AbstractTcrExtension` base class using template method pattern:

```
AbstractTcrExtension (base class)
├── TestCommitRevertExtension (full revert)
├── TestCommitRevertMainExtension (revert src/main/ only)
│   └── FastTestCommitRevertMainExtension (macOS AppleScript)
├── SilentTestCommitRevertMainExtension (silent + main revert)
├── CommitOnGreenExtension (commit only, no revert)
└── SilentCommitOnGreenExtension (silent commit only)
```

| Extension | Behavior | Use Case |
|-----------|----------|----------|
| `TestCommitRevertExtension` | Full revert (all changes) | Standard TCR - revert everything on failure |
| `TestCommitRevertMainExtension` | Revert only `src/main/` | Keep test changes, revert production code |
| `SilentTestCommitRevertMainExtension` | Silent commits with "working" message | Fast TCR without interactive prompts |
| `FastTestCommitRevertMainExtension` | macOS-optimized with AppleScript | Faster commit prompts on macOS |
| `CommitOnGreenExtension` | Commit on green, no revert | Commit when tests pass, do nothing on failure |
| `SilentCommitOnGreenExtension` | Silent commit on green | Commit with "work in progress" message |

### Key Components

1. **AbstractTcrExtension**: Base class implementing common JUnit interface (`TestExecutionExceptionHandler`, `AfterAllCallback`) with template methods `onTestsPassed()` and `onTestsFailed()`
2. **GitOperations**: Utility class centralizing all git operations (`getRootFolder()`, `isGitEmpty()`, `readStream()`, `runOnConsole()`)
3. **AbstractCommitPrompt**: Base class for UI frameworks with common Swing dialog infrastructure
4. **Commit Message Handling**: 
   - `ArlosGitNotationPrompt`/`ArlosGitNotation2Prompt`: Swing UI with Arlo's git notation (extend AbstractCommitPrompt)
   - `FastTestCommitRevertMainExtension`: macOS AppleScript dialog
   - Silent extensions: Hardcoded commit messages

### TCR Workflow

1. **Test Execution**: JUnit runs tests with extension attached
2. **Exception Handling**: `AbstractTcrExtension.handleTestExecutionException()` tracks failures  
3. **After All Tests**: `AbstractTcrExtension.afterAll()` calls `onTestsPassed()` or `onTestsFailed()` based on results
4. **Git Operations**: `GitOperations` utility handles git root discovery, status checking, and command execution
5. **Commit Messages**: Interactive (UI prompts) or silent (hardcoded) commit message generation
6. **Amend Support**: Users can type "amend" (case-insensitive) to perform `git commit --amend --no-edit` instead of regular commit

## Testing Approach

- **Self-Dogfooding**: Tests use the TCR extension themselves (`@ExtendWith(TestCommitRevertExtension.class)`)
- **Approval Testing**: UI components tested with ApprovalTests (macOS only)
- **Dependencies**: JUnit 5, ApprovalTests for UI testing

## Project Structure

- `build.gradle.kts` - Gradle build file (Kotlin DSL)
- `settings.gradle.kts` - Gradle settings file
- `src/main/java/com/github/larseckart/tcr/` - Main extension implementations
- `src/test/java/com/larseckart/tcr/` - Self-dogfooding tests
- Build via Gradle with Java 17 toolchain (Azul Zulu)
- Published to Maven Central via GitHub Actions with PGP signing

## Recent Refactoring (Issue #124)

**Major code duplication eliminated**: Refactored to extract common git operations and use inheritance hierarchy.

### Architecture Improvements:
- **~70% codebase reduction** while maintaining all functionality
- **Single source of truth** for git operations via `GitOperations` utility class
- **Template method pattern** via `AbstractTcrExtension` eliminates JUnit interface duplication
- **UI framework abstraction** via `AbstractCommitPrompt` reduces Swing code duplication
- **Dead code removal** from commit-only extensions

### Benefits:
- Easier maintenance (fix bugs once, apply everywhere)
- Better testability (test common functionality centrally)
- Enhanced extensibility (new extensions inherit common behavior)
- Cleaner codebase with established patterns

## Known Limitations

- No multi-module Gradle support
- No suite-level extension declaration
- Limited error handling for git operations
