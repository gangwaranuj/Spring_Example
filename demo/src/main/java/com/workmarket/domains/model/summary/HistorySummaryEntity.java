package com.workmarket.domains.model.summary;

import com.workmarket.domains.model.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class HistorySummaryEntity extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private Long dateId;

	protected HistorySummaryEntity() {
	}

	protected HistorySummaryEntity(Long dateId) {
		this.dateId = dateId;
	}

	@Column(name = "date_id", nullable = false, length = 11)
	public Long getDateId() {
		return dateId;
	}

	public void setDateId(Long dateId) {
		this.dateId = dateId;
	}
}
