package com.workmarket.common.cache;

import com.google.common.base.Optional;
import com.workmarket.domains.model.notification.UserNotification;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.dto.UnreadNotificationsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Service
public class UserNotificationCacheImpl implements UserNotificationCache {

	@Autowired SerializedObjectCache serializedObjectCache;
	@Autowired RedisAdapter redisAdapter;
	@Autowired @Qualifier("redisCacheOnly") RedisAdapter redisAdapterReadOnly;

	private static final long FIVE_DAYS_IN_SECONDS = TimeUnit.DAYS.toSeconds(5);

	@Override
	public String putNotifications(long userId, List<UserNotification> notifications) {
		if (isEmpty(notifications)) {
			return StringUtils.EMPTY;
		}
		return serializedObjectCache.put(RedisFilters.userNotificationDataKey(userId), notifications, FIVE_DAYS_IN_SECONDS);
	}

	@Override
	public void clearNotifications(long userId) {
		redisAdapter.delete(RedisFilters.userNotificationDataKey(userId));
	}

	@Override
	public void clearUnreadNotificationInfo(long userId) {
		redisAdapterReadOnly.delete(RedisFilters.getUnreadNotificationsInfo(userId));
	}

	@Override
	public void putUnreadNotificationInfo(long userId, UnreadNotificationsDTO unreadNotificationsDTO) {
		redisAdapterReadOnly.set(RedisFilters.getUnreadNotificationsInfo(userId), unreadNotificationsDTO, FIVE_DAYS_IN_SECONDS);
	}

	@Override
	public Optional<String> getNewUserNotificationJson(long userId) {
		return serializedObjectCache.get(RedisFilters.userNotificationDataKey(userId));
	}

	@Override
	public Optional<UnreadNotificationsDTO> getUnreadNotificationsInfoByUser(long userId) {
		Optional<Object> unreadNotificationsDTOOptional = redisAdapterReadOnly.get(RedisFilters.getUnreadNotificationsInfo(userId));
		if (!unreadNotificationsDTOOptional.isPresent()) {
			return Optional.absent();
		}

		return Optional.of((UnreadNotificationsDTO) unreadNotificationsDTOOptional.get());
	}

	@Override
	public Long getTimeOutInSeconds() {
		return FIVE_DAYS_IN_SECONDS;
	}
}
