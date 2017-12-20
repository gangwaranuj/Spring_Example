package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.asset.UserLinkAssociation;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserLinkAssociationDAOImpl extends DeletableAbstractDAO<UserLinkAssociation> implements UserLinkAssociationDAO {

	@Override
	protected Class<UserLinkAssociation> getEntityClass() {
		return UserLinkAssociation.class;
	}

	@Override
	public List<UserLinkAssociation> findUserLinkAssociationsByUserId(Long userId) {
		return (List<UserLinkAssociation>) (getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.asc("assetOrder")).list());
	}
}
