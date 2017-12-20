package com.workmarket.service.business.dto;

import com.workmarket.dto.AddressDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ClientCompanyDTO extends AddressDTO {

	private Long clientCompanyId;
	@NotNull
	@Size(min = 3, max = 255)
	private String name;
	private String primaryLocationName;
	private String customerId;
	private String region;
	private String division;
	private Long industryId;
	private String industryName;
	private String website;
	private String phoneNumber;
	private String phoneExtension;

	public Long getClientCompanyId() {
		return clientCompanyId;
	}

	public String getName() {
		return name;
	}

	public String getPrimaryLocationName() {
		return primaryLocationName;
	}

	public void setPrimaryLocationName(String primaryLocationName) {
		this.primaryLocationName = primaryLocationName;
	}

	public void setClientCompanyId(Long clientCompanyId) {
		this.clientCompanyId = clientCompanyId;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPhoneExtension() {
		return phoneExtension;
	}

	public void setPhoneExtension(String phoneExtension) {
		this.phoneExtension = phoneExtension;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ClientCompanyDTO that = (ClientCompanyDTO) o;

		if (name != null ? !name.equals(that.name) : that.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + super.hashCode();
		return result;
	}

	public String getIndustryName() {
		return industryName;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}
}
