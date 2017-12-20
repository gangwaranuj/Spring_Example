package com.workmarket.service.exception.account;

import java.math.BigDecimal;


public class InvalidBankAccountException extends Exception {

	/**
	 * Instance variables and constants 
	 */
	private BigDecimal spendLimit;
	private static final long serialVersionUID = 6465160824643656919L;
	

	public InvalidBankAccountException() {
		super();
	}

	public InvalidBankAccountException(String message) {
		super(message);
	}
	
	public InvalidBankAccountException(String message, BigDecimal spendLimit) {
		super(message);
		this.spendLimit = spendLimit;
	}

	public void setSpendLimit(BigDecimal spendLimit) {
		this.spendLimit = spendLimit;
	}

	public BigDecimal getSpendLimit() {
		return spendLimit;
	}

}
