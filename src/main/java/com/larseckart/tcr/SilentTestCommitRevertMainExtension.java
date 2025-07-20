package com.larseckart.tcr;

import java.io.File;

public class SilentTestCommitRevertMainExtension extends AbstractTcrExtension {

  @Override
  protected void onTestsPassed(File gitDir) {
    if (GitOperations.isGitEmpty(gitDir)) {
      System.out.println("Nothing to commit");
      return;
    }
    GitOperations.stageAllChanges(gitDir);
    GitOperations.commit(gitDir, "working");
  }

  @Override
  protected void onTestsFailed(File gitDir) {
    GitOperations.revertMainDirectoryOnly(gitDir);
    System.out.println("Test Failed, reverting...");
  }
}
