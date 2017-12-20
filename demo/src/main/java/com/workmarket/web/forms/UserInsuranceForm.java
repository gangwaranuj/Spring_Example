package com.workmarket.web.forms;

import java.util.Calendar;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public class UserInsuranceForm {
	Long id;
	Long userId;

	String policyNumber;
	
	@DateTimeFormat(pattern="MM/dd/yyyy") Calendar issueDate;
	@DateTimeFormat(pattern="MM/dd/yyyy") Calendar expirationDate;

	String provider;

	String coverage;
	
	MultipartFile file;
	
	String action;
	String note;

	boolean notApplicableOverride;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}		
	
	public String getPolicyNumber() {
		return policyNumber;
	}
	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
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
	
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}	
	
	public String getCoverage() {
		return coverage;
	}
	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}	
	
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}	
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}	

	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	public boolean isNotApplicableOverride() {
		return notApplicableOverride;
	}

	public void setNotApplicableOverride(boolean notApplicableOverride) {
		this.notApplicableOverride = notApplicableOverride;
	}
}
