package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.ProjectTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.work.model.project.Project;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Date: 5/9/13
 * Time: 9:02 PM
 */
@Component
@Scope(value = "prototype")
public class AddFundsToProject extends RegisterTransactionExecutor {
	private static final Log logger = LogFactory.getLog(TransferFundsToProjectCash.class);


	public AddFundsToProject() {
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType(RegisterTransactionType.ADD_FUNDS_TO_PROJECT);
	}

	@Override
	public void updateSummaries(RegisterTransaction wireTransaction) {
		if(!wireTransaction.getPendingFlag()){
			logger.debug(toString("Add Funds to project ", wireTransaction));
			ProjectTransaction projectTransaction = (ProjectTransaction) wireTransaction;
			AccountRegisterSummaryFields accountRegisterSummaryFields = projectTransaction.getAccountRegister().getAccountRegisterSummaryFields();
			addProjectCash(accountRegisterSummaryFields, projectTransaction);
			Project project = projectTransaction.getProject();
			project.setReservedFunds(project.getReservedFunds().add(projectTransaction.getAmount()));

		}
	}

	@Override
	public boolean reverse(RegisterTransaction wireTransaction) {
		logger.debug(toString("Add funds can't be reversed :", wireTransaction));
		return Boolean.FALSE;
	}

}
