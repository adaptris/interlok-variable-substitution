package com.adaptris.core.varsub;


enum VariableSubstitutionImplFactory {
  
  simple() {
    @Override
    public VariableSubstitutable create() {
      return new SimpleStringSubstitution();
    }
  },
  simpleWithLogging() {
    @Override
    public VariableSubstitutable create() {
      return new SimpleStringSubstitution(true, false);
    }
  },
  strict() {
    @Override
    public VariableSubstitutable create() {
      return new SimpleStringSubstitution(false, true);
    }
  },
  strictWithLogging() {
    @Override
    public VariableSubstitutable create() {
      return new SimpleStringSubstitution(true, true);
    }
  };
  
  public abstract VariableSubstitutable create();
  
}
