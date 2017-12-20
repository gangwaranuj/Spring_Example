package com.workmarket.dto;

/**
 * SuggestionDTO for vendor.
 */
public class VendorSuggestionDTO extends SuggestionDTO {
	private String name;
	private String effectiveName;
	private String companyNumber;
	private String cityStateCountry;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEffectiveName() {
		return effectiveName;
	}

	public void setEffectiveName(String effectiveName) {
		this.effectiveName = effectiveName;
	}

	public String getCompanyNumber() {
		return companyNumber;
	}

	public void setCompanyNumber(String companyNumber) {
		this.companyNumber = companyNumber;
	}

	public String getCityStateCountry() {
		return cityStateCountry;
	}

	public void setCityStateCountry(String cityStateCountry) {
		this.cityStateCountry = cityStateCountry;
	}
}
