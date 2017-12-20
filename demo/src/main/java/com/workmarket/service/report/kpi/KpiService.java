package com.workmarket.service.report.kpi;

import com.workmarket.data.report.internal.SnapshotReport;
import com.workmarket.data.report.internal.TopUser;
import com.workmarket.data.report.internal.TopEntity;
import com.workmarket.data.report.internal.AssignmentReport;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.kpi.KPIChartResponse;
import com.workmarket.domains.model.kpi.KPIDataTableResponse;
import com.workmarket.domains.model.kpi.KPIReportException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface KpiService {

	KPIChartResponse getKPIChart(KPIRequest request) throws KPIReportException;

	KPIDataTableResponse getKPITabularData(KPIRequest request) throws KPIReportException;

	List<TopUser> getTopUsersByCompany(KPIRequest request, Integer topLimit);

	List<TopEntity> getTopProjectsByCompany(KPIRequest request, Integer topLimit);

	List<TopEntity> getTopResourcesByCompany(KPIRequest request, Integer topLimit);

	AssignmentReport getAssignmentSegmentationReportAssignment(KPIRequest request, long companyId);

	AssignmentReport getAssignmentSegmentationReportRouting(KPIRequest request, long companyId);

	SnapshotReport getAssignmentSnapshotDataPointsForCompany(KPIRequest request, long companyId) throws KPIReportException;

	KPIRequest createKPIRequestForStatisticsAndCreateDateRange(String requestId);

	void populateStatisticsDataWithKPIReports(KPIRequest kpiRequest, JSONObject data, String requestId, Long companyId, Long userId)throws org.json.JSONException, KPIReportException, UnsupportedOperationException;

	KPIRequest createKPIRequestForKPIReport(Long companyId, Calendar fromDate, Calendar toDate);

	Map<String, Object> generateKPIReports(KPIRequest kpiRequest);

	List<Map<String, String>> getPaidWorkCountsGroupedByCounty(Long companyId, Calendar fromDate, Calendar toDate);

}
