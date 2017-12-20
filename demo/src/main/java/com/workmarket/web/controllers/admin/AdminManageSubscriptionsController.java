package com.workmarket.web.controllers.admin;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionUtilities;
import com.workmarket.domains.model.reporting.subscriptions.SubscriptionReportPagination;
import com.workmarket.domains.model.reporting.subscriptions.SubscriptionReportRow;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.SubscriptionReportService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.views.CSVView;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping("/admin/manage/subscriptions")
@PreAuthorize("hasAnyRole('ROLE_WM_SUBS_REPORTS','ROLE_WM_SUBS_APPROVE')")
public class AdminManageSubscriptionsController extends BaseController {
	@Autowired SubscriptionService subscriptionService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private SubscriptionReportService subscriptionReportService;
	@Autowired private UserService userService;

	private static final Character EXPORT_CSV_DELIMITER = ',';

	private static final Logger logger = LoggerFactory.getLogger(AdminManageSubscriptionsController.class);


	/**
	 * Subscription Approval Queue page
	 * @param model
	 * @return
	 */
	@RequestMapping(
		value = "/approval",
		method = GET)
	@PreAuthorize("hasRole('ROLE_WM_SUBS_APPROVE')")
	public String subscriptionApproval(Model model) throws Exception {

		return "web/pages/admin/manage/subscriptions/approval";
	}


	/**
	 * Returns a list of approval/rejection pending subscriptions
	 *
	 */
	@RequestMapping(
		value = "/queue",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_WM_SUBS_APPROVE')")
	public void subscriptionsQueue(HttpServletRequest httpRequest, Model model) throws Exception {

		// Fetch the subscriptions
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		SubscriptionConfigurationPagination pagination = new SubscriptionConfigurationPagination(false);
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination = subscriptionService.findAllPendingApprovalSubscriptionConfigurations(pagination);

		// Format the results
		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);
		List<SubscriptionConfiguration> subscriptions = pagination.getResults();
		Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(subscriptions, "creatorId"),
			"firstName", "lastName");

		for (SubscriptionConfiguration subscription : subscriptions) {
			List<String> row = Lists.newArrayList(
				// ID
				String.valueOf(subscription.getId()),

				// Company ID
				String.valueOf(subscription.getCompany().getId()),

				// Type
				String.valueOf(SubscriptionUtilities.APPROVAL_TYPE.lookupByCode(subscription.getApprovalType().ordinal())),

				// Company Name
				subscription.getCompany().getName(),

				// Submitted by
				StringUtilities.fullName((String) creatorProps.get(subscription.getCreatorId()).get("firstName"), (String) creatorProps.get(subscription.getCreatorId()).get("lastName")),

				// Effective Date
				DateUtilities.format("MM/dd/yyyy", subscription.getEffectiveDate()),

				// VOR (Vendor Of Record)
				StringUtilities.toYesNo(subscription.isVendorOfRecord()),

				// Auto Renewal
				SubscriptionUtilities.subscriptionAutoRenewalAsString(subscription),

				// Payment period
				subscription.getSubscriptionPeriod().getPeriodAsString(),

				// Term (months)
				String.valueOf(subscription.getTermsInMonths()),

				// Payment Period Amount (payment amount for the lowest tier)
				String.valueOf(CollectionUtilities.first(subscription.getSubscriptionPaymentTiers()).getPaymentAmount()),

				// VOR Period Amount (VoR payment amount for the lowest tier)
				String.valueOf(CollectionUtilities.first(subscription.getSubscriptionPaymentTiers()).getVendorOfRecordAmount()),

				// Setup Fee
				subscription.hasSetupFee()
					? String.valueOf(subscription.getSetUpFee())
					: null,

				// Total Discount
				subscription.hasDiscount()
					? String.valueOf(subscription.getTotalDiscount())
					: null,

				// Total Add-on (Must be calculated)
				String.valueOf(subscription.getTotalAddOnsFee()),

				// MRR (Monthly Recurring Revenue) (Must be calculated)
				String.valueOf(SubscriptionUtilities.calculateMonthlyRecurringRevenue(subscription)),

				// ARR (Annual Recurring Revenue) (Must be calculated)
				String.valueOf(SubscriptionUtilities.calculateAnnualRecurringRevenue(subscription))
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", subscription.getId(),
				"company_id", subscription.getCompany().getId()
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}


	/**
	 * Approve a subscription
	 *
	 * @param subscriptionIds
	 * @param flash
	 * @return
	 */
	@RequestMapping(
		value = "/approve",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_WM_SUBS_APPROVE')")
	public @ResponseBody AjaxResponseBuilder approveSubscription(@RequestParam(value = "subscription_id[]", required=false) Long[] subscriptionIds, RedirectAttributes flash) {
		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (ArrayUtils.isNotEmpty(subscriptionIds)) {
			int errors = subscriptionService.approveSubscriptionConfigurations(Arrays.asList(subscriptionIds));
			if (errors > 0) {
				if (errors == subscriptionIds.length) {
					messageHelper.addError(bundle, "admin.manage.subscriptions.error_approve");
					return new AjaxResponseBuilder()
						.setMessages(bundle.getErrors())
						.setSuccessful(false);
				} else {
					messageHelper.addError(bundle, "admin.manage.subscriptions.error_approve_partial");
					return new AjaxResponseBuilder()
						.setMessages(bundle.getErrors())
						.setSuccessful(false);
				}
			}
		}

		messageHelper.addSuccess(bundle, "admin.manage.subscriptions.success_approve");
		return new AjaxResponseBuilder()
			.setMessages(bundle.getSuccess())
			.setSuccessful(true);
	}


	/**
	 * Reject a subscription
	 *
	 * @param subscriptionIds
	 * @param flash
	 * @return
	 */
	@RequestMapping(
		value = "/reject",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_WM_SUBS_APPROVE')")
	public @ResponseBody AjaxResponseBuilder rejectSubscription(@RequestParam(value = "subscription_id[]") Long[] subscriptionIds, RedirectAttributes flash) {
		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (ArrayUtils.isNotEmpty(subscriptionIds)) {
			int errors = subscriptionService.rejectSubscriptionConfigurations(Arrays.asList(subscriptionIds));
			if (errors > 0) {
				if (errors == subscriptionIds.length) {
					messageHelper.addError(bundle, "admin.manage.subscriptions.error_reject");
					return new AjaxResponseBuilder()
						.setMessages(bundle.getErrors())
						.setSuccessful(false);
				} else {
					messageHelper.addError(bundle, "admin.manage.subscriptions.error_reject_partial");
					return new AjaxResponseBuilder()
						.setMessages(bundle.getErrors())
						.setSuccessful(false);
				}
			}
		}

		messageHelper.addSuccess(bundle, "admin.manage.subscriptions.success_reject");
		return new AjaxResponseBuilder()
			.setMessages(bundle.getSuccess())
			.setSuccessful(true);
	}


	@RequestMapping(
		value = "/reporting/standard",
		method = GET)
	@PreAuthorize("hasAnyRole('ROLE_WM_SUBS_REPORTS','ROLE_WM_SUBS_APPROVE')")
	public String subscriptionStandardView(Model model) {
		model.addAttribute("reportingView", "standard");
		SubscriptionReportPagination pagination = new SubscriptionReportPagination(true);
		pagination = subscriptionReportService.getStandardReport(pagination);
		model.addAttribute("generalData", pagination.getSubscriptionAggregate());

		return "web/pages/admin/manage/subscriptions/reporting/standard";
	}


	/**
	 * Returns a list of standard reporting view
	 *
	 */
	@RequestMapping(
		value = "/reporting/standard/list",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('ROLE_WM_SUBS_REPORTS','ROLE_WM_SUBS_APPROVE')")
	public void subscriptionStandardReporting(HttpServletRequest httpRequest, Model model) throws Exception{
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		SubscriptionReportPagination pagination = request.newPagination(SubscriptionReportPagination.class, true);
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());

		subscriptionReportService.getStandardReport(pagination);

		// Format the results
		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (SubscriptionReportRow data : pagination.getResults()) {
			//Company Id and Current Tier Range
			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"company_id",   data.getCompanyId(),
				"current_tier_lower_bound_throughput", data.getCurrentTierLowerBoundThroughput()
			);
			response.addRow(data.toStandardReportStringList(), meta);
		}

		model.addAttribute("response", response);
	}


	@RequestMapping(
		value = "/reporting/usage",
		method = GET)
	@PreAuthorize("hasAnyRole('ROLE_WM_SUBS_REPORTS','ROLE_WM_SUBS_APPROVE')")
	public String subscriptionUsageReporting(Model model) {
		model.addAttribute("reportingView", "usage");

		return "web/pages/admin/manage/subscriptions/reporting/usage";
	}

	/**
	 * Returns a list of usage reporting view
	 *
	 */
	@RequestMapping(
		value = "/reporting/usage/list",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('ROLE_WM_SUBS_REPORTS','ROLE_WM_SUBS_APPROVE')")
	public void subscriptionUsageReporting(HttpServletRequest httpRequest, Model model) throws Exception{
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		SubscriptionReportPagination pagination = request.newPagination(SubscriptionReportPagination.class, true);
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());

		subscriptionReportService.getUsageReport(pagination);

		// Format the results
		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (SubscriptionReportRow data : pagination.getResults()) {
			List<String> row = data.toUsageReportStringList();
			//Company Id
			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"company_id", data.getCompanyId()
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	/**
	 * Returns a list of usage reporting view
	 *
	 */
	@RequestMapping(
		value = "/reporting/usage/export",
		method = GET)
	@PreAuthorize("hasAnyRole('ROLE_WM_SUBS_REPORTS','ROLE_WM_SUBS_APPROVE')")
	public CSVView exportSubscriptionUsageReport(HttpServletRequest httpRequest, Model model) throws Exception{
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		SubscriptionReportPagination pagination = request.newPagination(SubscriptionReportPagination.class, true);
		subscriptionReportService.getUsageReport(pagination);

		List<String[]> data = Lists.newArrayListWithExpectedSize(pagination.getResults().size() + 2);
		data.add(new String[]{"Company Name", "Effective Date", "Existing Payable - Current",	"Existing Payable - Past Due","% On time Payment", "Current Annual Throughput",	"Tier Throughput Usage (%)", "Software Tier", "VOR Tier", "Next Software Tier", "Next VOR Tier"});

		for (SubscriptionReportRow subscriptionReportRow : pagination.getResults()) {
			List<String> row = subscriptionReportRow.toUsageReportStringList();
			data.add(row.toArray(new String[row.size()]));
		}
		model.addAttribute(CSVView.CSV_MODEL_KEY, data);

		String filename = String.format("subscriptionUsageReport_%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));

		return new CSVView(filename, EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
	}

}
