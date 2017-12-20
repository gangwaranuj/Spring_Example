package com.workmarket.service.business.dto;

import java.util.Calendar;

public class PaymentConfigurationDTO {

	private Integer paymentCycleDays = 0;
	private Integer accountingProcessDays = 0;
	private Integer preferredDayOfWeek = Calendar.MONDAY;
	private Integer preferredDayOfMonth = 1;
	private boolean checkPaymentMethodEnabled = false;
	private boolean wireTransferPaymentMethodEnabled = true;
	private boolean achPaymentMethodEnabled = false;
	private boolean creditCardPaymentMethodEnabled = false;
	private boolean prefundPaymentMethodEnabled = false;
	private boolean biweeklyPaymentOnSpecificDayOfMonth = false;
	private Integer preferredDayOfMonthBiweeklyFirstPayment = 1;

	public Integer getPaymentCycleDays() {
		return paymentCycleDays;
	}

	public void setPaymentCycleDays(Integer paymentCycleDays) {
		this.paymentCycleDays = paymentCycleDays;
	}

	public Integer getAccountingProcessDays() {
		return accountingProcessDays;
	}

	public void setAccountingProcessDays(Integer accountingProcessDays) {
		this.accountingProcessDays = accountingProcessDays;
	}

	public Integer getPreferredDayOfWeek() {
		return preferredDayOfWeek;
	}

	public void setPreferredDayOfWeek(Integer preferredDayOfWeek) {
		this.preferredDayOfWeek = preferredDayOfWeek;
	}

	public Integer getPreferredDayOfMonth() {
		return preferredDayOfMonth;
	}

	public void setPreferredDayOfMonth(Integer preferredDayOfMonth) {
		this.preferredDayOfMonth = preferredDayOfMonth;
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

	public boolean isBiweeklyPaymentOnSpecificDayOfMonth() {
		return biweeklyPaymentOnSpecificDayOfMonth;
	}

	public void setBiweeklyPaymentOnSpecificDayOfMonth(boolean biweeklyPaymentOnSpecificDayOfMonth) {
		this.biweeklyPaymentOnSpecificDayOfMonth = biweeklyPaymentOnSpecificDayOfMonth;
	}

	public Integer getPreferredDayOfMonthBiweeklyFirstPayment() {
		return preferredDayOfMonthBiweeklyFirstPayment;
	}

	public void setPreferredDayOfMonthBiweeklyFirstPayment(Integer preferredDayOfMonthBiweeklyFirstPayment) {
		this.preferredDayOfMonthBiweeklyFirstPayment = preferredDayOfMonthBiweeklyFirstPayment;
	}

	public boolean isPrefundPaymentMethodEnabled() {
		return prefundPaymentMethodEnabled;
	}

	public void setPrefundPaymentMethodEnabled(boolean prefundPaymentMethodEnabled) {
		this.prefundPaymentMethodEnabled = prefundPaymentMethodEnabled;
	}

	@Override
	public String toString() {
		return "PaymentConfigurationDTO [paymentCycleDays=" + paymentCycleDays + ", accountingProcessDays=" + accountingProcessDays + ", preferredDayOfWeek=" + preferredDayOfWeek
				+ ", preferredDayOfMonth=" + preferredDayOfMonth + ", checkPaymentMethodEnabled=" + checkPaymentMethodEnabled + ", wireTransferPaymentMethodEnabled="
				+ wireTransferPaymentMethodEnabled + ", achPaymentMethodEnabled=" + achPaymentMethodEnabled + ", creditCardPaymentMethodEnabled=" + creditCardPaymentMethodEnabled
				+ ", biweeklyPaymentOnSpecificDayOfMonth=" + biweeklyPaymentOnSpecificDayOfMonth + ", preferredDayOfMonthBiweeklyFirstPayment=" + preferredDayOfMonthBiweeklyFirstPayment
				+ "]";
	}
}
