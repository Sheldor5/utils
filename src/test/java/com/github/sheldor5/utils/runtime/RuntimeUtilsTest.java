package com.github.sheldor5.utils.runtime;

import com.github.sheldor5.test.api.Shape;
import com.github.sheldor5.test.api.Storage;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class RuntimeUtilsTest {

  private static final boolean VERBOSE = false;
  private static final String JARS = "src/test/resources/jars";

  @Test
  public void should_return_6_classes() throws IOException {
    Class[] superClass = new Class[] { Object.class };

    SuperClassMap map = RuntimeUtils.getClassesImplementing(superClass, JARS, VERBOSE);
    Assert.assertEquals(7, map.size());
  }

  @Test
  public void should_return_3_implementations() throws IOException {
    Class[] superClass = new Class[] { Shape.class };
    SuperClassMap map = RuntimeUtils.getClassesImplementing(superClass, JARS, VERBOSE);

    Assert.assertEquals(3, map.size());
  }

  @Test
  public void should_return_1_implementations() throws IOException {
    Class[] superClass = new Class[] { Storage.class };
    SuperClassMap map = RuntimeUtils.getClassesImplementing(superClass, JARS, VERBOSE);

    Assert.assertEquals(1, map.size());
  }
}
