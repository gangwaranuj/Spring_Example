package com.workmarket.dao.account.pricing.subscription;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.pricing.SubscriptionAccountServiceTypeConfiguration;
import org.springframework.stereotype.Repository;

/**
 * Author: rocio
 */
@Repository
public class SubscriptionAccountServiceTypeConfigurationDAOImpl extends AbstractDAO<SubscriptionAccountServiceTypeConfiguration> implements SubscriptionAccountServiceTypeConfigurationDAO {

	@Override
	protected Class<SubscriptionAccountServiceTypeConfigurationDAO> getEntityClass() {
		return SubscriptionAccountServiceTypeConfigurationDAO.class;
	}
}
