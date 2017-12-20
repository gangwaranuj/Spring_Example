package com.workmarket.api.v2.worker.marshaller;

import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v2.model.ApiBankAccountDTO;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountsMarshaller extends ApiMarshaller {

	public void marshallBankServiceAccountResponse(
		final AbstractBankAccount bankAccount,
		final FulfillmentPayloadDTO response) {

		if (response == null || bankAccount == null) {
			return;
		}

		response.addResponseResult(marshallBankAccountToApiModel(bankAccount));
	}

	public void marshallBankServiceAccountListResponse(
		final List<AbstractBankAccount> accounts,
		final FulfillmentPayloadDTO response) {

		if (CollectionUtils.isEmpty(accounts) || response == null) {
			return;
		}

		for (final AbstractBankAccount account : accounts) {
			response.addResponseResult(marshallBankAccountToApiModel(account));
		}
	}

	private ApiBankAccountDTO marshallBankAccountToApiModel(AbstractBankAccount bankAccount) {
		return new ApiBankAccountDTO.Builder(bankAccount).build();
	}
}
