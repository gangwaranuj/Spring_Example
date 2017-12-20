package com.workmarket.service.business.dto;

import com.workmarket.domains.model.license.License;
import com.workmarket.utility.BeanUtilities;

public class LicenseDTO {

	private Long licenseId;
	private String name;
	private Boolean deleted = Boolean.FALSE;
	private String state;
	private String licenseNumber;
	private UserLicenseDTO userLicense;

	public static LicenseDTO newDTO(License license) {
		LicenseDTO dto = new LicenseDTO();
		BeanUtilities.copyProperties(dto, license);
		dto.setLicenseId(license.getId());
		return dto;
	}

	public Long getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(Long licenseId) {
		this.licenseId = licenseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public UserLicenseDTO getUserLicense() {
		return userLicense;
	}

	public void setUserLicense(UserLicenseDTO userLicense) {
		this.userLicense = userLicense;
	}
}
