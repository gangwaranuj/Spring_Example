package com.workmarket.domains.payments.controllers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.common.template.pdf.CreditCardReceiptPDFTemplate;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionActivity;
import com.workmarket.domains.model.account.RegisterTransactionActivityPagination;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.summary.SummaryService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.views.CSVView;
import com.workmarket.web.views.HTML2PDFView;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@PreAuthorize("(!principal.userPaymentAccessBlocked) AND (principal.hasCustomAccessSettingsSet OR hasAnyRole('PERMISSION_ADDFUNDS', 'PERMISSION_WITHDRAW', 'PERMISSION_PAYABLES', 'PERMISSION_INVOICES',  'PERMISSION_PAYASSIGNMENT', 'PERMISSION_PAYINVOICE'))")
public class PaymentsController extends BaseController {

	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterService;
	@Autowired private SummaryService summaryService;
	@Autowired private CompanyService companyService;
	@Autowired private BankingService bankingService;
	@Autowired private BillingService billingService;
	@Autowired private JsonSerializationService jsonSerializationService;

	// GCC banner
	private boolean showCreateGccAccountBanner () {
		boolean hasGccAccount = bankingService.hasGCCAccount(getCurrentUser().getId());
		return (!hasGccAccount && Boolean.TRUE.equals(getCurrentUser().isSeller()));
	}

	@RequestMapping(
		value = "/payments",
		method = GET)
	public String index(Model model) {
		model.addAttribute("showGccBanner", showCreateGccAccountBanner());

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap(
				"isBuyer", getCurrentUser().isBuyer()
			)
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/payments/index";
	}

	@RequestMapping(
		value = "/payments/ledger",
		method = GET)
	public String ledger(Model model) {
		model.addAttribute("showGccBanner", showCreateGccAccountBanner());

		model.addAttribute("currentView", "ledger");
		model.addAttribute("defaultFromDate", DateUtilities.getMidnightMonthAgo());
		model.addAttribute("defaultToDate", DateUtilities.getMidnightTodayRelativeToTimezone(getCurrentUser().getTimeZoneId()));
		model.addAttribute("email", getCurrentUser().getEmail());

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/payments/ledger";
	}

	@RequestMapping(value = "/payments/ledger.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public Map<String,Object> loadLedger(HttpServletRequest httpRequest) throws Exception {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		RegisterTransactionActivityPagination pagination = getLedgerPagination(httpRequest, request);
		pagination = accountRegisterService.getLedgerForCompany(getCurrentUser().getCompanyId(), pagination);

		DataTablesResponse response = getLedgerDataTablesResponse(request, pagination, false);

		return CollectionUtilities.newObjectMap(
			"response", response
		);
	}

	@RequestMapping(
		value = "/payments/offline_ledger",
		method = GET)
	public String offlineLedger(Model model) {
		model.addAttribute("showGccBanner", showCreateGccAccountBanner());

		model.addAttribute("currentView", "offline_ledger");
		model.addAttribute("defaultFromDate", DateUtilities.getMidnightMonthAgo());
		model.addAttribute("defaultToDate", DateUtilities.getMidnightTodayRelativeToTimezone(getCurrentUser().getTimeZoneId()));
		model.addAttribute("email", getCurrentUser().getEmail());

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "payments",
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/payments/offlineLedger";
	}

	@RequestMapping(value = "/payments/offline_ledger.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public Map<String,Object> loadOfflineLedger(HttpServletRequest httpRequest) throws Exception {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		RegisterTransactionActivityPagination pagination = getLedgerPagination(httpRequest, request);
		pagination = accountRegisterService.getOfflineLedgerForCompany(getCurrentUser().getCompanyId(), pagination);

		DataTablesResponse response = getLedgerDataTablesResponse(request, pagination, true);
		return CollectionUtilities.newObjectMap(
			"response", response
		);
	}

	private RegisterTransactionActivityPagination getLedgerPagination(HttpServletRequest httpRequest, DataTablesRequest request) throws IllegalAccessException, InstantiationException {
		RegisterTransactionActivityPagination pagination = request.newPagination(RegisterTransactionActivityPagination.class);
		pagination.setSortColumn(RegisterTransactionActivityPagination.SORTS.TRANSACTION_DATE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		applyFilter(httpRequest, pagination);
		return pagination;
	}

	private DataTablesResponse getLedgerDataTablesResponse(DataTablesRequest request,
		RegisterTransactionActivityPagination pagination, boolean offlineLedger) {
		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (RegisterTransactionActivity activity : pagination.getResults()) {
			List<String> rows = Lists.newArrayList(
				activity.getFormattedTransactionDateWithTimeZone(getCurrentUser().getTimeZoneId()),
				activity.getFormattedTypeDescription(),
				activity.getFormattedDescription(),
				activity.getFormattedWithdrawal(),
				activity.getFormattedDeposit()
			);
			if(!offlineLedger) {
				rows.add(activity.getFormattedBalance());
			}

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"work_number", activity.getWorkNumber(),
				"invoice_id", activity.getInvoiceId(),
				"invoice_number", activity.getInvoiceNumber(),
				"bundle_id", activity.isBundle() ? activity.getPaidInvoiceSummaryId() : null,
				"bundle_number", activity.isBundle() ? activity.getPaidInvoiceSummaryNumber() : null,
				"statement_id", activity.isStatement() ? activity.getPaidInvoiceSummaryId() : null,
				"statement_number", activity.isStatement() ? activity.getPaidInvoiceSummaryNumber() : null,
				"is_invoice", activity.isWorkInvoice(),
				"is_bundle", activity.isBundle(),
				"is_statement", activity.isStatement(),
				"is_service_invoice", activity.isServiceInvoice(),
				"service_invoice_id", activity.isServiceInvoice() || activity.isCreditMemo() ? activity.getPaidInvoiceSummaryId() : null,
				"service_invoice_number", activity.isServiceInvoice() || activity.isCreditMemo() ? activity.getPaidInvoiceSummaryNumber() : null,
				"is_owner", activity.isOwner(),
				"resource_name", StringUtilities.fullName(activity.getWorkResourceFirstName(), activity.getWorkResourceLastName()),
				"resource_number", activity.getWorkResourceUserNumber(),
				"company_name", activity.getOwnerCompanyName(),
				"client_name", activity.getClientName(),
				"id", activity.getRegisterTransactionId(),
				"deposit_payment_type",  activity.getDepositPaymentType(),
				"type", activity.getDisplayTypeCode(),
				"is_pending", false,
				"is_work_market_initiated_transaction", RegisterTransactionType.WORK_MARKET_INITIATED_TRANSACTIONS.contains(activity.getRegisterTransactionTypeCode()),
				"is_credit_memo", activity.isCreditMemo()
			);

			response.addRow(rows, meta);
		}
		return response;
	}

	@RequestMapping(
		value="/payments/ledger_pending",
		method = RequestMethod.GET,
		produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String,Object> loadLedgerPending(HttpServletRequest httpRequest) throws Exception {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		RegisterTransactionActivityPagination pagination = request.newPagination(RegisterTransactionActivityPagination.class);
		pagination.setSortColumn(RegisterTransactionActivityPagination.SORTS.TRANSACTION_DATE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		pagination.setReturnAllRows();
		pagination = accountRegisterService.getPendingTransactions(getCurrentUser().getCompanyId(), pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (RegisterTransactionActivity activity : pagination.getResults()) {
			List<String> rows = Lists.newArrayList(
				activity.getFormattedTransactionDateWithTimeZone(getCurrentUser().getTimeZoneId()),
				activity.getFormattedTypeDescription(),
				activity.getFormattedDescription(),
				activity.getFormattedWithdrawal(),
				activity.getFormattedDeposit(),
				activity.getFormattedBalance()
			);

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"work_number", activity.getWorkNumber(),
				"invoice_id", activity.getInvoiceId(),
				"invoice_number", activity.getInvoiceNumber(),
				"bundle_id", activity.isBundle() ? activity.getPaidInvoiceSummaryId() : null,
				"bundle_number", activity.isBundle() ? activity.getPaidInvoiceSummaryNumber() : null,
				"statement_id", activity.isStatement() ? activity.getPaidInvoiceSummaryId() : null,
				"statement_number", activity.isStatement() ? activity.getPaidInvoiceSummaryNumber() : null,
				"is_invoice", activity.isWorkInvoice(),
				"is_bundle", activity.isBundle(),
				"is_statement", activity.isStatement(),
				"is_owner", activity.isOwner(),
				"is_prefund_assignment_authorization", activity.isPreFundAssignmentAuthorization(),
				"resource_name", StringUtilities.fullName(activity.getWorkResourceFirstName(), activity.getWorkResourceLastName()),
				"resource_number", activity.getWorkResourceUserNumber(),
				"company_name", activity.getOwnerCompanyName(),
				"client_name", activity.getClientName(),
				"id", activity.getRegisterTransactionId(),
				"type", activity.getDisplayTypeCode(),
				"is_pending", true
			);

			response.addRow(rows, meta);
		}

		return CollectionUtilities.newObjectMap(
			"response", response
		);
	}

	@RequestMapping(
		value = "/payments/ledger.csv",
		method = GET)
	public View paymentsCSV(HttpServletRequest httpRequest, Model model) throws Exception {

		model.addAttribute(CSVView.CSV_MODEL_KEY, createActivityExport(httpRequest));

		CSVView view = new CSVView();
		view.setFilename(String.format("payments-export-%s.csv", DateUtilities.getISO8601(DateUtilities.getCalendarNow())));
		return view;
	}

	@RequestMapping(
		value = "/payments/offline_ledger.csv",
		method = GET)
	public View offlinePaymentsCSV(HttpServletRequest httpRequest, Model model) throws Exception {

		model.addAttribute(CSVView.CSV_MODEL_KEY, createOfflineActivityExport(httpRequest));

		CSVView view = new CSVView();
		view.setFilename(String.format("payments-export-%s.csv", DateUtilities.getISO8601(DateUtilities.getCalendarNow())));
		return view;
	}

	@RequestMapping(
		value = "/payments/generate_cc_receipt/{creditCardTransactionId}",
		method = GET)
	public View generateCCReceipt(
		@PathVariable("creditCardTransactionId") Long creditCardTransactionId,
		Model model) {

		Optional<CreditCardTransaction> optionalCreditCardTransaction = accountRegisterService.findCreditCardTransaction(creditCardTransactionId, getCurrentUser().getCompanyId());

		if (!optionalCreditCardTransaction.isPresent()) {
			// transaction doesn't exist or they do not have permission to access it
			return new RedirectView("/payments");
		}

		CreditCardTransaction creditCardTransaction = optionalCreditCardTransaction.get();

		model.addAttribute("creditCardTransaction", creditCardTransaction);
		model.addAttribute("date", CreditCardReceiptPDFTemplate.formatDate(creditCardTransaction));
		model.addAttribute("total", NumberUtilities.currency(creditCardTransaction.getAmount()));
		model.addAttribute("fullName", StringUtilities.fullName(creditCardTransaction.getFirstName(), creditCardTransaction.getLastName()));
		model.addAttribute("rows", CreditCardReceiptPDFTemplate.formatRows(creditCardTransaction));

		return new HTML2PDFView("pdf/payments/credit_card_receipt", "receipt-" + creditCardTransactionId + ".pdf");
	}

	@RequestMapping(
		value = "/payments/generate_bank_receipt/{bankTransactionId}",
		method = GET)
	public View generateACHReceipt(
		@PathVariable("bankTransactionId") Long bankTransactionId,
		Model model) {

		Optional<BankAccountTransaction> optionalBankAccountTransaction = accountRegisterService.findBankAccountTransaction(bankTransactionId, getCurrentUser().getCompanyId());
		if (!optionalBankAccountTransaction.isPresent()) {
			// transaction doesn't exist or they do not have permission to access it
			return new RedirectView("/payments");
		}

		Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
		BankAccountTransaction bankAccountTransaction = optionalBankAccountTransaction.get();
		AbstractBankAccount bankAccount = bankAccountTransaction.getBankAccount();
		BigDecimal totalCharged = bankAccountTransaction.getAmount();
		Map<String, Object> row = CollectionUtilities.newObjectMap(
			"description", "Funds added to Work Market account",
			"amount", NumberUtilities.currency(totalCharged)
		);
		model.addAttribute("bankNumber", bankAccount.getBankAccountSecureNumber());
		model.addAttribute("bankAccountTransaction", bankAccountTransaction);
		model.addAttribute("address", company.getAddress());
		model.addAttribute("date", DateUtilities.format("MM/dd/YY", bankAccountTransaction.getTransactionDate()));
		model.addAttribute("total", NumberUtilities.currency(bankAccountTransaction.getAmount()));
		model.addAttribute("companyName", getCurrentUser().getCompanyName());
		model.addAttribute("row", row);

		return new HTML2PDFView("pdf/payments/bank_account_receipt", "receipt-" + bankTransactionId + ".pdf");
	}

	@RequestMapping(
		value = "/payments/generate_wire_or_check_receipt/{type}/{checkTransactionId}",
		method = GET)
	public View generateCheckReceipt(
		@PathVariable("checkTransactionId") Long checkTransactionId,
		@PathVariable("type") String type,
		Model model) {

		Optional<RegisterTransaction> optionalWireDirectTransaction = accountRegisterService.findWireOrCheckTransaction(checkTransactionId, getCurrentUser().getCompanyId());

		if (!optionalWireDirectTransaction.isPresent()) {
			// transaction doesn't exist or they do not have permission to access it
			return new RedirectView("/payments");
		}

		Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
		RegisterTransaction wireDirectTransaction = optionalWireDirectTransaction.get();
		BigDecimal totalCharged = wireDirectTransaction.getAmount();
		Map<String, Object> row = CollectionUtilities.newObjectMap(
			"description", "Funds added to Work Market account",
			"amount", NumberUtilities.currency(totalCharged)
		);

		model.addAttribute("address", company.getAddress());
		model.addAttribute("registerTransaction", wireDirectTransaction);
		model.addAttribute("date", DateUtilities.format("MM/dd/YY", wireDirectTransaction.getTransactionDate()));
		model.addAttribute("total", NumberUtilities.currency(wireDirectTransaction.getAmount()));
		model.addAttribute("companyName", getCurrentUser().getCompanyName());
		model.addAttribute("row", row);
		if (type.equals("wire")) {
			model.addAttribute("title", "Receipt for Wire/Direct Deposit Funding");
			model.addAttribute("type", "Wire/Direct");
		} else {
			model.addAttribute("title", "Receipt for Check Deposit Funding");
			model.addAttribute("type", "Check");
		}
		return new HTML2PDFView("pdf/payments/wire_or_check_receipt", "receipt-" + checkTransactionId + ".pdf");
	}

	// Export activity to CSV.
	protected List<String[]> createActivityExport(HttpServletRequest httpRequest) throws Exception {
		RegisterTransactionActivityPagination pagination = new RegisterTransactionActivityPagination();
		pagination.setReturnAllRows(true);
		pagination.setSortColumn(RegisterTransactionActivityPagination.SORTS.TRANSACTION_DATE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		applyFilter(httpRequest, pagination);

		RegisterTransactionActivityPagination results = accountRegisterService.getLedgerForCompany(getCurrentUser().getCompanyId(), pagination);

		// Output headers.

		List<String[]> rows = Lists.newArrayList();
		rows.add(CollectionUtilities.newArray(
			"Date",
			"Type",
			"Description",
			"Debits",
			"Credits",
			"Balance",
			"Assignment ID",
			"Invoice ID",
			"Bundle ID"
		));

		for (RegisterTransactionActivity activity : results.getResults()) {
			rows.add(CollectionUtilities.newArray(
				activity.getFormattedTransactionDate(),
				activity.getDisplayTypeDescription(),
				activity.getFormattedDescription(),
				activity.getFormattedWithdrawal(),
				activity.getFormattedDeposit(),
				activity.getFormattedBalance(),
				activity.getWorkNumber(),
				activity.getInvoiceNumber(),
				activity.getInvoiceSummaryNumber()
			));
		}

		return rows;
	}

	protected List<String[]> createOfflineActivityExport(HttpServletRequest httpRequest) throws Exception {

		RegisterTransactionActivityPagination pagination = createLedgerExportPagination(httpRequest);
		RegisterTransactionActivityPagination results = accountRegisterService.getOfflineLedgerForCompany(getCurrentUser().getCompanyId(), pagination);

		List<String[]> rows = Lists.newArrayList();
		rows.add(CollectionUtilities.newArray(
			"Date",
			"Type",
			"Description",
			"Debits",
			"Credits",
			"Assignment ID",
			"Invoice ID",
			"Bundle ID"
		));

		for (RegisterTransactionActivity activity : results.getResults()) {
			rows.add(CollectionUtilities.newArray(
				activity.getFormattedTransactionDate(),
				activity.getDisplayTypeDescription(),
				activity.getFormattedDescription(),
				activity.getFormattedWithdrawal(),
				activity.getFormattedDeposit(),
				activity.getWorkNumber(),
				activity.getInvoiceNumber(),
				activity.getInvoiceSummaryNumber()
			));
		}

		return rows;
	}

	protected RegisterTransactionActivityPagination createLedgerExportPagination(HttpServletRequest httpRequest) throws Exception {
		RegisterTransactionActivityPagination pagination = new RegisterTransactionActivityPagination();
		pagination.setReturnAllRows(true);
		pagination.setSortColumn(RegisterTransactionActivityPagination.SORTS.TRANSACTION_DATE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		applyFilter(httpRequest, pagination);

		return pagination;
	}

	@RequestMapping(
		value = "/payments/dashboard",
		method = GET)
	public String paymentDashboard(Model model) {
		model.addAttribute("showGccBanner", showCreateGccAccountBanner());

		ExtendedUserDetails currentUser = getCurrentUser();

		model.addAttribute("accountSummary", accountRegisterService.getAccountRegisterSummaryFields(currentUser.getCompanyId()));

		Long currentUserId = currentUser.getId();

		if (currentUser.isSeller() || currentUser.isDispatcher()) {
			model.addAttribute("hasFastFunds", billingService.hasAtLeastOneFastFundableInvoice(currentUserId));
		}

		if (currentUser.isSeller() || currentUser.isDispatcher()) {
			model.addAttribute("sellerSums", summaryService.getPaymentCenterAggregateSummaryForSeller(currentUserId, false));
		}

		if (currentUser.isBuyer()) {
			model.addAttribute("buyerSums", summaryService.getPaymentCenterAggregateSummaryForBuyer(currentUserId));
		}

		model.addAttribute("spendLimit", getSpendLimit());
		model.addAttribute("apLimit", getAPLimit());
		model.addAttribute("hasProjectBudgetEnabled", hasProjectBudgetEnabled());
		model.addAttribute("projectCash", getProjectCash());
		model.addAttribute("generalCash", getGeneralCash());

		return "web/pages/payments/dashboard";
	}

	protected void applyFilter(HttpServletRequest httpRequest, RegisterTransactionActivityPagination pagination) {
		for (Enumeration<?> e = httpRequest.getParameterNames(); e.hasMoreElements();) {
			String key = (String)e.nextElement();
			String value = httpRequest.getParameter(key);

			if (StringUtils.isEmpty(value)) continue;

			if ("transaction_date_from".equals(key)) {
				value = localDateToUtc(value);
				pagination.addFilter(RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_FROM, value);
			} else if ("transaction_date_to".equals(key)) {
				value = tomorrowMidnight(value);
				pagination.addFilter(RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_TO, value);
			}
		}
	}

	protected String tomorrowMidnight(String value) {
		Calendar calendar = DateUtilities.parseCalendar(value);
		Calendar midnightNextDay = DateUtilities.getMidnightNextDayInTimezone(calendar, TimeZone.getTimeZone(getCurrentUser().getTimeZoneId()));
		return DateUtilities.getISO8601(midnightNextDay);
	}

	protected String localDateToUtc(String value) {
		Calendar calendar = DateUtilities.parseCalendar(value);
		Calendar midnight = DateUtilities.getMidnightInTimezone(calendar, TimeZone.getTimeZone(getCurrentUser().getTimeZoneId()));
		return DateUtilities.getISO8601(midnight);
	}

	protected boolean hasProjectBudgetEnabled() {
		return isAuthenticated() && companyService.doesCompanyHaveReservedFundsEnabledProject(getCurrentUser().getCompanyId());
	}

	protected BigDecimal getProjectCash() {
		if (isAuthenticated())
			return accountRegisterServicePrefundImpl.calculateProjectCashByCompany(getCurrentUser().getCompanyId());
		return null;
	}

}
