package com.workmarket.thrift.work.display;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.reporting.*;
import com.workmarket.service.business.dto.ReportRecurrenceDTO;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class WorkDisplay {

	public interface Iface {
		public ReportingTypesCompositeResponse getWorkReportTypes(ReportingTypesInitialRequest reportingTypesInitialRequest) throws WorkDisplayException;

		public WorkReportEntityBucketsCompositeResponse getWorkReportEntityBuckets(ReportingTypeRequest reportingTypeRequest) throws WorkDisplayException;

		public ReportResponse getGenerateReport(FilteringEntityRequest filteringEntityRequest) throws Exception;

		public SavedCustomReportResponse saveCustomReportType(FilteringEntityRequest filteringEntityRequest) throws WorkDisplayException;

		public SavedCustomReportsCompositeResponse getCompanyCustomReports(ReportingTypesInitialRequest reportingTypesInitialRequest) throws WorkDisplayException;

		public WorkReportEntityBucketsCompositeResponse getGenerateCustomReport(ReportingTypesInitialRequest reportingTypesInitialRequest, long reportKey) throws Exception;

		public boolean deleteCustomReport(ReportingTypesInitialRequest reportingTypesInitialRequest, long reportingCriteriaId) throws WorkDisplayException;

		public ReportRecurrence findReportRecurrence(long reportKey);

		public List<ReportRecurrence> findReportRecurrencesByCompanyId(long companyId);

		SavedCustomReportResponse saveCustomReportRecurrence(ReportRecurrenceDTO dto);

		ReportRequestData extractReportRequestData(long reportKey, Locale locale, Company company) throws Exception;

		public List<ReportingCriteria> findRecurringReportsByDateTime(DateTime dateTime);

		public Set<Email> findRecurringReportRecipientsByReportId(Long reportId);

		ReportingCriteria getCustomReportCriteria(Long reportKey);

        public List<WorkReportEntityBucketResponse> constructWorkReportEntityBucketResponses(
                ReportingContext context,
                Locale locale,
                Map<String, ReportFilter> entityRequestMap,
                Map<String, ReportFilter> displayEntityRequestMap) throws Exception;

        public WorkReportEntityBucketsCompositeResponse constructWorkReportEntityBucketsCompositeResponse(
                ReportRequestData entityRequestForReport,
                ReportingReportType reportingReportType) throws Exception;
	}
}