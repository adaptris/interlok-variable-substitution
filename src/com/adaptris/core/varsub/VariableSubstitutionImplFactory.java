package com.adaptris.core.varsub;


enum VariableSubstitutionImplFactory {
  
  simple() {
    @Override
    public VariableSubstitutable create() {
      return new SimpleStringSubstitution();
    }
  };
  
  public abstract VariableSubstitutable create();
  
}
