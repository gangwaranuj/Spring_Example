package com.workmarket.service.business.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

public class PaymentSummaryDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BigDecimal maxSpendLimit = BigDecimal.ZERO;
	private BigDecimal actualSpendLimit = BigDecimal.ZERO;
	private BigDecimal buyerFee = BigDecimal.ZERO;
	private BigDecimal buyerFeePercentage = BigDecimal.ZERO;
	private Integer buyerFeeBand = BigDecimal.ONE.intValue();
	private BigDecimal totalCost = BigDecimal.ZERO;
	
	private Boolean legacyBuyerFee = Boolean.FALSE;
	
	private BigDecimal hoursWorked;
	private BigDecimal unitsProcessed;
	private BigDecimal additionalExpenses;
	private BigDecimal additionalExpensesWithFee;
	private BigDecimal bonus;
	private BigDecimal bonusWithFee;

	private Boolean salesTaxCollectedFlag;
	private BigDecimal salesTaxCollected;
	private BigDecimal salesTaxRate;
	
	private Calendar paidOn;
	private Calendar paymentDueOn;

	private BigDecimal perHourPriceWithFee = BigDecimal.ZERO;
	private BigDecimal perUnitPriceWithFee = BigDecimal.ZERO;
	private BigDecimal initialPerHourPriceWithFee = BigDecimal.ZERO;
	private BigDecimal additionalPerHourPriceWithFee = BigDecimal.ZERO;
	
	public BigDecimal getMaxSpendLimit() {
		return maxSpendLimit;
	}
	public void setMaxSpendLimit(BigDecimal maxSpendLimit) {
		this.maxSpendLimit = maxSpendLimit;
	}
	
	public BigDecimal getActualSpendLimit() {
		return actualSpendLimit;
	}
	public void setActualSpendLimit(BigDecimal actualSpendLimit) {
		this.actualSpendLimit = actualSpendLimit;
	}
	
	public BigDecimal getBuyerFee() {
		return buyerFee;
	}
	public void setBuyerFee(BigDecimal buyerFee) {
		this.buyerFee = buyerFee;
	}
	
	public BigDecimal getBuyerFeePercentage() {
		return buyerFeePercentage;
	}
	public void setBuyerFeePercentage(BigDecimal buyerFeePercentage) {
		this.buyerFeePercentage = buyerFeePercentage;
	}
	
	public Integer getBuyerFeeBand() {
		return buyerFeeBand;
	}
	public void setBuyerFeeBand(Integer buyerFeeBand) {
		this.buyerFeeBand = buyerFeeBand;
	}
	
	public BigDecimal getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}
	
	public Boolean getLegacyBuyerFee() {
		return legacyBuyerFee;
	}
	public void setLegacyBuyerFee(Boolean legacyBuyerFee) {
		this.legacyBuyerFee = legacyBuyerFee;
	}
	
	public BigDecimal getHoursWorked() {
		return hoursWorked;
	}
	public void setHoursWorked(BigDecimal hoursWorked) {
		this.hoursWorked = hoursWorked;
	}
	
	public BigDecimal getUnitsProcessed() {
		return unitsProcessed;
	}
	public void setUnitsProcessed(BigDecimal unitsProcessed) {
		this.unitsProcessed = unitsProcessed;
	}
	
	public BigDecimal getAdditionalExpenses() {
		return additionalExpenses;
	}
	public void setAdditionalExpenses(BigDecimal additionalExpenses) {
		this.additionalExpenses = additionalExpenses;
	}

	public BigDecimal getAdditionalExpensesWithFee() {
		return additionalExpensesWithFee;
	}

	public void setAdditionalExpensesWithFee(BigDecimal additionalExpensesWithFee) {
		this.additionalExpensesWithFee = additionalExpensesWithFee;
	}

	public BigDecimal getBonus() {
		return bonus;
	}

	public void setBonus(BigDecimal bonus) {
		this.bonus = bonus;
	}

	public BigDecimal getBonusWithFee() {
		return bonusWithFee;
	}

	public void setBonusWithFee(BigDecimal bonusWithFee) {
		this.bonusWithFee = bonusWithFee;
	}

	public Boolean getSalesTaxCollectedFlag() {
		return salesTaxCollectedFlag;
	}
	public void setSalesTaxCollectedFlag(Boolean salesTaxCollectedFlag) {
		this.salesTaxCollectedFlag = salesTaxCollectedFlag;
	}
	
	public BigDecimal getSalesTaxCollected() {
		return salesTaxCollected;
	}
	public void setSalesTaxCollected(BigDecimal salesTaxCollected) {
		this.salesTaxCollected = salesTaxCollected;
	}
	
	public BigDecimal getSalesTaxRate() {
		return salesTaxRate;
	}
	public void setSalesTaxRate(BigDecimal salesTaxRate) {
		this.salesTaxRate = salesTaxRate;
	}
	public Calendar getPaidOn() {
		return paidOn;
	}
	public void setPaidOn(Calendar paidOn) {
		this.paidOn = paidOn;
	}
	public Calendar getPaymentDueOn() {
		return paymentDueOn;
	}
	public void setPaymentDueOn(Calendar paymentDueOn) {
		this.paymentDueOn = paymentDueOn;
	}

	public BigDecimal getPerHourPriceWithFee() {
		return perHourPriceWithFee;
	}

	public void setPerHourPriceWithFee(BigDecimal perHourPriceWithFee) {
		this.perHourPriceWithFee = perHourPriceWithFee;
	}

	public BigDecimal getPerUnitPriceWithFee() {
		return perUnitPriceWithFee;
	}

	public void setPerUnitPriceWithFee(BigDecimal perUnitPriceWithFee) {
		this.perUnitPriceWithFee = perUnitPriceWithFee;
	}

	public BigDecimal getInitialPerHourPriceWithFee() {
		return initialPerHourPriceWithFee;
	}

	public void setInitialPerHourPriceWithFee(BigDecimal initialPerHourPriceWithFee) {
		this.initialPerHourPriceWithFee = initialPerHourPriceWithFee;
	}

	public BigDecimal getAdditionalPerHourPriceWithFee() {
		return additionalPerHourPriceWithFee;
	}

	public void setAdditionalPerHourPriceWithFee(BigDecimal additionalPerHourPriceWithFee) {
		this.additionalPerHourPriceWithFee = additionalPerHourPriceWithFee;
	}
}