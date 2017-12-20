package com.workmarket.dao.network;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.network.Network;
import com.workmarket.domains.model.network.UserNetworkAssociation;

import java.util.List;
import java.util.Set;

public interface UserNetworkAssociationDAO extends DAOInterface<UserNetworkAssociation> {

	UserNetworkAssociation findAssociationByUserIdAndNetwork(Long userId, Long network);

	void removeUserFromNetwork(Long userId, Long networkId);

	void addUserToNetwork(User worker, Network network);

	void removeUserFromAllNetworks(Long userId, List<Long> networkIds);

	Set<User> findActiveUsersInNetworkById(Long networkId);

}
