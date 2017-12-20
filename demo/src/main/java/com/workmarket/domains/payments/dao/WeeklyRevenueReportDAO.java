package com.workmarket.domains.payments.dao;

import com.workmarket.domains.model.account.WeekReportDetail;
import com.workmarket.domains.model.account.WeeklyReportRow;
import com.workmarket.domains.model.account.WeeklyReportRowPagination;

import java.util.List;

public interface WeeklyRevenueReportDAO {

	List<WeeklyReportRow> getCompanyWeeklyRevenueReport(WeeklyReportRowPagination pagination);

	WeekReportDetail getCompanyCurrentWeekRevenueTrend(Long companyId);
}
