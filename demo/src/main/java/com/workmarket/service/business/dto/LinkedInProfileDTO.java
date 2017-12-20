package com.workmarket.service.business.dto;

import java.util.List;

public class LinkedInProfileDTO extends ProfileDTO {

	private String companyName;
	private String avatarAbsoluteURI;
	private String linkedInId;
	private String industry; 
	private List<EmploymentHistoryDTO> employmentHistory;
	private List<EducationHistoryDTO> educationHistory;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAvatarAbsoluteURI() {
		return avatarAbsoluteURI;
	}

	public void setAvatarAbsoluteURI(String avatarAbsoluteURI) {
		this.avatarAbsoluteURI = avatarAbsoluteURI;
	}

	public String getLinkedInId() {
		return linkedInId;
	}

	public void setLinkedInId(String linkedInId) {
		this.linkedInId = linkedInId;
	}
	
	public String getIndustry() {
		return industry;
	}
	
	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public List<EmploymentHistoryDTO> getEmploymentHistory() {
		return employmentHistory;
	}

	public void setEmploymentHistory(
			List<EmploymentHistoryDTO> employmentHistory) {
		this.employmentHistory = employmentHistory;
	}

	public List<EducationHistoryDTO> getEducationHistory() {
		return educationHistory;
	}

	public void setEducationHistory(List<EducationHistoryDTO> educationHistory) {
		this.educationHistory = educationHistory;
	}

}