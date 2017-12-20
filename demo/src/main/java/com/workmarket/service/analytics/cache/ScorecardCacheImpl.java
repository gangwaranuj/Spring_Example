package com.workmarket.service.analytics.cache;

import com.google.common.collect.Lists;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.analytics.BuyerScoreCard;
import com.workmarket.domains.model.analytics.ResourceScoreCard;
import com.workmarket.domains.model.analytics.VendorScoreCard;
import com.workmarket.redis.AnalyticsRedisFilter;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.event.company.VendorSearchIndexEvent;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.transform;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Service
public class ScorecardCacheImpl implements ScorecardCache {

	@Autowired RedisAdapter redisAdapter;
	@Autowired JsonSerializationService jsonSerializationService;
	@Autowired CompanyService companyService;
	@Autowired EventRouter eventRouter;

	private static final long RESOURCE_SCORE_CARD_COMPANY_EXPIRATION_TIME_IN_SECONDS = 60 * 60 * 3;
	private static final long RESOURCE_SCORE_CARD_EXPIRATION_TIME_IN_SECONDS = 60 * 60 * 3;
	private static final long BUYER_SCORE_CARD_EXPIRATION_TIME_IN_SECONDS = 60 * 60 * 5;

	@Override
	public void put(long vendorId, VendorScoreCard card) {
		redisAdapter.set(
				AnalyticsRedisFilter.getVendorScoreCardVendorKey(vendorId),
				jsonSerializationService.toJson(card),
				RESOURCE_SCORE_CARD_EXPIRATION_TIME_IN_SECONDS);
	}

	@Override
	public void put(long companyId, long vendorId, VendorScoreCard card) {
		redisAdapter.set(
				AnalyticsRedisFilter.getVendorScoreCardCompanyVendorKey(companyId, vendorId),
				jsonSerializationService.toJson(card),
				RESOURCE_SCORE_CARD_EXPIRATION_TIME_IN_SECONDS);
	}

	@Override
	public void put(long userId, ResourceScoreCard card) {
		redisAdapter.set(
				AnalyticsRedisFilter.getResourceScoreCardUserKey(userId),
				jsonSerializationService.toJson(card),
				RESOURCE_SCORE_CARD_EXPIRATION_TIME_IN_SECONDS);
	}

	@Override
	public void put(long companyId, long userId, ResourceScoreCard card) {
		redisAdapter.set(
				AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(companyId, userId),
				jsonSerializationService.toJson(card),
				RESOURCE_SCORE_CARD_COMPANY_EXPIRATION_TIME_IN_SECONDS);
	}

	@Override
	public void put(long companyId, BuyerScoreCard card) {
		redisAdapter.set(
				AnalyticsRedisFilter.getBuyerScoreCardCompanyKey(companyId),
				jsonSerializationService.toJson(card),
				BUYER_SCORE_CARD_EXPIRATION_TIME_IN_SECONDS);
	}

	@Override
	public Optional<ResourceScoreCard> getResourceScorecard(long userId) {
		Optional<Object> result = redisAdapter.get(AnalyticsRedisFilter.getResourceScoreCardUserKey(userId));
		return result.isPresent() ?
				Optional.of(jsonSerializationService.fromJson(result.get().toString(), ResourceScoreCard.class)) :
				Optional.<ResourceScoreCard>absent();
	}

	@Override
	public Optional<ResourceScoreCard> getResourceScorecard(long companyId, long userId) {
		Optional<Object> result = redisAdapter.get(AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(companyId, userId));
		return result.isPresent() ?
				Optional.of(jsonSerializationService.fromJson(result.get().toString(), ResourceScoreCard.class)) :
				Optional.<ResourceScoreCard>absent();
	}

	@Override
	public Optional<VendorScoreCard> getVendorScorecard(long vendorId) {
		Optional<Object> result = redisAdapter.get(AnalyticsRedisFilter.getVendorScoreCardVendorKey(vendorId));
		return result.isPresent() ?
				Optional.of(jsonSerializationService.fromJson(result.get().toString(), VendorScoreCard.class)) :
				Optional.<VendorScoreCard>absent();
	}

	@Override
	public Optional<VendorScoreCard> getVendorScorecard(long companyId, long vendorId) {
		Optional<Object> result = redisAdapter.get(AnalyticsRedisFilter.getVendorScoreCardCompanyVendorKey(companyId, vendorId));
		return result.isPresent() ?
				Optional.of(jsonSerializationService.fromJson(result.get().toString(), VendorScoreCard.class)) :
				Optional.<VendorScoreCard>absent();
	}

	@Override
	public Map<Long, ResourceScoreCard> getResourceScorecards(final List<Long> userIds) {
		Map<Long, ResourceScoreCard> result = Maps.newHashMap();
		if (isEmpty(userIds)) { return result; }
		List<Object> cacheResults = redisAdapter.getMultiple(transform(userIds, new Function<Long, String>() {
			@Override public String apply(Long userId) {
				return AnalyticsRedisFilter.getResourceScoreCardUserKey(userId);
			}
		}));
		for (int i = 0; i < cacheResults.size(); i++) {
			Object cacheResult = cacheResults.get(i);
			if (cacheResult != null) {
				result.put(userIds.get(i), jsonSerializationService.fromJson(cacheResult.toString(), ResourceScoreCard.class));
			}
		}
		return result;
	}

	@Override
	public Map<Long, ResourceScoreCard> getResourceScorecards(final long companyId, final List<Long> userIds) {
		Map<Long, ResourceScoreCard> result = Maps.newHashMap();
		if (isEmpty(userIds)) { return result; }
		List<Object> cacheResults = redisAdapter.getMultiple(transform(userIds, new Function<Long, String>() {
			@Override public String apply(Long userId) {
				return AnalyticsRedisFilter.getResourceScoreCardCompanyUserKey(companyId, userId);
			}
		}));
		for (int i = 0; i < cacheResults.size(); i++) {
			Object cacheResult = cacheResults.get(i);
			if (cacheResult != null) {
				result.put(userIds.get(i), jsonSerializationService.fromJson(cacheResult.toString(), ResourceScoreCard.class));
			}
		}
		return result;
	}

	@Override
	public Map<Long, VendorScoreCard> getVendorScorecards(final List<Long> vendorIds) {
		Map<Long, VendorScoreCard> result = Maps.newHashMap();
		if (isEmpty(vendorIds)) { return result; }
		List<Object> cacheResults = redisAdapter.getMultiple(transform(vendorIds, new Function<Long, String>() {
			@Override public String apply(Long vendorId) {
				return AnalyticsRedisFilter.getVendorScoreCardVendorKey(vendorId);
			}
		}));
		for (int i = 0; i < cacheResults.size(); i++) {
			Object cacheResult = cacheResults.get(i);
			if (cacheResult != null) {
				result.put(vendorIds.get(i), jsonSerializationService.fromJson(cacheResult.toString(), VendorScoreCard.class));
			}
		}
		return result;
	}

	@Override
	public Map<Long, VendorScoreCard> getVendorScorecards(final Long companyId, final List<Long> vendorIds) {
		Map<Long, VendorScoreCard> result = Maps.newHashMap();
		if (isEmpty(vendorIds)) { return result; }
		List<Object> cacheResults = redisAdapter.getMultiple(transform(vendorIds, new Function<Long, String>() {
			@Override public String apply(Long vendorId) {
				return AnalyticsRedisFilter.getVendorScoreCardCompanyVendorKey(companyId, vendorId);
			}
		}));
		for (int i = 0; i < cacheResults.size(); i++) {
			Object cacheResult = cacheResults.get(i);
			if (cacheResult != null) {
				result.put(vendorIds.get(i), jsonSerializationService.fromJson(cacheResult.toString(), VendorScoreCard.class));
			}
		}
		return result;
	}

	@Override
	public Optional<BuyerScoreCard> getBuyerScorecard(long companyId) {
		Optional<Object> result = redisAdapter.get(AnalyticsRedisFilter.getBuyerScoreCardCompanyKey(companyId));
		return result.isPresent() ?
				Optional.of(jsonSerializationService.fromJson(result.get().toString(), BuyerScoreCard.class)) :
				Optional.<BuyerScoreCard>absent();
	}

	@Override
	public void evictAllResourceScoreCardsForUser(long userId) {
		redisAdapter.delete(AnalyticsRedisFilter.getResourceScoreCardUserKey(userId));
		eventRouter.sendEvent(new UserSearchIndexEvent(userId));

		// evict all company-associated cards
		for (Object key : redisAdapter.getKeys(AnalyticsRedisFilter.getResourceScoreCardAllCompanyUserPatternUserKey(userId))) {
			redisAdapter.delete((String) key);
		}

		List<Long> companyIds = companyService.findCompanyIdsForUsers(Lists.newArrayList(userId));
		if (!companyIds.isEmpty()) {
			redisAdapter.delete(AnalyticsRedisFilter.getVendorScoreCardVendorKey(companyIds.get(0)));
			eventRouter.sendEvent(new VendorSearchIndexEvent(companyIds.get(0)));

			for (Object key : redisAdapter.getKeys(AnalyticsRedisFilter.getVendorScoreCardAllCompanyVendorKey(companyIds.get(0)))) {
				redisAdapter.delete((String) key);
			}
		}
	}

}
