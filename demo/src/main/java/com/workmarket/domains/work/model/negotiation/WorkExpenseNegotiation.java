package com.workmarket.domains.work.model.negotiation;

import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.BeanUtilities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by nick on 2012-10-29 11:06 AM
 */
@Entity(name = "workReimbursementNegotiation")
@DiscriminatorValue(AbstractWorkNegotiation.EXPENSE)
@AuditChanges
public class WorkExpenseNegotiation extends AbstractWorkNegotiation {

	private static final long serialVersionUID = 1L;

	private Boolean priceNegotiation = Boolean.TRUE;
	@NotNull
	private FullPricingStrategy fullPricingStrategy = new FullPricingStrategy();
	private SpendLimitNegotiationType spendLimitNegotiationType;

	private BigDecimal standaloneAdditionalExpenses;         // to support overriding the value for display

	@Column(name = "negotiate_price_flag")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "spend_negotiation_type_code", referencedColumnName = "code")
	public SpendLimitNegotiationType getSpendLimitNegotiationType() {
		return spendLimitNegotiationType;
	}

	public void setSpendLimitNegotiationType(SpendLimitNegotiationType spendLimitNegotiationType) {
		this.spendLimitNegotiationType = spendLimitNegotiationType;
	}

	@Transient
	public PricingStrategy getPricingStrategy() {
		return fullPricingStrategy.getPricingStrategy();
	}

	public void setPricingStrategy(PricingStrategy pricingStrategy) {
		fullPricingStrategy.setPricingStrategy(pricingStrategy);
	}

	@Transient
	public BigDecimal getStandaloneAdditionalExpenses() {
		return standaloneAdditionalExpenses;
	}

	public void setStandaloneAdditionalExpenses(BigDecimal standaloneAdditionalExpenses) {
		this.standaloneAdditionalExpenses = standaloneAdditionalExpenses;
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
		return EXPENSE;
	}

}
