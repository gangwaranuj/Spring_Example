package com.workmarket.web.forms;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Calendar;

public class CertificationsForm {

	@NotNull
	Long industry;

	@NotNull
	String provider;

	String customProvider;
	String name;

	Long certification;

	String number;
	@NotNull @DateTimeFormat(pattern = "MM/dd/yyyy") Calendar issueDate;
	@DateTimeFormat(pattern = "MM/dd/yyyy") Calendar expirationDate;
	MultipartFile file;

	public Long getIndustry() {
		return industry;
	}

	public void setIndustry(Long industry) {
		this.industry = industry;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getCustomProvider() {
		return customProvider;
	}

	public void setCustomProvider(String customProvider) {
		this.customProvider = customProvider;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCertification() {
		return certification;
	}

	public void setCertification(Long certification) {
		this.certification = certification;
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
