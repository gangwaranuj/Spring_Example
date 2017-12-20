package com.workmarket.service.business.dto;

public class BuyerIdentityDTO {
	private final Long workId;
	private final Long companyId;
	private final String companyUuid;
	private final String companyNumber;

	public BuyerIdentityDTO(Long workId, Long companyId, String companyUuid, String companyNumber) {
		this.workId = workId;
		this.companyId = companyId;
		this.companyUuid = companyUuid;
		this.companyNumber = companyNumber;

	}

	public Long getWorkId() {
		return workId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public String getCompanyUuid() {
		return companyUuid;
	}

	public String getCompanyNumber() { return companyNumber; }

}

