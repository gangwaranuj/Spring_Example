package com.workmarket.domains.work.model;

import com.workmarket.domains.model.AbstractEntity;

import javax.persistence.*;

@Entity(name="workOptionalCustomForm")
@Table(name="work_optional_custom_forms")
@Access(AccessType.FIELD)
public class WorkOptionalCustomForm extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_id", referencedColumnName = "id", updatable = false)
	private Work work;

	public Work getWork() {
		return work;
	}

	public WorkOptionalCustomForm setWork(Work work) {
		this.work = work;
		return this;
	}
}
