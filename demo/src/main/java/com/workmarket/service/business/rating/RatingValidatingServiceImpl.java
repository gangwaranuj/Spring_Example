package com.workmarket.service.business.rating;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.rating.RatingPagination;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.work.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * User: iloveopt
 * Date: 1/14/14
 */
@Service
public class RatingValidatingServiceImpl implements RatingValidatingService {

	@Autowired RatingService ratingService;
	@Autowired WorkService workService;
	@Autowired UserService userService;

	final String WORK_RATING_RATABLE = "WORK_RATING_RATABLE";
	final String WORK_RATING_EDITABLE = "WORK_RATING_EDITABLE";

	@Override
	public boolean isWorkRatingRatableByUser(Long workId, Long userId) {
		Assert.notNull(workId);
		Assert.notNull(userId);

		return isWorkRatingActionValidate(workId, userId, WORK_RATING_RATABLE);
	}

	@Override
	public boolean isWorkRatingEditableByUser(Long workId, Long userId) {
		Assert.notNull(workId);
		Assert.notNull(userId);

		User user = userService.getUser(userId);
		RatingPagination ratingPagination = new RatingPagination();
		List<Rating> ratings = ratingService.findByUserCompanyForWork(user.getCompany().getId(), workId, ratingPagination);

		if (ratings.isEmpty()) {
			return false;
		}

		return isWorkRatingActionValidate(workId, userId, WORK_RATING_EDITABLE);
	}

	private boolean isWorkRatingActionValidate(Long workId, Long userId, String workRatingValidationType) {

		Work work = workService.findWork(workId);
		User user = userService.getUser(userId);
		WorkResource workResource = workService.findActiveWorkResource(workId);

		if(work == null || user == null || work.getWorkStatusType() == null || work.getBuyer() == null) {
			return false;
		}

		if (user.getCompany().getId().equals(work.getBuyer().getCompany().getId())) {
			// Buyer
			if (WORK_RATING_EDITABLE.equals(workRatingValidationType)) {
				return isWorkRatingEditableByUser(work.getWorkStatusType(), true);
			} else if (WORK_RATING_RATABLE.equals(workRatingValidationType)) {
				return isWorkRatingRatableByUser(work.getWorkStatusType(), true);
			}
		} else if (workResource != null && workResource.getUser() != null && workResource.getUser().getId().equals(user.getId())) {
			// Resource
			if (WORK_RATING_EDITABLE.equals(workRatingValidationType)) {
				return isWorkRatingEditableByUser(work.getWorkStatusType(), false);
			} else if (WORK_RATING_RATABLE.equals(workRatingValidationType)) {
				return isWorkRatingRatableByUser(work.getWorkStatusType(), false);
			}
		}
		return false;


	}

	private boolean isWorkRatingRatableByUser(WorkStatusType workStatusType, boolean isBuyer) {

		if (isBuyer) {
			// Buyer
			switch (workStatusType.getCode()) {
				case WorkStatusType.DRAFT: return false;
				case WorkStatusType.SENT: return false;
				default: return true;
			}
		} else {
			// Resource
			switch (workStatusType.getCode()) {
				// TODO: zhe refactor to make default false and list the case return true
				case WorkStatusType.DRAFT: return false;
				case WorkStatusType.SENT: return false;
				case WorkStatusType.ACTIVE: return false;
				case WorkStatusType.INPROGRESS: return false;
				case WorkStatusType.COMPLETE: return false;
				default:return true;
			}
		}
	}

	private boolean isWorkRatingEditableByUser(WorkStatusType workStatusType, boolean isBuyer) {

		if (isBuyer) {
			// Buyer
			switch (workStatusType.getCode()) {
				case WorkStatusType.DRAFT: return false;
				case WorkStatusType.SENT: return false;
				case WorkStatusType.PAID: return false;
				default: return true;
			}
		} else {
			// Resource
			switch (workStatusType.getCode()) {
				case WorkStatusType.DRAFT: return false;
				case WorkStatusType.SENT: return false;
				case WorkStatusType.ACTIVE: return false;
				case WorkStatusType.INPROGRESS: return false;
				case WorkStatusType.COMPLETE: return false;
				case WorkStatusType.PAID: return false;
				default:return true;
			}
		}
	}


}
