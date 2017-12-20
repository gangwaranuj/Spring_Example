/**
 *
 */
package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.service.exception.account.InsufficientFundsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;

/**
 * @since 5/1/2011
 */
@Component
@Scope(value = "prototype")
public class BuyerWorkPayment extends RegisterTransactionExecutor {

	private static final Log logger = LogFactory.getLog(BuyerWorkPayment.class);


	public BuyerWorkPayment() {
		setPending(Boolean.FALSE);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.BUYER_WORK_PAYMENT);
	}

	@Override
	public void updateSummaries(RegisterTransaction workResourceTransaction) throws InsufficientFundsException {
		if (((WorkResourceTransaction) workResourceTransaction).isBundlePayment()) {
			logger.debug("Not updating the summaries because of bundle payment");
			return;
		}
		logger.debug(toString("Payment added by", workResourceTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = workResourceTransaction.getAccountRegister().getAccountRegisterSummaryFields();

		/**
		 * If it's batch payment only update the actual cash.
		 * Don't update the available cash, since it was already updated by the main transaction.
		 */
		if (!((WorkResourceTransaction) workResourceTransaction).isBatchPayment()) {

			if (workResourceTransaction.getAmount().abs().compareTo(accountRegisterSummaryFields.getAvailableCash()) == 1) {
				throw new InsufficientFundsException("There isn't enough available cash for a work payment transaction...");
			}

			accountRegisterSummaryFields.setAvailableCash(accountRegisterSummaryFields.getAvailableCash().add(workResourceTransaction.getAmount()));
			updateDepositedAndWithdrawableCash(accountRegisterSummaryFields, workResourceTransaction);
		}

		accountRegisterSummaryFields.setActualCash(accountRegisterSummaryFields.getActualCash().add(workResourceTransaction.getAmount()));
	}

	@Override
	public boolean updateAssignmentThroughputSummaries(WorkResourceTransaction workResourceTransaction) {
		Assert.notNull(workResourceTransaction);
		AccountRegister buyerAccountRegister = workResourceTransaction.getAccountRegister();
		Assert.notNull(buyerAccountRegister);

		AccountRegisterSummaryFields accountRegisterSummaryFields = buyerAccountRegister.getAccountRegisterSummaryFields();
		Assert.notNull(accountRegisterSummaryFields);
		logger.info("[summaries] accountRegisterSummaryFields before " + accountRegisterSummaryFields);

		Assert.notNull(buyerAccountRegister.getCompany().getPaymentConfiguration());
		AccountPricingType companyPricingType = buyerAccountRegister.getCompany().getPaymentConfiguration().getAccountPricingType();
		Assert.notNull(companyPricingType);

		AccountPricingType workPricingType = workResourceTransaction.getWork().getAccountPricingType();
		Assert.notNull(workPricingType);

		//Updating the assignment throughput running summary
		BigDecimal throughputAmount = workResourceTransaction.getAmount().abs();

		BigDecimal newThroughputAmount = accountRegisterSummaryFields.getAssignmentThroughput().add(throughputAmount);
		accountRegisterSummaryFields.setAssignmentThroughput(newThroughputAmount);

		//Only increase the SW and VOR throughput numbers if the assignment has the same pricing type as the company
		if (companyPricingType.getCode().equals(workPricingType.getCode())) {
			BigDecimal newSWThroughputAmount = accountRegisterSummaryFields.getAssignmentSoftwareThroughput().add(throughputAmount);
			accountRegisterSummaryFields.setAssignmentSoftwareThroughput(newSWThroughputAmount);
			accountRegisterSummaryFields.setAssignmentVorThroughput(newSWThroughputAmount);
		}

		logger.info("[summaries] accountRegisterSummaryFields after " + accountRegisterSummaryFields);
		return true;
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		logger.debug(toString("Payment cant be reversed ", workResourceTransaction));
		return Boolean.FALSE;
	}

	@Override
	public RegisterTransaction onPostExecution(RegisterTransaction workResourceTransaction) {
		Assert.notNull(workResourceTransaction);
		accountPricingService.adjustSubscriptionPaymentConfigurationByThroughputAmount((WorkResourceTransaction) workResourceTransaction);
		return workResourceTransaction;
	}
}
