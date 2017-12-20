package com.workmarket.domains.payments.service;

import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountPagination;
import com.workmarket.domains.payments.model.BankAccountDTO;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface BankingService {
	List<Long> getAllIds();
	AbstractBankAccount saveBankAccount(Long userId, BankAccountDTO bankAccountDTO);

	boolean confirmBankAccount(Long bankAccountId, int amount1, int amount2, Long companyId) throws Exception;

	<T extends AbstractBankAccount> T confirmGCCAccount(Long id);

	<T extends AbstractBankAccount> T deactivateBankAccount(Long bankAccountId, Long companyId);

	<T extends AbstractBankAccount> T findBankAccount(Long bankAccountId);

	BankAccountPagination findBankAccounts(Long userId, BankAccountPagination pagination);

	List<? extends AbstractBankAccount> findBankAccounts(Long userId);

	List<? extends AbstractBankAccount> findBankAccountsByCompany(Long companyId);

	List<? extends AbstractBankAccount> findConfirmedBankAccounts(Long userId);

	List<BankAccount> getAllBankAccountsFrom(Calendar fromCreatedOnDate);
	List<BankAccount> getAllBankAccountsFromModifiedOn(Calendar fromModifiedOnDate);

	void saveOrUpdate(BankAccount account);

	BankAccountPagination findAllUnConfirmedGccAccounts(BankAccountPagination pagination);

	List<? extends AbstractBankAccount> findACHBankAccounts(Long userId);

	List<? extends AbstractBankAccount> findConfirmedACHBankAccounts(Long userId);

	List<String> getUnobfuscatedAccountNumbers(final List<? extends AbstractBankAccount> accounts);

	boolean hasPayPalAccount(Long userId);

	boolean hasGCCAccount(Long userId);

	boolean updateAutoWithdrawSettings(Long id, Long companyId, Boolean autoWithdraw);

	List<Map<String, Object>> findFieldsForAutoWithdrawalAccounts();

	BankAccountPagination findAllActiveGlobalCashCardAccounts(BankAccountPagination pagination);
}
