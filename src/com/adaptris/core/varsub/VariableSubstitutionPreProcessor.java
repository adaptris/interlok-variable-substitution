package com.adaptris.core.varsub;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.CoreException;
import com.adaptris.core.management.BootstrapProperties;
import com.adaptris.core.runtime.AbstractConfigurationPreProcessor;

/**
 * Custom {@link com.adaptris.core.runtime.ConfigurationPreProcessor} implementation that supports variable substitution before configuration is unmarshalled.
 * <p>
 * This ConfigurationPreProcessor can be activated by the setting or appending to the system property
 * {@value com.adaptris.core.management.AdapterConfigManager#CONFIGURATION_PRE_PROCESSORS} to be
 * {@code com.adaptris.core.varsub.VariableSubstitutionPreProcessor} and making sure the required jars are available on the classpath.
 * </p>
 * <p>
 * The following properties can be specified in the bootstrap.propertiues to control the behaviour of the variable substitution;
 * </p>
 * <p>
 * <table border="1">
 * <tr><th>Property</th><th>Default</th><th>Mandatory</th><th>Description</th></tr>
 * <tr>
 *   <td>variable-substitution.varprefix</td>
 *   <td><strong>${</strong></td>
 *   <td>No</td>
 *   <td>The value here will be prepended to the variable name to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 *   <td>variable-substitution.varpostfix</td>
 *   <td><strong>}</strong></td>
 *   <td>No</td>
 *   <td>The value here will be appended to the variable name to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 *   <td>variable-substitution.properties.url</td>
 *   <td></td>
 *   <td>Yes</td>
 *   <td>The URL to the property file containing the list of substitutions; in the form of variableName=Value. One substitution per line.</td>
 * </tr>
 * <tr>
 *   <td>variable-substitution.impl</td>
 *   <td><strong>simple</strong></td>
 *   <td>No</td>
 *   <td>The substitution engine that will perform the variable substitution.  At this time there is only one implementation - "simple".</td>
 * </tr>
 * </table>
 * </p>
 * For instance if you have in your bootstrap.properties
 * <pre>
 * <code>
 * sysprop.com.adaptris.adapter.registry.impl=com.adaptris.core.varsub.AdapterRegistry
 * variable-substitution.properties.url=file://localhost//path/to/my/variables
 * </code>
 * </pre>
 * And {@code /path/to/my/variables} contains
 * <pre>
 * <code>
 * broker.url=tcp://localhost:2506
 * broker.backup.url=tcp://my.host:2507
 * </code>
 * </pre>
 * Then all instances of {@code ${broker.url}} and {@code ${broker.backup.url}} will be replaced within the adapter.xml as it is
 * read in, but before the Adapter itself is unmarshalled.
 * </p>
 *
 * @author amcgrath
 *
 */
public class VariableSubstitutionPreProcessor extends AbstractConfigurationPreProcessor {
  
  private transient Logger log = LoggerFactory.getLogger(this.getClass());
  
  private static final String DEFAULT_VARIABLE_PREFIX = "${";
  private static final String DEFAULT_VARIABLE_POSTFIX = "}";

  private static final String DEFAULT_VAR_SUB_IMPL = "simple";

  private static final String VARIABLE_PREFIX_KEY = "variable-substitution.varprefix";
  private static final String VARIABLE_POSTFIX_KEY = "variable-substitution.varpostfix";

  private static final String VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY = "variable-substitution.properties.url";

  private static final String VARIABLE_SUBSTITUTION_IMPL_KEY = "variable-substitution.impl";

  private String variablePrefix;
  private String variablePostfix;

  private PropertyFileLoader propertyFileLoader;
  
  public VariableSubstitutionPreProcessor(BootstrapProperties bootstrapProperties) {
    super(bootstrapProperties);
    
    this.setVariablePrefix(this.getBootstrapProperties().getProperty(VARIABLE_PREFIX_KEY) != null ? this.getBootstrapProperties().getProperty(VARIABLE_PREFIX_KEY) : DEFAULT_VARIABLE_PREFIX);
    this.setVariablePostfix(this.getBootstrapProperties().getProperty(VARIABLE_POSTFIX_KEY) != null ? this.getBootstrapProperties().getProperty(VARIABLE_POSTFIX_KEY) : DEFAULT_VARIABLE_POSTFIX);

    propertyFileLoader = new PropertyFileLoader();
  }

  @Override
  public String process(String xml) throws CoreException {
    String variableSubPropertiesFile = this.getBootstrapProperties().getProperty(VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY);
    if(variableSubPropertiesFile == null) {
      log.error("Configuration variable substitution cannot be run; no properties file specifified in the bootstrap.properties (" + VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY + ")");
      throw new CoreException("Cannot perform variable substitution.");
    } else {
      Properties varSubs = null;
      try {
        varSubs = getPropertyFileLoader().load(variableSubPropertiesFile);
      } catch (IOException e) {
        throw new CoreException(e);
      }

      String varSubImpl = this.getBootstrapProperties().getProperty(VARIABLE_SUBSTITUTION_IMPL_KEY);
      VariableSubstitutionImplFactory impl = VariableSubstitutionImplFactory.valueOf(varSubImpl != null ? varSubImpl : DEFAULT_VAR_SUB_IMPL);
      impl.setVariablePostFix(this.getVariablePostfix());
      impl.setVariablePrefix(this.getVariablePrefix());

      return impl.doSubstitution(xml, varSubs);
    }
  }

  @Override
  public String process(URL urlToXml) throws CoreException {
    InputStream inputStream = null;
    try {
      inputStream = urlToXml.openConnection().getInputStream();
      String xml = IOUtils.toString(inputStream);
      return this.process(xml);
    } catch (IOException ex) {
      throw new CoreException(ex);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  public String getVariablePrefix() {
    return variablePrefix;
  }

  public void setVariablePrefix(String variablePrefix) {
    this.variablePrefix = variablePrefix;
  }

  public String getVariablePostfix() {
    return variablePostfix;
  }

  public void setVariablePostfix(String variablePostfix) {
    this.variablePostfix = variablePostfix;
  }

  public PropertyFileLoader getPropertyFileLoader() {
    return propertyFileLoader;
  }

  public void setPropertyFileLoader(PropertyFileLoader propertyFileLoader) {
    this.propertyFileLoader = propertyFileLoader;
  }

}
