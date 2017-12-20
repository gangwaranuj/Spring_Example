package com.workmarket.service.business;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.domains.model.reporting.ReportingCriteria;
import com.workmarket.thrift.work.display.FilteringEntityRequest;
import com.workmarket.thrift.work.display.ReportResponse;
import com.workmarket.thrift.work.display.WorkDisplayException;
import com.workmarket.thrift.work.display.WorkReportEntityBucketsCompositeResponse;
import com.workmarket.web.models.DataTableColumnHeader;

import java.util.List;
import java.util.Map;


public interface CustomReportService {

	ReportResponse generateSavedCustomReport(Long reportId, Company company);

	ReportRequestData getReportRequestData(ReportingCriteria reportingCriteria);

	ReportResponse generateAdhocCustomReport(ReportRequestData reportData);

	void generateAsyncCustomReport(Long reportId, Company company, String userNumber, Long masqueradeUserId);

	void generateAsyncAdhocCustomReport(FilteringEntityRequest filteringEntityRequest, Long companyId) throws WorkDisplayException;

	WorkReportEntityBucketsCompositeResponse fetchFiltersForDisplay(Long reportId, Company company) throws Exception;

	void updateFilters(Long reportId, Map<String, Object> filterMaps);

	List<DataTableColumnHeader> createDataTableColumnHeaders(Long reportId, List<String> columnHeaders);

	boolean hasAccessToCustomReport(long reportId, long companyId);

}
