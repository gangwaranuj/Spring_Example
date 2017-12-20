package com.workmarket.service.network;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.network.Network;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NetworkService {

	/**
	 * gets a network by networkId
	 *
	 * @param networkId
	 * @return
	 */
	Network get(Long networkId);

	/**
	 * soft deletes a network, flagging as active = false
	 *
	 * @param networkId
	 */
	void softDelete(Long networkId);

	/**
	 * persist or updates a network
	 *
	 * @param network
	 */

	Network saveOrUpdate(Network network);

	Network findNetworkById(Long networkId);

	List<Long> findAllCompanyNetworkIds(Long companyId);

	void addCompanyToNetworkWithRole(Long companyId, Long networkId, long aclNetworkRoleId);

	Set<User> getActiveUsersInNetworkById(Long networkId);

	void addWorkerToNetwork(Long workerId, Long networkId);

	void addWorkerToAllCompanyNetworks(Long userId, Long companyId);

	void removeWorkerFromNetwork(Long workerId, Long networkId);

	void removeUserFromCompanyNetworks(Long userId, Long companyId);

	boolean isWorkerInNetwork(Long workerId, Long networkId);

	Set<UserGroup> getGroupsInNetwork(Long networkId);

	void addGroupToNetwork(Long groupId, Long networkId);

	void addGroupToCompanyNetworks(Long groupId, Long companyId);

	void removeGroupFromNetwork(Long groupId, Long networkId);

	void removeGroupFromCompanyNetworks(Long groupId, Long companyId);

	boolean isGroupInNetwork(Long groupId, Long networkId);

	/**
	 *
	 * @param groupId -
	 * @return - True if there are any active Group <-> Network associations given the groupId, false otherwise
	 */
	public boolean isGroupShared(Long groupId);

	Company getSharingCompany(Long userGroupId);

	/**
	 *
	 * @param memberId - The ID of the group member
	 * @return - A map where the key is groupId and the value is the set of Networks that group has been shared with
	 */
	Map<Long, Set<Network>> getSharedGroupsByMemberId(Long memberId);

	boolean userCanViewSharedGroup(Long userGroupId, Long userId);

	List<Network> findNetworksWhereGroupIsShared(Long groupId);

	boolean isCompanyInNetwork(Long companyId, Long networkId);

	/**
	 * Given a company, can users of that company view the profile of a given user.
	 *
	 * @param companyId
	 * @param userId
	 * @return
	 */
	boolean isProfileViewableViaGroupSharing(Long companyId, Long userId);
}
