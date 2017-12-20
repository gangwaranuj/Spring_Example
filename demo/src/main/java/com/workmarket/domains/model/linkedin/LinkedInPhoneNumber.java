package com.workmarket.domains.model.linkedin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.code.linkedinapi.schema.PhoneNumber;
import com.google.code.linkedinapi.schema.PhoneType;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="linkedInPhoneNumber")
@Table(name="linkedin_phone_number")
@AuditChanges
public class LinkedInPhoneNumber extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private LinkedInPerson linkedInPerson;
	private String phoneNumber;
	private PhoneType phoneType;

	public LinkedInPhoneNumber() {}

	public LinkedInPhoneNumber(PhoneNumber phoneNumber) {
		this.phoneNumber = phoneNumber.getPhoneNumber();

		switch(phoneNumber.getPhoneType()) {
			case HOME: this.phoneType = PhoneType.HOME;
				break;
			case MOBILE: this.phoneType = PhoneType.MOBILE;
				break;
			case WORK: this.phoneType = PhoneType.WORK;
				break;
		}
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "linkedin_person_id")
	public LinkedInPerson getLinkedInPerson() {
		return linkedInPerson;
	}

	public void setLinkedInPerson(LinkedInPerson linkedInPerson) {
		this.linkedInPerson = linkedInPerson;
	}

	@Column(name = "phone_number", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Column(name = "phone_type", length = Constants.LINKEDIN_STRING_FIELD_MAX)
	@Enumerated(value=EnumType.STRING)
	public PhoneType getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(PhoneType phoneType) {
		this.phoneType = phoneType;
	}
}
