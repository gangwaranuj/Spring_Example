package com.workmarket.dao.company;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.company.CompanySearchTracking;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class CompanySearchTrackingDAOImpl extends AbstractDAO<CompanySearchTracking> implements CompanySearchTrackingDAO {

	@Autowired
	@Resource(name = "jdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<CompanySearchTracking> getEntityClass() {
		return CompanySearchTracking.class;
	}

	@Override
	public CompanySearchTracking findCompanySearchTrackingByCompanyId(long trackingCompanyId) {
		return (CompanySearchTracking) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("trackingCompany.id", trackingCompanyId)).uniqueResult();
	}

}