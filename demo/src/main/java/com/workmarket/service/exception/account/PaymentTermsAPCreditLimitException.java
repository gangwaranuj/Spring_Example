package com.workmarket.service.exception.account;


public class PaymentTermsAPCreditLimitException extends RuntimeException {
	
	/**
	 * Instance variables and constants
	 */
	private static final long serialVersionUID = 6026253470863463923L;

	public PaymentTermsAPCreditLimitException() {
		super();
	}

	public PaymentTermsAPCreditLimitException(String message) {
		super(message);
	}
	
}
