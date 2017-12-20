package com.workmarket.domains.groups.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupPagination;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.dto.UserGroupDTO;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.TagService;
import com.workmarket.service.business.dto.CreateNewWorkerRequest;
import com.workmarket.service.network.NetworkService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserGroupServiceIT extends UserGroupBaseIT {

	@Autowired TagService tagService;
	@Autowired ProfileService profileService;
	@Autowired NetworkService networkService;
	@Autowired UserGroupRequirementSetService requirementService;

	@Test
	public void saveOrUpdateCompanyUserGroup_createGroupSaveNewChanges_groupReflectsNewChanges() throws Exception {
		group1 = generateUserGroup(COMPANY_ID);
		group1 = userGroupService.findGroupById(group1.getId());
		assertFalse(group1.getRequiresApproval());

		group1 = saveOrUpdateGroup(group1.getId(), COMPANY_ID, true, true, FRONT_END_USER_ID);
		assertTrue(group1.getRequiresApproval());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveOrUpdateCompanyUserGroup_changeGroupMembershipType_throwException() throws Exception {
		UserGroupDTO dto = new UserGroupDTO();
		dto.setCompanyId(COMPANY_ID);
		dto.setName("Group " + RandomUtilities.nextLong());
		dto.setDescription("Description");
		dto.setOpenMembership(true);
		dto.setRequiresApproval(false);
		dto.setOwnerId(ANONYMOUS_USER_ID);

		group1 = userGroupService.saveOrUpdateCompanyUserGroup(dto);
		dto.setUserGroupId(group1.getId());
		dto.setOpenMembership(false);
		userGroupService.saveOrUpdateCompanyUserGroup(dto);
	}

	@Test
	public void findAllGroupsByCompanyId_returnNonEmptyResults() throws Exception {
		UserGroupPagination userGroupPagination = new UserGroupPagination();
		userGroupPagination.setResultsLimit(25);
		userGroupPagination.setStartRow(0);
		userGroupPagination = userGroupService.findAllGroupsByCompanyId(COMPANY_ID, userGroupPagination);

		assertNotNull(userGroupPagination);
		assertTrue(userGroupPagination.getRowCount() > 0);

		// test lazy loading
		for (UserGroup company : userGroupPagination.getResults()) {
			assertNotNull(company.getCreatedOn());
		}
	}

	@Test
	public void findCompanyGroupsOpenMembership_companyHasNoGroups_returnEmptyResults() throws Exception {
		ManagedCompanyUserGroupRowPagination userGroupPagination = new ManagedCompanyUserGroupRowPagination();
		buyer = newEmployeeWithCashBalance();
		userGroupPagination = userGroupService.findCompanyGroupsOpenMembership(buyer.getCompany().getId(), userGroupPagination);

		userGroupService.findCompanyActiveGroups(buyer.getId(), userGroupPagination);
		assertEquals(0, userGroupPagination.getResults().size());
	}

	@Test
	public void findCompanyGroupsOpenMembership_companyHasOneGroup_returnOneResult() throws Exception {
		ManagedCompanyUserGroupRowPagination userGroupPagination = new ManagedCompanyUserGroupRowPagination();
		buyer = newEmployeeWithCashBalance();
		generateUserGroup(buyer.getCompany().getId());
		userGroupPagination = userGroupService.findCompanyGroupsOpenMembership(buyer.getCompany().getId(), userGroupPagination);

		assertEquals(1, userGroupPagination.getResults().size());
	}

	@Test
	public void findGroupsOpenMembershipCompanyName_noGroups_success() {
		ManagedCompanyUserGroupRowPagination pagination = userGroupService.findGroupsActiveOpenMembershipByGroupIds(Sets.<Long>newHashSet(), new ManagedCompanyUserGroupRowPagination());
		List<ManagedCompanyUserGroupRow> results = pagination.getResults();
		assertNotNull(results);
		assertEquals(results.size(), 0);
	}

	@Test
	public void findGroupsOpenMembershipCompanyName_oneGroup_success() throws Exception {
		buyer = newEmployeeWithCashBalance();
		group1 = generateUserGroup(
			buyer.getCompany().getId(), REQUIRES_APPROVAL, OPEN_MEMBERSHIP, buyer.getId()
		);

		ManagedCompanyUserGroupRowPagination pagination = userGroupService.findGroupsActiveOpenMembershipByGroupIds(Sets.newHashSet(group1.getId()), new ManagedCompanyUserGroupRowPagination());
		List<ManagedCompanyUserGroupRow> results = pagination.getResults();
		assertNotNull(results);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0).getGroupId(), group1.getId());
	}

	@Test
	public void findAllUserGroupIds_createGroupThenDeleteGroup_groupIsNotList() throws Exception {
		buyer = newEmployeeWithCashBalance();

		// create new group
		group1 = generateUserGroup(
			buyer.getCompany().getId(), REQUIRES_APPROVAL, OPEN_MEMBERSHIP, buyer.getId()
		);

		// confirm group is in the list
		List<Long> groupIds = userGroupService.findAllUserGroupIds();
		assertTrue(groupIds.contains(group1.getId()));

		// mark it deleted
		userGroupService.deleteGroup(group1.getId());

		// confirm the group is not in the list
		groupIds = userGroupService.findAllUserGroupIds();
		assertFalse(groupIds.contains(group1.getId()));
	}

	@Test
	public void applyOnBehalfOfUsers_requiresApproval_noOverride_userIsPending() throws Exception {
		buyer = newEmployeeWithCashBalance();

		group1 = generateUserGroup(
			buyer.getCompany().getId(), REQUIRES_APPROVAL, OPEN_MEMBERSHIP, buyer.getId()
		);

		final RecruitingCampaign recruitingCampaign = newRecruitingCampaign(buyer.getCompany().getId(), group1.getId());

		User user = registrationService.registerNew(generateNewWorkerRequestBuilder()
			.setRecruitingCampaignId(recruitingCampaign.getId())
			.build());

		Map<String, List<String>> results = userGroupService.applyOnBehalfOfUsers(ImmutableList.of(user.getUserNumber()), group1.getId(), buyer.getId(), false, false);

		assertEquals(results.get("success").get(0), user.getUserNumber());
		assertEquals(userGroupService.findAllActiveAssociations(group1.getId()).get(0).getUser().getId(), user.getId());
		assertEquals(userGroupService.findAllActiveAssociations(group1.getId()).get(0).getApprovalStatus(), ApprovalStatus.PENDING);
	}

	@Test
	public void applyOnBehalfOfUsers_requiresApproval_withOverride_userIsApproved() throws Exception {
		buyer = newEmployeeWithCashBalance();

		group1 = generateUserGroup(
			buyer.getCompany().getId(), REQUIRES_APPROVAL, OPEN_MEMBERSHIP, buyer.getId()
		);

		final RecruitingCampaign recruitingCampaign = newRecruitingCampaign(buyer.getCompany().getId(), group1.getId());

		User user = registrationService.registerNew(generateNewWorkerRequestBuilder()
			.setRecruitingCampaignId(recruitingCampaign.getId())
			.build());

		Map<String, List<String>> results = userGroupService.applyOnBehalfOfUsers(ImmutableList.of(user.getUserNumber()), group1.getId(), buyer.getId(), false, true);

		assertEquals(results.get("success").get(0), user.getUserNumber());
		assertEquals(userGroupService.findAllActiveAssociations(group1.getId()).get(0).getUser().getId(), user.getId());
		assertEquals(userGroupService.findAllActiveAssociations(group1.getId()).get(0).getApprovalStatus(), ApprovalStatus.APPROVED);
	}

	@Test
	public void applyOnBehalfOfUsers_requiresApproval_andBackgroundCheck_withOverride_userIsApproved() throws Exception {
		buyer = newEmployeeWithCashBalance();

		group1 = generateUserGroup(
			buyer.getCompany().getId(), REQUIRES_APPROVAL, OPEN_MEMBERSHIP, buyer.getId()
		);

		requirementService.addBackgroundCheckRequirement(group1.getId());
		
		final RecruitingCampaign recruitingCampaign = newRecruitingCampaign(buyer.getCompany().getId(), group1.getId());

		User user = registrationService.registerNew(generateNewWorkerRequestBuilder()
			.setRecruitingCampaignId(recruitingCampaign.getId())
			.build());

		Map<String, List<String>> results = userGroupService.applyOnBehalfOfUsers(ImmutableList.of(user.getUserNumber()), group1.getId(), buyer.getId(), false, true);

		assertEquals(results.get("success").get(0), user.getUserNumber());
		assertEquals(userGroupService.findAllActiveAssociations(group1.getId()).get(0).getUser().getId(), user.getId());
		assertEquals(userGroupService.findAllActiveAssociations(group1.getId()).get(0).getApprovalStatus(), ApprovalStatus.APPROVED);
	}

	@Test
	public void applyOnBehalfOfUsers_noManualApproval_andBackgroundCheck_withOverride_userIsApproved() throws Exception {
		buyer = newEmployeeWithCashBalance();

		group1 = generateUserGroup(
			buyer.getCompany().getId(), DOES_NOT_REQUIRE_APPROVAL, OPEN_MEMBERSHIP, buyer.getId()
		);

		requirementService.addBackgroundCheckRequirement(group1.getId());

		final RecruitingCampaign recruitingCampaign = newRecruitingCampaign(buyer.getCompany().getId(), group1.getId());

		User user = registrationService.registerNew(generateNewWorkerRequestBuilder()
			.setRecruitingCampaignId(recruitingCampaign.getId())
			.build());

		Map<String, List<String>> results = userGroupService.applyOnBehalfOfUsers(ImmutableList.of(user.getUserNumber()), group1.getId(), buyer.getId(), false, true);

		assertEquals(results.get("success").get(0), user.getUserNumber());
		assertEquals(userGroupService.findAllActiveAssociations(group1.getId()).get(0).getUser().getId(), user.getId());
		assertEquals(userGroupService.findAllActiveAssociations(group1.getId()).get(0).getApprovalStatus(), ApprovalStatus.APPROVED);
	}

	@Test
	public void applyOnBehalfOfUsers_noManualApproval_andBackgroundCheck_noOverride_userIsApproved() throws Exception {
		buyer = newEmployeeWithCashBalance();

		group1 = generateUserGroup(
			buyer.getCompany().getId(), DOES_NOT_REQUIRE_APPROVAL, OPEN_MEMBERSHIP, buyer.getId()
		);

		requirementService.addBackgroundCheckRequirement(group1.getId());

		final RecruitingCampaign recruitingCampaign = newRecruitingCampaign(buyer.getCompany().getId(), group1.getId());

		User user = registrationService.registerNew(generateNewWorkerRequestBuilder()
			.setRecruitingCampaignId(recruitingCampaign.getId())
			.build());

		Map<String, List<String>> results = userGroupService.applyOnBehalfOfUsers(ImmutableList.of(user.getUserNumber()), group1.getId(), buyer.getId(), false, false);

		assertEquals(results.get("success").get(0), user.getUserNumber());
		assertEquals(userGroupService.findAllActiveAssociations(group1.getId()).get(0).getUser().getId(), user.getId());
		assertEquals(userGroupService.findAllActiveAssociations(group1.getId()).get(0).getApprovalStatus(), ApprovalStatus.PENDING);
	}

	@Test
	public void applyOnBehalfOfUsers_nonActiveWorker_noLane2_fails() throws Exception {
		buyer = newEmployeeWithCashBalance();

		group1 = generateUserGroup(
			buyer.getCompany().getId(), DOES_NOT_REQUIRE_APPROVAL, OPEN_MEMBERSHIP, buyer.getId()
		);

		User user = registrationService.registerNew(generateNewWorkerRequestBuilder()
			.build());

		Map<String, List<String>> results = userGroupService.applyOnBehalfOfUsers(ImmutableList.of(user.getUserNumber()), group1.getId(), buyer.getId(), false, false);

		assertEquals(results.get("failure").get(0), user.getUserNumber());
		assertEquals(userGroupService.findAllActiveAssociations(group1.getId()).size(), 0);
	}

	private CreateNewWorkerRequest.CreateNewWorkerDTOBuilder generateNewWorkerRequestBuilder() {
		return CreateNewWorkerRequest.builder()
			.setFirstName(RandomUtilities.generateNumericString(10))
			.setLastName(RandomUtilities.generateNumericString(10))
			.setEmail(RandomUtilities.generateNumericString(10) + "@workmarket.com")
			.setPassword("workerPassword" + RandomUtilities.generateNumericString(10))
			.setAddress1("240 W 27th St")
			.setCountry("USA")
			.setCity("New York")
			.setPostalCode("10018")
			.setState("NY")
			.setLatitude(new BigDecimal(0.01))
			.setLongitude(new BigDecimal(1.01));
	}

}

