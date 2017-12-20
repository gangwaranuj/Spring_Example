package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DeletableLookupEntity extends DeletableEntity {

	private static final long serialVersionUID = -8756217632715836693L;
	private String code;
	private String description;

	public DeletableLookupEntity() {
	}

	public DeletableLookupEntity(String code) {
		this.code = code;
	}

	@Column(name = "code", nullable = false, length = 10)
	public String getCode() {
		return code;
	}

	@Column(name = "description", nullable = false, length = 50)
	public String getDescription() {
		return description;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
