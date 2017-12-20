package com.workmarket.search.request.user;

public enum CompanyType {
	Corporation(1, "Corporation"),
	SoleProprietor(2, "Sole Proprietor");

	private final int value;
	private final String description;

	private CompanyType(final int value, final String description) {
		this.value = value;
		this.description = description;
	}

	public int getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public static CompanyType findByValue(final int value) {
		switch (value) {
			case 1:
				return Corporation;
			case 2:
				return SoleProprietor;
			default:
				return null;
		}
	}
}
