package com.workmarket.api.v2.model;

public class GetSignedEsignatureRequestDTO {

	private String templateUuid;
	private String userNumber;

	public GetSignedEsignatureRequestDTO() { }

	public String getTemplateUuid() {
		return templateUuid;
	}

	public void setTemplateUuid(final String templateUuid) {
		this.templateUuid = templateUuid;
	}

	public String getuserNumber() {
		return userNumber;
	}

	public void setuserNumber(final String userNumber) {
		this.userNumber = userNumber;
	}
}
