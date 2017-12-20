package com.workmarket.service.report.kpi;

import com.workmarket.dao.report.kpi.KpiDAO;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIAggregateEntityTable;
import com.workmarket.domains.model.kpi.KPIReportException;
import com.workmarket.domains.model.kpi.KPIReportFilter;
import com.workmarket.domains.model.kpi.KPIReportType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by theogugoiu on 3/28/14.
 */

@RunWith(MockitoJUnitRunner.class)
public class KpiReportFactoryTest {

	@Mock KpiDAO kpiDao;
	@InjectMocks KpiReportFactoryImpl kpiReportFactory = spy(new KpiReportFactoryImpl());

	KPIReportType kpiReportType = KPIReportType.SALES_THROUGHPUT_FORECAST;
	KPIRequest kpiRequest = new KPIRequest();
	KPIReportFilter kpiReportFilter = KPIReportFilter.COMPANY;

	List<Filter> filters = new ArrayList<>();
	Filter filter = new Filter();
	ArrayList<String> values = new ArrayList<>();

	@Test
	public void test_getKpiReportChartData_normalExecution_notNull() throws Exception{
		List<DataPoint> expectedResult = new ArrayList<>();
		when(kpiDao.forecastThroughput(kpiRequest)).thenReturn(expectedResult);
		List<DataPoint> result = kpiReportFactory.getKpiReportChartData(kpiReportType, kpiRequest);

		assertNotNull(result);
	}

	@Test
	public void test_getKpiReportChartData_normalExecution_expectedValue() throws Exception{
		List<DataPoint> expectedResult = new ArrayList<>();
		when(kpiDao.forecastThroughput(kpiRequest)).thenReturn(expectedResult);
		List<DataPoint> result = kpiReportFactory.getKpiReportChartData(kpiReportType, kpiRequest);

		assertEquals(expectedResult, result);
	}

	private void setup_getKPIAggregateEntityTableData_normalExecution(){
		filter.setName(kpiReportFilter);
		values.add("1");
		filter.setValues(values);
		filters.add(filter);

		kpiRequest.setFilters(filters);

		kpiReportType = KPIReportType.ASSIGNMENTS_TOTAL_IN_DRAFT_INDUSTRY_DATABLE;
	}

	@Test
	public void test_getKPIAggregateEntityTableData_normalExecution_notNull() throws Exception{
		setup_getKPIAggregateEntityTableData_normalExecution();

		List<KPIAggregateEntityTable> expectedResult = new ArrayList<>();
		when (kpiDao.countAssignmentsByIndustryByStatusDatatable(WorkStatusType.DRAFT, kpiRequest)).thenReturn(expectedResult);
		List<KPIAggregateEntityTable> result = kpiReportFactory.getKPIAggregateEntityTableData(kpiReportType, kpiRequest);
		assertNotNull(result);
	}

	@Test
	public void test_getKPIAggregateEntityTableData_normalExecution_ExpectedValue() throws Exception{
		setup_getKPIAggregateEntityTableData_normalExecution();

		List<KPIAggregateEntityTable> expectedResult = new ArrayList<>();
		when (kpiDao.countAssignmentsByIndustryByStatusDatatable(WorkStatusType.DRAFT, kpiRequest)).thenReturn(expectedResult);
		List<KPIAggregateEntityTable> result = kpiReportFactory.getKPIAggregateEntityTableData(kpiReportType, kpiRequest);
		assertEquals(expectedResult, result);
	}

	@Test(expected = KPIReportException.class)
	public void test_getKPIAggregateEntityTableData_noCompanyFilterThrowsException() throws Exception{
		kpiReportType = KPIReportType.BUYER_RATINGS_BREAKDOWN;
		List<KPIAggregateEntityTable> expectedResult = new ArrayList<>();
		when (kpiDao.countAssignmentsByIndustryByStatusDatatable(WorkStatusType.DRAFT, kpiRequest)).thenReturn(expectedResult);
		kpiReportFactory.getKPIAggregateEntityTableData(kpiReportType, kpiRequest);
	}

	@Test
	public void test_isFilterPresent_normalExecution() {
		filter.setName(kpiReportFilter);
		values.add("1");
		filter.setValues(values);
		filters.add(filter);

		boolean result = kpiReportFactory.isFilterPresent(filters, kpiReportFilter);
		assertTrue(result);
	}

	@Test
	public void test_isFilterPresent_filterNotPresent() {
		boolean result = kpiReportFactory.isFilterPresent(filters, kpiReportFilter);
		assertFalse(result);
	}
}
