package com.workmarket.dao;

import com.workmarket.domains.model.reporting.ReportingCriteria;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class ReportingCriteriasDAOImpl extends AbstractDAO<ReportingCriteria> implements ReportingCriteriasDAO {

	protected Class<ReportingCriteria> getEntityClass() {
		return ReportingCriteria.class;
	}


	@Override
	public ReportingCriteria findByReportKey(Long reportKey) {
		Session session = getFactory().getCurrentSession();
		Query query = session.getNamedQuery("reportingCriteria.byReportKey");
		query.setLong("reportKey", reportKey);

		return (ReportingCriteria) query.uniqueResult();
	}

	@Override
	public List<ReportingCriteria> findByReportKeys(List<Long> reportIds) {
		if (CollectionUtils.isEmpty(reportIds)) return new ArrayList<ReportingCriteria>() {};

		Session session = getFactory().getCurrentSession();
		Query query = session.getNamedQuery("reportingCriteria.selectByIdsList");
		query.setParameterList("ids", reportIds, LongType.INSTANCE);

		return query.list();
	}


	@Override
	public List<ReportingCriteria> findByCompanyId(Long companyId) {
		Session session = getFactory().getCurrentSession();
		Query query = session.getNamedQuery("reportingCriteria.byCompanyId");
		query.setLong("companyId", companyId);

		return query.list();
	}


	@Override
	public List<ReportingCriteria> findAll() {
		Query query = getFactory().getCurrentSession().getNamedQuery("reportingCriteria.selectAll");

		return query.list();
	}

}
