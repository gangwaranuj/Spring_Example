package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.banking.AbstractBankAccount;

import java.math.BigDecimal;

public interface FeeCalculator {
	TransactionBreakdown calculateWithdraw(AccountRegister register, AbstractBankAccount account, BigDecimal amount);
}
