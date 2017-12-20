package com.workmarket.service.exception.tax;

/**
 * Created by nick on 12/2/12 11:39 AM
 */
public class TaxVerificationException extends Exception {
	private static final long serialVersionUID = 1L;

	public TaxVerificationException(String message) {
		super(message);
	}
	public TaxVerificationException(String message, Exception e) {
		super(message, e);
	}
}
