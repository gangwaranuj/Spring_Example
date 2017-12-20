package com.workmarket.domains.payments.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.common.template.pdf.PDFTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionConfigurationDAO;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateFilter;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.RoleType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.AccountTransactionReportRowPagination;
import com.workmarket.domains.model.account.CreditMemoTransaction;
import com.workmarket.domains.model.account.FastFundsReceivableCommitment;
import com.workmarket.domains.model.account.InvoicePaymentTransaction;
import com.workmarket.domains.model.account.ServiceTransaction;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.account.payment.AccountingProcessTime;
import com.workmarket.domains.model.account.payment.BiweeklyPaymentDays;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.payment.PaymentCycle;
import com.workmarket.domains.model.account.payment.PaymentMethod;
import com.workmarket.domains.model.account.payment.PaymentTermsDays;
import com.workmarket.domains.model.account.pricing.PaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriodType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.CreditMemo;
import com.workmarket.domains.model.invoice.CreditMemoAudit;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceCollection;
import com.workmarket.domains.model.invoice.InvoiceDueLog;
import com.workmarket.domains.model.invoice.InvoicePagination;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.PaymentFulfillmentStatusType;
import com.workmarket.domains.model.invoice.ServiceInvoicePagination;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.StatementPagination;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.invoice.SubscriptionInvoiceType;
import com.workmarket.domains.model.invoice.WMInvoiceNumberType;
import com.workmarket.domains.model.invoice.WorkMarketSummaryInvoicePagination;
import com.workmarket.domains.model.invoice.item.CreditMemoIssuableInvoiceLineItem;
import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.domains.payments.dao.AccountStatementDetailDAO;
import com.workmarket.domains.payments.dao.CreditMemoAuditDAO;
import com.workmarket.domains.payments.dao.CreditMemoDAO;
import com.workmarket.domains.payments.dao.FastFundsReceivableCommitmentDAO;
import com.workmarket.domains.payments.dao.InvoiceDAO;
import com.workmarket.domains.payments.dao.InvoiceDueLogDAO;
import com.workmarket.domains.payments.dao.InvoiceLineItemDAO;
import com.workmarket.domains.payments.dao.PaymentConfigurationDAO;
import com.workmarket.domains.payments.dao.PaymentPeriodDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.payments.dao.ServiceInvoiceDAO;
import com.workmarket.domains.payments.dao.StatementDAO;
import com.workmarket.domains.payments.dao.WorkMarketSummaryInvoiceDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkWorkResourceAccountRegister;
import com.workmarket.domains.work.model.project.ProjectInvoiceBundle;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.dto.AggregatesDTO;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.InvoicePaymentHelper;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.InvoiceLineItemFactory;
import com.workmarket.service.business.account.SubscriptionCalculator;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.accountregister.RegisterTransactionExecutor;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionExecutableFactory;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.service.business.dto.PaymentConfigurationDTO;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.service.business.dto.account.PastDueCompaniesDTO;
import com.workmarket.service.business.dto.invoice.InvoiceDTO;
import com.workmarket.service.business.dto.invoice.InvoiceLineItemDTO;
import com.workmarket.service.business.dto.invoice.InvoiceSummaryDTO;
import com.workmarket.service.business.event.CompanyDueInvoicesEvent;
import com.workmarket.service.business.event.InvoicesDownloadedEvent;
import com.workmarket.service.business.event.UnlockCompanyEvent;
import com.workmarket.service.business.event.work.WorkInvoiceGenerateEvent;
import com.workmarket.service.business.event.work.WorkInvoiceSendType;
import com.workmarket.service.business.template.TemplateService;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.payment.InvoiceAlreadyPaidException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.service.infra.business.wrapper.FastFundInvoiceResponse;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.infra.status.FastFundInvoiceStatus;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static com.workmarket.utility.NumberUtilities.nullSafeAbs;

@SuppressWarnings("unchecked")
@Service
public class BillingServiceImpl implements BillingService {

	private static final int ONE_DAY_OFFSET = 1;

	private static final Log logger = LogFactory.getLog(BillingServiceImpl.class);

	@Autowired private WorkService workService;
	@Autowired private UserService userService;
	@Autowired private InvoiceDAO invoiceDAO;
	@Autowired private ServiceInvoiceDAO serviceInvoiceDAO;
	@Autowired private WorkMarketSummaryInvoiceDAO wmSummaryInvoiceDAO;
	@Autowired private StatementDAO statementDAO;
	@Autowired private WorkStatusService workStatusService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterServicePrefund;
	@Autowired @Qualifier("accountRegisterServicePaymentTermsImpl")
	private AccountRegisterService accountRegisterServicePaymentTerms;
	@Autowired private NotificationDispatcher notificationDispatcher;
	@Autowired private EmailTemplateFactory emailTemplateFactory;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private CompanyService companyService;
	@Autowired private PaymentConfigurationDAO paymentConfigurationDAO;
	@Autowired private AccountStatementDetailDAO accountStatementDetailDAO;
	@Autowired private PDFTemplateFactory PDFTemplateFactory;
	@Autowired private TemplateService templateService;
	@Autowired private InvoiceLineItemFactory invoiceLineItemFactory;
	@Autowired private InvoiceLineItemDAO invoiceLineItemDAO;
	@Autowired private LookupEntityDAO lookupEntityDAO;
	@Autowired private PaymentPeriodDAO paymentPeriodDAO;
	@Autowired private InvoiceDueLogDAO invoiceDueLogDAO;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private InvoicePaymentHelper invoicePaymentHelper;
	@Autowired private SubscriptionService subscriptionService;
	@Autowired private EventRouter eventRouter;
	@Autowired private PricingService pricingService;
	@Autowired private FastFundsReceivableCommitmentDAO fastFundsReceivableCommitmentDAO;
	@Autowired private CreditMemoDAO creditMemoDAO;
	@Autowired private CreditMemoAuditDAO creditMemoAuditDAO;
	@Autowired protected RegisterTransactionExecutableFactory registerTransactionExecutableFactory;
	@Autowired protected RegisterTransactionDAO registerTransactionDAO;
	@Autowired protected SubscriptionCalculator subscriptionCalculator;
	@Autowired protected SubscriptionConfigurationDAO subscriptionConfigurationDAO;
	@Autowired private UserRoleService userRoleService;
	@Autowired private WorkResourceService workResourceService;

	private static final String DATE_FORMAT = "EEE, d MMM yyyy";

	// Fast Funds Constants
	private static final BigDecimal FAST_FUNDS_FEE_PERCENTAGE = new BigDecimal(0.075);
	private static final int FAST_FUNDS_AVAILABILITY_THRESHOLD_HOURS = 24;
	private static final Long TELAID_COMPANY_ID = 8298L;
	private static final BigDecimal TELAID_FAST_FUNDS_FEE_PERCENTAGE = new BigDecimal(0.05);


	@Override
	public Integer countAllDueWorkByCompany(long companyId) {
		return workService.countAllDueWorkByCompany(companyId);
	}

	@Override
	public Invoice generateInvoiceForWork(Work work) {
		Assert.notNull(work);
		Assert.isNull(work.getInvoice(), "Invoice has been created already");
		Assert.notNull(work.getFulfillmentStrategy(), "Assignment with no FulfillmentStrategy, workStatus:" + (work.getWorkStatusType() != null ? work.getWorkStatusType().getCode() : "unknown"));
		Assert.isTrue(work.isPaymentPending() || work.isPaid() || work.isClosed(), "Invalid assignment's status");
		BigDecimal invoiceBalance = BigDecimal.ZERO;

		Calendar invoiceDueDate = null;
		if (work.getDueOn() != null) {
			invoiceDueDate = (Calendar) work.getDueOn().clone();
		}

		if (work.getFulfillmentStrategy() != null) {
			invoiceBalance = work.getFulfillmentStrategy().getBuyerTotalCost();
		}

		WorkResource resource = workService.findActiveWorkResource(work.getId());
		Assert.notNull(resource);

		Invoice invoice = new Invoice();
		invoice.setCompany(work.getCompany());
		invoice.setBalance(invoiceBalance);
		invoice.setRemainingBalance(invoiceBalance);
		invoice.setDueDate(invoiceDueDate);
		invoice.setActiveWorkResourceId(resource.getId());
		invoice.setWorkPrice(work.getFulfillmentStrategy().getWorkPrice());
		invoice.setWorkBuyerFee(work.getFulfillmentStrategy().getBuyerFee());

		Calendar fastFundedOn = invoiceDAO.findFastFundedOnDateForWorkResource(resource.getId());

		if (fastFundedOn != null) {
			invoice.setFastFundedOn(fastFundedOn);
			FastFundsReceivableCommitment fastFundsReceivableCommitment = fastFundsReceivableCommitmentDAO.findCommitmentByWorkId(work.getId());
			BigDecimal workPrice = work.getFulfillmentStrategy().getWorkPrice();
			if (fastFundsReceivableCommitment != null && workPrice.compareTo(nullSafeAbs(fastFundsReceivableCommitment.getAmount())) != 0) {
				fastFundsReceivableCommitment.setAmount(workPrice.negate());
				fastFundsReceivableCommitmentDAO.saveOrUpdate(fastFundsReceivableCommitment);
			}
		}

		if (work.isPaid()) {
			invoice.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.PAID));
			invoice.setPaymentFulfillmentStatusType(new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.FULFILLED));
			invoice.setRemainingBalance(BigDecimal.ZERO);
		}
		Company company = work.getCompany();

		// We're generating this random number to save the invoice for the first time (Invoice Number is a required field)
		// After the first save, we'll use the record id to save the proper "Invoice Number"
		int randomNumber = RandomUtilities.nextIntInRangeWithSeed(1, 1000000, System.currentTimeMillis());
		String invoiceNumber = String.format("%05d", randomNumber);
		invoice.setInvoiceNumber(invoiceNumber);
		invoice.setDescription("Invoice #" + invoiceNumber);
		invoiceDAO.saveOrUpdate(invoice);

		Long invoiceId = invoice.getId();
		invoiceNumber = String.format("%s-%05d", company.getCompanyNumber(), invoiceId);
		invoice.setInvoiceNumber(invoiceNumber);
		invoice.setDescription("Invoice #" + invoiceNumber);
		logger.debug("Saving invoice id " + invoiceNumber);
		invoiceDAO.saveOrUpdate(invoice);

		work.setInvoice(invoice);

		return invoice;
	}

	@Override
	public InvoiceSummary saveInvoiceSummary(InvoiceSummaryDTO invoiceSummaryDTO) throws IllegalArgumentException {
		return saveInvoiceSummary(new InvoiceSummary(), invoiceSummaryDTO);
	}

	private InvoiceCollection saveInvoiceCollection(InvoiceSummaryDTO invoiceSummaryDTO) throws IllegalArgumentException {
		return saveInvoiceSummary(new InvoiceCollection(), invoiceSummaryDTO);
	}

	<T extends InvoiceSummary> T saveInvoiceSummary(T invoiceSummary, InvoiceSummaryDTO invoiceSummaryDTO) {
		User user = authenticationService.getCurrentUser();
		Assert.notNull(user, "Unable to find user");
		Assert.notNull(invoiceSummaryDTO.getInvoicesIds(), "Empty invoice ids");
		Assert.isTrue(!CollectionUtils.isEmpty(invoiceSummaryDTO.getInvoicesIds()), "Empty invoice ids");
		List<Invoice> invoiceList = invoiceDAO.get(invoiceSummaryDTO.getInvoicesIds());
		Assert.isTrue(!CollectionUtils.isEmpty(invoiceList), "Empty invoice ids");

		if (invoiceSummaryDTO.getInvoiceSummaryId() != null) {
			invoiceSummary = (T) invoiceDAO.findInvoiceById(invoiceSummaryDTO.getInvoiceSummaryId());
			validateInvoice(invoiceSummary);
			Assert.state(invoiceSummary.isEditable(), "Invoice is not editable");
		} else {
			if (invoiceSummary instanceof InvoiceCollection) {
				invoiceSummary.setDescription("Invoice payment collection");
			} else {
				Assert.isTrue(!user.getCompany().hasStatementsEnabled(), "Can't create invoice bundle if statements are ON");
				invoiceSummary.setInvoiceNumber(companyService.getNextInvoiceSummaryNumber(user.getCompany()));
				invoiceSummary.setDescription(invoiceSummaryDTO.getDescription());
			}
			invoiceSummary.setCompany(user.getCompany());
		}
		invoiceDAO.saveOrUpdate(invoiceSummary);

		return addInvoicesToInvoiceSummary(invoiceSummary.getId(), invoiceList);
	}

	@Override
	public <T extends AbstractInvoice> T findInvoiceById(long invoiceId) {
		T invoice = (T) invoiceDAO.findInvoiceById(invoiceId);
		Assert.isTrue(validateAccessToInvoice(invoice), "Current user is not authorized");
		return invoice;
	}

	@Override
	public Invoice findInvoiceByWorkId(long workId) {
		Work work = workService.findWork(workId);
		if (work != null && work.getInvoice() != null) {
			Assert.isTrue(validateAccessToInvoice(work.getInvoice()), "Current user is not authorized");
			return work.getInvoice();
		}
		return null;
	}

	@Override
	public List<? extends AbstractInvoice> findInvoicesById(List<Long> invoiceIds) {
		List<? extends AbstractInvoice> invoices = invoiceDAO.get(invoiceIds);
		for (AbstractInvoice i : invoices) {
			Assert.isTrue(validateAccessToInvoice(i), "Current user is not authorized");
		}
		return invoices;
	}

	@Override
	public InvoicePagination findAllInvoicesByCompany(long companyId, InvoicePagination pagination) {
		Assert.notNull(pagination);
		return invoiceDAO.findAllByCompanyId(companyId, pagination);
	}

	@Override
	public WorkMarketSummaryInvoicePagination findAllWorkMarketSummaryInvoices(WorkMarketSummaryInvoicePagination pagination) {
		Assert.notNull(pagination);
		return wmSummaryInvoiceDAO.findAll(pagination);
	}

	@Override
	public ServiceInvoicePagination findAllServiceInvoices(ServiceInvoicePagination pagination) {
		Assert.notNull(pagination);
		return serviceInvoiceDAO.findAll(pagination);
	}

	@Override
	public AggregatesDTO getAllServiceInvoicesTotalsByStatus() {
		return serviceInvoiceDAO.getAllServiceInvoicesTotal();
	}

	@Override
	public AccountStatementDetailPagination getStatementDashboard(AccountStatementFilters filters, AccountStatementDetailPagination pagination) {
		User user = authenticationService.getCurrentUser();
		Assert.notNull(user);
		return accountStatementDetailDAO.findInvoices(user.getId(), user.getCompany().getId(), pagination, filters, authenticationService.hasAccessToAllInvoicesAndStatementsAtCompany(user));
	}

	@Override
	public AccountStatementDetailPagination getStatementDashboardWithBundledInvoices(AccountStatementFilters filters, AccountStatementDetailPagination pagination) {
		User user = authenticationService.getCurrentUser();
		Assert.notNull(user);
		return accountStatementDetailDAO.findInvoicesWithBundledInvoices(user.getId(), user.getCompany().getId(), pagination, filters, authenticationService.hasAccessToAllInvoicesAndStatementsAtCompany(user));
	}

	@Override
	public AccountStatementDetailPagination getStatementDashboardForUser(AccountStatementFilters filters, AccountStatementDetailPagination pagination, User user) {
		Assert.notNull(user);
		Assert.notNull(user.getCompany());
		return accountStatementDetailDAO.findInvoices(user.getId(), user.getCompany().getId(), pagination, filters, Boolean.TRUE);
	}

	@Override
	public AccountStatementDetailRow findAccountStatementDetailByInvoiceId(Long invoiceId) {
		return findAccountStatementDetailByInvoiceId(invoiceId, authenticationService.getCurrentUser());
	}

	@Override
	public void emailInvoiceForWork(long workId) {
		Work work = workService.findWork(workId);
		Assert.notNull(work);

		if (work.getInvoice() != null) {
			final AccountStatementDetailRow invoice = findAccountStatementDetailByInvoiceId(work.getInvoice().getId(), authenticationService.getCurrentUser());

			if (invoice != null) {
				boolean isAutoInvoiceEnabled = work.getManageMyWorkMarket() != null && work.getManageMyWorkMarket().getAutoSendInvoiceEmail() && StringUtils.isNotBlank(work.getCompany().getInvoiceSentToEmail());
				eventRouter.sendEvent(new WorkInvoiceGenerateEvent(work.getInvoice().getId(), work.getId(), isAutoInvoiceEnabled ? WorkInvoiceSendType.ALL : WorkInvoiceSendType.SUBSCRIBED));
			} else {
				logger.error("Null invoice id " + work.getInvoice().getId());
			}
		} else {
			logger.error("Null invoice for work id " + work.getId());
		}
	}

	@Override
	public AccountStatementDetailRow findAccountStatementDetailByInvoiceId(Long invoiceId, User user) {
		//Load the invoice to validate the user has access
		findInvoiceById(invoiceId);
		return accountStatementDetailDAO.findAccountStatementDetailByInvoiceId(invoiceId, user.getId(), user.getCompany().getId(), authenticationService.hasAccessToAllInvoicesAndStatementsAtCompany(user));
	}

	@Override
	public <T extends AbstractInvoice> AccountStatementDetailPagination findAllPendingPaymentInvoicesForInvoiceSummary(T invoiceSummary, AccountStatementDetailPagination pagination) {
		AccountStatementFilters accountStatementFilters = new AccountStatementFilters();
		accountStatementFilters.setPaidStatus(false);
		if (invoiceSummary instanceof InvoiceCollection) {
			accountStatementFilters.setInvoiceSummaryId(invoiceSummary.getId());
			return accountStatementDetailDAO.findInvoices(invoiceSummary.getCreatorId(), invoiceSummary.getCompany().getId(), pagination, accountStatementFilters, true);
		}
		if (invoiceSummary instanceof InvoiceSummary) {
			accountStatementFilters.setInvoiceSummaryId(invoiceSummary.getId());
			return accountStatementDetailDAO.findBundledInvoices(invoiceSummary.getCreatorId(), invoiceSummary.getCompany().getId(), pagination, accountStatementFilters, true);
		}
		if (invoiceSummary instanceof Statement) {
			accountStatementFilters.setStatementId(invoiceSummary.getId());
			return accountStatementDetailDAO.findInvoices(invoiceSummary.getCreatorId(), invoiceSummary.getCompany().getId(), pagination, accountStatementFilters, true);
		}
		return pagination;
	}

	@Override
	public Map<String, List<ConstraintViolation>> payInvoice(long userId, long invoiceId) throws InsufficientFundsException {
		AbstractInvoice invoice = invoiceDAO.findInvoiceById(invoiceId);
		if (invoice != null && invoice instanceof Invoice) {
			Assert.isTrue(!((Invoice) invoice).isBundled(), "Can't pay a bundled invoice");
		}
		User user = userService.getUser(userId);
		if (user != null) {
			return payInvoice(user, invoice);
		}
		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, List<ConstraintViolation>> payInvoices(long userId, List<Long> invoiceIds) throws InsufficientFundsException {
		logger.debug(userId);

		Assert.notNull(invoiceIds);
		Map<String, List<ConstraintViolation>> violations = Maps.newHashMap();

		List<Long> invoices = Lists.newArrayListWithExpectedSize(invoiceIds.size());
		for (Long id : invoiceIds) {
			AbstractInvoice invoice = invoiceDAO.findInvoiceById(id);
			if (invoice instanceof InvoiceSummary || invoice instanceof AbstractServiceInvoice) {
				violations.putAll(payInvoice(invoice));
			} else if (invoice instanceof Invoice) {
				if (validateAccessToInvoice(invoice)) {
					if (!((Invoice) invoice).isBundled()) {
						invoices.add(id);
					} else {
						violations.put(invoice.getInvoiceNumber(), Lists.newArrayList(new ConstraintViolation(MessageKeys.Invoice.INVOICE_IS_BUNDLED)));
					}
				} else {
					violations.put(invoice.getInvoiceNumber(), Lists.newArrayList(new ConstraintViolation(MessageKeys.Invoice.INVOICE_INVALID_ACCESS)));
				}
			}
		}

		if (invoices.isEmpty()) {
			return violations;
		}

		InvoiceSummaryDTO invoiceSummaryDTO = new InvoiceSummaryDTO();
		invoiceSummaryDTO.setInvoicesIds(invoices);

		InvoiceCollection collection = saveInvoiceCollection(invoiceSummaryDTO);
		return payInvoice(collection);
	}

	private <T extends AbstractInvoice> Map<String, List<ConstraintViolation>> payInvoice(User user, T invoice) throws InsufficientFundsException {
		validateInvoice(invoice);
		validateAccessToInvoice(invoice);
		Assert.notNull(user, "Unable to find user");
		Map<String, List<ConstraintViolation>> violations = Maps.newHashMap();
		if ((invoice instanceof InvoiceSummary) || (invoice instanceof Statement) || (invoice instanceof SubscriptionInvoice) ||
			(invoice instanceof AdHocInvoice)) {
			return payInvoice(invoice);
		}

		if (invoice instanceof Invoice) {
			List<Long> assignments = workService.findWorkIdsByInvoiceId(invoice.getId());
			return payAssignments(assignments);
		}
		return violations;
	}

	@Override
	public boolean hasAtLeastOneFastFundableInvoice(long userId) {
		List<Long> nonFastFundedAndUnpaidInvoiceIds = invoiceDAO.findAllNonFastFundedAndDueInvoiceIdsToUser(getFastFundsDeadlineThreshold(), userId);

		for (Long invoiceId : nonFastFundedAndUnpaidInvoiceIds) {
			Invoice invoice = invoiceDAO.get(invoiceId);
			if (companyService.isFastFundsEnabled(invoice.getCompany())) {
				return true;
			}
		}

		return false;
	}

	private Calendar getFastFundsDeadlineThreshold() {
		// Added this grace period to handle the case where a worker has an invoice due near FAST_FUNDS_AVAILABILITY_THRESHOLD_HOURS from now
		// This prevents the case where they are notified of a fast fundable invoice but have passed the fast funds deadline for that invoice
		// Fast Funds Deadline = invoiceDueDate - FAST_FUNDS_AVAILABILITY_THRESHOLD_HOURS
		int FIFTEEN_MINUTE_GRACE_PERIOD = 15;

		Calendar fastFundsDeadlineThreshold = Calendar.getInstance();
		fastFundsDeadlineThreshold.add(Calendar.HOUR, FAST_FUNDS_AVAILABILITY_THRESHOLD_HOURS);
		fastFundsDeadlineThreshold.add(Calendar.MINUTE, FIFTEEN_MINUTE_GRACE_PERIOD);

		return fastFundsDeadlineThreshold;
	}

	@Override
	public List<Invoice> findAllFastFundableInvoicesForWorker(final long userId) {
		final List<Long> nonFastFundedAndUnpaidInvoiceIds = invoiceDAO.findAllNonFastFundedAndDueInvoiceIdsToUser(getFastFundsDeadlineThreshold(), userId);
		final ImmutableList.Builder<Invoice> builder = ImmutableList.builder();

		for (final Long invoiceId : nonFastFundedAndUnpaidInvoiceIds) {
			final Invoice invoice = invoiceDAO.get(invoiceId);
			if (companyService.isFastFundsEnabled(invoice.getCompany())) {
				builder.add(invoice);
			}
		}

		return builder.build();
	}

	@Override
	public BigDecimal calculateTotalFastFundableResourceCostForWorker(final long userId) {
		BigDecimal total = BigDecimal.ZERO;
		final List<Invoice> fastFundableInvoices = findAllFastFundableInvoicesForWorker(userId);

		for (final Invoice invoice : fastFundableInvoices) {
			final WorkResource workResource = workResourceService.findWorkResourceById(invoice.getActiveWorkResourceId());
			final BigDecimal resourceCost = calculateTotalResourceCostOnWork(workResource);
			total = total.add(resourceCost);
		}

		return total;
	}

	@Override
	public BigDecimal getFastFundsFeePercentage(Long invoiceCompanyId) {
		if (TELAID_COMPANY_ID.equals(invoiceCompanyId)) {
			return TELAID_FAST_FUNDS_FEE_PERCENTAGE;
		}
		return FAST_FUNDS_FEE_PERCENTAGE;
	}

	@Override
	public int getFastFundsAvailabilityThresholdHours() {
		return FAST_FUNDS_AVAILABILITY_THRESHOLD_HOURS;
	}

	@Override
	public BigDecimal calculateFastFundsFeeCost(BigDecimal amountEarnedByResource, Long invoiceCompanyId) {
		Assert.notNull(amountEarnedByResource);
		Assert.notNull(invoiceCompanyId);

		return amountEarnedByResource.multiply(getFastFundsFeePercentage(invoiceCompanyId)).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateTotalResourceCostOnWork(final long workId) {
		final Work work = workService.findWork(workId, false);
		final WorkResource workResource = workResourceService.findActiveWorkResource(workId);
		final WorkCostDTO workCostDTO = accountRegisterServicePaymentTerms.calculateCostOnCompleteWork(work, workResource);
		return workCostDTO.getTotalResourceCost();
  }

	@Override
	public BigDecimal calculateTotalResourceCostOnWork(WorkResource workResource) {
		return accountRegisterServicePaymentTerms.calculateCostOnCompleteWork(workResource.getWork(), workResource).getTotalResourceCost();
	}

	@Override
	public BigDecimal calculateFastFundsFeeCostOnWork(final long workId, final BigDecimal amountEarnedByResource) {
		final Invoice invoice = findInvoiceByWorkId(workId);
		Assert.notNull(invoice);
		final Long invoiceCompanyId = invoice.getCompany().getId();
		return calculateFastFundsFeeCost(amountEarnedByResource, invoiceCompanyId);
	}

	@Override
	public FastFundInvoiceResponse fastFundInvoice(long invoiceId) {
		Work work = workService.findWorkByInvoice(invoiceId);
		Assert.notNull(work, "Unable to find work by invoice id: " + invoiceId);

		return fastFundInvoice(invoiceId, work.getId());
	}

	@Override
	public FastFundInvoiceResponse fastFundInvoice(long invoiceId, long workId) {
		Invoice invoice = invoiceDAO.findInvoiceById(invoiceId);
		Assert.notNull(invoice, "Unable to find invoice");
		Assert.isTrue(invoice.isPaymentPending(), "Invoice needs to be in payment pending status in order to be fast funded");
		Assert.isNull(invoice.getFastFundedOn(), "Invoice has already been fast funded");

		FastFundInvoiceResponse response = FastFundInvoiceResponse.fail();

		List<WorkWorkResourceAccountRegister> workResourceAccountRegisters = workService.findWorkAndWorkResourceForPayment(ImmutableList.of(workId));
		if (CollectionUtils.isEmpty(workResourceAccountRegisters)) {
			return response;
		}

		WorkWorkResourceAccountRegister workResourceAccountRegister = workResourceAccountRegisters.get(0);
		WorkResource workResource = workService.findWorkResourceById(workResourceAccountRegister.getWorkResourceId());
		AccountRegister resourceAccountRegister = pricingService.findDefaultRegisterForCompany(workResource.getUser().getCompany().getId(), true);

		Assert.notNull(workResource, "Unable to find work resource");

		WorkCostDTO workCostDTO = accountRegisterServicePaymentTerms.calculateCostOnCompleteWork(workResource.getWork(), workResource);

		BigDecimal totalResourceCost = workCostDTO.getTotalResourceCost();

		accountRegisterServicePaymentTerms.createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction(workResource, resourceAccountRegister, totalResourceCost);

		BigDecimal fastFundsFee = calculateFastFundsFeeCost(totalResourceCost, invoice.getCompany().getId());
		logger.debug("Amount earned by worker: " + totalResourceCost + " calculated fast funds fee: " + fastFundsFee);

		WorkResourceTransaction workResourceTransaction = accountRegisterServicePaymentTerms.createAndExecuteFastFundsFeeWorkResourceTransactionTransaction(workResource, resourceAccountRegister, fastFundsFee);
		Calendar fastFundsEffectiveDate = workResourceTransaction.getEffectiveDate();

		invoice.setFastFundedOn(fastFundsEffectiveDate);
		invoiceDAO.saveOrUpdate(invoice);

		FastFundsReceivableCommitment fastFundsReceivableCommitment = new FastFundsReceivableCommitment();

		fastFundsReceivableCommitment
			.setAmount(totalResourceCost.negate())
			.setWorkId(workId)
			.setEffectiveDate(fastFundsEffectiveDate)
			.setTransactionDate(fastFundsEffectiveDate)
			.setPending(true);

		fastFundsReceivableCommitmentDAO.saveOrUpdate(fastFundsReceivableCommitment);

		return response
			.setStatus(FastFundInvoiceStatus.SUCCESS)
			.setFastFundsOn(fastFundsEffectiveDate);
	}

	@Override
	public List<ConstraintViolation> payAssignment(Long workId) throws InsufficientFundsException {
		Assert.notNull(workId);

		List<ConstraintViolation> violationList = Lists.newArrayList();

		if (workService.isWorkPendingFulfillment(workId)) {
			violationList.add(new ConstraintViolation(MessageKeys.Work.WORK_IS_PENDING_FULFILLMENT));
			return violationList;
		}

		Work work = workService.findWork(workId);
		if (!work.isPaymentPending()) {
			violationList.add(new ConstraintViolation(MessageKeys.Work.INVALID_STATUS_FOR_PAID));
		}
		if (!work.isInvoiced()) {
			violationList.add(new ConstraintViolation(MessageKeys.Work.INVALID_INVOICE));
		}
		if (work.getInvoice().isBundled()) {
			violationList.add(new ConstraintViolation(MessageKeys.Invoice.INVOICE_IS_BUNDLED));
		}

		if (!violationList.isEmpty()) {
			return violationList;
		}

		List<Long> workIds = Lists.newArrayListWithExpectedSize(1);
		workIds.add(workId);
		Map<String, List<ConstraintViolation>> violations = payAssignments(workIds);
		for (Map.Entry<String, List<ConstraintViolation>> entry : violations.entrySet()) {
			return entry.getValue();
		}
		return violationList;
	}

	private Map<String, List<ConstraintViolation>> payAssignments(List<Long> assignmentIds) throws InsufficientFundsException {
		Map<String, List<ConstraintViolation>> violations = Maps.newHashMap();
		if (assignmentIds == null || assignmentIds.isEmpty()) {
			return violations;
		}
		return workStatusService.transitionPaymentPendingToPaid(assignmentIds);
	}

	private <T extends AbstractInvoice> void validateInvoice(T invoice) {
		Assert.notNull(invoice, "Unable to find invoice");
		Assert.state(!invoice.getDeleted(), "Invoice has been deleted");
		Assert.state(invoice.isPaymentPending(), "Invoice has been paid.");
	}

	@Override
	public boolean validateAccessToInvoice(long invoiceId) {
		AbstractInvoice invoice = invoiceDAO.findInvoiceById(invoiceId);
		return invoice != null && validateAccessToInvoice(invoice);
	}

	private boolean validateAccessToInvoice(AbstractInvoice invoice) {
		User user = authenticationService.getCurrentUser();
		String[] roles = authenticationService.getRoles(user.getId());
		if (ArrayUtils.isNotEmpty(roles) && Arrays.asList(roles).contains(RoleType.WM_ACCOUNTING)) {
			return true;
		}
		Long userCompanyId = user.getCompany().getId();
		Long inventoryCompanyId = invoice.getCompany().getId();

		if (!inventoryCompanyId.equals(userCompanyId)) {
			if (invoice instanceof Invoice) {
				WorkResource workResource = workService.findWorkResourceById(((Invoice) invoice).getActiveWorkResourceId());
				if (workResource.getDispatcherId() != null && workResource.getDispatcherId().equals(user.getId())) {
					return true;
				}

				User resource = (workResource != null) ? workResource.getUser() : null;
				if (resource != null) {
					if (resource.getId().equals(user.getId())) {
						return true;
					}

					Company resourceCompany = resource.getCompany();
					if (resourceCompany != null) {
						return userRoleService.isAdminOrManager(user) && resourceCompany.getId().equals(userCompanyId);
					}
				}
			}
			return false;
		} else if (invoice.getCreatorId().equals(user.getId())) {
			return true;
		} else if (userRoleService.isAdminOrManager(user) || userRoleService.hasAclRole(user, AclRole.ACL_CONTROLLER)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean validateAccessToFastFundInvoice(long invoiceId) {
		AbstractInvoice invoice = invoiceDAO.findInvoiceById(invoiceId);

		if (invoice == null) {
			logger.error("validateAccessToFastFundsInvoice: Invoice<" + invoiceId + "> not found.");
			return false;
		}

		User currentUser = authenticationService.getCurrentUser();
		if (currentUser == null) {
			logger.error("validateAccessToFastFundInvoice: Invoice<" + invoiceId + "> currentUser cannot be null");
			return false;
		}

		Long currentUserCompanyId = currentUser.getCompany().getId();
		if (currentUserCompanyId == null) {
			logger.error("validateAccessToFastFundInvoice: Invoice<" + invoiceId + "> currentUserCompanyId cannot be null");
			return false;
		}

		Long invoicedCompanyId = invoice.getCompany().getId();
		if (invoicedCompanyId == null) {
			logger.error("validateAccessToFastFundInvoice: Invoice<" + invoiceId + "> invoicedCompanyId cannot be null");
			return false;
		}

		if (!invoicedCompanyId.equals(currentUserCompanyId) && invoice instanceof Invoice) {
			WorkResource workResource = workService.findWorkResourceById(((Invoice) invoice).getActiveWorkResourceId());
			// current user dispatched this worker
			if (workResource.getDispatcherId() != null && workResource.getDispatcherId().equals(currentUser.getId())) {
				return true;
			}
			User resource = workResource != null ? workResource.getUser() : null;
			// current user is the work resource
			if (resource != null && resource.getId().equals(currentUser.getId())) {
				return true;
			}
			// current user is a company admin
			if (currentUserCompanyId.equals(resource.getCompany().getId()) && userRoleService.isAdmin(currentUser)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void emailInvoiceToUser(String email, Long invoiceId) {
		emailInvoiceToUser(email, invoiceId, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Override
	public void emailInvoiceToUser(String toEmail, Long invoiceId, String sourceFilePath, String mimeType) {
		if (invoiceId == null) return;
		AbstractInvoice invoice = findInvoiceById(invoiceId);

		if (invoice == null) return;
		if (invoice.getDeleted()) return;
		if (StringUtils.isBlank(toEmail)) return;
		if (!EmailValidator.getInstance().isValid(toEmail)) return;
		EmailTemplate template = emailTemplateFactory.buildInvoiceEmailTemplate(toEmail, invoice);

		if (StringUtilities.isNotEmpty(sourceFilePath) && StringUtilities.isNotEmpty(mimeType)) {
			FileDTO attachment = new FileDTO();
			attachment.setName(invoice.getInvoiceNumber() + Constants.PDF_EXTENSION);
			attachment.setMimeType(mimeType);
			attachment.setSourceFilePath(sourceFilePath);
			template.getAttachments().add(attachment);
		}

		try {
			notificationDispatcher.dispatchEmail(template);
			updateInvoiceLastSentOnAndSentTo(invoice, toEmail, DateUtilities.getCalendarNow());


		} catch (Exception e) {
			logger.error("Error sending invoice id: " + invoice.getId() + " to Email: " + toEmail, new Exception());
		}
	}

	/**
	 * Updated version of updateInvoiceLastDownloadedDate that takes a list of invoiceIds and the loggedInUserId.
	 * This method is called asynchronously so we cannot use authenticationService.getCurrentUser().
	 */
	@Override
	public void updateInvoiceLastDownloadedDate(List<Long> invoiceIds, Calendar date, Long loggedInUserId) {
		Assert.notNull(date);
		if (!CollectionUtilities.containsAny(Constants.ACCESS_ALL_INVOICES_USER_IDS, loggedInUserId)) {
			for (Long invoiceId : invoiceIds) {
				AbstractInvoice invoice = invoiceDAO.findInvoiceById(invoiceId);
				Assert.notNull(invoice, "Unable to find invoice");
				invoice.setDownloadedOn(date);
			}
		}
	}

	@Override
	public void updateInvoiceLastDownloadedDate(Long invoiceId, Calendar date) {

		User loggedInUser = authenticationService.getCurrentUser();
		updateInvoiceLastDownloadedDate(Collections.singletonList(invoiceId), date, loggedInUser.getId());
	}

	@Override
	public void unlockInvoice(Long invoiceId) {
		AbstractInvoice invoice = invoiceDAO.findInvoiceById(invoiceId);
		Assert.notNull(invoice, "Unable to find invoice");

		if (invoice.isPaymentPending()) {
			invoice.setDownloadedOn(null);
			invoice.setSentOn(null);
			invoice.setSentTo(null);
		}
	}

	private void updateInvoiceLastSentOnAndSentTo(AbstractInvoice invoice, String email, Calendar date) {
		Assert.notNull(date);
		Assert.hasText(email);
		Assert.notNull(invoice);
		invoice.setSentTo(email);
		invoice.setSentOn(date);
	}

	@Override
	public PaymentTermsDays calculateStatementPaymentTermsDays(PaymentCycle paymentCycle, PaymentMethod paymentMethod, AccountingProcessTime accountingProcessTime) {
		Assert.notNull(paymentCycle);
		Assert.notNull(paymentMethod);
		Assert.notNull(accountingProcessTime);
		int days = paymentCycle.getPaymentDays() + paymentMethod.getPaymentDays() + accountingProcessTime.getPaymentDays();
		return PaymentTermsDays.getNearestPaymentTermsDays(days);
	}

	Calendar calculateStartDatePaymentCycle(PaymentCycle paymentCycle, Integer preferredDayOfWeek, Integer preferredDayOfMonth, boolean isBiweeklyPaymentOnSpecificDayOfMonth,
													BiweeklyPaymentDays biweeklyPaymentDays) {

		// we need to generate statements at 10 am in GMT timezone
		Calendar paymentCycleStartDate = DateUtilities.getMidnightTodayRelativeToTimezone(TimeZone.getTimeZone("GMT"));
		paymentCycleStartDate.set(Calendar.HOUR_OF_DAY, 10);

		Assert.notNull(paymentCycle);
		switch (paymentCycle) {
			case DAILY:
				Calendar clone = DateUtilities.cloneCalendar(paymentCycleStartDate);
				clone.add(Calendar.DAY_OF_MONTH, 1);

				return clone;
			case WEEKLY:
				return DateUtilities.getCalendarWithNextDayOfWeek(
						paymentCycleStartDate,
						addDays(preferredDayOfWeek, ONE_DAY_OFFSET)
						);
			case BIWEEKLY:
				if (isBiweeklyPaymentOnSpecificDayOfMonth) {
					Assert.notNull(biweeklyPaymentDays);
					return DateUtilities.getCalendarWithNextDayOfMonth(
							paymentCycleStartDate,
							biweeklyPaymentDays.getFirstDay()
					);
				}
				return DateUtilities.getCalendarWithNextDayOfWeek(
						paymentCycleStartDate,
						addDays(preferredDayOfWeek, ONE_DAY_OFFSET));
			default:
				// MONTHLY
				return DateUtilities.getCalendarWithNextDayOfMonth(
						paymentCycleStartDate,
						preferredDayOfMonth
						);
		}
	}

	private Calendar getNextStatementDate(Calendar paymentCycleStartDate, PaymentCycle paymentCycle, Integer preferredDayOfWeek, Integer preferredDayOfMonth,
										  boolean isBiweeklyPaymentOnSpecificDayOfMonth, BiweeklyPaymentDays biweeklyPaymentDays) {
		Assert.notNull(paymentCycle);
		switch (paymentCycle) {
			case DAILY:
				Calendar clone = DateUtilities.cloneCalendar(paymentCycleStartDate);
				clone.add(Calendar.DAY_OF_MONTH, 1);

				return clone;
			case WEEKLY:
				return DateUtilities.getCalendarWithNextDayOfWeek(
						paymentCycleStartDate,
						addDays(preferredDayOfWeek, ONE_DAY_OFFSET)
						);
			case BIWEEKLY:
				if (isBiweeklyPaymentOnSpecificDayOfMonth) {
					Assert.notNull(biweeklyPaymentDays);
					return DateUtilities.getCalendarWithClosestNextDayOfMonth(
							paymentCycleStartDate,
							biweeklyPaymentDays.getFirstDay(),
							biweeklyPaymentDays.getSecondDay()
							);
				}
				paymentCycleStartDate = DateUtilities.getCalendarWithNextDayOfWeek(
						paymentCycleStartDate,
						addDays(preferredDayOfWeek, ONE_DAY_OFFSET)
						);
				paymentCycleStartDate.add(Calendar.WEEK_OF_YEAR, 1);
				return paymentCycleStartDate;
			default:
				// MONTHLY
				paymentCycleStartDate = DateUtilities.getCalendarWithNextDayOfMonth(
						paymentCycleStartDate,
						preferredDayOfMonth
						);
				return paymentCycleStartDate;
		}
	}

	@Override
	public PaymentConfiguration findStatementPaymentConfigurationByCompany(long companyId) {
		Company company = companyService.findCompanyById(companyId);
		if (company != null) {
			return company.getPaymentConfiguration();
		}
		return null;
	}

	@Override
	public PaymentConfiguration saveStatementPaymentConfigurationForCompany(long companyId, PaymentConfigurationDTO dto) {
		logger.debug(dto);
		Company company = companyService.findCompanyById(companyId);
		Assert.notNull(company, "Unable to find company");
		// Assert.isTrue(workDAO.countAllAssignmentsPaymentPendingByCompany(companyId).intValue() == 0, "There are assignments pending payment. Can't modify statement settings.");

		PaymentConfiguration companyPaymentConfiguration = company.getPaymentConfiguration();
		if (companyPaymentConfiguration == null) {
			companyPaymentConfiguration = new PaymentConfiguration();
			paymentConfigurationDAO.saveOrUpdate(companyPaymentConfiguration);
			company.setPaymentConfiguration(companyPaymentConfiguration);
		}

		PaymentCycle paymentCycle = PaymentCycle.getPaymentCycle(dto.getPaymentCycleDays());
		Assert.notNull(paymentCycle, "Invalid payment cycle.");

		validatePaymentCycle(paymentCycle, dto);

		AccountingProcessTime processTime = AccountingProcessTime.getAccountingProcessTime(dto.getAccountingProcessDays());
		Assert.notNull(processTime, "Invalid accounting process time.");

		Assert.isTrue(dto.isAchPaymentMethodEnabled() || dto.isCheckPaymentMethodEnabled()
				|| dto.isCreditCardPaymentMethodEnabled()
				|| dto.isWireTransferPaymentMethodEnabled()
				|| dto.isPrefundPaymentMethodEnabled(),
				"At least one payment method has to be selected.");

		BeanUtils.copyProperties(dto, companyPaymentConfiguration);

		companyPaymentConfiguration.setAccountingProcessDays(dto.getAccountingProcessDays());
		companyPaymentConfiguration.setPaymentCycleDays(dto.getPaymentCycleDays());
		companyPaymentConfiguration.setBiweeklyPaymentOnSpecificDayOfMonth(dto.isBiweeklyPaymentOnSpecificDayOfMonth());
		companyPaymentConfiguration.setPreferredDayOfMonthBiweeklyFirstPayment(dto.getPreferredDayOfMonthBiweeklyFirstPayment());

		BiweeklyPaymentDays biweeklyPaymentDays = null;
		if (paymentCycle.equals(PaymentCycle.BIWEEKLY) && dto.isBiweeklyPaymentOnSpecificDayOfMonth()) {
			biweeklyPaymentDays = BiweeklyPaymentDays.getPaymentCycle(dto.getPreferredDayOfMonthBiweeklyFirstPayment());
			companyPaymentConfiguration.setPreferredDayOfMonthBiweeklySecondPayment(biweeklyPaymentDays.getSecondDay());
		}

		PaymentMethod paymentMethod = PaymentMethod.getPaymentMethodWithGreatestProcessTime(dto.isCheckPaymentMethodEnabled(), dto.isWireTransferPaymentMethodEnabled(),
				dto.isAchPaymentMethodEnabled(), dto.isCreditCardPaymentMethodEnabled(), dto.isPrefundPaymentMethodEnabled());

		PaymentTermsDays paymentTermsDays = calculateStatementPaymentTermsDays(paymentCycle, paymentMethod, processTime);
		company.getManageMyWorkMarket().setPaymentTermsEnabled(true);
		company.getManageMyWorkMarket().setStatementsEnabled(true);
		company.getManageMyWorkMarket().setPaymentTermsDays(paymentTermsDays.getPaymentDays());

		Calendar paymentCycleStartDate = calculateStartDatePaymentCycle(paymentCycle, dto.getPreferredDayOfWeek(), dto.getPreferredDayOfMonth(), dto.isBiweeklyPaymentOnSpecificDayOfMonth(),
				biweeklyPaymentDays);
		logger.debug("paymentCycleStartDate " + DateUtilities.formatDateForEmail(paymentCycleStartDate));
		companyPaymentConfiguration.setStartDatePaymentCycle(paymentCycleStartDate);
		Calendar nextStatementOn = getNextStatementDate(paymentCycleStartDate, paymentCycle, dto.getPreferredDayOfWeek(), dto.getPreferredDayOfMonth(), dto.isBiweeklyPaymentOnSpecificDayOfMonth(),
				biweeklyPaymentDays);
		logger.debug("nextStatementOn " + DateUtilities.formatDateForEmail(nextStatementOn));
		companyPaymentConfiguration.setNextStatementDate(nextStatementOn);

		paymentConfigurationDAO.saveOrUpdate(companyPaymentConfiguration);
		authenticationService.refreshSessionForCompany(companyId);

		return companyPaymentConfiguration;
	}

	@Override
	public void saveStatementPaymentConfiguration(PaymentConfiguration paymentConfiguration) {
		paymentConfigurationDAO.saveOrUpdate(paymentConfiguration);
	}

	private void validatePaymentCycle(PaymentCycle paymentCycle, PaymentConfigurationDTO dto) {
		switch (paymentCycle) {
			case WEEKLY:
				Assert.isTrue(dto.getPreferredDayOfWeek() >= 1 && dto.getPreferredDayOfWeek() <= 7, "Invalid day of the week.");
				break;
			case BIWEEKLY:
				Assert.isTrue(!(dto.getAccountingProcessDays().equals(AccountingProcessTime.FIFTEEN_DAYS.getPaymentDays())), "Invalid accounting process days.");
				if (dto.isBiweeklyPaymentOnSpecificDayOfMonth()) {
					Assert.notNull(dto.getPreferredDayOfMonthBiweeklyFirstPayment());
					BiweeklyPaymentDays biweeklyPaymentDays = BiweeklyPaymentDays.getPaymentCycle(dto.getPreferredDayOfMonthBiweeklyFirstPayment());
					Assert.notNull(biweeklyPaymentDays, "Invalid accounting process time.");
				} else {
					Assert.isTrue(dto.getPreferredDayOfWeek() >= 1 && dto.getPreferredDayOfWeek() <= 7, "Invalid day of the week.");
				}
				break;
			case MONTHLY:
				Assert.isTrue(!(dto.getAccountingProcessDays().equals(AccountingProcessTime.FIFTEEN_DAYS.getPaymentDays())), "Invalid accounting process days.");
				Assert.isTrue(dto.getPreferredDayOfMonth() >= 1 && dto.getPreferredDayOfMonth() <= 30, "Invalid day of the month.");
				break;
		}
	}

	private int addDays(Integer preferredDayOfWeek, Integer days) {
		return preferredDayOfWeek % 7 + days;
	}

	@Override
	public PastDueCompaniesDTO findAllCompaniesWithOverdueInvoices(Calendar now) {
		/* The overdue warning hours ordered ASC */
		List<Integer> overdueWarningDays = Constants.LOCKED_ACCOUNT_OVERDUE_WARNING_DAYS;

		/* Days since account was overdue -> Companies to warn */
		Map<Integer, Set<Long>> companiesToOverdueWarn = Maps.newHashMap();
		for (Integer daysSinceOverdue : overdueWarningDays) {
			Set<Long> companies = Sets.newHashSet();
			companiesToOverdueWarn.put(daysSinceOverdue, companies);
		}

		/* CompanyId -> Closest due date in time of due assignments */
		final Map<Long, Calendar> companiesTo24HsWarn = Maps.newHashMap();
		final Map<Long, Long> invoiceIdsToWarnOwners = Maps.newHashMap();
		final Set<Long> companiesToLock = Sets.newHashSet();

		Calendar tomorrow = DateUtilities.cloneCalendar(now);
		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		Calendar dayAfterTomorrow = DateUtilities.cloneCalendar(tomorrow);
		dayAfterTomorrow.add(Calendar.DAY_OF_MONTH, 1);

		// We need the invoices past due to send the overdue warning or lock the companies
		Set<? extends AbstractInvoice> dueInvoices = invoiceDAO.findAllInvoicesPastDue(now);
		logger.debug("****** Found " + dueInvoices.size() + " invoices due.");
		logger.debug("****** Now " + DateUtilities.formatDateForEmail(now));
		logger.debug("****** Tomorrow " + DateUtilities.formatDateForEmail(tomorrow));
		logger.debug("****** Day After Tomorrow " + DateUtilities.formatDateForEmail(dayAfterTomorrow));

		/* Give the companies LOCKED_ACCOUNT_WINDOW_DAYS days before they get locked */
		Calendar dueDateWindowToCompareWith = (Calendar) now.clone();
		dueDateWindowToCompareWith.add(Calendar.DAY_OF_MONTH, -Constants.LOCKED_ACCOUNT_WINDOW_DAYS);
		logger.debug("****** dueDateWindowToCompareWith " + DateUtilities.formatDateForEmail(dueDateWindowToCompareWith));

		for (AbstractInvoice invoiceDue : dueInvoices) {
			/* If the invoice is past due and the window has been consumed, lock the company */
			if (invoiceDue.getDueDate().before(dueDateWindowToCompareWith)) {
				invoiceDueLogDAO.saveOrUpdate(new InvoiceDueLog(invoiceDue.getId()));
				companiesToLock.add(invoiceDue.getCompany().getId());
			} else {
				int daysForNextLock = DateUtilities.getDaysBetween(invoiceDue.getDueDate(), now);
				/* If the window has not been consumed and the company is not already locked then we need to warn the company */
				for (Integer warningDays : overdueWarningDays) {
					if (daysForNextLock <= warningDays) {
						companiesToOverdueWarn.get(warningDays).add(invoiceDue.getCompany().getId());
						break;
					}
				}
			}
		}

		/* We need the invoices going to be past due in 24Hs to send the warning */
		/* If an invoice will be past due in less than 24Hs then we already sent the warnings in a previous run of the process */
		Set<? extends AbstractInvoice> invociesToBeDue = invoiceDAO.findAllDueInvoicesByDueDate(tomorrow, dayAfterTomorrow);
		logger.debug("****** Found " + invociesToBeDue.size() + " invoices to be due.");

		for (AbstractInvoice invoiceToBeDue : invociesToBeDue) {
			Calendar dueOn = DateUtilities.cloneCalendar(invoiceToBeDue.getDueDate());
			if (!companiesTo24HsWarn.containsKey(invoiceToBeDue.getCompany().getId()) || dueOn.before(companiesTo24HsWarn.get(invoiceToBeDue.getCompany().getId()))) {
				companiesTo24HsWarn.put(invoiceToBeDue.getCompany().getId(), dueOn);
			}

			// create set of due invoices to notify assignment owners
			invoiceIdsToWarnOwners.put(invoiceToBeDue.getCreatorId(), invoiceToBeDue.getId());
		}

		PastDueCompaniesDTO pastDueCompaniesDTO = new PastDueCompaniesDTO();
		pastDueCompaniesDTO.setCompaniesTo24HsWarn(companiesTo24HsWarn);
		pastDueCompaniesDTO.setCompaniesToLock(companiesToLock);
		pastDueCompaniesDTO.setCompaniesToOverdueWarn(companiesToOverdueWarn);
		pastDueCompaniesDTO.setInvoiceIdsToWarnOwners(invoiceIdsToWarnOwners);
		return pastDueCompaniesDTO;
	}

	@Override
	public List<Company> findAllCompaniesWithStatementsAsOfToday() {
		return paymentConfigurationDAO.findAllCompanyAccountsByNextStatementDate(Calendar.getInstance());
	}

	@Override
	public Statement generateStatement(long companyId) {
		Company company = companyService.findCompanyById(companyId);
		Assert.notNull(company.getPaymentConfiguration());
		return generateStatement(company);
	}

	private Statement generateStatement(Company company) {
		if (company.getPaymentConfiguration() == null) {
			logger.error("Null payment configuration");
			return null;
		}

		PaymentConfiguration paymentConfiguration = company.getPaymentConfiguration();

		Calendar cycleStartDate = getNextStatementCycleStartDate(paymentConfiguration);
		Calendar cycleEndDate = getNextStatementCycleEndDate(paymentConfiguration);

		// correct the old statements so that they are now generated at 10 am GMT timezone
		cycleEndDate.setTimeZone(TimeZone.getTimeZone("GMT"));
		cycleEndDate.set(Calendar.HOUR_OF_DAY, 10);

		Calendar lastStatementSentOn = paymentConfiguration.getLastStatementSentOn();
		Statement statement = new Statement();

		if (lastStatementSentOn != null) {
			logger.debug(" ****** lastStatementSentOn " + DateUtilities.formatDateForEmail(lastStatementSentOn));
		}

		if (lastStatementSentOn != null) {
			logger.debug(" ****** DateUtilities.getMidnight(Calendar.getInstance()) " +
					DateUtilities.formatDateForEmail(DateUtilities.getMidnight(Calendar.getInstance())));

			if (DateUtilities.getDaysBetween(lastStatementSentOn, Calendar.getInstance(), false) <= 1) {
				logger.error("Statement has been already generated: " + company.getId());
				return null;
			}
		}
		//  Don't generate a statement if the payment cycle is not over yet
		if (cycleEndDate.compareTo(Calendar.getInstance()) > 0) {
			logger.error("There is no statement to generate for company id: " + company.getId());
			return null;
		}

		statement.setDescription("Statement from: " + DateUtilities.format(DATE_FORMAT, cycleStartDate) + " through: " + DateUtilities.format(DATE_FORMAT, cycleEndDate));
		statement.setPeriodStartDate(cycleStartDate);
		statement.setPeriodEndDate(cycleEndDate);
		statement.setCompany(company);
		String statementNumber = companyService.getNextStatementNumber(company.getId());
		logger.debug("Generating statement # " + statementNumber);
		statement.setInvoiceNumber(statementNumber);
		statementDAO.saveOrUpdate(statement);

		Calendar nextStatementDate = getNextStatementDate(cycleEndDate,
				paymentConfiguration.getPaymentCycle(),
				paymentConfiguration.getPreferredDayOfWeek(),
				paymentConfiguration.getPreferredDayOfMonth(),
				paymentConfiguration.isBiweeklyPaymentOnSpecificDayOfMonth(),
				paymentConfiguration.getBiweeklyPaymentDays());

		logger.debug(" ****** nextStatementDate " + DateUtilities.formatDateForEmail(nextStatementDate));

		paymentConfiguration.setLastStatementSentOn(cycleEndDate);
		paymentConfiguration.setNextStatementDate(nextStatementDate);
		return generateStatementDetails(cycleStartDate, cycleEndDate, statement);
	}

	private Statement generateStatementDetails(Calendar cycleStartDate, Calendar cycleEndDate, Statement statement) {
		AccountStatementFilters filters = new AccountStatementFilters();
		filters.setFromDate(cycleStartDate);
		filters.setToDate(cycleEndDate);
		filters.setPayables(true);
		AccountStatementDetailPagination pagination = new AccountStatementDetailPagination(true);
		pagination = accountStatementDetailDAO.findInvoices(Constants.WORKMARKET_SYSTEM_USER_ID, statement.getCompany().getId(), pagination, filters, true);
		Calendar dueDate = null;
		BigDecimal balance = BigDecimal.ZERO;
		BigDecimal paidBalance = BigDecimal.ZERO;

		for (AccountStatementDetailRow row : pagination.getResults()) {
			if (row.getWorkId() != null) {
				Work work = workService.findWork(row.getWorkId());
				// This should not be true, but just in case
				if (work != null) {
					if (work.hasStatement()) {
						logger.error("Assignment : " + row.getWorkId() + " is part of statement " + findStatementById(work.getStatementId()).getInvoiceNumber());
					} else if (work.getInvoice() != null) {
						Invoice invoice = work.getInvoice();
						statement.getInvoices().add(invoice);
						// Denormalizing the statement on work
						work.setStatementId(statement.getId());

						// Only if the invoice is pending payment we add the amount to the statement balance.
						if (invoice.isPaymentPending()) {
							if (dueDate == null || invoice.getDueDate().before(dueDate)) {
								dueDate = DateUtilities.cloneCalendar(invoice.getDueDate());
							}
						} else if (invoice.isPaid()) {
							paidBalance = paidBalance.add(invoice.getBalance());
						}
						balance = balance.add(invoice.getBalance());
					} else {
						logger.error("Assignment : " + row.getWorkId() + " without invoice");
					}
				} else {
					logger.error("Null Assignment : " + row.getWorkId());
				}
			}
		}

		if (dueDate == null) {
			dueDate = DateUtilities.cloneCalendar(cycleEndDate);
		}
		BigDecimal remainingBalance = balance.subtract(paidBalance);
		if (remainingBalance.compareTo(BigDecimal.ZERO) == 0) {
			statement.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.PAID));
			statement.setPaymentFulfillmentStatusType(new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.FULFILLED));
		}
		statement.setBalance(balance);
		statement.setRemainingBalance(remainingBalance);
		statement.setDueDate(dueDate);
		return statement;
	}

	@Override
	public Statement findStatementById(long statementId) {
		return statementDAO.findInvoiceById(statementId);
	}

	@Override
	public Statement payStatement(long statementId) throws InsufficientFundsException {
		Statement statement = statementDAO.findInvoiceById(statementId);
		Assert.notNull(statement);
		payInvoice(statement);
		return statement;
	}

	@Override
	public StatementPagination findAllStatements(StatementPagination pagination) {
		Assert.notNull(pagination);
		User user = authenticationService.getCurrentUser();
		return statementDAO.findAllStatements(user.getCompany().getId(), pagination);
	}

	@Override
	public InvoiceSummary addInvoiceToInvoiceSummary(long invoiceSummaryId, long invoiceId) {
		Invoice invoice = invoiceDAO.findInvoiceById(invoiceId);
		Assert.notNull(invoice, "Unable to find invoice");
		return addInvoiceToInvoiceSummary(invoiceSummaryId, invoice, true);
	}

	private <T extends InvoiceSummary> T addInvoicesToInvoiceSummary(long invoiceSummaryId, List<Invoice> invoices) {
		T invoiceSummary = (T) invoiceDAO.findInvoiceById(invoiceSummaryId);
		validateInvoice(invoiceSummary);
		Assert.state(invoiceSummary.isEditable(), "Invoice Summary is not editable");
		Assert.isTrue(!CollectionUtils.isEmpty(invoices), "Empty invoice ids");

		for (Invoice invoice : invoices) {
			addInvoiceToInvoiceSummary(invoiceSummaryId, invoice, false);
		}
		return generateInvoiceSummary(invoiceSummary);
	}


	private <T extends InvoiceSummary> T addInvoiceToInvoiceSummary(long invoiceSummaryId, Invoice invoice, boolean generateTotal) {
		Assert.isTrue(invoice.getType().equals(Invoice.INVOICE_TYPE), "Can't add an invoice bundle to a bundle");
		Assert.isTrue(!invoice.isBundled(), "Invoice is already part of a bundle");
		Assert.isTrue(!invoice.isPaid(), "Can't add a paid invoice to a bundle");

		T invoiceSummary = (T) invoiceDAO.findInvoiceById(invoiceSummaryId);
		validateInvoice(invoiceSummary);
		Assert.state(invoiceSummary.isEditable(), "Invoice Summary is not editable");

		Assert.state(invoice.getCompany().getId().equals(invoiceSummary.getCompany().getId()), "Invoice doesn't belong to the company");


		final InvoiceSummary parentSummary = invoiceDAO.findInvoiceSummaryByInvoiceBundledId(invoice.getId());
		final boolean hasOtherParentSummary = parentSummary != null && parentSummary.getId() != invoiceSummaryId;

		if (!invoiceSummary.getInvoices().contains(invoice) && !hasOtherParentSummary) {
			invoiceSummary.getInvoices().add(invoice);
			if (!(invoiceSummary instanceof InvoiceCollection)) {
				invoice.setBundled(true);
			}
		}

		if (generateTotal) {
			return generateInvoiceSummary(invoiceSummary);
		}
		return invoiceSummary;
	}

	@Override
	public InvoiceSummary removeInvoiceFromInvoiceSummary(long invoiceSummaryId, Long invoiceId) {
		Invoice invoice = invoiceDAO.findInvoiceById(invoiceId);
		Assert.notNull(invoice, "Unable to find invoice");
		return removeInvoiceFromInvoiceSummary(invoiceSummaryId, invoice);
	}

	private InvoiceSummary removeInvoiceFromInvoiceSummary(long invoiceSummaryId, Invoice invoice) {
		InvoiceSummary invoiceSummary = (InvoiceSummary) invoiceDAO.findInvoiceById(invoiceSummaryId);
		validateInvoice(invoiceSummary);
		Assert.state(invoiceSummary.isEditable(), "Invoice Summary is not editable");

		if (invoiceSummary.getInvoices().contains(invoice)) {
			Assert.isTrue(invoiceSummary.getInvoices().size() > 1, "Bundle should contain at least one invoice.");
			invoiceSummary.getInvoices().remove(invoice);

			// Don't change status if we're removing a 'void' invoice
			if (!InvoiceStatusType.VOID.equals(invoice.getInvoiceStatusType().getCode())) {
				invoice.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.PAYMENT_PENDING));
			}
			invoice.setBundled(false);
		}

		return generateInvoiceSummary(invoiceSummary);
	}

	public <T extends AbstractInvoice> T generateInvoiceSummary(T invoiceSummary) {
		Assert.isInstanceOf(AbstractInvoice.class, invoiceSummary);
		Calendar dueDate = null;
		BigDecimal balance = BigDecimal.ZERO;
		BigDecimal paidBalance = BigDecimal.ZERO;
		Set<Invoice> invoices;

		if (invoiceSummary instanceof Statement) {
			invoices = ((Statement) invoiceSummary).getInvoices();
		} else if (invoiceSummary instanceof InvoiceSummary) {
			invoices = ((InvoiceSummary) invoiceSummary).getInvoices();
		} else {
			return invoiceSummary;
		}

		for (Invoice invoice : invoices) {
			if (!invoice.isVoid() && !invoice.getDeleted()) {
				if (dueDate == null) {
					dueDate = DateUtilities.cloneCalendar(invoice.getDueDate());
				} else if (invoice.getDueDate().before(dueDate)) {
					dueDate = DateUtilities.cloneCalendar(invoice.getDueDate());
				}
				balance = balance.add(invoice.getBalance());
			}
			if (invoice.isPaid()) {
				paidBalance = paidBalance.add(invoice.getBalance());
			}
		}

		if (dueDate == null) {
			invoiceSummary.setPaymentFulfillmentStatusType(new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.FULFILLED));
			invoiceSummary.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.VOID));
			invoiceSummary.setVoidOn(DateUtilities.getCalendarNow());

		}

		invoiceSummary.setDueDate(dueDate);
		invoiceSummary.setBalance(balance);
		invoiceSummary.setRemainingBalance(balance.subtract(paidBalance));
		return invoiceSummary;
	}

	private <T extends AbstractInvoice> Map<String, List<ConstraintViolation>> payInvoice(T invoice) throws InsufficientFundsException {
		Assert.isInstanceOf(AbstractInvoice.class, invoice);
		if (!invoice.isPaymentPending()) {
			throw new InvoiceAlreadyPaidException("Invoice/Statement has been paid already");
		}
		Map<String, List<ConstraintViolation>> violations = Maps.newHashMap();

		if ((invoice instanceof Statement) || (invoice instanceof InvoiceSummary) || (invoice instanceof AbstractServiceInvoice)) {
			if (accountRegisterServicePrefund.payInvoice(invoice)) {
				invoice.setPaidBy(authenticationService.getCurrentUser());
				invoice.setPaymentDate(Calendar.getInstance());
				invoice.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.PAID));
				if (invoice instanceof AbstractServiceInvoice) {
					invoice.setRemainingBalance(BigDecimal.ZERO);
				}
			}
		} else {
			return violations;
		}

		// unlock company after they paid invoice
		if (invoice.getCompany() != null && invoice.getCompany().isLocked()) {
			eventRouter.sendEvent(new UnlockCompanyEvent(invoice.getCompany().getId()));
		}

		// check if there is still due/overdue invoices, if none, remove warning banner
		if (invoice.getCompany() != null) {
			eventRouter.sendEvent(new CompanyDueInvoicesEvent(invoice.getCompany().getId()));
		}

		return violations;
	}

	@Override
	public Company turnStatementsOff(long companyId) {
		Company company = companyService.findCompanyById(companyId);
		if (company != null && company.hasStatementsEnabled()) {
			Assert.isTrue(workService.countAllAssignmentsPaymentPendingByCompany(companyId) == 0, "There are assignments pending payment. Can't modify statement settings.");
			company.getManageMyWorkMarket().setStatementsEnabled(false);
			authenticationService.refreshSessionForCompany(companyId);
		}
		return company;
	}

	@Override
	public List<Integer> getAutoPayAssignmentList() {
		Calendar dueDate = DateUtilities.getMidnightTomorrowRelativeToTimezone(Constants.EST_TIME_ZONE);
		logger.debug("[autoPay] Looking for assignments with due date <= to " + DateUtilities.formatDateForEmail(dueDate));
		return workService.getAutoPayWorkIds(dueDate.getTime(), WorkStatusType.PAYMENT_PENDING_STATUS_TYPES);
	}

	@Override
	public List<Integer> getAutoPayInvoiceList() {
		Calendar dueDate = DateUtilities.getMidnightTomorrowRelativeToTimezone(Constants.EST_TIME_ZONE);
		logger.debug("[autoPay] Looking for invoices with due date <= to " + DateUtilities.formatDateForEmail(dueDate));
		return invoiceDAO.findAutoPayInvoices(dueDate.getTime(), InvoiceStatusType.PAYMENT_PENDING);
	}

	@Override
	public AccountTransactionReportRowPagination getPaymentPendingInvoicesRunningTotalsByCompany(DateFilter invoiceDueDateFilter, AccountTransactionReportRowPagination pagination) {
		Assert.notNull(pagination);
		Assert.notNull(invoiceDueDateFilter);
		Assert.notNull(invoiceDueDateFilter.getFromDate());
		Assert.notNull(invoiceDueDateFilter.getToDate());
		return accountStatementDetailDAO.getPaymentPendingInvoicesRunningTotalsByCompany(invoiceDueDateFilter.getFromDate(), invoiceDueDateFilter.getToDate(), pagination);
	}

	@Override
	public boolean voidWorkInvoice(Work work) {
		Assert.notNull(work);
		Assert.notNull(work.getInvoice());
		Assert.isTrue(!work.isPaid(), "Can't void an invoice that's already paid");

		if (!work.isPaid() && !work.getInvoice().isPaid()) {
			Invoice invoice = work.getInvoice();
			InvoiceSummary bundleInvoice = null;

			if (invoice.isBundled()) {
				bundleInvoice = invoiceDAO.findInvoiceSummaryByInvoiceBundledId(invoice.getId());
				Assert.notNull(bundleInvoice);
				if (!bundleInvoice.isEditable()) {
					return false;
				}
			}
			// update invoice
			invoice.setDueDate(null);
			invoice.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.VOID));
			invoice.setVoidOn(DateUtilities.getCalendarNow());
			invoice.setCancelPaymentWorkId(work.getId());

			if (bundleInvoice != null) {
				//Update the parent invoice
				generateInvoiceSummary(bundleInvoice);
			} else if (work.hasStatement()) {
				Statement statement = findStatementById(work.getStatementId());
				Assert.notNull(statement);
				generateInvoiceSummary(statement);
			}

			// update work
			work.setInvoice(null);
			work.setStatementId(null);
			return true;
		}
		return false;
	}

	private Calendar getNextStatementCycleStartDate(PaymentConfiguration paymentConfiguration) {
		Calendar cycleStartDate;
		if (paymentConfiguration.getLastStatementSentOn() != null) {
			cycleStartDate = paymentConfiguration.getLastStatementSentOn();
		} else {
			cycleStartDate = paymentConfiguration.getStartDatePaymentCycle();
		}

		return cycleStartDate;
	}

	private Calendar getNextStatementCycleEndDate(PaymentConfiguration paymentConfiguration) {
		Assert.notNull(paymentConfiguration);
		return paymentConfiguration.getNextStatementDate();
	}

	@Override
	public String getServiceInvoicePdfView(long invoiceId) {
		AbstractServiceInvoice invoice = serviceInvoiceDAO.findInvoiceById(invoiceId);
		if (invoice != null) {
			return getServiceInvoicePdfView(invoice);
		}
		return StringUtils.EMPTY;
	}

	private String getServiceInvoicePdfView(AbstractServiceInvoice invoice) {
		if (validateAccessToInvoice(invoice)) {
			updateInvoiceLastDownloadedDate(invoice.getId(), DateUtilities.getCalendarNow());
			SubscriptionConfiguration subscriptionConfiguration = subscriptionService.findActiveSubscriptionConfigurationByCompanyId(invoice.getCompany().getId());
			if (subscriptionConfiguration != null && invoice instanceof SubscriptionInvoice && StringUtilities.isNotEmpty(subscriptionConfiguration.getClientRefId())) {
				((SubscriptionInvoice) invoice).setClientRefId(subscriptionConfiguration.getClientRefId());
			}
			return templateService.renderPDFTemplate(PDFTemplateFactory.newServiceInvoicePDFTemplate(invoice), invoice);
		}
		return StringUtils.EMPTY;
	}

	private SubscriptionPaymentPeriod createSubscriptionPaymentPeriod(Calendar paymentPeriodDate) {

		// Create DateRange with month interval
		// i.e. turn 07/31/2016 into 07/01/2016 - 08/01/2016
		Calendar paymentPeriodStart = (Calendar)paymentPeriodDate.clone();
		paymentPeriodStart.set(Calendar.DAY_OF_MONTH, 1);

		Calendar paymentPeriodEnd = (Calendar)paymentPeriodDate.clone();
		paymentPeriodEnd.add(Calendar.MONTH, 1);
		paymentPeriodEnd.set(Calendar.DAY_OF_MONTH, 1);

		SubscriptionPaymentPeriod paymentPeriod = new SubscriptionPaymentPeriod();
		paymentPeriod.setPeriodDateRange(new DateRange(paymentPeriodStart, paymentPeriodEnd));

		paymentPeriod.setSubscriptionPeriodType(new SubscriptionPeriodType(SubscriptionPeriodType.ADHOC));

		return paymentPeriod;
	}

	@Override
	public AdHocInvoice issueAdHocInvoice(InvoiceDTO invoiceDTO) {
		Assert.notNull(invoiceDTO);
		Assert.notNull(invoiceDTO.getCompanyId());
		Assert.notNull(invoiceDTO.getDueDate());
		Assert.notNull(invoiceDTO.getLineItemDTOList());
		Assert.notEmpty(invoiceDTO.getLineItemDTOList());
		Assert.isTrue(DateUtilities.isInFuture(invoiceDTO.getDueDate()));

		Company company = companyService.findCompanyById(invoiceDTO.getCompanyId());
		Assert.notNull(company);

		Calendar dueDate = DateUtilities.getCalendarWithLastMinuteOfDay(invoiceDTO.getDueDate(), Constants.EST_TIME_ZONE);

		PaymentPeriod paymentPeriod;
		if (invoiceDTO.isSubscriptionInvoice()) {
			Assert.notNull(invoiceDTO.getPaymentPeriod());

			// For companies with active, configured subscription plans
			if (Sets.newHashSet(SubscriptionInvoiceType.REGULAR, SubscriptionInvoiceType.INCREMENTAL, SubscriptionInvoiceType.OVERAGE).contains(invoiceDTO.getSubscriptionInvoiceTypeCode())) {
				Map<String, Object> currentSubscriptionDetails = subscriptionCalculator.getCurrentSubscriptionDetails(company.getId());
				if (currentSubscriptionDetails.isEmpty()) {
					throw new IllegalArgumentException("Company has no active subscription. Please create subscription before submitting an adhoc subscription invoice.");
				}

				paymentPeriod = createSubscriptionPaymentPeriod(invoiceDTO.getPaymentPeriod());

				((SubscriptionPaymentPeriod)paymentPeriod).setSubscriptionConfiguration(
					subscriptionConfigurationDAO.get((Long)currentSubscriptionDetails.get("subscriptionConfigurationId"))
				);
				((SubscriptionPaymentPeriod)paymentPeriod).setSubscriptionFeeConfigurationId(
					(Long)currentSubscriptionDetails.get("subscriptionFeeConfigurationId")
				);

				if (currentSubscriptionDetails.get("currentTierId") != null) {
					((SubscriptionPaymentPeriod)paymentPeriod).setSubscriptionPaymentTierSWId(
						(Long)currentSubscriptionDetails.get("currentTierId")
					);
				}
				if (currentSubscriptionDetails.get("currentTierVORId") != null) {
					((SubscriptionPaymentPeriod)paymentPeriod).setSubscriptionPaymentTierVORId(
						(Long)currentSubscriptionDetails.get("currentTierVORId")
					);
				}

			// For companies with no active, configured subscription plan
			} else if (Sets.newHashSet(SubscriptionInvoiceType.RAMP, SubscriptionInvoiceType.PILOT).contains(invoiceDTO.getSubscriptionInvoiceTypeCode())) {
				paymentPeriod = createSubscriptionPaymentPeriod(invoiceDTO.getPaymentPeriod());
			} else {
				throw new IllegalArgumentException("Attempted to save Adhoc Subscription invoice with an invalid subscription invoice type.");
			}

		} else {
			paymentPeriod = new PaymentPeriod();
			paymentPeriod.setPeriodDateRange(new DateRange(Calendar.getInstance(), dueDate));
		}

		paymentPeriodDAO.saveOrUpdate(paymentPeriod);

		AdHocInvoice invoice = new AdHocInvoice(company);
		invoice.setInvoiceNumber(getNextWorkMarketInvoiceNumber(WMInvoiceNumberType.WORK_MARKET_INC_INVOICE));
		invoice.setDueDate(dueDate);
		invoice.setPaymentPeriod(paymentPeriod);
		if (invoiceDTO.getSubscriptionInvoiceTypeCode() != null) {
			invoice.setSubscriptionInvoiceType(SubscriptionInvoiceType.newSubscriptionInvoiceType(invoiceDTO.getSubscriptionInvoiceTypeCode()));
		}
		serviceInvoiceDAO.saveOrUpdate(invoice);

		addInvoiceLineItems(invoice, invoiceDTO.getLineItemDTOList());
		validateAdHocInvoice(invoice);
		userNotificationService.onNewInvoice(invoice);

		return invoice;
	}

	private void validateAdHocInvoice(AdHocInvoice invoice) {
		// Due date is in the future
		Assert.isTrue(DateUtilities.isInFuture(invoice.getDueDate()));
		// There's at least 1 invoice line item
		Assert.isTrue(!invoice.getInvoiceLineItems().isEmpty());
		for (InvoiceLineItem lineItem : invoice.getInvoiceLineItems()) {
			// Amount is positive and has a scale of 2 digits
			Assert.isTrue(BigDecimal.ZERO.compareTo(lineItem.getAmount()) < 0);
			Assert.isTrue(lineItem.getAmount().scale() <= 2);
		}
	}

	@Override
	public <T extends AbstractServiceInvoice> T addInvoiceLineItems(T invoice, List<InvoiceLineItemDTO> invoiceLineItemDTOs) {
		Assert.notNull(invoice);
		for (InvoiceLineItemDTO invoiceLineItemDTO : invoiceLineItemDTOs) {
			InvoiceLineItem invoiceLineItem = invoiceLineItemFactory.newInvoiceLineItem(invoiceLineItemDTO);
			invoiceLineItem.setInvoice(invoice);
			ServiceTransaction transaction = accountRegisterServicePrefund.createInvoiceItemRegisterTransaction(invoice.getCompany(), invoiceLineItem, true);
			invoiceLineItem.setRegisterTransaction(transaction);
			invoiceLineItem.setTransactionDate(transaction.getTransactionDate());
			invoiceLineItemDAO.saveOrUpdate(invoiceLineItem);
			invoice.getInvoiceLineItems().add(invoiceLineItem);
			transaction.setInvoiced(true);
			transaction.setInvoicedOn(Calendar.getInstance());
		}
		return generateServiceInvoiceSummary(invoice);
	}

	/**
	 * Create InvoiceLineItems (and register transactions) for the given invoiceLineItemsDTOs and add them to the
	 * given Credit Memo
	 */
	private <T extends AbstractServiceInvoice> T addCreditMemoInvoiceLineItems(
			T creditMemo, List<InvoiceLineItemDTO> invoiceLineItemDTOs, boolean paid) {

		Assert.notNull(creditMemo);
		for (InvoiceLineItemDTO invoiceLineItemDTO : invoiceLineItemDTOs) {
			InvoiceLineItem lineItem = invoiceLineItemFactory.newInvoiceLineItem(invoiceLineItemDTO);

			if(lineItem instanceof CreditMemoIssuableInvoiceLineItem) {

				CreditMemoIssuableInvoiceLineItem creditMemoIssuable = (CreditMemoIssuableInvoiceLineItem)lineItem;
				creditMemoIssuable.setInvoice(creditMemo);
				creditMemoIssuable.setTransactionDate(DateUtilities.getCalendarNow());

				CreditMemoTransaction transaction =
						accountRegisterServicePrefund.createCreditMemoInvoiceItemRegisterTransaction(
								creditMemo.getCompany(), creditMemoIssuable, paid);

				creditMemoIssuable.setRegisterTransaction(transaction);

				invoiceLineItemDAO.saveOrUpdate(creditMemoIssuable);
				creditMemo.getInvoiceLineItems().add(creditMemoIssuable);
			}
		}
		return generateServiceInvoiceSummary(creditMemo);
	}

	@Override
	public <T extends AbstractServiceInvoice> T generateServiceInvoiceSummary(T invoiceSummary) {
		BigDecimal balance = BigDecimal.ZERO;
		Set<InvoiceLineItem> invoiceLineItems = invoiceSummary.getInvoiceLineItems();

		for (InvoiceLineItem lineItem : invoiceLineItems) {
			if (!lineItem.getDeleted()) {
				balance = balance.add(lineItem.getAmount());
			}
		}
		invoiceSummary.setBalance(balance);
		invoiceSummary.setRemainingBalance(balance);
		return invoiceSummary;
	}

	@Override
	public String getNextWorkMarketInvoiceNumber(String invoiceNumberTypeCode) {
		Assert.hasText(invoiceNumberTypeCode);
		WMInvoiceNumberType invoiceNumberType = lookupEntityDAO.findByCode(WMInvoiceNumberType.class, invoiceNumberTypeCode);
		if (invoiceNumberType != null) {
			invoiceNumberType.setLastInvoiceNumber(invoiceNumberType.getLastInvoiceNumber() + 1);
			return invoiceNumberType.getPrefix() + "-" + String.format("%05d", invoiceNumberType.getLastInvoiceNumber());
		}
		return StringUtils.EMPTY;
	}

	public List<ProjectInvoiceBundle> groupInvoicesByProject(List<Long> invoiceIds, Long companyId) {
		List<? extends AbstractInvoice> invoices = findInvoicesById(invoiceIds);
		return invoicePaymentHelper.groupInvoicesByProject(invoices, companyId);
	}

	public List<? extends AbstractInvoice> findInvoicesWithoutProjectBudget(List<Long> invoiceIds, Long companyId) {
		List<? extends AbstractInvoice> invoices = findInvoicesById(invoiceIds);
		return invoicePaymentHelper.findInvoicesWithoutProjectBudget(invoices, companyId);
	}

	public BigDecimal findGeneralTotalDue(List<Long> invoiceIds, Long companyId) {
		List<? extends AbstractInvoice> invoices = findInvoicesById(invoiceIds);
		return invoicePaymentHelper.calculateTotalToPayFromGeneralCash(invoices, companyId);
	}

	@Override
	public CreditMemo issueCreditMemo(Long serviceInvoiceId) {

		AbstractServiceInvoice subscriptionInvoice = serviceInvoiceDAO.findInvoiceById(serviceInvoiceId);

		CreditMemo creditMemo = createCreditMemo(subscriptionInvoice);

		if (subscriptionInvoice.isPaid()) {

			// Create invoice_payment_transaction record
			createCreditMemoInvoicePaymentTransaction(creditMemo, subscriptionInvoice);
		} else {
			// Set Service invoice to paid without moving money
			subscriptionInvoice.setPaidBy(authenticationService.getCurrentUser());
			subscriptionInvoice.setPaymentDate(Calendar.getInstance());
			subscriptionInvoice.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.PAID));
			subscriptionInvoice.setRemainingBalance(BigDecimal.ZERO);
			serviceInvoiceDAO.saveOrUpdate(subscriptionInvoice);
		}

		creditMemo.markAsPaid(authenticationService.getCurrentUser(), DateUtilities.getCalendarNow());
		return creditMemo;
	}

	/**
	 * Create CreditMemo invoice with line items/transactions matching those in the original subscription invoice.
	 */
	private CreditMemo createCreditMemo(AbstractServiceInvoice subscriptionInvoice) {

		CreditMemo creditMemo = new CreditMemo(subscriptionInvoice);

		CreditMemoAudit creditMemoAudit = new CreditMemoAudit()
			.setCreditMemo(creditMemo)
			.setServiceInvoice(subscriptionInvoice)
			.setCreatedOn(DateUtilities.getCalendarNow());

		creditMemo.setCreditMemoAudit(creditMemoAudit)
			.setPaymentPeriod(subscriptionInvoice.getPaymentPeriod())
			.setPaymentDate(DateUtilities.getCalendarNow())
			.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.PAID))
			.setInvoiceNumber(getNextWorkMarketInvoiceNumber(WMInvoiceNumberType.WORK_MARKET_INC_INVOICE));

		creditMemoDAO.saveOrUpdate(creditMemo);
		creditMemoAuditDAO.saveOrUpdate(creditMemoAudit);

		List<InvoiceLineItemDTO> lineItemList = getCreditMemoLineItems(subscriptionInvoice);

		Assert.isTrue(lineItemList.size() == subscriptionInvoice.getInvoiceLineItems().size(),
			"Credit memos can only be issued for subscription invoice items");

		addCreditMemoInvoiceLineItems(creditMemo, lineItemList, subscriptionInvoice.isPaid());

		return creditMemo;
	}

	private List<InvoiceLineItemDTO> getCreditMemoLineItems(AbstractServiceInvoice serviceInvoice) {

		Set<InvoiceLineItem> lineItems = serviceInvoice.getInvoiceLineItems();

		List<InvoiceLineItemDTO> lineItemDTOList = Lists.newArrayList();

		for (InvoiceLineItem lineItem : lineItems) {

			if (lineItem instanceof CreditMemoIssuableInvoiceLineItem) {
				CreditMemoIssuableInvoiceLineItem creditMemoIssuable = (CreditMemoIssuableInvoiceLineItem) lineItem;
				lineItemDTOList.add(new InvoiceLineItemDTO(creditMemoIssuable.getInvoiceLineItemType()).setAmount(creditMemoIssuable.getAmount()));
			}
		}
		return lineItemDTOList;
	}

	/**
	 * Create an invoice_payment_transaction record so that the credit memo appears in the ledger as a credit against
	 * an invoice.
	 */
	private InvoicePaymentTransaction createCreditMemoInvoicePaymentTransaction(
			CreditMemo creditMemo, AbstractServiceInvoice subscriptionInvoice) {

		Assert.isTrue(subscriptionInvoice.isPaid(),
			"We only create InvoicePaymentTransactions for credit memos if the original invoice was paid");

		InvoicePaymentTransaction creditMemoPaymentTransaction = new InvoicePaymentTransaction();
		creditMemoPaymentTransaction.setInvoice(creditMemo);
		AccountRegister register =
			accountRegisterServicePaymentTerms.findDefaultRegisterForCompany(subscriptionInvoice.getCompany().getId());

		RegisterTransactionExecutor registerTransactionsAbstract =
			registerTransactionExecutableFactory.newInvoicePaymentRegisterTransaction(creditMemo);

		// Perform same operations as registerTransactionsAbstract.execute but don't update balances
		// We need an invoice_payment_transaction in order for Credit Memos (paid invoices only) to appear in the Ledger
		creditMemoPaymentTransaction.setAccountRegister(register);
		creditMemoPaymentTransaction.setTransactionDate(Calendar.getInstance());
		creditMemoPaymentTransaction.setRegisterTransactionType(registerTransactionsAbstract.getRegisterTransactionType());
		creditMemoPaymentTransaction.setPendingFlag(false);
		creditMemoPaymentTransaction.setEffectiveDate(Calendar.getInstance());
		creditMemoPaymentTransaction.setAmount(creditMemo.getBalance());
		AccountRegisterSummaryFields accountRegisterSummaryFields = new AccountRegisterSummaryFields();
		BeanUtilities.copyProperties(accountRegisterSummaryFields, register.getAccountRegisterSummaryFields());
		creditMemoPaymentTransaction.setAccountRegisterSummaryFields(accountRegisterSummaryFields);
		registerTransactionDAO.saveOrUpdate(creditMemoPaymentTransaction);

		return creditMemoPaymentTransaction;
	}

	@Override
	public boolean isCreditMemoIssuable(long invoiceId) {
		return this.invoiceDAO.isCreditMemoIssuable(invoiceId);
	}

	@Override
	public void updateInvoiceLastDownloadDate(List<Long> invoiceIds) {
		InvoicesDownloadedEvent invoicesDownloadedEvent = new InvoicesDownloadedEvent(invoiceIds, authenticationService.getCurrentUser().getId());
		eventRouter.sendEvent(invoicesDownloadedEvent);
	}
}
