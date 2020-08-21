package com.github.larseckart.tcr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TestCommitRevertExtension.class)
class LibraryTest {
  @Test
  void testSomeLibraryMethod() {
    assertEquals("", "");
  }
}
