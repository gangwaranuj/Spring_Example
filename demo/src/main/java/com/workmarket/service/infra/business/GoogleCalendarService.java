package com.workmarket.service.infra.business;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.workmarket.domains.model.google.CalendarSyncSettings;
import com.workmarket.domains.model.google.RefreshToken;
import com.workmarket.domains.work.model.Work;

import java.util.Map;

public interface GoogleCalendarService {

	String getCalendarAuthURL();

	boolean isAuthorizedToWM(String userNumber);

	boolean isAuthorizedToWM(Long userId);

	boolean hasCalendarSettings(Long userId);

	boolean revokeAuthorizationFromWM(String userNumber);

	boolean revokeAuthorizationFromWM(Long userId);

	RefreshToken saveOrUpdateRefreshToken(String refreshToken, Long userId);

	boolean authorizeWMFromAuthCode(String authCode, Long userId);

	Map<String, String> getCalendars(Long userId);

	void syncAllAssignmentsToCalendar(Long userId);

	Event addAssignmentToCalendar(Work work, String status, CalendarSyncSettings calendarSyncSettings);

	void addConfirmedAssignmentToCalendar(long workId);

	void cancelSync(Long userId);

	boolean saveCalendarSyncSettings(Long userId, String calendarId, String calendarName, boolean isNewCalendar);

	Calendar createNewCalendar(Long userId, String calendarName);

	void deleteCalendarAndEvents(Long userId, String refreshToken);

	void updateCalendarEventStatus(Long workId, String status);

	void updateCalendarEventSchedule(Long workId);
}
