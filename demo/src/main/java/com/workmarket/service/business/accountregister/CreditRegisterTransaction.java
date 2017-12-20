package com.workmarket.service.business.accountregister;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
@Scope(value = "prototype")
public class CreditRegisterTransaction extends RegisterTransactionExecutor implements RegisterTransactionValidator {

	private static final Log logger = LogFactory.getLog(CreditRegisterTransaction.class);
	private static final List<String> WITHDRAWABLE_TRANSACTIONS;

	static {
		WITHDRAWABLE_TRANSACTIONS = Lists.newArrayList(
				RegisterTransactionType.CREDIT_ACH_WITHDRAWABLE_RETURN,
				RegisterTransactionType.CREDIT_BACKGROUND_CHECK_REFUND,
				RegisterTransactionType.CREDIT_DRUG_TEST_REFUND,
				RegisterTransactionType.CREDIT_MARKETING_PAYMENT,
				RegisterTransactionType.CREDIT_RECLASS_TO_AVAILABLE_TO_WITHDRAWAL,
				RegisterTransactionType.CREDIT_FAST_FUNDS,
				RegisterTransactionType.CREDIT_FAST_FUNDS_FEE_REFUND,
				RegisterTransactionType.CREDIT_GENERAL_REFUND,
				RegisterTransactionType.CREDIT_MISCELLANEOUS,
				RegisterTransactionType.CREDIT_MEMO);
	}
	private RegisterTransactionType registerTransactionType;

	public CreditRegisterTransaction() {
		setPending(false);
	}

	@Override
	public void updateSummaries(RegisterTransaction registerTransaction) {
		if(registerTransaction.getRegisterTransactionType().getCode().equals(RegisterTransactionType.CREDIT_ADJUSTMENT)) {
			return;
		}

		AccountRegisterSummaryFields accountRegisterSummaryFields = registerTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		Assert.notNull(registerTransactionType);

		if (WITHDRAWABLE_TRANSACTIONS.contains(registerTransactionType.getCode())) {
			accountRegisterSummaryFields.setWithdrawableCash(accountRegisterSummaryFields.getWithdrawableCash().add(registerTransaction.getAmount()));
		} else {
			accountRegisterSummaryFields.setDepositedCash(accountRegisterSummaryFields.getDepositedCash().add(registerTransaction.getAmount()));
		}
		updateActualCashAndAvailableCash(accountRegisterSummaryFields, registerTransaction);
		addGeneralCash(accountRegisterSummaryFields, registerTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		return false;
	}

	@Override
	public boolean validateRegisterTransactionType(String registerTransactionTypeCode) {
		if (StringUtils.isBlank(registerTransactionTypeCode)) {
			logger.error("[creditTransaction] Empty register transaction code");
			return false;
		}
		return RegisterTransactionType.CREDIT_REGISTER_TRANSACTION_TYPE_CODES.contains(registerTransactionTypeCode);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return registerTransactionType;
	}

	public void setRegisterTransactionType(RegisterTransactionType registerTransactionType) {
		this.registerTransactionType = registerTransactionType;
	}
}
