package com.workmarket.web.forms;

import java.util.Calendar;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public class InsuranceForm {
	Long insuranceId;

	@Pattern(regexp="^[^<>\"]*$", message="Provider number may not contain (<,>,\"")
	String provider;

	@Pattern(regexp="^[^<>\"]*$", message="Policy Number may not contain (<,>,\"")
	String policyNumber; 

	@Pattern(regexp="^[0-9]*$")
	String coverage;
	
	@DateTimeFormat(pattern="MM/dd/yyyy") Calendar issueDate; 
	@DateTimeFormat(pattern="MM/dd/yyyy") Calendar expirationDate; 
	MultipartFile file;

	boolean notApplicableOverride;

	public Long getInsuranceId() {
		return insuranceId;
	}
	public void setInsuranceId(Long insuranceId) {
		this.insuranceId = insuranceId;
	}	
	
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}	
	
	public String getPolicyNumber() {
		return policyNumber;
	}
	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}		
	
	public String getCoverage() {
		return coverage;
	}
	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}		

	public Calendar getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Calendar issueDate) {
		this.issueDate = issueDate;
	}		

	public Calendar getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}		

	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public boolean hasFile() {
		return file != null && !file.isEmpty();
	}

	public boolean isNotApplicableOverride() {
		return notApplicableOverride;
	}

	public void setNotApplicableOverride(boolean notApplicableOverride) {
		this.notApplicableOverride = notApplicableOverride;
	}
}
