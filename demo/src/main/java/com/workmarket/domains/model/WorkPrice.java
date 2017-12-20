package com.workmarket.domains.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.work.model.Work;

@Entity(name="workPrice")
@Table(name="work_price")
@AuditChanges
@Access(AccessType.FIELD)
public class WorkPrice extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_id", referencedColumnName = "id", updatable = false)
	private Work work;

	@NotNull
	private FullPricingStrategy fullPricingStrategy = new FullPricingStrategy();

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
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
}
