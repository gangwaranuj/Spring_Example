package com.workmarket.service.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.dao.acl.AclRoleDAO;
import com.workmarket.dao.network.CompanyNetworkAssociationDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclNetworkRoleAssociation;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.network.Network;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.test.IntegrationTest;
import groovy.lang.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.workmarket.utility.CollectionUtilities.containsAll;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class NetworkServiceImplIT extends BaseServiceIT {

	@Autowired AclNetworkRoleAssociationService aclNetworkRoleAssociationService;
	@Autowired AclRoleDAO aclRoleDAO;
	@Autowired CompanyNetworkAssociationDAO companyNetworkAssociationDAO;
	@Autowired UserGroupService userGroupService;
	@Autowired @Qualifier("redisCacheOnly") RedisAdapter redisAdapter;

	@Test
	public void getActiveUsersInNetworkById_withNewNetwork_returnNoUsers() throws Exception {
		Network network = createNetwork();
		Set<User> emptyNetwork = networkService.getActiveUsersInNetworkById(network.getId());
		assertTrue(emptyNetwork.isEmpty());
	}

	@Test
	public void addWorkerToNetwork_addWorker_networkHasOneWorker() throws Exception {
		Network network = createNetwork();
		User worker = newContractor();

		networkService.addWorkerToNetwork(worker.getId(), network.getId());
		Set<User> networkWithAddedUser = networkService.getActiveUsersInNetworkById(network.getId());
		assertEquals(1, networkWithAddedUser.size());
	}

	@Test
	public void addWorkerToNetwork_addThenRemoveWorker_networkHasNoWorkers() throws Exception {
		Network network = createNetwork();
		User worker = newContractor();

		networkService.addWorkerToNetwork(worker.getId(), network.getId());
		networkService.removeWorkerFromNetwork(worker.getId(), network.getId());
		Set<User> updatedEmptyNetwork = networkService.getActiveUsersInNetworkById(network.getId());
		assertTrue(updatedEmptyNetwork.isEmpty());
	}

	@Test
	public void addWorkerToAllCompanyNetworks_removeUserFromCompanyNetworks_success() throws Exception {
		Network network = createNetwork();
		User worker = newContractor();
		Company company = newCompany();
		networkService.addCompanyToNetworkWithRole(company.getId(), network.getId(), AclRole.ACL_NETWORK_OWNER);

		networkService.addWorkerToAllCompanyNetworks(worker.getId(), company.getId());
		List<Long> companyNetworkIds = networkService.findAllCompanyNetworkIds(company.getId());

		for (Long networkId : companyNetworkIds) {
			Set<User> activeUserNetwork = networkService.getActiveUsersInNetworkById(networkId);
			assertTrue(!activeUserNetwork.isEmpty());
		}

		networkService.removeUserFromCompanyNetworks(worker.getId(), company.getId());
		Set<User> updatedEmptyNetwork = networkService.getActiveUsersInNetworkById(network.getId());
		assertTrue(updatedEmptyNetwork.isEmpty());
	}

	@Test
	public void addGroupToNetwork_success() throws Exception {
		Network network = createNetwork();
		Company company = newCompany();
		UserGroup group = newCompanyUserGroup(company.getId());

		networkService.addGroupToNetwork(group.getId(), network.getId());
		assertTrue(networkService.isGroupInNetwork(group.getId(), network.getId()));
	}

	@Test
	public void newNetworkAndCompany_addCompanyToNetwork_shouldHaveAssociation() throws Exception {
		Network network = createNetwork();
		Company company = newCompany();
		networkService.addCompanyToNetworkWithRole(company.getId(), network.getId(), AclRole.ACL_NETWORK_OWNER);

		List<Long> networkIds = networkService.findAllCompanyNetworkIds(company.getId());
		assertTrue(networkIds.contains(network.getId()));
	}

	@Test
	public void newGroupAndNetwork_getGroupsInNetwork_shouldHaveAssociation() throws Exception {
		Network network = createNetwork();
		Company company = newCompany();
		UserGroup group = newCompanyUserGroup(company.getId());

		networkService.addGroupToNetwork(group.getId(), network.getId());
		Set<UserGroup> foundGroups = networkService.getGroupsInNetwork(network.getId());
		assertTrue(foundGroups.contains(group));
	}

	@Test
	public void newCompany_addAclNetworkOwnerRole_shouldHaveRole() throws Exception {
		Network network = createNetwork();
		Company company = newCompany();
		networkService.addCompanyToNetworkWithRole(company.getId(), network.getId(), AclRole.ACL_NETWORK_OWNER);

		List<AclNetworkRoleAssociation> associations = aclNetworkRoleAssociationService.findAclNetworkRoleAssociationsByCompanyId(company.getId());

		boolean hasNetworkOwnerRole = false;
		for(AclNetworkRoleAssociation association : associations) {
			AclRole role = association.getRole();
			if(role.getId().equals(AclRole.ACL_NETWORK_OWNER)) {
				hasNetworkOwnerRole = true;
				break;
			}
		}

		assertTrue(hasNetworkOwnerRole);
	}

	@Test
	public void groupIsShared_whenRequestingSharingCompany_shouldReturnOwningCompanyOfNetwork() throws Exception {
		Network network = createNetwork();
		Company company = newCompany();
		UserGroup group = newCompanyUserGroup(company.getId());

		networkService.addCompanyToNetworkWithRole(company.getId(), network.getId(), AclRole.ACL_NETWORK_OWNER);
		networkService.addGroupToNetwork(group.getId(), network.getId());

		Company sharingCompany = networkService.getSharingCompany(group.getId());
		assertEquals(sharingCompany.getId(), company.getId());
	}

	@Test
	public void getSharedGroupsByMemberId_addMemberToGroupAddGroupToNetworks_returnGroupWithNetworks() throws Exception {
		Network network1 = createNetwork();
		Network network2 = createNetwork();

		User worker = newContractor();

		Company company = newCompany();
		UserGroup group = newCompanyUserGroup(company.getId());

		// Add worker to group
		userGroupService.applyToGroup(group.getId(), worker.getId());
		userGroupService.approveUser(group.getId(), worker.getId());
		assertTrue(userGroupService.isUserMemberOfGroup(group.getId(), worker.getId()));

		// Add group to networks
		networkService.addGroupToNetwork(group.getId(), network1.getId());
		networkService.addGroupToNetwork(group.getId(), network2.getId());

		Map<Long, Set<Network>> results = networkService.getSharedGroupsByMemberId(worker.getId());
		assertEquals(results.size(), 1);
		Set<Network> networks = results.get(group.getId());
		assertEquals(networks.size(), 2);
		assertTrue(containsAll(networks, network1, network2));

		// Test findUserGroupsNetworks as well
		Set<Network> networks2 = Sets.newHashSet(networkService.findNetworksWhereGroupIsShared(group.getId()));
		assertEquals(networks2.size(), 2);
		assertTrue(containsAll(networks2, network1, network2));
	}

	@Test
	public void groupIsNotShared_whenAskingIfUserCanViewGroup_groupIsNotShareable() throws Exception {
		User user = newInternalUser();
		Company company = newCompany();
		UserGroup group = newCompanyUserGroup(company.getId());

		assertFalse(networkService.userCanViewSharedGroup(group.getId(), user.getId()));
	}

	@Test
	public void groupIsShared_whenAskingIfUserCanViewGroup_shouldReturnTrueIfUserCompanyInNetwork() throws Exception {
		User user = newInternalUser();
		Network network = createNetwork();
		UserGroup group = newCompanyUserGroup(user.getCompany().getId());

		networkService.addCompanyToNetworkWithRole(user.getCompany().getId(), network.getId(), AclRole.ACL_NETWORK_OWNER);
		networkService.addGroupToNetwork(group.getId(), network.getId());

		assertTrue(networkService.userCanViewSharedGroup(group.getId(), user.getId()));
	}

	@Test
	public void groupIsShared_whenAskingIfUserCanViewGroup_shouldReturnFalseIfUserCompanyNotInNetwork() throws Exception {
		User user = newInternalUser();
		Network network = createNetwork();
		UserGroup group = newCompanyUserGroup(user.getCompany().getId());

		networkService.addCompanyToNetworkWithRole(user.getCompany().getId(), network.getId(), AclRole.ACL_NETWORK_OWNER);
		networkService.addGroupToNetwork(group.getId(), network.getId());

		assertFalse(networkService.userCanViewSharedGroup(group.getId(), -1L));
	}

	@Test
	public void userNotInANetwork_profileViewableForUserNotInANetwork_false() throws Exception {
		User user = newFirstEmployee();
		User userWithSameCompany = newFirstEmployee();

		assertFalse(networkService.isProfileViewableViaGroupSharing(user.getCompany().getId(), userWithSameCompany.getId()));
	}

	@Test
	public void userInGroup_profileViewableForUserNotInGroupInNetwork_false() throws Exception {
		User user = newInternalUser();
		User userWithDiffirentCompany = newFirstEmployee();
		Network network = createNetwork();
		UserGroup group = newCompanyUserGroup(user.getCompany().getId());

		networkService.addCompanyToNetworkWithRole(user.getCompany().getId(), network.getId(), AclRole.ACL_NETWORK_OWNER);
		networkService.addGroupToNetwork(group.getId(), network.getId());
		assertFalse(networkService.isProfileViewableViaGroupSharing(user.getCompany().getId(), userWithDiffirentCompany.getId()));
	}

	@Test
	public void userInGroup_profileViewableForUserInGroupInNetwork_true() throws Exception {
		User user = newInternalUser();
		User userWithDifferentCompany = newContractorIndependentLane4ReadyWithCashBalance();
		User userWithDifferentCompany2 = newContractorIndependentLane4ReadyWithCashBalance();
		Network network = createNetwork();
		UserGroup group = newCompanyUserGroup(userWithDifferentCompany.getCompany().getId());

		userGroupService.addUsersToGroup(Lists.newArrayList(userWithDifferentCompany2.getId()), group.getId(), user.getId());
		networkService.addCompanyToNetworkWithRole(user.getCompany().getId(), network.getId(), AclRole.ACL_NETWORK_OWNER);
		networkService.addGroupToNetwork(group.getId(), network.getId());
		assertTrue(networkService.isProfileViewableViaGroupSharing(user.getCompany().getId(), userWithDifferentCompany2.getId()));
	}

	@Test
	public void isGroupShared_groupDoesNotExist_returnFalse() {
		assertFalse(networkService.isGroupShared(0L));
	}

	@Test
	public void isGroupShared_groupIsNotShared_returnFalse() throws Exception {
		User user = newInternalUser();
		UserGroup group = newCompanyUserGroup(user.getCompany().getId());

		assertFalse(networkService.isGroupShared(group.getId()));
	}

	@Test
	public void isGroupShared_groupIsShared_returnTrue() throws Exception {
		User user = newInternalUser();
		Network network = createNetwork();
		UserGroup group = newCompanyUserGroup(user.getCompany().getId());
		networkService.addCompanyToNetworkWithRole(user.getCompany().getId(), network.getId(), AclRole.ACL_NETWORK_OWNER);
		networkService.addGroupToNetwork(group.getId(), network.getId());

		assertTrue(networkService.isGroupShared(group.getId()));
	}

	@Test
	public void addCompanyToNetworkWithRole_methodCallDoesCacheEvict() {
		Company company = newCompany();

		networkService.findAllCompanyNetworkIds(company.getId());
		assertTrue(redisAdapter.get(RedisConfig.COMPANY_NETWORK_IDS + company.getId()).isPresent());

		networkService.addCompanyToNetworkWithRole(company.getId(), 1L, 1L);
		assertFalse(redisAdapter.get(RedisConfig.COMPANY_NETWORK_IDS + company.getId()).isPresent());
	}

}
