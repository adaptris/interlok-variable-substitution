package com.adaptris.core.varsub;


public enum VariableSubstitutionType {
  
  SIMPLE() {
    @Override
    VariableSubstitutable create() {
      return new SimpleStringSubstitution();
    }
  },
  @Deprecated simple() {
    @Override
    VariableSubstitutable create() {
      return new SimpleStringSubstitution();
    }
  },
  SIMPLE_WITH_LOGGING() {
    @Override
    VariableSubstitutable create() {
      return new SimpleStringSubstitution(true, false);
    }
  },
  @Deprecated simpleWithLogging() {
    @Override
    VariableSubstitutable create() {
      return new SimpleStringSubstitution(true, false);
    }
  },
  STRICT() {
    @Override
    VariableSubstitutable create() {
      return new SimpleStringSubstitution(false, true);
    }
  },
  @Deprecated strict() {
    @Override
    VariableSubstitutable create() {
      return new SimpleStringSubstitution(false, true);
    }
  },
  STRICT_WITH_LOGGING() {
    @Override
    VariableSubstitutable create() {
      return new SimpleStringSubstitution(true, true);
    }
  },
  @Deprecated stringWithLogging() {
    @Override
    VariableSubstitutable create() {
      return new SimpleStringSubstitution(true, true);
    }
  };
  
  abstract VariableSubstitutable create();
  
}
