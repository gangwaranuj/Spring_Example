package com.workmarket.service.business.dto;

import java.util.Calendar;

public class UserCertificationDTO {

	private String certificationNumber;
	private Calendar issueDate;
	private boolean requireRemoveOnExpiration;
	private Calendar expirationDate;

	public String getCertificationNumber() {
		return certificationNumber;
	}

	public void setCertificationNumber(String certificationNumber) {
		this.certificationNumber = certificationNumber;
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
}
