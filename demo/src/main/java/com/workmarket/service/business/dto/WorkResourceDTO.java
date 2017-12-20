package com.workmarket.service.business.dto;

import com.workmarket.utility.StringUtilities;

import java.util.Calendar;

public class WorkResourceDTO {

	private Long workResourceId;
	private Long userId;
	private Long companyId;
	private String workResourceStatusTypeCode;
	private String firstName;
	private String lastName;
	private String companyName;
	private String userNumber;
	private boolean isAssignedToWork;
	private Calendar appointmentFrom;
	private Calendar appointmentThrough;
	private String workPhoneNumber;
	private String workPhoneExtension;
	private String mobilePhoneNumber;

	public Long getWorkResourceId() {
		return workResourceId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getWorkResourceStatusTypeCode() {
		return workResourceStatusTypeCode;
	}

	public void setWorkResourceId(Long workResourceId) {
		this.workResourceId = workResourceId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setWorkResourceStatusTypeCode(String workResourceStatusTypeCode) {
		this.workResourceStatusTypeCode = workResourceStatusTypeCode;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return StringUtilities.fullName(firstName, lastName);
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public boolean isAssignedToWork() {
		return isAssignedToWork;
	}

	public void setAssignedToWork(boolean assignedToWork) {
		isAssignedToWork = assignedToWork;
	}

	public Calendar getAppointmentFrom() {
		return appointmentFrom;
	}

	public void setAppointmentFrom(Calendar appointmentFrom) {
		this.appointmentFrom = appointmentFrom;
	}

	public Calendar getAppointmentThrough() {
		return appointmentThrough;
	}

	public void setAppointmentThrough(Calendar appointmentThrough) {
		this.appointmentThrough = appointmentThrough;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}

	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}

	public String getWorkPhoneNumber() {
		return workPhoneNumber;
	}

	public void setWorkPhoneNumber(String workPhoneNumber) {
		this.workPhoneNumber = workPhoneNumber;
	}

	public String getWorkPhoneExtension() {
		return workPhoneExtension;
	}

	public void setWorkPhoneExtension(String workPhoneExtension) {
		this.workPhoneExtension = workPhoneExtension;
	}
}