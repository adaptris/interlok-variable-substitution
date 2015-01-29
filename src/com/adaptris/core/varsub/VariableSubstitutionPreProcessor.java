package com.adaptris.core.varsub;

import static com.adaptris.core.varsub.Constants.VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.CoreException;
import com.adaptris.core.management.BootstrapProperties;
import com.adaptris.core.runtime.AbstractConfigurationPreProcessor;
import com.adaptris.core.util.ExceptionHelper;

/**
 * Custom {@link com.adaptris.core.runtime.ConfigurationPreProcessor} implementation that supports variable substitution before
 * configuration is un-marshalled.
 * <p>
 * This ConfigurationPreProcessor can be activated by the setting or appending to the bootstrap property
 * {@value com.adaptris.core.management.AdapterConfigManager#CONFIGURATION_PRE_PROCESSORS} to be
 * <strong>variableSubstitution</strong> and making sure the required jars are available on the
 * classpath.
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
 * <td>variable-substitution.varprefix</td>
 * <td><strong>${</strong></td>
 * <td>No</td>
 * <td>The value here will be prepended to the variable name to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 * <td>variable-substitution.varpostfix</td>
 * <td><strong>}</strong></td>
 * <td>No</td>
 * <td>The value here will be appended to the variable name to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 * <td>variable-substitution.properties.url</td>
 * <td></td>
 * <td>Yes</td>
 * <td>The URL to the property file containing the list of substitutions; in the form of variableName=Value. One substitution per
 * line.</td>
 * </tr>
 * <tr>
 * <td>variable-substitution.impl</td>
 * <td><strong>simple</strong></td>
 * <td>No</td>
 * <td>The substitution engine that will perform the variable substitution. At this time there is only one implementation -
 * "simple".</td>
 * </tr>
 * </table>
 * </p>
 * For instance if you have in your bootstrap.properties
 * 
 * <pre>
 * <code>
 * preProcessors=variableSubstitution
 * variable-substitution.properties.url=file://localhost//path/to/my/variables
 * </code>
 * </pre>
 * 
 * And {@code .//path/to/my/variables.properties} contains
 * 
 * <pre>
 * <code>
 * broker.url=tcp://localhost:2506
 * broker.backup.url=tcp://my.host:2507
 * </code>
 * </pre>
 * 
 * Then all instances of<code>${broker.url}</code> and <code>${broker.backup.url}</code> will be replaced within the adapter.xml as
 * it is read in, but before the Adapter itself is unmarshalled.
 * 
 * @author amcgrath
 * 
 */
public class VariableSubstitutionPreProcessor extends AbstractConfigurationPreProcessor {

  private transient Logger log = LoggerFactory.getLogger(this.getClass());

  private PropertyFileLoader propertyFileLoader;

  public VariableSubstitutionPreProcessor(BootstrapProperties bootstrapProperties) {
    super(bootstrapProperties);
    propertyFileLoader = new PropertyFileLoader();
  }

  @Override
  public String process(String xml) throws CoreException {
    String result = xml;
    try {
      Properties vars = loadSubstitutions();
      result = new Processor(getBootstrapProperties()).process(xml, vars);
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return result;
  }

  @Override
  public String process(URL urlToXml) throws CoreException {
    String result = "";
    try {
      Properties vars = loadSubstitutions();
      result = new Processor(getBootstrapProperties()).process(urlToXml, vars);
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return result;
  }

  private Properties loadSubstitutions() throws IOException, CoreException {
    Properties vars = null;
    String variableSubPropertiesFile = this.getBootstrapProperties().getProperty(VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY);
    if (variableSubPropertiesFile == null) {
      log.error("Configuration variable substitution cannot be run; no properties file specified against key ({})",
          VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY);
      throw new CoreException("no properties file specified against key (" + VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY + ")");
    }
    else {
      vars = getPropertyFileLoader().load(variableSubPropertiesFile);
    }
    return vars;
  }

  public PropertyFileLoader getPropertyFileLoader() {
    return propertyFileLoader;
  }

  public void setPropertyFileLoader(PropertyFileLoader propertyFileLoader) {
    this.propertyFileLoader = propertyFileLoader;
  }

}
