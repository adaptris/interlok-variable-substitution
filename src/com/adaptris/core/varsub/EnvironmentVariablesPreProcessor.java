package com.adaptris.core.varsub;

import static com.adaptris.core.varsub.Constants.DEFAULT_VARIABLE_POSTFIX;
import static com.adaptris.core.varsub.Constants.DEFAULT_VARIABLE_PREFIX;
import static com.adaptris.core.varsub.Constants.DEFAULT_VAR_SUB_IMPL;
import static com.adaptris.core.varsub.Constants.ENVVAR_ADDITIONAL_LOGGING;
import static com.adaptris.core.varsub.Constants.ENVVAR_POSTFIX_KEY;
import static com.adaptris.core.varsub.Constants.ENVVAR_PREFIX_KEY;
import static com.adaptris.core.varsub.Constants.ENVVAR_IMPL_KEY;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
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
 * {@value com.adaptris.core.management.AdapterConfigManager#CONFIGURATION_PRE_PROCESSORS} to be
 * <strong>environmentVariables</strong> and making sure the required jars are available on the classpath.
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
 * <td>{@value com.adaptris.core.varsub.Constants#ENVVAR_PREFIX_KEY}</td>
 * <td><strong>{@value com.adaptris.core.varsub.Constants#DEFAULT_VARIABLE_PREFIX}</strong></td>
 * <td>No</td>
 * <td>The value here will be prepended to the system property to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 * <td>{@value com.adaptris.core.varsub.Constants#ENVVAR_POSTFIX_KEY}</td>
 * <td><strong>{@value com.adaptris.core.varsub.Constants#DEFAULT_VARIABLE_POSTFIX}</strong></td>
 * <td>No</td>
 * <td>The value here will be appended to the system property to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 * <td>{@value com.adaptris.core.varsub.Constants#ENVVAR_IMPL_KEY}</td>
 * <td><strong>{@value com.adaptris.core.varsub.Constants#DEFAULT_VAR_SUB_IMPL}</strong></td>
 * <td>No</td>
 * <td>The substitution engine that will perform the system property. At this time there is only one implementation -
 * {@value com.adaptris.core.varsub.Constants#DEFAULT_VAR_SUB_IMPL}.</td>
 * </tr>
 * <tr>
 * <td>{@value com.adaptris.core.varsub.Constants#ENVVAR_ADDITIONAL_LOGGING}</td>
 * <td><strong>false</strong></td>
 * <td>No</td>
 * <td>Controls additional logging.</td>
 * </tr>
 * </table>
 * </p>
 * For instance if you have in your bootstrap.properties
 * 
 * <pre>
 * <code>
 * preProcessors=environmentVariables
 * </code>
 * </pre>
 * 
 * Then all available environment variables (such as <code>COMPUTERNAME</code> (windows only)), provided they are marked as
 * <code>${COMPUTERNAME}</code>) will be replaced within the adapter.xml as it is read in, but before the Adapter itself is
 * unmarshalled.
 * 
 */
public class EnvironmentVariablesPreProcessor extends AbstractConfigurationPreProcessor {

  private transient Logger log = LoggerFactory.getLogger(this.getClass());

  public EnvironmentVariablesPreProcessor(BootstrapProperties bootstrapProperties) {
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
    String varSubImpl = defaultIfBlank(cfg.getProperty(ENVVAR_IMPL_KEY), DEFAULT_VAR_SUB_IMPL);
    String variablePrefix = defaultIfBlank(cfg.getProperty(ENVVAR_PREFIX_KEY), DEFAULT_VARIABLE_PREFIX);
    String variablePostfix = defaultIfBlank(cfg.getProperty(ENVVAR_POSTFIX_KEY), DEFAULT_VARIABLE_POSTFIX);
    boolean logIt = BooleanUtils.toBoolean(defaultIfBlank(cfg.getProperty(ENVVAR_ADDITIONAL_LOGGING), "false"));

    VariableSubstitutionImplFactory impl = VariableSubstitutionImplFactory.valueOf(varSubImpl);
    return impl.create().doSubstitution(xml, PropertyFileLoader.getEnvironment(), variablePrefix, variablePostfix, logIt);
  }
}
