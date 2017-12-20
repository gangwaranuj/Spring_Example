package com.workmarket.service.business.dto;

public class CompleteWorkDTO {
	private String resolution;
	private Double hoursWorked;
	private Double unitsProcessed;
	private Double additionalExpenses;
	private Double bonus;
	private Double overridePrice;
	
	private Boolean salesTaxCollectedFlag = Boolean.FALSE;
	private Double salesTaxRate;
	
	private RatingDTO rating;

	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public Double getHoursWorked() {
		return hoursWorked;
	}
	public void setHoursWorked(Double hoursWorked) {
		this.hoursWorked = hoursWorked;
	}
	public Double getUnitsProcessed() {
		return unitsProcessed;
	}
	public void setUnitsProcessed(Double unitsProcessed) {
		this.unitsProcessed = unitsProcessed;
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
	public Double getOverridePrice() {
		return overridePrice;
	}
	public void setOverridePrice(Double overridePrice) {
		this.overridePrice = overridePrice;
	}
	public Boolean getSalesTaxCollectedFlag() {
		return salesTaxCollectedFlag;
	}
	public void setSalesTaxCollectedFlag(Boolean salesTaxCollectedFlag) {
		this.salesTaxCollectedFlag = salesTaxCollectedFlag;
	}
	public Double getSalesTaxRate() {
		return salesTaxRate;
	}
	public void setSalesTaxRate(Double salesTaxRate) {
		this.salesTaxRate = salesTaxRate;
	}
	public RatingDTO getRating() {
		return rating;
	}
	public void setRating(RatingDTO rating) {
		this.rating = rating;
	}
	
	public boolean hasRating() {
		return rating != null && rating.getValue() != null;
	}
}
