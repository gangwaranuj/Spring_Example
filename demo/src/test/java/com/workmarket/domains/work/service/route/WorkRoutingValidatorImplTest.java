package com.workmarket.domains.work.service.route;

import com.google.common.collect.Lists;
import com.workmarket.dao.BlockedAssociationDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.dto.WorkCostDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkRoutingValidatorImplTest {

	@Mock BlockedAssociationDAO blockedAssociationDAO;
	@Mock WorkValidationService workValidationService;
	@Mock AccountRegisterService accountRegisterServicePrefundImpl;
	@Mock AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@InjectMocks WorkRoutingValidatorImpl workResourceRouteValidator;

	private PeopleSearchResult searchResult;
	private List<Long> resourcesOnWork = Lists.newArrayList(1L, 2L, 3L);
	private Work work;
	private Company company;
	private Company userCompany;
	private ManageMyWorkMarket manageMyWorkMarket;
	private User user;

	@Before
	public void setUp() throws Exception {
		work = mock(Work.class);
		searchResult = mock(PeopleSearchResult.class);
		company = mock(Company.class);
		userCompany = mock(Company.class);
		manageMyWorkMarket = mock(ManageMyWorkMarket.class);
		user = mock(User.class);

		when(company.isLocked()).thenReturn(false);
		when(searchResult.getUserId()).thenReturn(4L);
		when(searchResult.getCompanyId()).thenReturn(1L);
		when(searchResult.getLane()).thenReturn(LaneType.LANE_1);

		when(manageMyWorkMarket.isInstantWorkerPoolEnabled()).thenReturn(false);

		when(work.getPricingStrategy()).thenReturn(new InternalPricingStrategy());
		when(work.getCompany()).thenReturn(company);
		when(work.getManageMyWorkMarket()).thenReturn(manageMyWorkMarket);

		when(user.getCompany()).thenReturn(userCompany);
		when(userCompany.getId()).thenReturn(5L);

		when(workValidationService.validateAssignmentCountry(any(Work.class), any(User.class))).thenReturn(true);
		when(workValidationService.validateAssignmentCountry(any(Work.class), any(PeopleSearchResult.class))).thenReturn(true);
	}

	@Test
	public void validateSearchResult_withInternalUser_success() throws Exception {
		Assert.assertEquals(workResourceRouteValidator.validateSearchResult(searchResult, resourcesOnWork, work), WorkAuthorizationResponse.SUCCEEDED);
	}

	@Test
	public void validateSearchResult_withNonInternalUserAndLockedCompany() throws Exception {
		when(company.isLocked()).thenReturn(true);
		when(searchResult.getLane()).thenReturn(LaneType.LANE_3);
		when(work.getPricingStrategy()).thenReturn(new FlatPricePricingStrategy());
		Assert.assertEquals(workResourceRouteValidator.validateSearchResult(searchResult, resourcesOnWork, work), WorkAuthorizationResponse.COMPANY_LOCKED);
	}

	@Test
	public void validateSearchResult_withNonInternalUserAlreadyAdded() throws Exception {
		when(searchResult.getUserId()).thenReturn(2L);
		when(work.getPricingStrategy()).thenReturn(new FlatPricePricingStrategy());
		Assert.assertEquals(workResourceRouteValidator.validateSearchResult(searchResult, resourcesOnWork, work), WorkAuthorizationResponse.ALREADY_ADDED);
	}

	@Test
	public void validateSearchResult_withNonInternalUserAndInternalPricing() throws Exception {
		when(searchResult.getLane()).thenReturn(LaneType.LANE_3);
		Assert.assertEquals(workResourceRouteValidator.validateSearchResult(searchResult, resourcesOnWork, work), WorkAuthorizationResponse.INTERNAL_PRICING);
	}

	@Test
	public void validateSearchResult_withBlockedResource() throws Exception {
		when(blockedAssociationDAO.isUserBlockedForCompany(anyLong(), anyLong(), anyLong())).thenReturn(true);
		when(searchResult.getLane()).thenReturn(LaneType.LANE_3);
		Assert.assertEquals(workResourceRouteValidator.validateSearchResult(searchResult, resourcesOnWork, work), WorkAuthorizationResponse.INTERNAL_PRICING);
	}

	@Test
	public void validateSearchResult_withLane0NotAllowed() throws Exception {
		when(searchResult.getLane()).thenReturn(LaneType.LANE_0);
		Assert.assertEquals(workResourceRouteValidator.validateSearchResult(searchResult, resourcesOnWork, work), WorkAuthorizationResponse.LANE0_NOT_ALLOWED);
	}

	@Test
	public void validateSearchResult_withInstantWorkerPoolDisabled() throws Exception {
		when(searchResult.getLane()).thenReturn(LaneType.LANE_4);
		Assert.assertEquals(workResourceRouteValidator.validateSearchResult(searchResult, resourcesOnWork, work), WorkAuthorizationResponse.DISABLED_WORKER_POOL);
	}

	@Test
	public void validateUser_withLane0NotAllowed() throws Exception {
		Assert.assertEquals(workResourceRouteValidator.validateUser(user, work, LaneType.LANE_0), WorkAuthorizationResponse.LANE0_NOT_ALLOWED);
	}

	@Test
	public void validateUser_withInternalPricingAndLane1() throws Exception {
		Assert.assertEquals(workResourceRouteValidator.validateUser(user, work, LaneType.LANE_1), WorkAuthorizationResponse.SUCCEEDED);
	}

	@Test
	public void validateUser_withInternalPricingAndLane3() throws Exception {
		Assert.assertEquals(workResourceRouteValidator.validateUser(user, work, LaneType.LANE_3), WorkAuthorizationResponse.INTERNAL_PRICING);
	}

	@Test
	public void validateUser_withNonInternalUserAndLockedCompany() throws Exception {
		when(company.isLocked()).thenReturn(true);
		when(work.getPricingStrategy()).thenReturn(new FlatPricePricingStrategy());
		Assert.assertEquals(workResourceRouteValidator.validateUser(user, work, LaneType.LANE_3), WorkAuthorizationResponse.COMPANY_LOCKED);
	}

	@Test
	public void validateUser_withInvalidCountry() throws Exception {
		when(work.getPricingStrategy()).thenReturn(new FlatPricePricingStrategy());
		when(workValidationService.validateAssignmentCountry(any(Work.class), any(User.class))).thenReturn(false);
		Assert.assertEquals(workResourceRouteValidator.validateUser(user, work, LaneType.LANE_3), WorkAuthorizationResponse.INVALID_COUNTRY);
	}

	@Test
	public void validateProjectBudget_success() throws Exception {
		when(work.hasProject()).thenReturn(false);
		Assert.assertEquals(workResourceRouteValidator.validateProjectBudget(work), WorkAuthorizationResponse.SUCCEEDED);
	}

	@Test
	public void validateProjectBudget_fail() throws Exception {
		Project project = mock(Project.class);
		when(work.getProject()).thenReturn(project);
		when(work.hasProject()).thenReturn(true);
		when(project.getBudgetEnabledFlag()).thenReturn(true);
		when(project.getRemainingBudget()).thenReturn(BigDecimal.ZERO);
		WorkCostDTO costDTO = new WorkCostDTO();
		costDTO.setTotalBuyerCost(BigDecimal.TEN);
		when(accountRegisterServicePrefundImpl.calculateCostOnSentWork(work)).thenReturn(costDTO);

		Assert.assertEquals(workResourceRouteValidator.validateProjectBudget(work), WorkAuthorizationResponse.INSUFFICIENT_BUDGET);
	}

	@Test
	public void validateWorkForRouting_withInvalidBundleState() {
		when(work.isInBundle()).thenReturn(true);
		WorkBundle parent = mock(WorkBundle.class);
		when(parent.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.ACTIVE));
		when(work.getParent()).thenReturn(parent);

		Set<WorkAuthorizationResponse> authorizationResponse = workResourceRouteValidator.validateWorkForRouting(work);
		assertTrue(authorizationResponse.contains(WorkAuthorizationResponse.INVALID_BUNDLE_STATE));
		verify(accountRegisterAuthorizationService, never()).authorizeWork(eq(work));
	}

	@Test
	public void validateWorkForRouting_withInsufficientProjectBudget() {
		Project project = mock(Project.class);
		when(work.getProject()).thenReturn(project);
		when(work.hasProject()).thenReturn(true);
		when(project.getBudgetEnabledFlag()).thenReturn(true);
		when(project.getRemainingBudget()).thenReturn(BigDecimal.ZERO);
		WorkCostDTO costDTO = new WorkCostDTO();
		costDTO.setTotalBuyerCost(BigDecimal.TEN);
		when(accountRegisterServicePrefundImpl.calculateCostOnSentWork(work)).thenReturn(costDTO);

		Set<WorkAuthorizationResponse> authorizationResponse = workResourceRouteValidator.validateWorkForRouting(work);
		assertTrue(authorizationResponse.contains(WorkAuthorizationResponse.INSUFFICIENT_BUDGET));
		verify(accountRegisterAuthorizationService, never()).authorizeWork(eq(work));
	}

	@Test
	public void validateWorkForRouting_withInsufficientFunds() {
		when(accountRegisterAuthorizationService.authorizeWork(eq(work))).thenReturn(WorkAuthorizationResponse.INSUFFICIENT_FUNDS);
		Set<WorkAuthorizationResponse> authorizationResponse = workResourceRouteValidator.validateWorkForRouting(work);
		assertTrue(authorizationResponse.contains(WorkAuthorizationResponse.INSUFFICIENT_FUNDS));
		verify(accountRegisterAuthorizationService, times(1)).authorizeWork(eq(work));
	}

	@Test
	public void validateWorkForRouting_success() {
		when(work.isInBundle()).thenReturn(true);
		WorkBundle parent = mock(WorkBundle.class);
		when(parent.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.SENT));
		when(work.getParent()).thenReturn(parent);
		when(accountRegisterAuthorizationService.authorizeWork(eq(work))).thenReturn(WorkAuthorizationResponse.SUCCEEDED);

		Set<WorkAuthorizationResponse> authorizationResponse = workResourceRouteValidator.validateWorkForRouting(work);
		assertTrue(authorizationResponse.contains(WorkAuthorizationResponse.SUCCEEDED));
		verify(accountRegisterAuthorizationService, times(1)).authorizeWork(eq(work));
	}
}
