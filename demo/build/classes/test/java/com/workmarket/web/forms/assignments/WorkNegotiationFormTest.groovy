package com.workmarket.web.forms.assignments

import com.workmarket.service.business.dto.WorkNegotiationDTO
import com.workmarket.thrift.work.Work
import org.junit.Test

import java.text.DateFormat
import java.text.SimpleDateFormat

class WorkNegotiationFormTest extends GroovyTestCase {

	@Test
	void testValidScheduleNegotiationSpecificTime_success() {
		def testForm = new WorkNegotiationForm();
		testForm.schedule_negotiation = true;
		testForm.from = new Date("Fri Jun 28 2020");
		testForm.fromtime = new Date(3600000);
		assertTrue(testForm.validScheduleNegotiation("US/Eastern"));
	}


	@Test
	void testValidScheduleNegotiationSpecificTimeInPast_fail() {
		def testForm = new WorkNegotiationForm();
		testForm.schedule_negotiation = true;
		testForm.from = new Date("Fri Jun 28 2010");
		testForm.fromtime = new Date(3600000);
		assertFalse(testForm.validScheduleNegotiation("US/Eastern"));
	}

	@Test
	void testValidScheduleNegotiationWindow_success() {
		def testForm = new WorkNegotiationForm();
		testForm.schedule_negotiation = true;
		testForm.from = new Date("Fri Jun 27 2020");
		testForm.fromtime = new Date(3600000);
		testForm.to = new Date("Sat Jun 28 2020");
		testForm.totime = new Date(3600000);
		assertTrue(testForm.validScheduleNegotiation("US/Eastern"));
	}

	@Test
	void testValidScheduleNegotiationWindowCloseBeforeOpen_fail() {
		def testForm = new WorkNegotiationForm();
		testForm.schedule_negotiation = true;
		testForm.reschedule_option = "window";
		testForm.from = new Date("Sat Jun 28 2020");
		testForm.fromtime = new Date(3600000);
		testForm.to = new Date("Fri Jun 27 2020");
		testForm.totime = new Date(3600000);
		assertFalse(testForm.validScheduleNegotiation("US/Eastern"));
	}

	@Test
	void testValidScheduleNegotiationWindowOpenTimeInPast_fail() {
		def testForm = new WorkNegotiationForm();
		testForm.schedule_negotiation = true;
		testForm.reschedule_option = "window";
		testForm.from = new Date("Sat Jun 27 2010");
		testForm.fromtime = new Date(3600000);
		testForm.to = new Date("Fri Jun 28 2020");
		testForm.totime = new Date(3600000);
		assertFalse(testForm.validScheduleNegotiation("US/Eastern"));
	}


	@Test
	void testValidScheduleNegotiationWindowCloseTimeInPast_fail() {
		def testForm = new WorkNegotiationForm();
		testForm.schedule_negotiation = true;
		testForm.reschedule_option = "window";
		testForm.from = new Date("Sat Jun 28 2020");
		testForm.fromtime = new Date(3600000);
		testForm.to = new Date("Fri Jun 27 2010");
		testForm.totime = new Date(3600000);
		assertFalse(testForm.validScheduleNegotiation("US/Eastern"));
	}

	@Test
	void testValidOfferExpiration_success() {
		def testForm = new WorkNegotiationForm();
		testForm.offer_expiration = true;
		testForm.expires_on = new Date("Fri Jun 28 2020");
		testForm.expires_on_time = new Date(3600000);
		assertTrue(testForm.validOfferExpiration("US/Eastern"));
	}

	@Test
	void testValidOfferExpirationInPast_fail() {
		def testForm = new WorkNegotiationForm();
		testForm.offer_expiration = true;
		testForm.expires_on = new Date("Fri Jun 28 2010");
		testForm.expires_on_time = new Date(3600000);
		assertFalse(testForm.validOfferExpiration("US/Eastern"));
	}

	@Test
	void testUTCScheduleDate_success() {
		WorkNegotiationDTO dto = getDtoDatesByTimeZoneId("UTC");

		assertEquals("2016-12-02T22:00", dto.getScheduleFromString().substring(0,16));
		assertEquals("2016-12-02T23:00", dto.getScheduleThroughString().substring(0,16));
	}

	@Test
	void testESTScheduleDate_success() {
		WorkNegotiationDTO dto = getDtoDatesByTimeZoneId("EST");

		assertEquals("2016-12-03T03:00", dto.getScheduleFromString().substring(0,16));
		assertEquals("2016-12-03T04:00", dto.getScheduleThroughString().substring(0,16));
	}

	WorkNegotiationDTO getDtoDatesByTimeZoneId(String timeZoneId) {
		def DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneId))
		def fromDate = dateFormat.parse("2016-12-02 22:00:00");
		def toDate = dateFormat.parse("2016-12-02 23:00:00");
		def workNegotiationForm = new WorkNegotiationForm();
		workNegotiationForm.schedule_negotiation = true;
		workNegotiationForm.from = fromDate;
		workNegotiationForm.fromtime = fromDate;
		workNegotiationForm.to = toDate;
		workNegotiationForm.setTotime(toDate);
		workNegotiationForm.timeZoneId = timeZoneId;
		workNegotiationForm.flat_price = 1d;
		workNegotiationForm.per_unit_price = 1d;
		workNegotiationForm.per_hour_price = 1d;
		workNegotiationForm.max_number_of_units = 1d;
		workNegotiationForm.max_number_of_hours = 1d;
		workNegotiationForm.initial_per_hour_price = 1d;
		workNegotiationForm.initial_number_of_hours = 1d;
		workNegotiationForm.additional_per_hour_price = 1d;
		workNegotiationForm.max_blended_number_of_hours = 1d;

		return workNegotiationForm.toDTO(new Work());
	}
}
