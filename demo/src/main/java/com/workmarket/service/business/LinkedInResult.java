package com.workmarket.service.business;

import java.io.Serializable;

/**
 * User: micah
 * Date: 2/27/13
 * Time: 9:54 AM
 */
public class LinkedInResult implements Serializable {
	public enum Status {
		ERROR, FAILURE, SUCCESS
	}

	private Status status;
	private String message;
	private String linkedInId;
	private String linkedInEmail;
	private String userEmail;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLinkedInId() {
		return linkedInId;
	}

	public void setLinkedInId(String linkedInId) {
		this.linkedInId = linkedInId;
	}

	public String getLinkedInEmail() {
		return linkedInEmail;
	}

	public void setLinkedInEmail(String linkedInEmail) {
		this.linkedInEmail = linkedInEmail;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}
