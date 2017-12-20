package com.workmarket.dao.state;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;

@Repository
public class WorkSubStatusTypeCompanySettingDAOImpl extends AbstractDAO<WorkSubStatusTypeCompanySetting> implements WorkSubStatusTypeCompanySettingDAO {

	private static final Log logger = LogFactory.getLog(WorkSubStatusTypeCompanySettingDAOImpl.class);

	@Override
	protected Class<WorkSubStatusTypeCompanySetting> getEntityClass() {
		return WorkSubStatusTypeCompanySetting.class;
	}

	@Override
	public WorkSubStatusTypeCompanySetting findWorkSubStatusTypeCompanySettingByWorkSubStatusAndCompany(Long workSubStatusTypeId, Long companyId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("workSubStatusType.id", workSubStatusTypeId))
				.add(Restrictions.eq("company.id", companyId));

		return (WorkSubStatusTypeCompanySetting) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WorkSubStatusTypeCompanySetting> findWorkSubStatusTypeCompanySettingByCompany(Long companyId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("company.id", companyId));

		return criteria.list();
	}
}
