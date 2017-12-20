package com.workmarket.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by chris on 6/24/12 9:30 PM
 */
public class CritsSecondaryContact {

	private String secondaryContactName;
	private String secondaryContactPhone;
	private String secondaryContactEmail;

	public String getSecondaryContactPhone() {
		return secondaryContactPhone;
	}

	@JsonProperty("secondary_contact_phone")
	public void setSecondaryContactPhone(String secondaryContactPhone) {
		this.secondaryContactPhone = secondaryContactPhone;
	}


	public String getSecondaryContactEmail() {
		return secondaryContactEmail;
	}

	@JsonProperty("secondary_contact_email")
	public void setSecondaryContactEmail(String secondaryContactEmail) {
		this.secondaryContactEmail = secondaryContactEmail;
	}

	public String getSecondaryContactName() {
		return secondaryContactName;
	}

	@JsonProperty("secondary_contact_name")
	public void setSecondaryContactName(String secondaryContactName) {
		this.secondaryContactName = secondaryContactName;
	}
}
