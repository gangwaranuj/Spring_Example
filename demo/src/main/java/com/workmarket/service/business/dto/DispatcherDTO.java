package com.workmarket.service.business.dto;

public class DispatcherDTO {
	private String firstName;
	private String lastName;
	private String email;
	private String workPhone;
	private String mobilePhone;

	public String getFirstName() {
		return firstName;
	}

	public DispatcherDTO setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public DispatcherDTO setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public DispatcherDTO setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public DispatcherDTO setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
		return this;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public DispatcherDTO setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
		return this;
	}
}
