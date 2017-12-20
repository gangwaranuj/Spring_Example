package com.workmarket.dao.rating;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.rating.RatingPagination;
import com.workmarket.domains.model.rating.RatingSummary;
import com.workmarket.domains.model.rating.RatingWorkData;
import com.workmarket.domains.model.reporting.RatingReportPagination;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RatingDAO extends PaginatableDAOInterface<Rating> {

	RatingPagination findByUser(Long userId, RatingPagination pagination);

	RatingPagination findForUserForWork(final Long userId, final Long workId, RatingPagination pagination);

	RatingPagination findByUserCompanyForWork(Long companyId, Long workId, RatingPagination ratingPagination);

	Double findSatisfactionRateForUser(Long userId);

	Double findSatisfactionRateForVendor(List<Long> userIds);

	RatingPagination findFlaggedForUser(final Long userId, RatingPagination pagination);

	RatingReportPagination buildRatingReportForCompany(final Long companyId, RatingReportPagination pagination);

	Map<Long, Rating> findLatestForUserVisibleToCompanyInWork(Long forUserId, Long byCompanyId, Collection<Long> workIds);

	Integer countAllUserRatings(Long userId);

	RatingSummary findRatingSummaryForUser(Long userId);

	RatingSummary findRatingSummaryForVendor(List<Long> userIds);

	RatingSummary findRatingSummaryForUserSinceDate(Long userId, Calendar fromDate);

	RatingSummary findRatingSummaryForVendorSinceDate(List<Long> userIds, Calendar fromDate);

	RatingSummary findRatingSummaryForUserByCompany(Long userId, Long companyId);

	RatingSummary findRatingSummaryForVendorByCompany(List<Long> userIds, Long companyId);

	RatingSummary findRatingSummaryForUserByCompanySinceDate(Long userId, Long companyId, Calendar fromDate);

	RatingSummary findRatingSummaryForVendorByCompanySinceDate(List<Long> userIds, Long companyId, Calendar fromDate);

	Double findSatisfactionRateOverallSinceDate(Long userId, Calendar fromDate);

	Double findVendorSatisfactionRateOverallSince(List<Long> userIds, Calendar fromDate);

	Rating findLatestForUserForWork(Long ratedUserId, Long workId);

	void markRatingsNonPendingByWorkId(Long workId);

	RatingWorkData findLatestRatingDataForResourceByWorkNumber(Long userId, String workNumber);
}
