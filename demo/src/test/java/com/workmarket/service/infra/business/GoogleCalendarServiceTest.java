package com.workmarket.service.infra.business;

import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.common.collect.ImmutableList;
import com.workmarket.dao.google.CalendarSyncSettingsDAO;
import com.workmarket.dao.google.RefreshTokenDAO;
import com.workmarket.dao.google.WorkCalendarEventDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.google.CalendarSyncSettings;
import com.workmarket.domains.model.google.RefreshToken;
import com.workmarket.domains.model.oauth.OAuthTokenProviderType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.UserService;
import com.workmarket.utility.RandomUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GoogleCalendarServiceTest {

	@Mock UserService userService;
	@Mock RefreshTokenDAO refreshTokenDAO;
	@Mock CalendarSyncSettingsDAO calendarSyncSettingsDAO;
	@Mock WorkService workService;
	@Mock WorkCalendarEventDAO workCalendarEventDAO;
	@InjectMocks GoogleCalendarServiceImpl googleCalendarService;

	private String userNumber = "0001003";

	User user;
	CalendarSyncSettings calendarSyncSettings;
	RefreshToken refreshToken;
	WorkResource workResource;
	Work work;


	@Before
	public void setup() {
		user = mock(User.class);
		calendarSyncSettings = mock(CalendarSyncSettings.class);
		workResource = mock(WorkResource.class);
		work = mock(Work.class);

		calendarSyncSettings = mock(CalendarSyncSettings.class);
		when(calendarSyncSettings.getUser()).thenReturn(user);

		when(workResource.getUser()).thenReturn(user);
		when(user.getId()).thenReturn(1L);
		when(user.getUserNumber()).thenReturn(userNumber);
		when(work.getScheduleFrom()).thenReturn(Calendar.getInstance());

		refreshToken = new RefreshToken();
		refreshToken.setRefreshToken(RandomUtilities.generateAlphaNumericString(20));

		when(userService.findUserByUserNumber(userNumber)).thenReturn(user);

		when(calendarSyncSettingsDAO.findByUser(user.getId())).thenReturn(calendarSyncSettings);

		when(refreshTokenDAO.findByUserAndProvider(user.getId(), OAuthTokenProviderType.GOOGLE_CALENDAR)).thenReturn(refreshToken);
		when(workService.findActiveWorkResource(anyLong())).thenReturn(workResource);
		when(workResource.getWork()).thenReturn(work);
		when(calendarSyncSettingsDAO.findByUser(anyLong())).thenReturn(calendarSyncSettings);
	}

	@Test
	public void filterCalendarListEntries() {
		CalendarListEntry calendarListEntry1 = new CalendarListEntry();
		calendarListEntry1.setId("id1").setSummary("summary1");
		CalendarListEntry calendarListEntry2 = new CalendarListEntry();
		calendarListEntry2.setId(null).setSummary("summary2");
		CalendarListEntry calendarListEntry3 = new CalendarListEntry();
		calendarListEntry3.setId("id3").setSummary(null);
		List<CalendarListEntry> calendarListEntries =
			ImmutableList.of(calendarListEntry1, calendarListEntry2, calendarListEntry3);

		List<CalendarListEntry> expected = ImmutableList.of(calendarListEntry1);
		List<CalendarListEntry> results = googleCalendarService.filterCalendarListEntries(calendarListEntries);
		assertEquals(expected, results);
	}

	@Test
	public void extractCalendarListItems_clientNullSafe() throws IOException {
		List<CalendarListEntry> entries = googleCalendarService.extractCalendarListItems(null);
		assertTrue(entries.isEmpty());
	}

	@Test
	public void extractCalendarListItems_calendarListNullSafe() throws IOException {
		com.google.api.services.calendar.Calendar calendar = mock(com.google.api.services.calendar.Calendar.class);
		when(calendar.calendarList()).thenReturn(null);
		List<CalendarListEntry> entries = googleCalendarService.extractCalendarListItems(calendar);
		assertTrue(entries.isEmpty());
	}

	@Test
	public void extractCalendarListItems_calendarListInnerListNullSafe() throws IOException {
		com.google.api.services.calendar.Calendar.CalendarList calendarList =
			mock(com.google.api.services.calendar.Calendar.CalendarList.class);
		when(calendarList.list()).thenReturn(null);
		com.google.api.services.calendar.Calendar calendar = mock(com.google.api.services.calendar.Calendar.class);
		when(calendar.calendarList()).thenReturn(calendarList);
		List<CalendarListEntry> entries = googleCalendarService.extractCalendarListItems(calendar);
		assertTrue(entries.isEmpty());
	}

	@Test
	public void extractCalendarListItems_calendarListInnerListExecuteNullSafe() throws IOException {
		com.google.api.services.calendar.Calendar.CalendarList.List calendarListInnerList
			= mock(com.google.api.services.calendar.Calendar.CalendarList.List.class);
		when(calendarListInnerList.execute()).thenReturn(null);
		com.google.api.services.calendar.Calendar.CalendarList calendarList =
			mock(com.google.api.services.calendar.Calendar.CalendarList.class);
		when(calendarList.list()).thenReturn(calendarListInnerList);
		com.google.api.services.calendar.Calendar calendar = mock(com.google.api.services.calendar.Calendar.class);
		when(calendar.calendarList()).thenReturn(calendarList);
		List<CalendarListEntry> entries = googleCalendarService.extractCalendarListItems(calendar);
		assertTrue(entries.isEmpty());
	}

	@Test
	public void extractCalendarListItems_calendarListEntriesNullSafe() throws IOException {
		CalendarList feed = new CalendarList();
		feed.setItems(null);
		com.google.api.services.calendar.Calendar.CalendarList.List calendarListInnerList
			= mock(com.google.api.services.calendar.Calendar.CalendarList.List.class);
		when(calendarListInnerList.execute()).thenReturn(feed);
		com.google.api.services.calendar.Calendar.CalendarList calendarList =
			mock(com.google.api.services.calendar.Calendar.CalendarList.class);
		when(calendarList.list()).thenReturn(calendarListInnerList);
		com.google.api.services.calendar.Calendar calendar = mock(com.google.api.services.calendar.Calendar.class);
		when(calendar.calendarList()).thenReturn(calendarList);
		List<CalendarListEntry> entries = googleCalendarService.extractCalendarListItems(calendar);
		assertTrue(entries.isEmpty());
	}

	@Test
	public void convertCalendarListEntryListToStringMap() {
		final String id1 = "id1";
		final String summary1 = "summary1";
		final String id2 = "id2";
		final String summary2 = "summary2";
		CalendarListEntry calendarListEntry1 = new CalendarListEntry();
		calendarListEntry1.setSummary(summary1);
		calendarListEntry1.setId(id1);
		CalendarListEntry calendarListEntry2 = new CalendarListEntry();
		calendarListEntry2.setId(id2);
		calendarListEntry2.setSummary(summary2);
		ImmutableList<CalendarListEntry> calendarListEntries = ImmutableList.of(calendarListEntry1, calendarListEntry2);

		Map<String, String> calendarMap = googleCalendarService.getCalendarMap(calendarListEntries);

		assertEquals(summary1, calendarMap.get(id1));
		assertEquals(summary2, calendarMap.get(id2));
	}

	@Test
	public void tsAuthorizedToWM_withUserNumber() {
		RefreshToken refreshToken1 = googleCalendarService.saveOrUpdateRefreshToken(refreshToken.getRefreshToken(), user.getId());
		assertNotNull(refreshToken1);
		boolean isAuthorized = googleCalendarService.isAuthorizedToWM(user.getUserNumber());
		assertTrue(isAuthorized);
	}

	@Test
	public void isAuthorizedToWM_withUserId() {
		RefreshToken refreshToken1 = googleCalendarService.saveOrUpdateRefreshToken(refreshToken.getRefreshToken(), user.getId());
		assertNotNull(refreshToken1);
		boolean isAuthorized = googleCalendarService.isAuthorizedToWM(user.getId());
		assertTrue(isAuthorized);
	}

	@Test
	public void revokeAuthorizationFromWM_byUserNumber() {
		RefreshToken refreshToken1 = googleCalendarService.saveOrUpdateRefreshToken(refreshToken.getRefreshToken(), user.getId());
		assertNotNull(refreshToken1);
		assertNotNull(refreshToken1.getRefreshToken());
		boolean isRevoked = googleCalendarService.revokeAuthorizationFromWM(user.getUserNumber());
		assertTrue(isRevoked);
		assertNull(refreshToken1.getRefreshToken());
	}

	@Test
	public void revokeAuthorizationFromWM_byUserId() {
		RefreshToken refreshToken1 = googleCalendarService.saveOrUpdateRefreshToken(refreshToken.getRefreshToken(), user.getId());
		assertNotNull(refreshToken1);
		assertNotNull(refreshToken1.getRefreshToken());
		boolean isRevoked = googleCalendarService.revokeAuthorizationFromWM(user.getId());
		assertTrue(isRevoked);
		assertNull(refreshToken1.getRefreshToken());
	}

	@Test
	public void addConfirmedAssignmentToCalendar_success() {
		googleCalendarService.addConfirmedAssignmentToCalendar(1L);
		verify(calendarSyncSettingsDAO, times(1)).findByUser(anyLong());
	}

	@Test
	public void addAssignmentToCalendar_success() {
		Event event = googleCalendarService.addAssignmentToCalendar(work, CalendarSyncSettings.CANCELLED, calendarSyncSettings);
		verify(refreshTokenDAO, times(1)).findByUserAndProvider(anyLong(), anyString());
		assertNotNull(event);
		assertNotNull(event.getStart());
		assertNotNull(event.getDescription());
	}
}
