package com.workmarket.domains.model.requirementset.resourcetype;

import org.springframework.util.Assert;

public enum ResourceType {
	EMPLOYEE(1L, "Employee"),
	CONTRACTOR(2L, "Contractor");

	ResourceType(Long id, String description) {
		this.id = id;
		this.description = description;
	}

	private Long id;
	private String description;

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public static ResourceType getById(Long id) {
		Assert.notNull(id);
		switch (id.intValue()) {
			case 1: return EMPLOYEE;
			case 2: return CONTRACTOR;
			default: return null;
		}
	}
}
