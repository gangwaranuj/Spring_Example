/**
 *
 */
package com.workmarket.service.business.accountregister;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.payments.dao.AccountRegisterDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.WorkBundleTransaction;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.account.pricing.AccountPricingServiceTypeEntity;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.utility.BeanUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @since 5/1/2011
 */

@Component
@Scope(value = "prototype")
public abstract class RegisterTransactionExecutor implements RegisterTransactionExecutable {

	@Autowired protected RegisterTransactionDAO registerTransactionDAO;
	@Autowired protected AccountRegisterDAO accountRegisterDAO;
	@Autowired protected AccountPricingService accountPricingService;
	@Autowired protected FeatureEvaluator featureEvaluator;
	@Autowired private MetricRegistry registry;

	private MetricRegistryFacade facade;

	private boolean pending = false;
	private static final String PENDING = "pending";
	private static final String WORK_BUNDLE = "Bundle";
	private static final Log logger = LogFactory.getLog(RegisterTransactionExecutor.class);

	@PostConstruct
	public void init() {
		facade = new WMMetricRegistryFacade(registry, "account_register");
	}

	public static String createBeanName(boolean pending, String registerTransactionType) {
		if (pending && StringUtils.isNotBlank(registerTransactionType)) {
			return PENDING + registerTransactionType.substring(0, 1).toUpperCase() + registerTransactionType.substring(1);
		}

		return registerTransactionType;
	}

	public static String createBeanName(boolean pending, String registerTransactionType, Work work) {
		if (work.isWorkBundle()) {
			if (pending && StringUtils.isNotBlank(registerTransactionType)) {
				return PENDING + WORK_BUNDLE + registerTransactionType.substring(0, 1).toUpperCase() + registerTransactionType.substring(1);
			}
		}

		return createBeanName(pending, registerTransactionType);
	}

	@Override
	public WorkResourceTransaction execute(Work work, WorkResource workResource, AccountRegister accountRegister, BigDecimal amount) {
		return execute(work, workResource, accountRegister, amount, true, false, false);
	}

	@Override
	public WorkResourceTransaction execute(Work work, WorkResource workResource, AccountRegister accountRegister, BigDecimal amount, boolean updateSummaries) {
		return execute(work, workResource, accountRegister, amount, updateSummaries, false, false);
	}

	@Override
	public WorkResourceTransaction execute(Work work, WorkResource workResource, AccountRegister accountRegister, BigDecimal amount, boolean updateSummaries, boolean isBundled, boolean isBatchPayment) {
		Assert.notNull(work);
		Assert.notNull(amount);
		facade.meter(getClass().getSimpleName() + ".execute").mark();
		try {
			WorkResourceTransaction workResourceTransaction = buildWorkResourceTransaction(work, amount);
			if (workResource != null) {
				workResourceTransaction.setWorkResource(workResource);
			}
			workResourceTransaction.setWork(work);
			workResourceTransaction.setBundlePayment(isBundled);
			workResourceTransaction.setBatchPayment(isBatchPayment);
			if (work.getProject() != null) {
				workResourceTransaction.setProject(work.getProject());
			}

			AccountPricingServiceTypeEntity accountPricingServiceTypeEntity = new AccountPricingServiceTypeEntity();
			BeanUtilities.copyProperties(accountPricingServiceTypeEntity, work.getAccountPricingServiceTypeEntity());
			workResourceTransaction.setAccountPricingServiceTypeEntity(accountPricingServiceTypeEntity);

			populateTransaction(accountRegister, workResourceTransaction, amount, getRegisterTransactionType(), isPending());

			if (updateSummaries) {
				updateSummaries(workResourceTransaction);
			}
			updateAssignmentThroughputSummaries(workResourceTransaction);

			AccountRegisterSummaryFields accountRegisterSummaryFields = new AccountRegisterSummaryFields();
			BeanUtilities.copyProperties(accountRegisterSummaryFields, accountRegister.getAccountRegisterSummaryFields());
			workResourceTransaction.setAccountRegisterSummaryFields(accountRegisterSummaryFields);

			logger.debug(accountRegister.getAccountRegisterSummaryFields());
			registerTransactionDAO.saveOrUpdate(workResourceTransaction);

			return (WorkResourceTransaction) onPostExecution(workResourceTransaction);

		} catch (HibernateOptimisticLockingFailureException | CannotAcquireLockException | StaleObjectStateException | LockAcquisitionException | DataException e) {
			throw new AccountRegisterConcurrentException(e.getMessage());
		}
	}

	<T extends WorkResourceTransaction> T buildWorkResourceTransaction(Work work, BigDecimal amount) {
		Assert.notNull(work);
		if (work.isWorkBundle()) {
			WorkBundleTransaction workBundleTransaction = new WorkBundleTransaction();
			workBundleTransaction.setRemainingAuthorizedAmount(amount.abs());
			return (T) workBundleTransaction;
		}
		return (T) new WorkResourceTransaction();
	}

	public RegisterTransaction onPostExecution(RegisterTransaction registerTransaction) {
		return registerTransaction;
	}

	@Override
	public RegisterTransaction execute(AccountRegister accountRegister, BigDecimal amount, RegisterTransaction registerTransaction) throws AccountRegisterConcurrentException {
		try {
			facade.meter(getClass().getSimpleName() + ".execute").mark();
			Assert.notNull(getRegisterTransactionType());
			Assert.notNull(amount);
			populateTransaction(accountRegister, registerTransaction, amount, getRegisterTransactionType(), isPending());
			updateSummaries(registerTransaction);

			AccountRegisterSummaryFields accountRegisterSummaryFields = new AccountRegisterSummaryFields();
			BeanUtilities.copyProperties(accountRegisterSummaryFields, accountRegister.getAccountRegisterSummaryFields());

			registerTransaction.setAccountRegisterSummaryFields(accountRegisterSummaryFields);
			registerTransactionDAO.saveOrUpdate(registerTransaction);
			// Returning RegisterTransaction for style purposes. Shows object was updated.
			return onPostExecution(registerTransaction);

		} catch (HibernateOptimisticLockingFailureException | CannotAcquireLockException | StaleObjectStateException | LockAcquisitionException | DataException e) {
			throw new AccountRegisterConcurrentException(e.getMessage());
		}
	}

	public void reverseSummaries(RegisterTransaction registerTransaction) throws AccountRegisterConcurrentException {
		if (registerTransaction != null) {
			logger.debug("[registerTransaction] " + registerTransaction);
			facade.meter(getClass().getSimpleName() + ".reverse-summaries").mark();
			try {
				if (reverse(registerTransaction)) {
					AccountRegisterSummaryFields accountRegisterSummaryFields = new AccountRegisterSummaryFields();
					BeanUtilities.copyProperties(accountRegisterSummaryFields, registerTransaction.getAccountRegister().getAccountRegisterSummaryFields());
					registerTransaction.setAccountRegisterSummaryFields(accountRegisterSummaryFields);
					registerTransactionDAO.saveOrUpdate(registerTransaction);
				}
			} catch (HibernateOptimisticLockingFailureException | CannotAcquireLockException | StaleObjectStateException | LockAcquisitionException | DataException e) {
				throw new AccountRegisterConcurrentException(e.getMessage());
			}
		}
	}

	protected void populateTransaction(AccountRegister accountRegister, RegisterTransaction transaction, BigDecimal amount, RegisterTransactionType type, boolean pendingFlag) {
		Assert.notNull(transaction);
		transaction.setAccountRegister(accountRegister);
		transaction.setTransactionDate(Calendar.getInstance());
		transaction.setRegisterTransactionType(type);
		transaction.setPendingFlag(pendingFlag);
		transaction.setEffectiveDate(Calendar.getInstance());
		transaction.setAmount(amount);
	}

	public abstract void updateSummaries(RegisterTransaction registerTransaction) throws InsufficientFundsException;

	public boolean updateAssignmentThroughputSummaries(WorkResourceTransaction workResourceTransaction) {
		return false;
	}

	public abstract boolean reverse(RegisterTransaction registerTransaction);

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public abstract RegisterTransactionType getRegisterTransactionType();

	@Override
	public void updateDepositedAndWithdrawableCash(AccountRegisterSummaryFields accountRegisterSummaryFields,
												   RegisterTransaction registerTransaction) throws InsufficientFundsException {

		Assert.notNull(accountRegisterSummaryFields);
		Assert.notNull(registerTransaction);
		//If there's enough money on the deposited Cash we take it from there.
		if (accountRegisterSummaryFields.getDepositedCash().compareTo(registerTransaction.getAmount().abs()) == 1) {
			// Adding a negative value
			accountRegisterSummaryFields.setDepositedCash(accountRegisterSummaryFields.getDepositedCash().add(registerTransaction.getAmount()));
			return;
		}

		//Otherwise we take all the money from deposited and the rest from the withdrawable
		BigDecimal depositedCash = accountRegisterSummaryFields.getDepositedCash();
		accountRegisterSummaryFields.setDepositedCash(BigDecimal.ZERO);
		// A larger negative transaction amount plus a positive depositedCash... result a lower negative
		BigDecimal takeRemainingFromWithdrawableCash = registerTransaction.getAmount().add(depositedCash);

		// Assuming there are enough funds in withdrawable_cash based on above InsufficientFundsException logic...
		if (takeRemainingFromWithdrawableCash.abs().compareTo(accountRegisterSummaryFields.getWithdrawableCash().abs()) == 1)
			throw new InsufficientFundsException("There isn't enough deposited cash and withdrawable cash to subtract from amount:"
					+ registerTransaction.getAmount() + " with a difference:"
					+ (accountRegisterSummaryFields.getWithdrawableCash().add(takeRemainingFromWithdrawableCash)));

		accountRegisterSummaryFields.setWithdrawableCash(accountRegisterSummaryFields.getWithdrawableCash().add(takeRemainingFromWithdrawableCash));

	}

	@Override
	public void updateActualCashAndAvailableCash(AccountRegisterSummaryFields accountRegisterSummaryFields, RegisterTransaction registerTransaction) {
		Assert.notNull(accountRegisterSummaryFields);
		Assert.notNull(registerTransaction);
		accountRegisterSummaryFields.setAvailableCash(accountRegisterSummaryFields.getAvailableCash().add(registerTransaction.getAmount()));
		accountRegisterSummaryFields.setActualCash(accountRegisterSummaryFields.getActualCash().add(registerTransaction.getAmount()));
	}

	@Override
	public void addGeneralCash(AccountRegisterSummaryFields accountRegisterSummaryFields, RegisterTransaction registerTransaction) {
		Assert.notNull(accountRegisterSummaryFields);
		Assert.notNull(registerTransaction);
		accountRegisterSummaryFields.setGeneralCash(accountRegisterSummaryFields.getGeneralCash().add(registerTransaction.getAmount()));
	}

	@Override
	public void addProjectCash(AccountRegisterSummaryFields accountRegisterSummaryFields, RegisterTransaction registerTransaction) {
		Assert.notNull(accountRegisterSummaryFields);
		Assert.notNull(registerTransaction);
		accountRegisterSummaryFields.setProjectCash(accountRegisterSummaryFields.getProjectCash().add(registerTransaction.getAmount()));
	}

	@Override
	public void subtractGeneralCash(AccountRegisterSummaryFields accountRegisterSummaryFields, RegisterTransaction registerTransaction) {
		Assert.notNull(accountRegisterSummaryFields);
		Assert.notNull(registerTransaction);
		accountRegisterSummaryFields.setGeneralCash(accountRegisterSummaryFields.getGeneralCash().subtract(registerTransaction.getAmount().abs()));
	}

	@Override
	public void subtractProjectCash(AccountRegisterSummaryFields accountRegisterSummaryFields, RegisterTransaction registerTransaction) {
		Assert.notNull(accountRegisterSummaryFields);
		Assert.notNull(registerTransaction);
		accountRegisterSummaryFields.setProjectCash(accountRegisterSummaryFields.getProjectCash().subtract(registerTransaction.getAmount().abs()));
	}

	public String toString(String prefix, RegisterTransaction registerTransaction) {
		if (registerTransaction != null) {
			return prefix + ": amount:" + registerTransaction.getAmount() + ", pending:" + registerTransaction.getPendingFlag()
				+ ", registerTransactionType:" + registerTransaction.getRegisterTransactionType().getCode() + " registerTransactionId:" + registerTransaction.getId();
		}
		return StringUtils.EMPTY;
	}

	public String toString(AccountRegisterSummaryFields accountRegisterSummaryFields) {
		if (accountRegisterSummaryFields != null) {
			return "accountRegisterSummaryFields:" + ": accountsPayableBalance:" + accountRegisterSummaryFields.getAccountsPayableBalance() + ", accountsReceivableBalance:"
				+ accountRegisterSummaryFields.getAccountsReceivableBalance()
				+ ", depositedCash:" + accountRegisterSummaryFields.getDepositedCash() + " pendingCommitments:" + accountRegisterSummaryFields.getPendingCommitments()
				+ ", pendingEarnedCash:" + accountRegisterSummaryFields.getPendingEarnedCash() + ", withdrawableCash:" + accountRegisterSummaryFields.getWithdrawableCash();
		}
		return StringUtils.EMPTY;
	}

}
