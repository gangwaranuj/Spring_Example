package com.workmarket.domains.reports.dao;

import com.workmarket.data.report.work.WorkReportPagination;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.service.business.dto.WorkAggregatesDTO;

public interface WorkReportDAO {

	WorkSearchDataPagination generateWorkDashboardReportBuyer(Long companyId, Long userId, WorkSearchDataPagination pagination);
	WorkSearchDataPagination generateWorkDashboardReportBuyerForList(Long companyId, Long userId, WorkSearchDataPagination pagination);

	/**
	 * Available (sent) assignments. Only in the context as a resource. 
	 * 
	 * @param companyId
	 * @param userId
	 * @param pagination
	 * @return
	 */
	WorkSearchDataPagination generateWorkDashboardReportAvailable(Long companyId, Long userId, WorkSearchDataPagination pagination);

	/**
	 * Work Aggregates
	 * 
	 * @param companyId
	 * @param userId
	 * @param pagination
	 * @return
	 */
	WorkAggregatesDTO generateWorkDashboardStatusAggregateBuyer(Long companyId, Long userId, WorkSearchDataPagination pagination);

	Integer countInprogressAssignmentsWithPaymentTermsByCompany(Long companyId, WorkSearchDataPagination pagination);
	
	Integer countInprogressAssignmentsPrefundByCompany(Long companyId, WorkSearchDataPagination pagination);

	WorkReportPagination findAllWorkByWorkNumber(Long companyId, Long userId, String[] workNumbers, WorkReportPagination pagination);

}
