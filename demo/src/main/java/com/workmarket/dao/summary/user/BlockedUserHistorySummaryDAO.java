package com.workmarket.dao.summary.user;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.summary.user.BlockedUserHistorySummary;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface BlockedUserHistorySummaryDAO extends DAOInterface<BlockedUserHistorySummary> {

	Map<Long, Integer> countDistinctBlockingCompaniesByUser(Calendar fromDate, List<Long> userIds);
}
