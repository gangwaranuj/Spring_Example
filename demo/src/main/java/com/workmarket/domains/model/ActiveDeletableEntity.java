package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ActiveDeletableEntity extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private boolean active = true;

	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
