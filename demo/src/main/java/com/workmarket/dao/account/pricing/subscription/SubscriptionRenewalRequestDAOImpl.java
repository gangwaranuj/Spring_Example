package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionRenewalRequest;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionRenewalRequestDAOImpl extends AbstractDAO<SubscriptionRenewalRequest> implements SubscriptionRenewalRequestDAO {

	@Override
	protected Class<SubscriptionRenewalRequest> getEntityClass() {
		return SubscriptionRenewalRequest.class;
	}

	@Override
	public SubscriptionRenewalRequest findLatestPendingApprovalSubscriptionRenewalRequest(long parentSubscriptionId) {
		Criteria query = getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("parentSubscription.id", parentSubscriptionId))
				.add(Restrictions.eq("approvalStatus", ApprovalStatus.PENDING))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.asc("modifiedOn"))
				.setProjection(Projections.property("id"))
				.setMaxResults(1);

		Long requestId = (Long) query.uniqueResult();
		/**
		 * Since we are using setMaxResults, this will only retrieve one item of whatever collections are in the object
		 * That's why we need to load the object.
		 */
		if (requestId != null) return get(requestId);
		return null;
	}

}
