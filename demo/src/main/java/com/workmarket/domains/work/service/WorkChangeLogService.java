package com.workmarket.domains.work.service;

import com.workmarket.domains.model.changelog.work.WorkChangeLog;
import com.workmarket.domains.model.changelog.work.WorkChangeLogPagination;

import java.util.Calendar;

public interface WorkChangeLogService {
    WorkChangeLogPagination findAllChangeLogsByWorkId(Long workId, WorkChangeLogPagination pagination);

	<T extends WorkChangeLog> void saveWorkChangeLog(T workChangeLog);

	<T extends WorkChangeLog> T findWorkChangeLog(long id);

	void saveWorkCreatedChangeLog(Long workId, Long userId, Long masqueradeUser, Long onBehalfUserId);

	int getWorkNotifyLogCountSinceDate(Long workId, Calendar date);
}
