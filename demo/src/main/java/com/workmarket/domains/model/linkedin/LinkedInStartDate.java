package com.workmarket.domains.model.linkedin;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.google.code.linkedinapi.schema.StartDate;
import com.google.code.linkedinapi.schema.impl.StartDateImpl;

@Embeddable
@Access(AccessType.FIELD)
public class LinkedInStartDate extends StartDateImpl implements Serializable, StartDate {
	private static final long serialVersionUID = -1345337517472810608L;
	@Column(name="start_year")
	private Long year;
	@Column(name="start_month")
	private Long month;

	public LinkedInStartDate() {
	}

	public LinkedInStartDate(StartDate startDate) {
		this.year = startDate.getYear();
		this.month = startDate.getMonth();
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		this.year = year;
	}

	public Long getMonth() {
		return month;
	}

	public void setMonth(Long month) {
		this.month = month;
	}
}
