package com.adaptris.core.varsub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.adaptris.core.CoreException;

public class SimpleStringSubstitutionTest {

  private String testInput = "The quick brown ${fox} jumps ${over} the lazy ${dog}";

  @Test
  public void testSubstitution() throws Exception {
    Properties props = new Properties();
    props.put("fox", "fox");
    props.put("over", "over");
    props.put("dog", "dog");

    String substitution = VariableSubstitutionType.SIMPLE.create().doSubstitution(testInput, props, "${", "}");

    assertEquals("The quick brown fox jumps over the lazy dog", substitution);
  }

  @Test
  public void testSingleSubstitution() throws Exception {
    Properties props = new Properties();
    props.put("over", "over");

    String substitution = VariableSubstitutionType.SIMPLE_WITH_LOGGING.create().doSubstitution(testInput, props, "${", "}");

    assertEquals("The quick brown ${fox} jumps over the lazy ${dog}", substitution);
  }

  @Test
  public void testSubstitutionWithoutPreAndPostFixes() throws Exception {
    Properties props = new Properties();
    props.put("fox", "fox");
    props.put("over", "over");
    props.put("dog", "dog");

    String substitution = VariableSubstitutionType.SIMPLE.create().doSubstitution(testInput, props, "", "");

    assertEquals("The quick brown ${fox} jumps ${over} the lazy ${dog}", substitution);
  }

  @Test
  public void testSubstitutionIncorrectPreAndPostFixes() throws Exception {
    Properties props = new Properties();
    props.put("fox", "fox");
    props.put("over", "over");
    props.put("dog", "dog");

    String substitution = VariableSubstitutionType.SIMPLE.create().doSubstitution(testInput, props, "&[", "]");

    assertEquals("The quick brown ${fox} jumps ${over} the lazy ${dog}", substitution);
  }

  @Test
  public void testSubstitutionWithoutProperties() throws Exception {
    Properties props = new Properties();

    String substitution = VariableSubstitutionType.SIMPLE.create().doSubstitution(testInput, props, "${", "}");

    assertEquals("The quick brown ${fox} jumps ${over} the lazy ${dog}", substitution);
  }

  @Test
  public void testSubstitutionMultipleMatches() throws Exception {
    Properties props = new Properties();
    props.put("fox", "fox");

    String multipleMatchesString = "The quick brown ${fox} jumps ${over} the lazy ${fox}";

    String substitution = VariableSubstitutionType.SIMPLE.create().doSubstitution(multipleMatchesString, props, "${", "}");

    assertEquals("The quick brown fox jumps ${over} the lazy fox", substitution);
  }

  @Test
  public void testSubstitutionNoMatches() throws Exception {
    Properties props = new Properties();
    props.put("foxxxx", "fox");
    props.put("overxxx", "over");
    props.put("dogxxx", "dog");

    String substitution = VariableSubstitutionType.SIMPLE_WITH_LOGGING.create().doSubstitution(testInput, props, "${", "}");

    assertEquals("The quick brown ${fox} jumps ${over} the lazy ${dog}", substitution);
  }

  @Test
  public void testSubstitutionStrict() throws Exception {
    Properties props = new Properties();
    props.put("fox", "fox");
    props.put("dog", "dog");
    try {
      VariableSubstitutionType.SIMPLE.create().doSubstitution(testInput, props, "${", "}");
    } catch (CoreException e) {
      assertTrue(e.getMessage().contains("${over} is undefined"));
    }
  }

  @Test
  public void testSubstitutionStrictWithLogging() throws Exception {
    Properties props = new Properties();
    props.put("fox", "fox");
    props.put("dog", "dog");
    try {
      VariableSubstitutionType.SIMPLE_WITH_LOGGING.create().doSubstitution(testInput, props, "${", "}");
    } catch (CoreException e) {
      assertTrue(e.getMessage().contains("${over} is undefined"));
    }
  }

}
