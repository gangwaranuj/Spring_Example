/**
 *
 */
package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.service.exception.account.InsufficientFundsException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class DebitRegisterTransaction extends RegisterTransactionExecutor implements RegisterTransactionValidator {

	private static final Log logger = LogFactory.getLog(DebitRegisterTransaction.class);

	private RegisterTransactionType registerTransactionType;

	public DebitRegisterTransaction() {
		setPending(false);
	}

	@Override
	public void updateSummaries(RegisterTransaction registerTransaction) {
		if(registerTransaction.getRegisterTransactionType().getCode().equals(RegisterTransactionType.DEBIT_ADJUSTMENT)) {
			return;
		}

		AccountRegisterSummaryFields accountRegisterSummaryFields = registerTransaction.getAccountRegister().getAccountRegisterSummaryFields();

		if (registerTransaction.getAmount().abs().compareTo(accountRegisterSummaryFields.getAvailableCash()) == 1)
			throw new InsufficientFundsException("There isn't enough available cash for a credit transaction...");

		updateActualCashAndAvailableCash(accountRegisterSummaryFields, registerTransaction);
		updateDepositedAndWithdrawableCash(accountRegisterSummaryFields, registerTransaction);
		addGeneralCash(accountRegisterSummaryFields, registerTransaction);

	}

	@Override
	public boolean reverse(RegisterTransaction workResourceTransaction) {
		return false;
	}

	@Override
	public boolean validateRegisterTransactionType(String registerTransactionTypeCode) {
		return StringUtils.isNotBlank(registerTransactionTypeCode) && RegisterTransactionType.DEBIT_REGISTER_TRANSACTION_TYPE_CODES.contains(registerTransactionTypeCode);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return registerTransactionType;
	}

	public void setRegisterTransactionType(RegisterTransactionType registerTransactionType) {
		this.registerTransactionType = registerTransactionType;
	}
}
