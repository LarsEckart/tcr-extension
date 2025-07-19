package com.github.larseckart.tcr;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import java.io.File;

public abstract class AbstractTcrExtension implements TestExecutionExceptionHandler, AfterAllCallback {

  private boolean failures = false;

  @Override
  public final void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    failures = true;
    throw throwable;
  }

  @Override
  public final void afterAll(ExtensionContext context) {
    File gitDir = GitOperations.getRootFolder();
    if (gitDir == null) {
      System.err.println("No .git repo found at " + new File(".").getAbsolutePath());
      return;
    }
    if (failures) {
      onTestsFailed(gitDir);
    } else {
      onTestsPassed(gitDir);
    }
  }

  protected abstract void onTestsPassed(File gitDir);

  protected abstract void onTestsFailed(File gitDir);
}