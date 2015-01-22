package com.adaptris.core.varsub;

import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Implementation of {@link VariableSubstitutable}.
 * </p>
 * 
 * @author amcgrath
 */

class SimpleStringSubstitution implements VariableSubstitutable {

  private transient Logger log = LoggerFactory.getLogger("VariableSubstitution");

  @Override
  public String doSubstitution(String input, Properties variableSubs, String variablePrefix, String variablePostFix,
                               boolean logSubstitutions) {
    Set<String> keySet = variableSubs.stringPropertyNames();
    log.trace("Performing configuration variable substitution");
    for (String key : keySet) {
      String variable = variablePrefix + key + variablePostFix;
      if (logSubstitutions) {
        log.trace("Replacing {} with {}", variable, variableSubs.getProperty(key));
      }
      input = input.replace(variable, variableSubs.getProperty(key));
    }
    return input;
  }

}
