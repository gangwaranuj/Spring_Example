package com.workmarket.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

public class FastFundInvoiceDTO implements Serializable {
	private static final long serialVersionUID = -8056900026770619873L;

	private BigDecimal fastFundsFee;
	private Calendar fastFundedOn;

	public BigDecimal getFastFundsFee() {
		return fastFundsFee;
	}

	public void setFastFundsFee(BigDecimal fastFundsFee) {
		this.fastFundsFee = fastFundsFee;
	}

	public Calendar getFastFundedOn() {
		return fastFundedOn;
	}

	public void setFastFundedOn(Calendar fastFundedOn) {
		this.fastFundedOn = fastFundedOn;
	}
}
