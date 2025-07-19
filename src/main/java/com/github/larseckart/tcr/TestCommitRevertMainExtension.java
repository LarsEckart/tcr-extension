package com.github.larseckart.tcr;

import java.io.File;

public class TestCommitRevertMainExtension extends AbstractTcrExtension {

  @Override
  protected void onTestsPassed(File gitDir) {
    if (GitOperations.isGitEmpty(gitDir)) {
      System.out.println("Nothing to commit");
      return;
    }
    String message = getCommitMessage();
    if (!message.isEmpty()) {
      GitOperations.runOnConsole(gitDir, "git", "add", "-A");
      GitOperations.runOnConsole(gitDir, "git", "commit", "-m", message);
    }
  }

  @Override
  protected void onTestsFailed(File gitDir) {
    GitOperations.runOnConsole(gitDir, "git", "checkout", "src/main/");
    System.out.println("Test Failed, reverting...");
  }

  protected String getCommitMessage() {
    return ArlosGitNotation2Prompt.display();
  }
}
