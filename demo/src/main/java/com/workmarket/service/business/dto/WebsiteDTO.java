package com.workmarket.service.business.dto;

import org.apache.commons.lang.StringUtils;

import com.workmarket.domains.model.directory.ContactContextType;

public class WebsiteDTO extends AbstractDTO {

	private String website;	
	private ContactContextType contactContextType = ContactContextType.WORK;

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
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

}
