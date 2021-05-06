[![test](https://github.com/LarsEckart/tcr-extension/actions/workflows/test.yml/badge.svg)](https://github.com/LarsEckart/tcr-extension/actions/workflows/test.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.larseckart/junit-tcr-extensions/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.larseckart/junit-tcr-extensions)

# tcr-extension

[Kent Beck's test && commit || revert](https://medium.com/@kentbeck_7670/test-commit-revert-870bbd756864)
was an interesting and new approach at the end of 2018. Taking very small steps, allowing yourself
to throw away code that didn't work and starting over, it's been fun to give that a try. Doing all
the git ceremony around the workflow manually turned out to be quite tedious though. For Java &
IntelliJ, there was no continuous test runner yet and using file watchers and bash scripts also
didn't produce good results. While working
on [ApprovalTests.Java](https://github.com/approvals/ApprovalTests.Java), I stumbled on a few JUnit4
runners around that topic. To use them with JUnit5, I learned
about [Extensions](https://junit.org/junit5/docs/current/user-guide/#extensions) and that is how
this project was born.

## How to get it

### Gradle

```groovy
dependencies {
    testImplementation("com.larseckart:junit-tcr-extensions:0.0.1")
}
```

### Maven

```xml

<dependency>
  <groupId>com.larseckart</groupId>
  <artifactId>junit-tcr-extensions</artifactId>
  <version>0.0.1</version>
  <scope>test</scope>
</dependency>
```

## Examples

```java
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.larseckart.tcr.TestCommitRevertExtension;
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

## Limitations

* Does not support Gradle multi module projects
* Right now there is a lot of duplication, I expect a lot of refactoring. Luckily this won't
  influence the usage at all.
* No support yet to declare this extension for the whole test suit (at least I'm not aware).
* No GitHub actions yet to automate Ci and release

## LICENSE

[Apache 2.0 License](https://github.com/LarsEckart/tcr-extension/blob/main/LICENSE)

## Questions?

Reach out on twitter: [@LrsEckrt](https://twitter.com/LrsEckrt)
or publish an [issue](https://github.com/LarsEckart/tcr-extension/issues).

