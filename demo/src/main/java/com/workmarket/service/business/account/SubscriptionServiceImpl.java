package com.workmarket.service.business.account;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.dao.LookupEntityDAO;
import com.workmarket.dao.account.pricing.CompanyAccountPricingTypeChangeDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionAccountServiceTypeConfigurationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionAddOnTypeAssociationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionCancellationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionConfigurationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionFeeConfigurationDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionPaymentTierDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionPaymentTierRenewalDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionRenewalRequestDAO;
import com.workmarket.dao.account.pricing.subscription.SubscriptionThroughputIncrementTransactionDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.note.NoteDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.Pagination.SORT_DIRECTION;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.ServiceTransaction;
import com.workmarket.domains.model.account.TransactionStatus;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.CompanyAccountPricingTypeChange;
import com.workmarket.domains.model.account.pricing.SubscriptionAccountServiceTypeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.AbstractPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionAddOnType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionAddOnTypeAssociation;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionCancellation;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionFeeConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTierRenewal;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTierStatusType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionRenewalRequest;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionStatusType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionThroughputIncrementTransaction;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.invoice.SubscriptionInvoiceType;
import com.workmarket.domains.model.invoice.WMInvoiceNumberType;
import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import com.workmarket.domains.model.note.SubscriptionNote;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.dao.InvoiceLineItemDAO;
import com.workmarket.domains.payments.dao.PaymentPeriodDAO;
import com.workmarket.domains.payments.dao.ServiceInvoiceDAO;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionAddOnDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionCancelDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionConfigurationDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentTierDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionRenewalRequestDTO;
import com.workmarket.service.business.dto.invoice.SubscriptionInvoiceDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Author: rocio
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	private static final Log logger = LogFactory.getLog(SubscriptionServiceImpl.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private SubscriptionFeeConfigurationDAO subscriptionFeeConfigurationDAO;
	@Autowired private SubscriptionConfigurationDAO subscriptionConfigurationDAO;
	@Autowired private SubscriptionAddOnTypeAssociationDAO subscriptionAddOnTypeAssociationDAO;
	@Autowired private NoteDAO noteDAO;
	@Autowired private LookupEntityDAO lookupEntityDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private ServiceInvoiceDAO serviceInvoiceDAO;
	@Autowired private InvoiceLineItemDAO invoiceLineItemDAO;
	@Autowired private PaymentPeriodDAO paymentPeriodDAO;
	@Autowired private InvoiceLineItemFactory invoiceLineItemFactory;
	@Autowired private CompanyAccountPricingTypeChangeDAO companyAccountPricingTypeChangeDAO;
	@Autowired private SubscriptionPaymentTierDAO subscriptionPaymentTierDAO;
	@Autowired
	@Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterService;
	@Autowired private SubscriptionThroughputIncrementTransactionDAO subscriptionThroughputIncrementInvoiceDAO;
	@Autowired private SubscriptionCancellationDAO subscriptionCancellationDAO;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private SubscriptionCalculator subscriptionCalculator;
	@Autowired private SubscriptionAccountServiceTypeConfigurationDAO subscriptionAccountServiceTypeConfigurationDAO;
	@Autowired private SubscriptionRenewalRequestDAO subscriptionRenewalRequestDAO;
	@Autowired private BillingService billingService;
	@Autowired private AccountPricingService accountPricingService;
	@Autowired private SubscriptionPaymentTierRenewalDAO subscriptionPaymentTierRenewalDAO;
	@Autowired private PricingService pricingService;

	@Override
	public Set<SubscriptionConfiguration> findSubscriptionsPendingInvoiceByPaymentPeriodStartDate(Calendar startDate) {
		Assert.notNull(startDate);
		return subscriptionConfigurationDAO.findSubscriptionsPendingInvoiceByPaymentPeriodStartDate(startDate);
	}

	@Override
	public Set<SubscriptionConfiguration> findSubscriptionRenewalsPendingInvoiceByPaymentPeriodStartDate(Calendar startDate) {
		Assert.notNull(startDate);
		return subscriptionConfigurationDAO.findSubscriptionRenewalsPendingInvoiceByPaymentPeriodStartDate(startDate);
	}

	@Override
	public List<SubscriptionThroughputIncrementTransaction> findAllSubmittedSubscriptionThroughputIncrementTxs() {
		List<SubscriptionThroughputIncrementTransaction> subscriptionThroughputIncrementTransactions = subscriptionThroughputIncrementInvoiceDAO.findAllSubscriptionThroughputIncrementTxsByStatus(TransactionStatus.SUBMITTED);
		logger.debug("Found " + subscriptionThroughputIncrementTransactions.size() + " transactions");
		return subscriptionThroughputIncrementTransactions;
	}

	private SubscriptionInvoice saveOrUpdateSubscriptionInvoice(SubscriptionInvoiceDTO subscriptionInvoiceDTO, SubscriptionPaymentPeriod paymentPeriod) {
		Assert.notNull(subscriptionInvoiceDTO);
		SubscriptionInvoice invoice;
		if (subscriptionInvoiceDTO.getInvoiceId() != null) {
			invoice = (SubscriptionInvoice)serviceInvoiceDAO.findInvoiceById(subscriptionInvoiceDTO.getInvoiceId());
			if (invoice == null) {
				return invoice;
			}
			if (invoice.isEditable()) {
				Assert.hasText(subscriptionInvoiceDTO.getDescription(), "Description can't be empty");
				invoice.setDescription(subscriptionInvoiceDTO.getDescription());
			}
		} else {
			invoice = createNewSubscriptionInvoice(subscriptionInvoiceDTO, paymentPeriod);
		}

		if (invoice.isEditable()) {
			return billingService.addInvoiceLineItems(invoice, subscriptionInvoiceDTO.getLineItemDTOList());
		}

		return invoice;
	}

	private SubscriptionInvoice createNewSubscriptionInvoice(SubscriptionInvoiceDTO subscriptionInvoiceDTO, SubscriptionPaymentPeriod paymentPeriod) {
		Assert.notNull(subscriptionInvoiceDTO.getCompanyId());
		Assert.hasText(subscriptionInvoiceDTO.getDescription(), "Description can't be empty");

		Company company = companyDAO.get(subscriptionInvoiceDTO.getCompanyId());
		Assert.notNull(company);
		SubscriptionInvoice invoice = new SubscriptionInvoice(company);
		invoice.setInvoiceNumber(billingService.getNextWorkMarketInvoiceNumber(WMInvoiceNumberType.WORK_MARKET_INC_INVOICE));
		invoice.setPaymentPeriod(paymentPeriod);

		invoice.setDescription(subscriptionInvoiceDTO.getDescription());
		invoice.setDueDate(subscriptionInvoiceDTO.getDueDate());

		if (subscriptionInvoiceDTO.getSubscriptionInvoiceType() == null) {
			invoice.setSubscriptionInvoiceType(SubscriptionInvoiceType.newSubscriptionInvoiceType(SubscriptionInvoiceType.AD_HOC));
		} else {
			invoice.setSubscriptionInvoiceType(subscriptionInvoiceDTO.getSubscriptionInvoiceType());
		}
		serviceInvoiceDAO.saveOrUpdate(invoice);
		return invoice;
	}

	@Override
	public SubscriptionInvoice issueFutureSubscriptionInvoice(long subscriptionPaymentPeriodId) {
		SubscriptionPaymentPeriod paymentPeriod = (SubscriptionPaymentPeriod) paymentPeriodDAO.get(subscriptionPaymentPeriodId);
		Assert.notNull(paymentPeriod, "Unable to find the payment period");
		Assert.isNull(paymentPeriod.getSubscriptionInvoice(), "An invoice has been already created for this period");
		return newSubscriptionInvoiceFromSubscriptionPaymentPeriod(SubscriptionInvoiceType.FUTURE, paymentPeriod);
	}

	@Override
	public SubscriptionInvoice issueRegularSubscriptionInvoice(long subscriptionConfigurationId) {
		SubscriptionPaymentPeriod paymentPeriod = paymentPeriodDAO.findNextNotInvoicedSubscriptionPaymentPeriod(subscriptionConfigurationId);
		if (paymentPeriod == null) {
			logger.debug("No payment period to invoiced found for subscriptionConfigurationId " + subscriptionConfigurationId);
			return null;
		}
		return newSubscriptionInvoiceFromSubscriptionPaymentPeriod(SubscriptionInvoiceType.REGULAR, paymentPeriod);
	}

	@Override
	public SubscriptionInvoice issueRegularInvoiceableSubscriptionInvoice(long subscriptionConfigurationId, Calendar firstDayOfNextMonth) {
		SubscriptionPaymentPeriod paymentPeriod = paymentPeriodDAO.findNextInvoiceableSubscriptionPaymentPeriod(subscriptionConfigurationId, firstDayOfNextMonth);
		if (paymentPeriod == null) {
			logger.debug("No payment period to invoice found for subscriptionConfigurationId " + subscriptionConfigurationId);
			return null;
		}
		return newSubscriptionInvoiceFromSubscriptionPaymentPeriod(SubscriptionInvoiceType.REGULAR, paymentPeriod);
	}

	@Override
	public Calendar findNextPossibleSubscriptionUpdateDate(long companyId) {
		Assert.notNull(companyId);
		SubscriptionConfiguration subscription = subscriptionConfigurationDAO.findActiveSubscriptionConfigurationByCompanyId(companyId);
		if (subscription != null) {
			SubscriptionPaymentPeriod nextPaymentPeriod = paymentPeriodDAO.findNextNotInvoicedSubscriptionPaymentPeriod(subscription.getId());
			if (nextPaymentPeriod != null) {
				return DateUtilities.cloneCalendar(nextPaymentPeriod.getPeriodDateRange().getFrom());
			}
		}
		return null;
	}

	@Override
	public SubscriptionInvoice issueIncrementalSubscriptionInvoice(List<Long> subscriptionTransactionIds) {
		if (isEmpty(subscriptionTransactionIds)) {
			return null;
		}

		List<ServiceTransaction> invalidTransactions = Lists.newArrayList();
		List<Long> differentPaymentPeriodTransactions = Lists.newArrayList();
		//Get the first transaction on the list
		ServiceTransaction tx = (ServiceTransaction) accountRegisterService.findRegisterTransaction(CollectionUtilities.first(subscriptionTransactionIds));
		//Reload the account register since it was called from a non-transactional service
		AccountRegister accountRegister = accountRegisterService.getAccountRegisterById(tx.getAccountRegister().getId());
		Assert.notNull(accountRegister);
		SubscriptionPaymentPeriod paymentPeriod = (SubscriptionPaymentPeriod) paymentPeriodDAO.get(tx.getPaymentPeriod().getId());
		Assert.notNull(paymentPeriod);

		Calendar dueDate = Calendar.getInstance();
		dueDate.add(Calendar.DATE, paymentPeriod.getSubscriptionConfiguration().getPaymentTermsDays());

		SubscriptionInvoiceType subscriptionInvoiceType = lookupEntityDAO.findByCode(SubscriptionInvoiceType.class, SubscriptionInvoiceType.INCREMENTAL);
		final String description = subscriptionInvoiceType.getDescription();
		SubscriptionInvoiceDTO invoiceDTO = new SubscriptionInvoiceDTO();
		invoiceDTO.setCompanyId(accountRegister.getCompany().getId());
		invoiceDTO.setDescription(description);
		invoiceDTO.setDueDate(dueDate);
		invoiceDTO.setSubscriptionInvoiceType(subscriptionInvoiceType);

		//Create the invoice
		SubscriptionInvoice invoice = createNewSubscriptionInvoice(invoiceDTO, paymentPeriod);

		//Add the line items
		for (long registerTxId : subscriptionTransactionIds) {
			ServiceTransaction transaction = (ServiceTransaction) accountRegisterService.findRegisterTransaction(registerTxId);
			if (transaction.getAccountRegister().getId().equals(accountRegister.getId())) {
				if (transaction.getPaymentPeriod().getId().equals(paymentPeriod.getId())) {
					InvoiceLineItem invoiceLineItem = invoiceLineItemFactory.newSubscriptionInvoiceLineItem(transaction);
					invoiceLineItem.setInvoice(invoice);
					invoiceLineItemDAO.saveOrUpdate(invoiceLineItem);
					invoice.getInvoiceLineItems().add(invoiceLineItem);
					transaction.setInvoiced(true);
					transaction.setInvoicedOn(Calendar.getInstance());
				} else {
					differentPaymentPeriodTransactions.add(transaction.getId());
				}
			} else {
				invalidTransactions.add(transaction);
			}
		}

		billingService.generateServiceInvoiceSummary(invoice);
		userNotificationService.onNewInvoice(invoice);

		if (isNotEmpty(differentPaymentPeriodTransactions)) {
			return issueIncrementalSubscriptionInvoice(differentPaymentPeriodTransactions);
		}
		return invoice;
	}

	@Override
	public void processThroughputIncrementTransaction(SubscriptionThroughputIncrementTransaction throughputIncrementTx) throws IllegalStateException {
		Assert.notNull(throughputIncrementTx);
		Assert.notNull(throughputIncrementTx.getTriggeredByRegisterTransaction());

		//Load the object again to avoid the lazy loading exceptions
		WorkResourceTransaction workResourceTransaction = (WorkResourceTransaction) accountRegisterService.findRegisterTransaction(throughputIncrementTx.getTriggeredByRegisterTransaction().getId());
		SubscriptionPaymentTier transactionPaymentTier = subscriptionPaymentTierDAO.get(throughputIncrementTx.getSubscriptionPaymentTier().getId());

		boolean isSoftwareIncrement = throughputIncrementTx.isSoftwareIncrement();
		boolean isVORIncrement = throughputIncrementTx.isVorIncrement();

		if (workResourceTransaction != null && transactionPaymentTier != null) {

			Company company = workResourceTransaction.getWork().getCompany();

			SubscriptionFeeConfiguration feeConfiguration = transactionPaymentTier.getSubscriptionFeeConfiguration();
			Calendar transactionDate = DateUtilities.cloneCalendar(workResourceTransaction.getTransactionDate());
			SubscriptionConfiguration subscriptionConfiguration = feeConfiguration.getSubscriptionConfiguration();
			if (subscriptionConfiguration == null) {
				throw new IllegalStateException("[subscription] Invalid subscription configuration for increment transaction id: " + throughputIncrementTx.getId());
			}

			SubscriptionPaymentPeriod currentPaymentPeriod = paymentPeriodDAO.findBySubscriptionConfigurationIdAndDateInRange(subscriptionConfiguration.getId(), transactionDate);
			if (currentPaymentPeriod == null) {
				throw new IllegalStateException("[subscription] Invalid payment period for increment transaction id: " + throughputIncrementTx.getId());
			}

			BigDecimal throughput = workResourceTransaction.getAccountRegisterSummaryFields().getAssignmentSoftwareThroughput();

			SubscriptionPaymentDTO paymentDTO = subscriptionCalculator.calculateIncrementalSubscriptionPayment(subscriptionConfiguration, transactionDate, transactionPaymentTier, isSoftwareIncrement, isVORIncrement);
			accountRegisterService.createSubscriptionIncrementalTransactions(subscriptionConfiguration.getCompany(), currentPaymentPeriod, paymentDTO, true);

			/**
			 * Switch the payment tiers.
			 * The active becomes REACHED and the PROCESSING becomes ACTIVE
			 **/
			if (isSoftwareIncrement) {
				SubscriptionPaymentTier activeSubscriptionPaymentTier = subscriptionPaymentTierDAO.findActiveSubscriptionPaymentTier(subscriptionConfiguration.getId(), SubscriptionPaymentTier.PaymentTierCategory.SOFTWARE);
				activeSubscriptionPaymentTier.setSubscriptionPaymentTierSoftwareStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.REACHED));
				transactionPaymentTier.setSubscriptionPaymentTierSoftwareStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.ACTIVE));
				// Send throughput usage emails for the old tier - 100% reached
				userNotificationService.onSubscriptionPaymentTierThroughputReached(subscriptionConfiguration, activeSubscriptionPaymentTier, throughput);
			}

			if (isVORIncrement) {
				SubscriptionPaymentTier activeSubscriptionPaymentTier = subscriptionPaymentTierDAO.findActiveSubscriptionPaymentTier(subscriptionConfiguration.getId(), SubscriptionPaymentTier.PaymentTierCategory.VENDOR_OF_RECORD);
				activeSubscriptionPaymentTier.setSubscriptionPaymentTierVorStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.REACHED));
				transactionPaymentTier.setSubscriptionPaymentTierVorStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.ACTIVE));
			}

			throughputIncrementTx.setTransactionStatus(new TransactionStatus(TransactionStatus.PROCESSED));
			subscriptionThroughputIncrementInvoiceDAO.saveOrUpdate(throughputIncrementTx);

			List<SubscriptionPaymentPeriod> futurePaymentPeriods = paymentPeriodDAO.findAllInvoicedFutureSubscriptionPaymentPeriods(subscriptionConfiguration.getId());
			for (SubscriptionPaymentPeriod paymentPeriod : futurePaymentPeriods) {
				SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateIncrementalSubscriptionPaymentForFutureInvoice(company.getId(), paymentPeriod, throughput, feeConfiguration, isSoftwareIncrement, isVORIncrement);
				accountRegisterService.createSubscriptionIncrementalTransactions(subscriptionConfiguration.getCompany(), paymentPeriod, subscriptionPaymentDTO, true);
			}

		} else {
			throw new IllegalStateException("Failed to create Incremental Subscription Invoice, tx " + throughputIncrementTx.toString());
		}
	}

	SubscriptionInvoice newSubscriptionInvoiceFromSubscriptionPaymentPeriod(String subscriptionInvoiceTypeCode, SubscriptionPaymentPeriod paymentPeriod) {
		Assert.notNull(paymentPeriod);
		Assert.notNull(paymentPeriod.getSubscriptionConfiguration());
		SubscriptionConfiguration subscriptionConfiguration = paymentPeriod.getSubscriptionConfiguration();

		long companyId = paymentPeriod.getSubscriptionConfiguration().getCompany().getId();
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(companyId);
		BigDecimal assignmentThroughput = subscriptionConfiguration.isPendingRenewal() ? BigDecimal.ZERO : accountRegister.getAccountRegisterSummaryFields().getAssignmentSoftwareThroughput();

		Calendar paymentPeriodStartDate = DateUtilities.cloneCalendar(paymentPeriod.getPeriodDateRange().getFrom());

		SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateSubscriptionPayment(paymentPeriod.getSubscriptionConfiguration(), assignmentThroughput, paymentPeriodStartDate);

		// determine our due date - this is basically the due date + our payment term days
		Calendar paymentDueDate = DateUtilities.cloneCalendar(paymentPeriod.getPeriodDateRange().getFrom());
		if (DateUtilities.getDaysBetweenFromNow(paymentDueDate) < paymentPeriod.getSubscriptionConfiguration().getPaymentTermsDays()) {
			paymentDueDate = Calendar.getInstance();
			paymentDueDate.add(Calendar.DATE, paymentPeriod.getSubscriptionConfiguration().getPaymentTermsDays());
		}

		subscriptionPaymentDTO.setDueDate(paymentDueDate);
		subscriptionPaymentDTO.setVendorOfRecord(subscriptionConfiguration.isVendorOfRecord());
		SubscriptionInvoice invoice = newSubscriptionInvoiceFromSubscriptionPaymentDTO(companyId, subscriptionPaymentDTO, subscriptionInvoiceTypeCode, paymentPeriod);
		if (invoice != null) {
			paymentPeriod.setSubscriptionInvoice(invoice);
			paymentPeriod.setSubscriptionFeeConfigurationId(subscriptionPaymentDTO.getSubscriptionFeeConfigurationId());
			paymentPeriod.setSubscriptionPaymentTierSWId(subscriptionPaymentDTO.getSubscriptionPaymentTierSWId());
			paymentPeriod.setSubscriptionPaymentTierVORId(subscriptionPaymentDTO.getSubscriptionPaymentTierVORId());

			subscriptionConfiguration.setPastPaymentPeriods(subscriptionConfiguration.getPastPaymentPeriods() + 1);
			SubscriptionPaymentPeriod nextPaymentPeriod = paymentPeriodDAO.findNextNotInvoicedSubscriptionPaymentPeriod(subscriptionConfiguration.getId());
			if (nextPaymentPeriod != null) {
				Assert.isTrue(!paymentPeriod.getId().equals(nextPaymentPeriod.getId()));
				subscriptionConfiguration.setNextPaymentPeriodStartDate(DateUtilities.cloneCalendar(nextPaymentPeriod.getPeriodDateRange().getFrom()));
			}
		}
		return invoice;
	}

	SubscriptionInvoice newSubscriptionInvoiceFromSubscriptionPaymentDTO(long companyId, SubscriptionPaymentDTO paymentDTO, String subscriptionInvoiceTypeCode, SubscriptionPaymentPeriod paymentPeriod) {
		Assert.notNull(paymentDTO);
		if (paymentDTO.hasValue()) {
			Assert.notNull(paymentDTO.getDueDate());
			SubscriptionInvoiceType subscriptionInvoiceType = lookupEntityDAO.findByCode(SubscriptionInvoiceType.class, subscriptionInvoiceTypeCode);
			Assert.notNull(subscriptionInvoiceType, "Invalid type");
			final String description = subscriptionInvoiceType.getDescription();

			SubscriptionInvoiceDTO invoiceDTO = new SubscriptionInvoiceDTO();
			invoiceDTO.setCompanyId(companyId);
			invoiceDTO.setDescription(description);
			invoiceDTO.setDueDate(paymentDTO.getDueDate());
			invoiceDTO.setSubscriptionInvoiceType(subscriptionInvoiceType);
			invoiceDTO.setLineItemDTOList(invoiceLineItemFactory.newSubscriptionInvoiceLineItemDTOList(paymentDTO));

			SubscriptionInvoice invoice = saveOrUpdateSubscriptionInvoice(invoiceDTO, paymentPeriod);
			userNotificationService.onNewInvoice(invoice);
			return invoice;
		}

		return null;
	}

	@Override
	public SubscriptionConfiguration findActiveSubscriptionConfigurationByCompanyId(long companyId) {
		return subscriptionConfigurationDAO.findActiveSubscriptionConfigurationByCompanyId(companyId);
	}

	@Override
	public SubscriptionConfigurationPagination findAllSubscriptionConfigurations(SubscriptionConfigurationPagination pagination) {
		return subscriptionConfigurationDAO.findAllSubscriptionConfigurations(pagination);
	}

	@Override
	public SubscriptionConfigurationPagination findAllActiveSubscriptionConfigurations() {
		SubscriptionConfigurationPagination pagination = new SubscriptionConfigurationPagination(true);
		Map filters = Maps.newHashMap();
		filters.put(SubscriptionConfigurationPagination.FILTER_KEYS.SUBSCRIPTION_STATUS, SubscriptionStatusType.ACTIVE);
		pagination.setFilters(filters);
		return subscriptionConfigurationDAO.findAllSubscriptionConfigurations(pagination);
	}

	@Override
	public SubscriptionConfigurationPagination findAllPendingApprovalSubscriptionConfigurations(SubscriptionConfigurationPagination pagination) {
		if (!pagination.hasSortColumn()) {
			pagination.setSortColumn(SubscriptionConfigurationPagination.SORTS.EFFECTIVE_DATE.getColumn());
			pagination.setSortDirection(SORT_DIRECTION.DESC);
		}
		return subscriptionConfigurationDAO.findAllPendingSubscriptionConfigurations(pagination);
	}

	@Override
	public SubscriptionConfiguration findSubscriptionConfigurationById(long subscriptionConfigurationId) {
		return subscriptionConfigurationDAO.get(subscriptionConfigurationId);
	}

	@Override
	public SubscriptionConfiguration saveOrUpdateSubscriptionConfigurationForCompany(long companyId, SubscriptionConfigurationDTO subscriptionConfigurationDTO, boolean submitForApproval) {
		Assert.notNull(subscriptionConfigurationDTO);
		Assert.notNull(subscriptionConfigurationDTO.getEffectiveDate());
		Assert.notNull(subscriptionConfigurationDTO.getSubscriptionPeriod());
		Assert.notNull(subscriptionConfigurationDTO.getNumberOfPeriods());
		Assert.isTrue(subscriptionConfigurationDTO.getNumberOfPeriods() > 0);
		Assert.notNull(subscriptionConfigurationDTO.getSubscriptionTypeCode());

		if (subscriptionConfigurationDTO.isBlockSubscription()) {
			Assert.notNull(subscriptionConfigurationDTO.getBlockTierPercentage());
			Assert.isTrue(subscriptionConfigurationDTO.getBlockTierPercentage().compareTo(BigDecimal.ZERO) > 0);
		}

		Company company = companyDAO.get(companyId);
		Assert.notNull(company);

		SubscriptionConfiguration subscription = null;

		if (subscriptionConfigurationDTO.getSubscriptionConfigurationId() != null) {
			subscription = subscriptionConfigurationDAO.get(subscriptionConfigurationDTO.getSubscriptionConfigurationId());
		}

		if (subscription == null) {
			subscription = new SubscriptionConfiguration(company);
			subscription.setApprovalStatus(ApprovalStatus.NOT_READY);
		}

		// If subscription is not pending nor active then modifications are not allowed
		Assert.state(subscription.isPending() || subscription.isActive(), "You cannot edit this subscription");

		// If subscription isApproved then we only allow a reduced amount of changes
		if (subscription.isApproved()) {

			// If you don't want to remove them then just pass them around
			addOrRemoveAddOnsToOrFromSubscriptionConfiguration(subscription.getId(), subscriptionConfigurationDTO.getSubscriptionAddOnDTOs());

			// If you don't pass any dtos here it won't do anything
			changeSubscriptionFeeConfiguration(
				subscription.getId(),
				subscriptionConfigurationDTO.getSubscriptionPaymentTierDTOs(),
				subscriptionConfigurationDTO.getPaymentTierEffectiveDate(),
				subscriptionConfigurationDTO.getSubscriptionTypeCode(),
				subscriptionConfigurationDTO.getBlockTierPercentage()
			);

			if (subscriptionConfigurationDTO.hasNotes()) {
				addNotesToSubscriptionConfiguration(subscription.getId(), subscriptionConfigurationDTO.getSubscriptionNoteDTOs());
			}

			validateSubscriptionConfiguration(subscription);
			return subscription;
		}

		//Since we can keep going with the modifications
		BeanUtilities.copyProperties(subscription, subscriptionConfigurationDTO);
		Calendar effectiveDate = DateUtilities.cloneCalendar(subscriptionConfigurationDTO.getEffectiveDate());

		subscription.setApprovalStatus((submitForApproval) ? ApprovalStatus.PENDING : ApprovalStatus.NOT_READY);
		subscription.setEffectiveDate(effectiveDate);
		subscription.setEndDate(subscription.calculateEndDate());
		subscription.setSignedDate(DateUtilities.cloneCalendar(subscriptionConfigurationDTO.getSignedDate()));
		subscriptionConfigurationDAO.saveOrUpdate(subscription);

		addOrRemoveAddOnsToOrFromSubscriptionConfiguration(subscription.getId(), subscriptionConfigurationDTO.getSubscriptionAddOnDTOs());
		changeSubscriptionFeeConfiguration(
			subscription.getId(),
			subscriptionConfigurationDTO.getSubscriptionPaymentTierDTOs(),
			subscriptionConfigurationDTO.getSubscriptionTypeCode(),
			subscriptionConfigurationDTO.getBlockTierPercentage()
		);
		changeAccountServiceTypeConfiguration(subscription.getId(), subscriptionConfigurationDTO.getAccountServiceTypeDTOs());

		if (subscriptionConfigurationDTO.hasNotes()) {
			addNotesToSubscriptionConfiguration(subscription.getId(), subscriptionConfigurationDTO.getSubscriptionNoteDTOs());
		}

		if (submitForApproval) {
			validateSubscriptionConfiguration(subscription);
		}

		subscriptionConfigurationDAO.saveOrUpdate(subscription);
		return subscription;
	}

	@SuppressWarnings("unchecked")
	private void changeAccountServiceTypeConfiguration(long subscriptionId, List<AccountServiceTypeDTO> accountServiceTypeDTOs) {
		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);
		if (subscriptionConfiguration != null) {
			for (AccountServiceTypeDTO accountServiceTypeDTO : accountServiceTypeDTOs) {
				if (Country.WM_SUPPORTED_COUNTRIES.contains(accountServiceTypeDTO.getCountryCode())) {
					changeAccountServiceTypeConfiguration(accountServiceTypeDTO, subscriptionConfiguration);
				}
			}
		}
	}

	private void changeAccountServiceTypeConfiguration(AccountServiceTypeDTO accountServiceTypeDTO, SubscriptionConfiguration subscriptionConfiguration) {
		AccountServiceType accountServiceType = lookupEntityDAO.findByCode(AccountServiceType.class, accountServiceTypeDTO.getAccountServiceTypeCode());
		Country country = Country.valueOf(accountServiceTypeDTO.getCountryCode());

		if (accountServiceType != null && country != null) {
			SubscriptionAccountServiceTypeConfiguration serviceTypeConfiguration = subscriptionConfiguration.findAccountServiceTypeConfigurationForCountry(country.getId());
			if (serviceTypeConfiguration != null) {
				serviceTypeConfiguration.setAccountServiceType(accountServiceType);
			} else {
				SubscriptionAccountServiceTypeConfiguration accountServiceTypeConfiguration = new SubscriptionAccountServiceTypeConfiguration(accountServiceType, country, subscriptionConfiguration);
				subscriptionAccountServiceTypeConfigurationDAO.saveOrUpdate(accountServiceTypeConfiguration);
				subscriptionConfiguration.getAccountServiceTypeConfigurations().add(accountServiceTypeConfiguration);
			}
		}
	}

	@Override
	public SubscriptionConfiguration changeSubscriptionFeeConfiguration(long subscriptionId, List<SubscriptionPaymentTierDTO> subscriptionPaymentTierDTOs, String subscriptionTypeCode, BigDecimal blockTierPercentage) {
		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);
		if (subscriptionConfiguration != null) {
			changeSubscriptionFeeConfiguration(
				subscriptionId,
				subscriptionPaymentTierDTOs,
				subscriptionConfiguration.getEffectiveDate(),
				subscriptionTypeCode,
				blockTierPercentage
			);
		}
		return subscriptionConfiguration;
	}

	@Override
	public SubscriptionConfiguration changeSubscriptionFeeConfiguration(long subscriptionId, List<SubscriptionPaymentTierDTO> subscriptionPaymentTierDTOs, Calendar effectiveDate, String subscriptionTypeCode, BigDecimal blockTierPercentage) {
		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);
		if (subscriptionConfiguration != null) {
			Set<SubscriptionFeeConfiguration> subscriptionPaymentConfigurations = subscriptionConfiguration.getSubscriptionFeeConfigurations();
			Assert.state(isNotEmpty(subscriptionPaymentTierDTOs) || isNotEmpty(subscriptionPaymentConfigurations),
					"Cannot save a subscription without a fee configuration");
			if (isEmpty(subscriptionPaymentTierDTOs)) {
				// Nothing to do here - We keep the previous fee configuration
				return subscriptionConfiguration;
			}
			SubscriptionFeeConfiguration subscriptionFeeConfiguration = new SubscriptionFeeConfiguration();
			// If the subscription is active we cannot edit approved associations
			// We can however edit pending approval associations if any
			if (subscriptionConfiguration.isActive()) {
				if (isNotEmpty(subscriptionPaymentConfigurations)) {
					for (SubscriptionFeeConfiguration association : subscriptionPaymentConfigurations) {
						// We don't delete removed ones to preserve history
						if (!association.getActive() && (association.isPendingApproval() || association.isNotReady())) {
							association.setDeleted(true);
							subscriptionFeeConfigurationDAO.saveOrUpdate(association);
						}
					}
				}
			}
			// If it's pending we can edit anything
			if (subscriptionConfiguration.isPending()) {
				if (isNotEmpty(subscriptionPaymentConfigurations)) {
					// This will prevent us from creating useless associations
					// Any other association with the same addOn will be deleted
					for (SubscriptionFeeConfiguration association : subscriptionPaymentConfigurations) {
						association.setDeleted(true);
						subscriptionFeeConfigurationDAO.saveOrUpdate(association);
					}
				}
			}

			subscriptionFeeConfiguration.setSubscriptionConfiguration(subscriptionConfiguration);
			subscriptionFeeConfiguration.setEffectiveDate(effectiveDate);
			subscriptionFeeConfiguration.setApprovalStatus(ApprovalStatus.PENDING);
			subscriptionFeeConfiguration.setVerificationStatus(VerificationStatus.PENDING);
			subscriptionFeeConfiguration.setActive(false);

			SubscriptionType subscriptionType = lookupEntityDAO.findByCode(SubscriptionType.class, subscriptionTypeCode);
			subscriptionFeeConfiguration.setSubscriptionType(subscriptionType);
			if (subscriptionType.getCode().equals(SubscriptionType.BLOCK)) {
				subscriptionFeeConfiguration.setBlockTierPercentage(blockTierPercentage);
			}

			subscriptionFeeConfigurationDAO.saveOrUpdate(subscriptionFeeConfiguration);

			for (SubscriptionPaymentTierDTO dto : subscriptionPaymentTierDTOs) {
				addTierToSubscriptionPaymentConfiguration(subscriptionFeeConfiguration, dto);
			}

			SubscriptionPaymentTier activePaymentTier = null;
			if (subscriptionConfiguration.isPending()) {
				List<SubscriptionPaymentTier> subscriptionPaymentTiers = subscriptionFeeConfiguration.getSubscriptionPaymentTiers();
				if (isNotEmpty(subscriptionPaymentTiers)) {
					activePaymentTier = subscriptionPaymentTiers.get(0);
				}
			} else {
				AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(subscriptionConfiguration.getCompany().getId());
				BigDecimal throughputToDate = accountRegister.getAccountRegisterSummaryFields().getAssignmentSoftwareThroughput();
				activePaymentTier = subscriptionFeeConfiguration.findSubscriptionPaymentTierForThroughputAmount(throughputToDate);
			}
			if (activePaymentTier != null) {
				activePaymentTier.setSubscriptionPaymentTierSoftwareStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.ACTIVE));
				activePaymentTier.setSubscriptionPaymentTierVorStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.ACTIVE));
			}
		}
		return subscriptionConfiguration;
	}

	@Override
	public Set<SubscriptionConfiguration> findAllUpdatableSubscriptionConfigurationsByUpdateDate(Calendar updateDate) {
		Assert.notNull(updateDate);
		return subscriptionConfigurationDAO.findAllUpdatableSubscriptionConfigurationsByUpdateDate(updateDate);
	}

	@Override
	public Set<SubscriptionConfiguration> findAllSubscriptionConfigurationsWithNextThroughputReset(Calendar updateDate) {
		return subscriptionConfigurationDAO.findAllSubscriptionConfigurationsWithNextThroughputReset(updateDate);
	}

	private void addTierToSubscriptionPaymentConfiguration(SubscriptionFeeConfiguration subscriptionPaymentConfiguration, SubscriptionPaymentTierDTO subscriptionPaymentTierDTO) {
		Assert.notNull(subscriptionPaymentTierDTO);
		SubscriptionPaymentTier subscriptionPaymentTier = new SubscriptionPaymentTier();
		BeanUtilities.copyProperties(subscriptionPaymentTier, subscriptionPaymentTierDTO);
		subscriptionPaymentTier.setSubscriptionFeeConfiguration(subscriptionPaymentConfiguration);
		subscriptionPaymentConfiguration.getSubscriptionPaymentTiers().add(subscriptionPaymentTier);
		subscriptionPaymentTierDAO.saveOrUpdate(subscriptionPaymentTier);
	}

	@Override
	public SubscriptionConfiguration addOrRemoveAddOnsToOrFromSubscriptionConfiguration(long subscriptionId, List<SubscriptionAddOnDTO> subscriptionAddOnDTOs) {
		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);
		if (subscriptionConfiguration != null) {
			//Get current add-ons
			Set<SubscriptionAddOnTypeAssociation> subscriptionAddOnTypeAssociations = subscriptionConfiguration.getSubscriptionAddOns();
			Map<String, SubscriptionAddOnDTO> subscriptionAddOnDTOMap = CollectionUtilities.newEntityIdMap(subscriptionAddOnDTOs, "addOnTypeCode");

			for (SubscriptionAddOnTypeAssociation association : subscriptionAddOnTypeAssociations) {
				if (!subscriptionAddOnDTOMap.containsKey(association.getSubscriptionAddOnType().getCode())) {
					removeAddOnFromSubscriptionConfiguration(subscriptionConfiguration, association.getSubscriptionAddOnType().getCode());
				}
			}
			for (SubscriptionAddOnDTO dto : subscriptionAddOnDTOs) {
				addAddOnToSubscriptionConfiguration(subscriptionConfiguration, dto);
			}
		}
		return subscriptionConfiguration;
	}

	@Override
	public SubscriptionConfiguration addAddOnToSubscriptionConfiguration(long subscriptionId, SubscriptionAddOnDTO subscriptionAddOnDTO) {
		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);
		if (subscriptionConfiguration != null) {
			addAddOnToSubscriptionConfiguration(subscriptionConfiguration, subscriptionAddOnDTO);
		}
		return subscriptionConfiguration;
	}

	private void addAddOnToSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration, SubscriptionAddOnDTO subscriptionAddOnDTO) {
		Assert.notNull(subscriptionAddOnDTO);
		Assert.notNull(subscriptionAddOnDTO.getCostPerPeriod());
		Assert.hasText(subscriptionAddOnDTO.getAddOnTypeCode());
		Assert.isTrue(SubscriptionAddOnType.addOnTypeCodes.contains(subscriptionAddOnDTO.getAddOnTypeCode()));
		Assert.state(subscriptionConfiguration.isActive() || subscriptionConfiguration.isPending(),
				"Trying to add AddOns to an unmodifiable subscriptionConfiguration with status :" + subscriptionConfiguration.getSubscriptionStatusType().getCode());

		SubscriptionAddOnTypeAssociation association = new SubscriptionAddOnTypeAssociation();
		List<SubscriptionAddOnTypeAssociation> associations = subscriptionAddOnTypeAssociationDAO.findBySubscriptionIdAndAddOnType(subscriptionConfiguration.getId(), subscriptionAddOnDTO.getAddOnTypeCode());
		// We fork on the subscription status here and proceed to the right approval status
		if (subscriptionConfiguration.isPending()) {
			if (isNotEmpty(associations)) {
				// Since the subscription is pending we will allow the update of an association
				// This will prevent us from creating useless associations
				association = associations.remove(0);
				// Any other association with the same addOn will be deleted
				for (SubscriptionAddOnTypeAssociation existingAssociation : associations) {
					existingAssociation.setDeleted(true);
					subscriptionAddOnTypeAssociationDAO.saveOrUpdate(existingAssociation);
				}
			}
		} else { // subscriptionConfiguration.isActive()
			if (isNotEmpty(associations)) {
				// Since the subscription is active we won't allow the update of an association unless it's inactive
				// This will prevent us from creating useless associations
				for (SubscriptionAddOnTypeAssociation existingAssociation : associations) {
					if (!existingAssociation.getActive()) {
						association = existingAssociation;
						break;
					}
				}
			}
		}
		association.setActive(false);
		association.setEffectiveDate(subscriptionAddOnDTO.getEffectiveDate());
		association.setApprovalStatus(ApprovalStatus.PENDING);
		association.setVerificationStatus(VerificationStatus.PENDING);
		association.setCostPerPeriod(subscriptionAddOnDTO.getCostPerPeriod());
		association.setSubscriptionAddOnType(new SubscriptionAddOnType(subscriptionAddOnDTO.getAddOnTypeCode()));
		association.setSubscriptionConfiguration(subscriptionConfiguration);
		subscriptionAddOnTypeAssociationDAO.saveOrUpdate(association);
		subscriptionConfiguration.getSubscriptionAddOns().add(association);
	}

	private void removeAddOnFromSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration, String subscriptionAddOnTypeCode) {
		Assert.hasText(subscriptionAddOnTypeCode);
		Assert.state(subscriptionConfiguration.isActive() || subscriptionConfiguration.isPending(),
				"Trying to remove AddOns from an unmodifiable subscriptionConfiguration with status :" + subscriptionConfiguration.getSubscriptionStatusType().getCode());

		List<SubscriptionAddOnTypeAssociation> associations = subscriptionAddOnTypeAssociationDAO.findBySubscriptionIdAndAddOnType(subscriptionConfiguration.getId(), subscriptionAddOnTypeCode);
		// We fork on the subscription status here and proceed to the right approval status
		if (subscriptionConfiguration.isPending()) {
			// Since the subscription is pending we will allow the removal of associations
			// Any association with the same addOn will be deleted
			for (SubscriptionAddOnTypeAssociation existingAssociation : associations) {
				existingAssociation.setApprovalStatus(ApprovalStatus.REMOVED);
				subscriptionAddOnTypeAssociationDAO.saveOrUpdate(existingAssociation);
			}
		}
	}

	@Override
	public SubscriptionConfiguration approveSubscriptionConfiguration(long subscriptionId) {
		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);

		if (subscriptionConfiguration != null) {
			validateSubscriptionConfiguration(subscriptionConfiguration);
			if (subscriptionConfiguration.isApproved()) {
				approveSubscriptionAddOnAssociationModifications(subscriptionConfiguration);
				approveLatestSubscriptionFeeConfiguration(subscriptionConfiguration);
				if (subscriptionConfiguration.getSubscriptionCancellation() != null
						&& subscriptionConfiguration.getSubscriptionCancellation().isPendingApproval()) {
					approveSubscriptionCancellation(subscriptionId);
				}
				approveLatestSubscriptionRenewalRequest(subscriptionConfiguration);
			} else {
				approveLatestSubscriptionFeeConfiguration(subscriptionConfiguration);
				approveSubscriptionAddOnAssociationModifications(subscriptionConfiguration);
				approveSubscriptionConfiguration(subscriptionConfiguration);

				generateSubscriptionPaymentPeriods(subscriptionConfiguration);

				// Schedule the change from transactional to subscription on the effectiveDate
				createCompanyAccountPricingTypeChange(new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE), new AccountPricingType(AccountPricingType.SUBSCRIPTION_PRICING_TYPE), subscriptionConfiguration, DateUtilities.cloneCalendar(subscriptionConfiguration.getEffectiveDate()));

				// Create all auto-renew subscription configurations
				Calendar endDate = DateUtilities.cloneCalendar(subscriptionConfiguration.getEndDate());
				if (subscriptionConfiguration.getNumberOfRenewals() > 0) {
					SubscriptionConfiguration renewSubscription = subscriptionConfiguration;
					for (int i = subscriptionConfiguration.getNumberOfRenewals(); i > 0; i--) {
						renewSubscription = renewSubscriptionConfigurationForCompany(renewSubscription.getId(), Optional.<SubscriptionRenewalRequest>absent());
					}
					endDate = DateUtilities.cloneCalendar(renewSubscription.getEndDate());
				}

				// Schedule the change from subscription to transactional on the endDate (of the last renewed subscription)
				createCompanyAccountPricingTypeChange(new AccountPricingType(AccountPricingType.SUBSCRIPTION_PRICING_TYPE), new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE), subscriptionConfiguration, endDate);

				if (DateUtilities.getMonthsBetween(DateUtilities.getMidnight(Calendar.getInstance()), subscriptionConfiguration.getEffectiveDate()) <= 1) {
					issueRegularSubscriptionInvoice(subscriptionConfiguration.getId());
				}
			}
		}
		return subscriptionConfiguration;
	}

	private void createCompanyAccountPricingTypeChange(AccountPricingType fromAccountPricingType, AccountPricingType toAccountPricingType, SubscriptionConfiguration subscriptionConfiguration, Calendar date) {
		CompanyAccountPricingTypeChange pricingTypeChange = new CompanyAccountPricingTypeChange();
		pricingTypeChange.setCompany(subscriptionConfiguration.getCompany());
		pricingTypeChange.setFromAccountPricingType(fromAccountPricingType);
		pricingTypeChange.setToAccountPricingType(toAccountPricingType);
		pricingTypeChange.setScheduledChangeDate(date);
		companyAccountPricingTypeChangeDAO.saveOrUpdate(pricingTypeChange);

	}

	@Override
	public int approveSubscriptionConfigurations(List<Long> subscriptionConfigurationIds) {
		int errors = 0;
		if (isEmpty(subscriptionConfigurationIds)) {
			return errors;
		}
		for (Long id : subscriptionConfigurationIds) {
			SubscriptionConfiguration subscription = findSubscriptionConfigurationById(id);
			if (subscription != null) {
				try {
					approveSubscriptionConfiguration(id);
				} catch (Exception ex) {
					errors++;
					logger.error("[subscriptions] Error approving subscription id: " + id, ex);
				}
			}
		}
		return errors;
	}

	@Override
	public SubscriptionConfiguration findRenewSubscriptionConfiguration(long subscriptionId) {
		return subscriptionConfigurationDAO.findRenewSubscriptionConfiguration(subscriptionId);
	}

	@Override
	public SubscriptionRenewalRequest findLatestPendingApprovalSubscriptionRenewalRequest(long subscriptionId) {
		return subscriptionRenewalRequestDAO.findLatestPendingApprovalSubscriptionRenewalRequest(subscriptionId);
	}


	@Override
	public void submitSubscriptionRenewalRequest(SubscriptionRenewalRequestDTO renewalRequestDTO) {
		Assert.notNull(renewalRequestDTO);

		// If the parent subscription has more renewals - find the latest one and assign the renewal request to that one
		SubscriptionConfiguration parentSubscription = subscriptionConfigurationDAO.get(renewalRequestDTO.getParentSubscriptionId());
		Assert.notNull(parentSubscription);
		SubscriptionConfiguration currentParentSubscription = parentSubscription;
		while (currentParentSubscription != null) {
			currentParentSubscription = subscriptionConfigurationDAO.findRenewSubscriptionConfiguration(currentParentSubscription.getId());
			if (currentParentSubscription != null) {
				parentSubscription = currentParentSubscription;
			}
		}

		// If there's already a renewal request for this subscription - update it
		// Otherwise - create it
		SubscriptionRenewalRequest subscriptionRenewalRequest = subscriptionRenewalRequestDAO.findLatestPendingApprovalSubscriptionRenewalRequest(parentSubscription.getId());
		if (subscriptionRenewalRequest == null) {
			subscriptionRenewalRequest = new SubscriptionRenewalRequest();
		}
		BeanUtilities.copyProperties(subscriptionRenewalRequest, renewalRequestDTO);
		subscriptionRenewalRequest.setApprovalStatus(ApprovalStatus.PENDING);
		subscriptionRenewalRequest.setParentSubscription(parentSubscription);
		subscriptionRenewalRequestDAO.saveOrUpdate(subscriptionRenewalRequest);

		if (renewalRequestDTO.getModifyPricing()) {
			List<SubscriptionPaymentTierRenewal> subscriptionPaymentTierRenewals = subscriptionRenewalRequest.getSubscriptionPaymentTiers();
			for (SubscriptionPaymentTierDTO paymentTier : renewalRequestDTO.getSubscriptionPaymentTierDTOs()) {
				SubscriptionPaymentTierRenewal paymentTierRenewal = new SubscriptionPaymentTierRenewal();
				BeanUtilities.copyProperties(paymentTierRenewal, paymentTier);
				paymentTierRenewal.setSubscriptionRenewalRequest(subscriptionRenewalRequest);
				subscriptionPaymentTierRenewalDAO.saveOrUpdate(paymentTierRenewal);
				subscriptionPaymentTierRenewals.add(paymentTierRenewal);
			}
		}
		validateRenewalRequest(parentSubscription, subscriptionRenewalRequest);
	}

	private void approveLatestSubscriptionRenewalRequest(SubscriptionConfiguration subscriptionConfiguration) {
		Assert.notNull(subscriptionConfiguration);

		SubscriptionRenewalRequest renewalRequest = subscriptionRenewalRequestDAO.findLatestPendingApprovalSubscriptionRenewalRequest(subscriptionConfiguration.getId());
		if (renewalRequest != null) {
			SubscriptionConfiguration currentSubscription = subscriptionConfiguration;
			SubscriptionConfiguration lastRenewedSubscription = subscriptionConfiguration;
			while ((currentSubscription = subscriptionConfigurationDAO.findRenewSubscriptionConfiguration(currentSubscription.getId())) != null) {
				lastRenewedSubscription = currentSubscription;
			}
			renewSubscriptionConfigurationForCompany(lastRenewedSubscription.getId(), Optional.fromNullable(renewalRequest));

			subscriptionConfiguration.setNumberOfRenewals(subscriptionConfiguration.getNumberOfRenewals() + 1);
			renewalRequest.setApprovalStatus(ApprovalStatus.APPROVED);
			renewalRequest.setVerificationStatus(VerificationStatus.VERIFIED);
		}
	}

	private void approveLatestSubscriptionFeeConfiguration(SubscriptionConfiguration subscriptionConfiguration) {
		Assert.notNull(subscriptionConfiguration);
		SubscriptionFeeConfiguration feeConfiguration = subscriptionFeeConfigurationDAO.getLatestPendingApprovalSubscriptionFeeConfiguration(subscriptionConfiguration.getId());
		if (subscriptionConfiguration.isApproved()) {
			if (feeConfiguration != null) {
				feeConfiguration.setActive(false);

				if (feeConfiguration.getEffectiveDate().compareTo(DateUtilities.getCalendarNow()) <= 0) {
					SubscriptionFeeConfiguration activeConfiguration = subscriptionConfiguration.getActiveSubscriptionFeeConfiguration();
					activeConfiguration.setActive(false);
					subscriptionFeeConfigurationDAO.saveOrUpdate(activeConfiguration);
					feeConfiguration.setActive(true);
				}
				// Else It will be updated on the effectiveDate
				feeConfiguration.setApprovalStatus(ApprovalStatus.APPROVED);
				feeConfiguration.setVerificationStatus(VerificationStatus.VERIFIED);
				feeConfiguration.setSubscriptionConfiguration(subscriptionConfiguration);
				subscriptionFeeConfigurationDAO.saveOrUpdate(feeConfiguration);
			}
		} else if (subscriptionConfiguration.isPendingApproval()) {
			Assert.notNull(feeConfiguration);
			feeConfiguration.setActive(true);
			feeConfiguration.setApprovalStatus(ApprovalStatus.APPROVED);
			feeConfiguration.setVerificationStatus(VerificationStatus.VERIFIED);
			feeConfiguration.setSubscriptionConfiguration(subscriptionConfiguration);
			subscriptionFeeConfigurationDAO.saveOrUpdate(feeConfiguration);
		}
	}

	private void approveSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration) {
		subscriptionConfiguration.setSubscriptionStatusType(new SubscriptionStatusType(SubscriptionStatusType.ACTIVE));
		subscriptionConfiguration.setApprovalStatus(ApprovalStatus.APPROVED);
		subscriptionConfiguration.setVerificationStatus(VerificationStatus.VERIFIED);
		subscriptionConfiguration.setApprovedBy(authenticationService.getCurrentUser());
		subscriptionConfiguration.setApprovedOn(Calendar.getInstance());
		subscriptionConfiguration.setNextPaymentPeriodStartDate(subscriptionConfiguration.getEffectiveDate());
	}

	private void approveSubscriptionAddOnAssociationModifications(SubscriptionConfiguration subscriptionConfiguration) {
		Assert.notNull(subscriptionConfiguration);
		Set<SubscriptionAddOnTypeAssociation> activeAddOns = subscriptionConfiguration.getActiveSubscriptionAddOns();
		Map<String, SubscriptionAddOnTypeAssociation> addOnAssociations = Maps.newHashMap();
		for (SubscriptionAddOnTypeAssociation activeAddOn : activeAddOns) {
			addOnAssociations.put(activeAddOn.getSubscriptionAddOnType().getCode(), activeAddOn);
		}
		for (SubscriptionAddOnTypeAssociation association : subscriptionConfiguration.getSubscriptionAddOns()) {
			if (association.isPendingRemoval()) {
				if (association.getEffectiveDate().compareTo(DateUtilities.getCalendarNow()) <= 0) {
					association.setApprovalStatus(ApprovalStatus.REMOVED);
					association.setActive(false);
					if (addOnAssociations.containsKey(association.getSubscriptionAddOnType().getCode())) {
						SubscriptionAddOnTypeAssociation activeAddOnAssociation = addOnAssociations.get(association.getSubscriptionAddOnType().getCode());
						activeAddOnAssociation.setActive(false);
						activeAddOnAssociation.setApprovalStatus(ApprovalStatus.REMOVED);
					}
				} else {
					// It will be updated on the effectiveDate
					association.setApprovalStatus(ApprovalStatus.PENDING_REMOVAL);
					association.setActive(false);
				}
			}
			if (association.isPendingApproval()) {
				association.setApprovalStatus(ApprovalStatus.APPROVED);
				// It will be updated on the effectiveDate if false
				association.setActive(subscriptionConfiguration.isPendingApproval() || association.getEffectiveDate().compareTo(DateUtilities.getCalendarNow()) <= 0);
			}
		}
	}

	@Override
	public SubscriptionConfiguration rejectSubscriptionConfiguration(Long subscriptionId) {
		SubscriptionConfiguration subscription = subscriptionConfigurationDAO.get(subscriptionId);
		if (subscription != null) {
			Assert.state((subscription.isApproved() && subscription.isActive()) || (subscription.isPending() && subscription.isPendingApproval()));
			if (subscription.isPending() && subscription.isPendingApproval()) {
				subscription.setApprovalStatus(ApprovalStatus.DECLINED);
				subscription.setSubscriptionStatusType(new SubscriptionStatusType(SubscriptionStatusType.REJECTED));
			} else if (subscription.isApproved() && subscription.isActive()) {
				// Check for fee configuration changes
				for (SubscriptionFeeConfiguration feeConfiguration : subscription.getSubscriptionFeeConfigurations()) {
					if (feeConfiguration.isPendingApproval()) {
						feeConfiguration.setApprovalStatus(ApprovalStatus.DECLINED);
					}
				}
				// Check for addOn changes
				for (SubscriptionAddOnTypeAssociation addOn : subscription.getSubscriptionAddOns()) {
					if (addOn.isPendingApproval()) {
						addOn.setApprovalStatus(ApprovalStatus.DECLINED);
					}
				}
				// Check for subscription cancellations
				if (subscription.getSubscriptionCancellation() != null
						&& subscription.getSubscriptionCancellation().isPendingApproval()) {
					rejectSubscriptionCancellation(subscriptionId);
				}
				// Check for renewal requests
				SubscriptionRenewalRequest request;
				while ((request = subscriptionRenewalRequestDAO.findLatestPendingApprovalSubscriptionRenewalRequest(subscription.getId())) != null) {
					request.setApprovalStatus(ApprovalStatus.DECLINED);
				}
			}
		}
		return subscription;
	}

	@Override
	public int rejectSubscriptionConfigurations(List<Long> subscriptionConfigurationIds) {
		int errors = 0;
		if (isEmpty(subscriptionConfigurationIds)) {
			return errors;
		}
		for (Long id : subscriptionConfigurationIds) {
			SubscriptionConfiguration subscription = findSubscriptionConfigurationById(id);
			if (subscription != null) {
				try {
					rejectSubscriptionConfiguration(id);
				} catch (Exception ex) {
					errors++;
					logger.error("[subscriptions] Error rejecting subscription id: " + id, ex);
				}
			}
		}
		return errors;
	}

	private void generateSubscriptionPaymentPeriods(SubscriptionConfiguration subscriptionConfiguration) {
		Assert.notNull(subscriptionConfiguration);
		Calendar startDate = DateUtilities.cloneCalendar(subscriptionConfiguration.getEffectiveDate());
		int incrementInMonths = subscriptionConfiguration.getSubscriptionPeriod().getMonths();

		for (int i = 1; i <= subscriptionConfiguration.getNumberOfPeriods(); i++) {
			Calendar endDate = DateUtilities.cloneCalendar(startDate);
			endDate.add(Calendar.MONTH, incrementInMonths);

			SubscriptionPaymentPeriod paymentPeriod = new SubscriptionPaymentPeriod(subscriptionConfiguration);
			DateRange periodDate = paymentPeriod.getPeriodDateRange();
			periodDate.setFrom(startDate);
			periodDate.setThrough(endDate);
			paymentPeriod.setPeriodDateRange(periodDate);
			paymentPeriodDAO.saveOrUpdate(paymentPeriod);

			startDate = DateUtilities.cloneCalendar(endDate);
		}
	}

	@Override
	public SubscriptionCancellation submitCancellationForSubscriptionConfiguration(long subscriptionId, SubscriptionCancelDTO subscriptionCancelDTO) {
		Assert.notNull(subscriptionCancelDTO);
		Assert.notNull(subscriptionCancelDTO.getCancellationDate());

		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);
		Assert.notNull(subscriptionConfiguration);

		SubscriptionCancellation cancellation = subscriptionConfiguration.getSubscriptionCancellation();
		if (cancellation == null) {
			cancellation = new SubscriptionCancellation(subscriptionConfiguration);
			subscriptionConfiguration.setSubscriptionCancellation(cancellation);
		} else {
			Assert.state(!cancellation.isApproved());
		}

		Calendar cancellationEffectiveDate = DateUtilities.cloneCalendar(subscriptionCancelDTO.getCancellationDate());

		// Create the cancellation
		cancellation.setEffectiveDate(cancellationEffectiveDate);
		cancellation.setApprovalStatus(ApprovalStatus.PENDING);

		if (subscriptionCancelDTO.hasCancellationFee()) {
			cancellation.setCancellationFee(subscriptionCancelDTO.getCancellationFee());
		}

		validateSubscriptionCancellation(subscriptionConfiguration);
		subscriptionCancellationDAO.saveOrUpdate(cancellation);
		return cancellation;
	}

	@Override
	public void approveSubscriptionCancellation(long subscriptionId) {
		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);
		Assert.notNull(subscriptionConfiguration);
		Assert.notNull(subscriptionConfiguration.getSubscriptionCancellation(), "Cancellation hasn't been submitted");

		validateSubscriptionCancellation(subscriptionConfiguration);

		SubscriptionCancellation cancellation = subscriptionConfiguration.getSubscriptionCancellation();
		cancellation.setApprovedBy(authenticationService.getCurrentUser());
		cancellation.setApprovedOn(Calendar.getInstance());
		cancellation.setApprovalStatus(ApprovalStatus.APPROVED);
		cancellation.setVerificationStatus(VerificationStatus.VERIFIED);

		if (cancellation.hasCancellationFee()) {
			SubscriptionPaymentPeriod currentPaymentPeriod = paymentPeriodDAO.findBySubscriptionConfigurationIdAndDateInRange(subscriptionConfiguration.getId(), Calendar.getInstance());
			if (currentPaymentPeriod == null) {
				currentPaymentPeriod = paymentPeriodDAO.findNextFromDateBySubscriptionConfigurationId(subscriptionConfiguration.getId(), Calendar.getInstance());
			}
			issueSubscriptionCancellationInvoice(cancellation, currentPaymentPeriod);
		}

		// Let's delete the entry for the expiration date - if it's still there
		CompanyAccountPricingTypeChange pricingTypeChange = companyAccountPricingTypeChangeDAO.getCompanyAccountPricingTypeChange(subscriptionConfiguration.getCompany().getId(), subscriptionConfiguration.getEndDate());
		if (pricingTypeChange != null && pricingTypeChange.getFromAccountPricingType().getCode().equals(AccountPricingType.SUBSCRIPTION_PRICING_TYPE) &&
				pricingTypeChange.getToAccountPricingType().isTransactionalPricing()) {
			pricingTypeChange.setDeleted(true);
			companyAccountPricingTypeChangeDAO.saveOrUpdate(pricingTypeChange);
		}

		// Let's cancel the renewal too - if there's any
		SubscriptionConfiguration newSubscription = subscriptionConfigurationDAO.findRenewSubscriptionConfiguration(subscriptionConfiguration.getId());
		if (newSubscription != null) {
			SubscriptionCancelDTO cancelDTO = new SubscriptionCancelDTO();
			cancelDTO.setCancellationDate(cancellation.getEffectiveDate());
			submitCancellationForSubscriptionConfiguration(newSubscription.getId(), cancelDTO);
			approveSubscriptionCancellation(newSubscription.getId());
		} else {
			// We will only execute this for the last renewal - otherwise we would be saving duplicates
			// Change pricing to transactional on the cancellation date
			createCompanyAccountPricingTypeChange(new AccountPricingType(AccountPricingType.SUBSCRIPTION_PRICING_TYPE), new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE), subscriptionConfiguration, DateUtilities.cloneCalendar(cancellation.getEffectiveDate()));
		}
	}

	@Override
	public void rejectSubscriptionCancellation(long subscriptionId) {
		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);
		Assert.notNull(subscriptionConfiguration);
		Assert.notNull(subscriptionConfiguration.getSubscriptionCancellation(), "Cancellation hasn't been submitted");

		SubscriptionCancellation cancellation = subscriptionConfiguration.getSubscriptionCancellation();
		cancellation.setApprovalStatus(ApprovalStatus.DECLINED);
		cancellation.setVerificationStatus(VerificationStatus.UNVERIFIED);
	}

	private void issueSubscriptionCancellationInvoice(SubscriptionCancellation cancellation, SubscriptionPaymentPeriod paymentPeriod) {
		Assert.notNull(cancellation);
		if (cancellation.hasCancellationFee()) {
			BigDecimal cancellationFee = cancellation.getCancellationFee();
			Calendar cancellationDate = DateUtilities.cloneCalendar(cancellation.getEffectiveDate());

			Calendar dueDateCancellationInvoice = DateUtilities.cloneCalendar(cancellationDate);
			dueDateCancellationInvoice.add(Calendar.DATE, paymentPeriod.getSubscriptionConfiguration().getPaymentTermsDays());

			SubscriptionPaymentDTO subscriptionPaymentDTO = new SubscriptionPaymentDTO()
					.setSoftwareFeeAmount(cancellationFee)
					.setDueDate(dueDateCancellationInvoice);

			SubscriptionInvoice subscriptionCancelInvoice = newSubscriptionInvoiceFromSubscriptionPaymentDTO(cancellation.getSubscriptionConfiguration().getCompany().getId(),
					subscriptionPaymentDTO, SubscriptionInvoiceType.CANCELLATION, paymentPeriod);
			cancellation.setCancellationInvoice(subscriptionCancelInvoice);
		}
	}

	@Override
	public SubscriptionConfiguration renewSubscriptionConfigurationForCompany(long subscriptionId, Optional<SubscriptionRenewalRequest> renewalRequest) {
		SubscriptionConfiguration oldSubscription = subscriptionConfigurationDAO.get(subscriptionId);
		if (renewalRequest.isPresent()) {
			validateRenewalRequest(oldSubscription, renewalRequest.get());
		}

		SubscriptionConfiguration newSubscription = new SubscriptionConfiguration();
		BeanUtilities.copyProperties(newSubscription, oldSubscription);

		newSubscription.setId(null);
		newSubscription.setParentSubscription(oldSubscription);
		newSubscription.setAddOns(Sets.<SubscriptionAddOnTypeAssociation>newHashSet());
		newSubscription.setAccountServiceTypeConfigurations(Sets.<SubscriptionAccountServiceTypeConfiguration>newHashSet());
		newSubscription.setSubscriptionFeeConfigurations(Sets.<SubscriptionFeeConfiguration>newHashSet());
		newSubscription.setNotes(Sets.<SubscriptionNote>newHashSet());
		newSubscription.setNumberOfRenewals(0);
		newSubscription.setPastPaymentPeriods(0);

		newSubscription.setSubscriptionStatusType(new SubscriptionStatusType(SubscriptionStatusType.PENDING_RENEWAL));

		newSubscription.setNumberOfPeriods((renewalRequest.isPresent()) ? renewalRequest.get().getNumberOfPeriods() : oldSubscription.getNumberOfPeriods());
		newSubscription.setEffectiveDate(DateUtilities.cloneCalendar(oldSubscription.getEndDate()));
		newSubscription.setEndDate(newSubscription.calculateEndDate());

		subscriptionConfigurationDAO.saveOrUpdate(newSubscription);
		subscriptionConfigurationDAO.saveOrUpdate(oldSubscription);

		renewSubscriptionAccountServiceTypeConfigurations(newSubscription, oldSubscription.getAccountServiceTypeConfigurations());
		renewSubscriptionAddOnAssociations(newSubscription, oldSubscription.getActiveSubscriptionAddOns());
		renewSubscriptionFeeConfiguration(newSubscription, oldSubscription.getActiveSubscriptionFeeConfiguration(), renewalRequest);

		newSubscription.setNextPaymentPeriodStartDate(newSubscription.getEffectiveDate());
		generateSubscriptionPaymentPeriods(newSubscription);

		subscriptionConfigurationDAO.saveOrUpdate(newSubscription);

		if (DateUtilities.getMonthsBetween(Calendar.getInstance(), newSubscription.getEffectiveDate()) < 1) {
			issueRegularSubscriptionInvoice(newSubscription.getId());
		}
		return newSubscription;
	}

	private void validateRenewalRequest(SubscriptionConfiguration oldSubscription, SubscriptionRenewalRequest renewalRequest) {
		if (renewalRequest.getModifyPricing()) {
			Assert.isTrue(renewalRequest.getSubscriptionPaymentTiers().size() == oldSubscription.getSubscriptionPaymentTiers().size());
			Collections.sort(renewalRequest.getSubscriptionPaymentTiers());
			int index = 0;
			for (SubscriptionPaymentTierRenewal newPaymentTier : renewalRequest.getSubscriptionPaymentTiers()) {
				Assert.isTrue(newPaymentTier.getMinimum().compareTo(oldSubscription.getSubscriptionPaymentTiers().get(index).getMinimum()) == 0);
				Assert.isTrue(newPaymentTier.getMaximum().compareTo(oldSubscription.getSubscriptionPaymentTiers().get(index).getMaximum()) == 0);
				index++;
			}
		}
	}

	private void renewSubscriptionFeeConfiguration(SubscriptionConfiguration newSubscription, SubscriptionFeeConfiguration oldFeeConfiguration,
												   Optional<SubscriptionRenewalRequest> renewalRequest) {
		SubscriptionFeeConfiguration newFeeConfiguration = new SubscriptionFeeConfiguration();
		BeanUtilities.copyProperties(newFeeConfiguration, oldFeeConfiguration);
		newFeeConfiguration.setId(null);
		newFeeConfiguration.setSubscriptionConfiguration(newSubscription);
		subscriptionFeeConfigurationDAO.saveOrUpdate(newFeeConfiguration);
		newSubscription.getSubscriptionFeeConfigurations().add(newFeeConfiguration);
		if (renewalRequest.isPresent() && renewalRequest.get().getModifyPricing()) {
			renewSubscriptionPaymentTierRenewals(newFeeConfiguration, renewalRequest.get().getSubscriptionPaymentTiers());
		} else {
			renewSubscriptionPaymentTiers(newFeeConfiguration, oldFeeConfiguration.getSubscriptionPaymentTiers());
		}
	}

	private void renewSubscriptionPaymentTiers(SubscriptionFeeConfiguration newFeeConfiguration, List<SubscriptionPaymentTier> oldPaymentTiers) {
		List<SubscriptionPaymentTier> newPaymentTiers = Lists.newArrayList();
		for (SubscriptionPaymentTier oldPaymentTier : oldPaymentTiers) {
			newPaymentTiers.add(renewSubscriptionPaymentTier(newFeeConfiguration, oldPaymentTier));
		}
		newFeeConfiguration.setSubscriptionPaymentTiers(newPaymentTiers);
	}

	private void renewSubscriptionPaymentTierRenewals(SubscriptionFeeConfiguration newFeeConfiguration, List<SubscriptionPaymentTierRenewal> renewPaymentTiers) {
		List<SubscriptionPaymentTier> newPaymentTiers = Lists.newArrayList();
		for (SubscriptionPaymentTierRenewal renewPaymentTier : renewPaymentTiers) {
			newPaymentTiers.add(renewSubscriptionPaymentTier(newFeeConfiguration, renewPaymentTier));
		}
		newFeeConfiguration.setSubscriptionPaymentTiers(newPaymentTiers);
	}

	private <T extends AbstractPaymentTier> SubscriptionPaymentTier renewSubscriptionPaymentTier(SubscriptionFeeConfiguration newFeeConfiguration, T paymentTier) {
		SubscriptionPaymentTier newPaymentTier = new SubscriptionPaymentTier();
		BeanUtilities.copyProperties(newPaymentTier, paymentTier);
		if (newPaymentTier.getMinimum().compareTo(BigDecimal.ZERO) == 0 ) {
			newPaymentTier.setSubscriptionPaymentTierSoftwareStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.ACTIVE));
			newPaymentTier.setSubscriptionPaymentTierVorStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.ACTIVE));
		} else {
			newPaymentTier.setSubscriptionPaymentTierSoftwareStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.NOT_REACHED));
			newPaymentTier.setSubscriptionPaymentTierVorStatusType(new SubscriptionPaymentTierStatusType(SubscriptionPaymentTierStatusType.NOT_REACHED));
		}
		newPaymentTier.setSubscriptionFeeConfiguration(newFeeConfiguration);
		newPaymentTier.setId(null);
		subscriptionPaymentTierDAO.saveOrUpdate(newPaymentTier);
		return newPaymentTier;
	}

	private void renewSubscriptionAddOnAssociations(SubscriptionConfiguration renewSubscription, Set<SubscriptionAddOnTypeAssociation> oldAddOnTypeAssociations) {
		Set<SubscriptionAddOnTypeAssociation> newAddOnTypeAssociations = Sets.newHashSet();
		for (SubscriptionAddOnTypeAssociation oldAddOnTypeAssociation : oldAddOnTypeAssociations) {
			SubscriptionAddOnTypeAssociation newAddOnTypeAssociation = new SubscriptionAddOnTypeAssociation(oldAddOnTypeAssociation.getSubscriptionAddOnType(), renewSubscription);
			newAddOnTypeAssociation.setEffectiveDate(renewSubscription.getEffectiveDate());
			subscriptionAddOnTypeAssociationDAO.saveOrUpdate(newAddOnTypeAssociation);
			newAddOnTypeAssociations.add(newAddOnTypeAssociation);
		}
		renewSubscription.setSubscriptionAddOns(newAddOnTypeAssociations);
	}

	private void renewSubscriptionAccountServiceTypeConfigurations(SubscriptionConfiguration renewSubscription, Set<SubscriptionAccountServiceTypeConfiguration> oldAccountServiceTypeConfigurations) {
		Set<SubscriptionAccountServiceTypeConfiguration> newAccountServiceTypeConfigurations = Sets.newHashSet();
		for (SubscriptionAccountServiceTypeConfiguration oldAccountServiceTypeConfiguration : oldAccountServiceTypeConfigurations) {
			SubscriptionAccountServiceTypeConfiguration newAccountServiceTypeConfiguration = new SubscriptionAccountServiceTypeConfiguration(
					oldAccountServiceTypeConfiguration.getAccountServiceType(), oldAccountServiceTypeConfiguration.getCountry(), renewSubscription);
			subscriptionAccountServiceTypeConfigurationDAO.saveOrUpdate(newAccountServiceTypeConfiguration);
			newAccountServiceTypeConfigurations.add(newAccountServiceTypeConfiguration);
		}
		renewSubscription.setAccountServiceTypeConfigurations(newAccountServiceTypeConfigurations);
	}

	@Override
	public void addNoteToSubscriptionConfiguration(long subscriptionId, NoteDTO noteDTO) {
		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);
		if (subscriptionConfiguration != null) {
			addNoteToSubscriptionConfiguration(subscriptionConfiguration, noteDTO);
		}
	}

	private void addNotesToSubscriptionConfiguration(long subscriptionId, List<NoteDTO> noteDTOs) {
		Assert.notNull(noteDTOs);
		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);
		if (subscriptionConfiguration != null) {
			for (NoteDTO noteDTO : noteDTOs) {
				addNoteToSubscriptionConfiguration(subscriptionConfiguration, noteDTO);
			}
		}
	}

	private void addNoteToSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration, NoteDTO noteDTO) {
		Assert.notNull(noteDTO);
		Assert.hasText(noteDTO.getContent());
		SubscriptionNote note = new SubscriptionNote(noteDTO.getContent(), subscriptionConfiguration);
		if (noteDTO.getIsPrivate()) {
			note.setPrivacy(PrivacyType.PRIVATE);
		}
		noteDAO.saveOrUpdate(note);
	}

	private void validateSubscriptionCancellation(SubscriptionConfiguration configuration) {
		Assert.notNull(configuration.getSubscriptionCancellation());
		// Cancellation is not in the past
		Assert.isTrue(!DateUtilities.isInPast(configuration.getSubscriptionCancellation().getEffectiveDate()));
		// Expiration is not before cancellation
		Assert.isTrue(!configuration.getEndDate().before(configuration.getSubscriptionCancellation().getEffectiveDate()));
		// Subscription is active or pending renewal
		Assert.isTrue(configuration.isActive() || configuration.isPendingRenewal());
	}

	/**
	 * This validates subscriptions before they are approved
	 */
	private void validateSubscriptionConfiguration(SubscriptionConfiguration subscription) {

		Assert.notNull(subscription);
		Assert.notNull(subscription.getSubscriptionPeriod());
		Assert.notNull(subscription.getEffectiveDate());
		Assert.notNull(subscription.getEndDate());
		Assert.notNull(subscription.getNumberOfPeriods());
		Assert.isTrue(subscription.getNumberOfPeriods() > 0);

		final int MAX_CLIENT_REF_ID_LENGTH = 50;

		Calendar effectiveDate = subscription.getEffectiveDate();
		Calendar requiredEndDate = DateUtilities.cloneCalendar(effectiveDate);
		requiredEndDate.add(Calendar.MONTH, subscription.getNumberOfPeriods() * subscription.getSubscriptionPeriod().getMonths());
		Assert.isTrue(DateUtilities.getMonthsBetween(subscription.getEndDate(), requiredEndDate) == 0,
				"Subscription termination date must be " + subscription.getNumberOfPeriods() * subscription.getSubscriptionPeriod().getMonths() + " months after effective date");
		Assert.isTrue(subscription.getEffectiveDate().get(Calendar.DAY_OF_MONTH) == 1, "The effective date must be a first day of a month");
		if (!subscription.isActive()) {
			Assert.isTrue(subscription.getEffectiveDate().compareTo(DateUtilities.getCalendarNow()) > 0);
		}

		if(StringUtilities.isNotEmpty(subscription.getClientRefId())) {
			Assert.isTrue(subscription.getClientRefId().length() < MAX_CLIENT_REF_ID_LENGTH, "The subscription client reference id can not exceed 50 letters");
		}

		/* Validate SubscriptionPaymentConfiguration */
		SubscriptionFeeConfiguration subscriptionPaymentConfiguration = subscriptionFeeConfigurationDAO.getLatestPendingApprovalSubscriptionFeeConfiguration(subscription.getId());
		if (!subscription.isApproved() || subscriptionPaymentConfiguration != null) {
			validateSubscriptionPaymentConfiguration(subscriptionPaymentConfiguration);
		}

		if (subscription.getDiscountedPeriods() > 0) {
			Assert.notNull(subscription.getDiscountedAmountPerPeriod());
		}

		Assert.isTrue(subscription.getSetUpFee() != null && subscription.getSetUpFee().compareTo(BigDecimal.ZERO) >= 0, "Set up fee must be higher or equal to $0.00");

		for (SubscriptionAddOnTypeAssociation addOnTypeAssociation : subscription.getSubscriptionAddOns()) {
			validateSubscriptionAddOnTypeAssociation(addOnTypeAssociation);
		}
	}

	private void validateSubscriptionPaymentConfiguration(SubscriptionFeeConfiguration subscriptionFeeConfiguration) {
		/* Validate SubscriptionPaymentConfiguration */
		/* The natural order of tiers is by minimum DESC */
		Assert.notNull(subscriptionFeeConfiguration);
		Assert.isTrue(subscriptionFeeConfiguration.getEffectiveDate().get(Calendar.DAY_OF_MONTH) == 1, "The effective date must be a first day of a month");
		if (subscriptionFeeConfiguration.getSubscriptionConfiguration().isActive()) {
			SubscriptionPaymentPeriod paymentPeriod = findNextNotInvoicedSubscriptionPaymentPeriod(subscriptionFeeConfiguration.getSubscriptionConfiguration().getId());
			if (paymentPeriod != null) {
				Assert.isTrue(paymentPeriod.getPeriodDateRange().getFrom().compareTo(subscriptionFeeConfiguration.getEffectiveDate()) <= 0);
			}
		}

		BigDecimal previousMaximum = BigDecimal.ZERO;
		BigDecimal previousPaymentAmount = BigDecimal.ZERO;
		BigDecimal previousVORAmount = BigDecimal.ZERO;
		for (SubscriptionPaymentTier tier : subscriptionFeeConfiguration.getSubscriptionPaymentTiers()) {
			validateSubscriptionPaymentTier(tier, subscriptionFeeConfiguration.getSubscriptionConfiguration().isVendorOfRecord());
			Assert.isTrue(tier.getMinimum().compareTo(previousMaximum) == 0,
					(previousMaximum.compareTo(BigDecimal.ZERO) == 0) ? "First payment tier minimum must always be zero"
							: "The minimum of the current payment tier must be equal to the maximum value of the previous tier");
			Assert.isTrue(tier.getPaymentAmount().compareTo(previousPaymentAmount) >= 0, "Payment amounts must be in ascending order");
			Assert.isTrue(tier.getVendorOfRecordAmount().compareTo(previousVORAmount) >= 0, "VOR Payment amounts must be in ascending order");
			previousMaximum = tier.getMaximum();
			previousPaymentAmount = tier.getPaymentAmount();
			previousVORAmount = tier.getVendorOfRecordAmount();
		}
		Assert.isTrue(previousMaximum.compareTo(SubscriptionPaymentTier.MAXIMUM) == 0, "The maximum of the last tier must be infinity");
		/* SubscriptionPaymentConfiguration validated */
	}

	private void validateSubscriptionPaymentTier(SubscriptionPaymentTier tier, boolean isVendorOfRecord) {
		Assert.notNull(tier);
		Assert.isTrue(tier.getMinimum() != null && tier.getMinimum().compareTo(BigDecimal.ZERO) >= 0, "Minimum must be higher or equal to $0.00");
		Assert.isTrue(tier.getMaximum() != null && tier.getMaximum().compareTo(tier.getMinimum()) > 0, "Maximum must be higher than minimum");
		Assert.isTrue(tier.getPaymentAmount() != null && tier.getPaymentAmount().compareTo(BigDecimal.ZERO) > 0, "Payment amount must be higher than $0.00");
		if (isVendorOfRecord) {
			Assert.isTrue(tier.getVendorOfRecordAmount() != null && tier.getVendorOfRecordAmount().compareTo(BigDecimal.ZERO) > 0, "Vendor of records amount must be higher than $0.00");
		}
	}

	private void validateSubscriptionAddOnTypeAssociation(SubscriptionAddOnTypeAssociation addOnTypeAssociation) {
		Assert.notNull(addOnTypeAssociation);
		Assert.notNull(addOnTypeAssociation.getSubscriptionConfiguration());
		Assert.notNull(addOnTypeAssociation.getSubscriptionAddOnType());
		Assert.isTrue(SubscriptionAddOnType.addOnTypeCodes.contains(addOnTypeAssociation.getSubscriptionAddOnType().getCode()));
		Assert.isTrue(addOnTypeAssociation.getCostPerPeriod().compareTo(BigDecimal.ZERO) > 0);
		Assert.isTrue(addOnTypeAssociation.getEffectiveDate().get(Calendar.DAY_OF_MONTH) == 1, "The effective date must be a first day of a month");
		if (addOnTypeAssociation.getSubscriptionConfiguration().isActive()) {
			SubscriptionPaymentPeriod paymentPeriod = findNextNotInvoicedSubscriptionPaymentPeriod(addOnTypeAssociation.getSubscriptionConfiguration().getId());
			if (paymentPeriod != null && addOnTypeAssociation.isPendingApproval()) {
				Assert.isTrue(paymentPeriod.getPeriodDateRange().getFrom().compareTo(addOnTypeAssociation.getEffectiveDate()) <= 0);
			}
		}
	}

	@Override
	public List<SubscriptionPaymentPeriod> findAllSubscriptionPaymentPeriods(long subscriptionId) {
		return paymentPeriodDAO.findBySubscriptionConfigurationId(subscriptionId);
	}

	@Override
	public SubscriptionPaymentPeriod findNextNotInvoicedSubscriptionPaymentPeriod(long subscriptionId) {
		return paymentPeriodDAO.findNextNotInvoicedSubscriptionPaymentPeriod(subscriptionId);
	}

	@Override
	public void updateSubscriptionConfigurationChanges(long subscriptionId, Calendar updateDate) {
		SubscriptionConfiguration subscription = subscriptionConfigurationDAO.get(subscriptionId);
		Assert.notNull(subscription);
		// Does it need expiration or cancellation?
		if (subscription.getSubscriptionCancellation() != null && subscription.getSubscriptionCancellation().isApproved()) {
			applySubscriptionCancellation(subscription, updateDate);

		} else if (subscription.getEndDate().compareTo(updateDate) <= 0) {
			applySubscriptionExpiration(subscription, updateDate);

		} else {
			applySubscriptionConfigurationUpdates(subscription, updateDate);
		}
	}

	void applySubscriptionCancellation(SubscriptionConfiguration subscription, Calendar updateDate) {
		Assert.notNull(subscription);
		boolean isActiveSubscription = subscription.isActive();
		// Change pricing
		changeCompanyAccountPricingTypeFromSubscriptionToTransactional(subscription, updateDate, true);
		// Proceed with the cancellation
		logger.debug("[updateSubscriptionConfigurationChanges] Cancelling subscription id: " + subscription.getId());
		subscription.setSubscriptionStatusType(new SubscriptionStatusType(SubscriptionStatusType.CANCELLED));

		if (isActiveSubscription) {
			//Don't notify for future renewals
			userNotificationService.onSubscriptionConfigurationCancelled(subscription);
		}
	}

	private void applySubscriptionExpiration(SubscriptionConfiguration subscription, Calendar updateDate) {
		// Activate the renewal - if any
		SubscriptionConfiguration newSubscription = subscriptionConfigurationDAO.findRenewSubscriptionConfiguration(subscription.getId());
		if (newSubscription != null) {
			newSubscription.setSubscriptionStatusType(new SubscriptionStatusType(SubscriptionStatusType.ACTIVE));
			Calendar nextThroughputResetDate = calculateNextThroughputResetDate(newSubscription);
			newSubscription.setNextThroughputResetDate(nextThroughputResetDate);
			subscriptionConfigurationDAO.saveOrUpdate(newSubscription);
			accountRegisterService.resetAccountRegisterForAccountPricingType(newSubscription.getCompany());
		} else {
			// Change the pricing back to transactional - only if there are no renewals
			changeCompanyAccountPricingTypeFromSubscriptionToTransactional(subscription, updateDate, false);
		}
		// Proceed with the expiration
		subscription.setSubscriptionStatusType(new SubscriptionStatusType(SubscriptionStatusType.EXPIRED));
	}

	@Override
	public void updateApprovedSubscriptionsPricingType(Calendar updateDate) {
		// This will change the pricing to subscription pricing on the effectiveDate of an active subscription
		for (SubscriptionConfiguration subscription : subscriptionConfigurationDAO.findApprovedSubscriptionConfigurationsWithTransactionalPricingByEffectiveDate(updateDate)) {
			changeCompanyAccountPricingTypeFromTransactionalToSubscription(subscription, updateDate);
		}
	}

	/**
	 * Changing from transaction to subscription pricing means there's an active subscription effective on the scheduledDate
	 */
	private void changeCompanyAccountPricingTypeFromTransactionalToSubscription(SubscriptionConfiguration activeConfiguration, Calendar scheduledDate) {
		Assert.notNull(scheduledDate);
		Assert.notNull(activeConfiguration);
		Assert.notNull(activeConfiguration.getCompany());
		if (activeConfiguration.getCompany().getAccountPricingType().isTransactionalPricing()) {
			Assert.isTrue(!scheduledDate.before(activeConfiguration.getEffectiveDate()), "Subscription effective date must be before the scheduled date");

			CompanyAccountPricingTypeChange pricingTypeChange = companyAccountPricingTypeChangeDAO.getCompanyAccountPricingTypeChange(activeConfiguration.getCompany()
					.getId(), activeConfiguration.getEffectiveDate());
			if (pricingTypeChange != null && pricingTypeChange.isFromTransactionalToSubscription()) {
				PaymentConfiguration paymentConfiguration = activeConfiguration.getCompany().getPaymentConfiguration();
				paymentConfiguration.setAccountPricingType(new AccountPricingType(AccountPricingType.SUBSCRIPTION_PRICING_TYPE));
				paymentConfiguration.setAccountPricingModifiedOn(Calendar.getInstance());

				for (SubscriptionAccountServiceTypeConfiguration c : activeConfiguration.getAccountServiceTypeConfigurations()) {
					accountPricingService.updatePaymentConfigurationAccountServiceType(paymentConfiguration, c.getCountry().getId(), c.getAccountServiceType());
				}
				userNotificationService.onSubscriptionConfigurationEffective(activeConfiguration);

				pricingTypeChange.setActualChangeDate(DateUtilities.getCalendarNow());
				accountRegisterService.resetAccountRegisterForAccountPricingType(activeConfiguration.getCompany());
			}
		}
	}

	/**
	 * Changing from subscription to transaction pricing means the active subscription must become cancelled or expired on the scheduledDate
	 */
	protected void changeCompanyAccountPricingTypeFromSubscriptionToTransactional(SubscriptionConfiguration activeSubscription, Calendar scheduledDate, boolean isCancellation) {
		Assert.notNull(scheduledDate);
		Assert.notNull(activeSubscription);
		Assert.notNull(activeSubscription.getCompany());
		if (activeSubscription.getCompany().getAccountPricingType().isSubscriptionPricing()) {
			Assert.state(activeSubscription.isActive());

			if (isCancellation) {
				Assert.isTrue(activeSubscription.getSubscriptionCancellation() != null && activeSubscription.getSubscriptionCancellation()
								.isApproved() && !scheduledDate.before(activeSubscription.getCancelledOn()),
						"Pricing change for cancelled subscription was requested but subscription wasn't cancelled on the scheduled date");
			} else {
				// Is expiration
				Assert.isTrue(!scheduledDate.before(activeSubscription.getEndDate()), "Pricing change for expired subscription was requested but subscription didn't expire on the scheduled date");
			}

			PaymentConfiguration paymentConfiguration = activeSubscription.getCompany().getPaymentConfiguration();
			if (paymentConfiguration.isSubscriptionPricing()) {
				paymentConfiguration.setAccountPricingType(new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE));
				paymentConfiguration.setAccountPricingModifiedOn(Calendar.getInstance());
				// The default for transactional pricing is None
				accountPricingService.updatePaymentConfigurationAccountServiceType(paymentConfiguration, new AccountServiceType(AccountServiceType.NONE));
				accountRegisterService.resetAccountRegisterForAccountPricingType(activeSubscription.getCompany());
			}

			Calendar pricingTypeModificationDate;
			if (isCancellation) {
				Assert.notNull(activeSubscription.getSubscriptionCancellation());
				pricingTypeModificationDate = DateUtilities.cloneCalendar(activeSubscription.getSubscriptionCancellation().getEffectiveDate());
			} else {
				pricingTypeModificationDate = DateUtilities.cloneCalendar(activeSubscription.getEndDate());
			}

			CompanyAccountPricingTypeChange pricingTypeChange = companyAccountPricingTypeChangeDAO.getCompanyAccountPricingTypeChange(activeSubscription.getCompany().getId(), pricingTypeModificationDate);
			if (pricingTypeChange != null && pricingTypeChange.isFromSubscriptionToTransactional()) {
				pricingTypeChange.setActualChangeDate(DateUtilities.getCalendarNow());

			}
		}
	}

	private void applySubscriptionConfigurationUpdates(SubscriptionConfiguration subscription, Calendar updateDate) {
		// Check for approved fee configuration changes
		SubscriptionFeeConfiguration activeFeeConfiguration = subscription.getActiveSubscriptionFeeConfiguration();
		for (SubscriptionFeeConfiguration feeConfiguration : subscription.getSubscriptionFeeConfigurations()) {
			if (feeConfiguration.isApproved() && feeConfiguration.getEffectiveDate().compareTo(updateDate) <= 0
					&& !feeConfiguration.getActive() && !feeConfiguration.getId().equals(activeFeeConfiguration.getId())) {
				activeFeeConfiguration.setActive(false);
				activeFeeConfiguration.setApprovalStatus(ApprovalStatus.REMOVED);
				activeFeeConfiguration.setRemovedOn(Calendar.getInstance());

				feeConfiguration.setActive(true);
				break;
			}
		}
		// Check for approved addOn changes
		Set<SubscriptionAddOnTypeAssociation> activeSubscriptionAddOns = subscription.getActiveSubscriptionAddOns();

		Map<String, SubscriptionAddOnTypeAssociation> activeAddOnAssociationMap = Maps.newHashMap();
		for (SubscriptionAddOnTypeAssociation activeAddOn : activeSubscriptionAddOns) {
			activeAddOnAssociationMap.put(activeAddOn.getSubscriptionAddOnType().getCode(), activeAddOn);
		}

		for (SubscriptionAddOnTypeAssociation addOn : subscription.getSubscriptionAddOns()) {
			//If the effective days has past
			if (addOn.getEffectiveDate().before(updateDate)) {
				//And if it's APPROVED but not ACTIVE
				if (addOn.isApproved() && !addOn.getActive()
						&& !activeAddOnAssociationMap.containsKey(addOn.getSubscriptionAddOnType().getCode())) {
					addOn.setActive(true);
					activeAddOnAssociationMap.put(addOn.getSubscriptionAddOnType().getCode(), addOn);

				} else if (addOn.isPendingRemoval()) {
					addOn.setApprovalStatus(ApprovalStatus.REMOVED);
					addOn.setRemovedOn(Calendar.getInstance());
					addOn.setActive(false);
					if (activeAddOnAssociationMap.containsKey(addOn.getSubscriptionAddOnType().getCode())) {
						SubscriptionAddOnTypeAssociation activeAddOn = activeAddOnAssociationMap.get(addOn.getSubscriptionAddOnType().getCode());
						activeAddOn.setActive(false);
						activeAddOn.setApprovalStatus(ApprovalStatus.REMOVED);
					}
				}
			}
		}
	}

	@Override
	public SubscriptionConfiguration findLatestPendingApprovalSubscriptionConfigurationByCompanyId(long companyId) {
		return subscriptionConfigurationDAO.findLatestPendingApprovalSubscriptionConfigurationByCompanyId(companyId);
	}

	@Override
	public SubscriptionConfiguration findLatestNotReadySubscriptionConfigurationByCompanyId(long companyId) {
		return subscriptionConfigurationDAO.findLatestNotReadySubscriptionConfigurationByCompanyId(companyId);
	}

	@Override
	public BigDecimal calculateSubscriptionPaymentTotalAmount(long subscriptionConfigurationId) {
		SubscriptionConfiguration subscriptionConfiguration = findSubscriptionConfigurationById(subscriptionConfigurationId);

		if (subscriptionConfiguration != null) {
			long companyId = subscriptionConfiguration.getCompany().getId();
			AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(companyId);
			BigDecimal assignmentThroughput = accountRegister.getAccountRegisterSummaryFields().getAssignmentSoftwareThroughput();

			SubscriptionPaymentDTO subscriptionPaymentDTO = subscriptionCalculator.calculateSubscriptionPayment(subscriptionConfiguration, assignmentThroughput, Calendar.getInstance());
			return subscriptionPaymentDTO.getTotal();
		}
		return BigDecimal.ZERO;
	}

	@Override
	public Set<SubscriptionConfiguration> findPreviousSubscriptionConfigurationsByCompanyId(long companyId) {
		return Sets.newHashSet(subscriptionConfigurationDAO.findPreviousSubscriptionConfigurationsByCompanyId(companyId));
	}

	@Override
	public List<SubscriptionAddOnType> findAllSubscriptionAddOnTypes() {
		return lookupEntityDAO.findLookupEntities(SubscriptionAddOnType.class);
	}

	@Override
	public boolean hasMboServiceType(long companyId) {
		SubscriptionConfiguration subscriptionConfiguration = findActiveSubscriptionConfigurationByCompanyId(companyId);
		return (subscriptionConfiguration != null && subscriptionConfiguration.hasAccountServiceType(AccountServiceType.MBO));
	}

	@Override
	public SubscriptionConfiguration updateYearlySubscriptionThroughput(long subscriptionId) {
		SubscriptionConfiguration subscriptionConfiguration = subscriptionConfigurationDAO.get(subscriptionId);
		if (subscriptionConfiguration != null && subscriptionConfiguration.getNextThroughputResetDate() != null) {
			//Leave this in two different if statements, otherwise if the date is null, isInFuture will return false.
			if (subscriptionConfiguration.isActive() && !DateUtilities.isInFuture(subscriptionConfiguration.getNextThroughputResetDate())) {
				//1 - reset the throughput
				accountRegisterService.resetAccountRegisterForAccountPricingType(subscriptionConfiguration.getCompany());
				//2 - update next reset date
				Calendar nextThroughputResetDate = calculateNextThroughputResetDate(subscriptionConfiguration);
				subscriptionConfiguration.setNextThroughputResetDate(nextThroughputResetDate);
				subscriptionConfigurationDAO.saveOrUpdate(subscriptionConfiguration);
				//3 - reset tiers
				SubscriptionFeeConfiguration feeConfiguration = subscriptionConfiguration.getActiveSubscriptionFeeConfiguration();
				Assert.notNull(feeConfiguration);
				feeConfiguration.resetSubscriptionPaymentTiers();
			}
		}
		return subscriptionConfiguration;
	}

	@Override
	public Calendar calculateNextThroughputResetDate(SubscriptionConfiguration subscriptionConfiguration) {
		Assert.notNull(subscriptionConfiguration);
		Calendar nextThroughputDate = null;
		if (subscriptionConfiguration != null && subscriptionConfiguration.getEffectiveDate() != null && subscriptionConfiguration.getEndDate() != null) {
			int monthsBetweenEffectiveDateAndEndDate = DateUtilities.getMonthsBetween(subscriptionConfiguration.getEffectiveDate(), subscriptionConfiguration.getEndDate());
			if (monthsBetweenEffectiveDateAndEndDate > 12) {
				int monthsBetweenEffectiveDateAndToday = DateUtilities.getMonthsBetween(subscriptionConfiguration.getEffectiveDate(), Calendar.getInstance());
				nextThroughputDate = DateUtilities.cloneCalendar(subscriptionConfiguration.getEffectiveDate());
				nextThroughputDate.add(Calendar.YEAR, monthsBetweenEffectiveDateAndToday / 12 + 1);
			}
		}
		return nextThroughputDate;
	}

	@Override
	public Calendar calculateNextThroughputResetDate(long subscriptionConfigurationId) {
		return calculateNextThroughputResetDate(subscriptionConfigurationDAO.get(subscriptionConfigurationId));
	}

}