package com.workmarket.domains.payments.service;

import com.google.common.collect.Maps;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.FastFundsReceivableCommitment;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkWorkResourceAccountRegister;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.accountregister.RegisterTransactionExecutor;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.service.exception.account.AccountRegisterNoActiveWorkResourceException;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InsufficientSpendLimitException;
import com.workmarket.service.exception.account.PaymentTermsAPCreditLimitException;
import com.workmarket.service.exception.project.InsufficientBudgetException;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
public class AccountRegisterServicePaymentTermsImpl extends AccountRegisterServiceAbstract implements AccountRegisterService {



	private static final Log logger = LogFactory.getLog(AccountRegisterServicePaymentTermsImpl.class);

	public Long[] acceptWork(WorkResource workResource) throws InsufficientFundsException, PaymentTermsAPCreditLimitException {
		Assert.notNull(workResource);
		WorkCostDTO accountRegisterMoniesDTO = calculateCostOnSentWork(workResource.getWork());
		verifySufficientBuyerFunds(workResource.getWork(), accountRegisterMoniesDTO, true);
		return acceptWork(workResource, RegisterTransactionType.RESOURCE_PAYMENT_TERMS_COMMITMENT_TO_RECEIVE_PAY, RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT);
	}

	@Override
	public boolean isWorkAuthorized(Work work) {
		WorkCostDTO accountRegisterMoniesDTO = calculateCostOnSentWork(work);
		WorkResourceTransaction workResourceTransaction;
		if (work.isWorkBundle()) {
			workResourceTransaction = registerTransactionDAO.findWorkBundleAuthorizationTransaction(work.getId());
			if (workResourceTransaction != null && !RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT_WORK_BUNDLE.equals(workResourceTransaction.getRegisterTransactionType().getCode())) {
				return false;
			}
		} else {
			workResourceTransaction = registerTransactionDAO.findWorkResourcePendingPaymentTermsCommitmentTransaction(work.getId());
		}
		return workResourceTransaction != null && workResourceTransaction.getAmount().abs().compareTo(accountRegisterMoniesDTO.getTotalBuyerCost()) >= 0;
	}

	@Override
	protected WorkResourceTransaction createAuthorizationTransaction(Work work, WorkCostDTO accountRegisterMoniesDTO, WorkResource authorizeWorkResource, boolean updateSummaries) {
		Assert.notNull(work);
		AccountRegister workRegister = findDefaultRegisterForCompany(work.getCompany());

		//Find open transactions
		WorkResourceTransaction workResourceTransaction = registerTransactionDAO.findWorkResourcePendingPaymentTermsCommitmentTransaction(work.getId());

		if (workResourceTransaction != null) {
			// If new commitment has a higher totalBuyerCost, update. Another solution might be to use voidPending... and create new register_transaction.
			if (accountRegisterMoniesDTO.getTotalBuyerCost().compareTo(workResourceTransaction.getAmount().abs()) == 1) {
				logger.debug("Found a workResourceTransaction with id:" + workResourceTransaction.getId());
				RegisterTransactionExecutor transactionExecutor = registerTransactionExecutableFactory.newInstance(RegisterTransactionExecutor.createBeanName(Boolean.TRUE, RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT, work));

				workResourceTransaction.setPendingFlag(Boolean.FALSE);
				workResourceTransaction.setEffectiveDate(Calendar.getInstance());
				transactionExecutor.reverseSummaries(workResourceTransaction);

				if (updateSummaries) {
					verifySufficientBuyerFunds(work, accountRegisterMoniesDTO, true);
				}
				workResourceTransaction = transactionExecutor.execute(work, authorizeWorkResource, workRegister, accountRegisterMoniesDTO.getTotalBuyerCost().negate(), updateSummaries);
			}
		} else {
			if (updateSummaries) {
				verifySufficientBuyerFunds(work, accountRegisterMoniesDTO, true);
			}
			RegisterTransactionExecutor transactionExecutor = registerTransactionExecutableFactory.newInstance(RegisterTransactionExecutor.createBeanName(Boolean.TRUE, RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT, work));
			workResourceTransaction = transactionExecutor.execute(work, authorizeWorkResource, workRegister, accountRegisterMoniesDTO.getTotalBuyerCost().negate(), updateSummaries);
			logger.debug("Created a workResourceTransaction with id:" + workResourceTransaction.getId());
		}
		return workResourceTransaction;
	}
	@Override
	public boolean completeWork(WorkResource workResource) {
		Work work = workResource.getWork();
		WorkCostDTO workCostDTO = calculateCostOnCompleteWork(work, workResource);
		logger.debug(workCostDTO.toString());

		work.setFulfillmentStrategy(createFulfillmentStrategyFromWorkCostDTO(workCostDTO));
		return updateWorkRegisterTransactionDueDate(workResource, workCostDTO);
	}

	/**
	 * @param workResource
	 * @
	 */
	protected boolean updateWorkRegisterTransactionDueDate(WorkResource workResource, WorkCostDTO accountRegisterMoniesDTO) {
		Assert.notNull(workResource);
		Assert.notNull(accountRegisterMoniesDTO);

		List<WorkResourceTransaction> workResourceTransactions =
				registerTransactionDAO.findWorkResourceTransactionPaymentTermsCommitmentReceivePay(workResource.getId());

		WorkResourceTransaction workResourceTransaction = CollectionUtilities.first(workResourceTransactions);
		if (workResourceTransaction != null) {
			if (workResourceTransaction.getAmount().compareTo(workResource.getWork().getFulfillmentStrategy().getAmountEarned()) != 0) {
				// There's been an change to the original agreed upon price, must void original transaction then create new one for new pricing.
				Work work = workResource.getWork();
				voidWork(work);
				verifySufficientBuyerFunds(work, accountRegisterMoniesDTO, false);

				AccountRegister resourceAccountRegister = findDefaultRegisterForCompany(workResource.getUser().getCompany().getId());
				RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionExecutor.createBeanName(Boolean.TRUE, RegisterTransactionType.RESOURCE_PAYMENT_TERMS_COMMITMENT_TO_RECEIVE_PAY));
				registerTransactionsAbstract.execute(work, workResource, resourceAccountRegister, accountRegisterMoniesDTO.getTotalResourceCost());

				// Buyer Transaction
				AccountRegister buyerAccountRegister = findDefaultRegisterForCompany(work.getCompany());
				registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(
						RegisterTransactionExecutor.createBeanName(Boolean.TRUE, RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT));
				registerTransactionsAbstract.execute(work, workResource, buyerAccountRegister, accountRegisterMoniesDTO.getTotalBuyerCost().negate());

				work.setFulfillmentStrategy(createFulfillmentStrategyFromWorkCostDTO(accountRegisterMoniesDTO));

				// Remove budget when cancel with pay
				decreaseProjectBudget(work, accountRegisterMoniesDTO);
			}
			return true;
		}
		return false;
	}

	/**
	 * @param work
	 * @return
	 * @throws InsufficientFundsException
	 * @
	 */
	public void repriceWork(Work work) throws InsufficientFundsException, InsufficientBudgetException, InsufficientSpendLimitException {
		Assert.notNull(work);
		logger.debug("workStatusType.code:" + work.getWorkStatusType().getCode());

		WorkResource workResource = workService.findActiveWorkResource(work.getId());

		// If the assignment is not 'active' and doesn't have a resource we reissue the buyer authorization
		if (!work.isActive() && workResource == null) {
			// revert pending authorization transaction and insert a new one with new price
			WorkAuthorizationResponse response = authorizeWork(work, false);
			if (WorkAuthorizationResponse.INSUFFICIENT_SPEND_LIMIT.equals(response)) {
				throw new InsufficientSpendLimitException();
			} else if(WorkAuthorizationResponse.INSUFFICIENT_FUNDS.equals(response)) {
				throw new InsufficientFundsException();
			}
			return;
		}

		// Otherwise we reauthorize the buyer and worker authorizations
		if (workResource != null) {
			acceptWork(workResource);
			return;
		}
		throw new AccountRegisterNoActiveWorkResourceException("Can't locate an active resource for workId" + work.getId());
	}

	/**
	 * Bulk pay of assignments with payment terms.
	 *
	 * @param workWorkResourceAccountRegisters
	 *
	 * @return A map of the paid assignments, with the workId as the Key and the paid resource as the Value
	 */
	@Override
	public Map<Long, Long> payPaymentTerms(List<WorkWorkResourceAccountRegister> workWorkResourceAccountRegisters) throws InsufficientFundsException {
		Map<Long, Long> paidAssignments = Maps.newHashMapWithExpectedSize(workWorkResourceAccountRegisters.size());

		for (WorkWorkResourceAccountRegister workWorkResourceAccountRegister : workWorkResourceAccountRegisters) {

			payPaymentTerms(workWorkResourceAccountRegister);
			paidAssignments.put(workWorkResourceAccountRegister.getWorkId(), workWorkResourceAccountRegister.getWorkResourceId());

		}
		return paidAssignments;
	}

	private void payPaymentTerms(WorkWorkResourceAccountRegister workResourceAccountRegister) {
		LaneType laneType = laneService.getLaneTypeForUserAndCompany(workResourceAccountRegister.getWorkResourceUserId(), workResourceAccountRegister.getCompanyId());
		Assert.notNull(laneType);
		long workId = workResourceAccountRegister.getWorkId();
		if (!laneType.isLane0() && !laneType.isLane1()) {

			Work work = workService.findWork(workId, false);
			if (!validatePayWork(work)) {
				return;
			}

			// Any pending amounts are reversed in the AccountRegister Summaries and pending flags are set false.
			voidWork(work);

			AccountRegister buyerAccountRegister = accountRegisterDAO.get(workResourceAccountRegister.getBuyerAccountRegisterId());
			logger.debug(buyerAccountRegister);

			AccountRegister resourceAccountRegister = findDefaultResourceRegisterForWork(work);
			WorkResource workResource = workResourceService.findWorkResourceById(workResourceAccountRegister.getWorkResourceId());

			// Determine the work transaction fee for the buyer
			WorkCostDTO workCostDTO = calculateCostOnCompleteWork(work, workResource);
			logger.debug(workCostDTO.toString());

			/* 	Verify there are sufficient funds in buyers account. For NetMoney
			** 	-1, 0, or 1 as this BigDecimal is numerically less than, equal to, or greater than val.
			*/
			if (workCostDTO.getTotalBuyerCost().compareTo(buyerAccountRegister.getAvailableCash()) == 1) {
				throw new InsufficientFundsException(" Buyer has availableCash of:" + buyerAccountRegister.getAvailableCash() + " while the totalBuyerCost is:" +
						workCostDTO.getTotalBuyerCost() + " companyId:" + work.getCompany().getId());
			}

			if (work.hasProject() && work.getProject().isReservedFundsEnabled()) {
				removeFundsFromProject(work.getProject().getId(), workResourceAccountRegister.getCompanyId(), workCostDTO.getTotalBuyerCost());
				decreaseProjectBudget(work, workCostDTO);
			} else {
				removeFundsFromGeneral(workResourceAccountRegister.getCompanyId(), workCostDTO.getTotalBuyerCost());
			}

			BigDecimal totalResourceCost = workCostDTO.getTotalResourceCost();

			createAndExecuteFeePaymentWorkResourceTransaction(workResource, buyerAccountRegister, workCostDTO.getBuyerFee(), laneType);
			createAndExecuteBuyerWorkPaymentWorkResourceTransaction(workResource, buyerAccountRegister, totalResourceCost);
			work.setFulfillmentStrategy(createFulfillmentStrategyFromWorkCostDTO(workCostDTO));
			createAndExecuteResourceWorkPaymentWorkResourceTransaction(workResource, resourceAccountRegister, totalResourceCost);

			Invoice invoice = billingService.findInvoiceByWorkId(workId);
			boolean hasInvoiceBeenFastFunded = invoice != null && invoice.getFastFundedOn() != null;
			if (hasInvoiceBeenFastFunded) {
				createAndExecuteFastFundsDebitWorkResourceTransactionTransaction(workResource, resourceAccountRegister, totalResourceCost);

				FastFundsReceivableCommitment fastFundsReceivableCommitment = fastFundsReceivableCommitmentDAO.findCommitmentByWorkId(workId);
				Assert.notNull(fastFundsReceivableCommitment, "Work with ID: " + workId + " has been fast funded, it should have a receivable commitment");
				fastFundsReceivableCommitment.setPending(false);
				fastFundsReceivableCommitment.setEffectiveDate(Calendar.getInstance());
			}
		}
	}

	protected void verifySufficientBuyerFunds(Work work, WorkCostDTO accountRegisterMoniesDTO, Boolean includeTotalBuyerCostToAPBalance)
			throws InsufficientFundsException {
		Assert.notNull(work);
		Assert.notNull(accountRegisterMoniesDTO);

		final Company company = work.getCompany();
		final BigDecimal apLimit = getAPLimit(company.getId());
		final BigDecimal accountsPayableBalance = getAccountsPayableBalance(company.getId());

		logger.debug(
			"accountsPayableBalance:" + accountsPayableBalance +
			" apLimit:" + apLimit +
			" difference:" + apLimit.subtract(accountsPayableBalance)
		);

		BigDecimal newAccountsPayableBalance = accountsPayableBalance;
		if (includeTotalBuyerCostToAPBalance) {
			RegisterTransaction registerTransaction = registerTransactionDAO.findPendingAuthorizationTransactionsByWorkId(work.getId());
			if (registerTransaction != null && accountRegisterMoniesDTO.getTotalBuyerCost().compareTo(registerTransaction.getAmount().abs()) != -1) {
				BigDecimal difference = accountRegisterMoniesDTO.getTotalBuyerCost().add(registerTransaction.getAmount());
				newAccountsPayableBalance = accountsPayableBalance.add(difference);
			} else {
				newAccountsPayableBalance = accountsPayableBalance.add(accountRegisterMoniesDTO.getTotalBuyerCost());
			}
		}

		validatePayableBalance(newAccountsPayableBalance, apLimit, accountRegisterMoniesDTO, company);
	}

	@Override
	protected void verifySufficientBuyerFunds(Company company, Project project, WorkCostDTO accountRegisterMoniesDTO) throws InsufficientFundsException {
		Assert.notNull(company);
		Assert.notNull(accountRegisterMoniesDTO);

		final BigDecimal apLimit = getAPLimit(company.getId());
		final BigDecimal accountsPayableBalance = getAccountsPayableBalance(company.getId());

		logger.debug(
			"accountsPayableBalance:" + accountsPayableBalance +
			" apLimit:" + apLimit +
			" difference:" + apLimit.subtract(accountsPayableBalance)
		);

		BigDecimal newAccountsPayableBalance = accountsPayableBalance.add(accountRegisterMoniesDTO.getTotalBuyerCost());
		validatePayableBalance(newAccountsPayableBalance, apLimit, accountRegisterMoniesDTO, company);
	}

	protected void validatePayableBalance(BigDecimal amount, BigDecimal apLimit, WorkCostDTO accountRegisterMoniesDTO, Company company) {
		Assert.notNull(amount);
		Assert.notNull(accountRegisterMoniesDTO);
		Assert.notNull(company);

		// -1, 0, or 1 as this BigDecimal is numerically less than, equal to, or greater than val.
		if (amount.compareTo(apLimit) == 1)
			throw new InsufficientFundsException("Buyer would have an accounts payable balance of:" + amount.toString() + " while the totalBuyerCost is:"
					+ accountRegisterMoniesDTO.getTotalBuyerCost() + " . companyId:"
					+ company.getId()
					+ "AP Limit is = $" + apLimit);
	}


	/**
	 * @param companyId
	 * @return
	 * @
	 */
	public BigDecimal calcSufficientBuyerFundsByCompany(Long companyId) {
		Assert.notNull(companyId);
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(companyId);
		return accountRegister.getAccountRegisterSummaryFields().getAccountsPayableBalance();
	}

}
