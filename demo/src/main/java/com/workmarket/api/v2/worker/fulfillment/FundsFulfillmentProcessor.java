package com.workmarket.api.v2.worker.fulfillment;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.v2.worker.marshaller.FundsMarshaller;
import com.workmarket.api.v2.worker.model.WithdrawalRequestDTO;
import com.workmarket.api.v2.worker.service.BankAccountsService;
import com.workmarket.api.v2.worker.service.FundsService;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.cache.PaymentCenterAggregateSummary;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InvalidBankAccountException;
import com.workmarket.service.exception.account.WithdrawalExceedsDailyMaximumException;
import com.workmarket.service.exception.tax.InvalidTaxEntityException;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Delegates to data pulling services and data marshallers to assemble response payloads for REST endpoints that
 * operate on user account funds
 */
@Service
public class FundsFulfillmentProcessor {
	// TODO API - what do we do with this

	@Autowired private FundsService fundsService;
	@Autowired private BankAccountsService bankAccountsService;
	@Autowired private FundsMarshaller fundsMarshaller;
	@Autowired protected MessageBundleHelper messageHelper;

	public FulfillmentPayloadDTO getFunds(final ExtendedUserDetails user) {

		final AbstractTaxEntity taxEntity = fundsService.findTaxEntity(user.getId());
		final BigDecimal availableBalance = fundsService.lookupAvailableBalanceByCompany(user.getCompanyId());
		final PaymentCenterAggregateSummary fundsSummary = fundsService.getFundsSummaryDataForUser(user.getId());

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		fundsMarshaller.marshallTaxEntityIntoGetFundsResponse(user, taxEntity, response);
		fundsMarshaller.addAvailableBalanceToGetFundsResponse(availableBalance, response);
		fundsMarshaller.marshallFundsSummaryIntoGetFundsResponse(fundsSummary, response);

		return response;
	}

	public FulfillmentPayloadDTO withdrawFunds(final ExtendedUserDetails user,
																						 final WithdrawalRequestDTO withdrawRequest) throws Exception {

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		response.setSuccessful(Boolean.FALSE);

		final List<String> validationErrors = fundsService.validateFundsWithdrawal(user);

		if (CollectionUtils.isNotEmpty(validationErrors)) {

			for (final String messageKey : validationErrors) {
				response.addResponseResult(messageHelper.getMessage(messageKey));
			}

			return response;
		}

		try {

			final Long withdrawalTransactionId = fundsService.withdrawFunds(user.getId(),
																																			withdrawRequest.getAccount(),
																																			withdrawRequest.getAmount().toString());
		}
		catch (final WithdrawalExceedsDailyMaximumException e) {

			throw new MessageSourceApiException("funds.withdraw.exceed_max",
																					ImmutableList.of(Constants.DAILY_WITHDRAWAL_LIMIT).toArray());
		}
		catch (final InsufficientFundsException e) {

			throw new MessageSourceApiException("funds.withdraw.insufficient");
		}
		catch (final InvalidBankAccountException e) {

			throw new MessageSourceApiException("funds.withdraw.invalid_account");
		}
		catch (final InvalidTaxEntityException e) {

			throw new MessageSourceApiException(Country.USA.equals(user.getCountry()) ? "funds.withdraw.no_usa_taxentity" : "funds.withdraw.no_taxentity");
		}

		final AbstractBankAccount account = bankAccountsService.getBankAccount(withdrawRequest.getAccount());

		response.addResponseResult(messageHelper.getMessage(String.format("funds.withdraw.%s.success",
																																			StringUtils.lowerCase(account.getType()))));

		response.setSuccessful(Boolean.TRUE);

		return response;
	}

	// Utility Methods to support unit test mocks
	protected void setFundsService(FundsService fundsService) {
		this.fundsService = fundsService;
	}

	protected void setBankAccountsService(BankAccountsService bankAccountsService) {
		this.bankAccountsService = bankAccountsService;
	}

	protected void setFundsMarshaller(FundsMarshaller fundsMarshaller) {
		this.fundsMarshaller = fundsMarshaller;
	}

	protected void setMessageHelper(MessageBundleHelper messageHelper) {
		this.messageHelper = messageHelper;
	}
}
