package com.workmarket.domains.model.linkedin;

import com.google.code.linkedinapi.schema.EndDate;
import com.google.code.linkedinapi.schema.impl.EndDateImpl;
import com.google.gson.annotations.SerializedName;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Access(AccessType.FIELD)
public class LinkedInEndDate extends EndDateImpl implements Serializable, EndDate {
	private static final long serialVersionUID = 1350038963808878560L;

	@Column(name="end_year") @SerializedName("end_year")
	private Long year;
	@Column(name="end_month") @SerializedName("end_month")
	private Long month;

	public LinkedInEndDate() {
	}

	public LinkedInEndDate(EndDate endDate) {
		this.year = endDate.getYear();
		this.month = endDate.getMonth();
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
