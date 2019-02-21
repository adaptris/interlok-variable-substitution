package com.adaptris.core.varsub;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.adaptris.core.CoreException;
import com.adaptris.core.management.properties.PropertyResolver;
import com.adaptris.core.util.ExceptionHelper;

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
public class VariableExpander {

  private String varPrefix;
  private String varSuffix;

  public VariableExpander(String prefix, String suffix) {
    setVarPrefix(prefix);
    setVarSuffix(suffix);
  }

  public Properties resolve(Properties input) throws CoreException {
    Properties resolved = resolveCustom(input);
    Properties result = new Properties();
    Properties sysProps = System.getProperties();
    Properties environment = PropertyFileLoader.getEnvironment();

    Set<String> variables = createVariableNames(resolved);
    Set<String> sysPropVariables = createVariableNames(sysProps);
    Set<String> environmentVariables = createVariableNames(environment);

    for (String key : resolved.stringPropertyNames()) {
      String value = resolved.getProperty(key);
      // Loop through and sort out all the defined variables first.
      while (containsVariable(value, variables)) {
        value = expand(value, resolved);
      }
      // Now loop through and expand any system properties.
      while (containsVariable(value, sysPropVariables)) {
        value = expand(value, sysProps);
      }

      // Now loop through and expand any environment variables.
      while (containsVariable(value, environmentVariables)) {
        value = expand(value, environment);
      }
      result.setProperty(key, value);
    }
    return result;
  }

  private Properties resolveCustom(Properties input) throws CoreException {
    Properties result = new Properties();
    try {
      PropertyResolver resolver = PropertyResolver.getDefaultInstance();
      for (String key : input.stringPropertyNames()) {
        String value = StringUtils.trimToEmpty(input.getProperty(key));
        result.setProperty(key, resolver.resolve(value));
      }
    } catch (Exception exc) {
      throw ExceptionHelper.wrapCoreException(exc);
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
