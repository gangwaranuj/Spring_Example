package com.workmarket.dao.account.payment;

import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.payment.PaymentTermsDuration;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by nick on 9/17/12 6:42 PM
 */
@Repository
public class PaymentTermsDurationDAOImpl extends AbstractDAO<PaymentTermsDuration> implements PaymentTermsDurationDAO {

	@Override protected Class<?> getEntityClass() {
		return PaymentTermsDuration.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PaymentTermsDuration> findDefaultPaymentTermsDurations() {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in(
						"type",
						Lists.newArrayList(PaymentTermsDuration.SYSTEM, PaymentTermsDuration.SYSTEM_NOT_DELETABLE)));

		return (List<PaymentTermsDuration>) criteria.list();
	}
}
