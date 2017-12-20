package com.workmarket.web.controllers.assignments;

import com.google.common.base.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class WorkDetailsControllerTest {

	@InjectMocks WorkDetailsController workDetailsController;

	@Test
	public void getDateTime_blankDate_noDateTime() {
		final String date = "";
		final String time = "11:00am";

		final Optional<String> result = workDetailsController.getDateTime(date, time);

		assertFalse(result.isPresent());
	}

	@Test
	public void getDateTime_dateWithoutTime() {
		final String date = "11/01/2017";
		final String time = "";

		final Optional<String> result = workDetailsController.getDateTime(date, time);

		assertTrue(result.isPresent());
		assertEquals(date, result.get());
	}

	@Test
	public void getDateTime() {
		final String date = "11/01/2017";
		final String time = "11:00am";
		final String expected = date + " " + time;

		final Optional<String> result = workDetailsController.getDateTime(date, time);

		assertTrue(result.isPresent());
		assertEquals(expected, result.get());
	}

	@Test
	public void getDateTimeFormat_blankDate_noDateTime() {
		final String date = "";
		final String time = "11:00am";

		final Optional<String> result = workDetailsController.getDateTimeFormat(date, time);

		assertFalse(result.isPresent());
	}

	@Test
	public void getDateTimeFormat_dateWithoutTime() {
		final String date = "11/01/2017";
		final String time = "";

		Optional<String> result = workDetailsController.getDateTimeFormat(date, time);
		final String expected = "MM/dd/yyyy";

		assertTrue(result.isPresent());
		assertEquals(expected, result.get());
	}

	@Test
	public void getDateTimeFormat() {
		final String date = "11/01/2017";
		final String time = "11:00am";
		final String expected = "MM/dd/yyyy hh:mmaa";

		final Optional<String> result = workDetailsController.getDateTimeFormat(date, time);

		assertTrue(result.isPresent());
		assertEquals(expected, result.get());
	}


}
