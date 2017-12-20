package com.workmarket.domains.reports.service;

import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.data.report.work.DecoratedWorkReportRow;
import com.workmarket.data.report.work.WorkReportPagination;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.service.business.dto.WorkAggregatesDTO;

import java.util.List;
import java.util.Map;

public interface WorkReportService {

	WorkSearchDataPagination generateWorkDashboardReportBuyer(Long companyId, Long userId, WorkSearchDataPagination pagination);
	WorkSearchDataPagination generateWorkDashboardReportBuyerForList(Long companyId, Long userId, WorkSearchDataPagination pagination);

	/**
	 * Assignment dashboard by company. Returns the available assignments for a user/company. 
	 * 
	 * @param companyId
	 * @param userId
	 * @param pagination
	 * @return {@link com.workmarket.data.solr.model.WorkSearchDataPagination WorkDashboardReportPagination}
	 */
	WorkSearchDataPagination generateWorkDashboardReportAvailable(Long companyId, Long userId, WorkSearchDataPagination pagination);

	/**
	 * Count work by company as buyer 
	 * 
	 * @param companyId
	 * @param pagination
	 * @return
	 */
	WorkAggregatesDTO generateWorkDashboardStatusAggregate(Long companyId, WorkSearchDataPagination pagination);

	/**
	 * Pre-built work report with option to include custom fields. 
	 * 
	 * @param userId
	 * @param pagination
	 * @return {@link com.workmarket.data.report.work.WorkReportPagination WorkReportPagination}
	 */
	WorkReportPagination generateWorkReportBuyer(Long userId, WorkReportPagination pagination, boolean includeCustomFields);
	WorkReportPagination generateBudgetReportBuyer(Long userId, WorkReportPagination pagination, boolean includeCustomFields);
	WorkReportPagination generateEarningsReportResource(Long userId, WorkReportPagination pagination, boolean includeCustomFields);

	List<CustomFieldReportRow> findAllWorkCustomFields(Long userId, CustomFieldReportFilters filters);

	Integer countInprogressAssignmentsWithPaymentTermsByCompany(Long companyId, WorkSearchDataPagination pagination);
	
	Integer countInprogressAssignmentsPrefundByCompany(Long companyId, WorkSearchDataPagination pagination);

	WorkReportPagination findAllWorkByWorkNumber(Long companyId, Long userId, String[] workNumbers, WorkReportPagination pagination);
	WorkReportPagination findAllWorkByWorkNumber(Long companyId, Long userId, List<String> workNumbers, WorkReportPagination pagination);

	boolean canViewAllCompanyAssignmentData(Long userId);

	<T extends DecoratedWorkReportRow> List<T> addCustomFields(Long userId, Long companyId, List<T> rows, CustomFieldReportFilters filters);

	Map<String, Object> getWorkCustomFieldsMapForBuyer(Map<String, List<Long>> params);

}
