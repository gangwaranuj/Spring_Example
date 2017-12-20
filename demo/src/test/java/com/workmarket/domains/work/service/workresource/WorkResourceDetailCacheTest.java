package com.workmarket.domains.work.service.workresource;

import com.google.common.base.Optional;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.business.JsonSerializationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class WorkResourceDetailCacheTest {

	@Mock RedisAdapter redisAdapter;
	@Mock JsonSerializationService jsonSerializationService;
	@InjectMocks WorkResourceDetailCacheImpl workResourceDetailCacheImpl;

	@Test
	public void get_withNullPagination_returnAbsentObject() {
		assertEquals(Optional.<WorkResourceDetailPagination>absent(), workResourceDetailCacheImpl.get(1, null));
	}
}
