package com.workmarket.service.analytics;

import com.google.api.client.util.Sets;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.session.ImpressionDAO;
import com.workmarket.dao.summary.user.BlockedUserHistorySummaryDAO;
import com.workmarket.dao.summary.user.UserRatingHistorySummaryDAO;
import com.workmarket.dao.summary.work.WorkHistorySummaryDAO;
import com.workmarket.dao.summary.work.WorkStatusTransitionHistorySummaryDAO;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.analytics.BuyerScoreCard;
import com.workmarket.domains.model.analytics.BuyerScoreField;
import com.workmarket.domains.model.analytics.CompanyStatsCard;
import com.workmarket.domains.model.analytics.CompanyStatsField;
import com.workmarket.domains.model.analytics.ResourceScoreCard;
import com.workmarket.domains.model.analytics.ResourceScoreField;
import com.workmarket.domains.model.analytics.ScoreCard;
import com.workmarket.domains.model.analytics.VendorScoreCard;
import com.workmarket.domains.model.rating.RatingSummary;
import com.workmarket.domains.model.session.Impression;
import com.workmarket.domains.model.session.ImpressionType;
import com.workmarket.domains.model.summary.company.CompanySummary;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.dao.WorkResourceLabelDAO;
import com.workmarket.domains.work.model.WorkResourceAggregateFilter;
import com.workmarket.domains.work.model.WorkResourceLabelType;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.dto.CampaignStatisticsDTO;
import com.workmarket.service.analytics.cache.ScorecardCache;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.dto.ImpressionDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

	@Autowired private SummaryService summaryService;
	@Autowired private RatingService ratingService;
	@Autowired private WorkResourceService resourceService;
	@Autowired private BillingService billingService;
	@Autowired private ScorecardCache scorecardCache;
	@Autowired private BlockedUserHistorySummaryDAO blockedUserHistorySummaryDAO;
	@Autowired private WorkHistorySummaryDAO workHistorySummaryDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private ImpressionDAO impressionDAO;
	@Autowired private WorkResourceLabelDAO workResourceLabelDAO;
	@Autowired private WorkStatusTransitionHistorySummaryDAO workStatusTransitionHistorySummaryDAO;
	@Autowired private UserRatingHistorySummaryDAO userRatingHistorySummaryDAO;
	@Autowired private WorkService workService;
	@Autowired private CompanyService companyService;
	@Autowired private ProfileService profileService;
	@Autowired private AuthenticationService authenticationService;

	private static final int HIGH_RATING_VALUE = 2;
	private static final int PAST_DUE_DAYS = 3;

	@Override
	public Impression saveOrUpdateImpression(ImpressionDTO impressionDTO) {
		Assert.notNull(impressionDTO);

		Impression impression;
		if (impressionDTO.getImpressionId() == null) {
			impression = BeanUtilities.newBean(Impression.class, impressionDTO);
			impression.setImpressionType(ImpressionType.values()[impressionDTO.getImpressionTypeId().intValue()]);
		} else {
			impression = impressionDAO.get(impressionDTO.getImpressionId());
			BeanUtilities.copyProperties(impression, impressionDTO);
			impression.setImpressionType(ImpressionType.values()[impressionDTO.getImpressionTypeId().intValue()]);
		}

		impressionDAO.saveOrUpdate(impression);

		return impression;
	}

	@Override
	public CampaignStatisticsDTO newCampaignStatistics(Long impressionTypeId, Long campaignId) {
		Assert.notNull(impressionTypeId);
		Assert.notNull(campaignId);
		return impressionDAO.newCampaignStatistics(impressionTypeId, campaignId);
	}

	@Override
	public Map<Long, Integer> countDistinctBlockingCompaniesByUser(Calendar fromDate) {
		Assert.notNull(fromDate);
		List<Long> usersIds = Lists.newArrayList();
		return countDistinctBlockingCompaniesByUser(fromDate, usersIds);
	}

	@Override
	public Map<Long, Integer> countDistinctBlockingCompaniesByUser(Calendar fromDate, List<Long> userIds) {
		Assert.notNull(fromDate);
		return blockedUserHistorySummaryDAO.countDistinctBlockingCompaniesByUser(fromDate, userIds);
	}

	@Override
	public Map<Long, Integer> countRepeatedClientsByUser(Calendar fromDate, List<Long> userIds) {
		Assert.notNull(fromDate);
		return workHistorySummaryDAO.countRepeatedClientsByUser(fromDate, userIds);
	}

	@Override
	public ScoreCard getResourceScoreCard(long userId) {

		Optional<ResourceScoreCard> cachedScoreCard = scorecardCache.getResourceScorecard(userId);
		if (cachedScoreCard.isPresent()) {
			return cachedScoreCard.get();
		}

		ResourceScoreCard scoreCard = buildResourceScoreCardByUser(userId);

		scorecardCache.put(userId, scoreCard);

		return scoreCard;
	}

	@Override
	public ScoreCard getVendorScoreCard(long vendorId) {

		Optional<VendorScoreCard> cachedScoreCard = scorecardCache.getVendorScorecard(vendorId);
		if (cachedScoreCard.isPresent()) {
			return cachedScoreCard.get();
		}

		VendorScoreCard scoreCard = buildVendorScoreCard(vendorId);

		scorecardCache.put(vendorId, scoreCard);

		return scoreCard;
	}

	@Override
	public ScoreCard getResourceScoreCardForCompany(long companyId, long userId) {

		Optional<ResourceScoreCard> cachedScoreCard = scorecardCache.getResourceScorecard(companyId, userId);
		if (cachedScoreCard.isPresent()) {
			return cachedScoreCard.get();
		}

		ResourceScoreCard scoreCard = buildResourceScorecardForCompany(companyId, userId);

		scorecardCache.put(companyId, userId, scoreCard);

		return scoreCard;
	}

	@Override
	public ScoreCard getVendorScoreCardForCompany(long companyId, long vendorId) {

		Optional<VendorScoreCard> cachedScoreCard = scorecardCache.getVendorScorecard(companyId, vendorId);
		if (cachedScoreCard.isPresent()) {
			return cachedScoreCard.get();
		}

		VendorScoreCard scoreCard = buildVendorScorecardForCompany(companyId, vendorId);

		scorecardCache.put(companyId, vendorId, scoreCard);

		return scoreCard;
	}

	@Override
	public ScoreCard getBuyerScoreCardByUserId(Long userId) {
		User user = userDAO.get(userId);
		Assert.notNull(user);

		return getBuyerScoreCardByCompanyId(user.getCompany().getId());
	}

	@Override
	public ScoreCard getBuyerScoreCardByCompanyId(Long companyId) {
		Calendar now = Calendar.getInstance();
		DateRange dateRangeLast90Days = new DateRange(DateUtilities.getMidnightNMonthsAgo(3), now);
		BuyerScoreCard scoreCard = new BuyerScoreCard();

		Optional<BuyerScoreCard> cachedBuyerScoreCard = scorecardCache.getBuyerScorecard(companyId);
		if (cachedBuyerScoreCard.isPresent()) {
			return cachedBuyerScoreCard.get();
		}

		CompanySummary companySummaryAllTime = summaryService.findCompanySummary(companyId);
		ScoreCard.DateIntervalData dateIntervalDataPaidWork = new ScoreCard.DateIntervalData()
			.setNet90(workHistorySummaryDAO.countWork(companyId, WorkStatusType.PAID, dateRangeLast90Days));

		if (companySummaryAllTime != null) {
			dateIntervalDataPaidWork.setAll(companySummaryAllTime.getTotalPaidAssignments());
		}

		scoreCard.addToValues(BuyerScoreField.PAID_WORK, dateIntervalDataPaidWork);

		scoreCard.addToValues(BuyerScoreField.AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS, new ScoreCard.DateIntervalData()
			.setNet90(calculateAverageTimeToApproveWorkInDaysByCompany(companyId, dateRangeLast90Days)));

		scoreCard.addToValues(BuyerScoreField.AVERAGE_TIME_TO_PAY_WORK_IN_DAYS, new ScoreCard.DateIntervalData()
			.setNet90(calculateAverageTimeToPayWorkInDaysByCompany(companyId, dateRangeLast90Days)));

		scoreCard.addToValues(BuyerScoreField.PERCENTAGE_RATINGS_OVER_4_STARS, new ScoreCard.DateIntervalData()
			.setNet90(userRatingHistorySummaryDAO.calculatePercentageRatingsByCompany(companyId, HIGH_RATING_VALUE, dateRangeLast90Days, true).doubleValue()));

		if(scoreCard.hasBadScore() || companyService.hasWorkPastDueMoreThanXDays(companyId, PAST_DUE_DAYS)) {
			// Fill in company summary
			CompanyStatsCard companyStatsCard = new CompanyStatsCard();
			companyStatsCard.addToValues(CompanyStatsField.AVAILABLE_WORK_PERCENTAGE, calculateAvailableWorkPercentage(companyId));
			companyStatsCard.addToValues(CompanyStatsField.PENDING_APPROVAL_WORK_PERCENTAGE, calculatePendingApprovalWorkPercentage(companyId));
			companyStatsCard.addToValues(CompanyStatsField.PAST_DUE_WORK_PERCENTAGE, calculatePastDueWorkPercentage(companyId));
			companyStatsCard.addToValues(CompanyStatsField.PAST_DUE_MORE_THAN_3_DAYS, companyService.hasWorkPastDueMoreThanXDays(companyId, PAST_DUE_DAYS));

			scoreCard.setCompanyStatsCard(companyStatsCard);
		}

		scorecardCache.put(companyId, scoreCard);

		return scoreCard;
	}

	@Override
	public Map<Long, ResourceScoreCard> getResourceScoreCards(List<Long> userIds) {
		Validate.noNullElements(userIds);

		Map<Long, ResourceScoreCard> scoreCards = scorecardCache.getResourceScorecards(userIds);

		for (Long userId : userIds) {
			if (scoreCards.containsKey(userId)) {
				continue;
			}

			ResourceScoreCard scoreCard = buildResourceScoreCardByUser(userId);
			scoreCards.put(userId, scoreCard);
			scorecardCache.put(userId, scoreCard);
		}
		return scoreCards;
	}

	@Override
	public Map<Long, ResourceScoreCard> getResourceScoreCardsForCompany(Long companyId, List<Long> userIds) {
		Validate.noNullElements(userIds);

		Map<Long, ResourceScoreCard> scoreCards = scorecardCache.getResourceScorecards(companyId, userIds);

		for (Long userId : userIds) {
			if (scoreCards.containsKey(userId)) {
				continue;
			}

			ResourceScoreCard scoreCard = buildResourceScorecardForCompany(companyId, userId);
			scoreCards.put(userId, scoreCard);
			scorecardCache.put(companyId, userId, scoreCard);
		}
		return scoreCards;
	}

	private ResourceScoreCard buildResourceScoreCardByUser(long userId) {

		RatingSummary ratingSummary = ratingService.findRatingSummaryForUser(userId);
		RatingSummary ratingSummaryNet90 = ratingService.findRatingSummaryForUserSinceDate(userId, DateUtilities.getMidnightNMonthsAgo(3));

		ResourceScoreCard scoreCard = (ResourceScoreCard) new ResourceScoreCard()
				.setRating(ratingSummary);

		WorkResourceAggregateFilter labelFilters = new WorkResourceAggregateFilter();

		Map<String, Integer> allLabels3MonthsAgo = workResourceLabelDAO
				.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), Lists.newArrayList(userId));
		Map<String, Integer> allLabels1MonthAgo = workResourceLabelDAO
				.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(DateUtilities.getMidnightMonthAgo()), Lists.newArrayList(userId));
		Map<String, Integer> allLabels = workResourceLabelDAO
				.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(null), Lists.newArrayList(userId));

		scoreCard.addToValues(ResourceScoreField.COMPLETED_WORK, new ScoreCard.DateIntervalData()
				.setNet90(resourceService.countAssignmentsByResourceUserIdAndStatus(Lists.newArrayList(userId), new WorkResourceAggregateFilter()
						.setFromDate(DateUtilities.getMidnightNMonthsAgo(3))
						.setWorkStatusTypeCode(WorkStatusType.CLOSED)
				))
				.setAll(resourceService.countAllAssignmentsByResourceUserIdAndStatus(Lists.newArrayList(userId), new WorkResourceAggregateFilter()
						.setWorkStatusTypeCode(WorkStatusType.CLOSED)
				)));

		scoreCard.addToValues(ResourceScoreField.ON_TIME_PERCENTAGE, new ScoreCard.DateIntervalData()
				.setNet90(resourceService.calculateOnTimePercentageForUser(Lists.newArrayList(userId), new WorkResourceAggregateFilter()
						.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), allLabels3MonthsAgo
				))
				.setAll(resourceService.calculateOnTimePercentageForUser(Lists.newArrayList(userId), new WorkResourceAggregateFilter()
						.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH)), allLabels
				)));

		scoreCard.addToValues(ResourceScoreField.DELIVERABLE_ON_TIME_PERCENTAGE, new ScoreCard.DateIntervalData()
			.setNet90(resourceService.calculateDeliverableOnTimePercentageForUser(Lists.newArrayList(userId), new WorkResourceAggregateFilter()
				.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), allLabels3MonthsAgo
			))
			.setAll(resourceService.calculateDeliverableOnTimePercentageForUser(Lists.newArrayList(userId), new WorkResourceAggregateFilter()
				.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH)), allLabels
			)));

		fillResourceScoreCardLabelCounts(scoreCard, allLabels3MonthsAgo, allLabels1MonthAgo, allLabels);

		scoreCard.getValues().get(ResourceScoreField.CANCELLED_WORK_IN_LESS_THAN_24_HOURS)
				.setNet90(resourceService.countConfirmedWorkResourceLabelByUserId(userId, new WorkResourceAggregateFilter()
						.setFromDate(DateUtilities.getMidnightNMonthsAgo(3))
						.setResourceLabelTypeCode(WorkResourceLabelType.CANCELLED)
						.setLessThan24Hours(true)
				))
				.setAll(resourceService.countConfirmedWorkResourceLabelByUserId(userId, new WorkResourceAggregateFilter()
						.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH))
						.setResourceLabelTypeCode(WorkResourceLabelType.CANCELLED)
						.setLessThan24Hours(true)
				));

		scoreCard.addToValues(ResourceScoreField.SATISFACTION_OVER_ALL, new ScoreCard.DateIntervalData()
				.setNet90(ratingSummaryNet90.getSatisfactionRate())
				.setAll(ratingSummary.getSatisfactionRate())
		);

		scoreCard.addToValues(ResourceScoreField.QUALITY, new ScoreCard.DateIntervalData()
				.setNet90(ratingSummaryNet90.getQuality())
				.setAll(ratingSummary.getQuality())
		);

		scoreCard.addToValues(ResourceScoreField.PROFESSIONALISM, new ScoreCard.DateIntervalData()
				.setNet90(ratingSummaryNet90.getProfessionalism())
				.setAll(ratingSummary.getProfessionalism())
		);

		scoreCard.addToValues(ResourceScoreField.COMMUNICATION, new ScoreCard.DateIntervalData()
				.setNet90(ratingSummaryNet90.getCommunication())
				.setAll(ratingSummary.getCommunication())
		);

		return scoreCard;
	}

	private ResourceScoreCard buildResourceScorecardForCompany(long companyId, long userId) {

		RatingSummary ratingSummary = ratingService.findRatingSummaryForUserByCompany(userId, companyId);
		RatingSummary ratingSummaryNet90 = ratingService.findRatingSummaryForUserByCompanySinceDate(userId, companyId, DateUtilities.getMidnightNMonthsAgo(3));

		ResourceScoreCard scoreCard = (ResourceScoreCard) new ResourceScoreCard()
				.setRating(ratingService.findRatingSummaryForUserByCompany(userId, companyId));

		WorkResourceAggregateFilter labelFilters = new WorkResourceAggregateFilter().setCompanyId(companyId);

		Map<String, Integer> allLabels3MonthsAgo = workResourceLabelDAO.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), Lists.newArrayList(userId));
		Map<String, Integer> allLabels1MonthAgo = workResourceLabelDAO.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(DateUtilities.getMidnightMonthAgo()), Lists.newArrayList(userId));
		Map<String, Integer> allLabels = workResourceLabelDAO.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(null), Lists.newArrayList(userId));

		WorkResourceAggregateFilter completedWorkFilter = new WorkResourceAggregateFilter()
				.setFromDate(DateUtilities.getMidnightMonthAgo())
				.setWorkStatusTypeCode(WorkStatusType.CLOSED)
				.setCompanyId(companyId);

		scoreCard.addToValues(ResourceScoreField.COMPLETED_WORK, new ScoreCard.DateIntervalData()
				.setNet90(resourceService.countAssignmentsByResourceUserIdAndStatus(Lists.newArrayList(userId), completedWorkFilter
						.setFromDate(DateUtilities.getMidnightNMonthsAgo(3))
				))
				.setAll(resourceService.countAllAssignmentsByResourceUserIdAndStatus(Lists.newArrayList(userId), new WorkResourceAggregateFilter()
						.setWorkStatusTypeCode(WorkStatusType.CLOSED)
						.setCompanyId(companyId)
				)));

		WorkResourceAggregateFilter onTimePercentageFilter = new WorkResourceAggregateFilter()
				.setFromDate(DateUtilities.getMidnightMonthAgo())
				.setCompanyId(companyId);

		scoreCard.addToValues(ResourceScoreField.ON_TIME_PERCENTAGE, new ScoreCard.DateIntervalData()
				.setNet90(resourceService.calculateOnTimePercentageForUser(Lists.newArrayList(userId), onTimePercentageFilter
						.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), allLabels3MonthsAgo
				))
				.setAll(resourceService.calculateOnTimePercentageForUser(Lists.newArrayList(userId), onTimePercentageFilter
						.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH)), allLabels
				)));

		WorkResourceAggregateFilter deliverableOnTimePercentageFilter = new WorkResourceAggregateFilter()
			.setFromDate(DateUtilities.getMidnightMonthAgo())
			.setCompanyId(companyId);

		scoreCard.addToValues(ResourceScoreField.DELIVERABLE_ON_TIME_PERCENTAGE, new ScoreCard.DateIntervalData()
			.setNet90(resourceService.calculateDeliverableOnTimePercentageForUser(Lists.newArrayList(userId), deliverableOnTimePercentageFilter
				.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), allLabels3MonthsAgo
			))
			.setAll(resourceService.calculateDeliverableOnTimePercentageForUser(Lists.newArrayList(userId), deliverableOnTimePercentageFilter
					.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH)), allLabels
			)));

		fillResourceScoreCardLabelCounts(scoreCard, allLabels3MonthsAgo, allLabels1MonthAgo, allLabels);

		WorkResourceAggregateFilter cancelledLess24hoursFilter = new WorkResourceAggregateFilter()
				.setFromDate(DateUtilities.getMidnightMonthAgo())
				.setResourceLabelTypeCode(WorkResourceLabelType.CANCELLED)
				.setLessThan24Hours(true)
				.setCompanyId(companyId);

		scoreCard.getValues().get(ResourceScoreField.CANCELLED_WORK_IN_LESS_THAN_24_HOURS)
				.setNet90(resourceService.countConfirmedWorkResourceLabelByUserId(userId, cancelledLess24hoursFilter
						.setFromDate(DateUtilities.getMidnightNMonthsAgo(3))
				))
				.setAll(resourceService.countConfirmedWorkResourceLabelByUserId(userId, cancelledLess24hoursFilter
						.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH))
				));

		scoreCard.addToValues(ResourceScoreField.SATISFACTION_OVER_ALL, new ScoreCard.DateIntervalData()
				.setNet90(ratingSummaryNet90.getSatisfactionRate())
				.setAll(ratingSummary.getSatisfactionRate())
		);

		scoreCard.addToValues(ResourceScoreField.QUALITY, new ScoreCard.DateIntervalData()
				.setNet90(ratingSummaryNet90.getQuality())
				.setAll(ratingSummary.getQuality())
		);

		scoreCard.addToValues(ResourceScoreField.PROFESSIONALISM, new ScoreCard.DateIntervalData()
				.setNet90(ratingSummaryNet90.getProfessionalism())
				.setAll(ratingSummary.getProfessionalism())
		);

		scoreCard.addToValues(ResourceScoreField.COMMUNICATION, new ScoreCard.DateIntervalData()
				.setNet90(ratingSummaryNet90.getCommunication())
				.setAll(ratingSummary.getCommunication())
		);

		return scoreCard;
	}

	@Override
	public Map<Long, VendorScoreCard> getVendorScoreCards(List<Long> vendorIds) {
		Validate.noNullElements(vendorIds);

		Map<Long, VendorScoreCard> scoreCards = scorecardCache.getVendorScorecards(vendorIds);

		for (Long vendorId : vendorIds) {
			if (scoreCards.containsKey(vendorId)) {
				continue;
			}

			VendorScoreCard scoreCard = buildVendorScoreCard(vendorId);
			scoreCards.put(vendorId, scoreCard);
			scorecardCache.put(vendorId, scoreCard);
		}
		return scoreCards;
	}

	@Override
	public Map<Long, VendorScoreCard> getVendorScoreCardsForCompany(Long companyId, List<Long> vendorIds) {
		Validate.noNullElements(vendorIds);

		Map<Long, VendorScoreCard> scoreCards = scorecardCache.getVendorScorecards(companyId, vendorIds);

		for (Long vendorId : vendorIds) {
			if (scoreCards.containsKey(vendorId)) {
				continue;
			}

			VendorScoreCard scoreCard = buildVendorScorecardForCompany(companyId, vendorId);
			scoreCards.put(vendorId, scoreCard);
			scorecardCache.put(companyId, vendorId, scoreCard);
		}
		return scoreCards;
	}

	private VendorScoreCard buildVendorScoreCard(long vendorId) {

		RatingSummary ratingSummary = ratingService.findRatingSummaryForVendor(vendorId);
		RatingSummary ratingSummaryNet90 = ratingService.findRatingSummaryForVendorSinceDate(vendorId, DateUtilities.getMidnightNMonthsAgo(3));

		VendorScoreCard scoreCard = (VendorScoreCard) new VendorScoreCard()
			.setRating(ratingSummary);

		scoreCard.addToValues(ResourceScoreField.SATISFACTION_OVER_ALL, new ScoreCard.DateIntervalData()
			.setNet90(ratingSummaryNet90.getSatisfactionRate())
			.setAll(ratingSummary.getSatisfactionRate())
		);

		scoreCard.addToValues(ResourceScoreField.QUALITY, new ScoreCard.DateIntervalData()
			.setNet90(ratingSummaryNet90.getQuality())
			.setAll(ratingSummary.getQuality())
		);

		scoreCard.addToValues(ResourceScoreField.PROFESSIONALISM, new ScoreCard.DateIntervalData()
			.setNet90(ratingSummaryNet90.getProfessionalism())
			.setAll(ratingSummary.getProfessionalism())
		);

		scoreCard.addToValues(ResourceScoreField.COMMUNICATION, new ScoreCard.DateIntervalData()
			.setNet90(ratingSummaryNet90.getCommunication())
			.setAll(ratingSummary.getCommunication())
		);

		Set<User> users = Sets.newHashSet();
		users.addAll(authenticationService.findAllUsersByACLRoleAndCompany(vendorId, AclRole.ACL_SHARED_WORKER));
		users.addAll(authenticationService.findAllUsersByACLRoleAndCompany(vendorId, AclRole.ACL_WORKER));
		List<Long> userIds = extract(users, on(User.class).getId());

		if(CollectionUtils.isEmpty(userIds)) {
			return scoreCard;
		}

		WorkResourceAggregateFilter labelFilters = new WorkResourceAggregateFilter();

		Map<String, Integer> allLabels3MonthsAgo = workResourceLabelDAO
				.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), userIds);
		Map<String, Integer> allLabels1MonthAgo = workResourceLabelDAO
				.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(DateUtilities.getMidnightMonthAgo()), userIds);
		Map<String, Integer> allLabels = workResourceLabelDAO
				.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(null), userIds);

		scoreCard.addToValues(ResourceScoreField.COMPLETED_WORK, new ScoreCard.DateIntervalData()
				.setNet90(resourceService.countAssignmentsByResourceUserIdAndStatus(userIds, new WorkResourceAggregateFilter()
								.setFromDate(DateUtilities.getMidnightNMonthsAgo(3))
								.setWorkStatusTypeCode(WorkStatusType.CLOSED)
				))
				.setAll(resourceService.countAllAssignmentsByResourceUserIdAndStatus(userIds, new WorkResourceAggregateFilter()
								.setWorkStatusTypeCode(WorkStatusType.CLOSED)
				)));

		scoreCard.addToValues(ResourceScoreField.ON_TIME_PERCENTAGE, new ScoreCard.DateIntervalData()
				.setNet90(resourceService.calculateOnTimePercentageForUser(userIds, new WorkResourceAggregateFilter()
								.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), allLabels3MonthsAgo
				))
				.setAll(resourceService.calculateOnTimePercentageForUser(userIds, new WorkResourceAggregateFilter()
						.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH)), allLabels
				)));

		scoreCard.addToValues(ResourceScoreField.DELIVERABLE_ON_TIME_PERCENTAGE, new ScoreCard.DateIntervalData()
				.setNet90(resourceService.calculateDeliverableOnTimePercentageForUser(userIds, new WorkResourceAggregateFilter()
								.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), allLabels3MonthsAgo
				))
				.setAll(resourceService.calculateDeliverableOnTimePercentageForUser(userIds, new WorkResourceAggregateFilter()
								.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH)), allLabels
				)));

		fillResourceScoreCardLabelCounts(scoreCard, allLabels3MonthsAgo, allLabels1MonthAgo, allLabels);

		scoreCard.getValues().get(ResourceScoreField.CANCELLED_WORK_IN_LESS_THAN_24_HOURS)
				.setNet90(resourceService.countConfirmedWorkResourceLabelByUserId(userIds, new WorkResourceAggregateFilter()
								.setFromDate(DateUtilities.getMidnightNMonthsAgo(3))
								.setResourceLabelTypeCode(WorkResourceLabelType.CANCELLED)
								.setLessThan24Hours(true)
				))
				.setAll(resourceService.countConfirmedWorkResourceLabelByUserId(userIds, new WorkResourceAggregateFilter()
								.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH))
								.setResourceLabelTypeCode(WorkResourceLabelType.CANCELLED)
								.setLessThan24Hours(true)
				));

		return scoreCard;
	}

	private VendorScoreCard buildVendorScorecardForCompany(long companyId, long vendorId) {

		RatingSummary ratingSummary = ratingService.findRatingSummaryForVendorByCompany(vendorId, companyId);
		RatingSummary ratingSummaryNet90 = ratingService.findRatingSummaryForVendorByCompanySinceDate(vendorId, companyId, DateUtilities.getMidnightNMonthsAgo(3));

		VendorScoreCard scoreCard = (VendorScoreCard) new VendorScoreCard()
				.setRating(ratingService.findRatingSummaryForVendorByCompany(vendorId, companyId));

		scoreCard.addToValues(ResourceScoreField.SATISFACTION_OVER_ALL, new ScoreCard.DateIntervalData()
			.setNet90(ratingSummaryNet90.getSatisfactionRate())
			.setAll(ratingSummary.getSatisfactionRate())
		);

		scoreCard.addToValues(ResourceScoreField.QUALITY, new ScoreCard.DateIntervalData()
			.setNet90(ratingSummaryNet90.getQuality())
			.setAll(ratingSummary.getQuality())
		);

		scoreCard.addToValues(ResourceScoreField.PROFESSIONALISM, new ScoreCard.DateIntervalData()
			.setNet90(ratingSummaryNet90.getProfessionalism())
			.setAll(ratingSummary.getProfessionalism())
		);

		scoreCard.addToValues(ResourceScoreField.COMMUNICATION, new ScoreCard.DateIntervalData()
			.setNet90(ratingSummaryNet90.getCommunication())
			.setAll(ratingSummary.getCommunication())
		);

		Set<User> users = Sets.newHashSet();
		users.addAll(authenticationService.findAllUsersByACLRoleAndCompany(vendorId, AclRole.ACL_SHARED_WORKER));
		users.addAll(authenticationService.findAllUsersByACLRoleAndCompany(vendorId, AclRole.ACL_WORKER));
		List<Long> userIds = extract(users, on(User.class).getId());

		if(CollectionUtils.isEmpty(userIds)) {
			return scoreCard;
		}

		WorkResourceAggregateFilter labelFilters = new WorkResourceAggregateFilter().setCompanyId(companyId);
		Map<String, Integer> allLabels3MonthsAgo = workResourceLabelDAO.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), userIds);
		Map<String, Integer> allLabels1MonthAgo = workResourceLabelDAO.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(DateUtilities.getMidnightMonthAgo()), userIds);
		Map<String, Integer> allLabels = workResourceLabelDAO.countAllConfirmedWorkResourceLabelsByUserId(labelFilters.setFromDate(null), userIds);

		WorkResourceAggregateFilter completedWorkFilter = new WorkResourceAggregateFilter()
				.setFromDate(DateUtilities.getMidnightMonthAgo())
				.setWorkStatusTypeCode(WorkStatusType.CLOSED)
				.setCompanyId(companyId);

		scoreCard.addToValues(ResourceScoreField.COMPLETED_WORK, new ScoreCard.DateIntervalData()
				.setNet90(resourceService.countAssignmentsByResourceUserIdAndStatus(userIds, completedWorkFilter
								.setFromDate(DateUtilities.getMidnightNMonthsAgo(3))
				))
				.setAll(resourceService.countAllAssignmentsByResourceUserIdAndStatus(userIds, new WorkResourceAggregateFilter()
								.setWorkStatusTypeCode(WorkStatusType.CLOSED)
								.setCompanyId(companyId)
				)));

		WorkResourceAggregateFilter onTimePercentageFilter = new WorkResourceAggregateFilter()
				.setFromDate(DateUtilities.getMidnightMonthAgo())
				.setCompanyId(companyId);

		scoreCard.addToValues(ResourceScoreField.ON_TIME_PERCENTAGE, new ScoreCard.DateIntervalData()
				.setNet90(resourceService.calculateOnTimePercentageForUser(userIds, onTimePercentageFilter
								.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), allLabels3MonthsAgo
				))
				.setAll(resourceService.calculateOnTimePercentageForUser(userIds, onTimePercentageFilter
								.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH)), allLabels
				)));

		WorkResourceAggregateFilter deliverableOnTimePercentageFilter = new WorkResourceAggregateFilter()
				.setFromDate(DateUtilities.getMidnightMonthAgo())
				.setCompanyId(companyId);

		scoreCard.addToValues(ResourceScoreField.DELIVERABLE_ON_TIME_PERCENTAGE, new ScoreCard.DateIntervalData()
				.setNet90(resourceService.calculateDeliverableOnTimePercentageForUser(userIds, deliverableOnTimePercentageFilter
								.setFromDate(DateUtilities.getMidnightNMonthsAgo(3)), allLabels3MonthsAgo
				))
				.setAll(resourceService.calculateDeliverableOnTimePercentageForUser(userIds, deliverableOnTimePercentageFilter
								.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH)), allLabels
				)));

		fillResourceScoreCardLabelCounts(scoreCard, allLabels3MonthsAgo, allLabels1MonthAgo, allLabels);

		WorkResourceAggregateFilter cancelledLess24hoursFilter = new WorkResourceAggregateFilter()
			.setFromDate(DateUtilities.getMidnightMonthAgo())
			.setResourceLabelTypeCode(WorkResourceLabelType.CANCELLED)
			.setLessThan24Hours(true)
			.setCompanyId(companyId);

		scoreCard.getValues().get(ResourceScoreField.CANCELLED_WORK_IN_LESS_THAN_24_HOURS)
			.setNet90(resourceService.countConfirmedWorkResourceLabelByUserId(userIds, cancelledLess24hoursFilter
				.setFromDate(DateUtilities.getMidnightNMonthsAgo(3))
			))
			.setAll(resourceService.countConfirmedWorkResourceLabelByUserId(userIds, cancelledLess24hoursFilter
				.setFromDate(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH))
			));

		return scoreCard;
	}


	private void fillResourceScoreCardLabelCounts(ScoreCard scoreCard, Map<String, Integer> allLabels3MonthsAgo, Map<String, Integer> allLabels1MonthAgo, Map<String, Integer> allLabels) {
		Assert.notNull(scoreCard);
		scoreCard.addToValues(ResourceScoreField.CANCELLED_WORK, new ScoreCard.DateIntervalData()
				.setNet90(MapUtils.getDoubleValue(allLabels3MonthsAgo, WorkResourceLabelType.CANCELLED, 0))
				.setAll(MapUtils.getDoubleValue(allLabels, WorkResourceLabelType.CANCELLED, 0)));

		scoreCard.addToValues(ResourceScoreField.CANCELLED_WORK_IN_LESS_THAN_24_HOURS, new ScoreCard.DateIntervalData());

		scoreCard.addToValues(ResourceScoreField.LATE_WORK, new ScoreCard.DateIntervalData()
				.setNet90(MapUtils.getDoubleValue(allLabels3MonthsAgo, WorkResourceLabelType.LATE, 0))
				.setAll(MapUtils.getDoubleValue(allLabels, WorkResourceLabelType.LATE, 0)));

		scoreCard.addToValues(ResourceScoreField.ABANDONED_WORK, new ScoreCard.DateIntervalData()
				.setNet90(MapUtils.getDoubleValue(allLabels3MonthsAgo, WorkResourceLabelType.ABANDONED, 0))
				.setAll(MapUtils.getDoubleValue(allLabels, WorkResourceLabelType.ABANDONED, 0)));
	}

	private Integer calculateAverageTimeToApproveWorkInDaysByCompany(long companyId, DateRange dateRange) {
		Assert.notNull(dateRange);
		BigDecimal averageInSeconds = workStatusTransitionHistorySummaryDAO.calculateAverageTransitionTimeByCompanyInSeconds(WorkStatusType.COMPLETE, WorkStatusType.CLOSED, companyId, dateRange);
		if (averageInSeconds != null) {
			return DateUtilities.secondsToDays(averageInSeconds.doubleValue());
		}
		return 0;
	}

	private Integer calculateAverageTimeToPayWorkInDaysByCompany(long companyId, DateRange dateRange) {
		Assert.notNull(dateRange);
		BigDecimal averageInSeconds = workStatusTransitionHistorySummaryDAO.calculateAverageTimeToPayFromDueDateByCompanyInSeconds(companyId, dateRange, (billingService.countAllDueWorkByCompany(companyId) > 0));
		if (averageInSeconds != null) {
			return DateUtilities.secondsToDays(averageInSeconds.doubleValue());
		}
		return 0;
	}

	private BigDecimal calculateAvailableWorkPercentage(long companyId) {
		List<String> statuses = Lists.newArrayList();
		statuses.add(WorkStatusType.SENT);
		BigDecimal availableWorkCount = BigDecimal.valueOf(workService.countWorkByCompanyByStatus(companyId, statuses));
		BigDecimal allWorkCount = BigDecimal.valueOf(workService.countWorkByCompanyByStatus(companyId, WorkStatusType.OPEN_WORK_STATUS_TYPES));
		return NumberUtilities.percentage(allWorkCount, availableWorkCount);
	}

	private BigDecimal calculatePendingApprovalWorkPercentage(long companyId) {
		List<String> statuses = Lists.newArrayList();
		statuses.add(WorkStatusType.COMPLETE);
		BigDecimal pendingApprovalWorkCount = BigDecimal.valueOf(workService.countWorkByCompanyByStatus(companyId, statuses));
		BigDecimal allWorkCount = BigDecimal.valueOf(workService.countWorkByCompanyByStatus(companyId, WorkStatusType.OPEN_WORK_STATUS_TYPES));
		return NumberUtilities.percentage(allWorkCount, pendingApprovalWorkCount);

	}

	private BigDecimal calculatePastDueWorkPercentage(long companyId) {
		BigDecimal dueWorkCount = BigDecimal.valueOf(workService.countAllDueWorkByCompany(companyId));
		BigDecimal allWorkCount = BigDecimal.valueOf(workService.countWorkByCompanyByStatus(companyId, WorkStatusType.OPEN_WORK_STATUS_TYPES));
		return NumberUtilities.percentage(allWorkCount, dueWorkCount);
	}
}
