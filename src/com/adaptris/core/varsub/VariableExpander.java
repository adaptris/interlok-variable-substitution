package com.adaptris.core.varsub;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Creates a resolved set of substitutions for use by {@link Processor}.
 * <p>
 * <code>
 * <pre>
adapter.unique.id=edi-adapter
adapter.home.url=file://localhost/c:/users/lchan/work/runtime/v3-nightly
adapter.fs.url=${adapter.home.url}/fs/${adapter.unique.id}
adapter.config.url=${adapter.home.url}/config
adapter.x12.dir.in=x12-in
adapter.x12.dir.out=x12-out
adapter.d96a.dir.in=d96a-in
adapter.d96a.dir.out=d96a-out
 * </pre>
 * </code>
 * </p>
 * becomes
 * <p>
 * <code>
 * <pre>
adapter.unique.id=edi-adapter
adapter.fs.url=file://localhost/c:/users/lchan/work/runtime/v3-nightly/fs/edi-adapter
adapter.config.url=file://localhost/c:/users/lchan/work/runtime/v3-nightly/config
adapter.x12.dir.in=x12-in
adapter.x12.dir.out=x12-out
adapter.d96a.dir.in=d96a-in
adapter.d96a.dir.out=d96a-out
 * </pre>
 * </code>
 * <p>
 * 
 * @author lchan
 * 
 */
class VariableExpander {

  private String varPrefix;
  private String varSuffix;

  VariableExpander(String prefix, String suffix) {
    setVarPrefix(prefix);
    setVarSuffix(suffix);
  }

  Properties resolve(Properties input) {
    Properties result = new Properties();
    Properties sysProps = System.getProperties();
    Set<String> variables = createVariableNames(input);
    Set<String> sysPropVariables = createVariableNames(sysProps);
    for (String key : input.stringPropertyNames()) {
      String value = input.getProperty(key);
      // Loop through and sort out all the defined variables first.
      while (containsVariable(value, variables)) {
        value = expand(value, input);
      }
      // Now loop through and expand any system properties.
      while (containsVariable(value, sysPropVariables)) {
        value = expand(value, sysProps);
      }
      result.setProperty(key, value);
    }
    return result;
  }

  private boolean containsVariable(String value, Set<String> variables) {
    for (String var : variables) {
      if (value.contains(var)) {
        return true;
      }
    }
    return false;
  }

  private String expand(String value, Properties replacements) {
    String expanded = value;
    for (String key : replacements.stringPropertyNames()) {
      String replacementValue = replacements.getProperty(key);
      String variable = createVariable(key);
      expanded = expanded.replace(variable, replacementValue);
    }
    return expanded;
  }

  private Set<String> createVariableNames(Properties p) {
    Set<String> variables = new LinkedHashSet<String>();
    for (String key : p.stringPropertyNames()) {
      variables.add(createVariable(key));
    }
    return variables;
  }

  private String createVariable(String varName) {
    return getVarPrefix() + varName + getVarSuffix();
  }

  private String getVarPrefix() {
    return varPrefix;
  }

  private void setVarPrefix(String varPrefix) {
    this.varPrefix = varPrefix;
  }

  private String getVarSuffix() {
    return varSuffix;
  }

  private void setVarSuffix(String varSuffix) {
    this.varSuffix = varSuffix;
  }
}
