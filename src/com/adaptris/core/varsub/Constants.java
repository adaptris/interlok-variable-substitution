package com.adaptris.core.varsub;

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
   * The default substitution method, {@value #DEFAULT_VAR_SUB_IMPL}
   */
  public static final String DEFAULT_VAR_SUB_IMPL = "simple";

  /**
   * The key in configuration controlling the prefix, {@value #VARIABLE_PREFIX_KEY}
   * 
   */
  public static final String VARIABLE_PREFIX_KEY = "variable-substitution.varprefix";
  /**
   * The key in configuration controlling the prefix, {@value #VARIABLE_POSTFIX_KEY}
   * 
   */
  public static final String VARIABLE_POSTFIX_KEY = "variable-substitution.varpostfix";

  /**
   * The key in configuration controlling additional logging, {@value #VARIABLE_SUBSTITUTION_LOG_VAR_SUBS_KEY}, defaults to true
   * 
   */
  public static final String VARIABLE_SUBSTITUTION_LOG_VAR_SUBS_KEY = "variable-substitution.log.varsubs";

  /**
   * The key in configuration controlling additional logging, {@value #VARIABLE_SUBSTITUTION_LOG_VAR_SUBS_KEY}, defaults to false
   * 
   */
  public static final String VARIABLE_SUBSTITUTION_PROPERTIES_URL_KEY = "variable-substitution.properties.url";

  /**
   * The key in configuration controlling the substitution method, {@value #VARIABLE_SUBSTITUTION_IMPL_KEY}, defaults to
   * {@value #DEFAULT_VAR_SUB_IMPL}
   * 
   */
  public static final String VARIABLE_SUBSTITUTION_IMPL_KEY = "variable-substitution.impl";

  /**
   * The key in configuration controlling the prefix, {@value #SYSPROP_PREFIX_KEY}
   * 
   */
  public static final String SYSPROP_PREFIX_KEY = "system-properties.varprefix";
  /**
   * The key in configuration controlling the prefix, {@value #SYSPROP_POSTFIX_KEY}
   * 
   */
  public static final String SYSPROP_POSTFIX_KEY = "system-properties.varpostfix";

  /**
   * The key in configuration controlling additional logging, {@value #SYSPROP_LOG_VAR_SUBS_KEY}, defaults to false
   * 
   */
  public static final String SYSPROP_LOG_VAR_SUBS_KEY = "system-properties.log.substitutions";

  /**
   * The key in configuration controlling the substitution method, {@value #SYSPROP_SUBSTITUTION_IMPL_KEY}, defaults to
   * {@value #DEFAULT_VAR_SUB_IMPL}
   * 
   */
  public static final String SYSPROP_SUBSTITUTION_IMPL_KEY = "system-properties.impl";

  /**
   * The key in configuration controlling the prefix, {@value #ENVVAR_POSTFIX_KEY}
   * 
   */
  public static final String ENVVAR_PREFIX_KEY = "environment-variables.varprefix";
  /**
   * The key in configuration controlling the prefix, {@value #ENVVAR_POSTFIX_KEY}
   * 
   */
  public static final String ENVVAR_POSTFIX_KEY = "environment-variables.varpostfix";

  /**
   * The key in configuration controlling additional logging, {@value #ENVVAR_LOG_SUBS_KEY}, defaults to false
   * 
   */
  public static final String ENVVAR_LOG_SUBS_KEY = "environment-variables.log.substitutions";

  /**
   * The key in configuration controlling the substitution method, {@value #ENVVAR_SUBSTITUTION_IMPL_KEY}, defaults to
   * {@value #DEFAULT_VAR_SUB_IMPL}
   * 
   */
  public static final String ENVVAR_SUBSTITUTION_IMPL_KEY = "environment-variables.impl";

}
