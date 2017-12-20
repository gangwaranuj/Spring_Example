package com.workmarket.utility;

import com.workmarket.configuration.Constants;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import static com.workmarket.utility.DateUtilities.getCalendarFromMillis;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class DateUtilitiesTest {

	Calendar[] newRange;
	Calendar[] curRange;

	@Before
	public void before() {
		// dates
		newRange = new Calendar[2];

		newRange[DateUtilities.FROM] = Calendar.getInstance();
		newRange[DateUtilities.FROM].set(1999, Calendar.JANUARY, 1);

		newRange[DateUtilities.TO] = Calendar.getInstance();
		newRange[DateUtilities.TO].set(1999, Calendar.JANUARY, 1);

		curRange = new Calendar[2];
	}

	@Test
	public void test_toISO8601() throws Exception {
		Calendar calendar = Calendar.getInstance(DateTimeZone.UTC.toTimeZone());
		calendar.set(1981, Calendar.NOVEMBER, 4, 4, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date date = calendar.getTime();

		assertEquals("1981-11-04T04:00:00.000Z", DateUtilities.getISO8601(calendar));
		assertEquals("1981-11-04T04:00:00.000Z", DateUtilities.getISO8601(date));
	}

	@Test
	public void toISO8601WithSpaces_withUTC_same() throws Exception {
		Calendar calendar = Calendar.getInstance(DateTimeZone.UTC.toTimeZone());
		calendar.set(1981, Calendar.NOVEMBER, 4, 4, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		assertEquals("1981-11-04 04:00:00", DateUtilities.getISO8601WithSpaces(calendar, TimeZone.getTimeZone("UTC")));
	}

	@Test
	public void toISO8601WithSpaces_withEST_converted() throws Exception {
		Calendar calendar = Calendar.getInstance(DateTimeZone.UTC.toTimeZone());
		calendar.set(1981, Calendar.NOVEMBER, 4, 4, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		assertEquals("1981-11-03 23:00:00", DateUtilities.getISO8601WithSpaces(calendar, TimeZone.getTimeZone("EST")));
	}

	@Test
	public void test_fromISO8601() throws Exception {
		String iso8601 = "1981-11-04T04:00:00-05:00";

		Calendar calendar = DateUtilities.getCalendarFromISO8601(iso8601);
		calendar.setTimeZone(DateTimeZone.UTC.toTimeZone());

		assertEquals(1981, calendar.get(Calendar.YEAR));
		assertEquals(10, calendar.get(Calendar.MONTH));
		assertEquals(4, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(9, calendar.get(Calendar.HOUR));
		assertEquals(0, calendar.get(Calendar.MINUTE));
		assertEquals(0, calendar.get(Calendar.SECOND));

		Date date = DateUtilities.getDateFromISO8601(iso8601);
		calendar = Calendar.getInstance(DateTimeZone.UTC.toTimeZone());
		calendar.setTime(date);

		assertEquals(1981, calendar.get(Calendar.YEAR));
		assertEquals(10, calendar.get(Calendar.MONTH));
		assertEquals(4, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(9, calendar.get(Calendar.HOUR));
		assertEquals(0, calendar.get(Calendar.MINUTE));
		assertEquals(0, calendar.get(Calendar.SECOND));
	}

	@Test
	public void test_toMilitaryTime() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, Calendar.NOVEMBER, 1, 13, 23);

		assertTrue(Integer.valueOf(1323).equals(DateUtilities.toMilitaryTime(calendar)));
	}

	@Test
	public void test_newCalendarFromMilitaryTime() throws Exception {
		Calendar calendar = DateUtilities.newCalendarFromMilitaryTime(1323);

		assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(23, calendar.get(Calendar.MINUTE));
	}

	@Test
	public void test_timeIntervalsOverlap() throws Exception {
		int y = 2010, m = 11, d = 4;

		// Test from: http://joda-time.sourceforge.net/apidocs/org/joda/time/base/AbstractInterval.html#overlaps(org.joda.time.ReadableInterval)

		assertFalse(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 8, 0, 0),
				DateUtilities.newCalendar(y, m, d, 8, 30, 0)
		));

		assertFalse(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 8, 0, 0),
				DateUtilities.newCalendar(y, m, d, 9, 0, 0)
		));

		assertTrue(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 8, 0, 0),
				DateUtilities.newCalendar(y, m, d, 9, 30, 0)
		));

		assertTrue(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 8, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0)
		));

		assertTrue(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 8, 0, 0),
				DateUtilities.newCalendar(y, m, d, 11, 0, 0)
		));

		//

		assertFalse(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 9, 0, 0)
		));

		assertTrue(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 9, 30, 0)
		));

		assertTrue(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0)
		));

		assertTrue(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 11, 0, 0)
		));

		// Edge cases

		assertTrue(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 22, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 0, 0, 0)
		));

		assertFalse(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 0, 0, 0)
		));

		assertFalse(DateUtilities.timeIntervalsOverlap(
				DateUtilities.newCalendar(y, m, d, 9, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 0, 0),
				DateUtilities.newCalendar(y, m, d, 10, 30, 0),
				DateUtilities.newCalendar(y, m, d, 0, 0, 0)
		));
	}

	@Test
	public void getCalendarFromDateStringTest() {
		Calendar cal = DateUtilities.getCalendarFromDateString("05/20/2012", "UTC");
		assertEquals(4, cal.get(Calendar.MONTH)); // "The first month of the year is JANUARY which is 0"
		assertEquals(20, cal.get(Calendar.DAY_OF_MONTH));
		assertEquals(2012, cal.get(Calendar.YEAR));
	}

	@Test
	public void test_equals() throws Exception {
		assertFalse(DateUtilities.equals(null, DateUtilities.newCalendar(2011, 12, 9, 9, 0, 0)));
		assertFalse(DateUtilities.equals(DateUtilities.newCalendar(2011, 12, 9, 9, 0, 0), null));
		assertFalse(DateUtilities.equals(null, null));
		assertFalse(DateUtilities.equals(DateUtilities.newCalendar(2010, 12, 9, 9, 0, 0), DateUtilities.newCalendar
			(2011, 12, 9, 9, 0, 0)));
		assertTrue(DateUtilities.equals(DateUtilities.newCalendar(2011, 12, 9, 9, 0, 0), DateUtilities.newCalendar
			(2011, 12, 9, 9, 0, 0)));
	}

	@Test
	public void testCalendarWithFirstDayOfYear() throws Exception {

		Calendar firstDayOfYear = DateUtilities.getCalendarWithFirstDayOfYear(Calendar.getInstance(), Constants
			.EST_TIME_ZONE);
		assertNotNull(firstDayOfYear);
		assertEquals(firstDayOfYear.get(Calendar.MONTH), Calendar.JANUARY);
		assertEquals(firstDayOfYear.get(Calendar.DAY_OF_MONTH), 1);
	}

	@Test
	public void testCalendarWithLastDayOfYear() throws Exception {

		Calendar lastDayOfYear = DateUtilities.getCalendarWithLastDayOfYear(Calendar.getInstance(), TimeZone
			.getTimeZone("UTC"));
		assertNotNull(lastDayOfYear);
		assertEquals(lastDayOfYear.get(Calendar.MONTH), Calendar.DECEMBER);
		assertEquals(lastDayOfYear.get(Calendar.DAY_OF_MONTH), 31);
		assertEquals(lastDayOfYear.get(Calendar.MINUTE), 59);
		assertEquals(lastDayOfYear.get(Calendar.HOUR), 11);
		assertEquals(lastDayOfYear.get(Calendar.SECOND), 59);
	}

	@Test
	public void testGetLastDayOfMonth() throws Exception {

		Calendar lastDayOfMonth = DateUtilities.newCalendar(2012, Calendar.MARCH, 13, 9, 0, 0);
		assertEquals(DateUtilities.getLastDayOfTheMonth(lastDayOfMonth), 31);

		lastDayOfMonth = DateUtilities.newCalendar(2012, Calendar.APRIL, 13, 9, 0, 0);
		assertEquals(DateUtilities.getLastDayOfTheMonth(lastDayOfMonth), 30);

		lastDayOfMonth = DateUtilities.newCalendar(2012, Calendar.FEBRUARY, 13, 9, 0, 0);
		assertTrue(DateUtilities.getLastDayOfTheMonth(lastDayOfMonth) == 28 || DateUtilities.getLastDayOfTheMonth
			(lastDayOfMonth) == 29);
	}

	@Test
	public void testGetCalendarWithFirstDayOfTheMonth() {
		Calendar calendar = DateUtilities.newCalendar(2012, Calendar.MARCH, 27, 9, 0, 0);
		Calendar result = DateUtilities.getCalendarWithFirstDayOfTheMonth(calendar, TimeZone.getTimeZone("UTC"));
		assertEquals(1, result.get(Calendar.DAY_OF_MONTH));
		assertEquals(0, result.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, result.get(Calendar.MINUTE));
		assertEquals(0, result.get(Calendar.SECOND));
	}

	@Test
	public void testGetCalendarWithFirstDayOfTheWeek() {
		Calendar calendar = DateUtilities.newCalendar(2012, Calendar.MARCH, 27, 0, 0, 0);
		Calendar result = DateUtilities.getCalendarWithFirstDayOfWeek(calendar, TimeZone.getTimeZone("UTC"));
		assertEquals(26, result.get(Calendar.DAY_OF_MONTH));

	}

	@Test
	public void testGetPreviousMonth() {
		Calendar march2012 = DateUtilities.newCalendar(2012, Calendar.MARCH, 27, 9, 0, 0);
		Calendar february2012 = DateUtilities.getPreviousMonth(march2012);
		assertEquals(Calendar.FEBRUARY, february2012.get(Calendar.MONTH));
		assertEquals(march2012.get(Calendar.DAY_OF_MONTH), february2012.get(Calendar.DAY_OF_MONTH));
		assertEquals(march2012.get(Calendar.YEAR), february2012.get(Calendar.YEAR));

		Calendar january2012 = DateUtilities.newCalendar(2012, Calendar.JANUARY, 5, 9, 0, 0);
		Calendar december2011 = DateUtilities.getPreviousMonth(january2012);
		assertEquals(Calendar.DECEMBER, december2011.get(Calendar.MONTH));
		assertEquals(january2012.get(Calendar.DAY_OF_MONTH), december2011.get(Calendar.DAY_OF_MONTH));
		assertEquals(2011, december2011.get(Calendar.YEAR));
	}

	@Test
	public void testGetDurationBreakdown() {
		Calendar after = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar before = (Calendar) after.clone();
		after.add(Calendar.HOUR_OF_DAY, 4);
		after.add(Calendar.MINUTE, 30);
		long millis = DateUtilities.getDuration(before, after);
		String duration = DateUtilities.getDurationBreakdown(millis);
		assertEquals("0d 4h 30m", duration);

		after = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		before = (Calendar) after.clone();
		after.add(Calendar.DAY_OF_MONTH, 1);
		after.add(Calendar.HOUR_OF_DAY, 4);
		after.add(Calendar.MINUTE, 30);
		millis = DateUtilities.getDuration(before, after);
		duration = DateUtilities.getDurationBreakdown(millis);
		assertEquals("1d 4h 30m", duration);
	}

	@Test
	public void testGetDurationHours() {
		Calendar after = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar before = (Calendar) after.clone();
		after.add(Calendar.HOUR_OF_DAY, 4);
		after.add(Calendar.MINUTE, 30);
		long millis = DateUtilities.getDuration(before, after);
		String duration = DateUtilities.getDurationBreakdownHours(millis);
		assertEquals("4h 30m", duration);

		after = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		before = (Calendar) after.clone();
		after.add(Calendar.DAY_OF_MONTH, 1);
		after.add(Calendar.HOUR_OF_DAY, 4);
		after.add(Calendar.MINUTE, 30);
		millis = DateUtilities.getDuration(before, after);
		duration = DateUtilities.getDurationBreakdownHours(millis);
		assertEquals("28h 30m", duration);
	}

	@Test
	public void testGetMidnight() {
		String timeZone = Constants.WM_TIME_ZONE;
		Calendar midnightToday = DateUtilities.getMidnightTodayRelativeToTimezone(TimeZone.getTimeZone(timeZone));
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_YEAR, 1);
		Calendar midnightTomorrow = DateUtilities.getMidnightRelativeToTimezone(tomorrow, TimeZone.getTimeZone(timeZone));

		assertTrue(midnightToday.equals(DateUtilities.getMidnightTodayRelativeToTimezone(timeZone)));
		assertTrue(midnightTomorrow.equals(DateUtilities.getMidnightTomorrowRelativeToTimezone(timeZone)));
	}

	@Test
	public void testGetMidnightFromCalendar() {
		TimeZone tz = TimeZone.getTimeZone("US/Eastern");

		Calendar utc = new DateTime(2012, 11, 11, 11, 12, 0, 0, DateTimeZone.UTC).toCalendar(Locale.ENGLISH);

		int offsetHours = tz.getOffset(utc.getTimeInMillis());

		Calendar midnightUtc = DateUtilities.getMidnight(utc);
		Calendar midnightLocal = DateUtilities.getMidnightRelativeToTimezone(utc, tz);

		assertEquals(offsetHours, midnightUtc.getTimeInMillis() - midnightLocal.getTimeInMillis());
	}

	@Test
	public void testGetMidnightNextDay() {
		TimeZone tz = TimeZone.getTimeZone("US/Eastern");

		Calendar utc = new DateTime(2012, 11, 11, 11, 12, 0, 0, DateTimeZone.UTC).toCalendar(Locale.ENGLISH);

		int offsetHours = tz.getOffset(utc.getTimeInMillis());

		Calendar midnightUtc = DateUtilities.getMidnightNextDay(utc);
		Calendar midnightLocal = DateUtilities.getMidnightNextDayRelativeToTimezone(utc, tz);

		assertEquals(offsetHours, midnightUtc.getTimeInMillis() - midnightLocal.getTimeInMillis());
	}

	@Test
	public void testGetWeekdayOrdinalByMonth() throws Exception {
		DateTime date1 = new DateTime(2012, 6, 15, 0, 0, 0, 0, DateTimeZone.UTC); // third Friday
		DateTime date2 = new DateTime(2012, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);  // first Sunday
		DateTime date3 = new DateTime(2012, 2, 29, 0, 0, 0, 0, DateTimeZone.UTC); // fifth Wed
		DateTime date4 = new DateTime(2012, 1, 31, 0, 0, 0, 0, DateTimeZone.UTC); // fifth Tuesday
		DateTime date5 = new DateTime(2012, 1, 16, 0, 0, 0, 0, DateTimeZone.UTC); // third Monday

		assertEquals(3, DateUtilities.getWeekdayOrdinalByMonth(date1));
		assertEquals(1, DateUtilities.getWeekdayOrdinalByMonth(date2));
		assertEquals(5, DateUtilities.getWeekdayOrdinalByMonth(date3));
		assertEquals(5, DateUtilities.getWeekdayOrdinalByMonth(date4));
		assertEquals(3, DateUtilities.getWeekdayOrdinalByMonth(date5));

		DateTimeZone pst = DateTimeZone.forID("America/Los_Angeles");
		assertEquals(2, DateUtilities.getWeekdayOrdinalByMonth(date1.withZone(pst)));
		assertEquals(5, DateUtilities.getWeekdayOrdinalByMonth(date2.withZone(pst)));
		assertEquals(4, DateUtilities.getWeekdayOrdinalByMonth(date3.withZone(pst)));
		assertEquals(5, DateUtilities.getWeekdayOrdinalByMonth(date4.withZone(pst)));
		assertEquals(3, DateUtilities.getWeekdayOrdinalByMonth(date5.withZone(pst)));
	}

	@Test
	public void testIntervalContains() {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		Calendar instance = Calendar.getInstance();

		//Testing positive case
		start.add(Calendar.MONTH, -1);
		instance.add(Calendar.DAY_OF_MONTH, -5);

		assertTrue(DateUtilities.intervalContains(start, end, instance));

		start = null;
		end = null;

		//Testing null
		assertFalse(DateUtilities.intervalContains(start, end, instance));

		//Testing the start being after the end
		start = Calendar.getInstance();
		end = Calendar.getInstance();
		end.add(Calendar.MONTH, -1);

		assertFalse(DateUtilities.intervalContains(start, end, instance));
	}

	@Test
	public void getHoursAndMinutesTest() {
		Period period = DateUtilities.getHoursAndMinutes(1.33F);
		assertEquals(1, period.getHours());
		assertEquals(20, period.getMinutes());

		period = DateUtilities.getHoursAndMinutes(5.99F);
		assertEquals(5, period.getHours());
		assertEquals(59, period.getMinutes());

		period = DateUtilities.getHoursAndMinutes(null);
		assertNull(period);
	}

	@Test
	public void getDecimalHoursTest() {
		assertEquals(1.33D, DateUtilities.getDecimalHours(1, 20), 0.001);
		assertEquals(1D, DateUtilities.getDecimalHours(1, null), 0.001);
		assertEquals(0.33D, DateUtilities.getDecimalHours(null, 20), 0.001);
		assertEquals(1D, DateUtilities.getDecimalHours(1, 0), 0.001);
		assertEquals(2.67D, DateUtilities.getDecimalHours(1, 100), 0.001);

		assertEquals(0.00D, DateUtilities.getDecimalHours(null), 0.001);
		assertEquals(2.67D, DateUtilities.getDecimalHours(new Period(1, 100, 0, 0)), 0.001);

		assertEquals(0.33333333D, DateUtilities.getDecimalHours(0, 20, 8), 0.001);
		assertEquals(0.33D, DateUtilities.getDecimalHours(0, 20, null), 0.001);
	}


	@Test
	public void changeTimeZoneRetainFields_success() {
		DateTime date = new DateTime(2012, 6, 15, 13, 0, 0, 0, DateTimeZone.UTC);
		DateTimeZone tz = DateTimeZone.forID(Constants.WM_TIME_ZONE);
		long converted = DateUtilities.changeTimeZoneRetainFields(date.getMillis(), tz.getID());
		assertEquals(date.getMillis(), converted + tz.getOffset(date));
	}

	@Test
	public void changeTimeZoneRetainFields_nullTimeZone_UseEST() {
		DateTime date = new DateTime(2012, 6, 15, 13, 0, 0, 0, DateTimeZone.UTC);
		DateTimeZone tz = DateTimeZone.forID(Constants.WM_TIME_ZONE);
		long converted = DateUtilities.changeTimeZoneRetainFields(date.getMillis(), null);
		assertEquals(date.getMillis(), converted + tz.getOffset(date));
	}

	@Test
	public void changeTimeZoneFromCalendar() {

		/* UTC now time */
		long time = Calendar.getInstance().getTimeInMillis();
		Calendar utcTime = new DateTime(time, DateTimeZone.UTC).toCalendar(Locale.ENGLISH);

		/* Change to UTC - Shouldn't change anything */
		Calendar utcTimeChanged = DateUtilities.changeTimeZone(utcTime, DateTimeZone.UTC.getID());

		compareCalendarFields(utcTime, utcTimeChanged);

		utcTime = new DateTime(time, DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
		TimeZone tz = TimeZone.getTimeZone("America/Los Angeles");

		/* Change the timeZone to Los Angeles */
		utcTimeChanged = DateUtilities.changeTimeZone(utcTime, tz.getID());
		/* If the time zone change was correct, this will revert all fields to UTC time */
		utcTime.add(Calendar.MILLISECOND, tz.getOffset(utcTimeChanged.getTimeInMillis()));

		compareCalendarFields(utcTime, utcTimeChanged);

		utcTime = new DateTime(time, DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
		tz = TimeZone.getTimeZone("America/Montevideo");

		/* Change the timeZone to Montevideo */
		utcTimeChanged = DateUtilities.changeTimeZone(utcTime, tz.getID());
		/* If the time zone change was correct, this will revert all fields to UTC time */
		utcTime.add(Calendar.MILLISECOND, tz.getOffset(utcTimeChanged.getTimeInMillis()));

		compareCalendarFields(utcTime, utcTimeChanged);

	}

	@Test
	public void changeTimeZoneFromMillis() {

		/* UTC now time */
		long time = Calendar.getInstance(DateTimeZone.UTC.toTimeZone()).getTimeInMillis();
		Calendar utcTime = new DateTime(time, DateTimeZone.UTC).toCalendar(Locale.ENGLISH);

		/* Change to UTC - Should leave it as it is */
		Calendar utcTimeChanged = DateUtilities.getCalendarFromDate(DateUtilities.changeTimeZone(time, DateTimeZone
			.UTC.getID()));

		compareCalendarFields(utcTime, utcTimeChanged);

		utcTime = new DateTime(time, DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
		TimeZone tz = TimeZone.getTimeZone("America/Los Angeles");

		/* Change the timeZone to Los Angeles */
		utcTimeChanged = DateUtilities.getCalendarFromDate(DateUtilities.changeTimeZone(time, tz.getID()));
		/* If the time zone change was correct, this will revert all fields to UTC time */
		utcTime.add(Calendar.MILLISECOND, tz.getOffset(utcTimeChanged.getTimeInMillis()));
		compareCalendarFields(utcTime, utcTimeChanged);

		utcTime = new DateTime(time, DateTimeZone.UTC).toCalendar(Locale.ENGLISH);
		tz = TimeZone.getTimeZone("America/Montevideo");

		/* Change the timeZone to Montevideo */
		utcTimeChanged = DateUtilities.getCalendarFromDate(DateUtilities.changeTimeZone(time, tz.getID()));
		/* If the time zone change was correct, this will revert all fields to UTC time */
		utcTime.add(Calendar.MILLISECOND, tz.getOffset(utcTimeChanged.getTimeInMillis()));
		compareCalendarFields(utcTime, utcTimeChanged);
	}

	private void compareCalendarFields(Calendar c1, Calendar c2){
		assertEquals(c1.get(Calendar.YEAR), c2.get(Calendar.YEAR));
		assertEquals(c1.get(Calendar.MONTH), c2.get(Calendar.MONTH));
		assertEquals(c1.get(Calendar.DAY_OF_MONTH), c2.get(Calendar.DAY_OF_MONTH));
		assertEquals(c1.get(Calendar.HOUR), c2.get(Calendar.HOUR));
		assertEquals(c1.get(Calendar.MINUTE), c2.get(Calendar.MINUTE));
		assertEquals(c1.get(Calendar.SECOND), c2.get(Calendar.SECOND));
	}

	@Test
	public void testGetDaysBetween() {
		Calendar from = DateUtilities.newCalendar(2012, 1, 15, 3, 0, 0);
		Calendar to = DateUtilities.newCalendar(2012, 1, 30, 0, 0, 0);
		assertEquals(DateUtilities.getDaysBetween(from, to), 14);


		from = DateUtilities.newCalendar(2012, 1, 30, 3, 0, 0);
		to = DateUtilities.newCalendar(2012, 1, 30, 2, 0, 0);
		assertEquals(DateUtilities.getDaysBetween(from, to), 0);

	}

	@Test
	public void testGetCalendarWithLastDayOfThePreviousMonth() {
		Calendar c = DateUtilities.newCalendar(2012, 2, 2, 0, 0, 0);
		Calendar previousMonth = DateUtilities.getCalendarWithLastDayOfThePreviousMonth(c);
		assertTrue(previousMonth.get(Calendar.MONTH) == Calendar.FEBRUARY);
		assertTrue(previousMonth.get(Calendar.DAY_OF_MONTH) == 29);
	}

	@Test
	public void testGetCalendarWithLastMinuteOfDay() {
		Calendar c = DateUtilities.newCalendar(2012, 2, 2, 0, 0, 0, TimeZone.getTimeZone("UTC"));
		Calendar lastMinuteOfDay = DateUtilities.getCalendarWithLastMinuteOfDay(c, Constants.EST_TIME_ZONE);
		assertTrue(lastMinuteOfDay.get(Calendar.MONTH) == Calendar.MARCH);
		assertTrue(lastMinuteOfDay.get(Calendar.DAY_OF_MONTH) == 2);
		assertTrue(lastMinuteOfDay.get(Calendar.HOUR) == 4);
		assertTrue(lastMinuteOfDay.get(Calendar.MINUTE) == 59);
		assertTrue(lastMinuteOfDay.get(Calendar.SECOND) == 59);
	}

	@Test
	public void testGetBetweenFromNow(){
		Calendar now = DateUtilities.getCalendarNow(Constants.WM_TIME_ZONE);
		Calendar from = DateUtilities.cloneCalendar(now);

		// The 30 minutes hack is so we don't have a problem when test run slowly

		Random rand = new Random();
		int hourOffset = rand.nextInt(10);
		from.add(Calendar.HOUR_OF_DAY, -hourOffset);
		from.add(Calendar.MINUTE, -30);
		assertEquals(hourOffset, DateUtilities.getHoursBetweenFromNow(from));

		now = DateUtilities.getCalendarNow(Constants.WM_TIME_ZONE);
		from = DateUtilities.cloneCalendar(now);
		hourOffset = rand.nextInt(10);
		from.add(Calendar.HOUR_OF_DAY, hourOffset);
		from.add(Calendar.MINUTE, 30);
		assertEquals(-hourOffset, DateUtilities.getHoursBetweenFromNow(from));

		now = DateUtilities.getCalendarNow(Constants.WM_TIME_ZONE);
		from = DateUtilities.cloneCalendar(now);
		int dayOffset = rand.nextInt(10);
		from.add(Calendar.DAY_OF_MONTH, -dayOffset);
		from.add(Calendar.MINUTE, -30);
		assertEquals(dayOffset, DateUtilities.getDaysBetweenFromNow(from));

		now = DateUtilities.getCalendarNow(Constants.WM_TIME_ZONE);
		from = DateUtilities.cloneCalendar(now);
		dayOffset = rand.nextInt(10);
		from.add(Calendar.DAY_OF_MONTH, dayOffset);
		from.add(Calendar.MINUTE, 30);
		assertEquals(-dayOffset, DateUtilities.getDaysBetweenFromNow(from));

	}

	@Test
	public void testIsSameDay() {
		Calendar from = DateUtilities.newCalendar(2012, 1, 15, 3, 0, 0);
		Calendar to = DateUtilities.newCalendar(2012, 1, 30, 0, 0, 0);
		assertFalse(DateUtilities.isSameDay(from, to));


		from = DateUtilities.newCalendar(2012, 1, 30, 3, 0, 0);
		to = DateUtilities.newCalendar(2012, 1, 30, 2, 0, 0);
		assertTrue(DateUtilities.isSameDay(from, to));
	}

	@Test
	public void getCalendarWithLastDayOfTheMonthWithMinimumTime_success() {
		Calendar now = DateUtilities.newCalendar(2013, 5, 5, 5, 5, 0, TimeZone.getTimeZone("EST"));
		now = DateUtilities.getCalendarWithLastDayOfTheMonthWithMinimumTime(now, TimeZone.getTimeZone("UTC"));
		assertEquals(now.get(Calendar.MONTH), 5);
		assertEquals(now.get(Calendar.DAY_OF_MONTH), 30);
		assertEquals(now.get(Calendar.HOUR_OF_DAY), 0);
		assertEquals(now.get(Calendar.MINUTE), 0);

	}

	@Test
	public void getCurrentYear() {
		Calendar now = Calendar.getInstance();
		assertEquals(DateUtilities.getCurrentYear(), now.get(Calendar.YEAR));
	}


	@Test
	public void test_setSpecificTime(){
		Calendar date = DateUtilities.getCalendarFromDate(DateUtilities.getNow());
		Calendar time = DateUtilities.getMidnightToday();
		Calendar desired = DateUtilities.setSpecificTime(date, time);
		assertEquals(desired.getTimeZone(), time.getTimeZone());
		assertEquals(desired.get(Calendar.HOUR_OF_DAY), time.get(Calendar.HOUR_OF_DAY));
		assertEquals(desired.get(Calendar.MINUTE), time.get(Calendar.MINUTE));
		assertEquals(desired.get(Calendar.YEAR), date.get(Calendar.YEAR));
		assertEquals(desired.get(Calendar.MONTH), date.get(Calendar.MONTH));
		assertEquals(desired.get(Calendar.DAY_OF_WEEK), date.get(Calendar.DAY_OF_WEEK));
	}

	@Test
	public void getSecondsBetween_withNullArgs_NotNull() {
		assertNotNull(DateUtilities.getSecondsBetween(null, null));
	}

	@Test
	public void getSecondsBetween_TwoYearGap_FitsInInt() {
		newRange[DateUtilities.TO].set(Calendar.YEAR, 2001);
		long actual = DateUtilities.getSecondsBetween(newRange[DateUtilities.FROM], newRange[DateUtilities.TO]);
		assertEquals(63158400, actual);
	}

	@Test(expected = ArithmeticException.class)
	public void getSecondsBetween_LargeGap_DoesNotFitInInt() {
		newRange[DateUtilities.TO].set(Calendar.YEAR, 2150);
		DateUtilities.getSecondsBetween(newRange[DateUtilities.FROM], newRange[DateUtilities.TO]);
	}

	@Test
	public void secondsToDays_success() {
		assertEquals(1, DateUtilities.secondsToDays(Constants.DAY_IN_SECONDS));
	}

	@Test
	public void format_WithNullParams_ReturnEmptyString() {
		assertEquals(StringUtils.EMPTY, DateUtilities.format("mm-yy-dd", (Calendar)null, null));
		assertNotNull(DateUtilities.format("mm-yy-dd", Calendar.getInstance(), "New York"));
	}


	@Test
	public void getDateRange_NullCurRange() {
		Calendar[] range = DateUtilities.getWidestDateRange(null, newRange);

		assertEquals(newRange[DateUtilities.FROM], range[DateUtilities.FROM]);
		assertEquals(newRange[DateUtilities.TO], range[DateUtilities.TO]);
	}

	@Test
	public void getDateRange_NullValuesInCurRange() {
		Calendar[] range = DateUtilities.getWidestDateRange(curRange, newRange);

		assertEquals(newRange[DateUtilities.FROM], range[DateUtilities.FROM]);
		assertEquals(newRange[DateUtilities.TO], range[DateUtilities.TO]);
	}

	@Test
	public void getDateRange_CurRangeFromLessThanNewRangeFrom() {
		curRange[DateUtilities.FROM] = Calendar.getInstance();
		curRange[DateUtilities.FROM].set(1998, Calendar.DECEMBER, 31);

		Calendar[] range = DateUtilities.getWidestDateRange(curRange, newRange);

		assertEquals(curRange[DateUtilities.FROM], range[DateUtilities.FROM]);
		assertEquals(newRange[DateUtilities.TO], range[DateUtilities.TO]);
	}

	@Test
	public void getDateRange_NewRangeFromLessThanCurRangeFrom() {
		curRange[DateUtilities.FROM] = Calendar.getInstance();
		curRange[DateUtilities.FROM].set(1999, Calendar.FEBRUARY, 1);

		Calendar[] range = DateUtilities.getWidestDateRange(curRange, newRange);

		assertEquals(newRange[DateUtilities.FROM], range[DateUtilities.FROM]);
		assertEquals(newRange[DateUtilities.TO], range[DateUtilities.TO]);
	}

	@Test
	public void getDateRange_CurRangeToLessThanNewRangeTo() {
		curRange[DateUtilities.TO] = Calendar.getInstance();
		curRange[DateUtilities.TO].set(1998, Calendar.DECEMBER, 31);

		Calendar[] range = DateUtilities.getWidestDateRange(curRange, newRange);

		assertEquals(newRange[DateUtilities.FROM], range[DateUtilities.FROM]);
		assertEquals(newRange[DateUtilities.TO], range[DateUtilities.TO]);
	}

	@Test
	public void getDateRange_NewRangeToLessThanCurRangeTo() {
		curRange[DateUtilities.TO] = Calendar.getInstance();
		curRange[DateUtilities.TO].set(1999, Calendar.FEBRUARY, 1);

		Calendar[] range = DateUtilities.getWidestDateRange(curRange, newRange);

		assertEquals(newRange[DateUtilities.FROM], range[DateUtilities.FROM]);
		assertEquals(curRange[DateUtilities.TO], range[DateUtilities.TO]);
	}

	@Test
	public void getDateRange_CurRangeFromEpochZero() {
		curRange[DateUtilities.FROM] = Calendar.getInstance();
		curRange[DateUtilities.FROM].setTimeInMillis(0);

		Calendar[] range = DateUtilities.getWidestDateRange(curRange, newRange);

		assertEquals(newRange[DateUtilities.FROM], range[DateUtilities.FROM]);
	}

	@Test
	public void getDateRange_CurRangeToEpochZero() {
		curRange[DateUtilities.TO] = Calendar.getInstance();
		curRange[DateUtilities.TO].setTimeInMillis(0);

		Calendar[] range = DateUtilities.getWidestDateRange(curRange, newRange);

		assertEquals(newRange[DateUtilities.TO], range[DateUtilities.TO]);
	}

	@Test
	public void getDateRange_NewRangeFromEpochZero() {
		newRange[DateUtilities.FROM].setTimeInMillis(0);

		curRange[DateUtilities.FROM] = Calendar.getInstance();
		curRange[DateUtilities.FROM].set(1999, Calendar.FEBRUARY, 1);

		Calendar[] range = DateUtilities.getWidestDateRange(curRange, newRange);

		assertEquals(curRange[DateUtilities.FROM], range[DateUtilities.FROM]);
	}

	@Test
	public void getDateRange_NewRangeToEpochZero() {
		newRange[DateUtilities.TO].setTimeInMillis(0);

		curRange[DateUtilities.TO] = Calendar.getInstance();
		curRange[DateUtilities.TO].set(1999, Calendar.FEBRUARY, 1);

		Calendar[] range = DateUtilities.getWidestDateRange(curRange, newRange);

		assertEquals(curRange[DateUtilities.TO], range[DateUtilities.TO]);
	}

	@Test
	public void getReadableDate_firstDayIsMonday_pass() {
		assertEquals(DateUtilities.getReadableDayOfWeek(0), "Monday");
	}

	@Test
	public void getReadableDate_lastDayIsSunday_pass() {
		assertEquals(DateUtilities.getReadableDayOfWeek(6), "Sunday");
	}

	@Test
	public void getReadableDate_outsideBounds_fail() {
		assertEquals(DateUtilities.getReadableDayOfWeek(-1), "");
		assertEquals(DateUtilities.getReadableDayOfWeek(7), "");
	}

	@Test
	public void test_calendarNotInFuture_pass() {
		Calendar cal = Calendar.getInstance();
		assertFalse(DateUtilities.isInFuture(cal));
	}

	@Test
	public void test_calendarIsInFuture_fail() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 15);
		assertTrue(DateUtilities.isInFuture(cal));
	}

	@Test
	public void testNotInFuture_WithBuffer_pass() {
		Calendar cal = Calendar.getInstance();
		assertFalse(DateUtilities.isInFutureWithBuffer(cal, 5, Constants.MINUTE));
	}

	@Test
	public void testNotInFuture_WithSameBuffer_pass() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 5);
		assertFalse(DateUtilities.isInFutureWithBuffer(cal, 5, Constants.MINUTE));
	}

	@Test
	public void testIsInFuture_WithBuffer_fail() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 15);
		assertTrue(DateUtilities.isInFutureWithBuffer(cal, 5, Constants.MINUTE));
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_Jan(){
		assertEquals(DateUtilities.newCalendar(2012, 0, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
			DateUtilities.getCalendarWithBeginningOfQuarter(
				DateUtilities.newCalendar(2012, 0, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_Feb(){
		assertEquals(DateUtilities.newCalendar(2012, 0, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
			DateUtilities.getCalendarWithBeginningOfQuarter(
				DateUtilities.newCalendar(2012, 1, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_Mar(){
		assertEquals(DateUtilities.newCalendar(2012, 0, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
			DateUtilities.getCalendarWithBeginningOfQuarter(
				DateUtilities.newCalendar(2012, 2, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_Apr(){
		assertEquals(DateUtilities.newCalendar(2012, 3, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
			DateUtilities.getCalendarWithBeginningOfQuarter(
				DateUtilities.newCalendar(2012, 3, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_May(){
		assertEquals(DateUtilities.newCalendar(2012, 3, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
			DateUtilities.getCalendarWithBeginningOfQuarter(
				DateUtilities.newCalendar(2012, 4, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_Jun(){
		assertEquals(DateUtilities.newCalendar(2012, 3, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
			DateUtilities.getCalendarWithBeginningOfQuarter(
				DateUtilities.newCalendar(2012, 5, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_Jul(){
		assertEquals(DateUtilities.newCalendar(2012, 6, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
				DateUtilities.getCalendarWithBeginningOfQuarter(
						DateUtilities.newCalendar(2012, 6, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_Aug(){
		assertEquals(DateUtilities.newCalendar(2012, 6, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
				DateUtilities.getCalendarWithBeginningOfQuarter(
						DateUtilities.newCalendar(2012, 7, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_Sep(){
		assertEquals(DateUtilities.newCalendar(2012, 6, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
				DateUtilities.getCalendarWithBeginningOfQuarter(
						DateUtilities.newCalendar(2012, 8, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_Oct(){
		assertEquals(DateUtilities.newCalendar(2012, 9, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
				DateUtilities.getCalendarWithBeginningOfQuarter(
						DateUtilities.newCalendar(2012, 9, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_Nov(){
		assertEquals(DateUtilities.newCalendar(2012, 9, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
				DateUtilities.getCalendarWithBeginningOfQuarter(
						DateUtilities.newCalendar(2012, 10, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void test_getCalendarWithBeginningOfQuarterWithMonth_Dec(){
		assertEquals(DateUtilities.newCalendar(2012, 9, 1, 0, 0, 0, TimeZone.getTimeZone("UTC")).getTimeInMillis(),
				DateUtilities.getCalendarWithBeginningOfQuarter(
						DateUtilities.newCalendar(2012, 11, 13, 9, 0, 0), TimeZone.getTimeZone("UTC")).getTimeInMillis());
	}

	@Test
	public void getMonthsBetween_success() {
		Calendar today = DateUtilities.newCalendar(2014, Calendar.MAY, 1, 0, 0, 0);
		Calendar july = DateUtilities.newCalendar(2014, Calendar.JULY, 1, 0, 0, 0);
		assertEquals(DateUtilities.getMonthsBetween(today, july), 2);
	}

	@Test
	public void getMonthsBetween_withOneFullMonthAndFraction() {
		Calendar today = DateUtilities.newCalendar(2014, Calendar.MAY, 1, 18, 0, 0);
		Calendar july = DateUtilities.newCalendar(2014, Calendar.JULY, 1, 0, 0, 0);
		assertEquals(DateUtilities.getMonthsBetween(today, july), 1);
	}

	@Test
	public void getMonthsBetween_withMidnight_success() {
		Calendar today = DateUtilities.newCalendar(2014, Calendar.MAY, 1, 18, 0, 0);
		Calendar july = DateUtilities.newCalendar(2014, Calendar.JULY, 1, 0, 0, 0);
		assertEquals(DateUtilities.getMonthsBetween(DateUtilities.getMidnight(today), july), 2);
	}

	@Test
	public void addMinutes_success() {
		Calendar today = DateUtilities.newCalendar(2014, Calendar.MAY, 1, 18, 5, 0);
		Calendar oneMinuteAgo = DateUtilities.newCalendar(2014, Calendar.MAY, 1, 18, 4, 0);
		Calendar result = DateUtilities.addMinutes(today, -1);
		assertEquals(result.get(Calendar.HOUR), oneMinuteAgo.get(Calendar.HOUR));
		assertEquals(result.get(Calendar.MONTH), oneMinuteAgo.get(Calendar.MONTH));
		assertEquals(result.get(Calendar.YEAR), oneMinuteAgo.get(Calendar.YEAR));
		assertEquals(result.get(Calendar.MINUTE), oneMinuteAgo.get(Calendar.MINUTE));
		assertEquals(result.get(Calendar.SECOND), oneMinuteAgo.get(Calendar.SECOND));
	}

	@Test
	public void getDaysBetween_success() {
		int days = DateUtilities.getDaysBetween(DateUtilities.newCalendar(2014, Calendar.MAY, 1, 18, 5, 0),
			DateUtilities.newCalendar(2014, Calendar.JUNE, 1, 18, 5, 0));
		assertEquals(days, 31);
	}

	@Test
	public void getDaysBetween_withReverseOrder_success() {
		int days = DateUtilities.getDaysBetween(DateUtilities.newCalendar(2014, Calendar.JUNE, 1, 18, 5, 0),
			DateUtilities.newCalendar(2014, Calendar.MAY, 1, 18, 5, 0));
		assertEquals(days, 31);
	}

	@Test
	public void equal_withNull_true() {
		DateTime d1 = null;
		DateTime d2 = null;
		assertTrue(DateUtilities.equal(d1, d2));
	}

	@Test
	public void equal_withANull_false() {
		DateTime d1 = new DateTime();
		DateTime d2 = null;
		assertFalse(DateUtilities.equal(d1, d2));
		assertFalse(DateUtilities.equal(d2, d1));
	}

	@Test
	public void equal_withANull_true() {
		DateTime d = new DateTime();
		assertTrue(DateUtilities.equal(d, d));
	}

	@Test
	public void equal_withDifferentTimes_false() {
		DateTime d1 = new DateTime("2011-01-01");
		DateTime d2 = new DateTime("2011-01-02");
		assertFalse(DateUtilities.equal(d1, d2));
	}

	@Test
	public void equal_withCalendarNull_true() {
		Calendar d1 = null;
		Calendar d2 = null;
		assertTrue(DateUtilities.equal(d1, d2));
	}

	@Test
	public void equal_withACalendarNull_false() {
		Calendar d1 = Calendar.getInstance();
		Calendar d2 = null;
		assertFalse(DateUtilities.equal(d1, d2));
		assertFalse(DateUtilities.equal(d2, d1));
	}

	@Test
	public void equal_withCalendar_true() {
		Calendar d = Calendar.getInstance();
		assertTrue(DateUtilities.equal(d, d));
	}

	@Test
	public void equal_withDifferentCalendarTimes_false() {
		Calendar d1 = Calendar.getInstance();
		Calendar d2 = Calendar.getInstance();
		d2.add(Calendar.DAY_OF_WEEK, 1);
		assertFalse(DateUtilities.equal(d1, d2));
	}

	@Test
	public void shouldTestCalendarMillisWithTimeZone() {
		final Long millis = 1475332200000L; // 10/01/2016, 10:30:00 AM EDT; 10/01/2016 14:30:00 GMT
		final Calendar dateEDT = DateUtilities.getCalendarFromMillis(millis, "US/Eastern");
		final Calendar dateUTC = DateUtilities.getCalendarFromMillis(millis, "UTC");

		assertEquals(9, dateEDT.get(Calendar.MONTH)); // 0 based
		assertEquals(9, dateUTC.get(Calendar.MONTH));
		assertEquals(1, dateEDT.get(Calendar.DAY_OF_MONTH));
		assertEquals(1, dateUTC.get(Calendar.DAY_OF_MONTH));
		assertEquals(2016, dateEDT.get(Calendar.YEAR));
		assertEquals(2016, dateUTC.get(Calendar.YEAR));
		assertEquals(10, dateEDT.get(Calendar.HOUR_OF_DAY));
		assertEquals(14, dateUTC.get(Calendar.HOUR_OF_DAY));
		assertEquals(30, dateEDT.get(Calendar.MINUTE));
		assertEquals(30, dateUTC.get(Calendar.MINUTE));
	}

	@Test
	public void testIso8601Format() {
		final long millis = 1475706791000L;  // Wed, 05 Oct 2016 22:33:11 GMT
		final long millis2 = 1475659991000L; // Wed, 05 Oct 2016 09:33:11 GMT
		assertEquals("2016-10-05T22:33:11Z", DateUtilities.formatISO8601Instant(millis));
		assertEquals("2016-10-05T09:33:11Z", DateUtilities.formatISO8601Instant(millis2));
	}
}
