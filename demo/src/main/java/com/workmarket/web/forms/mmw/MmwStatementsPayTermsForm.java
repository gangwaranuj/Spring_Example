package com.workmarket.web.forms.mmw;

import java.io.Serializable;

public class MmwStatementsPayTermsForm implements Serializable {
	private static final long serialVersionUID = 1L;

	private String paymentType;
	private boolean statementsEnabled;
	private Integer paymentTermsDays;
	private Integer frequency;
	private Integer weekday;
	private String biweeklyCycle;
	private Integer biweeklyWeekdays;
	private Integer biweeklySet;
	private Integer monthDays;
	private Integer delay;
	private boolean checkPaymentMethodEnabled;
	private boolean wireTransferPaymentMethodEnabled;
	private boolean achPaymentMethodEnabled;
	private boolean creditCardPaymentMethodEnabled;
	private boolean prefundPaymentMethodEnabled;

	private Boolean autoPayEnabled;

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public boolean isStatementsEnabled() {
		return statementsEnabled;
	}

	public void setStatementsEnabled(boolean statementsEnabled) {
		this.statementsEnabled = statementsEnabled;
	}

	public Integer getPaymentTermsDays() {
		return paymentTermsDays;
	}

	public void setPaymentTermsDays(Integer paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public Integer getWeekday() {
		return weekday;
	}

	public void setWeekday(Integer weekday) {
		this.weekday = weekday;
	}

	public String getBiweeklyCycle() {
		return biweeklyCycle;
	}

	public void setBiweeklyCycle(String biweeklyCycle) {
		this.biweeklyCycle = biweeklyCycle;
	}

	public Integer getBiweeklyWeekdays() {
		return biweeklyWeekdays;
	}

	public void setBiweeklyWeekdays(Integer biweeklyWeekdays) {
		this.biweeklyWeekdays = biweeklyWeekdays;
	}

	public Integer getBiweeklySet() {
		return biweeklySet;
	}

	public void setBiweeklySet(Integer biweeklySet) {
		this.biweeklySet = biweeklySet;
	}

	public Integer getMonthDays() {
		return monthDays;
	}

	public void setMonthDays(Integer monthDays) {
		this.monthDays = monthDays;
	}

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}

	public boolean isCheckPaymentMethodEnabled() {
		return checkPaymentMethodEnabled;
	}

	public void setCheckPaymentMethodEnabled(boolean checkPaymentMethodEnabled) {
		this.checkPaymentMethodEnabled = checkPaymentMethodEnabled;
	}

	public boolean isWireTransferPaymentMethodEnabled() {
		return wireTransferPaymentMethodEnabled;
	}

	public void setWireTransferPaymentMethodEnabled(boolean wireTransferPaymentMethodEnabled) {
		this.wireTransferPaymentMethodEnabled = wireTransferPaymentMethodEnabled;
	}

	public boolean isAchPaymentMethodEnabled() {
		return achPaymentMethodEnabled;
	}

	public void setAchPaymentMethodEnabled(boolean achPaymentMethodEnabled) {
		this.achPaymentMethodEnabled = achPaymentMethodEnabled;
	}

	public boolean isCreditCardPaymentMethodEnabled() {
		return creditCardPaymentMethodEnabled;
	}

	public void setCreditCardPaymentMethodEnabled(boolean creditCardPaymentMethodEnabled) {
		this.creditCardPaymentMethodEnabled = creditCardPaymentMethodEnabled;
	}

	public Boolean getAutoPayEnabled() {
		return autoPayEnabled;
	}

	public void setAutoPayEnabled(Boolean autoPayEnabled) {
		this.autoPayEnabled = autoPayEnabled;
	}

	public boolean isPrefundPaymentMethodEnabled() {
		return prefundPaymentMethodEnabled;
	}

	public void setPrefundPaymentMethodEnabled(boolean prefundPaymentMethodEnabled) {
		this.prefundPaymentMethodEnabled = prefundPaymentMethodEnabled;
	}
}
