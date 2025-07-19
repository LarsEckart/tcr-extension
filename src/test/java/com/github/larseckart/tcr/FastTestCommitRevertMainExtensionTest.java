package com.github.larseckart.tcr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FastTestCommitRevertMainExtension to verify AppleScript command execution
 * without actually executing osascript commands.
 */
class FastTestCommitRevertMainExtensionTest {

    private MockProcessExecutor mockExecutor;
    private FastTestCommitRevertMainExtension extension;

    @BeforeEach
    void setUp() {
        mockExecutor = new MockProcessExecutor();
        extension = new FastTestCommitRevertMainExtension();
        FastTestCommitRevertMainExtension.setExecutor(mockExecutor);
    }

    @Test
    void getCommitMessage_shouldExecuteOsascriptCommand() {
        // Given
        String expectedMessage = "Fix user authentication bug";
        mockExecutor.setNextOutput(expectedMessage);

        // When
        String actualMessage = extension.getCommitMessage();

        // Then
        assertEquals(expectedMessage, actualMessage);
        
        List<ExecutedCommand> commands = mockExecutor.getAllCommands();
        assertEquals(1, commands.size());
        
        ExecutedCommand command = commands.get(0);
        assertEquals(2, command.command.size());
        assertEquals("osascript", command.command.get(0));
        assertTrue(command.command.get(1).endsWith(".scpt"), 
            "Script path should end with .scpt, but was: " + command.command.get(1));
    }

    @Test
    void getCommitMessage_shouldTrimWhitespace() {
        // Given
        String messageWithWhitespace = "  commit message with spaces  \n";
        String expectedTrimmed = "commit message with spaces";
        mockExecutor.setNextOutput(messageWithWhitespace);

        // When
        String actualMessage = extension.getCommitMessage();

        // Then
        assertEquals(expectedTrimmed, actualMessage);
    }

    /**
     * Mock implementation for testing command execution
     */
    static class MockProcessExecutor implements ProcessExecutor {
        private final List<ExecutedCommand> executedCommands = new ArrayList<>();
        private String nextOutput = "";

        public void setNextOutput(String output) {
            this.nextOutput = output;
        }

        public List<ExecutedCommand> getAllCommands() {
            return new ArrayList<>(executedCommands);
        }

        @Override
        public void executeCommand(File workingDir, String... cmdArgs) throws IOException, InterruptedException {
            executedCommands.add(new ExecutedCommand(workingDir, Arrays.asList(cmdArgs)));
        }

        @Override
        public String executeCommandForOutput(File workingDir, String... cmdArgs) throws IOException, InterruptedException {
            executeCommand(workingDir, cmdArgs);
            String output = nextOutput;
            nextOutput = "";
            return output;
        }
    }

    /**
     * Record of an executed command for verification
     */
    static class ExecutedCommand {
        final File workingDir;
        final List<String> command;

        ExecutedCommand(File workingDir, List<String> command) {
            this.workingDir = workingDir;
            this.command = command;
        }
    }
}
