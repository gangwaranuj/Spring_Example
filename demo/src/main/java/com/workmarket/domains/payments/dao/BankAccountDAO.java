package com.workmarket.domains.payments.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountPagination;

import java.util.Calendar;
import java.util.List;

public interface BankAccountDAO extends DAOInterface<AbstractBankAccount> {
	BankAccountPagination find(Long companyId, BankAccountPagination pagination);

	int countGccBankAccounts(Calendar date);

	List<BankAccount> getAllBankAccountsFrom(Calendar fromCreatedOnDate);

	List<BankAccount> getAllBankAccountsFromModifiedDate(Calendar fromModifiedOnDate);
}