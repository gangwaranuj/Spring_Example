package com.workmarket.service.business.dto;

public class NotificationPreferenceDTO {
	
	private String notificationTypeCode;
	private Boolean emailFlag;
	private Boolean followFlag;
	private Boolean bullhornFlag;
	private Boolean pushFlag;
	private Boolean smsFlag;
	private Boolean voiceFlag;
	private Integer days;
	
	public NotificationPreferenceDTO() {}
	public NotificationPreferenceDTO(String notificationTypeCode, Boolean emailFlag, Boolean followFlag,  Boolean bullhornFlag, Boolean pushFlag, Boolean smsFlag, Boolean voiceFlag) {
		this.notificationTypeCode = notificationTypeCode;
		this.emailFlag = emailFlag;
		this.followFlag = followFlag;
		this.bullhornFlag = bullhornFlag;
		this.pushFlag = pushFlag;
		this.smsFlag = smsFlag;
		this.voiceFlag = voiceFlag;
	}
	public NotificationPreferenceDTO(String notificationTypeCode, Integer days) {
		this.notificationTypeCode = notificationTypeCode;
		this.days = days;
	}

	public String getNotificationTypeCode() {
		return notificationTypeCode;
	}

	public void setNotificationTypeCode(String notificationTypeCode) {
		this.notificationTypeCode = notificationTypeCode;
	}
	
	public Boolean getEmailFlag() {
		return emailFlag;
	}

	public void setEmailFlag(Boolean emailFlag) {
		this.emailFlag = emailFlag;
	}

	public Boolean getFollowFlag() {
		return followFlag;
	}

	public void setFollowFlag(Boolean followFlag) {
		this.followFlag = followFlag;
	}

	public Boolean getBullhornFlag() {
		return bullhornFlag;
	}

	public void setBullhornFlag(Boolean bullhornFlag) {
		this.bullhornFlag = bullhornFlag;
	}

	public Boolean getPushFlag() {
		return pushFlag;
	}

	public void setPushFlag(Boolean pushFlag) {
		this.pushFlag = pushFlag;
	}
	
	public Boolean getSmsFlag() {
		return smsFlag;
	}

	public void setSmsFlag(Boolean smsFlag) {
		this.smsFlag = smsFlag;
	}
	
	public Boolean getVoiceFlag() {
		return voiceFlag;
	}

	public void setVoiceFlag(Boolean voiceFlag) {
		this.voiceFlag = voiceFlag;
	}
	
	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}
}