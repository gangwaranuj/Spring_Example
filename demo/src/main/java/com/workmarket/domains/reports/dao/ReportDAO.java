package com.workmarket.domains.reports.dao;

import com.workmarket.data.report.work.WorkReportPagination;

public interface ReportDAO {
    
	WorkReportPagination generateWorkReportBuyer(Long companyId, Long userId, WorkReportPagination pagination, boolean includeCustomFields);

	WorkReportPagination generateBudgetReportBuyer(Long companyId, Long userId, WorkReportPagination pagination, boolean includeCustomFields);
    
	WorkReportPagination generateEarningsReportResource(Long companyId, Long userId, WorkReportPagination pagination, boolean includeCustomFields);

}
