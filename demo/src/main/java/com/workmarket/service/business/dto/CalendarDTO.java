package com.workmarket.service.business.dto;

import java.util.Calendar;

public class CalendarDTO {

	private Calendar fromDate;
	private Calendar toDate;
	private String title;
	private String toEmail;
	private String toName;
	private String description;
	private String location;

	public Calendar getFromDate() {
		return fromDate;
	}

	public Calendar getToDate() {
		return toDate;
	}

	public String getTitle() {
		return title;
	}

	public String getToEmail() {
		return toEmail;
	}

	public String getToName() {
		return toName;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}
	
	public void setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

}