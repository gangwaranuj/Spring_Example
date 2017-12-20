package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.workmarket.domains.model.audit.AuditedEntity;

@MappedSuperclass
public abstract class DeletableEntity extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private Boolean deleted = Boolean.FALSE;

	@Column(name = "deleted", nullable = false)
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
}
