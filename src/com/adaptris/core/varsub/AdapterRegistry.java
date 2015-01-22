package com.adaptris.core.varsub;

import static com.adaptris.core.varsub.Constants.VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.CoreException;
import com.adaptris.core.management.BootstrapProperties;
import com.adaptris.util.license.LicenseException;

/**
 * Custom {@link com.adaptris.core.runtime.AdapterRegistry} implementation that supports variable substitution before configuration
 * is unmarshalled.
 * <p>
 * This AdapterRegistry can be activated by the setting the system property
 * {@value com.adaptris.core.management.AdapterConfigManager#ADAPTER_REGISTRY_IMPL} to be
 * {@code com.adaptris.core.varsub.AdapterRegistry} and making sure the required jars are available on the classpath.
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
 * sysprop.com.adaptris.adapter.registry.impl=com.adaptris.core.varsub.AdapterRegistry
 * variable-substitution.properties.url=file://localhost//path/to/my/variables
 * </code>
 * </pre>
 * 
 * And {@code /path/to/my/variables} contains
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
 * </p>
 * 
 * @author amcgrath
 * 
 */

public class AdapterRegistry extends com.adaptris.core.runtime.AdapterRegistry {

  private transient Logger log = LoggerFactory.getLogger(this.getClass());

  private BootstrapProperties config;
  private PropertyFileLoader propertyFileLoader;

  public AdapterRegistry(BootstrapProperties config) throws MalformedObjectNameException {
    super(config);
    this.setConfig(config);
    propertyFileLoader = new PropertyFileLoader();
  }

  @Override
  public ObjectName createAdapter(URL url) throws IOException, MalformedObjectNameException, CoreException, LicenseException {
    InputStream inputStream = null;
    try {
      inputStream = url.openConnection().getInputStream();
      String xml = IOUtils.toString(inputStream);
      return this.createAdapter(xml);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  @Override
  public ObjectName createAdapter(String xml) throws IOException, MalformedObjectNameException, CoreException, LicenseException {
    String variableSubPropertiesFile = this.getConfig().getProperty(VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY);
    if(variableSubPropertiesFile == null) {
      log.warn(
          "Configuration variable substitution cannot be run; no properties file specifified in the bootstrap.properties ({})",
          VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY);
      return super.createAdapter(xml);
    } else {
      Properties varSubs = getPropertyFileLoader().load(variableSubPropertiesFile);
      return super.createAdapter(new Processor(getConfig()).process(xml, varSubs));
    }
  }

  BootstrapProperties getConfig() {
    return config;
  }

  void setConfig(BootstrapProperties config) {
    this.config = config;
  }

  PropertyFileLoader getPropertyFileLoader() {
    return propertyFileLoader;
  }

  void setPropertyFileLoader(PropertyFileLoader propertyFileLoader) {
    this.propertyFileLoader = propertyFileLoader;
  }

}
