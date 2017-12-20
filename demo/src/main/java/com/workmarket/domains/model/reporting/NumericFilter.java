/**
 * 
 */
package com.workmarket.domains.model.reporting;

import java.math.BigDecimal;

public class NumericFilter extends Filter {

	/**
	 * Instance variables and constants
	 */
	private BigDecimal fromValue;
	private BigDecimal toValue;
	private static final long serialVersionUID = -6119701761363382238L;

	public BigDecimal getFromValue() {
		return fromValue;
	}

	public void setFromValue(BigDecimal fromValue) {
		this.fromValue = fromValue;
	}

	public BigDecimal getToValue() {
		return toValue;
	}

	public void setToValue(BigDecimal toValue) {
		this.toValue = toValue;
	}

}
