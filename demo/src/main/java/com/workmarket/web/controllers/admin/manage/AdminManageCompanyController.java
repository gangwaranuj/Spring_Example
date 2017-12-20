package com.workmarket.web.controllers.admin.manage;

import ch.lambdaj.function.convert.PropertyExtractor;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.configuration.Constants;
import com.workmarket.data.aggregate.CompanyAggregate;
import com.workmarket.data.aggregate.CompanyAggregatePagination;
import com.workmarket.data.report.internal.TopUser;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.Pagination.SORT_DIRECTION;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.WorkFeeBand;
import com.workmarket.domains.model.account.WorkFeeConfiguration;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.SubscriptionAccountServiceTypeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionAddOnTypeAssociation;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionCancellation;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionRenewalRequest;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AssetPagination;
import com.workmarket.domains.model.asset.type.CompanyAssetAssociationType;
import com.workmarket.domains.model.asset.type.TaxEntityAssetAssociationType;
import com.workmarket.domains.model.changelog.company.CompanyChangeLogPagination;
import com.workmarket.domains.model.comment.Comment;
import com.workmarket.domains.model.comment.CommentPagination;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIReportFilter;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.note.SubscriptionNote;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.domains.work.service.dashboard.WorkDashboardService;
import com.workmarket.dto.CompanyResource;
import com.workmarket.dto.CompanyResourcePagination;
import com.workmarket.search.request.SearchSortDirection;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.request.work.WorkSearchSortType;
import com.workmarket.search.response.work.DashboardResponse;
import com.workmarket.search.response.work.DashboardStatusFilter;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CommentService;
import com.workmarket.service.business.CompanyChangeLogService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.CompanyCommentDTO;
import com.workmarket.service.business.dto.WorkAggregatesDTO;
import com.workmarket.service.business.dto.WorkFeeBandDTO;
import com.workmarket.service.business.dto.WorkFeeConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionAddOnDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionCancelDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationFormDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionRenewalRequestDTO;
import com.workmarket.service.business.event.company.VendorSearchIndexEvent;
import com.workmarket.service.business.integration.hooks.sugar.SugarIntegrationService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.account.OverAPLimitException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.report.kpi.KpiService;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.Venue;
import com.workmarket.velvetrope.Venue.VenueType;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.forms.admin.company.EditCompanyInfoForm;
import com.workmarket.web.forms.admin.company.finances.AccountServiceTypeConfigurationForm;
import com.workmarket.web.forms.reports.CompanyReportForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.TaxEntityValidator;
import com.workmarket.web.views.HTML2PDFView;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ch.lambdaj.Lambda.convert;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/admin/manage/company")
public class AdminManageCompanyController extends BaseController {

	private static final Log logger = LogFactory.getLog(AdminManageCompanyController.class);

	@Autowired CompanyService companyService;
	@Autowired ProfileService profileService;
	@Autowired TaxService taxService;
	@Autowired PricingService pricingService;
	@Autowired BankingService bankingService;
	@Autowired AuthenticationService authenticationService;
	@Autowired SummaryService summaryService;
	@Autowired CommentService commentService;
	@Autowired WorkReportService workReportService;
	@Autowired LaneService laneService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired InvariantDataService invariantDataService;
	@Autowired AssetManagementService assetManagementService;
	@Autowired UserService userService;
	@Autowired RestTemplate restTemplate;
	@Autowired private WorkDashboardService dashboardService;
	@Autowired KpiService kpiService;
	@Autowired TaxEntityValidator taxEntityValidator;
	@Autowired SubscriptionService subscriptionService;
	@Autowired BillingService billingService;
	@Autowired AccountPricingService accountPricingService;
	@Autowired private WorkSearchService workSearchService;
	@Autowired private AdmissionService admissionService;
	@Autowired SugarIntegrationService sugarIntegrationService;
	@Autowired EventRouter eventRouter;
	@Autowired private MessageSource messageSource;
	@Autowired private CompanyChangeLogService companyChangeLogService;

	@Value("${baseurl}")
	private String baseUrl;

	private static final Map<Integer, String> assignmentSortColumnMap = ImmutableMap.<Integer, String>builder()
		.put(0, WorkSearchDataPagination.SORTS.WORK_ID.name())
		.put(1, WorkSearchDataPagination.SORTS.STATUS.name())
		.put(2, WorkSearchDataPagination.SORTS.TITLE.name())
		.put(3, WorkSearchDataPagination.SORTS.CLIENT.name())
		.put(4, WorkSearchDataPagination.SORTS.LOCATION.name())
		.put(5, WorkSearchDataPagination.SORTS.SCHEDULE_FROM.name())
		.put(6, WorkSearchDataPagination.SORTS.SCHEDULE_FROM.name())
		.put(7, WorkSearchDataPagination.SORTS.RESOURCE.name())
		.build();

	@RequestMapping(
		value = {"", "/", "/index", "search"},
		method = GET)
	public String index() {
		return "web/pages/admin/manage/company/search";
	}


	@RequestMapping(
		value = "/runSearch",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void search(
		Model model,
		HttpServletRequest httpRequest,
		@RequestParam(value = "sSearch", required = false) String searchQuery) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		CompanyAggregatePagination pagination = request.newPagination(CompanyAggregatePagination.class);
		if (StringUtils.isNotEmpty(searchQuery)){
			pagination.addFilter(CompanyAggregatePagination.FILTER_KEYS.COMPANY_NAME, searchQuery);
		}
		pagination = companyService.findAllCompanies(pagination);


		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
		for (CompanyAggregate company : pagination.getResults()) {
			List<String> row = Lists.newArrayList();
			row.add(company.getCompanyName());
			row.add(company.getCustomerType());
			row.add(String.valueOf(company.getLane0Users()));
			row.add(String.valueOf(company.getLane1Users()));
			row.add(String.valueOf(company.getLane2Users()));
			row.add(String.valueOf(company.getLane3Users()));
			row.add(sdf.format(company.getCreatedOn().getTime()));
			response.addRow(row, CollectionUtilities.newObjectMap("company_id", company.getCompanyId()));
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/overview/{id}",
		method = GET)
	public String overview(
		@PathVariable("id") Long companyId,
		Model model) {

		Company company = companyService.findCompanyById(companyId);
		if (company == null) throw new HttpException404(); // TODO remove

		model.addAttribute("company", company);
		model.addAttribute("taxentity", taxService.findActiveTaxEntityByCompany(companyId));
		model.addAttribute("account_register", pricingService.findDefaultRegisterForCompany(companyId));
		model.addAttribute("companyView", "overview");
		model.addAttribute("form", new EditCompanyInfoForm(company));

		ManageMyWorkMarket mmw = company.getManageMyWorkMarket();

		if (CollectionUtilities.containsAny(getCurrentUser().getId(), Constants.JEFF_WALD_USER_ID)) {
			model.addAttribute("enable_payment_terms_override",
				BooleanUtils.isFalse(mmw.getPaymentTermsEnabled()) &&
					BooleanUtils.isFalse(mmw.getPaymentTermsOverride()) &&
					BooleanUtils.isFalse(companyService.hasConfirmedBankAccounts(companyId))
			);
		}

		model.addAttribute("bankAccounts", bankingService.findBankAccountsByCompany(companyId));
		model.addAttribute("money_summary", summaryService.findMoneyAggregateSummaryByCompany(company.getId()));
		model.addAttribute("people_summary", summaryService.findPeopleAggregateSummaryByCompany(company.getId()));

		WorkAggregatesDTO work = summaryService.countWorkByCompany(company.getId());

		model.addAttribute("INPROGRESS_PREFUND", work.getCountForStatus(WorkStatusType.INPROGRESS_PREFUND));
		model.addAttribute("INPROGRESS_PAYMENT_TERMS", work.getCountForStatus(WorkStatusType.INPROGRESS_PAYMENT_TERMS));
		model.addAttribute("COMPLETE", work.getCountForStatus(WorkStatusType.COMPLETE));
		model.addAttribute("PAYMENT_PENDING", work.getCountForStatus(WorkStatusType.PAYMENT_PENDING));
		model.addAttribute("PAID", work.getCountForStatus(WorkStatusType.PAID));

		model.addAttribute("STATUS_DRAFT", work.getCountForStatus(WorkStatusType.DRAFT));
		model.addAttribute("STATUS_SENT", work.getCountForStatus(WorkStatusType.SENT));
		model.addAttribute("STATUS_ACTIVE", work.getCountForStatus(WorkStatusType.ACTIVE));

		WorkFeeConfiguration configuration = pricingService.findActiveWorkFeeConfiguration(companyId);

		model.addAttribute("fees", configuration);
		model.addAttribute("tests_count", summaryService.countAssessmentsByCompany(company.getId()));
		model.addAttribute("currentUserId", getCurrentUser().getId());

		CompanyChangeLogPagination changeLogPagination = new CompanyChangeLogPagination();
		changeLogPagination.setSortColumn(CompanyChangeLogPagination.SORTS.CREATED_ON);
		changeLogPagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		changeLogPagination.setReturnAllRows();

		try {
			changeLogPagination = companyChangeLogService.findAllCompanyChangeLogsByCompanyId(companyId, changeLogPagination);
		} catch (Exception e) {
			throw new RuntimeException("Unknown exception while executing findAllUserChangeLogsByUserId for userId=" + companyId, e);
		}

		model.addAttribute("changelog", changeLogPagination.getResults());

		return "web/pages/admin/manage/company/overview";
	}

	@RequestMapping(
		value = "/work/{id}",
		method = GET)
	public String work(
		@PathVariable("id") Long companyId,
		Model model) {

		Company company = companyService.findCompanyById(companyId);

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		model.addAttribute("company", company);
		model.addAttribute("taxentity", taxService.findActiveTaxEntityByCompany(companyId));
		model.addAttribute("companyView", "work");
		model.addAttribute("fromDate", sdf.format(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30))));
		model.addAttribute("toDate", sdf.format(new Date(System.currentTimeMillis())));

		return "web/pages/admin/manage/company/work";
	}

	@RequestMapping(
		value = "/assignments/{id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void assignments(
		@PathVariable("id") Long companyId,
		Model model,
		HttpServletRequest httpRequest) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(assignmentSortColumnMap);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request);
		response.setEcho(request.getEcho());

		List<User> admins = authenticationService.findAllUsersByACLRoleAndCompany(companyId, AclRole.ACL_ADMIN);
		if (isNotEmpty(admins)) {
			WorkSearchRequest workSearchRequest = new WorkSearchRequest()
				.setStartRow(request.getStart())
				.setPageSize(request.getLimit())
				.setSortBy(WorkSearchSortType.SCHEDULED_FROM)
				.setSortDirection(SearchSortDirection.DESCENDING)
				.setStatusFilter(new DashboardStatusFilter().setStatusCode(WorkStatusType.ACTIVE))
				.setWorkSearchRequestUserType(WorkSearchRequestUserType.CLIENT)
				.setUserNumber(CollectionUtilities.first(admins).getUserNumber());

			DashboardResponse dashboardResponse = dashboardService.getDashboard(workSearchRequest);
			Integer rowCount = NumberUtilities.getNullSafe(dashboardResponse.getDashboardResultList().getTotalResults());

			response.setTotalRecords(rowCount);
			response.setTotalDisplayRecords(rowCount);

			for (SolrWorkData work : dashboardResponse.getResults()) {
				List<String> row = Lists.newArrayList(
					work.getWorkNumber(),
					work.getWorkStatusTypeCode(),
					work.getTitle(),
					StringUtils.isBlank(work.getClientCompanyName()) ? "-" : work.getClientCompanyName(),
					StringUtils.isBlank(work.getPostalCode()) ? "-" : PostalCodeUtilities.formatAddressShort(work.getCity(), work.getState(), work.getPostalCode(), work.getCountry()),
					DateUtilities.format("MM/dd/yyyy h:mma z", work.getScheduleFrom(), work.getTimeZoneId()),
					(work.getTimeToAppointment() > 0) ? DateUtilities.fuzzySpan(new Date(work.getScheduleFrom().getTimeInMillis())) : "-",
					NumberUtilities.currency(work.getSpendLimit()),
					work.getAssignedResourceFullName()
				);

				response.addRow(row, CollectionUtilities.newObjectMap(
						"id", work.getWorkNumber(),
						"assigned_to_id", work.getAssignedResourceUserNumber())
				);
			}
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/statistics/{id}",
		method = GET)
	public String statistics(
		@PathVariable("id") Long companyId,
		Model model,
		@ModelAttribute("filterForm") CompanyReportForm form) {

		Company company = companyService.findCompanyById(companyId);
		if (company != null){
			model.addAttribute("company", company);
		}
		model.addAttribute("companyView", "statistics");
		return "web/pages/admin/manage/company/statistics";
	}

	@RequestMapping(
		value = "/statisticsAjaxReload",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder statisticsUsJson(
		@RequestParam("id") Long companyId,
		@RequestParam("requestId") String requestId) {

		boolean kpiRequestSuccessful = false;
		JSONObject data = new JSONObject();

		if (companyId != null) {
			KPIRequest kpiRequest = kpiService.createKPIRequestForStatisticsAndCreateDateRange(requestId);
			kpiRequest.addToFilters(new Filter(KPIReportFilter.COMPANY, Lists.newArrayList(companyId.toString())));
			try{
				kpiService.populateStatisticsDataWithKPIReports(kpiRequest, data, requestId, companyId, getCurrentUser().getId());
				kpiRequestSuccessful = true;
			} catch (Exception e) {
				logger.error("There was a problem generating a KPI report for: " + companyId, e);
			}
		}

		return new AjaxResponseBuilder()
			.setSuccessful(kpiRequestSuccessful)
			.setData(ImmutableMap.<String, Object>of("ajaxResponse",data.toString()));
	}

	@RequestMapping(
		value = "/report/{id}",
		method = GET)
	public String report(
		@PathVariable("id") Long companyId,
		Model model, @ModelAttribute("filterForm") CompanyReportForm form,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (form.getFromDate() != null && form.getToDate() != null){
			DateRange dateRange = new DateRange(form.getFromDate(), form.getToDate());
			if (dateRange.getThrough().before(dateRange.getFrom())){
				messageHelper.addError(bundle, "admin.manage.company.report.invalid_date_range_from_after_to");
				return "redirect:/admin/manage/company/report/" + companyId;
			} else if (DateUtilities.getMonthsBetween(dateRange.getFrom(), dateRange.getThrough()) > 12){
				messageHelper.addError(bundle, "admin.manage.company.report.invalid_date_range_range_too_large");
				return "redirect:/admin/manage/company/report/" + companyId;
			}
		}
		populateKPIReportsModel(companyId, form, model);
		return "web/pages/admin/manage/company/report";
	}

	@RequestMapping(
		value = "/{id}/report.pdf",
		method = GET)
	public View reportPdf(
		@PathVariable("id") Long companyId,
		Model model,
		@ModelAttribute("filterForm") CompanyReportForm form) {

		populateKPIReportsModel(companyId, form, model);
		return new HTML2PDFView("web/pages/admin/manage/company/report-export");
	}

	private void populateKPIReportsModel(Long companyId, CompanyReportForm form, Model model) {
		KPIRequest kpiRequest = kpiService.createKPIRequestForKPIReport(companyId, form.getFromDate(), form.getToDate());
		Company company = companyService.findCompanyById(companyId);

		form.setFromDate(kpiRequest.getFrom());
		form.setToDate(kpiRequest.getTo());

		model.addAttribute("company", company);
		model.addAttribute("companyView", "report");
		model.addAttribute("accountRegister", pricingService.findDefaultRegisterForCompany(companyId));

		List<TopUser> topUsers = kpiService.getTopUsersByCompany(kpiRequest, 5);
		model.addAttribute("topUsers", topUsers);

		model.addAttribute("reports", kpiService.generateKPIReports(kpiRequest));
	}

	@RequestMapping(
		value = "/resources/{id}",
		method = GET)
	public String resources(
		@PathVariable("id") Long companyId,
		Model model,
		@RequestParam(value = "type", required = false) String type) {

		Company company = companyService.findCompanyById(companyId);

		model.addAttribute("company", company);
		model.addAttribute("taxentity", taxService.findActiveTaxEntityByCompany(companyId));
		model.addAttribute("companyView", "resources");
		model.addAttribute("type", type == null ? "employees" : type);
		model.addAttribute("lane", "contractors".equals(type) ? 2 : 1);

		return "web/pages/admin/manage/company/resources";
	}


	@RequestMapping(
		value = "/resourceslist/{id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void listResources(
		@PathVariable("id") Long companyId,
		Model model,
		HttpServletRequest httpRequest,
		@RequestParam("type") String type) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		if ("employees".equals(type)) {
			request.setSortableColumnMapping(ImmutableMap.<Integer, String>of(
				0, CompanyResourcePagination.SORTS.RESOURCE_LASTNAME.name(),
				1, CompanyResourcePagination.SORTS.ROLES.name(),
				2, CompanyResourcePagination.SORTS.LAST_LOGIN.name()
			));
		} else {
			request.setSortableColumnMapping(ImmutableMap.<Integer, String>of(
				0, CompanyResourcePagination.SORTS.RESOURCE_LASTNAME.name(),
				1, CompanyResourcePagination.SORTS.RESOURCE_COMPANY_NAME.name(),
				2, CompanyResourcePagination.SORTS.LANE.name(),
				3, CompanyResourcePagination.SORTS.YTD_WORK.name(),
				4, CompanyResourcePagination.SORTS.YTD_PAYMENTS.name()
			));
		}

		CompanyResourcePagination pagination = request.newPagination(CompanyResourcePagination.class);
		pagination.addFilter(CompanyAggregatePagination.FILTER_KEYS.COMPANY_ID, companyId);

		pagination = ("employees".equals(type)) ?
			laneService.findAllEmployeesByCompany(companyId, pagination) :
			laneService.findAllContractorsByCompany(companyId, pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");

		for (CompanyResource resource : pagination.getResults()) {
			List<String> row = Lists.newArrayList();

			if (resource.getLaneType() == 1) {
				row.add(resource.getFirstName() + " " + resource.getLastName());
				row.add(resource.getRolesString());
				row.add(resource.getLastLogin() != null ? sdf.format(resource.getLastLogin().getTime()) : "-");
			} else if (resource.getLaneType() == 3 || resource.getLaneType() == 2) {
				row.add(resource.getFirstName() + " " + resource.getLastName());
				row.add(resource.getCompanyName());
				row.add(String.valueOf(resource.getLaneType()));
			} else {
				continue;
			}

			Map<String, Object> meta = Maps.newHashMap();
			meta.put("id", resource.getUserNumber());
			meta.put("user_id", resource.getId());
			// TODO: this is for masquerading link - email and fullName should be in CompanyResource to avoid this call
			User user = userService.findUserByUserNumber(resource.getUserNumber());
			if (user != null) {
				meta.put("email", user.getEmail());
			}

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/change_lane",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody
	AjaxResponseBuilder changeLane(
		@RequestParam(value = "user_id") Long userId,
		@RequestParam(value = "company_id") Long companyId,
		@RequestParam(value = "lane_id") int lane) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		try {
			laneService.updateUserCompanyLaneAssociation(userId, companyId, (lane == 2 ? LaneType.LANE_2 : LaneType.LANE_3));
			messageHelper.addMessage(response, "admin.manage.company.lane_change.success");
			response.setSuccessful(true);
		} catch (Exception e) {
			messageHelper.addMessage(response, "admin.manage.company.lane_change.error");
			logger.warn(String.format("Error changing the lane association of user %d with company %d: ", userId, companyId), e);
		}

		return response;
	}



	@RequestMapping(
		value = "/suspend/{id}",
		method = GET)
	public String suspend(
		Model model,
		@PathVariable("id") Long companyId) {

		model.addAttribute("id", companyId);
		return "web/pages/admin/manage/company/companySuspend";
	}

	@RequestMapping(value = "/reindexwork/{id}")
	public String reindexwork(
		Model model,
		@PathVariable("id") Long companyId) {

		workSearchService.reindexAllWorkByCompanyAsynchronous(companyId);
		return "redirect:/admin/manage/company/overview/" + companyId;
	}

	@RequestMapping(
		value = "/suspend/{id}",
		method = POST)
	public String suspend(
		@RequestParam("comment") String comment,
		@PathVariable("id") Long companyId,
		RedirectAttributes redirectAttributes) {

		profileService.suspendCompany(companyId, comment);

		MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);
		messageHelper.addSuccess(bundle, "company.suspended");

		return "redirect:/admin/manage/company/overview/" + companyId;
	}

	@RequestMapping(
		value = "/unsuspend/{id}",
		method = GET)
	public String unsuspend(
		@PathVariable("id") Long companyId,
		RedirectAttributes redirectAttributes) {

		profileService.unsuspendCompany(companyId);

		MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);
		messageHelper.addSuccess(bundle, "company.unsuspended");

		return "redirect:/admin/manage/company/overview/" + companyId;
	}

	@RequestMapping(
		value = "/edit_company_info",
		method = POST)
	public String editCompanyInfo(
		@Valid @ModelAttribute("form") EditCompanyInfoForm form,
		BindingResult bind,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
		} else {
			try {
				companyService.updateCompanyProperties(form.getId(), CollectionUtilities.newStringMap(
					"name", form.getCompany_name(),
					"operatingAsIndividualFlag", form.getOperating_as_individual_flag().toString()
				));

				messageHelper.addSuccess(bundle, "company.info_updated");
			} catch (Exception e) {
				logger.error(String.format("Error editing company info for company %d: ", form.getId()), e);
				messageHelper.addError(bundle, "company.info_update_failed");
			}
		}

		return "redirect:/admin/manage/company/overview/" + form.getId();
	}


	@RequestMapping(
		value = "/get_company_comments",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getCompanyComments(
		@RequestParam("id") Long companyId,
		HttpServletRequest httpRequest) {


		CommentPagination pagination = new CommentPagination();
		pagination.setSortColumn(CommentPagination.SORTS.CREATED_ON);
		pagination.setSortDirection(SORT_DIRECTION.DESC);
		pagination.setReturnAllRows();

		pagination = commentService.findAllActiveClientServiceCompanyComments(companyId, pagination);

		Map<String, Object> results = Maps.newHashMap();
		List<Map<String, String>> list = Lists.newArrayList();

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		List<Comment> comments = pagination.getResults();
		Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(comments, "creatorId"),
			"firstName", "lastName");

		for (Comment comment : comments) {
			list.add(CollectionUtilities.newStringMap(
				"id", String.valueOf(comment.getId()),
				"comment", comment.getComment(),
				"user_id", String.valueOf(comment.getCreatorId()),
				"name", StringUtilities.fullName((String) creatorProps.get(comment.getCreatorId()).get("firstName"), (String) creatorProps.get(comment.getCreatorId()).get("lastName")),
				"date", sdf.format(comment.getCreatedOn().getTime())));
		}

		results.put("results", list);
		results.put("successful", true);

		return results;
	}


	@RequestMapping(
		value = "/get_company_attachments/{id}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getCompanyAttachments(
		@PathVariable("id") Long companyId,
		HttpServletRequest httpRequest) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		AssetPagination pagination = assetManagementService.findAllCsrInternalAssetsByCompany(companyId, new AssetPagination(true));

		DataTablesResponse.newInstance(request, pagination);

		Map<String, Object> results = Maps.newHashMap();
		List<Map<String, String>> list = Lists.newArrayList();

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyyy");

		for (Asset asset : pagination.getResults()) {
			list.add(CollectionUtilities.newStringMap(
				"id", String.valueOf(asset.getId()),
				"name", asset.getName(),
				"description", asset.getDescription(),
				"date", sdf.format(asset.getCreatedOn().getTime()),
				"uri", asset.getUri()));
		}

		results.put("results", list);
		results.put("successful", true);

		return results;
	}


	@RequestMapping(
		value = "/add_comment_to_company",
		method = POST)
	public String addCommentToCompany(
		@RequestParam("comment") String comment,
		@RequestParam("id") Long companyId) {

		CompanyCommentDTO dto = new CompanyCommentDTO();

		dto.setComment(comment);
		dto.setCompanyId(companyId);

		commentService.saveOrUpdateClientServiceCompanyComment(dto);

		return "redirect:/admin/manage/company/overview/" + companyId;
	}


	@RequestMapping(
		value = "/update_ap_limit",
		method = POST)
	public @ResponseBody Map<String, Object> updateApLimit(
		@RequestParam("ap_limit") String limit,
		@RequestParam("id") Long companyId) {

		Map<String, Object> results = Maps.newHashMap();

		try {
			// Only Jeff + Jeff, etc., can change terms
			if (getCurrentUser().hasAnyRoles("ROLE_WM_ACCOUNTING")) {
				pricingService.updateAPLimit(companyId, limit);
				results.put("successful", true);
			} else {
				results.put("successful", false);
				results.put("errors", new Object[]{messageHelper.getMessage("admin.manage.company.update_ap_limit.not_allowed")});
			}
		} catch (OverAPLimitException e) {
			results.put("successful", false);
			logger.error(e);
			results.put("errors", new Object[]{messageHelper.getMessage("admin.manage.company.update_ap_limit.over_ap_limit")});
		} catch (Exception e) {
			results.put("successful", false);
			logger.error(e);

			results.put("errors", new Object[]{messageHelper.getMessage("admin.manage.company.update_ap_limit.exception")});
		}

		return results;
	}


	@RequestMapping(
		value = "/delete_comment_of_company",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deleteCommentOfCompany(
		@RequestParam("id") Long commentId) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		try {
			commentService.deleteComment(commentId);
		} catch (Exception e) {
			logger.error("Problem deleting comment: ", e);
			messageHelper.addMessage(response, "admin.manage.company.delete_comment_of_company.exception");
		}

		if (isNotEmpty(response.getMessages())) {
			return response;
		}

		messageHelper.addMessage(response, "admin.manage.company.delete_comment_of_company.success");

		return response.setSuccessful(true);
	}


	@RequestMapping(
		value = "/delete_attachment_of_company/{companyId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> deleteAttachmentOfCompany(
		@PathVariable Long companyId,
		@RequestBody Map<String, Long> asset,
		HttpServletResponse response) {

		String message;

		try {
			assetManagementService.removeAssetFromCompany(asset.get("id"), companyId);
			message = messageSource.getMessage("admin.manage.company.delete_attachment_of_company.success", null, null);
		} catch (Exception e) {
			logger.error("Problem deleting attachment: ", e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			message = messageSource.getMessage("admin.manage.company.delete_attachment_of_company.exception", null, null);
		}

		return CollectionUtilities.newObjectMap(
			"message", message
		);
	}


	@RequestMapping(
		value = "/update_fee_ranges",
		method = POST)
	public String updateFeeRanges(
		@RequestParam("id") Long companyId,
		@ModelAttribute("fees") WorkFeeConfiguration fees,
		RedirectAttributes redirectAttributes) {

		List<WorkFeeBandDTO> bands = Lists.newArrayList();

		for (WorkFeeBand band : fees.getWorkFeeBands()) {
			WorkFeeBandDTO bandDTO = new WorkFeeBandDTO();
			bandDTO.setMaximum(band.getMaximum() != null ? band.getMaximum().toString() : WorkFeeBand.MAXIMUM.toString());
			bandDTO.setMinimum(band.getMinimum().toString());
			bandDTO.setPercentage(band.getPercentage() != null ? band.getPercentage().toString() : Constants.DEFAULT_WORK_FEE_PERCENTAGE.toString());

			bands.add(bandDTO);
		}

		WorkFeeConfigurationDTO dto = new WorkFeeConfigurationDTO();
		dto.setWorkFeeBandDTOs(bands);

		pricingService.saveAndActivateWorkFeeConfiguration(companyId, dto);

		MessageBundle bundle = messageHelper.newFlashBundle(redirectAttributes);
		messageHelper.addSuccess(bundle, "company.fees_updated");

		return "redirect:/admin/manage/company/pricing/" + companyId;
	}


	@RequestMapping(
		value = "/unlock/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE,
		consumes = MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody AjaxResponseBuilder unlock(
		@PathVariable("id") Long companyId,
		@RequestParam("hours") String hours,
		@RequestParam("comment") String comment,
		@RequestParam(value = "qqfile", required = false) MultipartFile attachment,
		RedirectAttributes flash) throws IOException, HostServiceException, AssetTransformationException {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		if (!StringUtils.isNumeric(hours))
			messageHelper.addMessage(response, "admin.manage.company.unlock.hours");
		if (StringUtils.isEmpty(comment))
			messageHelper.addMessage(response, "admin.manage.company.unlock.comment");

		if (isNotEmpty(response.getMessages()))
			return response;


		companyService.unlockCompanyAccount(companyId, Integer.valueOf(hours));

		CompanyCommentDTO dto = new CompanyCommentDTO();
		dto.setComment(comment);
		dto.setCompanyId(companyId);

		commentService.saveOrUpdateClientServiceCompanyComment(dto);

		if (attachment != null && attachment.getSize() > 0) {
			File tempFile = File.createTempFile("unlock_" + companyId, ".dat");
			FileOutputStream fos = new FileOutputStream(tempFile);
			try {
				int bytesCopied = IOUtils.copy(attachment.getInputStream(), fos);

				AssetDTO assetDto = new AssetDTO();
				assetDto.setMimeType(URLConnection.guessContentTypeFromName(attachment.getOriginalFilename()));
				assetDto.setName(attachment.getOriginalFilename());
				assetDto.setAssociationType(TaxEntityAssetAssociationType.NONE);
				assetDto.setFileByteSize(bytesCopied);
				assetDto.setSourceFilePath(tempFile.getAbsolutePath());

				Asset asset = assetManagementService.storeAssetForCompany(assetDto, companyId);

				assetDto.setAssetId(asset.getId());
				assetDto.setAssociationType(CompanyAssetAssociationType.CLIENT_SERVICES_INTERNAL);

				assetManagementService.addAssetToCompany(assetDto, companyId);

			} catch (Exception e) {
				logger.error("Error adding asset on company unlock", e);

			} finally {
				try {
					tempFile.delete();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		messageHelper.addMessage(response, "company.unlocked");

		return response.setSuccessful(true);
	}

	@RequestMapping(
		value = "/override_payterms/{companyId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder submitOverridePayterms(
		@PathVariable("companyId") Long companyId,
		@RequestParam(value="note", required=false) String note) {

		AjaxResponseBuilder result = new AjaxResponseBuilder();

		MessageBundle bundle = messageHelper.newBundle();
		if (StringUtils.isBlank(note)) {
			messageHelper.addError(bundle, "NotEmpty", "Note");
			return result
				.setSuccessful(false)
				.setMessages(bundle.getErrors());
		}

		result.setRedirect("/admin/manage/company/finances/" + companyId);

		try {
			companyService.overridePaymentTerms(companyId, note);
			messageHelper.addSuccess(bundle, "admin.manage.company.overview.success");
			return result
				.setSuccessful(true)
				.setMessages(bundle.getSuccess());
		} catch (Exception e) {
			logger.error(e);
			messageHelper.addError(bundle, "admin.manage.company.overview.exception");
			return result
				.setSuccessful(false)
				.setMessages(bundle.getErrors());
		}
	}

	@RequestMapping(
		value = "/sales/{id}",
		method = GET)
	public String sales(
		@PathVariable("id") Long companyId,
		Model model) {

		Company company = companyService.findCompanyById(companyId);

		model.addAttribute("companyView", "sales");
		model.addAttribute("company", company);
		model.addAttribute("taxentity", taxService.findActiveTaxEntityByCompany(companyId));
		model.addAttribute("currentUserId", getCurrentUser().getId());

		return "web/pages/admin/manage/company/sales";
	}

	@RequestMapping(value = "/{companyId}/accountOwner", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder fullResources(@PathVariable("companyId") String companyId) throws Exception {
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		String accountOwner = sugarIntegrationService.getAccountOwner(companyId);
		return response.setSuccessful(true).addData("results", accountOwner);
	}

	@RequestMapping(value = "/finances/{id}",method = GET)
	public String finances(@PathVariable("id") Long companyId, Model model) {
		Company company = companyService.findCompanyById(companyId);

		model.addAttribute("company", company);
		model.addAttribute("companyView", "finances");
		model.addAttribute("taxentity", taxService.findActiveTaxEntityByCompany(companyId));
		model.addAttribute("account_register", pricingService.findDefaultRegisterForCompany(companyId));
		model.addAttribute("payment_configuration", companyService.getPaymentConfiguration(companyId));
		model.addAttribute("currentUserId", getCurrentUser().getId());
		model.addAttribute("customerType", company.getCustomerType());


		List<Country> countries = buildListOfServiceTypeCountries();
		model.addAttribute("countries",countries);

		List<AccountServiceType> accountServiceTypes = accountPricingService.findAllAccountServiceType();
		AccountServiceTypeConfigurationForm form = new AccountServiceTypeConfigurationForm();
		form.setVipFlag(company.isVipFlag());
		form.setCustomerType(company.getCustomerType());
		model.addAttribute("account_service_types", accountServiceTypes);
		model.addAttribute("vip_permission", getCurrentUser().hasAnyRoles("ROLE_WM_ACCOUNTING"));
		model.addAttribute("form", form);

		if (company.getVipSetOn() != null) {
			// Note: we lazily load the User via a service call instead of defining a FetchProfile
			// when we get the Company, as we are anticipating very few companies will be VIP and so
			// this if statement should be hit much less than the controller action.
			User u = userService.findUserById(company.getVipSetBy());
			model.addAttribute("vip_set_by_first_name", u.getFirstName());
			model.addAttribute("vip_set_by_last_name", u.getLastName());
			model.addAttribute("vip_set_on", company.getVipSetOn());
		}


		ManageMyWorkMarket mmw = company.getManageMyWorkMarket();
		AccountStatementDetailPagination pagination = new AccountStatementDetailPagination(true);

		if (getCurrentUser().hasAnyRoles("ROLE_WM_ACCOUNTING")) {
			model.addAttribute("enable_payment_terms_override",
				BooleanUtils.isFalse(mmw.getPaymentTermsEnabled()) &&
					BooleanUtils.isFalse(mmw.getPaymentTermsOverride()) &&
					BooleanUtils.isFalse(companyService.hasConfirmedBankAccounts(companyId))
			);
			model.addAttribute("enable_account_service_type_edit", true);
		}

		model.addAttribute("bankAccounts", bankingService.findBankAccountsByCompany(companyId));

		AccountStatementFilters filters = new AccountStatementFilters();
		filters.setInvoiceType(SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE);
		filters.setPaidStatus(false);
		filters.setPayables(true);
		pagination = billingService.getStatementDashboard(filters, pagination);

		model.addAttribute("subscription_invoices", pagination.getResults());

		return "web/pages/admin/manage/company/finances";
	}

	@RequestMapping(
		value = "/finances/save_account_service_type_configuration/{id}",
		method = POST)
	public String saveAccountServiceTypeConfiguration(
		@PathVariable("id") Long companyId,
		@Valid @ModelAttribute("form") AccountServiceTypeConfigurationForm form,
		BindingResult bindingResult,
		RedirectAttributes flash,
		Model model) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return "redirect:/admin/manage/company/finances/" + companyId;
		}

		if (!form.isValid()) {
			messageHelper.addError(bundle, "admin.manage.company.finances.invalid");
			return "redirect:/admin/manage/company/finances/" + companyId;
		}

		List<AccountServiceTypeDTO> accountServiceTypeList = form.toDTOList();
		Company company = companyService.findCompanyById(companyId);

		if (company != null && company.getAccountPricingType().isTransactionalPricing()
			&& !accountServiceTypeList.isEmpty()) {
			try {
				accountPricingService.updateCompanyAccountServiceType(company.getId(), accountServiceTypeList);
				messageHelper.addSuccess(bundle, "admin.manage.company.finances.success");
			} catch (Exception e) {
				logger.error(e.getMessage());
				messageHelper.addError(bundle, "admin.manage.company.finances.exception");
			}
		}

		if (company != null && form.getVipFlag() != null && getCurrentUser().hasAnyRoles("ROLE_WM_ACCOUNTING")) {
			try {
				companyService.markAsVip(company.getId(), form.getVipFlag());
				messageHelper.addSuccess(bundle, "admin.manage.company.finances.vip_locking.success");
			} catch (Exception e) {
				logger.error(e.getMessage());
				messageHelper.addError(bundle, "admin.manage.company.finances.vip_locking.exception");
			}

		}

		if (company != null && form.getCustomerType() != null) {
			try {
				companyService.setCustomerType(companyId, form.getCustomerType());
				messageHelper.addSuccess(bundle, "admin.manage.company.finances.customer_type.success");
			} catch (Exception e) {
				logger.error(e.getMessage());
				messageHelper.addError(bundle, "admin.manage.company.finances.customer_type.exception");
			}

		}


		return "redirect:/admin/manage/company/finances/" + companyId;
	}


	/**
	 * Return a list of countries for service type
	 */
	private List<Country> buildListOfServiceTypeCountries() {
		List<Country> countries = Lists.newArrayList();
		for(String country : Country.WM_SUPPORTED_COUNTRIES){
			countries.add(Country.newInstance(country));
		}

		return countries;
	}


	/**
	 * Auxiliary method for binding a SubscriptionConfiguration to a SubscriptionConfigurationFormDTO
	 * @param subscription
	 * @return
	 */
	private SubscriptionConfigurationFormDTO buildSubscriptionConfigurationFormDTO(SubscriptionConfiguration subscription) {
		SubscriptionConfigurationFormDTO formDTO = new SubscriptionConfigurationFormDTO();

		if (subscription != null) {
			User creator = userService.findUserById(subscription.getCreatorId());
			formDTO.setSubscriptionConfigurationId(subscription.getId());
			formDTO.setCompanyId(subscription.getCompany().getId());
			formDTO.setCreator(creator);
			formDTO.setEffectiveDateMonth(subscription.getEffectiveDate().get(Calendar.MONTH));
			formDTO.setEffectiveDateYear(subscription.getEffectiveDate().get(Calendar.YEAR));
			formDTO.setSignedDate(subscription.getSignedDate());
			formDTO.setSubscriptionPeriod(subscription.getSubscriptionPeriod().getMonths());
			formDTO.setNumberOfMonths(subscription.getSubscriptionPeriod().getMonths() * subscription.getNumberOfPeriods());
			formDTO.setPaymentTermsDays(subscription.getPaymentTermsDays());
			formDTO.setVendorOfRecord(subscription.isVendorOfRecord());
			formDTO.setCancellationOption(subscription.getCancellationOption());
			formDTO.setClientRefId(subscription.getClientRefId());
			formDTO.setAutoRenewal(subscription.getNumberOfRenewals());

			SubscriptionFeeConfiguration feeConfiguration;
			if (subscription.isPending() || subscription.isNotReady()) {
				feeConfiguration = subscription.getLatestPendingApprovalFeeConfiguration();
			} else {
				feeConfiguration = subscription.getActiveSubscriptionFeeConfiguration();
			}
			formDTO.setSubscriptionTypeCode(feeConfiguration.getSubscriptionType() == null ? null : feeConfiguration.getSubscriptionType().getCode());
			formDTO.setBlockTierPercentage(feeConfiguration.getBlockTierPercentage());

			// Add-ons
			List<SubscriptionAddOnTypeAssociation> subscriptionAddons = subscription.getAddOns();
			if (isNotEmpty(subscriptionAddons)) {
				List<SubscriptionAddOnDTO> addOnDTOs = Lists.newArrayList();
				for (SubscriptionAddOnTypeAssociation addonAssociation: subscription.getAddOns()) {
					addOnDTOs.add(new SubscriptionAddOnDTO(addonAssociation));
				}

				formDTO.setHasAddOns(true);
				formDTO.setSubscriptionAddOnDTOs(addOnDTOs);
			}

			// Discount options and setup fee
			formDTO.setHasDiscountOptions(subscription.hasDiscount());
			formDTO.setDiscountNumberOfPeriods(subscription.getDiscountedPeriods());
			formDTO.setDiscountPerPeriod(subscription.getDiscountedAmountPerPeriod());
			formDTO.setSetUpFee(subscription.getSetUpFee());

			// Payment tiers
			List<SubscriptionPaymentTierDTO> tierDTOs = Lists.newArrayList();
			for (SubscriptionPaymentTier tier: subscription.getSubscriptionPaymentTiers()) {
				SubscriptionPaymentTierDTO tierDTO = new SubscriptionPaymentTierDTO();

				BeanUtilities.copyProperties(tierDTO, tier);
				tierDTOs.add(tierDTO);
			}

			formDTO.setPricingRanges(tierDTOs);

			// Account service types
			List<AccountServiceTypeDTO> accountServiceTypeDTOs = Lists.newArrayList();
			for (SubscriptionAccountServiceTypeConfiguration serviceType: subscription.getAccountServiceTypeConfigurations()) {
				AccountServiceTypeDTO dto = new AccountServiceTypeDTO();

				dto.setCountryCode(serviceType.getCountry().getId());
				dto.setAccountServiceTypeCode(serviceType.getAccountServiceType().getCode());
				accountServiceTypeDTOs.add(dto);
			}
			formDTO.setAccountServiceTypeDTOs(accountServiceTypeDTOs);

			// Additional Notes (NOTE: it was not specified in the requirements which note must be shown, so we take the last one)
			List<SubscriptionNote> additionalNotes = Lists.newArrayList(subscription.getNotes());
			if (isNotEmpty(additionalNotes)) {
				formDTO.setAdditionalNotes(CollectionUtilities.last(subscription.getNotes()).getContent());
			}
		}

		return formDTO;
	}


	@RequestMapping(
		value = "/pricing/{id}",
		method = GET)
	public String pricing(
		@PathVariable("id") final Long companyId,
		Model model) {

		if (getCurrentUser().hasAnyRoles("ROLE_WM_ACCOUNTING")) {
			model.addAttribute("enable_fee_band_edit", true);
		}

		Company company = companyService.findCompanyById(companyId);

		model.addAttribute("company", company);
		model.addAttribute("companyView", "pricing");
		model.addAttribute("payment_configuration", companyService.getPaymentConfiguration(companyId));
		model.addAttribute("currentUserId", getCurrentUser().getId());

		// Transactional fee ranges
		WorkFeeConfiguration configuration = pricingService.findActiveWorkFeeConfiguration(companyId);
		model.addAttribute("fees", configuration);

		// Determine subscription status
		Long subscriptionId = null;
		Map<String, Boolean> subStatus = Maps.newHashMap();
		SubscriptionConfiguration activeSubscription = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(companyId);
		SubscriptionConfiguration subscriptionData = null;
		Long subscriptionCreatorId = null;

		if (company.getAccountPricingType().isSubscriptionPricing() || activeSubscription != null) {
			// Company is in subscription mode
			subStatus.put("isActive", true);
			subscriptionId = activeSubscription.getId();
			subscriptionData = activeSubscription;
			subscriptionCreatorId = activeSubscription.getCreatorId();

			SubscriptionRenewalRequest renewalPending = subscriptionService.findLatestPendingApprovalSubscriptionRenewalRequest(subscriptionId);
			subStatus.put("hasRenewalPending", renewalPending != null);

			SubscriptionCancellation cancel = activeSubscription.getSubscriptionCancellation();
			subStatus.put("hasCancelPending", cancel != null && cancel.isPendingApproval());
			subStatus.put("hasCancelApproved", cancel != null && cancel.isApproved());

			//subStatus.put("hasEditPending", activeSubscription.hasPendingApprovalEditions());

			if (company.getAccountPricingType().isTransactionalPricing()) {
				// Effective date not reached. Set submitter and submission date
				User creator = userService.findUserById(activeSubscription.getCreatorId());
				model.addAttribute("subscription_submitter", creator);
				model.addAttribute("subscription_date", activeSubscription.getCreatedOn());
			}
		} else {
			SubscriptionConfiguration pendingApprovalSubscription = subscriptionService.findLatestPendingApprovalSubscriptionConfigurationByCompanyId(companyId);
			subStatus.put("pendingApproval", pendingApprovalSubscription != null);

			// Company is in transactional mode; we have to find if there's any subscription pending approval
			if (pendingApprovalSubscription != null) {
				subscriptionData = pendingApprovalSubscription;
				subscriptionCreatorId = pendingApprovalSubscription.getCreatorId();

				// Set id, submitter and submission date
				User creator = userService.findUserById(pendingApprovalSubscription.getCreatorId());
				subscriptionId = pendingApprovalSubscription.getId();
				model.addAttribute("subscription_submitter", creator);
				model.addAttribute("subscription_date", pendingApprovalSubscription.getCreatedOn());
			} else {
				// Check if we have any saved subscription
				SubscriptionConfiguration notReadySubscription = subscriptionService.findLatestNotReadySubscriptionConfigurationByCompanyId(companyId);
				if (notReadySubscription != null) {
					subscriptionData = notReadySubscription;
					subscriptionId = subscriptionData.getId();
					subscriptionCreatorId = notReadySubscription.getCreatorId();
				}
			}
		}

		Map<String, Object> props = userService.getProjectionMapById(subscriptionCreatorId, "firstName", "lastName");
		model.addAttribute("subscription_creator_full_name", StringUtilities.fullName((String)props.get("firstName"), (String)props.get("lastName")));

		model.addAttribute("subscription_id", subscriptionId);
		model.addAttribute("subscription", buildSubscriptionConfigurationFormDTO(subscriptionData));
		model.addAttribute("subStatus", subStatus);

		// For active subscriptions, edition effective date must be later than the latest invoiced payment period,
		// so we pass that date to the model.
		Calendar nextPossibleUpdateDate = subscriptionService.findNextPossibleSubscriptionUpdateDate(companyId);
		model.addAttribute("next_possible_update_date", nextPossibleUpdateDate);

		// Determine if a subscription renewal can be requested
		model.addAttribute("subscription_can_renew",
			(activeSubscription != null) && (subscriptionService.findRenewSubscriptionConfiguration(activeSubscription.getId()) == null));

		model.addAttribute("previous_subscriptions", subscriptionService.findPreviousSubscriptionConfigurationsByCompanyId(companyId));
		model.addAttribute("countries",buildListOfServiceTypeCountries());
		model.addAttribute("account_service_types", accountPricingService.findAllAccountServiceType());
		model.addAttribute("addOnTypes", subscriptionService.findAllSubscriptionAddOnTypes());
		LinkedList<String> subscriptionTypeCodes = Lists.newLinkedList(SubscriptionType.SUBSCRIPTION_TYPE_CODES);
		subscriptionTypeCodes.addFirst("- Select -");
		model.addAttribute("subscriptionTypes", subscriptionTypeCodes);

		return "web/pages/admin/manage/company/pricing";
	}


	/**
	 * Submit or save a subscription configuration
	 */
	private Long submitOrSaveSubscription(SubscriptionConfigurationFormDTO form, boolean submitForApproval,
										  RedirectAttributes redirectAttributes, MessageBundle messages) {
		Company company = companyService.findCompanyById(form.getCompanyId());

		// This should not happen unless the form is manipulated, so we throw a Bad Request Error
		if (company == null) {
			logger.error("Company ID is invalid trying to submit a subscription configuration");
			throw new HttpException400();
		}

		Long companyId = company.getId();
		SubscriptionConfigurationDTO subscription = form.toSubscriptionConfigurationDTO();

		SubscriptionConfiguration updatedSubscription = new SubscriptionConfiguration();
		try {
			updatedSubscription = subscriptionService.saveOrUpdateSubscriptionConfigurationForCompany(companyId, subscription, submitForApproval);
			messageHelper.addSuccess(messages,
				submitForApproval ? "admin.manage.company.pricing.subscription_submit_success" : "admin.manage.company.pricing.subscription_save_success");
		} catch (Exception ex) {
			logger.error("", ex);
			messageHelper.addError(messages,
				submitForApproval ? "admin.manage.company.pricing.subscription_submit_error" : "admin.manage.company.pricing.subscription_save_error");
		}

		redirectAttributes.addFlashAttribute("bundle", messages);

		return updatedSubscription.getId();
	}


	/**
	 * Submit a subscription configuration for approval.
	 */
	@RequestMapping(
		value = "/submit_subscription",
		method = POST)
	public String submitSubscription(
		@Valid @ModelAttribute("subscriptionForm") SubscriptionConfigurationFormDTO form,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes redirectAttributes,
		MessageBundle messages) {

		// Submit the subscription for approval (submitForApproval = true)
		submitOrSaveSubscription(form, true, redirectAttributes, messages);

		model.addAttribute("subscription", form);
		return "redirect:/admin/manage/company/pricing/" + form.getCompanyId();
	}


	/**
	 * Save a subscription configuration.
	 */
	@RequestMapping(
		value = "/save_subscription",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder saveSubscription(
		@Valid @ModelAttribute("subscriptionForm") SubscriptionConfigurationFormDTO form,
		BindingResult bindingResult,
		Model model,
		RedirectAttributes redirectAttributes,
		MessageBundle messages) {

		AjaxResponseBuilder response = new AjaxResponseBuilder();

		// Save the subscription, but not submit it (submitForApproval = false)
		Long subscriptionId = submitOrSaveSubscription(form, false, redirectAttributes, messages);

		if (messages.hasErrors()) {
			response.setSuccessful(false);
			response.setMessages(messages.getErrors());
		} else {
			Map<String, Object> data = Maps.newHashMap();
			data.put("subscription_id", subscriptionId);

			response.setSuccessful(true);
			response.setData(data);
			response.setMessages(messages.getSuccess());
		}

		return response;
	}


	/**
	 * Cancel the active subscription for a company
	 */
	@RequestMapping(
		value = "/cancel_subscription",
		method = POST)
	public String cancelSubscription(
		@RequestParam("company_id") Long companyId,
		@RequestParam("cancellation_date") Date cancellationDate,
		@RequestParam("penalty_amount") String cancellationFee,
		@RequestParam("note") String note,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		Company company = companyService.findCompanyById(companyId);
		if (company == null) {
			throw new HttpException400();
		}

		SubscriptionConfiguration subscription = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(companyId);
		if (subscription == null) {
			messageHelper.addError(bundle, "admin.manage.company.pricing.subscription_cancel_error");
			return "redirect:/admin/manage/company/pricing/" + companyId;
		}

		SubscriptionCancelDTO cancelDTO = new SubscriptionCancelDTO();
		cancelDTO.setCancellationDate(DateUtilities.getCalendarFromDate(cancellationDate));
		cancelDTO.setCancellationFee(Double.valueOf(cancellationFee));
		cancelDTO.setNote(note);

		try {
			subscriptionService.submitCancellationForSubscriptionConfiguration(subscription.getId(), cancelDTO);
			messageHelper.addSuccess(bundle, "admin.manage.company.pricing.subscription_cancel_success");
		} catch (Exception ex) {
			logger.error("", ex);
			messageHelper.addError(bundle, "admin.manage.company.pricing.subscription_cancel_error");
		}

		return "redirect:/admin/manage/company/pricing/" + companyId;
	}


	/**
	 * Renew the active subscription for a company
	 * @return
	 */
	@RequestMapping(
		value="/renew_subscription",
		method = POST)
	public String renewSubscription(
		@Valid @ModelAttribute("renewalForm") SubscriptionRenewalRequestDTO form,
		Model model,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		SubscriptionConfiguration subscription = subscriptionService.findSubscriptionConfigurationById(form.getParentSubscriptionId());
		if (subscription == null || !subscription.isActive() || form.getNumberOfPeriods() <= 0) {
			throw new HttpException400();
		}

		// Process new pricing tiers (if needed)
		if (form.getModifyPricing()) {
			List<SubscriptionPaymentTier> currentTiers = subscription.getSubscriptionPaymentTiers();
			List<SubscriptionPaymentTierDTO> renewalTiers = form.getSubscriptionPaymentTierDTOs();

			// Set minimum and maximum from existing subscription
			for (int i = 0; i < currentTiers.size(); ++i) {
				SubscriptionPaymentTier currentTier = currentTiers.get(i);
				SubscriptionPaymentTierDTO newTier = renewalTiers.get(i);

				newTier.setMinimum(currentTier.getMinimum());
				newTier.setMaximum(currentTier.getMaximum());
			}
		}

		try {
			subscriptionService.submitSubscriptionRenewalRequest(form);
			messageHelper.addSuccess(bundle, "admin.manage.company.pricing.subscription_renew.success");
		} catch (Exception ex) {
			logger.error("", ex);
			messageHelper.addError(bundle, "admin.manage.company.pricing.subscription_renew.error");
		}

		return "redirect:/admin/manage/company/pricing/" + subscription.getCompany().getId();
	}


	@RequestMapping(
		value = "/issue_future_invoice/{companyId}/{subscriptionId}",
		method = GET)
	public String issueFutureInvoicePreview(
		@PathVariable("subscriptionId") Long subscriptionId,
		@PathVariable("companyId") Long companyId,
		Model model) {

		Calendar futureRangeFrom = DateUtilities.getCalendarNow();
		Calendar futureRangeTo = DateUtilities.getCalendarNow();
		Calendar futureInvoiceDueDate = DateUtilities.getCalendarNow();
		BigDecimal futureInvoiceBalance = BigDecimal.ZERO;
		int currentInvoicesOutstandingNumber = 0;

		SubscriptionPaymentPeriod subscriptionPaymentPeriod = subscriptionService.findNextNotInvoicedSubscriptionPaymentPeriod(subscriptionId);

		if(subscriptionPaymentPeriod != null){
			futureRangeFrom = subscriptionPaymentPeriod.getPeriodDateRange().getFrom();
			futureRangeTo = subscriptionPaymentPeriod.getPeriodDateRange().getThrough();

			futureInvoiceDueDate = DateUtilities.cloneCalendar(futureRangeFrom);
			futureInvoiceDueDate.add(Calendar.MONTH, 1);

			futureInvoiceBalance = subscriptionService.calculateSubscriptionPaymentTotalAmount(subscriptionId);
		}

		List<User> users = authenticationService.findAllUsersByACLRoleAndCompany(companyId, AclRole.ACL_ADMIN);
		if (isNotEmpty(users)) {
			User firstAdminUser = users.get(0);
			AccountStatementDetailPagination pagination = new AccountStatementDetailPagination(true);
			AccountStatementFilters filters = new AccountStatementFilters();
			filters.setInvoiceType(SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE);
			filters.setPaidStatus(false);
			filters.setPayables(true);

			pagination = billingService.getStatementDashboardForUser(filters, pagination, firstAdminUser);

			currentInvoicesOutstandingNumber = pagination.getResults().size();
		}

		model.addAttribute("subscription_id", subscriptionId);
		model.addAttribute("company_id", companyId);
		model.addAttribute("future_range_from", futureRangeFrom);
		model.addAttribute("future_range_to", futureRangeTo);
		model.addAttribute("future_invoice_due_date", futureInvoiceDueDate);
		model.addAttribute("future_invoice_balance", futureInvoiceBalance);
		model.addAttribute("current_invoices_outstanding_number", currentInvoicesOutstandingNumber);

		return "web/partials/admin/manage/company/issue_future_invoice";
	}

	@RequestMapping(
		value = "/issue_future_invoice/{companyId}/{subscriptionId}",
		method = POST)
	public String issueFutureInvoice(
		@PathVariable("subscriptionId") Long subscriptionId,
		@PathVariable("companyId") Long companyId,
		RedirectAttributes redirectAttributes,
		Model model) {

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);

		SubscriptionPaymentPeriod subscriptionPaymentPeriod = subscriptionService.findNextNotInvoicedSubscriptionPaymentPeriod(subscriptionId);

		if (subscriptionPaymentPeriod != null){
			try{
				subscriptionService.issueFutureSubscriptionInvoice(subscriptionPaymentPeriod.getId());
				messageHelper.addSuccess(messages, "admin.manage.company.pricing.future_invoice.success");
			} catch (Exception e){
				logger.error(e.getMessage());
				messageHelper.addError(messages, "admin.manage.company.pricing.future_invoice.error");
			}
		} else{
			messageHelper.addError(messages, "admin.manage.company.pricing.future_invoice.subscription_id.error");
		}

		return "redirect:/admin/manage/company/pricing/" + companyId;
	}

	@RequestMapping(value = "/features/{companyId}")
	public String features(
		@PathVariable final Long companyId,
		Model model) {

		Company company = companyService.findCompanyById(companyId);
		if (company == null) { throw new HttpException404(); }

		Boolean hasInternalBetaFeatures = Boolean.FALSE;
		Boolean hasOpenSignUpBetaFeatures = Boolean.FALSE;

		if (!Venue.getVenueSetByType(VenueType.INTERNAL_BETA_FEATURE).isEmpty()) {
			hasInternalBetaFeatures = Boolean.TRUE;
		}

		if (!Venue.getVenueSetByType(VenueType.OPEN_SIGNUP_BETA_FEATURE).isEmpty()) {
			hasOpenSignUpBetaFeatures = Boolean.TRUE;
		}

		model.addAttribute("company", company);
		model.addAttribute("companyView", "features");
		model.addAttribute("hasInternalBetaFeatures", hasInternalBetaFeatures);
		model.addAttribute("hasOpenSignUpBetaFeatures", hasOpenSignUpBetaFeatures);
		model.addAttribute("venues", Venue.values());

		List<Admission> admissions = admissionService.findAllAdmissionsByCompanyIdForVenue(companyId, Venue.values());
		model.addAttribute("admittedVenues", convert(admissions, new PropertyExtractor("venue")));

		return "web/pages/admin/manage/company/features";
	}

	@RequestMapping(
		value = "/admissions/{companyId}/add",
		method = POST)
	@ResponseStatus(value = OK)
	@PreAuthorize("hasAnyRole('ROLE_WM_ADMIN')")
	public void addAdmission(
		@PathVariable("companyId") final Long companyId,
		@RequestParam("venue") Venue venue
	) {
		admissionService.saveAdmissionForCompanyIdAndVenue(companyId, venue);
	}

	@RequestMapping(
		value = "/admissions/{companyId}/remove",
		method = POST)
	@ResponseStatus(value = OK)
	@PreAuthorize("hasAnyRole('ROLE_WM_ADMIN')")
	public void removeAdmission(
		@PathVariable("companyId") final Long companyId,
		@RequestParam("venue") Venue venue
	) {
		admissionService.destroyAdmissionForCompanyIdAndVenue(companyId, venue);

		// TODO[Jim]: This is cool. We should extract it into some kind of callback
		// mechanism so that we can do something like this for ANY Velvet Rope Venue
		// If we don't, things can get cluttered in here.
		if (Venue.EMPLOYEE_WORKER_ROLE == venue) {
			List<User> users = authenticationService.findAllUsersByACLRoleAndCompany(companyId, AclRole.ACL_EMPLOYEE_WORKER);
			for (User user : users) {
				try {
					//revoke employeeWorker role from and assign VIEW_ONLY role to user
					authenticationService.removeAclRoleFromUser(user.getId(), AclRole.ACL_EMPLOYEE_WORKER);
					authenticationService.assignAclRolesToUser(user.getId(), new Long[]{AclRole.ACL_VIEW_ONLY});
					PersonaPreference personaPreference = new PersonaPreference()
						.setUserId(user.getId())
						.setBuyer(true);
					userService.saveOrUpdatePersonaPreference(personaPreference);
				} catch (InvalidAclRoleException e) {
					logger.error(String.format("Error on re-assigning role: %s to user: %s", AclRole.ACL_VIEW_ONLY, user.getUuid()), e);
				}
			}
		}
	}

	@RequestMapping(
		value ="/reindex_vendor/{companyId}",
		method = POST)
	@ResponseStatus(value = OK)
	public void reindexVendor(@PathVariable("companyId") final Long companyId) throws Exception {
		if (companyId == null) {
			throw new HttpException404();
		}

		eventRouter.sendEvent(new VendorSearchIndexEvent(companyId));
	}
}

