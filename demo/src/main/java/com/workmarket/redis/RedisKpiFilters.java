package com.workmarket.redis;

import com.workmarket.domains.model.kpi.KPIReportAggregateInterval;
import com.workmarket.domains.model.kpi.KPIReportType;

/**
 * Author: rocio
 */
public class RedisKpiFilters {

	public static final String STATISTICS_DASHBOARD_HASH_KEY = "statistics:%s:%s:%s";

	public static String getStatisticsDashboardHashKey(long companyId, KPIReportType kpiReportType, KPIReportAggregateInterval kpiReportAggregateInterval) {
		return String.format(STATISTICS_DASHBOARD_HASH_KEY, companyId, kpiReportType.toString(), kpiReportAggregateInterval.toString());
	}
}
