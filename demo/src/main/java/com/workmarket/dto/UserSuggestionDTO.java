package com.workmarket.dto;

public class UserSuggestionDTO extends SuggestionDTO {

	private String email;
	private String companyName;
	private String userNumber;
	private String cityStateCountry;

	public String getCityStateCountry() {
		return cityStateCountry;
	}

	public void setCityStateCountry(String cityStateCountry) {
		this.cityStateCountry = cityStateCountry;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyName() {
		return companyName;
	}
}
