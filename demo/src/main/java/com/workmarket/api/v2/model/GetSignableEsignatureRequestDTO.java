package com.workmarket.api.v2.model;

public class GetSignableEsignatureRequestDTO {

	private String templateUuid;
	private String companyUuid;

	public GetSignableEsignatureRequestDTO() { }

	public String getTemplateUuid() {
		return templateUuid;
	}

	public void setTemplateUuid(final String templateUuid) {
		this.templateUuid = templateUuid;
	}

	public String getCompanyUuid() {
		return companyUuid;
	}

	public void setCompanyUuid(final String companyUuid) {
		this.companyUuid = companyUuid;
	}
}
