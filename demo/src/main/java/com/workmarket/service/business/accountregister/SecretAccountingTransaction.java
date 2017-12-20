package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.service.exception.account.InsufficientFundsException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public abstract class SecretAccountingTransaction extends RegisterTransactionExecutor {
	public SecretAccountingTransaction() {
		setPending(Boolean.FALSE);
	}

	@Override
	public void updateSummaries(RegisterTransaction registerTransaction) throws InsufficientFundsException {
		// noop
	}

	@Override
	public boolean reverse(RegisterTransaction registerTransaction) {
		return Boolean.FALSE;
	}

	@Override
	abstract public RegisterTransactionType getRegisterTransactionType();
}
