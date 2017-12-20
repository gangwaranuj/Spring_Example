package com.workmarket.service.business.dto;

public class VoiceDTO extends NotificationDTO {
   
	private static final long serialVersionUID = 1L;

	private String toNumber;
	
	public VoiceDTO() {}

	public String getToNumber() {
		return toNumber;
	}
	public void setToNumber(String toNumber) {
		this.toNumber = toNumber;
	}
}