package com.larseckart.tcr;

import static com.github.larseckart.tcr.GitOperations.isAmendMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.OS.MAC;

import com.github.larseckart.tcr.TestCommitRevertExtension;
import org.junit.jupiter.api.Test;
import java.io.File;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.extension.ExtendWith;

@EnabledOnOs({ MAC })
class LibraryTest {

  @Test
  void testAmendMessageDetectedCaseInsensitive() {
    // These should all be detected as amend (case-insensitive)
    assertTrue(isAmendMessage("amend"));
    assertTrue(isAmendMessage("AMEND"));
    assertTrue(isAmendMessage("Amend"));
    assertTrue(isAmendMessage("AmEnD"));
  }

  @Test
  void testNonAmendMessagesRejected() {
    // These should not be detected as amend
    assertFalse(isAmendMessage("regular commit"));
    assertFalse(isAmendMessage(" amend "));
    assertFalse(isAmendMessage("amend please"));
    assertFalse(isAmendMessage(""));
  }
}
