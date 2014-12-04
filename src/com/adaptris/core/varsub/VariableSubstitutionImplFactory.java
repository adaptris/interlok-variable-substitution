package com.adaptris.core.varsub;

import java.util.Properties;

public enum VariableSubstitutionImplFactory {
  
  simple() {
    @Override
    public String doSubstitution(String xml, Properties variableSubs) {
      return new SimpleStringSubstitution().doSubstitution(xml, variableSubs, getVariablePrefix(), getVariablePostFix(), isLogSubstitutions());
    }
  };
  
  private String variablePrefix;
  
  private String variablePostFix;
  
  private boolean logSubstitutions;

  public boolean isLogSubstitutions() {
    return logSubstitutions;
  }

  public void setLogSubstitutions(boolean logSubstitutions) {
    this.logSubstitutions = logSubstitutions;
  }

  public String getVariablePrefix() {
    return variablePrefix;
  }

  public void setVariablePrefix(String variablePrefix) {
    this.variablePrefix = variablePrefix;
  }

  public String getVariablePostFix() {
    return variablePostFix;
  }

  public void setVariablePostFix(String variablePostFix) {
    this.variablePostFix = variablePostFix;
  }
  
  public abstract String doSubstitution(String xml, Properties variableSubs);
  
}
