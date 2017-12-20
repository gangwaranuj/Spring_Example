package com.workmarket.web.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

import static com.workmarket.web.helpers.AutotaskWorkFormHelper.*;

/**
 * User: micah
 * Date: 9/21/13
 * Time: 12:40 AM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class AutotaskWorkFormHelperTest {
	@Test
	public void getFullDateParts_HappyPath() throws Exception {
		Map<String, Date> parts = getFullDateParts("9/18/2013 3:00 PM");
		assertEquals(DATE_FORMAT.parse("9/18/2013"), parts.get("date"));
		assertEquals(TIME_FORMAT.parse("3:00 PM"), parts.get("time"));
	}

	@Test
	public void getFullDateParts_BadFormat() throws Exception {
		Map<String, Date> parts = getFullDateParts("9/18/2013 3:00PM");
		assertNotSame(DATE_FORMAT.parse("9/18/2013"), parts.get("date"));
	}
}
