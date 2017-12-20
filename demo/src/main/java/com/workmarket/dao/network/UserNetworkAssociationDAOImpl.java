package com.workmarket.dao.network;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.network.Network;
import com.workmarket.domains.model.network.UserNetworkAssociation;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserNetworkAssociationDAOImpl extends AbstractDAO<UserNetworkAssociation> implements UserNetworkAssociationDAO {

	protected Class<UserNetworkAssociation> getEntityClass() {
		return UserNetworkAssociation.class;
	}

	@Override
	public UserNetworkAssociation findAssociationByUserIdAndNetwork(Long userId, Long network) {

		Criteria criteria = getFactory().getCurrentSession()
			.createCriteria(UserNetworkAssociation.class)
			.add(Restrictions.eq("worker.id", network))
			.add(Restrictions.eq("network.id", userId))
			.setMaxResults(1);

		return (UserNetworkAssociation) criteria.uniqueResult();
	}

	@Override
	public void addUserToNetwork(User worker, Network network) {
		Assert.notNull(worker);
		Assert.notNull(network);

		UserNetworkAssociation userNetworkAssociation = findAssociationByUserIdAndNetwork(worker.getId(), network.getId());

		if (userNetworkAssociation == null) {
			userNetworkAssociation = new UserNetworkAssociation();
			userNetworkAssociation.setNetwork(network);
			userNetworkAssociation.setWorker(worker);
		}

		userNetworkAssociation.setActive(true);
		userNetworkAssociation.setDeleted(Boolean.FALSE);
		saveOrUpdate(userNetworkAssociation);
	}

	@Override
	public void removeUserFromNetwork(Long workerId, Long networkId) {
		Assert.notNull(workerId);
		Assert.notNull(networkId);

		Query updateQuery = getFactory().getCurrentSession().getNamedQuery("userNetworkAssociation.softDeleteUserNetworkAssocation");
		updateQuery.setParameter("workerId", workerId);
		updateQuery.setParameter("networkId", networkId);

		updateQuery.executeUpdate();
	}

	@Override
	public void removeUserFromAllNetworks(Long userId, List<Long> networkIds) {
		Assert.notNull(userId);
		Assert.notNull(networkIds);

		Query updateQuery = getFactory().getCurrentSession().getNamedQuery("userNetworkAssociation.removeUserFromAllNetworks");
		updateQuery.setParameter("workerUserId", userId);
		updateQuery.setParameterList("networkIds", networkIds);

		updateQuery.executeUpdate();
	}

	@Override
	public Set<User> findActiveUsersInNetworkById(Long networkId) {
		Assert.notNull(networkId);

		Query query = getFactory().getCurrentSession().getNamedQuery("userNetworkAssociation.findActiveUsersInNetworkById");
		query.setParameter("networkId", networkId);

		Set<User> userSet = new HashSet<>();
		userSet.addAll(query.list());
		return userSet;
	}
}
