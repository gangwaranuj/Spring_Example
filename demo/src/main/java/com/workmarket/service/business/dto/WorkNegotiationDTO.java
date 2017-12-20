package com.workmarket.service.business.dto;

import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.work.model.negotiation.SpendLimitNegotiationType;
import com.workmarket.utility.NumberUtilities;

public class WorkNegotiationDTO extends WorkDTO {

	private Boolean priceNegotiation = Boolean.FALSE;
	private Boolean scheduleNegotiation = Boolean.FALSE;
	private Boolean budgetIncrease = Boolean.FALSE;
	private String expiresOn; // ISO8601 date
	private String note;
	private String declinedNote;
	private String spendLimitNegotiationTypeCode = SpendLimitNegotiationType.NEED_MORE_EXPENSES;
	private Double additionalExpenses = 0.0;
	private Double bonus = 0.0;
	private Long associatedWorkSubStatusTypeId;
	private Boolean initiatedByResource = Boolean.TRUE;
	private Boolean isPreapproved = Boolean.FALSE;

	public Boolean isPriceNegotiation() {
		return this.priceNegotiation;
	}

	public void setPriceNegotiation(Boolean priceNegotiation) {
		this.priceNegotiation = priceNegotiation;
	}

	public Boolean isScheduleNegotiation() {
		return this.scheduleNegotiation;
	}

	public void setScheduleNegotiation(Boolean scheduleNegotiation) {
		this.scheduleNegotiation = scheduleNegotiation;
	}

	public Boolean isBudgetIncrease() {
		return budgetIncrease;
	}

	public void setBudgetIncrease(Boolean budgetIncrease) {
		this.budgetIncrease = budgetIncrease;
	}

	public String getExpiresOn() {
		return this.expiresOn;
	}

	public void setExpiresOn(String expiresOn) {
		this.expiresOn = expiresOn;
	}

	public String getNote() {
		return note;
	}

	public void setDeclinedNote(String note) {
		this.declinedNote = note;
	}

	public String getDeclinedNote() {
		return declinedNote;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getSpendLimitNegotiationTypeCode() {
		return spendLimitNegotiationTypeCode;
	}

	public void setSpendLimitNegotiationTypeCode(String spendLimitNegotiationTypeCode) {
		this.spendLimitNegotiationTypeCode = spendLimitNegotiationTypeCode;
	}

	public Double getAdditionalExpenses() {
		return additionalExpenses;
	}

	public void setAdditionalExpenses(Double additionalExpenses) {
		this.additionalExpenses = additionalExpenses;
	}

	public Double getBonus() {
		return bonus;
	}

	public void setBonus(Double bonus) {
		this.bonus = bonus;
	}

	public Long getAssociatedWorkSubStatusTypeId() {
		return associatedWorkSubStatusTypeId;
	}

	public void setAssociatedWorkSubStatusTypeId(Long associatedWorkSubStatusTypeId) {
		this.associatedWorkSubStatusTypeId = associatedWorkSubStatusTypeId;
	}

	public Boolean isInitiatedByResource() {
		return initiatedByResource;
	}

	public void setInitiatedByResource(Boolean initiatedByResource) {
		this.initiatedByResource = initiatedByResource;
	}

	public Boolean isPreapproved() {
		return isPreapproved;
	}

	public void setPreapproved(Boolean preapproved) {
		isPreapproved = preapproved;
	}

	public boolean isBonus() {
		return SpendLimitNegotiationType.BONUS.equals(spendLimitNegotiationTypeCode);
	}

	public void copyAndAddExpensesToFullPricingStrategy(FullPricingStrategy fullPricingStrategy) {
		fullPricingStrategy.setAdditionalExpenses(NumberUtilities.nullSafeAddDoubleToBigDecimal(getAdditionalExpenses(), fullPricingStrategy.getAdditionalExpenses()));
		fullPricingStrategy.setBonus(NumberUtilities.nullSafeAddDoubleToBigDecimal(getBonus(), fullPricingStrategy.getBonus()));

		fullPricingStrategy.setFlatPrice(NumberUtilities.convertDoubleToBigDecimalNullSafe(getFlatPrice()));
		fullPricingStrategy.setPerHourPrice(NumberUtilities.convertDoubleToBigDecimalNullSafe(getPerHourPrice()));
		fullPricingStrategy.setMaxNumberOfHours(NumberUtilities.convertDoubleToBigDecimalNullSafe(getMaxNumberOfHours()));
		fullPricingStrategy.setPerUnitPrice(NumberUtilities.convertDoubleToBigDecimalNullSafe(getPerUnitPrice()));
		fullPricingStrategy.setMaxNumberOfUnits(NumberUtilities.convertDoubleToBigDecimalNullSafe(getMaxNumberOfUnits()));
		fullPricingStrategy.setInitialPerHourPrice(NumberUtilities.convertDoubleToBigDecimalNullSafe(getInitialPerHourPrice()));
		fullPricingStrategy.setInitialNumberOfHours(NumberUtilities.convertDoubleToBigDecimalNullSafe(getInitialNumberOfHours()));
		fullPricingStrategy.setAdditionalPerHourPrice(NumberUtilities.convertDoubleToBigDecimalNullSafe(getAdditionalPerHourPrice()));
		fullPricingStrategy.setMaxBlendedNumberOfHours(NumberUtilities.convertDoubleToBigDecimalNullSafe(getMaxBlendedNumberOfHours()));
		fullPricingStrategy.setInitialPerUnitPrice(NumberUtilities.convertDoubleToBigDecimalNullSafe(getInitialPerUnitPrice()));
		fullPricingStrategy.setInitialNumberOfUnits(NumberUtilities.convertDoubleToBigDecimalNullSafe(getInitialNumberOfUnits()));
		fullPricingStrategy.setAdditionalPerUnitPrice(NumberUtilities.convertDoubleToBigDecimalNullSafe(getAdditionalPerUnitPrice()));
		fullPricingStrategy.setMaxBlendedNumberOfUnits(NumberUtilities.convertDoubleToBigDecimalNullSafe(getAdditionalPerUnitPrice()));
	}
}
