
package com.workmarket.domains.payments.service;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateFilter;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.*;
import com.workmarket.domains.model.account.pricing.AccountPricingServiceTypeEntity;
import com.workmarket.domains.model.account.pricing.PaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.request.FundsRequest;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.fulfillment.FulfillmentStrategy;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.CreditMemo;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceCollection;
import com.workmarket.domains.model.invoice.InvoiceSummary;
import com.workmarket.domains.model.invoice.PaymentFulfillmentStatusType;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.invoice.item.CreditMemoIssuableInvoiceLineItem;
import com.workmarket.domains.model.invoice.item.DepositReturnFeeInvoiceLineItem;
import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import com.workmarket.domains.model.invoice.item.LatePaymentFeeInvoiceLineItem;
import com.workmarket.domains.model.invoice.item.MiscFeeInvoiceLineItem;
import com.workmarket.domains.model.invoice.item.SubscriptionAddOnLineItem;
import com.workmarket.domains.model.invoice.item.SubscriptionDiscountLineItem;
import com.workmarket.domains.model.invoice.item.SubscriptionSetupFeeLineItem;
import com.workmarket.domains.model.invoice.item.SubscriptionSoftwareFeeLineItem;
import com.workmarket.domains.model.invoice.item.SubscriptionVORLineItem;
import com.workmarket.domains.model.invoice.item.SubscriptionVorSoftwareFeeLineItem;
import com.workmarket.domains.model.invoice.item.WithdrawalReturnFeeInvoiceLineItem;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.option.WorkOption;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.DrugTest;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.domains.payments.dao.AccountRegisterDAO;
import com.workmarket.domains.payments.dao.AccountRegisterSummaryFieldsDAO;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.domains.payments.dao.FastFundsReceivableCommitmentDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionActivityDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.payments.dao.WeeklyRevenueReportDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.project.ProjectInvoiceBundle;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.project.ProjectBudgetService;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.InvoicePaymentHelper;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.business.accountregister.BankAccountTransactionExecutor;
import com.workmarket.service.business.accountregister.CreditMemoType;
import com.workmarket.service.business.accountregister.CreditRegisterTransaction;
import com.workmarket.service.business.accountregister.DebitRegisterTransaction;
import com.workmarket.service.business.accountregister.FeeCalculator;
import com.workmarket.service.business.accountregister.RegisterTransactionExecutor;
import com.workmarket.service.business.accountregister.TransactionBreakdown;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionExecutableFactory;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionFactory;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentDTO;
import com.workmarket.service.business.integration.mbo.MboProfileDAO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.exception.account.AccountRegisterNoActiveWorkResourceException;
import com.workmarket.service.exception.account.CreditCardErrorException;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InsufficientSpendLimitException;
import com.workmarket.service.exception.account.InvalidBankAccountException;
import com.workmarket.service.exception.account.InvalidSpendLimitException;
import com.workmarket.service.exception.account.PaymentTermsAPCreditLimitException;
import com.workmarket.service.exception.account.WithdrawalExceedsDailyMaximumException;
import com.workmarket.service.exception.project.InsufficientBudgetException;
import com.workmarket.service.exception.tax.InvalidTaxEntityException;
import com.workmarket.service.infra.business.PaymentService;
import com.workmarket.service.option.OptionsService;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.AutoPopulatingList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public abstract class AccountRegisterServiceAbstract implements AccountRegisterService {

	@Autowired protected LaneService laneService;
	@Autowired protected PaymentService paymentService;
	@Autowired protected UserNotificationService userNotificationService;
	@Autowired protected WorkService workService;
	@Autowired protected BillingService billingService;
	@Autowired protected WorkResourceService workResourceService;
	@Autowired protected PricingService pricingService;
	@Autowired protected WorkBundleService workBundleService;
	@Autowired protected TaxService taxService;
	@Autowired protected BankAccountDAO bankAccountDAO;
	@Autowired protected UserDAO userDAO;
	@Autowired protected RegisterTransactionDAO registerTransactionDAO;
	@Autowired protected AccountRegisterDAO accountRegisterDAO;
	@Autowired protected AccountRegisterSummaryFieldsDAO accountRegisterSummaryFieldsDAO;
	@Autowired protected RegisterTransactionActivityDAO registerTransactionActivityDAO;
	@Autowired protected WeeklyRevenueReportDAO weeklyRevenueReportDAO;
	@Autowired protected RegisterTransactionFactory registerTransactionFactory;
	@Autowired protected FeeCalculator feeCalculator;
	@Autowired protected ProjectService projectService;
	@Autowired protected ProjectBudgetService projectBudgetService;
	@Autowired protected CompanyService companyService;
	@Autowired protected InvoicePaymentHelper invoicePaymentHelper;
	@Autowired protected RegisterTransactionExecutableFactory registerTransactionExecutableFactory;
	@Autowired protected MboProfileDAO mboProfileDAO;
	@Autowired protected FeatureEvaluator featureEvaluator;
	@Autowired protected AccountPricingService accountPricingService;
	@Autowired protected UserService userService;
	@Autowired protected FastFundsReceivableCommitmentDAO fastFundsReceivableCommitmentDAO;
	@Qualifier("workOptionsService") @Autowired protected OptionsService<AbstractWork> workOptionsService;

	private static final String GENERAL_CASH = "general_cash";
	private static final Log logger = LogFactory.getLog(AccountRegisterServiceAbstract.class);

	public Long[] acceptWork(WorkResource workResource, String resourceToReceivePay, String buyerToPay) throws InsufficientFundsException, PaymentTermsAPCreditLimitException {
		Assert.notNull(workResource, "WorkResource can't be null");
		logger.debug("acceptWork():" + buyerToPay);
		// First, cancel the previous commitments
		Work work = workResource.getWork();
		voidWork(work);

		if (work.isWorkBundle()) {
			return null;
		}

		// No authorization required for Lane 0/1 or assignments paid offline (i.e. MBO)
		LaneType laneType = laneService.getLaneTypeForUserAndCompany(workResource.getUser().getId(), workResource.getWork().getCompany().getId());
		boolean hasOfflinePayment = workOptionsService.hasOption(work, WorkOption.MBO_ENABLED, "true") ||
			workService.isOfflinePayment(work);
		if (LaneType.LANE_0.equals(laneType) || LaneType.LANE_1.equals(laneType) || hasOfflinePayment) {
			return null;
		}

		WorkCostDTO accountRegisterMoniesDTO = calculateCostOnSentWork(work);
		logger.debug(accountRegisterMoniesDTO.toString());

		//Worker account register
		AccountRegister resourceRegister = findDefaultRegisterForCompany(workResource.getUser().getCompany().getId());
		//Work company owner register
		AccountRegister workRegister = findDefaultRegisterForCompany(workResource.getWork().getCompany().getId());

		// Commit to the work - the worker should see a pending commitment payment in their account register.
		// Worker Transaction
		String transactionExecutorName = RegisterTransactionExecutor.createBeanName(Boolean.TRUE, resourceToReceivePay);
		RegisterTransactionExecutor registerTransactionExecutor = registerTransactionExecutableFactory.newInstance(transactionExecutorName);
		WorkResourceTransaction resourceWorkResourceTransaction = registerTransactionExecutor.execute(work, workResource, resourceRegister,
				accountRegisterMoniesDTO.getTotalResourceCost());

		// Buyer Transaction
		transactionExecutorName = RegisterTransactionExecutor.createBeanName(Boolean.TRUE, buyerToPay);
		registerTransactionExecutor = registerTransactionExecutableFactory.newInstance(transactionExecutorName);
		WorkResourceTransaction buyerWorkResourceTransaction = registerTransactionExecutor.execute(work, workResource, workRegister,
				accountRegisterMoniesDTO.getTotalBuyerCost().negate());

		//TODO: move this to the implementation classes as opposed to the abstract
		// Remove funds from project/general
		if (!work.hasPaymentTerms()) {
			if (work.hasProject() && work.getProject().isReservedFundsEnabled()) {
				ProjectTransaction projectTransaction = new ProjectTransaction();
				projectTransaction.setProject(work.getProject());
				projectTransaction.setParentTransaction(buyerWorkResourceTransaction);

				RegisterTransactionExecutor projectRegisterTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.REMOVE_FUNDS_FROM_PROJECT);
				projectRegisterTransactionsAbstract.setPending(Boolean.TRUE);
				projectRegisterTransactionsAbstract.execute(workRegister, accountRegisterMoniesDTO.getTotalBuyerCost().negate(), projectTransaction);

			} else {
				GeneralTransaction generalTransaction = new GeneralTransaction();
				generalTransaction.setParentTransaction(buyerWorkResourceTransaction);

				RegisterTransactionExecutor generalRegisterTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.REMOVE_FUNDS_FROM_GENERAL);
				generalRegisterTransactionsAbstract.setPending(Boolean.TRUE);
				generalRegisterTransactionsAbstract.execute(workRegister, accountRegisterMoniesDTO.getTotalBuyerCost().negate(), generalTransaction);
			}
		}

		// Remove budget from project if the work has project and the project enables budget
		decreaseProjectBudget(work, accountRegisterMoniesDTO);

		FulfillmentStrategy fulfillmentStrategy = createFulfillmentStrategyFromWorkCostDTO(accountRegisterMoniesDTO);
		work.setFulfillmentStrategy(fulfillmentStrategy);


		return new Long[]{resourceWorkResourceTransaction.getId(), buyerWorkResourceTransaction.getId()};
	}

	protected Long commitToWork(Work work) throws InsufficientFundsException, InsufficientBudgetException, InsufficientSpendLimitException, AccountRegisterConcurrentException {
		Assert.notNull(work);

		if (work.getPricingStrategyType() != null && work.getPricingStrategyType().isInternal()) {
			return null;
		}

		WorkResource workResource = workService.findActiveWorkResource(work.getId());
		return commitToWork(work, workResource);
	}

	protected abstract WorkResourceTransaction createAuthorizationTransaction(Work work, WorkCostDTO accountRegisterMoniesDTO, WorkResource authorizeWorkResource, boolean updateSummaries);

	protected <T extends Work> Long commitToWork(T work, WorkResource authorizeWorkResource) throws InsufficientFundsException,
			InsufficientSpendLimitException, InsufficientBudgetException, AccountRegisterConcurrentException {

		Assert.notNull(work, "Work can't be null");
		try {

			WorkCostDTO accountRegisterMoniesDTO = calculateCostOnSentWork(work);
			if (work.isWorkBundle() && !work.isSent()) {
				Assert.isTrue(NumberUtilities.isPositive(accountRegisterMoniesDTO.getTotalBuyerCost()));
			}
			logger.debug(accountRegisterMoniesDTO.toString());

			// Check the buyer's spend limit per assignment
			checkBuyerSpendLimit(accountRegisterMoniesDTO, work.getBuyer());

			WorkResourceTransaction workResourceTransaction = createAuthorizationTransaction(work, accountRegisterMoniesDTO, authorizeWorkResource, true);
			Assert.notNull(workResourceTransaction);

			// Remove budget from project if the work has project and the project enables budget
			decreaseProjectBudget(work, accountRegisterMoniesDTO);
			return workResourceTransaction.getId();

		} catch (HibernateOptimisticLockingFailureException | CannotAcquireLockException | StaleObjectStateException | LockAcquisitionException | DataException e) {
			throw new AccountRegisterConcurrentException(e.getMessage());
		}
	}


	/**
	 * @param work
	 * @return
	 * @throws Exception Please note that this function calculates totalMaximumResourceCost
	 */
	public WorkCostDTO calculateCostOnSentWork(Work work) {
		Assert.notNull(work);

		if (work.isWorkBundle()) {
			// return the Total Buyer Cost of bundle only until it's sent
			if (!work.isActive() && !work.isSent()) {
				WorkBundle workBundle = (WorkBundle)work;
				BigDecimal budgetCost = workBundleService.getBundleBudget(work.getBuyer(), workBundle);
				workBundle.setBundleBudget(budgetCost);
				return new WorkCostDTO()
					.setTotalBuyerCost(budgetCost)
					.setTotalResourceCost(BigDecimal.ZERO)
					.setBuyerFee(BigDecimal.ZERO);
			}
		}

		BigDecimal maximumResourceCost = pricingService.calculateMaximumResourceCost(work);
		BigDecimal buyerNetMoneyFee = pricingService.calculateBuyerNetMoneyFee(work, maximumResourceCost);
		BigDecimal totalBuyerCost = buyerNetMoneyFee.add(maximumResourceCost);

		WorkCostDTO workCost = roundCostAndFees(totalBuyerCost, maximumResourceCost, buyerNetMoneyFee);
		logger.debug(workCost);
		return workCost;
	}

	@Override
	public abstract Long[] acceptWork(WorkResource workResource) throws InsufficientFundsException,
			PaymentTermsAPCreditLimitException;

	@Override
	public WorkCostDTO calculateCostOnCompleteWork(Work work, WorkResource workResource) {
		Assert.notNull(workResource);
		Assert.notNull(work);
		BigDecimal totalResourceCost = pricingService.calculateTotalResourceCost(work, workResource);
		BigDecimal buyerNetMoneyFee = pricingService.calculateBuyerNetMoneyFee(work, totalResourceCost);
		BigDecimal resourceCostPlusFee = buyerNetMoneyFee.add(totalResourceCost);
		return roundCostAndFees(resourceCostPlusFee, totalResourceCost, buyerNetMoneyFee);
	}

	@Override
	public WorkCostDTO calculateOfflinePaymentCostOnCompleteWork(Work work, WorkResource workResource) {
		Assert.notNull(workResource);
		Assert.notNull(work);
		BigDecimal totalResourceCost = pricingService.calculateTotalResourceCost(work, workResource);
		return roundCostAndFees(totalResourceCost, totalResourceCost, totalResourceCost);
	}

	/*
	 * Resolves three issues, read below
	 */
	WorkCostDTO roundCostAndFees(BigDecimal totalBuyerCost, BigDecimal maximumResourceCost, BigDecimal buyerNetMoneyFee) {
		maximumResourceCost = roundResult(maximumResourceCost, true);

		if (BigDecimal.ZERO.compareTo(buyerNetMoneyFee) == 0) {
			totalBuyerCost = maximumResourceCost;
		} else {
			totalBuyerCost = roundResult(totalBuyerCost, false);
			buyerNetMoneyFee = totalBuyerCost.subtract(maximumResourceCost);// 3. Subtract two rounded values, no rounding issues. Resolve Account_Register reconcilation issues.
		}
		return new WorkCostDTO(maximumResourceCost, buyerNetMoneyFee, totalBuyerCost);
	}

	BigDecimal roundResult(BigDecimal value, Boolean up) {
		logger.debug("roundResult prior to rounding:" + value + " up:" + up);
		if (up) {
			value = value.setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			value = value.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		logger.debug("roundResult after rounding:" + value);
		return value;
	}

	@Override
	public boolean completeWork(WorkResource workResource) {
		Assert.notNull(workResource);
		logger.debug("completeWork()....");

		// WorkStatusService calls this from closeWork()...
		Work work = workResource.getWork();

		// Find the original delegating resource and user their lane
		// relationship for fee determination
		Company buyerCompany = work.getCompany();
		WorkResource originalResource = findOriginalWorkResource(workResource);

		logger.debug("CompleteWork resourceId:" + workResource.getUser().getId() + ", originalResourceId:"
				+ originalResource.getUser().getId() + " buyerCompanyId:" + buyerCompany.getId());

		// Find commitments and marks them non pending
		AccountRegister buyerAccountRegister = findDefaultRegisterForCompany(buyerCompany.getId());
		AccountRegister resourceAccountRegister = findDefaultResourceRegisterForWork(work);

		LaneType laneType = laneService.getLaneTypeForUserAndCompany(originalResource.getUser().getId(), buyerCompany.getId());
		if (laneType == null) {
			return false;
		}
		boolean hasOfflinePayment = workOptionsService.hasOption(work, WorkOption.MBO_ENABLED, "true");

		if (laneType.isLane0() || laneType.isLane1() || hasOfflinePayment) {
			return false;
		}

		// Any pending amounts are reversed in the AccountRegister Summaries and pending flags are set false.
		voidWork(work);

		// Determine the work transaction fee for the buyer
		WorkCostDTO workCostDTO = calculateCostOnCompleteWork(work, workResource);
		logger.debug(workCostDTO.toString());

		// Verify there are sufficient funds in buyers account. For NetMoney
		// there probably is, however for Payment Terms there might not be.
		verifySufficientBuyerFunds(work, workCostDTO, false);

		//Buyer fee account register transaction
		createAndExecuteFeePaymentWorkResourceTransaction(workResource, buyerAccountRegister, workCostDTO.getBuyerFee(), laneType);
		//Buyer work price register transaction
		createAndExecuteBuyerWorkPaymentWorkResourceTransaction(workResource, buyerAccountRegister, workCostDTO.getTotalResourceCost());

		work.setFulfillmentStrategy(createFulfillmentStrategyFromWorkCostDTO(workCostDTO));
		//resource account register transaction
		createAndExecuteResourceWorkPaymentWorkResourceTransaction(workResource, resourceAccountRegister, workCostDTO.getTotalResourceCost());

		// Remove money from general/project cash when pay immediate work
		if (!work.hasPaymentTerms()) {
			if (work.hasProject() && work.getProject().isReservedFundsEnabled()) {
				ProjectTransaction projectTransaction = new ProjectTransaction();
				projectTransaction.setProject(work.getProject());

				RegisterTransactionExecutor projectRegisterTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.REMOVE_FUNDS_FROM_PROJECT);
				projectRegisterTransactionsAbstract.setPending(Boolean.TRUE);
				projectRegisterTransactionsAbstract.execute(buyerAccountRegister, workCostDTO.getTotalBuyerCost().negate(), projectTransaction);

			} else {
				GeneralTransaction generalTransaction = new GeneralTransaction();

				RegisterTransactionExecutor generalRegisterTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.REMOVE_FUNDS_FROM_GENERAL);
				generalRegisterTransactionsAbstract.setPending(Boolean.TRUE);
				generalRegisterTransactionsAbstract.execute(buyerAccountRegister, workCostDTO.getTotalBuyerCost().negate(), generalTransaction);
			}

			// Remove budget when pay immediate work
			decreaseProjectBudget(work, workCostDTO);

		}

		return true;
	}

	@Override
	public boolean completeOfflinePayment(WorkResource workResource) {
		Assert.notNull(workResource);
		logger.debug("completeWork()....");
		Work work = workResource.getWork();

		AccountRegister buyerAccountRegister = findDefaultRegisterForCompany(work.getCompany().getId());
		AccountRegister resourceAccountRegister = findDefaultResourceRegisterForWork(work);

		// Determine the work transaction fee for the buyer
		WorkCostDTO workCostDTO = calculateOfflinePaymentCostOnCompleteWork(work, workResource);

		//Buyer work price register transaction
		createAndExecuteBuyerWorkOfflinePaymentWorkResourceTransaction(workResource, buyerAccountRegister, workCostDTO.getTotalResourceCost());
		// resource account register transaction
		createAndExecuteResourceWorkOfflinePaymentWorkResourceTransaction(workResource, resourceAccountRegister, workCostDTO.getTotalResourceCost());

		work.setFulfillmentStrategy(createFulfillmentStrategyFromWorkCostDTO(workCostDTO));
		return true;
	}


	private WorkResourceTransaction createAndExecuteBuyerWorkPaymentWorkResourceTransaction(WorkResource workResource, AccountRegister buyerAccountRegister, BigDecimal amount, boolean updateSummaries, boolean isBundled, boolean isBatchPayment) {
		Assert.notNull(workResource);

		//We need to update the accountPricingTypeEntity based on the new company settings as of the payment date.
		Work work = workResource.getWork();
		AccountPricingServiceTypeEntity accountPricingServiceTypeEntity = new AccountPricingServiceTypeEntity();
		accountPricingServiceTypeEntity.setAccountPricingType(work.getCompany().getAccountPricingType());
		accountPricingServiceTypeEntity.setAccountServiceType(accountPricingService.findAccountServiceTypeConfiguration(work));
		work.setAccountPricingServiceTypeEntity(accountPricingServiceTypeEntity);

		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.BUYER_WORK_PAYMENT);
		return registerTransactionsAbstract.execute(work, workResource, buyerAccountRegister, amount.negate(), updateSummaries, isBundled, isBatchPayment);
	}

	private WorkResourceTransaction createAndExecuteBuyerWorkOfflinePaymentWorkResourceTransaction(WorkResource workResource, AccountRegister buyerAccountRegister, BigDecimal amount) {
		Assert.notNull(workResource);

		//We need to update the accountPricingTypeEntity based on the new company settings as of the payment date.
		Work work = workResource.getWork();

		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.BUYER_OFFLINE_WORK_PAYMENT);
		return registerTransactionsAbstract.execute(work, workResource, buyerAccountRegister, amount.negate(), false, false, false);
	}

	private WorkResourceTransaction createAndExecuteFeePaymentWorkResourceTransaction(WorkResource workResource, AccountRegister buyerAccountRegister, BigDecimal amount, LaneType laneType, boolean updateSummaries, boolean isBundled, boolean isBatchPayment) {
		Assert.notNull(workResource);
		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(laneType);
		return registerTransactionsAbstract.execute(workResource.getWork(), workResource, buyerAccountRegister, amount.negate(), updateSummaries, isBundled, isBatchPayment);
	}

	@Override
	public WorkResourceTransaction createAndExecuteResourceWorkPaymentWorkResourceTransaction(WorkResource workResource, AccountRegister resourceAccountRegister, BigDecimal amount) {
		Assert.notNull(workResource);
		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.RESOURCE_WORK_PAYMENT);
		return registerTransactionsAbstract.execute(workResource.getWork(), workResource, resourceAccountRegister, amount, true, false, false);
	}

	protected WorkResourceTransaction createAndExecuteResourceWorkOfflinePaymentWorkResourceTransaction(WorkResource workResource, AccountRegister resourceAccountRegister, BigDecimal amount) {
		Assert.notNull(workResource);
		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.RESOURCE_OFFLINE_WORK_PAYMENT);
		return registerTransactionsAbstract.execute(workResource.getWork(), workResource, resourceAccountRegister, amount, true, false, false);
	}

	@Override
	public WorkResourceTransaction createAndExecuteFastFundsFeeWorkResourceTransactionTransaction(WorkResource workResource, AccountRegister resourceAccountRegister, BigDecimal amount) {
		Assert.notNull(workResource);
		Assert.notNull(resourceAccountRegister);
		Assert.notNull(amount);

		Work work = workResource.getWork();
		Assert.notNull(work);

		RegisterTransactionExecutor registerTransactionExecutor = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.FAST_FUNDS_FEE);
		return registerTransactionExecutor.execute(work, workResource, resourceAccountRegister, amount.negate(), true, false, false);
	}

	@Override
	public WorkResourceTransaction createAndExecuteFastFundsPaymentWorkResourceTransactionTransaction(WorkResource workResource, AccountRegister resourceAccountRegister, BigDecimal amount) {
		Assert.notNull(workResource);
		Assert.notNull(resourceAccountRegister);
		Assert.notNull(amount);

		Work work = workResource.getWork();
		Assert.notNull(work);

		RegisterTransactionExecutor registerTransactionExecutor = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.FAST_FUNDS_PAYMENT);
		return registerTransactionExecutor.execute(work, workResource, resourceAccountRegister, amount, true, false, false);
	}

	@Override
	public WorkResourceTransaction createAndExecuteFastFundsDebitWorkResourceTransactionTransaction(WorkResource workResource, AccountRegister resourceAccountRegister, BigDecimal amount) {
		Assert.notNull(workResource);
		Assert.notNull(resourceAccountRegister);
		Assert.notNull(amount);

		Work work = workResource.getWork();
		Assert.notNull(work);

		RegisterTransactionExecutor registerTransactionExecutor = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.FAST_FUNDS_DEBIT);
		return registerTransactionExecutor.execute(work, workResource, resourceAccountRegister, amount.negate(), true, false, false);
	}

	public WorkResourceTransaction createAndExecuteBuyerWorkPaymentWorkResourceTransaction(WorkResource workResource, AccountRegister buyerAccountRegister, BigDecimal amount) {
		return createAndExecuteBuyerWorkPaymentWorkResourceTransaction(workResource, buyerAccountRegister, amount, true, false, false);
	}

	public WorkResourceTransaction createAndExecuteFeePaymentWorkResourceTransaction(WorkResource workResource, AccountRegister buyerAccountRegister, BigDecimal amount, LaneType laneType) {
		return createAndExecuteFeePaymentWorkResourceTransaction(workResource, buyerAccountRegister, amount, laneType, true, false, false);
	}

	protected FulfillmentStrategy createFulfillmentStrategyFromWorkCostDTO(WorkCostDTO workCostDTO) {
		FulfillmentStrategy fulfillmentStrategy = new FulfillmentStrategy();
		fulfillmentStrategy.setWorkPrice(workCostDTO.getTotalResourceCost());
		fulfillmentStrategy.setAmountEarned(workCostDTO.getTotalResourceCost());
		fulfillmentStrategy.setBuyerFee(workCostDTO.getBuyerFee());
		fulfillmentStrategy.setBuyerTotalCost(workCostDTO.getTotalBuyerCost());
		fulfillmentStrategy.setWorkPricePriorComplete(workCostDTO.getTotalResourceCost());
		return fulfillmentStrategy;
	}

	/**
	 * @param userBuyer
	 * @param accountRegisterMoniesDTO
	 * @throws com.workmarket.service.exception.account.InsufficientSpendLimitException
	 *
	 */
	void checkBuyerSpendLimit(WorkCostDTO accountRegisterMoniesDTO, User userBuyer) throws InsufficientSpendLimitException {

		BigDecimal buyerSpendLimit = userBuyer.getSpendLimit();
		if (buyerSpendLimit != null) {
			if (accountRegisterMoniesDTO.getTotalBuyerCost().compareTo(buyerSpendLimit) == 1) {
				throw new InsufficientSpendLimitException(String.format("total cost of $%.02f exceeds maximum spend limit of $%.02f", accountRegisterMoniesDTO.getTotalBuyerCost(), buyerSpendLimit));
			}
			if (accountRegisterMoniesDTO.getTotalBuyerCost().compareTo(BigDecimal.ZERO) < 0) {
				throw new InvalidSpendLimitException(String.format("total cost of $%.02f is an insufficient spend limit", accountRegisterMoniesDTO.getTotalBuyerCost()));
			}
		}
	}

	protected abstract void verifySufficientBuyerFunds(Work work, WorkCostDTO accountRegisterMoniesDTO,
													   Boolean accept) throws InsufficientFundsException;

	protected abstract void verifySufficientBuyerFunds(Company company, Project project, WorkCostDTO accountRegisterMoniesDTO) throws InsufficientFundsException;

	public void verifySufficientProjectRemainingBudget(Work work, WorkCostDTO accountRegisterMoniesDTO) throws InsufficientBudgetException {
		Assert.notNull(work);
		Assert.notNull(accountRegisterMoniesDTO);
		if (work.hasProject()) {
			verifySufficientProjectRemainingBudget(work.getProject(), accountRegisterMoniesDTO);
		}
	}

	public void verifySufficientProjectRemainingBudget(Project project, WorkCostDTO accountRegisterMoniesDTO) throws InsufficientBudgetException {
		Assert.notNull(accountRegisterMoniesDTO);
		if (project != null && project.getBudgetEnabledFlag()) {
			if (project.getRemainingBudget().compareTo(accountRegisterMoniesDTO.getTotalBuyerCost().abs()) == -1) {
				throw new InsufficientBudgetException("There is no enough budget in the project !");
			}
		}
	}

	@Override
	public void createACHVerificationTransactions(Long userId, BankAccount bankAccount) {
		Assert.notNull(userId);
		Assert.notNull(bankAccount);
		logger.debug("Create ACH Verification Transactions for bank account " + bankAccount.getAccountNumberSanitized());

		User user = userDAO.get(userId);
		AccountRegister register = findDefaultRegisterForCompany(user.getCompany().getId());

		// Require two verification transactions
		for (int i = 0; i < 2; i++) {
			/*
			 * Random generator = new Random(); int amount = generator.nextInt(15) + 1; BigDecimal bigDecimalAmount = new BigDecimal(amount); bigDecimalAmount.setScale(2,RoundingMode.HALF_UP);
			 * bigDecimalAmount = bigDecimalAmount.divide(new BigDecimal(100));
			 */
			BankAccountTransactionExecutor iBankAccountTransaction = registerTransactionExecutableFactory.newInstance(
					RegisterTransactionType.BANK_ACCOUNT_TRANSACTION);
			iBankAccountTransaction.executeAddAchVerify(bankAccount, register, generateRandomAchVerifyValue());

		}
	}

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#updateAccountRegisterWorkFeeData(java.lang.Long)
	 */
	@Override
	public void updateAccountRegisterWorkFeeData(Long accountRegisterId) {
		Date fromDate = DateUtilities.getMidnight1YearAgo().getTime();
		AccountRegister accountRegister = accountRegisterDAO.get(accountRegisterId);
		Assert.notNull(accountRegister);
		Assert.isTrue(!accountRegister.getCompany().getPaymentConfiguration().isSubscriptionPricing(), "Can't override work fee bands under Subscription");

		BigDecimal paymentSummation = registerTransactionDAO.paymentsByAccountRegisterIdAndDate(accountRegister.getId(), fromDate);
		WorkFeeBand workFeeBand = pricingService.determineWorkFeeBand(accountRegister);
		accountRegister.setPaymentSummation(paymentSummation.abs());
		accountRegister.setWorkFeeLevel(workFeeBand.getLevel());
		accountRegister.setCurrentWorkFeePercentage(workFeeBand.getPercentage());
		accountRegisterDAO.saveOrUpdate(accountRegister);
	}

	protected BigDecimal generateRandomAchVerifyValue() {
		Random generator = new Random();
		int amount = generator.nextInt(15) + 1;
		BigDecimal bigDecimalAmount = NumberUtilities.currency(amount);
		bigDecimalAmount = bigDecimalAmount.divide(new BigDecimal(100));
		return bigDecimalAmount;
	}

	@Override
	public boolean payForBackgroundCheckUsingBalance(Long userId, BackgroundCheck backgroundCheck, String countryCode) throws InsufficientFundsException {
		Assert.notNull(userId);
		Assert.notNull(backgroundCheck);
		Assert.hasText(countryCode);

		User user = userDAO.get(userId);
		BigDecimal amount = pricingService.findBackgroundCheckPrice(user.getCompany().getId(), countryCode);
		Assert.notNull(amount, "Can't find price for country code " + countryCode);

		RegisterTransactionType type = registerTransactionFactory.newBackgroundCheckRegisterTransactionType(countryCode);
		return createBackgroundCheckTransactions(user, backgroundCheck, amount, Boolean.FALSE, type);
	}

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#payForBackgroundCheckUsingCreditCard(java.lang.Long, com.workmarket.domains.model.screening.BackgroundCheck, com.workmarket.service.business.dto.PaymentDTO, java.lang.String)
	 */
	@Override
	public boolean payForBackgroundCheckUsingCreditCard(Long userId, BackgroundCheck backgroundCheck, PaymentDTO paymentDTO, String countryCode) throws Exception {
		Assert.notNull(userId);
		Assert.notNull(backgroundCheck);
		Assert.notNull(paymentDTO);
		Assert.hasText(countryCode);

		User user = userDAO.get(userId);
		BigDecimal amount = pricingService.findBackgroundCheckPrice(user.getCompany().getId(), countryCode);
		Assert.notNull(amount, "Can't find price for country code " + countryCode);

		RegisterTransactionType type = registerTransactionFactory.newBackgroundCheckRegisterTransactionType(countryCode);
		Assert.notNull(type);
		paymentDTO.setAmount(amount.toString());
		PaymentResponseDTO responseDTO = addFundsToRegisterFromCreditCard(user, paymentDTO, false);

		if (responseDTO.isApproved()) {
			return createBackgroundCheckTransactions(user, backgroundCheck, amount, Boolean.TRUE, type);
		}

		// something failed when processing the credit card, so return false
		return false;
	}

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#payForDrugTestUsingBalance(java.lang.Long, com.workmarket.domains.model.screening.DrugTest)
	 */
	@Override
	public Long payForDrugTestUsingBalance(Long userId, DrugTest drugTest) throws InsufficientFundsException {
		Assert.notNull(userId);
		Assert.notNull(drugTest);
		User user = userDAO.get(userId);
		AccountRegister register = findDefaultRegisterForCompany(user.getCompany().getId());

		RegisterTransactionCost transactionCost = pricingService.findCostForTransactionType(RegisterTransactionType.DRUG_TEST, register);
		BigDecimal amount = transactionCost.getFixedAmount();
		return createDrugTestTransactions(user, drugTest, amount, Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.workmarket.domains.payments.service.AccountRegisterService# payForBackgroundCheckUsingCreditCard(java.lang.Long, com.workmarket.domains.model.screening.BackgroundCheck,
	 * com.workmarket.service.business.dto.PaymentDTO)
	 */
	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#payForDrugTestUsingCreditCard(java.lang.Long, com.workmarket.domains.model.screening.DrugTest, com.workmarket.service.business.dto.PaymentDTO)
	 */
	@Override
	public boolean payForDrugTestUsingCreditCard(Long userId, DrugTest drugTest, PaymentDTO paymentDTO) throws Exception {
		Assert.notNull(userId);
		Assert.notNull(drugTest);
		Assert.notNull(paymentDTO);
		User user = userDAO.get(userId);
		AccountRegister accountRegister = findDefaultRegisterForCompany(user.getCompany().getId());

		RegisterTransactionCost transactionCost = pricingService.findCostForTransactionType(RegisterTransactionType.DRUG_TEST, accountRegister);

		BigDecimal amount = transactionCost.getFixedAmount();
		paymentDTO.setAmount(amount.toString());
		PaymentResponseDTO responseDTO = addFundsToRegisterFromCreditCard(user, paymentDTO, false);

		if (responseDTO.isApproved()) {
			createDrugTestTransactions(user, drugTest, amount, Boolean.TRUE);
			return true;
		}

		// something failed when processing the credit card, so return false
		return false;
	}

	/**
	 * @param user
	 * @param backgroundCheck
	 * @param amount
	 * @param type
	 * @throws InsufficientFundsException
	 */
	boolean createBackgroundCheckTransactions(User user, BackgroundCheck backgroundCheck, BigDecimal amount, Boolean paidOnCreditCard, RegisterTransactionType type)
			throws InsufficientFundsException {

		BigDecimal availableCash = calcAvailableCash(user.getId());
		if (availableCash.compareTo(amount) < 0) {
			throw new InsufficientFundsException();
		}
		AccountRegister register = findDefaultRegisterForCompany(user.getCompany().getId());
		BackgroundCheckTransaction backgroundCheckTransaction = new BackgroundCheckTransaction();
		backgroundCheckTransaction.setPaidOnCreditCard(paidOnCreditCard);
//		backgroundCheckTransaction.setBackgroundCheck(backgroundCheck);
		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(type.getCode());
		registerTransactionsAbstract.execute(register, amount.negate(), backgroundCheckTransaction);

		return true;
	}

	/**
	 * @param user
	 * @param drugTest
	 * @param amount
	 * @return
	 * @throws Exception
	 * @throws InsufficientFundsException
	 */
	Long createDrugTestTransactions(User user, DrugTest drugTest, BigDecimal amount, Boolean paidOnCreditCard) throws InsufficientFundsException {

		if (0 > this.calcAvailableCash(user.getId()).compareTo(amount)) {
			throw new InsufficientFundsException();
		}

		AccountRegister register = findDefaultRegisterForCompany(user.getCompany().getId());
		DrugTestTransaction drugTestTransaction = new DrugTestTransaction();
		drugTestTransaction.setPaidOnCreditCard(paidOnCreditCard);
//		drugTestTransaction.setDrugTest(drugTest);
		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.DRUG_TEST);
		drugTestTransaction = (DrugTestTransaction) registerTransactionsAbstract.execute(register, amount.negate(), drugTestTransaction);

		return drugTestTransaction.getId();
	}

	@Override
	public List<BankAccountTransaction> findACHVerificationTransactions(Long bankAccountId) {
		return registerTransactionDAO.findACHVerificationTransactions(bankAccountId);
	}

	@Override
	public PaymentResponseDTO addFundsToRegisterFromCreditCard(AutoPopulatingList<String> projectIds, AutoPopulatingList<Float> projectAmounts, Long userId, PaymentDTO paymentDTO, boolean hasFee) throws CreditCardErrorException {
		Assert.notNull(userId);
		Assert.notNull(paymentDTO);
		Assert.notNull(projectIds);
		Assert.notNull(projectAmounts);
		User user = userDAO.get(userId);
		PaymentResponseDTO dto = addFundsToRegisterFromCreditCard(user, paymentDTO, hasFee);
		if (dto.isApproved()) {
			// Allocate Funds to Project, the projectIds and projectAmounts are matched
			try {
				if (projectIds.size() > 1) {
					for (int i = 1; i < projectIds.size(); i++) {
						if (projectIds.get(i) == null || GENERAL_CASH.equals(projectIds.get(i))) {
							continue;
						}
						Project project = projectService.findById(new Long(projectIds.get(i)));
						if (project == null) {
							continue;
						}
						BigDecimal amount = NumberUtilities.currency(projectAmounts.get(i));
						transferFundsToProject(project.getId(), user.getCompany().getId(), amount);
					}
				}
			} catch (Exception e) {
				logger.debug("Error adding funds from Credit Card", e);
			}
		}
		return dto;
	}

	@Override
	public PaymentResponseDTO addFundsToRegisterFromCreditCard(User user, PaymentDTO paymentDTO, boolean hasFee) {
		Assert.notNull(user);
		Assert.notNull(paymentDTO);

		BigDecimal amount = NumberUtilities.currency(paymentDTO.getAmount());
		AccountRegister resourceRegister = findDefaultRegisterForCompany(user.getCompany().getId());

		Assert.notNull(paymentDTO.getCardType(), "Credit Card Type is required");
		CreditCardType creditCardType = CreditCardType.getCreditCardType(paymentDTO.getCardType());
		Assert.notNull(creditCardType, "Invalid credit card type");

		BigDecimal fee = BigDecimal.ZERO;

		if (hasFee) {
			RegisterTransactionCost txCost;

			switch (creditCardType) {
				case AMERICAN_EXPRESS:
					txCost = pricingService.findCostForTransactionType(RegisterTransactionType.AMEX_CREDIT_CARD_FEE,
							resourceRegister);
					break;
				default:
					txCost = pricingService.findCostForTransactionType(RegisterTransactionType.CREDIT_CARD_FEE,
							resourceRegister);
			}

			fee = amount.multiply(txCost.getPercentageAmount());
		}

		BigDecimal totalCost = amount.add(fee);
		paymentDTO.setAmount(totalCost.setScale(2, RoundingMode.HALF_UP).toString());
		logger.debug("Attempting to add funds to account with payment service..." + paymentDTO.getAmount());

		PaymentResponseDTO response = paymentService.doCardPayment(paymentDTO);

		if (response.isApproved()) {
			logger.debug("Funds added via Credit Card for user id " + user.getId());

			// add the funds
			CreditCardTransaction creditCardTransaction = createCreditCardTransaction(paymentDTO);

			RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.ADD_FUNDS);
			registerTransactionsAbstract.execute(resourceRegister, totalCost, creditCardTransaction);
			response.setCreditCardTransactionId(creditCardTransaction.getId());

			// deduct the fee
			if (hasFee) {
				CreditCardTransaction feeCreditCardTransaction = new CreditCardTransaction();
				RegisterTransactionExecutor feeExecutor;
				if (CreditCardType.AMERICAN_EXPRESS.equals(creditCardType)) {
					feeExecutor = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.AMEX_CREDIT_CARD_FEE);
				} else {
					feeExecutor = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.CREDIT_CARD_FEE);
				}

				RegisterTransaction feeRegisterTransaction = feeExecutor.execute(resourceRegister, fee.negate(), feeCreditCardTransaction);
				response.setCreditCardFeeTransactionId(feeRegisterTransaction.getId());

				// link the credit card transaction to the fee
				creditCardTransaction.setFeeTransaction(feeRegisterTransaction);
				registerTransactionDAO.saveOrUpdate(creditCardTransaction);
			}

			// dispatch the email
			userNotificationService.onCreditCardTransaction(creditCardTransaction, user);
		} else {
			logger.debug("Funds NOT added for user id " + user.getId() + " because " + response.getResponseMessage());
		}

		return response;
	}

	@Override
	public RegisterTransaction addFundsToRegisterAsCredit(FundsRequest fundsRequest) {
		Assert.notNull(fundsRequest);
		Assert.notNull(fundsRequest.getAmount());
		Assert.hasText(fundsRequest.getRegisterTransactionTypeCode());
		AccountRegister register = findDefaultRegisterForCompany(fundsRequest.getCompanyId());
		if (register != null) {
			CreditTransaction creditTransaction = new CreditTransaction();
			creditTransaction.setNote(fundsRequest.getNote());

			CreditRegisterTransaction creditRegisterTransaction = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.CREDIT);
			if (creditRegisterTransaction.validateRegisterTransactionType(fundsRequest.getRegisterTransactionTypeCode())) {
				creditRegisterTransaction.setRegisterTransactionType(new RegisterTransactionType(fundsRequest.getRegisterTransactionTypeCode()));
				creditTransaction = (CreditTransaction) creditRegisterTransaction.execute(register, fundsRequest.getAmount(), creditTransaction);
				if (RegisterTransactionType.CREDIT_REGISTER_TRANSACTION_NOTIFY_TYPE_CODES.contains(fundsRequest.getRegisterTransactionTypeCode())) {
					userNotificationService.onCreditTransaction(creditTransaction);
				}
				return creditTransaction;
			}
		}
		return null;
	}

	private CreditCardTransaction createCreditCardTransaction(PaymentDTO paymentDTO) {
		Assert.notNull(paymentDTO);
		Assert.notNull(paymentDTO.getCardNumber());
		CreditCardTransaction creditCardTransaction = new CreditCardTransaction();

		creditCardTransaction.setCardType(paymentDTO.getCardType());

		String cardNumber = paymentDTO.getCardNumber();
		creditCardTransaction.setLastFourDigits(cardNumber.substring(cardNumber.length() - 4));

		creditCardTransaction.setFirstName(paymentDTO.getFirstName());
		creditCardTransaction.setLastName(paymentDTO.getLastName());
		creditCardTransaction.setAddress1(paymentDTO.getAddress1());
		creditCardTransaction.setAddress2(paymentDTO.getAddress2());
		creditCardTransaction.setCity(paymentDTO.getCity());
		creditCardTransaction.setCountry(Country.valueOf(paymentDTO.getCountry()));
		creditCardTransaction.setPostalCode(paymentDTO.getPostalCode());
		creditCardTransaction.setState(paymentDTO.getState());

		return creditCardTransaction;
	}

	@Override
	public Long removeFundsFromRegisterAsCash(FundsRequest fundsRequest) throws InsufficientFundsException {
		Assert.notNull(fundsRequest);
		Assert.notNull(fundsRequest.getAmount());
		Assert.hasText(fundsRequest.getRegisterTransactionTypeCode());
		logger.debug("removeFundsFromRegisterAsCash()....");
		AccountRegister register = findDefaultRegisterForCompany(fundsRequest.getCompanyId());
		// Check for available funds
		BigDecimal sufficientBuyerFunds = calcSufficientBuyerFundsByCompany(fundsRequest.getCompanyId());

		if (!fundsRequest.getRegisterTransactionTypeCode().equals(RegisterTransactionType.DEBIT_ADJUSTMENT) &&
			fundsRequest.getAmount().compareTo(sufficientBuyerFunds) == 1) {
			logger.debug("There are insufficient funds to remove funds from register as cash for company "
					+ register.getCompany().getName() + " with value of " + fundsRequest.getAmount());
			throw new InsufficientFundsException("There are insufficient funds to remove from register as cash for company "
					+ register.getCompany().getName() + " with value of " + fundsRequest.getAmount());
		}
		DebitTransaction debitTransaction = new DebitTransaction();
		debitTransaction.setNote(fundsRequest.getNote());

		DebitRegisterTransaction debitRegisterTransaction = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.DEBIT);
		if (debitRegisterTransaction.validateRegisterTransactionType(fundsRequest.getRegisterTransactionTypeCode())) {
			debitRegisterTransaction.setRegisterTransactionType(new RegisterTransactionType(fundsRequest.getRegisterTransactionTypeCode()));
			debitTransaction = (DebitTransaction) debitRegisterTransaction.execute(register, fundsRequest.getAmount().negate(), debitTransaction);
			if (fundsRequest.isNotify()) {
				userNotificationService.onDebitTransaction(debitTransaction);
			}
			return debitTransaction.getId();
		}

		return null;
	}

	@Override
	public Long addFundsToRegisterFromAch(AutoPopulatingList<String> projectIds, AutoPopulatingList<Float> projectAmounts, Long userId, Long bankAccountId, String amount) throws Exception {
		Assert.notNull(userId);
		Assert.notNull(bankAccountId);
		Assert.notNull(amount);
		AbstractBankAccount bankAccount = bankAccountDAO.get(bankAccountId);
		Assert.notNull(bankAccount);
		Assert.isTrue(Country.USA_COUNTRY.getId().equals(bankAccount.getCountry().getId()), "Only USA accounts are authorized.");

		User user = userDAO.get(userId);
		Long parentTxId = addFundsToRegisterFromAch(userId, bankAccountId, amount);

		if (companyService.doesCompanyHaveReservedFundsEnabledProject(user.getCompany().getId())) {
			// Allocate Funds to Project, the projectIds and projectAmounts are matched
			if (projectIds.size() > 1) {
				for (int i = 1; i < projectIds.size(); i++) {
					if (projectIds.get(i) == null || GENERAL_CASH.equals(projectIds.get(i))) {
						BigDecimal generalAmount = NumberUtilities.currency(projectAmounts.get(i));
						addFundsToGeneralFromAch(user.getCompany().getId(), generalAmount, parentTxId);
					} else {
						Project project = projectService.findById(new Long(projectIds.get(i)));
						BigDecimal projectAmount = NumberUtilities.currency(projectAmounts.get(i));
						addFundsToProjectFromAch(project.getId(), user.getCompany().getId(), projectAmount, parentTxId);
					}
				}
			}
		} else {
			BigDecimal generalAmount = NumberUtilities.currency(amount);
			addFundsToGeneralFromAch(user.getCompany().getId(), generalAmount, parentTxId);
		}

		return parentTxId;
	}

	@Override
	public Long addFundsToRegisterFromAch(Long userId, Long bankAccountId, String amount) throws Exception {
		Assert.notNull(userId);
		Assert.notNull(bankAccountId);
		Assert.notNull(amount);
		User user = userDAO.get(userId);
		AccountRegister accountRegister = findDefaultRegisterForCompany(user.getCompany().getId());
		AbstractBankAccount bankAccount = bankAccountDAO.get(bankAccountId);
		Assert.isTrue(Country.USA_COUNTRY.getId().equals(bankAccount.getCountry().getId()), "Only USA accounts are authorized.");

		BankAccountTransactionExecutor iBankAccountTransaction = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.BANK_ACCOUNT_TRANSACTION);
		BankAccountTransaction bankAccountTransaction = iBankAccountTransaction.executeAddFundsToRegisterFromAch(user, bankAccount, amount, accountRegister);

		userNotificationService.onCreditTransaction(bankAccountTransaction);

		return bankAccountTransaction.getId();
	}

	@Override
	public RegisterTransaction addFundsToRegisterFromWire(Long companyId, String amount) {
		Company company = companyService.findById(companyId);
		Assert.notNull(company);
		return addFundsToRegisterFromWire(company, null, amount);
	}

	@Override
	public RegisterTransaction addFundsToRegisterFromWire(Company company, String note, String amount) throws AccountRegisterConcurrentException {
		try {
			return addFundsToRegisterAsCredit(new FundsRequest(company.getId(), note, NumberUtilities.currency(amount), RegisterTransactionType.CREDIT_WIRE_DIRECT_DEPOSIT));

		} catch (HibernateOptimisticLockingFailureException | CannotAcquireLockException | LockAcquisitionException | DataException | StaleObjectStateException e) {
			throw new AccountRegisterConcurrentException(e.getMessage());
		}
	}

	/**
	 * Withdraw funds from bank account
	 * Authorize, calculate any fees, execute.
	 */

	@Override
	public Long withdrawFundsFromRegister(Long userId, Long bankAccountId, String amount)
			throws InsufficientFundsException, WithdrawalExceedsDailyMaximumException, InvalidBankAccountException, InvalidTaxEntityException {

		Assert.notNull(userId);
		Assert.notNull(bankAccountId);
		Assert.notNull(amount);
		User user = userDAO.get(userId);
		Assert.notNull(user);
		Assert.isTrue(!user.getCompany().isLocked(), "funds.withdraw.company_locked");

		// in case our incoming string has more than 2 decimal places we need to normalize here - we do this once
		// so it gets applied universally through all future computations (there was a problem in the account register
		// where the available_cash seemed to round one way while the amount rounded another which was throwing off
		// the reporting). We are using HALF_UP as it matches what is displaying on the UI
		BigDecimal withdrawAmount = NumberUtilities.currency(amount);
		AccountRegister accountRegister = findDefaultRegisterForCompany(user.getCompany().getId());
		AbstractBankAccount bankAccount = bankAccountDAO.get(bankAccountId);

		checkAuthorizationForWithdrawal(userId, accountRegister, bankAccount, bankAccountId, withdrawAmount);

		BankAccountTransaction withdrawTransaction = executeWithdraw(accountRegister, bankAccount, withdrawAmount);
		userNotificationService.onFundsWithdrawn(withdrawTransaction);

		return withdrawTransaction.getId();
	}

	private void checkAuthorizationForWithdrawal(Long userId, AccountRegister accountRegister, AbstractBankAccount bankAccount, Long bankAccountId, BigDecimal withdrawAmount)
			throws WithdrawalExceedsDailyMaximumException, InvalidBankAccountException, InvalidTaxEntityException {
		Assert.isTrue(accountRegister.getCompany().isActive(), "Can't withdraw funds if company's account is not active");

		if (bankAccount == null) {
			throw new InvalidBankAccountException(String.format("Bank account [%d] does not exist", bankAccountId));
		}
		if (!bankAccount.getConfirmedFlag()) {
			throw new InvalidBankAccountException(String.format("Bank account [%d] not confirmed", bankAccountId));
		}

		// ensure the bank_account_id is associated with the company
		User user = userService.getUser(userId);
		Assert.isTrue(user.getCompany().getId().equals(bankAccount.getCompany().getId()), "Bank Account is not associated with this company");

		AccountRegisterSummaryFields accountRegisterSummaryFields = accountRegister.getAccountRegisterSummaryFields();
		logger.debug("Attempting to withdraw funds, withdrawable cash is: " + accountRegisterSummaryFields.getWithdrawableCash());

		if (0 > accountRegisterSummaryFields.getWithdrawableCash().compareTo(withdrawAmount)) {
			throw new InsufficientFundsException();
		}

		BigDecimal totalWithdrawalsForToday = registerTransactionDAO.findTotalWithdrawalsForTodayBalance(accountRegister.getId());
		BigDecimal totalWithdrawalsToCompare = totalWithdrawalsForToday.negate().add(withdrawAmount);

		logger.debug("Total withdrawals for today: " + totalWithdrawalsForToday);
		logger.debug("Total withdrawals for today plus current amount: " + totalWithdrawalsToCompare);

		if (0 < totalWithdrawalsToCompare.compareTo(Constants.DAILY_WITHDRAWAL_LIMIT)) {
			throw new WithdrawalExceedsDailyMaximumException();
		}

		AbstractTaxEntity activeTaxEntity = taxService.findActiveTaxEntity(userId);
		if (activeTaxEntity == null) {
			throw new InvalidTaxEntityException(String.format("Active tax entity not found for user [%d]", userId));
		}
		if (!isValidTaxEntityForWithdrawal(activeTaxEntity)) {
			throw new InvalidTaxEntityException(String.format("Active tax entity [%d] not verified for user [%d]", activeTaxEntity.getId(), userId));
		}
	}

	/**
	 * Only US tax entities with Approved status and non-null CAN/OTHER entities can withdraw
	 *
	 * @param taxEntity
	 * @return
	 */
	private boolean isValidTaxEntityForWithdrawal(AbstractTaxEntity taxEntity) {
		return taxEntity != null && !(taxEntity instanceof UsaTaxEntity && !taxEntity.getStatus().isApproved());
	}

	private BankAccountTransaction executeWithdraw(AccountRegister accountRegister, AbstractBankAccount bankAccount, BigDecimal withdrawAmount) {
		RegisterTransactionType bankAccountTransactionType = registerTransactionFactory.newBankAccountRegisterTransactionType(bankAccount);
		Assert.notNull(bankAccountTransactionType);

		TransactionBreakdown breakdown = feeCalculator.calculateWithdraw(accountRegister, bankAccount, withdrawAmount);

		BankAccountTransactionExecutor withdrawExecutor = registerTransactionExecutableFactory.newInstance(bankAccountTransactionType.getCode());
		BankAccountTransaction withdrawTransaction = withdrawExecutor.executeRemove(bankAccount, accountRegister, breakdown.getNet(), breakdown.getTransactionType());

		// Conditionally process a fee for the withdrawal
		if (breakdown.hasFee()) {
			BankAccountTransactionExecutor feeExecutor = registerTransactionExecutableFactory.newInstance(breakdown.getFeeTransactionType().getCode());
			BankAccountTransaction feeTransaction = feeExecutor.executeRemove(bankAccount, accountRegister, breakdown.getFee(), breakdown.getFeeTransactionType());
			feeTransaction.setParentRegisterTransaction(withdrawTransaction);
		}

		if (breakdown.hasSecretFee()) {
			SecretTransaction secretTransaction = new SecretTransaction();
			secretTransaction.setParentRegisterTransaction(withdrawTransaction);

			AccountRegister secretRegister = findDefaultRegisterForCompany(Constants.WM_COMPANY_ID);
			RegisterTransactionExecutor secretFeeExecutor = registerTransactionExecutableFactory.newInstance(breakdown.getSecretFeeTransactionType().getCode());
			secretFeeExecutor.execute(secretRegister, breakdown.getSecretFee(), secretTransaction);
			secretTransaction.setPendingFlag(Boolean.TRUE);
		}

		return withdrawTransaction;
	}

	@Override
	public WorkAuthorizationResponse authorizeWork(Work work) throws InsufficientFundsException, InsufficientBudgetException {
		return authorizeWork(work, true);
	}

	@Override
	public WorkAuthorizationResponse authorizeMultipleWork(BigDecimal totalOfMultipleWorkCost, User userBuyer, Project project, Set<BigDecimal> uniqueWorkCosts) {
		Assert.notNull(totalOfMultipleWorkCost);
		Assert.notNull(userBuyer);
		Assert.notNull(uniqueWorkCosts);
		try {

			WorkCostDTO totalWorkCostDTO = new WorkCostDTO();
			totalWorkCostDTO.setTotalBuyerCost(totalOfMultipleWorkCost);

			// Check the buyer's spend limit per assignment
			for (BigDecimal workCost : uniqueWorkCosts) {
				checkBuyerSpendLimit(new WorkCostDTO(null, null, workCost), userBuyer);
			}

			// calculate charge and see if the user can pay for it
			verifySufficientBuyerFunds(userBuyer.getCompany(), project, totalWorkCostDTO);
			verifySufficientProjectRemainingBudget(project, totalWorkCostDTO);
		} catch (InsufficientFundsException ife) {
			logger.debug("[authorizeWork] ", ife);
			return WorkAuthorizationResponse.INSUFFICIENT_FUNDS;
		} catch (InsufficientSpendLimitException isle) {
			logger.debug("[authorizeWork] ", isle);
			return WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT;
		} catch (InvalidSpendLimitException e) {
			logger.debug("[authorizeWork] ", e);
			return WorkAuthorizationResponse.INVALID_SPEND_LIMIT;
		} catch (InsufficientBudgetException ibe) {
			logger.debug("[authorizeWork] ", ibe);
			return WorkAuthorizationResponse.INSUFFICIENT_BUDGET;
		}
		return WorkAuthorizationResponse.SUCCEEDED;
	}

	@Override
	public WorkAuthorizationResponse verifyFundsForAuthorization(BigDecimal workTotalCost, User userBuyer, Project project) throws InsufficientFundsException, InsufficientBudgetException {
		Assert.notNull(workTotalCost);
		Assert.notNull(userBuyer);
		try {
			WorkCostDTO workCostDTO = new WorkCostDTO().setTotalBuyerCost(workTotalCost);

			// Check the buyer's spend limit per assignment
			checkBuyerSpendLimit(workCostDTO, userBuyer);

			// calculate charge and see if the user can pay for it
			verifySufficientBuyerFunds(userBuyer.getCompany(), project, workCostDTO);
			verifySufficientProjectRemainingBudget(project, workCostDTO);

		} catch (InsufficientFundsException ife) {
			logger.debug("[authorizeWork] ", ife);
			return WorkAuthorizationResponse.INSUFFICIENT_FUNDS;
		} catch (InsufficientSpendLimitException isle) {
			logger.debug("[authorizeWork] ", isle);
			return WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT;
		} catch (InvalidSpendLimitException e) {
			logger.debug("[authorizeWork] ", e);
			return WorkAuthorizationResponse.INVALID_SPEND_LIMIT;
		} catch (InsufficientBudgetException ibe) {
			logger.debug("[authorizeWork] ", ibe);
			return WorkAuthorizationResponse.INSUFFICIENT_BUDGET;
		}
		return WorkAuthorizationResponse.SUCCEEDED;
	}

	@Override
	public WorkAuthorizationResponse registerWorkInBundleAuthorization(Work work) {
		Assert.notNull(work);
		if (isWorkAuthorized(work)) {
			return WorkAuthorizationResponse.SUCCEEDED;
		}
		WorkBundle parent = work.getParent();
		Assert.notNull(parent);
		WorkBundleTransaction workBundleTransaction = registerTransactionDAO.findWorkBundlePendingAuthorizationTransaction(parent.getId());
		if (workBundleTransaction == null) {
			return WorkAuthorizationResponse.INSUFFICIENT_WORK_BUNDLE_AUTHORIZED_BUDGET;
		}
		WorkCostDTO workCostDTO = calculateCostOnSentWork(work);
		BigDecimal amount = workCostDTO.getTotalBuyerCost();

		long remainingAuthorizationAmount =  workBundleTransaction.getRemainingAuthorizedAmount().abs().compareTo(amount);
		if (remainingAuthorizationAmount < 0) {
			logger.debug("Insufficient Budget. Bundle: " + parent.getId() + " Remaining: " +  remainingAuthorizationAmount + " Work: " + work.getId() + " Cost: " + amount);
			return WorkAuthorizationResponse.INSUFFICIENT_WORK_BUNDLE_AUTHORIZED_BUDGET;
		}
		WorkResourceTransaction workResourceTransaction = createAuthorizationTransaction(work, workCostDTO, null, false);
		if (workResourceTransaction != null) {
			BigDecimal newBalance = workBundleTransaction.getRemainingAuthorizedAmount().subtract(workCostDTO.getTotalBuyerCost());
			workBundleTransaction.setRemainingAuthorizedAmount(newBalance);
			if (NumberUtilities.isZero(newBalance)) {
				workBundleTransaction.setPendingFlag(false);
			}
			return WorkAuthorizationResponse.SUCCEEDED;
		}
		return WorkAuthorizationResponse.UNKNOWN;
	}

	public WorkAuthorizationResponse authorizeWork(Work work, boolean skipOnPreviousAuthorization) throws InsufficientFundsException, InsufficientBudgetException {
		Assert.notNull(work);
		try {
			boolean hasOfflinePayment = workOptionsService.hasOption(work, WorkOption.MBO_ENABLED, "true") ||
				workService.isOfflinePayment(work);
			if (hasOfflinePayment || (skipOnPreviousAuthorization && isWorkAuthorized(work))) {
				return WorkAuthorizationResponse.SUCCEEDED;
			}
			voidWork(work);
			commitToWork(work);
		} catch (InsufficientFundsException ife) {
			logger.debug("[authorizeWork] ", ife);
			return WorkAuthorizationResponse.INSUFFICIENT_FUNDS;
		} catch (InsufficientSpendLimitException isle) {
			logger.debug("[authorizeWork] ", isle);
			return WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT;
		} catch (InvalidSpendLimitException e) {
			logger.debug("[authorizeWork] ", e);
			return WorkAuthorizationResponse.INVALID_SPEND_LIMIT;
		} catch (InsufficientBudgetException ibe) {
			logger.debug("[authorizeWork] ", ibe);
			return WorkAuthorizationResponse.INSUFFICIENT_BUDGET;
		}
		return WorkAuthorizationResponse.SUCCEEDED;
	}

	public abstract boolean isWorkAuthorized(Work work);

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#repriceWork(com.workmarket.domains.work.model.Work)
	 */
	@Override
	public void repriceWork(Work work) throws InsufficientFundsException, InsufficientBudgetException, InsufficientSpendLimitException {
		// check to see if status is active, which means that a resource has
		// accepted.
		logger.debug("workStatusType.code:" + work.getWorkStatusType().getCode());
		if (work.isActive()) {
			WorkResource workResource = workService.findActiveWorkResource(work.getId());
			if (workResource != null) {
				acceptWork(workResource);
				return;
			}
		} else {
			WorkAuthorizationResponse response = authorizeWork(work, false);
			if (WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT.equals(response)) {
				throw new InsufficientSpendLimitException();
			} else if (WorkAuthorizationResponse.INSUFFICIENT_FUNDS.equals(response)) {
				throw new InsufficientFundsException();
			}
			return;
		}

		throw new AccountRegisterNoActiveWorkResourceException("Can't locate an active resource for workId" + work.getId());
	}

	@Override
	public Work voidWork(Work work) throws AccountRegisterConcurrentException {
		logger.debug("voidWork(Work).....");
		Assert.notNull(work);

		revertAccountRegisterPendingTransactions(work);
		FulfillmentStrategy fulfillmentStrategy = new FulfillmentStrategy();
		work.setFulfillmentStrategy(fulfillmentStrategy);
		return work;
	}

	@Override
	public void revertAccountRegisterPendingTransactions(Long workId) throws AccountRegisterConcurrentException {
		revertAccountRegisterPendingTransactions((Work) workService.findWork(workId, false));
	}

	@Override
	public void revertAccountRegisterPendingTransactions(Work work) throws AccountRegisterConcurrentException {
		logger.debug("revertAccountRegisterPendingTransactions(Work).....");
		Assert.notNull(work);

		// Added this hack because we'll eventually be writing to Account Register
		// If we don't grab the exclusive lock here we may run into a StaleObjectException on write
		pricingService.lockAccountRegisterForWritingHack(work.getCompany().getId());

		List<WorkResourceTransaction> workResourceTransactionL = registerTransactionDAO.findWorkResourceTransactionWorkIdPending(work.getId());
		/*
		 * Equivalent to:
		 *
		 * SELECT * FROM work_resource_transaction rt INNER JOIN register_transaction r ON r.id = rt.id INNER JOIN work_resource ON work_resource.id = rt.work_resource_id AND work_resource.work_id =
		 * :workId AND pending_flag = 'Y' AND r.register_transaction_type_code != 'addfunds';
		 */

		// For each transaction found, usually there are only 2 (1 for the buyer, 1 for the resource's account register)
		for (WorkResourceTransaction workResourceTransaction : workResourceTransactionL) {
			if (workResourceTransaction != null && workResourceTransaction.getAmount().doubleValue() != 0) {
				logger.debug("workResourceTransactionId:" + workResourceTransaction.getId() + " code:"
						+ workResourceTransaction.getRegisterTransactionType().getCode() + " amount:"
						+ workResourceTransaction.getAmount());

				RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(
						RegisterTransactionExecutor.createBeanName(Boolean.TRUE, workResourceTransaction.getRegisterTransactionType().getCode()));
				registerTransactionsAbstract.reverseSummaries(workResourceTransaction);

				// Revert the children project/general transactions
				List<RegisterTransaction> registerTransactionList = findChildFromWorkResourceTransaction(workResourceTransaction);

				// For buyer commitment transaction, if it has children, revert the children and put money back to general or project
				// If it has no children, add money back to general cash
				if (RegisterTransactionType.BUYER_COMMITMENT_TO_PAY.equals(workResourceTransaction.getRegisterTransactionType().getCode()) && registerTransactionList.size() == 0) {
					addFundsToGeneral(work.getCompany().getId(), workResourceTransaction.getAmount().abs());
				}

				// Put remaining budget back for buyer
				if (RegisterTransactionType.BUYER_COMMITMENT_TO_PAY.equals(workResourceTransaction.getRegisterTransactionType().getCode())
						|| RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT.equals(workResourceTransaction.getRegisterTransactionType().getCode())) {
					if (work.getProject() != null && work.getProject().getBudgetEnabledFlag()) {
						projectBudgetService.increaseRemainingBudget(work.getProject(), workResourceTransaction.getAmount().abs());
					}
				}

				for (RegisterTransaction registerTransaction : registerTransactionList) {
					RegisterTransactionExecutor childRegisterTransactionsAbstract = registerTransactionExecutableFactory.newInstance(registerTransaction.getRegisterTransactionType().getCode());
					registerTransaction.setPendingFlag(Boolean.FALSE);
					childRegisterTransactionsAbstract.reverseSummaries(registerTransaction);

				}
			}
		}
		List<RegisterTransaction> transactions = registerTransactionDAO.findAllWorkResourceTransactionsPending(work.getId());
		for (RegisterTransaction r : transactions) {
			r.setPendingFlag(false);
			r.setEffectiveDate(Calendar.getInstance());
		}
	}

	private List<RegisterTransaction> findChildFromWorkResourceTransaction(WorkResourceTransaction workResourceTransaction) {
		List<RegisterTransaction> registerTransactionList = Lists.newArrayList();
		List<RegisterTransaction> projectTransactionList = registerTransactionDAO.findProjectChildTransactions(workResourceTransaction.getId());
		List<RegisterTransaction> generalTransactionList = registerTransactionDAO.findGeneralChildTransactions(workResourceTransaction.getId());
		registerTransactionList.addAll(projectTransactionList);
		registerTransactionList.addAll(generalTransactionList);
		return registerTransactionList;
	}

	/**
	 * Recursively find the originally assigned work resource who subsequently delegated the assignment.
	 *
	 * @param workResource
	 * @return Delegating resource
	 */
	@Deprecated
	WorkResource findOriginalWorkResource(WorkResource workResource) {
		if (workResource.getDelegator() == null) {
			return workResource;
		}
		return findOriginalWorkResource(workResource.getDelegator());
	}

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#calcAvailableCash(java.lang.Long)
	 */
	@Override
	public BigDecimal calcAvailableCash(Long userId) {
		Assert.notNull(userId);
		User user = userDAO.get(userId);
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(user.getCompany().getId());
		logger.info("accountRegister.AccountRegisterSummaryFields.availableCash():"
				+ accountRegister.getAvailableCash());
		return accountRegister.getAvailableCash();
	}

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#calcAvailableCashByCompany(java.lang.Long)
	 */
	@Override
	public BigDecimal calcAvailableCashByCompany(Long companyId) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(companyId);
		if (accountRegister != null) {
			return accountRegister.getAvailableCash();
		}
		return BigDecimal.ZERO;
	}

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#calcSufficientBuyerFundsByCompany(java.lang.Long)
	 */
	@Override
	public abstract BigDecimal calcSufficientBuyerFundsByCompany(Long companyId);

	@Override
	public BigDecimal calculateGeneralCashByCompany(Long companyId) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(companyId);
		if (accountRegister.getAccountRegisterSummaryFields() != null) {
			return accountRegister.getAccountRegisterSummaryFields().getGeneralCash();
		}
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal calculateProjectCashByCompany(Long companyId) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(companyId);
		if (accountRegister.getAccountRegisterSummaryFields() != null) {
			return accountRegister.getAccountRegisterSummaryFields().getProjectCash();
		}
		return BigDecimal.ZERO;
	}

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#calcPendingCashByCompany(java.lang.Long)
	 */
	@Override
	public BigDecimal calcPendingCashByCompany(Long companyId) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(companyId);
		return accountRegister.getAccountRegisterSummaryFields().getPendingCommitments();
	}

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#calcEarnedInProgressByCompany(java.lang.Long)
	 */
	@Override
	public BigDecimal calcEarnedInProgressByCompany(Long companyId) {
		AccountRegister register = pricingService.findDefaultRegisterForCompany(companyId);
		return register.getAccountRegisterSummaryFields().getPendingEarnedCash();
	}

	@Override
	public BigDecimal calcEarnedPendingByCompany(Long companyId) {
		return calcEarnedInProgressByCompany(companyId);
	}

	@Override
	public BigDecimal calculateWithdrawableCashByCompany(Long companyId) {
		AccountRegisterSummaryFields fields = getAccountRegisterSummaryFields(companyId);
		return fields == null ? BigDecimal.ZERO : fields.getWithdrawableCash();
	}

	@Override
	public AccountRegisterSummaryFields getAccountRegisterSummaryFields(Long companyId) {
		Assert.notNull(companyId);

		Optional<AccountRegisterSummaryFields> fieldsOpt = accountRegisterSummaryFieldsDAO.findAccountRegisterSummaryByCompanyId(companyId);
		return fieldsOpt.isPresent() ? fieldsOpt.get() : null;
	}

	@Override
	public AccountRegister getAccountRegisterById(Long accountRegisterId) {
		Assert.notNull(accountRegisterId);
		return accountRegisterDAO.findById(accountRegisterId);
	}

	@Override
	public List<BankAccountTransaction> findAccountFundingTransaction() {
		return registerTransactionDAO.findBankAccountTransactions(RegisterTransactionType.ADD_FUNDS,
				BankAccountTransactionStatus.SUBMITTED);
	}

	@Override
	public Collection<RegisterTransaction> findAllRegisterTransactions(long companyId) {
		return registerTransactionDAO.findAllRegisterTransactions(accountRegisterDAO.findByCompanyId(companyId).getId());
	}

	@Override
	public List<BankAccountTransaction> findACHAccountWithdrawalTransactions() {
		return registerTransactionDAO.findBankAccountTransactions(RegisterTransactionType.REMOVE_FUNDS, BankAccountTransactionStatus.SUBMITTED);
	}

	@Override
	public List<BankAccountTransaction> findGCCAccountWithdrawalTransactions() {
		return registerTransactionDAO.findBankAccountTransactions(RegisterTransactionType.REMOVE_FUNDS_GCC, BankAccountTransactionStatus.SUBMITTED);
	}

	@Override
	public List<BankAccountTransaction> findPayPalAccountWithdrawalTransactions() {
		return registerTransactionDAO.findBankAccountTransactions(RegisterTransactionType.REMOVE_FUNDS_PAYPAL, BankAccountTransactionStatus.SUBMITTED);
	}

	@Override
	public List<BankAccountTransaction> findBankACHVerificationTransactions() {
		return registerTransactionDAO.findBankAccountTransactions(RegisterTransactionType.ACH_VERIFY,
				BankAccountTransactionStatus.SUBMITTED);
	}

	@Override
	public AccountRegister findDefaultRegisterForCompany(Company company) {
		return findDefaultRegisterForCompany(company.getId());
	}

	@Override
	public AccountRegister findDefaultRegisterForCompany(Long companyId) {
		return pricingService.findDefaultRegisterForCompany(companyId, true);
	}

	public AccountRegister findDefaultResourceRegisterForWork(Work work) {
		WorkResource workResource = workService.findActiveWorkResource(work.getId());
		MboProfile mboProfile = mboProfileDAO.findMboProfile(workResource.getUser().getId());
		boolean hasOfflinePayment = workOptionsService.hasOption(work, WorkOption.MBO_ENABLED, "true");

		/* If assignment not configured for "offline" payment, & resource elects to receive payment through MBO, then use MBO register */
		if (!hasOfflinePayment && mboProfile != null && mboProfile.isPayMbo()) {
			AccountRegister mboRegister = accountRegisterDAO.findByCompanyNumber(Constants.MBO_COMPANY_NUMBER);
			Assert.notNull(mboRegister);
			return mboRegister;
		}

		return pricingService.findDefaultRegisterForCompany(workResource.getUser().getCompany().getId(), true);
	}

	/**
	 * @param companyId
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public RegisterTransactionActivityPagination getLedgerForCompany(Long companyId, RegisterTransactionActivityPagination pagination) {
		AccountRegister register = pricingService.findDefaultRegisterForCompany(companyId);
		return registerTransactionActivityDAO.getLedgerForCompany(companyId, register.getId(), pagination);
	}

	/**
	 * @param companyId
	 * @param pagination
	 * @return
	 */
	@Override
	public RegisterTransactionActivityPagination getOfflineLedgerForCompany(Long companyId, RegisterTransactionActivityPagination pagination) {
		AccountRegister register = pricingService.findDefaultRegisterForCompany(companyId);
		return registerTransactionActivityDAO.getOfflineLedgerForCompany(companyId, register.getId(), pagination);
	}

	public RegisterTransactionActivityPagination getPendingTransactions(Long companyId, RegisterTransactionActivityPagination pagination) {
		AccountRegister register = pricingService.findDefaultRegisterForCompany(companyId);
		pagination = registerTransactionActivityDAO.getPendingTransactions(companyId, register.getId(), pagination);
		RegisterTransactionActivity pendingAssignmentAuthorizationsSummary = null;
		List<RegisterTransactionActivity> activityList = Lists.newArrayList(pagination.getResults());
		//We want to display only one row for commitments
		for (RegisterTransactionActivity activity : activityList) {
			if (activity.isPreFundAssignmentAuthorization()) {
				if (pendingAssignmentAuthorizationsSummary == null) {
					pendingAssignmentAuthorizationsSummary = BeanUtilities.newBean(RegisterTransactionActivity.class, activity);
				} else {
					BigDecimal pendingAuthorizationsTotal = pendingAssignmentAuthorizationsSummary.getAmount().add(activity.getAmount());
					pendingAssignmentAuthorizationsSummary.setAmount(pendingAuthorizationsTotal);
				}
				pagination.getResults().remove(activity);
			}
		}
		if (pendingAssignmentAuthorizationsSummary != null) {
			pagination.getResults().add(pendingAssignmentAuthorizationsSummary);
		}
		pagination.setRowCount(pagination.getResults().size());
		return pagination;
	}

	public RegisterTransactionActivityPagination getAccountRegisterTransactionReport(Long companyId, RegisterTransactionActivityPagination pagination) {
		AccountRegister register = pricingService.findDefaultRegisterForCompany(companyId);
		return registerTransactionActivityDAO.getAccountRegisterTransactionReport(companyId, register.getId(), pagination);
	}

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#getAccountRegisterWeeklyReport(com.workmarket.domains.model.account.WeeklyReportRowPagination)
	 */
	@Override
	public List<WeeklyReportRow> getAccountRegisterWeeklyReport(WeeklyReportRowPagination pagination) {
		return weeklyRevenueReportDAO.getCompanyWeeklyRevenueReport(pagination);
	}

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#getCurrentWeekTrend(java.lang.Long)
	 */
	@Override
	public WeekReportDetail getCurrentWeekTrend(Long companyId) {
		Assert.notNull(companyId, "Company id can't be null");
		return weeklyRevenueReportDAO.getCompanyCurrentWeekRevenueTrend(companyId);
	}

	@Override
	public List<RegisterTransaction> findFundingTransactionsByDate(DateFilter datefilter) {
		if(datefilter == null || datefilter.getFromDate() == null || datefilter.getToDate() == null) {
			return Collections.EMPTY_LIST;
		}
		return registerTransactionDAO.findFundingTransactionsByDate(datefilter);
	}

	@Override
	public <T extends AbstractInvoice> boolean payInvoice(T invoice) throws InsufficientFundsException {
		Assert.notNull(invoice);
		if (invoice.isPaid()) {
			logger.info("Invoice id " + invoice.getId() + " has been marked as paid already");
			return false;
		}

		BigDecimal amountToPay = invoice.getRemainingBalance();
		if (amountToPay.compareTo(BigDecimal.ZERO) < 0) {
			logger.error("remaining balance is 0.00 but invoice is not mark as paid, invoice id: " + invoice.getId());
			return false;
		}

		logger.debug("[amountToPay] = " + amountToPay);
		AccountRegister register = findDefaultRegisterForCompany(invoice.getCompany().getId());
		BigDecimal availableCash = register.getAvailableCash();

		if (0 > availableCash.compareTo(amountToPay)) {
			throw new InsufficientFundsException("availableCash : " + availableCash + " invoice balance: " + amountToPay);
		}

		InvoicePaymentTransaction invoicePaymentTransaction = new InvoicePaymentTransaction();
		invoicePaymentTransaction.setInvoice(invoice);

		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInvoicePaymentRegisterTransaction(invoice);
		registerTransactionsAbstract.execute(register, amountToPay.negate(), invoicePaymentTransaction);

		if (Statement.STATEMENT_TYPE.equals(invoice.getType()) || InvoiceSummary.INVOICE_SUMMARY_TYPE.equals(invoice.getType()) || InvoiceCollection.INVOICE_COLLECTION_TYPE.equals(invoice.getType())) {
			List<Invoice> invoices = Lists.newArrayList();
			if (InvoiceSummary.INVOICE_SUMMARY_TYPE.equals(invoice.getType())) {
				invoices = Lists.newArrayList(((InvoiceSummary) invoice).getInvoices());
			} else if (InvoiceCollection.INVOICE_COLLECTION_TYPE.equals(invoice.getType())) {
				invoices = Lists.newArrayList(((InvoiceCollection) invoice).getInvoices());
			} else if (Statement.STATEMENT_TYPE.equals(invoice.getType())) {
				invoices = Lists.newArrayList(((Statement) invoice).getInvoices());
			}

			BigDecimal generalTotalDue = invoicePaymentHelper.calculateTotalToPayFromGeneralCash(invoices, invoice.getCompany().getId());

			if (companyService.doesCompanyHaveReservedFundsEnabledProject(invoice.getCompany().getId())) {
				List<ProjectInvoiceBundle> projectInvoiceBundleList = invoicePaymentHelper.groupInvoicesByProject(invoices, invoice.getCompany().getId());
				for (ProjectInvoiceBundle p : projectInvoiceBundleList) {
					if (p.getProject().getReservedFunds().compareTo(p.getSumOfInvoices()) == -1) {
						throw new InsufficientFundsException("There is no enough reserved funds in the project" + p.getProject().getName());
					}
					removeFundsFromProject(p.getProject().getId(), invoice.getCompany().getId(), p.getSumOfInvoices());
				}
			}
			if (calculateGeneralCashByCompany(invoice.getCompany().getId()).compareTo(generalTotalDue) == -1) {
				throw new InsufficientFundsException("There is no enough general cash in the account");
			}
			removeFundsFromGeneral(invoice.getCompany().getId(), generalTotalDue);
		}

		if (invoice instanceof AbstractServiceInvoice) {
			if (calculateGeneralCashByCompany(invoice.getCompany().getId()).compareTo(invoice.getRemainingBalance()) == -1) {
				throw new InsufficientFundsException("There is no enough general cash in the account");
			}
			removeFundsFromGeneral(invoice.getCompany().getId(), amountToPay);
			/**
			 * Subscription or ad-hoc invoices payments are registered as a one single transaction.
			 * However for internal revenue reporting we need the detail of the transactions.
			 */
			return updateServiceInvoiceDetailedTransactions(invoicePaymentTransaction);
		}
		return true;
	}

	private boolean updateServiceInvoiceDetailedTransactions(InvoicePaymentTransaction invoicePaymentTransaction) {
		AbstractServiceInvoice invoice = (AbstractServiceInvoice) invoicePaymentTransaction.getInvoice();
		Set<InvoiceLineItem> invoiceLineItems = invoice.getInvoiceLineItems();
		for (InvoiceLineItem invoiceLineItem : invoiceLineItems) {
			/**
			 * None of these transactions, update summaries.
			 */
			if (invoiceLineItem.isSetRegisterTransaction()) {
				ServiceTransaction serviceTransaction = (ServiceTransaction) invoiceLineItem.getRegisterTransaction();
				serviceTransaction.copyStatus(invoicePaymentTransaction);
			}
		}
		return true;
	}

	@Override
	public <T extends InvoiceLineItem> ServiceTransaction createInvoiceItemRegisterTransaction(Company company, T invoiceLineItem, boolean pending) {
		AccountRegister accountRegister = findDefaultRegisterForCompany(company);
		BigDecimal amountToPay = invoiceLineItem.getAmount();
		String txTypeCode = StringUtils.EMPTY;
		boolean isAdHocVORSoftwareFee = false;

		if (invoiceLineItem instanceof SubscriptionSoftwareFeeLineItem) {
			txTypeCode = RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT;

		} else if (invoiceLineItem instanceof SubscriptionVorSoftwareFeeLineItem) {
			txTypeCode = RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT;
			isAdHocVORSoftwareFee = true;

		} else if (invoiceLineItem instanceof SubscriptionVORLineItem) {
			txTypeCode = RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT;

		} else if (invoiceLineItem instanceof SubscriptionAddOnLineItem) {
			txTypeCode = RegisterTransactionType.SUBSCRIPTION_ADD_ON_PAYMENT;

		} else if (invoiceLineItem instanceof SubscriptionSetupFeeLineItem) {
			txTypeCode = RegisterTransactionType.SUBSCRIPTION_SETUP_FEE_PAYMENT;

		} else if (invoiceLineItem instanceof SubscriptionDiscountLineItem) {
			txTypeCode = RegisterTransactionType.SUBSCRIPTION_DISCOUNT;

		} else if (invoiceLineItem instanceof DepositReturnFeeInvoiceLineItem) {
			txTypeCode = RegisterTransactionType.SERVICE_FEE_DEPOSIT_RETURN;

		} else if (invoiceLineItem instanceof LatePaymentFeeInvoiceLineItem) {
			txTypeCode = RegisterTransactionType.SERVICE_FEE_LATE_PAYMENT;

		} else if (invoiceLineItem instanceof MiscFeeInvoiceLineItem) {
			txTypeCode = RegisterTransactionType.SERVICE_FEE_MISCELLANEOUS;

		} else if (invoiceLineItem instanceof WithdrawalReturnFeeInvoiceLineItem) {
			txTypeCode = RegisterTransactionType.SERVICE_FEE_WITHDRAWAL_RETURN;
		}

		if (StringUtils.isNotBlank(txTypeCode)) {
			if (invoiceLineItem.getInvoice() instanceof SubscriptionInvoice) {
				SubscriptionInvoice invoice = (SubscriptionInvoice) invoiceLineItem.getInvoice();
				SubscriptionPaymentPeriod paymentPeriod = (SubscriptionPaymentPeriod) invoice.getPaymentPeriod();
				return createAndExecuteServiceTransaction(accountRegister, paymentPeriod, amountToPay, txTypeCode, pending, paymentPeriod.getSubscriptionConfiguration().isVendorOfRecord());

			} else if (invoiceLineItem.getInvoice() instanceof AdHocInvoice) {
				AdHocInvoice invoice = (AdHocInvoice) invoiceLineItem.getInvoice();
				return createAndExecuteServiceTransaction(accountRegister, invoice.getPaymentPeriod(), amountToPay, txTypeCode, pending, isAdHocVORSoftwareFee);
			}
		}
		return null;
	}

	/**
	 * Create 'cmItem' register transactions. If original invoice has been paid we execute the transaction to update
	 * account balances.
	 */
	@Override
	public <T extends InvoiceLineItem> CreditMemoTransaction createCreditMemoInvoiceItemRegisterTransaction(
		Company company, CreditMemoIssuableInvoiceLineItem invoiceLineItem, boolean originalInvoicePaid) {

		AccountRegister accountRegister = findDefaultRegisterForCompany(company);
		BigDecimal amountToPay = invoiceLineItem.getAmount();
		CreditMemoType creditMemoType = invoiceLineItem.getCreditMemoType();
		boolean isAdHocVORSoftwareFee = invoiceLineItem.isAdHocVORSoftwareFee();

		if (invoiceLineItem.getInvoice() instanceof CreditMemo) {

			// Create 'transaction' records (inserts into register_transaction and credit_memo_transaction)
			CreditMemoTransaction creditMemoTransaction = createCreditMemoTransaction(accountRegister, invoiceLineItem,
				creditMemoType, isAdHocVORSoftwareFee, originalInvoicePaid);

			// If the original invoice was paid we execute the transaction and updated account balances in order
			// to refund the original invoice transaction
			if (originalInvoicePaid) {
				RegisterTransactionExecutor registerTransactionsAbstract =
					registerTransactionExecutableFactory.newInstance(RegisterTransactionType.CREDIT_MEMO_ITEM);
				registerTransactionsAbstract.setPending(false);
				registerTransactionsAbstract.execute(accountRegister, amountToPay, creditMemoTransaction);
			}

			return creditMemoTransaction;

		}
		return null;
	}

	/**
	 * Create CreditMemoTransaction for the given Credit Memo line item. A Credit Memo transaction has register_transaction
	 * record with register_transaction_type = 'cmItem' and an associated credit_memo_transaction record with a
	 * credit_memo_type field that identifies the type of credit memo transaction and another field that indicates
	 * if the original invoice has already been paid.
	 */
	private <T extends InvoiceLineItem> CreditMemoTransaction createCreditMemoTransaction(
		AccountRegister accountRegister, T invoiceLineItem, CreditMemoType creditMemoType,
		boolean isAdHocVORSoftwareFee, boolean originalInvoicePaid) {

		CreditMemoTransaction creditMemoTransaction = new CreditMemoTransaction();
		creditMemoTransaction
			.setCreditMemoType(creditMemoType.ordinal())
			.setOriginalInvoicePaid(originalInvoicePaid)
			.setSubscriptionVendorOfRecord(isAdHocVORSoftwareFee)
			.setAccountRegister(accountRegister)
			.setTransactionDate(Calendar.getInstance())
			.setRegisterTransactionType(
				new RegisterTransactionType(RegisterTransactionType.CREDIT_MEMO_ITEM))
			.setPendingFlag(false)
			.setEffectiveDate(Calendar.getInstance())
			.setAmount(invoiceLineItem.getAmount());

		AccountRegisterSummaryFields accountRegisterSummaryFields = new AccountRegisterSummaryFields();
		BeanUtilities.copyProperties(accountRegisterSummaryFields, accountRegister.getAccountRegisterSummaryFields());

		creditMemoTransaction.setAccountRegisterSummaryFields(accountRegisterSummaryFields);
		registerTransactionDAO.saveOrUpdate(creditMemoTransaction);

		return creditMemoTransaction;
	}

	@Override
	public void createSubscriptionIncrementalTransactions(Company company, SubscriptionPaymentPeriod paymentPeriod, SubscriptionPaymentDTO subscriptionPaymentDTO, boolean pending) {
		AccountRegister accountRegister = findDefaultRegisterForCompany(company);
		boolean vendorOfRecord = paymentPeriod.getSubscriptionConfiguration().isVendorOfRecord();
		boolean incrementalTransaction = true;

		if (subscriptionPaymentDTO.hasValue()) {
			if (subscriptionPaymentDTO.hasDiscount()) {
				createAndExecuteServiceTransaction(accountRegister, paymentPeriod, subscriptionPaymentDTO.getDiscount(), RegisterTransactionType.SUBSCRIPTION_DISCOUNT, pending, vendorOfRecord, incrementalTransaction);
			}
			if (subscriptionPaymentDTO.hasAddOnsFee()) {
				createAndExecuteServiceTransaction(accountRegister, paymentPeriod, subscriptionPaymentDTO.getAddOnsAmount(), RegisterTransactionType.SUBSCRIPTION_ADD_ON_PAYMENT, pending, vendorOfRecord, incrementalTransaction);
			}
			if (subscriptionPaymentDTO.hasSetupFee()) {
				createAndExecuteServiceTransaction(accountRegister, paymentPeriod, subscriptionPaymentDTO.getSetupFee(), RegisterTransactionType.SUBSCRIPTION_SETUP_FEE_PAYMENT, pending, vendorOfRecord, incrementalTransaction);
			}
			if (subscriptionPaymentDTO.hasVorFee()) {
				createAndExecuteServiceTransaction(accountRegister, paymentPeriod, subscriptionPaymentDTO.getVorFeeAmount(), RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT, pending, vendorOfRecord, incrementalTransaction);
			}
			if (subscriptionPaymentDTO.hasSoftwareFee()) {
				createAndExecuteServiceTransaction(accountRegister, paymentPeriod, subscriptionPaymentDTO.getSoftwareFeeAmount(), RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT, pending, vendorOfRecord, incrementalTransaction);
			}
		}
	}

	ServiceTransaction createAndExecuteServiceTransaction(AccountRegister accountRegister, PaymentPeriod paymentPeriod, BigDecimal amount, String registerTransactionTypeCode, boolean pending, boolean vendorOfRecord) {
		return createAndExecuteServiceTransaction(accountRegister, paymentPeriod, amount, registerTransactionTypeCode, pending, vendorOfRecord, false);
	}

	ServiceTransaction createAndExecuteServiceTransaction(AccountRegister accountRegister, PaymentPeriod paymentPeriod, BigDecimal amount, String registerTransactionTypeCode, boolean pending, boolean vendorOfRecord, boolean isIncrementalTx) {
		Assert.notNull(amount);
		Assert.hasText(registerTransactionTypeCode);
		Assert.notNull(accountRegister);
		if (!NumberUtilities.isZero(amount)) {
			ServiceTransaction serviceTransaction = new ServiceTransaction();
			serviceTransaction.setPaymentPeriod(paymentPeriod);
			serviceTransaction.setSubscriptionVendorOfRecord(vendorOfRecord);
			serviceTransaction.setSubscriptionIncrementalTransaction(isIncrementalTx);
			RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(registerTransactionTypeCode);
			registerTransactionsAbstract.setPending(pending);
			registerTransactionsAbstract.execute(accountRegister, amount.negate(), serviceTransaction);
			return serviceTransaction;
		}
		return null;
	}

	@Override
	public RegisterTransaction findRegisterTransaction(long id) {
		return registerTransactionDAO.get(id);
	}

	@Override
	public void resetAccountRegisterForAccountPricingType(Company company) {
		AccountRegister register = findDefaultRegisterForCompany(company);
		if (register != null) {
			if (company.getPaymentConfiguration().getAccountPricingType().isSubscriptionPricing()) {
				register.setCurrentWorkFeePercentage(BigDecimal.ZERO);
				AccountRegisterSummaryFields summaryFields = register.getAccountRegisterSummaryFields();
				summaryFields.setAssignmentSoftwareThroughput(BigDecimal.ZERO);
				summaryFields.setAssignmentVorThroughput(BigDecimal.ZERO);
			} else {
				register.setCurrentWorkFeePercentage(Constants.DEFAULT_WORK_FEE_PERCENTAGE);
			}
		}
	}

	@Override
	public Map<Long, List<Long>> findAllSubscriptionTransactionPendingInvoice() {
		return registerTransactionDAO.findAllSubscriptionTransactionPendingInvoice();
	}

	@Override
	public BigDecimal findPaymentTermsCommitmentBalance(Long accountRegisterId) {
		return registerTransactionDAO.findPaymentTermsCommitmentBalance(accountRegisterId);
	}

	/* (non-Javadoc)
		 * @see com.workmarket.domains.payments.service.AccountRegisterService#fulfillWorkPayment(com.workmarket.domains.work.model.Work)
		 */
	@Override
	public boolean fulfillWorkPayment(Work work, AbstractInvoice invoiceSummary) throws InsufficientFundsException, InsufficientBudgetException {
		Assert.notNull(work);
		WorkResource workResource = workService.findActiveWorkResource(work.getId());

		Assert.notNull(workResource);
		logger.debug("fulfillWorkPayment()....");

		Company buyerCompany = work.getCompany();
		WorkResource originalResource = findOriginalWorkResource(workResource);

		LaneType laneType = laneService.getLaneTypeForUserAndCompany(originalResource.getUser().getId(), buyerCompany.getId());
		boolean hasOfflinePayment = workOptionsService.hasOption(work, WorkOption.MBO_ENABLED, "true");
		if ((laneType.isLane0() || laneType.isLane1()) || hasOfflinePayment) {
			return false;
		}

		// Find commitments and marks them non pending
		AccountRegister buyerAccountRegister = findDefaultRegisterForCompany(buyerCompany.getId());
		AccountRegister resourceAccountRegister = findDefaultResourceRegisterForWork(work);

		// Any pending amounts are reversed in the AccountRegister Summaries and pending flags are set false.
		revertAccountRegisterPendingTransactions(work);

		//Since there was a payment already we just need to create the transactions for the work amounts, no calculation required
		BigDecimal buyerFee = work.getFulfillmentStrategy().getBuyerFee();
		BigDecimal workPrice = work.getFulfillmentStrategy().getWorkPrice();
		logger.debug("[fulfillWorkPayment] buyerFee: " + buyerFee);
		logger.debug("[fulfillWorkPayment] workPrice: " + workPrice);

		boolean updateSummaries = true;
		String invoiceSummaryType = invoiceSummary.getType();
		//If it was paid using a bundle, none of the buyer transactions should update the summaries since the invoice/statement payment already did it
		if (invoiceSummaryType.equals(InvoiceSummary.INVOICE_SUMMARY_TYPE) || invoiceSummaryType.equals(Statement.STATEMENT_TYPE)) {
			updateSummaries = false;
		}
		boolean isBatchPayment = invoiceSummaryType.equals(InvoiceCollection.INVOICE_COLLECTION_TYPE);
		boolean isBundledPayment = !isBatchPayment;

		WorkResourceTransaction buyerWorkResourceTransactionFee = createAndExecuteFeePaymentWorkResourceTransaction(workResource, buyerAccountRegister, buyerFee, laneType, updateSummaries, isBundledPayment, isBatchPayment);
		logger.debug(buyerWorkResourceTransactionFee);

		WorkResourceTransaction buyerWorkResourceTransactionResourceCost = createAndExecuteBuyerWorkPaymentWorkResourceTransaction(workResource, buyerAccountRegister, workPrice, updateSummaries, isBundledPayment, isBatchPayment);
		logger.debug(buyerWorkResourceTransactionResourceCost);

		//For the resource register transaction the summaries should be updated as usual.
		WorkResourceTransaction resourceWorkResourceTransaction = createAndExecuteResourceWorkPaymentWorkResourceTransaction(workResource, resourceAccountRegister, workPrice);
		logger.debug(resourceWorkResourceTransaction);

		// Remove budget from project if the work has project and the project enables budget during fulfillment
		if (work.hasProject() && work.getProject().getBudgetEnabledFlag()) {
			if (work.getProject().getRemainingBudget().compareTo(work.getFulfillmentStrategy().getBuyerTotalCost()) == -1) {
				throw new InsufficientBudgetException("There is no enough budget in the project !");
			}
			projectBudgetService.decreaseRemainingBudget(work.getProject(), work.getFulfillmentStrategy().getBuyerTotalCost());
		}

		// Invoice was fast funded. We need to resolve the work payment and commitment transactions
		boolean hasInvoiceBeenFastFunded = work.isInvoiced() && work.getInvoice().getFastFundedOn() != null;
		if (hasInvoiceBeenFastFunded) {
			createAndExecuteFastFundsDebitWorkResourceTransactionTransaction(workResource, resourceAccountRegister, workPrice);

			FastFundsReceivableCommitment fastFundsReceivableCommitment = fastFundsReceivableCommitmentDAO.findCommitmentByWorkId(work.getId());
			Assert.notNull(fastFundsReceivableCommitment, "Work with ID: " + work.getId() + " has been fast funded, it should have a receivable commitment");
			fastFundsReceivableCommitment.setPending(false);
			fastFundsReceivableCommitment.setEffectiveDate(Calendar.getInstance());
		}

		return true;
	}

	@Override
	public List<InvoicePaymentTransaction> findAllInvoicePaymentTransactionsPendingFulfillment() {
		return registerTransactionDAO.findAllInvoicePaymentTransactionsByFulfillmentStatus(PaymentFulfillmentStatusType.PENDING_FULFILLMENT);
	}

	@Override
	public List<Long> findAllAccountRegisterIds() {
		return accountRegisterDAO.findAllIds();
	}

	@Override
	public BigDecimal calculateAvailableCashByAccountRegister(Long accountRegisterId) {
		Assert.notNull(accountRegisterId);
		return registerTransactionDAO.calculateAvailableCashByAccountRegister(accountRegisterId);
	}

	@Override
	public BigDecimal getSumSpentAvailableCash(Long accountRegisterId) {
		Assert.notNull(accountRegisterId);
		return registerTransactionDAO.sumSpentAvailableCash(accountRegisterId);
	}

	@Override
	public void processPaymentSummationsForAccountRegister(Long accountRegisterId, Date fromDate) {
		AccountRegister accountRegister = getAccountRegisterById(accountRegisterId);
		if (accountRegister != null) {
			BigDecimal paymentSummation = registerTransactionDAO.paymentsByAccountRegisterIdAndDate(accountRegister.getId(), fromDate);
			if (paymentSummation != null) {
				WorkFeeBand workFeeBand = pricingService.determineWorkFeeBand(accountRegister);
				if (workFeeBand != null) {
					accountRegister.setPaymentSummation(paymentSummation.abs());
					accountRegister.setWorkFeeLevel(workFeeBand.getLevel());
					accountRegister.setCurrentWorkFeePercentage(workFeeBand.getPercentage());
					accountRegisterDAO.saveOrUpdate(accountRegister);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#findFundingTransactionsByTransactionDate(com.workmarket.domains.model.DateFilter, com.workmarket.domains.model.account.AccountTransactionReportRowPagination)
	 */
	@Override
	public AccountTransactionReportRowPagination findFundingTransactionsByTransactionDate(DateFilter transactionDateFilter, AccountTransactionReportRowPagination pagination) {
		Assert.notNull(transactionDateFilter);
		Assert.notNull(transactionDateFilter.getFromDate());
		Assert.notNull(transactionDateFilter.getToDate());
		Assert.notNull(pagination);
		return registerTransactionActivityDAO.findFundingTransactionsByTransactionDate(transactionDateFilter.getFromDate(), transactionDateFilter.getToDate(), pagination);
	}

	public BigDecimal getActualCashFundsByCompany(Long companyId) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(companyId);
		return accountRegister.getAccountRegisterSummaryFields().getActualCash();
	}

	public Optional<CreditCardTransaction> findCreditCardTransaction(Long transactionId, Long companyId) {
		Assert.notNull(transactionId);
		Assert.notNull(companyId);
		CreditCardTransaction registerTransaction = registerTransactionDAO.findCreditCardTransaction(transactionId, companyId);
		return Optional.fromNullable(registerTransaction);
	}

	public Optional<BankAccountTransaction> findBankAccountTransaction(Long transactionId, Long companyId) {
		Assert.notNull(transactionId);
		Assert.notNull(companyId);
		BankAccountTransaction registerTransaction = registerTransactionDAO.findBankAccountTransaction(transactionId, companyId);
		return Optional.fromNullable(registerTransaction);
	}

	public Optional<RegisterTransaction> findWireOrCheckTransaction(Long transactionId, Long companyId) {
		Assert.notNull(transactionId);
		Assert.notNull(companyId);
		RegisterTransaction registerTransaction = registerTransactionDAO.findWireOrCheckTransaction(transactionId, companyId);
		return Optional.fromNullable(registerTransaction);
	}

	@Override
	public void transferFundsToGeneral(Long projectId, Long companyId, BigDecimal amount) {
		Assert.notNull(companyId);
		Assert.notNull(projectId);
		addFundsToGeneral(companyId, amount);
		removeFundsFromProject(projectId, companyId, amount);
	}

	public void transferFundsToProject(Long projectId, Long companyId, BigDecimal amount) {
		Assert.notNull(companyId);
		Assert.notNull(projectId);
		removeFundsFromGeneral(companyId, amount);
		addFundsToProject(projectId, companyId, amount);
	}

	@Override
	public void transferFundsBetweenProjects(Long projectFrom, Long projectTo, Long companyId, BigDecimal amount) {
		Assert.notNull(companyId);
		Assert.notNull(projectFrom);
		Assert.notNull(projectTo);
		removeFundsFromProject(projectFrom, companyId, amount);
		addFundsToProject(projectTo, companyId, amount);
	}

	public void addFundsToProjectFromAch(Long projectId, Long companyId, BigDecimal amount, Long parentTxId) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = findDefaultRegisterForCompany(companyId);
		ProjectTransaction projectTransaction = new ProjectTransaction();
		Project project = projectService.findById(projectId);
		RegisterTransaction parentTransaction = findRegisterTransaction(parentTxId);
		projectTransaction.setProject(project);
		projectTransaction.setParentTransaction(parentTransaction);

		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.ADD_FUNDS_TO_PROJECT);
		registerTransactionsAbstract.setPending(true);
		registerTransactionsAbstract.execute(accountRegister, amount, projectTransaction);

	}

	@Override
	public void addFundsToGeneralFromAch(Long companyId, BigDecimal amount, Long parentTxId) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = findDefaultRegisterForCompany(companyId);
		GeneralTransaction generalTransaction = new GeneralTransaction();
		generalTransaction.setParentTransaction(findRegisterTransaction(parentTxId));

		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.ADD_FUNDS_TO_GENERAL);
		registerTransactionsAbstract.setPending(true);
		registerTransactionsAbstract.execute(accountRegister, amount, generalTransaction);
	}

	public void addFundsToProject(Long projectId, Long companyId, BigDecimal amount) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = findDefaultRegisterForCompany(companyId);
		ProjectTransaction projectTransaction = new ProjectTransaction();
		Project project = projectService.findById(projectId);
		projectTransaction.setProject(project);

		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.ADD_FUNDS_TO_PROJECT);
		registerTransactionsAbstract.execute(accountRegister, amount, projectTransaction);
	}

	@Override
	public void removeFundsFromProject(Long projectId, Long companyId, BigDecimal amount) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = findDefaultRegisterForCompany(companyId);
		ProjectTransaction projectTransaction = new ProjectTransaction();
		Project project = projectService.findById(projectId);
		projectTransaction.setProject(project);

		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.REMOVE_FUNDS_FROM_PROJECT);
		registerTransactionsAbstract.execute(accountRegister, amount.negate(), projectTransaction);
	}

	void addFundsToGeneral(Long companyId, BigDecimal amount) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = findDefaultRegisterForCompany(companyId);
		RegisterTransaction registerTransaction = new RegisterTransaction();

		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.ADD_FUNDS_TO_GENERAL);
		registerTransactionsAbstract.setPending(false);
		registerTransactionsAbstract.execute(accountRegister, amount, registerTransaction);
	}

	@Override
	public void removeFundsFromGeneral(Long companyId, BigDecimal amount) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = findDefaultRegisterForCompany(companyId);
		RegisterTransaction registerTransaction = new RegisterTransaction();

		RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.REMOVE_FUNDS_FROM_GENERAL);
		registerTransactionsAbstract.execute(accountRegister, amount.negate(), registerTransaction);
	}

	@Override
	public boolean validatePayWork(Work work) {
		Assert.notNull(work);
		//Validate that there hasn't been a payment for a bundle or statement.
		if (workService.isWorkPendingFulfillment(work.getId())) {
			logger.error("work id :" + work.getId() + " can't be paid because is pending fulfillment");
			return false;
		}

		//Assignment needs to have a payment pending status
		if (!work.isPaymentPending()) {
			logger.error("Invalid status, work id :" + work.getId() + " " + work.getWorkStatusType().getCode());
			return false;
		}

		if (!work.isInvoiced()) {
			logger.error("Null invoice for work id :" + work.getId());
			return false;
		}

		if (work.getInvoice().isBundled()) {
			logger.error("Can't pay a bundled invoice, work id: " + work.getId() + " invoice id : " + work.getInvoice().getId());
			return false;
		}
		return true;
	}

	@Override
	public void updateApLimit(long companyId, BigDecimal amount) {
		Assert.notNull(amount);
		AccountRegister accountRegister = findDefaultRegisterForCompany(companyId);
		if (accountRegister != null && NumberUtilities.isZero(accountRegister.getApLimit())) {
			accountRegister.setApLimit(amount);
		}

	}

	@Override
	public BigDecimal findRemainingAuthorizedAmountByWorkBundle(long workBundleId) {
		WorkBundleTransaction workBundleTransaction = registerTransactionDAO.findWorkBundlePendingAuthorizationTransaction(workBundleId);
		if (workBundleTransaction != null) {
			return workBundleTransaction.getRemainingAuthorizedAmount();
		}
		return BigDecimal.ZERO;
	}

	@Override
	public void reconcileSubscriptionThroughput(long companyId, Calendar fromDate) {
		Assert.notNull(fromDate);
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(companyId);
		final Set<String> differences = Sets.newHashSet();

		if (accountRegister != null) {
			AccountRegisterSummaryFields accountRegisterSummaryFields = accountRegister.getAccountRegisterSummaryFields();

			BigDecimal swThroughput = registerTransactionDAO.calculateSubscriptionAssignmentThroughput(accountRegister.getId(), fromDate);

			if (swThroughput.abs().compareTo(accountRegisterSummaryFields.getAssignmentSoftwareThroughput()) != 0) {
				String error = "[throughput] ******** Found difference on SW Throughput for company " + companyId + " Calculated swThroughput " + swThroughput.abs();
				logger.error(error);
				differences.add(error);
			}
		}
		if (isNotEmpty(differences)) {
			userNotificationService.onSubscriptionThroughputDifference(differences);
		}
	}

	public Optional<BigDecimal> decreaseProjectBudget(Work work, WorkCostDTO accountRegisterMoniesDTO) throws InsufficientBudgetException {
		if (work.hasProject() && work.getProject().getBudgetEnabledFlag()) {
			verifySufficientProjectRemainingBudget(work, accountRegisterMoniesDTO);
			projectBudgetService.decreaseRemainingBudget(work.getProject(), accountRegisterMoniesDTO.getTotalBuyerCost().abs());
		}
		return Optional.absent();
	}

	@Override
	public BigDecimal getPaymentSummation(Long companyId) {
		Assert.notNull(companyId);

		return accountRegisterDAO.getPaymentSummation(companyId);
	}

	@Override
	public BigDecimal getCurrentWorkFeePercentage(Long companyId) {
		Assert.notNull(companyId);

		return accountRegisterDAO.getCurrentWorkFeePercentage(companyId);
	}

	@Override
	public BigDecimal getAccountsPayableBalance(Long companyId) {
		Assert.notNull(companyId);

		return accountRegisterDAO.getAccountsPayableBalance(companyId);
	}

	@Override
	public BigDecimal getAPLimit(Long companyId) {
		Assert.notNull(companyId);

		return accountRegisterDAO.getAPLimit(companyId);
	}

	@Override
	public InvoicePaymentTransaction findInvoicePaymentTransactionByInvoice(AbstractInvoice invoice){
		Assert.notNull(invoice);
		return registerTransactionDAO.findInvoicePaymentTransactionByInvoice(invoice);
	}
}
