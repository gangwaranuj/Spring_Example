package com.workmarket.domains.work.model;

import java.math.BigDecimal;

public class WorkWorkResourceAccountRegister {

	private Long workId;
	private Long companyId;
	private Long workResourceUserId;
	private Long workResourceId;
	private Long buyerAccountRegisterId;
	private Long resourceAccountRegisterId;
	private Long workResourceDelegatorId;
	private Long workResourceDelegatorUserId;
	private BigDecimal hoursWorked;
	private BigDecimal unitsProcessed;
	private BigDecimal additionalExpenses;
	private BigDecimal bonus;
	private BigDecimal overridePrice;
	private String pricingStrategyType;
	private BigDecimal totalResourceCost;
	private BigDecimal workFeeBandPercentage;
	private BigDecimal totalBuyerCost;

	public Long getWorkId() {
		return workId;
	}

	public WorkWorkResourceAccountRegister setWorkId(Long workId) {
		this.workId = workId;
		return this;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public WorkWorkResourceAccountRegister setCompanyId(Long companyId) {
		this.companyId = companyId;
		return this;
	}

	public Long getWorkResourceUserId() {
		return workResourceUserId;
	}

	public WorkWorkResourceAccountRegister setWorkResourceUserId(Long workResourceUserId) {
		this.workResourceUserId = workResourceUserId;
		return this;
	}

	public Long getWorkResourceId() {
		return workResourceId;
	}

	public WorkWorkResourceAccountRegister setWorkResourceId(Long workResourceId) {
		this.workResourceId = workResourceId;
		return this;
	}

	public Long getBuyerAccountRegisterId() {
		return buyerAccountRegisterId;
	}

	public WorkWorkResourceAccountRegister setBuyerAccountRegisterId(Long buyerAccountRegisterId) {
		this.buyerAccountRegisterId = buyerAccountRegisterId;
		return this;
	}

	public Long getResourceAccountRegisterId() {
		return resourceAccountRegisterId;
	}

	public WorkWorkResourceAccountRegister setResourceAccountRegisterId(Long resourceAccountRegisterId) {
		this.resourceAccountRegisterId = resourceAccountRegisterId;
		return this;
	}

	public Long getWorkResourceDelegatorId() {
		return workResourceDelegatorId;
	}

	public WorkWorkResourceAccountRegister setWorkResourceDelegatorId(Long workResourceDelegatorId) {
		this.workResourceDelegatorId = workResourceDelegatorId;
		return this;
	}

	public Long getWorkResourceDelegatorUserId() {
		return workResourceDelegatorUserId;
	}

	public WorkWorkResourceAccountRegister setWorkResourceDelegatorUserId(Long workResourceDelegatorUserId) {
		this.workResourceDelegatorUserId = workResourceDelegatorUserId;
		return this;
	}

	public BigDecimal getHoursWorked() {
		return hoursWorked;
	}

	public WorkWorkResourceAccountRegister setHoursWorked(BigDecimal hoursWorked) {
		this.hoursWorked = hoursWorked;
		return this;
	}

	public BigDecimal getUnitsProcessed() {
		return unitsProcessed;
	}

	public WorkWorkResourceAccountRegister setUnitsProcessed(BigDecimal unitsProcessed) {
		this.unitsProcessed = unitsProcessed;
		return this;
	}

	public BigDecimal getAdditionalExpenses() {
		return additionalExpenses;
	}

	public WorkWorkResourceAccountRegister setAdditionalExpenses(BigDecimal additionalExpenses) {
		this.additionalExpenses = additionalExpenses;
		return this;
	}

	public BigDecimal getBonus() {
		return bonus;
	}

	public WorkWorkResourceAccountRegister setBonus(BigDecimal bonus) {
		this.bonus = bonus;
		return this;
	}

	public BigDecimal getOverridePrice() {
		return overridePrice;
	}

	public WorkWorkResourceAccountRegister setOverridePrice(BigDecimal overridePrice) {
		this.overridePrice = overridePrice;
		return this;
	}

	public String getPricingStrategyType() {
		return pricingStrategyType;
	}

	public WorkWorkResourceAccountRegister setPricingStrategyType(String pricingStrategyType) {
		this.pricingStrategyType = pricingStrategyType;
		return this;
	}

	public BigDecimal getTotalResourceCost() {
		return totalResourceCost;
	}

	public WorkWorkResourceAccountRegister setTotalResourceCost(BigDecimal totalResourceCost) {
		this.totalResourceCost = totalResourceCost;
		return this;
	}

	public BigDecimal getWorkFeeBandPercentage() {
		return workFeeBandPercentage;
	}

	public WorkWorkResourceAccountRegister setWorkFeeBandPercentage(BigDecimal workFeeBandPercentage) {
		this.workFeeBandPercentage = workFeeBandPercentage;
		return this;
	}

	public BigDecimal getTotalBuyerCost() {
		return totalBuyerCost;
	}

	public WorkWorkResourceAccountRegister setTotalBuyerCost(BigDecimal totalBuyerCost) {
		this.totalBuyerCost = totalBuyerCost;
		return this;
	}

	@Override
	public String toString() {
		return "WorkWorkResourceAccountRegister [workId=" + workId + ", companyId=" + companyId + ", workResourceUserId=" + workResourceUserId + ", workResourceId=" + workResourceId
				+ ", buyerAccountRegisterId=" + buyerAccountRegisterId + ", resourceAccountRegisterId=" + resourceAccountRegisterId + ", workResourceDelegatorId=" + workResourceDelegatorId
				+ ", workResourceDelegatorUserId=" + workResourceDelegatorUserId + ", hoursWorked=" + hoursWorked + ", unitsProcessed=" + unitsProcessed + ", additionalExpenses=" + additionalExpenses
				+ ", overridePrice=" + overridePrice + ", pricingStrategyType=" + pricingStrategyType + ", totalResourceCost=" + totalResourceCost + ", workFeeBandPercentage=" + workFeeBandPercentage
				+ ", totalBuyerCost=" + totalBuyerCost + "]";
	}

}