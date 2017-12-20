package com.workmarket.common.template;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BundleWorkAcceptFailedNotificationTemplateTest {

	private static final Long
		WORKER_ID = 1L,
		WORK_ID = 2L;

	private static final String timeZoneString = "UTC";
	private static final String WORK_TITLE = "An assignment to do work";
	private static final String FAILURE_MESSAGE_1 = "Accept work failure message";
	private static final String FAILURE_MESSAGE_HTML = "Accept <b>work</b> failure message";
	private static final String FAILURE_MESSAGE_HTML_ESCAPED = "Accept &lt;b&gt;work&lt;/b&gt; failure message";

	private AcceptWorkResponse failure;
	private TimeZone timeZone;
	private User worker;
	private Work work;

	@Before
	public void setup() {
		timeZone = mock(TimeZone.class);
		when(timeZone.getTimeZoneId()).thenReturn(timeZoneString);

		work = mock(Work.class);
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getTitle()).thenReturn(WORK_TITLE);
		when(work.getTimeZone()).thenReturn(timeZone);

		worker = mock(User.class);
		failure = mock(AcceptWorkResponse.class);
	}

	@Test
	public void nullWork_blankTitle() {
		when(failure.getWork()).thenReturn(null);

		BundleWorkAcceptFailedNotificationTemplate notificationTemplate =
			new BundleWorkAcceptFailedNotificationTemplate(WORKER_ID, worker, work, failure);

		assertTrue(StringUtils.isBlank(notificationTemplate.getTitle()));
	}

	@Test
	public void noFailureMessages_blankMessage() {
		when(failure.getMessages()).thenReturn(Collections.EMPTY_LIST);

		BundleWorkAcceptFailedNotificationTemplate notificationTemplate =
			new BundleWorkAcceptFailedNotificationTemplate(WORKER_ID, worker, work, failure);

		assertTrue(StringUtils.isBlank(notificationTemplate.getTitle()));
	}

	@Test
	public void title_is_workTitle() {
		when(failure.getWork()).thenReturn(work);

		BundleWorkAcceptFailedNotificationTemplate notificationTemplate =
			new BundleWorkAcceptFailedNotificationTemplate(WORKER_ID, worker, work, failure);

		assertEquals(notificationTemplate.getTitle(), WORK_TITLE);
	}

	@Test
	public void message_is_firstFailureMessage() {
		when(failure.getMessages()).thenReturn(ImmutableList.of(FAILURE_MESSAGE_1));

		BundleWorkAcceptFailedNotificationTemplate notificationTemplate =
			new BundleWorkAcceptFailedNotificationTemplate(WORKER_ID, worker, work, failure);

		assertEquals(notificationTemplate.getMessage(), FAILURE_MESSAGE_1);
	}

	@Test
	public void message_is_firstFailureMessageHtmlEscaped() {
		when(failure.getMessages()).thenReturn(ImmutableList.of(FAILURE_MESSAGE_HTML));

		BundleWorkAcceptFailedNotificationTemplate notificationTemplate =
			new BundleWorkAcceptFailedNotificationTemplate(WORKER_ID, worker, work, failure);

		assertEquals(notificationTemplate.getMessage(), FAILURE_MESSAGE_HTML_ESCAPED);
	}
}
