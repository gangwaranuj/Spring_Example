package com.workmarket.domains.model.summary;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.DateUtilities;

@Entity(name = "timeDimension")
@Table(name = "time_dimension")
public class TimeDimension extends AbstractEntity {

	private static final long serialVersionUID = 8235787948681040468L;

	long year;
	long dayOfMonth;
	long monthOfYear;
	long dayOfYear;
	long hourOfDay;
	long weekOfYear;
	long quarterOfYear;
	Calendar date;
	Calendar endDate;

	@Column(name = "year", nullable = false, length = 11, updatable = false)
	public long getYear() {
		return year;
	}

	public void setYear(long year) {
		this.year = year;
	}

	@Column(name = "day_of_month", nullable = false, length = 11, updatable = false)
	public long getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(long dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	@Column(name = "month_of_year", nullable = false, length = 11, updatable = false)
	public long getMonthOfYear() {
		return monthOfYear;
	}

	public void setMonthOfYear(long monthOfYear) {
		this.monthOfYear = monthOfYear;
	}

	@Column(name = "day_of_year", nullable = false, length = 11, updatable = false)
	public long getDayOfYear() {
		return dayOfYear;
	}

	public void setDayOfYear(long dayOfYear) {
		this.dayOfYear = dayOfYear;
	}

	@Column(name = "hour_of_day", nullable = false, length = 11, updatable = false)
	public long getHourOfDay() {
		return hourOfDay;
	}

	public void setHourOfDay(long hourOfDay) {
		this.hourOfDay = hourOfDay;
	}

	@Column(name = "week_of_year", nullable = false, length = 11, updatable = false)
	public long getWeekOfYear() {
		return weekOfYear;
	}

	public void setWeekOfYear(long weekOfYear) {
		this.weekOfYear = weekOfYear;
	}

	@Column(name = "quarter_of_year", nullable = false, length = 11, updatable = false)
	public long getQuarterOfYear() {
		return quarterOfYear;
	}

	public void setQuarterOfYear(long quarterOfYear) {
		this.quarterOfYear = quarterOfYear;
	}

	// Note: This column is only used for migrations and typically no one writes to this invariant table
	// except in the case of running a test, but I add the setter/getters for that purpose here.
	@Column(name = "end_time_non_summary_table_filter", updatable = false)
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar date) {
		this.endDate = date;
	}

	@Column(name = "date", updatable = false)
	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	/**
	 * Utility method to get the right time_dimension_offset table for
	 * reports that require display data on EST.
	 *
	 * SELECT 	*
	 * FROM 	time_dimension
	 * INNER 	JOIN time_dimension_offset_5hours offsetTable ON offsetTable.id = time_dimension.id;
	 *
	 * @return String the name of the table
	 */
	@Transient
	public static String getOffsetTableForESTTimeZone() {
		if (Math.abs(DateUtilities.getOffsetHoursForTimeZone(Constants.EST_TIME_ZONE)) == 5) {
			return "time_dimension_offset_5hours";
		}
		return "time_dimension_offset_4hours";
	}

}
