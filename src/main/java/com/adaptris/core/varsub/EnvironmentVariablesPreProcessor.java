package com.adaptris.core.varsub;

import static com.adaptris.core.varsub.Constants.DEFAULT_VARIABLE_POSTFIX;
import static com.adaptris.core.varsub.Constants.DEFAULT_VARIABLE_PREFIX;
import static com.adaptris.core.varsub.Constants.DEFAULT_VAR_SUB_IMPL;
import static com.adaptris.core.varsub.Constants.ENVVAR_IMPL_KEY;
import static com.adaptris.core.varsub.Constants.ENVVAR_POSTFIX_KEY;
import static com.adaptris.core.varsub.Constants.ENVVAR_PREFIX_KEY;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.util.Properties;

import com.adaptris.core.CoreException;
import com.adaptris.core.management.BootstrapProperties;
import com.adaptris.util.KeyValuePairSet;

/**
 * Custom com.adaptris.core.runtime.ConfigurationPreProcessor implementation that supports substitution of system properties before
 * configuration is un-marshalled.
 * <p>
 * This ConfigurationPreProcessor can be activated by the setting or appending to the bootstrap property
 * {@value com.adaptris.core.management.AdapterConfigManager#CONFIGURATION_PRE_PROCESSORS} to be <strong>environmentVariables</strong> and
 * making sure the required jars are available on the classpath.
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
 * preProcessors=environmentVariables
 * </code>
 * </pre>
 *
 * Then all available environment variables (such as <code>COMPUTERNAME</code> (windows only)), provided they are marked as
 * <code>${COMPUTERNAME}</code>) will be replaced within the adapter.xml as it is read in, but before the Adapter itself is unmarshalled.
 *
 * @since 3.0.1
 */
public class EnvironmentVariablesPreProcessor extends VariablePreProcessorImpl {

  public EnvironmentVariablesPreProcessor(BootstrapProperties bootstrapProperties) {
    super(bootstrapProperties);
  }

  public EnvironmentVariablesPreProcessor(KeyValuePairSet kvps) {
    super(kvps);
  }

  @Override
  protected String expand(String xml) throws CoreException {
    Properties cfg = getProperties();
    String varSubImpl = defaultIfBlank(cfg.getProperty(ENVVAR_IMPL_KEY), DEFAULT_VAR_SUB_IMPL);
    String variablePrefix = defaultIfBlank(cfg.getProperty(ENVVAR_PREFIX_KEY), DEFAULT_VARIABLE_PREFIX);
    String variablePostfix = defaultIfBlank(cfg.getProperty(ENVVAR_POSTFIX_KEY), DEFAULT_VARIABLE_POSTFIX);
    VariableSubstitutionType impl = VariableSubstitutionType.valueOf(varSubImpl);
    return impl.create().doSubstitution(xml, PropertyFileLoader.getEnvironment(), variablePrefix, variablePostfix);
  }

}
