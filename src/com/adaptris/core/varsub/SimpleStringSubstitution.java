package com.adaptris.core.varsub;

import java.util.Properties;
import java.util.Set;

public class SimpleStringSubstitution implements VariableSubstitutable {

  @Override
  public String doSubstitution(String input, Properties variableSubs, String variablePrefix, String variablePostFix) {
    Set<Object> keySet = variableSubs.keySet();
    for(Object key : keySet) {
      input = input.replaceAll(variablePrefix + key + variablePostFix, variableSubs.getProperty((String) key));
    }
    
    return input;
  }

}
