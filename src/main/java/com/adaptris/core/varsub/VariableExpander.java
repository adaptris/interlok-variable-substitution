package com.adaptris.core.varsub;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.adaptris.core.CoreException;
import com.adaptris.core.management.properties.PropertyResolver;
import com.adaptris.core.util.ExceptionHelper;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class VariableExpander {

  private String varPrefix;
  private String varSuffix;

  public VariableExpander(String prefix, String suffix) {
    setVarPrefix(prefix);
    setVarSuffix(suffix);
  }

  public Properties resolve(Properties input) throws CoreException {
    Properties result = new Properties();
    try {
      Properties resolved = resolveCustom(input);
      Properties sysProps = System.getProperties();
      Properties environment = PropertyFileLoader.getEnvironment();
      for (String key : resolved.stringPropertyNames()) {
        String value = resolved.getProperty(key);
        log.trace("Initial key [{}], value[{}]", key, value);
        // Loop through and sort out all the defined variables first.
        value = handleExpansion(key, value, resolved);
        // Now loop through and expand any system properties.
        value = handleExpansion(key, value, sysProps);
        // Now loop through and expand any environment variables.
        value = handleExpansion(key, value, environment);
        result.setProperty(key, value);
      }
    } catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return result;
  }

  private String handleExpansion(String key, String initialValue, Properties knownVariables) throws Exception {
    String value = initialValue;
    Set<String> variables = createVariableNames(knownVariables);
    while (containsVariable(value, variables)) {
      String expanded = expand(value, knownVariables);
      if (value.equalsIgnoreCase(expanded)) {
        String s = String.format("Expansion Failure :[%s], self-referential variables?", key);
        log.error(s);
        throw new Exception(s);
      }
      value = expanded;
    }
    return value;
  }

  private Properties resolveCustom(Properties input) throws Exception {
    Properties result = new Properties();
    PropertyResolver resolver = PropertyResolver.getDefaultInstance();
    for (String key : input.stringPropertyNames()) {
      String value = StringUtils.trimToEmpty(input.getProperty(key));
      result.setProperty(key, resolver.resolve(value));
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
    Set<String> variables = new LinkedHashSet<>();
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
