/**
 *
 */
package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.exception.account.InvalidAccountRegisterException;
import com.workmarket.service.exception.account.InvalidBankAccountException;

import java.math.BigDecimal;

/**
 * @since 5/15/2011
 */
public interface BankAccountTransactionExecutor {

	public BankAccountTransaction executeRemove(AbstractBankAccount bankAccount, AccountRegister accountRegister, BigDecimal removeAmount, RegisterTransactionType registerTransactionType) throws AccountRegisterConcurrentException;

	public BankAccountTransaction executeAddAchVerify(BankAccount bankAccount, AccountRegister accountRegister, BigDecimal achVerifyAmount) throws AccountRegisterConcurrentException;

	public BankAccountTransaction executeAddFundsToRegisterFromAch(User user, AbstractBankAccount bankAccount, String amount, AccountRegister accountRegister) throws AccountRegisterConcurrentException, InvalidAccountRegisterException, InvalidBankAccountException;

	public void executeAddFundsApprovalAccountRegister(BankAccountTransaction bankAccountTransaction);

	public void reverseWithdrawableSummaries(RegisterTransaction registerTransaction);

}
