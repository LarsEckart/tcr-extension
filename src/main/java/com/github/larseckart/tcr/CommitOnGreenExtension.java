package com.github.larseckart.tcr;

import java.io.File;

public class CommitOnGreenExtension extends AbstractTcrExtension {

  @Override
  protected void onTestsPassed(File gitDir) {
    if (GitOperations.isGitEmpty(gitDir)) {
      System.out.println("Nothing to commit");
      return;
    }
    String message = ArlosGitNotation2Prompt.display();
    if (!message.isEmpty()) {
      GitOperations.stageAllChanges(gitDir);
      GitOperations.commit(gitDir, message);
    }
  }

  @Override
  protected void onTestsFailed(File gitDir) {
    // Commit on green only - do nothing on test failure
  }
}
