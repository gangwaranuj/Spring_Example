package com.workmarket.api.v2.worker.fulfillment;

import com.workmarket.api.v2.worker.marshaller.BankAccountsMarshaller;
import com.workmarket.api.v2.worker.service.BankAccountsService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.web.exceptions.HttpException403;
import com.workmarket.web.exceptions.HttpException404;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountsFulfillmentProcessor {
	// TODO API - what do we do with this

	@Autowired private BankAccountsService bankAccountsService;
	@Autowired private BankAccountsMarshaller bankAccountsMarshaller;

	public FulfillmentPayloadDTO getBankAccount(final Long bankAccountId, final Long companyId) {

		if (bankAccountId <= 0) {
			throw new IllegalArgumentException("A Non-valid account id was passed.");
		}

		final AbstractBankAccount bankAccount = bankAccountsService.getBankAccount(bankAccountId);
		if (bankAccount == null) {
			throw new HttpException404("No bank account found with id : " + bankAccountId);
		}
		if (!companyId.equals(bankAccount.getCompany().getId())) {
			throw new HttpException403("Invalid user access");
		}

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		bankAccountsMarshaller.marshallBankServiceAccountResponse(bankAccount, response);

		return response;
	}

	public FulfillmentPayloadDTO getBankAccounts(final ExtendedUserDetails user) {

		FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		if (user == null || user.getId() <= 0) {
			return response;
		}

		final List<AbstractBankAccount> accounts = bankAccountsService.getBankAccountsForUser(user.getId());

		bankAccountsMarshaller.marshallBankServiceAccountListResponse(accounts, response);

		return response;
	}

	protected void setBankAccountsService(BankAccountsService bankAccountsService) {
		this.bankAccountsService = bankAccountsService;
	}


	protected void setBankAccountsMarshaller(BankAccountsMarshaller bankAccountsMarshaller) {
		this.bankAccountsMarshaller = bankAccountsMarshaller;
	}
}
