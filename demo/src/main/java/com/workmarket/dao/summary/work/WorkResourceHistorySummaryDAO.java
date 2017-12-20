package com.workmarket.dao.summary.work;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.summary.work.WorkResourceHistorySummary;

import java.util.List;

/**
 * Author: rocio
 */
public interface WorkResourceHistorySummaryDAO extends DAOInterface<WorkResourceHistorySummary> {

	List<Long> getAllWorkIdsByWorkResourceUserIdAndStatus(long userId, WorkResourceStatusType workResourceStatusType);
}
