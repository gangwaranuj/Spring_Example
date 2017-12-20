package com.workmarket.domains.model;

public enum WorkProperties {

	DESCRIPTION("description"),
	INSTRUCTIONS("instructions"),
	RESOLUTION("resolution"),
	EXTERNAL_ID("externalId");

	private final String fieldName;

	private WorkProperties(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getName() {
		return this.fieldName;
	}
}
