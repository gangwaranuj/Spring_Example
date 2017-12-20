package com.workmarket.service.business.dto;


public class ProfileLanguageDTO {

	private Long profileLanguageId;
	private Long languageId;
	private String languageProficiencyTypeCode;

	public Long getProfileLanguageId() {
		return profileLanguageId;
	}
	public Long getLanguageId() {
		return languageId;
	}
	public String getLanguageProficiencyTypeCode() {
		return languageProficiencyTypeCode;
	}
	public void setProfileLanguageId(Long profileLanguageId) {
		this.profileLanguageId = profileLanguageId;
	}
	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}
	public void setLanguageProficiencyTypeCode(String languageProficiencyTypeCode) {
		this.languageProficiencyTypeCode = languageProficiencyTypeCode;
	}
	


}

