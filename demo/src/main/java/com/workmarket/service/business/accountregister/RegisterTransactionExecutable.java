package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.exception.account.InsufficientFundsException;

import java.math.BigDecimal;

public interface RegisterTransactionExecutable {

	/**
	 * Executes a register transaction.
	 *
	 * @param work
	 * @param workResource
	 * @param accountRegister
	 * @param amount
	 * @return WorkResourceTransaction
	 */
	WorkResourceTransaction execute(Work work, WorkResource workResource, AccountRegister accountRegister, BigDecimal amount);

	/**
	 * Executes a register transaction.
	 *
	 * @param work
	 * @param workResource
	 * @param accountRegister
	 * @param amount
	 * @param updateSummaries
	 * @return WorkResourceTransaction
	 */
	WorkResourceTransaction execute(Work work, WorkResource workResource, AccountRegister accountRegister, BigDecimal amount, boolean updateSummaries);

	/**
	 * Executes a register transaction.
	 *
	 * @param work
	 * @param workResource
	 * @param accountRegister
	 * @param amount
	 * @param updateSummaries
	 * @param isBundled
	 * @param isBatchPayment
	 * @return WorkResourceTransaction
	 */
	WorkResourceTransaction execute(Work work, WorkResource workResource, AccountRegister accountRegister, BigDecimal amount, boolean updateSummaries, boolean isBundled, boolean isBatchPayment);

	/**
	 * Executes a register transaction.
	 *
	 * @param accountRegister
	 * @param amount
	 * @param registerTransaction
	 * @return RegisterTransaction
	 * @throws AccountRegisterConcurrentException
	 */
	RegisterTransaction execute(AccountRegister accountRegister, BigDecimal amount, RegisterTransaction registerTransaction) throws AccountRegisterConcurrentException;

	/**
	 * Actions that are going to be performed after executing a register transaction.
	 *
	 * @param registerTransaction
	 * @return RegisterTransaction
	 */
	RegisterTransaction onPostExecution(RegisterTransaction registerTransaction);

	void reverseSummaries(RegisterTransaction registerTransaction) throws AccountRegisterConcurrentException;

	void updateSummaries(RegisterTransaction registerTransaction) throws InsufficientFundsException;

	boolean updateAssignmentThroughputSummaries(WorkResourceTransaction workResourceTransaction);

	boolean reverse(RegisterTransaction registerTransaction);

	void updateDepositedAndWithdrawableCash(AccountRegisterSummaryFields accountRegisterSummaryFields, RegisterTransaction registerTransaction) throws InsufficientFundsException;

	void updateActualCashAndAvailableCash(AccountRegisterSummaryFields accountRegisterSummaryFields, RegisterTransaction registerTransaction);

	void addGeneralCash(AccountRegisterSummaryFields accountRegisterSummaryFields, RegisterTransaction registerTransaction);

	void addProjectCash(AccountRegisterSummaryFields accountRegisterSummaryFields, RegisterTransaction registerTransaction);

	void subtractGeneralCash(AccountRegisterSummaryFields accountRegisterSummaryFields, RegisterTransaction registerTransaction);

	void subtractProjectCash(AccountRegisterSummaryFields accountRegisterSummaryFields, RegisterTransaction registerTransaction);
}
