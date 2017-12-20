package com.workmarket.api.v2.worker.fulfillment;

import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.v2.worker.marshaller.FundsMarshaller;
import com.workmarket.api.v2.worker.model.WithdrawalRequestDTO;
import com.workmarket.api.v2.worker.service.BankAccountsService;
import com.workmarket.api.v2.worker.service.FundsService;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.cache.PaymentCenterAggregateSummary;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.account.InvalidBankAccountException;
import com.workmarket.service.exception.account.WithdrawalExceedsDailyMaximumException;
import com.workmarket.service.exception.tax.InvalidTaxEntityException;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FundsFulfillmentProcessorTest {

    private FundsFulfillmentProcessor fulfillmentProcessor;
    @Mock private FundsService fundsService;
    @Mock private BankAccountsService bankAccountsService;
    @Mock private FundsMarshaller marshaller;
    @Mock private MessageBundleHelper messageHelper;
    private ExtendedUserDetails user;
    private AbstractTaxEntity taxEntity;
    private PaymentCenterAggregateSummary fundsSummary;
    private WithdrawalRequestDTO requestModel;

    @Before
    public void setup() {

        fundsService = mock(FundsService.class);
        bankAccountsService = mock(BankAccountsService.class);
        marshaller = mock(FundsMarshaller.class);

        fulfillmentProcessor = new FundsFulfillmentProcessor();

        fulfillmentProcessor.setFundsMarshaller(marshaller);
        fulfillmentProcessor.setFundsService(fundsService);
        fulfillmentProcessor.setBankAccountsService(bankAccountsService);

        user = new ExtendedUserDetails("TESTID", "TESTPASSWORD", CollectionUtils.EMPTY_COLLECTION);
        user.setId(9999L);
        user.setCompanyId(543876L);
        user.setCountry(Country.USA);

        taxEntity = new UsaTaxEntity();
        taxEntity.setStatus(new TaxVerificationStatusType(TaxVerificationStatusType.APPROVED));
        when
            (
                fundsService.findTaxEntity(eq(user.getId()))
            )
            .thenReturn
            (
                taxEntity
            );

        fundsSummary = new PaymentCenterAggregateSummary();
        fundsSummary.setPaidYtd(new BigDecimal(5689.98D));
        fundsSummary.setPastDue(new BigDecimal(314.00D));
        fundsSummary.setUpcomingDue(new BigDecimal(543.89D));

        when
            (
                fundsService.getFundsSummaryDataForUser(eq(user.getId()))
            )
            .thenReturn
            (
                fundsSummary
            );

        when
            (
                fundsService.lookupAvailableBalanceByCompany(eq(user.getCompanyId()))
            )
            .thenReturn
            (
                new BigDecimal(6500.43D)
            );

        when
            (
                fundsService.validateFundsWithdrawal(user)
            )
            .thenReturn
            (
                new ArrayList<String>()
            );

        requestModel = generateTestRequest();

        AbstractBankAccount bankAccount = new BankAccount();
        bankAccount.setBankAccountType(new BankAccountType(BankAccountType.CHECKING));
        bankAccount.setBankName("Thirst National Bank");
        bankAccount.setId(8274682L);
        bankAccount.setConfirmedFlag(true);
        bankAccount.setNameOnAccount("Schmedley Smallberries");

        when
            (
                bankAccountsService.getBankAccount(8274682L)
            )
            .thenReturn
            (
                bankAccount
            );

        messageHelper = mock(MessageBundleHelper.class);

        when
            (
                messageHelper.getMessage("funds.withdraw.ach.success")
            )
            .thenReturn
            (
                "Your request has been processed."
            );
        when
            (
                messageHelper.getMessage("funds.withdraw.unauthorized")
            )
            .thenReturn
            (
                "No permission."
            );
        when
            (
                messageHelper.getMessage("funds.withdraw.company_locked")
            )
            .thenReturn
            (
                "Account locked."
            );

        fulfillmentProcessor.setMessageHelper(messageHelper);
    }

    @Test
    public void getFunds_goodDate_ReturnsResponse() {

        FulfillmentPayloadDTO response = fulfillmentProcessor.getFunds(user);

        verify(fundsService, times(1))
            .findTaxEntity
			(
				eq(user.getId())
			);

        verify(fundsService, times(1))
            .getFundsSummaryDataForUser
			(
				eq(user.getId())
			);

        verify(fundsService, times(1))
            .lookupAvailableBalanceByCompany
			(
				eq(user.getCompanyId())
			);

        verify(marshaller, times(1))
            .addAvailableBalanceToGetFundsResponse
			(
				eq(new BigDecimal(6500.43D)),
				(FulfillmentPayloadDTO) anyObject()
			);

        verify(marshaller, times(1))
            .marshallFundsSummaryIntoGetFundsResponse
			(
				eq(fundsSummary),
				(FulfillmentPayloadDTO) anyObject()
			);

        verify(marshaller, times(1))
            .marshallTaxEntityIntoGetFundsResponse
			(
				eq(user),
				eq(taxEntity),
				(FulfillmentPayloadDTO) anyObject()
			);

        assertNotNull(response);
    }


    @Test
    public void withdrawFunds_goodRequest_GoodReturn() throws Exception {

        when
            (
                fundsService.withdrawFunds
                (
                    user.getId(),
                    requestModel.getAccount(),
                    requestModel.getAmount().toString()
                )
            )
            .thenReturn
            (
                5L
            );

        FulfillmentPayloadDTO response = fulfillmentProcessor.withdrawFunds(user, requestModel);

        verify(fundsService, times(1))
            .validateFundsWithdrawal(user);
        verify(fundsService, times(1))
            .withdrawFunds(user.getId(),
                           requestModel.getAccount(),
                           requestModel.getAmount().toString());
        verify(bankAccountsService, times(1))
            .getBankAccount(requestModel.getAccount());

        assertTrue(response.isSuccessful());
        assertEquals(1, response.getPayload().size());
        assertEquals("Your request has been processed.", response.getPayload().get(0));
    }

    @Test
    public void withdrawFunds_badValidation_errorReturn()
        throws Exception {

        List<String> validationMessages = new LinkedList();
        validationMessages.add("funds.withdraw.unauthorized");
        validationMessages.add("funds.withdraw.company_locked");

        when
            (
                fundsService.validateFundsWithdrawal(user)
            )
            .thenReturn
            (
                validationMessages
            );

        FulfillmentPayloadDTO response = fulfillmentProcessor.withdrawFunds(user, requestModel);

        verify(fundsService, times(1))
            .validateFundsWithdrawal(user);
        verify(fundsService, never())
            .withdrawFunds(user.getId(),
                           requestModel.getAccount(),
                           requestModel.getAmount().toString());
        verify(bankAccountsService, never())
            .getBankAccount(requestModel.getAccount());

        assertFalse(response.isSuccessful());
        assertEquals(2, response.getPayload().size());
        assertEquals("No permission.", response.getPayload().get(0));
        assertEquals("Account locked.", response.getPayload().get(1));
    }

    @Test
    public void withdrawFunds_withdrawalExceedsDailyMaximumException_messageSourceApiExceptionThrown()
        throws Exception {

        when
            (
                fundsService.withdrawFunds
                (
                    user.getId(), requestModel.getAccount(),
                    requestModel.getAmount().toString()
                )
            )
            .thenThrow
            (
                new WithdrawalExceedsDailyMaximumException()
            );

        try {

            FulfillmentPayloadDTO response = fulfillmentProcessor.withdrawFunds(user, requestModel);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
        catch (MessageSourceApiException msae) {

            assertEquals("funds.withdraw.exceed_max", msae.getMessage());
            assertEquals(Constants.DAILY_WITHDRAWAL_LIMIT, msae.getArguments()[0]);
        }
        catch (Exception e) {

            fail("Expected a MessageSourceApiException to be thrown. Instead, " + e.getClass() + " was thrown.");
        }

        verify(fundsService, times(1)).validateFundsWithdrawal(user);
        verify(fundsService, times(1)).withdrawFunds(user.getId(), requestModel.getAccount(),
                                                     requestModel.getAmount().toString());
        verify(bankAccountsService, never()).getBankAccount(requestModel.getAccount());
    }

    @Test
    public void withdrawFunds_insufficientFundsException_messageSourceApiExceptionThrown()
        throws Exception {

        when
            (
                fundsService.withdrawFunds(user.getId(),
                                           requestModel.getAccount(),
                                           requestModel.getAmount().toString())
            )
            .thenThrow
            (
                new InsufficientFundsException()
            );

        try {

            FulfillmentPayloadDTO response = fulfillmentProcessor.withdrawFunds(user, requestModel);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
        catch (MessageSourceApiException msae) {
            assertEquals("funds.withdraw.insufficient", msae.getMessage());
        }
        catch (Exception e) {
            fail("Expected a MessageSourceApiException to be thrown. Instead, " + e.getClass() + " was thrown.");
        }

        verify(fundsService, times(1)).validateFundsWithdrawal(user);
        verify(fundsService, times(1)).withdrawFunds(user.getId(), requestModel.getAccount(),
                                                     requestModel.getAmount().toString());
        verify(bankAccountsService, never()).getBankAccount(requestModel.getAccount());
    }

    @Test
    public void withdrawFunds_invalidBankAccountException_messageSourceApiExceptionThrown()
        throws Exception {

        when
            (
                fundsService.withdrawFunds(user.getId(),
                                           requestModel.getAccount(),
                                           requestModel.getAmount().toString())
            )
            .thenThrow
            (
                new InvalidBankAccountException()
            );

        try {

            FulfillmentPayloadDTO response = fulfillmentProcessor.withdrawFunds(user, requestModel);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
        catch (MessageSourceApiException msae) {

            assertEquals("funds.withdraw.invalid_account", msae.getMessage());
        }
        catch (Exception e) {

            fail("Expected a MessageSourceApiException to be thrown. Instead, " + e.getClass() + " was thrown.");
        }

        verify(fundsService, times(1)).validateFundsWithdrawal(user);
        verify(fundsService, times(1)).withdrawFunds(user.getId(), requestModel.getAccount(),
                                                     requestModel.getAmount().toString());
        verify(bankAccountsService, never()).getBankAccount(requestModel.getAccount());
    }

    @Test
    public void withdrawFunds_invalidTaxEntityException_messageSourceApiExceptionThrown()
        throws Exception {

        when
            (
                fundsService.withdrawFunds(user.getId(),
                                           requestModel.getAccount(),
                                           requestModel.getAmount().toString())
            )
            .thenThrow
            (
                new InvalidTaxEntityException()
            );

        try {

            FulfillmentPayloadDTO response = fulfillmentProcessor.withdrawFunds(user, requestModel);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
        catch (MessageSourceApiException msae) {

            assertEquals("funds.withdraw.no_usa_taxentity", msae.getMessage());
        }
        catch (Exception e) {

            fail("Expected a MessageSourceApiException to be thrown. Instead, " + e.getClass() + " was thrown.");
        }

        verify(fundsService, times(1)).validateFundsWithdrawal(user);
        verify(fundsService, times(1)).withdrawFunds(user.getId(), requestModel.getAccount(),
                                                     requestModel.getAmount().toString());
        verify(bankAccountsService, never()).getBankAccount(requestModel.getAccount());
    }

    private WithdrawalRequestDTO generateTestRequest() {

        WithdrawalRequestDTO requestModel = new WithdrawalRequestDTO.Builder()
          .withAccount(8274682L)
          .withAmount(new BigDecimal(45.00D))
          .build();

        return requestModel;
    }
}
