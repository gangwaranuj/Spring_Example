package com.workmarket.service.business.dto.account.pricing.subscription;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionAddOnTypeAssociation;

import javax.annotation.Nullable;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/** Author: rocio */
public class SubscriptionAddOnDTO {

	private String     addOnTypeCode;
	private Calendar   effectiveDate;
	private BigDecimal costPerPeriod = BigDecimal.ZERO;

	public SubscriptionAddOnDTO() {}

	public SubscriptionAddOnDTO(SubscriptionAddOnTypeAssociation addOn) {
		this.setAddOnTypeCode(addOn.getSubscriptionAddOnType().getCode());
		this.setCostPerPeriod(addOn.getCostPerPeriod());
	}

	public String getAddOnTypeCode() {
		return addOnTypeCode;
	}

	public void setAddOnTypeCode(String addOnTypeCode) {
		this.addOnTypeCode = addOnTypeCode;
	}

	public BigDecimal getCostPerPeriod() {
		return costPerPeriod;
	}

	public void setCostPerPeriod(BigDecimal costPerPeriod) {
		this.costPerPeriod = costPerPeriod;
	}

	public static List<SubscriptionAddOnDTO> transform(Collection<SubscriptionAddOnTypeAssociation> addOns) {
		return Lists.transform(Lists.newArrayList(addOns), new Function<SubscriptionAddOnTypeAssociation, SubscriptionAddOnDTO>() {
			@Override
			public SubscriptionAddOnDTO apply(@Nullable SubscriptionAddOnTypeAssociation addOn) {
				return (addOn != null) ? new SubscriptionAddOnDTO(addOn) : null;
			}
		});
	}

	public Calendar getEffectiveDate() {
	    return effectiveDate;
    }

	public void setEffectiveDate(Calendar effectiveDate) {
	    this.effectiveDate = effectiveDate;
    }
}
