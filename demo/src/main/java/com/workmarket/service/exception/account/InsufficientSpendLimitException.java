package com.workmarket.service.exception.account;

import java.math.BigDecimal;


public class InsufficientSpendLimitException extends RuntimeException {

	private BigDecimal spendLimit;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4747736297958215619L;

	public InsufficientSpendLimitException() {
		super();
	}

	public InsufficientSpendLimitException(String message) {
		super(message);
	}
	
	public InsufficientSpendLimitException(String message, BigDecimal spendLimit) {
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
