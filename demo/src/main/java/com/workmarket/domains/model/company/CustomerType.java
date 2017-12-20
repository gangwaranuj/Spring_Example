package com.workmarket.domains.model.company;

public enum CustomerType {
	MANAGED("managed"),
	BUYER("buyer"),
	RESOURCE("resource");

	CustomerType(String value) {
		this.value = value;
	}

	private String value;

	public String value() {
		return this.value;
	}
}
