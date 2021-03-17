package com.adaptris.core.varsub;

import java.util.Properties;

import com.adaptris.core.CoreException;

import junit.framework.TestCase;

public class SimpleStringSubstitutionTest extends TestCase {
  
  private String testInput = "The quick brown ${fox} jumps ${over} the lazy ${dog}";
  
  public void setUp() throws Exception {
    
  }
  
  public void tearDown() throws Exception {
    
  }
  
  public void testSubstitution() throws Exception {
    Properties props = new Properties();
    props.put("fox", "fox");
    props.put("over", "over");
    props.put("dog", "dog");
    
    String substitution = VariableSubstitutionType.SIMPLE.create().doSubstitution(testInput, props, "${", "}");
    
    assertEquals("The quick brown fox jumps over the lazy dog", substitution);
  }
  
  public void testSingleSubstitution() throws Exception {
    Properties props = new Properties();
    props.put("over", "over");
    
    String substitution = VariableSubstitutionType.SIMPLE_WITH_LOGGING.create().doSubstitution(testInput, props, "${", "}");
    
    assertEquals("The quick brown ${fox} jumps over the lazy ${dog}", substitution);
  }
  
  public void testSubstitutionWithoutPreAndPostFixes() throws Exception {
    Properties props = new Properties();
    props.put("fox", "fox");
    props.put("over", "over");
    props.put("dog", "dog");
    
    String substitution = VariableSubstitutionType.SIMPLE.create().doSubstitution(testInput, props, "", "");
    
    assertEquals("The quick brown ${fox} jumps ${over} the lazy ${dog}", substitution);
  }
  
  public void testSubstitutionIncorrectPreAndPostFixes() throws Exception {
    Properties props = new Properties();
    props.put("fox", "fox");
    props.put("over", "over");
    props.put("dog", "dog");
    
    String substitution = VariableSubstitutionType.SIMPLE.create().doSubstitution(testInput, props, "&[", "]");
    
    assertEquals("The quick brown ${fox} jumps ${over} the lazy ${dog}", substitution);
  }
  
  public void testSubstitutionWithoutProperties() throws Exception {
    Properties props = new Properties();
    
    String substitution = VariableSubstitutionType.SIMPLE.create().doSubstitution(testInput, props, "${", "}");
    
    assertEquals("The quick brown ${fox} jumps ${over} the lazy ${dog}", substitution);
  }
  
  public void testSubstitutionMultipleMatches() throws Exception {
    Properties props = new Properties();
    props.put("fox", "fox");
    
    String multipleMatchesString = "The quick brown ${fox} jumps ${over} the lazy ${fox}";
    
    String substitution =
 VariableSubstitutionType.SIMPLE.create().doSubstitution(multipleMatchesString, props, "${", "}");
    
    assertEquals("The quick brown fox jumps ${over} the lazy fox", substitution);
  }
  
  public void testSubstitutionNoMatches() throws Exception {
    Properties props = new Properties();
    props.put("foxxxx", "fox");
    props.put("overxxx", "over");
    props.put("dogxxx", "dog");
    
    String substitution = VariableSubstitutionType.SIMPLE_WITH_LOGGING.create().doSubstitution(testInput, props, "${", "}");
    
    assertEquals("The quick brown ${fox} jumps ${over} the lazy ${dog}", substitution);
  }
  

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
