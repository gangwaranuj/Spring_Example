package com.workmarket.testutils.matchers;

import java.util.Calendar;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.ibm.icu.util.TimeZone;
import com.workmarket.utility.DateUtilities;

public class CommonMatchers {
	public static Matcher<Calendar> at10amGMTorUTC() {
		return new BaseMatcher<Calendar>() {
			Calendar date;
			
			@Override
			public boolean matches(final Object item) {
				date = (Calendar) item;

				return (("GMT".equals(date.getTimeZone().getID()) ||
						"UTC".equals(date.getTimeZone().getID())) && 
						date.get(Calendar.HOUR_OF_DAY) == 10);

			}

			@Override
			public void describeTo(final Description description) {
				if (!TimeZone.getTimeZone("GMT").equals(date.getTimeZone()))
					description.appendText("expected timezone: GMT or UTC - actual: " + date.getTimeZone().getID());
				
				if (date.get(Calendar.HOUR_OF_DAY) != 10)
					description.appendText("expected hour of the day: 10 - actual: " + date.get(Calendar.HOUR_OF_DAY));
			}
		};
	}
	
	public static Matcher<Calendar> dayAfter(final Calendar date) {
		return new BaseMatcher<Calendar>() {
			Calendar startDate = date;
			Calendar endDate;
			
			@Override
			public boolean matches(final Object item) {
				endDate = (Calendar) item;

				return (DateUtilities.getDaysBetween(startDate, endDate, false) == 1);

			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("the given date: " + DateUtilities.formatDateForEmail(endDate) + " should be day after: " + DateUtilities.formatDateForEmail(startDate));
			}
		};
	}
	
	public static Matcher<Calendar> dayOfWeek(final int dow) {
		return new BaseMatcher<Calendar>() {
			private int day_of_week = dow;
			private Calendar actual;
			
			@Override
			public boolean matches(final Object item) {
				actual = (Calendar) item;
				return (actual.get(Calendar.DAY_OF_WEEK) == day_of_week);

			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("the given date does not fall on the expected day of week actual: " + actual.get(Calendar.DAY_OF_WEEK) + " expected: " + day_of_week);
			}
		};
	}
	
	public static Matcher<Calendar> twoWeeksAfter(final Calendar date) {
		return new BaseMatcher<Calendar>() {
			Calendar startDate = date;
			Calendar endDate;
			
			@Override
			public boolean matches(final Object item) {
				endDate = (Calendar) item;

				return (DateUtilities.getDaysBetween(startDate, endDate, false) == 14);

			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("the given date: " + DateUtilities.formatDateForEmail(endDate) + " should be two weeks after: " + DateUtilities.formatDateForEmail(startDate));
			}
		};
	}
	
	public static Matcher<Calendar> weekAfter(final Calendar date) {
		return new BaseMatcher<Calendar>() {
			Calendar startDate = date;
			Calendar endDate;
			
			@Override
			public boolean matches(final Object item) {
				endDate = (Calendar) item;

				return (DateUtilities.getDaysBetween(startDate, endDate, false) == 7);

			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("the given date: " + DateUtilities.formatDateForEmail(endDate) + " should be week after: " + DateUtilities.formatDateForEmail(startDate));
			}
		};
	}

	public static Matcher<? super List<?>> isInAscendingOrder() {
		return new TypeSafeMatcher<List<?>>() {
			private List<?> list;
			private int index;

			@Override
			public void describeTo(Description description) {
				description.appendText("the given list is not in ascending order offending items at index: " + index
						+ " values: " + list.get(index) + " : " + list.get(index + 1));
			}

			@Override
			protected boolean matchesSafely(List<?> list) {
				this.list = list;

				for (int i = 0; i < list.size() - 1; i++) {
					index = i;

					Object o1 = list.get(i);
					Object o2 = list.get(i + 1);

					if (o1 == null && o2 == null)
						continue;

					if (o1 == null)
						continue;

					if (o2 == null || ((Comparable) o1).compareTo(o2) > 0)
						return false;
				}
				return true;
			}
		};
	}

	public static Matcher<? super List<?>> isInDescendingOrder() {
		return new TypeSafeMatcher<List<?>>() {
			private List<?> list;
			private int index;

			@Override
			public void describeTo(Description description) {
				description.appendText("the given list is not in descending order offending items at index: " + index
						+ " values: " + list.get(index) + " : " + list.get(index + 1));
			}

			@Override
			protected boolean matchesSafely(List<?> list) {
				this.list = list;

				for (int i = 0; i < list.size() - 1; i++) {
					index = i;

					Object o1 = list.get(i);
					Object o2 = list.get(i + 1);

					if (o1 == null && o2 == null)
						continue;

					if (o2 == null)
						continue;

					if (o1 == null || ((Comparable) o1).compareTo(o2) < 0)
						return false;
				}
				return true;
			}
		};
	}

	public static Matcher<String> isUpperCase() {
		return new BaseMatcher<String>() {
			private String actual;

			@Override
			public boolean matches(final Object item) {
				actual = (String) item;

				for (int i = 0; i < actual.length(); i++) {
					if (Character.isLetter(actual.charAt(i)) && !Character.isUpperCase(actual.charAt(i)))
						return false;
				}

				return true;
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("the given string is not in upper case: " + actual);
			}
		};
	}

	public static Matcher<String> matchesRegex(final String regex) {
		return new BaseMatcher<String>() {
			private String actual;

			@Override
			public boolean matches(final Object item) {
				actual = (String) item;

				return actual.matches(regex);
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("the given string: " + actual + " does not not match reqular expression: " + regex);
			}
		};
	}
}
