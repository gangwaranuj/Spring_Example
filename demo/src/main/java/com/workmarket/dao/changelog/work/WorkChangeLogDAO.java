package com.workmarket.dao.changelog.work;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.changelog.work.WorkChangeLog;
import com.workmarket.domains.model.changelog.work.WorkChangeLogPagination;

public interface WorkChangeLogDAO extends DAOInterface<WorkChangeLog> {

	WorkChangeLogPagination findAllChangeLogByWorkId(Long workId, WorkChangeLogPagination pagination);
}
