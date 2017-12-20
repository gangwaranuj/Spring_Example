package com.workmarket.service.business.dto;

public class CompanyIdentityDTO {

	private final Long companyId;
	private final String companyNumber;
	private final String uuid;

	public CompanyIdentityDTO(Long companyId, String companyNumber, String uuid) {
		this.companyId = companyId;
		this.companyNumber = companyNumber;
		this.uuid = uuid;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public String getCompanyNumber() {
		return companyNumber;
	}

	public String getUuid() {
		return uuid;
	}
}
