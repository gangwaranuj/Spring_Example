package com.workmarket.domains.model.account;

import java.util.Map;

public class WeeklyReportRow extends AccountTransactionReportRow {

	private Integer year;
	private Integer currentWeek;
	private Map<Integer, WeekReportDetail> weekDetail;

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getCurrentWeek() {
		return currentWeek;
	}

	public void setCurrentWeek(Integer currentWeek) {
		this.currentWeek = currentWeek;
	}

	public Map<Integer, WeekReportDetail> getWeekDetail() {
		return weekDetail;
	}

	public void setWeekDetail(Map<Integer, WeekReportDetail> weekDetail) {
		this.weekDetail = weekDetail;
	}

	@Override
	public String toString() {
		return "WeeklyReportRow{" +
				"year=" + year +
				", currentWeek=" + currentWeek +
				", weekDetail=" + weekDetail +
				'}';
	}
}
