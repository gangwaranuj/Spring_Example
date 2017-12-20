package com.workmarket.service.business.dto;

import com.workmarket.domains.model.directory.ContactContextType;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EmailAddressDTO extends AbstractDTO {
	
	public static final String SPLIT_EMAILS_REGEX = ",|;";
	public static final String JOIN_EMAILS = ", ";

	private String email;
	private ContactContextType contactContextType = ContactContextType.WORK;
	
	public EmailAddressDTO() {}
	public EmailAddressDTO(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
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
	
	public List<EmailAddressDTO> getNewEmailAddressDTOFromString(String subscriptionEmailToString) {
		
		List<EmailAddressDTO> emailDTOs = new ArrayList<>();
		String[] arrayEmails = subscriptionEmailToString.split(EmailAddressDTO.SPLIT_EMAILS_REGEX);

		for(String email : arrayEmails) {
			emailDTOs.add(new EmailAddressDTO(email.trim()));
		}
		
		return emailDTOs;
	}

}
