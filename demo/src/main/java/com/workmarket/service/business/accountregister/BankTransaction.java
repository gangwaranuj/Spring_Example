/**
 *
 */
package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.BankAccountTransactionStatus;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InvalidAccountRegisterException;
import com.workmarket.service.exception.account.InvalidBankAccountException;
import com.workmarket.utility.BeanUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;

/**
 * @since 5/2/2011
 */
@Component(value = "bankAccountTransaction")
@Scope(value = "prototype")
public class BankTransaction extends RegisterTransactionExecutor implements BankAccountTransactionExecutor {

	/*
	 * Instance variables and statics
	 */
	private static final Log logger = LogFactory.getLog(BankTransaction.class);


	public BankTransaction() {
		setPending(Boolean.FALSE);
	}

	public RegisterTransactionType getRegisterTransactionType() {
		return new RegisterTransactionType("");
	}

	@Override
	public com.workmarket.domains.model.account.BankAccountTransaction executeAddFundsToRegisterFromAch(User user, AbstractBankAccount bankAccount, String amount, AccountRegister accountRegister) throws AccountRegisterConcurrentException, InvalidAccountRegisterException, InvalidBankAccountException {
		Assert.notNull(user);
		Assert.notNull(amount);

		if (accountRegister == null)
			throw new InvalidAccountRegisterException("There isn't an accountRegister for userId:" + user.getId());

		//Note, pending is true until
		com.workmarket.domains.model.account.BankAccountTransaction bankAccountTransaction = new com.workmarket.domains.model.account.BankAccountTransaction();
		bankAccountTransaction.setBankAccount(bankAccount);
		//TODO JWA There is an acknowledgement that the pending has succedded?    1055
		/*	The process consist of 3-4 steps. Constants located within the BankAccountTransactionStatus class.
		 * 	1. SUBMITTED.	Where an user triggers a bank account transaction online.
		 *  2. APPROVED.	Work Market Approval to be sent to bank 
		 *  3. REJECTED	 	Work Market Rejection, is n The bank transaction is rejected? By bank or Jeff ro proceed to file...
		 *  4. PROCESSED	The bank transaction has been acceped by the bank, and WorkMarket has received the funds. Now allocate to 
		 *  				the RegisterTransaction and AccountRegister.
		 */
		try {
			AccountRegisterSummaryFields accountRegisterSummaryFields = new AccountRegisterSummaryFields();
			BeanUtilities.copyProperties(accountRegisterSummaryFields, accountRegister.getAccountRegisterSummaryFields());

			bankAccountTransaction.setAccountRegisterSummaryFields(accountRegisterSummaryFields);

			populateTransaction(accountRegister, bankAccountTransaction, new BigDecimal(amount), new RegisterTransactionType(RegisterTransactionType.ADD_FUNDS), Boolean.TRUE);
			bankAccountTransaction.setBankAccountTransactionStatus(new BankAccountTransactionStatus(BankAccountTransactionStatus.SUBMITTED));
			registerTransactionDAO.saveOrUpdate(bankAccountTransaction);
			return bankAccountTransaction;
		} catch (org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException | org.springframework.dao.CannotAcquireLockException | org.hibernate.exception.LockAcquisitionException | org.hibernate.exception.DataException holfe) {
			logger.warn(holfe.getMessage());
		} catch (org.hibernate.StaleObjectStateException sose) {
			logger.warn(sose.getMessage());
		}

		try {
			bankAccountTransaction.setAccountRegisterSummaryFields(accountRegister.getAccountRegisterSummaryFields());
			populateTransaction(accountRegister, bankAccountTransaction, new BigDecimal(amount), new RegisterTransactionType(RegisterTransactionType.ADD_FUNDS), Boolean.TRUE);
			bankAccountTransaction.setBankAccountTransactionStatus(new BankAccountTransactionStatus(BankAccountTransactionStatus.SUBMITTED));
			registerTransactionDAO.saveOrUpdate(bankAccountTransaction);
			return bankAccountTransaction;
		} catch (org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException | org.springframework.dao.CannotAcquireLockException | org.hibernate.exception.LockAcquisitionException | org.hibernate.exception.DataException holfe) {
			logger.warn(holfe.getMessage());
		} catch (org.hibernate.StaleObjectStateException sose) {
			logger.warn(sose.getMessage());
		}

		throw new AccountRegisterConcurrentException();

	}

	@Override
	public com.workmarket.domains.model.account.BankAccountTransaction executeRemove(AbstractBankAccount bankAccount, AccountRegister accountRegister, BigDecimal removeAmount, RegisterTransactionType registerTransactionType) throws AccountRegisterConcurrentException {

		if (accountRegister.getAccountRegisterSummaryFields().getWithdrawableCash().compareTo(removeAmount) == -1)
			throw new InsufficientFundsException();

		try {
			com.workmarket.domains.model.account.BankAccountTransaction bankAccountTransaction = new com.workmarket.domains.model.account.BankAccountTransaction();
			populateTransaction(accountRegister, bankAccountTransaction, removeAmount.abs().multiply(new BigDecimal(-1)), registerTransactionType, Boolean.FALSE);
			bankAccountTransaction.setBankAccount(bankAccount);
			bankAccountTransaction.setBankAccountTransactionStatus(new BankAccountTransactionStatus(BankAccountTransactionStatus.SUBMITTED));
			updateRemoveWithdrawableSummaries(bankAccountTransaction);

			AccountRegisterSummaryFields accountRegisterSummaryFields = new AccountRegisterSummaryFields();
			BeanUtilities.copyProperties(accountRegisterSummaryFields, bankAccountTransaction.getAccountRegister().getAccountRegisterSummaryFields());
			bankAccountTransaction.setAccountRegisterSummaryFields(accountRegisterSummaryFields);

			registerTransactionDAO.saveOrUpdate(bankAccountTransaction);
			return bankAccountTransaction;
		} catch (org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException | org.springframework.dao.CannotAcquireLockException | org.hibernate.exception.LockAcquisitionException | org.hibernate.exception.DataException holfe) {
			logger.warn(holfe.getMessage());
		} catch (org.hibernate.StaleObjectStateException sose) {
			logger.warn(sose.getMessage());
		}

		throw new AccountRegisterConcurrentException();
	}

	@Override
	public com.workmarket.domains.model.account.BankAccountTransaction executeAddAchVerify(BankAccount bankAccount, AccountRegister accountRegister, BigDecimal achVerifyAmount) throws AccountRegisterConcurrentException {
		try {
			com.workmarket.domains.model.account.BankAccountTransaction bankAccountTransaction = new com.workmarket.domains.model.account.BankAccountTransaction();
			populateTransaction(accountRegister, bankAccountTransaction, achVerifyAmount,
					new RegisterTransactionType(RegisterTransactionType.ACH_VERIFY), Boolean.FALSE);
			bankAccountTransaction.setBankAccount(bankAccount);
			bankAccountTransaction.setBankAccountTransactionStatus(new BankAccountTransactionStatus(BankAccountTransactionStatus.APPROVED));
			bankAccountTransaction.setAccountRegisterSummaryFields(bankAccountTransaction.getAccountRegister().getAccountRegisterSummaryFields());

			registerTransactionDAO.saveOrUpdate(bankAccountTransaction);
			return bankAccountTransaction;
		} catch (org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException | org.hibernate.StaleObjectStateException | org.springframework.dao.CannotAcquireLockException | org.hibernate.exception.LockAcquisitionException | org.hibernate.exception.DataException holfe) {
			logger.warn(holfe.getMessage());
		}

		throw new AccountRegisterConcurrentException();
	}

	/**
	 * @param registerTransaction
	 */
	protected void updateRemoveWithdrawableSummaries(RegisterTransaction registerTransaction) {
		logger.debug(toString("BankAccountTransaction remove earned funds ", registerTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = registerTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setAvailableCash(accountRegisterSummaryFields.getAvailableCash().add(registerTransaction.getAmount()));
		accountRegisterSummaryFields.setActualCash(accountRegisterSummaryFields.getActualCash().add(registerTransaction.getAmount()));
		accountRegisterSummaryFields.setWithdrawableCash(accountRegisterSummaryFields.getWithdrawableCash().add(registerTransaction.getAmount()));
		accountRegisterSummaryFields.setGeneralCash(accountRegisterSummaryFields.getGeneralCash().add(registerTransaction.getAmount()));
	}

	public void reverseWithdrawableSummaries(RegisterTransaction registerTransaction) {
		logger.debug(toString("BankAccountTransaction reverse a removefunds ", registerTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = registerTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setAvailableCash(accountRegisterSummaryFields.getAvailableCash().subtract(registerTransaction.getAmount()));
		accountRegisterSummaryFields.setActualCash(accountRegisterSummaryFields.getActualCash().subtract(registerTransaction.getAmount()));
		accountRegisterSummaryFields.setWithdrawableCash(accountRegisterSummaryFields.getWithdrawableCash().subtract(registerTransaction.getAmount()));
		accountRegisterSummaryFields.setGeneralCash(accountRegisterSummaryFields.getGeneralCash().subtract(registerTransaction.getAmount()));
		registerTransaction.setAccountRegisterSummaryFields(accountRegisterSummaryFields);
		registerTransactionDAO.saveOrUpdate(registerTransaction);
	}

	@Override
	public void executeAddFundsApprovalAccountRegister(com.workmarket.domains.model.account.BankAccountTransaction bankAccountTransaction) {
		//called from BankingFileGenerationServiceImpl.markBankAccountTransactionNonPending
		updateSummaries(bankAccountTransaction);
	}

	@Override
	public void updateSummaries(RegisterTransaction registerTransaction) {
		logger.debug(toString("BankAccountTransaction Funds added by", registerTransaction));
		AccountRegisterSummaryFields accountRegisterSummaryFields = registerTransaction.getAccountRegister().getAccountRegisterSummaryFields();
		accountRegisterSummaryFields.setDepositedCash(accountRegisterSummaryFields.getDepositedCash().add(registerTransaction.getAmount()));
		updateActualCashAndAvailableCash(accountRegisterSummaryFields, registerTransaction);
		registerTransaction.setAccountRegisterSummaryFields(accountRegisterSummaryFields);
	}

	@Override
	public boolean reverse(RegisterTransaction wireTransaction) {
		logger.debug(toString("BankAccountTransaction can't be reversed :", wireTransaction));
		return Boolean.FALSE;
	}

}
