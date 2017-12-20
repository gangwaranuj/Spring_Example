package com.workmarket.web.forms;

import java.util.Calendar;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;

public class LicensesForm {
	
	@NotEmpty
	String state;
	
	@NotEmpty
	String name;

	String license;

	@Pattern(regexp="^[^<>\"]*$", message="License number may not contain (<,>,\"")
	String number;

	@DateTimeFormat(pattern="MM/dd/yyyy")
	Calendar issueDate;
	@DateTimeFormat(pattern="MM/dd/yyyy")
	Calendar expirationDate;

	MultipartFile file;
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	} 	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	} 
	
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}		
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
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
}
