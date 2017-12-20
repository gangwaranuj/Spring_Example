package com.workmarket.api.exceptions;

import java.util.List;

public class UnprocessableEntityException extends Exception {
  private final List<String> errors;

  public UnprocessableEntityException(final List<String> errors) {
    this.errors = errors;
  }

  public List<String> getErrors() {
    return errors;
  }
}
