package com.workmarket.web.controllers.admin;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.data.aggregate.CompanyAggregate;
import com.workmarket.data.aggregate.CompanyAggregatePagination;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.AccountingCreditMemoSummaryDetail;
import com.workmarket.domains.model.account.AccountingEndOfYearTaxSummary;
import com.workmarket.domains.model.account.AccountingSummary;
import com.workmarket.domains.model.account.AccountingSummaryDetail;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.BankAccountTransactionStatus;
import com.workmarket.domains.model.account.FastFundsReceivableSummaryDetail;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.request.FundsRequest;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.domains.model.asset.type.TaxVerificationRequestAssetAssociationType;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequest;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequestType;
import com.workmarket.domains.model.company.CompanyStatusType;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.domains.model.invoice.CreditMemo;
import com.workmarket.domains.model.invoice.CreditMemoAudit;
import com.workmarket.domains.model.invoice.CreditMemoReasons;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.ServiceInvoicePagination;
import com.workmarket.domains.model.invoice.SubscriptionInvoiceType;
import com.workmarket.domains.model.invoice.WorkMarketSummaryInvoice;
import com.workmarket.domains.model.invoice.WorkMarketSummaryInvoicePagination;
import com.workmarket.domains.model.invoice.item.InvoiceLineItemType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.EarningDetailReport;
import com.workmarket.domains.model.tax.EarningDetailReportSet;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.EarningReportSet;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxForm1099Set;
import com.workmarket.domains.model.tax.TaxServiceReport;
import com.workmarket.domains.model.tax.TaxServiceReportSet;
import com.workmarket.domains.model.tax.TaxVerificationRequest;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.payments.service.CreditMemoAuditService;
import com.workmarket.dto.AggregatesDTO;
import com.workmarket.integration.autotask.util.StringUtil;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.BankingFileGenerationService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.account.JournalEntrySummaryService;
import com.workmarket.service.business.account.SubscriptionCalculator;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.account.summary.AccountingSummaryService;
import com.workmarket.service.business.accountregister.CreditMemoRegisterTransaction;
import com.workmarket.service.business.accountregister.CreditMemoType;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.business.tax.TaxStatusUpgradeService;
import com.workmarket.service.business.tax.TaxVerificationService;
import com.workmarket.service.business.tax.report.TaxReportService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.tax.TaxVerificationException;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.admin.AdhocInvoiceForm;
import com.workmarket.web.forms.admin.ChangeTaxStatusForm;
import com.workmarket.web.forms.admin.CreditMemoForm;
import com.workmarket.web.forms.admin.ManageFundsForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.views.CSVView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.workmarket.utility.StringUtilities.remove;
import static com.workmarket.utility.StringUtilities.truncate;
import static org.apache.commons.lang.StringUtils.upperCase;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/admin/accounting")
public class AdminAccountingController extends BaseController {

	private static final Log logger = LogFactory.getLog(AdminAccountingController.class);

	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterServicePrefundImpl;
	@Autowired private JournalEntrySummaryService journalEntrySummaryService;
	@Autowired private AccountingSummaryService accountingSummaryService;
	@Autowired private BankingFileGenerationService bankingFileGenerationService;
	@Autowired private TaxVerificationService taxVerificationService;
	@Autowired private CompanyService companyService;
	@Autowired private ProfileService profileService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private UploadService uploadService;
	@Autowired private TaxReportService taxReportService;
	@Autowired private TaxStatusUpgradeService taxStatusUpgradeService;
	@Autowired private BillingService billingService;
	@Autowired protected VaultHelper vaultHelper;
	@Autowired private CreditMemoAuditService creditMemoAuditService;
	@Autowired private SubscriptionCalculator subscriptionCalculator;
	@Autowired private SubscriptionService subscriptionService;
	@Autowired private JsonSerializationService jsonSerializationService;

	@Value("${baseurl}")
	private String baseUrl;

	private static final Character TIN_EXPORT_CSV_DELIMITER = ';';
	private static final Character JES_EXPORT_CSV_DELIMITER = ',';
	private static final int TIN_EXPORT_CSV_TAX_NAME_MAX_LENGTH = 40;
	private static final int POSSIBLE_ROWS_PER_FAST_FUNDS_RECEIVABLE_ENTRY = 2;
	private static final int NUMBER_OF_ROWS_FOR_HEADERS_AND_SPACING = 2;

	// Manage Company Funds - Credit Options
	private static final Map<String, String> creditOptions;
	// Manage Company Funds - Debit Options
	private static final Map<String, String> debitOptions;
	// WM Outstanding invoices status filters
	private static final List<String> wmInvoicesFilters;

	static {
		creditOptions = new ImmutableMap.Builder<String, String>()
			.put(RegisterTransactionType.CREDIT_CHECK_DEPOSIT, "Check Deposit")
			.put(RegisterTransactionType.CREDIT_WIRE_DIRECT_DEPOSIT, "Wire/Direct Deposit")
			.put(RegisterTransactionType.CREDIT_ACH_WITHDRAWABLE_RETURN, "Withdrawal Returns")
			.put(RegisterTransactionType.CREDIT_ADVANCE, "Advance")
			.put(RegisterTransactionType.CREDIT_ASSIGNMENT_PAYMENT_REVERSAL, "Assignment Payment Reversal")
			.put(RegisterTransactionType.CREDIT_FEE_REFUND_VOR, "Fee Refund - VOR")
			.put(RegisterTransactionType.CREDIT_FEE_REFUND_NVOR, "Fee Refund - NVOR")
			.put(RegisterTransactionType.CREDIT_BACKGROUND_CHECK_REFUND, "Background Check Refund")
			.put(RegisterTransactionType.CREDIT_DRUG_TEST_REFUND, "Drug Test Refund")
			.put(RegisterTransactionType.CREDIT_MARKETING_PAYMENT, "Marketing Payment")
			.put(RegisterTransactionType.CREDIT_RECLASS_TO_AVAILABLE_TO_WITHDRAWAL, "Reclass to Available to Withdraw")
			.put(RegisterTransactionType.CREDIT_FAST_FUNDS, "Fast Funds Credit")
			.put(RegisterTransactionType.CREDIT_FAST_FUNDS_FEE_REFUND, "Fast Funds Fee Refund")
			.put(RegisterTransactionType.CREDIT_GENERAL_REFUND, "General Refund")
			.put(RegisterTransactionType.CREDIT_MISCELLANEOUS, "Miscellaneous")
			.put(RegisterTransactionType.CREDIT_ADJUSTMENT, "Adjustment")
			.build();
		debitOptions = new ImmutableMap.Builder<String, String>()
			.put(RegisterTransactionType.DEBIT_ACH_DEPOSIT_RETURN, "ACH Deposit Return")
			.put(RegisterTransactionType.DEBIT_ADVANCE_REPAYMENT, "Advance Repayment")
			.put(RegisterTransactionType.DEBIT_ASSIGNMENT_PAYMENT_REVERSAL, "Assignment Payment Reversal")
			.put(RegisterTransactionType.DEBIT_CREDIT_CARD_CHARGEBACK, "Credit Card Chargeback")
			.put(RegisterTransactionType.DEBIT_CREDIT_CARD_REFUND, "Credit Card Return")
			.put(RegisterTransactionType.DEBIT_RECLASS_FROM_AVAILABLE_TO_SPEND, "Reclass From Available to Spend")
			.put(RegisterTransactionType.DEBIT_FAST_FUNDS, "Fast Funds Debit")
			.put(RegisterTransactionType.DEBIT_MISCELLANEOUS, "Miscellaneous")
			.put(RegisterTransactionType.DEBIT_ADJUSTMENT, "Adjustment")
			.build();
		wmInvoicesFilters = ImmutableList.of(
			InvoiceStatusType.PAYMENT_PENDING,
			InvoiceStatusType.PAID,
			"all"
		);
	}

	@RequestMapping(value = {"", "/", "/index"}, method = GET)
	public String index() {
		return "web/pages/admin/accounting/index";
	}

	/**
	 * Transaction Management
	 */
	@RequestMapping(value = "/withdrawals", method = GET)
	public String withdrawals() throws Exception {
		return "redirect:/admin/accounting/withdrawals/ach";
	}

	@RequestMapping(value = "/withdrawals/ach", method = GET)
	public String withdrawalsAch(Model model) throws Exception {
		List<BankAccountTransaction> transactions = accountRegisterServicePrefundImpl.findACHAccountWithdrawalTransactions();
		model.addAttribute("transactions", transactions);
		model.addAttribute("type", AbstractBankAccount.ACH);
		model.addAttribute("pageId", "adminAccountingAchWithdrawals");
		model.addAttribute("pageUri", "/admin/accounting/withdrawals/ach");

		return "web/pages/admin/accounting/withdrawals";
	}

	@RequestMapping(value = "/withdrawals/gcc", method = GET)
	public String withdrawalsGcc(Model model) throws Exception {
		model.addAttribute("transactions", accountRegisterServicePrefundImpl.findGCCAccountWithdrawalTransactions());
		model.addAttribute("type", AbstractBankAccount.GCC);
		model.addAttribute("pageId", "adminAccountingGCCWithdrawals");
		model.addAttribute("pageUri", "/admin/accounting/withdrawals/gcc");

		return "web/pages/admin/accounting/withdrawals";
	}

	@RequestMapping(value = "/withdrawals/paypal", method = GET)
	public String withdrawalsPayPal(Model model) throws Exception {
		model.addAttribute("transactions", accountRegisterServicePrefundImpl.findPayPalAccountWithdrawalTransactions());
		model.addAttribute("type", AbstractBankAccount.PAYPAL);
		model.addAttribute("pageId", "adminAccountingPayPalWithdrawals");
		model.addAttribute("pageUri", "/admin/accounting/withdrawals/paypal");

		return "web/pages/admin/accounting/withdrawals";
	}

	@RequestMapping(value = "/update_transaction_status", method = POST)
	public String updateTransactionStatus(
		RedirectAttributes flash,
		@RequestParam("returnTo") String returnTo,
		@RequestParam(value = "transactionIds[]", required = false) List<Long> transactionIds,
		@RequestParam(value = "updateStatus", required = false) String status) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (CollectionUtils.isEmpty(transactionIds)) {
			messageHelper.addError(bundle, "admin.accounting.update_txn_status.no_selection");
			return String.format("redirect:%s", returnTo);
		}

		bankingFileGenerationService.markBankAccountTransactionProcessing(transactionIds);
		bankingFileGenerationService.updateBankTransactionsStatusAsync(getCurrentUser().getId(), transactionIds,
			"", status);

		messageHelper.addSuccess(bundle, "admin.accounting.update_txn_status.processing");
		return String.format("redirect:%s", returnTo);
	}

	@RequestMapping(value = "/settle_transaction_pending_status", method = POST)
	public String settleTransactionPendingStatus(
		RedirectAttributes flash,
		@RequestParam("returnTo") String returnTo,
		@RequestParam(value = "transactionIds[]", required = false) List<Long> transactionIds,
		@RequestParam(value = "updateStatus", required = false) String status,
		@RequestParam(value = "updateNote", required = false) String note) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (CollectionUtils.isEmpty(transactionIds)) {
			messageHelper.addError(bundle, "admin.accounting.nacha.settle.empty");
			return String.format("redirect:%s", returnTo);
		}

		Set<Long> errorIds = Sets.newHashSet();

		for (Long tid : transactionIds) {
			try {
				if (BankAccountTransactionStatus.APPROVED.equalsIgnoreCase(status)) {
					bankingFileGenerationService.markBankAccountTransactionNonPending(tid);
				} else {
					bankingFileGenerationService.updateBankTransactionStatus(getCurrentUser().getId(), tid, note, status);
				}
			} catch (Exception e) {
				logger.error(String.format("[txSettle] transaction settle failed for transactionId=%d", tid), e);
				errorIds.add(tid);
			}
		}

		if (errorIds.isEmpty()) {
			messageHelper.addSuccess(bundle, "admin.accounting.nacha.settle.success");
		} else {
			messageHelper.addError(bundle, "admin.accounting.nacha.settle.error", errorIds.toString());
		}

		return String.format("redirect:%s", returnTo);
	}

	@RequestMapping(value = "/achfunding", method = GET)
	public String achfunding(Model model) throws Exception {
		List<BankAccountTransaction> transactions = accountRegisterServicePrefundImpl.findAccountFundingTransaction();
		model.addAttribute("transactions", transactions);
		model.addAttribute("type", AbstractBankAccount.ACH);
		model.addAttribute("pageId", "adminAccountingAchFunding");
		model.addAttribute("pageUri", "/admin/accounting/achfunding");

		return "web/pages/admin/accounting/withdrawals";
	}

	@RequestMapping(value = "/achverifications", method = GET)
	public String achVerifications(Model model) throws Exception {
		model.addAttribute("transactions", accountRegisterServicePrefundImpl.findBankACHVerificationTransactions());
		model.addAttribute("type", AbstractBankAccount.ACH);
		model.addAttribute("pageId", "adminAccountingAchVerifications");
		model.addAttribute("pageUri", "/admin/accounting/achverifications");

		return "web/pages/admin/accounting/withdrawals";
	}

	/**
	 * Fund Management
	 */
	@RequestMapping(value = "/managefunds", method = GET)
	public String manageFunds(@ModelAttribute("form") ManageFundsForm form, Model model) {
		model.addAttribute("credit_options", creditOptions);
		model.addAttribute("debit_options", debitOptions);

		return "web/pages/admin/accounting/managefunds";
	}

	@RequestMapping(value = "/managefunds", method = POST)
	public String manageFundsSubmit(
		@Valid @ModelAttribute("form") ManageFundsForm form,
		BindingResult bindingResult,
		RedirectAttributes flash,
		Model model) {

		MessageBundle messages = messageHelper.newBundle(model);

		model.addAttribute("credit_options", creditOptions);
		model.addAttribute("debit_options", debitOptions);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			return "web/pages/admin/accounting/managefunds";
		}

		if (!NumberUtilities.isPositive(form.getAmount())) {
			messageHelper.addError(messages, "admin.accounting.managefunds.invalid_amount");
			return "web/pages/admin/accounting/managefunds";
		}

		try {
			FundsRequest fundsRequest = new FundsRequest(form.getCompanyId(), form.getNote(), form.getAmount(), form.getDescription()).setNotify(false);
			if (form.getType().equalsIgnoreCase("credit")) {
				if (!creditOptions.containsKey(form.getDescription())) {
					return "web/pages/admin/accounting/managefunds";
				}
				accountRegisterServicePrefundImpl.addFundsToRegisterAsCredit(fundsRequest);

			} else if (form.getType().equalsIgnoreCase("cash_out")) {
				if (!debitOptions.containsKey(form.getDescription())) {
					return "web/pages/admin/accounting/managefunds";
				}
				accountRegisterServicePrefundImpl.removeFundsFromRegisterAsCash(fundsRequest);
			}

			Company company = profileService.findCompanyById(form.getCompanyId());

			messages = messageHelper.newFlashBundle(flash);
			messageHelper.addSuccess(messages, "admin.accounting.managefunds.success", company.getName(), company.getId().toString());
			return "redirect:/admin/accounting/managefunds";
		} catch (InsufficientFundsException ife) {
			messageHelper.addError(messages, "admin.accounting.managefunds.insufficientfunds");
			return "web/pages/admin/accounting/managefunds";
		} catch (Exception e) {
			messageHelper.addError(messages, "admin.accounting.managefunds.error");
			return "web/pages/admin/accounting/managefunds";
		}
	}

	/**
	 * Bank File Generation - NACHA & PayPal
	 */
	@RequestMapping(value = "/nacha", method = GET)
	public String nacha(
		Model model,
		@RequestParam(value = "type", defaultValue = BankingIntegrationGenerationRequestType.ACHVERIFY) String requestType)
		throws Exception {

		// TODO: port these to use Pagination
		if (TaxVerificationRequest.TAX_TYPE_TIN.equals(requestType)) {
			model.addAttribute("requests", taxVerificationService.findTaxVerificationRequests());
		} else {
			model.addAttribute("requests", bankingFileGenerationService.findBankingIntegrationGenerationRequests(requestType));
		}
		model.addAttribute("current_type", requestType);
		model.addAttribute("inboundAvailable", !bankingFileGenerationService.bankFileGenerationInProcess(BankingIntegrationGenerationRequestType.INBOUND));
		model.addAttribute("outboundAvailable", !bankingFileGenerationService.bankFileGenerationInProcess(BankingIntegrationGenerationRequestType.OUTBOUND));
		model.addAttribute("nonUsaOutboundAvailable", !bankingFileGenerationService.bankFileGenerationInProcess(BankingIntegrationGenerationRequestType.NON_USA_OUTBOUND));
		model.addAttribute("achverifyAvailable", !bankingFileGenerationService.bankFileGenerationInProcess(BankingIntegrationGenerationRequestType.ACHVERIFY));
		model.addAttribute("paypalAvailable", !bankingFileGenerationService.bankFileGenerationInProcess(BankingIntegrationGenerationRequestType.PAYPAL));
		model.addAttribute("gccAvailable", !bankingFileGenerationService.bankFileGenerationInProcess(BankingIntegrationGenerationRequestType.GCC));
		model.addAttribute("tinAvailable", taxVerificationService.isTaxVerificationAvailable());
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/admin/accounting/nacha"));

		return "web/pages/admin/accounting/nacha";
	}


	@RequestMapping(value = "/deleteRequest/{id}")
	public String deleteRequest(@PathVariable("id") long requestId, RedirectAttributes flash) throws Exception {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		BankingIntegrationGenerationRequest request = bankingFileGenerationService.findBankingIntegrationGenerationRequest(requestId);
		if (request != null && request.getBankingIntegrationGenerationRequestStatus().isSubmitted()) {
			bankingFileGenerationService.cancelBankingIntegrationGenerationRequest(requestId);
			messageHelper.addSuccess(bundle, "admin.accounting.nacha.delete_request.success");
			return "redirect:/admin/accounting/nacha?type=" + request.getBankingIntegrationGenerationRequestType().getCode();
		} else {
			messageHelper.addError(bundle, "admin.accounting.nacha.delete_request.exception");
		}
		return "redirect:/admin/accounting/nacha";
	}


	@RequestMapping(value = "/initiate_nacha", method = POST)
	public String initiateNacha(@RequestParam("type") String type, RedirectAttributes flash) throws Exception {

		if (TaxVerificationRequest.TAX_TYPE_TIN.equals(type)) {
			Optional<TaxVerificationRequest> requestOpt = taxVerificationService.createUsaTaxVerificationBatch(getCurrentUser().getId());
			MessageBundle bundle = messageHelper.newFlashBundle(flash);
			if (!requestOpt.isPresent()) {
				messageHelper.addError(bundle, "admin.accounting.tin.initiate_nacha.no_tins_found");
			} else {
				messageHelper.addSuccess(bundle, "admin.accounting.tin.initiate_nacha.num_tins_found", requestOpt.get().getTinCount());
			}
		} else {
			bankingFileGenerationService.initiateBankFileProcessing(getCurrentUser().getId(), type, "");
		}
		return "redirect:/admin/accounting/nacha?type=" + type;
	}

	// TODO: port this to use proper param mapping (@RequestParam) and AjaxResponseBuilder
	@RequestMapping(value = "/set_batch_number", method = POST, produces = APPLICATION_JSON_VALUE)
	public void setBatchNumber(Model model, HttpServletRequest httpRequest) {

		Map<String, Object> response = Maps.newHashMap();

		if (StringUtils.isBlank(httpRequest.getParameter("batch_number"))) {
			response.put("successful", false);
			response.put("errors", Lists.newArrayList("Batch number is a required field."));
		} else {
			try {
				bankingFileGenerationService.addBatchNumberToBankingFile(
					NumberUtils.createLong(httpRequest.getParameter("request_id")),
					httpRequest.getParameter("batch_number")
				);
				response.put("successful", true);
			} catch (Exception e) {
				response.put("successful", false);
				response.put("errors", Lists.newArrayList("Batch number is a required field."));
			}
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/settle/{id}", method = GET)
	public String settle(Model model, @PathVariable("id") Long id) throws Exception {
		BankingIntegrationGenerationRequest request = bankingFileGenerationService.loadTransactionsAttachedToBankingIntegration(id);
		List<BankAccountTransaction> txs = request.getBankAccountTransactions();
		if (hasFeature(getCurrentUser().getCompanyId(), "vaultRead")) {
			for (BankAccountTransaction tx : txs) {
				vaultHelper.setVaultedValues(tx.getBankAccount());
			}
		}
		model.addAttribute("request", request);
		model.addAttribute("transactions", txs);
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/admin/accounting/settle"));

		return "web/pages/admin/accounting/settle";
	}

	/**
	 * Accounting Summaries & Reports
	 */
	@RequestMapping(value = "/summaries", method = GET)
	public String summaries(Model model) {

		model.addAttribute("summaries", journalEntrySummaryService.findAllSummaries());
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/admin/accounting/summaries"));

		return "web/pages/admin/accounting/summaries";
	}

	@RequestMapping(value = "/summary_detail/{id}", method = GET)
	public String summaryDetail(
		@PathVariable("id") Long id,
		Model model) throws Exception {

		List<AccountingSummaryDetail> summaryDetailNonVor, summaryDetailSoftware, summaryDetailVor;
		summaryDetailSoftware = journalEntrySummaryService.findMoneyOutSubscriptionSWFeesDetail(id);
		summaryDetailVor = journalEntrySummaryService.findMoneyOutSubscriptionVORFeesDetail(id);
		summaryDetailNonVor = journalEntrySummaryService.findMoneyOutSubscriptionNVORSoftwareFeesDetail(id);
		model.addAttribute("summary", journalEntrySummaryService.findSummary(id));
		model.addAttribute("summaryId", id);
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/admin/accounting/summary"));
		model.addAttribute("summaryDetailSoftwareSize", summaryDetailSoftware.size());
		model.addAttribute("summaryDetailVorSize", summaryDetailVor.size());
		model.addAttribute("summaryDetailNonVorSize", summaryDetailNonVor.size());

		return "web/pages/admin/accounting/summary";
	}

	private String formatDateForCSV(Calendar date) {
		// Same timezone that's rendered on JES page (see: summary.jsp)
		return DateUtilities.getISO8601WithSpaces(date, getCurrentUser().getTimeZoneId());
	}

	private void getFastFundsReceivableSummaryDetail(List<FastFundsReceivableSummaryDetail> fastFundsReceivableSummaryDetails, long summaryId, boolean isYTD, Model model) {
		Calendar summaryStartRequestDate;
		if (isYTD) {
			AccountingSummary accountingSummary = journalEntrySummaryService.findSummary(summaryId);
			summaryStartRequestDate = journalEntrySummaryService.findOrCreateStartFiscalYearForDate(accountingSummary.getRequestDate());
		} else {
			summaryStartRequestDate = journalEntrySummaryService.findPreviousRequestDateOfSummary(summaryId);
		}

		Calendar summaryEndRequestDate = journalEntrySummaryService.findRequestDateOfSummary(summaryId);
		Assert.notNull(summaryStartRequestDate);
		Assert.notNull(summaryEndRequestDate);

		final String TYPE_FIELD_COLUMN = "Type", AMOUNT_COLUMN = "Amount",
			FAST_FUNDS_DEPOSIT_TYPE = "Fast Funds Deposit", FAST_FUNDS_PAYMENT_TYPE = "Fast Funds Assignment Payment";

		List<String[]> data = Lists.newArrayListWithExpectedSize((fastFundsReceivableSummaryDetails.size() * POSSIBLE_ROWS_PER_FAST_FUNDS_RECEIVABLE_ENTRY) + NUMBER_OF_ROWS_FOR_HEADERS_AND_SPACING);
		data.add(new String[] {
			"Fast Funds Date",
			TYPE_FIELD_COLUMN,
			"Assignment ID",
			"Invoice ID",
			"Assignment Payment Due Date",
			"Assignment Payment Date",
			"Buyer Company Name",
			"Buyer Company ID",
			"Worker Company Name",
			"Worker Company ID",
			AMOUNT_COLUMN
		});

		List columnList = Lists.newArrayList(data.get(0));
		int indexOfTypeColumn = columnList.indexOf(TYPE_FIELD_COLUMN);
		int indexOfAmountColumn = columnList.indexOf(AMOUNT_COLUMN);

		data.add(new String[]{StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY});

		for (FastFundsReceivableSummaryDetail ffDetail : fastFundsReceivableSummaryDetails) {
			Calendar fastFundedOnDate = ffDetail.getFastFundedOnDate();
			Calendar paymentDate = ffDetail.getPaymentDate();

			String[] row = new String[] {
				formatDateForCSV(fastFundedOnDate),
				FAST_FUNDS_DEPOSIT_TYPE,
				ffDetail.getWorkNumber(),
				ffDetail.getInvoiceNumber(),
				formatDateForCSV(ffDetail.getInvoiceDueDate()),
				formatDateForCSV(paymentDate),
				ffDetail.getCompanyName(),
				String.valueOf(ffDetail.getBuyerCompanyId()),
				ffDetail.getWorkerCompanyName(),
				String.valueOf(ffDetail.getWorkerCompanyId()),
				String.valueOf(ffDetail.getAmount().negate())
			};

			if (fastFundedOnDate.compareTo(summaryStartRequestDate) >= 0 && fastFundedOnDate.compareTo(summaryEndRequestDate) <= 0) {
				data.add(row.clone());
			}
			if (paymentDate != null && paymentDate.compareTo(summaryStartRequestDate) >= 0 && paymentDate.compareTo(summaryEndRequestDate) <= 0) {
				row[indexOfTypeColumn] = FAST_FUNDS_PAYMENT_TYPE;
				row[indexOfAmountColumn] = String.valueOf(ffDetail.getAmount());
				data.add(row);
			}
		}
		model.addAttribute(CSVView.CSV_MODEL_KEY, data);
	}

	private void getSummaryDetail(List<AccountingSummaryDetail> summaryDetail, Model model) {
		List<String[]> data = Lists.newArrayListWithExpectedSize(summaryDetail.size() + 2);
		data.add(new String[]{"Amount", "Invoice Type", "Invoice number", "Company Name", "Invoice On",
			"Invoice Due Date", "Payment Date"});
		data.add(new String[]{StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY});
		for (AccountingSummaryDetail detail : summaryDetail) {
			data.add(new String[]{String.valueOf(
				detail.getAmount()), detail.getInvoiceType(), detail.getInvoiceNumber(), detail.getCompanyName(),
				DateUtilities.format("MM-dd-yyyy", detail.getInvoiceOn(), Constants.WM_TIME_ZONE),
				DateUtilities.format("MM-dd-yyyy", detail.getInvoiceDueDate(), Constants.WM_TIME_ZONE),
				DateUtilities.format("MM-dd-yyyy", detail.getPaymentDate(), Constants.WM_TIME_ZONE)});
		}
		model.addAttribute(CSVView.CSV_MODEL_KEY, data);
	}

	private void getCreditMemoTransactionDetail(List<AccountingCreditMemoSummaryDetail> summaryDetail, Model model) {
		List<String[]> data = Lists.newArrayListWithExpectedSize(summaryDetail.size() + 2);
		data.add(new String[]{"amount", "register_transaction_type_code", "type", "invoice_number", "effective_name", "invoicedOn", "subscription_invoice_type_code", "reason", "note", "amount", "revenue_effective_date", "register_transaction_type_code", "type", "invoice_number", "effective_name", "invoicedOn", "due_date", "payment_date", "subscription_invoice_type_code"});
		data.add(new String[]{StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY});
		for (AccountingCreditMemoSummaryDetail detail : summaryDetail) {
			data.add(new String[]{
				String.valueOf(detail.getAmount().negate()),
				detail.getCreditMemoType(),
				detail.getInvoiceType(),
				detail.getInvoiceNumber(),
				detail.getEffectiveName(),
				DateUtilities.format("MM-dd-yyyy", detail.getInvoicedOn(), Constants.WM_TIME_ZONE),
				detail.getSubscriptionInvoiceTypeCode(),
				detail.getReason(),
				detail.getNote(),
				String.valueOf(detail.getOriginalTransactionAmount()),
				DateUtilities.format("MM-dd-yyyy", detail.getOriginalInvoiceRevenueEffectiveDate(), Constants.WM_TIME_ZONE),
				detail.getOriginalInvoiceRegisterTransactionTypeCode(),
				detail.getOriginalInvoiceType(),
				detail.getOriginalInvoiceNumber(),
				detail.getOriginalInvoiceEffectiveName(),
				DateUtilities.format("MM-dd-yyyy", detail.getOriginalInvoicedOn(), Constants.WM_TIME_ZONE),
				DateUtilities.format("MM-dd-yyyy", detail.getOriginalInvoiceDueDate(), Constants.WM_TIME_ZONE),
				DateUtilities.format("MM-dd-yyyy", detail.getOriginalInvoicePaymentDate(), Constants.WM_TIME_ZONE),
				detail.getOriginalInvoiceSubscriptionInvoiceTypeCode()
			});
		}
		model.addAttribute(CSVView.CSV_MODEL_KEY, data);
	}

	@RequestMapping(value = "/vor_nvor_status_update",method = GET)
	public String vorNvorStatusUpdate(@ModelAttribute("form") ChangeTaxStatusForm form, Model model) throws Exception {
		Calendar now = Calendar.getInstance();

		model.addAttribute("minYear", now.get(Calendar.YEAR) - 5);
		model.addAttribute("maxYear", now.get(Calendar.YEAR) - 1);

		model.addAttribute("latestPublishedVorYear",
				taxReportService.findLatestPublishedTaxForm1099Report().getTaxYear());
		model.addAttribute("latestPublishedTaxYear",
				taxReportService.findLatestPublishedTaxServiceReport().getTaxYear());
		model.addAttribute("latestPublishedNoneYear",
				taxReportService.findLatestPublishedEarningDetailReport().getTaxYear());

		return "web/pages/admin/accounting/vor_nvor_status_update";
	}

	@RequestMapping(value = "/vor_nvor_status_update", method = POST)
	public String vorNvorStatusUpdate(
		@Valid @ModelAttribute("form") ChangeTaxStatusForm form,
		BindingResult bindingResult,
		RedirectAttributes flash,
		Model model) throws Exception {

		MessageBundle messages = messageHelper.newBundle(model);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			return "redirect:/admin/accounting/vor_nvor_status_update";
		}

		messages = messageHelper.newFlashBundle(flash);

		try {
			Calendar start = journalEntrySummaryService.findOrCreateStartFiscalYear(form.getFiscalYear());
			Calendar end = journalEntrySummaryService.findOrCreateStartFiscalYear(form.getFiscalYear() + 1);
			Company company = profileService.findCompanyById(form.getCompanyId());
			AccountServiceType serviceType = new AccountServiceType(form.getTaxStatus());

			taxStatusUpgradeService.upgradeCompanyTaxStatus(company, serviceType, start, end);

			messageHelper.addSuccess(messages, "admin.accounting.taxupdate.success");
		} catch (Exception e) {
			logger.error(e.getMessage());
			messageHelper.addError(messages, "admin.accounting.taxupdate.error");
		}

		return "redirect:/admin/accounting/vor_nvor_status_update";
	}

	@RequestMapping(value = "/vor_nvor_report",method = GET)
	public String vorNvorReport(Model model) throws Exception {
		return "web/pages/admin/accounting/vor_nvor_report";
	}

	@RequestMapping(value = "/export_end_of_year_taxes_report", method = GET)
	public CSVView getEndOfYearTaxReport(
			Model model,
			@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to
	) throws Exception {
		List<AccountingEndOfYearTaxSummary> summaryDetail = null;

		Calendar start = journalEntrySummaryService.findOrCreateStartFiscalYearForDate(Calendar.getInstance());
		Calendar end = Calendar.getInstance();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

			if (from != null && from.trim().length() > 0) {
				start.setTime(sdf.parse(from));
			}

			if (to != null && to.trim().length() > 0) {
				end.setTime(sdf.parse(to));
			}

			logger.debug("Start historical: " + DateUtilities.format("MM/dd/yyyy hh:mm:ss", start));
			logger.debug("End historical: " + DateUtilities.format("MM/dd/yyyy  hh:mm:ss", end));

			summaryDetail = journalEntrySummaryService.getEndOfYearTaxReport(start, end);
		} catch (Exception e) {
			logger.error("Error fetching JES Detail summary CSV: ", e);
		}

		if (summaryDetail != null) {

			getEndOfYearTaxReportDetail(summaryDetail, model);
			String filename = String.format("VorNvorReport--%s--%s.csv", DateUtilities.format("yyyy-MM-dd HH-mm-ss", start), DateUtilities.format("yyyy-MM-dd HH-mm-ss", end));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	private void getEndOfYearTaxReportDetail(List<AccountingEndOfYearTaxSummary> summaryDetail, Model model) {
		List<String[]> data = Lists.newArrayListWithExpectedSize(summaryDetail.size() + 2);

		data.add(new String[]{"Company Id", "Company Name", "Paid To Workers", "Service Type", "Pricing Type", "Start Date", "End Date"});
		data.add(new String[]{StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY});

		for (AccountingEndOfYearTaxSummary detail : summaryDetail) {
			data.add(new String[]{
					String.valueOf(detail.getCompanyId()), detail.getCompanyName(), String.valueOf(detail.getPaidToWorkers()),
					detail.getServiceType(), detail.getPricingType(),
				DateUtilities.format("MM/dd/yyyy h:mmaa z", detail.getStartDate(), Constants.WM_TIME_ZONE),
				DateUtilities.format("MM/dd/yyyy h:mmaa z", detail.getEndDate(), Constants.WM_TIME_ZONE)});
		}
		model.addAttribute(CSVView.CSV_MODEL_KEY, data);
	}


	@RequestMapping(value = "/export_summary_subs_sw_fees/{id}", method = GET)
	public CSVView downloadJESDetail(@PathVariable Long id, Model model) {

		List<AccountingSummaryDetail> summaryDetail = null;
		try {
			summaryDetail = journalEntrySummaryService.findMoneyOutSubscriptionSWFeesDetail(id);
		} catch (Exception e) {
			logger.error("Error fetching JES Detail summary CSV: ", e);
		}

		if (summaryDetail != null) {

			getSummaryDetail(summaryDetail, model);
			String filename = String.format("jesExportDetail%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	@RequestMapping(value = "/export_summary_subs_vor_fees/{id}", method = GET)
	public CSVView downloadJESDetailVor(@PathVariable Long id, Model model) {

		List<AccountingSummaryDetail> summaryDetail = null;
		try {
			summaryDetail = journalEntrySummaryService.findMoneyOutSubscriptionVORFeesDetail(id);
		} catch (Exception e) {
			logger.error("Error exporting JES Detail VOR CSV", e);
		}

		if (summaryDetail != null) {
			getSummaryDetail(summaryDetail, model);
			String filename = String.format("jesExportDetail%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	@RequestMapping(value = "/export_summary_subs_nonvor_sw_fees/{id}", method = GET)
	public CSVView downloadJESDetailNonVor(@PathVariable Long id, Model model) {

		List<AccountingSummaryDetail> summaryDetail = null;
		try {
			summaryDetail = journalEntrySummaryService.findMoneyOutSubscriptionNVORSoftwareFeesDetail(id);
		} catch (Exception e) {
			logger.error("Error exporting JES Detail non-VOR SW Fees CSV", e);
		}

		if (summaryDetail != null) {
			getSummaryDetail(summaryDetail, model);
			String filename = String.format("jesExportDetail%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	@RequestMapping(value = "/export_summary_acc_rev_subs_vor_sw/{id}", method = GET)
	public CSVView downloadJESDetailAccItemRevSubVorSw(@PathVariable Long id, Model model) {

		List<AccountingSummaryDetail> summaryDetail = journalEntrySummaryService.getAccItemRevSubVorSw(id);

		if (summaryDetail != null) {

			getSummaryDetail(summaryDetail, model);
			String filename = String.format("jesExportDetailAccItemRevSubVorSw%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	@RequestMapping(value = "/export_summary_acc_rev_subs_vor_sw_ytd/{id}", method = GET)
	public CSVView downloadJESDetailAccItemRevSubVorSwYTD(@PathVariable Long id, Model model) {

		List<AccountingSummaryDetail> summaryDetail = journalEntrySummaryService.getAccItemRevSubVorSwYTD(id);

		if (summaryDetail != null) {

			getSummaryDetail(summaryDetail, model);
			String filename = String.format("jesExportDetailAccItemRevSubVorSwYTD%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	@RequestMapping(value = "/export_summary_fast_funds_receivables/{id}", method = GET)
	public CSVView downloadJESDetailAccItemRevSubVorSw(
		@PathVariable Long id,
		@RequestParam(value = "ytd", required = false, defaultValue = "false") boolean isYTD,
		Model model) {

		List<FastFundsReceivableSummaryDetail> fastFundsReceivableSummaryDetail = journalEntrySummaryService.getFastFundsReceivableSummaryDetails(id, isYTD);

		if (fastFundsReceivableSummaryDetail != null) {
			getFastFundsReceivableSummaryDetail(fastFundsReceivableSummaryDetail, id, isYTD, model);
			String filename = String.format("jesExportDetailFastFundsReceivables%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	@RequestMapping(value = "/export_summary_acc_rev_subs_vor_vor/{id}", method = GET)
	public CSVView downloadJESDetailAccItemRevSubVorVor(@PathVariable Long id, Model model) {

		List<AccountingSummaryDetail> summaryDetail = journalEntrySummaryService.getAccItemRevSubVorVor(id);

		if (summaryDetail != null) {

			getSummaryDetail(summaryDetail, model);
			String filename = String.format("jesExportDetailAccItemRevSubVorVor%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	@RequestMapping(value = "/export_summary_acc_rev_subs_vor_vor_ytd/{id}", method = GET)
	public CSVView downloadJESDetailAccItemRevSubVorVorYTD(@PathVariable Long id, Model model) {

		List<AccountingSummaryDetail> summaryDetail = journalEntrySummaryService.getAccItemRevSubVorVorYTD(id);

		if (summaryDetail != null) {

			getSummaryDetail(summaryDetail, model);
			String filename = String.format("jesExportDetailAccItemRevSubVorVorYTD%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	@RequestMapping(value = "/export_summary_acc_rev_subs_nvor/{id}", method = GET)
	public CSVView downloadJESDetailAccItemRevSubNVor(@PathVariable Long id, Model model) {

		List<AccountingSummaryDetail> summaryDetail = journalEntrySummaryService.getAccItemRevSubNVor(id);

		if (summaryDetail != null) {

			getSummaryDetail(summaryDetail, model);
			String filename = String.format("jesExportDetailAccItemRevSubNVor%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	@RequestMapping(value = "/export_summary_acc_rev_subs_nvor_ytd/{id}", method = GET)
	public CSVView downloadJESDetailAccItemRevSubNVorYTD(@PathVariable Long id, Model model) {

		List<AccountingSummaryDetail> summaryDetail = journalEntrySummaryService.getAccItemRevSubNVorYTD(id);

		if (summaryDetail != null) {

			getSummaryDetail(summaryDetail, model);
			String filename = String.format("jesExportDetailAccItemRevSubNVorSYTD%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	/**
	 * Subscription Fees - Credit - Subscription VOR - Software Fees
	 * Credit Memos for Paid+Unpaid Subscription VOR - Software Fee Transactions
	 */
	@RequestMapping(value = "/export_acc_credit_memo_details_vor_software/{id}", method = GET)
	public CSVView downloadJESDetailVorSoftwareFeeCreditMemos(
		@PathVariable Long id,
		@RequestParam(value = "ytd", required = false, defaultValue = "false") boolean isYTD,
		Model model) {

		List<AccountingCreditMemoSummaryDetail> summaryDetail = journalEntrySummaryService.getCreditMemoTransactionDetails(id,
			isYTD, CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, false, false, true, true);
		return getJESExportCreditMemoDetail(summaryDetail, "jesExportDetailVorSoftwareFeeCreditMemos", model, isYTD);
	}

	/**
	 * Subscription Fees - Credit - Subscription VOR - VOR Fees
	 * Credit Memos for Paid+Unpaid Subscription VOR - VOR Fees Transactions
	 */
	@RequestMapping(value = "/export_acc_credit_memo_details_vor_vor/{id}", method = GET)
	public CSVView downloadJESDetailVorFeeCreditMemos(
		@PathVariable Long id,
		@RequestParam(value = "ytd", required = false, defaultValue = "false") boolean isYTD,
		Model model) {

		List<AccountingCreditMemoSummaryDetail> summaryDetail = journalEntrySummaryService.getCreditMemoTransactionDetails(id,
			isYTD, CreditMemoType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT_CREDIT.ordinal(), false, false);
		return getJESExportCreditMemoDetail(summaryDetail, "jesExportDetailVorFeeCreditMemos", model, isYTD);
	}

	/**
	 * Subscription Fees - Credit - Subscription NVOR - Software Fees
	 * Credit Memos for Paid+Unpaid Subscription NVOR - Software Fee Transactions
	 */
	@RequestMapping(value = "/export_acc_credit_memo_details_nonvor_software/{id}", method = GET)
	public CSVView downloadJESDetailNonVorSoftwareFeeCreditMemos(
		@PathVariable Long id,
		@RequestParam(value = "ytd", required = false, defaultValue = "false") boolean isYTD,
		Model model) {

		List<AccountingCreditMemoSummaryDetail> summaryDetail = journalEntrySummaryService.getCreditMemoTransactionDetails(id,
			isYTD, CreditMemoRegisterTransaction.CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS, false, false, true, false);
		return getJESExportCreditMemoDetail(summaryDetail, "jesExportDetailNonVorSoftwareFeeCreditMemos", model, isYTD);
	}

	/**
	 * Professional Services Fees - Credits
	 * Credit Memos for Paid+Unpaid Professional Services Transactions
	 */
	@RequestMapping(value = "/export_acc_credit_memo_details_prof_services/{id}", method = GET)
	public CSVView downloadJESDetailProfServicesFeeCreditMemos(
		@PathVariable Long id,
		@RequestParam(value = "ytd", required = false, defaultValue = "false") boolean isYTD,
		Model model) {

		List<AccountingCreditMemoSummaryDetail> summaryDetail = journalEntrySummaryService.getCreditMemoTransactionDetails(id,
			isYTD, CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal(), false, false);
		return getJESExportCreditMemoDetail(summaryDetail, "jesExportDetailProfServicesFeeCreditMemos", model, isYTD);
	}

	/**
	 * Money In - Professional Services - Paid Invoices
	 */
	@RequestMapping(value = "/export_money_in_credit_memo_details_prof_services/{id}", method = GET)
	public CSVView downloadJESMoneyInProfServicesCreditMemos(
		@PathVariable Long id,
		@RequestParam(value = "ytd", required = false, defaultValue = "false") boolean isYTD,
		Model model) {

		List<AccountingCreditMemoSummaryDetail> summaryDetail = journalEntrySummaryService.getCreditMemoTransactionDetails(id,
			isYTD, CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal(), true, true);
		return getJESExportCreditMemoDetail(summaryDetail, "jesExportMoneyInProfServicesCreditMemos", model, isYTD);
	}

	/**
	 * Money In - Subscription Fees - Paid Invoices
	 */
	@RequestMapping(value = "/export_money_in_credit_memo_details_subscription/{id}", method = GET)
	public CSVView downloadJESMoneyInSubscriptionCreditMemos(
		@PathVariable Long id,
		@RequestParam(value = "ytd", required = false, defaultValue = "false") boolean isYTD,
		Model model) {

		List<AccountingCreditMemoSummaryDetail> summaryDetail = journalEntrySummaryService.getCreditMemoTransactionDetails(id,
			isYTD, CreditMemoRegisterTransaction.CREDIT_MEMO_SUBSCRIPTION_TYPE_IDS,
			true, true, false, false);
		return getJESExportCreditMemoDetail(summaryDetail, "jesExportMoneyInSubscriptionCreditMemos", model, isYTD);
	}

	/**
	 * Professional Services Fees Receivables - Credits
	 * Credit Memos for Unpaid Professional Services Transactions
	 */
	@RequestMapping(value = "/export_summary_acc_credit_prof_services_receivables/{id}", method = GET)
	public CSVView downloadJESReceivablesProfServicesCreditMemos(
		@PathVariable Long id,
		@RequestParam(value = "ytd", required = false, defaultValue = "false") boolean isYTD,
		Model model) {

		List<AccountingCreditMemoSummaryDetail> summaryDetail = journalEntrySummaryService.getCreditMemoTransactionDetails(id,
			isYTD, CreditMemoType.SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT.ordinal(), true, false);
		return getJESExportCreditMemoDetail(summaryDetail, "jesExportReceivablesProfServicesCreditMemos", model, isYTD);
	}

	/**
	 * Subscription Fees Receivables - Credits
	 * Credit Memos for Unpaid Subscription Transactions
	 */
	@RequestMapping(value = "/export_summary_acc_credit_subscription_receivables/{id}", method = GET)
	public CSVView downloadJESReceivablesSubscriptionCreditMemos(
		@PathVariable Long id,
		@RequestParam(value = "ytd", required = false, defaultValue = "false") boolean isYTD,
		Model model) {

		List<AccountingCreditMemoSummaryDetail> summaryDetail = journalEntrySummaryService.getCreditMemoTransactionDetails(id,
			isYTD, CreditMemoRegisterTransaction.CREDIT_MEMO_SUBSCRIPTION_TYPE_IDS,
			true, false, false, false);
		return getJESExportCreditMemoDetail(summaryDetail, "jesExportReceivablesSubscriptionCreditMemos", model, isYTD);
	}

	private CSVView getJESExportCreditMemoDetail(List<AccountingCreditMemoSummaryDetail> summaryDetail, String baseFilename, Model model, boolean isYTD){
		if (summaryDetail != null) {

			getCreditMemoTransactionDetail(summaryDetail, model);
			String filename = String.format("%s%s%s.csv", baseFilename, isYTD ? "YTD" : "",
				DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}

	@RequestMapping(value = "/export_summary_detail/{id}", method = GET)
	public CSVView downloadJES(@PathVariable Long id, Model model) {

		AccountingSummary summary = null;
		try {
			summary = journalEntrySummaryService.findSummary(id);
		} catch (Exception e) {
			logger.error("Error exporting JES Summary detail", e);
		}

		if (summary != null) {

			List<String[]> data = getAccountingSummaryDataForCSV(summary);
			model.addAttribute(CSVView.CSV_MODEL_KEY, data);
			String filename = String.format("jesExport%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);

		}

		return new CSVView();
	}

	private List<String[]> getAccountingSummaryDataForCSV(AccountingSummary summary) {

		List<String[]> data = Lists.newArrayList();
		String[] separator = new String[]{StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY};

		if (summary != null) {

			//Cash Items Title
			data.add(new String[]{"Cash Items", StringUtils.EMPTY, StringUtils.EMPTY});
			data.add(separator);

			//Money In
			data.add(new String[]{"Money In", "Daily", "YTD"});
			data.add(new String[]{"Fast Funds",
				summary.getMoneyInFastFunds().toString(),
				summary.getMoneyInFastFundsHistorical().toString()
			});
			data.add(new String[]{"Subscription Credits - Paid Invoices",
				summary.getMoneyInSubscriptionCreditMemo().toString(),
				summary.getMoneyInSubscriptionCreditMemoHistorical().toString()
			});
			data.add(new String[]{"Professional Service Credits - Paid Invoices",
				summary.getMoneyInProfServicesCreditMemo().toString(),
				summary.getMoneyInProfServicesCreditMemoHistorical().toString()
			});
			data.add(new String[]{"Checks",
				summary.getMoneyInChecks().toString(),
				summary.getMoneyInChecksHistorical().toString()
			});
			data.add(new String[]{"ACH",
				summary.getMoneyInAch().toString(),
				summary.getMoneyInAchHistorical().toString()
			});
			data.add(new String[]{"Wire Transfers",
				summary.getMoneyInWire().toString(),
				summary.getMoneyInWireHistorical().toString()
			});
			data.add(new String[]{"Credit Card Deposits",
				summary.getMoneyInCreditCard().toString(),
				summary.getMoneyInCreditCardHistorical().toString()
			});
			//Separation
			data.add(separator);

			//Money Out
			data.add(new String[]{"Money Out", "Daily", "YTD"});
			data.add(new String[]{"Withdrawals (US)",
				summary.getMoneyOutWithdrawals().toString(),
				summary.getMoneyOutWithdrawalsHistorical().toString()
			});
			//Canadian payments
			data.add(new String[]{"Withdrawals (Non-US)",
				summary.getMoneyOutNonUSAWithdrawals().toString(),
				summary.getMoneyOutNonUSAWithdrawalsHistorical().toString()
			});
			data.add(new String[]{"Withdrawals PayPal",
				summary.getMoneyOutPayPalWithdrawal().toString(),
				summary.getMoneyOutPayPalWithdrawalHistorical().toString()
			});

			data.add(new String[]{"Withdrawals GCC",
				summary.getMoneyOutGCCWithdrawal().toString(),
				summary.getMoneyOutGCCWithdrawalHistorical().toString()
			});

			data.add(new String[]{"VOR Fees --- Old WM Fees",
				summary.getMoneyOutFees().toString(),
				summary.getMoneyOutFeesHistorical().toString()
			});
			data.add(new String[]{"Transaction Fees - Software",
				summary.getMoneyOutTransactionalVorSoftwareFee().toString(),
				summary.getMoneyOutTransactionalVorSoftwareFeeHistorical().toString()
			});
			data.add(new String[]{"Transaction Fees - VOR",
				summary.getMoneyOutTransactionalVorVorFee().toString(),
				summary.getMoneyOutTransactionalVorVorFeeHistorical().toString()
			});
			data.add(new String[]{"Transaction Fees - Software (NVOR)",
				summary.getMoneyOutTransactionalNonVorSoftwareFee().toString(),
				summary.getMoneyOutTransactionalNonVorSoftwareFeeHistorical().toString()
			});
			data.add(new String[]{"Subscription VOR - Software",
				summary.getMoneyOutSubscriptionVorSoftwareFee().toString(),
				summary.getMoneyOutSubscriptionVorSoftwareFeeHistorical().toString()
			});
			data.add(new String[]{"Subscription VOR - VOR Fees",
				summary.getMoneyOutSubscriptionVorVorFee().toString(),
				summary.getMoneyOutSubscriptionVorVorFeeHistorical().toString()
			});
			data.add(new String[]{"Subscription NVOR - Software Fees",
				summary.getMoneyOutSubscriptionNonVorSoftwareFee().toString(),
				summary.getMoneyOutSubscriptionNonVorSoftwareFeeHistorical().toString()
			});
			data.add(new String[]{"Professional Services Fees",
				summary.getMoneyOutProfessionalServiceFee().toString(),
				summary.getMoneyOutProfessionalServiceFeeHistorical().toString()
			});
			data.add(new String[]{"PayPal Fees",
				summary.getMoneyOutPayPalFees().toString(),
				summary.getMoneyOutPayPalFeesHistorical().toString()
			});
			data.add(new String[]{"Credit Card Fees",
				summary.getMoneyOutCreditCardFees().toString(),
				summary.getMoneyOutCreditCardFeesHistorical().toString()
			});
			data.add(new String[]{"Fast Funds Receivable Payments",
				summary.getMoneyOutFastFundsReceivablePayments().toString(),
				summary.getMoneyOutFastFundsReceivablePaymentsHistorical().toString()
			});
			data.add(new String[]{"Fast Funds Fee",
				summary.getMoneyOutFastFundsFee().toString(),
				summary.getMoneyOutFastFundsFeeHistorical().toString()
			});
			data.add(new String[]{"Background Checks",
				summary.getMoneyOutBackgroundChecks().toString(),
				summary.getMoneyOutBackgroundChecksHistorical().toString()
			});
			data.add(new String[]{"Drug Tests",
				summary.getMoneyOutDrugTests().toString(),
				summary.getMoneyOutDrugTestsHistorical().toString()
			});
			data.add(new String[]{"Deposit Return Fees",
				summary.getMoneyOutDepositReturnFee().toString(),
				summary.getMoneyOutDepositReturnFeeHistorical().toString()
			});
			data.add(new String[]{"Withdrawal Return Fees",
				summary.getMoneyOutWithdrawalReturnFee().toString(),
				summary.getMoneyOutWithdrawalReturnFeeHistorical().toString()
			});
			data.add(new String[]{"Late Payment Fees",
				summary.getMoneyOutLatePaymentFee().toString(),
				summary.getMoneyOutLatePaymentFeeHistorical().toString()
			});
			data.add(new String[]{"Miscellaneous Fees",
				summary.getMoneyOutMiscellaneousFee().toString(),
				summary.getMoneyOutMiscellaneousFeeHistorical().toString()
			});

			//Separation
			data.add(separator);

			//MISC Cash
			data.add(new String[]{"MISC Cash", "Daily", "YTD"});
			data.add(new String[]{"Cash Out (Debits)",
				summary.getMoneyOutDebitTransactions().toString(),
				summary.getMoneyOutDebitTransactionsHistorical().toString()
			});
			data.add(new String[]{"Cash In (Credits)",
				summary.getMoneyOutCreditTransactions().toString(),
				summary.getMoneyOutCreditTransactionsHistorical().toString()
			});

			//Separation
			data.add(separator);

			//Other Cash Debits
			data.add(new String[]{"Other Cash Debits", "Daily", "YTD"});
			data.add(new String[]{"ACH Deposit Return",
				summary.getDebitAchDepositReturn().toString(),
				summary.getDebitAchDepositReturnHistorical().toString()});

			data.add(new String[]{"Advance Repayment",
				summary.getDebitAdvanceRepayment().toString(),
				summary.getDebitAdvanceRepaymentHistorical().toString()});

			data.add(new String[]{"Assignment Payment Reversal",
				summary.getDebitAssignmentPaymentReversal().toString(),
				summary.getDebitAssignmentPaymentReversalHistorical().toString()});

			data.add(new String[]{"Credit Card Chargeback",
				summary.getDebitCreditCardChargeback().toString(),
				summary.getDebitCreditCardChargebackHistorical().toString()});

			data.add(new String[]{"Credit Card Return",
				summary.getDebitCreditCardRefund().toString(),
				summary.getDebitCreditCardRefundHistorical().toString()});

			data.add(new String[]{"Reclass From Available to Spend",
				summary.getDebitReclassFromAvailableToSpend().toString(),
				summary.getDebitReclassFromAvailableToSpendHistorical().toString()});

			data.add(new String[]{"Fast Funds Debit",
				summary.getDebitFastFunds().toString(),
				summary.getDebitFastFundsHistorical().toString()});

			data.add(new String[]{"Miscellaneous",
				summary.getDebitMiscellaneous().toString(),
				summary.getDebitMiscellaneousHistorical().toString()});

			data.add(new String[]{"Adjustment",
				summary.getDebitAdjustment().toString(),
				summary.getDebitAdjustmentHistorical().toString()});

			//Separation
			data.add(separator);

			//Other Cash Debits
			data.add(new String[]{"Other Cash Credits", "Daily", "YTD"});
			data.add(new String[]{"Withdrawal Returns",
				summary.getCreditAchWithdrawableReturn().toString(),
				summary.getCreditAchWithdrawableReturnHistorical().toString()});

			data.add(new String[]{"Advance",
				summary.getCreditAdvance().toString(),
				summary.getCreditAdvanceHistorical().toString()});

			data.add(new String[]{"Assignment Payment Reversal",
				summary.getCreditAssignmentPaymentReversal().toString(),
				summary.getCreditAssignmentPaymentReversalHistorical().toString()});

			data.add(new String[]{"Fee Refund - VOR (Software)",
				summary.getCreditFeeRefundVorSoftware().toString(),
				summary.getCreditFeeRefundVorSoftwareHistorical().toString()});

			data.add(new String[]{"Fee Refund - VOR (VOR)",
				summary.getCreditFeeRefundVor().toString(),
				summary.getCreditFeeRefundVorHistorical().toString()});

			data.add(new String[]{"Fee Refund - NVOR",
				summary.getCreditFeeRefundNvor().toString(),
				summary.getCreditFeeRefundNvorHistorical().toString()});

			data.add(new String[]{"Background Check Refund",
				summary.getCreditBackgroundCheckRefund().toString(),
				summary.getCreditBackgroundCheckRefundHistorical().toString()});

			data.add(new String[]{"Drug Test Refund",
				summary.getCreditDrugTestRefund().toString(),
				summary.getCreditDrugTestRefundHistorical().toString()});

			data.add(new String[]{"General Refund",
				summary.getCreditGeneralRefund().toString(),
				summary.getCreditGeneralRefundHistorical().toString()});

			data.add(new String[]{"Marketing Payment",
				summary.getCreditMarketingPayment().toString(),
				summary.getCreditMarketingPaymentHistorical().toString()});

			data.add(new String[]{"Reclass To Available to Withdraw",
				summary.getCreditReclassToAvailableToWithdrawal().toString(),
				summary.getCreditReclassToAvailableToWithdrawalHistorical().toString()});

			data.add(new String[]{"Fast Funds Credit",
				summary.getCreditFastFunds().toString(),
				summary.getCreditFastFundsHistorical().toString()});

			data.add(new String[]{"Fast Funds Fee Refund",
				summary.getCreditFastFundsFeeRefund().toString(),
				summary.getCreditFastFundsFeeRefundHistorical().toString()});

			data.add(new String[]{"Miscellaneous",
				summary.getCreditMiscellaneous().toString(),
				summary.getCreditMiscellaneousHistorical().toString()});

			data.add(new String[]{"Adjustment",
				summary.getCreditAdjustment().toString(),
				summary.getCreditAdjustmentHistorical().toString()});

			//Separation
			data.add(separator);

			//Money On Platform
			data.add(new String[]{"Money On Platform",
				summary.getTotalMoneyOnSystem().toString(),
				summary.getTotalMoneyOnSystemHistorical().toString()
			});

			//Separation
			data.add(separator);

			//Accounting Items Title
			data.add(new String[]{"Accounting Items", StringUtils.EMPTY, StringUtils.EMPTY});
			data.add(separator);

			//Throughput
			data.add(new String[]{"Throughput", "Daily", "YTD"});
			data.add(new String[]{"Transaction VOR",
				summary.getThroughputTransactionalVor().toString(),
				summary.getThroughputTransactionalVorHistorical().toString()
			});
			data.add(new String[]{"Transaction NVOR",
				summary.getThroughputTransactionalNonVor().toString(),
				summary.getThroughputTransactionalNonVorHistorical().toString()
			});
			data.add(new String[]{"Subscription VOR",
				summary.getThroughputSubscriptionVor().toString(),
				summary.getThroughputSubscriptionVorHistorical().toString()
			});
			data.add(new String[]{"Subscription NVOR",
				summary.getThroughputSubscriptionNonVor().toString(),
				summary.getThroughputSubscriptionNonVorHistorical().toString()
			});

			//Separation
			data.add(separator);

			//Revenue
			data.add(new String[]{"Subscription Fees", "Daily", "YTD"});
			data.add(new String[]{"Subscription VOR - Software Fees",
				summary.getRevenueSubscriptionVorSoftwareFee().toString(),
				summary.getRevenueSubscriptionVorSoftwareFeeHistorical().toString()
			});
			data.add(new String[]{"Subscription VOR - VOR Fees",
				summary.getRevenueSubscriptionVorVorFee().toString(),
				summary.getRevenueSubscriptionVorVorFeeHistorical().toString()
			});
			data.add(new String[]{"Subscription NVOR - Software Fees",
				summary.getRevenueSubscriptionNonVorSoftwareFee().toString(),
				summary.getRevenueSubscriptionNonVorSoftwareFeeHistorical().toString()
			});

			data.add(separator);

			//Revenue
			data.add(new String[]{"Subscription Fees - Credit", "Daily", "YTD"});
			data.add(new String[]{"Subscription VOR - Software Fees - Credits",
				summary.getCreditSubscriptionVorSoftwareFee().toString(),
				summary.getCreditSubscriptionVorSoftwareFeeHistorical().toString()
			});
			data.add(new String[]{"Subscription VOR - VOR Fees - Credits",
				summary.getCreditSubscriptionVorVorFee().toString(),
				summary.getCreditSubscriptionVorVorFeeHistorical().toString()
			});
			data.add(new String[]{"Subscription NVOR - Software Fees - Credits",
				summary.getCreditSubscriptionNonVorSoftwareFee().toString(),
				summary.getCreditSubscriptionNonVorSoftwareFeeHistorical().toString()
			});

			//Separation
			data.add(separator);

			//Professional Services
			data.add(new String[]{"Professional Services Fees", "Daily", "YTD"});
			data.add(new String[]{"Professional Services Fees",
				summary.getRevenueProfessionalServiceFee().toString(),
				summary.getRevenueProfessionalServiceFeeHistorical().toString()
			});
			data.add(new String[]{"Professional Services Fees - Credits",
				summary.getCreditProfessionalServiceFee().toString(),
				summary.getCreditProfessionalServiceFeeHistorical().toString()
			});

			//Separation
			data.add(separator);

			//Revenue
			data.add(new String[]{"Other Fees", "Daily", "YTD"});
			data.add(new String[]{"Deposit Return Fees",
				summary.getRevenueDepositReturnFee().toString(),
				summary.getRevenueDepositReturnFeeHistorical().toString()
			});
			data.add(new String[]{"Withdrawal Return Fees",
				summary.getRevenueWithdrawalReturnFee().toString(),
				summary.getRevenueWithdrawalReturnFeeHistorical().toString()
			});
			data.add(new String[]{"Late Payment Fees",
				summary.getRevenueLatePaymentFee().toString(),
				summary.getRevenueLatePaymentFeeHistorical().toString()
			});
			data.add(new String[]{"Miscellaneous Fees",
				summary.getRevenueMiscellaneousFee().toString(),
				summary.getRevenueMiscellaneousFeeHistorical().toString()
			});

			//Separation
			data.add(separator);

			//Deferred Revenue
			data.add(new String[]{"Deferred Subscription Fees", "Daily", "YTD"});
			data.add(new String[]{"Subscription VOR - Software Fees",
				summary.getDefRevenueSubscriptionVorSoftwareFee().toString(),
				summary.getDefRevenueSubscriptionVorSoftwareFeeHistorical().toString()
			});
			data.add(new String[]{"Subscription VOR - VOR Fees",
				summary.getDefRevenueSubscriptionVorVorFee().toString(),
				summary.getDefRevenueSubscriptionVorVorFeeHistorical().toString()
			});
			data.add(new String[]{"Subscription NVOR - Software Fees",
				summary.getDefRevenueSubscriptionNonVorSoftwareFee().toString(),
				summary.getDefRevenueSubscriptionNonVorSoftwareFeeHistorical().toString()
			});
			data.add(new String[]{"Professional Services Fees",
				summary.getDefRevenueProfessionalServiceFee().toString(),
				summary.getDefRevenueProfessionalServiceFeeHistorical().toString()
			});

			//Separation
			data.add(separator);

			//Other
			data.add(new String[]{"Receivables", "Daily", "YTD"});
			data.add(new String[]{"Subscription Fee Receivables",
				summary.getSubscriptionFeeReceivables().toString(),
				summary.getSubscriptionFeeReceivablesHistorical().toString()
			});
			data.add(new String[]{"Subscription Fees Receivables - Credits",
				summary.getSubscriptionCreditMemoReceivables().toString(),
				summary.getSubscriptionCreditMemoReceivablesHistorical().toString()
			});
			data.add(new String[]{"Professional Services Fees Receivable",
				summary.getProfessionalServiceFeeReceivables().toString(),
				summary.getProfessionalServiceFeeReceivablesHistorical().toString()
			});
			data.add(new String[]{"Professional Services Fees Receivables - Credits",
				summary.getProfServicesCreditMemoReceivables().toString(),
				summary.getProfServicesCreditMemoReceivablesHistorical().toString()
			});
			data.add(new String[]{"Other Fees Receivable",
				summary.getAdHocServiceFeeReceivables().toString(),
				summary.getAdHocServiceFeeReceivablesHistorical().toString()
			});
			data.add(new String[]{"Fast Funds Receivables",
				summary.getFastFundsFeeReceivables().toString(),
				summary.getFastFundsFeeReceivablesHistorical().toString()
			});

			//Separation
			data.add(separator);

			//Other
			data.add(new String[]{"Other", "Daily", "YTD"});
			data.add(new String[]{"ACH Verification",
				summary.getMoneyOutAchVerifications().toString(),
				summary.getMoneyOutAchVerificationsHistorical().toString()
			});
		}

		return data;
	}


	@RequestMapping(value = "/create_summary", method = GET)
	public String createSummary() throws Exception {

		AccountingSummary summary = accountingSummaryService.createNewSummary();
		return "redirect:/admin/accounting/summary_detail/" + summary.getId();
	}

	/**
	 * Locked Accounts
	 */
	@RequestMapping(value = "/locked_accounts", method = GET)
	public String lockedAccounts() {
		return "web/pages/admin/accounting/locked_accounts";
	}

	@RequestMapping(value = "/load_locked_accounts", method = GET, produces = APPLICATION_JSON_VALUE)
	public void loadLockedAccounts(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, CompanyAggregatePagination.SORTS.COMPANY_NAME.toString());
		}});

		CompanyAggregatePagination pagination = request.newPagination(CompanyAggregatePagination.class);
		pagination.addFilter(CompanyAggregatePagination.FILTER_KEYS.COMPANY_STATUS.toString(), CompanyStatusType.LOCKED);

		pagination = companyService.findAllCompanies(pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (CompanyAggregate company : pagination.getResults()) {
			String lockedOn = DateUtilities.format("MM/dd/yyyy h:mm a", company.getLockedOn(), getCurrentUser().getTimeZoneId());

			List<String> row = Lists.newArrayList(
				company.getCompanyName(),
				lockedOn
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"company_id", company.getCompanyId()
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}


	/**
	 * List Work Market outstanding (payment pending) service invoices
	 */
	@RequestMapping(value = "/workmarket_invoices")
	public String workMarketOutstangingInvoices(Model model) {

		AggregatesDTO aggregatesDTO = billingService.getAllServiceInvoicesTotalsByStatus();
		model.addAttribute("pendingInvoicesTotal", aggregatesDTO.getTotalForStatus(InvoiceStatusType.PAYMENT_PENDING));
		model.addAttribute("invoiceStatusTypes", wmInvoicesFilters);
		return "web/pages/admin/accounting/workmarket_invoices";
	}

	/**
	 * Build JSON containing all outstanding service invoices (for all companies)
	 *
	 * @param model
	 * @param httpRequest
	 * @throws Exception
	 */
	@RequestMapping(value = "/outstanding_invoices", method = GET, produces = APPLICATION_JSON_VALUE)
	public void search(Model model,
	                   HttpServletRequest httpRequest,
	                   @RequestParam(value = "sStatus", required = false) String status,
	                   @RequestParam(value = "sCompany", required = false) String company,
	                   @RequestParam(value = "sInvoice", required = false) String invoiceNumber) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, ServiceInvoicePagination.SORTS.INVOICE_TYPE.toString());
			put(2, ServiceInvoicePagination.SORTS.INVOICE_NUMBER.toString());
			put(3, ServiceInvoicePagination.SORTS.COMPANY_NAME.toString());
			put(4, ServiceInvoicePagination.SORTS.CREATED_DATE.toString());
			put(5, ServiceInvoicePagination.SORTS.DUE_DATE.toString());
			put(6, ServiceInvoicePagination.SORTS.PAYMENT_DATE.toString());
			put(7, ServiceInvoicePagination.SORTS.INVOICE_AMOUNT.toString());
			put(9, ServiceInvoicePagination.SORTS.INVOICE_STATUS.toString());
		}});

		WorkMarketSummaryInvoicePagination pagination = request.newPagination(WorkMarketSummaryInvoicePagination.class);
		if (InvoiceStatusType.PAID.equals(status) || InvoiceStatusType.PAYMENT_PENDING.equals(status)) {
			pagination.addFilter(ServiceInvoicePagination.FILTER_KEYS.INVOICE_STATUS, status);
		}

		if(StringUtils.isNotEmpty(company)){
			pagination.addFilter(ServiceInvoicePagination.FILTER_KEYS.COMPANY_NAME, company);
		}

		if(StringUtils.isNotEmpty(invoiceNumber)){
			pagination.addFilter(ServiceInvoicePagination.FILTER_KEYS.INVOICE_NUMBER, invoiceNumber);
		}

		pagination = billingService.findAllWorkMarketSummaryInvoices(pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		for (WorkMarketSummaryInvoice invoice : pagination.getResults()) {
			List<String> row = Lists.newArrayList();

			// Invoice type
			row.add(invoice.getType());

			// Invoice name
			row.add(invoice.getDescription());

			// Invoice ID
			row.add(invoice.getInvoiceNumber());

			// Company name
			row.add(invoice.getCompany().getEffectiveName());

			// Issued date
			row.add(sdf.format(invoice.getCreatedOn().getTime()));

			// Due date
			row.add(invoice.getDueDate() != null ? sdf.format(invoice.getDueDate().getTime()) : "-");

			// Payment date
			row.add(invoice.getPaymentDate() != null ? sdf.format(invoice.getPaymentDate().getTime()) : "-");

			// Invoice amount
			row.add(String.valueOf(invoice.getBalance()));

			// Days past due date (in days)
			row.add(String.valueOf(invoice.getDaysPastDue()));

			// Invoice Status
			row.add(invoice.getInvoiceStatusType().toString());

			//Revenue month
			row.add("");

			//Action
			row.add("-");

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", invoice.getId(),
				"company_id", invoice.getCompany().getId(),
				"isCreditMemo", invoice.getType().equals(CreditMemo.CREDIT_MEMO_TYPE) ? true :false,
				"isCreditMemoIssuable", billingService.isCreditMemoIssuable(invoice.getId()) &&
					!creditMemoAuditService.creditMemoAlreadyExisted(invoice.getId())
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	/**
	 * Ad-hoc invoices for subscriptions
	 */
	@RequestMapping(value = "/adhoc_invoices", method = GET)
	public String adhocInvoices(@ModelAttribute("form") AdhocInvoiceForm form, Model model) throws Exception {
		List<String> adhocSubscriptionInvoiceTypeCodes = Lists.newArrayList(SubscriptionInvoiceType.ADHOC_SUBSCRIPTION_INVOICE_TYPE_CODES);
		adhocSubscriptionInvoiceTypeCodes.add("N/A");
		model.addAttribute("subscriptionInvoiceTypeCodes", adhocSubscriptionInvoiceTypeCodes);
		model.addAttribute("noPlanSubscriptionInvoiceTypeCodes", jsonSerializationService.toJson(SubscriptionInvoiceType.NO_PLAN_ADHOC_SUBSCRIPTION_INVOICE_TYPE_CODES));

		List<InvoiceLineItemType> invoiceLineItems = Lists.newArrayList(InvoiceLineItemType.ADHOC_SERVICE_INVOICE_LINE_ITEMS);
		invoiceLineItems.remove(InvoiceLineItemType.SUBSCRIPTION_ADD_ON);
		invoiceLineItems = Lists.newLinkedList(invoiceLineItems);
		((LinkedList)invoiceLineItems).addFirst(InvoiceLineItemType.SUBSCRIPTION_ADD_ON);
		model.addAttribute("invoiceLineItemTypes", invoiceLineItems);

		model.addAttribute("nonSubscriptionInvoiceLineItemTypes", jsonSerializationService.toJson(InvoiceLineItemType.getNonSubscriptionInvoiceLineItemTypeCodes()));

		return "web/pages/admin/accounting/adhoc_invoices";
	}

	@RequestMapping(value = "/subscriptionDetails/{companyId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder getCurrentSubscriptionDetails(@PathVariable("companyId") final Long companyId) {
		Map<String, Object> currentSubscriptionDetails = subscriptionCalculator.getCurrentSubscriptionDetails(companyId);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

		if (currentSubscriptionDetails.containsKey("effectiveDate") && currentSubscriptionDetails.containsKey("endDate")) {
			currentSubscriptionDetails.put("effectiveDate", simpleDateFormat.format(currentSubscriptionDetails.get("effectiveDate")));
			currentSubscriptionDetails.put("endDate", simpleDateFormat.format(currentSubscriptionDetails.get("endDate")));
		}

		if (currentSubscriptionDetails.containsKey("subscriptionConfigurationId")) {
			SubscriptionPaymentPeriod nextNotInvoicedPaymentPeriod = subscriptionService.findNextNotInvoicedSubscriptionPaymentPeriod((long)currentSubscriptionDetails.get("subscriptionConfigurationId"));
			currentSubscriptionDetails.put("nextNotInvoicedPaymentPeriod", simpleDateFormat.format(nextNotInvoicedPaymentPeriod.getPeriodDateRange().getFrom().getTime()));
		}

		return AjaxResponseBuilder.success().addData("subscriptionDetails", currentSubscriptionDetails);
	}

	/**
	 * Create an ad-hoc invoice
	 */
	@RequestMapping(value = "/adhoc_invoices", method = POST)
	public String adhocInvoicesCreate(
		@Valid @ModelAttribute("form") AdhocInvoiceForm form,
		BindingResult bindingResult,
		RedirectAttributes flash,
		Model model) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return "redirect:/admin/accounting/adhoc_invoices";
		}

		for (String error : form.validateLineItems()) {
			messageHelper.addError(bundle, error);
		}

		if (bundle.hasErrors()) {
			return "redirect:/admin/accounting/adhoc_invoices";
		}

		try {
			billingService.issueAdHocInvoice(form.toDTO());
			messageHelper.addSuccess(bundle, "admin.accounting.adhoc_invoices.success");
		} catch (Exception e) {
			logger.error(e.getMessage());
			messageHelper.addError(bundle, "admin.accounting.adhoc_invoices.error");
		}

		return "redirect:/admin/accounting/adhoc_invoices";
	}

	/**
	 * TIN requests
	 */
	@RequestMapping(value = "/download_tin_file/{id}", method = GET)
	public CSVView downloadTin(@PathVariable Long id, Model model) {

		Optional<TaxVerificationRequest> optRequest = taxVerificationService.findTaxEntityValidationRequest(id);
		if (!optRequest.isPresent())
			return new CSVView();

		TaxVerificationRequest tinRequest = optRequest.get();
		List<String[]> data = Lists.newArrayList();
		for (AbstractTaxEntity e : tinRequest.getTaxEntities()) {
			if (!(e instanceof UsaTaxEntity)) {
				logger.error("TIN download process found non-USA tax entity " + e.getId());
				continue;
			}
			/*
			- "1" Employer Identification Number (EIN)
			- "2" represents a Social Security Number (SSN) or Individual Tax Id Number (ITIN) and,
			- "3" represents an unknown TIN type
				see http://www.irs.gov/pub/irs-pdf/p2108a.pdf
			*/
			String tinType = (e.getBusinessFlag() == null) ?
				"3" : !e.getBusinessFlag() ?
				"2" :
				"1";

			String taxNumber = vaultHelper.get(e, "taxNumber", e.getTaxNumberSanitized()).getValue();
			String tinName = StringUtils.left(e.getTaxName().replaceAll("[^ \\p{Alnum}&-]", ""), TIN_EXPORT_CSV_TAX_NAME_MAX_LENGTH);

			data.add(new String[]{
				tinType.trim(),
				taxNumber.trim(),
				tinName.trim(),
				e.getCompany().getId().toString().trim()
			});
		}
		model.addAttribute(CSVView.CSV_MODEL_KEY, data);

		String filename = String.format("tinExport%s.txt", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
		return new CSVView(filename, TIN_EXPORT_CSV_DELIMITER, CSVWriter.NO_QUOTE_CHARACTER);
	}

	@RequestMapping(value = "/set_confirmation_number", method = POST, produces = APPLICATION_JSON_VALUE)
	public
	@ResponseBody AjaxResponseBuilder setConfirmationNumber(
		@RequestParam("confirmation_number") String confirmationNumber,
		@RequestParam("request_id") Long requestId) {

		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (StringUtils.isEmpty(confirmationNumber)) {
			response.setSuccessful(false);
			response.setMessages(Lists.newArrayList(messageHelper.getMessage("admin.accounting.tin.set_confirmation_number.required")));
		} else {
			try {
				taxVerificationService.addConfirmationNumberToTaxEntityValidationRequest(requestId, confirmationNumber);
				response.setSuccessful(true);
			} catch (Exception e) {
				response.setSuccessful(false);
				response.setMessages(Lists.newArrayList(messageHelper.getMessage("admin.accounting.tin.set_confirmation_number.required")));
			}
		}
		return response;
	}

	@RequestMapping(value = "process_tin_file", method = POST, produces = APPLICATION_JSON_VALUE)
	public
	@ResponseBody AjaxResponseBuilder processTinFile(
		@RequestParam("request_id") Long requestId,
		@RequestParam("asset_uuid") String assetUuid) throws Exception {

		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setSuccessful(false)
			.setRedirect("/admin/accounting/nacha?type=tin");

		// first validate the request and the upload
		Upload upload = uploadService.findUploadByUUID(assetUuid);
		if (upload == null) {
			messageHelper.addMessage(response, "admin.accounting.tin.process_tin_file.asset_notfound");
			return response;
		}

		UploadDTO dto = UploadDTO.newDTO(upload);
		dto.setAssociationType(TaxVerificationRequestAssetAssociationType.IRS_MATCH);
		try {
			Asset asset = assetManagementService.addUploadToTaxVerificationRequest(dto, requestId);

			long rowsProcessed = taxVerificationService.validateRequestFromCsv(requestId, asset);

			if (rowsProcessed > 0) {
				messageHelper.addMessage(response, "admin.accounting.tin.process_tin_file.success", rowsProcessed);

				return response.setSuccessful(true);
			} else {
				messageHelper.addMessage(response, "admin.accounting.tin.process_tin_file.no_rows");
			}

		} catch (TaxVerificationException e) {
			messageHelper.addMessage(response, e.getMessage());
		} catch (HostServiceException e) {
			logger.error("Error saving CSV asset: " + e.getMessage(), e);
			messageHelper.addMessage(response, "admin.accounting.tin.process_tin_file.exception");
		}

		return response;
	}

	@RequestMapping(value = "cancel_tin_file", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder cancelTinFile(@RequestParam("request_id") Long requestId) throws Exception {

		AjaxResponseBuilder response = new AjaxResponseBuilder()
			.setSuccessful(false)
			.setRedirect("/admin/accounting/nacha?type=tin");

		try {
			if (taxVerificationService.cancelTaxVerificationRequest(requestId)) {
				messageHelper.addMessage(response, "admin.accounting.tin.cancel_tin_file.success");

				return response.setSuccessful(true);
			} else {
				messageHelper.addMessage(response, "admin.accounting.tin.cancel_tin_file.exception");
			}

		} catch (Exception e) {
			messageHelper.addMessage(response, e.getMessage());
		}
		return response;
	}

	/**
	 * Form 1099-MISC Tab
	 *
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/form_1099", method = GET)
	public String taxForm1099(Model model) {
		final Integer taxYearToGenerate = taxReportService.findLatestPublishedTaxForm1099Report().getTaxYear() + 1;
		final boolean canPublish = taxReportService.canPublishTax1099ReportForYear(taxYearToGenerate);

		model.addAttribute("accountingView", "1099misc");
		model.addAttribute("canPublish", canPublish);
		model.addAttribute("taxYear", taxYearToGenerate);

		return "web/pages/admin/accounting/1099_misc";
	}

	/**
	 * @param set
	 * @return
	 */
	private Map<String, Object> buildJSONreportItem(TaxForm1099Set set) {
		Map<String, Object> item = Maps.newHashMap();
		item.put("id", set.getId());
		item.put("taxYear", set.getTaxYear());
		item.put("status", set.getTaxReportSetStatusType());
		item.put("createdOn", DateUtilities.format("yyyy-MM-dd hh:mm a z", set.getCreatedOn(),
			getCurrentUser().getTimeZoneId()));
		return item;
	}

	/**
	 * Form 1099 JSON
	 */
	@RequestMapping(value = "/form_1099.json", method = GET, produces = APPLICATION_JSON_VALUE)
	public
	@ResponseBody AjaxResponseBuilder taxForm1099JSON() {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		List<TaxForm1099Set> taxForm1099Reports = taxReportService.findAllTaxForm1099Reports();

		List<Map<String, Object>> items = Lists.newArrayList();
		for (TaxForm1099Set set : taxForm1099Reports) {
			items.add(buildJSONreportItem(set));
		}

		return response
			.setSuccessful(true)
			.addData("taxForm1099Reports", items);
	}

	/**
	 * Generate Form 1099 report CSV
	 *
	 * @return
	 */
	@RequestMapping(value = "/form_1099/generate_csv", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder generateForm1099csv() {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			final Integer taxYearToGenerate = taxReportService.findLatestPublishedTaxForm1099Report().getTaxYear() + 1;
			final TaxForm1099Set newSet = taxReportService.generateTaxForm1099Report(taxYearToGenerate);
			response
				.setData(buildJSONreportItem(newSet))
				.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}

		return response;
	}

	/**
	 * Download Form 1099 report CSV
	 *
	 * @return
	 */
	@RequestMapping(value = "/form_1099/download/{id}", method = GET, produces = TEXT_PLAIN_VALUE)
	public CSVView downloadForm1099csv(@PathVariable("id") long id, Model model) {
		final List<TaxForm1099> tax1099Forms = taxReportService.findAllTaxForm1099ForReportGeneration(id);
		final TaxForm1099Set taxForm1099Set = taxReportService.findTaxForm1099Set(id);

		List<String[]> rows = Lists.newArrayList();

		final String[] header = {"TIN", "First Name", "Last Name", "Address", "City", "State",
			"Postal Code", "Country", "Amount"};
		rows.add(header);

		final Map<Long, String> taxIdToTaxNumberMap = getTaxIdToTaxNumberMapFromVault(tax1099Forms);

		for (TaxForm1099 taxForm : tax1099Forms) {
			AbstractTaxEntity taxEntity = taxForm.getTaxEntity();
			taxEntity.setTaxNumber(taxIdToTaxNumberMap.get(taxEntity.getId()));

			List<String> data = Lists.newArrayList(
				taxEntity.getFormattedTaxNumber(),
				(taxForm.getBusinessFlag() ? StringUtils.EMPTY : formatFirstName(taxForm.getFirstName())),
				formatLastName(taxForm.getLastName()),
				formatAddress(taxForm.getAddress()),
				formatCity(taxForm.getCity()),
				upperCase(taxForm.getState()),
				taxForm.getPostalCode(),
				upperCase(taxForm.getCountry()),
				taxForm.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString()
			);
			rows.add(data.toArray(new String[0]));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format("taxform-report-%s.csv", String.valueOf(taxForm1099Set.getTaxYear())));

		return view;
	}

	private String formatCity(String city) {
		return upperCase(remove(remove(city, "'"), ","));
	}

	private String formatAddress(String address) {
		return truncate(upperCase(remove(remove(address, "'"), ",")), 30);
	}

	private String formatFirstName(String firstName) {
		return truncate(upperCase(remove(remove(firstName, "'"), ",")), 14);
	}

	private String formatLastName(String lastName) {
		return truncate(upperCase(remove(remove(lastName, "'"), ",")), 18);
	}

	private <T extends AbstractTaxReport> List<AbstractTaxEntity> mapToTaxEntity(final List<T> taxReports) {
		List<AbstractTaxEntity> taxEntities = new ArrayList<>();

		for (AbstractTaxReport form : taxReports) {
			taxEntities.add(form.getTaxEntity());
		}

		return taxEntities;
	}

	private <T extends AbstractTaxReport> Map<Long, String> getTaxIdToTaxNumberMapFromVault(final List<T> taxReports) {
		final List<AbstractTaxEntity> taxEntities = mapToTaxEntity(taxReports);

		final Map<Long, String> taxIdToTaxNumberMap =
			vaultHelper.mapEntityIdToFieldValue(taxEntities, AbstractTaxEntity.class, "taxNumber");

		return taxIdToTaxNumberMap;
	}

	/**
	 * Delete Form 1099 report CSV
	 *
	 * @return
	 */
	@RequestMapping(value = "/form_1099/delete/{id}", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deleteForm1099csv(@PathVariable("id") long id) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			taxReportService.deleteTaxForm1099Report(id);
			response.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}

		return response;
	}

	/**
	 * Publish Form 1099 report CSV
	 *
	 * @return
	 */
	@RequestMapping(value = "/form_1099/publish/{id}", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder publishForm1099csv(@PathVariable("id") long id) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			taxReportService.publishTaxForm1099Report(id);
			response.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}
		return response;
	}

	/**
	 * Earnings Report Tab
	 *
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/earnings", method = GET)
	public String taxEarningsReport(Model model) {
		final Integer taxYearToGenerate = taxReportService.findLatestPublishedEarningReport().getTaxYear() + 1;
		final boolean canPublish = taxReportService.canPublishEarningReportForYear(taxYearToGenerate);

		model.addAttribute("accountingView", "earnings");
		model.addAttribute("canPublish", canPublish);
		model.addAttribute("taxYear", taxYearToGenerate);

		return "web/pages/admin/accounting/earnings";
	}

	/**
	 * @param set
	 * @return
	 */
	private Map<String, Object> buildJSONreportItem(EarningReportSet set) {
		Map<String, Object> item = Maps.newHashMap();
		item.put("id", set.getId());
		item.put("taxYear", set.getTaxYear());
		item.put("status", set.getTaxReportSetStatusType());
		item.put("createdOn", DateUtilities.format("yyyy-MM-dd hh:mm a z", set.getCreatedOn(), getCurrentUser().getTimeZoneId()));

		return item;
	}

	/**
	 * Earnings Report JSON
	 */
	@RequestMapping(value = "/earnings.json", method = GET, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder earningsReportJSON() {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		List<EarningReportSet> earningReports = taxReportService.findAllEarningReportReports();

		List<Map<String, Object>> items = Lists.newArrayList();
		for (EarningReportSet set : earningReports) {
			items.add(buildJSONreportItem(set));
		}

		return response
			.setSuccessful(true)
			.addData("earningReports", items);
	}

	/**
	 * Generate Earnings report CSV
	 *
	 * @return
	 */
	@RequestMapping(value = "/earnings/generate_csv", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder generateEarningsReportcsv() {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			final Integer taxYearToGenerate = taxReportService.findLatestPublishedEarningReport().getTaxYear() + 1;
			final EarningReportSet newSet = taxReportService.generateEarningsReport(taxYearToGenerate);
			response
				.setData(buildJSONreportItem(newSet))
				.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}

		return response;
	}

	/**
	 * Download Earnings report CSV
	 *
	 * @return
	 */
	@RequestMapping(value = "/earnings/download/{id}", method = GET, produces = TEXT_PLAIN_VALUE)
	public CSVView downloadEarningsReportcsv(@PathVariable("id") long id, Model model) {
		final List<EarningReport> earningReports = taxReportService.findAllEarningReportForReportGeneration(id);
		final EarningReportSet earningReportSet = taxReportService.findEarningReportSet(id);
		List<String[]> rows = Lists.newArrayList();

		final String[] header = {"First Name", "Last Name", "Address", "City", "State",
			"Postal Code", "Country", "TIN", "Gross Earnings", "Expenses", "Net Earnings"};
		rows.add(header);

		final Map<Long, String> taxIdToTaxNumberMap = getTaxIdToTaxNumberMapFromVault(earningReports);

		for (EarningReport earningReport : earningReports) {
			AbstractTaxEntity taxEntity = earningReport.getTaxEntity();
			taxEntity.setTaxNumber(taxIdToTaxNumberMap.get(taxEntity.getId()));

			List<String> data = Lists.newArrayList(
				(earningReport.getBusinessFlag() ? StringUtils.EMPTY : formatFirstName(earningReport.getFirstName())),
				formatLastName(earningReport.getLastName()),
				formatAddress(earningReport.getAddress()),
				formatCity(earningReport.getCity()),
				upperCase(earningReport.getState()),
				earningReport.getPostalCode(),
				upperCase(earningReport.getCountry()),
				taxEntity.getFormattedTaxNumber(),
				earningReport.getEarnings().setScale(2, RoundingMode.HALF_UP).toPlainString(),
				earningReport.getExpenses().setScale(2, RoundingMode.HALF_UP).toPlainString(),
				(earningReport.getEarnings().add(earningReport.getExpenses())).setScale(2, RoundingMode.HALF_UP).toPlainString()
			);
			rows.add(data.toArray(new String[0]));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format("earnings-report-%s.csv", String.valueOf(earningReportSet.getTaxYear())));

		return view;
	}

	/**
	 * Delete Earnings report CSV
	 *
	 * @return
	 */
	@RequestMapping(value = "/earnings/delete/{id}", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deleteEarningsReportcsv(@PathVariable("id") long id) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			taxReportService.deleteEarningReport(id);
			response.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}

		return response;
	}

	/**
	 * Publish Earnings report CSV
	 *
	 * @return
	 */
	@RequestMapping(value = "/earnings/publish/{id}", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder publishEarningsReportcsv(@PathVariable("id") long id) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			taxReportService.publishEarningReport(id);
			response.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}
		return response;
	}

	@RequestMapping(value = "/non_vor_tax", method = GET)
	public String nonVorTax(Model model) {
		final Integer taxYearToGenerate = taxReportService.findLatestPublishedEarningDetailReport().getTaxYear() + 1;
		final boolean canPublish = taxReportService.canPublishEarningDetailReportForYear(taxYearToGenerate);

		model.addAttribute("accountingView", "earningsDetail");
		model.addAttribute("canPublish", canPublish);
		model.addAttribute("taxYear", taxYearToGenerate);

		return "web/pages/admin/accounting/non_vor_tax";
	}

	@RequestMapping(value = "/earnings_detail.json", method = GET, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder earningsDetailReportJSON() {

		AjaxResponseBuilder response = new AjaxResponseBuilder();

		List<EarningDetailReportSet> earningDetailReports = taxReportService.findAllEarningDetailReports();

		List<Map<String, Object>> items = Lists.newArrayList();
		for (EarningDetailReportSet set : earningDetailReports) {
			items.add(buildJSONreportItem(set));
		}

		return response
			.setSuccessful(true)
			.addData("earningDetailReports", items);
	}

	@RequestMapping(value = "/earnings_detail/generate_csv", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder generateEarningsDetailReportcsv() {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			final Integer taxYearToGenerate = taxReportService.findLatestPublishedEarningDetailReport().getTaxYear() + 1;
			final EarningDetailReportSet newSet = taxReportService.generateEarningsDetailReport(taxYearToGenerate);
			response
				.setData(buildJSONreportItem(newSet))
				.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}

		return response;
	}

	private Map<String, Object> buildJSONreportItem(EarningDetailReportSet set) {
		Map<String, Object> item = Maps.newHashMap();
		item.put("id", set.getId());
		item.put("taxYear", set.getTaxYear());
		item.put("status", set.getTaxReportSetStatusType());
		item.put("createdOn", DateUtilities.format("yyyy-MM-dd hh:mm a z", set.getCreatedOn(), getCurrentUser().getTimeZoneId()));

		return item;
	}


	@RequestMapping(value = "/earnings_detail/download/{id}", method = GET, produces = TEXT_PLAIN_VALUE)
	public CSVView downloadEarningsDetailReportcsv(@PathVariable("id") long id, Model model) {
		final List<EarningDetailReport> earningDetailReports =
			taxReportService.findAllEarningDetailReportForReportGeneration(id);
		final EarningDetailReportSet earningDetailReportSet = taxReportService.findEarningDetailReportSet(id);
		List<String[]> rows = Lists.newArrayList();

		final String[] header = {"Resource CompanyId", "TIN", "First Name", "Last Name", "Address", "City", "State",
			"Postal Code", "Country", "Gross Earnings", "Expenses", "Net Earnings", "Buyer CompanyId"};
		rows.add(header);

		final Map<Long, String> taxIdToTaxNumberMap = getTaxIdToTaxNumberMapFromVault(earningDetailReports);

		for (EarningDetailReport earningDetailReport : earningDetailReports) {
			AbstractTaxEntity taxEntity = earningDetailReport.getTaxEntity();
			taxEntity.setTaxNumber(taxIdToTaxNumberMap.get(taxEntity.getId()));

			if (earningDetailReport.getBuyerCompanyId() != null) {
				List<String> data = Lists.newArrayList(
					earningDetailReport.getCompanyId().toString(),
					taxEntity.getFormattedTaxNumber(),
					(earningDetailReport.getBusinessFlag() ? StringUtils.EMPTY : formatFirstName(earningDetailReport.getFirstName())),
					formatLastName(earningDetailReport.getLastName()),
					formatAddress(earningDetailReport.getAddress()),
					formatCity(earningDetailReport.getCity()),
					upperCase(earningDetailReport.getState()),
					earningDetailReport.getPostalCode(),
					upperCase(earningDetailReport.getCountry()),
					earningDetailReport.getEarnings().setScale(2, RoundingMode.HALF_UP).toPlainString(),
					earningDetailReport.getExpenses().setScale(2, RoundingMode.HALF_UP).toPlainString(),
					(earningDetailReport.getEarnings().add(earningDetailReport.getExpenses())).setScale(2, RoundingMode.HALF_UP).toPlainString(),
					earningDetailReport.getBuyerCompanyId().toString()
				);

				rows.add(data.toArray(new String[0]));
			}
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format("earnings-detail-report-%s.csv", String.valueOf(earningDetailReportSet.getTaxYear())));

		return view;
	}

	@RequestMapping(value = "/earnings_detail/delete/{id}", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder deleteEarningsDetailReportcsv(@PathVariable("id") long id) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			taxReportService.deleteEarningDetailReport(id);
			response.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}

		return response;
	}

	@RequestMapping(value = "/earnings_detail/publish/{id}", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder publishEarningsDetailReportcsv(@PathVariable("id") long id) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			taxReportService.publishEarningDetailReport(id);
			response.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}
		return response;
	}

	@RequestMapping(value = "/tax_service_detail", method = GET)
	public String taxServiceDetail(Model model) {
		final Integer taxYearToGenerate = taxReportService.findLatestPublishedTaxServiceReport().getTaxYear() + 1;
		final boolean canPublish = taxReportService.canPublishTaxServiceDetailReportForYear(taxYearToGenerate);

		model.addAttribute("accountingView", "taxServiceDetail");
		model.addAttribute("canPublish", canPublish);
		model.addAttribute("taxYear", taxYearToGenerate);

		return "web/pages/admin/accounting/tax_service_detail";
	}

	@RequestMapping(value = "/tax_service_detail.json", method = GET, produces = APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	AjaxResponseBuilder taxServiceDetailReportJSON() {

		AjaxResponseBuilder response = new AjaxResponseBuilder();

		List<TaxServiceReportSet> taxServiceDetailReports = taxReportService.findAllTaxServiceDetailReports();

		List<Map<String, Object>> items = Lists.newArrayList();
		for (TaxServiceReportSet set : taxServiceDetailReports) {
			items.add(buildJSONreportItem(set));
		}

		return response
			.setSuccessful(true)
			.addData("taxServiceDetailReports", items);
	}

	@RequestMapping(value = "/tax_service_detail/generate_csv", method = POST, produces = APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	AjaxResponseBuilder generateTaxServiceDetailReportcsv() {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			final Integer taxYearToGenerate = taxReportService.findLatestPublishedTaxServiceReport().getTaxYear() + 1;
			final TaxServiceReportSet newSet = taxReportService.generateTaxServiceDetailReport(taxYearToGenerate);
			response
				.setData(buildJSONreportItem(newSet))
				.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}

		return response;
	}

	private Map<String, Object> buildJSONreportItem(TaxServiceReportSet set) {
		Map<String, Object> item = Maps.newHashMap();
		item.put("id", set.getId());
		item.put("taxYear", set.getTaxYear());
		item.put("status", set.getTaxReportSetStatusType());
		item.put("createdOn", DateUtilities.format("yyyy-MM-dd hh:mm a z", set.getCreatedOn(), getCurrentUser().getTimeZoneId()));

		return item;
	}


	@RequestMapping(value = "/tax_service_detail/download/{id}", method = GET, produces = TEXT_PLAIN_VALUE)
	public CSVView downloadTaxServiceDetailReportcsv(@PathVariable("id") long id, Model model) {
		final List<TaxServiceReport> taxServiceReports = taxReportService.findAllTaxServiceReportForReportGeneration(id);
		final TaxServiceReportSet taxServiceReportSet = taxReportService.findTaxServiceDetailReportSet(id);
		List<String[]> rows = Lists.newArrayList();

		final String[] header = {"Resource CompanyId", "TIN", "First Name", "Last Name", "Address", "City", "State",
			"Postal Code", "Country", "Gross Earnings", "Expenses", "Net Earnings", "Buyer CompanyId"};
		rows.add(header);

		final Map<Long, String> taxIdToTaxNumberMap = getTaxIdToTaxNumberMapFromVault(taxServiceReports);

		for (TaxServiceReport taxServiceReport : taxServiceReports) {
			AbstractTaxEntity taxEntity = taxServiceReport.getTaxEntity();
			taxEntity.setTaxNumber(taxIdToTaxNumberMap.get(taxEntity.getId()));

			if (taxServiceReport.getBuyerCompanyId() != null) {
				List<String> data = Lists.newArrayList(
					taxServiceReport.getCompanyId().toString(),
					taxEntity.getFormattedTaxNumber(),
					(taxServiceReport.getBusinessFlag() ? StringUtils.EMPTY : formatFirstName(taxServiceReport.getFirstName())),
					formatLastName(taxServiceReport.getLastName()),
					formatAddress(taxServiceReport.getAddress()),
					formatCity(taxServiceReport.getCity()),
					upperCase(taxServiceReport.getState()),
					taxServiceReport.getPostalCode(),
					upperCase(taxServiceReport.getCountry()),
					taxServiceReport.getEarnings().setScale(2, RoundingMode.HALF_UP).toPlainString(),
					taxServiceReport.getExpenses().setScale(2, RoundingMode.HALF_UP).toPlainString(),
					(taxServiceReport.getEarnings().add(taxServiceReport.getExpenses())).setScale(2, RoundingMode.HALF_UP).toPlainString(),
					taxServiceReport.getBuyerCompanyId().toString()
				);
				rows.add(data.toArray(new String[0]));
			}
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format("tax-service-detail-report-%s.csv", String.valueOf(taxServiceReportSet.getTaxYear())));

		return view;
	}

	@RequestMapping(value = "/tax_service_detail/delete/{id}", method = POST, produces = APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	AjaxResponseBuilder deleteTaxServiceDetailReportcsv(@PathVariable("id") long id) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			taxReportService.deleteTaxServiceDetailReport(id);
			response.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}

		return response;
	}

	@RequestMapping(value = "/tax_service_detail/publish/{id}", method = POST, produces = APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	AjaxResponseBuilder publishTaxServiceDetailReportcsv(@PathVariable("id") long id) {
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		try {
			taxReportService.publishTaxServiceDetailReport(id);
			response.setSuccessful(true);
		} catch (Exception ex) {
			logger.error("", ex);
			response.setSuccessful(false);
		}
		return response;
	}

	//Credit Memo
	@RequestMapping(value = "/credit_memo", method = GET)
	public String creditMemo(@RequestParam("invoiceId") String id,
	                         @ModelAttribute("form") CreditMemoForm form,
	                         Model model) throws Exception {

		model.addAttribute("reasons", CreditMemoReasons.CREDITMEMO_REASONS);

		if(!StringUtil.isNullOrEmpty(id)){
			AbstractInvoice adhocInvoice = billingService.findInvoiceById(Long.valueOf(id));
			if(adhocInvoice != null) {
				form.setRefInvoiceId(String.valueOf(adhocInvoice.getId()));
				model.addAttribute("invoiceRef", adhocInvoice);
				return "web/pages/admin/accounting/credit_memo";
			}
		}
		return "redirect:web/pages/admin/accounting/workmarket_invoices";
	}

	@RequestMapping(value = "/credit_memo", method = POST)
	public String creditMemoCreate(@Valid @ModelAttribute("form") CreditMemoForm form,
	                               BindingResult bindingResult,
	                               RedirectAttributes flash,
	                               Model model) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		AbstractServiceInvoice adhocInvoice = billingService.findInvoiceById(Long.valueOf(form.getRefInvoiceId()));
		model.addAttribute("invoiceRef", adhocInvoice);
		model.addAttribute("reasons", CreditMemoReasons.CREDITMEMO_REASONS);

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(bundle, bindingResult);
			return "web/pages/admin/accounting/credit_memo";
		}
		try {
			AbstractServiceInvoice creditMemo = billingService.issueCreditMemo(adhocInvoice.getId());
			CreditMemoAudit creditMemoAudit = creditMemoAuditService.findByInvoiceId(creditMemo.getId());
			creditMemoAudit.setReasonId(Integer.valueOf(form.getReason()));
			creditMemoAudit.setNote(form.getNote());
			creditMemoAuditService.saveOrUpdate(creditMemoAudit);

			messageHelper.addSuccess(bundle, "admin.accounting.credit_memo.success");
		} catch (Exception e) {
			logger.error(e.getMessage());
			messageHelper.addError(bundle, "admin.accounting.credit_memo.error");
		}

		return "redirect:/admin/accounting/workmarket_invoices";
	}

	@RequestMapping(value = "/export_offline/{id}", method = GET)
	public CSVView downloadOfflineDetail(
		@PathVariable Long id,
		@RequestParam(value = "ytd", required = false, defaultValue = "false") boolean isYTD,
		@RequestParam(value = "subscription", required = false, defaultValue = "false") boolean isSubscription,
		@RequestParam(value = "vor", required = false, defaultValue = "false") boolean isVOR,
		Model model) {

		List<AccountingSummaryDetail> summaryDetail = null;
		try {
			summaryDetail = journalEntrySummaryService.getOfflinePaymentDetails(id, isSubscription, isVOR, isYTD);
		} catch (Exception e) {
			logger.error("Error fetching JES Detail summary CSV: ", e);
		}

		if (summaryDetail != null) {

			getSummaryDetail(summaryDetail, model);
			String filename = String.format("jesExportDetail%s.csv", DateUtilities.formatCalendar_MMDDYY(Calendar.getInstance()));
			return new CSVView(filename, JES_EXPORT_CSV_DELIMITER, CSVWriter.DEFAULT_QUOTE_CHARACTER);
		}
		return new CSVView();
	}
}
