package com.workmarket.domains.groups.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.domains.groups.service.association.UserGroupAssociationService;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserGroupAssociationServiceIT extends BaseServiceIT {

	@Autowired UserGroupAssociationService userGroupAssociationService;

	/**
	 * All these tests are meant to test the HQL behind the DAO
	 *
	 */
	@Test
	public void findAllPendingAssociationsWithCertification() throws Exception {
		assertNotNull(userGroupAssociationService.findAllPendingAssociationsWithCertification(1L, 1234L));
	}

	@Test
	public void findAllPendingAssociationsWithLicense() throws Exception {
		assertNotNull(userGroupAssociationService.findAllPendingAssociationsWithLicense(1L, 1234L));
	}

	@Test
	public void findAllPendingAssociationsWithIndustry() throws Exception {
		assertNotNull(userGroupAssociationService.findAllPendingAssociationsWithIndustry(1L));
	}

	@Test
	public void findAllPendingAssociationsWithInsurance() throws Exception {
		assertNotNull(userGroupAssociationService.findAllPendingAssociationsWithInsurance(1L, 1234L));
	}

	@Test
	public void findAllPendingAssociationsWithDrugTest() throws Exception {
		assertNotNull(userGroupAssociationService.findAllPendingAssociationsWithDrugTest(1L));
	}

	@Test
	public void findAllPendingAssociationsWithBackgroundCheck() throws Exception {
		assertNotNull(userGroupAssociationService.findAllPendingAssociationsWithBackgroundCheck(1L));
	}

	@Test
	public void findUserGroupAssociationByUserIdAndGroupId() throws Exception {
		User user = newWMEmployee();
		UserGroup group = newPublicUserGroup(user);
		User worker = newContractor();
		userGroupService.applyToGroup(group.getId(), worker.getId());
		assertNotNull(userGroupAssociationService.findUserGroupAssociationByUserIdAndGroupId(worker.getId(), group.getId()));
	}

	@Test
	public void findAllPendingAssociationsWithAssessment() throws Exception {
		assertNotNull(userGroupAssociationService.findAllPendingAssociationsWithAssessment(1L, 1234L));
	}

	@Test
	public void findAllPendingAssociationsWithLaneRequirement() throws Exception {
		assertNotNull(userGroupAssociationService.findAllPendingAssociationsWithLaneRequirement(1L));
	}

	@Test
	public void findAllPendingAssociationsWithWorkingHours() throws Exception {
		assertNotNull(userGroupAssociationService.findAllPendingAssociationsWithWorkingHours(1L));
	}

	@Test
	public void findAllPendingAssociationsWithLocationRequirements() throws Exception {
		assertNotNull(userGroupAssociationService.findAllPendingAssociationsWithLocationRequirements(1L));
	}

	@Test
	public void findAllPendingAssociationsWithRating() throws Exception {
		assertNotNull(userGroupAssociationService.findAllPendingAssociationsWithRating(1L));
	}

	@Test
	public void findAllUserGroupAssociationsByUserId() throws Exception {
		User user = newWMEmployee();
		UserGroup group = newPublicUserGroup(user);
		User worker = newContractor();
		userGroupService.applyToGroup(group.getId(), worker.getId());
		assertNotNull(userGroupAssociationService.findAllUserGroupAssociationsByUserId(worker.getId()));
	}
}
