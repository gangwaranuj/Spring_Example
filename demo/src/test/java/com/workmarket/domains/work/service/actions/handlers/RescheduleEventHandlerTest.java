package com.workmarket.domains.work.service.actions.handlers;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.actions.RescheduleEvent;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.validators.DateRangeValidator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RescheduleEventHandlerTest {

	private static final String ISO_DATE = "2017-01-01T00:00:00";
	private static final String PARSABLE_DATE = "01/01/2017";
	private static final String DATE_FORMAT = "MM/dd/yyyy";

	@Mock WorkService workService;
	@Mock MessageBundleHelper messageBundleHelper;
	@Mock DateRangeValidator dateRangeValidator;
	@Mock MessageBundleHelper messageHelper;

	@InjectMocks RescheduleEventHandler rescheduleEventHandler;

	RescheduleEvent event;
	AjaxResponseBuilder response;
	List<String> workNumbers = Lists.newArrayList();
	User user;
	Work work;
	TimeZone tz;


	@Before
	public void setup() {
		user = mock(User.class);
		tz = mock(TimeZone.class);

		workNumbers = mock(ArrayList.class);
		when(workNumbers.isEmpty()).thenReturn(false);

		response = mock(AjaxResponseBuilder.class);

		event = mock(RescheduleEvent.class);
		when(event.getResponse()).thenReturn(response);
		when(event.getMessageKey()).thenReturn("");
		when(event.getWorkNumbers()).thenReturn(workNumbers);
		when(event.getUser()).thenReturn(user);
		when(event.getOnBehalfOfUser()).thenReturn(user);
		when(event.isValid()).thenReturn(true);
		when(event.getStartDateTime()).thenReturn(Optional.of(PARSABLE_DATE));
		when(event.getStartDateTimeFormat()).thenReturn(Optional.of(DATE_FORMAT));
		when(event.getEndDateTime()).thenReturn(Optional.of(PARSABLE_DATE));
		when(event.getEndDateTimeFormat()).thenReturn(Optional.of(DATE_FORMAT));
		when(event.getNote()).thenReturn("Some note");
		when(tz.getTimeZoneId()).thenReturn("NY");
	}

	@Test
	public void test_handleEvent_update_schedule_without_negotiation() {
		final boolean isVoidable = true;
		List<Work> works = getWorks(isVoidable);
		when(event.getWorks()).thenReturn(works);
		rescheduleEventHandler.handleEvent(event);
		verify(workService).updateWorkSchedule(any(Work.class), any(DateRange.class), any(String.class));
	}

	@Test
	public void test_handleEvent_create_negotiation_for_previously_assigned_resource() {
		final boolean notVoidable = false;
		List<Work> works = getWorks(notVoidable);
		when(event.getWorks()).thenReturn(works);
		rescheduleEventHandler.handleEvent(event);
		verify(workService).setAppointmentTime(any(Long.class), any(DateRange.class), any(String.class));
	}

	@Test
	public void parseDateTime_absentDateTime_null() {
		final Optional<String> dateTime = Optional.of(PARSABLE_DATE);
		final Optional<String> format = Optional.absent();
		final java.util.TimeZone timeZone = mock(java.util.TimeZone.class);

		final Calendar result = rescheduleEventHandler.parseDateTime(dateTime, format, timeZone);
		assertNull(result);
	}

	@Test
	public void parseDateTime_absentFormat_null() {
		final Optional<String> dateTime = Optional.absent();
		final Optional<String> format = Optional.of(DATE_FORMAT);
		final java.util.TimeZone timeZone = mock(java.util.TimeZone.class);

		final Calendar result = rescheduleEventHandler.parseDateTime(dateTime, format, timeZone);

		assertNull(result);
	}

	@Test
	public void parseDateTime_unparseableDate_null() {
		final Optional<String> dateTime = Optional.of("some string");
		final Optional<String> format = Optional.absent();
		final java.util.TimeZone timeZone = mock(java.util.TimeZone.class);

		final Calendar result = rescheduleEventHandler.parseDateTime(dateTime, format, timeZone);

		assertNull(result);
	}

	@Ignore // DO NOT MERGE THIS
	@Test
	public void parseDateTime() {
		final Optional<String> dateTime = Optional.of(PARSABLE_DATE);
		final Optional<String> format = Optional.of(DATE_FORMAT);
		final java.util.TimeZone timeZone = mock(java.util.TimeZone.class);

		final Calendar result = rescheduleEventHandler.parseDateTime(dateTime, format, timeZone);
		final Calendar expected = DateUtilities.getCalendarFromString(ISO_DATE);

		assertTrue(DateUtilities.equal(result, expected));
	}

	private List<Work> getWorks(boolean isVoidable) {
		work = mock(Work.class);
		when(work.isVoidable()).thenReturn(isVoidable);
		when(work.getTimeZone()).thenReturn(tz);
		return ImmutableList.of(work);
	}
}
