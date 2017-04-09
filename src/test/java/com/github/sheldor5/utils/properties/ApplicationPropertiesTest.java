package com.github.sheldor5.utils.properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ApplicationPropertiesTest {

  private static final String PROPERTIES_PATH = "src/test/resources";

  private static final String DEFAULT_PROPERTIES = PROPERTIES_PATH + "/default.properties";
  private static final String CUSTOM_PROPERTIES = PROPERTIES_PATH + "/custom.properties";


  @Before
  public void should_load_default_properties() {
    ApplicationProperties.load(DEFAULT_PROPERTIES);
    Assert.assertEquals("true", ApplicationProperties.get("default.property"));
    Assert.assertEquals("1", ApplicationProperties.get("default.magic"));
  }

  @Test
  public void should_load_custom_properties() {
    ApplicationProperties.load(CUSTOM_PROPERTIES);
    Assert.assertEquals("42", ApplicationProperties.get("default.magic"));
  }

  @After
  public void should_set_and_get_property() {
    ApplicationProperties.set("test.magic", 42);
    Assert.assertEquals(42, ApplicationProperties.get("test.magic"));
  }
}
