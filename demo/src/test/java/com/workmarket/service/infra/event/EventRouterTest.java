package com.workmarket.service.infra.event;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.notification.vo.EmailNotifyResponse;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.service.business.event.BulkWorkUploadEvent;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.InvoicesDownloadedEvent;
import com.workmarket.service.business.event.WorkResourceCacheEvent;
import com.workmarket.service.business.event.user.ProfileUpdateEvent;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkClosedEvent;
import com.workmarket.service.business.event.work.WorkResourceInvitation;
import com.workmarket.service.business.integration.mbo.MboProfileDAO;
import com.workmarket.service.business.integration.mbo.SalesForceClient;
import com.workmarket.service.business.requirementsets.RequirementSetsService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.jms.JmsService;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventRouterTest {

	@Mock SalesForceClient salesForceClient;
	@Mock ProfileService profileService;
	@Mock MboProfileDAO mboProfileDAO;
	@Mock MetricRegistry metricRegistry;
	@Mock NotificationTemplateFactory notificationTemplateFactory;
	@Mock NotificationDispatcher notificationDispatcher;
	@Mock WorkService workService;
	@Mock WorkResourceService workResourceService;
	@Mock UserService userService;
	@Mock WorkIndexer workIndexer;
	@Mock BillingService billingService;
	@Mock WorkStatusService workStatusService;
	@Mock UserIndexer userIndexer;
	@Mock RequirementSetsService requirementSetsService;
	@Mock WorkBundleService workBundleService;
	@Mock NotificationService notificationService;
	@Mock AuthenticationService authenticationService;
	@Mock JmsService jmsService;
	@Mock WebRequestContextProvider webRequestContextProvider;
	@InjectMocks EventRouterImpl eventRouter;

	User resource, buyer, mboResource;
	Profile profile;
	Work work;
	MboProfile mboProfile;
	UserSearchIndexEvent userSearchIndexEvent;
	WorkResourceInvitation workResourceInvitation;
	NotificationTemplate notificationTemplate;
	InvoicesDownloadedEvent invoiceDownloadEvent;

	NotificationTemplate dispatcherNotificationTemplate;

	Long userId = 1L, workId = 2L, dispatcherId = 3L;
	int reqCount = 4;
	Set<Long> userIds = Sets.newHashSet(userId);
	String workNumber = "2345";

	@Captor ArgumentCaptor<Event> captorEvent;

	@Before
	public void setup() {
		final Meter mockMeter = mock(Meter.class);
		when(metricRegistry.meter((String) anyObject())).thenReturn(mockMeter);
		eventRouter.init();
		resource = mock(User.class);
		buyer = mock(User.class);
		mboResource = mock(User.class);
		profile = mock(Profile.class);
		work = mock(Work.class);
		mboProfile = mock(MboProfile.class);
		userSearchIndexEvent = mock(UserSearchIndexEvent.class);
		workResourceInvitation = mock(WorkResourceInvitation.class);
		notificationTemplate = mock(NotificationTemplate.class);
		dispatcherNotificationTemplate = mock(NotificationTemplate.class);
		invoiceDownloadEvent = mock(InvoicesDownloadedEvent.class);

		when(work.getId()).thenReturn(workId);
		when(work.getWorkNumber()).thenReturn(workNumber);
		when(userSearchIndexEvent.isDelete()).thenReturn(false);
		when(userSearchIndexEvent.getFromId()).thenReturn(null);
		when(userSearchIndexEvent.getToId()).thenReturn(null);
		when(resource.getId()).thenReturn(1L);
		when(notificationDispatcher.dispatchEmail(any(EmailTemplate.class)))
				.thenReturn(new EmailNotifyResponse(EmailNotifyResponse.Status.OK));
		when(workService.findWork(anyLong(), anyBoolean())).thenReturn(work);
		when(workService.findWork(anyLong())).thenReturn(work);
		when(workService.findWorkForInvitation(workId)).thenReturn(work);
		when(userService.findUserById(anyLong())).thenReturn(buyer);
		when(buyer.getProfile()).thenReturn(profile);
		when(profile.getFindWork()).thenReturn(false);
		when(workResourceInvitation.getWorkId()).thenReturn(workId);
		when(workResourceInvitation.getUserResourceIds()).thenReturn(userIds);
		when(requirementSetsService.getMandatoryRequirementCountByWorkId(workId)).thenReturn(reqCount);
		when(workBundleService.isAssignmentBundle(work)).thenReturn(false);
		doCallRealMethod().when(webRequestContextProvider).inject(any(Object.class));
	}

	@Test
	public void processWorkResourceInvitation_filterMboResourcesFromList() throws Exception {
		eventRouter.onEvent(workResourceInvitation);

		verify(mboProfileDAO).filterMboResourcesFromList(workResourceInvitation.getUserResourceIds());
	}

	@Test
	public void processWorkResourceInvitation_buildWorkResourceInvitation() throws Exception {
		eventRouter.onEvent(workResourceInvitation);

		verify(notificationTemplateFactory).buildWorkResourceInvitation(eq(work), eq(userId), anyBoolean(), anyBoolean(), eq(reqCount));
	}

	@Test
	public void processWorkResourceInvitation_getDispatcherIdsForWorker() throws Exception {
		eventRouter.onEvent(workResourceInvitation);

		verify(workResourceService).getAllDispatcherIdsForWorker(anyLong());
	}

	@Test
	public void processWorkResourceInvitation_dispatcherExists_andIsNotWorker_buildAndDispatchNotification() throws Exception {
		List<Long> dispatcherIds = Lists.newArrayList(dispatcherId);
		when(workResourceService.getAllDispatcherIdsForWorker(anyLong())).thenReturn(dispatcherIds);
		when(notificationTemplateFactory.buildWorkResourceInvitation(
			eq(work), eq(dispatcherId), eq(userId), anyBoolean(), anyBoolean(), eq(reqCount)))
			.thenReturn(dispatcherNotificationTemplate);

		eventRouter.onEvent(workResourceInvitation);

		verify(
			notificationTemplateFactory
		).buildWorkResourceInvitation(
			eq(work), eq(dispatcherId), eq(userId), anyBoolean(), anyBoolean(), eq(reqCount)
		);
		verify(notificationService).sendNotification(dispatcherNotificationTemplate);
	}

	@Test
	public void processWorkResourceInvitation_dispatcherExists_andIsTheWorker_doNotDispatchNotification() throws Exception {
		List<Long> dispatcherIds = Lists.newArrayList(userId);
		when(workResourceService.getAllDispatcherIdsForWorker(anyLong())).thenReturn(dispatcherIds);

		eventRouter.onEvent(workResourceInvitation);

		verify(notificationDispatcher, never()).dispatchNotification(dispatcherNotificationTemplate);
	}

	@Test
	public void processWorkResourceInvitation_nonMboResource_neverCallsCreateFeed() throws Exception {
		Set<Long> resourceIds = Sets.newHashSet(resource.getId(), mboResource.getId());
		when(mboProfileDAO.filterMboResourcesFromList(anySetOf(Long.class))).thenReturn(new HashSet<Long>());

		when(workResourceInvitation.getWorkId()).thenReturn(workId);
		when(workResourceInvitation.getUserResourceIds()).thenReturn(resourceIds);

		eventRouter.onEvent(workResourceInvitation);

		verify(salesForceClient, never()).createFeed(anyString(), any(MboProfile.class));
	}

	@Test
	public void processWorkResourceInvitation_mboResource_callsCreateFeed() throws Exception {
		Set<Long> resourceIds = Sets.newHashSet(resource.getId(), mboResource.getId());
		when(mboProfile.getUserId()).thenReturn(2L);
		when(mboResource.getId()).thenReturn(2L);
		when(mboProfileDAO.filterMboResourcesFromList(anySetOf(Long.class))).thenReturn(Sets.newHashSet(2L));
		when(profileService.findMboProfile(anyLong())).thenReturn(mboProfile);

		when(workResourceInvitation.getWorkId()).thenReturn(workId);
		when(workResourceInvitation.getUserResourceIds()).thenReturn(resourceIds);

		eventRouter.onEvent(workResourceInvitation);

		verify(salesForceClient).createFeed(workNumber, mboProfile);
	}

	@Test
	public void processProfileUpdateEvent_nonMboResource_neverCallsUpdateUser() throws Exception {
		ProfileUpdateEvent profileUpdateEvent = mock(ProfileUpdateEvent.class);
		when(profileUpdateEvent.getUserId()).thenReturn(1L);
		when(profileUpdateEvent.getProperties()).thenReturn(CollectionUtilities.newObjectMap("title", "job title"));
		when(profileService.findMboProfile(1L)).thenReturn(null);

		eventRouter.onEvent(profileUpdateEvent);

		verify(salesForceClient, never()).updateUser(anyString(), anyMapOf(String.class, Object.class));
	}

	@Test
	public void processProfileUpdateEvent_mboResource_callsUpdateUser() throws Exception {
		ProfileUpdateEvent profileUpdateEvent = mock(ProfileUpdateEvent.class);
		when(profileUpdateEvent.getUserId()).thenReturn(1L);
		when(profileUpdateEvent.getProperties()).thenReturn(CollectionUtilities.newObjectMap("title", "job title"));
		when(profileService.findMboProfile(1L)).thenReturn(mboProfile);

		eventRouter.onEvent(profileUpdateEvent);

		verify(salesForceClient).updateUser(anyString(), anyMapOf(String.class, Object.class));
	}

	@Test
	public void processWorkResourceCache_buildCache() {
		eventRouter.onEvent(new WorkResourceCacheEvent(1L));
		verify(workResourceService).populateWorkResourceDetailCache(anyLong(), any(WorkResourceDetailPagination.class));
	}

	@Test
	public void onEvent_withWorkClosedEvent() {
		eventRouter.onEvent(new WorkClosedEvent(new CloseWorkDTO(), 1L));

		verify(billingService).emailInvoiceForWork(eq(1L));
		verify(workStatusService).onPostTransitionToClosed(eq(1L), any(CloseWorkDTO.class));
		verify(workResourceService).onPostTransitionToClosed(eq(1L), any(CloseWorkDTO.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void onEvent_withNullUserSearchIndexEvent_throwException() {
		eventRouter.onEvent((UserSearchIndexEvent) null);
	}

	@Test
	public void onEvent_withUserSearchIndexEvent_withIds_withNoIdRange_thenReindexById() {
		when(userSearchIndexEvent.getUserIds()).thenReturn(userIds);

		eventRouter.onEvent(userSearchIndexEvent);

		verify(userIndexer).reindexById(anyCollectionOf(Long.class));
	}

	@Test
	public void onEvent_withUserSearchIndexEvent_withNoIds_withIdRange_thenReindexByRange() {
		when(userSearchIndexEvent.getFromId()).thenReturn(1L);
		when(userSearchIndexEvent.getToId()).thenReturn(2L);

		eventRouter.onEvent(userSearchIndexEvent);

		verify(userIndexer).reindexBetweenIds(1L, 2L);
	}

	@Test
	public void onEvent_withUserSearchIndexEvent_withNoIds_withNoIdRange_doNothing() {
		eventRouter.onEvent(userSearchIndexEvent);

		verify(userIndexer, never()).reindexById(anyCollectionOf(Long.class));
		verify(userIndexer, never()).reindexBetweenIds(anyLong(), anyLong());
	}

	@Test
	public void onEvent_withUserSearchIndexEvent_withIds_withIdRange_thenReindexByIdAndRange() {
		when(userSearchIndexEvent.getUserIds()).thenReturn(userIds);
		when(userSearchIndexEvent.getFromId()).thenReturn(1L);
		when(userSearchIndexEvent.getToId()).thenReturn(2L);

		eventRouter.onEvent(userSearchIndexEvent);

		verify(userIndexer).reindexById(anyCollectionOf(Long.class));
		verify(userIndexer).reindexBetweenIds(anyLong(), anyLong());
	}

	@Test
	public void onEvent_withUserSearchIndexEvent_withIds_isDeleteEvent_thenDeleteById() {
		when(userSearchIndexEvent.getUserIds()).thenReturn(userIds);
		when(userSearchIndexEvent.getFromId()).thenReturn(1L);
		when(userSearchIndexEvent.getToId()).thenReturn(2L);
		when(userSearchIndexEvent.isDelete()).thenReturn(true);

		eventRouter.onEvent(userSearchIndexEvent);

		verify(userIndexer).deleteById(anyCollectionOf(Long.class));
		verify(userIndexer, never()).reindexById(anyCollectionOf(Long.class));
		verify(userIndexer, never()).reindexBetweenIds(anyLong(), anyLong());
	}

	@Test
	public void onEvent_invoiceDownloaded() {

		when(invoiceDownloadEvent.getInvoiceIds()).thenReturn(new ArrayList<Long>());
		when(invoiceDownloadEvent.getLoggedInUserId()).thenReturn(Constants.WORKMARKET_SYSTEM_USER_ID);

		eventRouter.onEvent(invoiceDownloadEvent);

		verify(billingService).updateInvoiceLastDownloadedDate(any(List.class), any(Calendar.class), anyLong());
	}

	@Test
	public void sendEvent_shouldSetCurrentUserOnEvent() {
		final BulkWorkUploadEvent event = new BulkWorkUploadEvent(null, null, null);
		when(authenticationService.getCurrentUser()).thenReturn(buyer);

		eventRouter.sendEvent(event);

		verify(jmsService).sendEventMessage(captorEvent.capture());
		assertEquals(buyer, captorEvent.getValue().getUser());
	}

	@Test
	public void sendEvents_shouldSetCurrentUserOnEvent() {
		final BulkWorkUploadEvent event = new BulkWorkUploadEvent(null, null, null);
		when(authenticationService.getCurrentUser()).thenReturn(buyer);

		eventRouter.sendEvents(ImmutableList.of(event));

		verify(jmsService, times(1)).sendEventMessage(captorEvent.capture());
		assertEquals(buyer, captorEvent.getValue().getUser());
	}
}
