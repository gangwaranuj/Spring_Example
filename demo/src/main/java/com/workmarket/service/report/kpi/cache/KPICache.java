package com.workmarket.service.report.kpi.cache;

import com.google.common.base.Optional;
import com.workmarket.data.report.internal.AssignmentReport;
import com.workmarket.data.report.internal.SnapshotReport;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.kpi.KPIChartResponse;

/**
 * Author: rocio
 */
public interface KPICache {

	void put(KPIRequest request, long companyId, KPIChartResponse kpiChartResponse);

	void put(KPIRequest request, long companyId, AssignmentReport assignmentReport);

	void put(KPIRequest request, long companyId, SnapshotReport snapshotReport);

	Optional<KPIChartResponse> get(KPIRequest request, long companyId);

	Optional<AssignmentReport> getAssignmentReport(KPIRequest request, long companyId);

	Optional<SnapshotReport> getSnapshotReport(KPIRequest request, long companyId);
}
