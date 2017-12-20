package com.workmarket.web.controllers.admin;

import com.google.common.collect.Lists;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.service.report.kpi.KpiService;
import com.workmarket.domains.model.kpi.*;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.utility.KpiUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.text.SimpleDateFormat;

import static com.workmarket.web.utility.KpiUtilities.Format;
import static com.workmarket.web.utility.KpiUtilities.Format.MONEY;
import static com.workmarket.web.utility.KpiUtilities.Format.NUMBER;
import static com.workmarket.web.utility.KpiUtilities.Format.OTHER;
import static com.workmarket.web.utility.KpiUtilities.Format.PERCENTAGE;

@Controller
@RequestMapping("/admin/kpis")
public class AdminKpisController extends BaseController {

	@Autowired KpiService kpiService;

	private boolean isIndustryChecked;

	private String fundingTypeFilter;

	public boolean getIsIndustryChecked() {
		return isIndustryChecked;
	}

	public void setIsIndustryChecked(boolean isIndustryChecked) {
		this.isIndustryChecked = isIndustryChecked;
	}

	public String getFundingTypeFilter() {
		return fundingTypeFilter;
	}

	public void setFundingTypeFilter(String fundingTypeFilter) {
		this.fundingTypeFilter = fundingTypeFilter;
	}

	@RequestMapping(value={"", "/", "/index"}, method=RequestMethod.GET)
	public String index() {
		return "web/pages/admin/reporting/index";
	}

	@RequestMapping(value = "/financial", method = RequestMethod.GET)
	public String financial(Model model, HttpServletRequest httpServletRequest) throws Exception {

		if (httpServletRequest.getParameter("fundingType") != null) {
			if (httpServletRequest.getParameter("fundingType").equals("cash") || httpServletRequest.getParameter("fundingType").equals("terms")) {
				// Set filter to report
				setFundingTypeFilter(httpServletRequest.getParameter("fundingType"));
			} else {
				setFundingTypeFilter("none");
			}
		}

		generateDataTableHeader(model);
		model.addAttribute("fundingType", getFundingTypeFilter());
		return "web/pages/admin/kpis/financial";
	}

	@RequestMapping(value="/buyer", method=RequestMethod.GET)
	public String buyer(Model model, HttpServletRequest httpServletRequest) throws Exception {

		generateDataTableHeader(model);
		return "web/pages/admin/kpis/buyer";
	}

	@RequestMapping(value="/resource", method=RequestMethod.GET)
	public String resource(Model model) throws Exception {

		generateDataTableHeader(model);
		return "web/pages/admin/kpis/resource";
	}

	@RequestMapping(value="/marketplace", method=RequestMethod.GET)
	public String marketplace(Model model) throws Exception {

		generateDataTableHeader(model);
		return "web/pages/admin/kpis/marketplace";
	}

	@RequestMapping(value = "/financial/monthlyThroughput", method = RequestMethod.GET)
	public void monthlyThroughput(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.SALES_THROUGHPUT_ACTUAL, KPIReportAggregateInterval.MONTH_OF_YEAR, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/weeklyThroughput", method = RequestMethod.GET)
	public void weeklyThroughput(Model model, HttpServletRequest httpRequest) throws Exception {
		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.SALES_THROUGHPUT_ACTUAL, KPIReportAggregateInterval.WEEK_OF_YEAR, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/dailyThroughput", method = RequestMethod.GET)
	public void dailyThroughput(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.SALES_THROUGHPUT_ACTUAL, KPIReportAggregateInterval.DAY_OF_MONTH, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/monthlyTransactionFees", method = RequestMethod.GET)
	public void monthlyTransactionFees(Model model, HttpServletRequest httpRequest) throws Exception {
		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.SALES_FEES_ACTUAL, KPIReportAggregateInterval.MONTH_OF_YEAR, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/weeklyTransactionFees", method = RequestMethod.GET)
	public void weeklyTransactionFees(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.SALES_FEES_ACTUAL, KPIReportAggregateInterval.WEEK_OF_YEAR, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/dailyTransactionFees", method = RequestMethod.GET)
	public void dailyTransactionFees(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.SALES_FEES_ACTUAL, KPIReportAggregateInterval.DAY_OF_MONTH, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/monthlyAssignmentsCreated", method = RequestMethod.GET)
	public void monthlyAssignmentsCreated(Model model, HttpServletRequest httpServletRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpServletRequest, KPIReportType.ASSIGNMENTS_TOTAL_IN_DRAFT, KPIReportAggregateInterval.MONTH_OF_YEAR, NUMBER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/weeklyAssignmentsCreated", method = RequestMethod.GET)
	public void weeklyAssignmentsCreated(Model model, HttpServletRequest httpServletRequest) throws Exception {
		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpServletRequest, KPIReportType.ASSIGNMENTS_TOTAL_IN_DRAFT, KPIReportAggregateInterval.WEEK_OF_YEAR, NUMBER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/dailyAssignmentsCreated", method = RequestMethod.GET)
	public void dailyAssignmentsCreated(Model model, HttpServletRequest httpServletRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpServletRequest, KPIReportType.ASSIGNMENTS_TOTAL_IN_DRAFT, KPIReportAggregateInterval.DAY_OF_MONTH, NUMBER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/monthlyAvgAssignmentsValue", method = RequestMethod.GET)
	public void monthlyAvgAssignmentsValue(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.ASSIGNMENTS_VALUE_AVERAGE, KPIReportAggregateInterval.MONTH_OF_YEAR, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/weeklyAvgAssignmentsValue", method = RequestMethod.GET)
	public void weeklyAvgAssignmentsValue(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.ASSIGNMENTS_VALUE_AVERAGE, KPIReportAggregateInterval.WEEK_OF_YEAR, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/dailyAvgAssignmentsValue", method = RequestMethod.GET)
	public void dailyAvgAssignmentsValue(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.ASSIGNMENTS_VALUE_AVERAGE, KPIReportAggregateInterval.DAY_OF_MONTH, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/monthlyAvailableBuyerFunds", method = RequestMethod.GET)
	public void monthlyAvailableBuyerFunds(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.AVAILABLE_AP_AND_CASH_TOTAL, KPIReportAggregateInterval.MONTH_OF_YEAR, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/monthlyWithdrawableCash", method = RequestMethod.GET)
	public void monthlyWithdrawableCash(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.AVAILABLE_WITHDRAWABLE_CASH_ON_ACCOUNT, KPIReportAggregateInterval.MONTH_OF_YEAR, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/financial/monthlyTotalCash", method = RequestMethod.GET)
	public void monthlyTotalCash(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.AVAILABLE_CASH_ON_ACCOUNT, KPIReportAggregateInterval.MONTH_OF_YEAR, MONEY);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/buyer/monthlyNumOfNewBuyers", method = RequestMethod.GET)
	public void monthlyNumOfNewBuyers(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.NEW_BUYER_SIGNUPS, KPIReportAggregateInterval.MONTH_OF_YEAR, NUMBER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/buyer/weeklyNumOfNewBuyers", method = RequestMethod.GET)
	public void weeklyNumOfNewBuyers(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.NEW_BUYER_SIGNUPS, KPIReportAggregateInterval.WEEK_OF_YEAR, NUMBER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/buyer/monthlyAvgAssignmentsSentByNewBuyers", method = RequestMethod.GET)
	public void monthlyAvgAssignmentsSentByNewBuyers(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.NEW_SIGNUPS_AVERAGE_NUMBER_OF_ASSIGNMENTS, KPIReportAggregateInterval.MONTH_OF_YEAR, NUMBER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/buyer/weeklyAvgAssignmentsSentByNewBuyers", method = RequestMethod.GET)
	public void weeklyAvgAssignmentsSentByNewBuyers(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.NEW_SIGNUPS_AVERAGE_NUMBER_OF_ASSIGNMENTS, KPIReportAggregateInterval.WEEK_OF_YEAR, NUMBER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/buyer/monthlyPercentageNewBuyersSubscription", method = RequestMethod.GET)
	public void monthlyPercentageNewBuyersSubscription(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.PERCENT_OF_BUYERS_SENDING_FIRST_ASSIGNMENT_SUBSCRIPTION, KPIReportAggregateInterval.MONTH_OF_YEAR, PERCENTAGE);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/buyer/monthlyPercentageNewBuyersTransactional", method = RequestMethod.GET)
	public void monthlyPercentageNewBuyersTransactional(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.PERCENT_OF_BUYERS_SENDING_FIRST_ASSIGNMENT_TRANSACTIONAL, KPIReportAggregateInterval.MONTH_OF_YEAR, PERCENTAGE);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/buyer/weeklyNumOfNewBuyersSendingAssignmentsTTM", method = RequestMethod.GET)
	public void weeklyNumOfNewBuyersSendingAssignmentsTTM(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.BUYERS_SENDING_ONE_ASSIGNMENT_TRAILING_12_MONTHS, KPIReportAggregateInterval.WEEK_OF_YEAR, NUMBER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/resource/monthlyResourceReceivingAssignment", method = RequestMethod.GET)
	public void monthlyResourceReceivingAssignment(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.RESOURCES_RECEIVING_ONE_ASSIGNMENT, KPIReportAggregateInterval.MONTH_OF_YEAR, NUMBER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/resource/weeklyResourceReceivingAssignment", method = RequestMethod.GET)
	public void weeklyResourceReceivingAssignment(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.RESOURCES_RECEIVING_ONE_ASSIGNMENT, KPIReportAggregateInterval.WEEK_OF_YEAR, NUMBER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/resource/weeklyResourceReceivingAssignmentTTM", method = RequestMethod.GET)
	public void weeklyResourceReceivingAssignmentTTM(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.RESOURCES_RECEIVING_ONE_ASSIGNMENT_TRAILING_12_MONTHS, KPIReportAggregateInterval.WEEK_OF_YEAR, NUMBER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/marketplace/weeklyAvgHourSentToAssigned", method = RequestMethod.GET)
	public void weeklyAvgHourSentToAssigned(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.AVERAGE_HOURS_SENT_TO_ASSIGNED, KPIReportAggregateInterval.WEEK_OF_YEAR, OTHER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/marketplace/weeklyAvgHourAssignedToComplete", method = RequestMethod.GET)
	public void weeklyAvgHourAssignedToComplete(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.AVERAGE_HOURS_ASSIGNED_TO_COMPLETE, KPIReportAggregateInterval.WEEK_OF_YEAR, OTHER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/marketplace/weeklyAvgHourCompleteToPaid", method = RequestMethod.GET)
	public void weeklyAvgHourCompleteToPaid(Model model, HttpServletRequest httpRequest) throws Exception {
		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.AVERAGE_HOURS_COMPLETE_TO_PAID, KPIReportAggregateInterval.WEEK_OF_YEAR, OTHER);
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/marketplace/weeklyAvgHourSentToPaid", method = RequestMethod.GET)
	public void weeklyAvgHourSentToPaid(Model model, HttpServletRequest httpRequest) throws Exception {
		DataTablesResponse<List<String>, Map<String, Object>> response = getKpiReportResponse(httpRequest, KPIReportType.AVERAGE_HOURS_SENT_TO_PAID, KPIReportAggregateInterval.WEEK_OF_YEAR, OTHER);
		model.addAttribute("response", response);
	}

	private void generateDataTableHeader (Model model) {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.YEAR, -1);

		// Create Month List for Monthly Report
		List<String> monthList = Lists.newArrayList();
		monthList.add("Year");
		monthList.add("Jan");
		monthList.add("Feb");
		monthList.add("Mar");
		monthList.add("Apr");
		monthList.add("May");
		monthList.add("Jun");
		monthList.add("Jul");
		monthList.add("Aug");
		monthList.add("Sep");
		monthList.add("Oct");
		monthList.add("Nov");
		monthList.add("Dec");
		monthList.add("Total");

		// Create Week List for Weekly Report
		List<String> weekList = Lists.newArrayList();
		today = Calendar.getInstance();
		today.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		today.add(Calendar.WEEK_OF_YEAR, -12);
		for(int i = 0; i < 12; i++) {
			Date currentDate = new Date(today.getTimeInMillis());
			String currentWeekBegin = new SimpleDateFormat("MM/dd").format(currentDate);
			today.add(Calendar.DAY_OF_YEAR, 6);
			currentDate = new Date(today.getTimeInMillis());
			String currentWeekEnd = new SimpleDateFormat("MM/dd").format(currentDate);
			weekList.add(currentWeekBegin + "-" + currentWeekEnd);
			today.add(Calendar.DAY_OF_YEAR, 1);
		}

		// Create Day List for Daily Report
		List<String> dayList = Lists.newArrayList();
		today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_YEAR, -14);
		for(int i = 0; i < 14; i++) {
			Date currentDate = new Date(today.getTimeInMillis());
			String currentDay = new SimpleDateFormat("MM/dd").format(currentDate);
			dayList.add(currentDay);
			today.add(Calendar.DAY_OF_YEAR, 1);
		}

		model.addAttribute("monthList", monthList);
		model.addAttribute("weekList", weekList);
		model.addAttribute("dayList", dayList);

	}

	private DataTablesResponse<List<String>, Map<String, Object>> getKpiReportResponse(HttpServletRequest httpServletRequest, KPIReportType kpiReportType, KPIReportAggregateInterval kpiReportAggregateInterval, Format format) throws Exception {
		KPIRequest reportRequest = new KPIRequest();

		// Report Type
		reportRequest.setReportType(kpiReportType);
		reportRequest.setAggregateInterval(kpiReportAggregateInterval);

		// Report Filter
		Calendar from = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		from = DateUtilities.getMidnightRelativeToTimezone(from, TimeZone.getTimeZone("EST"));
		switch (kpiReportAggregateInterval) {
			case MONTH_OF_YEAR:
				from.set(Calendar.MONTH, 0);
				from.set(Calendar.DAY_OF_MONTH, 1);
				if (kpiReportType.equals(KPIReportType.SALES_THROUGHPUT_ACTUAL) || kpiReportType.equals(KPIReportType.SALES_FEES_ACTUAL) || kpiReportType.equals(KPIReportType.ASSIGNMENTS_TOTAL_IN_DRAFT) || kpiReportType.equals(KPIReportType.ASSIGNMENTS_VALUE_AVERAGE) || kpiReportType.equals(KPIReportType.NEW_BUYER_SIGNUPS)) {
					from.add(Calendar.YEAR, -2);
				}
				if (kpiReportType.equals(KPIReportType.AVAILABLE_WITHDRAWABLE_CASH_ON_ACCOUNT) || kpiReportType.equals(KPIReportType.AVAILABLE_CASH_ON_ACCOUNT)) {
					from.add(Calendar.YEAR, -1);
				}
				break;
			case WEEK_OF_YEAR:
				from.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				from.add(Calendar.WEEK_OF_YEAR, -12);
				break;
			case DAY_OF_MONTH:
				from.add(Calendar.DAY_OF_YEAR, -14);
				break;
		}
		reportRequest.setFrom(from);
		reportRequest.setTo(Calendar.getInstance(TimeZone.getTimeZone("EST")));

		// Payment Term Filter
		if (this.getFundingTypeFilter() != null) {
			List<String> filterList = Lists.newArrayList();
			if (this.getFundingTypeFilter().equals("cash")) {
				filterList.add("true");
				Filter filter = new Filter(KPIReportFilter.PAYMENT_TERMS, filterList);
				reportRequest.addToFilters(filter);
			} else if (this.getFundingTypeFilter().equals("terms")) {
				filterList.add("false");
				Filter filter = new Filter(KPIReportFilter.PAYMENT_TERMS, filterList);
				reportRequest.addToFilters(filter);
			}
		}

		KPIChartResponse kpiChartResponse = kpiService.getKPIChart(reportRequest);

		DataTablesRequest request = DataTablesRequest.newInstance(httpServletRequest);
		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request);

		return KpiUtilities.getResponse(kpiChartResponse, kpiReportAggregateInterval, response, format);
	}

}
