package com.workmarket.domains.reports.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.data.report.work.DecoratedWorkReportRow;
import com.workmarket.data.report.work.WorkReportPagination;
import com.workmarket.data.report.work.WorkReportRow;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.account.RegisterTransactionActivity;
import com.workmarket.domains.model.account.RegisterTransactionActivityPagination;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIReportFilter;
import com.workmarket.domains.model.rating.RatingPagination;
import com.workmarket.domains.model.reporting.RatingReport;
import com.workmarket.domains.model.reporting.RatingReportPagination;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.RatingService;
import com.workmarket.service.report.kpi.KpiService;
import com.workmarket.thrift.work.display.ReportingTypesInitialRequest;
import com.workmarket.thrift.work.display.SavedCustomReportsCompositeResponse;
import com.workmarket.thrift.work.display.WorkDisplay;
import com.workmarket.thrift.work.display.WorkDisplayException;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.forms.reports.CompanyReportForm;
import com.workmarket.web.forms.reports.WorkReportsForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.RatingStarsHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.views.CSVView;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasAnyRole('PERMISSION_REPORTMYWORK', 'PERMISSION_REPORTCOWORK') AND !principal.companyIsLocked")
public class ReportsController extends BaseController {

	public static final Map<String, Enum<?>> STANDARD_FILTERS = new ImmutableMap.Builder<String, Enum<?>>()
		.put("filters.status", WorkReportPagination.FILTER_KEYS.WORK_STATUS)
		.put("filters.subStatus", WorkReportPagination.FILTER_KEYS.WORK_SUB_STATUS_CODE)
		.put("filters.owner", WorkReportPagination.FILTER_KEYS.BUYER_ID)
		.put("filters.client", WorkReportPagination.FILTER_KEYS.CLIENT_ID)
		.put("filters.project", WorkReportPagination.FILTER_KEYS.PROJECT_ID)
		.put("filters.resourceType", WorkReportPagination.FILTER_KEYS.LANE_TYPE_ID)
		.put("filters.fromPrice", WorkReportPagination.FILTER_KEYS.FROM_PRICE)
		.put("filters.toPrice", WorkReportPagination.FILTER_KEYS.TO_PRICE)
		.put("filters.assignment_scheduled_date_from", WorkReportPagination.FILTER_KEYS.START_DATE)
		.put("filters.assignment_scheduled_date_to", WorkReportPagination.FILTER_KEYS.END_DATE)
		.put("filters.assignment_approved_date_from", WorkReportPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_FROM)
		.put("filters.assignment_approved_date_to", WorkReportPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_TO)
		.put("filters.assignment_paid_date_from", WorkReportPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_FROM)
		.put("filters.assignment_paid_date_to", WorkReportPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_TO)
		.put("filters.from_date", WorkReportPagination.FILTER_KEYS.START_DATE)
		.put("filters.to_date", WorkReportPagination.FILTER_KEYS.END_DATE)
		.build();

	public static final Map<String, Enum<?>> TRANSACTIONAL_FILTERS = new ImmutableMap.Builder<String, Enum<?>>()
		.put("filters.status", RegisterTransactionActivityPagination.FILTER_KEYS.WORK_STATUS_TYPE_CODE)
		.put("filters.subStatus", RegisterTransactionActivityPagination.FILTER_KEYS.SUBSTATUS_TYPE_CODE)
		.put("filters.owner", RegisterTransactionActivityPagination.FILTER_KEYS.BUYER_ID)
		.put("filters.client", RegisterTransactionActivityPagination.FILTER_KEYS.CLIENT_COMPANY_ID)
		.put("filters.project", RegisterTransactionActivityPagination.FILTER_KEYS.PROJECT_ID)
		.put("filters.assignment_scheduled_date_from", RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_SCHEDULED_DATE_FROM)
		.put("filters.assignment_scheduled_date_to", RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_SCHEDULED_DATE_TO)
		.put("filters.assignment_approved_date_from", RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_FROM)
		.put("filters.assignment_approved_date_to", RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_TO)
		.put("filters.assignment_paid_date_from", RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_FROM)
		.put("filters.assignment_paid_date_to", RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_TO)
		.put("filters.transaction_date_from", RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_FROM)
		.put("filters.transaction_date_to", RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_TO)
		.put("filters.start_date", RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_FROM)
		.put("filters.end_date", RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_TO)
		.build();

	public static final Map<String, Enum<?>> ASSIGNMENT_FILTERS = new ImmutableMap.Builder<String, Enum<?>>()
		.put("filters.from_date", WorkReportPagination.FILTER_KEYS.START_DATE)
		.put("filters.to_date", WorkReportPagination.FILTER_KEYS.END_DATE)
		.build();


	public static final List<String> REPORT_CSV_ASSIGNMENT_COLUMNS = ImmutableList.of(
		"Assignment ID",
		"Assignment"
	);
	public static final List<String> REPORT_CSV_BUYER_AND_BUDGET_COLUMNS = ImmutableList.of(
		"Create Date",
		"Type",
		"Amount",
		"Note",
		"Approval Status",
		"Approve Date",
		"Approver name"
	);
	public static final List<String> REPORT_CSV_ASSIGNMENT_DATA_COLUMNS = ImmutableList.of(
		"Internal Owner",
		"Client",
		"Resource First Name",
		"Resource Last Name",
		"Status",
		"Labels",
		"Address One",
		"Address Two",
		"City",
		"State",
		"Postal Code",
		"Country",
		"Latitude",
		"Longitude",
		"Date Sent",
		"Month Sent",
		"Year Sent",
		"Assignment Window Start",
		"Assignment Window End",
		"Schedule Time",
		"Date Completed",
		"Month Completed",
		"Year Completed",
		"Date Closed ",
		"Month Closed",
		"Year Closed",
		"Assignment Cost",
		"Sales Tax Flag",
		"Sales Tax Rate",
		"Taxes Due",
		"Transaction Fee",
		"Total Cost",
		"Pending Approval Cost",
		"Pay Terms",
		"Payment Due Date",
		"Hours Budgeted",
		"Hours Worked",
		"Invoice",
		"Bundle ID"
	);
	public static final List<String> REPORT_CSV_BUYER_FEE_COLUMNS = ImmutableList.of(
		"Sales Tax Flag",
		"Sales Tax Rate",
		"Taxes Due"
	);
	public static final List<String> REPORT_CSV_NON_BUYER_FEE_COLUMNS = ImmutableList.of(
		"Transaction Fee",
		"Total Cost",
		"Pending Approval Cost"
	);
	public static final List<String> REPORT_CSV_ASSIGNMENT_FEEDBACK_COLUMNS = ImmutableList.of(
		"Id",
		"Title",
		"Worker",
		"Internal Owner",
		"Rating Date",
		"Paid Date",
		"Rating",
		"Review",
		"Payment Timeliness"
	);

	public static final String CSV_EXPORT_FILE_FORMAT = "assignments-export-%s-%s.csv";

	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private WorkReportService reportService;
	@Autowired private WorkDisplay.Iface customReportService;
	@Autowired private FormOptionsDataHelper formOptionsDataHelper;
	@Autowired private RatingService ratingService;
	@Autowired private KpiService kpiService;
	@Autowired private MessageBundleHelper messageBundleHelper;

	private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

	@RequestMapping(method = GET)
	public String index(Model model) throws WorkDisplayException {
		Calendar now = DateUtilities.getCalendarNow();

		model.addAttribute("last30Start", DateUtilities.lastNDaysMidnight(30));
		model.addAttribute("last30End", now);
		model.addAttribute("last90Start", DateUtilities.lastNDaysMidnight(90));
		model.addAttribute("last90End", now);
		model.addAttribute("ytdStart", DateUtilities.getMidnightYTD());
		model.addAttribute("ytdEnd", now);
		model.addAttribute("lastYear", (now.get(Calendar.YEAR) - 1));
		model.addAttribute("companyId", getCurrentUser().getCompanyId());

		ReportingTypesInitialRequest reportRequest = new ReportingTypesInitialRequest()
			.setUserNumber(getCurrentUser().getUserNumber())
			.setCompanyId(getCurrentUser().getCompanyId())
			.setLocale(Locale.ENGLISH.getLanguage());
		SavedCustomReportsCompositeResponse reportResponse = customReportService.getCompanyCustomReports(reportRequest);

		model.addAttribute("savedReports", reportResponse.getSavedCustomReportResponses());
		model.addAttribute("email", getCurrentUser().getEmail());

		return "web/pages/reports/index";
	}

	@RequestMapping(value = "/budget", method = GET, produces = TEXT_HTML_VALUE)
	public String budgetReport(
		Model model,
		@ModelAttribute("filterForm") WorkReportsForm form,
		HttpServletRequest request) throws Exception {

		form.setBuyerReport(true);
		form.setBudgetReport(true);
		return standardReport(model, form, request);
	}

	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public String statisticsRedirectToCompany(Model model, @ModelAttribute("filterForm") CompanyReportForm form) {
		return "redirect:/reports/statistics/" + getCurrentUser().getCompanyId();
	}

	@RequestMapping(value = "/statistics/{id}", method = RequestMethod.GET)
	public String statistics(@PathVariable("id") Long companyId, Model model, @ModelAttribute("filterForm") CompanyReportForm form) {
		if (!getCurrentUser().getCompanyId().equals(companyId)) {
			throw new HttpException401().setMessageKey("work.not_authorized");
		}
		Company company = companyService.findCompanyById(companyId);
		if (company != null){
			model.addAttribute("company", company);
		}
		return "web/pages/reports/statistics";
	}

	@RequestMapping(
		value = "/statisticsAjaxReload",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder statisticsUsJson(@RequestParam("id") Long companyId, @RequestParam("requestId") String requestId) {

		AjaxResponseBuilder responseBuilder = AjaxResponseBuilder.fail();
		JSONObject data = new JSONObject();

		if (companyId != null) {
			KPIRequest kpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
			kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(companyId.toString())));
			try {
				kpiService.populateStatisticsDataWithKPIReports(kpiRequest, data, requestId, companyId, getCurrentUser().getId());
			} catch (Exception e) {
				logger.error("There was a problem generating a KPI report for: " + companyId, e);
				return responseBuilder.addMessage(messageBundleHelper.getMessage("admin.manage.company.report.error"));
			}
		}

		return responseBuilder
			.setSuccessful(true)
			.setData(ImmutableMap.<String, Object>of("ajaxResponse", data.toString()));
	}

	@RequestMapping(value = "/budget", method = POST, produces = APPLICATION_JSON_VALUE)
	public void budgetReportList(
		Model model,
		@ModelAttribute("filterForm") WorkReportsForm form,
		HttpServletRequest request) throws Exception {

		form.setBuyerReport(true);
		form.setBudgetReport(true);
		standardReportList(model, form, request);
	}

	@RequestMapping(value = "/buyer", method = GET, produces = TEXT_HTML_VALUE)
	public String buyerReport(
		Model model,
		@ModelAttribute("filterForm") WorkReportsForm form,
		HttpServletRequest request) throws Exception {
		form.setBuyerReport(true);
		form.setBudgetReport(false);
		return standardReport(model, form, request);
	}

	@RequestMapping(value = "/buyer", method = POST, produces = APPLICATION_JSON_VALUE)
	public void buyerReportList(
		Model model,
		@ModelAttribute("filterForm") WorkReportsForm form,
		HttpServletRequest request) throws Exception {

		form.setBuyerReport(true);
		form.setBudgetReport(false);
		standardReportList(model, form, request);
	}

	@RequestMapping(value = "/resource", method = GET, produces = TEXT_HTML_VALUE)
	public String resourceReport(
		Model model,
		@ModelAttribute("filterForm") WorkReportsForm form,
		HttpServletRequest httpRequest) throws Exception {

		form.setBuyerReport(false);
		form.setBudgetReport(false);
		return standardReport(model, form, httpRequest);
	}

	@RequestMapping(value = "/resource", method = POST, produces = APPLICATION_JSON_VALUE)
	public void resourceReportList(
		Model model,
		@ModelAttribute("filterForm") WorkReportsForm form,
		HttpServletRequest httpRequest) throws Exception {

		form.setBuyerReport(false);
		form.setBudgetReport(false);
		standardReportList(model, form, httpRequest);
	}

	@RequestMapping(value = "/export", method = GET)
	public CSVView export(
		Model model,
		@ModelAttribute("filterForm") WorkReportsForm form,
		HttpServletRequest httpRequest) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest, form);
		request.setFilterMapping(STANDARD_FILTERS);

		WorkReportPagination pagination = request.newPagination(WorkReportPagination.class, true);
		pagination.setReturnAllRows(true);

		List<String> columns = Lists.newArrayList(REPORT_CSV_ASSIGNMENT_COLUMNS);

		if (form.isBuyerReport() && form.isBudgetReport()) {
			columns.addAll(REPORT_CSV_BUYER_AND_BUDGET_COLUMNS);
		}

		// add remaining columns
		columns.addAll(REPORT_CSV_ASSIGNMENT_DATA_COLUMNS);

		// Resources must not be able to know the fees paid by the client
		if (form.isBuyerReport()) {
			columns.removeAll(REPORT_CSV_BUYER_FEE_COLUMNS);
		} else {
			columns.removeAll(REPORT_CSV_NON_BUYER_FEE_COLUMNS);
		}

		if (form.isBuyerReport()) {
			pagination = form.isBudgetReport() ?
				reportService.generateBudgetReportBuyer(getCurrentUser().getId(), pagination, form.isIncludeCustomFields()) :
				reportService.generateWorkReportBuyer(getCurrentUser().getId(), pagination, form.isIncludeCustomFields());
		} else {
			pagination = reportService.generateEarningsReportResource(getCurrentUser().getId(), pagination, form.isIncludeCustomFields());
		}

		List<CustomFieldReportRow> customFields = Lists.newArrayList();
		if (form.isIncludeCustomFields()) {
			customFields = getCustomFields(form, pagination.getResults());
			for (CustomFieldReportRow row : customFields) {
				columns.add(row.getFieldName());
			}
		}

		List<String[]> rows = Lists.newArrayList();
		rows.add(columns.toArray(new String[columns.size()]));

		for (WorkReportRow row : pagination.getResults()) {
			List<String> data = getStandardRowData(form, customFields, row);
			rows.add(data.toArray(new String[data.size()]));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format(
			CSV_EXPORT_FILE_FORMAT,
			DateUtilities.format("YYYYMMdd", form.getFilters().getFrom_date()),
			DateUtilities.format("YYYYMMdd", form.getFilters().getTo_date())
		));
		return view;
	}

	@RequestMapping(value = "/assignment_feedback", method = GET, produces = TEXT_HTML_VALUE)
	public String assignmentFeedbackReport(
		Model model,
		@ModelAttribute("filterForm") WorkReportsForm form,
		HttpServletRequest httpRequest) throws Exception {
		form.setAssignmentFeedbackReport(true);
		form.setBuyerReport(false);
		form.setBudgetReport(false);

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest, form);
		request.setFilterMapping(ASSIGNMENT_FILTERS);

		model.addAttribute("users", formOptionsDataHelper.getActiveUsers(getCurrentUser()));
		model.addAttribute("statuses", formOptionsDataHelper.getWorkStatusTypes());

		model.addAttribute("fluid", "1");

		return "web/pages/reports/assignment_feedback";
	}

	@RequestMapping(value = "/assignment_feedback.json", method = GET, produces = APPLICATION_JSON_VALUE)
	public void assignmentFeedbackReportList(
		Model model,
		@ModelAttribute("filterForm") WorkReportsForm form,
		HttpServletRequest httpRequest) throws Exception {

		form.setAssignmentFeedbackReport(true);
		form.setBuyerReport(true);
		form.setBudgetReport(false);

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest, form);
		request.setFilterMapping(ASSIGNMENT_FILTERS);

		request.setSortableColumnMapping(ImmutableMap.<Integer, String>builder()
			.put(0, RatingReportPagination.SORTS.WORK_NUMBER.toString())
			.put(1, RatingReportPagination.SORTS.TITLE.toString())
			.put(2, RatingReportPagination.SORTS.CLIENT_NAME.toString())
			.put(3, RatingReportPagination.SORTS.RESOURCE_NAME.toString())
			.put(4, RatingReportPagination.SORTS.CREATED_ON.toString())
			.put(5, RatingReportPagination.SORTS.PAID_ON.toString())
			.put(6, RatingReportPagination.SORTS.VALUE.toString()).build());

		RatingReportPagination pagination = request.newPagination(RatingReportPagination.class);
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());
		pagination.addFilter(RatingPagination.FILTER_KEYS.REVIEW_SHARED_FLAG, Boolean.TRUE);
		if (form.getFilters() != null) {
			pagination.addFilter(WorkPagination.FILTER_KEYS.FROM_DATE, DateUtilities.format("yyyy-MM-dd", form.getFilters().getFrom_date()));
			pagination.addFilter(WorkPagination.FILTER_KEYS.THROUGH_DATE, DateUtilities.format("yyyy-MM-dd", form.getFilters().getTo_date()));
		} else {
			DateTime tonight = new LocalDate().toDateTimeAtStartOfDay();
			pagination.addFilter(WorkPagination.FILTER_KEYS.FROM_DATE, DateUtilities.format("yyyy-MM-dd", tonight.plusDays(-7).toGregorianCalendar()));
			pagination.addFilter(WorkPagination.FILTER_KEYS.THROUGH_DATE, DateUtilities.format("yyyy-MM-dd", new GregorianCalendar().getTime()));
		}
		try {
			pagination = ratingService.findRatingsByCompany(getCurrentUser().getCompanyId(), pagination);
		} catch (Exception e) {
			logger.error("Trouble finding user's received ratings", e);
		}

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (RatingReport e : pagination.getResults()) {

			List<String> data = Lists.newArrayList(
				e.getWorkNumber(),
				e.getTitle(),
				StringUtilities.fullName(e.getRatingUserFirstName(), e.getRatingUserLastName()),
				StringUtilities.fullName(e.getRatedUserFirstName(), e.getRatedUserLastName()),
				DateUtilities.format("MMM d, yyyy", e.getRatingDate()),
				DateUtilities.format("MMM d, yyyy", e.getPaidOn()),
				e.getValue().toString(),
				e.getReview(),
				e.getPaymentTimeliness()
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"is_flagged_for_review", e.isFlaggedForReview(),
				"work_number", e.getWorkNumber(),
				"is_show_assignment_title", true,
				"ratingValue", e.getValue(),
				"rating", RatingStarsHelper.getLevels(e.getValue()),
				"review", e.getReview(),
				"review_short", e.getReview(),
				"show_review", false,
				"show_flag", false,
				"time_to_pay_from_due_date", e.getPaymentTimeliness()
			);

			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/assignment_feedback.csv", method = GET)
	public CSVView assignmentFeedbackExport(Model model, WorkReportsForm form, HttpServletRequest httpRequest) throws Exception {

		List<String> columns = REPORT_CSV_ASSIGNMENT_FEEDBACK_COLUMNS;

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest, form);
		request.setFilterMapping(ASSIGNMENT_FILTERS);

		RatingReportPagination pagination = request.newPagination(RatingReportPagination.class, true);
		pagination.setFetchAll(true);
		pagination.setSortColumn(WorkPagination.SORTS.CREATED_DATE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		pagination.addFilter(RatingPagination.FILTER_KEYS.REVIEW_SHARED_FLAG, Boolean.TRUE);
		if (form.getFilters() != null) {
			pagination.addFilter(WorkPagination.FILTER_KEYS.FROM_DATE, DateUtilities.format("yyyy-MM-dd", form.getFilters().getFrom_date()));
			pagination.addFilter(WorkPagination.FILTER_KEYS.THROUGH_DATE, DateUtilities.format("yyyy-MM-dd", form.getFilters().getTo_date()));
		} else {
			DateTime tonight = new LocalDate().toDateTimeAtStartOfDay();
			pagination.addFilter(WorkPagination.FILTER_KEYS.FROM_DATE, DateUtilities.format("yyyy-MM-dd", tonight.plusDays(-7).toGregorianCalendar()));
			pagination.addFilter(WorkPagination.FILTER_KEYS.THROUGH_DATE, DateUtilities.format("yyyy-MM-dd", new GregorianCalendar().getTime()));
		}
		try {
			pagination = ratingService.findRatingsByCompany(getCurrentUser().getCompanyId(), pagination);
		} catch (Exception e) {
			logger.error("Trouble finding user's received ratings", e);
		}

		List<String[]> rows = Lists.newArrayList();
		rows.add(columns.toArray(new String[columns.size()]));

		for (RatingReport row : pagination.getResults()) {
			List<String> data = getAssignmentFeedbackRowData(row);
			rows.add(data.toArray(new String[data.size()]));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format("assignment-feedback-export-%s.csv", DateUtilities.getISO8601(DateUtilities.getCalendarNow())));
		return view;
	}

	@RequestMapping(value = "/transactions", method = GET, produces = TEXT_HTML_VALUE)
	public String transactionsReport(
		Model model,
		@ModelAttribute("filterForm") WorkReportsForm form,
		HttpServletRequest httpRequest) throws Exception {

		form.setTransactionsReport(true);
		form.setBuyerReport(true);
		form.setBudgetReport(false);

		List<WorkSubStatusType> subStatuses = workSubStatusService.findAllSubStatuses(form.isBuyerReport(), !form.isBuyerReport());

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest, form);
		request.setFilterMapping(TRANSACTIONAL_FILTERS);

		RegisterTransactionActivityPagination pagination = request.newPagination(RegisterTransactionActivityPagination.class);
		pagination = accountRegisterService.getAccountRegisterTransactionReport(getCurrentUser().getCompanyId(), pagination);

		List<CustomFieldReportRow> customFields = Lists.newArrayList();
		if (form.isIncludeCustomFields()) {
			customFields = getCustomFields(form, pagination.getResults());
		}

		model.addAttribute("clients", formOptionsDataHelper.getClients(getCurrentUser()));
		model.addAttribute("projects", formOptionsDataHelper.getProjects(getCurrentUser()));
		model.addAttribute("users", formOptionsDataHelper.getActiveUsers(getCurrentUser()));
		model.addAttribute("lanes", formOptionsDataHelper.getLanes());
		model.addAttribute("statuses", formOptionsDataHelper.getWorkStatusTypes());
		model.addAttribute("subStatuses", CollectionUtilities.extractKeyValues(subStatuses, "code", "description"));
		model.addAttribute("customFields", customFields);

		model.addAttribute("fluid", "1");

		return "web/pages/reports/transactions";
	}

	@RequestMapping(value = "/transactions.json", method = GET, produces = APPLICATION_JSON_VALUE)
	public void transactionsReportList(
		Model model,
		@ModelAttribute("filterForm") WorkReportsForm form,
		HttpServletRequest httpRequest) throws Exception {

		form.setTransactionsReport(true);
		form.setBuyerReport(true);
		form.setBudgetReport(false);

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest, form);
		request.setFilterMapping(TRANSACTIONAL_FILTERS);
		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, RegisterTransactionActivityPagination.SORTS.WORK_NUMBER.toString());
			put(1, RegisterTransactionActivityPagination.SORTS.ASSIGNMENT_SCHEDULED_DATE.toString());
			put(2, RegisterTransactionActivityPagination.SORTS.ASSIGNMENT_APPROVED_DATE.toString());
			put(3, RegisterTransactionActivityPagination.SORTS.ASSIGNMENT_PAID_DATE.toString());
			put(4, RegisterTransactionActivityPagination.SORTS.TRANSACTION_TYPE.toString());
		}});

		RegisterTransactionActivityPagination pagination = request.newPagination(RegisterTransactionActivityPagination.class);
		pagination = accountRegisterService.getAccountRegisterTransactionReport(getCurrentUser().getCompanyId(), pagination);

		List<CustomFieldReportRow> customFields = Lists.newArrayList();
		if (form.isIncludeCustomFields()) {
			customFields = getCustomFields(form, pagination.getResults());
		}

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (RegisterTransactionActivity row : pagination.getResults()) {
			List<String> data = getTransactionRowData(form, customFields, row);
			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"work_number", row.getWorkNumber(),
				"is_show_assignment_title", row.isShowAssignmentTitle()
			);
			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/transactions.csv", method = GET)
	public CSVView transactionsExport(Model model, WorkReportsForm form, HttpServletRequest httpRequest) throws Exception {

		List<String> columns = Lists.newArrayList(
			"Id",
			"Scheduled Date",
			"Approved Date",
			"Paid Date",
			"Transaction Date",
			"Type",
			"Title",
			"Payment",
			"Fees",
			"Authorization",
			"Credits",
			"Invoice",
			"Bundle ID"
		);

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest, form);
		request.setFilterMapping(TRANSACTIONAL_FILTERS);

		RegisterTransactionActivityPagination pagination = request.newPagination(RegisterTransactionActivityPagination.class, true);
		pagination.setReturnAllRows(true);
		pagination.setSortColumn(RegisterTransactionActivityPagination.SORTS.TRANSACTION_DATE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		pagination = accountRegisterService.getAccountRegisterTransactionReport(getCurrentUser().getCompanyId(), pagination);

		List<CustomFieldReportRow> customFields = Lists.newArrayList();
		if (form.isIncludeCustomFields()) {
			customFields = getCustomFields(form, pagination.getResults());
		}

		for (CustomFieldReportRow row : customFields) {
			columns.add(row.getFieldName());
		}

		List<String[]> rows = Lists.newArrayList();
		rows.add(columns.toArray(new String[columns.size()]));

		for (RegisterTransactionActivity row : pagination.getResults()) {
			List<String> data = getTransactionRowData(form, customFields, row);
			rows.add(data.toArray(new String[data.size()]));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format("activity-export-%s.csv", DateUtilities.getISO8601(DateUtilities.getCalendarNow())));
		return view;
	}

	private void standardReportList(Model model, WorkReportsForm form, HttpServletRequest httpRequest) throws Exception {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest, form);
		request.setFilterMapping(STANDARD_FILTERS);
		Map<Integer, String> sortableMap = Maps.newHashMap();

		List<String> sortableList = Lists.newArrayList(
			WorkReportPagination.SORTS.WORK_NUMBER.toString(),
			WorkReportPagination.SORTS.TITLE.toString()
		);

		if (form.isBudgetReport() && form.isBuyerReport()) {
			sortableList.addAll(ImmutableList.of(
				WorkReportPagination.SORTS.NEGOTIATION_DATE.toString(),
				WorkReportPagination.SORTS.NEGOTIATION_TYPE.toString(),
				WorkReportPagination.SORTS.NEGOTIATION_NOTE.toString(),
				WorkReportPagination.SORTS.NEGOTIATION_STATUS.toString(),
				WorkReportPagination.SORTS.NEGOTIATION_APPROVED_ON.toString(),
				WorkReportPagination.SORTS.BUYER_LAST_NAME.toString(),
				WorkReportPagination.SORTS.CLIENT.toString()
			));
		}

		sortableList.addAll(ImmutableList.of(
			WorkReportPagination.SORTS.BUYER_LAST_NAME.toString(),
			WorkReportPagination.SORTS.CLIENT.toString(),
			WorkReportPagination.SORTS.RESOURCE.toString(),
			WorkReportPagination.SORTS.RESOURCE.toString(),
			WorkReportPagination.SORTS.STATUS.toString(),
			WorkReportPagination.SORTS.ADDRESS1.toString(),
			WorkReportPagination.SORTS.ADDRESS2.toString(),
			WorkReportPagination.SORTS.CITY.toString(),
			WorkReportPagination.SORTS.STATE.toString(),
			WorkReportPagination.SORTS.LOCATION.toString(),
			WorkReportPagination.SORTS.SENT_DATE.toString(),
			WorkReportPagination.SORTS.SENT_DATE.toString(),
			WorkReportPagination.SORTS.SENT_DATE.toString(),
			WorkReportPagination.SORTS.SCHEDULE_FROM.toString(),
			WorkReportPagination.SORTS.COMPLETED_DATE.toString(),
			WorkReportPagination.SORTS.COMPLETED_DATE.toString(),
			WorkReportPagination.SORTS.COMPLETED_DATE.toString(),
			WorkReportPagination.SORTS.CLOSED_DATE.toString(),
			WorkReportPagination.SORTS.CLOSED_DATE.toString(),
			WorkReportPagination.SORTS.CLOSED_DATE.toString(),
			WorkReportPagination.SORTS.WORK_PRICE.toString(),
			WorkReportPagination.SORTS.WM_FEE.toString(),
			WorkReportPagination.SORTS.WORK_TOTAL_COST.toString(),
			WorkReportPagination.SORTS.PAYMENT_TERMS_DAYS.toString(),
			WorkReportPagination.SORTS.DUE_DATE.toString(),
			WorkReportPagination.SORTS.HOURS_BUDGETED.toString(),
			WorkReportPagination.SORTS.HOURS_WORKED.toString(),
			WorkReportPagination.SORTS.INVOICE_NUMBER.toString()
		));

		for (int i = 0; i < sortableList.size(); ++i) {
			sortableMap.put(i, sortableList.get(i));
		}

		request.setSortableColumnMapping(sortableMap);

		WorkReportPagination pagination = request.newPagination(WorkReportPagination.class);
		if (form.isBuyerReport()) {
			pagination = form.isBudgetReport() ?
				reportService.generateBudgetReportBuyer(getCurrentUser().getId(), pagination, form.isIncludeCustomFields()) :
				reportService.generateWorkReportBuyer(getCurrentUser().getId(), pagination, form.isIncludeCustomFields());
		} else {
			pagination = reportService.generateEarningsReportResource(getCurrentUser().getId(), pagination, form.isIncludeCustomFields());
		}

		List<CustomFieldReportRow> customFields = Lists.newArrayList();
		if (form.isIncludeCustomFields()) {
			customFields = getCustomFields(form, pagination.getResults());
		}

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (WorkReportRow row : pagination.getResults()) {
			List<String> data = getStandardRowData(form, customFields, row);
			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"work_number", row.getWorkNumber()
			);
			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	private String standardReport(Model model, WorkReportsForm form, HttpServletRequest httpRequest) throws Exception {
		List<WorkSubStatusType> subStatuses = workSubStatusService.findAllSubStatuses(form.isBuyerReport(), !form.isBuyerReport());
		List<CustomFieldReportRow> customFields = Lists.newArrayList();
		if (form.isIncludeCustomFields()) {
			DataTablesRequest request = DataTablesRequest.newInstance(httpRequest, form);
			request.setFilterMapping(STANDARD_FILTERS);

			WorkReportPagination pagination = request.newPagination(WorkReportPagination.class);
			if (form.isBuyerReport()) {
				pagination = form.isBudgetReport() ?
					reportService.generateBudgetReportBuyer(getCurrentUser().getId(), pagination, form.isIncludeCustomFields()) :
					reportService.generateWorkReportBuyer(getCurrentUser().getId(), pagination, form.isIncludeCustomFields());
			} else {
				pagination = reportService.generateEarningsReportResource(getCurrentUser().getId(), pagination, form.isIncludeCustomFields());
			}
			customFields = getCustomFields(form, pagination.getResults());
		}

		model.addAttribute("clients", formOptionsDataHelper.getClients(getCurrentUser()));
		model.addAttribute("projects", formOptionsDataHelper.getProjects(getCurrentUser()));
		model.addAttribute("users", formOptionsDataHelper.getActiveUsers(getCurrentUser()));
		model.addAttribute("lanes", formOptionsDataHelper.getLanes());
		model.addAttribute("statuses", formOptionsDataHelper.getWorkStatusTypes());
		model.addAttribute("subStatuses", CollectionUtilities.extractKeyValues(subStatuses, "code", "description"));
		model.addAttribute("canViewAllData", reportService.canViewAllCompanyAssignmentData(getCurrentUser().getId()));
		model.addAttribute("customFields", customFields);

		model.addAttribute("fluid", "1");

		return "web/pages/reports/standard";
	}

	@SuppressWarnings("unchecked")
	private <T extends DecoratedWorkReportRow> List<CustomFieldReportRow> getCustomFields(WorkReportsForm form, List<T> list) {

		CustomFieldReportFilters filters = new CustomFieldReportFilters();
		filters.setWorkIds(CollectionUtilities.newListPropertyProjection(list, "workId"));

		if (form.isBuyerReport()) {
			filters.setVisibleToBuyer(true);
		} else {
			filters.setVisibleToResource(true);
		}

		return reportService.findAllWorkCustomFields(getCurrentUser().getId(), filters);
	}


	private List<String> getStandardRowData(WorkReportsForm form, List<CustomFieldReportRow> customFields, WorkReportRow row) {
		List<String> data = Lists.newArrayList(
			row.getWorkNumber(),
			row.getTitle()
		);

		if (form.isBudgetReport() && form.isBuyerReport()) {
			List<String> budgetRelatedData = Lists.newArrayList(
				DateUtilities.format("MM/dd/yyyy @ h:mma z", row.getExpenseCreateDate(), row.getTimeZoneId(), "-"),
				row.getExpenseType(),
				String.valueOf(row.getExpenseAmount()),
				row.getExpenseNote(),
				row.getExpenseApprovalStatus(),
				DateUtilities.format("MM/dd/yyyy @ h:mma z", row.getExpenseActionDate(), row.getTimeZoneId(), "-"),
				row.getExpenseApproverName()
			);

			data.addAll(budgetRelatedData);
		}

		List<String> remainingColumns = Lists.newArrayList(
			row.getBuyerFullName(),
			(row.isOwner()) ? row.getClientCompanyName() : "",
			row.getAssignedResourceFirstName(),
			row.getAssignedResourceLastName(),
			row.getStatus(),
			CollectionUtilities.join(row.getWorkSubStatusTypes(), "description", ", "),
			(row.isOwner()) ? row.getAddress1() : "",
			(row.isOwner()) ? row.getAddress2() : "",
			row.getCity(),
			row.getState(),
			row.getPostalCode(),
			row.getCountry(),
			row.getLatitude().toString(),
			row.getLongitude().toString(),
			DateUtilities.format("MM/dd/yyyy @ h:mma z", row.getSentOn(), row.getTimeZoneId(), "-"),
			DateUtilities.format("MMMM", row.getSentOn(), row.getTimeZoneId(), "-"),
			DateUtilities.format("yyyy", row.getSentOn(), row.getTimeZoneId(), "-"),
			DateUtilities.format("MM/dd/yyyy @ h:mma z", row.getScheduleFrom(), row.getTimeZoneId(), "-"),
			DateUtilities.format("MM/dd/yyyy @ h:mma z", row.getScheduleThrough(), row.getTimeZoneId(), "-"),
			DateUtilities.format("MM/dd/yyyy @ h:mma z", row.getAssignedResourceAppointmentDate(), row.getTimeZoneId(), "-"),
			DateUtilities.format("MM/dd/yyyy @ h:mma z", row.getCompletedOn(), row.getTimeZoneId(), "-"),
			DateUtilities.format("MMMM", row.getCompletedOn(), row.getTimeZoneId(), "-"),
			DateUtilities.format("yyyy", row.getCompletedOn(), row.getTimeZoneId(), "-"),
			DateUtilities.format("MM/dd/yyyy @ h:mma z", row.getClosedOn(), row.getTimeZoneId(), "-"),
			DateUtilities.format("MMMM", row.getClosedOn(), row.getTimeZoneId(), "-"),
			DateUtilities.format("yyyy", row.getClosedOn(), row.getTimeZoneId(), "-"),
			NumberUtilities.currency(row.getWorkPrice())
		);

		data.addAll(remainingColumns);

		// Resources must not be able to know the fees paid by the client
		if (form.isBuyerReport()) {
			data.add(NumberUtilities.currency(row.getWorkMarketFee()));
			data.add(NumberUtilities.currency(row.getWorkTotalCost()));
			data.add(NumberUtilities.currency(row.getPendingApprovalCost()));
		} else {
			//Add taxes columns
			data.add(row.getTaxCollected());
			data.add(NumberUtilities.percent(row.getTaxRate()));
			data.add(NumberUtilities.currency(row.getTaxesDue()));
		}

		data.addAll(Lists.newArrayList(
			(row.isPaymentTermsEnabled() && row.getPaymentTermsDays() > 0) ? row.getPaymentTermsDays().toString() : "-",
			DateUtilities.format("MM/dd/yyyy @ h:mma z", row.getDueOn(), row.getTimeZoneId(), "-"),
			(row.getHoursBudgeted() > 0) ? row.getHoursBudgeted().toString() : "-",
			(row.getHoursWorked() > 0) ? row.getHoursWorked().toString() : "-",
			(row.isOwner()) ? row.getInvoiceNumber() : "",
			(row.isOwner()) ? row.getInvoiceSummaryNumber() : ""
		));

		if (form.isIncludeCustomFields()) {
			Map<Long, CustomFieldReportRow> customFieldsLookup = CollectionUtilities.newEntityIdMap(row.getCustomFields(), "fieldId");
			for (CustomFieldReportRow f : customFields) {
				String value = null;
				if (customFieldsLookup.containsKey(f.getFieldId())) {
					value = customFieldsLookup.get(f.getFieldId()).getFieldValue();
				}
				data.add(value);
			}
		}
		return data;
	}

	private List<String> getAssignmentFeedbackRowData(RatingReport row) {

		List<String> data = Lists.newArrayList(
			row.getWorkNumber(),
			row.getTitle(),
			StringUtilities.fullName(row.getRatingUserFirstName(), row.getRatingUserLastName()),
			StringUtilities.fullName(row.getRatedUserFirstName(), row.getRatedUserLastName()),
			DateUtilities.format("MM/dd/yyyy", row.getRatingDate(), getCurrentUser().getTimeZoneId(), "-"),
			DateUtilities.format("MM/dd/yyyy", row.getPaidOn(), getCurrentUser().getTimeZoneId(), "-"),
			row.getValue().toString(),
			StringUtils.defaultString(row.getReview()),
			row.getPaymentTimeliness()
		);
		return data;
	}

	private List<String> getTransactionRowData(WorkReportsForm form, List<CustomFieldReportRow> customFields, RegisterTransactionActivity row) {
		List<String> data = Lists.newArrayList(
			row.getWorkNumber(),
			DateUtilities.format("MM/dd/yyyy", row.getScheduleDate(), getCurrentUser().getTimeZoneId(), "-"),
			DateUtilities.format("MM/dd/yyyy", row.getClosedOn(), getCurrentUser().getTimeZoneId(), "-"),
			DateUtilities.format("MM/dd/yyyy", row.getPaidOn(), getCurrentUser().getTimeZoneId(), "-"),
			row.isSetWorkNumber() ? "-" : DateUtilities.format("MM/dd/yyyy", row.getRegisterTransactionDate(), getCurrentUser().getTimeZoneId(), "-"),
			row.getDisplayTypeDescription(),
			row.getFormattedDescription(),
			(CollectionUtilities.containsAny(row.getRegisterTransactionTypeCode(), RegisterTransactionType.BUYER_WORK_PAYMENT)) ?
				NumberUtilities.currency(row.getAmount()) :
				StringUtils.EMPTY,
			(CollectionUtilities.containsAny(row.getRegisterTransactionTypeCode(),
				RegisterTransactionType.NEW_WORK_LANE_2,
				RegisterTransactionType.NEW_WORK_LANE_3,
				RegisterTransactionType.CREDIT_CARD_FEE,
				RegisterTransactionType.AMEX_CREDIT_CARD_FEE)) ?
				NumberUtilities.currency(row.getAmount()) :
				StringUtils.EMPTY,
			(row.isPreFundAssignmentAuthorization()) ? NumberUtilities.currency(row.getAmount()) : StringUtils.EMPTY,
			(CollectionUtilities.containsAny(row.getRegisterTransactionTypeCode(),
				RegisterTransactionType.ADD_FUNDS,
				RegisterTransactionType.RESOURCE_WORK_PAYMENT)) ?
				NumberUtilities.currency(row.getAmount()) :
				StringUtils.EMPTY,
			row.getInvoiceNumber(),
			row.getInvoiceSummaryNumber()
		);

		if (form.isIncludeCustomFields()) {
			Map<Long, CustomFieldReportRow> customFieldsLookup = CollectionUtilities.newEntityIdMap(row.getCustomFields(), "fieldId");
			for (CustomFieldReportRow f : customFields) {
				String value = null;
				if (customFieldsLookup.containsKey(f.getFieldId())) {
					value = customFieldsLookup.get(f.getFieldId()).getFieldValue();
				}
				data.add(value);
			}
		}
		return data;
	}
}
