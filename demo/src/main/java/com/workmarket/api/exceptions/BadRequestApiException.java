package com.workmarket.api.exceptions;

public class BadRequestApiException extends ApiException {
  public BadRequestApiException(final String message) {
    super(message);
  }
}
