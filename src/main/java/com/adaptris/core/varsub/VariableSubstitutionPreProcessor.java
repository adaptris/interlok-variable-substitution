package com.adaptris.core.varsub;

import static com.adaptris.core.varsub.Constants.VARSUB_PROPERTIES_URL_KEY;
import static com.adaptris.core.varsub.Constants.VARSUB_PROPERTIES_USE_HOSTNAME;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.CoreException;
import com.adaptris.core.config.ConfigPreProcessorImpl;
import com.adaptris.core.management.BootstrapProperties;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.core.util.PropertyHelper;
import com.adaptris.util.KeyValuePairSet;

/**
 * Custom {@link com.adaptris.core.runtime.ConfigurationPreProcessor} implementation that supports variable substitution before
 * configuration is un-marshalled.
 * <p>
 * This ConfigurationPreProcessor can be activated by the setting or appending to the bootstrap property
 * {@value com.adaptris.core.management.AdapterConfigManager#CONFIGURATION_PRE_PROCESSORS} to be
 * <strong>variableSubstitution</strong> and making sure the required jars are available on the classpath.
 * </p>
 * <p>
 * The following properties can be specified in the bootstrap.properties to control the behaviour of the variable substitution;
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Property</th>
 * <th>Default</th>
 * <th>Mandatory</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>{@value com.adaptris.core.varsub.Constants#VARSUB_PREFIX_KEY}</td>
 * <td><strong>{@value com.adaptris.core.varsub.Constants#DEFAULT_VARIABLE_PREFIX} </strong></td>
 * <td>No</td>
 * <td>The value here will be prepended to the variable name to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 * <td>{@value com.adaptris.core.varsub.Constants#VARSUB_POSTFIX_KEY}</td>
 * <td><strong>{@value com.adaptris.core.varsub.Constants#DEFAULT_VARIABLE_POSTFIX}</strong></td>
 * <td>No</td>
 * <td>The value here will be appended to the variable name to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 * <td>{@value com.adaptris.core.varsub.Constants#VARSUB_PROPERTIES_URL_KEY}</td>
 * <td></td>
 * <td>Yes</td>
 * <td>The URL to the property file containing the list of substitutions; in the form of variableName=Value. One substitution per
 * line. Multiple property files are supported by specifying a unique suffix for each property file.</td>
 * </tr>
 * <tr>
 * <td>{@value com.adaptris.core.varsub.Constants#VARSUB_PROPERTIES_USE_HOSTNAME}</td>
 * <td><strong>false</strong></td>
 * <td>No</td>
 * <td>If true, Each URL defined by {@value com.adaptris.core.varsub.Constants#VARSUB_PROPERTIES_URL_KEY} will be formatted using
 * {@code String#format(String, Object...)} passing in the hostname as the parameter.</td>
 * </tr>
 * <tr>
 * <td>{@value com.adaptris.core.varsub.Constants#VARSUB_IMPL_KEY}</td>
 * <td><strong>SIMPLE</strong></td>
 * <td>No</td>
 * <td>The substitution engine that will perform the variable substitution. defaults to {@code SIMPLE}
 * ({@link com.adaptris.core.varsub.Constants#DEFAULT_VAR_SUB_IMPL}).</td>
 * </tr>
 * </table>
 * </p>
 * For instance if you have in your bootstrap.properties
 * 
 * <pre>
 * <code>
 * preProcessors=variableSubstitution
 * variable-substitution.properties.url.1=file://localhost//path/to/my/variables
 * variable-substitution.properties.url.2=file://localhost//path/to/my/variables.2
 * variable-substitution.properties.url.3=file://localhost//path/to/my/variables-%1$s
 * variable-substitution.url.useHostname=true
 * </code> </pre>
 * 
 * And {@code .//path/to/my/variables.properties} / {@code /path/to/my/variables.2} /
 * {@code /path/to/my/variables-localhost.localdomain} contains
 * 
 * <pre>
 * <code>
 * broker.url=tcp://localhost:2506
 * broker.backup.url=tcp://my.host:2507
 * </code> </pre>
 * 
 * Then all instances of <code>${broker.url}</code> and <code>${broker.backup.url}</code> will be replaced within the adapter.xml as
 * it is read in, but before the Adapter itself is unmarshalled. A warning is logged if a file cannot be accessed.
 * 
 * @author amcgrath
 * 
 */
public class VariableSubstitutionPreProcessor extends ConfigPreProcessorImpl {

  private transient Logger log = LoggerFactory.getLogger(this.getClass());

  private PropertyFileLoader propertyFileLoader;

  public VariableSubstitutionPreProcessor(BootstrapProperties bootstrapProperties) {
    super(bootstrapProperties);
    propertyFileLoader = new PropertyFileLoader();
  }

  public VariableSubstitutionPreProcessor(KeyValuePairSet kvps) {
    super(kvps);
    propertyFileLoader = new PropertyFileLoader();
  }

  @Override
  public String process(String xml) throws CoreException {
    String result = xml;
    try {
      Properties vars = loadSubstitutions();
      result = new Processor(getProperties()).process(xml, vars);
    }
    catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return result;
  }

  @Override
  public String process(URL urlToXml) throws CoreException {
    String result = "";
    try {
      Properties vars = loadSubstitutions();
      result = new Processor(getProperties()).process(urlToXml, vars);
    }
    catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return result;
  }

  private Properties loadSubstitutions() throws IOException, CoreException {
    String hostname = InetAddress.getLocalHost().getHostName();
    Properties result = new Properties();
    // Get all the properties starting with variable-substitution.properties.url
    // sort; and then iterate through them adding them to the list of substitutions.
    SortedSet<String> keys =
        new TreeSet<>(PropertyHelper.getPropertySubset(getProperties(), VARSUB_PROPERTIES_URL_KEY, true).stringPropertyNames());
    if (keys.size() == 0) {
      log.warn("Configuration variable substitution will have no effect; no properties file specified against key ({})",
          VARSUB_PROPERTIES_URL_KEY);
    }
    for (String key : keys) {
      String val = getProperties().getProperty(key);
      result.putAll(load(val, hostname));
    }
    return result;
  }

  PropertyFileLoader getPropertyFileLoader() {
    return propertyFileLoader;
  }

  void setPropertyFileLoader(PropertyFileLoader propertyFileLoader) {
    this.propertyFileLoader = propertyFileLoader;
  }

  private Properties load(String val, String hostname) throws IOException {
    Properties result = new Properties();
    if (BooleanUtils.toBoolean(getProperties().getProperty(VARSUB_PROPERTIES_USE_HOSTNAME, "false"))) {
      result = getPropertyFileLoader().formatAndLoad(val, false, hostname);
    }
    else {
      result = getPropertyFileLoader().load(val, false);
    }
    return result;
  }

}
