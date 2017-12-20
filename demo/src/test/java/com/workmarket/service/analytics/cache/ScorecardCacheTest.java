package com.workmarket.service.analytics.cache;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.analytics.BuyerScoreCard;
import com.workmarket.domains.model.analytics.ResourceScoreCard;
import com.workmarket.redis.AnalyticsRedisFilter;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.infra.event.EventRouter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScorecardCacheTest {

	@Mock RedisAdapter redisAdapter;
	@Mock JsonSerializationService jsonSerializationService;
	@Mock CompanyService companyService;
	@Mock EventRouter eventRouter;
	@InjectMocks ScorecardCacheImpl cache;

	@Test
	public void put_ResourceScorecard_ByUser_isSerialized() throws Exception {
		ResourceScoreCard scoreCard = new ResourceScoreCard();
		cache.put(1L, scoreCard);
		verify(jsonSerializationService).toJson(scoreCard);
	}

	@Test
	public void put_ResourceScorecard_ByUser_StoredInRedisWithExpiry() throws Exception {
		ResourceScoreCard scoreCard = new ResourceScoreCard();
		String jsonResponse = RandomStringUtils.random(4);
		when(jsonSerializationService.toJson(scoreCard)).thenReturn(jsonResponse);

		cache.put(1L, scoreCard);

		verify(redisAdapter).set(
				eq(AnalyticsRedisFilter.getResourceScoreCardUserKey(1L)),
				eq(jsonResponse),
				anyLong());
	}

	@Test
	public void put_ResourceScorecard_ByUserAndCompany_isSerialized() throws Exception {
		ResourceScoreCard scoreCard = new ResourceScoreCard();
		cache.put(1L, 2L, scoreCard);
		verify(jsonSerializationService).toJson(scoreCard);
	}

	@Test
	public void put_ResourceScorecard_ByUserAndCompany_StoredInRedisWithExpiry() throws Exception {
		ResourceScoreCard scoreCard = new ResourceScoreCard();
		String jsonResponse = RandomStringUtils.random(4);
		when(jsonSerializationService.toJson(scoreCard)).thenReturn(jsonResponse);

		cache.put(1L, 2L, scoreCard);
		
		verify(redisAdapter).set(
				eq(AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(1L, 2L)),
				eq(jsonResponse),
				anyLong());
	}

	@Test
	public void put_BuyerScorecard_ByCompany_isSerialized() throws Exception {
		BuyerScoreCard scoreCard = new BuyerScoreCard();
		cache.put(3L, scoreCard);
		verify(jsonSerializationService).toJson(scoreCard);
	}

	@Test
	public void put_BuyerScorecard_ByCompany_StoredInRedisWithExpiry() throws Exception {
		BuyerScoreCard scoreCard = new BuyerScoreCard();
		String jsonResponse = RandomStringUtils.random(4);
		when(jsonSerializationService.toJson(scoreCard)).thenReturn(jsonResponse);

		cache.put(3L, scoreCard);

		verify(redisAdapter).set(
				eq(AnalyticsRedisFilter.getBuyerScoreCardCompanyKey(3L)),
				eq(jsonResponse),
				anyLong());
	}

	@Test
	public void getResourceScorecard_ByUserNotPresent_NoResult() throws Exception {
		String key = AnalyticsRedisFilter.getResourceScoreCardUserKey(2L);
		when(redisAdapter.get(key)).thenReturn(Optional.absent());
		assertFalse(cache.getResourceScorecard(2L).isPresent());
	}

	@Test
	public void getResourceScorecard_ByUserPresent_GetResult() throws Exception {
		String key = AnalyticsRedisFilter.getResourceScoreCardUserKey(4L);
		when(redisAdapter.get(key)).thenReturn(Optional.<Object>of(new ResourceScoreCard()));
		when(jsonSerializationService.fromJson(anyString(), any(Class.class))).thenReturn(new ResourceScoreCard());
		assertTrue(cache.getResourceScorecard(4L).isPresent());
	}

	@Test
	public void getResourceScorecard_ByUserAndCompanyNotPresent_NoResult() throws Exception {
		String key = AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(5L, 6L);
		when(redisAdapter.get(key)).thenReturn(Optional.absent());
		assertFalse(cache.getResourceScorecard(5L, 6L).isPresent());
	}

	@Test
	public void getResourceScorecard_ByUserAndCompanyPresent_GetResult() throws Exception {
		String key = AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(7L, 8L);
		when(redisAdapter.get(key)).thenReturn(Optional.<Object>of(new ResourceScoreCard()));
		when(jsonSerializationService.fromJson(anyString(), any(Class.class))).thenReturn(new ResourceScoreCard());
		assertTrue(cache.getResourceScorecard(7L, 8L).isPresent());
	}

	@Test
	public void getBuyerScorecard_ByCompanyNotPresent_NoResult() throws Exception {
		String key = AnalyticsRedisFilter.getBuyerScoreCardCompanyKey(10L);
		when(redisAdapter.get(key)).thenReturn(Optional.absent());
		assertFalse(cache.getBuyerScorecard(10L).isPresent());
	}

	@Test
	public void getBuyerScorecard_ByCompanyPresent_GetResult() throws Exception {
		String key = AnalyticsRedisFilter.getBuyerScoreCardCompanyKey(11L);
		when(redisAdapter.get(key)).thenReturn(Optional.<Object>of(new BuyerScoreCard()));
		when(jsonSerializationService.fromJson(anyString(), any(Class.class))).thenReturn(new BuyerScoreCard());
		assertTrue(cache.getBuyerScorecard(11L).isPresent());
	}

	@Test
	public void getResourceScorecards_ByUser_NullArgument_EmptyResult() throws Exception {
		when(redisAdapter.getMultiple(null)).thenReturn(Lists.newArrayList());
		assertTrue((cache.getResourceScorecards(null).isEmpty()));
	}

	@Test
	public void getResourceScorecards_ByUser_EmptyArgument_EmptyResult() throws Exception {
		when(redisAdapter.getMultiple(anyListOf(String.class))).thenReturn(Lists.newArrayList());
		assertTrue((cache.getResourceScorecards(Lists.<Long>newArrayList()).isEmpty()));
	}

	@Test
	public void getResourceScorecards_ByUser_BothInCache_GetCachedResult() throws Exception {
		String key1 = AnalyticsRedisFilter.getResourceScoreCardUserKey(1L);
		String key2 = AnalyticsRedisFilter.getResourceScoreCardUserKey(2L);
		when(redisAdapter.getMultiple(Lists.newArrayList(key1, key2)))
				.thenReturn(Lists.<Object>newArrayList(new ResourceScoreCard(), new ResourceScoreCard()));
		when(jsonSerializationService.fromJson(anyString(), any(Class.class))).thenReturn(new ResourceScoreCard());

		cache.getResourceScorecards(Lists.newArrayList(1L, 2L));
		verify(jsonSerializationService, times(2)).fromJson(anyString(), any(Class.class));
	}

	@Test
	public void getResourceScorecards_ByUser_OneOfTwoInCache_GetOneCachedResult() throws Exception {
		String key1 = AnalyticsRedisFilter.getResourceScoreCardUserKey(1L);
		String key2 = AnalyticsRedisFilter.getResourceScoreCardUserKey(2L);
		when(redisAdapter.getMultiple(Lists.newArrayList(key1, key2)))
				.thenReturn(Lists.<Object>newArrayList(new ResourceScoreCard()));
		when(jsonSerializationService.fromJson(anyString(), any(Class.class))).thenReturn(new ResourceScoreCard());

		cache.getResourceScorecards(Lists.newArrayList(1L, 2L));
		verify(jsonSerializationService).fromJson(anyString(), any(Class.class));
	}

	@Test
	public void getResourceScorecards_ByUser_NeitherInCache_GetNoCachedResults() throws Exception {
		String key1 = AnalyticsRedisFilter.getResourceScoreCardUserKey(1L);
		String key2 = AnalyticsRedisFilter.getResourceScoreCardUserKey(2L);
		when(redisAdapter.getMultiple(Lists.newArrayList(key1, key2)))
				.thenReturn(Lists.newArrayList());
		when(jsonSerializationService.fromJson(anyString(), any(Class.class))).thenReturn(new ResourceScoreCard());

		cache.getResourceScorecards(Lists.newArrayList(1L, 2L));
		verify(jsonSerializationService, never()).fromJson(anyString(), any(Class.class));
	}

	@Test
	public void getResourceScorecards_ByCompany_NullArgument_EmptyResult() throws Exception {
		when(redisAdapter.getMultiple(null)).thenReturn(Lists.newArrayList());
		assertTrue((cache.getResourceScorecards(1L, null).isEmpty()));
	}

	@Test
	public void getResourceScorecards_ByCompany_EmptyArgument_EmptyResult() throws Exception {
		when(redisAdapter.getMultiple(anyListOf(String.class))).thenReturn(Lists.newArrayList());
		assertTrue((cache.getResourceScorecards(1L, Lists.<Long>newArrayList()).isEmpty()));
	}

	@Test
	public void getResourceScorecards_ByCompany_BothInCache_GetCachedResult() throws Exception {
		String key1 = AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(6L, 1L);
		String key2 = AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(6L, 2L);
		when(redisAdapter.getMultiple(Lists.newArrayList(key1, key2)))
				.thenReturn(Lists.<Object>newArrayList(new ResourceScoreCard(), new ResourceScoreCard()));
		when(jsonSerializationService.fromJson(anyString(), any(Class.class))).thenReturn(new ResourceScoreCard());

		cache.getResourceScorecards(6L, Lists.newArrayList(1L, 2L));
		verify(jsonSerializationService, times(2)).fromJson(anyString(), any(Class.class));
	}

	@Test
	public void getResourceScorecards_ByCompany_OneOfTwoInCache_GetOneCachedResult() throws Exception {
		String key1 = AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(6L, 1L);
		String key2 = AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(6L, 2L);
		when(redisAdapter.getMultiple(Lists.newArrayList(key1, key2)))
				.thenReturn(Lists.<Object>newArrayList(new ResourceScoreCard()));
		when(jsonSerializationService.fromJson(anyString(), any(Class.class))).thenReturn(new ResourceScoreCard());

		cache.getResourceScorecards(6L, Lists.newArrayList(1L, 2L));
		verify(jsonSerializationService).fromJson(anyString(), any(Class.class));
	}

	@Test
	public void getResourceScorecards_ByCompany_NeitherInCache_GetNoCachedResults() throws Exception {
		String key1 = AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(6L, 1L);
		String key2 = AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(6L, 2L);
		when(redisAdapter.getMultiple(Lists.newArrayList(key1, key2)))
				.thenReturn(Lists.newArrayList());
		when(jsonSerializationService.fromJson(anyString(), any(Class.class))).thenReturn(new ResourceScoreCard());

		cache.getResourceScorecards(6L, Lists.newArrayList(1L, 2L));
		verify(jsonSerializationService, never()).fromJson(anyString(), any(Class.class));
	}

	@Test
	public void evictAllResourceScoreCardsForUser_BothKeyTypes_AllDeleted() throws Exception {
		String userKey = AnalyticsRedisFilter.getResourceScoreCardUserKey(1L);
		String companyKey1 = AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(2L, 1L);
		String companyKey2 = AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(2L, 1L);
		when(redisAdapter.getKeys(AnalyticsRedisFilter.getResourceScoreCardAllCompanyUserPatternUserKey(1L)))
				.thenReturn(Sets.<Object>newHashSet(companyKey1, companyKey2));

		cache.evictAllResourceScoreCardsForUser(1L);
		verify(redisAdapter).delete(userKey);
		verify(redisAdapter).delete(companyKey1);
		verify(redisAdapter).delete(companyKey2);
	}

	@Test
	public void evictAllResourceScoreCardsForVendor_BothKeyTypes_AllDeleted() throws Exception {
		String vendorKey = AnalyticsRedisFilter.getVendorScoreCardVendorKey(1L);
		String companyKey1 = AnalyticsRedisFilter.getVendorScoreCardCompanyVendorKey(1L, 1L);
		String companyKey2 = AnalyticsRedisFilter.getVendorScoreCardCompanyVendorKey(2L, 1L);
		List <Long> companyIds = Lists.newArrayList(1L);
		when(redisAdapter.getKeys(AnalyticsRedisFilter.getVendorScoreCardAllCompanyVendorKey(1L)))
				.thenReturn(Sets.<Object>newHashSet(companyKey1, companyKey2));
		when(companyService.findCompanyIdsForUsers(anyListOf(Long.class))).thenReturn(companyIds);

		cache.evictAllResourceScoreCardsForUser(1L);
		verify(redisAdapter).delete(vendorKey);
		verify(redisAdapter).delete(companyKey1);
		verify(redisAdapter).delete(companyKey2);
	}
}
