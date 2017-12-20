package com.workmarket.service.business.dto.account.pricing.subscription;

import java.math.BigDecimal;
import java.util.Calendar;

public class SubscriptionCancelDTO {

	private Calendar cancellationDate;
	private BigDecimal cancellationFee = BigDecimal.ZERO;
	private String note;

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public BigDecimal getCancellationFee() {
		return cancellationFee;
	}

	public void setCancellationFee(BigDecimal cancellationFee) {
		this.cancellationFee = cancellationFee;
	}

	public void setCancellationFee(double cancellationFee) {
		this.cancellationFee = BigDecimal.valueOf(cancellationFee);
	}

	public Calendar getCancellationDate() {
		return cancellationDate;
	}

	public void setCancellationDate(Calendar cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	public boolean hasCancellationFee() {
		return (cancellationFee != null && cancellationFee.compareTo(BigDecimal.ZERO) > 0);
	}
}
