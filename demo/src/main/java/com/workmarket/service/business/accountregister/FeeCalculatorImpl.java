package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.RegisterTransactionCost;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class FeeCalculatorImpl implements FeeCalculator {
	@Autowired private RegisterTransactionFactory transactionFactory;
	@Autowired private PricingService pricingService;

	protected FeeCalculatorImpl() {
	}

	protected FeeCalculatorImpl(RegisterTransactionFactory transactionFactory, PricingService pricingService) {
		this.transactionFactory = transactionFactory;
		this.pricingService = pricingService;
	}

	@Override
	public TransactionBreakdown calculateWithdraw(AccountRegister register, AbstractBankAccount account, BigDecimal amount) {
		RegisterTransactionType withdrawTransactionType = transactionFactory.newRemoveFundsRegisterTransactionType(account);
		RegisterTransactionType feeTransactionType = transactionFactory.newWithdrawalFeeRegisterTransactionType(account);
		RegisterTransactionType secretFeeTransactionType = transactionFactory.newSecretWithdrawalFeeRegisterTransactionType(account);

		TransactionBreakdown breakdown = new TransactionBreakdown()
				.setGross(amount)
				.setTransactionType(withdrawTransactionType);

		if (feeTransactionType != null) {
			RegisterTransactionCost cost = pricingService.findCostForTransactionType(feeTransactionType.getCode(), register);

			BigDecimal fee;
			if (cost.isFixed()) {
				fee = cost.getFixedAmount();
			} else {
				fee = amount.multiply(cost.getPercentageAmount(), MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP);
				if (cost.hasPercentageBasedAmountLimit()) {
					fee = fee.min(cost.getPercentageBasedAmountLimit());
				}
			}

			breakdown.setFee(fee).setFeeTransactionType(feeTransactionType);
		}

		if (secretFeeTransactionType != null) {
			RegisterTransactionCost cost = pricingService.findCostForTransactionType(secretFeeTransactionType.getCode(), register);

			BigDecimal fee;
			if (cost.isFixed()) {
				fee = cost.getFixedAmount();
			} else {
				fee = amount.multiply(cost.getPercentageAmount(), MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP);
				if (cost.hasPercentageBasedAmountLimit()) {
					fee = fee.min(cost.getPercentageBasedAmountLimit());
				}
			}

			breakdown.setSecretFee(fee).setSecretFeeTransactionType(secretFeeTransactionType);
		}

		return breakdown;
	}
}
