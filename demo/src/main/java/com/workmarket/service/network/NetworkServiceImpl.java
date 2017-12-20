package com.workmarket.service.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.dao.network.CompanyNetworkAssociationDAO;
import com.workmarket.dao.network.NetworkDAO;
import com.workmarket.dao.network.UserGroupNetworkAssociationDAO;
import com.workmarket.dao.network.UserNetworkAssociationDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.network.CompanyNetworkAssociation;
import com.workmarket.domains.model.network.Network;
import com.workmarket.domains.model.network.UserGroupNetworkAssociation;
import com.workmarket.domains.model.network.UserNetworkAssociation;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.index.UpdateUserGroupSearchIndex;
import com.workmarket.service.search.group.GroupSearchService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class NetworkServiceImpl implements NetworkService {

	@Autowired private NetworkDAO networkDAO;
	@Autowired private UserGroupNetworkAssociationDAO userGroupNetworkAssociationDAO;
	@Autowired private UserNetworkAssociationDAO userNetworkAssociationDAO;
	@Autowired private CompanyNetworkAssociationDAO companyNetworkAssociationDAO;
	@Autowired private UserService userService;
	@Autowired private CompanyService companyService;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private UserGroupService groupService;
	@Autowired private GroupSearchService groupSearchService;
	@Autowired private UserGroupDAO userGroupDAO;

	public static final String COMPANY_NETWORK_IDS = RedisConfig.COMPANY_NETWORK_IDS;

	@Override
	public Network findNetworkById(Long networkId) {
		Assert.notNull(networkId);
		return networkDAO.get(networkId);
	}

	@Override
	public Network get(Long networkId) {
		Assert.notNull(networkId);
		return networkDAO.get(networkId);
	}

	@Override
	public void softDelete(Long networkId) {
		Assert.notNull(networkId);
		networkDAO.softDelete(networkId);
	}

	@Override
	public Network saveOrUpdate(Network network) {
		Assert.notNull(network);
		networkDAO.saveOrUpdate(network);
		return network;
	}

	@Override
	@Cacheable(
		value = COMPANY_NETWORK_IDS,
		key = "#root.target.COMPANY_NETWORK_IDS + #companyId"
	)
	public List<Long> findAllCompanyNetworkIds(Long companyId) {
		Assert.notNull(companyId);
		List<Network> networks = networkDAO.findAllActiveNetworks(companyId);
		List<Long> networkIds = Lists.newArrayList();

		for (Network n : networks) {
			networkIds.add(n.getId());
		}

		return networkIds;
	}

	@Override
	@CacheEvict(
		value = COMPANY_NETWORK_IDS,
		key = "#root.target.COMPANY_NETWORK_IDS + #companyId"
	)
	public void addCompanyToNetworkWithRole(Long companyId, Long networkId, long aclNetworkRoleId) {
		Company company = companyService.findCompanyById(companyId);
		Network network = findNetworkById(networkId);
		Assert.notNull(network);
		Assert.notNull(company);

		companyNetworkAssociationDAO.addCompanyToNetworkWithRole(company, network, aclNetworkRoleId);
	}

	@Override
	public Set<User> getActiveUsersInNetworkById(Long networkId) {
		Assert.notNull(networkId);
		return userNetworkAssociationDAO.findActiveUsersInNetworkById(networkId);
	}

	@Override
	public void addWorkerToNetwork(Long workerId, Long networkId) {
		Network network = findNetworkById(networkId);
		User worker = userService.getUser(workerId);
		Assert.notNull(network);
		Assert.notNull(worker);

		userNetworkAssociationDAO.addUserToNetwork(worker, network);
	}

	@Override
	public void addWorkerToAllCompanyNetworks(Long workerId, Long companyId) {
		User worker = userService.getUser(workerId);
		Assert.notNull(worker);

		List<Long> networks = findAllCompanyNetworkIds(companyId);
		for (Long n : networks) {
			Network network = findNetworkById(n);
			Assert.notNull(network);
			userNetworkAssociationDAO.addUserToNetwork(worker, network);
		}
	}

	@Override
	public void removeWorkerFromNetwork(Long workerId, Long networkId) {
		Assert.notNull(workerId);
		Assert.notNull(networkId);
		userNetworkAssociationDAO.removeUserFromNetwork(workerId, networkId);
	}

	@Override
	public void removeUserFromCompanyNetworks(Long userId, Long companyId) {
		Assert.notNull(userId);
		Assert.notNull(companyId);

		List<Long> networkIds = findAllCompanyNetworkIds(companyId);
		if (CollectionUtils.isNotEmpty(networkIds)) {
			userNetworkAssociationDAO.removeUserFromAllNetworks(userId, networkIds);
		}
	}

	@Override
	public boolean isWorkerInNetwork(Long userId, Long networkId) {
		Assert.notNull(userId);
		Assert.notNull(networkId);

		UserNetworkAssociation una = userNetworkAssociationDAO.findAssociationByUserIdAndNetwork(userId, networkId);
		return una.isActive();
	}

	@Override
	public Set<UserGroup> getGroupsInNetwork(Long networkId) {
		Assert.notNull(networkId);
		return userGroupNetworkAssociationDAO.findUserGroupsInNetwork(networkId);
	}

	@Override
	public void addGroupToNetwork(Long groupId, Long networkId) {
		UserGroup group = groupService.findGroupByIdNoAssociations(groupId);
		Network network = findNetworkById(networkId);
		Assert.notNull(group);
		Assert.notNull(network);

		userGroupNetworkAssociationDAO.addGroupToNetwork(group, network);
		groupSearchService.reindexGroupMembers(groupId);
	}

	@Override
	@UpdateUserGroupSearchIndex(updateUsers = false, userGroupIdArgument = 1)
	public void addGroupToCompanyNetworks(Long groupId, Long companyId) {
		UserGroup group = groupService.findGroupByIdNoAssociations(groupId);
		Assert.notNull(group);

		List<Long> networks = findAllCompanyNetworkIds(companyId);
		for (Long n : networks) {
			Network network = findNetworkById(n);
			Assert.notNull(network);
			userGroupNetworkAssociationDAO.addGroupToNetwork(group, network);
		}
		if(networks.size() > 0) {
			groupSearchService.reindexGroupMembers(groupId);
		}
	}

	@Override
	@UpdateUserGroupSearchIndex(updateUsers = false, userGroupIdArgument = 1)
	public void removeGroupFromNetwork(Long groupId, Long networkId) {
		Assert.notNull(groupId);
		Assert.notNull(networkId);
		userGroupNetworkAssociationDAO.removeGroupFromNetwork(groupId, networkId);
		groupSearchService.reindexGroupMembers(groupId);
	}

	@Override
	@UpdateUserGroupSearchIndex(updateUsers = false, userGroupIdArgument = 1)
	public void removeGroupFromCompanyNetworks(Long groupId, Long companyId) {
		Assert.notNull(groupId);
		Assert.notNull(companyId);

		List<Long> networkIds = findAllCompanyNetworkIds(companyId);
		if (CollectionUtils.isNotEmpty(networkIds)) {
			userGroupNetworkAssociationDAO.removeGroupFromAllNetworks(groupId, networkIds);
			groupSearchService.reindexGroupMembers(groupId);
		}
	}

	@Override
	public boolean isGroupInNetwork(Long groupId, Long networkId) {
		Assert.notNull(groupId);
		Assert.notNull(networkId);
		UserGroupNetworkAssociation ugna = userGroupNetworkAssociationDAO.findAssociationByGroupIdAndNetworkId(groupId, networkId);
		return ugna != null && ugna.isActive();
	}

	@Override
	public boolean isGroupShared(Long groupId) {
		Assert.notNull(groupId);
		return userGroupDAO.isGroupShared(groupId);
	}

	@Override
	public Company getSharingCompany(Long userGroupId) {
		Assert.notNull(userGroupId);
		return companyDAO.getSharingCompany(userGroupId);
	}

	@Override
	public Map<Long, Set<Network>> getSharedGroupsByMemberId(Long memberId) {
		Assert.notNull(memberId);

		Map<Long, Set<Network>> mapResults =  Maps.newHashMap();
		List<Map<String, Long>> listResults = userGroupNetworkAssociationDAO.getSharedGroupIdsByMemberId(memberId);
		for (Map<String, Long> row : listResults) {
			Long networkId = row.get("network_id");
			Long groupId = row.get("user_group_id");

			if (mapResults.get(groupId) == null) {
				mapResults.put(groupId, new HashSet<Network>());
			}

			Network network = findNetworkById(networkId);
			mapResults.get(groupId).add(network);
		}
		return mapResults;
	}

	@Override
	public boolean userCanViewSharedGroup(Long userGroupId, Long userId) {
		Assert.notNull(userGroupId);
		Assert.notNull(userId);

		return userGroupDAO.userCanViewSharedGroup(userGroupId, userId);
	}

	@Override
	public List<Network> findNetworksWhereGroupIsShared(Long groupId) {
		Assert.notNull(groupId);
		return userGroupNetworkAssociationDAO.findNetworksWhereGroupIsShared(groupId);
	}

	@Override
	public boolean isCompanyInNetwork(Long companyId, Long networkId) {
		Assert.notNull(companyId);
		Assert.notNull(networkId);

		CompanyNetworkAssociation cna = companyNetworkAssociationDAO.findAssociationByNetworkAndCompany(companyId, networkId);
		return cna != null && cna.isActive();
	}

	public boolean isProfileViewableViaGroupSharing(Long companyId, Long userId) {
		Assert.notNull(companyId);
		Assert.notNull(userId);

		return networkDAO.isProfileViewableViaGroupSharing(companyId, userId);
	}
}
