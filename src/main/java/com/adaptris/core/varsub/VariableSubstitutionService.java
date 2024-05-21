package com.adaptris.core.varsub;

import static com.adaptris.core.varsub.Constants.DEFAULT_VARIABLE_POSTFIX;
import static com.adaptris.core.varsub.Constants.DEFAULT_VARIABLE_PREFIX;
import static com.adaptris.core.varsub.Constants.VARSUB_IMPL_KEY;
import static com.adaptris.core.varsub.Constants.VARSUB_POSTFIX_KEY;
import static com.adaptris.core.varsub.Constants.VARSUB_PREFIX_KEY;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.io.StringReader;
import java.util.Properties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreException;
import com.adaptris.core.ServiceException;
import com.adaptris.core.util.Args;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.interlok.config.DataInputParameter;
import com.adaptris.interlok.config.DataOutputParameter;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Getter;
import lombok.Setter;

/**
 * A service that does variable substitution to a string input
 */
@XStreamAlias("variable-substitution-service")
public class VariableSubstitutionService extends com.adaptris.core.ServiceImp {

  /**
   * The input to process.
   */
  @Valid
  @NotNull
  @Getter
  @Setter
  private DataInputParameter<String> input;

  /**
   * Where to output the processed input
   */
  @Valid
  @NotNull
  @Getter
  @Setter
  private DataOutputParameter<String> output;

  /**
   * The variable properties.
   */
  @Valid
  @NotNull
  @Getter
  @Setter
  private DataInputParameter<String> variables;

  /**
   * Set the Substitution Type.
   *
   * If not specified then {@link VariableSubstitutionType#SIMPLE}.
   */
  @AdvancedConfig
  @Getter
  @Setter
  private VariableSubstitutionType substitutionType;

  /**
   * Set the variable prefix.
   *
   * Defaults to {@value com.adaptris.core.varsub.Constants#DEFAULT_VARIABLE_PREFIX}
   */
  @InputFieldDefault(Constants.DEFAULT_VARIABLE_PREFIX)
  @AdvancedConfig
  @Getter
  @Setter
  private String variablePrefix;

  /**
   * Set the variable postfix.
   *
   * Defaults to {@value com.adaptris.core.varsub.Constants#DEFAULT_VARIABLE_POSTFIX}
   */
  @InputFieldDefault(Constants.DEFAULT_VARIABLE_POSTFIX)
  @AdvancedConfig
  @Getter
  @Setter
  private String variablePostfix;

  public VariableSubstitutionService() throws CoreException {
  }

  @Override
  public void doService(AdaptrisMessage msg) throws ServiceException {
    try {
      String processed = process(getInput().extract(msg), loadSubstitutions(msg));
      getOutput().insert(processed, msg);
    } catch (Exception expt) {
      throw ExceptionHelper.wrapServiceException(expt);
    }
  }

  @Override
  public void prepare() throws CoreException {
  }

  @Override
  protected void initService() throws CoreException {
  }

  @Override
  protected void closeService() {
  }

  public String process(String inputToProcess, Properties properties) throws CoreException {
    Args.notNull(inputToProcess, "input");
    String xml = new Processor(configAsProperties()).process(inputToProcess, properties);
    return xml;
  }

  private Properties loadSubstitutions(AdaptrisMessage msg) throws CoreException {
    Properties result = new Properties();
    try {
      String variablesStr = getVariables().extract(msg);
      result.load(new StringReader(variablesStr));
    } catch (Exception expts) {
      throw ExceptionHelper.wrapCoreException(expts);
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

  String variablePrefix() {
    return defaultIfBlank(getVariablePrefix(), DEFAULT_VARIABLE_PREFIX);
  }

  String variableSuffix() {
    return defaultIfBlank(getVariablePostfix(), DEFAULT_VARIABLE_POSTFIX);
  }

  VariableSubstitutionType substitutionImpl() {
    return getSubstitutionType() != null ? getSubstitutionType() : VariableSubstitutionType.SIMPLE;
  }

}
