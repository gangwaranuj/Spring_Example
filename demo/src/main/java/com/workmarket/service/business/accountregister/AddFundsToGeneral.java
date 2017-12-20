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
 * Date: 5/9/13
 * Time: 9:06 PM
 */
@Component
@Scope(value = "prototype")
public class AddFundsToGeneral extends RegisterTransactionExecutor {
	private static final Log logger = LogFactory.getLog(TransferFundsToGeneralCash.class);


	public AddFundsToGeneral() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.ADD_FUNDS_TO_GENERAL);
	}

	@Override
	public void updateSummaries(RegisterTransaction wireTransaction) throws InsufficientFundsException {
		if(!wireTransaction.getPendingFlag()){
			logger.debug(toString("Add funds to general ", wireTransaction));
			AccountRegisterSummaryFields accountRegisterSummaryFields = wireTransaction.getAccountRegister().getAccountRegisterSummaryFields();
			addGeneralCash(accountRegisterSummaryFields, wireTransaction);
		}
	}

	@Override
	public boolean reverse(RegisterTransaction wireTransaction) {
		logger.debug(toString("Add funds can't be reversed :", wireTransaction));
		return Boolean.FALSE;
	}
}
