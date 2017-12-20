package com.workmarket.service.analytics.cache;

import com.google.common.base.Optional;
import com.workmarket.domains.model.analytics.BuyerScoreCard;
import com.workmarket.domains.model.analytics.ResourceScoreCard;
import com.workmarket.domains.model.analytics.VendorScoreCard;

import java.util.List;
import java.util.Map;

/**
 * Created by nick on 9/30/13 4:06 PM
 *
 */
public interface ScorecardCache {

	void put(long vendorId, VendorScoreCard card);

	void put(long companyId, long vendorId, VendorScoreCard card);

	void put(long userId, ResourceScoreCard card);

	void put(long companyId, long userId, ResourceScoreCard card);

	void put(long companyId, BuyerScoreCard card);

	Optional<ResourceScoreCard> getResourceScorecard(long userId);

	Optional<ResourceScoreCard> getResourceScorecard(long companyId, long userId);

	Optional<VendorScoreCard> getVendorScorecard(long vendorId);

	Optional<VendorScoreCard> getVendorScorecard(long companyId, long vendorId);

	Map<Long, ResourceScoreCard> getResourceScorecards(List<Long> userId);

	Map<Long, ResourceScoreCard> getResourceScorecards(long companyId, List<Long> userId);

	Map<Long, VendorScoreCard> getVendorScorecards(List<Long> vendorIds);

	Map<Long, VendorScoreCard> getVendorScorecards(Long companyId, List<Long> vendorIds);

	Optional<BuyerScoreCard> getBuyerScorecard(long companyId);

	void evictAllResourceScoreCardsForUser(long userId);
}
