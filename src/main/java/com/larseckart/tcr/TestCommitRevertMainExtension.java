package com.larseckart.tcr;

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
      GitOperations.stageAllChanges(gitDir);
      GitOperations.commit(gitDir, message);
    }
  }

  @Override
  protected void onTestsFailed(File gitDir) {
    GitOperations.revertMainDirectoryOnly(gitDir);
    System.out.println("Test Failed, reverting...");
  }

  protected String getCommitMessage() {
    return ArlosGitNotation2Prompt.display();
  }
}
