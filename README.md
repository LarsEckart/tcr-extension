[![test](https://github.com/LarsEckart/tcr-extension/actions/workflows/test.yml/badge.svg)](https://github.com/LarsEckart/tcr-extension/actions/workflows/test.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.larseckart/junit-tcr-extensions)](https://central.sonatype.com/artifact/com.larseckart/junit-tcr-extensions)

# tcr-extension

JUnit 5 extensions for automating **Test-Commit-Revert (TCR)** workflow - taking the manual git ceremony out of Kent Beck's innovative development approach.

## Table of Contents

* [About](#about)
* [Getting Started](#getting-started)
  * [Starter Project](#starter-project)
  * [Gradle](#gradle)
  * [Maven](#maven)
* [Usage](#usage)
  * [Examples](#examples)
  * [Which Extension Should I Use?](#which-extension-should-i-use)
  * [Amend Support](#amend-support)
* [Limitations](#limitations)
* [License](#license)
* [Questions](#questions)

## About

[Kent Beck's test && commit || revert](https://medium.com/@kentbeck_7670/test-commit-revert-870bbd756864) introduced an interesting development workflow in 2018: take very small steps, commit when tests pass, revert when they fail. This forces you to work in tiny increments and throw away code that doesn't work.

**The Problem:** Manually performing all the git operations (checking status, committing, reverting) becomes tedious ceremony that interrupts the flow of development.

**The Solution:** These JUnit 5 extensions automate the git operations, letting you focus on writing code while the extension handles the commit/revert decisions based on your test results.

Originally inspired by JUnit 4 runners for TCR, this project was born while working on [ApprovalTests.Java](https://github.com/approvals/ApprovalTests.Java) when I discovered JUnit 5 [Extensions](https://junit.org/junit5/docs/current/user-guide/#extensions).

## Getting Started

### Starter Project

The easiest way to start a new project with this is by cloning [the starter project](https://github.com/LarsEckart/tcr-extension.starterproject).

### Gradle

```groovy
dependencies {
    testImplementation("com.larseckart:junit-tcr-extensions:1.0.0")
}
```

### Maven

```xml

<dependency>
  <groupId>com.larseckart</groupId>
  <artifactId>junit-tcr-extensions</artifactId>
  <version>1.0.0</version>
  <scope>test</scope>
</dependency>
```

## Usage

### Examples

```java
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.larseckart.tcr.TestCommitRevertExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestCommitRevertExtension.class)
class LibraryTest {

  @Test
  void testSomeLibraryMethod() {
    assertEquals("42", "4" + "2");
  }
}
```

### Which extension should I use?

| I want to | Extension |
|-----------|-----------|
| ... commit when tests pass, but not revert anything when they fail | CommitOnGreenExtension |
| ... commit when tests pass, revert when they fail | TestCommitRevertExtension | 
| ... do TCR but don't bug me with commit messages | SilentTestCommitRevertMainExtension |
| ... do TCR but don't revert my tests, only my production code | TestCommitRevertMainExtension |
| ... do TCR without reverting my tests and I want it to be fast on macOS | FastTestCommitRevertMainExtension |

**Code:** `@ExtendWith(<extension_from_above>.class)`

### Amend Support

In interactive extensions (those that prompt for commit messages), you can type `amend` (case-insensitive) to amend the last commit instead of creating a new one. This executes `git commit --amend --no-edit`.

## Limitations

* Does not support Gradle multi module projects
* Major code duplication has been eliminated through recent refactoring efforts.
* No support yet to declare this extension for the whole test suit (at least I'm not aware).
* GitHub Actions automate CI and release to Maven Central

## License

[Apache 2.0 License](https://github.com/LarsEckart/tcr-extension/blob/main/LICENSE)

## Questions?

Reach out on twitter: [@LrsEckrt](https://twitter.com/LrsEckrt)
or publish an [issue](https://github.com/LarsEckart/tcr-extension/issues).

