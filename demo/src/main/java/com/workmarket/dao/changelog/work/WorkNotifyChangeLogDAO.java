package com.workmarket.dao.changelog.work;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.changelog.work.WorkNotifyChangeLog;

import java.util.Calendar;

/**
 * Created by ianha on 12/24/14
 */
public interface WorkNotifyChangeLogDAO extends DAOInterface<WorkNotifyChangeLog> {
	int getLogCountSinceDate(Long workId, Calendar date);
}
