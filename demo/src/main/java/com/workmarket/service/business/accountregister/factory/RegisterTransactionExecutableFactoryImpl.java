package com.workmarket.service.business.accountregister.factory;

import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.CreditMemo;
import com.workmarket.domains.model.invoice.InvoiceCollection;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.service.business.accountregister.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Author: rocio
 */
@Component
public class RegisterTransactionExecutableFactoryImpl implements RegisterTransactionExecutableFactory {

	@Autowired private AddFunds addFunds;
	@Qualifier("removeFunds") @Autowired private RemoveFunds removeFunds;
	@Autowired private RemoveFundsPayPal removeFundsPayPal;
	@Autowired private TransferFundsToProjectCash transferFundsToProject;
	@Autowired private TransferFundsToGeneralCash transferFundsToGeneral;
	@Autowired private AddFundsToProject addFundsToProject;
	@Autowired private AddFundsToGeneral addFundsToGeneral;
	@Autowired private RemoveFundsFromGeneralCash removeFundsFromGeneral;
	@Autowired private RemoveFundsFromProjectCash removeFundsFromProject;
	@Autowired private SubscriptionVORTransaction subscriptionVORTransaction;
	@Autowired private SubscriptionSetupFeeTransaction subscriptionSetupFeeTransaction;
	@Autowired private SubscriptionPeriodPaymentTransaction subscriptionPeriodPaymentTransaction;
	@Autowired private SubscriptionAddOnTransaction subscriptionAddOnTransaction;
	@Autowired private SubscriptionDiscountTransaction subscriptionDiscountTransaction;
	@Autowired private CreditCardFee creditCardFee;
	@Autowired private AmexCreditCardFee amexCreditCardFee;
	@Qualifier("backgroundCheckExec") @Autowired private BackgroundCheck backgroundCheck;
	@Autowired private BackgroundCheckInternational backgroundCheckInternational;
	@Autowired private AdHocServiceInvoicePayment adHocServiceInvoicePayment;
	@Autowired private SubscriptionInvoicePayment subscriptionInvoicePayment;
	@Autowired private CreditRegisterTransaction creditRegisterTransaction;
	@Autowired private CreditMemoRegisterTransaction creditMemoRegisterTransaction;
	@Autowired private DebitRegisterTransaction debitRegisterTransaction;
	@Autowired private ServiceFeeDepositReturn serviceFeeDepositReturn;
	@Autowired private ServiceFeeWithdrawalReturn serviceFeeWithdrawalReturn;
	@Autowired private ServiceFeeLatePayment serviceFeeLatePayment;
	@Autowired private ServiceFeeMiscellaneous serviceFeeMiscellaneous;
	@Autowired private DrugTest drugTest;
	@Autowired private BuyerWorkPayment buyerWorkPayment;
	@Autowired private CancelFee cancelFee;
	@Autowired private CancelPayment cancelPayment;
	@Autowired private ResourceWorkPayment resourceWorkPayment;
	@Autowired private FinishedWorkFeeLane2 finishedWorkFeeLane2;
	@Autowired private FinishedWorkFeeLane3 finishedWorkFeeLane3;
	@Autowired private BankTransaction bankAccountTransaction;
	@Qualifier("payPalAccountTransaction") @Autowired private PayPalAccountTransaction payPalAccountTransaction;
	@Autowired private InvoicePayment invoicePayment;
	@Autowired private AssignmentBatchPayment assignmentBatchPayment;
	@Autowired private PayPalFee payPalFee;
	@Autowired private PayPalFeeCanada payPalFeeCanada;
	@Autowired private PayPalFeeIntl payPalFeeIntl;
	@Autowired private WMToPayPalFee wmToPayPalFee;
	@Autowired private WMToPayPalFeeCanada wmToPayPalFeeCanada;
	@Autowired private WMToPayPalFeeIntl wmToPayPalFeeIntl;
	@Autowired private ResourceAuthorizationPaymentTermsWorkPayment resourceAuthorizationPaymentTermsWorkPayment;
	@Autowired private ResourceAuthorizationImmediateWorkPayment resourceAuthorizationImmediateWorkPayment;
	@Autowired private BuyerAuthorizationPaymentTermsWorkPayment buyerAuthorizationPaymentTermsWorkPayment;
	@Autowired private BuyerAuthorizationImmediateWorkPayment buyerAuthorizationImmediateWorkPayment;
	@Autowired private BuyerAuthorizationPaymentTermsWorkBundlePayment buyerAuthorizationPaymentTermsWorkBundlePayment;
	@Autowired private BuyerAuthorizationImmediateWorkBundlePayment buyerAuthorizationImmediateWorkBundlePayment;
	@Autowired private FastFundsFeeTransaction fastFundsFeeTransaction;
	@Autowired private FastFundsPaymentTransaction fastFundsPaymentTransaction;
	@Autowired private FastFundsDebitTransaction fastFundsDebitTransaction;
	@Autowired private ResourceOfflineWorkPayment resourceOfflineWorkPayment;
	@Autowired private BuyerOfflineWorkPayment buyerOfflineWorkPayment;

	@Override
	public RegisterTransactionExecutor newInstance(String transactionType) {
		Assert.hasText(transactionType);

		switch (transactionType) {
			case RegisterTransactionType.ADD_FUNDS:
				return addFunds;
			case RegisterTransactionType.REMOVE_FUNDS:
				return removeFunds;
			case RegisterTransactionType.REMOVE_FUNDS_PAYPAL:
				return removeFundsPayPal;
			case RegisterTransactionType.TRANSFER_FUNDS_TO_PROJECT:
				return transferFundsToProject;
			case RegisterTransactionType.TRANSFER_FUNDS_TO_GENERAL:
				return transferFundsToGeneral;
			case RegisterTransactionType.ADD_FUNDS_TO_PROJECT:
				return addFundsToProject;
			case RegisterTransactionType.REMOVE_FUNDS_FROM_PROJECT:
				return removeFundsFromProject;
			case RegisterTransactionType.ADD_FUNDS_TO_GENERAL:
				return addFundsToGeneral;
			case RegisterTransactionType.REMOVE_FUNDS_FROM_GENERAL:
				return removeFundsFromGeneral;
			case RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT:
				return subscriptionVORTransaction;
			case RegisterTransactionType.SUBSCRIPTION_SETUP_FEE_PAYMENT:
				return subscriptionSetupFeeTransaction;
			case RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT:
				return subscriptionPeriodPaymentTransaction;
			case RegisterTransactionType.SUBSCRIPTION_ADD_ON_PAYMENT:
				return subscriptionAddOnTransaction;
			case RegisterTransactionType.SUBSCRIPTION_DISCOUNT:
				return subscriptionDiscountTransaction;
			case RegisterTransactionType.CREDIT_CARD_FEE:
				return creditCardFee;
			case RegisterTransactionType.AMEX_CREDIT_CARD_FEE:
				return amexCreditCardFee;
			case RegisterTransactionType.BACKGROUND_CHECK:
				return backgroundCheck;
			case RegisterTransactionType.BACKGROUND_CHECK_INTERNATIONAL:
				return backgroundCheckInternational;
			case RegisterTransactionType.AD_HOC_SERVICE_INVOICE_PAYMENT:
				return adHocServiceInvoicePayment;
			case RegisterTransactionType.SUBSCRIPTION_INVOICE_PAYMENT:
				return subscriptionInvoicePayment;
			case RegisterTransactionType.CREDIT:
				return creditRegisterTransaction;
			case RegisterTransactionType.DEBIT:
				return debitRegisterTransaction;
			case RegisterTransactionType.SERVICE_FEE_DEPOSIT_RETURN:
				return serviceFeeDepositReturn;
			case RegisterTransactionType.SERVICE_FEE_WITHDRAWAL_RETURN:
				return serviceFeeWithdrawalReturn;
			case RegisterTransactionType.SERVICE_FEE_LATE_PAYMENT:
				return serviceFeeLatePayment;
			case RegisterTransactionType.SERVICE_FEE_MISCELLANEOUS:
				return serviceFeeMiscellaneous;
			case RegisterTransactionType.DRUG_TEST:
				return drugTest;
			case RegisterTransactionType.BUYER_WORK_PAYMENT:
				return buyerWorkPayment;
			case RegisterTransactionType.CANCEL_FEE:
				return cancelFee;
			case RegisterTransactionType.CANCEL_PAYMENT:
				return cancelPayment;
			case RegisterTransactionType.RESOURCE_WORK_PAYMENT:
				return resourceWorkPayment;
			case RegisterTransactionType.NEW_WORK_LANE_2:
				return finishedWorkFeeLane2;
			case RegisterTransactionType.NEW_WORK_LANE_3:
				return finishedWorkFeeLane3;
			case RegisterTransactionType.NEW_WORK_LANE_4:
				return finishedWorkFeeLane3;
			case RegisterTransactionType.BANK_ACCOUNT_TRANSACTION:
				return bankAccountTransaction;
			case RegisterTransactionType.PAYPAL_ACCOUNT_TRANSACTION:
				return payPalAccountTransaction;
			case RegisterTransactionType.INVOICE_PAYMENT:
				return invoicePayment;
			case RegisterTransactionType.ASSIGNMENT_BATCH_PAYMENT:
				return assignmentBatchPayment;
			case RegisterTransactionType.PAY_PAL_FEE_USA:
				return payPalFee;
			case RegisterTransactionType.PAY_PAL_FEE_CANADA:
				return payPalFeeCanada;
			case RegisterTransactionType.PAY_PAL_FEE_INTL:
				return payPalFeeIntl;
			case RegisterTransactionType.WM_PAY_PAL_FEE_USA:
				return wmToPayPalFee;
			case RegisterTransactionType.WM_PAY_PAL_FEE_CANADA:
				return wmToPayPalFeeCanada;
			case RegisterTransactionType.WM_PAY_PAL_FEE_INTL:
				return wmToPayPalFeeIntl;
			case RegisterTransactionType.RESOURCE_AUTHORIZATION_PAYMENT_TERMS:
				return resourceAuthorizationPaymentTermsWorkPayment;
			case RegisterTransactionType.BUYER_AUTHORIZATION_PAYMENT_TERMS:
				return buyerAuthorizationPaymentTermsWorkPayment;
			case RegisterTransactionType.BUYER_AUTHORIZATION_IMMEDIATE:
				return buyerAuthorizationImmediateWorkPayment;
			case RegisterTransactionType.RESOURCE_AUTHORIZATION_IMMEDIATE:
				return resourceAuthorizationImmediateWorkPayment;
			case RegisterTransactionType.BUYER_AUTHORIZATION_WORK_BUNDLE_PAYMENT_TERMS_VIRTUAL_CODE:
				return buyerAuthorizationPaymentTermsWorkBundlePayment;
			case RegisterTransactionType.BUYER_AUTHORIZATION_WORK_BUNDLE_IMMEDIATE_VIRTUAL_CODE:
			 	return buyerAuthorizationImmediateWorkBundlePayment;
			case RegisterTransactionType.BUYER_AUTHORIZATION_WORK_BUNDLE_PAYMENT_TERMS:
				return buyerAuthorizationPaymentTermsWorkBundlePayment;
			case RegisterTransactionType.BUYER_AUTHORIZATION_WORK_BUNDLE_IMMEDIATE:
				return buyerAuthorizationImmediateWorkBundlePayment;
			case RegisterTransactionType.FAST_FUNDS_FEE:
				return fastFundsFeeTransaction;
			case RegisterTransactionType.FAST_FUNDS_PAYMENT:
				return fastFundsPaymentTransaction;
			case RegisterTransactionType.FAST_FUNDS_DEBIT:
				return fastFundsDebitTransaction;
			case RegisterTransactionType.CREDIT_MEMO:
				creditMemoRegisterTransaction.setRegisterTransactionType(new RegisterTransactionType(transactionType));
				return creditMemoRegisterTransaction;
			case RegisterTransactionType.CREDIT_MEMO_ITEM:
				creditMemoRegisterTransaction.setRegisterTransactionType(new RegisterTransactionType(transactionType));
				return creditMemoRegisterTransaction;
			case RegisterTransactionType.RESOURCE_OFFLINE_WORK_PAYMENT:
				return resourceOfflineWorkPayment;
			case RegisterTransactionType.BUYER_OFFLINE_WORK_PAYMENT:
				return buyerOfflineWorkPayment;
		}
		return null;
	}

	@Override
	public RegisterTransactionExecutor newInstance(LaneType laneType) {
		Assert.notNull(laneType);
		switch (laneType) {
			case LANE_0:
				return newInstance(RegisterTransactionType.NEW_WORK_LANE_0);
			case LANE_1:
				return newInstance(RegisterTransactionType.NEW_WORK_LANE_1);
			case LANE_2:
				return newInstance(RegisterTransactionType.NEW_WORK_LANE_2);
			case LANE_4:
				return newInstance(RegisterTransactionType.NEW_WORK_LANE_4);
			default:
				return newInstance(RegisterTransactionType.NEW_WORK_LANE_3);
		}
	}

	@SuppressWarnings("unchecked") @Override
	public <T extends RegisterTransactionExecutor> T newInvoicePaymentRegisterTransaction(AbstractInvoice invoice) {
		if (invoice instanceof InvoiceCollection) {
			return (T) newInstance(RegisterTransactionType.ASSIGNMENT_BATCH_PAYMENT);
		} else if (invoice instanceof SubscriptionInvoice) {
			return (T) newInstance(RegisterTransactionType.SUBSCRIPTION_INVOICE_PAYMENT);
		} else if (invoice instanceof AdHocInvoice) {
			return (T) newInstance(RegisterTransactionType.AD_HOC_SERVICE_INVOICE_PAYMENT);
		} else if (invoice instanceof CreditMemo) {
			return (T) newInstance(RegisterTransactionType.CREDIT_MEMO);
		}
		return (T) newInstance(RegisterTransactionType.INVOICE_PAYMENT);
	}
}
