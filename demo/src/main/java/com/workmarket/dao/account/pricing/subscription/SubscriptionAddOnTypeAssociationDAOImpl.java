package com.workmarket.dao.account.pricing.subscription;

import java.util.List;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionAddOnTypeAssociation;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Author: rocio
 */
@Repository
public class SubscriptionAddOnTypeAssociationDAOImpl extends AbstractDAO<SubscriptionAddOnTypeAssociation> implements SubscriptionAddOnTypeAssociationDAO {

	@Override
	protected Class<SubscriptionAddOnTypeAssociation> getEntityClass() {
		return SubscriptionAddOnTypeAssociation.class;
	}

	@Override
	public List<SubscriptionAddOnTypeAssociation> findBySubscriptionIdAndAddOnType(long subscriptionId, String subscriptionAddOnTypeCode) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("subscriptionConfiguration.id", subscriptionId))
				.add(Restrictions.eq("subscriptionAddOnType.code", subscriptionAddOnTypeCode));
		return (List<SubscriptionAddOnTypeAssociation>)criteria.list();
	}

}
