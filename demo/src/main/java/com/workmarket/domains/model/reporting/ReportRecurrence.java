package com.workmarket.domains.model.reporting;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.joda.time.DateTimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;

/**
 * Created by nick on 7/12/12 11:47 AM
 */
@javax.persistence.Entity(name = "reportRecurrence")
@Table(name = "report_recurrence")
@NamedQueries({
		// Find Reporting Recurrence by reportKey
		@NamedQuery(
				name = "reportRecurrence.byReportKey",
				query = "FROM reportRecurrence rr WHERE rr.reportKey = :reportKey AND deleted = 0"),
		// Find Reporting Recurrence by time slot
		@NamedQuery(
				name = "reportRecurrence.byReportHour",
				query = "FROM reportRecurrence rr WHERE rr.reportHour = :reportHour AND enabled = 1 AND deleted = 0")
})
@AuditChanges
public class ReportRecurrence extends DeletableEntity {

	private static final long serialVersionUID = -7740683706605127406L;

	public static final String DAILY = "daily";
	public static final String WEEKLY = "weekly";
	public static final String MONTHLY = "monthly";

	// 0h to 23h - these are UTC - each reportHour value is calculated relative to this
	public static final Integer MORNING_REPORT_TIME_HOUR = 4;
	public static final Integer EVENING_REPORT_TIME_HOUR = 22;

	private Long reportKey;

	private Long companyId;

	private Long userId;

	private String recurrenceType = DAILY;               // 0 - daily 1 - weekly 2 - monthly

	private Boolean recurrenceEnabledFlag = Boolean.TRUE;

	private Boolean dailyWeekdaysOnlyFlag;

	private Integer weeklyDays = null;

	private Boolean monthlyUseDayOfMonthFlag;
	private Integer monthlyFrequencyDay;
	private Integer monthlyFrequencyWeekday;
	private Integer monthlyFrequencyWeekdayOrdinal; // between 1 and 4 (1st, 2nd, etc)

	private Boolean timeMorningFlag = Boolean.TRUE;

	private com.workmarket.domains.model.datetime.TimeZone timeZone;

	private Set<Email> recipients = Sets.newHashSet();

	private Integer reportHour = MORNING_REPORT_TIME_HOUR;

	@Column(name = "report_id", nullable = false)
	public Long getReportKey() {
		return reportKey;
	}

	public void setReportKey(Long reportKey) {
		this.reportKey = reportKey;
	}

	@Column(name = "company_id", nullable = false)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Column(name = "user_id", nullable = false)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "enabled", nullable = false)
	public Boolean getRecurrenceEnabledFlag() {
		return recurrenceEnabledFlag;
	}

	public void setRecurrenceEnabledFlag(Boolean recurrenceEnabledFlag) {
		this.recurrenceEnabledFlag = recurrenceEnabledFlag;
	}

	@Column(name = "recurrence_type", nullable = false)
	public String getRecurrenceType() {
		return recurrenceType;
	}

	public void setRecurrenceType(String recurrenceType) {
		this.recurrenceType = recurrenceType;
	}

	@Column(name = "daily_weekdays_only_flag", nullable = false)
	public Boolean getDailyWeekdaysOnlyFlag() {
		return this.dailyWeekdaysOnlyFlag;
	}

	public void setDailyWeekdaysOnlyFlag(Boolean dailyWeekdaysOnlyFlag) {
		this.dailyWeekdaysOnlyFlag = dailyWeekdaysOnlyFlag;
	}

	@Column(name = "weekly_days", nullable = true)
	public Integer getWeeklyDays() {
		return weeklyDays;
	}

	public void setWeeklyDays(Integer weeklyDays) {
		this.weeklyDays = weeklyDays;
	}

	@Column(name = "monthly_use_day_of_month_flag", nullable = false)
	public Boolean getMonthlyUseDayOfMonthFlag() {
		return monthlyUseDayOfMonthFlag;
	}

	public void setMonthlyUseDayOfMonthFlag(Boolean monthlyUseDayOfMonthFlag) {
		this.monthlyUseDayOfMonthFlag = monthlyUseDayOfMonthFlag;
	}

	@Column(name = "monthly_frequency_day", nullable = true)
	public Integer getMonthlyFrequencyDay() {
		return monthlyFrequencyDay;
	}

	public void setMonthlyFrequencyDay(Integer monthlyFrequencyDay) {
		this.monthlyFrequencyDay = monthlyFrequencyDay;
	}

	@Column(name = "monthly_frequency_weekday", nullable = true)
	public Integer getMonthlyFrequencyWeekday() {
		return monthlyFrequencyWeekday;
	}

	public void setMonthlyFrequencyWeekday(Integer monthlyFrequencyWeekday) {
		this.monthlyFrequencyWeekday = monthlyFrequencyWeekday;
	}

	@Column(name = "monthly_frequency_weekday_ordinal", nullable = true)
	public Integer getMonthlyFrequencyWeekdayOrdinal() {
		return monthlyFrequencyWeekdayOrdinal;
	}

	public void setMonthlyFrequencyWeekdayOrdinal(Integer monthlyFrequencyWeekdayOrdinal) {
		this.monthlyFrequencyWeekdayOrdinal = monthlyFrequencyWeekdayOrdinal;
	}

	@Column(name = "time_morning_flag", nullable = false)
	public Boolean getTimeMorningFlag() {
		return timeMorningFlag;
	}

	public void setTimeMorningFlag(Boolean timeMorningFlag) {
		this.timeMorningFlag = timeMorningFlag;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="time_zone_id", referencedColumnName="id")
	public com.workmarket.domains.model.datetime.TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(com.workmarket.domains.model.datetime.TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "report_recurrence_email",
			joinColumns = @JoinColumn(name = "report_recurrence_id"),
			inverseJoinColumns = @JoinColumn(name = "email_id"))
	public Set<Email> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<Email> recipients) {
		this.recipients = recipients;
	}

	@Column(name="report_hour", nullable = false)
	public Integer getReportHour() {
		return reportHour;
	}

	public void setReportHour(Integer reportHour) {
		this.reportHour = reportHour;
	}

	/**
	 * Returns e.g. [1, 2, 6] (Mon, Tues, Sat) for 0100110 (70)
	 * @return
	 */
	@Transient
	public Set<Integer> getWeeklyDaysSet() {
		Set<Integer> result = Sets.newHashSet();

		if (weeklyDays == null) return result;

		for (int i = 0; i < 7; i++) {
			int shift = (1 << i);
			if ((weeklyDays & shift) != 0)
				result.add(i);
		}
		return result;
	}

	/**
	 * does opposite calculation from setWeeklyDaysSet
	 * @param weeklyDays
	 */
	@Transient
	public void setWeeklyDaysFromSet(Set<Integer> weeklyDays) {
		if (CollectionUtilities.isEmpty(weeklyDays)) {
			this.weeklyDays = null;
			return;
		}
		this.weeklyDays = 0;
		for (Integer weekday : weeklyDays) {
			this.weeklyDays = this.weeklyDays | (1 << weekday);
		}
	}

	public String toString() {
		String timeZoneShort = DateTimeZone.forID(timeZone.getTimeZoneId()).getShortName(System.currentTimeMillis());
		String timestamp = timeMorningFlag ?
				String.format("in the morning (%dam %s)", MORNING_REPORT_TIME_HOUR, timeZoneShort) :
				String.format("in the evening (%dpm %s)", EVENING_REPORT_TIME_HOUR - 12, timeZoneShort);

		if (DAILY.equals(recurrenceType)) {
			return dailyWeekdaysOnlyFlag ?
					String.format("daily, except weekends, %s", timestamp) :
					String.format("daily, %s", timestamp);

		} else if (WEEKLY.equals(recurrenceType)) {
			String result = "";
			for (Integer day : getWeeklyDaysSet())
				result += DateUtilities.getReadableDayOfWeek(day) + ", ";
			return result + timestamp;

		} else if (MONTHLY.equals(recurrenceType)) {
			return monthlyUseDayOfMonthFlag ?
					String.format("on the %s of every month, %s", StringUtilities.ordinalize(monthlyFrequencyDay), timestamp) :
					String.format("on the %s %s of every month, %s",
							StringUtilities.ordinalize(monthlyFrequencyWeekdayOrdinal),
							DateUtilities.getReadableDayOfWeek(monthlyFrequencyWeekday),
							timestamp);
		}
		return "";
	}
}
