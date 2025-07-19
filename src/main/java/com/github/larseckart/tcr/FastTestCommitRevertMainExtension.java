package com.github.larseckart.tcr;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FastTestCommitRevertMainExtension extends TestCommitRevertMainExtension {

  private static String SCRIPT_PATH = null;

  @Override
  protected String getCommitMessage() {
    try {
      ensureApplescript();
      ProcessBuilder pb = new ProcessBuilder("osascript", SCRIPT_PATH);
      Process exec = pb.start();
      exec.waitFor();
      InputStream inputStream = exec.getInputStream();
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(inputStream, StandardCharsets.UTF_8));
      StringBuffer string = new StringBuffer();
      while (reader.ready()) {
        string.append(reader.readLine());
      }
      reader.close();
      String commitMessage = string.toString().trim();
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
