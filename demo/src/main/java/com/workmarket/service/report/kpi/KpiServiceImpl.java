package com.workmarket.service.report.kpi;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.workmarket.configuration.Constants;
import com.workmarket.dao.report.kpi.KpiDAO;
import com.workmarket.data.report.internal.AssignmentReport;
import com.workmarket.data.report.internal.SnapshotReport;
import com.workmarket.data.report.internal.TopEntity;
import com.workmarket.data.report.internal.TopUser;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIAggregateEntityTable;
import com.workmarket.domains.model.kpi.KPIChartResponse;
import com.workmarket.domains.model.kpi.KPIDataTableResponse;
import com.workmarket.domains.model.kpi.KPIReportAggregateInterval;
import com.workmarket.domains.model.kpi.KPIReportException;
import com.workmarket.domains.model.kpi.KPIReportFilter;
import com.workmarket.domains.model.kpi.KPIReportType;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.response.FacetResult;
import com.workmarket.search.response.work.WorkFacetResultType;
import com.workmarket.search.response.work.WorkMilestoneFilter;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.report.kpi.cache.KPICache;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Component
public class KpiServiceImpl implements KpiService {

	private static final Log logger = LogFactory.getLog(KpiServiceImpl.class);
	private static final String BUTTON_LABEL_PREFIX = "button-";

	@Autowired private KpiReportFactory kpiReportFactory;
	@Autowired private KpiDAO kpiDao;
	@Autowired private RatingService ratingService;
	@Autowired private WorkSearchService workSearchService;
	@Autowired private KPICache kpiCache;

	@Override
	public KPIChartResponse getKPIChart(KPIRequest request) throws KPIReportException {
		KPIReportType kpiReportType = request.getReportType();
		KPIRequest kpiRequest = validateKpiRequest(request);

		List<DataPoint> chartData = kpiReportFactory.getKpiReportChartData(kpiReportType, kpiRequest);
		KPIChartResponse response = new KPIChartResponse();
		response.setChartData(chartData);
		return response;
	}

	@Override
	public KPIDataTableResponse getKPITabularData(KPIRequest request) throws KPIReportException {
		KPIReportType kpiReportType = request.getReportType();
		KPIRequest kpiRequest = validateKpiRequest(request);

		List<KPIAggregateEntityTable> tabularData = kpiReportFactory.getKPIAggregateEntityTableData(kpiReportType, kpiRequest);
		KPIDataTableResponse response = new KPIDataTableResponse();
		response.setTabularData(tabularData);
		return response;
	}

	/**
	 * @param kpiReportRequest
	 * @return KPIRequest
	 */
	private KPIRequest validateKpiRequest(KPIRequest kpiReportRequest) {
		Assert.notNull(kpiReportRequest.getTo(), "Invalid date filter, missing through value");

		logger.debug(kpiReportRequest);
		Calendar to = kpiReportRequest.getTo();
		to = DateUtilities.getMidnightNextDayRelativeToTimezone(to, Constants.EST_TIME_ZONE);

		Calendar from = (Calendar) to.clone();

		if (kpiReportRequest.isSetFrom()) {
			from = kpiReportRequest.getFrom();
			from = DateUtilities.newCalendar(from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH), 0, 0, 0, Constants.EST_TIME_ZONE);
		} else {
			switch (kpiReportRequest.getAggregateInterval()) {
				case DAY_OF_MONTH:
					from.add(Calendar.DAY_OF_YEAR, -12);
					break;
				case MONTH_OF_YEAR:
					from.add(Calendar.MONTH, -12);
					break;
				case WEEK_OF_YEAR:
					from.add(Calendar.WEEK_OF_YEAR, -12);
					break;
				default:
					//YEAR
					from.add(Calendar.YEAR, -12);
					break;
			}
		}

		if (!kpiReportRequest.isSetFilters()) {
			List<Filter> filters = Lists.newArrayList();
			kpiReportRequest.setFilters(filters);
		}

		kpiReportRequest.setFrom(from);
		kpiReportRequest.setTo(to);
		return kpiReportRequest;
	}

	@Override
	public List<TopUser> getTopUsersByCompany(KPIRequest request, Integer topLimit) {
		Assert.notNull(topLimit);
		Assert.isTrue(kpiReportFactory.isFilterPresent(request.getFilters(), KPIReportFilter.COMPANY), "Company filter is required");
		KPIRequest kpiRequest = validateKpiRequest(request);
		List<TopUser> topUsers = kpiDao.getTopUsersByCompany(kpiRequest, topLimit);
		for (TopUser t : topUsers) {
			t.setRating(ratingService.findSatisfactionRateForUser(t.getUserId()));
		}
		return topUsers;
	}

	@Override
	public List<TopEntity> getTopProjectsByCompany(KPIRequest request, Integer topLimit) {
		Assert.notNull(topLimit);
		Assert.isTrue(kpiReportFactory.isFilterPresent(request.getFilters(), KPIReportFilter.COMPANY), "Company filter is required");
		KPIRequest kpiRequest = validateKpiRequest(request);
		List<TopEntity> topEntities = kpiDao.getTopProjectsByCompany(kpiRequest, topLimit);
		for (TopEntity t : topEntities) {
			t.setType("Project");
		}
		return topEntities;
	}

	@Override
	public List<TopEntity> getTopResourcesByCompany(KPIRequest request, Integer topLimit) {
		Assert.notNull(topLimit);
		Assert.isTrue(kpiReportFactory.isFilterPresent(request.getFilters(), KPIReportFilter.COMPANY), "Company filter is required");
		KPIRequest kpiRequest = validateKpiRequest(request);
		List<TopEntity> topEntities = kpiDao.getTopResourcesByCompany(kpiRequest, topLimit);
		for (TopEntity t : topEntities) {
			t.setType("Resource");
		}
		return topEntities;
	}

	@Override
	public AssignmentReport getAssignmentSegmentationReportAssignment(KPIRequest request, long companyId) {
		request.setReportType(KPIReportType.ASSIGNMENT_SEGMENTATION_REPORT);
		AssignmentReport assignmentReport;
		Assert.isTrue(kpiReportFactory.isFilterPresent(request.getFilters(), KPIReportFilter.COMPANY), "Company filter is required");
		KPIRequest kpiRequest = validateKpiRequest(request);

		Optional<AssignmentReport> assignmentReportOptional = kpiCache.getAssignmentReport(kpiRequest, companyId);
		if (assignmentReportOptional.isPresent()) {
			return assignmentReportOptional.get();
		} else {
			assignmentReport = kpiDao.getAssignmentSegmentationReportAssignment(kpiRequest);
			kpiCache.put(request, companyId, assignmentReport);
		}
		return assignmentReport;
	}

	@Override
	public AssignmentReport getAssignmentSegmentationReportRouting(KPIRequest request, long companyId) {
		request.setReportType(KPIReportType.ROUTING_SEGMENTATION_REPORT);
		AssignmentReport assignmentReport;
		Assert.isTrue(kpiReportFactory.isFilterPresent(request.getFilters(), KPIReportFilter.COMPANY), "Company filter is required");
		KPIRequest kpiRequest = validateKpiRequest(request);

		Optional<AssignmentReport> assignmentReportOptional = kpiCache.getAssignmentReport(kpiRequest, companyId);
		if (assignmentReportOptional.isPresent()) {
			return assignmentReportOptional.get();
		} else {
			assignmentReport = kpiDao.getAssignmentSegmentationReportRouting(kpiRequest);
			kpiCache.put(request, companyId, assignmentReport);
		}
		return assignmentReport;
	}

	@Override
	public SnapshotReport getAssignmentSnapshotDataPointsForCompany(KPIRequest request, long companyId) throws KPIReportException{
		KPIRequest kpiRequest = validateKpiRequest(request);
		request.setReportType(KPIReportType.SNAPSHOT_REPORT);

		SnapshotReport assignmentSnapshotReport;
		Optional<SnapshotReport> assignmentReportOptional = kpiCache.getSnapshotReport(kpiRequest, companyId);
		if (assignmentReportOptional.isPresent()) {
			assignmentSnapshotReport = assignmentReportOptional.get();
		} else {
			assignmentSnapshotReport = kpiDao.getSnapshotReportVoidRateAndLifeCycle(kpiRequest);
			kpiCache.put(request, companyId, assignmentSnapshotReport);
		}

		KPIChartResponse total_in_paid;
		kpiRequest.setReportType(KPIReportType.ASSIGNMENTS_TOTAL_IN_PAID);
		Optional<KPIChartResponse> chartResponse = kpiCache.get(kpiRequest, companyId);
		if (chartResponse.isPresent()) {
			total_in_paid = chartResponse.get();
		} else {
			total_in_paid = getKPIChart(kpiRequest);
			kpiCache.put(kpiRequest, companyId, total_in_paid);
		}

		KPIChartResponse value_in_paid;
		kpiRequest.setReportType(KPIReportType.ASSIGNMENTS_VALUE_IN_PAID);
		chartResponse = kpiCache.get(kpiRequest, companyId);
		if (chartResponse.isPresent()) {
			value_in_paid = chartResponse.get();
		} else {
			value_in_paid = getKPIChart(kpiRequest);
			kpiCache.put(kpiRequest, companyId, value_in_paid);
		}

		for (DataPoint dp : total_in_paid.getChartData()){
			SnapshotReport.SnapshotDataPoint newDp = new SnapshotReport.SnapshotDataPoint();
			newDp.setAssignmentsSentCount(dp.getY());
			Calendar date = DateUtilities.getCalendarFromMillis(dp.getX());
			newDp.setTimeInMillis(dp.getX());
			assignmentSnapshotReport.addDataAtYearAndMonth(newDp, date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1);
		}

		for (DataPoint dp : value_in_paid.getChartData()){
			SnapshotReport.SnapshotDataPoint newDp = new SnapshotReport.SnapshotDataPoint();
			newDp.setAssignmentsSent(dp.getY());
			Calendar date = DateUtilities.getCalendarFromMillis(dp.getX());
			newDp.setTimeInMillis(dp.getX());
			assignmentSnapshotReport.addDataAtYearAndMonth(newDp, date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1);
		}

		return assignmentSnapshotReport;
	}

	@Override
	public KPIRequest createKPIRequestForStatisticsAndCreateDateRange(String requestId) {
		KPIRequest kpiRequest = new KPIRequest();
		requestId = (requestId.startsWith(BUTTON_LABEL_PREFIX)) ? requestId.replaceFirst(BUTTON_LABEL_PREFIX, "") : requestId;
		Calendar from = DateUtilities.getCalendarNow(Constants.WM_TIME_ZONE);
		Calendar to = DateUtilities.getCalendarNow(Constants.WM_TIME_ZONE);

		if (requestId.startsWith("throughput")) {
			to = DateUtilities.getMidnightRelativeToTimezone(from, TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE));
			from = DateUtilities.getMidnightRelativeToTimezone(from, TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE));

			if (requestId.equals("throughput-daily")) {
				kpiRequest.setAggregateInterval(KPIReportAggregateInterval.DAY_OF_MONTH);
				from.add(Calendar.DAY_OF_MONTH, -12);
			}
			if (requestId.equals("throughput-weekly")) {
				kpiRequest.setAggregateInterval(KPIReportAggregateInterval.WEEK_OF_YEAR);
				from.add(Calendar.WEEK_OF_YEAR, -12);
			}
			if (requestId.equals("throughput-monthly")) {
				kpiRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);
				from.add(Calendar.YEAR, -1);
			}
			if (requestId.equals("throughput-quarterly")) {
				kpiRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);    //QUARTERLY AGGREGATE UNIMPLEMENTED
				from.add(Calendar.YEAR, -3);
			}
			if (requestId.equals("throughput-yearly")) {
				kpiRequest.setAggregateInterval(KPIReportAggregateInterval.YEAR);
				from.add(Calendar.YEAR, -12);
			}
		} else {
			if (requestId.endsWith("daily")) {
				kpiRequest.setAggregateInterval(KPIReportAggregateInterval.DAY_OF_MONTH);
				from = DateUtilities.getCalendarWithTime(0, 0, Constants.DEFAULT_TIMEZONE);
			}
			if (requestId.endsWith("weekly")) {
				kpiRequest.setAggregateInterval(KPIReportAggregateInterval.WEEK_OF_YEAR);
				from = DateUtilities.getCalendarWithFirstDayOfWeek(DateUtilities.getCalendarWithTime(0, 0, Constants.DEFAULT_TIMEZONE), TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE));
			}
			if (requestId.endsWith("monthly")) {
				kpiRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);
				from = DateUtilities.getCalendarWithFirstDayOfTheMonth(to, TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE));
			}
			if (requestId.endsWith("quarterly")) {
				kpiRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);
				from = DateUtilities.getCalendarWithBeginningOfQuarter(to, TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE));
			}
			if (requestId.endsWith("yearly")) {
				kpiRequest.setAggregateInterval(KPIReportAggregateInterval.YEAR);
				from = DateUtilities.getCalendarWithFirstDayOfYear(to, TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE));
			}
			if (requestId.endsWith("default")) {
				kpiRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);
				from.add(Calendar.YEAR, -1);
			}
		}

		kpiRequest.setFrom(from);
		kpiRequest.setTo(to);
		return kpiRequest;
	}

	private KPIChartResponse lastTimeSegmentFix(KPIChartResponse kcr) {
		List<DataPoint> chartData = kcr.getChartData();
		if (chartData.size() < 3) {
			return kcr;
		}
		chartData.get(chartData.size() - 1).setX(2 * chartData.get(chartData.size() - 2).getX() - chartData.get(chartData.size() - 3).getX());
		return kcr;
	}

	@Override
	public void populateStatisticsDataWithKPIReports(KPIRequest kpiRequest, JSONObject data, String requestId, Long companyId, Long userId) throws org.json.JSONException, KPIReportException, UnsupportedOperationException {
		if (requestId.startsWith("button-throughput")) {
			KPIChartResponse sent;
			kpiRequest.setReportType(KPIReportType.ASSIGNMENTS_TOTAL_IN_PAID);
			Optional<KPIChartResponse> chartResponse = kpiCache.get(kpiRequest, companyId);
			if (chartResponse.isPresent()) {
				sent = chartResponse.get();
			} else {
				sent = getKPIChart(kpiRequest);
				kpiCache.put(kpiRequest, companyId, sent);
			}

			KPIChartResponse totalAssignmentsSent;
			kpiRequest.setReportType(KPIReportType.ASSIGNMENTS_VALUE_IN_PAID);
			chartResponse = kpiCache.get(kpiRequest, companyId);

			if (chartResponse.isPresent()) {
				totalAssignmentsSent = chartResponse.get();
			} else {
				totalAssignmentsSent = getKPIChart(kpiRequest);
				kpiCache.put(kpiRequest, companyId, totalAssignmentsSent);
			}

			sent = lastTimeSegmentFix(sent);
			totalAssignmentsSent = lastTimeSegmentFix(totalAssignmentsSent);

			data.put("assignmentsSent", new JSONObject(sent));
			data.put("totalValueInAssignmentsSent", new JSONObject(totalAssignmentsSent));

		} else if (requestId.startsWith("button-life-cycle")) {

			kpiRequest.setReportType(KPIReportType.AVERAGE_HOURS_ASSIGNMENT_SENT_TO_START);
			KPIChartResponse averageHoursAssignmentSentToStart;
			Optional<KPIChartResponse> chartResponse = kpiCache.get(kpiRequest, companyId);
			if (chartResponse.isPresent()) {
			 	averageHoursAssignmentSentToStart = chartResponse.get();
			} else {
				averageHoursAssignmentSentToStart = getKPIChart(kpiRequest);
				kpiCache.put(kpiRequest, companyId, averageHoursAssignmentSentToStart);
			}

			kpiRequest.setReportType(KPIReportType.AVERAGE_HOURS_ASSIGNMENT_START_TO_COMPLETE);
			chartResponse = kpiCache.get(kpiRequest, companyId);
			KPIChartResponse averageHoursAssignmentStartToComplete;
			if (chartResponse.isPresent()) {
				averageHoursAssignmentStartToComplete = chartResponse.get();
			} else {
				averageHoursAssignmentStartToComplete = getKPIChart(kpiRequest);
				kpiCache.put(kpiRequest, companyId, averageHoursAssignmentStartToComplete);
			}

			kpiRequest.setReportType(KPIReportType.AVERAGE_HOURS_ASSIGNMENT_COMPLETE_TO_CLOSED);
			chartResponse = kpiCache.get(kpiRequest, companyId);
			KPIChartResponse averageHoursAssignmentCompleteToClosed;
			if (chartResponse.isPresent()) {
				averageHoursAssignmentCompleteToClosed = chartResponse.get();
			} else {
				averageHoursAssignmentCompleteToClosed = getKPIChart(kpiRequest);
				kpiCache.put(kpiRequest, companyId, averageHoursAssignmentCompleteToClosed);
			}

			kpiRequest.setReportType(KPIReportType.AVERAGE_HOURS_ASSIGNMENT_CLOSED_TO_PAID);
			chartResponse = kpiCache.get(kpiRequest, companyId);
			KPIChartResponse averageHoursAssignmentClosedToPaid;
			if (chartResponse.isPresent()) {
				averageHoursAssignmentClosedToPaid = chartResponse.get();
			} else {
				averageHoursAssignmentClosedToPaid = getKPIChart(kpiRequest);
				kpiCache.put(kpiRequest, companyId, averageHoursAssignmentClosedToPaid);
			}

			data.put("AVERAGE_HOURS_ASSIGNMENT_SENT_TO_START", new JSONObject(averageHoursAssignmentSentToStart));
			data.put("AVERAGE_HOURS_ASSIGNMENT_START_TO_COMPLETE", new JSONObject(averageHoursAssignmentStartToComplete));
			data.put("AVERAGE_HOURS_ASSIGNMENT_COMPLETE_TO_CLOSED", new JSONObject(averageHoursAssignmentCompleteToClosed));
			data.put("AVERAGE_HOURS_ASSIGNMENT_CLOSED_TO_PAID", new JSONObject(averageHoursAssignmentClosedToPaid));

		} else if (requestId.startsWith("button-segmentation-assignment")) {
			AssignmentReport assignmentSegmentationReport = getAssignmentSegmentationReportAssignment(kpiRequest, companyId);
			data.put("assignmentSegmentationReportAssignment", new JSONObject(assignmentSegmentationReport));
		} else if (requestId.startsWith("button-segmentation-routing")) {
			AssignmentReport assignmentSegmentationReport = getAssignmentSegmentationReportRouting(kpiRequest, companyId);
			data.put("assignmentSegmentationReportRouting", new JSONObject(assignmentSegmentationReport));

		} else if (requestId.startsWith("button-clients")) {

			List<TopUser> topUsers = getTopUsersByCompany(kpiRequest, 5);
			List<JSONObject> topUsersJSON = new ArrayList<>();
			for (TopUser topUser : topUsers) {
				topUsersJSON.add(new JSONObject(topUser));
			}
			data.put("topUsers", topUsersJSON);

			List<TopEntity> topProjects = getTopProjectsByCompany(kpiRequest, 5);
			List<JSONObject> topProjectsJSON = Lists.newArrayList();
			for (TopEntity topProject : topProjects) {
				topProjectsJSON.add(new JSONObject(topProject));
			}
			data.put("topProjects", topProjectsJSON);
		} else if (requestId.startsWith("button-market")) {

			List<JSONObject> topResourcesJSON = Lists.newArrayList();
			if (!Constants.WM_COMPANY_ID.equals(companyId)) {
				List<TopEntity> topResources = getTopResourcesByCompany(kpiRequest, 10);
				for (TopEntity topResource : topResources) {
					topResourcesJSON.add(new JSONObject(topResource));
				}
			}
			data.put("topResources", topResourcesJSON);

			List<JSONObject> countyData = Lists.newArrayList();
			List<JSONObject> topMarkets = Lists.newArrayListWithExpectedSize(10);

			List<Map<String, String>> paidWorkCountsGroupedByCounty = getPaidWorkCountsGroupedByCounty(companyId, kpiRequest.getFrom(), kpiRequest.getTo());

			int count = 0;
			for (Map<String, String> countyElementData : paidWorkCountsGroupedByCounty) {
				JSONObject object = new JSONObject()
						.accumulate("id", countyElementData.get("id"))
						.accumulate("rate", countyElementData.get("count"))
						.accumulate("name", countyElementData.get("name"));

				countyData.add(object);

				if (count < 10) {
					topMarkets.add(object);
				}
				count++;
			}

			data.put("countyData", countyData);
			data.put("topMarkets", topMarkets);

		} else if (requestId.startsWith("button-snapshot")){
			SnapshotReport assignmentSnapshotReport = getAssignmentSnapshotDataPointsForCompany(kpiRequest, companyId);

			data.put("snapshotReport", new JSONObject(assignmentSnapshotReport));

		} else {
			throw new UnsupportedOperationException("Unsupported report type: " + requestId);
		}
	}

	@Override
	public KPIRequest createKPIRequestForKPIReport(Long companyId, Calendar fromDate, Calendar toDate) {
		Assert.notNull(companyId);
		KPIRequest kpiRequest = new KPIRequest();
		kpiRequest.setAggregateInterval(KPIReportAggregateInterval.MONTH_OF_YEAR);

		Calendar fromDefault = Calendar.getInstance();
		fromDefault.add(Calendar.YEAR, -1);
		Calendar from = MoreObjects.firstNonNull(fromDate, fromDefault);
		kpiRequest.setFrom(from);

		Calendar toDefault = Calendar.getInstance();
		Calendar to = MoreObjects.firstNonNull(toDate, toDefault);
		kpiRequest.setTo(to);

		kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(companyId.toString())));
		return kpiRequest;
	}

	@Override
	public Map<String, Object> generateKPIReports(KPIRequest kpiRequest) {

		LinkedHashMap<String, KPIReportType> kpiReportTypes = new LinkedHashMap<>();
		kpiReportTypes.put("Funding", KPIReportType.TOTAL_FUNDING_BY_COMPANY);

		kpiReportTypes.put("Assignments Created", KPIReportType.ASSIGNMENTS_TOTAL_IN_DRAFT);
		kpiReportTypes.put("Throughput", KPIReportType.SALES_THROUGHPUT_ACTUAL);
		kpiReportTypes.put("Approved for Payment", KPIReportType.ASSIGNMENTS_VALUE_APPROVED_FOR_PAYMENT);

		kpiReportTypes.put("Assignments Sent", KPIReportType.ASSIGNMENTS_TOTAL_IN_SENT);
		kpiReportTypes.put("Total Value in Assignments Sent", KPIReportType.ASSIGNMENTS_VALUE_IN_SENT);
		kpiReportTypes.put("Total Value in Pending Payment", KPIReportType.ASSIGNMENTS_VALUE_IN_PENDING_APPROVAL);

		kpiReportTypes.put("Assignments Cancelled", KPIReportType.ASSIGNMENTS_CANCELLED);
		kpiReportTypes.put("Total Value in Cancellations", KPIReportType.ASSIGNMENTS_VALUE_IN_CANCELLED);
		kpiReportTypes.put("Total Groups", KPIReportType.GROUPS_CREATED);

		kpiReportTypes.put("Voided", KPIReportType.ASSIGNMENTS_VOIDED);
		kpiReportTypes.put("Total Value in Voided", KPIReportType.ASSIGNMENTS_VALUE_IN_VOIDED);
		kpiReportTypes.put("Resources in Groups", KPIReportType.GROUPS_TOTAL_MEMBERS);

		kpiReportTypes.put("Tests Created", KPIReportType.TESTS_CREATED);
		kpiReportTypes.put("% Payments within Terms", KPIReportType.PERCENTAGE_PAYMENT_WITHIN_TERMS);
		kpiReportTypes.put("Avg Time to Approve (days)", KPIReportType.AVERAGE_HOURS_COMPLETE_TO_PAID);

		kpiReportTypes.put("Avg Time to Pay (days)", KPIReportType.AVERAGE_HOURS_SENT_TO_PAID);

		Map<String, Object> kpiReportsGenerated = CollectionUtilities.newObjectMap();
		for (Map.Entry<String, KPIReportType> entry : kpiReportTypes.entrySet()) {
			try {
				kpiRequest.setReportType(entry.getValue());
				KPIChartResponse kpiChartResponse = getKPIChart(kpiRequest);
				kpiReportsGenerated.put(entry.getKey(), kpiChartResponse);
			} catch (Exception e) {
				logger.error("There was a problem generating a KPI report: " + entry.getKey(), e);
			}
		}

		return kpiReportsGenerated;
	}

	/**
	 * Takes a workSearchResponse's facets
	 * returns a list of maps containing county id,
	 * county name, and work count in county
	 *
	 * @param companyId
	 * @param fromDate, toDate
	 * @return List<Map>
	 */
	@Override
	public List<Map<String, String>> getPaidWorkCountsGroupedByCounty(Long companyId, Calendar fromDate, Calendar toDate) {
		Assert.notNull(companyId);
		Assert.notNull(fromDate);
		Assert.notNull(toDate);
		DateUtilities.isBefore(fromDate, toDate);

		WorkSearchRequest workSearchRequest = new WorkSearchRequest()
			.setFacetFields(
				ImmutableSet.of(
					WorkSearchableFields.COUNTY_ID,
					WorkSearchableFields.COUNTY_NAME
				)
			)
			.setDateRange(new DateRange(fromDate, toDate))
			.setWorkMilestoneFilter(WorkMilestoneFilter.PAID_DATE)
			.setWorkSearchRequestUserType(WorkSearchRequestUserType.CLIENT);

		workSearchRequest.setType(SearchType.WORK_KPI.toString());

		WorkSearchResponse workSearchResponse = workSearchService.searchAllWorkByCompanyId(companyId, workSearchRequest);
		return packageCountyData(workSearchResponse);
	}

	private List<Map<String, String>> packageCountyData(WorkSearchResponse workSearchResponse) {
		List<Map<String, String>> workCountsByCounty = Lists.newArrayList();
		Map<Enum<WorkFacetResultType>, List<FacetResult>> facets = workSearchResponse.getFacets();
		if (facets == null) {
			return workCountsByCounty;
		}

		List<FacetResult> countyIds = facets.get(WorkFacetResultType.COUNTY_ID);
		List<FacetResult> countyName = facets.get(WorkFacetResultType.COUNTY_NAME);
		if (countyIds == null || countyName == null) {
			return workCountsByCounty;
		}

		Iterator<FacetResult> idIterator = countyIds.iterator();
		Iterator<FacetResult> nameIterator = countyName.iterator();

		while (idIterator.hasNext() && nameIterator.hasNext()) {
			FacetResult countyId = idIterator.next();
			workCountsByCounty.add(ImmutableMap.of(
					"id", countyId.getFacetId(),
					"count", String.valueOf(countyId.getFacetCount()),
					"name", nameIterator.next().getFacetId()
			));
		}
		return workCountsByCounty;
	}
}
