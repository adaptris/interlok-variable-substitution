package com.adaptris.core.varsub;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants used by various pre-processors.
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
  /**
   * The default variable prefix, {@value #DEFAULT_VARIABLE_PREFIX}
   *
   */
  public static final String DEFAULT_VARIABLE_PREFIX = "${";
  /**
   * The default variable postfix, {@value #DEFAULT_VARIABLE_POSTFIX}
   *
   */
  public static final String DEFAULT_VARIABLE_POSTFIX = "}";

  /**
   * The default substitution method: {@code SIMPLE}
   */
  public static final String DEFAULT_VAR_SUB_IMPL = VariableSubstitutionType.SIMPLE.name();

  /**
   * The key in configuration controlling the prefix: {@value #VARSUB_PREFIX_KEY}, defaults to {@value #DEFAULT_VARIABLE_PREFIX}
   *
   */
  public static final String VARSUB_PREFIX_KEY = "variable-substitution.varprefix";
  /**
   * The key in configuration controlling the prefix, {@value #VARSUB_POSTFIX_KEY}, defaults to {@value #DEFAULT_VARIABLE_POSTFIX}
   *
   */
  public static final String VARSUB_POSTFIX_KEY = "variable-substitution.varpostfix";

  /**
   * The key in configuration defining the properties file where variables are defined:
   * {@value #VARSUB_PROPERTIES_URL_KEY}.
   *
   */
  public static final String VARSUB_PROPERTIES_URL_KEY = "variable-substitution.properties.url";

  /**
   * The key in configuration defining the properties file allowing formatting of the URL: {@value #VARSUB_PROPERTIES_USE_HOSTNAME}.
   * <p>
   * The default is false, but if set to true, then each URL defined by {@value #VARSUB_PROPERTIES_URL_KEY} will be formatted using
   * {@code String#format(String, Object...)} passing in the hostname as the parameter.
   * </p>
   */
  public static final String VARSUB_PROPERTIES_USE_HOSTNAME = "variable-substitution.url.useHostname";

  /**
   * The key in configuration controlling the substitution method: {@value #VARSUB_IMPL_KEY}, defaults to {@code SIMPLE}
   */
  public static final String VARSUB_IMPL_KEY = "variable-substitution.impl";

  /**
   * The key in configuration controlling the prefix: {@value #SYSPROP_PREFIX_KEY}
   *
   */
  public static final String SYSPROP_PREFIX_KEY = "system-properties.varprefix";
  /**
   * The key in configuration controlling the prefix: {@value #SYSPROP_POSTFIX_KEY}
   *
   */
  public static final String SYSPROP_POSTFIX_KEY = "system-properties.varpostfix";

  /**
   * The key in configuration controlling the substitution method: {@value #SYSPROP_IMPL_KEY}, defaults to {@code SIMPLE} *
   */
  public static final String SYSPROP_IMPL_KEY = "system-properties.impl";

  /**
   * The key in configuration controlling the prefix: {@value #ENVVAR_PREFIX_KEY}
   *
   */
  public static final String ENVVAR_PREFIX_KEY = "environment-variables.varprefix";
  /**
   * The key in configuration controlling the prefix: {@value #ENVVAR_POSTFIX_KEY}
   *
   */
  public static final String ENVVAR_POSTFIX_KEY = "environment-variables.varpostfix";

  /**
   * The key in configuration controlling the substitution method: {@value #ENVVAR_IMPL_KEY}, defaults to {@code SIMPLE}
   *
   */
  public static final String ENVVAR_IMPL_KEY = "environment-variables.impl";
}
