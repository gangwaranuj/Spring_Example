package com.workmarket.dao.summary.work;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.summary.work.WorkHistorySummary;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface WorkHistorySummaryDAO extends DAOInterface<WorkHistorySummary> {

	Integer countWork(long companyId, String workStatusTypeCode, DateRange dateFilter);

	Integer countWorkWithLatePayment(long companyId, DateRange dateFilter);

	Map<Long, Integer> countWorkForCompany(List<Long> workResourceUserIds, long companyId, String workStatusTypeCode);

	Map<Long, Integer> countRepeatedClientsByUser(Calendar fromDate, List<Long> userIds);
}
   