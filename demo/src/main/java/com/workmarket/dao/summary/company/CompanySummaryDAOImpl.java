package com.workmarket.dao.summary.company;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.summary.company.CompanySummary;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Author: rocio
 */
@Repository
public class CompanySummaryDAOImpl extends AbstractDAO<CompanySummary> implements CompanySummaryDAO {

	@Override
	protected Class<CompanySummary> getEntityClass() {
		return CompanySummary.class;
	}

	@Override
	public CompanySummary findByCompany(long companyId) {
		return (CompanySummary)getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("company.id", companyId)).uniqueResult();
	}
}
