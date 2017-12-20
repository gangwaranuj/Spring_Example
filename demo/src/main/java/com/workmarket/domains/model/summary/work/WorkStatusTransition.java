package com.workmarket.domains.model.summary.work;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "workStatusTransition")
@Table(name = "work_status_transition")
public class WorkStatusTransition implements Serializable {

	private WorkStatusPK transitionId;
	private Long dateId;
	private Long companyId;
	private BigDecimal workPrice;

	public WorkStatusTransition() {
	}

	public WorkStatusTransition(WorkStatusPK transitionId, Long dateId) {
		this.transitionId = transitionId;
		this.dateId = dateId;
	}

	@EmbeddedId
	public WorkStatusPK getTransitionId() {
		return transitionId;
	}

	public void setTransitionId(WorkStatusPK transitionId) {
		this.transitionId = transitionId;
	}

	@Column(name = "date_id", nullable = false, length = 11)
	public Long getDateId() {
		return dateId;
	}

	public void setDateId(Long dateId) {
		this.dateId = dateId;
	}

	@Column(name = "company_id", nullable = false, length = 11)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Column(name = "work_price", nullable = false)
	public BigDecimal getWorkPrice() {
		return workPrice;
	}

	public void setWorkPrice(BigDecimal workPrice) {
		this.workPrice = workPrice;
	}
}
