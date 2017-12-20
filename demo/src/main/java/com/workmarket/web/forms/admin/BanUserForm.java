package com.workmarket.web.forms.admin;

public class BanUserForm {

	private String reason;
	private String bannedUserEmail;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getBannedUserEmail() {
		return bannedUserEmail;
	}

	public void setBannedUserEmail(String bannedUserEmail) {
		this.bannedUserEmail = bannedUserEmail;
	}
}
