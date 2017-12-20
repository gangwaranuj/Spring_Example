package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionCancellation;
import org.springframework.stereotype.Repository;

/**
 * Author: rocio
 */
@Repository
public class SubscriptionCancellationDAOImpl extends AbstractDAO<SubscriptionCancellation> implements SubscriptionCancellationDAO {

	@Override
	protected Class<SubscriptionCancellation> getEntityClass() {
		return SubscriptionCancellation.class;
	}
}
