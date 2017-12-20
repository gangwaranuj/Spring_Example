package com.workmarket.service.infra.notification;

import com.google.common.base.Optional;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.notification.NotificationUserNotificationTemplate;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserNotificationPreferencePojo;
import com.workmarket.service.business.UserNotificationPrefsService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.template.TemplateService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationDispatcherImplTest {

	@Mock ProfileService profileService;
	@Mock TemplateService templateService;
	@Mock UserNotificationService userNotificationService;
	@Mock UserService userService;
	@Mock UserNotificationPrefsService userNotificationPrefsService;
	@InjectMocks NotificationDispatcherImpl notificationDispatcher = spy(new NotificationDispatcherImpl());

	private static final Long
		WORKER_ID = 1L,
		BUYER_ID = 2L;

	private NotificationTemplate dispatcherNotification = mock(NotificationTemplate.class);
	private NotificationType notificationType = mock(NotificationType.class);
	private NotificationUserNotificationTemplate userNotificationTemplate = mock(NotificationUserNotificationTemplate.class);
	private UserNotificationPreferencePojo pref = mock(UserNotificationPreferencePojo.class);
	private User user = mock(User.class);
	private PersonaPreference personaPreferenceDispatcher = mock(PersonaPreference.class);
	private Optional<PersonaPreference> personaPreferenceOptional = Optional.of(personaPreferenceDispatcher);

	@Before
	public void setup() {
		notificationDispatcher.metricRegistryFacade  = new WMMetricRegistryFacade(new MetricRegistry(), "x");
		when(notificationType.getUserNotificationFlag()).thenReturn(true);
		when(dispatcherNotification.getToId()).thenReturn(WORKER_ID);
		when(dispatcherNotification.getFromId()).thenReturn(BUYER_ID);
		when(dispatcherNotification.getNotificationType()).thenReturn(notificationType);
		when(dispatcherNotification.getUserNotificationEnabled()).thenReturn(true);
		when(dispatcherNotification.getUserNotificationTemplate()).thenReturn(userNotificationTemplate);
		when(templateService.render(dispatcherNotification.getUserNotificationTemplate())).thenReturn("");
		when(userNotificationPrefsService.findByUserAndNotificationType(dispatcherNotification.getToId(), dispatcherNotification.getNotificationType().getCode()))
			.thenReturn(pref);
//		when(pref.getUser()).thenReturn(user);
		when(personaPreferenceDispatcher.isDispatcher()).thenReturn(true);
		when(userService.getPersonaPreference(anyLong())).thenReturn(personaPreferenceOptional);
	}

	@Test(expected = Exception.class)
	public void dispatchNotification_withNullNotificationTemplate_throwException() {
		notificationDispatcher.dispatchNotification(null);
	}

	@Test(expected = Exception.class)
	public void dispatchNotification_withNullNotificationTemplateFromId_throwException() {
		when(dispatcherNotification.getFromId()).thenReturn(null);

		notificationDispatcher.dispatchNotification(dispatcherNotification);
	}

	@Test(expected = Exception.class)
	public void dispatchNotification_withNullNotificationTemplateToId_throwException() {
		when(dispatcherNotification.getToId()).thenReturn(null);

		notificationDispatcher.dispatchNotification(dispatcherNotification);
	}

	@Test(expected = Exception.class)
	public void dispatchNotification_withNullNotificationTemplateNotificationType_throwException() {
		when(dispatcherNotification.getNotificationType()).thenReturn(null);

		notificationDispatcher.dispatchNotification(dispatcherNotification);
	}

	@Test
	public void dispatchNotification_bullhornFlagTrue_dispatchUserNotificationInvoked() throws Exception {
		when(pref.getDispatchBullhornFlag()).thenReturn(true);
//		when(pref.getUser()).thenReturn(user);

		notificationDispatcher.dispatchNotification(dispatcherNotification);

		verify(notificationDispatcher).dispatchUserNotification(dispatcherNotification.getUserNotificationTemplate());
	}

	@Test
	public void dispatchNotification_bullhornFlagFalse_dispatchUserNotificationNotInvoked() throws Exception {
		notificationDispatcher.dispatchNotification(dispatcherNotification);

		verify(notificationDispatcher, never()).dispatchUserNotification(dispatcherNotification.getUserNotificationTemplate());
	}

}
