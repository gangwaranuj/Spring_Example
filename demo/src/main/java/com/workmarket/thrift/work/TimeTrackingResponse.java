package com.workmarket.thrift.work;

import com.workmarket.domains.work.model.WorkResourceTimeTracking;

public class TimeTrackingResponse {

	private WorkResourceTimeTracking timeTracking;
	private String message;
	private Boolean successful = Boolean.FALSE;


	public WorkResourceTimeTracking getTimeTracking() {
		return timeTracking;
	}

	public void setTimeTracking(WorkResourceTimeTracking timeTracking) {
		this.timeTracking = timeTracking;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(Boolean success) {
		this.successful = success;
	}
}
