package com.workmarket.api.v2.worker.service;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.cache.PaymentCenterAggregateSummary;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InvalidBankAccountException;
import com.workmarket.service.exception.account.WithdrawalExceedsDailyMaximumException;
import com.workmarket.service.exception.tax.InvalidTaxEntityException;
import com.workmarket.web.controllers.mobile.MobileFundsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interacts with existing mobile "v1" API monolith controllers to obtain user funds related data and make
 * funds related processing calls. Basically a wrapper for the UGLY part of our V2 implementation. In time, this should
 * give way to service classes that call on microservices for this type of work.
 */
@Service
public class FundsService {

	@Autowired MobileFundsController fundsController;

	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	protected AccountRegisterService accountRegisterServicePrefundImpl;

	public AbstractTaxEntity findTaxEntity(final Long userId) {

		return fundsController.lookupTaxEntity(userId);
	}

	public BigDecimal lookupAvailableBalanceByCompany(final Long companyId) {

		return fundsController.getAvailableBalance(companyId);
	}

	public PaymentCenterAggregateSummary getFundsSummaryDataForUser(final Long userId) {

		return fundsController.getSellerSums(userId);
	}

	public List<String> validateFundsWithdrawal(final ExtendedUserDetails user) {

		return fundsController.validateFundsWithdrawal(user);
	}

	public Long withdrawFunds(final Long userId,
							  final Long accountId,
							  final String amount)
		throws InsufficientFundsException,
			   WithdrawalExceedsDailyMaximumException,
			   InvalidBankAccountException,
			   InvalidTaxEntityException {

		return accountRegisterServicePrefundImpl.withdrawFundsFromRegister(userId, accountId, amount);
	}
}
