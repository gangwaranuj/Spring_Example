package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.rating.AverageRating;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.dao.rating.RatingDAO;
import com.workmarket.dao.summary.user.UserRatingHistorySummaryDAO;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.rating.RatingPagination;
import com.workmarket.domains.model.rating.RatingSummary;
import com.workmarket.domains.model.rating.RatingWorkData;
import com.workmarket.domains.model.reporting.RatingReportPagination;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.analytics.cache.ScorecardCache;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.business.dto.WorkRatingDTO;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.service.business.rating.RatingValidatingService;
import com.workmarket.service.exception.RatingException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.infra.security.LaneContext;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workmarket.domains.model.rating.Rating.EXCELLENT;
import static com.workmarket.domains.model.rating.Rating.SATISFIED;
import static com.workmarket.domains.model.rating.Rating.UNSATISFIED;

@Service
public class RatingServiceImpl implements RatingService {

	@Autowired private UserDAO userDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private RatingDAO ratingDAO;
	@Autowired private LaneService laneService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private UserGroupValidationService userGroupValidationService;
	@Autowired private EmailTemplateFactory emailTemplateFactory;
	@Autowired private NotificationDispatcher notificationDispatcher;
	@Autowired private SummaryService summaryService;
	@Autowired private UserRatingHistorySummaryDAO userRatingHistorySummaryDAO;
	@Autowired private ScorecardCache scorecardCache;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkService workService;
	@Autowired private ProfileService profileService;
	@Autowired private RatingValidatingService ratingValidatingService;

	private static final Log logger = LogFactory.getLog(RatingServiceImpl.class);

	public static final String AVERAGE_USER_RATING_FOR_COMPANY = RedisConfig.AVERAGE_USER_RATING_FOR_COMPANY;

	public void rateWork(WorkRatingDTO dto) throws RatingException {

		Integer value = dto.getValue();
		Integer communication = dto.getCommunication();
		Integer professionalism = dto.getProfessionalism();
		Integer quality = dto.getQuality();

		//how do i make it so this will speak to the API?

		if (!CollectionUtilities.containsAny(value, EXCELLENT, SATISFIED, UNSATISFIED)) {
			throw new RatingException("assignment.add_rating.badvalue");
		}
		if (!CollectionUtilities.containsAny(communication, EXCELLENT, SATISFIED, UNSATISFIED)) {
			throw new RatingException("assignment.add_rating.badvalue");
		}
		if (!CollectionUtilities.containsAny(professionalism, EXCELLENT, SATISFIED, UNSATISFIED)) {
			throw new RatingException("assignment.add_rating.badvalue");
		}
		if (!CollectionUtilities.containsAny(quality, EXCELLENT, SATISFIED, UNSATISFIED)) {
			throw new RatingException("assignment.add_rating.badvalue");
		}

		final boolean canCreateNewRating = ratingValidatingService.isWorkRatingRatableByUser(dto.getWorkId(), dto.getRaterUserId());
		final boolean canUpdateExistingRating = ratingValidatingService.isWorkRatingEditableByUser(dto.getWorkId(), dto.getRaterUserId());
		final Rating rating = findLatestRatingForUserForWork(dto.getRatedUserId(), dto.getWorkId());

		if (!canCreateNewRating && !canUpdateExistingRating)
		{
			throw new RatingException("assignment.add_rating.error");
		}

		if (rating != null && !canUpdateExistingRating)
		{
			throw new RatingException("assignment.add_rating.alreadyrated");
		}

		if ((rating != null) & canUpdateExistingRating) {
			final RatingDTO ratingDTO = new RatingDTO(dto.getValue(), dto.getQuality(), dto.getProfessionalism(), dto.getCommunication(), dto.getReview());
				updateLatestRatingForUserForWork(dto.getWorkId(), dto.getRatedUserId(), ratingDTO);
		}

		if ((rating == null) && canCreateNewRating) {
				createRatingForWork(dto);
		}

	}

	@Override
	public Rating createRatingForWork(WorkRatingDTO dto) {
		return createRatingForWork(dto.getRaterUserId(), dto.getRatedUserId(), dto.getWorkId(), dto);
	}

	@Override
	public Rating createRatingForWork(Long raterUserId, Long ratedUserId, Long workId, RatingDTO ratingDTO) {
		Assert.notNull(raterUserId);
		Assert.notNull(ratedUserId);
		Assert.notNull(workId);
		Assert.notNull(ratingDTO);

		Work work = workDAO.get(workId);
		Assert.notNull(work);

		User rater = userDAO.get(raterUserId);
		User rated = userDAO.get(ratedUserId);

		Rating rating = new Rating();
		rating.setRatingUser(rater);
		rating.setRatingCompany(rater.getCompany());
		rating.setRatedUser(rated);
		rating.setWork(work);
		//Set a flag if this is a buyer rating.
		rating.setBuyerRating(work.getBuyer().getId().equals(ratedUserId));

		BeanUtils.copyProperties(ratingDTO, rating);

		/**
		 *  Rating visibility (updated w/WORK-3139)
		 *  lane 1 always private
		 *  lane 2 always private
		 *  lane 3 defaults to private w/option to share
		 *  if no lane private
		 */
		LaneContext laneContext = laneService.getLaneContextForUserAndCompany(rated.getId(), rater.getCompany().getId());

		if (laneContext != null && laneContext.getLaneType() != null) {
			if (laneContext.getLaneType().equals(LaneType.LANE_1) || laneContext.getLaneType().equals(LaneType.LANE_2)) {
				rating.setRatingSharedFlag(false);
				rating.setReviewSharedFlag(false);
			}
		} else {
			rating.setRatingSharedFlag(false);
			rating.setReviewSharedFlag(false);
		}

		if (WorkStatusType.RATING_FINAL_WORK_STATUS_TYPES.contains(work.getWorkStatusType().getCode())) {
			rating.setPending(false);
		}

		ratingDAO.saveOrUpdate(rating);

		if (WorkStatusType.RATING_FINAL_WORK_STATUS_TYPES.contains(work.getWorkStatusType().getCode())) {
			summaryService.saveUserRatingHistorySummary(rating);
			userNotificationService.onRatingCreated(rating);
			scorecardCache.evictAllResourceScoreCardsForUser(ratedUserId);

			Map<String, Object> params = new HashMap<>();
			params.put(ProfileModificationType.RATING, rating.getValue());
			userGroupValidationService.revalidateAllAssociationsByUserAsync(ratedUserId, params);
		}

		summaryService.saveUserRatingChangeLogSummary(rating, work);
		return rating;
	}

	@Override
	public Rating flagRatingForReview(Long ratingId, boolean flagForReview) {
		Assert.notNull(ratingId);

		Rating rating = ratingDAO.get(ratingId);
		Assert.notNull(rating, "Unable to find rating.");

		rating.setFlaggedForReview(flagForReview);

		if (flagForReview && rating.getWork() != null) {
			notificationDispatcher.dispatchEmail(emailTemplateFactory.buildRatingFlaggedClientServicesEmailTemplate(rating.getCreatorId(), rating));
		}

		return rating;
	}

	@Override
	public Rating deleteRating(Long ratingId) {
		Assert.notNull(ratingId);

		Rating rating = ratingDAO.get(ratingId);
		Assert.notNull(rating, "Unable to find rating.");

		rating.setDeleted(true);
		userRatingHistorySummaryDAO.deleteRatingHistorySummaryByRatingId(ratingId);
		scorecardCache.evictAllResourceScoreCardsForUser(rating.getRatedUser().getId());

		return rating;
	}

	@Override
	public Double findSatisfactionRateForUser(Long userId) {
		Assert.notNull(userId);
		return ratingDAO.findSatisfactionRateForUser(userId);
	}

	@Override public Double findAverageStarRatingInLastNMonths(Long userId, Integer nMonths) {
		Assert.notNull(userId);
		Assert.notNull(nMonths);
		Assert.isTrue(nMonths > 0);
		return ratingDAO.findSatisfactionRateOverallSinceDate(userId, DateUtilities.getMidnightNMonthsAgo(nMonths));
	}

	@Override
	public List<Rating> findByUserCompanyForWork(Long companyId, Long workId, RatingPagination ratingPagination) {
		Assert.notNull(companyId);
		Assert.notNull(workId);
		Assert.notNull(ratingPagination);
		return ratingDAO.findByUserCompanyForWork(companyId, workId, ratingPagination).getResults();
	}

	@Override
	public RatingReportPagination findRatingsByCompany(Long companyId, RatingReportPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		return ratingDAO.buildRatingReportForCompany(companyId, pagination);
	}

	@Override
	public WorkPagination findAllWorkPendingRatingByResource(final Long userId, WorkPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		return workDAO.findAllWorkPendingRatingByResource(userId, pagination);
	}

	@Override
	public List<Rating> findRatingsForUserForWork(Long userId, Long workId) {
		return findRatingsForUserForWork(userId, workId, new RatingPagination(true)).getResults();
	}

	private RatingPagination findRatingsForUserForWork(Long userId, Long workId, RatingPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(workId);
		Assert.notNull(pagination);
		return ratingDAO.findForUserForWork(userId, workId, pagination);
	}

	@Override
	public RatingPagination findRatingsFlaggedForUser(Long userId, RatingPagination pagination) {
		return ratingDAO.findFlaggedForUser(userId, pagination);
	}

	@Override
	public Integer countAllUserRatings(Long userId) {
		return ratingDAO.countAllUserRatings(userId);
	}

	@Override
	public RatingSummary findRatingSummaryForUser(Long userId) {
		return ratingDAO.findRatingSummaryForUser(userId);
	}

	@Override
	public RatingSummary findRatingSummaryForVendor(Long vendorId) {
		List <User> users = profileService.findAllUsersByCompanyId(vendorId, Lists.newArrayList(UserStatusType.APPROVED));
		return ratingDAO.findRatingSummaryForVendor(getListOfUserIds(users));
	}

	@Override
	public RatingSummary findRatingSummaryForVendorSinceDate(Long vendorId, Calendar fromDate) {
		Assert.notNull(vendorId);
		Assert.notNull(fromDate);
		List <User> users = profileService.findAllUsersByCompanyId(vendorId, Lists.newArrayList(UserStatusType.APPROVED));
		return ratingDAO.findRatingSummaryForVendorSinceDate(getListOfUserIds(users), fromDate);
	}

	@Override
	public RatingSummary findRatingSummaryForUserSinceDate(Long userId, Calendar fromDate) {
		Assert.notNull(userId);
		Assert.notNull(fromDate);
		return ratingDAO.findRatingSummaryForUserSinceDate(userId, fromDate);
	}

	@Override
	public RatingSummary findRatingSummaryForVendorByCompany(Long vendorId, Long companyId) {
		List <User> users = profileService.findAllUsersByCompanyId(vendorId, Lists.newArrayList(UserStatusType.APPROVED));
		return ratingDAO.findRatingSummaryForVendorByCompany(getListOfUserIds(users), companyId);
	}

	@Override
	public RatingSummary findRatingSummaryForUserByCompany(Long userId, Long companyId) {
		return ratingDAO.findRatingSummaryForUserByCompany(userId, companyId);
	}

	@Override
	public RatingSummary findRatingSummaryForVendorByCompanySinceDate(Long vendorId, Long companyId, Calendar fromDate) {
		List <User> users = profileService.findAllUsersByCompanyId(vendorId, Lists.newArrayList(UserStatusType.APPROVED));
		return ratingDAO.findRatingSummaryForVendorByCompanySinceDate(getListOfUserIds(users), companyId, fromDate);
	}

	@Override
	public RatingSummary findRatingSummaryForUserByCompanySinceDate(Long userId, Long companyId, Calendar fromDate) {
		return ratingDAO.findRatingSummaryForUserByCompanySinceDate(userId, companyId, fromDate);
	}

	@Override
	public boolean hasWorkPendingRatingByResource(Long userId) {
		Assert.notNull(userId);
		return workDAO.hasWorkPendingRatingByResource(userId);
	}

	@Override
	public Rating findLatestRatingForUserForWork(Long ratedUserId, Long workId) {
		Assert.notNull(workId);
		Assert.notNull(ratedUserId);
		return ratingDAO.findLatestForUserForWork(ratedUserId, workId);
	}

	@Override
	public void updateLatestRatingForUserForWork(Long workId, Long ratedUserId, RatingDTO ratingDTO) {
		Assert.notNull(ratingDTO);
		Assert.notNull(workId);

		Work work = workDAO.findWorkById(workId);

		if (work == null) {
			logger.debug("Update rating fails because there is no work related.");
			return;
		}

		Rating rating = findLatestRatingForUserForWork(ratedUserId, workId);
		if (rating != null) {
			BeanUtils.copyProperties(ratingDTO, rating);
			rating.setRatingUser(authenticationService.getCurrentUser());
			ratingDAO.saveOrUpdate(rating);
		} else {
			rating = createRatingForWork(authenticationService.getCurrentUser().getId(), ratedUserId, workId, ratingDTO);
		}

		summaryService.saveUserRatingChangeLogSummary(rating, work);
	}

	private void markRatingsNonPending(Long workId, Long workerId, Long buyerId) {
		Assert.notNull(workId);
		Assert.notNull(workerId);
		Assert.notNull(buyerId);

		Rating ratingForResource = findLatestRatingForUserForWork(workerId, workId);

		if (ratingForResource != null) {
			summaryService.saveUserRatingHistorySummary(ratingForResource);
			scorecardCache.evictAllResourceScoreCardsForUser(workerId);
		}

		Rating ratingForBuyer = findLatestRatingForUserForWork(buyerId, workId);
		if (ratingForBuyer != null) {
			summaryService.saveUserRatingHistorySummary(ratingForBuyer);
		}

		ratingDAO.markRatingsNonPendingByWorkId(workId);
	}

	@Override
	public void markRatingsNonPending(WorkResource workResource) {
		Assert.notNull(workResource);
		Assert.notNull(workResource.getUser());
		Assert.notNull(workResource.getWork());

		final Long workerId = workResource.getUser().getId();
		final Long workId = workResource.getWork().getId();
		final Long buyerId = workResource.getWork().getBuyer().getId();

		markRatingsNonPending(workId, workerId, buyerId);
	}

	@Override
	public void markRatingsNonPending(Long workId) {
		Assert.notNull(workId);

		Work work = workDAO.findWorkById(workId);
		if (work == null) {
			logger.debug("Update rating fails because there is no work related.");
			return;
		}

		final Long workerId = workService.findActiveWorkerId(work.getId());

		markRatingsNonPending(workId, workerId, work.getBuyer().getId());
	}

	@Override
	public RatingWorkData findLatestRatingDataForResourceByWorkNumber(Long userId, String workNumber) {
		Assert.notNull(workNumber);
		Assert.notNull(userId);
		return ratingDAO.findLatestRatingDataForResourceByWorkNumber(userId, workNumber);
	}

	@Override
	@Cacheable(
		value = AVERAGE_USER_RATING_FOR_COMPANY,
		key = "#root.target.AVERAGE_USER_RATING_FOR_COMPANY + #userId + ':' + #companyId"
	)
	public AverageRating findAverageRatingForUserByCompany(final Long userId, final Long companyId) {
		Assert.notNull(userId);
		Assert.notNull(companyId);

		return userRatingHistorySummaryDAO.findAverageRatingForUserByCompany(userId, companyId);
	}

	@Override
	@CachePut(
		value = AVERAGE_USER_RATING_FOR_COMPANY,
		key = "#root.target.AVERAGE_USER_RATING_FOR_COMPANY + #userId + ':' + #companyId"
	)
	public AverageRating refreshAverageRatingForUserByCompany(final Long userId, final Long companyId) {
		Assert.notNull(userId);
		Assert.notNull(companyId);

		return userRatingHistorySummaryDAO.findAverageRatingForUserByCompany(userId, companyId);
	}

	private List<Long> getListOfUserIds(List<User> users) {
		List<Long> ids = new ArrayList<>();
		for (User user : users) {
			ids.add(user.getId());
		}
		return ids;
	}
}
