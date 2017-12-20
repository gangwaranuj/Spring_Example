package com.workmarket.service.infra.business;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.common.annotations.VisibleForTesting;
import com.workmarket.dao.google.CalendarSyncSettingsDAO;
import com.workmarket.dao.google.RefreshTokenDAO;
import com.workmarket.dao.google.WorkCalendarEventDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.google.CalendarSyncSettings;
import com.workmarket.domains.model.google.RefreshToken;
import com.workmarket.domains.model.google.WorkCalendarEvent;
import com.workmarket.domains.model.oauth.OAuthTokenProviderType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.calendar.CalendarSyncAddAssignmentsEvent;
import com.workmarket.service.business.event.calendar.CalendarSyncRemoveAssignmentsEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

	private static final Log logger = LogFactory.getLog(GoogleCalendarServiceImpl.class);

	@Value("${google.key}")
	private String GOOGLE_KEY;

	@Value("${google.secret}")
	private String GOOGLE_SECRET;

	@Value("${google.callback.calendar}")
	private String GOOGLE_CALLBACK_CALENDAR;

	@Value("${google.scope.calendar}")
	private String GOOGLE_SCOPE_CALENDAR;

	@Value("${baseurl}")
	private String baseUrl;

	@Autowired private RefreshTokenDAO refreshTokenDAO;
	@Autowired private UserService userService;
	@Autowired private CalendarSyncSettingsDAO calendarSyncSettingsDAO;
	@Autowired private WorkCalendarEventDAO workCalendarEventDAO;
	@Autowired private WorkService workService;
	@Autowired private EventRouter eventRouter;

	private final static String APPLICATION_NAME = "Work Market";

	@Override
	public String getCalendarAuthURL() {
		return new GoogleAuthorizationCodeRequestUrl(GOOGLE_KEY, GOOGLE_CALLBACK_CALENDAR,
				Collections.singleton(CalendarScopes.CALENDAR)).setAccessType("offline").setApprovalPrompt("force").build();
	}

	@Override
	public boolean isAuthorizedToWM(String userNumber) {
		Assert.notNull(userNumber);
		User user = userService.findUserByUserNumber(userNumber);
		Assert.notNull(user);
		return isAuthorizedToWM(user.getId());
	}

	@Override
	public boolean isAuthorizedToWM(Long userId) {
		RefreshToken refreshToken = refreshTokenDAO.findByUserAndProvider(userId, OAuthTokenProviderType.GOOGLE_CALENDAR);
		return (refreshToken != null && refreshToken.getRefreshToken() != null);
	}

	@Override
	public boolean hasCalendarSettings(Long userId) {
		Assert.notNull(userId);
		CalendarSyncSettings calendarSyncSettings = calendarSyncSettingsDAO.findByUser(userId);
		return calendarSyncSettings != null;
	}

	@Override
	public boolean revokeAuthorizationFromWM(String userNumber) {
		Assert.notNull(userNumber);
		User user = userService.findUserByUserNumber(userNumber);
		Assert.notNull(user);
		return revokeAuthorizationFromWM(user.getId());
	}

	@Override
	public boolean revokeAuthorizationFromWM(Long userId) {
		RefreshToken refreshToken = refreshTokenDAO.findByUserAndProvider(userId, OAuthTokenProviderType.GOOGLE_CALENDAR);
		if (refreshToken != null && refreshToken.getRefreshToken() != null) {
			refreshToken.setRefreshToken(null);
		}
		CalendarSyncSettings calendarSyncSettings = calendarSyncSettingsDAO.findByUser(userId);
		if (calendarSyncSettings != null) {
			calendarSyncSettings.setDeleted(true);
		}

		return true;
	}

	@Override
	public RefreshToken saveOrUpdateRefreshToken(String refreshTokenString, Long userId) {
		Assert.notNull(refreshTokenString);
		RefreshToken refreshToken =
				refreshTokenDAO.findByUserAndProvider(userId, OAuthTokenProviderType.GOOGLE_CALENDAR);
		if (refreshToken != null) {
			refreshToken.setRefreshToken(refreshTokenString);
		} else {
			User user = userService.findUserById(userId);
			Assert.notNull(user);
			refreshToken = new RefreshToken();
			refreshToken.setProviderType(new OAuthTokenProviderType(OAuthTokenProviderType.GOOGLE_CALENDAR));
			refreshToken.setUser(user);
			refreshToken.setRefreshToken(refreshTokenString);
			refreshTokenDAO.saveOrUpdate(refreshToken);
		}

		return refreshToken;
	}

	@Override
	public boolean authorizeWMFromAuthCode(String authCode, Long userId) {
		Assert.notNull(authCode);
		Assert.notNull(userId);
		try {
			final GoogleAuthorizationCodeFlow flow = makeNewGoogleAuthCodeFlow(GOOGLE_KEY, GOOGLE_SECRET, GOOGLE_SCOPE_CALENDAR);
			GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(GOOGLE_CALLBACK_CALENDAR).execute();
			Assert.notNull(response);

			String refreshToken = (String) response.get("refresh_token");

			if (refreshToken != null && StringUtils.isNotBlank(refreshToken)) {
				saveOrUpdateRefreshToken(refreshToken, userId);
				return true;
			}
		} catch (IOException ex) {
			logger.debug(String.format("[GoogleCalendarService] Error occurred while authorizing calendar for user with ID %s with error %s and stack trace %s",
					userId, ex.getMessage(), ex.getCause()));
		}
		return false;
	}

	@Override
	public Map<String, String> getCalendars(Long userId) {
		com.google.api.services.calendar.Calendar client = refreshAuthToken(userId);
		final List<CalendarListEntry> calendarListEntries = getCalendarListEntries(client, userId);
		final List<CalendarListEntry> filteredCalendarListEntries = filterCalendarListEntries(calendarListEntries);
		return getCalendarMap(filteredCalendarListEntries);
	}

	@VisibleForTesting
	protected Map<String, String> getCalendarMap(final List<CalendarListEntry> calendarListEntries) {
		final Map<String, String> calendars = new HashMap<>();
		for (CalendarListEntry entry : calendarListEntries) {
			calendars.put(entry.getId(), entry.getSummary());
		}
		return calendars;
	}

	@VisibleForTesting
	protected List<CalendarListEntry> filterCalendarListEntries(List<CalendarListEntry> calendarListEntries) {
		List<CalendarListEntry> calendarListEntriesWithIds = new LinkedList<>();
		for(CalendarListEntry calendarListEntry : calendarListEntries) {
			if(calendarListEntry.getId() == null) {
				continue;
			}
			if(calendarListEntry.getSummary() == null) {
				continue;
			}
			calendarListEntriesWithIds.add(calendarListEntry);
		}
		return calendarListEntriesWithIds;
	}

	private List<CalendarListEntry> getCalendarListEntries(
		final com.google.api.services.calendar.Calendar client,
		final Long userId) {

		try {
			return extractCalendarListItems(client);
		} catch (IOException e) {
			logger.error(getGoogleCalendarListErrorString(userId, e));
			return Collections.emptyList();
		}
	}

	@VisibleForTesting
	protected List<CalendarListEntry> extractCalendarListItems(
		final com.google.api.services.calendar.Calendar client) throws IOException {

		if (client == null) {
			logger.warn("[GoogleCalendarService] calendarList");
			return Collections.emptyList();
		}

		com.google.api.services.calendar.Calendar.CalendarList calendarList = client.calendarList();
		if (calendarList == null) {
			logger.warn("[GoogleCalendarService] calendarList");
			return Collections.emptyList();
		}

		com.google.api.services.calendar.Calendar.CalendarList.List calendarListInnerList;
		try {
			calendarListInnerList = calendarList.list();
		} catch (IOException e) {
			throw new IOException(e);
		}
		if (calendarListInnerList == null) {
			logger.warn("[GoogleCalendarService] calendarList list");
			return Collections.emptyList();
		}

		CalendarList feed;
		try {
			feed = calendarListInnerList.execute();
		} catch (IOException e) {
			throw new IOException(e);
		}
		if (feed == null) {
			logger.warn("[GoogleCalendarService] calendar feed");
			return Collections.emptyList();
		}

		List<CalendarListEntry> feedItems = feed.getItems();
		if (feedItems == null) {
			logger.warn("[GoogleCalendarService] calendar feed items");
			return Collections.emptyList();
		}

		return feedItems;
	}

	private String getGoogleCalendarListErrorString(final long userId, final IOException e) {
		return String.format(
			"[GoogleCalendarService] Error occurred while getting list of calendars for userId %s with error %s and stack trace %s",
			userId,
			e.getMessage(),
			e.getCause());
	}

	@Override
	public void syncAllAssignmentsToCalendar(Long userId) {
		Assert.notNull(userId);
		CalendarSyncSettings calendarSyncSettings = calendarSyncSettingsDAO.findByUser(userId);
		if (calendarSyncSettings != null) {
			WorkPagination pagination = new WorkPagination();
			pagination.setResultsLimit(100);
			WorkPagination workPagination = workService.findWorkByWorkResource(userId, pagination);
			for (Work work : workPagination.getResults()) {
				if (work != null && work.isActive()) {
					addAssignmentToCalendar(work, CalendarSyncSettings.CONFIRMED, calendarSyncSettings);
				}
			}
		}
	}

	@Override
	public Event addAssignmentToCalendar(Work work, String status, CalendarSyncSettings calendarSyncSettings) {
		Assert.notNull(work);
		Assert.notNull(work.getScheduleFrom());
		Assert.notNull(calendarSyncSettings);
		Assert.notNull(calendarSyncSettings.getUser());
		Event event = new Event();
		event.setSummary(work.getTitle());
		event.setStatus(status);
		WorkResource workResource = workService.findActiveWorkResource(work.getId());
		if (workResource == null) {
			logger.error("[addAssignmentToCalendar] error finding active resource for work " + work.getId());
			return null;
		}

		event.setDescription(String.format("%s (%s)\n%s/assignments/details/%s\n%s\nFor: %s\n%s", work.getTitle(), work.getWorkNumber(), baseUrl, work.getWorkNumber(),
				(workResource.getUser() != null && workResource.getUser().getCompany() != null) ? workResource.getUser().getCompany().getName() : "",
				(work.getBuyer() != null && work.getBuyer().getCompany() != null) ? work.getBuyer().getCompany().getName() : "",
				(work.getBuyer() != null) ? work.getBuyer().getFirstName() + " " + work.getBuyer().getLastName() : ""));
		if (work.getScheduleFrom() != null) {
			event.setStart(new EventDateTime().setDateTime(new DateTime(work.getScheduleFrom().getTime(), work.getScheduleFrom().getTimeZone())));
		}
		if (work.getScheduleThrough() != null) {
			event.setEnd(new EventDateTime().setDateTime(new DateTime(work.getScheduleThrough().getTime(), work.getScheduleThrough().getTimeZone())));
		} else if (work.getScheduleFrom() != null) {
			event.setEnd(new EventDateTime().setDateTime(new DateTime(work.getScheduleFrom().getTime(), work.getScheduleFrom().getTimeZone())));
		}

		if (work.getAddressOnsiteFlag()) {
			if (work.getAddress() != null) {
				Address address = work.getAddress();
				String locationString = String.format("%s %s, %s, %s, %s, %s", address.getAddress1(),
						address.getAddress2(),
						address.getCity(),
						address.getState() != null ? address.getState().getName() : "",
						address.getCountry() != null ? address.getCountry().getName() : "",
						address.getPostalCode());
				event.setLocation(locationString);
			} else {
				logger.error("[addAssignmentToCalendar] null address for work " + work.getId());
			}
		}

		try {
			com.google.api.services.calendar.Calendar client = refreshAuthToken(calendarSyncSettings.getUser().getId());
			if (client != null) {
				Event result = client.events().insert(calendarSyncSettings.getCalendarId(), event).execute();
				if (result != null) {
					WorkCalendarEvent workCalendarEvent = new WorkCalendarEvent();
					workCalendarEvent.setWork(work);
					workCalendarEvent.setCalendarSyncSettings(calendarSyncSettings);
					workCalendarEvent.setDeleted(false);
					workCalendarEvent.setEventId(result.getId());
					workCalendarEventDAO.saveOrUpdate(workCalendarEvent);
				}
			}
		} catch (IOException ex) {
			logger.debug(String.format("[GoogleCalendarService] Error occurred while adding event to Calendar on Assignment Id %s with error %s and stack trace %s", work.getId(), ex.getMessage(), ex.getCause()));
		}
		return event;
	}

	@Override
	public void addConfirmedAssignmentToCalendar(long workId) {
		WorkResource workResource = workService.findActiveWorkResource(workId);
		if (workResource == null) {
			logger.error("[addAssignmentToCalendar] error finding active resource for work " + workId);
			return;
		}
		CalendarSyncSettings calendarSyncSettings = calendarSyncSettingsDAO.findByUser(workResource.getUser().getId());
		if (calendarSyncSettings != null) {
			addAssignmentToCalendar(workResource.getWork(), CalendarSyncSettings.CONFIRMED, calendarSyncSettings);
		}
	}

	@Override
	public void updateCalendarEventStatus(Long workId, String status) {
		if (workId == null || isBlank(status)) {
			return;
		}

		List<WorkCalendarEvent> workCalendarEvents = workCalendarEventDAO.findByWork(workId);

		try {
			for (WorkCalendarEvent workCalendarEvent : workCalendarEvents) {
				User user = workCalendarEvent.getCalendarSyncSettings().getUser();
				com.google.api.services.calendar.Calendar client = refreshAuthToken(user.getId());
				if (client != null) {
					Event event = (client.events().get(workCalendarEvent.getCalendarSyncSettings().getCalendarId(), workCalendarEvent.getEventId()).execute()).setStatus(status);
					client.events().update(workCalendarEvent.getCalendarSyncSettings().getCalendarId(), workCalendarEvent.getEventId(), event).execute();
				}
			}
		} catch (IOException ex) {
			logger.error(String.format("[GoogleCalendarService] Error occurred while updating Calendar on Assignment Id %s with error %s and stack trace %s", workId, ex.getMessage(), ex.getCause()));
		}
	}

	@Override
	public void updateCalendarEventSchedule(Long workId) {
		if (workId != null) {
			Work work = workService.findWork(workId);

			if (work != null) {
				List<WorkCalendarEvent> workCalendarEvents = workCalendarEventDAO.findByWork(workId);
				try {
					for (WorkCalendarEvent workCalendarEvent : workCalendarEvents) {
						com.google.api.services.calendar.Calendar client = refreshAuthToken(workCalendarEvent.getCalendarSyncSettings().getUser().getId());
						if (client != null) {

							java.util.Calendar calendar = work.getScheduleFrom();
							if (work.getScheduleThrough() != null) {
								calendar = work.getScheduleThrough();
							}

							Event event = (client.events().get(workCalendarEvent.getCalendarSyncSettings().getCalendarId(), workCalendarEvent.getEventId()).execute()).
									setStart(new EventDateTime().setDateTime(new DateTime(work.getScheduleFrom().getTime(), work.getScheduleFrom().getTimeZone())))
									.setEnd(new EventDateTime().setDateTime(new DateTime(calendar.getTime(), calendar.getTimeZone())));
							client.events().update(workCalendarEvent.getCalendarSyncSettings().getCalendarId(), workCalendarEvent.getEventId(), event).execute();
						}
					}
				} catch (IOException ex) {
					logger.error(String.format("[GoogleCalendarService] Error occurred while updating Calendar event schedule on Assignment Id %s with error %s and stack trace %s", workId, ex.getMessage(), ex.getCause()));
				}
			}
		}
	}

	/**
	 * Remove refreshToken after creating event. This way we don't have to wait for all events to be removed from the
	 * calendar to 'unsync'
	 */
	@Override
	public void cancelSync(Long userId) {
		RefreshToken refreshToken = refreshTokenDAO.findByUserAndProvider(userId, OAuthTokenProviderType.GOOGLE_CALENDAR);
		if (refreshToken != null && refreshToken.getRefreshToken() != null) {
			eventRouter.sendEvent(new CalendarSyncRemoveAssignmentsEvent(userId, refreshToken.getRefreshToken()));
			refreshToken.setRefreshToken(null);
		}
	}

	@Override
	public boolean saveCalendarSyncSettings(Long userId, String calendarId, String calendarName, boolean isNewCalendar) {
		Assert.notNull(calendarName);
		Assert.notNull(userId);

		User user = userService.findUserById(userId);
		Assert.notNull(user);

		if (isNewCalendar) {
			com.google.api.services.calendar.model.Calendar calendar = createNewCalendar(user.getId(), calendarName);
			if (calendar != null && calendar.getId() != null) {
				calendarId = calendar.getId();
			}
		}

		RefreshToken refreshToken = refreshTokenDAO.findByUserAndProvider(userId, OAuthTokenProviderType.GOOGLE_CALENDAR);

		CalendarSyncSettings calendarSyncSettings = calendarSyncSettingsDAO.findByUserAndDeleted(userId);
		if (calendarSyncSettings != null) {
			calendarSyncSettings.setUser(user);
			calendarSyncSettings.setRefreshToken(refreshToken);
			calendarSyncSettings.setDeleted(false);
			if (calendarId != null) {
				calendarSyncSettings.setNewCalendar(isNewCalendar);
				calendarSyncSettings.setCalendarId(calendarId);
				calendarSyncSettings.setCalendarName(calendarName);
			}
		} else {
			Assert.notNull(calendarId);
			calendarSyncSettings = new CalendarSyncSettings(user, refreshToken, calendarId, calendarName, isNewCalendar);
			calendarSyncSettingsDAO.saveOrUpdate(calendarSyncSettings);
		}
		eventRouter.sendEvent(new CalendarSyncAddAssignmentsEvent(userId));
		return true;
	}

	@Override
	public Calendar createNewCalendar(Long userId, String calendarName) {
		Calendar entry = new Calendar();
		entry.setSummary(calendarName);
		com.google.api.services.calendar.Calendar client = refreshAuthToken(userId);
		Assert.notNull(client);

		try {
			return client.calendars().insert(entry).execute();
		} catch (IOException ex) {
			logger.debug(String.format("[GoogleCalendarService] Error occurred while creating a new calendar with name %s, userId %s and error %s and stack trace %s",
					calendarName, userId, ex.getMessage(), ex.getCause()));
		}
		return null;
	}

	@Override
	public void deleteCalendarAndEvents(Long userId, String refreshToken) {
		com.google.api.services.calendar.Calendar client = refreshAuthToken(userId, refreshToken);
		if (client == null) {
			return;
		}
		CalendarSyncSettings calendarSyncSettings = calendarSyncSettingsDAO.findByUser(userId);
		try {
			if (calendarSyncSettings != null) {
				boolean deleteCalendar = false;
				if (calendarSyncSettings.isNewCalendar()) {
					client.calendars().delete(calendarSyncSettings.getCalendarId()).execute();
					deleteCalendar = true;
				}
				List<WorkCalendarEvent> workCalendarEvents = workCalendarEventDAO.findByCalendarSyncSettings(calendarSyncSettings.getId());
				for (WorkCalendarEvent workCalendarEvent : workCalendarEvents) {
					if (!deleteCalendar) {
						client.events().delete(calendarSyncSettings.getCalendarId(), workCalendarEvent.getEventId()).execute();
					}
					workCalendarEvent.setDeleted(true);
				}
			}
		} catch (IOException ex) {
			logger.debug(String.format("[GoogleCalendarService] Error occurred while deleting calendar for userId %s and error %s and stack trace %s", userId, ex.getMessage(), ex.getCause()));
		}
	}

	protected GoogleAuthorizationCodeFlow makeNewGoogleAuthCodeFlow(String googleKey, String googleSercret, String googleScopeCalendar) {
		if (isNotBlank(googleKey) && isNotBlank(googleSercret) && isNotBlank(googleScopeCalendar)) {
			return new GoogleAuthorizationCodeFlow.Builder(
					new NetHttpTransport(), new JacksonFactory(), googleKey, googleSercret,
					Arrays.asList(googleScopeCalendar.split(";"))).setAccessType("offline").build();
		}
		return null;
	}

	protected com.google.api.services.calendar.Calendar refreshAuthToken(Long userId) {
		if (userId != null) {
			RefreshToken refreshToken = refreshTokenDAO.findByUserAndProvider(userId, OAuthTokenProviderType.GOOGLE_CALENDAR);
			if (refreshToken != null && refreshToken.getRefreshToken() != null) {
				return refreshAuthToken(userId, refreshToken.getRefreshToken());
			}
		}
		return null;
	}

	protected com.google.api.services.calendar.Calendar refreshAuthToken(Long userId, String refreshToken) {
		if (userId != null) {
			try {
				if (refreshToken != null) {
					final GoogleAuthorizationCodeFlow flow = makeNewGoogleAuthCodeFlow(GOOGLE_KEY, GOOGLE_SECRET, GOOGLE_SCOPE_CALENDAR);
					if (flow != null) {
						GoogleTokenResponse response = new GoogleRefreshTokenRequest(
							new NetHttpTransport(), new JacksonFactory(), refreshToken, GOOGLE_KEY, GOOGLE_SECRET).execute();
						if (response != null) {
							Credential credential = flow.createAndStoreCredential(response, null);
							return new com.google.api.services.calendar.Calendar.Builder(
								new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(APPLICATION_NAME).build();
						}
					}
				}
			} catch (IOException ex) {
				logger.debug(String.format("[GoogleCalendarService] Error occurred while refreshing auth token for calendar for userId %s and error %s and stack trace %s", userId, ex.getMessage(), ex.getCause()));
			}
		}
		return null;
	}
}
