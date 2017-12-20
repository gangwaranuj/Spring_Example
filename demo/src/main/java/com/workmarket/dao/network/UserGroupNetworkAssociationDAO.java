package com.workmarket.dao.network;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.network.Network;
import com.workmarket.domains.model.network.UserGroupNetworkAssociation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserGroupNetworkAssociationDAO extends DAOInterface<UserGroupNetworkAssociation> {

	UserGroupNetworkAssociation findAssociationByGroupIdAndNetworkId(Long groupId, Long networkId);

	void addGroupToNetwork(UserGroup group, Network network);

	void removeGroupFromNetwork(Long groupId, Long networkId);

	void removeGroupFromAllNetworks(Long groupId, List<Long> networkIds);

	Set<UserGroup> findUserGroupsInNetwork(Long networkId);

	List<Network> findNetworksWhereGroupIsShared(Long groupId);

	/**
	 *
	 * @param memberId - The ID of a group member
	 * @return - A list of maps, with two keys: group_id and network_id (a network the group is shared with)
	 */
	List<Map<String, Long>> getSharedGroupIdsByMemberId(Long memberId);

}
