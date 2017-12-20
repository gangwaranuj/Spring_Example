package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionThroughputIncrementTransaction;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author: rocio
 */
@Repository
public class SubscriptionThroughputIncrementTransactionDAOImpl extends AbstractDAO<SubscriptionThroughputIncrementTransaction> implements SubscriptionThroughputIncrementTransactionDAO {

	@Override
	protected Class<SubscriptionThroughputIncrementTransaction> getEntityClass() {
		return SubscriptionThroughputIncrementTransaction.class;
	}

	@Override
	public List<SubscriptionThroughputIncrementTransaction> findAllSubscriptionThroughputIncrementTxsByStatus(String transactionStatus) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("transactionStatus.code", transactionStatus));
		return criteria.list();
	}
}
