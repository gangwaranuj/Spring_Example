package com.workmarket.domains.model;

public enum InvitationType {
	EMPLOYEE("Employee"), CONTRACTOR("Contractor"), EXCLUSIVE("Exclusive");

	private final String code;

	private InvitationType(String code) {
		this.code = code;
	}

	public static InvitationType getEnumFromCode(String code) {
		for (InvitationType g : InvitationType.values()) {
			if (g.toString().equals(code.toUpperCase())) {
				return g;
			}
		}
		return null;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return this.code;
	}
}
