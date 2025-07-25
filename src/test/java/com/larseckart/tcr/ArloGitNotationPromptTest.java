package com.larseckart.tcr;

import org.approvaltests.awt.AwtApprovals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.condition.OS.MAC;

@EnabledOnOs({ MAC })
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
