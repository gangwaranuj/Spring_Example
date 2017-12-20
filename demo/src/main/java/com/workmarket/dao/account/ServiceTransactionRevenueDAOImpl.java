package com.workmarket.dao.account;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.ServiceTransactionRevenue;
import org.springframework.stereotype.Repository;

/**
 * Author: rocio
 */
@Repository
public class ServiceTransactionRevenueDAOImpl extends AbstractDAO<ServiceTransactionRevenue> implements ServiceTransactionRevenueDAO {

	@Override
	protected Class<ServiceTransactionRevenue> getEntityClass() {
		return ServiceTransactionRevenue.class;
	}

}
