package com.workmarket.service.business;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.rating.AverageRating;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.rating.RatingPagination;
import com.workmarket.domains.model.rating.RatingSummary;
import com.workmarket.domains.model.rating.RatingWorkData;
import com.workmarket.domains.model.reporting.RatingReportPagination;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.business.dto.WorkRatingDTO;
import com.workmarket.service.exception.RatingException;

import java.util.Calendar;
import java.util.List;

public interface RatingService {

	void rateWork(WorkRatingDTO ratingDTO) throws RatingException;

	Rating createRatingForWork(WorkRatingDTO ratingDTO);
	Rating createRatingForWork(Long raterUserId, Long ratedUserId, Long workId, RatingDTO ratingDTO);

	Rating flagRatingForReview(Long ratingId, boolean flagForReview);

	Rating deleteRating(Long ratingId);

	Double findSatisfactionRateForUser(Long userId);
	Double findAverageStarRatingInLastNMonths(Long userId, Integer nMonths);

	List<Rating> findRatingsForUserForWork(Long userId, Long workId);

	List<Rating> findByUserCompanyForWork(Long companyId, Long workId, RatingPagination ratingPagination);

	RatingReportPagination findRatingsByCompany(Long companyId, RatingReportPagination pagination);

	WorkPagination findAllWorkPendingRatingByResource(final Long userId, WorkPagination pagination);

	RatingPagination findRatingsFlaggedForUser(Long userId, RatingPagination pagination);

	Integer countAllUserRatings(Long userId);

	RatingSummary findRatingSummaryForUser(Long userId);

	RatingSummary findRatingSummaryForUserSinceDate(Long userId, Calendar fromDate);

	RatingSummary findRatingSummaryForUserByCompany(Long userId, Long companyId);

	RatingSummary findRatingSummaryForUserByCompanySinceDate(Long userId, Long companyId, Calendar fromDate);

	RatingSummary findRatingSummaryForVendor(Long vendorId);

	RatingSummary findRatingSummaryForVendorSinceDate(Long vendorId, Calendar fromDate);

	RatingSummary findRatingSummaryForVendorByCompany(Long vendorId, Long companyId);

	RatingSummary findRatingSummaryForVendorByCompanySinceDate(Long vendorId, Long companyId, Calendar fromDate);

	boolean hasWorkPendingRatingByResource(Long id);

	Rating findLatestRatingForUserForWork(Long ratedUserId, Long workId);

	void updateLatestRatingForUserForWork(Long workId, Long ratedUserId, RatingDTO ratingDTO);

	void markRatingsNonPending(Long workId);

	void markRatingsNonPending(WorkResource workResource);

	RatingWorkData findLatestRatingDataForResourceByWorkNumber(Long userId, String workNumber);

	AverageRating findAverageRatingForUserByCompany(final Long userId, final Long companyId);

	AverageRating refreshAverageRatingForUserByCompany(final Long userId, final Long companyId);
}