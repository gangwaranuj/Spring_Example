package com.workmarket.dao.changelog.work;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.changelog.work.FlatWorkUpdatedChangeLog;

@Repository
public class WorkUpdateChangeLogDAOImpl extends AbstractDAO<FlatWorkUpdatedChangeLog> implements WorkUpdateChangeLogDAO {

	protected Class<FlatWorkUpdatedChangeLog> getEntityClass() {
		return FlatWorkUpdatedChangeLog.class;
	}

	
}
