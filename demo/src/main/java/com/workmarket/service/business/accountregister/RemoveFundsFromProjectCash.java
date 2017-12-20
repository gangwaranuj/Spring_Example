package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.ProjectTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.service.exception.account.InsufficientFundsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Date: 5/9/13
 * Time: 9:06 PM
 */
@Component
@Scope(value = "prototype")
public class RemoveFundsFromProjectCash extends RegisterTransactionExecutor {
	private static final Log logger = LogFactory.getLog(TransferFundsToGeneralCash.class);

	public RemoveFundsFromProjectCash() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.REMOVE_FUNDS_FROM_PROJECT);
	}

	@Override
	public void updateSummaries(RegisterTransaction wireTransaction) throws InsufficientFundsException {
		logger.debug(toString("Remove funds from project ", wireTransaction));
		ProjectTransaction projectTransaction = (ProjectTransaction) wireTransaction;
		Project project = projectTransaction.getProject();
		AccountRegisterSummaryFields accountRegisterSummaryFields = projectTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		if(accountRegisterSummaryFields.getProjectCash().compareTo(projectTransaction.getAmount().abs()) == -1 || project.getReservedFunds().compareTo(projectTransaction.getAmount().abs()) == -1) {
			throw new InsufficientFundsException("There isn't enough project cash");
		}
		subtractProjectCash(accountRegisterSummaryFields, projectTransaction);
		project.setReservedFunds(project.getReservedFunds().subtract(projectTransaction.getAmount().abs()));
	}

	@Override
	public boolean reverse(RegisterTransaction wireTransaction) {
		logger.debug(toString("ProjectCash reversed by", wireTransaction));
		ProjectTransaction projectTransaction = (ProjectTransaction) wireTransaction;
		AccountRegisterSummaryFields accountRegisterSummaryFields = projectTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setProjectCash(accountRegisterSummaryFields.getProjectCash().add(projectTransaction.getAmount().abs()));
		Project project = projectTransaction.getProject();
		project.setReservedFunds(project.getReservedFunds().add(projectTransaction.getAmount().abs()));
		setPending(Boolean.FALSE);
		return Boolean.TRUE;
	}
}
