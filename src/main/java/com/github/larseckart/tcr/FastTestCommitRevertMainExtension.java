package com.github.larseckart.tcr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FastTestCommitRevertMainExtension extends TestCommitRevertMainExtension {

  private static String SCRIPT_PATH = null;
  private static ProcessExecutor executor = new ProcessBuilderExecutor();
  
  // Package-visible for testing
  static void setExecutor(ProcessExecutor executor) {
    FastTestCommitRevertMainExtension.executor = executor;
  }

  @Override
  protected String getCommitMessage() {
    try {
      ensureApplescript();
      // Use current working directory (doesn't matter for osascript)
      File workingDir = new File(".");
      String output = executor.executeCommandForOutput(workingDir, "osascript", SCRIPT_PATH);
      String commitMessage = output.trim();
      System.out.println("message: " + commitMessage);
      return commitMessage;
    } catch (Throwable t) {
      throw throwAsError(t);
    }
  }

  private void ensureApplescript() throws IOException {
    if (SCRIPT_PATH == null) {
      Path dialog = Files.createTempFile("Dialog", ".scpt");

      String text =
          "set theResponse to display dialog \"Commit Message?\" default answer \"\" with icon note buttons {\"Cancel\", \"Continue\"} default button \"Continue\"\n"
          +
          "--> {button returned:\"Continue\", text returned:\"Jen\"}\n" +
          "copy (text returned of theResponse) to stdout";
      Files.writeString(dialog, text);
      SCRIPT_PATH = dialog.toAbsolutePath().toString();
    }
  }

  private RuntimeException throwAsError(Throwable t) {
    if (t instanceof RuntimeException) {
      throw (RuntimeException) t;
    } else if (t instanceof Error) {
      throw (Error) t;
    } else {
      throw new Error(t);
    }
  }
}
