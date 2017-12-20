package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ActiveEntity extends ApprovableVerifiableEntity {

	private static final long serialVersionUID = 1L;

	private Boolean active = Boolean.TRUE;

	@Column(name = "active")
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
