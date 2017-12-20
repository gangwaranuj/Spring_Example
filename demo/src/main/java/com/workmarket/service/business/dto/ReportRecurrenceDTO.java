package com.workmarket.service.business.dto;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.reporting.ReportRecurrence;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by nick on 7/31/12 11:35 AM
 */
public class ReportRecurrenceDTO implements Serializable {

	public static final String DAILY = "daily";
	public static final String WEEKLY = "weekly";
	public static final String MONTHLY = "monthly";

	private Long reportKey;

	private Long companyId;

	private Long userId;

	private Boolean recurrenceEnabledFlag = Boolean.FALSE;

	private String recurrenceType = DAILY;

	private Boolean dailyWeekdaysOnlyFlag = Boolean.TRUE;

	private Set<Integer> weeklyDays = Sets.newHashSet();

	private Boolean monthlyUseDayOfMonthFlag = Boolean.TRUE;  // false means it will use the ordinal ("4th Thursday") style
	private Integer monthlyFrequencyDay = 15;                 // specific day of month, when monthlyUseDayOfMonthFlag is true
	private Integer monthlyFrequencyWeekday = 1;              // else, use day of week...
	private Integer monthlyFrequencyWeekdayOrdinal = 1;       // ... with 1st, 2nd, 3rd, 4th ordinal

	private Boolean timeMorningFlag = Boolean.TRUE;

	private Set<String> recipients = Sets.newHashSet();

	private String timeZoneId = null;

	public ReportRecurrenceDTO() {
	}

	public ReportRecurrenceDTO(ReportRecurrence reportRecurrence) {
		setFromReportingRecurrence(reportRecurrence);
	}

	public ReportRecurrence copyToReportRecurrence(ReportRecurrence reportRecurrence) {
		BeanUtils.copyProperties(this, reportRecurrence, new String[]{"recipients", "weeklyDays"});
		reportRecurrence.setWeeklyDaysFromSet(getWeeklyDays());

		for (String recipient : getRecipients())
			reportRecurrence.getRecipients().add(new Email(recipient, ContactContextType.WORK));

		// calculate time slot (0..23)
		int reportHourUTC = (getTimeMorningFlag() ? ReportRecurrence.MORNING_REPORT_TIME_HOUR : ReportRecurrence.EVENING_REPORT_TIME_HOUR);

		int offsetHours = DateUtilities.getOffsetHoursForTimeZone(MoreObjects.firstNonNull(
			TimeZone.getTimeZone(getTimeZoneId()),
			TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE)));

		// we want (offset +/- report hour) modulo 24. result should be 0..23
		reportRecurrence.setReportHour((reportHourUTC - offsetHours) % 24);
		if (reportRecurrence.getReportHour() < 0)
			reportRecurrence.setReportHour(24 + reportRecurrence.getReportHour());
		return reportRecurrence;
	}

	@SuppressWarnings("unchecked")
	public void setFromReportingRecurrence(ReportRecurrence reportRecurrence) {
		if (reportRecurrence == null) return;
		BeanUtils.copyProperties(reportRecurrence, this, new String[]{"recipients", "weeklyDays"});
		weeklyDays = reportRecurrence.getWeeklyDaysSet();
		recipients = CollectionUtilities.newSetPropertyProjection(reportRecurrence.getRecipients(), "email");
		timeZoneId = reportRecurrence.getTimeZone().getTimeZoneId();
	}

	public Long getReportKey() {
		return reportKey;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setReportKey(Long reportKey) {
		this.reportKey = reportKey;
	}

	public Boolean getRecurrenceEnabledFlag() {
		return recurrenceEnabledFlag;
	}

	public void setRecurrenceEnabledFlag(Boolean recurrenceEnabledFlag) {
		this.recurrenceEnabledFlag = recurrenceEnabledFlag;
	}

	public String getRecurrenceType() {
		return recurrenceType;
	}

	public void setRecurrenceType(String recurrenceType) {
		this.recurrenceType = recurrenceType;
	}

	public Boolean getDailyWeekdaysOnlyFlag() {
		return dailyWeekdaysOnlyFlag;
	}

	public void setDailyWeekdaysOnlyFlag(Boolean dailyWeekdaysOnlyFlag) {
		this.dailyWeekdaysOnlyFlag = dailyWeekdaysOnlyFlag;
	}

	public Set<Integer> getWeeklyDays() {
		return weeklyDays;
	}

	public void setWeeklyDays(Set<Integer> weeklyDays) {
		this.weeklyDays = weeklyDays;
	}

	public Boolean getMonthlyUseDayOfMonthFlag() {
		return monthlyUseDayOfMonthFlag;
	}

	public void setMonthlyUseDayOfMonthFlag(Boolean monthlyUseDayOfMonthFlag) {
		this.monthlyUseDayOfMonthFlag = monthlyUseDayOfMonthFlag;
	}

	public Integer getMonthlyFrequencyDay() {
		return monthlyFrequencyDay;
	}

	public void setMonthlyFrequencyDay(Integer monthlyFrequencyDay) {
		this.monthlyFrequencyDay = monthlyFrequencyDay;
	}

	public Integer getMonthlyFrequencyWeekday() {
		return monthlyFrequencyWeekday;
	}

	public void setMonthlyFrequencyWeekday(Integer monthlyFrequencyWeekday) {
		this.monthlyFrequencyWeekday = monthlyFrequencyWeekday;
	}

	public Integer getMonthlyFrequencyWeekdayOrdinal() {
		return monthlyFrequencyWeekdayOrdinal;
	}

	public void setMonthlyFrequencyWeekdayOrdinal(Integer monthlyFrequencyWeekdayOrdinal) {
		this.monthlyFrequencyWeekdayOrdinal = monthlyFrequencyWeekdayOrdinal;
	}

	public Boolean getTimeMorningFlag() {
		return timeMorningFlag;
	}

	public void setTimeMorningFlag(Boolean timeMorningFlag) {
		this.timeMorningFlag = timeMorningFlag;
	}

	public Set<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<String> recipients) {
		this.recipients = recipients;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public boolean isDaily() {
		return DAILY.equals(recurrenceType);
	}

	public boolean isWeekly() {
		return WEEKLY.equals(recurrenceType);
	}

	public boolean isMonthly() {
		return MONTHLY.equals(recurrenceType);
	}
}
