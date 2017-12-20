package com.workmarket.service.business.project;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.status.WorkNegotiationResponseStatus;
import com.workmarket.domains.work.service.project.ProjectBudgetService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.service.exception.project.InsufficientBudgetException;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ProjectBudgetServiceIT extends BaseServiceIT {

	@Autowired ProjectBudgetService projectBudgetService;
	@Autowired WorkNegotiationService workNegotiationService;

	private static final String DEFAULT_CASH_AMOUNT = "2000.00";
	private static final String DEFAULT_BUDGET_AMOUNT = "1000.00";
	private static final Double WORK_FLAT_FEE = 100.00;
	private static final Double WORK_FLAT_FEE_EXCEED_BUDGET = 1500.00;
	private static final Double WORK_NEGOTIATION_FLAT_FEE_EXCEED_BUDGET = 1500.00;
	private static final Double WORK_NEGOTIATION_RESOURCE_FLAT_FEE = 200.00;
	private static final Double WORK_NEGOTIATION_BUYER_FLAT_FEE = 220.00;
	private static final int NUMBER_OF_WORK = 1;

	@Test
	public void sendAssignmentFromProject_HaveRemainingBudget_Success() throws Exception{
		User user = initUserForProjectTest();
		User contractor = newContractor();
		Project projectWithBudgetEnabled = createProjectWithBudgetEnabled(user);
		BigDecimal exptectedRemainingBudget = projectWithBudgetEnabled.getRemainingBudget().subtract(new BigDecimal(WORK_FLAT_FEE));

		// Sent work to a resource
		Work work = createWorkInProject(user, projectWithBudgetEnabled.getId());
		laneService.addUserToCompanyLane2(contractor.getId(), user.getCompany().getId());
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));

		BigDecimal actualRemainingBudget = projectService.findById(projectWithBudgetEnabled.getId()).getRemainingBudget();
		Assert.assertEquals(exptectedRemainingBudget.compareTo(actualRemainingBudget), 0);

	}


	@Test
	public void sendAssignmentFromProject_DoNotHaveRemainingBudget_InsuffienctBudget() throws Exception{
		User user = initUserForProjectTest();
		User contractor = newContractor();
		Project projectWithBudgetEnabled = createProjectWithBudgetEnabled(user);

		// Sent work to a resource
		Work work = createOverBudgetWorkInProject(user, projectWithBudgetEnabled.getId());
		laneService.addUserToCompanyLane2(contractor.getId(), user.getCompany().getId());
		Map<WorkAuthorizationResponse, Set<String>> addWorkResourceResponseTypeCollectionMap = workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber())).getResponse();
		Assert.assertTrue(addWorkResourceResponseTypeCollectionMap.containsKey(WorkAuthorizationResponse.INSUFFICIENT_BUDGET));
	}


	@Test
	public void acceptCounterOffer_HaveRemainingBudget_Success() throws Exception{
		User user = initUserForProjectTest();
		User contractor = newContractor();
		Project projectWithBudgetEnabled = createProjectWithBudgetEnabled(user);
		BigDecimal expectedRemainingBudget = projectWithBudgetEnabled.getRemainingBudget().subtract(new BigDecimal(WORK_NEGOTIATION_BUYER_FLAT_FEE));

		// Sent work to a resource
		Work work = createWorkInProject(user, projectWithBudgetEnabled.getId());
		laneService.addUserToCompanyLane2(contractor.getId(), user.getCompany().getId());
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		WorkNegotiationDTO dto = createNegotiation();

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
		WorkNegotiation negotiation = (WorkNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());

		authenticationService.setCurrentUser(user);
		workNegotiationService.approveNegotiation(negotiation.getId());

		BigDecimal actualRemainingBudget = projectService.findById(projectWithBudgetEnabled.getId()).getRemainingBudget();
		Assert.assertEquals(expectedRemainingBudget.compareTo(actualRemainingBudget), 0);

	}

	@Test (expected = InsufficientBudgetException.class)
	public void acceptCounterOffer_DoNotHaveRemainingBudget_Failure() throws Exception{
		User user = initUserForProjectTest();
		User contractor = newContractor();
		Project projectWithBudgetEnabled = createProjectWithBudgetEnabled(user);

		// Sent work to a resource
		Work work = createWorkInProject(user, projectWithBudgetEnabled.getId());
		laneService.addUserToCompanyLane2(contractor.getId(), user.getCompany().getId());
		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		WorkNegotiationDTO dto = createExceedBudgetNegotiation();

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
		WorkNegotiation negotiation = (WorkNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());

		authenticationService.setCurrentUser(user);
		WorkNegotiationResponse workNegotiationResponse = workNegotiationService.approveNegotiation(negotiation.getId());

		Assert.assertTrue((WorkNegotiationResponseStatus.FAILURE).equals(workNegotiationResponse.getStatus()));

	}

	@Test
	public void testCalcTotalWorkValue() throws Exception{
		User user = initUserForProjectTest();
		User contractor = newContractor();
		Project projectWithBudgetEnabled = createProjectWithBudgetEnabled(user);
		BigDecimal expectedTotalWorkValue = new BigDecimal(WORK_FLAT_FEE).multiply(new BigDecimal(NUMBER_OF_WORK));

		for (int i = 0; i < NUMBER_OF_WORK ; i++) {
			// Sent work to a resource
			Work work = createWorkInProject(user, projectWithBudgetEnabled.getId());
			laneService.addUserToCompanyLane2(contractor.getId(), user.getCompany().getId());
			workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		}

		BigDecimal acturalTotalWorkValue = projectBudgetService.calcTotalWorkValue(projectService.findById(projectWithBudgetEnabled.getId()));
		Assert.assertEquals(expectedTotalWorkValue.compareTo(acturalTotalWorkValue), 0);

	}

	@Test
	public void test_resetAllProjectBudget() throws Exception{
		User user = initUserForProjectTest();
		Project projectWithBudgetEnabled = createProjectWithBudgetEnabled(user);
		Assert.assertTrue(projectWithBudgetEnabled.getBudgetEnabledFlag());
		projectService.resetAllProjectBudget(user.getCompany().getId());

		Project projectUpdated = projectService.findById(projectWithBudgetEnabled.getId());
		Assert.assertFalse(projectUpdated.getBudgetEnabledFlag());
		Assert.assertTrue(projectUpdated.getBudget().compareTo(BigDecimal.ZERO) == 0);
		Assert.assertTrue(projectUpdated.getRemainingBudget().compareTo(BigDecimal.ZERO) == 0);

	}



	private User initUserForProjectTest() throws Exception {
		User user = newFirstEmployeeWithNOCashBalanceAndPaymentTerms();
		accountRegisterService.addFundsToRegisterFromWire(user.getCompany().getId(), DEFAULT_CASH_AMOUNT);
		ClientCompany clientCompany = newClientCompany(user.getId());

		// Create a project with budget enabled
		Project projectWithBudgetEnabled =
			newProject(user.getId(), clientCompany.getId(), "Project With Budget Enabled");
		projectWithBudgetEnabled.setBudgetEnabledFlag(true);
		projectWithBudgetEnabled.setBudget(new BigDecimal(DEFAULT_BUDGET_AMOUNT));
		projectWithBudgetEnabled.setRemainingBudget(new BigDecimal(DEFAULT_BUDGET_AMOUNT));
		projectService.saveOrUpdate(projectWithBudgetEnabled);

		// Create a project without budget enabled
		Project projectWithoutBudgetEnabled =
			newProject(user.getId(), clientCompany.getId(), "Project Without Budget Enabled");
		projectService.saveOrUpdate(projectWithoutBudgetEnabled);

		return user;
	}

	private Project createProjectWithBudgetEnabled (User user) throws Exception{
		ClientCompany clientCompany = newClientCompany(user.getId());

		// Create a project with budget funds enabled
		Project projectWithBudgetEnabled =
			newProject(user.getId(), clientCompany.getId(), "Project With Budget Enabled");
		projectWithBudgetEnabled.setBudgetEnabledFlag(true);
		projectWithBudgetEnabled.setBudget(new BigDecimal(DEFAULT_BUDGET_AMOUNT));
		projectWithBudgetEnabled.setRemainingBudget(new BigDecimal(DEFAULT_BUDGET_AMOUNT));
		projectService.saveOrUpdate(projectWithBudgetEnabled);
		return projectWithBudgetEnabled;
	}

	private Project createProjectWithoutBudgetEnabled (User user) throws Exception {
		ClientCompany clientCompany = newClientCompany(user.getId());
		// Create a project without budget funds enabled
		Project projectWithoutBudgetEnabled =
			newProject(user.getId(), clientCompany.getId(), "Project Without Budget Enabled");
		projectService.saveOrUpdate(projectWithoutBudgetEnabled);
		return projectWithoutBudgetEnabled;

	}

	private WorkNegotiationDTO createNegotiation() {
		Calendar reschedule = DateUtilities.newCalendar(2013, 8, 2, 9, 0, 0);
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(WORK_NEGOTIATION_RESOURCE_FLAT_FEE);
		dto.setScheduleNegotiation(true);
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DateUtilities.getISO8601(reschedule));
		dto.setNote("note comment");
		dto.setDeclinedNote("declined note comment");
		return dto;
	}

	private WorkNegotiationDTO createExceedBudgetNegotiation() {
		Calendar reschedule = DateUtilities.newCalendar(2013, 8, 2, 9, 0, 0);
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(WORK_NEGOTIATION_FLAT_FEE_EXCEED_BUDGET);
		dto.setScheduleNegotiation(true);
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DateUtilities.getISO8601(reschedule));
		dto.setNote("note");
		dto.setDeclinedNote("note");
		return dto;
	}

	private Work createWork(User user) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!");
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(WORK_FLAT_FEE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2010-09-02T09:00:00Z");
		workDTO.setIvrActive(true);
		workDTO.setCheckinCallRequired(true);
		Work work = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);
		Assert.assertNotNull(work);
		return work;
	}

	private Work createOverBudgetWork(User user) {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!");
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(WORK_FLAT_FEE_EXCEED_BUDGET);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2010-09-02T09:00:00Z");
		workDTO.setIvrActive(true);
		workDTO.setCheckinCallRequired(true);
		Work work = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);
		Assert.assertNotNull(work);
		return work;
	}

	private Work createWorkInProject(User user, Long projectId) {
		Work work = createWork(user);
		projectService.addWorkToProject(work.getId(), projectId);
		Assert.assertNotNull(work);
		return work;
	}

	private Work createOverBudgetWorkInProject(User user, Long projectId) {
		Work work = createOverBudgetWork(user);
		projectService.addWorkToProject(work.getId(), projectId);
		Assert.assertNotNull(work);
		return work;
	}
}
