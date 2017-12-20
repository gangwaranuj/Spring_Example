package com.workmarket.service.exception.account;

import java.math.BigDecimal;


public class InvalidSpendLimitException extends RuntimeException {

	private BigDecimal spendLimit;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4747736297958215619L;

	public InvalidSpendLimitException() {
		super();
	}

	public InvalidSpendLimitException(String message) {
		super(message);
	}
	
	public InvalidSpendLimitException(String message, BigDecimal spendLimit) {
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
