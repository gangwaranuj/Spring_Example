package com.workmarket.domains.model.reporting;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

import java.util.Calendar;

public class DailySummaryPagination extends AbstractPagination<DailySummary> implements Pagination<DailySummary> {

	private Calendar fromDate;


	public Calendar getToDate() {
		return toDate;
	}


	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}


	private Calendar toDate;

	public Calendar getFromDate() {
		return fromDate;
	}


	public void setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
	}


	public enum FILTER_KEYS {}

	public enum SORTS {}
}
