package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

import java.util.List;

public class WorkRepriceEvent extends Event {

	private List<Long> workIds;
	private Long pricingStrategyId;
	private Double flatPrice;
	private Double perHourPrice;
	private Double maxNumberOfHours;
	private Double perUnitPrice;
	private Double maxNumberOfUnits;
	private Double initialPerHourPrice;
	private Double initialNumberOfHours;
	private Double additionalPerHourPrice;
	private Double maxBlendedNumberOfHours;
	private String pricingMode;

	private static final long serialVersionUID = 901151710246991419L;

	public WorkRepriceEvent() {}

	public List<Long> getWorkIds() {
		return workIds;
	}

	public WorkRepriceEvent setWorkIds(List<Long> workIds) {
		this.workIds = workIds;
		return this;
	}

	public Long getPricingStrategyId() {
		return pricingStrategyId;
	}

	public WorkRepriceEvent setPricingStrategyId(Long pricingStrategyId) {
		this.pricingStrategyId = pricingStrategyId;
		return this;
	}

	public Double getFlatPrice() {
		return flatPrice;
	}

	public WorkRepriceEvent setFlatPrice(Double flatPrice) {
		this.flatPrice = flatPrice;
		return this;
	}

	public Double getPerHourPrice() {
		return perHourPrice;
	}

	public WorkRepriceEvent setPerHourPrice(Double perHourPrice) {
		this.perHourPrice = perHourPrice;
		return this;
	}

	public Double getMaxNumberOfHours() {
		return maxNumberOfHours;
	}

	public WorkRepriceEvent setMaxNumberOfHours(Double maxNumberOfHours) {
		this.maxNumberOfHours = maxNumberOfHours;
		return this;
	}

	public Double getPerUnitPrice() {
		return perUnitPrice;
	}

	public WorkRepriceEvent setPerUnitPrice(Double perUnitPrice) {
		this.perUnitPrice = perUnitPrice;
		return this;
	}

	public Double getMaxNumberOfUnits() {
		return maxNumberOfUnits;
	}

	public WorkRepriceEvent setMaxNumberOfUnits(Double maxNumberOfUnits) {
		this.maxNumberOfUnits = maxNumberOfUnits;
		return this;
	}

	public Double getInitialPerHourPrice() {
		return initialPerHourPrice;
	}

	public WorkRepriceEvent setInitialPerHourPrice(Double initialPerHourPrice) {
		this.initialPerHourPrice = initialPerHourPrice;
		return this;
	}

	public Double getInitialNumberOfHours() {
		return initialNumberOfHours;
	}

	public WorkRepriceEvent setInitialNumberOfHours(Double initialNumberOfHours) {
		this.initialNumberOfHours = initialNumberOfHours;
		return this;
	}

	public Double getAdditionalPerHourPrice() {
		return additionalPerHourPrice;
	}

	public WorkRepriceEvent setAdditionalPerHourPrice(Double additionalPerHourPrice) {
		this.additionalPerHourPrice = additionalPerHourPrice;
		return this;
	}

	public Double getMaxBlendedNumberOfHours() {
		return maxBlendedNumberOfHours;
	}

	public WorkRepriceEvent setMaxBlendedNumberOfHours(Double maxBlendedNumberOfHours) {
		this.maxBlendedNumberOfHours = maxBlendedNumberOfHours;
		return this;
	}

	public String getPricingMode() {
		return pricingMode;
	}

	public WorkRepriceEvent setPricingMode(String pricingMode) {
		this.pricingMode = pricingMode;
		return this;
	}

}
