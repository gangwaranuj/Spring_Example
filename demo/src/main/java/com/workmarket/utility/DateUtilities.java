package com.workmarket.utility;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import com.workmarket.configuration.Constants;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class DateUtilities {

	public static final Calendar WM_EPOCH_CALENDAR = getCalendarFromISO8601("2010-01-01");
	public static final long WM_EPOCH = WM_EPOCH_CALENDAR.getTimeInMillis();

	private DateUtilities() {}

	private static final DateTimeFormatter timeFormatter;
	private static final DateTimeFormatter dateFormatter;
	private static final DateTimeFormatter dateTimeFormatter;
	private static final SimpleDateFormat simpleDateFormat;

	private static final Map<Integer, String> months = new HashMap<>();

	private static final Map<Integer, String> daysOfWeek = new ImmutableMap.Builder<Integer, String>()
			.put(DateTimeConstants.MONDAY, "Monday")
			.put(DateTimeConstants.TUESDAY, "Tuesday")
			.put(DateTimeConstants.WEDNESDAY, "Wednesday")
			.put(DateTimeConstants.THURSDAY, "Thursday")
			.put(DateTimeConstants.FRIDAY, "Friday")
			.put(DateTimeConstants.SATURDAY, "Saturday")
			.put(DateTimeConstants.SUNDAY, "Sunday")
			.build();

	/**
	 * the static function is used to make the date formatter that'll be used
	 *
	 * @param patterns
	 * @return
	 */
	private static DateTimeFormatter createDateTimeFormatterFromPatterns(String[] patterns) {

		DateTimeParser[] parsers = new DateTimeParser[patterns.length];
		for (int i = 0; i < parsers.length; i++) {
			parsers[i] = DateTimeFormat.forPattern(patterns[i]).getParser();
		}
		return new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
	}

	static {
		months.put(1, "January");
		months.put(2, "February");
		months.put(3, "March");
		months.put(4, "April");
		months.put(5, "May");
		months.put(6, "June");
		months.put(7, "July");
		months.put(8, "August");
		months.put(9, "September");
		months.put(10, "October");
		months.put(11, "November");
		months.put(12, "December");

		String[] ACCEPTED_TIME_FORMATS = new String[] {
				"h aa",
				"h:mm aa",
				"h:mm:ss aa",
				"h:mm:ss:SS aa",
				"haa",
				"h:mmaa",
				"h:mm:ssaa",
				"h:mm:ss:SSaa",
				"H",
				"H:mm",
				"H:mm:ss",
				"H:mm:ss:SS"
		};
		timeFormatter = createDateTimeFormatterFromPatterns(ACCEPTED_TIME_FORMATS);

		String[] ACCEPTED_DATE_FORMATS = new String[] {
				"MM/dd/yy",
				"MM-dd-yy",
				"MM/dd/yyyy",
				"yyyy-MM-dd",
				"yyyy/MM/dd",
				"dd-MMM",
				"MM/dd",
				"yyyy-MM-dd HH:mm:ss"
		};
		dateFormatter = createDateTimeFormatterFromPatterns(ACCEPTED_DATE_FORMATS);
		simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

		// Build all permutations of accepted date and time formats

		List<String> formats = Lists.newArrayListWithCapacity(ACCEPTED_TIME_FORMATS.length * ACCEPTED_DATE_FORMATS.length);
		for (String date : ACCEPTED_DATE_FORMATS) {
			for (String time : ACCEPTED_TIME_FORMATS) {
				formats.add(String.format("%s %s", date, time));
			}
		}
		dateTimeFormatter = createDateTimeFormatterFromPatterns(formats.toArray(new String[formats.size()]));
	}

	static String[] WEEKDAY_NAMES = new DateFormatSymbols().getWeekdays();

	//Work has to be in the current fiscal year
	public static long getWorkBackDateThreshold() {
		return getCalendarWithFirstDayOfYear(Calendar.getInstance()).getTimeInMillis();

	}
	// Parsing

	public static Calendar setSpecificTime(Calendar calendar, Calendar time){
		Calendar c = Calendar.getInstance();
		c.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
		c.setTimeZone(time.getTimeZone());
		return c;
	}

	public static Calendar getCalendarInUTC(Calendar calendar) {
		if (calendar == null)
			return null;
		if (calendar.getTimeZone().equals(java.util.TimeZone.getTimeZone("UTC"))) {
			return calendar;
		}
		DateTime dt = new DateTime(calendar, DateTimeZone.UTC);
		return dt.toGregorianCalendar();
	}

	public static Calendar getCalendarFromISO8601(String iso8601) {
		if (StringUtils.isEmpty(iso8601))
			return null;
		DateTime dt = new DateTime(iso8601);
		return dt.toCalendar(Locale.ENGLISH);
	}

	public static String format(String format, String iso8601, String timeZoneId) {
		DateTime dt;
		try {
			dt = new DateTime(iso8601, DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneId)));
		} catch (IllegalArgumentException e) {
			return null;
		}

		return format(format, dt);
	}

	public static Date getDateFromISO8601(String iso8601) {
		if (StringUtils.isEmpty(iso8601))
			return null;
		DateTime dt = new DateTime(iso8601);
		return dt.toDate();
	}

	public static Calendar getCalendarFromString(String s) {
		if (StringUtils.isEmpty(s)) {
			return null;
		}
		DateTime dt = new DateTime(s);
		return dt.toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarFromString(String dateStr, String formatStr, String timeZoneId) throws java.text.ParseException {
		Assert.notNull(dateStr);
		Assert.notNull(formatStr);
		Assert.notNull(timeZoneId);
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		Date date = sdf.parse(dateStr);
		long time = date.getTime();
		TimeZone tz = TimeZone.getTimeZone(timeZoneId);
		if (tz != null) {
			time += tz.getRawOffset();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar;
	}

	public static Calendar getCalendarFromDateTimeString(String dateTimeString, String defaultTimeZone) {
		DateTime dt = getDateTimeFromDateTimeString(dateTimeString, defaultTimeZone);
		return (dt != null) ? dt.toGregorianCalendar() : null;
	}

	public static Calendar getCalendarFromDateTimeString(String dateString, String timeString, String defaultTimeZone) {
		if (dateString == null || timeString == null)
			return null;
		DateTime date = getDateTimeFromDateString(StringUtils.deleteWhitespace(dateString), defaultTimeZone);
		if (date == null)
			return null;
		DateTime time = getDateTimeFromTimeString(StringUtils.deleteWhitespace(timeString), defaultTimeZone);
		if (time != null) {
			int minuteOfDay = time.getMinuteOfDay();
			date = date.withHourOfDay(minuteOfDay / 60);
			date = date.withMinuteOfHour(minuteOfDay % 60);
			return date.toGregorianCalendar();
		}
		Calendar c = date.toGregorianCalendar();
		// Unset hour field to indictate time is unparseable
		c.clear(Calendar.HOUR);
		return c;
	}

	public static String formatDateStringToISO8601(String dateString) {
		try {
			DateTime dt = dateFormatter.parseDateTime(dateString);
			if (dt.getYear() == 1970) {
				dt = dt.plusYears(new DateTime().getYear() - 1970);
				if (dt.isBeforeNow())
					dt = dt.plusYears(1);
			}

			return dt.toString();
		} catch (IllegalArgumentException lae) {
			return null;
		}
	}

	public static Calendar getCalendarFromDateString(String dateString, String defaultTimeZone) {
		if (!StringUtilities.all(dateString, defaultTimeZone)) return null;
		DateTime dt = getDateTimeFromDateString(dateString, defaultTimeZone);
		return (dt != null) ? dt.toGregorianCalendar() : null;
	}

	public static Calendar getCalendarFromTimeString(String timeString, String defaultTimeZone) {
		DateTime dt = getDateTimeFromTimeString(timeString, defaultTimeZone);
		return (dt != null) ? dt.toGregorianCalendar() : null;
	}

	private static DateTime getDateTimeFromDateTimeString(String dateTimeString, String defaultTimeZone) {
		try {
			return (dateTimeFormatter.withZone(DateTimeZone.forID(defaultTimeZone))).parseDateTime(dateTimeString);
		} catch (IllegalArgumentException lae) {
			return null;
		}
	}

	private static DateTime getDateTimeFromTimeString(String timeString, String defaultTimeZone) {
		try {
			return (timeFormatter.withZone(DateTimeZone.forID(defaultTimeZone))).parseDateTime(timeString);
		} catch (IllegalArgumentException lae) {
			return null;
		}
	}

	private static DateTime getDateTimeFromDateString(String dateString, String defaultTimeZone) {
		try {
			DateTime response = dateFormatter.withZone(DateTimeZone.forID(defaultTimeZone)).parseDateTime(dateString);
			if (response.getYear() == 1970) {
				response = response.plusYears(new DateTime().getYear() - 1970);
				if (response.isBeforeNow())
					response = response.plusYears(1);
			}
			return response;
		} catch (IllegalArgumentException lae) {
			return null;
		}
	}

	// Formatting

	public static String getISO8601(Date date) {
		if (date == null)
			return null;
		DateTime dt = new DateTime(date, DateTimeZone.UTC);
		return dt.toString();
	}

	public static String getISO8601(Calendar calendar) {
		if (calendar == null)
			return null;
		DateTime dt = new DateTime(calendar, DateTimeZone.UTC);
		return dt.toString();
	}

	public static String getISO8601(Long milliseconds) {
		if (milliseconds == null)
			return null;
		DateTime dt = new DateTime(milliseconds, DateTimeZone.UTC);
		return dt.toString();
	}

	public static String getISO8601WithSpaces(Calendar calendar, TimeZone timeZone) {
		if (calendar == null)
			return null;
		DateTime dt = new DateTime(calendar, DateTimeZone.forTimeZone(timeZone));
		return format("yyyy-MM-dd HH:mm:ss", dt);
	}

	public static String getISO8601WithSpaces(Calendar calendar, String timeZoneId) {
		return getISO8601WithSpaces(calendar, TimeZone.getTimeZone(timeZoneId));
	}

	public static String format(String format, String iso8601) {
		DateTime dt = new DateTime(iso8601);
		return format(format, dt);
	}

	public static String format(String format, Date date) {
		DateTime dt = new DateTime(date);
		return format(format, dt);
	}

	public static String format(String format, Date date, String timeZoneId) {
		DateTime dt = new DateTime(date, DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneId)));
		return format(format, dt);
	}

	public static String format(String format, Calendar calendar) {
		DateTime dt = new DateTime(calendar, DateTimeZone.UTC);
		return format(format, dt);
	}

	public static String formatISO8601Instant(final long millis) {
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
		return fmt.format(getCalendarFromMillis(millis, "UTC").getTime());
	}

	public static String format(String format, Calendar calendar, String timeZoneId) {
		if (calendar == null || timeZoneId == null) {
			return StringUtils.EMPTY;
		}
		DateTime dt = new DateTime(calendar, DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneId)));
		return format(format, dt);
	}

	public static String format(String format, Calendar calendar, String timeZoneId, String defaultIfNull) {
		if (calendar == null) return defaultIfNull;
		return format(format, calendar, timeZoneId);
	}

	private static String format(String format, DateTime dt) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
		return fmt.print(dt);
	}

	public static String formatMillis(String format, Long millis) {
		return (millis == null || millis == 0L) ? "" : format(format, new DateTime(millis));
	}

	public static String formatMillis(String format, Long millis, String timeZoneId) {
		return (millis == null || millis == 0L) ? "" : format(format, new DateTime(millis, DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneId))));
	}

	public static String formatTodayForSQL() {
		return formatCalendarForSQL(getCalendarNow());
	}

	public static String formatCalendarForSQL(Calendar theCalendar) {
		return format("yyyy-MM-dd HH:mm", theCalendar);
	}

	public static String formatDate_MMDDYY_HHMMAM(Date date) {
		Assert.notNull(date);

		return String.format("%1$tm%1$td%1$tY %1$tH:%1$tM %1$ta", date);
	}

	public static String formatDate_MMDDYY_HHMMAM(Calendar date) {
		Assert.notNull(date);

		return String.format("%1$tm%1$td%1$tY %1$tH:%1$tM %1$ta", date);
	}

	public static String formatCalendar_MMDDYY(Calendar calendar) {
		Assert.notNull(calendar);

		return String.format("%1$tm%1$td%1$tY", calendar);
	}

	public static String formatDateForEmail(Calendar date) {
		if (date != null) {
			return format("EEE, d MMM yyyy hh:mm aaa", date);
		}
		return StringUtils.EMPTY;
	}

	public static String formatDateForEmail(Calendar date, String timeZoneId) {
		if (date != null) {
			return format("EEE, d MMM yyyy hh:mm aaa z", date, timeZoneId);
		}
		return StringUtils.EMPTY;
	}

	public static String formatDateForEmailNoTime(Calendar date, String timeZoneId) {
		if (date != null) {
			return format("EEE, d MMM yyyy", date, timeZoneId);
		}
		return StringUtils.EMPTY;
	}

	public static String getLuceneDate(Date date) {
		Assert.notNull(date);
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormatGmt.format(date);
	}

	public static String getLuceneDate(long millis) {
		return getLuceneDate(new Date(millis));
	}

	/**
	 * Convert a millisecond duration to a string format
	 *
	 * @param millis
	 *            A duration to convert to a string form
	 * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
	 */
	public static String getDurationBreakdown(long millis) {
		boolean less = false;
		if (millis < 0) {
			less = true;
			millis = Math.abs(millis);
		}

		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		/*
		 * millis -= TimeUnit.MINUTES.toMillis(minutes); long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		 */

		StringBuilder sb = new StringBuilder(64);
		if (less)
			sb.append("-");
		sb.append(days);
		sb.append("d ");
		sb.append(hours);
		sb.append("h ");
		sb.append(minutes);
		sb.append("m");
		/*
		 * sb.append(seconds); sb.append(" Seconds");
		 */

		return (sb.toString());
	}

	public static String getSimpleDurationBreakdown(long millis) {
		millis = Math.abs(millis);

		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		if (seconds < 60) return String.format("%ds", seconds);
		if (seconds < 3600) return String.format("%dmin", TimeUnit.MILLISECONDS.toMinutes(millis));
		if (seconds < Constants.DAY_IN_SECONDS) return String.format("%dhr", TimeUnit.MILLISECONDS.toHours(millis));
		return String.format("%dd", TimeUnit.MILLISECONDS.toDays(millis));
	}


	/*
	 * Same as above but with hours as the highest unit
	 */
	public static String getDurationBreakdownHours(long millis) {
		boolean less = false;
		if (millis < 0) {
			less = true;
			millis = Math.abs(millis);
		}

		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

		StringBuilder sb = new StringBuilder(64);
		if (less)
			sb.append("-");
		sb.append(hours);
		sb.append("h ");
		sb.append(minutes);
		sb.append("m");

		return (sb.toString());
	}


	public static String getMonthName(int month) {
		return months.get(month);
	}

	public static String getWeekdayName(int day) {
		return WEEKDAY_NAMES[day + 1];
	}

	// Constructors

	public static Calendar getCalendarNow() {
		return Calendar.getInstance();
	}

	public static Calendar getCalendarNow(String timeZoneId) {
		return Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
	}

	public static Calendar getCalendarNowUtc() {
		return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}

	public static int getHoursBetween(Calendar start, Calendar end) {
		return getHoursBetween(start, end, true);
	}

	public static int getHoursBetween(Calendar start, Calendar end, boolean absolute) {
		int diff = Hours.hoursBetween(new DateTime(start), new DateTime(end)).getHours();
		return (absolute) ? Math.abs(diff) : diff;
	}

	public static int getMinutesBetween(Calendar start, Calendar end) {
		int diff = Minutes.minutesBetween(new DateTime(start), new DateTime(end)).getMinutes();
		return Math.abs(diff);
	}

	public static int getSecondsBetween(Calendar start, Calendar end) {
		int diff = Seconds.secondsBetween(new DateTime(start), new DateTime(end)).getSeconds();
		return Math.abs(diff);
	}

	public static int getWeeksBetween(Calendar start, Calendar end) {
		int diff = Weeks.weeksBetween(new DateTime(start), new DateTime(end)).getWeeks();
		return Math.abs(diff);
	}

	public static int getHoursBetweenFromNow(Calendar date) {
		return getHoursBetween(date, getCalendarNow(), false);
	}

	public static int getDaysBetween(Calendar start, Calendar end) {
		return getDaysBetween(start, end, true);
	}

	public static int getDaysBetween(Calendar start, Calendar end, boolean absolute) {
		int diff = Days.daysBetween(new DateTime(start), new DateTime(end)).getDays();
		return (absolute) ? Math.abs(diff) : diff;
	}

	public static int getDaysBetweenNow(Calendar date) {
		return getDaysBetween(getCalendarNow(), date, false);
	}

	public static int getDaysBetweenFromNow(Calendar date) {
		return getDaysBetween(date, getCalendarNow(), false);
	}

	public static int getMonthsBetween(Calendar start, Calendar end) {
		return getMonthsBetween(start, end, false);
	}

	public static int getMonthsBetween(Calendar start, Calendar end, boolean absolute) {
		int diff = Months.monthsBetween(new DateTime(start), new DateTime(end)).getMonths();
		return (absolute) ? Math.abs(diff) : diff;
	}

	public static int getMonthsBetweenNow(Calendar date) {
		if (date == null) return 0;
		return getMonthsBetween(getCalendarNow(), date, false);
	}

	public static Boolean intervalContains(Calendar start, Calendar end, Calendar instance) {
		if (start != null && end != null && instance != null && start.before(end)) {
			Interval interval = new Interval(new DateTime(start), new DateTime(end));
			return interval.contains(new DateTime(instance));
		}
		return false;
	}

	/**
	 * Return a Period of hours and minutes for a decimal hours value e.g. "(1, 15) for 1.25", or a null for null input
	 * @param value
	 * @return
	 */
	public static Period getHoursAndMinutes(Float value) {
		return (value != null) ?
				new Period(Math.round((float) (Math.floor(value))), Math.round((value % 1) * 60), 0, 0) :
				null;
	}

	/**
	 * Return a decimal hours value (2 decimal places) from an hours and a minutes. Null values assume zero.
	 * @param hours
	 * @param minutes
	 * @return
	 */
	public static double getDecimalHours(Integer hours, Integer minutes) {
		double decimal = MoreObjects.firstNonNull(hours, 0) + MoreObjects.firstNonNull(minutes, 0) / 60D;
		BigDecimal result = new BigDecimal(decimal).setScale(2, BigDecimal.ROUND_HALF_UP);
		return result.doubleValue();
	}

	public static double getDecimalHours(Integer hours, Integer minutes, Integer scale) {
		double decimal = MoreObjects.firstNonNull(hours, 0) + MoreObjects.firstNonNull(minutes, 0) / 60D;
		BigDecimal result = new BigDecimal(decimal).setScale(MoreObjects.firstNonNull(scale, 2), BigDecimal.ROUND_HALF_UP);
		return result.doubleValue();
	}

	public static double getDecimalHours(Period period) {
		return (period != null) ? getDecimalHours(period.getHours(), period.getMinutes()) : 0.00D;
	}

	/**
	 * checks if a date is within one day (on either side) of 1/1/70 (to account for time zone)
	 * @param millis
	 * @return
	 */
	public static Boolean isNearEpoch(Long millis) {
		Assert.notNull(millis);
		Long dayMillis = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
		return (millis < dayMillis && millis > 0 - dayMillis);
	}


	/**
	 * Determine whether <code>instance</code> is within the specific time window.
	 * Time window is relative to the current time.
	 * @param timeUnit
	 * @param timeWindow
	 * @param instance
	 * @return
	 */
	public static Boolean withinIntervalWindow(int timeUnit, int timeWindow, Calendar instance) {
		return withinIntervalWindow(timeUnit, timeWindow, getCalendarNow(), instance);
	}

	/**
	 * Determine whether <code>instance</code> is within the specific time window.
	 * Time window is relative to the <code>pivot</code>.
	 * @param timeUnit
	 * @param timeWindow
	 * @param pivot
	 * @param instance
	 * @return
	 */
	public static Boolean withinIntervalWindow(int timeUnit, int timeWindow, Calendar pivot, Calendar instance) {
		Calendar startWindow = cloneCalendar(pivot);
		startWindow.add(timeUnit, -timeWindow);

		Calendar endWindow = cloneCalendar(pivot);
		endWindow.add(timeUnit, timeWindow);

		return DateUtilities.intervalContains(startWindow, endWindow, instance);
	}

	public static long getDuration(Calendar start, Calendar end) {
		Duration duration = new Duration(start.getTimeInMillis(), end.getTimeInMillis());
		return duration.getMillis();
	}

	@SuppressWarnings("deprecation")
	public static Calendar parseCalendar(String s) {
		Assert.notNull(s);
		Assert.hasText(s);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(Date.parse(s)));

		return calendar;
	}

	public static Date getNow() {
		return new Date();
	}

	public static Date getMidnight(Date date) {
		Assert.notNull(date);
		DateMidnight dateMidnight = new DateMidnight(date);

		return new Date(dateMidnight.getMillis());
	}

	public static Date getMidnightNextDay(Date date) {
		Assert.notNull(date);
		DateMidnight dateMidnight = new DateMidnight(date).plusDays(1);

		return new Date(dateMidnight.getMillis());
	}

	public static Date getMidnightMonthsBefore(Date date, int months) {
		Assert.notNull(date);
		DateMidnight dateMidnight = new DateMidnight(date).minusMonths(months);

		return new Date(dateMidnight.getMillis());
	}

	public static Calendar getMidnight(Calendar calendar) {
		Assert.notNull(calendar);
		DateMidnight dateMidnight = new DateMidnight(calendar);

		return dateMidnight.toCalendar(Locale.ENGLISH);
	}

	public static Calendar getMidnightRelativeToTimezone(Calendar calendar, TimeZone timeZoneId) {
		Assert.notNull(calendar);

		return new DateMidnight(new DateTime(calendar).toDateTime(DateTimeZone.forTimeZone(timeZoneId)))
			.toDateTime(DateTimeZone.UTC)
			.toCalendar(Locale.ENGLISH);
	}

	public static Calendar getMidnightNextDay(Calendar calendar) {
		Assert.notNull(calendar);

		return new DateMidnight(calendar)
				.plusDays(1)
				.toDateTime(DateTimeZone.UTC)
				.toCalendar(Locale.ENGLISH);
	}

	public static Calendar getMidnightNextDayRelativeToTimezone(Calendar calendar, TimeZone timeZoneId) {
		Assert.notNull(calendar);

		return new DateMidnight(new DateTime(calendar).toDateTime(DateTimeZone.forTimeZone(timeZoneId)))
				.plusDays(1)
				.toDateTime(DateTimeZone.UTC)
				.toCalendar(Locale.ENGLISH);
	}

	public static Calendar getMidnightInTimezone(Calendar calendar, TimeZone timeZoneId) {
		Assert.notNull(calendar);

		return new DateMidnight(calendar)
				.withZoneRetainFields(DateTimeZone.forTimeZone(timeZoneId))
				.toDateTime(DateTimeZone.UTC)
				.toCalendar(Locale.ENGLISH);
	}

	public static Calendar getMidnightNextDayInTimezone(Calendar calendar, TimeZone timeZoneId) {
		Assert.notNull(calendar);

		return new DateMidnight(calendar)
				.plusDays(1)
				.withZoneRetainFields(DateTimeZone.forTimeZone(timeZoneId))
				.toDateTime(DateTimeZone.UTC)
				.toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithTime(int hour, int minute) {
		return new DateTime().withTime(hour, minute, 0, 0).toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithTime(int hour, int minute, String timeZoneId) {
		return new DateTime()
				.toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneId)))
				.withTime(hour, minute, 0, 0)
				.toDateTime(DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
	}

	public static Long getDifferenceInMillisFromNow(Calendar calendar) {
		if (calendar == null) {
			return 0L;
		}
		long diff = calendar.getTimeInMillis() - getCalendarNow().getTimeInMillis();
		return (diff >= 0 ? diff : 0);
	}

	public static Integer toMilitaryTime(Calendar calendar) {
		Assert.notNull(calendar);
		int time = 0;
		time += calendar.get(Calendar.HOUR_OF_DAY) * 100;
		time += calendar.get(Calendar.MINUTE);
		return time;
	}

	public static Integer toMilitaryTimeNull(Calendar calendar) {
		if (calendar == null)
			return null;
		int time = 0;
		time += calendar.get(Calendar.HOUR_OF_DAY) * 100;
		time += calendar.get(Calendar.MINUTE);
		return time;
	}

	public static Calendar newCalendarFromMilitaryTime(Integer hours) {
		Assert.notNull(hours);
		Assert.state(hours.intValue() < 2401);

		Calendar calendar = Calendar.getInstance();

		calendar.set(0, 0, 0, hours.intValue() / 100, hours.intValue() % 100, 0);

		return calendar;
	}

	public static Calendar newCalendar(int year, int month, int day, int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear(Calendar.MILLISECOND);
		calendar.set(year, month, day, hour, minute, second);

		return calendar;
	}

	public static Calendar newCalendar(int year, int month, int day, int hour, int minute, int second, TimeZone timeZoneId) {
		Calendar calendar = Calendar.getInstance(timeZoneId);
		calendar.clear(Calendar.MILLISECOND);
		calendar.set(year, month, day, hour, minute, second);
		return new DateTime(calendar).toDateTime(DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
	}

	public static Calendar addMinutes(Calendar calendarNow, Integer snoozeTime) {
		Assert.notNull(calendarNow);
		Assert.notNull(snoozeTime);
		calendarNow.add(Calendar.MINUTE, snoozeTime);
		return calendarNow;
	}

	public static Calendar addHours(Calendar calendarNow, double snoozeTime) {
		Assert.notNull(calendarNow);
		Assert.notNull(snoozeTime);
		int hours = (int) snoozeTime;
		int minutes = (int) ((snoozeTime - hours) * 60);
		calendarNow.add(Calendar.HOUR, hours);
		calendarNow.add(Calendar.MINUTE, minutes);
		return calendarNow;
	}

	private static Calendar addTime(Calendar calendarNow, double snoozeTime, String timeUnit, int multiplier) {
		Assert.notNull(calendarNow);
		Assert.notNull(snoozeTime);
		Assert.isTrue(timeUnit.equals(Constants.MINUTE) || timeUnit.equals(Constants.HOUR) || timeUnit.equals(Constants.DAY), "Time unit not supported");
		Assert.isTrue(Math.abs(multiplier) == 1);

		int integral = (int) snoozeTime;
		double fraction = snoozeTime - integral;
		integral = integral * multiplier;
		fraction = fraction * multiplier;

		if (timeUnit.equals(Constants.MINUTE)) {
			calendarNow.add(Calendar.MINUTE, integral);
			calendarNow.add(Calendar.SECOND, (int) (fraction * 60));
		}
		if (timeUnit.equals(Constants.HOUR)) {
			calendarNow.add(Calendar.HOUR, integral);
			calendarNow.add(Calendar.MINUTE, (int) (fraction * 60));
		}
		if (timeUnit.equals(Constants.DAY)) {
			calendarNow.add(Calendar.DAY_OF_YEAR, integral);
			calendarNow.add(Calendar.HOUR, (int) (fraction * 24));
		}

		return calendarNow;
	}

	public static Calendar addTime(Calendar calendarNow, int snoozeTime, String timeUnit) {
		Assert.notNull(calendarNow);
		Assert.notNull(snoozeTime);

		if (timeUnit.equals(Constants.SECOND)) {
			calendarNow.add(Calendar.SECOND, snoozeTime);
		}
		if (timeUnit.equals(Constants.MINUTE)) {
			calendarNow.add(Calendar.MINUTE, snoozeTime);
		}
		if (timeUnit.equals(Constants.HOUR)) {
			calendarNow.add(Calendar.HOUR, snoozeTime);
		}
		if (timeUnit.equals(Constants.DAY)) {
			calendarNow.add(Calendar.DAY_OF_YEAR, snoozeTime);
		}
		if (timeUnit.equals(Constants.WEEK)) {
			calendarNow.add(Calendar.WEEK_OF_YEAR, snoozeTime);
		}

		return calendarNow;
	}

	public static Calendar addTime(Calendar calendarNow, double snoozeTime, String timeUnit) {
		return addTime(calendarNow, snoozeTime, timeUnit, 1);
	}

	public static Calendar subtractTime(Calendar calendarNow, double snoozeTime, String timeUnit) {
		return addTime(calendarNow, snoozeTime, timeUnit, -1);
	}

	public static Calendar subtractTime(Calendar calendarNow, Integer snoozeTime, String timeUnit) {
		return addTime(calendarNow, snoozeTime * -1, timeUnit);
	}

	public static boolean isInFuture(Calendar calendar) {
		return calendar != null && calendar.after(Calendar.getInstance());
	}

	public static boolean isInFutureWithBuffer(Calendar calendar, int buffer, String timeUnit) {
		if (calendar == null) {
			return false;
		}
		Calendar bufferedCalendar = addTime(Calendar.getInstance(), buffer, timeUnit);
		return calendar.after(bufferedCalendar);
	}

	public static boolean isInPast(Calendar calendar) {
		return calendar != null && calendar.before(Calendar.getInstance());
	}

	public static boolean timeIntervalsOverlap(Calendar s1, Calendar e1, Calendar s2, Calendar e2) {
		LocalTime start1 = LocalTime.fromCalendarFields(s1);
		LocalTime end1 = LocalTime.fromCalendarFields(e1);
		LocalTime start2 = LocalTime.fromCalendarFields(s2);
		LocalTime end2 = LocalTime.fromCalendarFields(e2);

		// If end is before start, assume that it's through the night and on the flipside of midnight.
		// E.g. affects users in EST who work until >= 7pm (i.e. >= 00:00:00 UTC)
		// and throws illegal interval exceptions otherwise

		DateTime start1d = start1.toDateTimeToday();
		DateTime end1d = end1.isAfter(start1) || end1.isEqual(start1) ? end1.toDateTimeToday() : end1.toDateTimeToday().plusDays(1);
		DateTime start2d = start2.toDateTimeToday();
		DateTime end2d = end2.isAfter(start2) || end2.isEqual(start2) ? end2.toDateTimeToday() : end2.toDateTimeToday().plusDays(1);

		Interval i1 = new Interval(start1d, end1d);
		Interval i2 = new Interval(start2d, end2d);
		return i1.overlaps(i2);
	}

	public static Calendar cloneCalendar(Calendar c) {
		if (c == null) {
			return null;
		}
		return (Calendar) c.clone();
	}

	public static Calendar getCalendarFromDate(Date date) {
		if (date == null) {
			return null;
		}

		Calendar calendar = getCalendarNow();
		calendar.setTime(date);
		return calendar;
	}

	public static Calendar getCalendarFromMillis(Long milliseconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliseconds);
		return calendar;
	}

	public static Calendar getCalendarFromDate(Date date, String timeZoneId) {
		if (date == null)
			return null;
		if (StringUtils.isBlank(timeZoneId))
			return getCalendarFromDate(date);

		Calendar calendar = getCalendarNow();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		return calendar;
	}

	public static Calendar getMidnightToday() {
		DateMidnight midnight = new DateMidnight();
		return midnight.toGregorianCalendar();
	}

	public static Calendar getMidnightTodayRelativeToTimezone(String timeZone) {
		return getMidnightRelativeToTimezone(Calendar.getInstance(), TimeZone.getTimeZone(timeZone));
	}

	public static Calendar getMidnightTodayRelativeToTimezone(TimeZone timeZone) {
		return getMidnightRelativeToTimezone(Calendar.getInstance(), timeZone);
	}

	public static Calendar getMidnightTomorrow() {
		DateMidnight midnight = new DateMidnight();
		return midnight.plusDays(1).toGregorianCalendar();
	}

	public static Calendar getMidnightTomorrowRelativeToTimezone(String timeZone) {
		return getMidnightTomorrowRelativeToTimezone(TimeZone.getTimeZone(timeZone));
	}

	public static Calendar getMidnightTomorrowRelativeToTimezone(TimeZone timeZone) {
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_YEAR, 1);
		return getMidnightRelativeToTimezone(tomorrow, timeZone);
	}

	public static Calendar getMidnightNextWeek() {
		return new DateMidnight()
			.plusWeeks(1)
			.plusDays(1)
			.toGregorianCalendar();
	}

	public static Calendar getMidnightNextMonth() {
		return new DateMidnight()
			.plusMonths(1)
			.plusDays(1)
			.toGregorianCalendar();
	}

	public static Calendar getMidnightMonthAgo() {
		DateMidnight m = new DateMidnight();

		m = m.minusMonths(1);
		m = m.plusDays(1);

		return m.toGregorianCalendar();
	}

    public static Calendar getMidnightNMonthsAgo(Integer n) {
        return new DateMidnight()
                .minusMonths(n)
                .plusDays(1)
                .toGregorianCalendar();
    }


    public static Calendar getMidnightNMonthsFromNow(Integer n) {
        return new DateMidnight()
                .plusMonths(n)
                .plusDays(1)
                .toGregorianCalendar();
    }

	public static Calendar getMidnightYesterday() {
		DateMidnight midnight = new DateMidnight();
		return midnight.minusDays(1).toGregorianCalendar();
	}

	public static Calendar getMidnightYesterday(String timeZone) {
		DateMidnight midnight = new DateMidnight(DateTimeZone.forID(timeZone));
		return midnight.minusDays(1).toGregorianCalendar();
	}

	public static Calendar getMidnightWeekAgo() {
		DateMidnight m = new DateMidnight();

		m = m.minusWeeks(1);
		m = m.plusDays(1);

		return m.toGregorianCalendar();
	}

	public static Calendar getMidnightQuarterAgo() {
		DateMidnight m = new DateMidnight();

		m = m.minusMonths(3);
		m = m.plusDays(1);

		return m.toGregorianCalendar();
	}

	public static Calendar getMidnight6MonthsAgo() {
		DateMidnight m = new DateMidnight();

		m = m.minusMonths(6);
		m = m.plusDays(1);

		return m.toGregorianCalendar();
	}

	public static Calendar getMidnight1YearAgo() {
		DateMidnight m = new DateMidnight();

		m = m.minusYears(1);

		return m.toGregorianCalendar();
	}

	public static Calendar getMidnightYTD() {
		return new DateMidnight().withDayOfYear(1).toGregorianCalendar();
	}

	public static Calendar getMidnightYTDAYearAgo() {
		return new DateMidnight().withDayOfYear(1).minusYears(1).toGregorianCalendar();
	}

	public static Calendar getMidnightYTDNextYear() {
		return new DateMidnight().withDayOfYear(1).plusYears(1).minusDays(1).toGregorianCalendar();
	}

	public static Long getMillisSinceMidnight(Calendar date) {
		Calendar midnight = getMidnight(date);
		return date.getTimeInMillis() - midnight.getTimeInMillis();
	}

	public static Long getMillisFromNow(Calendar date) {
		Calendar now = Calendar.getInstance();
		return now.getTimeInMillis() - (date != null ? date.getTimeInMillis() : 0);
	}

	public static Calendar max(Calendar d1, Calendar d2) {
		if (d1 == null) return d2;
		if (d2 == null) return d1;
		return (d1.compareTo(d2) > 0) ? d1 : d2;
	}

	public static Calendar min(Calendar d1, Calendar d2) {
		if (d1 == null) return d2;
		if (d2 == null) return d1;
		return (d1.compareTo(d2) < 1) ? d1 : d2;
	}

	public static boolean isBefore(Calendar date1, Calendar date2) {
		return (new DateTime(date1).isBefore(new DateTime(date2)));
	}

	public static Calendar lastNDaysMidnight(int days) {
		Calendar calendar = DateUtilities.subtractTime(Calendar.getInstance(), days + 1, Constants.DAY);
		calendar.set(Calendar.HOUR, 24);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}

    public static Calendar nextNDaysMidnight(int days) {
        Calendar calendar = DateUtilities.addTime(Calendar.getInstance(), days + 1, Constants.DAY);
        calendar.set(Calendar.HOUR, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

	public static boolean isLeapYear(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		return calendar.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
	}

	public static Calendar getCalendarOfWMEPOCH() {
		return getCalendarFromMillis(WM_EPOCH);
	}

	public static Calendar getCalendarWithBeginningOfQuarter(Calendar calendar, TimeZone timeZoneId) {
		DateTime dt = new DateTime(calendar.get(Calendar.YEAR), 1 + (3*(calendar.get(Calendar.MONTH)/3)), 1, 0, 0, 0, 0, DateTimeZone.forTimeZone(timeZoneId));
		return dt.toDateTime(DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithLastDayOfQuarter(Calendar calendar, TimeZone timeZoneId) {
		DateTime dt = new DateTime(calendar.get(Calendar.YEAR), 1 + (3*(calendar.get(Calendar.MONTH)/3)), 1, 0, 0, 0, 0, DateTimeZone.forTimeZone(timeZoneId));
		return dt.plusMonths(3).minusDays(1).toDateTime(DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithLastDayOfTheMonth(Calendar calendar, TimeZone timeZoneId) {
		DateTime dt = new DateTime(calendar).toDateTime(DateTimeZone.forTimeZone(timeZoneId));
		return dt.dayOfMonth().withMaximumValue().hourOfDay().withMaximumValue().minuteOfDay().withMaximumValue()
				.toDateTime(DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithLastDayOfTheMonthWithMinimumTime(Calendar calendar, TimeZone timeZoneId) {
		DateTime dt = new DateTime(calendar).toDateTime(DateTimeZone.forTimeZone(timeZoneId));
		return dt.dayOfMonth().withMaximumValue().hourOfDay().withMinimumValue().minuteOfDay().withMinimumValue()
				.toDateTime(DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithLastDayOfThePreviousMonth(Calendar calendar) {
		DateTime dt = new DateTime(calendar).toDateTime(DateTimeZone.UTC).minusMonths(1);
		return dt.dayOfMonth().withMaximumValue().hourOfDay().withMaximumValue().minuteOfDay().withMaximumValue().toCalendar(Locale.ENGLISH);
	}

	public static int getLastDayOfTheMonth(Calendar calendar) {
				return new DateTime(calendar).dayOfMonth().withMaximumValue().toCalendar(Locale.ENGLISH).get(Calendar.DAY_OF_MONTH);
	}

	public static Calendar getCalendarWithFirstDayOfTheMonth(Calendar calendar, TimeZone timeZoneId) {
		DateTime dt = new DateTime(calendar).toDateTime(DateTimeZone.forTimeZone(timeZoneId));
		return dt.dayOfMonth().withMinimumValue().hourOfDay().withMinimumValue().minuteOfDay().withMinimumValue()
				.secondOfDay().withMinimumValue().toDateTime(DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithLastDayOfYear(Calendar calendar, TimeZone timeZoneId) {
		DateTime dt = new DateTime(calendar).toDateTime(DateTimeZone.forTimeZone(timeZoneId));
		return dt.monthOfYear().withMaximumValue().dayOfMonth().withMaximumValue().hourOfDay().withMaximumValue().minuteOfDay().withMaximumValue()
				.secondOfDay().withMaximumValue().toDateTime(DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
    }

	public static Calendar getCalendarWithLastMinuteOfDay(Calendar calendar, TimeZone timeZoneId) {
		DateTime dt = new DateTime(calendar).toDateTime(DateTimeZone.forTimeZone(timeZoneId));
		return dt.hourOfDay().withMaximumValue().minuteOfDay().withMaximumValue().secondOfDay().withMaximumValue()
				.toDateTime(DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithFirstDayOfYear(Calendar calendar) {
		DateTime dt = new DateTime(calendar).toDateTime(DateTimeZone.UTC);
		return dt.monthOfYear().withMinimumValue().dayOfMonth().withMinimumValue().hourOfDay().withMinimumValue().minuteOfDay().withMinimumValue()
				.toDateTime().toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithFirstDayOfYear(Calendar calendar, TimeZone timeZoneId) {
		DateTime dt = new DateTime(calendar).toDateTime(DateTimeZone.forTimeZone(timeZoneId));
		return dt.monthOfYear().withMinimumValue().dayOfMonth().withMinimumValue().hourOfDay().withMinimumValue().minuteOfDay().withMinimumValue()
				.toDateTime(DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithFirstDayOfWeek(Calendar calendar) {
		return new DateTime(calendar).dayOfWeek().withMinimumValue().toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithFirstDayOfWeek(Calendar calendar, TimeZone timeZoneId) {
		return new DateTime(calendar).toDateTime(DateTimeZone.forTimeZone(timeZoneId)).dayOfWeek().withMinimumValue().toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithLastDayOfWeek(Calendar calendar) {
		return new DateTime(calendar).dayOfWeek().withMaximumValue().toCalendar(Locale.ENGLISH);
	}

	public static Calendar getCalendarWithLastDayOfWeek(Calendar calendar, TimeZone timeZoneId) {
		return new DateTime(calendar).toDateTime(DateTimeZone.forTimeZone(timeZoneId)).dayOfWeek().withMaximumValue().toCalendar(Locale.ENGLISH);
	}

	public static Integer getOffsetHoursForTimeZone(TimeZone timeZoneId) {
		int offset = DateTimeZone.forTimeZone(timeZoneId).getOffset(new DateTime(DateTimeZone.UTC));
		return offset / 3600 / 1000;
	}

	/**
	 * Null safe calendar equality test
	 *
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static boolean equals(Calendar c1, Calendar c2) {
		if (c1 == null)
			return false;
		return c2 != null && c1.equals(c2);
	}

	/**
	 * Returns a calendar with the closest date with the specified day of the week. E.g. Next Monday. Next Sunday.
	 *
	 * @param dayOfWeek
	 * @return
	 */
	public static Calendar getCalendarWithNextDayOfWeek(Calendar date, int dayOfWeek) {
		Assert.isTrue(dayOfWeek >= 1 && dayOfWeek <= 7);
		Calendar newCalendar = (Calendar) date.clone();

		int dateDayOfTheWeek = newCalendar.get(Calendar.DAY_OF_WEEK);
		if (dateDayOfTheWeek < dayOfWeek) {
			newCalendar.add(Calendar.DATE, Math.abs(dayOfWeek - dateDayOfTheWeek));
		} else {
			newCalendar.add(Calendar.DATE, Math.abs(7 - dateDayOfTheWeek + dayOfWeek));
		}
		return newCalendar;
	}

	/**
	 * Returns a calendar with the closest date with the specified day of the month.
	 *
	 * @param date
	 * @param dayOfMonth
	 * @return
	 */
	public static Calendar getCalendarWithNextDayOfMonth(Calendar date, int dayOfMonth) {
		Assert.isTrue(dayOfMonth >= 1 && dayOfMonth <= 30);
		Calendar newCalendar = (Calendar) date.clone();
		int month = newCalendar.get(Calendar.MONTH);

		if (newCalendar.get(Calendar.DAY_OF_MONTH) < dayOfMonth) {
			if (month != Calendar.FEBRUARY) {
				newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				return newCalendar;
			} else if (dayOfMonth < 29 || (dayOfMonth == 29 && newCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) > 365)) {
				newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				return newCalendar;
			}
		}

		if (month == Calendar.JANUARY && dayOfMonth >= 29) {
			return new DateTime(newCalendar).plusMonths(1).dayOfMonth().withMaximumValue().toGregorianCalendar();
		}

		newCalendar = new DateTime(newCalendar).plusMonths(1).toGregorianCalendar();
		newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		return newCalendar;
	}

	/**
	 * Returns a calendar with the closed date between to possible days of the month
	 *
	 * @param date
	 * @param dayOfMonth1
	 * @param dayOfMonth2
	 * @return
	 */
	public static Calendar getCalendarWithClosestNextDayOfMonth(Calendar date, int dayOfMonth1, int dayOfMonth2) {
		Calendar calendarWithNextDayOfMonth1 = getCalendarWithNextDayOfMonth(date, dayOfMonth1);
		Calendar calendarWithNextDayOfMonth2 = getCalendarWithNextDayOfMonth(date, dayOfMonth2);
		if (calendarWithNextDayOfMonth1.before(calendarWithNextDayOfMonth2)) {
			return calendarWithNextDayOfMonth1;
		}
		return calendarWithNextDayOfMonth2;
	}

	/**
	 * For a DateTime dt, get "n" where n = "dt is the nth (dt.weekday) of the month of (dt.month)"
	 * @param date
	 * @return
	 */
	public static int getWeekdayOrdinalByMonth(final DateTime date) {
		final DateTime startOfMonth = date.withDayOfMonth(1).withMillisOfDay(0);
		DateTime firstWeekDay = startOfMonth.withDayOfWeek(date.getDayOfWeek());

		if (firstWeekDay.isBefore(startOfMonth))
			firstWeekDay = firstWeekDay.plusWeeks(1);

		final int weeksBetween = Weeks.weeksBetween(firstWeekDay, date).getWeeks();
		return weeksBetween + 1;
	}

	// zero-indexed
	public static String getReadableDayOfWeek(int i) {
		return Range.closed(0, 6).contains(i) ? daysOfWeek.get(i+1) : "";
	}

	public static boolean isWeekend(DateTime dateTime) {
		return (DateTimeConstants.SATURDAY == dateTime.dayOfWeek().get() || DateTimeConstants.SUNDAY == dateTime.dayOfWeek().get());
	}


	public static String fuzzySpan(Long millis) {
		return fuzzySpan(new Date(millis));
	}

	public static String fuzzySpan(Date targetDate) {
		return fuzzySpan(targetDate, null);
	}

	public static String fuzzySpan(Date targetDate, Date fromDate) {
		if (targetDate == null) {
			return "never";
		}

		if (fromDate == null) {
			fromDate = new Date(System.currentTimeMillis());
		}

		long diff = Math.abs(fromDate.getTime() - targetDate.getTime());

		String span;

		if (diff <= MINUTES.toMillis(1)) {
			span = "moments";
		} else if (diff <= MINUTES.toMillis(20)) {
			span = "a few minutes";
		} else if (diff < HOURS.toMillis(1)) {
			span = "less than an hour";
		} else if (diff < HOURS.toMillis(4)) {
			span = "a couple of hours";
		} else if (diff < DAYS.toMillis(1)) {
			span = "less than a day";
		} else if (diff < DAYS.toMillis(2)) {
			span = "about a day";
		} else if (diff < DAYS.toMillis(4)) {
			span = "a couple of days";
		} else if (diff < DAYS.toMillis(7)) {
			span = "less than a week";
		} else if (diff < DAYS.toMillis(14)) {
			span = "about a week";
		} else if (diff < DAYS.toMillis(30)) {
			span = "less than a month";
		} else if (diff < DAYS.toMillis(30 * 2)) {
			span = "about a month";
		} else if (diff < DAYS.toMillis(30 * 4)) {
			span = "a couple of months";
		} else if (diff < DAYS.toMillis(365)) {
			span = "less than a year";
		} else if (diff < DAYS.toMillis(365 * 2)) {
			span = "about a year";
		} else if (diff < DAYS.toMillis(365 * 4)) {
			span = "a couple of years";
		} else if (diff < DAYS.toMillis(365 * 8)) {
			span = "a few years";
		} else if (diff < DAYS.toMillis(365 * 12)) {
			span = "about a decade";
		} else if (diff < DAYS.toMillis(365 * 24)) {
			span = "a couple of decades";
		} else if (diff < DAYS.toMillis(365 * 64)) {
			span = "several decades";
		} else {
			return "a long time";
		}

		if (fromDate.after(targetDate)) {
			// This is in the past
			return span + " ago";
		} else {
			return "in " + span;

		}
	}

	public static String daySpanFromMillis(long millis) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millis);
		return String.valueOf(getDaysBetweenNow(c));
	}

	public static String getShortTimeZoneName(String longTimeZoneName) {
		return TimeZone.getTimeZone(longTimeZoneName).getDisplayName(false, TimeZone.SHORT);
	}

	public static long getUnixTime(Calendar calendar) {
		if (calendar != null) {
			return getUnixTime(calendar.getTimeInMillis());
		}
		return 0L;
	}

	public static long getUnixTime(long timeInMillis) {
		return timeInMillis / 1000L;
	}

	public static Calendar getPreviousMonth() {
		return getPreviousMonth(getCalendarNow());
	}

	public static Calendar getPreviousMonth(Calendar calendar) {
		DateTime dt = new DateTime(calendar);
		return dt.minusMonths(1).toCalendar(Locale.ENGLISH);
	}

	public static String formatDateForTimeZone(Date date, String timeZoneId){
		DateFormat format = simpleDateFormat;
		format.setTimeZone(TimeZone.getTimeZone(timeZoneId));

		return format.format(date);
	}

	public static Date changeTimeZone(long millis, String timeZoneId) {
		Calendar first = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
		first.setTimeInMillis(millis);

		Calendar output = Calendar.getInstance();
		output.set(Calendar.YEAR, first.get(Calendar.YEAR));
		output.set(Calendar.MONTH, first.get(Calendar.MONTH));
		output.set(Calendar.DAY_OF_MONTH, first.get(Calendar.DAY_OF_MONTH));
		output.set(Calendar.HOUR_OF_DAY, first.get(Calendar.HOUR_OF_DAY));
		output.set(Calendar.MINUTE, first.get(Calendar.MINUTE));
		output.set(Calendar.SECOND, first.get(Calendar.SECOND));
		output.set(Calendar.MILLISECOND, first.get(Calendar.MILLISECOND));

		return output.getTime();
	}

	public static long changeTimeZoneRetainFields(long millis, String timeZoneId) {
		return new DateTime(millis, DateTimeZone.UTC)
				.withZoneRetainFields(DateTimeZone.forID(StringUtilities.defaultString(timeZoneId, Constants.WM_TIME_ZONE)))
				.getMillis();
	}

	/** Changes a calendars time zone to match the given time zone
	 * @param calendar The calendar that needs it's time zone changed
	 * @param timeZoneId The time zone the calendar will be converted to
	 * @return A calendar with it's time zone switched */
	public static Calendar changeTimeZone(Calendar calendar, String timeZoneId) {
		return new DateTime(calendar).withZone(DateTimeZone.forID(timeZoneId)).toCalendar(Locale.ENGLISH);
	}

	public static boolean isSameDay(Calendar start, Calendar end) {
		if (start == null || end == null) return false;
		if (start.get(Calendar.YEAR) != end.get(Calendar.YEAR)) return false;
		if (start.get(Calendar.MONTH) != end.get(Calendar.MONTH)) return false;
		if (start.get(Calendar.DAY_OF_MONTH) != end.get(Calendar.DAY_OF_MONTH)) return false;
		return true;
	}

	public static int getCurrentYear() {
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.YEAR);
	}

	public static int secondsToDays(double seconds) {
		return (int) TimeUnit.SECONDS.toDays((long)seconds);
	}

	public static final int FROM = 0;
	public static final int TO = 1;

	public static Calendar[] getWidestDateRange(Calendar origFrom, Calendar origTo, Calendar newFrom, Calendar newTo) {
		Calendar[] origRange = {origFrom, origTo};
		Calendar[] newRange = {newFrom, newTo};

		return getWidestDateRange(origRange, newRange);
	}

	public static Calendar[] getWidestDateRange(Calendar[] origRange, Calendar[] newRange) {
		if (origRange == null || (origRange[FROM] == null && origRange[TO] == null)) { return newRange; }
		if (newRange == null || (newRange[FROM] == null && newRange[TO] == null)) { return origRange; }

		Calendar[] ret = origRange.clone();

		if (
			(newRange[FROM] != null && origRange[FROM] == null) ||
			(newRange[FROM] != null && origRange[FROM] != null && newRange[FROM].getTimeInMillis() != 0 && (newRange[FROM].compareTo(origRange[FROM]) < 0 || origRange[FROM].getTimeInMillis() == 0))
		) {
			ret[FROM] = newRange[FROM];
		}

		if (
			(newRange[TO] != null && origRange[TO] == null) ||
			(newRange[TO] != null && origRange[TO] != null && newRange[FROM].getTimeInMillis() != 0 && (newRange[TO].compareTo(origRange[TO]) > 0 || origRange[TO].getTimeInMillis() == 0))
		) {
			ret[TO] = newRange[TO];
		}

		return ret;
	}

	public static String getSearchCSVFilename() {
		return "search-" + formatDate_MMDDYY_HHMMAM(getCalendarNow()).replaceAll("\\s+", "");
	}

	public static boolean equal(DateTime d1, DateTime d2) {
		if (d1 == d2) {
			return true;
		}

		if (d1 != null && d2 != null) {
			return d1.getMillis() == d2.getMillis(); // ignore idiocyncrocies with timezones
		}

		return d1 != null && d1.equals(d2);
	}

	public static boolean equal(Calendar d1, Calendar d2) {
		if (d1 == d2) {
			return true;
		}

		if (d1 != null && d2 != null) {
			return d1.getTimeInMillis() == d2.getTimeInMillis(); // ignore idiocyncrocies with timezones
		}

		return d1 != null && d1.equals(d2);
	}

	public static Calendar getCalendarFromMillis(final Long milliseconds, final String timeZoneId) {
		Calendar calendar = getCalendarNow(timeZoneId);
		calendar.setTimeInMillis(milliseconds);
		return calendar;
	}

	public static Date getDateFromString(final String format, String dateTime) throws ParseException {
		return new SimpleDateFormat(format).parse(dateTime);
	}

	public static boolean isOn(Calendar instance, Long unixTime) {
		final Calendar date = getCalendarFromMillis(unixTime);
		return instance.get(Calendar.YEAR) == date.get(Calendar.YEAR)
				&& instance.get(Calendar.MONTH) == date.get(Calendar.MONTH)
				&& instance.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH);
	}
}

