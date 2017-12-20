package com.workmarket.domains.work.model;

import com.workmarket.domains.model.DateRange;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class WorkScheduleTest {

	private WorkSchedule workSchedule;
	private WorkSchedule workSchedule2;

	@Before
	public void setUp() throws Exception {
		Calendar from = DateUtilities.newCalendar(2014, Calendar.JANUARY, 1, 0, 0, 0, TimeZone.getTimeZone("UTC"));
		Calendar through = DateUtilities.newCalendar(2014, Calendar.JANUARY, 15, 0, 0, 0, TimeZone.getTimeZone("UTC"));
		workSchedule = new WorkSchedule(new DateRange(from, through));
	}

	@Test
	public void contains_withFromAndNoThroughDate_containsReturnsTrue() throws Exception {
		Calendar from = DateUtilities.newCalendar(2014, Calendar.JANUARY, 1, 0, 0, 0, TimeZone.getTimeZone("UTC"));
		workSchedule2 = new WorkSchedule(new DateRange(from));
		assertTrue(workSchedule.contains(workSchedule2));
	}

	@Test
	public void contains_withFromAndNoThroughDateAtTheEndOfTheInterval_containsReturnsTrue() throws Exception {
		Calendar from = DateUtilities.newCalendar(2014, Calendar.JANUARY, 14, 0, 0, 0, TimeZone.getTimeZone("UTC"));
		workSchedule2 = new WorkSchedule(new DateRange(from));
		assertTrue(workSchedule.contains(workSchedule2));
	}

	@Test
	public void contains_withFromAndThroughDate_containsReturnsTrue() throws Exception {
		Calendar from = DateUtilities.newCalendar(2014, Calendar.JANUARY, 9, 0, 0, 0, TimeZone.getTimeZone("UTC"));
		Calendar through = DateUtilities.newCalendar(2014, Calendar.JANUARY, 14, 0, 0, 0, TimeZone.getTimeZone("UTC"));
		workSchedule2 = new WorkSchedule(new DateRange(from, through));
		assertTrue(workSchedule.contains(workSchedule2));
	}
}