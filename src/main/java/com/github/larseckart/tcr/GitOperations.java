package com.github.larseckart.tcr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GitOperations {

  private static final boolean PRINT_ONLY = false;

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
      runOnConsole(gitDir, "git", "status");
      Process p = Runtime.getRuntime().exec(new String[] {"git", "status"}, null, gitDir);
      p.waitFor();

      String text = readStream(p.getInputStream());
      return text.contains("nothing to commit");
    } catch (Exception e) {
      return false;
    }
  }

  public static String readStream(InputStream inputStream) {
    try (var reader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      return reader.lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void runOnConsole(File workingDir, String... cmdArgs) throws Error {
    if (PRINT_ONLY) {
      System.out.println(Arrays.toString(cmdArgs));
      return;
    }
    try {
      Process p = Runtime.getRuntime().exec(cmdArgs, null, workingDir);
      p.waitFor();
      System.out.println(readStream(p.getInputStream()));
      System.out.println(readStream(p.getErrorStream()));
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}