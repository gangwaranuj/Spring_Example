package com.workmarket.dao.network;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.network.Network;

import java.util.List;

public interface NetworkDAO extends DAOInterface<Network> {

	/**
	 * get all active networks
	 *
	 * @return
	 */
	public List<Network> findAllActiveNetworks(Long companyId);

	/**
	 * returns a network by networkId
	 *
	 * @param networkId
	 * @return
	 */
	public Network get(Long networkId);

	/**
	 * soft deletes a network, flagging as active = false
	 *
	 * @param networkId
	 */
	public void softDelete(Long networkId);

	/**
	 * persist or updates a network
	 *
	 * @param network
	 */
	public void saveOrUpdate(Network network);

	boolean isProfileViewableViaGroupSharing(Long companyId, Long userId);
}
