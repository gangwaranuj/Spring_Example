package com.workmarket.service.business.dto;

import com.workmarket.utility.StringUtilities;

public class ClientCompanySearchDTO {

	private Long clientId;
	private String clientName;
	private Long companyId;
	private String companyName;
	private Long locationId;
	private Boolean locationPrimary;
	private String locationName;
	private Long contactId;
	private String contactFirstName;
	private String contactLastName;
	private Boolean contactPrimary;

	public Long getClientId() {
		return clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public Boolean getLocationPrimary() {
		return locationPrimary;
	}

	public String getLocationName() {
		return locationName;
	}
	
	public Long getContactId() {
		return contactId;
	}
	
	public String getContactFirstName() {
		return contactFirstName;
	}

	public String getContactLastName() {
		return contactLastName;
	}
	
	public String getContactFullName() {
		return StringUtilities.fullName(contactFirstName, contactLastName);
	}

	public Boolean getContactPrimary() {
		return contactPrimary;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setLocationPrimary(Boolean locationPrimary) {
		this.locationPrimary = locationPrimary;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	
	public void setContactFirstName(String contactFirstName) {
		this.contactFirstName = contactFirstName;
	}

	public void setContactLastName(String contactLastName) {
		this.contactLastName = contactLastName;
	}

	public void setContactPrimary(Boolean contactPrimary) {
		this.contactPrimary = contactPrimary;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public Long getLocationId() {
		return locationId;
	}

}
