package com.workmarket.service.exception.payment;

public class InvoiceAlreadyPaidException extends RuntimeException {

  private static final long serialVersionUID = 7105241938930749835L;

  public InvoiceAlreadyPaidException() { }

  public InvoiceAlreadyPaidException(final String message) {
    super(message);
  }
}
