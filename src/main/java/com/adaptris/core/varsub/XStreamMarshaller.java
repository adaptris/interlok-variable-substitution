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
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.validation.constraints.NotNull;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.AdaptrisMarshaller;
import com.adaptris.core.CoreException;
import com.adaptris.core.util.Args;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.util.URLHelper;
import com.adaptris.util.URLString;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

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

  private transient Logger log = LoggerFactory.getLogger(this.getClass());
  private transient PropertyFileLoader loader = new PropertyFileLoader();

  @XStreamImplicit(itemFieldName = "variable-properties-url")
  @NotNull
  @AutoPopulated
  private List<String> variablePropertiesUrls;

  @AdvancedConfig
  private VariableSubstitutionType substitutionType;
  @AdvancedConfig
  private String variablePrefix;
  @AdvancedConfig
  private String variablePostfix;
  @AdvancedConfig
  @InputFieldDefault(value = "false")
  private Boolean useHostname;

  public XStreamMarshaller() throws CoreException {
    setVariablePropertiesUrls(new ArrayList<String>());
  }

  @Override
  public Object unmarshal(Reader in) throws CoreException {
    Args.notNull(in, "reader");
    Object result = null;
    try (Reader autoClose = in) {
      String xml = IOUtils.toString(autoClose);
      result = unmarshal(xml);
    }
    catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return result;
  }

  @Override
  public Object unmarshal(String input) throws CoreException {
    Args.notNull(input, "input");
    String xml = new Processor(configAsProperties()).process(input, loadSubstitutions());
    return getInstance().fromXML(xml);
  }

  @Override
  public Object unmarshal(File file) throws CoreException {
    Args.notNull(file, "file");
    Object result = null;
    try (InputStream autoClose = new FileInputStream(file)) {
      result = unmarshal(autoClose);
    }
    catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return result;
  }

  @Override
  public Object unmarshal(URL url) throws CoreException {
    Args.notNull(url, "url");
    Object result = null;
    try (InputStream autoClose = url.openStream()) {
      result = this.unmarshal(autoClose);
    }
    catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return result;
  }

  @Override
  public Object unmarshal(URLString url) throws CoreException {
    Args.notNull(url, "url");
    Object result = null;
    try (InputStream in = URLHelper.connect(url)) {
      if (in != null) {
        result = this.unmarshal(in);
      }
      else {
        throw new CoreException("could not unmarshal component from [" + url + "]");
      }
    }
    catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return result;
  }

  @Override
  public Object unmarshal(InputStream in) throws CoreException {
    Args.notNull(in, "inputstream");
    Object result = null;
    try (InputStream autoClose = in) {
      String xml = IOUtils.toString(autoClose);
      result = unmarshal(xml);
    }
    catch (IOException e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return result;
  }


  private Properties loadSubstitutions() throws CoreException {
    Properties result = new Properties();
    try {
      String hostname = InetAddress.getLocalHost().getHostName();
      for (String url : getVariablePropertiesUrls()) {
        result.putAll(useHostname() ? loader.formatAndLoad(url, true, hostname) : loader.load(url));
      }
    } catch (IOException e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return result;
  }


  private Properties configAsProperties() {
    Properties config = new Properties();
    config.setProperty(VARSUB_PREFIX_KEY, variablePrefix());
    config.setProperty(VARSUB_POSTFIX_KEY, variableSuffix());
    config.setProperty(VARSUB_IMPL_KEY, substitutionImpl().name());
    return config;
  }

  public List<String> getVariablePropertiesUrls() {
    return variablePropertiesUrls;
  }

  /**
   * Specify the file where the substitution properties are held.
   * 
   * @param list the urls (so it could be remote).
   */
  public void setVariablePropertiesUrls(List<String> list) {
    this.variablePropertiesUrls = Args.notNull(list, "variablePropertiesUrls");
  }

  public void addVariablePropertiesUrls(String url) {
    variablePropertiesUrls.add(Args.notNull(url, "Variable Property URL"));
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

  public VariableSubstitutionType getSubstitutionType() {
    return substitutionType;
  }

  /**
   * Set the Substitution Type.
   * 
   * @param type the type, if not specified then {@link VariableSubstitutionType#SIMPLE}.
   */
  public void setSubstitutionType(VariableSubstitutionType type) {
    this.substitutionType = type;
  }

  VariableSubstitutionType substitutionImpl() {
    return getSubstitutionType() != null ? getSubstitutionType() : VariableSubstitutionType.SIMPLE;
  }

  public Boolean getUseHostname() {
    return useHostname;
  }

  /**
   * Whether or not to attempt to format the URL with the hostname.
   * 
   * @param b
   * @see Constants#VARSUB_PROPERTIES_USE_HOSTNAME
   */
  public void setUseHostname(Boolean b) {
    this.useHostname = b;
  }

  public XStreamMarshaller withUseHostname(Boolean b) {
    setUseHostname(b);
    return this;
  }

  private boolean useHostname() {
    return BooleanUtils.toBooleanDefaultIfNull(getUseHostname(), false);
  }
}
