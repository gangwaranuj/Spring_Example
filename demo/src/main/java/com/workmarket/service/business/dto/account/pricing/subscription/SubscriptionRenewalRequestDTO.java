package com.workmarket.service.business.dto.account.pricing.subscription;

import com.google.common.collect.Lists;

import java.util.List;

public class SubscriptionRenewalRequestDTO {

	private Integer numberOfPeriods = 0;
	private Boolean modifyPricing = Boolean.FALSE;
	private List<SubscriptionPaymentTierDTO> subscriptionPaymentTierDTOs = Lists.newArrayList();
	private Long parentSubscriptionId;

	public Integer getNumberOfPeriods() {
		return numberOfPeriods;
	}

	public void setNumberOfPeriods(Integer numberOfPeriods) {
		this.numberOfPeriods = numberOfPeriods;
	}

	public Boolean getModifyPricing() {
		return modifyPricing;
	}

	public void setModifyPricing(Boolean modifyPricing) {
		this.modifyPricing = modifyPricing;
	}

	public List<SubscriptionPaymentTierDTO> getSubscriptionPaymentTierDTOs() {
		return subscriptionPaymentTierDTOs;
	}

	public void setSubscriptionPaymentTierDTOs(List<SubscriptionPaymentTierDTO> subscriptionPaymentTiers) {
		this.subscriptionPaymentTierDTOs = subscriptionPaymentTiers;
	}

	public Long getParentSubscriptionId() {
		return parentSubscriptionId;
	}

	public void setParentSubscriptionId(Long parentSubscriptionId) {
		this.parentSubscriptionId = parentSubscriptionId;
	}

}
