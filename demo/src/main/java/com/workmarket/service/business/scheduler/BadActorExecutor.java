package com.workmarket.service.business.scheduler;

import com.google.common.annotations.VisibleForTesting;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.configuration.UserIndexerConfiguration;
import com.workmarket.domains.model.analytics.ResourceScoreField;
import com.workmarket.domains.model.analytics.ScoreCard;
import com.workmarket.domains.model.summary.user.UserSummary;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.FeatureToggleService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.DateUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

/**
 * Scheduled recommendations.
 */
@Service
@ManagedResource(
	objectName = "bean:name=badActorExecutor",
	description = "bad actors")
public class BadActorExecutor {
	private static final Logger logger = LoggerFactory.getLogger(BadActorExecutor.class);

	public static final int MAX_ABANDONED_ASSIGNMENTS = 1;
	public static final int MAX_CANCELLED_ASSIGNMENTS = 2;
	public static final int MIN_ON_TIME_PERCENTAGE = 90;
	public static final int MIN_SATISFACTION_RATING = 75;
	public static final int MIN_PAID_ASSIGNMENTS = 0;
	public static final int MAX_BAD_CONDITIONS = 2;

	@Autowired private AnalyticsService analyticsService;
	@Autowired private UserService userService;
	@Autowired private SummaryService summaryService;
	@Autowired private FeatureToggleService featureToggleService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;

	@ManagedOperation(description = "Block bad actors")
	public void blockBadActors() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		Calendar onTimeThresholdDate = UserIndexerConfiguration.getOnTimePercentageThresholdDate();
		Calendar deliverableOnTimeThresholdDate = UserIndexerConfiguration.getDeliverableOnTimePercentageThresholdDate();
		Calendar earliestThresholdDate = onTimeThresholdDate.before(deliverableOnTimeThresholdDate) ? onTimeThresholdDate : deliverableOnTimeThresholdDate;

		blockBadActors(earliestThresholdDate, DateUtilities.getMidnightNextDay(DateUtilities.getCalendarNow()));
	}

	@VisibleForTesting
	private void blockBadActors(Calendar workersActiveFrom, Calendar workersActiveTo) {
		List<Long> blockingCompanyIds = featureToggleService.getCompaniesWithFeature("bad-actor");
		logger.debug(String.format("Blocking users for %d companies", blockingCompanyIds.size()));

		List<UserSummary> userSummaries = summaryService.findAllUsersWithLastAssignedDateBetweenDates(workersActiveFrom, workersActiveTo);

		logger.debug("Evaluating " + userSummaries.size() + " users");
		for(UserSummary userSummary : userSummaries) {
			long start = System.currentTimeMillis();
			long userId = userSummary.getUserId();
			ScoreCard scorecard = analyticsService.getResourceScoreCard(userId);
			if(isBadActor(scorecard)) {
				blockBadActor(userId, blockingCompanyIds);
			}
			long end = System.currentTimeMillis();
			logger.debug(String.format("evaluated worker in %f seconds", (end-start)/1000.0));
		}
	}

	@VisibleForTesting
	boolean isBadActor(ScoreCard scorecard) {
		double abandoned = scorecard.getValueForField(ResourceScoreField.ABANDONED_WORK).getAll();
		double cancelled = scorecard.getValueForField(ResourceScoreField.CANCELLED_WORK).getAll();
		double onTimePercent = scorecard.getValueForField(ResourceScoreField.ON_TIME_PERCENTAGE).getAll() * 100.0;
		double paidAssignments = scorecard.getValueForField(ResourceScoreField.COMPLETED_WORK).getAll();
		double satisfactionRate = scorecard.getRating().getSatisfactionRate() * 100.0;

		int faultCount = 0;
		if(abandoned > MAX_ABANDONED_ASSIGNMENTS) {
			faultCount++;
		}
		if(cancelled > MAX_CANCELLED_ASSIGNMENTS) {
			faultCount++;
		}
		if(onTimePercent < MIN_ON_TIME_PERCENTAGE) {
			faultCount++;
		}
		if(satisfactionRate < MIN_SATISFACTION_RATING) {
			faultCount++;
		}
		if(paidAssignments < MIN_PAID_ASSIGNMENTS) {
			faultCount++;
		}
		return faultCount > MAX_BAD_CONDITIONS;
	}

	void blockBadActor(Long blockedUserId, List<Long> blockingCompanyIds) {
		if(!blockingCompanyIds.isEmpty()) {
			logger.debug("Blocking bad actor: " + blockedUserId + " for " + blockingCompanyIds.size() + " companies");
			eventRouter.sendEvent(eventFactory.buildBadActorEvent(blockedUserId, blockingCompanyIds));
		}
	}
}
