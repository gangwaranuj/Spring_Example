package com.workmarket.service.business.event.reports;

import com.workmarket.service.business.event.Event;

public class DownloadCertificatesEvent extends Event {
	Long groupId;
	String toEmail;
	String screeningType;

	public DownloadCertificatesEvent(String toEmail,Long groupId,String screeningType) {
		this.groupId = groupId;
		this.toEmail = toEmail;
		this.screeningType = screeningType;

	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getScreeningType() {
		return screeningType;
	}

	public void setScreeningType(String screeningType) {
		this.screeningType = screeningType;
	}
}
