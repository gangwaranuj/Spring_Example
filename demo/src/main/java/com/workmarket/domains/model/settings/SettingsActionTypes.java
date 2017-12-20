package com.workmarket.domains.model.settings;

public enum SettingsActionTypes {
	OVERVIEW("overview"),
	BANK("bank"),
	FUNDS("funds"),
	TAX("tax"),
	ASSIGNMENT_SETTINGS("assignment_settings");

	private final String code;

	SettingsActionTypes(String code) {
		this.code = code;
	}

	public String code() {
		return this.code;
	}
}
