package com.workmarket.service.business.scheduler;

import com.google.common.collect.ImmutableList;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.analytics.ResourceScoreField;
import com.workmarket.domains.model.analytics.ScoreCard;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.business.WebActivityAuditService;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.user.BadActorEvent;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.service.infra.business.FeatureToggleService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class BadActorExecutorIT extends BaseServiceIT {

	@Autowired private BadActorExecutor badActorExecutor;
	@Autowired RatingService ratingService;
	@Autowired private WebActivityAuditService webActivityAuditService;
	@Autowired private AnalyticsService analyticsService;
	@Autowired private FeatureToggleService featureToggleService;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;

	private User wmEmployee;

	@Before
	public void initEmployee() throws Exception {
		this.wmEmployee = newWMEmployee();

		String feature = "bad-actor";
		ServiceResponseBuilder response = featureToggleService.getFeature(feature);
		if(response.failed()) {
			featureToggleService.addFeature(feature, true);
		}

		featureToggleService.addSegment(feature, "companyId", String.valueOf(this.wmEmployee.getCompany().getId()));
	}

	@Test
	public void testBuildBadActorScorecardForUser() throws Exception {
		TestParams[] testParams = {
			// abandoned, cancelledLessThan24Hours, cancelledMoreThan24Hours, completedNotRated, completedRated4, completedRated1, expectedBadActor

			// failing conditions: abandoned, cancelled, on-time percent, satisfaction rating
			new TestParams(2, 2, 2, 2, 2, 2, true),
			// failing conditions: abandoned, cancelled, on-time percent, satisfaction rating
			new TestParams(3, 3, 3, 3, 3, 3, true),
			// failing conditions: abandoned, cancelled, on-time percent, satisfaction rating
			new TestParams(4, 4, 4, 4, 4, 4, true),
			// failing conditions: none
			new TestParams(0, 0, 0, 2, 2, 0, false),
			// failing conditions: on-time percent
			new TestParams(BadActorExecutor.MAX_ABANDONED_ASSIGNMENTS, BadActorExecutor.MAX_CANCELLED_ASSIGNMENTS, 0, BadActorExecutor.MIN_PAID_ASSIGNMENTS, 1, 0, false),
			// failing conditions: abandoned, on-time percent
			new TestParams(BadActorExecutor.MAX_ABANDONED_ASSIGNMENTS+1, BadActorExecutor.MAX_CANCELLED_ASSIGNMENTS, 0, BadActorExecutor.MIN_PAID_ASSIGNMENTS, 1, 0, false),
			// failing conditions: cancelled, on-time
			new TestParams(0, 0, 10, 1, 1, 0, false),
			// failing conditions:
			new TestParams(BadActorExecutor.MAX_ABANDONED_ASSIGNMENTS+1, BadActorExecutor.MAX_CANCELLED_ASSIGNMENTS, 0, 25, 1, 1, true)};
		for(TestParams params : testParams) {
			validateBadActorScorecardForUser(params);
		}
	}

	@Test
	public void testGetResourceScorecardForUser() throws Exception {
		for(int i = 0;i < 10;i++) {
			validateResourceScorecardForUser(new TestParams(
				new Random().nextInt(10),
				new Random().nextInt(10),
				new Random().nextInt(10),
				new Random().nextInt(10),
				new Random().nextInt(10),
				new Random().nextInt(10)));
		}
	}

	@Test
	public void testBadActorEvent() throws Exception {

		User contractor = newContractorIndependentlane4Ready();
		eventRouter.onEvent(
			eventFactory.buildBadActorEvent(contractor.getId(), ImmutableList.of(wmEmployee.getCompany().getId()))
		);

		List<Long> blockedUserIds = userService.findAllBlockedUserIdsByBlockingUserId(Constants.WORKMARKET_SYSTEM_USER_ID);
		assertTrue(blockedUserIds.contains(contractor.getId()));
	}

	public void validateBadActorScorecardForUser(TestParams params) throws Exception {

		User contractor = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane2(contractor.getId(), wmEmployee.getCompany().getId());
		webActivityAuditService.saveLoginInfo(contractor.getId(), DateUtilities.getCalendarNow(), contractor.getCompany().getId(), "0:0:0:0:0:0:0:1", true);

		prepareTestAssignments(contractor, params);

		ScoreCard scorecard = analyticsService.getResourceScoreCard(contractor.getId());

		double abandoned = scorecard.getValueForField(ResourceScoreField.ABANDONED_WORK).getAll();
		double cancelled = scorecard.getValueForField(ResourceScoreField.CANCELLED_WORK).getAll();
		double onTimePercent = scorecard.getValueForField(ResourceScoreField.ON_TIME_PERCENTAGE).getAll();
		double satisfactionRate = scorecard.getRating().getSatisfactionRate();
		double paidAssignments = scorecard.getValueForField(ResourceScoreField.COMPLETED_WORK).getAll();
		double numCompleted = params.getNumCompletedNotRated() + params.getNumCompletedRated1() + params.getNumCompletedRated4();
		double numCancelled = params.getNumCancelledLessThan24Hours() + params.getNumCancelledMoreThan24Hours();
		double numCompletedAndRated = params.getNumCompletedRated1() + params.getNumCompletedRated4();

		assertEquals(abandoned, Double.valueOf(params.getNumAbandonedAssignments()), 0);
		assertEquals(cancelled, numCancelled, 0);
		if (numCompleted + params.getNumCancelledLessThan24Hours() +
			params.getNumCancelledMoreThan24Hours() + params.getNumAbandonedAssignments() > 0) {
			assertEquals(onTimePercent,
				Double.valueOf(numCompleted + params.getNumCancelledMoreThan24Hours()) /
					Double.valueOf(numCompleted + numCancelled + params.getNumAbandonedAssignments()), 0);
		}
		if (params.getNumCompletedRated1() + params.getNumCompletedRated4() > 0) {
			assertEquals(satisfactionRate, Double.valueOf(params.getNumCompletedRated4()) / numCompletedAndRated, 0);
		}
		assertEquals(paidAssignments, numCompleted, 0);
		assertEquals(params.isBadActor(), badActorExecutor.isBadActor(scorecard));
	}

	public void validateResourceScorecardForUser(TestParams params) throws Exception {

		User contractor = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane2(contractor.getId(), wmEmployee.getCompany().getId());
		webActivityAuditService.saveLoginInfo(contractor.getId(), DateUtilities.getCalendarNow(), contractor.getCompany().getId(), "0:0:0:0:0:0:0:1", true);

		prepareTestAssignments(contractor, params);

		ScoreCard scorecard = analyticsService.getResourceScoreCard(contractor.getId());

		double abandoned = scorecard.getValueForField(ResourceScoreField.ABANDONED_WORK).getAll();
		double cancelled = scorecard.getValueForField(ResourceScoreField.CANCELLED_WORK).getAll();
		double cancelled24 = scorecard.getValueForField(ResourceScoreField.CANCELLED_WORK_IN_LESS_THAN_24_HOURS).getAll();
		double onTimePercent = scorecard.getValueForField(ResourceScoreField.ON_TIME_PERCENTAGE).getAll();
		double satisfactionRate = scorecard.getRating().getSatisfactionRate();
		double paidAssignments = scorecard.getValueForField(ResourceScoreField.COMPLETED_WORK).getAll();
		double numCompleted = params.getNumCompletedNotRated() + params.getNumCompletedRated1() + params.getNumCompletedRated4();
		double numCancelled = params.getNumCancelledLessThan24Hours() + params.getNumCancelledMoreThan24Hours();
		double numCompletedAndRated = params.getNumCompletedRated1() + params.getNumCompletedRated4();

		assertEquals(abandoned, Double.valueOf(params.getNumAbandonedAssignments()), .001);
		assertEquals(cancelled, numCancelled, 0);
		assertEquals(cancelled24, Double.valueOf(params.getNumCancelledLessThan24Hours()), .001);
		if(numCompleted + params.getNumCancelledLessThan24Hours() +
			params.getNumCancelledMoreThan24Hours() + params.getNumAbandonedAssignments() > .001) {
			assertEquals(onTimePercent,
				Double.valueOf(numCompleted + params.getNumCancelledMoreThan24Hours()) /
					Double.valueOf(numCompleted + numCancelled + params.getNumAbandonedAssignments()), .001);
		}
		if(params.getNumCompletedRated1() + params.getNumCompletedRated4() > 0) {
			assertEquals(satisfactionRate, Double.valueOf(params.getNumCompletedRated4()) / numCompletedAndRated, .001);
		}
		assertEquals(paidAssignments, numCompleted, 0);
	}

	private void prepareTestAssignments(User contractor, TestParams params) throws Exception {

			laneService.addUserToCompanyLane2(contractor.getId(), wmEmployee.getCompany().getId());
			webActivityAuditService.saveLoginInfo(contractor.getId(), DateUtilities.getCalendarNow(), contractor.getCompany().getId(), "0:0:0:0:0:0:0:1", true);
			createAbandonedWork(contractor, params.getNumAbandonedAssignments());
			createCancelledLessThan24HoursWork(contractor, params.getNumCancelledLessThan24Hours());
			createCancelledMoreThan24HoursWork(contractor, params.getNumCancelledMoreThan24Hours());
			createApprovedWork(contractor, params.getNumCompletedNotRated());
			createRatedWork(contractor, getFilledIntArray(params.getNumCompletedRated4(), 4));
			createRatedWork(contractor, getFilledIntArray(params.getNumCompletedRated1(), 1));

		}
	private void createAbandonedWork(User contractor, int numAbandonedAssignments) throws Exception {
		for(int i = 0;i < numAbandonedAssignments;i++) {
			createAbandonedWork(contractor);
		}
	}

	private void createAbandonedWork(User contractor) throws Exception {
		Work work = newWorkWithPaymentTerms(this.wmEmployee.getId(), 30);
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		Assert.assertNotNull(work);
		workService.acceptWork(contractor.getId(), work.getId());

		CancelWorkDTO cancelWorkDTO = new CancelWorkDTO();
		cancelWorkDTO.setWorkId(work.getId());
		cancelWorkDTO.setPrice(0.0);
		cancelWorkDTO.setCancellationReasonTypeCode(CancellationReasonType.RESOURCE_ABANDONED);
		cancelWorkDTO.setNote("This is a abandonment note");

		workService.cancelWork(cancelWorkDTO);
	}

	private void createCancelledLessThan24HoursWork(User contractor, int numCancelledAssignments) throws Exception {
		for(int i = 0;i < numCancelledAssignments;i++) {
			createCancelledLessThan24HoursWork(contractor);
		}
	}

	private void createCancelledLessThan24HoursWork(User contractor) throws Exception {
		Work work = newWorkWithPaymentTerms(this.wmEmployee.getId(), 30);
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		Assert.assertNotNull(work);
		workService.acceptWork(contractor.getId(), work.getId());
		CancelWorkDTO cancelWorkDTO = new CancelWorkDTO();
		cancelWorkDTO.setWorkId(work.getId());
		cancelWorkDTO.setPrice(0.0);
		cancelWorkDTO.setCancellationReasonTypeCode(CancellationReasonType.RESOURCE_CANCELLED);
		cancelWorkDTO.setNote("This is a abandonment note");

		workService.cancelWork(cancelWorkDTO);
	}

	private void createCancelledMoreThan24HoursWork(User contractor, int numCancelledAssignments) throws Exception {
		for(int i = 0;i < numCancelledAssignments;i++) {
			createCancelledMoreThan24HoursWork(contractor);
		}
	}

	private void createCancelledMoreThan24HoursWork(User contractor) throws Exception {
		Calendar cal = DateUtilities.getCalendarNow();
		cal.add(Calendar.DAY_OF_MONTH, 2);
		Work work = newWorkWithPaymentTerms(this.wmEmployee.getId(), 30, cal);
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		Assert.assertNotNull(work);
		workService.acceptWork(contractor.getId(), work.getId());
		CancelWorkDTO cancelWorkDTO = new CancelWorkDTO();
		cancelWorkDTO.setWorkId(work.getId());
		cancelWorkDTO.setPrice(0.0);
		cancelWorkDTO.setCancellationReasonTypeCode(CancellationReasonType.RESOURCE_CANCELLED);
		cancelWorkDTO.setNote("This is a abandonment note");

		workService.cancelWork(cancelWorkDTO);
	}

	private void createApprovedWork(User contractor, int numApprovedAssignments) throws Exception {
		for(int i = 0;i < numApprovedAssignments;i++) {
			createApprovedWork(contractor);
		}
	}

	private void createApprovedWork(User contractor) throws Exception {
		Work work = newWorkWithPaymentTerms(this.wmEmployee.getId(), 30);
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		Assert.assertNotNull(work);
		workService.acceptWork(contractor.getId(), work.getId());

		CompleteWorkDTO dto = new CompleteWorkDTO();
		dto.setResolution("Resolved");
		workService.completeWork(work.getId(), dto);

		authenticationService.setCurrentUser(this.wmEmployee.getId());
		CloseWorkDTO closeWorkDTO = new CloseWorkDTO();
		closeWorkDTO.setRating(new RatingDTO(80));
		workService.closeWork(work.getId(), closeWorkDTO);
	}

	private void createRatedWork(User contractor, int[] ratings) throws Exception {
		for(int rating : ratings) {
			createRatedWork(contractor, rating);
		}
	}

	private void createRatedWork(User contractor, int rating) throws Exception {
		User employee = newEmployeeWithCashBalance();
		// lane 3 so rating is public
		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		Work work = newWorkWithPaymentTerms(employee.getId(), 0);
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		Assert.assertNotNull(work);
		workService.acceptWork(contractor.getId(), work.getId());

		CompleteWorkDTO dto = new CompleteWorkDTO();
		dto.setResolution("Resolved");
		workService.completeWork(work.getId(), dto);

		authenticationService.setCurrentUser(employee.getId());
		CloseWorkDTO closeWorkDTO = new CloseWorkDTO();
		closeWorkDTO.setRating(new RatingDTO(rating));
		workService.closeWork(work.getId(), closeWorkDTO);
	}

	private int[] getFilledIntArray(int size, int value) {
		int[] filled = new int[size];
		Arrays.fill(filled, value);
		return filled;
	}

	static class TestParams {
		private final int numAbandonedAssignments;
		private final int numCancelledLessThan24Hours;
		private final int numCancelledMoreThan24Hours;
		private final int numCompletedNotRated;
		private final int numCompletedRated4;
		private final int numCompletedRated1;
		private final boolean badActor;

		public TestParams(
			int numAbandonedAssignments,
			int numCancelledLessThan24Hours,
			int numCancelledMoreThan24Hours,
			int numCompletedNotRated,
			int numCompletedRated4,
			int numCompletedRated1,
			boolean badActor) {
			this.numAbandonedAssignments = numAbandonedAssignments;
			this.numCancelledLessThan24Hours = numCancelledLessThan24Hours;
			this.numCancelledMoreThan24Hours = numCancelledMoreThan24Hours;
			this.numCompletedNotRated = numCompletedNotRated;
			this.numCompletedRated4 = numCompletedRated4;
			this.numCompletedRated1 = numCompletedRated1;
			this.badActor = badActor;
		}

		public TestParams(
			int numAbandonedAssignments,
			int numCancelledLessThan24Hours,
			int numCancelledMoreThan24Hours,
			int numCompletedNotRated,
			int numCompletedRated4,
			int numCompletedRated1) {
			this(numAbandonedAssignments, numCancelledLessThan24Hours, numCancelledMoreThan24Hours, numCompletedNotRated,
				numCompletedRated4, numCompletedRated1, false);
		}

		public int getNumAbandonedAssignments() {
			return numAbandonedAssignments;
		}

		public int getNumCancelledLessThan24Hours() {
			return numCancelledLessThan24Hours;
		}

		public int getNumCancelledMoreThan24Hours() {
			return numCancelledMoreThan24Hours;
		}

		public int getNumCompletedNotRated() {
			return numCompletedNotRated;
		}

		public int getNumCompletedRated4() {
			return numCompletedRated4;
		}

		public int getNumCompletedRated1() {
			return numCompletedRated1;
		}

		public boolean isBadActor() {
			return badActor;
		}
	}
}
