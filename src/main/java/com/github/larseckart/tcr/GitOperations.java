package com.github.larseckart.tcr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GitOperations {

  private static final boolean PRINT_ONLY = false;

  public static File getRootFolder() {
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

  public static boolean isGitEmpty(File gitDir) {
    try {
      String output = runOnConsoleForOutput(gitDir, "git", "status");
      return output.contains("nothing to commit");
    } catch (Exception e) {
      return false;
    }
  }

  public static void stageAllChanges(File gitDir) {
    runOnConsole(gitDir, "git", "add", "-A");
  }

  public static void amendCommit(File gitDir) {
    try {
      String output = runOnConsoleForOutput(gitDir, "git", "log", "--oneline", "-1");
      if (output.trim().isEmpty()) {
        System.err.println("Cannot amend: no previous commits found");
        return;
      }
    } catch (Exception e) {
      System.err.println("Cannot amend: no previous commits found");
      return;
    }
    runOnConsole(gitDir, "git", "commit", "--amend", "--no-edit");
  }

  public static boolean isAmendMessage(String message) {
    return "amend".equalsIgnoreCase(message);
  }

  public static void commit(File gitDir, String message) {
    if (isAmendMessage(message)) {
      amendCommit(gitDir);
    } else {
      runOnConsole(gitDir, "git", "commit", "-m", message);
    }
  }

  public static void revertAllChanges(File gitDir) {
    runOnConsole(gitDir, "git", "clean", "-fd");
    runOnConsole(gitDir, "git", "reset", "--hard", "HEAD");
  }

  public static void revertMainDirectoryOnly(File gitDir) {
    runOnConsole(gitDir, "git", "checkout", "src/main/");
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

  private static String runOnConsoleForOutput(File workingDir, String... cmdArgs) throws IOException, InterruptedException {
    if (PRINT_ONLY) {
      System.out.println(Arrays.toString(cmdArgs));
      return "";
    }
    Process p = Runtime.getRuntime().exec(cmdArgs, null, workingDir);
    p.waitFor();
    return readStream(p.getInputStream());
  }
}
