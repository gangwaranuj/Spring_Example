package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Point;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.common.template.WorkUpdatedNotificationTemplate;
import com.workmarket.dao.google.CalendarSyncSettingsDAO;
import com.workmarket.dao.requirement.TravelDistanceRequirementDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.changelog.PropertyChange;
import com.workmarket.domains.model.changelog.PropertyChangeType;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirement;
import com.workmarket.domains.work.dao.follow.WorkFollowDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.dto.UnreadNotificationsDTO;
import com.workmarket.service.business.event.EntityUpdateEvent;
import com.workmarket.service.business.event.MarkUserNotificationsAsReadEvent;
import com.workmarket.service.business.event.work.WorkUpdatedEvent;
import com.workmarket.service.business.integration.mbo.MboProfileDAO;
import com.workmarket.service.business.integration.mbo.SalesForceClient;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.infra.business.GeocodingService;
import com.workmarket.service.infra.event.transactional.EventServiceImpl;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.option.OptionsService;
import com.workmarket.service.search.user.PeopleSearchService;
import com.workmarket.service.search.user.SearchCSVGenerateEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

	@Mock SalesForceClient salesForceClient;
	@Mock ProfileService profileService;
	@Mock MboProfileDAO mboProfileDAO;
	@Mock UserService userService;
	@Mock WorkService workService;
	@Mock WorkBundleService workBundleService;
	@Mock CalendarSyncSettingsDAO calendarSyncSettingsDAO;
	@Mock NotificationTemplateFactory notificationTemplateFactory;
	@Mock NotificationDispatcher notificationDispatcher;
	@Mock OptionsService<AbstractWork> workOptionsService;
	@Mock PeopleSearchService peopleSearchService;
	@Mock WorkResourceService workResourceService;
	@Mock WorkFollowDAO workFollowDAO;
	@Mock UserNotificationService userNotificationService;
	@Mock GeocodingService geocodingService;
	@Mock TravelDistanceRequirementDAO travelDistanceRequirementDAO;
	@InjectMocks EventServiceImpl eventService;

	private static final Long WORK_ID = 1L;
	private static final Long WORKER_ID = 2L;
	private static final Long DISPATCHER_ID = 3L;
	private static final Long FOLLOWER_ID = 4L;
	private static final Map<PropertyChangeType, List<PropertyChange>> PROPERTY_CHANGES = new HashMap<>();

	private Work work;
	private WorkUpdatedEvent workUpdatedEvent;
	private List<Long> workerIds = Lists.newArrayList(WORKER_ID);
	private WorkFollow workFollow;
	private User worker, follower;
	private WorkUpdatedNotificationTemplate workerWorkUpdatedNotification;
	private TravelDistanceRequirement travelDistanceRequirement;
	private EntityUpdateEvent entityUpdateEvent;
	private Point point;

	@Before
	public void setUp() {
		follower = mock(User.class);
		when(follower.getId()).thenReturn(FOLLOWER_ID);
		worker = mock(User.class);
		when(worker.getId()).thenReturn(WORKER_ID);

		workFollow = mock(WorkFollow.class);
		when(workFollow.getUser()).thenReturn(follower);

		work = mock(Work.class);

		workUpdatedEvent = mock(WorkUpdatedEvent.class);
		when(workUpdatedEvent.getWorkId()).thenReturn(WORK_ID);
		when(workUpdatedEvent.getPropertyChanges()).thenReturn(PROPERTY_CHANGES);

		when(workService.findWork(WORK_ID)).thenReturn(work);
		when(workResourceService.findUserIdsNotDeclinedForWork(WORK_ID)).thenReturn(workerIds);

		workerWorkUpdatedNotification = mock(WorkUpdatedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkUpdatedNotificationTemplate(WORKER_ID, work)).thenReturn(workerWorkUpdatedNotification);

		entityUpdateEvent = mock(EntityUpdateEvent.class);
		travelDistanceRequirement = mock(TravelDistanceRequirement.class);
		when(entityUpdateEvent.getEntity()).thenReturn(travelDistanceRequirement);

		point = mock(Point.class);
		when(geocodingService.geocode(anyString())).thenReturn(point);
	}

	@Test(expected = IllegalArgumentException.class)
	public void processEvent_searchCSVGenerateEvent_withNullPagination_throwException() throws SearchException {
		eventService.processEvent(new SearchCSVGenerateEvent(new PeopleSearchRequest()));
	}

	@Test
	public void processEvent_searchCSVGenerateEvent_searchPeople() throws SearchException {
		eventService.processEvent(new SearchCSVGenerateEvent(new PeopleSearchRequest().setPaginationRequest(new Pagination())));

		verify(peopleSearchService).searchPeople(any(PeopleSearchRequest.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void processEvent_workUpdatedEvent_throwException() throws Exception {
		when(workUpdatedEvent.getWorkId()).thenReturn(null);

		eventService.processEvent(workUpdatedEvent);
	}

	@Test
	public void processEvent_workUpdatedEvent_noWorkers_logAndEarlyReturn() throws Exception {
		when(workResourceService.findUserIdsNotDeclinedForWork(workUpdatedEvent.getWorkId()))
			.thenReturn(Lists.<Long>newArrayList());

		eventService.processEvent(workUpdatedEvent);

		verify(notificationDispatcher, never()).dispatchNotification(any(NotificationTemplate.class));
	}

	@Test
	public void processEvent_workUpdatedEvent_sendNotification_toWorkers() throws Exception {
		eventService.processEvent(workUpdatedEvent);

		verify(workerWorkUpdatedNotification).setPropertyChanges(workUpdatedEvent.getPropertyChanges());
		verify(notificationDispatcher).dispatchNotification(workerWorkUpdatedNotification);
	}

	@Test
	public void processEvent_EntityUpdateddEvent_does_not_geocode() throws Exception {
		when(travelDistanceRequirement.getLatitude()).thenReturn(1D);
		when(travelDistanceRequirement.getLongitude()).thenReturn(1D);

		eventService.processEvent(entityUpdateEvent);
		verify(geocodingService, never()).geocode(anyString());
	}

	@Test
	public void processEvent_EntityUpdateddEvent_geocodes() throws Exception {
		eventService.processEvent(entityUpdateEvent);
		verify(geocodingService).geocode(anyString());
	}

	@Test
	public void processEvent_workUpdatedEvent_sendNotification_toDispatchers() throws Exception {
		List<Long> dispatcherIds = Lists.newArrayList(DISPATCHER_ID);
		when(workResourceService.getDispatcherIdsForWorkAndWorkers(eq(work.getId()), anyListOf(Long.class))).thenReturn(dispatcherIds);
		WorkUpdatedNotificationTemplate dispatcherNotification = mock(WorkUpdatedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkUpdatedNotificationTemplate(DISPATCHER_ID, work)).thenReturn(dispatcherNotification);

		eventService.processEvent(workUpdatedEvent);

		verify(dispatcherNotification).setPropertyChanges(workUpdatedEvent.getPropertyChanges());
		verify(notificationDispatcher).dispatchNotification(dispatcherNotification);
	}

	@Test
	public void processEvent_workUpdatedEvent_doNotSendNotification_toDispatchers() throws Exception {
		WorkUpdatedNotificationTemplate dispatcherNotification = mock(WorkUpdatedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkUpdatedNotificationTemplate(DISPATCHER_ID, work)).thenReturn(dispatcherNotification);

		eventService.processEvent(workUpdatedEvent);

		verify(notificationDispatcher, never()).dispatchNotification(dispatcherNotification);
	}

	@Test
	public void processEvent_workUpdatedEvent_sendNotification_toFollowers() throws Exception {
		workFollow = mock(WorkFollow.class);
		follower = mock(User.class);
		when(workFollow.getUser()).thenReturn(follower);
		when(follower.getId()).thenReturn(FOLLOWER_ID);
		List<WorkFollow> workFollows = Lists.newArrayList(workFollow);
		when(workFollowDAO.getFollowers(work.getId())).thenReturn(workFollows);
		WorkUpdatedNotificationTemplate followerNotification = mock(WorkUpdatedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkUpdatedNotificationTemplate(follower.getId(), work)).thenReturn(followerNotification);

		eventService.processEvent(workUpdatedEvent);

		verify(followerNotification).setPropertyChanges(workUpdatedEvent.getPropertyChanges());
		verify(followerNotification).setWorkFollow(workFollow);
		verify(notificationDispatcher).dispatchNotification(followerNotification);
	}

	@Test
	public void processEvent_workUpdatedEvent_doNotSendNotification_toFollowers() throws Exception {
		WorkUpdatedNotificationTemplate followerNotification = mock(WorkUpdatedNotificationTemplate.class);
		when(notificationTemplateFactory.buildWorkUpdatedNotificationTemplate(follower.getId(), work)).thenReturn(followerNotification);

		eventService.processEvent(workUpdatedEvent);

		verify(notificationDispatcher, never()).dispatchNotification(followerNotification);
	}

	@Test
	public void processEvent_markUserNotificationsAsReadEvent_callSetViewedAtNotification() {
		MarkUserNotificationsAsReadEvent markUserNotificationsAsReadEvent = mock(MarkUserNotificationsAsReadEvent.class);
		UnreadNotificationsDTO unreadNotificationsDTO = mock(UnreadNotificationsDTO.class);

		when(markUserNotificationsAsReadEvent.getUserId()).thenReturn(WORKER_ID);
		when(markUserNotificationsAsReadEvent.getUnreadNotificationsDTO()).thenReturn(unreadNotificationsDTO);

		eventService.processEvent(markUserNotificationsAsReadEvent);

		verify(userNotificationService).setViewedAtNotification(
			markUserNotificationsAsReadEvent.getUserId(), markUserNotificationsAsReadEvent.getUnreadNotificationsDTO()
		);
	}

}
