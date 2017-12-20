package com.workmarket.common.template.sms;

public class WorkResourceConfirmationSMSTemplate extends SMSTemplate {
	private static final long serialVersionUID = 2727352084444329794L;

	String workTitle;
	String workShortUrl;
	String workRelativeURI;

	public WorkResourceConfirmationSMSTemplate(Long providerId, String toNumber, String message, String workTitle,
		String workShortUrl, String workRelativeURI) {
		super(providerId, toNumber);
		this.workTitle = workTitle;
		this.workShortUrl = workRelativeURI;
		this.workRelativeURI = workRelativeURI;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getWorkShortUrl() {
		return workShortUrl;
	}

	public void setWorkShortUrl(String workShortUrl) {
		this.workShortUrl = workShortUrl;
	}

	public String getWorkRelativeURI() {
		return workRelativeURI;
	}

	public void setWorkRelativeURI(String workRelativeURI) {
		this.workRelativeURI = workRelativeURI;
	}
}
