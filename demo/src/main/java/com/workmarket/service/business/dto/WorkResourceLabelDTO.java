package com.workmarket.service.business.dto;

public class WorkResourceLabelDTO {

	private Long workResourceId;
	private String workResourceLabelTypeCode;
	private boolean confirmed = false;
	private boolean lessThan24HoursFromAppointmentTime = false;

	public WorkResourceLabelDTO() {}

	public WorkResourceLabelDTO(Long workResourceId, String workResourceLabelTypeCode) {
		this.workResourceId = workResourceId;
		this.workResourceLabelTypeCode = workResourceLabelTypeCode;
	}

	public WorkResourceLabelDTO(Long workResourceId, String workResourceLabelTypeCode, boolean confirmed) {
		this.workResourceId = workResourceId;
		this.workResourceLabelTypeCode = workResourceLabelTypeCode;
		this.confirmed = confirmed;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public WorkResourceLabelDTO setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
		return this;
	}

	public Long getWorkResourceId() {
		return workResourceId;
	}

	public WorkResourceLabelDTO setWorkResourceId(Long workResourceId) {
		this.workResourceId = workResourceId;
		return this;
	}

	public String getWorkResourceLabelTypeCode() {
		return workResourceLabelTypeCode;
	}

	public WorkResourceLabelDTO setWorkResourceLabelTypeCode(String workResourceLabelTypeCode) {
		this.workResourceLabelTypeCode = workResourceLabelTypeCode;
		return this;
	}

	public boolean isLessThan24HoursFromAppointmentTime() {
		return lessThan24HoursFromAppointmentTime;
	}

	public WorkResourceLabelDTO setLessThan24HoursFromAppointmentTime(boolean lessThan24HoursFromAppointmentTime) {
		this.lessThan24HoursFromAppointmentTime = lessThan24HoursFromAppointmentTime;
		return this;
	}
}