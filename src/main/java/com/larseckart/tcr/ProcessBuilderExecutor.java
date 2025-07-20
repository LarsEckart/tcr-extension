package com.larseckart.tcr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * ProcessBuilder-based implementation of ProcessExecutor.
 * This replaces the legacy Runtime.exec() approach with the more secure ProcessBuilder API.
 */
class ProcessBuilderExecutor implements ProcessExecutor {
    
    private static final boolean PRINT_ONLY = false;

    @Override
    public void executeCommand(File workingDir, String... cmdArgs) throws IOException, InterruptedException {
        if (PRINT_ONLY) {
            System.out.println(Arrays.toString(cmdArgs));
            return;
        }
        
        ProcessBuilder pb = new ProcessBuilder(cmdArgs);
        pb.directory(workingDir);
        Process process = pb.start();
        process.waitFor();
        
        // Read and print streams (maintaining existing behavior)
        System.out.println(readStream(process.getInputStream()));
        System.out.println(readStream(process.getErrorStream()));
    }

    @Override
    public String executeCommandForOutput(File workingDir, String... cmdArgs) throws IOException, InterruptedException {
        if (PRINT_ONLY) {
            System.out.println(Arrays.toString(cmdArgs));
            return "";
        }
        
        ProcessBuilder pb = new ProcessBuilder(cmdArgs);
        pb.directory(workingDir);
        Process process = pb.start();
        process.waitFor();
        
        return readStream(process.getInputStream());
    }
    
    private String readStream(InputStream inputStream) {
        try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}