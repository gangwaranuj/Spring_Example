package com.workmarket.domains.work.service.route;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.WorkActionRequestFactory;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.resource.WorkResourceAddOptions;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.velvetrope.Doorman;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkRoutingServiceTest {

	@Mock	WorkService workService;
	@Mock UserService userService;
	@Mock EventFactory eventFactory;
	@Mock	WorkRoutingValidator workRoutingValidator;
	@Mock	WorkStatusService workStatusService;
	@Mock	WorkResourceService workResourceService;
	@Mock SummaryService summaryService;
	@Mock	WorkValidationService workValidationService;
	@Mock LaneService laneService;
	@Mock EventRouter eventRouter;
	@Mock	WorkActionRequestFactory workActionRequestFactory;
	@Mock AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Mock FeatureEvaluator featureEvaluator;
	@Mock AuthenticationService authenticationService;
	@Mock WorkResourceDAO workResourceDAO;
	@Mock Doorman doorman;
	@InjectMocks WorkRoutingServiceImpl workRoutingService;

	private Work work;
	private Work mockWork;
	private User user1;
	private User user2;
	private User user3;
	private User user4;
	private User mboResource;
	private LaneAssociation laneAssociation1;
	private LaneAssociation laneAssociation2;
	private LaneAssociation laneAssociation3;
	private LaneAssociation laneAssociation4;
	private Company workCompany;
	private Company userCompany;
	private Industry industry;
	private PeopleSearchRequest peopleSearchRequest;

	private Set<Long> potentialWorkResourcesUserIds = Sets.newHashSet(1L, 2L, 3L, 4L);
	private WorkResourceAddOptions options = new WorkResourceAddOptions(true);

	private PeopleSearchResponse peopleSearchResponse;
	ManageMyWorkMarket companyManageMyWorkMarket = new ManageMyWorkMarket();
	ManageMyWorkMarket manageMyWorkMarket = mock(ManageMyWorkMarket.class);

	private MboProfile mboProfile;

	@Before
	public void setUp() throws Exception {
		peopleSearchResponse = new PeopleSearchResponse();
		PeopleSearchResult peopleSearchResult = new PeopleSearchResult().setUserId(1L);
		PeopleSearchResult peopleSearchResult2 = new PeopleSearchResult().setUserId(2L);
		PeopleSearchResult peopleSearchResult3 = new PeopleSearchResult().setUserId(3L);
		peopleSearchResponse.addToResults(peopleSearchResult);
		peopleSearchResponse.addToResults(peopleSearchResult2);
		peopleSearchResponse.addToResults(peopleSearchResult3);

		peopleSearchRequest = new PeopleSearchRequest();
		peopleSearchRequest.addToGroupFilter(1L);

		industry = new Industry();
		industry.setId(1L);
		industry.setName("Technology");

		workCompany = new Company();
		workCompany.setId(1L);

		userCompany = new Company();
		userCompany.setId(1000L);


		manageMyWorkMarket.setPaymentTermsDays(15);
		manageMyWorkMarket.setPaymentTermsEnabled(true);


		mockWork = mock(Work.class);
		work = new Work();
		work.setId(1L);
		work.setManageMyWorkMarket(manageMyWorkMarket);
		work.setCompany(workCompany);
		work.setIndustry(industry);
		work.setCreatorId(345L);
		workCompany.setManageMyWorkMarket(companyManageMyWorkMarket);

		user1 = new User();
		user1.setId(1L);
		user1.setUserNumber("243243244");
		user1.setCompany(userCompany);
		user2 = new User();
		user2.setId(2L);
		user2.setUserNumber("455435");
		user2.setCompany(userCompany);
		user3 = new User();
		user3.setId(3L);
		user3.setUserNumber("6877979");
		user3.setCompany(userCompany);
		user4 = new User();
		user4.setId(4L);
		user4.setUserNumber("2368643");
		user4.setCompany(userCompany);

		laneAssociation1 = new LaneAssociation(user1, workCompany, LaneType.LANE_3);
		laneAssociation2 = new LaneAssociation(user2, workCompany, LaneType.LANE_3);
		laneAssociation3 = new LaneAssociation(user3, workCompany, LaneType.LANE_3);
		laneAssociation4 = new LaneAssociation(user4, workCompany, LaneType.LANE_3);

		Set<LaneAssociation> laneAssociations = Sets.newHashSet(laneAssociation1, laneAssociation2, laneAssociation3, laneAssociation4);
		List<User> users = Lists.newArrayList(user1, user2, user3, user4);
		Set<Long> userIds = Sets.newHashSet(user1.getId(), user2.getId(), user3.getId(), user4.getId());

		when(accountRegisterAuthorizationService.authorizeWork(any(Work.class))).thenReturn(WorkAuthorizationResponse.SUCCEEDED);
		when(accountRegisterAuthorizationService.authorizeWork(anyLong())).thenReturn(WorkAuthorizationResponse.SUCCEEDED);
		when(workRoutingValidator.validateWorkForRouting(any(Work.class))).thenReturn(Sets.newHashSet(WorkAuthorizationResponse.SUCCEEDED));

		when(workResourceService.findAllResourcesForWork(anyLong())).thenReturn(Collections.EMPTY_LIST);
		when(laneService.findAllAssociationsWhereUserIdIn(anyLong(), anySet())).thenReturn(laneAssociations);
		when(workValidationService.validateAssignmentCountry(any(Work.class), any(User.class))).thenReturn(true);
		when(userService.findAllUsersByIds(anyList())).thenReturn(users);
		when(userService.findAllUserIdsByUserNumbers(anyList())).thenReturn(userIds);

		when(userService.findUserById(1L)).thenReturn(user1);
		when(userService.findUserById(2L)).thenReturn(user2);
		when(userService.findUserById(3L)).thenReturn(user3);
		when(userService.findUserById(4L)).thenReturn(user4);
		when(userService.findUserById(5L)).thenReturn(mboResource);
		when(userService.findUserById(345L)).thenReturn(mock(User.class));

		when(workService.findWork(anyLong())).thenReturn(mockWork);
		when(workRoutingValidator.validateUser(any(User.class), any(Work.class), any(LaneType.class))).thenReturn(WorkAuthorizationResponse.SUCCEEDED);
		when(workRoutingValidator.validateSearchResult(any(PeopleSearchResult.class), anyList(), any(Work.class))).thenReturn(WorkAuthorizationResponse.SUCCEEDED);
		when(workRoutingValidator.validateProjectBudget(any(Work.class))).thenReturn(WorkAuthorizationResponse.SUCCEEDED);

	}

	@Test
	public void addToWorkResources_WithOptionsAndNoResourcesAssumesMoney_success() throws Exception {
		Set<Long> potentialWorkResourcesUserIds = Collections.EMPTY_SET;
		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work, potentialWorkResourcesUserIds, options);
		assertNotNull(response.getResponse());
		assertTrue(response.getResponse().keySet().contains(WorkAuthorizationResponse.SUCCEEDED));
		verify(eventRouter, times(2)).sendEvent(any(Event.class));
		verify(workStatusService, times(1)).transitionToSend(any(Work.class), any(WorkActionRequest.class));
	}

	@Test
	public void addToWorkResources_WithOptionsAndNoResourcesInsufficientBudget_fail() throws Exception {
		Set<Long> potentialWorkResourcesUserIds = Collections.EMPTY_SET;
		when(accountRegisterAuthorizationService.authorizeWork(anyLong())).thenReturn(WorkAuthorizationResponse.INSUFFICIENT_BUDGET);
		when(workRoutingValidator.validateWorkForRouting(any(Work.class))).thenReturn(Sets.newHashSet(WorkAuthorizationResponse.INSUFFICIENT_BUDGET));

		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work, potentialWorkResourcesUserIds, options);
		assertNotNull(response);
		assertTrue(response.getResponse().keySet().contains(WorkAuthorizationResponse.INSUFFICIENT_BUDGET));
		verify(eventRouter, never()).sendEvent(any(Event.class));
		verify(workStatusService, never()).transitionToSend(any(Work.class), any(WorkActionRequest.class));
	}

	@Test(expected = WorkNotFoundException.class)
	public void addToWorkResources_WithNullWork_fail() throws Exception {
		Set<Long> potentialWorkResourcesUserIds = Collections.EMPTY_SET;
		workRoutingService.addToWorkResources(null, potentialWorkResourcesUserIds, options);
	}

	@Test
	public void addToWorkResources_success() throws Exception {

		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work, potentialWorkResourcesUserIds, options);
		Assert.assertNotNull(response);
		assertTrue(response.getResponse().keySet().contains(WorkAuthorizationResponse.SUCCEEDED));
		verify(eventFactory, atLeast(1)).buildWorkResourceCache(anyLong());
		verify(eventRouter, atLeast(1)).sendEvent(any(Event.class));
	}

	@Test
	public void addToWorkResources_AddAllResourcesTwice_fail() throws Exception {
		WorkResource workResource1 = new WorkResource();
		workResource1.setUser(user1);
		workResource1.setWork(work);
		WorkResource workResource2 = new WorkResource();
		workResource2.setUser(user2);
		workResource2.setWork(work);
		WorkResource workResource3 = new WorkResource();
		workResource3.setUser(user3);
		workResource3.setWork(work);
		WorkResource workResource4 = new WorkResource();
		workResource4.setUser(user4);
		workResource4.setWork(work);
		List<WorkResource> resources = Lists.newArrayList(workResource1, workResource2, workResource3, workResource4);
		when(workResourceService.findAllResourcesForWork(anyLong())).thenReturn(resources);

		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work, potentialWorkResourcesUserIds, options);
		Assert.assertNotNull(response);
		assertTrue(response.getResponse().keySet().contains(WorkAuthorizationResponse.ALREADY_ADDED));

		verify(eventRouter, never()).sendEvent(any(Event.class));
		verify(workStatusService, never()).transitionToSend(any(Work.class), any(WorkActionRequest.class));
	}

	@Test
	public void addToWorkResources_AddOneResourceTwice_success() throws Exception {
		WorkResource workResource1 = new WorkResource();
		workResource1.setUser(user1);
		workResource1.setWork(work);
		List<WorkResource> resources = Lists.newArrayList(workResource1);
		when(workResourceService.findAllResourcesForWork(anyLong())).thenReturn(resources);

		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work, potentialWorkResourcesUserIds, options);
		Assert.assertNotNull(response);
		assertTrue(response.getResponse().keySet().contains(WorkAuthorizationResponse.ALREADY_ADDED));
		assertTrue(response.getResponse().keySet().contains(WorkAuthorizationResponse.SUCCEEDED));

		verify(eventRouter, times(3)).sendEvent(any(Event.class));
		verify(workStatusService, times(1)).transitionToSend(any(Work.class), any(WorkActionRequest.class));
	}

	@Test
	public void addToWorkResources_WithBlockedResources_fail() throws Exception {
		when(workRoutingValidator.validateUser(any(User.class), any(Work.class), any(LaneType.class))).thenReturn(WorkAuthorizationResponse.BLOCKED_RESOURCE);
		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work, potentialWorkResourcesUserIds, options);
		Assert.assertNotNull(response);
		assertTrue(response.getResponse().keySet().contains(WorkAuthorizationResponse.BLOCKED_RESOURCE));
		verify(eventRouter, never()).sendEvent(any(Event.class));
		verify(workStatusService, never()).transitionToSend(any(Work.class), any(WorkActionRequest.class));
	}

	@Test
	public void addToWorkResources_WithWrongCountry_fail() throws Exception {
		when(workRoutingValidator.validateUser(any(User.class), any(Work.class), any(LaneType.class))).thenReturn(WorkAuthorizationResponse.INVALID_COUNTRY);
		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work, potentialWorkResourcesUserIds, options);
		Assert.assertNotNull(response);
		assertTrue(response.getResponse().keySet().contains(WorkAuthorizationResponse.INVALID_COUNTRY));
		verify(eventRouter, never()).sendEvent(any(Event.class));
		verify(workStatusService, never()).transitionToSend(any(Work.class), any(WorkActionRequest.class));
	}

	@Test
	public void addToWorkResources_WorkCreatorNotInternal() throws Exception {
		Set<Long> workers = Sets.newHashSet(1L);
		Set<LaneAssociation> laneAssociations = Sets.newHashSet(laneAssociation1);
		when(laneService.findAllAssociationsWhereUserIdIn(anyLong(), anySet())).thenReturn(laneAssociations);
		Work work = new Work();
		work.setId(2L);
		work.setManageMyWorkMarket(manageMyWorkMarket);
		work.setCompany(workCompany);
		work.setIndustry(industry);
		work.setCreatorId(1L);
		workCompany.setManageMyWorkMarket(companyManageMyWorkMarket);
		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work, workers, options);
		assertTrue(response.getResponse().isEmpty());
	}

	@Test
	public void addToWorkResources_WorkCreatorNotInternal_fail() throws Exception {
		Set<Long> workers = Sets.newHashSet(1L,2L);
		Set<LaneAssociation> laneAssociations = Sets.newHashSet(laneAssociation1, laneAssociation2);
		when(laneService.findAllAssociationsWhereUserIdIn(anyLong(), anySet())).thenReturn(laneAssociations);
		Work work = new Work();
		work.setId(2L);
		work.setManageMyWorkMarket(manageMyWorkMarket);
		work.setCompany(workCompany);
		work.setIndustry(industry);
		work.setCreatorId(1L);
		workCompany.setManageMyWorkMarket(companyManageMyWorkMarket);
		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work, workers, options);
		assertTrue(response.getResponse().keySet().contains(WorkAuthorizationResponse.SUCCEEDED));
	}

	@Test
	public void addToWorkResources_WorkCreatorInternal() throws Exception {
		Set<Long> workers = Sets.newHashSet(1L);
		Set<LaneAssociation> laneAssociations = Sets.newHashSet(laneAssociation1);
		when(laneService.findAllAssociationsWhereUserIdIn(anyLong(), anySet())).thenReturn(laneAssociations);
		Work work = new Work();
		work.setPricingStrategy(new InternalPricingStrategy());
		work.setId(2L);
		work.setManageMyWorkMarket(manageMyWorkMarket);
		work.setCompany(workCompany);
		work.setIndustry(industry);
		work.setCreatorId(1L);
		workCompany.setManageMyWorkMarket(companyManageMyWorkMarket);
		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work, workers, options);
		assertFalse(response.getResponse().isEmpty());
	}

	@Test
	public void addToWorkResources_withEmptyResults() throws Exception {
		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work.getId(), Collections.EMPTY_LIST, options, false);
		assertNotNull(response);
		assertTrue(response.getResponse().isEmpty());
	}

	@Test
	public void addToWorkResources_withBundle_NoFeed() throws Exception {
		work.getManageMyWorkMarket().setShowInFeed(true);
		WorkBundle bundle = mock(WorkBundle.class);
		work.setParent(bundle);
		when(bundle.getWorkStatusType()).thenReturn(WorkStatusType.newWorkStatusType(WorkStatusType.SENT));
		WorkRoutingResponseSummary response = workRoutingService.addToWorkResources(work, potentialWorkResourcesUserIds, options);
		Assert.assertNotNull(response);
		assertTrue(response.getResponse().keySet().contains(WorkAuthorizationResponse.SUCCEEDED));
		assertEquals(false, work.getManageMyWorkMarket().getShowInFeed());
	}

	@Test
	public void getEligibleLane3Users_withEmptyPotentialUsers_returnsEmptySet() {
		Set<User> users = workRoutingService.getEligibleLane3Users(null, Collections.EMPTY_LIST, workCompany);
		assertNotNull(users);
		assertTrue(users.isEmpty());
	}

	@Test
	public void getEligibleLane3Users_withNoLaneActiveUsers_returnsEmptySet() {
		WorkRoutingResponseSummary workRoutingResponseSummary = new WorkRoutingResponseSummary();
		Set<User> users = workRoutingService.getEligibleLane3Users(workRoutingResponseSummary, Lists.newArrayList(1L, 3L, 6L), workCompany);
		verify(userService, times(1)).findAllUsersByIds(anyList());
		assertNotNull(users);
		assertTrue(users.isEmpty());
		assertTrue(workRoutingResponseSummary.getResponse().containsKey(WorkAuthorizationResponse.INVALID_USER));
		assertTrue(workRoutingResponseSummary.getResponse().get(WorkAuthorizationResponse.INVALID_USER).size() == 4);
	}
}
