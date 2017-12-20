package com.workmarket.service.business.dto;

public class PushDTO extends NotificationDTO {

	private static final long serialVersionUID = 1L;

	private String message;
	private String regid;
	private String type;

	public PushDTO() {}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRegid() {
		return regid;
	}

	public void setRegid(String regid) {
		this.regid = regid;
	}
}
