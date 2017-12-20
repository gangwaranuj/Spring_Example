package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Author: rocio
 */
@Repository
public class SubscriptionFeeConfigurationDAOImpl extends AbstractDAO<SubscriptionFeeConfiguration> implements SubscriptionFeeConfigurationDAO {

	protected Class<SubscriptionFeeConfiguration> getEntityClass() {
		return SubscriptionFeeConfiguration.class;
	}

	public SubscriptionFeeConfiguration getLatestPendingApprovalSubscriptionFeeConfiguration(long subscriptionConfigurationId){
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("subscriptionConfiguration", "subscriptionConfiguration")
				.add(Restrictions.eq("subscriptionConfiguration.id", subscriptionConfigurationId))
				.add(Restrictions.eq("active", Boolean.FALSE))
				.add(Restrictions.eq("approvalStatus", ApprovalStatus.PENDING))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.desc("modifiedOn"))
				.setMaxResults(1);
		return (SubscriptionFeeConfiguration) criteria.uniqueResult();
	}

}