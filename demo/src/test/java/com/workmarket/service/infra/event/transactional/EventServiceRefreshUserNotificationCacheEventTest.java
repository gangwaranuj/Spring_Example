package com.workmarket.service.infra.event.transactional;

import com.google.common.base.Optional;
import com.workmarket.common.cache.UserNotificationCache;
import com.workmarket.dto.UnreadNotificationsDTO;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.event.RefreshUserNotificationCacheEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceRefreshUserNotificationCacheEventTest {

	@Mock UserNotificationCache userNotificationCache;
	@Mock UserNotificationService userNotificationService;
	@InjectMocks EventServiceImpl service;

	Long userId = 1L, unreadCount = 5L;
	String startUuid = "START-uuid";
	String endUuid = "END-uuid";
	String notificationUuid = "middle-uuid";

	RefreshUserNotificationCacheEvent notificationEvent = mock(RefreshUserNotificationCacheEvent.class);
	UnreadNotificationsDTO cachedUnreadNotificationsDTO, unreadNotificationsDTO;

	@Before
	public void setUp() {
		notificationEvent = mock(RefreshUserNotificationCacheEvent.class);
		cachedUnreadNotificationsDTO = mock(UnreadNotificationsDTO.class);
		unreadNotificationsDTO = mock(UnreadNotificationsDTO.class);

		when(notificationEvent.getUserId()).thenReturn(userId);
		when(notificationEvent.getNotificationUuid()).thenReturn(notificationUuid);
		when(userNotificationCache.getUnreadNotificationsInfoByUser(userId)).thenReturn(Optional.of(cachedUnreadNotificationsDTO));
		when(cachedUnreadNotificationsDTO.getStartUuid()).thenReturn(startUuid);
		when(cachedUnreadNotificationsDTO.getEndUuid()).thenReturn(endUuid);
		when(cachedUnreadNotificationsDTO.getUnreadCount()).thenReturn(unreadCount);
	}

	@Test
	public void processEvent_clearNotificationCache() {
		service.processEvent(notificationEvent);
		verify(userNotificationCache).clearNotifications(userId);
	}

	@Test
	public void processEvent_cachedNotificationsPresent_unreadCountIsIncremented() {
		service.processEvent(notificationEvent);

		ArgumentCaptor<UnreadNotificationsDTO> argumentCaptor = ArgumentCaptor.forClass(UnreadNotificationsDTO.class);
		verify(userNotificationCache).putUnreadNotificationInfo(eq(userId), argumentCaptor.capture());

		assertEquals(argumentCaptor.getValue().getUnreadCount(), unreadCount + 1);
	}

	@Test
	public void processEvent_cachedNotificationsPresent_noUnreadMessages_startIdEqualsNotificationId_endIdEqualsNotificationId() {
		when(cachedUnreadNotificationsDTO.getUnreadCount()).thenReturn(0L);

		service.processEvent(notificationEvent);

		ArgumentCaptor<UnreadNotificationsDTO> argumentCaptor = ArgumentCaptor.forClass(UnreadNotificationsDTO.class);
		verify(userNotificationCache).putUnreadNotificationInfo(eq(userId), argumentCaptor.capture());

		assertEquals(argumentCaptor.getValue().getStartUuid(), notificationEvent.getNotificationUuid());
		assertEquals(argumentCaptor.getValue().getEndUuid(), notificationEvent.getNotificationUuid());
	}

	@Test
	public void processEvent_cachedNotificationsPresent_withUnreadMessages_startIdEqualsCachedStartId_endIdEqualsNotificationId() {
		service.processEvent(notificationEvent);

		ArgumentCaptor<UnreadNotificationsDTO> argumentCaptor = ArgumentCaptor.forClass(UnreadNotificationsDTO.class);
		verify(userNotificationCache).putUnreadNotificationInfo(eq(userId), argumentCaptor.capture());

		assertEquals(argumentCaptor.getValue().getStartUuid(), cachedUnreadNotificationsDTO.getStartUuid());
		assertEquals(argumentCaptor.getValue().getEndUuid(), notificationEvent.getNotificationUuid());
	}

	@Test
	public void processEvent_cachedNotificationsNotPresent_getUnreadNotificationInfoFromDB() {
		when(userNotificationCache.getUnreadNotificationsInfoByUser(userId)).thenReturn(Optional.<UnreadNotificationsDTO>absent());
		when(userNotificationService.getUnreadNotificationsDTO(userId)).thenReturn(unreadNotificationsDTO);
		service.processEvent(notificationEvent);

		verify(userNotificationService).getUnreadNotificationsDTO(userId);
		verify(userNotificationCache).putUnreadNotificationInfo(userId, unreadNotificationsDTO);
	}

}
