package com.workmarket.service.report.kpi;

import com.google.common.collect.Lists;
import com.workmarket.data.report.internal.TopUser;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIChartResponse;
import com.workmarket.domains.model.kpi.KPIReportAggregateInterval;
import com.workmarket.domains.model.kpi.KPIReportFilter;
import com.workmarket.domains.model.kpi.KPIReportType;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class KPIServiceIT extends BaseServiceIT {

	@Autowired
	private KpiService kpiService;

	private Long USER_ID = 1L;

	@Test
	public void getKPIChartSALES_THROUGHPUT_ACTUAL() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.SALES_THROUGHPUT_ACTUAL);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -2);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartSalesCommissionsActual() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.SALES_FEES_ACTUAL);
		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -2);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartAssignmentsTotalInDraft() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.ASSIGNMENTS_TOTAL_IN_DRAFT);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -2);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartAssignmentsValueAverage() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.ASSIGNMENTS_VALUE_AVERAGE);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -2);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartAvailableApAndCashTotal() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.AVAILABLE_AP_AND_CASH_TOTAL);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		reportRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, companyIds));
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -2);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartNewBuyerSignUps() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.NEW_BUYER_SIGNUPS);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		reportRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, companyIds));
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -2);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartBuyerSendingFirstAssignment() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.BUYERS_SENDING_FIRST_ASSIGNMENT);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -2);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartNewSignupsAverageNumberOfAssignments() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.NEW_SIGNUPS_AVERAGE_NUMBER_OF_ASSIGNMENTS);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -2);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartPerfectOfBuyersSendingFirstAssignmentTransactional() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.PERCENT_OF_BUYERS_SENDING_FIRST_ASSIGNMENT_TRANSACTIONAL);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -2);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartPerfectOfBuyersSendingFirstAssignmentSubscription() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.PERCENT_OF_BUYERS_SENDING_FIRST_ASSIGNMENT_SUBSCRIPTION);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -2);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartResourcesRecceivingOneAssignment() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.RESOURCES_RECEIVING_ONE_ASSIGNMENT);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		reportRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, companyIds));
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -2);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartAvailableWithdrawableCashOnAccount() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.AVAILABLE_WITHDRAWABLE_CASH_ON_ACCOUNT);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		reportRequest.addToFilters(new Filter(KPIReportFilter.PAYMENT_TERMS, companyIds));
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -1);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getKPIChartAvailableCashOnAccount() throws Exception {
		KPIRequest reportRequest = new KPIRequest();
		reportRequest.setReportType(KPIReportType.AVAILABLE_CASH_ON_ACCOUNT);

		reportRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		reportRequest.addToFilters(new Filter(KPIReportFilter.PAYMENT_TERMS, companyIds));
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -1);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DAY_OF_MONTH, 1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		KPIChartResponse response = kpiService.getKPIChart(reportRequest);
		assertNotNull(response);
	}

	@Test
	public void getTopUsersByCompany() throws Exception {
		KPIRequest reportRequest = new KPIRequest();

		List<String> companyIds = Lists.newArrayList();
		companyIds.add("1");
		reportRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, companyIds));
		Calendar from = Calendar.getInstance();
		from.add(Calendar.YEAR, -1);

		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance());
		List<TopUser> response = kpiService.getTopUsersByCompany(reportRequest, 5);
		assertNotNull(response);
		for (TopUser t : response) {
			assertNotNull(t.getUserNumber());
			assertNotNull(t.getUserId());
			assertNotNull(t.getFirstName());
			assertNotNull(t.getLastName());
			assertNotNull(t.getRating());
			assertNotNull(t.getSentAssignments());
		}
	}

}