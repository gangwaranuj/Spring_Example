package com.workmarket.service.business.event;

import java.math.BigDecimal;
import java.util.Calendar;

public class SendLowBalanceAlertEvent extends Event {
	private static final long serialVersionUID = 6413851436430338919L;

	private Long userId;
	private String email;
	private BigDecimal spendLimit;
	private Calendar scheduleDate;

	public SendLowBalanceAlertEvent(Long userId, String email, BigDecimal spendLimit, Calendar scheduleDate) {
		this.userId = userId;
		this.email = email;
		this.spendLimit = spendLimit;
		this.scheduleDate = scheduleDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Calendar getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Calendar scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getSpendLimit() {
		return spendLimit;
	}

	public void setSpendLimit(BigDecimal spendLimit) {
		this.spendLimit = spendLimit;
	}
}
