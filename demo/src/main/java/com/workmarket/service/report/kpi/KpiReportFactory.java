package com.workmarket.service.report.kpi;

import java.util.List;

import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.KPIReportType;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIAggregateEntityTable;
import com.workmarket.domains.model.kpi.KPIReportException;
import com.workmarket.domains.model.kpi.KPIReportFilter;

public interface KpiReportFactory {

	List<DataPoint> getKpiReportChartData(KPIReportType kpiReportType, KPIRequest kpiRequest) throws KPIReportException;

	List<KPIAggregateEntityTable> getKPIAggregateEntityTableData(KPIReportType kpiReportType, KPIRequest kpiRequest) throws KPIReportException;

	boolean isFilterPresent(List<Filter> filters, KPIReportFilter filter);
}
