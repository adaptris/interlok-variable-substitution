package com.adaptris.core.varsub;

import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.CoreException;

/**
 * <p>
 * Implementation of {@link VariableSubstitutable}.
 * </p>
 *
 * @author amcgrath
 */

public class SimpleStringSubstitution extends VariableSubstitutable {

  private transient Logger log = LoggerFactory.getLogger("VariableSubstitution");
  private transient boolean strictMode = false;
  private transient boolean verboseMode = false;

  public SimpleStringSubstitution() {
    this(false, false);
  }

  public SimpleStringSubstitution(boolean verbose, boolean strict) {
    strictMode = strict;
    verboseMode = verbose;
  }

  @Override
  public String doSubstitution(String input, Properties variableSubs, String variablePrefix, String variablePostFix) throws CoreException {
    Set<String> keySet = variableSubs.stringPropertyNames();
    String substitute = input;
    log.trace("Performing configuration variable substitution");
    for (String key : keySet) {
      String variable = variablePrefix + key + variablePostFix;
      if (verboseMode) {
        log.trace("Replacing {} with {}", variable, variableSubs.getProperty(key));
      }
      substitute = substitute.replace(variable, variableSubs.getProperty(key));
    }
    validateSubstitutions(substitute, variablePrefix, variablePostFix);
    return substitute;
  }

  private void validateSubstitutions(String input, String variablePrefix, String variablePostFix) throws CoreException {
    Pattern hasMoreVars = buildPattern(variablePrefix, variablePostFix);
    Matcher matcher = hasMoreVars.matcher(input);
    if (matcher.matches()) {
      String varName = variablePrefix + matcher.group(1) + variablePostFix;
      if (strictMode) {
        throw new CoreException(varName + " is undefined for variable substitution");
      } else {
        log.warn("{} is undefined for variable substitution", varName);
      }
    }
  }

  private static Pattern buildPattern(String variablePrefix, String variablePostFix) {
    // This should give us : ^.*\Q${\E(\S*)\Q}\E.* which we can run in DOTALL mode.
    // This assumes that no one is going to have ${ my variable name with spaces } (eek).
    String pattern = "^.*" + Pattern.quote(variablePrefix) + "(\\S*)" + Pattern.quote(variablePostFix) + ".*";
    return Pattern.compile(pattern, Pattern.DOTALL);
  }

}
