package com.adaptris.core.varsub;

import static com.adaptris.core.varsub.Constants.DEFAULT_VARIABLE_POSTFIX;
import static com.adaptris.core.varsub.Constants.DEFAULT_VARIABLE_PREFIX;
import static com.adaptris.core.varsub.Constants.VARSUB_IMPL_KEY;
import static com.adaptris.core.varsub.Constants.VARSUB_POSTFIX_KEY;
import static com.adaptris.core.varsub.Constants.VARSUB_PREFIX_KEY;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.adaptris.core.CoreException;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.util.URLString;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * XStream version of {@link AdaptrisMarshaller} that supports variable substitutions when unmarshalling.
 * 
 * <p>
 * If the {@code variable-properties-url} is configured as {@code file:///.//path/to/my/variables.properties} and that contains
 * 
 * <pre>
 * <code>
 * broker.url=tcp://localhost:2506
 * broker.backup.url=tcp://my.host:2507
 * </code>
 * </pre>
 * 
 * Then all instances of<code>${broker.url}</code> and <code>${broker.backup.url}</code> will be replaced as the input is read in,
 * but before the unmarshalling occurs.
 * </p>
 * 
 * @config xstream-varsub-marshaller
 * 
 */
@XStreamAlias("xstream-varsub-marshaller")
public class XStreamMarshaller extends com.adaptris.core.XStreamMarshaller {

  private transient PropertyFileLoader loader = new PropertyFileLoader();

  private String variablePropertiesUrl;
  private String variablePrefix;
  private String variablePostfix;
  private Boolean logSubstitutions;
  private String substitutionType;

  public XStreamMarshaller() throws CoreException {
  }

  @Override
  public Object unmarshal(Reader in) throws CoreException {
    validate(in);
    Object result = null;
    try {
      String xml = IOUtils.toString(in);
      result = unmarshal(xml);
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    finally {
      IOUtils.closeQuietly(in);
    }
    return result;
  }

  @Override
  public Object unmarshal(String input) throws CoreException {
    validate(input);
    String xml = new Processor(configAsProperties()).process(input, loadSubstitutions());
    return getInstance().fromXML(xml);
  }

  @Override
  public Object unmarshal(File file) throws CoreException {
    validate(file);
    Object result = null;
    try  {
      result = unmarshal(new FileInputStream(file));
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return result;
  }

  @Override
  public Object unmarshal(URL url) throws CoreException {
    validate(url);
    Object result = null;
    try {
      result = this.unmarshal(url.openStream());
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return result;
  }

  @Override
  public Object unmarshal(URLString url) throws CoreException {
    validate(url);
    Object result = null;
    try (InputStream in = connectToUrl(url)) {
      if (in != null) {
        result = this.unmarshal(in);
      }
      else {
        throw new CoreException("could not unmarshal component from [" + url + "]");
      }
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return result;
  }

  @Override
  public Object unmarshal(InputStream in) throws CoreException {
    validate(in);
    Object result = null;
    try {
      String xml = IOUtils.toString(in);
      result = unmarshal(xml);
    }
    catch (IOException e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    finally {
      IOUtils.closeQuietly(in);
    }
    return result;
  }

  private static void validate(Object o) {
    if (o == null) {
      throw new IllegalArgumentException("Attempt to unmarshal null object");
    }
  }

  private Properties loadSubstitutions() throws CoreException {
    Properties vars = null;
    try {
      if (getVariablePropertiesUrl() == null) {
        vars = new Properties();
      }
      else {
        vars = loader.load(getVariablePropertiesUrl());
      }
    }
    catch (IOException e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return vars;
  }

  private Properties configAsProperties() {
    Properties config = new Properties();
    config.setProperty(VARSUB_PREFIX_KEY, variablePrefix());
    config.setProperty(VARSUB_POSTFIX_KEY, variableSuffix());
    config.setProperty(VARSUB_IMPL_KEY, substitutionImpl());
    return config;
  }

  public String getVariablePropertiesUrl() {
    return variablePropertiesUrl;
  }

  /**
   * Specify the file where the substitution properties are held.
   * 
   * @param s the url to the file (so it could be remote).
   */
  public void setVariablePropertiesUrl(String s) {
    this.variablePropertiesUrl = s;
  }

  public String getVariablePrefix() {
    return variablePrefix;
  }

  /**
   * Set the variable prefix.
   * 
   * @param prefix the prefix, defaults to {@value com.adaptris.core.varsub.Constants#DEFAULT_VARIABLE_PREFIX}
   */
  public void setVariablePrefix(String prefix) {
    this.variablePrefix = prefix;
  }

  String variablePrefix() {
    return defaultIfBlank(getVariablePrefix(), DEFAULT_VARIABLE_PREFIX);
  }

  public String getVariablePostfix() {
    return variablePostfix;
  }

  /**
   * Set the variable prefix.
   * 
   * @param suffix the suffix, defaults to {@value com.adaptris.core.varsub.Constants#DEFAULT_VARIABLE_POSTFIX}
   */
  public void setVariablePostfix(String suffix) {
    this.variablePostfix = suffix;
  }

  String variableSuffix() {
    return defaultIfBlank(getVariablePostfix(), DEFAULT_VARIABLE_POSTFIX);
  }

  public Boolean getLogSubstitutions() {
    return logSubstitutions;
  }

  /**
   * Whether or not to log substitutions.
   * 
   * @param b true to log each substitution as it happens, default null (false)
   */
  public void setLogSubstitutions(Boolean b) {
    this.logSubstitutions = b;
  }

  boolean logSubstitutions() {
    return getLogSubstitutions() != null ? getLogSubstitutions().booleanValue() : false;
  }

  public String getSubstitutionType() {
    return substitutionType;
  }

  /**
   * Set the Substitution Type.
   * 
   * @param type the type, if not specified then {@value com.adaptris.core.varsub.Constants#DEFAULT_VAR_SUB_IMPL}.
   */
  public void setSubstitutionType(String type) {
    this.substitutionType = type;
  }

  String substitutionImpl() {
    return defaultIfBlank(getSubstitutionType(), VariableSubstitutionImplFactory.simple.name());
  }
}
