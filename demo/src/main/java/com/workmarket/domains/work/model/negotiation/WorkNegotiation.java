package com.workmarket.domains.work.model.negotiation;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.BeanUtilities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Calendar;

@Entity(name="workNegotiation")
@DiscriminatorValue(AbstractWorkNegotiation.NEGOTIATION)
@NamedQueries({})
@AuditChanges
public class WorkNegotiation extends AbstractWorkNegotiation implements ScheduleNegotiation {

	private static final long serialVersionUID = -5685450384210360634L;

	private Calendar expiresOn;

	private Boolean priceNegotiation = Boolean.FALSE;
	@NotNull
	private FullPricingStrategy fullPricingStrategy = new FullPricingStrategy();

	private Boolean scheduleRangeFlag;
	private Calendar scheduleFrom;
	private Calendar scheduleThrough;

	@Column(name="expires_on", nullable=true)
	public Calendar getExpiresOn() {
		return this.expiresOn;
	}
	public void setExpiresOn(Calendar expiresOn) {
		this.expiresOn = expiresOn;
	}

	// Pricing

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

	// Scheduling

	@Column(name="schedule_is_range_flag", nullable=false, length=1)
	public Boolean getScheduleRangeFlag() {
		return this.scheduleRangeFlag;
	}
	public void setScheduleRangeFlag(Boolean scheduleRangeFlag) {
		this.scheduleRangeFlag = scheduleRangeFlag;
	}

	@Column(name="schedule_from", nullable=true)
	public Calendar getScheduleFrom() {
		return this.scheduleFrom;
	}
	public void setScheduleFrom(Calendar scheduleFrom) {
		this.scheduleFrom = scheduleFrom;
	}

	@Column(name="schedule_through", nullable=true)
	public Calendar getScheduleThrough() {
		return this.scheduleThrough;
	}
	public void setScheduleThrough(Calendar scheduleThrough) {
		this.scheduleThrough = scheduleThrough;
	}

	@Transient
	public DateRange getSchedule() {
		return new DateRange(scheduleFrom, scheduleThrough);
	}

	@Transient
	public WorkNegotiationDTO newDTO() {
		WorkNegotiationDTO dto = BeanUtilities.newBean(WorkNegotiationDTO.class, this);
		BeanUtilities.copyProperties(dto, fullPricingStrategy);
		dto.setPricingStrategyId(getPricingStrategy().getId());
		// The pricing strategy is in the context of the resource, not the buyer
		dto.setUseMaxSpendPricingDisplayModeFlag(false);
		return dto;
	}

	@Transient
	public boolean hasExpirationDate() {
		return (getExpiresOn() != null);
	}

	@Transient
	public String getNegotiationType() {
		return NEGOTIATION;
	}
}
