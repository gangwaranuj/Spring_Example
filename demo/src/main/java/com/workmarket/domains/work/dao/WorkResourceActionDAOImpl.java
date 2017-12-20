package com.workmarket.domains.work.dao;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.WorkResourceAction;

@Repository
public class WorkResourceActionDAOImpl extends AbstractDAO<WorkResourceAction> implements WorkResourceActionDAO {

	@Override
	protected Class<?> getEntityClass() {
		return WorkResourceAction.class;
	}

}
