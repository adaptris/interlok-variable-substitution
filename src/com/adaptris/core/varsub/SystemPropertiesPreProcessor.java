package com.adaptris.core.varsub;

import static com.adaptris.core.varsub.Constants.DEFAULT_VARIABLE_POSTFIX;
import static com.adaptris.core.varsub.Constants.DEFAULT_VARIABLE_PREFIX;
import static com.adaptris.core.varsub.Constants.DEFAULT_VAR_SUB_IMPL;
import static com.adaptris.core.varsub.Constants.SYSPROP_IMPL_KEY;
import static com.adaptris.core.varsub.Constants.SYSPROP_POSTFIX_KEY;
import static com.adaptris.core.varsub.Constants.SYSPROP_PREFIX_KEY;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;

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
import com.adaptris.core.util.ExceptionHelper;

/**
 * Custom {@link com.adaptris.core.runtime.ConfigurationPreProcessor} implementation that supports substitution of system properties
 * before configuration is un-marshalled.
 * <p>
 * This ConfigurationPreProcessor can be activated by the setting or appending to the bootstrap property
 * {@value com.adaptris.core.management.AdapterConfigManager#CONFIGURATION_PRE_PROCESSORS} to be <strong>systemProperties</strong>
 * and making sure the required jars are available on the classpath.
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
 * <td>{@value com.adaptris.core.varsub.Constants#SYSPROP_PREFIX_KEY}</td>
 * <td><strong>{@value com.adaptris.core.varsub.Constants#DEFAULT_VARIABLE_PREFIX} </strong></td>
 * <td>No</td>
 * <td>The value here will be prepended to the system property to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 * <td>{@value com.adaptris.core.varsub.Constants#SYSPROP_POSTFIX_KEY}</td>
 * <td><strong>{@value com.adaptris.core.varsub.Constants#DEFAULT_VARIABLE_POSTFIX} </strong></td>
 * <td>No</td>
 * <td>The value here will be appended to the system property to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 * <td>{@value com.adaptris.core.varsub.Constants#SYSPROP_IMPL_KEY}</td>
 * <td><strong>{@value com.adaptris.core.varsub.Constants#DEFAULT_VAR_SUB_IMPL}</strong></td>
 * <td>No</td>
 * <td>The substitution engine that will perform the system property. defaults to
 * {@value com.adaptris.core.varsub.Constants#DEFAULT_VAR_SUB_IMPL}.</td>
 * </tr>
 * </table>
 * </p>
 * For instance if you have in your bootstrap.properties
 * 
 * <pre>
 * <code>
 * preProcessors=systemProperties
 * </code>
 * </pre>
 * 
 * Then all available system properties (such as {@code user.dir}, provided they are marked as <code>${user.dir}</code>) will be
 * replaced within the adapter.xml as it is read in, but before the Adapter itself is unmarshalled.
 * 
 * @since 3.0.1
 */
public class SystemPropertiesPreProcessor extends AbstractConfigurationPreProcessor {

  private transient Logger log = LoggerFactory.getLogger(this.getClass());

  public SystemPropertiesPreProcessor(BootstrapProperties bootstrapProperties) {
    super(bootstrapProperties);
  }

  @Override
  public String process(String xml) throws CoreException {
    String result = xml;
    try {
      result = expand(xml);
    }
    catch (Exception e) {
      ExceptionHelper.rethrowCoreException(e);
    }
    return result;
  }

  @Override
  public String process(URL urlToXml) throws CoreException {
    String result = null;
    try (InputStream inputStream = urlToXml.openConnection().getInputStream()) {
      String xml = IOUtils.toString(inputStream);
      result = expand(xml);
    }
    catch (IOException ex) {
      ExceptionHelper.rethrowCoreException(ex);
    }
    return result;
  }

  String expand(String xml) throws CoreException {
    Properties cfg = getBootstrapProperties();
    String varSubImpl = defaultIfBlank(cfg.getProperty(SYSPROP_IMPL_KEY), DEFAULT_VAR_SUB_IMPL);
    String variablePrefix = defaultIfBlank(cfg.getProperty(SYSPROP_PREFIX_KEY), DEFAULT_VARIABLE_PREFIX);
    String variablePostfix = defaultIfBlank(cfg.getProperty(SYSPROP_POSTFIX_KEY), DEFAULT_VARIABLE_POSTFIX);
    VariableSubstitutionType impl = VariableSubstitutionType.valueOf(varSubImpl);
    return impl.create().doSubstitution(xml, System.getProperties(), variablePrefix, variablePostfix);
  }
}
