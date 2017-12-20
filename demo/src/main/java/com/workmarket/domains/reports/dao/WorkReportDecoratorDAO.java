package com.workmarket.domains.reports.dao;

import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.DecoratedWorkReportRow;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;

import java.util.List;

public interface WorkReportDecoratorDAO {
	<T extends DecoratedWorkReportRow> List<T> addCustomFields(Long userId, List<T> rows, CustomFieldReportFilters filters);
	
	<T extends DecoratedWorkReportRow> List<T> addCustomFields(Long userId, Long companyId, List<T> rows, CustomFieldReportFilters filters);
	
	<T extends DecoratedWorkReportRow> List<T> addWorkSubStatus(List<T> rows, WorkSubStatusTypeFilter workSubStatusTypeFilters);
}
