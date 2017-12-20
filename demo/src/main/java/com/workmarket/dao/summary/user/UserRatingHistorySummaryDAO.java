package com.workmarket.dao.summary.user;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.rating.AverageRating;
import com.workmarket.domains.model.summary.user.UserRatingHistorySummary;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserRatingHistorySummaryDAO extends DAOInterface<UserRatingHistorySummary> {

	BigDecimal calculatePercentageRatingsByCompany(long companyId, int ratingValue, DateRange dateRange, Boolean buyerRatings);

	int deleteRatingHistorySummaryByRatingId(long ratingId);

	/**
	 * Returns Map<K, Map<K2, V>>>
	 * K = userId
	 * K2 = companyId
	 * V = SatisfactionRate
	 * @return
	 */
	Map<Long, Map<Long, AverageRating>> findAllSatisfactionRatePerUserPerCompany();

	Map<Long, AverageRating> findSatisfactionForUsersByCompany(List<Long> userIds, Long companyId);

	AverageRating findAverageRatingForUserByCompany(Long userId, Long companyId);
}
