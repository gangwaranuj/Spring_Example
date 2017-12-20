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
public class TransferFundsToGeneralCash extends RegisterTransactionExecutor {
	private static final Log logger = LogFactory.getLog(TransferFundsToGeneralCash.class);

	public TransferFundsToGeneralCash() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.TRANSFER_FUNDS_TO_GENERAL);
	}

	@Override
	public void updateSummaries(RegisterTransaction wireTransaction) throws InsufficientFundsException {
		logger.debug(toString("TransferFunds to general ", wireTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = wireTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		if(accountRegisterSummaryFields.getProjectCash().compareTo(wireTransaction.getAmount()) == -1) {
			throw new InsufficientFundsException("There isn't enough project cash for transfer");
		}
		subtractProjectCash(accountRegisterSummaryFields, wireTransaction);
		addGeneralCash(accountRegisterSummaryFields, wireTransaction);
	}

	@Override
	public boolean reverse(RegisterTransaction wireTransaction) {
		// Do we need to keep this ?
		logger.debug(toString("TransferFunds can't be reversed :", wireTransaction));
		return Boolean.FALSE;
	}


}


