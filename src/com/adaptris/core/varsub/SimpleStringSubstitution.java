package com.adaptris.core.varsub;

import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Implementation of {@link VariableSubstitutable}.
 * </p>
 * @author amcgrath
 */

public class SimpleStringSubstitution implements VariableSubstitutable {

  private transient Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public String doSubstitution(String input, Properties variableSubs, String variablePrefix, String variablePostFix) {
    Set<Object> keySet = variableSubs.keySet();
    log.trace("About to start configuration variable substitution");
    for(Object key : keySet) {
      log.trace("Replacing " + variablePrefix + key + variablePostFix + " with " + variableSubs.getProperty((String) key));
      input = input.replace(variablePrefix + key + variablePostFix, variableSubs.getProperty((String) key));
    }

    return input;
  }

}
