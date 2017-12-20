package com.workmarket.common.cache;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.notification.UserNotification;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserNotificationCacheImplTest {

	@Mock SerializedObjectCache serializedObjectCache;
	@Mock RedisAdapter redisAdapter;
	@InjectMocks UserNotificationCacheImpl cache;

	static final String SERIALIZED_OUTPUT = "seerealeyezed ootpoot";
	List<UserNotification> oneNewOneOld;
	UserNotification newNotification;
	Long userId = 1L;

	@Before
	public void setUp() {
		when(serializedObjectCache.get(anyString()))
			.thenReturn(Optional.<String>absent());
		when(serializedObjectCache.get(RedisFilters.userNotificationDataKey(userId)))
			.thenReturn(Optional.of(SERIALIZED_OUTPUT));

		when(serializedObjectCache.put(RedisFilters.userNotificationDataKey(userId), Lists.<UserNotification>newArrayList(), cache.getTimeOutInSeconds()))
			.thenReturn(StringUtils.EMPTY);

		newNotification = new UserNotification();
		newNotification.setViewedAt(null);
		UserNotification oldNotification = new UserNotification();
		newNotification.setViewedAt(Calendar.getInstance());
		oneNewOneOld = Lists.newArrayList(newNotification, oldNotification);

		when(serializedObjectCache.put(eq(RedisFilters.userNotificationDataKey(userId)), anyListOf(UserNotification.class), eq(cache.getTimeOutInSeconds())))
			.thenReturn(SERIALIZED_OUTPUT);
	}

	@Test
	public void putNotifications_NullInput_EmptyResult() {
		assertTrue(cache.putNotifications(userId, null).equals(StringUtils.EMPTY));
	}

	@Test
	public void putNotifications_EmptyInput_EmptyResult() {
		assertTrue(cache.putNotifications(userId, Lists.<UserNotification>newArrayList()).equals(StringUtils.EMPTY));
	}

	@Test
	public void putNotifications_ValidList_SerializedResult() {
		assertTrue(cache.putNotifications(userId, null).equals(StringUtils.EMPTY));
	}

	@Test
	public void putNotifications_MixedList_AllNotificationSaved() {
		cache.putNotifications(userId, oneNewOneOld);
		verify(serializedObjectCache).put(RedisFilters.userNotificationDataKey(userId), oneNewOneOld, cache.getTimeOutInSeconds());
	}

	@Test
	public void getNewUserNotificationJson_ValidId_ReturnsValue() {
		assertTrue(cache.getNewUserNotificationJson(userId).isPresent());
	}

	@Test
	public void getNewUserNotificationJson_ValidId_ReturnsSerializedValue() {
		assertTrue(cache.getNewUserNotificationJson(userId).get().equals(SERIALIZED_OUTPUT));
	}
}
