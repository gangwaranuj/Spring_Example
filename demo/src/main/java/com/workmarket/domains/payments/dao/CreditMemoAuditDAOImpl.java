package com.workmarket.domains.payments.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.invoice.CreditMemoAudit;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class CreditMemoAuditDAOImpl extends AbstractDAO<CreditMemoAudit> implements CreditMemoAuditDAO {

	@Override
	protected Class<?> getEntityClass() {
		return CreditMemoAudit.class;
	}

	@Override
	public CreditMemoAudit findByInvoiceId(Long id) {

		CreditMemoAudit creditMemoAudit = null;
		Criteria query = getFactory().getCurrentSession().createCriteria(CreditMemoAudit.class);
		query.add(Restrictions.eq("creditMemo.id", id));
		if(query.uniqueResult() != null)
			creditMemoAudit = (CreditMemoAudit)query.uniqueResult();
		return creditMemoAudit;
	}

	@Override
	public CreditMemoAudit findByOriginalInvoiceId(Long id) {

		CreditMemoAudit creditMemoAudit = null;
		Criteria query = getFactory().getCurrentSession().createCriteria(CreditMemoAudit.class);
		query.add(Restrictions.eq("serviceInvoice.id", id));
		if(query.uniqueResult() != null)
			creditMemoAudit = (CreditMemoAudit)query.uniqueResult();
		return creditMemoAudit;
	}

	@Override
	public boolean creditMemoAlreadyExisted(Long id) {
		CreditMemoAudit creditMemoAudit = findByOriginalInvoiceId(id);
		return creditMemoAudit == null ? false : true;
	}

}
