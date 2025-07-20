package com.larseckart.tcr;

import java.io.File;
import java.io.IOException;

/**
 * Interface for executing system processes.
 * This allows for dependency injection and easier testing.
 */
interface ProcessExecutor {
    
    /**
     * Execute a command and wait for completion.
     * 
     * @param workingDir the working directory for the command
     * @param cmdArgs the command and its arguments
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the process is interrupted
     */
    void executeCommand(File workingDir, String... cmdArgs) throws IOException, InterruptedException;
    
    /**
     * Execute a command and return its output.
     * 
     * @param workingDir the working directory for the command
     * @param cmdArgs the command and its arguments
     * @return the command's stdout output
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the process is interrupted
     */
    String executeCommandForOutput(File workingDir, String... cmdArgs) throws IOException, InterruptedException;
}