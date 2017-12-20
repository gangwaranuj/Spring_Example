package com.workmarket.service.business.dto;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public class UserImportDTO implements Serializable {

	private static final long serialVersionUID = 8266878216017983055L;

	private String firstName;
	private String lastName;
	private String email;
	private String jobTitle;
	private String workPhone;
	private String workPhoneExtension;
	private String workPhoneInternationalCode;
	private String role;
	private String error;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getWorkPhoneExtension() {
		return workPhoneExtension;
	}

	public void setWorkPhoneExtension(String workPhoneExtension) {
		this.workPhoneExtension = workPhoneExtension;
	}

	public String getWorkPhoneInternationalCode() {
		return workPhoneInternationalCode;
	}

	public void setWorkPhoneInternationalCode(String workPhoneInternationalCode) {
		this.workPhoneInternationalCode = workPhoneInternationalCode;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isEmpty() {
		return StringUtils.isEmpty(firstName) &&
			StringUtils.isEmpty(lastName) &&
			StringUtils.isEmpty(email) &&
			StringUtils.isEmpty(jobTitle) &&
			StringUtils.isEmpty(workPhone) &&
			StringUtils.isEmpty(workPhoneExtension) &&
			StringUtils.isEmpty(workPhoneInternationalCode) &&
			StringUtils.isEmpty(role);
	}

	public String[] toCSVRow() {
		return new String[]{
			firstName,
			lastName,
			email,
			workPhone,
			workPhoneExtension,
			workPhoneInternationalCode,
			jobTitle,
			role,
			error == null ? "" : error
		};
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UserImportDTO)) {
			return false;
		}
		UserImportDTO that = (UserImportDTO) o;
		return Objects.equals(firstName == null ? firstName : firstName.toLowerCase(), that.firstName == null ? that.firstName : that.firstName.toLowerCase()) &&
			Objects.equals(lastName == null ? lastName : lastName.toLowerCase(), that.lastName == null ? that.lastName : that.lastName.toLowerCase()) &&
			Objects.equals(email == null ? email : email.toLowerCase(), that.email == null ? that.email : that.email.toLowerCase()) &&
			Objects.equals(jobTitle == null ? jobTitle : jobTitle.toLowerCase(), that.jobTitle == null ? that.jobTitle : that.jobTitle.toLowerCase()) &&
			Objects.equals(workPhone == null ? workPhone : workPhone.toLowerCase(), that.workPhone == null ? that.workPhone : that.workPhone.toLowerCase()) &&
			Objects.equals(workPhoneExtension == null ? workPhoneExtension : workPhoneExtension.toLowerCase(),
				that.workPhoneExtension == null ? that.workPhoneExtension : that.workPhoneExtension.toLowerCase()) &&
			Objects.equals(workPhoneInternationalCode == null ? workPhoneInternationalCode : workPhoneInternationalCode.toLowerCase(),
				that.workPhoneInternationalCode == null ? that.workPhoneInternationalCode : that.workPhoneInternationalCode.toLowerCase())&&
			Objects.equals(role == null ? role : role.toLowerCase(), that.role == null ? that.role : that.role.toLowerCase());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			firstName == null ? firstName : firstName.toLowerCase(),
			lastName == null ? lastName : lastName.toLowerCase(),
			email == null ? email : email.toLowerCase(),
			jobTitle == null ? jobTitle : jobTitle.toLowerCase(),
			workPhone == null ? workPhone : workPhone.toLowerCase(),
			workPhoneExtension == null ? workPhoneExtension : workPhoneExtension.toLowerCase(),
			workPhoneInternationalCode == null ? workPhoneInternationalCode : workPhoneInternationalCode.toLowerCase(),
			role == null ? role : role.toLowerCase());
	}

	@Override
	public String toString() {
		return org.apache.commons.lang3.StringUtils.join(toCSVRow(), ",");
	}
}
