package com.workmarket.service.infra.notification;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.service.infra.jms.JmsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceImplTest {

	@Mock private JmsService jmsService;
	@InjectMocks private NotificationServiceImpl notificationService = spy(new NotificationServiceImpl());

	private NotificationTemplate notificationTemplate;
	private Calendar date;

	@Before
	public void setUp() {
		notificationTemplate = mock(NotificationTemplate.class);
		notificationService.metricRegistryFacade = new WMMetricRegistryFacade(new MetricRegistry(), "x");
		date = mock(Calendar.class);
	}

	@Test
	public void sendNotification_withNotification_sendWithCalendar() throws Exception {
		notificationService.sendNotification(notificationTemplate);

		verify(notificationService).sendNotification(eq(notificationTemplate), any(Calendar.class));
	}

	@Test
	public void sendNotification_withNotificationAndDate_sendNotification() throws Exception {
		notificationService.sendNotification(notificationTemplate, date);

		verify(jmsService).sendMessage(notificationTemplate, date);
	}

	@Test
	public void sendNotification_withNotificationAndDate_withNullNotification_doNotSendNotification() throws Exception {
		notificationService.sendNotification((NotificationTemplate) null, date);

		verify(jmsService, never()).sendMessage(any(NotificationTemplate.class), any(Calendar.class));
	}

}
