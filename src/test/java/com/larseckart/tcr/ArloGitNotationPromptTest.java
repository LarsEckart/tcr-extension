package com.larseckart.tcr;

import org.approvaltests.awt.AwtApprovals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnOs;

import static org.junit.jupiter.api.condition.OS.MAC;

// These UI approval tests require a real macOS display/WindowServer and do not run in headless CI.
@EnabledOnOs({ MAC })
@DisabledIfEnvironmentVariable(named = "CI", matches = ".*")
public class ArloGitNotationPromptTest {
    @Test
    void testVersion1() {
        AwtApprovals.verify(new ArlosGitNotationPrompt().getPanel());
    }

    @Test
    void testVersion2() {
        AwtApprovals.verify(new ArlosGitNotation2Prompt().getPanel());
    }
}
