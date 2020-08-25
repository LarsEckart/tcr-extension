package com.github.larseckart.tcr;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SilentTestCommitRevertMainExtension
    implements TestExecutionExceptionHandler, AfterAllCallback {

  private static final boolean PRINT_ONLY = false;
  private boolean failures = false;

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    failures = true;
    throw throwable;
  }

  @Override
  public void afterAll(ExtensionContext context) {
    File gitDir = getRootFolder();
    if (gitDir == null) {
      System.err.println("No .git repo found at " + new File(".").getAbsolutePath());
      return;
    }
    if (failures) {
      revertGit(gitDir);
    } else {
      commit(gitDir);
    }
  }

  private static File getRootFolder() {
    try {
      File file = new File(".").getCanonicalFile();
      while (true) {
        File gitFolder = new File(file, ".git");
        if (gitFolder.exists()) {
          return file;
        }
        file = file.getParentFile();
        if (file == null) {
          return null;
        }
      }
    } catch (IOException e) {
      return null;
    }
  }

  private static void revertGit(File gitDir) {
    runOnConsole(gitDir, "git", "checkout", "src/main/");
    System.out.println("Test Failed, reverting...");
  }

  private static void commit(File gitDir) {
    if (isGitEmpty(gitDir)) {
      System.out.println("Nothing to commit");
      return;
    }
    runOnConsole(gitDir, "git", "add", "-A");
    runOnConsole(gitDir, "git", "commit", "-m", "working");
  }

  private static boolean isGitEmpty(File gitDir) {
    try {
      runOnConsole(gitDir, "git", "status");
      Process p = Runtime.getRuntime().exec(new String[] {"git", "status"}, null, gitDir);
      p.waitFor();

      String text = readStream(p.getInputStream());
      return text.contains("nothing to commit");
    } catch (Exception e) {
      return false;
    }
  }

  private static String readStream(InputStream inputStream) {
    try (var reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      return reader.lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void runOnConsole(File workingDir, String... cmdArgs) throws Error {
    if (PRINT_ONLY) {
      System.out.println(Arrays.toString(cmdArgs));
      return;
    }
    try {
      Process p = Runtime.getRuntime().exec(cmdArgs, null, workingDir);
      p.waitFor();
      System.out.println(readStream(p.getInputStream()));
      System.out.println(readStream(p.getErrorStream()));
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
