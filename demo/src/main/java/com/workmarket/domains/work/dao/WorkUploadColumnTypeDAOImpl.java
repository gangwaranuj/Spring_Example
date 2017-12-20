package com.workmarket.domains.work.dao;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.WorkUploadColumnType;

@Repository
public class WorkUploadColumnTypeDAOImpl extends AbstractDAO<WorkUploadColumnType> implements WorkUploadColumnTypeDAO {

	@Override
	protected Class<WorkUploadColumnType> getEntityClass() {
		return WorkUploadColumnType.class;
	}
}


