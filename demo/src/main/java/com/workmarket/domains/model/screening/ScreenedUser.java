package com.workmarket.domains.model.screening;

import java.util.Calendar;

public class ScreenedUser {

	private Long id;
	private String firstName;
	private String lastName;
	private Long companyId;
	private String companyName;
	private String backgroundCheckStatus;
	private String drugTestStatus;
	private String creditCheckStatus;
	private Calendar backgroundCheckRequestDate;
	private Calendar drugTestRequestDate;
	private Calendar creditCheckRequestDate;
	private String userNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBackgroundCheckStatus() {
		return backgroundCheckStatus;
	}

	public void setBackgroundCheckStatus(String backgroundCheckStatus) {
		this.backgroundCheckStatus = backgroundCheckStatus;
	}

	public String getDrugTestStatus() {
		return drugTestStatus;
	}

	public void setDrugTestStatus(String drugTestStatus) {
		this.drugTestStatus = drugTestStatus;
	}

	public String getCreditCheckStatus() {
		return creditCheckStatus;
	}

	public void setCreditCheckStatus(String creditCheckStatus) {
		this.creditCheckStatus = creditCheckStatus;
	}

	public Calendar getBackgroundCheckRequestDate() {
		return backgroundCheckRequestDate;
	}

	public void setBackgroundCheckRequestDate(
			Calendar backgroundCheckRequestDate) {
		this.backgroundCheckRequestDate = backgroundCheckRequestDate;
	}

	public Calendar getDrugTestRequestDate() {
		return drugTestRequestDate;
	}

	public void setDrugTestRequestDate(Calendar drugTestRequestDate) {
		this.drugTestRequestDate = drugTestRequestDate;
	}

	public Calendar getCreditCheckRequestDate() {
		return creditCheckRequestDate;
	}

	public void setCreditCheckRequestDate(Calendar creditCheckRequestDate) {
		this.creditCheckRequestDate = creditCheckRequestDate;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getUserNumber() {
		return userNumber;
	}

}
