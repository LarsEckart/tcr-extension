package com.github.larseckart.tcr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GitOperations to verify correct command execution
 * without actually executing git commands.
 */
class GitOperationsTest {

    @TempDir
    Path tempDir;

    private MockProcessExecutor mockExecutor;
    private File testDir;

    @BeforeEach
    void setUp() {
        mockExecutor = new MockProcessExecutor();
        testDir = tempDir.toFile();
        GitOperations.setExecutor(mockExecutor);
    }

    @Test
    void stageAllChanges_shouldExecuteGitAddMinusA() {
        // When
        GitOperations.stageAllChanges(testDir);

        // Then
        ExecutedCommand lastCommand = mockExecutor.getLastCommand();
        assertEquals(testDir, lastCommand.workingDir);
        assertEquals(Arrays.asList("git", "add", "-A"), lastCommand.command);
    }

    @Test
    void commit_shouldExecuteGitCommitWithMessage() {
        // Given
        String commitMessage = "Fix important bug";

        // When
        GitOperations.commit(testDir, commitMessage);

        // Then
        ExecutedCommand lastCommand = mockExecutor.getLastCommand();
        assertEquals(testDir, lastCommand.workingDir);
        assertEquals(Arrays.asList("git", "commit", "-m", commitMessage), lastCommand.command);
    }

    @Test
    void commit_withAmendMessage_shouldExecuteGitCommitAmend() {
        // Given - mock that there are previous commits
        mockExecutor.setNextOutput("abc123 Previous commit");

        // When
        GitOperations.commit(testDir, "amend");

        // Then
        List<ExecutedCommand> commands = mockExecutor.getAllCommands();
        assertEquals(2, commands.size());
        
        // First command should check for previous commits
        ExecutedCommand firstCommand = commands.get(0);
        assertEquals(Arrays.asList("git", "log", "--oneline", "-1"), firstCommand.command);
        
        // Second command should be the amend
        ExecutedCommand secondCommand = commands.get(1);
        assertEquals(Arrays.asList("git", "commit", "--amend", "--no-edit"), secondCommand.command);
    }

    @Test
    void commit_withAmendMessageButNoHistory_shouldNotExecuteAmend() {
        // Given - mock that there are no previous commits
        mockExecutor.setNextOutput("");

        // When
        GitOperations.commit(testDir, "amend");

        // Then
        List<ExecutedCommand> commands = mockExecutor.getAllCommands();
        assertEquals(1, commands.size());
        
        // Only the history check should have been executed
        ExecutedCommand command = commands.get(0);
        assertEquals(Arrays.asList("git", "log", "--oneline", "-1"), command.command);
    }

    @Test
    void revertAllChanges_shouldExecuteCleanAndReset() {
        // When
        GitOperations.revertAllChanges(testDir);

        // Then
        List<ExecutedCommand> commands = mockExecutor.getAllCommands();
        assertEquals(2, commands.size());
        
        ExecutedCommand cleanCommand = commands.get(0);
        assertEquals(Arrays.asList("git", "clean", "-fd"), cleanCommand.command);
        assertEquals(testDir, cleanCommand.workingDir);
        
        ExecutedCommand resetCommand = commands.get(1);
        assertEquals(Arrays.asList("git", "reset", "--hard", "HEAD"), resetCommand.command);
        assertEquals(testDir, resetCommand.workingDir);
    }

    @Test
    void revertMainDirectoryOnly_shouldExecuteCheckoutSrcMain() {
        // When
        GitOperations.revertMainDirectoryOnly(testDir);

        // Then
        ExecutedCommand lastCommand = mockExecutor.getLastCommand();
        assertEquals(testDir, lastCommand.workingDir);
        assertEquals(Arrays.asList("git", "checkout", "src/main/"), lastCommand.command);
    }

    @Test
    void isGitEmpty_withNothingToCommit_shouldReturnTrue() {
        // Given
        mockExecutor.setNextOutput("On branch main\nnothing to commit, working tree clean");

        // When
        boolean result = GitOperations.isGitEmpty(testDir);

        // Then
        assertTrue(result);
        ExecutedCommand lastCommand = mockExecutor.getLastCommand();
        assertEquals(Arrays.asList("git", "status"), lastCommand.command);
    }

    @Test
    void isGitEmpty_withChangesToCommit_shouldReturnFalse() {
        // Given
        mockExecutor.setNextOutput("On branch main\nChanges to be committed:\n  modified: file.java");

        // When
        boolean result = GitOperations.isGitEmpty(testDir);

        // Then
        assertFalse(result);
    }

    @Test
    void isAmendMessage_shouldDetectAmendCaseInsensitive() {
        assertTrue(GitOperations.isAmendMessage("amend"));
        assertTrue(GitOperations.isAmendMessage("AMEND"));
        assertTrue(GitOperations.isAmendMessage("Amend"));
        assertTrue(GitOperations.isAmendMessage("AmEnD"));
    }

    @Test
    void isAmendMessage_shouldRejectNonAmendMessages() {
        assertFalse(GitOperations.isAmendMessage("regular commit"));
        assertFalse(GitOperations.isAmendMessage(" amend "));
        assertFalse(GitOperations.isAmendMessage("amend please"));
        assertFalse(GitOperations.isAmendMessage(""));
        assertFalse(GitOperations.isAmendMessage(null));
    }

    /**
     * Mock implementation for testing command execution
     */
    static class MockProcessExecutor implements ProcessExecutor {
        private final List<ExecutedCommand> executedCommands = new ArrayList<>();
        private String nextOutput = "";
        private IOException nextException = null;

        public void setNextOutput(String output) {
            this.nextOutput = output;
        }

        public void setNextException(IOException exception) {
            this.nextException = exception;
        }

        public ExecutedCommand getLastCommand() {
            if (executedCommands.isEmpty()) {
                fail("No commands were executed");
            }
            return executedCommands.get(executedCommands.size() - 1);
        }

        public List<ExecutedCommand> getAllCommands() {
            return new ArrayList<>(executedCommands);
        }

        @Override
        public void executeCommand(File workingDir, String... cmdArgs) throws IOException, InterruptedException {
            executedCommands.add(new ExecutedCommand(workingDir, Arrays.asList(cmdArgs)));
            
            if (nextException != null) {
                IOException exception = nextException;
                nextException = null;
                throw exception;
            }
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

        @Override
        public String toString() {
            return "ExecutedCommand{workingDir=" + workingDir + ", command=" + command + "}";
        }
    }
}
