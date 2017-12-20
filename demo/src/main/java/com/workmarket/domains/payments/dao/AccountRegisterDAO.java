package com.workmarket.domains.payments.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.AccountRegister;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRegisterDAO extends DAOInterface<AccountRegister>{

	AccountRegister findByCompanyId(Long companyId);

	AccountRegister findByCompanyId(Long companyId, boolean lockForWriting);

	AccountRegister findByCompanyNumber(String companyNumber);

	List<Long> findAllIds();

	AccountRegister findById(Long accountRegisterId);

	BigDecimal calcRemainingAPBalance(Long companyId);

	BigDecimal getPaymentSummation(Long companyId);

	BigDecimal getCurrentWorkFeePercentage(Long companyId);

	BigDecimal getAccountsPayableBalance(Long companyId);

	BigDecimal getAPLimit(Long companyId);
}
