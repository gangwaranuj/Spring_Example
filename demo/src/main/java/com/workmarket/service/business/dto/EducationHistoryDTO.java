package com.workmarket.service.business.dto;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.linkedin.LinkedInEducation;
import com.workmarket.domains.model.linkedin.LinkedInPerson;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;

import java.util.Calendar;
import java.util.List;

public class EducationHistoryDTO {

	private Long educationId;
	private String schoolName;
	private String degree;
	private String fieldOfStudy;
	private String activities;
	private Calendar dateFrom;
	private Long dateFromYear;
	private Long dateFromMonth;
	private Calendar dateTo;
	private Long dateToYear;
	private Long dateToMonth;

	public EducationHistoryDTO() {}

	public EducationHistoryDTO(LinkedInEducation education) {
		this.schoolName = education.getSchoolName();
		this.degree = education.getDegree();
		this.fieldOfStudy = education.getFieldOfStudy();
		this.activities = education.getActivities();
		if(education.getStartDate() != null && education.getStartDate().getYear() != null) {
			this.dateFrom = DateUtilities.newCalendar(education.getStartDate().getYear().intValue(), NumberUtilities.intValueNullSafe(education.getStartDate().getMonth()), 1, 0, 0, 0);
			this.dateFromYear = education.getStartDate().getYear();
			this.dateFromMonth = education.getStartDate().getMonth();
		}

		if(education.getEndDate() != null && education.getEndDate().getYear() != null) {
			this.dateTo = DateUtilities.newCalendar(education.getEndDate().getYear().intValue(), NumberUtilities.intValueNullSafe(education.getEndDate().getMonth()), 1, 0, 0, 0);
			this.dateToYear = education.getEndDate().getYear();
			this.dateToMonth = education.getEndDate().getMonth();
		}
	}

	public static List<EducationHistoryDTO> getEducationHistory(LinkedInPerson linkedInPerson) {
		List<EducationHistoryDTO> list = Lists.newArrayList();

		for (LinkedInEducation education : linkedInPerson.getLinkedInEducation()) {
			list.add(new EducationHistoryDTO(education));
		}

		return list;
	}

	public Long getEducationId() {
		return educationId;
	}

	public void setEducationId(Long educationId) {
		this.educationId = educationId;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getFieldOfStudy() {
		return fieldOfStudy;
	}

	public void setFieldOfStudy(String fieldOfStudy) {
		this.fieldOfStudy = fieldOfStudy;
	}

	public String getActivities() {
		return activities;
	}

	public void setActivities(String activities) {
		this.activities = activities;
	}

	public Calendar getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Calendar dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Long getDateFromYear() {
		return dateFromYear;
	}

	public void setDateFromYear(Long dateFromYear) {
		this.dateFromYear = dateFromYear;
	}

	public Long getDateFromMonth() {
		return dateFromMonth;
	}

	public void setDateFromMonth(Long dateFromMonth) {
		this.dateFromMonth = dateFromMonth;
	}

	public Calendar getDateTo() {
		return dateTo;
	}

	public void setDateTo(Calendar dateTo) {
		this.dateTo = dateTo;
	}

	public Long getDateToYear() {
		return dateToYear;
	}

	public void setDateToYear(Long dateToYear) {
		this.dateToYear = dateToYear;
	}

	public Long getDateToMonth() {
		return dateToMonth;
	}

	public void setDateToMonth(Long dateToMonth) {
		this.dateToMonth = dateToMonth;
	}
}
