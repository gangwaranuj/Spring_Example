package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.*;
import com.workmarket.service.exception.account.InsufficientFundsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class RemoveFundsFromGeneralCash extends RegisterTransactionExecutor {
	private static final Log logger = LogFactory.getLog(TransferFundsToGeneralCash.class);


	public RemoveFundsFromGeneralCash() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.REMOVE_FUNDS_FROM_GENERAL);
	}

	@Override
	public void updateSummaries(RegisterTransaction wireTransaction) throws InsufficientFundsException {
		logger.debug(toString("Remove funds from general ", wireTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = wireTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		if(accountRegisterSummaryFields.getGeneralCash().compareTo(wireTransaction.getAmount().abs()) == -1) {
			throw new InsufficientFundsException("There isn't enough general cash");
		}
		subtractGeneralCash(accountRegisterSummaryFields, wireTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction wireTransaction) {
		logger.debug(toString("Generalcash reversed by", wireTransaction));
		GeneralTransaction generalTransaction = (GeneralTransaction) wireTransaction;
		AccountRegisterSummaryFields accountRegisterSummaryFields = generalTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setGeneralCash(accountRegisterSummaryFields.getGeneralCash().add(generalTransaction.getAmount().abs()));
		setPending(Boolean.FALSE);
		return Boolean.TRUE;
	}
}
