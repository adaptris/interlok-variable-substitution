package com.adaptris.core.varsub;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.adaptris.core.CoreException;

abstract class VariableSubstitutable {

  private List<String> keysToMask = Collections.emptyList();

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
  abstract String doSubstitution(String input, Properties variableSubs, String variablePrefix, String variablePostFix)
      throws CoreException;

  public VariableSubstitutable withLogMaskedKeys(List<String> keysToMask) {
    this.keysToMask = keysToMask;
    return this;
  }

  /**
   * Gets the value with any log masking based on the provided keysToMask
   * @param keysToMask
   * @param key
   * @param value
   * @return
   */
  protected String getLogMaskedValue(List<String> keysToMask, String key, String value) {
    return LogMasking.getLogMaskedValue(keysToMask, key, value);
  }

  /**
   * Gets the value with any log masking based on the value provided to withLogMaskedKeys()
   * @param key
   * @param value
   * @return
   */
  protected String getLogMaskedValue(String key, String value) {
    return LogMasking.getLogMaskedValue(keysToMask, key, value);
  }

}
