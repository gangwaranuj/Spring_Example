package com.workmarket.domains.work.model.negotiation;

import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.BeanUtilities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity(name="workBudgetNegotiation")
@DiscriminatorValue(AbstractWorkNegotiation.BUDGET_INCREASE)
@AuditChanges
public class WorkBudgetNegotiation extends AbstractWorkNegotiation {

	private static final long serialVersionUID = 1L;

	private Boolean priceNegotiation = Boolean.TRUE;
	@NotNull
	private FullPricingStrategy fullPricingStrategy = new FullPricingStrategy();

	@Column(name="negotiate_price_flag")
	public Boolean isPriceNegotiation() {
		return this.priceNegotiation;
	}
	public void setPriceNegotiation(Boolean priceNegotiation) {
		this.priceNegotiation = priceNegotiation;
	}

	public FullPricingStrategy getFullPricingStrategy() {
		return this.fullPricingStrategy;
	}
	public void setFullPricingStrategy(FullPricingStrategy fullPricingStrategy) {
		this.fullPricingStrategy = fullPricingStrategy;
	}

	@Transient
	public PricingStrategy getPricingStrategy() {
		return fullPricingStrategy.getPricingStrategy();
	}

	public void setPricingStrategy(PricingStrategy pricingStrategy) {
		fullPricingStrategy.setPricingStrategy(pricingStrategy);
	}

	@Transient
	public WorkNegotiationDTO toDTO() {
		WorkNegotiationDTO dto = BeanUtilities.newBean(WorkNegotiationDTO.class, this);
		BeanUtilities.copyProperties(dto, fullPricingStrategy);
		dto.setPricingStrategyId(getPricingStrategy().getId());
		// The pricing strategy is in the context of the resource, not the buyer
		dto.setUseMaxSpendPricingDisplayModeFlag(false);
		dto.setPriceNegotiation(priceNegotiation);
		return dto;
	}

	@Transient
	public String getNegotiationType() {
		return BUDGET_INCREASE;
	}
}
