package com.workmarket.domains.payments.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccountPagination;


public interface GlobalCashCardAccountDAO extends DAOInterface<AbstractBankAccount> {
	String findAccountNumber(Long id);
	BankAccountPagination find(BankAccountPagination pagination);
}
