package com.workmarket.domains.payments.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.FastFundsReceivableCommitment;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class FastFundsCommitmentDAOImpl extends AbstractDAO<FastFundsReceivableCommitment> implements FastFundsReceivableCommitmentDAO {
	protected Class<FastFundsReceivableCommitment> getEntityClass() {
		return FastFundsReceivableCommitment.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FastFundsReceivableCommitment findCommitmentByWorkId(long workId) {
		Query query = getFactory().getCurrentSession()
			.getNamedQuery("fastFundsReceivableCommitment.findByWorkId")
			.setParameter("workId", workId);

		return (FastFundsReceivableCommitment) query.uniqueResult();
	}
}
