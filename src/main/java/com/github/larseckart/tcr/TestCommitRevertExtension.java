package com.github.larseckart.tcr;

import java.io.File;

public class TestCommitRevertExtension extends AbstractTcrExtension {

  @Override
  protected void onTestsPassed(File gitDir) {
    if (GitOperations.isGitEmpty(gitDir)) {
      System.out.println("Nothing to commit");
      return;
    }
    String message = ArlosGitNotation2Prompt.display();
    if (!message.isEmpty()) {
      GitOperations.runOnConsole(gitDir, "git", "add", "-A");
      GitOperations.runOnConsole(gitDir, "git", "commit", "-m", message);
    }
  }

  @Override
  protected void onTestsFailed(File gitDir) {
    GitOperations.runOnConsole(gitDir, "git", "clean", "-fd");
    GitOperations.runOnConsole(gitDir, "git", "reset", "--hard", "HEAD");
    System.out.println("Test Failed, reverting...");
  }
}
