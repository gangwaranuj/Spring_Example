package com.workmarket.dao.account.payment;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.payment.PaymentTermsDurationCompanyAssociation;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by nick on 9/18/12 12:30 PM
 */
@Repository
public class PaymentTermsDurationCompanyAssociationDAOImpl extends AbstractDAO<PaymentTermsDurationCompanyAssociation> implements PaymentTermsDurationCompanyAssociationDAO {

	@Override protected Class<?> getEntityClass() {
		return PaymentTermsDurationCompanyAssociation.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PaymentTermsDurationCompanyAssociation> findPaymentTermsDurationCompanyAssociationByCompanyId(Long companyId) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("deleted", false))
				.createAlias("paymentTermsDuration", "ptd")
				.addOrder(Order.asc("ptd.numDays"));

		return (List<PaymentTermsDurationCompanyAssociation>) criteria.list();
	}

}
