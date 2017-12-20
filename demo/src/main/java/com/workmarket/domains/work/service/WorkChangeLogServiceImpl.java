package com.workmarket.domains.work.service;

import com.workmarket.dao.changelog.work.WorkChangeLogDAO;
import com.workmarket.dao.changelog.work.WorkNotifyChangeLogDAO;
import com.workmarket.domains.model.changelog.work.WorkChangeLog;
import com.workmarket.domains.model.changelog.work.WorkChangeLogPagination;
import com.workmarket.domains.model.changelog.work.WorkCreatedChangeLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;

@Service
public class WorkChangeLogServiceImpl implements WorkChangeLogService {

	@Autowired private WorkChangeLogDAO workChangeLogDAO;
	@Autowired private WorkNotifyChangeLogDAO workNotifyChangeLogDAO;

	@Override
	public WorkChangeLogPagination findAllChangeLogsByWorkId(Long workId, WorkChangeLogPagination pagination) {
		Assert.notNull(workId);
		Assert.notNull(pagination);
		return workChangeLogDAO.findAllChangeLogByWorkId(workId, pagination);
	}

	@Override
	public <T extends WorkChangeLog> void saveWorkChangeLog(T workChangeLog) {
		if (workChangeLog == null || workChangeLog.getWorkId() == null) {
			return;
		}
		workChangeLogDAO.saveOrUpdate(workChangeLog);
	}

	@Override public <T extends WorkChangeLog> T findWorkChangeLog(long id) {
		return (T)workChangeLogDAO.get(id);
	}

	@Override
	public void saveWorkCreatedChangeLog(Long workId, Long userId, Long masqueradeUser, Long onBehalfUserId) {
		if (workId != null && userId != null) {
			saveWorkChangeLog(new WorkCreatedChangeLog(workId, userId, masqueradeUser, onBehalfUserId));
		}
	}

	@Override
	public int getWorkNotifyLogCountSinceDate(Long workId, Calendar date) {
		return workNotifyChangeLogDAO.getLogCountSinceDate(workId, date);
	}
}
