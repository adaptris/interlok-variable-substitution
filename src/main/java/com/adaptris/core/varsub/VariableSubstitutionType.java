package com.adaptris.core.varsub;

import com.adaptris.annotation.Removal;

public enum VariableSubstitutionType {

  SIMPLE() {
    @Override
    VariableSubstitutable create() {
      return new SimpleStringSubstitution();
    }
  },
  @Deprecated
  @Removal(version = "4.0.0")
  simple() {
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
  @Deprecated
  @Removal(version = "4.0.0")
  simpleWithLogging() {
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
  @Deprecated
  @Removal(version = "4.0.0")
  strict() {
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
  @Deprecated
  @Removal(version = "4.0.0")
  strictWithLogging() {
    @Override
    VariableSubstitutable create() {
      return new SimpleStringSubstitution(true, true);
    }
  };

  abstract VariableSubstitutable create();

}
