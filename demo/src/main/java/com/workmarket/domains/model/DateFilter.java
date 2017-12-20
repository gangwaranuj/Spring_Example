package com.workmarket.domains.model;

import java.util.Calendar;

public class DateFilter {

	private Calendar fromDate;
	private Calendar toDate;

	public DateFilter() {}

	public DateFilter(Calendar fromDate, Calendar toDate) {
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	public Calendar getFromDate() {
		return fromDate;
	}

	public void setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
	}

	public Calendar getToDate() {
		return toDate;
	}

	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}

	public boolean isSetFromDate() {
		return fromDate != null;
	}

	public boolean isSetToDate() {
		return toDate != null;
	}
}
