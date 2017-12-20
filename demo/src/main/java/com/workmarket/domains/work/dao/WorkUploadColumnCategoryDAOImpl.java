package com.workmarket.domains.work.dao;

import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.WorkUploadColumnCategory;

@Repository
public class WorkUploadColumnCategoryDAOImpl extends AbstractDAO<WorkUploadColumnCategory> implements WorkUploadColumnCategoryDAO {

	@Override
	protected Class<WorkUploadColumnCategory> getEntityClass() {
		return WorkUploadColumnCategory.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WorkUploadColumnCategory> findAllColumnCategories() {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			// .add(Restrictions.eq("visible", Boolean.TRUE))
			.addOrder(Order.asc("order"))
			.list();
	}
}