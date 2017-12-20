package com.workmarket.service.business.dto;

import com.workmarket.dto.AddressDTO;
import com.workmarket.utility.StringUtilities;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class ScreeningDTO extends AddressDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long screeningId;
	private String title;
	private String firstName;
	private String middleName;
	private String lastName;
	private String maidenName;
	private String gender;
	private PhoneNumberDTO phone;
	private PhoneNumberDTO mobilePhone;
	private Integer birthDay;
	private Integer birthMonth;
	private Integer birthYear;
	private String dateOfBirth;
	private String workIdentificationNumber;
	private String email;
	private String referenceId;
	private String screeningType; // Used for validator-specific rules
	private String vendorName;

	public Long getScreeningId() {
		return screeningId;
	}
	public void setScreeningId(Long screeningId) {
		this.screeningId = screeningId;
	}

	@NotEmpty
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@NotEmpty
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMaidenName() {
		return maidenName;
	}
	public void setMaidenName(String maidenName) {
		this.maidenName = maidenName;
	}

	//@NotEmpty
	public Integer getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(Integer birthDay) {
		this.birthDay = birthDay;
	}

	//@NotEmpty
	public Integer getBirthMonth() {
		return birthMonth;
	}
	public void setBirthMonth(Integer birthMonth) {
		this.birthMonth = birthMonth;
	}

	//@NotEmpty
	public Integer getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	public String getDateOfBirth() {
		if (dateOfBirth == null) {
			return String.format("%04d-%02d-%02d", birthYear, birthMonth, birthDay);
		}
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getWorkIdentificationNumber() {
		return workIdentificationNumber;
	}
	public void setWorkIdentificationNumber(String workIdentificationNumber) {
		this.workIdentificationNumber = workIdentificationNumber;
	}

	@NotEmpty
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getScreeningType() {
		return screeningType;
	}
	public void setScreeningType(String screeningType) {
		this.screeningType = screeningType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public PhoneNumberDTO getPhone() {
		return phone;
	}

	public void setPhone(PhoneNumberDTO phone) {
		this.phone = phone;
	}

	public PhoneNumberDTO getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(PhoneNumberDTO mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
}
