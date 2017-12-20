package com.workmarket.domains.payments.controllers;

import ch.lambdaj.function.convert.PropertyExtractor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.StatementPagination;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.domains.work.model.project.ProjectInvoiceBundle;
import com.workmarket.dto.FastFundInvoiceDTO;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.dto.invoice.InvoiceSummaryDTO;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.payment.InvoiceAlreadyPaidException;
import com.workmarket.service.infra.business.FastFundsService;
import com.workmarket.service.infra.business.wrapper.FastFundInvoiceResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.payments.InvoiceBundleAddForm;
import com.workmarket.web.forms.payments.InvoiceBundleForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.ValidationMessageHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.FastFundsValidator;
import com.workmarket.web.validators.UserEmailValidator;
import com.workmarket.web.views.CSVView;
import com.workmarket.web.views.HTML2PDFView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/payments/invoices")
@PreAuthorize("(hasAnyRole('ROLE_WM_ACCOUNTING')) OR ((!principal.userPaymentAccessBlocked) AND (principal.hasCustomAccessSettingsSet OR hasAnyRole('PERMISSION_ADDFUNDS', 'PERMISSION_WITHDRAW', 'PERMISSION_PAYABLES', 'PERMISSION_INVOICES',  'PERMISSION_PAYASSIGNMENT', 'PERMISSION_PAYINVOICE')))")
public class InvoicesController extends BaseController {

	private static final Log logger = LogFactory.getLog(InvoicesController.class);

	@Autowired private BillingService billingService;
	@Autowired private FastFundsService fastFundsService;
	@Autowired private CompanyService companyService;
	@Autowired private WorkReportService reportService;
	@Autowired private JsonSerializationService jsonService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private FormOptionsDataHelper formOptionsData;
	@Autowired private UserEmailValidator userEmailValidator;
	@Autowired private MessageSource messageSource;
	@Autowired private FastFundsValidator fastFundsValidator;
	@Autowired private JsonSerializationService jsonSerializationService;

	@RequestMapping(
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String index(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters) {

		return getCurrentUser().isBuyer() ? payables(model, filters) : receivables(model, filters);
	}

	/**
	 * Payables
	 * Pre-canned payable views for buyers.
	 */
	@RequestMapping(
		value = "/payables",
		method = GET)
	public String payables(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters) {

		filters.setPayables(true);

		return showInvoices(model, filters, "payables");
	}

	@RequestMapping(
		value = "/payables/due",
		method = GET)
	public String payablesDue(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters) {

		filters.setPaidStatus(false);
		filters.setDateFilterType(AccountStatementFilters.DATE_DUE);

		return payables(model, filters);
	}

	@RequestMapping(
		value = "/payables/past-due",
		method = GET)
	public String payablesPastDue(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters) {

		filters.setPaidStatus(false);
		filters.setDateFilterType(AccountStatementFilters.DATE_DUE);
		filters.setFromDate(null);
		filters.setToDate(DateUtilities.getCalendarNow());

		return payables(model, filters);
	}

	@RequestMapping(
		value = "/payables/upcoming-due",
		method = GET)
	public String payablesUpcomingDue(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters) {

		filters.setPaidStatus(false);
		filters.setDateFilterType(AccountStatementFilters.DATE_DUE);
		filters.setFromDate(DateUtilities.getCalendarNow());
		filters.setToDate(null);

		return payables(model, filters);
	}

	@RequestMapping(
		value = "/payables/paid-ytd",
		method = GET)
	public String payablesPaidYtd(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters) {

		filters.setPaidStatus(true);
		filters.setDateFilterType(AccountStatementFilters.DATE_PAID);
		filters.setFromDate(DateUtilities.getMidnightYTD());
		filters.setToDate(DateUtilities.getMidnightYTDNextYear());

		return payables(model, filters);
	}

	@RequestMapping(
		value = "/payables/statements",
		method = GET)
	public String payablesStatements(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters) {
		return payables(model, filters);
	}

	@RequestMapping(
		value = "/payables/statements/{statementId}",
		method = GET)
	public String payablesStatement(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters,
		@PathVariable("statementId") Long statementId) {

		filters.setStatementId(statementId);
		filters.setDateFilterType(AccountStatementFilters.DATE_DUE);

		return payables(model, filters);
	}

	/**
	 * Receivables
	 * Pre-canned receivable views for workers.
	 */
	@RequestMapping(
		value = "/receivables",
		method = GET)
	public String receivables(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters) {

		filters.setPayables(false);

		return showInvoices(model, filters, "receivables");
	}

	@RequestMapping(
		value = "/receivables/past-due",
		method = GET)
	public String receivablesPastDue(
		Model model, @ModelAttribute("filterForm") AccountStatementFilters filters) {

		filters.setPaidStatus(false);
		filters.setDateFilterType(AccountStatementFilters.DATE_DUE);
		filters.setFromDate(null);
		filters.setToDate(DateUtilities.getCalendarNow());

		return receivables(model, filters);
	}

	@RequestMapping(
		value = "/receivables/upcoming-due",
		method = GET)
	public String receivablesUpcomingDue(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters) {

		filters.setPaidStatus(false);
		filters.setDateFilterType(AccountStatementFilters.DATE_DUE);
		filters.setFromDate(DateUtilities.getCalendarNow());
		filters.setToDate(null);

		return receivables(model, filters);
	}

	@RequestMapping(
		value = "/receivables/paid-ytd",
		method = GET)
	public String receivablesPaidYtd(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters) {

		filters.setPaidStatus(true);
		filters.setDateFilterType(AccountStatementFilters.DATE_PAID);
		filters.setFromDate(DateUtilities.getMidnightYTD());
		filters.setToDate(DateUtilities.getMidnightYTDNextYear());

		return receivables(model, filters);
	}

	@RequestMapping(
		value = "/fast_funds/confirmation/{invoiceId}",
		produces = APPLICATION_JSON_VALUE,
		method = POST)
	public @ResponseBody AjaxResponseBuilder fastFundsConfirmation(@PathVariable("invoiceId") Long invoiceId, MessageBundle messageBundle) {
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		fastFundsValidator.validate(invoiceId, messageBundle);

		if (messageBundle.hasErrors()) {
			return response.setMessages(messageBundle.getAllMessages());
		}

		FastFundInvoiceResponse fastFundInvoiceResponse = fastFundsService.fastFundInvoice(invoiceId);

		if (fastFundInvoiceResponse.isSuccess()) {
			return AjaxResponseBuilder.success();
		}

		return response;
	}

	private String showInvoices(Model model, AccountStatementFilters filters, String currentView) {

		Map<Long, String> statementsOptions = Maps.newLinkedHashMap();
		Map<Long, Map<String, Object>> statementsDetails = Maps.newHashMap();

		StatementPagination pagination = new StatementPagination(true);
		pagination.setResultsLimit(10);	//overriding the global limit to retrieve only the latest 10 statements.
		pagination.setSortColumn(StatementPagination.SORTS.DUE_DATE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		pagination.addFilter(StatementPagination.FILTER_KEYS.COMPANY_ID, getCurrentUser().getCompanyId());
		pagination = billingService.findAllStatements(pagination);

		for (Statement s : pagination.getResults()) {
			String label = String.format("%s - %s (#%s)", DateUtilities.format("MM/dd/YYYY", s.getPeriodStartDate(), getCurrentUser().getTimeZoneId()),
				DateUtilities.format("MM/dd/YYYY", s.getPeriodEndDate(), getCurrentUser().getTimeZoneId()),
				s.getStatementNumber());
			statementsOptions.put(s.getId(), label);
			Map<String, Object> statementsAux = Maps.newHashMap();
			statementsAux.put("statementNumber", s.getStatementNumber());
			statementsAux.put("balance", s.getBalance());
			statementsAux.put("dueDate", s.getDueDate());
			statementsAux.put("isPaid", s.isPaid());
			statementsAux.put("remainingBalance", s.getRemainingBalance());
			statementsDetails.put(s.getId(), statementsAux);
		}

		boolean showCurrentInvoiceView = false;
		boolean showAllStatementsView = false;
		boolean showStatementsIdView = false;

		pagination = new StatementPagination(false);
		pagination.addFilter(StatementPagination.FILTER_KEYS.COMPANY_ID, getCurrentUser().getCompanyId());
		pagination.addFilter(StatementPagination.FILTER_KEYS.INVOICE_STATUS, InvoiceStatusType.PAYMENT_PENDING);
		// I only need 2 to decide
		pagination.setResultsLimit(2);
		pagination = billingService.findAllStatements(pagination);
		List<Statement> statements = pagination.getResults();

		if (statements.isEmpty()) {
			// If users have unpaid invoices in only current invoices, default to "current invoices" and filter to payment status = unpaid
			// "Current invoices" includes all invoices not yet in an statement and all other data in the table are displayed the same
			AccountStatementDetailPagination detailPagination = new AccountStatementDetailPagination(false);
			// I only need to know if there's at least one
			// Remember that there are no unpaid statements so anything returned will be statement-less
			detailPagination.setResultsLimit(1);

			Boolean paidStatus = filters.getPaidStatus();
			filters.setPaidStatus(false);

			detailPagination = billingService.getStatementDashboard(filters, detailPagination);
			if (!detailPagination.getResults().isEmpty()) {
				showCurrentInvoiceView = true;
			}
			//Restore previous filter value
			filters.setPaidStatus(paidStatus);
		} else if (statements.size() == 1) {
			// If users have unpaid invoices in only ONE statement, default to that statement and filter to payment status = unpaid
			showStatementsIdView = true;
		} else if (statements.size() > 1) {
			// If users have unpaid invoices in MULTIPLE statements, default to "All statements" and filter to payment status = unpaid
			// "All statements" includes all invoices which is the same view if you did not have statements turned on.
			showAllStatementsView = true;
		}

		model.addAttribute("show_current_invoice_view", showCurrentInvoiceView);
		model.addAttribute("show_all_statements_view", showAllStatementsView);
		model.addAttribute("show_statements_id_view", showStatementsIdView);
		model.addAttribute("invoice_paid_status", filters.getPaidStatus());
		model.addAttribute("statements", statementsOptions);
		model.addAttribute("statements_details", jsonService.toJson(statementsDetails));
		model.addAttribute("statements_configuration", billingService.findStatementPaymentConfigurationByCompany(getCurrentUser().getCompanyId()));
		model.addAttribute("users", formOptionsData.getActiveUsers(getCurrentUser()));
		model.addAttribute("clients", formOptionsData.getClients(getCurrentUser()));
		model.addAttribute("projects", formOptionsData.getProjects(getCurrentUser()));
		ManageMyWorkMarket mmw = companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId());
		model.addAttribute("mmw", mmw);
		model.addAttribute("currentView", currentView);
		model.addAttribute("payment_statuses", CollectionUtilities.newStringMap(
			"1", "Paid",
			"0", "Unpaid"
		));

		Map<String, String> invoiceType = CollectionUtilities.newStringMap(InvoiceSummary.INVOICE_SUMMARY_TYPE, "Bundle", Invoice.INVOICE_TYPE, "Invoice");
		model.addAttribute("invoice_type", invoiceType);

		if (!filters.hasDateFilterType()) {
			filters.setDateFilterType("due");
			filters.setFromDate(DateUtilities.subtractTime(DateUtilities.getCalendarNow(), 90, Constants.DAY));
			filters.setToDate(DateUtilities.getMidnightNextWeek());
		}

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap(
				"mmwAutoPayEnabled", mmw.getAutoPayEnabled(),
				"currentView", currentView,
				"invoicePaidStatus", filters.getPaidStatus(),
				"showStatementsIdView", filters.getStatementId() != null && showStatementsIdView,
				"showAllStatementsView", filters.getStatementId() != null && showAllStatementsView,
				"showCurrentInvoiceView", filters.getStatementId() != null && showCurrentInvoiceView
			)
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/payments/invoices/index";
	}

	@RequestMapping(
		value = "/list",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void indexList(
		Model model,
		@ModelAttribute("filterForm") AccountStatementFilters filters,
		@RequestParam(value = "start", defaultValue = "0") Integer start,
		@RequestParam(value = "limit", defaultValue = "10") Integer limit,
		@RequestParam(value = "sort", defaultValue = "payment_status") String sort,
		@RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) throws Exception {

		if (CollectionUtils.isEmpty(filters.getAssignedResourceId())) {
			filters.setAssignedResourceId(null);
		}
		AccountStatementDetailPagination pagination = new AccountStatementDetailPagination();
		pagination.setStartRow(start);
		pagination.setResultsLimit(limit);
		switch (sort) {
			case "payment_status":
				pagination.addSort(AccountStatementDetailPagination.SORTS.INVOICE_STATUS.name(), Pagination.SORT_DIRECTION.DESC);
				pagination.addSort(AccountStatementDetailPagination.SORTS.INVOICE_DUE_DATE.name(), Pagination.SORT_DIRECTION.valueOf(sortDirection.toUpperCase()));
				break;
			case "amount":
				pagination.addSort(AccountStatementDetailPagination.SORTS.INVOICE_AMOUNT.name(), Pagination.SORT_DIRECTION.valueOf(sortDirection.toUpperCase()));
				break;
			default:
				pagination.addSort(AccountStatementDetailPagination.SORTS.INVOICE_NUMBER.name(), Pagination.SORT_DIRECTION.valueOf(sortDirection.toUpperCase()));
				break;
		}
		//Include ad-hoc invoices under the invoice filter. The user doesn't know the difference.
		if (StringUtils.isNotBlank(filters.getInvoiceType()) && filters.getInvoiceType().equals(Invoice.INVOICE_TYPE)) {
			filters.setInvoiceTypes(Lists.newArrayList(Invoice.INVOICE_TYPE, AdHocInvoice.AD_HOC_INVOICE_TYPE, SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE));
		}

		filters.setUserTimezone(getCurrentUser().getTimeZoneId());
		filters.setDateFiltersAccordingToType();
		filters.setDateFiltersInclusive();
		pagination = billingService.getStatementDashboard(filters, pagination);

		List<AccountStatementDetailRow> accountStatementDetailRows = pagination.getResults();
		List<AccountStatementDetailRow> rowsToRemove = Lists.newArrayList();
		HashMap<Long, FastFundInvoiceDTO> fastFundsMap = Maps.newHashMap();

		if (getCurrentUser().isSeller() || getCurrentUser().isDispatcher()) {
			for (AccountStatementDetailRow accountStatementDetailRow : accountStatementDetailRows) {
				Invoice invoice = null;
				try {
					invoice = billingService.findInvoiceById(accountStatementDetailRow.getInvoiceId());
				}
				catch (Exception e) {
					rowsToRemove.add(accountStatementDetailRow);
					continue;
				}
				Calendar fastFundedOn = invoice.getFastFundedOn();
				FastFundInvoiceDTO fastFundInvoiceDTO = new FastFundInvoiceDTO();
				if (fastFundedOn != null) {
					fastFundInvoiceDTO.setFastFundedOn(fastFundedOn);
					fastFundsMap.put(accountStatementDetailRow.getInvoiceId(), fastFundInvoiceDTO);
				} else if (fastFundsValidator.isInvoiceFastFundable(accountStatementDetailRow.getInvoiceId())) {
					fastFundInvoiceDTO.setFastFundsFee(billingService.calculateFastFundsFeeCost(accountStatementDetailRow.getAmountEarned(), accountStatementDetailRow.getCompanyId()));
					fastFundsMap.put(accountStatementDetailRow.getInvoiceId(), fastFundInvoiceDTO);
				}
			}
			if (!rowsToRemove.isEmpty()) {
				accountStatementDetailRows.removeAll(rowsToRemove);
			}
		}

		model.addAttribute("response", CollectionUtilities.newObjectMap(
			"total_results", pagination.getRowCount(),
			"count", pagination.getResults().size(),
			"start", start,
			"limit", limit,
			"data", pagination.getResults(),
			"fast_funds_map", fastFundsMap
		));
	}

	@RequestMapping(
		value = "/load_bundle/{invoiceId}",
		method = GET)
	public void loadBundle(
		Model model,
		@PathVariable("invoiceId") Long invoiceId) {
		model.addAttribute("response", CollectionUtilities.newObjectMap(
			"data", billingService.findAccountStatementDetailByInvoiceId(invoiceId)
		));
	}

	@RequestMapping(
		value = "/pay",
		method = {GET, POST})
	@PreAuthorize("!principal.isMasquerading()")
	public String pay(
		Model model,
		@RequestParam(value = "ids[]", required = false) Long[] invoiceIds) {

		if (ArrayUtils.isEmpty(invoiceIds)) {
			MessageBundle messages = messageHelper.newBundle(model);
			BindingResult bindingResult = ValidationMessageHelper.newBindingResult("invoices");
			bindingResult.rejectValue("invoices", "NotNull", CollectionUtilities.newArray("invoices"), "No invoices selected.");
			messageHelper.setErrors(messages, bindingResult);
			return "web/pages/payments/invoices/pay";
		}

		List<? extends AbstractInvoice> invoices = billingService.findInvoicesById(Lists.newArrayList(invoiceIds));

		BigDecimal totalDue = BigDecimal.ZERO;
		for (AbstractInvoice i : invoices) {
			totalDue = totalDue.add(i.getRemainingBalance());
		}

		// Group invoices by project
		if (companyService.doesCompanyHaveReservedFundsEnabledProject(getCurrentUser().getCompanyId())) {
			List<ProjectInvoiceBundle> projectInvoiceBundleList = billingService.groupInvoicesByProject(Lists.newArrayList(invoiceIds), getCurrentUser().getCompanyId());
			List<? extends AbstractInvoice> generalInvoices = billingService.findInvoicesWithoutProjectBudget(Lists.newArrayList(invoiceIds), getCurrentUser().getCompanyId());
			BigDecimal generalTotalDue = billingService.findGeneralTotalDue(Lists.newArrayList(invoiceIds), getCurrentUser().getCompanyId());
			model.addAttribute("general_cash", getGeneralCash());
			model.addAttribute("project_list", projectInvoiceBundleList);
			model.addAttribute("general_invoices", generalInvoices);
			model.addAttribute("general_totalDue", generalTotalDue);
		}

		model.addAttribute("isBundled", (invoices.size() == 1 && invoices.get(0).getType().equals(InvoiceSummary.INVOICE_SUMMARY_TYPE)));
		model.addAttribute("doesCompanyHaveReservedFundsEnabledProject", String.valueOf(companyService.doesCompanyHaveReservedFundsEnabledProject(getCurrentUser().getCompanyId())));
		model.addAttribute("invoices", invoices);
		model.addAttribute("invoiceIds", invoiceIds);
		model.addAttribute("totalDue", totalDue);

		return "web/pages/payments/invoices/pay";
	}

	@RequestMapping(
		value = "/pay-confirm",
		method = POST)
	@ResponseBody public AjaxResponseBuilder doPay(
		MessageBundle messages,
		@RequestParam("ids[]") Long[] invoiceIds) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (companyService.doesCompanyHaveReservedFundsEnabledProject(getCurrentUser().getCompanyId())) {
			List<ProjectInvoiceBundle> projectInvoiceBundleList = billingService.groupInvoicesByProject(Lists.newArrayList(invoiceIds), getCurrentUser().getCompanyId());
			BigDecimal generalTotalDue = billingService.findGeneralTotalDue(Lists.newArrayList(invoiceIds), getCurrentUser().getCompanyId());

			for (ProjectInvoiceBundle p :projectInvoiceBundleList) {
				if (p.getProject().getReservedFunds().compareTo(p.getSumOfInvoices()) == -1) {
					messageHelper.addError(messages, "You do not have enough cash in " + p.getProject().getName() + " to pay all the invoices under that project.");
					return response.setMessages(messages.getErrors());
				}
			}

			if (getGeneralCash().compareTo(generalTotalDue) == -1) {
				messageHelper.addError(messages, "You do not have enough unreserved cash to pay all the invoices.");
				return response.setMessages(messages.getErrors());
			}
		}

		Map<String, List<ConstraintViolation>> violations;
		try {
			violations = (invoiceIds.length > 1) ?
				billingService.payInvoices(getCurrentUser().getId(), Lists.newArrayList(invoiceIds)) :
				billingService.payInvoice(getCurrentUser().getId(), invoiceIds[0]);
		} catch (InsufficientFundsException e) {
			messageHelper.addError(messages, "payments.invoices.insufficient_funds");
			return response.setMessages(messages.getErrors());
		} catch (InvoiceAlreadyPaidException e) {
			messageHelper.addError(messages, "payments.invoices.already_paid");
			return response.setMessages(messages.getErrors());
		} catch (Exception e) {
			logger.error(e.getMessage());
			messageHelper.addError(messages, "payments.invoices.pay.error");
			return response.setMessages(messages.getErrors());
		}

		if (violations.isEmpty()) {
			messageHelper.addSuccess(messages, "payments.invoices.paid", invoiceIds.length, StringUtilities.pluralize("invoice", invoiceIds.length));
			return response.setSuccessful(true).setMessages(messages.getSuccess());
		}

		for (Map.Entry<String, List<ConstraintViolation>> entry : violations.entrySet()) {
			MessageBundle invoiceViolations = messageHelper.newBundle();
			for (ConstraintViolation v : entry.getValue()) {
				messageHelper.addError(invoiceViolations, v.getKey());
			}

			messageHelper.addError(messages, "payments.invoices.violations", entry.getKey(), CollectionUtilities.joinHuman(invoiceViolations.getErrors(), ", ", "and"));
		}

		return response.setMessages(messages.getErrors());
	}

	@RequestMapping(
		value = "/add_to_bundle",
		method = {GET, POST})
	public String getBundle(
		Model model,
		@ModelAttribute("InvoiceBundleAddForm") InvoiceBundleAddForm form,
		MessageBundle messages,
		@RequestParam(value = "bundleIds[]", required = false) String[] bundleIds,
		@RequestParam(value = "bundleNames[]", required = false) String[] bundleNames) {

		if (ArrayUtils.isEmpty(bundleIds) || ArrayUtils.isEmpty(bundleNames)) {
			messageHelper.addError(messages, "payments.invoices.bundle.no_bundle");
			return "redirect:/payments/invoices";
		}

		List<Map<String,String>> bundles = Lists.newArrayList();
		for (int i = 0; i < bundleIds.length; i++) {
			Map<String,String> hashMap = Maps.newHashMap();
			hashMap.put("bundleIds", Lists.newArrayList(bundleIds).get(i));
			hashMap.put("bundleNames", Lists.newArrayList(bundleNames).get(i));
			bundles.add(hashMap);
		}
		model.addAttribute("bundle", bundles);
		return "web/pages/payments/invoices/add_to_bundles";
	}

	@RequestMapping(
		value = "/add_to_bundle_save",
		method = POST)
	@ResponseBody public AjaxResponseBuilder doAddToBundleSave(
		MessageBundle messages,
		@Valid @ModelAttribute("InvoiceBundleAddForm") InvoiceBundleAddForm form,
		BindingResult bindingResult) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(response, bindingResult);
			return response;
		}

		List<Long> ids = Lists.newArrayList(form.getInvoice_ids());
		List<? extends AbstractInvoice> invoices = billingService.findInvoicesById(ids);
		for (AbstractInvoice invoice: invoices) {
			if (!invoice.getType().equals(Invoice.INVOICE_TYPE)) {
				ids.remove(invoice.getId());
			}
		}

		if (CollectionUtils.isEmpty(ids)) {
			messageHelper.addError(messages, "payments.invoices.bundle.invalid_type");
			return response.setMessages(messages.getErrors());
		}

		InvoiceSummaryDTO dto = new InvoiceSummaryDTO();
		Long invoiceSummaryId = Long.parseLong(form.getBundle_id());
		dto.setInvoiceSummaryId(invoiceSummaryId);
		dto.setInvoicesIds(ids);

		billingService.saveInvoiceSummary(dto);

		if (ids.size() < form.getInvoice_ids().length) {
			messageHelper.addSuccess(messages, "payments.invoices.bundle.partial_success", ids.size());
		}

		messageHelper.addSuccess(messages, "payments.invoices.bundle.add_success");
		return response.setSuccessful(true).setMessages(messages.getSuccess());
	}

	@RequestMapping(
		value = "/bundle",
		method = GET)
	public String bundle(
		@ModelAttribute("invoiceBundleForm") InvoiceBundleForm form) {

		return "web/pages/payments/invoices/bundle";
	}

	@RequestMapping(
		value = "/bundle",
		method = POST)
	@ResponseBody public AjaxResponseBuilder doBundle(
		MessageBundle messages,
		@Valid @ModelAttribute("invoiceBundleForm") InvoiceBundleForm form,
		BindingResult bindingResult) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(response, bindingResult);
			return response;
		}

		List<Long> ids = Lists.newArrayList(form.getInvoice_ids());
		List<? extends AbstractInvoice> invoices = billingService.findInvoicesById(ids);

		for (AbstractInvoice invoice: invoices) {
			if (!invoice.getType().equals(Invoice.INVOICE_TYPE)) {
				ids.remove(invoice.getId());
			}
		}

		if (CollectionUtils.isEmpty(ids)) {
			messageHelper.addError(messages, "payments.invoices.bundle.invalid_type");
			return response.setMessages(messages.getErrors());
		}

		InvoiceSummaryDTO dto = new InvoiceSummaryDTO();
		dto.setDescription(form.getDescription());
		dto.setInvoicesIds(ids);

		billingService.saveInvoiceSummary(dto);
		if (ids.size() < form.getInvoice_ids().length) {
			messageHelper.addSuccess(messages, "payments.invoices.bundle.partial_success", ids.size());
			return response.setSuccessful(true).setMessages(messages.getSuccess());
		}

		messageHelper.addSuccess(messages, "payments.invoices.bundle.success", dto.getDescription());
		return response.setSuccessful(true).setMessages(messages.getSuccess());
	}

	@RequestMapping(
		value = "/remove_from_bundle/{invoiceId}/{bundleId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder doRemoveFromBundle(
		@PathVariable("invoiceId") Long invoiceId,
		@PathVariable("bundleId") Long bundleId,
		MessageBundle messages) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		billingService.removeInvoiceFromInvoiceSummary(bundleId, invoiceId);
		messageHelper.addSuccess(messages, "payments.invoices.unbundle.success");

		return response.setSuccessful(true).setMessages(messages.getSuccess());
	}

	/**
	 * Unlocks an invoice by setting downloaded on, sent on, and sent to to null
	 */
	@RequestMapping(
		value = "/unlock_invoice/{invoiceId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map unlockInvoice(
		@PathVariable Long invoiceId,
		HttpServletResponse response) {

		String message;

		try {
			billingService.unlockInvoice(invoiceId);
			message = messageSource.getMessage("payments.invoices.unlocked.success", null, null);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			message = messageSource.getMessage("payments.invoices.unlocked.exception", null, null);
			logger.error(String.format("Failed to unlock invoice for invoice id: %s", invoiceId), e);
		}

		return CollectionUtilities.newObjectMap(
			"message", message
		);
	}

	/**
	 * Generates a printable PDF for either a specific invoice or a
	 * filtered collection of invoices.
	 */
	@RequestMapping(
		value = "/print",
		method = GET)
	public View print(Model model, AccountStatementFilters filters) {
		filters.setUserTimezone(getCurrentUser().getTimeZoneId());
		filters.setDateFiltersAccordingToType();
		filters.setDateFiltersInclusive();

		AccountStatementDetailPagination pagination = new AccountStatementDetailPagination(true);
		pagination.addSort(AccountStatementDetailPagination.SORTS.INVOICE_CREATED_ON.name(), Pagination.SORT_DIRECTION.ASC);
		pagination = billingService.getStatementDashboardWithBundledInvoices(filters, pagination);

		List<AccountStatementDetailRow> invoices = pagination.getResults();
		model.addAttribute("isReceivables", BooleanUtils.isFalse(filters.getPayables()));

		return getInvoicesPDFView(model, invoices);
	}

	@RequestMapping(
		value = "/print/{invoiceId}",
		method = GET)
	public ModelAndView printSingle(
		@PathVariable("invoiceId") Long invoiceId,
		Model model,
		MessageBundle bundle) {

		model.addAttribute("company", companyService.findCompanyById(getCurrentUser().getCompanyId()));
		model.addAttribute("bundle", bundle);
		if (!billingService.validateAccessToInvoice(invoiceId)) {
			messageHelper.addError(bundle, "payments.invoices.print.not_found");
			return new ModelAndView("forward:/payments/invoices", model.asMap());
		}

		AccountStatementDetailRow invoice = billingService.findAccountStatementDetailByInvoiceId(invoiceId);
		return new ModelAndView(getInvoicesPDFView(model, Lists.newArrayList(invoice)));
	}

	@RequestMapping(
		value = "/print_service_invoice/{invoiceId}",
		method = GET)
	public ModelAndView printSubscrptionInvoice(
		@PathVariable("invoiceId") Long invoiceId,
		Model model,
		MessageBundle bundle) {

		model.addAttribute("bundle", bundle);
		if (!billingService.validateAccessToInvoice(invoiceId)) {
			messageHelper.addError(bundle, "payments.invoices.print.not_found");
			return new ModelAndView("forward:/payments/invoices", model.asMap());
		}

		HTML2PDFView view = new HTML2PDFView(StringUtils.EMPTY);
		view.setHtml(billingService.getServiceInvoicePdfView(invoiceId));
		return new ModelAndView(view);
	}

	/**
	 * Emails a printable PDF of a single invoice
	 */
	@RequestMapping(
		value = "/email/{invoiceId}",
		method = RequestMethod.GET)
	public String email(
		Model model,
		@PathVariable("invoiceId") Long invoiceId) {

		Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
		model.addAttribute("defaultEmail", company.getInvoiceSentToEmail());

		return "web/pages/payments/invoices/email";
	}

	@RequestMapping(
		value = "/email/{invoiceId}",
		method = POST)
	@ResponseBody public AjaxResponseBuilder doEmail(
		@PathVariable("invoiceId") Long invoiceId,
		@RequestParam("email") String email,
		MessageBundle messages,
		Model model,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest httpRequest) throws Exception {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		AbstractInvoice abstractInvoice = billingService.findInvoiceById(invoiceId);
		userEmailValidator.validate(email, bindingResult);
		if (!bindingResult.hasErrors()) {
			if (abstractInvoice.getType().equals(AdHocInvoice.AD_HOC_INVOICE_TYPE)) {
				billingService.emailInvoiceToUser(email, invoiceId);
			} else {
				AccountStatementDetailRow invoice = billingService.findAccountStatementDetailByInvoiceId(invoiceId);
				HTML2PDFView view = (HTML2PDFView) getInvoicesPDFView(model, Lists.newArrayList(invoice));
				String filename = view.renderToFile(model.asMap(), httpRequest);
				billingService.emailInvoiceToUser(email, invoiceId, filename, MimeType.PDF.getMimeType());
			}
			messageHelper.addSuccess(messages, "payments.invoices.email.success");
		} else {
			messageHelper.addError(messages, "payments.invoices.email.invalid");
			return response.setMessages(messages.getErrors());
		}

		redirectAttributes.addFlashAttribute("bundle", messages);
		return response.setSuccessful(true).setMessages(messages.getSuccess());
	}

	private View getInvoicesPDFView(Model model, List<AccountStatementDetailRow> invoices) {
		Calendar dueOn = null;
		BigDecimal totalDue = BigDecimal.ZERO;
		BigDecimal remainingBalanceDue = BigDecimal.ZERO;

		List<AccountStatementDetailRow> printableInvoices = Lists.newArrayList(invoices);
		Set<Long> statementIds = Sets.newHashSet(convert(printableInvoices, new PropertyExtractor("invoiceSummaryId")));

		int numStatements = CollectionUtils.isNotEmpty(statementIds) ? statementIds.size(): 0;

		List<Long> invoiceIds = new ArrayList<>();

		for (AccountStatementDetailRow row : invoices) {
			totalDue = totalDue.add(row.getInvoiceBalance());
			remainingBalanceDue = remainingBalanceDue.add(row.getInvoiceRemainingBalance());
			dueOn = DateUtilities.min(row.getInvoiceDueDate(), dueOn);
			invoiceIds.add(row.getInvoiceId());
		}

		// set lastDownloadedDate for selected invoices
		billingService.updateInvoiceLastDownloadDate(invoiceIds);

		boolean isBundle = (CollectionUtils.isNotEmpty(printableInvoices)  && printableInvoices.get(0).isBundle());

		model.addAttribute("invoices", printableInvoices);
		model.addAttribute("dueOn", dueOn);
		model.addAttribute("totalDue", totalDue);
		model.addAttribute("remainingBalanceDue", remainingBalanceDue);
		model.addAttribute("isBundle", isBundle);
		model.addAttribute("numStatements", numStatements);
		return new HTML2PDFView("pdf/payments/invoices");
	}

	/**
	 * Generates an exported CSV file for either a specific invoice or a
	 * filtered collection of invoices.
	 */
	@RequestMapping(
		value = "/export",
		method = GET)
	public CSVView export(Model model, AccountStatementFilters filters) {
		filters.setUserTimezone(getCurrentUser().getTimeZoneId());
		filters.setDateFiltersAccordingToType();
		filters.setDateFiltersInclusive();

		AccountStatementDetailPagination pagination = new AccountStatementDetailPagination(true);
		pagination.addSort(AccountStatementDetailPagination.SORTS.INVOICE_CREATED_ON.name(), Pagination.SORT_DIRECTION.ASC);
		pagination.setReturnAllRows();
		pagination = billingService.getStatementDashboardWithBundledInvoices(filters, pagination);

		return generateExport(model, pagination.getResults());
	}

	@RequestMapping(
		value = "/export/{invoiceId}",
		method = GET)
	public CSVView exportSingle(
		@PathVariable("invoiceId") Long invoiceId,
		Model model) {

		AccountStatementDetailRow invoice = billingService.findAccountStatementDetailByInvoiceId(invoiceId);
		return generateExport(model, Lists.newArrayList(invoice));
	}

	private CSVView generateExport(Model model, List<AccountStatementDetailRow> invoices) {
		// Flatten the list of bundled invoices
		List<AccountStatementDetailRow> flattenedInvoices = Lists.newArrayList();
		for (AccountStatementDetailRow r : invoices) {
			if (r.getInvoiceType().equals(InvoiceSummary.INVOICE_TYPE)) {
				flattenedInvoices.add(r);
			} else {
				flattenedInvoices.addAll(r.getBundledInvoices());
			}
		}

		// set lastDownloadedDate for selected invoices
		List<Long> invoiceIds = CollectionUtilities.newListPropertyProjection(flattenedInvoices, "invoiceId");
		billingService.updateInvoiceLastDownloadDate(invoiceIds);

		CustomFieldReportFilters filters = new CustomFieldReportFilters();
		List<Long> workIds = CollectionUtilities.newListPropertyProjection(flattenedInvoices, "workId");
		filters.setWorkIds(workIds);
		filters.setVisibleToBuyer(true);
		filters.setShowOnInvoice(true);

		List<CustomFieldReportRow> customFields = reportService.findAllWorkCustomFields(getCurrentUser().getId(), filters);

		List<String> columns = Lists.newArrayList(
			"Invoice Number",
			"Assignment Title",
			"Assignment ID",
			"Worker Name",
			"Company Name",
			"Balance",
			"Approved Date",
			"Due Date",
			"Payment Date",
			"Payment Status"
		);

		columns.addAll(CollectionUtilities.newListPropertyProjection(customFields, "fieldName"));

		List<String[]> rows = Lists.newArrayList();
		rows.add(columns.toArray(new String[columns.size()]));

		for (AccountStatementDetailRow row : flattenedInvoices) {
			List<String> data = Lists.newArrayList(
				row.getInvoiceNumber(),
				row.getWorkTitle(),
				row.getWorkNumber(),
				row.getWorkResourceName() == null || row.getWorkResourceName().isEmpty() ? null : row.getWorkResourceName(),
				row.getWorkResourceCompanyName() == null ? null : row.getWorkResourceCompanyName().equals("Sole Proprietor") ? null : row.getWorkResourceCompanyName(),
				NumberUtilities.currency(row.isOwner() ? row.getInvoiceBalance() : row.getAmountEarned()),
				row.getInvoiceCreatedDate() == null ? null : DateUtilities.format("MM/dd/yyyy", row.getInvoiceCreatedDate(), getCurrentUser().getTimeZoneId()),
				row.getInvoiceEarliestDueDate() == null ? null : DateUtilities.format("MM/dd/yyyy", row.getInvoiceEarliestDueDate(), getCurrentUser().getTimeZoneId()),
				row.getInvoicePaymentDate() == null ? null : DateUtilities.format("MM/dd/yyyy", row.getInvoicePaymentDate(), getCurrentUser().getTimeZoneId()),
				StringUtilities.humanize(row.getInvoiceStatusTypeCode())
			);

			Map<Long, CustomFieldReportRow> customFieldsLookup = CollectionUtilities.newEntityIdMap(row.getCustomFields(), "fieldId");
			for (CustomFieldReportRow f : customFields) {
				String value = (customFieldsLookup.containsKey(f.getFieldId())) ?
					customFieldsLookup.get(f.getFieldId()).getFieldValue() : StringUtils.EMPTY;
				data.add(value);
			}

			rows.add(data.toArray(new String[data.size()]));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format("invoices-export-%s.csv", DateUtilities.getISO8601(DateUtilities.getCalendarNow())));
		return view;
	}
}
