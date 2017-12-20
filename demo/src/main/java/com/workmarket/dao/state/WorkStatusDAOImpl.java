package com.workmarket.dao.state;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.WorkStatusType;

@SuppressWarnings("unchecked")
@Repository
public class WorkStatusDAOImpl extends AbstractDAO<WorkStatusType> implements WorkStatusDAO {

	protected Class<WorkStatusType> getEntityClass() {
		return WorkStatusType.class;
	}

	@Override
	public WorkStatusType findByCode(String code) {
		Assert.notNull(code);
		return (WorkStatusType) getFactory().getCurrentSession().createQuery("select ss from work_status_type ss where ss.code = :code")
				.setParameter("code", code).uniqueResult();
	}

	@Override
	public List<WorkStatusType> findByCode(String... codes) {
		return getFactory().getCurrentSession().createQuery("select ss from work_status_type ss where ss.code in (:codes)")
				.setParameterList("codes", codes)
				.list();
	}

}