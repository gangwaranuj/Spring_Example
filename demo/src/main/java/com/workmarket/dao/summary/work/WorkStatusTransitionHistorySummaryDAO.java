package com.workmarket.dao.summary.work;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.summary.work.WorkStatusTransitionHistorySummary;

import java.math.BigDecimal;

/**
 * Author: rocio
 */
public interface WorkStatusTransitionHistorySummaryDAO extends DAOInterface<WorkStatusTransitionHistorySummary> {

	BigDecimal calculateAverageTransitionTimeByCompanyInSeconds(String fromWorkStatusType, String toWorkStatusType, long companyId, DateRange dateRange);

	BigDecimal calculateAverageTimeToPayFromDueDateByCompanyInSeconds(long companyId, DateRange dateRange, boolean includeOverdueWork);

}
