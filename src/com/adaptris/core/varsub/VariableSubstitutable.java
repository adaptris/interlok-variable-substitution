package com.adaptris.core.varsub;

import java.util.Properties;

public interface VariableSubstitutable {
  
  public String doSubstitution(String input, Properties variableSubs, String variablePrefix, String variablePostFix, boolean logSubstitutions);

}
