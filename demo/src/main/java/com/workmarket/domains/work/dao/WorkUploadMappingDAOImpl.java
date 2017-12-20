package com.workmarket.domains.work.dao;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.WorkUploadMapping;

@Repository
public class WorkUploadMappingDAOImpl extends AbstractDAO<WorkUploadMapping> implements WorkUploadMappingDAO {

	@Override
	protected Class<WorkUploadMapping> getEntityClass() {
		return WorkUploadMapping.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<WorkUploadMapping> findByMappingGroupId(Long mappingGroupId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("mappingGroup.id", mappingGroupId))
			.addOrder(Order.asc("columnIndex"))
			.list();
	}
}