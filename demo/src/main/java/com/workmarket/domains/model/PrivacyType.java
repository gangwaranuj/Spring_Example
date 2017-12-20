package com.workmarket.domains.model;

public enum PrivacyType {
	PRIVATE("Private"),
	PUBLIC("Public"),
	PRIVILEGED("Privileged");

	@SuppressWarnings("unused")
	private String displayName;

	PrivacyType(String displayName) {
		this.displayName = displayName;
	}

	public boolean isPrivate() {
		return PrivacyType.PRIVATE.equals(this);
	}
	
	public boolean isPublic() {
		return PrivacyType.PUBLIC.equals(this);
	}
	
	public boolean isPrivileged() {
		return PrivacyType.PRIVILEGED.equals(this);
	}
}
