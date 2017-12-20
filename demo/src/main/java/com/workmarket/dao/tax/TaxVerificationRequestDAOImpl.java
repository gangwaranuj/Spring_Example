package com.workmarket.dao.tax;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.tax.TaxVerificationRequest;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by nick on 11/29/12 4:31 PM
 */
@Repository
public class TaxVerificationRequestDAOImpl extends AbstractDAO<TaxVerificationRequest> implements TaxVerificationRequestDAO {

	@Override
	protected Class<?> getEntityClass() {
		return TaxVerificationRequest.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TaxVerificationRequest> findTaxVerificationRequests() {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.addOrder(Order.desc("id"))
				.list();
	}
}
