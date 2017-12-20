package com.workmarket.domains.groups.service;

import com.google.common.collect.Sets;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupLastRoutedDTO;
import com.workmarket.domains.groups.model.UserGroupThroughputDTO;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.DeliveryStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.summary.group.UserGroupSummary;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.service.WorkHistorySummaryService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.TimeDimensionService;
import com.workmarket.service.business.UserGroupSummaryService;
import com.workmarket.service.business.UserUserGroupAssociationService;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.test.IntegrationTest;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class GroupSummaryIT extends UserGroupBaseIT {

	@Autowired WorkResourceService workResourceService;
	@Autowired TimeDimensionService timeDimensionService;
	@Autowired WorkHistorySummaryService workHistorySummaryService;
	@Autowired UserGroupSummaryService userGroupSummaryService;
	@Autowired UserUserGroupAssociationService userUserGroupAssociationService;

	@Test
	public void findAllWithNewLastRoutedSinceLastUpdate_queryNewGroup_groupIsNotInResults() throws Exception {
		buyer = newEmployeeWithCashBalance();
		group1 = newCompanyUserGroupOpenActive(buyer.getCompany().getId(), buyer.getId());

		List<UserGroupLastRoutedDTO> userGroups = userGroupService.findAllWithNewLastRoutedSinceLastUpdate();
		List<Long> newLastRoutedGroupIds = extract(userGroups, on(UserGroupLastRoutedDTO.class).getUserGroupId());
		assertFalse(newLastRoutedGroupIds.contains(group1.getId()));
	}

	@Test
	public void findAllWorkPriceSumsSinceLastUpdate_returnNotNull() throws Exception {
		List<UserGroupThroughputDTO> userGroupWithSums = userGroupService.calculateThroughputSinceLastUpdate();
		assertNotNull(userGroupWithSums);
	}

	@Test
	public void findAllWorkPriceSumsSinceLastUpdate_routeWorkToGroup_returnValues() throws Exception {
		buyer = newEmployeeWithCashBalance();
		group1 = newCompanyUserGroupOpenActive(buyer.getCompany().getId(), buyer.getId());
		worker = newContractorIndependentlane4Ready();

		createSummaryForUserGroup();
		addWorkerToGroup();

		work = newWork(buyer.getId());
		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		routeWorkToGroup();
		closeWork();

		work = workService.findWork(work.getId());
		assertEquals(WorkStatusType.PAID, work.getWorkStatusType().getCode());

		List<UserGroupThroughputDTO> userGroupWithSums = userGroupService.calculateThroughputSinceLastUpdate();
		assertFalse(userGroupWithSums.isEmpty());
		List<Long> groupIds = extract(userGroupWithSums, on(UserGroupThroughputDTO.class).getUserGroupId());
		assertTrue(groupIds.contains(group1.getId()));
	}

	private void createSummaryForUserGroup() {
		UserGroupSummary userGroupSummary = new UserGroupSummary();
		userGroupSummary.setUserGroup(group1);
		userGroupSummaryService.saveOrUpdate(userGroupSummary); // create summary for group
	}

	private void addWorkerToGroup() {
		UserUserGroupAssociation userUserGroupAssociation = new UserUserGroupAssociation();
		userUserGroupAssociation.setUser(worker);
		userUserGroupAssociation.setUserGroup(group1);
		userUserGroupAssociationService.saveOrUpdateAssociation(userUserGroupAssociation);
	}

	private void routeWorkToGroup() {
		AbstractRoutingStrategy strategy = routingStrategyService.addGroupIdsRoutingStrategy(work.getId(), Sets.newHashSet(group1.getId()), 0, false);
		strategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SENT)); // override default of SCHEDULED
		routingStrategyService.saveOrUpdateRoutingStrategy(strategy);
	}

	private void closeWork() throws WorkNotFoundException {
		laneService.addUserToCompanyLane2(worker.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), worker.getId());
		workService.acceptWork(worker.getId(), work.getId());
		CompleteWorkDTO dto = new CompleteWorkDTO();
		dto.setResolution("Resolved");
		workService.completeWork(work.getId(), dto);

		authenticationService.setCurrentUser(buyer.getId());
		CloseWorkDTO closeWorkDTO = new CloseWorkDTO();
		workService.closeWork(work.getId(), closeWorkDTO);
	}

}
