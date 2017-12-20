package com.workmarket.service.report.kpi;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.dao.report.kpi.KpiDAO;
import com.workmarket.data.report.internal.AssignmentReport;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIAggregateEntityTable;
import com.workmarket.domains.model.kpi.KPIChartResponse;
import com.workmarket.domains.model.kpi.KPIDataTableResponse;
import com.workmarket.domains.model.kpi.KPIReportFilter;
import com.workmarket.domains.model.kpi.KPIReportType;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.WorkFacetResultType;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.report.kpi.cache.KPICache;
import com.workmarket.service.search.work.WorkSearchServiceImpl;
import com.workmarket.test.IntegrationTest;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by theogugoiu on 2/13/14.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(IntegrationTest.class)
public class KpiServiceTest {

	@Mock private KpiReportFactory kpiReportFactory;
	@Mock private KpiDAO kpiDao;
	@Mock private RatingService ratingService;
	@Mock private WorkSearchServiceImpl workSearchService;
	@Mock private Calendar fromDate;
	@Mock private Calendar toDate;
	@Mock private Map facetMap;
	@Mock private List facetList;
	@Mock private Iterator facetIterator;
	@Mock private WorkSearchResponse workSearchResponse;
	@Mock private KPICache kpiCache;

	@InjectMocks private KpiServiceImpl kpiService;

	private Long companyId = 1000L;
	private Long userId = 1L;
	private Optional<AssignmentReport> assignmentReportOptional = Optional.absent();
	private Optional<KPIChartResponse> kpiChartResponseOptional = Optional.absent();

	@Before
	public void setup() {
	}

	@Test
	public void getKPITabularData_normalExecution() throws Exception {
		KPIRequest request = new KPIRequest();
		request.setFrom(Calendar.getInstance());
		request.setTo(Calendar.getInstance());
		List<KPIAggregateEntityTable> tabularDataResponse = new ArrayList<>();

		when(kpiReportFactory.getKPIAggregateEntityTableData(any(KPIReportType.class), eq(request))).thenReturn(tabularDataResponse);

		KPIDataTableResponse result = kpiService.getKPITabularData(request);
		assertNotNull(result);
	}

	@Test
	public void createKPIRequestForStatisticsAndCreateDateRangeTestThroughputDaily() {
		String requestId = "button-throughput-daily";
		KPIRequest resultKpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		assertNotNull(resultKpiRequest);
		Calendar returnFrom = resultKpiRequest.getFrom();
		Calendar returnTo = resultKpiRequest.getTo();
		assertNotNull(returnFrom);
		assertNotNull(returnTo);
		assertFalse(returnFrom.equals(returnTo));
	}

	@Test
	public void createKPIRequestForStatisticsAndCreateDateRangeTestThroughputWeekly() {
		String requestId = "button-throughput-weekly";
		KPIRequest resultKpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		assertNotNull(resultKpiRequest);
		Calendar returnFrom = resultKpiRequest.getFrom();
		Calendar returnTo = resultKpiRequest.getTo();
		assertNotNull(returnFrom);
		assertNotNull(returnTo);
		assertFalse(returnFrom.equals(returnTo));
	}

	@Test
	public void createKPIRequestForStatisticsAndCreateDateRangeTestThroughputMonthly() {
		String requestId = "button-throughput-monthly";
		KPIRequest resultKpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		assertNotNull(resultKpiRequest);
		Calendar returnFrom = resultKpiRequest.getFrom();
		Calendar returnTo = resultKpiRequest.getTo();
		assertNotNull(returnFrom);
		assertNotNull(returnTo);
		assertFalse(returnFrom.equals(returnTo));
	}

	@Test
	public void createKPIRequestForStatisticsAndCreateDateRangeTestThroughputQuarterly() {
		String requestId = "button-throughput-quarterly";
		KPIRequest resultKpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		assertNotNull(resultKpiRequest);
		Calendar returnFrom = resultKpiRequest.getFrom();
		Calendar returnTo = resultKpiRequest.getTo();
		assertNotNull(returnFrom);
		assertNotNull(returnTo);
		assertFalse(returnFrom.equals(returnTo));
	}

	@Test
	public void createKPIRequestForStatisticsAndCreateDateRangeTestThroughputYearly() {
		String requestId = "button-throughput-yearly";
		KPIRequest resultKpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		assertNotNull(resultKpiRequest);
		Calendar returnFrom = resultKpiRequest.getFrom();
		Calendar returnTo = resultKpiRequest.getTo();
		assertNotNull(returnFrom);
		assertNotNull(returnTo);
		assertFalse(returnFrom.equals(returnTo));
	}

	@Test
	public void createKPIRequestForStatisticsAndCreateDateRangeTestLifeCycleDaily() {
		String requestId = "button-life-cycle-daily";
		KPIRequest resultKpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		assertNotNull(resultKpiRequest);
		Calendar returnFrom = resultKpiRequest.getFrom();
		Calendar returnTo = resultKpiRequest.getTo();
		assertNotNull(returnFrom);
		assertNotNull(returnTo);
		assertFalse(returnFrom.equals(returnTo));
	}

	@Test
	public void createKPIRequestForStatisticsAndCreateDateRangeTestSegmentationWeekly() {
		String requestId = "button-segmentation-weekly";
		KPIRequest resultKpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		assertNotNull(resultKpiRequest);
		Calendar returnFrom = resultKpiRequest.getFrom();
		Calendar returnTo = resultKpiRequest.getTo();
		assertNotNull(returnFrom);
		assertNotNull(returnTo);
		assertFalse(returnFrom.equals(returnTo));
	}

	@Test
	public void createKPIRequestForStatisticsAndCreateDateRangeTestClientsMonthly() {
		String requestId = "button-clients-monthly";
		KPIRequest resultKpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		assertNotNull(resultKpiRequest);
		Calendar returnFrom = resultKpiRequest.getFrom();
		Calendar returnTo = resultKpiRequest.getTo();
		assertNotNull(returnFrom);
		assertNotNull(returnTo);
		assertFalse(returnFrom.equals(returnTo));
	}

	@Test
	public void createKPIRequestForStatisticsAndCreateDateRangeTestMarketQuarterly() {
		String requestId = "button-market-quarterly";
		KPIRequest resultKpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		assertNotNull(resultKpiRequest);
		Calendar returnFrom = resultKpiRequest.getFrom();
		Calendar returnTo = resultKpiRequest.getTo();
		assertNotNull(returnFrom);
		assertNotNull(returnTo);
		assertFalse(returnFrom.equals(returnTo));
	}

	@Test
	public void createKPIRequestForStatisticsAndCreateDateRangeTestCustomYearly() {
		String requestId = "button-custom-yearly";
		KPIRequest resultKpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		assertNotNull(resultKpiRequest);
		Calendar returnFrom = resultKpiRequest.getFrom();
		Calendar returnTo = resultKpiRequest.getTo();
		assertNotNull(returnFrom);
		assertNotNull(returnTo);
		assertFalse(returnFrom.equals(returnTo));
	}

	@Test
	public void populateStatisticsDataWithKPIReportsThroughput() {
		JSONObject data = new JSONObject();
		String requestId = "button-throughput-monthly";

		KPIRequest kpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(companyId.toString())));

		Optional<KPIChartResponse> response = Optional.absent();
		when(kpiCache.get(kpiRequest, companyId)).thenReturn(response);

		try{
			kpiService.populateStatisticsDataWithKPIReports(kpiRequest, data, requestId, companyId, userId);
			assertNotNull(data);
			assertTrue(data.has("totalValueInAssignmentsSent"));
			assertTrue(data.has("assignmentsSent"));
		} catch (Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void populateStatisticsDataWithKPIReportsLifeCycle() {
		JSONObject data = new JSONObject();
		String requestId = "button-life-cycle-monthly";

		KPIRequest kpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(companyId.toString())));

		Optional<KPIChartResponse> response = Optional.absent();
		when(kpiCache.get(kpiRequest, companyId)).thenReturn(response);

		try{
			kpiService.populateStatisticsDataWithKPIReports(kpiRequest, data, requestId, companyId, userId);
			assertNotNull(data);
			assertTrue(data.has("AVERAGE_HOURS_ASSIGNMENT_COMPLETE_TO_CLOSED"));
			assertTrue(data.has("AVERAGE_HOURS_ASSIGNMENT_START_TO_COMPLETE"));
			assertTrue(data.has("AVERAGE_HOURS_ASSIGNMENT_SENT_TO_START"));
			assertTrue(data.has("AVERAGE_HOURS_ASSIGNMENT_CLOSED_TO_PAID"));
		} catch (Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void populateStatisticsDataWithKPIReportsSegmentationAssignment() {
		JSONObject data = new JSONObject();
		String requestId = "button-segmentation-assignment-monthly";

		KPIRequest kpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(companyId.toString())));

		when(kpiReportFactory.isFilterPresent(kpiRequest.getFilters(), KPIReportFilter.COMPANY)).thenReturn(true);
		when(kpiDao.getAssignmentSegmentationReportAssignment(kpiRequest)).thenReturn(new AssignmentReport());
		when(kpiCache.getAssignmentReport(any(KPIRequest.class), anyLong())).thenReturn(assignmentReportOptional);
		try{

			kpiService.populateStatisticsDataWithKPIReports(kpiRequest, data, requestId, companyId, userId);
			assertNotNull(data);
			assertTrue(data.has("assignmentSegmentationReportAssignment"));
		} catch (Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void populateStatisticsDataWithKPIReportsSegmentationRouting() {
		JSONObject data = new JSONObject();
		String requestId = "button-segmentation-routing-monthly";

		KPIRequest kpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(companyId.toString())));

		when(kpiReportFactory.isFilterPresent(kpiRequest.getFilters(), KPIReportFilter.COMPANY)).thenReturn(true);
		when(kpiDao.getAssignmentSegmentationReportRouting(kpiRequest)).thenReturn(new AssignmentReport());
		when(kpiCache.get(any(KPIRequest.class), anyLong())).thenReturn(kpiChartResponseOptional);
		when(kpiCache.getAssignmentReport(any(KPIRequest.class), anyLong())).thenReturn(assignmentReportOptional);
		try{

			kpiService.populateStatisticsDataWithKPIReports(kpiRequest, data, requestId, companyId, userId);
			assertNotNull(data);
			assertTrue(data.has("assignmentSegmentationReportRouting"));
		} catch (Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void populateStatisticsDataWithKPIReportsClients() {
		JSONObject data = new JSONObject();
		String requestId = "button-clients-monthly";

		KPIRequest kpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(companyId.toString())));

		when(kpiReportFactory.isFilterPresent(kpiRequest.getFilters(), KPIReportFilter.COMPANY)).thenReturn(true);

		try{
			kpiService.populateStatisticsDataWithKPIReports(kpiRequest, data, requestId, companyId, userId);
			assertNotNull(data);
			assertTrue(data.has("topProjects"));
			assertTrue(data.has("topUsers"));
		} catch (Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void populateStatisticsDataWithKPIReportsMarket() {
		JSONObject data = new JSONObject();
		String requestId = "button-market-monthly";

		KPIRequest kpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(companyId.toString())));

		when(kpiReportFactory.isFilterPresent(kpiRequest.getFilters(), KPIReportFilter.COMPANY)).thenReturn(true);

		when(workSearchService.searchAllWorkByCompanyId(anyLong(), any(WorkSearchRequest.class)))
				.thenReturn(workSearchResponse);
		when(workSearchResponse.getFacets())
				.thenReturn(facetMap);
		when(facetMap.get(any(WorkFacetResultType.class)))
				.thenReturn(facetList);
		when(facetList.iterator()).thenReturn(facetIterator);
		when(facetIterator.hasNext()).thenReturn(false);

		try{
			kpiService.populateStatisticsDataWithKPIReports(kpiRequest, data, requestId, companyId, userId);
			assertNotNull(data);
			assertTrue(data.has("topResources"));
		} catch (Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void populateStatisticsDataWithKPIReportsInvalid() {
		JSONObject data = new JSONObject();
		String requestId = "button-market-monthly";

		KPIRequest kpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
		kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(companyId.toString())));

		try{
			kpiService.populateStatisticsDataWithKPIReports(kpiRequest, data, requestId, companyId, userId);
			assertTrue(false);
		} catch (Exception e){
			assertTrue(true);
		}
	}


	@Test
	public void createKPIRequestForKPIReportTestBasic() {
		KPIRequest kpiRequest = kpiService.createKPIRequestForKPIReport(companyId, fromDate, toDate);
		assertNotNull(kpiRequest);

		assertEquals(1, kpiRequest.getFilters().size());
	}

	@Test
	public void createKPIRequestForKPIReportTestBasicWithNullDatesShouldCreateDefaultDateRangeAndPass() {
		KPIRequest kpiRequest = kpiService.createKPIRequestForKPIReport(companyId, null, null);
		assertNotNull(kpiRequest);
		assertNotNull(kpiRequest.getFrom());
		assertNotNull(kpiRequest.getTo());
	}

	@Test
	public void createKPIRequestForKPIReportTestShouldFailWithNullCompanyId() {
		try{
			kpiService.createKPIRequestForKPIReport(null, fromDate, toDate);
			assertTrue(false);
		} catch (Exception e) {

		}
	}

	@Test
	public void generateKPIReportsNumberTestBasic() {
		KPIRequest kpiRequest = kpiService.createKPIRequestForKPIReport(companyId, fromDate, toDate);

		Map<String, Object> result = kpiService.generateKPIReports(kpiRequest);

		assertNotNull(result);

		assertEquals(result.size(), 17);

		assertTrue(result.containsKey("Funding"));
		assertTrue(result.containsKey("Assignments Created"));
		assertTrue(result.containsKey("Throughput"));
		assertTrue(result.containsKey("Approved for Payment"));
		assertTrue(result.containsKey("Assignments Sent"));
		assertTrue(result.containsKey("Total Value in Assignments Sent"));
		assertTrue(result.containsKey("Total Value in Pending Payment"));
		assertTrue(result.containsKey("Assignments Cancelled"));
		assertTrue(result.containsKey("Total Value in Cancellations"));
		assertTrue(result.containsKey("Total Groups"));
		assertTrue(result.containsKey("Voided"));
		assertTrue(result.containsKey("Total Value in Voided"));
		assertTrue(result.containsKey("Resources in Groups"));
		assertTrue(result.containsKey("Tests Created"));
		assertTrue(result.containsKey("% Payments within Terms"));
		assertTrue(result.containsKey("Avg Time to Approve (days)"));
		assertTrue(result.containsKey("Avg Time to Pay (days)"));

		assertFalse(result.containsKey("Nonexistent Report"));
	}

	@Test
	public void getAssignmentSegmentationReportAssignment(){
		KPIRequest kpiRequest = kpiService.createKPIRequestForKPIReport(companyId, fromDate, toDate);
		AssignmentReport returnReport = new AssignmentReport();
		when(kpiReportFactory.isFilterPresent(kpiRequest.getFilters(), KPIReportFilter.COMPANY)).thenReturn(true);
		when(kpiDao.getAssignmentSegmentationReportAssignment(kpiRequest)).thenReturn(returnReport);
		when(kpiCache.getAssignmentReport(any(KPIRequest.class), anyLong())).thenReturn(assignmentReportOptional);
		kpiService.getAssignmentSegmentationReportAssignment(kpiRequest, companyId);
	}

	@Test
	public void getAssignmentSegmentationReportRouting(){
		KPIRequest kpiRequest = kpiService.createKPIRequestForKPIReport(companyId, fromDate, toDate);
		AssignmentReport returnReport = new AssignmentReport();
		when(kpiReportFactory.isFilterPresent(kpiRequest.getFilters(), KPIReportFilter.COMPANY)).thenReturn(true);
		when(kpiDao.getAssignmentSegmentationReportRouting(kpiRequest)).thenReturn(returnReport);
		when(kpiCache.getAssignmentReport(any(KPIRequest.class), anyLong())).thenReturn(assignmentReportOptional);
		kpiService.getAssignmentSegmentationReportRouting(kpiRequest, companyId);
	}

	@Test
	public void getPaidWorkCountsGroupedByCounty() {
		when(workSearchService.searchAllWorkByCompanyId(anyLong(), any(WorkSearchRequest.class)))
				.thenReturn(workSearchResponse);
		when(workSearchResponse.getFacets())
				.thenReturn(facetMap);
		when(facetMap.get(any(WorkFacetResultType.class)))
				.thenReturn(facetList);
		when(facetList.iterator()).thenReturn(facetIterator);
		when(facetIterator.hasNext()).thenReturn(false);

		kpiService.getPaidWorkCountsGroupedByCounty(userId, fromDate, toDate);
		verify(workSearchService).searchAllWorkByCompanyId(anyLong(), any(WorkSearchRequest.class));
	}

}
