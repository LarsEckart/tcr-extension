package com.larseckart.tcr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.condition.OS.MAC;

import com.github.larseckart.tcr.TestCommitRevertExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestCommitRevertExtension.class)
@EnabledOnOs({ MAC })
class LibraryTest {

  @Test
  void testSomeLibraryMethod() {
    assertEquals("42", "4" + "2");
  }
}
