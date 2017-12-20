package com.workmarket.service.report.kpi.cache;

import com.google.common.base.Optional;
import com.workmarket.data.report.internal.AssignmentReport;
import com.workmarket.data.report.internal.SnapshotReport;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.kpi.KPIChartResponse;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisKpiFilters;
import com.workmarket.service.business.JsonSerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: rocio
 */
@Service
public class KPICacheImpl implements KPICache {

	@Autowired RedisAdapter redisAdapter;
	@Autowired JsonSerializationService jsonSerializationService;
	private static final long KPI_REQUEST_EXPIRATION_TIME_IN_SECONDS = 900;

	@Override
	public void put(KPIRequest request, long companyId, KPIChartResponse kpiChartResponse) {
		if (request != null && request.getReportType() != null && request.getAggregateInterval() != null) {
			String key = RedisKpiFilters.getStatisticsDashboardHashKey(companyId, request.getReportType(), request.getAggregateInterval());
			redisAdapter.set(key, jsonSerializationService.toJson(kpiChartResponse), KPI_REQUEST_EXPIRATION_TIME_IN_SECONDS);
		}
	}

	@Override
	public void put(KPIRequest request, long companyId, AssignmentReport assignmentReport) {
		if (request != null && request.getReportType() != null && request.getAggregateInterval() != null) {
			String key = RedisKpiFilters.getStatisticsDashboardHashKey(companyId, request.getReportType(), request.getAggregateInterval());
			redisAdapter.set(key, jsonSerializationService.toJson(assignmentReport), KPI_REQUEST_EXPIRATION_TIME_IN_SECONDS);
		}
	}

	@Override
	public void put(KPIRequest request, long companyId, SnapshotReport snapshotReport) {
		if (request != null && request.getReportType() != null && request.getAggregateInterval() != null) {
			String key = RedisKpiFilters.getStatisticsDashboardHashKey(companyId, request.getReportType(), request.getAggregateInterval());
			redisAdapter.set(key, jsonSerializationService.toJson(snapshotReport), KPI_REQUEST_EXPIRATION_TIME_IN_SECONDS);
		}
	}

	@Override
	public Optional<KPIChartResponse> get(KPIRequest request, long companyId) {
		if (request != null && request.getReportType() != null && request.getAggregateInterval() != null) {
			String key = RedisKpiFilters.getStatisticsDashboardHashKey(companyId, request.getReportType(), request.getAggregateInterval());
			Optional<Object> result = redisAdapter.get(key);
			if(result.isPresent()) {
				return Optional.of(jsonSerializationService.fromJson(result.get().toString(), KPIChartResponse.class));
			}

		}
		return Optional.absent();
	}

	@Override
	public Optional<AssignmentReport> getAssignmentReport(KPIRequest request, long companyId) {
		if (request != null && request.getReportType() != null && request.getAggregateInterval() != null) {
			String key = RedisKpiFilters.getStatisticsDashboardHashKey(companyId, request.getReportType(), request.getAggregateInterval());
			Optional<Object> result = redisAdapter.get(key);
			if(result.isPresent()) {
				return Optional.of(jsonSerializationService.fromJson(result.get().toString(), AssignmentReport.class));
			}

		}
		return Optional.absent();
	}

	@Override
	public Optional<SnapshotReport> getSnapshotReport(KPIRequest request, long companyId) {
		if (request != null && request.getReportType() != null && request.getAggregateInterval() != null) {
			String key = RedisKpiFilters.getStatisticsDashboardHashKey(companyId, request.getReportType(), request.getAggregateInterval());
			Optional<Object> result = redisAdapter.get(key);
			if(result.isPresent()) {
				return Optional.of(jsonSerializationService.fromJson(result.get().toString(), SnapshotReport.class));
			}
		}
		return Optional.absent();
	}
}
