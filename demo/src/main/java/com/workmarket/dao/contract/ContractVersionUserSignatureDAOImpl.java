package com.workmarket.dao.contract;


import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.contract.ContractVersionUserSignature;
import com.workmarket.domains.model.contract.ContractVersionUserSignaturePagination;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class ContractVersionUserSignatureDAOImpl extends AbstractDAO<ContractVersionUserSignature> implements ContractVersionUserSignatureDAO {

	protected Class<ContractVersionUserSignature> getEntityClass() {
		return ContractVersionUserSignature.class;
	}

	@Override
	@SuppressWarnings(value = "unchecked")
	public ContractVersionUserSignaturePagination findAllContractVersionsUserSignaturesByUserId(long userId, ContractVersionUserSignaturePagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());
		criteria.setFetchMode("contractVersion", FetchMode.JOIN);
		criteria.setFetchMode("contractVersion.contract", FetchMode.JOIN);
		criteria.setFetchMode("contractVersion.contractVersionAssets", FetchMode.JOIN);

		criteria.add(Restrictions.eq("user.id", userId));
		count.add(Restrictions.eq("user.id", userId));

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

}
