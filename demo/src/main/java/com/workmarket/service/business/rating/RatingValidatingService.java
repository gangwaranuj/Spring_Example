package com.workmarket.service.business.rating;

/**
 * User: iloveopt
 * Date: 1/14/14
 */
public interface RatingValidatingService {

	boolean isWorkRatingRatableByUser(Long workId, Long userId);

	boolean isWorkRatingEditableByUser(Long workId, Long userId);

}
