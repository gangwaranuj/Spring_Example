package com.workmarket.web.forms;

import java.util.Calendar;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public class UserLicenseForm {
	Long id;
	Long userId;	
	String licenseNumber;

	@DateTimeFormat(pattern="MM/dd/yyyy") Calendar issueDate;
	@DateTimeFormat(pattern="MM/dd/yyyy") Calendar expirationDate;

	MultipartFile file;
	
	String action;
	String note;
	
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
	
	public String getLicenseNumber() {
		return licenseNumber;
	}
	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
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
}
