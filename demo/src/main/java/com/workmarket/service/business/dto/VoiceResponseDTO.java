package com.workmarket.service.business.dto;

public class VoiceResponseDTO extends VoiceDTO {
   
	private static final long serialVersionUID = 1L;

	private String fromNumber;
	private String callId;
	private String callStatus;
	private String callDuration;
	private String redirectToSubStatus;

	public VoiceResponseDTO() {}

	public String getFromNumber() {
		return fromNumber;
	}
	public void setFromNumber(String fromNumber) {
		this.fromNumber = fromNumber;
	}
	
	public String getCallId() {
		return callId;
	}
	public void setCallId(String callId) {
		this.callId = callId;
	}
	
	public String getCallStatus() {
		return callStatus;
	}
	public void setCallStatus(String callStatus) {
		this.callStatus = callStatus;
	}
	
	public String getCallDuration() {
		return callDuration;
	}
	public void setCallDuration(String callDuration) {
		this.callDuration = callDuration;
	}
	
	public String getRedirectToSubStatus() {
		return redirectToSubStatus;
	}
	public void setRedirectToSubStatus(String redirectToSubStatus) {
		this.redirectToSubStatus = redirectToSubStatus;
	}
}