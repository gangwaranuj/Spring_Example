package com.workmarket.domains.work.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.work.model.WorkUploadMappingGroup;
import com.workmarket.domains.work.model.WorkUploadMappingGroupPagination;
import com.workmarket.utility.HibernateUtilities;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class WorkUploadMappingGroupDAOImpl extends AbstractDAO<WorkUploadMappingGroup> implements WorkUploadMappingGroupDAO {

	@Override
	protected Class<WorkUploadMappingGroup> getEntityClass() {
		return WorkUploadMappingGroup.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public WorkUploadMappingGroupPagination findByCompanyId(Long companyId, WorkUploadMappingGroupPagination pagination) {
		Criteria query = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());

		HibernateUtilities.setupPagination(query, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);
		HibernateUtilities.addRestrictionsEq(query, "company.id", companyId, "deleted", Boolean.FALSE);
		HibernateUtilities.addRestrictionsEq(count, "company.id", companyId, "deleted", Boolean.FALSE);

		pagination.setRowCount(HibernateUtilities.getRowCount(count));
		pagination.setResults(query.list());

		return pagination;
	}

	@Override
	public WorkUploadMappingGroup findByMappingGroupId(Long mappingId) {
		return (WorkUploadMappingGroup) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("id", mappingId))
				.uniqueResult();
	}
}
