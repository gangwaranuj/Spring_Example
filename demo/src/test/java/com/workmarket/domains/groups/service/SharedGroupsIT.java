package com.workmarket.domains.groups.service;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.network.Network;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class SharedGroupsIT extends UserGroupBaseIT {

	@Test
	public void findSharedAndOwnedGroups_success() throws Exception {
		// Buyer 1 group
		User buyer1 = newEmployeeWithCashBalance();
		group1 = generateUserGroup(
			buyer1.getCompany().getId(), REQUIRES_APPROVAL, OPEN_MEMBERSHIP, buyer1.getId()
		);

		// Buyer 2 group
		User buyer2 = newEmployeeWithCashBalance();
		group2 = generateUserGroup(
			buyer2.getCompany().getId(), REQUIRES_APPROVAL, OPEN_MEMBERSHIP, buyer2.getId()
		);

		// Add both companies to network
		Network network = createNetwork();
		networkService.addCompanyToNetworkWithRole(buyer1.getCompany().getId(), network.getId(), AclRole.ACL_NETWORK_READ);
		networkService.addCompanyToNetworkWithRole(buyer2.getCompany().getId(), network.getId(), AclRole.ACL_NETWORK_READ);

		// Add buyer 2's group to network
		networkService.addGroupToNetwork(group2.getId(), network.getId());

		// Buyer 1 has access to both groups
		List<ManagedCompanyUserGroupRow> groups = userGroupService.findSharedAndOwnedGroups(buyer1.getCompany().getId());
		assertEquals(groups.size(), 2);
		assertFalse(groups.get(0).getCompanyId().equals(groups.get(1).getCompanyId()));

		// Test the paginated query too
		ManagedCompanyUserGroupRowPagination pagination = userGroupService.findSharedAndOwnedGroups(buyer1.getId(), buyer1.getCompany().getId(), new ManagedCompanyUserGroupRowPagination());
		assertEquals(pagination.getRowCount().intValue(), 2);

		assertFalse(groups.get(0).getCompanyId().equals(groups.get(1).getCompanyId()));

		List<ManagedCompanyUserGroupRow> results = pagination.getResults();
		assertFalse(results.get(0).getCompanyId().equals(results.get(1).getCompanyId()));

		for (ManagedCompanyUserGroupRow group : results) {
			if (group.getGroupId().equals(group1.getId())) {
				assertFalse(group.isShared());
			} else {
				// Assert group2 is shared by me (in context of buyer2) and shared with me (in context of buyer1)
				assertTrue(group.isShared());
				assertTrue(group.isSharedByMe(buyer2.getCompany().getId()) && group.isSharedWithMe(buyer1.getCompany().getId()));
			}
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findMyGroupMemberships_workerContext_onlyRetrievePublicGroups() throws Exception {
		prepareDataForFindMyGroupMemberships();

		ManagedCompanyUserGroupRowPagination pagination = userGroupService.findMyGroupMemberships(worker.getId(), new ManagedCompanyUserGroupRowPagination());

		// Worker should see 3 groups, the 3 public ones (group1, group3, group4)
		assertEquals(pagination.getResults().size(), 3);
		for (ManagedCompanyUserGroupRow group : pagination.getResults()) {
			assertThat(group.getGroupId(), anyOf(is(group1.getId()), is(group3.getId()), is(group4.getId())));
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findMyGroupMemberships_groupShareeContext_includeSharedGroup() throws Exception {
		prepareDataForFindMyGroupMemberships();

		ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination();
		pagination.setShowSharedGroups(true);
		pagination.setShowPrivateGroups(true);
		pagination.setCurrentUserCompanyId(buyer.getCompany().getId());
		pagination = userGroupService.findMyGroupMemberships(worker.getId(), pagination);

		// The company in our network should see 2 groups, the one they own and the one we shared with them (group3 and group4)
		assertEquals(pagination.getResults().size(), 2);
		for (ManagedCompanyUserGroupRow group : pagination.getResults()) {
			// Assert that group3 is shared with the company
			if (group.getGroupId().equals(group3.getId())) {
				assertTrue(group.isSharedWithMe(buyer.getCompany().getId()));
			}
			assertThat(group.getGroupId(), anyOf(is(group3.getId()), is(group4.getId())));
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findMyGroupMemberships_groupOwnerContext_retrieveAllGroups() throws Exception {
		prepareDataForFindMyGroupMemberships();

		ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination();
		pagination.setShowSharedGroups(true);
		pagination.setShowPrivateGroups(true);
		pagination.setCurrentUserCompanyId(Constants.WM_COMPANY_ID);
		pagination = userGroupService.findMyGroupMemberships(worker.getId(), pagination);

		// We should see all 3 of our groups (group1, group2, group3)
		assertEquals(pagination.getResults().size(), 3);
		for (ManagedCompanyUserGroupRow group : pagination.getResults()) {
			// Assert that group3 is shared by us
			if (group.getGroupId().equals(group3.getId())) {
				assertTrue(group.isSharedByMe(Constants.WM_COMPANY_ID));
			}
			assertThat(group.getGroupId(), anyOf(is(group1.getId()), is(group2.getId()), is(group3.getId())));
		}
	}

	/**
	 * For tests testing a worker's group memberships. Different contexts produce different results.
	 *
	 * @throws Exception
	 */
	public void prepareDataForFindMyGroupMemberships() throws Exception {
		worker = newContractorIndependentlane4Ready();

		// Group 1 (not shared, public)
		group1 = generateUserGroup(
			Constants.WM_COMPANY_ID, REQUIRES_APPROVAL, OPEN_MEMBERSHIP, Constants.JEFF_WALD_USER_ID
		);
		userGroupService.applyToGroup(group1.getId(), worker.getId());
		userGroupService.approveUser(group1.getId(), worker.getId());

		// Group 2 (not shared, private)
		group2 = generateUserGroup(
			Constants.WM_COMPANY_ID, DOES_NOT_REQUIRE_APPROVAL, CLOSED_MEMBERSHIP, Constants.JEFF_WALD_USER_ID
		);
		userGroupService.buildUserUserGroupAssociation(worker.getId(), group2, Collections.<UserGroupInvitation>emptyList());

		// Group 3 (shared, public)
		group3 = generateUserGroup(
			Constants.WM_COMPANY_ID, REQUIRES_APPROVAL, OPEN_MEMBERSHIP, Constants.JEFF_WALD_USER_ID
		);
		userGroupService.applyToGroup(group3.getId(), worker.getId());
		userGroupService.approveUser(group3.getId(), worker.getId());

		// Add group3 to our network
		networkService.addGroupToNetwork(group3.getId(), Constants.WORKMARKET_NETWORK_ID);

		// Add another company to our network
		buyer = newFirstEmployee();
		networkService.addCompanyToNetworkWithRole(buyer.getCompany().getId(), Constants.WORKMARKET_NETWORK_ID, AclRole.ACL_NETWORK_READ);

		// They have a group too (not shared, public)
		group4 = generateUserGroup(
			buyer.getCompany().getId(), REQUIRES_APPROVAL, OPEN_MEMBERSHIP, buyer.getId()
		);
		userGroupService.applyToGroup(group4.getId(), worker.getId());
		userGroupService.approveUser(group4.getId(), worker.getId());
	}
}
