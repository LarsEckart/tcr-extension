package com.github.larseckart.tcr;

import java.io.File;
import java.io.IOException;

public class GitOperations {

  private static ProcessExecutor executor = new ProcessBuilderExecutor();
  
  // Package-visible for testing
  static void setExecutor(ProcessExecutor executor) {
    GitOperations.executor = executor;
  }

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
      String output = executor.executeCommandForOutput(gitDir, "git", "status");
      return output.contains("nothing to commit");
    } catch (Exception e) {
      return false;
    }
  }

  public static void stageAllChanges(File gitDir) {
    try {
      executor.executeCommand(gitDir, "git", "add", "-A");
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void amendCommit(File gitDir) {
    try {
      String output = executor.executeCommandForOutput(gitDir, "git", "log", "--oneline", "-1");
      if (output.trim().isEmpty()) {
        System.err.println("Cannot amend: no previous commits found");
        return;
      }
    } catch (Exception e) {
      System.err.println("Cannot amend: no previous commits found");
      return;
    }
    try {
      executor.executeCommand(gitDir, "git", "commit", "--amend", "--no-edit");
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean isAmendMessage(String message) {
    return "amend".equalsIgnoreCase(message);
  }

  public static void commit(File gitDir, String message) {
    if (isAmendMessage(message)) {
      amendCommit(gitDir);
    } else {
      try {
        executor.executeCommand(gitDir, "git", "commit", "-m", message);
      } catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void revertAllChanges(File gitDir) {
    try {
      executor.executeCommand(gitDir, "git", "clean", "-fd");
      executor.executeCommand(gitDir, "git", "reset", "--hard", "HEAD");
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void revertMainDirectoryOnly(File gitDir) {
    try {
      executor.executeCommand(gitDir, "git", "checkout", "src/main/");
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
