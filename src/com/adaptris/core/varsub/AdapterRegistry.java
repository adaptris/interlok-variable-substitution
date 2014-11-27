package com.adaptris.core.varsub;

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

public class AdapterRegistry extends com.adaptris.core.runtime.AdapterRegistry {
  
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
  
  private BootstrapProperties config;
  
  private PropertyFileLoader propertyFileLoader;
  
  public AdapterRegistry(BootstrapProperties config) throws MalformedObjectNameException {
    super(config);
    
    this.setVariablePrefix(config.getProperty(VARIABLE_PREFIX_KEY) != null ? config.getProperty(VARIABLE_PREFIX_KEY) : DEFAULT_VARIABLE_PREFIX);
    this.setVariablePostfix(config.getProperty(VARIABLE_POSTFIX_KEY) != null ? config.getProperty(VARIABLE_POSTFIX_KEY) : DEFAULT_VARIABLE_POSTFIX);
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
      log.warn("Configuration variable substitution cannot be run; no properties file specifified in the bootstrap.properties (" + VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY + ")");
      return super.createAdapter(xml);
    } else {
      Properties varSubs = getPropertyFileLoader().load(variableSubPropertiesFile);
      
      VariableSubstitutionImplFactory impl = VariableSubstitutionImplFactory.valueOf
          (this.getConfig().getProperty(VARIABLE_SUBSTITUTION_IMPL_KEY) != null ? this.getConfig().getProperty(VARIABLE_SUBSTITUTION_IMPL_KEY) : DEFAULT_VAR_SUB_IMPL);
      impl.setVariablePostFix(this.getVariablePostfix());
      impl.setVariablePrefix(this.getVariablePrefix());
      
      return super.createAdapter(impl.doSubstitution(xml, varSubs));
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

  public BootstrapProperties getConfig() {
    return config;
  }

  public void setConfig(BootstrapProperties config) {
    this.config = config;
  }

  public PropertyFileLoader getPropertyFileLoader() {
    return propertyFileLoader;
  }

  public void setPropertyFileLoader(PropertyFileLoader propertyFileLoader) {
    this.propertyFileLoader = propertyFileLoader;
  }

}
