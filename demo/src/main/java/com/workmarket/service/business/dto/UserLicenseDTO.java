package com.workmarket.service.business.dto;

import java.util.Calendar;

public class UserLicenseDTO {

	private String licenseNumber;
	private Calendar issueDate;
	private boolean requireRemoveOnExpiration;
	private Calendar expirationDate;

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public Calendar getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Calendar issueDate) {
		this.issueDate = issueDate;
	}

	public boolean isRequireRemoveOnExpiration() {
		return requireRemoveOnExpiration;
	}

	public void setRequireRemoveOnExpiration(boolean requireRemoveOnExpiration) {
		this.requireRemoveOnExpiration = requireRemoveOnExpiration;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * Constants for license verification statuses.
	 */
	public enum VERIFICATION_STATUS {
		PENDING, APPROVED, FAILED, ON_HOLD, PENDING_INFORMATION;
	}

}
