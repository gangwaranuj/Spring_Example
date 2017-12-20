package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.dto.CompanyResource;
import com.workmarket.dto.CompanyResourcePagination;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class LaneServiceIT extends BaseServiceIT {

	@Autowired private LaneService laneService;

	@Test
	public void findAllEmployeesByCompany() throws Exception {
		CompanyResourcePagination pagination = new CompanyResourcePagination();
		pagination.setResultsLimit(10);
		pagination = laneService.findAllEmployeesByCompany(COMPANY_ID, pagination);
		Assert.assertNotNull(pagination);

		for (CompanyResource resource : pagination.getResults()) {
			Assert.assertNotNull(resource.getRolesString());
			Assert.assertNotNull(resource.getFirstName());
			Assert.assertNotNull(resource.getLastName());
		}
	}

	@Test
	public void findAllContractorsByCompany() throws Exception {
		CompanyResourcePagination pagination = new CompanyResourcePagination();
		pagination.setResultsLimit(10);
		pagination = laneService.findAllContractorsByCompany(COMPANY_ID, pagination);
		Assert.assertNotNull(pagination);

		assertTrue(pagination.getResults().size() >= 10);

		for (CompanyResource resource : pagination.getResults()) {
			Assert.assertNotNull(resource.getCompanyName());
			Assert.assertNotNull(resource.getFirstName());
			Assert.assertNotNull(resource.getLastName());
			Assert.assertNotNull(resource.getYTDWork());
			Assert.assertNotNull(resource.getYTDPayments());
		}
	}

	@Test
	public void removeUserFromCompanyLane() throws Exception {
		User employee = newWMEmployee();
		User contractor = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		LaneAssociation lane = laneService.findActiveAssociationByUserIdAndCompanyId(contractor.getId(), employee.getCompany().getId());

		Assert.assertNotNull(lane);
		assertTrue(lane.getLaneType().ordinal() == 2);
		laneService.removeUserFromCompanyLane(contractor.getId(), employee.getCompany().getId());

		lane = laneService.findActiveAssociationByUserIdAndCompanyId(contractor.getId(), employee.getCompany().getId());
		Assert.assertNull(lane);
	}

	@Test
	public void updateUserCompanyLaneAssociation() throws Exception {
		User employee = newWMEmployee();
		User contractor = newContractorIndependent();

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		LaneAssociation lane = laneService.findActiveAssociationByUserIdAndCompanyId(contractor.getId(), employee.getCompany().getId());

		Assert.assertNotNull(lane);
		assertTrue(lane.getLaneType().ordinal() == 2);
		laneService.updateUserCompanyLaneAssociation(contractor.getId(), employee.getCompany().getId(), LaneType.LANE_3);
		lane = laneService.findActiveAssociationByUserIdAndCompanyId(contractor.getId(), employee.getCompany().getId());

		Assert.assertNotNull(lane);
		assertTrue(lane.getLaneType().ordinal() == 3);
		assertFalse(lane.getDeleted());
		lane = laneService.findAssociationByUserIdAndCompanyId(contractor.getId(), employee.getCompany().getId(), LaneType.LANE_2);
		Assert.assertNull(lane);
	}

	@Test
	public void findAllCompaniesWhereUserIsResource() throws Exception {
		User employee = newEmployeeWithCashBalance();

		laneService.addUserToCompanyLane2(ANONYMOUS_USER_ID, employee.getCompany().getId());
		assertTrue(laneService.findAllCompaniesWhereUserIsResource(ANONYMOUS_USER_ID, LaneType.LANE_2).contains(employee.getCompany().getId()));
		assertFalse(laneService.findAllCompaniesWhereUserIsResource(ANONYMOUS_USER_ID, LaneType.LANE_2).contains(COMPANY_ID));
	}

	@Test
	public void addUserToWorkerPool_usersAdded() throws Exception {
		User employee = newWMEmployee();
		User contractor = newContractorIndependentlane4Ready();
		assertFalse(laneService.isUserPartOfLane123(contractor.getId(), employee.getCompany().getId()));
		laneService.addUserToWorkerPool(employee.getCompany().getId(), employee.getUserNumber(), contractor.getUserNumber());
		assertTrue(laneService.isUserPartOfLane123(contractor.getId(), employee.getCompany().getId()));
	}

	@Test
	public void getLaneContextForUserAndCompany_lane4_lane2() throws Exception {
		User employee = newWMEmployee();
		User contractor = newContractorIndependentlane4Ready();
		assertEquals(LaneType.LANE_4, laneService.getLaneContextForUserAndCompany(contractor.getId(), employee.getCompany().getId()).getLaneType());
		laneService.addUsersToCompanyLane2(Lists.newArrayList(contractor.getId()), employee.getCompany().getId());
		assertEquals(LaneType.LANE_2, laneService.getLaneContextForUserAndCompany(contractor.getId(), employee.getCompany().getId()).getLaneType());
		userService.suspendUser(contractor.getId(), true);
		assertNull(laneService.getLaneContextForUserAndCompany(contractor.getId(), employee.getCompany().getId()));
	}

	@Test
	public void getLaneContextForUserAndCompany_lane0_lane1() throws Exception {
		User employee = newWMEmployee();
		assertEquals(LaneType.LANE_0, laneService.getLaneContextForUserAndCompany(employee.getId(), employee.getCompany().getId()).getLaneType());
		assertTrue(laneService.getLaneContextForUserAndCompany(employee.getId(), employee.getCompany().getId()).getLaneType().isEmployeeLane());
		authenticationService.assignAclRoleToUser(employee.getId(), ACL_ROLE_WORKER);
		assertEquals(LaneType.LANE_1, laneService.getLaneContextForUserAndCompany(employee.getId(), employee.getCompany().getId()).getLaneType());
		authenticationService.removeAclRoleFromUser(employee.getId(), ACL_ROLE_WORKER);
		assertEquals(LaneType.LANE_0, laneService.getLaneContextForUserAndCompany(employee.getId(), employee.getCompany().getId()).getLaneType());
	}

}
