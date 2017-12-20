package com.workmarket.service.business.dto;

import com.fasterxml.jackson.annotation.JsonSetter;

import org.apache.commons.lang.StringUtils;

import com.workmarket.domains.model.directory.ContactContextType;

public class PhoneNumberDTO extends AbstractDTO {

	private String phone;
	private String extension;
	private ContactContextType contactContextType = ContactContextType.WORK;

	public PhoneNumberDTO() {}
	public PhoneNumberDTO(String phone, ContactContextType contactContextType) {
		this.phone = phone;
		this.contactContextType = contactContextType;
	}

	public PhoneNumberDTO(String phone, String extension, ContactContextType contactContextType) {
		this.phone = phone;
		this.contactContextType = contactContextType;
		this.extension = extension;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@JsonSetter
	public void setContactContextType(String contactContextType) {
		if (StringUtils.isNotBlank(contactContextType)) {
			this.contactContextType = ContactContextType.valueOf(contactContextType.toUpperCase());
		}
	}

	public ContactContextType getContactContextType() {
		return contactContextType;
	}

	public void setContactContextType(ContactContextType contactContextType) {
		this.contactContextType = contactContextType;
	}

}
