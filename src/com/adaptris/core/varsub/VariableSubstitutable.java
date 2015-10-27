package com.adaptris.core.varsub;

import java.util.Properties;

import com.adaptris.core.CoreException;

public interface VariableSubstitutable {
  
  /**
   * Do the substitution.
   * 
   * @param input the input string.
   * @param variableSubs the set or variables.
   * @param variablePrefix the variable prefix
   * @param variablePostFix the variable postfix
   * @return the string with substitutions made
   * @throws CoreException if the substitutions failed (e.g. some variables were undefined when using {@link
   * Constants#VAR_SUB_STRICT}
   */
  public String doSubstitution(String input, Properties variableSubs, String variablePrefix, String variablePostFix)
      throws CoreException;

}
