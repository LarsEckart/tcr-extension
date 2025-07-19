package com.github.larseckart.tcr;

import java.io.File;

public class SilentCommitOnGreenExtension extends AbstractTcrExtension {

  @Override
  protected void onTestsPassed(File gitDir) {
    if (GitOperations.isGitEmpty(gitDir)) {
      System.out.println("Nothing to commit");
      return;
    }
    GitOperations.stageAllChanges(gitDir);
    GitOperations.commit(gitDir, "work in progress");
  }

  @Override
  protected void onTestsFailed(File gitDir) {
    // Commit on green only - do nothing on test failure
  }
}
