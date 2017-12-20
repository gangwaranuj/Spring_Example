package com.workmarket.service.business.dto;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.linkedin.LinkedInPerson;
import com.workmarket.domains.model.linkedin.LinkedInPosition;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;

import java.util.Calendar;
import java.util.List;

public class EmploymentHistoryDTO {

	private Long employmentHistoryId;
	private String companyName;
	private String title;
	private String description;
	private Calendar dateFrom;
	private Long dateFromYear;
	private Long dateFromMonth;
	private Calendar dateTo;
	private Long dateToYear;
	private Long dateToMonth;
	private Boolean current = Boolean.FALSE;

	public EmploymentHistoryDTO() {
	}

	public EmploymentHistoryDTO(LinkedInPosition position) {
		if (position != null) {
			if (position.getCompany() != null) {
				this.companyName = position.getCompany().getName();
			}
			this.title = position.getTitle();
			this.description = position.getSummary();
			if (position.getStartDate() != null && position.getStartDate().getYear() != null) {
				this.dateFrom = DateUtilities.newCalendar(position.getStartDate().getYear().intValue(), NumberUtilities.intValueNullSafe(position.getStartDate().getMonth()), 1, 0, 0, 0);
				this.dateFromYear = position.getStartDate().getYear();
				this.dateFromMonth = position.getStartDate().getMonth();
			}
			if (position.getEndDate() != null && position.getEndDate().getYear() != null) {
				this.dateTo = DateUtilities.newCalendar(position.getEndDate().getYear().intValue(), NumberUtilities.intValueNullSafe(position.getEndDate().getMonth()), 1, 0, 0, 0);
				this.dateToYear = position.getEndDate().getYear();
				this.dateToMonth = position.getEndDate().getMonth();
			}
			this.current = position.getCurrent();
		}
	}

	public static List<EmploymentHistoryDTO> getEmploymentHistory(LinkedInPerson linkedInPerson) {
		List<EmploymentHistoryDTO> list = Lists.newArrayList();

		for (LinkedInPosition position : linkedInPerson.getLinkedInPositions()) {
			list.add(new EmploymentHistoryDTO(position));
		}

		return list;
	}

	public Long getEmploymentHistoryId() {
		return employmentHistoryId;
	}

	public void setEmploymentHistoryId(Long employmentHistoryId) {
		this.employmentHistoryId = employmentHistoryId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Boolean getCurrent() {
		return current;
	}

	public void setCurrent(Boolean current) {
		this.current = current;
	}
}
