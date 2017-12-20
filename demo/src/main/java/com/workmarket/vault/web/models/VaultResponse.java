package com.workmarket.vault.web.models;

public class VaultResponse {
	private final String value;

	public VaultResponse(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
