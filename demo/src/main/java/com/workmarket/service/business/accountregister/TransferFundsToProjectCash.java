package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.service.exception.account.InsufficientFundsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Date: 5/3/13
 */
@Component
@Scope(value = "prototype")
public class TransferFundsToProjectCash extends RegisterTransactionExecutor {
	private static final Log logger = LogFactory.getLog(TransferFundsToProjectCash.class);


	public TransferFundsToProjectCash() {
		setPending(Boolean.FALSE);
	}

	@Override
	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.TRANSFER_FUNDS_TO_PROJECT);
	}

	@Override
	public void updateSummaries(RegisterTransaction wireTransaction) throws InsufficientFundsException {
		logger.debug(toString("TransferFunds to project ", wireTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = wireTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		if(accountRegisterSummaryFields.getGeneralCash().compareTo(wireTransaction.getAmount()) == -1) {
			throw new InsufficientFundsException("There isn't enough general cash for transfer");
		}
		addProjectCash(accountRegisterSummaryFields, wireTransaction);
		subtractGeneralCash(accountRegisterSummaryFields, wireTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction wireTransaction) {
		// Do we need to keep this ?
		logger.debug(toString("TransferFunds can't be reversed :", wireTransaction));
		return Boolean.FALSE;
	}


}


