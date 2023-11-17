package com.adaptris.core.varsub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.adaptris.interlok.junit.scaffolding.BaseCase;
import com.adaptris.util.URLString;

public class PropertyFileLoaderTest extends BaseCase {

  public static final String SAMPLE_SUBSTITUTION_PROPERTIES = "varsub.variables.properties";
  public static final String SAMPLE_MISSING_SUBSTITUTION_PROPERTIES = "varsub.missing.variables.properties";

  @Test
  public void testLoad() throws Exception {
    String fileProperty = PROPERTIES.getProperty(SAMPLE_SUBSTITUTION_PROPERTIES);
    PropertyFileLoader propertyFileLoader = new PropertyFileLoader();
    Properties properties = propertyFileLoader.load(fileProperty);
    assertTrue(properties.containsKey("adapter.id"));
    assertEquals("MyAdapterID", properties.getProperty("adapter.id"));
  }

  @Test
  public void testLoadURL() throws Exception {
    String fileProperty = PROPERTIES.getProperty(SAMPLE_SUBSTITUTION_PROPERTIES);
    PropertyFileLoader propertyFileLoader = new PropertyFileLoader();
    Properties properties = propertyFileLoader.load(new URLString(fileProperty).getURL());
    assertTrue(properties.containsKey("adapter.id"));
    assertEquals("MyAdapterID", properties.getProperty("adapter.id"));
  }

  @Test
  public void testLoadNoFile() throws Exception {
    String fileProperty = PROPERTIES.getProperty(SAMPLE_MISSING_SUBSTITUTION_PROPERTIES);
    PropertyFileLoader propertyFileLoader = new PropertyFileLoader();
    try {
      propertyFileLoader.load(fileProperty);
      fail();
    } catch (FileNotFoundException ignored) {
      assertTrue(ignored.getMessage().contains("sample-missing-variable-substitutions.properties"));
    }
  }

  @Test
  public void testLoadNoURL() throws Exception {
    String fileProperty = PROPERTIES.getProperty(SAMPLE_MISSING_SUBSTITUTION_PROPERTIES);
    PropertyFileLoader propertyFileLoader = new PropertyFileLoader();
    try {
      propertyFileLoader.load(new URLString(fileProperty).getURL());
      fail();
    } catch (FileNotFoundException ignored) {
      assertTrue(ignored.getMessage().contains("sample-missing-variable-substitutions.properties"));
    }
  }

}