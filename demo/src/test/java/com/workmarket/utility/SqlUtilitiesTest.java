package com.workmarket.utility;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class SqlUtilitiesTest {
  @Test
  public void testWithPrefixFalse() {
    assertEquals("%term%", SqlUtilities.prepareLikeString("term", false));
  }

  @Test
  public void testWithPrefixTrue() {
    assertEquals("term%", SqlUtilities.prepareLikeString("term", true));
  }

  @Test
  public void testWithPrefixDefaultFalse() {
    assertEquals("%term%", SqlUtilities.prepareLikeString("term"));
  }
}