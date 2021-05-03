package com.larseckart.tcr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.larseckart.tcr.TestCommitRevertExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestCommitRevertExtension.class)
class LibraryTest {

  @Test
  void testSomeLibraryMethod() {
    assertEquals("42", "4" + "2");
  }
}
