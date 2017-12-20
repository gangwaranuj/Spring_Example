package com.workmarket.domains.model.account;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.LookupEntity;
import com.workmarket.domains.model.postalcode.Country;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import java.util.Map;

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity(name = "register_transaction_type")
@Table(name = "register_transaction_type")
public class RegisterTransactionType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String BUYER_COMMITMENT_TO_PAY = "commitment";
	public static final String BUYER_COMMITMENT_TO_PAY_WORK_BUNDLE = "bundleAuth";
	public static final String RESOURCE_COMMITMENT_TO_RECEIVE_PAY = "paycommit";
	public static final String BUYER_WORK_PAYMENT = "payment";
	public static final String RESOURCE_WORK_PAYMENT = "wrkpayment";
	public static final String BUYER_OFFLINE_WORK_PAYMENT = "payoff";
	public static final String RESOURCE_OFFLINE_WORK_PAYMENT = "wrkpayoff";
	public static final String NEW_WORK_LANE_0 = "lane0work";
	public static final String NEW_WORK_LANE_1 = "lane1work";
	public static final String NEW_WORK_LANE_2 = "lane2work";
	public static final String NEW_WORK_LANE_3 = "lane3work";
	public static final String NEW_WORK_LANE_4 = "lane4work";
	public static final String FINISHED_WORK_FEE_LANE2 = "finwork2";
	public static final String FINISHED_WORK_FEE_LANE3 = "finwork3";
	public static final String CREDIT_CARD_FEE = "ccfee";
	public static final String AMEX_CREDIT_CARD_FEE = "amexCcFee";
	public static final String ADD_FUNDS = "addfunds";
	public static final String REMOVE_FUNDS = "removefund";
	public static final String REMOVE_FUNDS_PAYPAL = "removefpp";
	public static final String REMOVE_FUNDS_GCC = "removefgcc";
	public static final String ACH_VERIFY = "achverify";

	public static final String BACKGROUND_CHECK = "bkgrdchk";
	public static final String BACKGROUND_CHECK_CANADA = "bkgrdchkCA";
	public static final String BACKGROUND_CHECK_INTERNATIONAL = "bkgrdchkIN";


	public static final String DRUG_TEST = "drugtest";
	public static final String TRANSFER_FUNDS_TO_PROJECT = "transproj";
	public static final String TRANSFER_FUNDS_TO_GENERAL = "transgenr";
	public static final String ADD_FUNDS_TO_PROJECT = "addproj";
	public static final String REMOVE_FUNDS_FROM_PROJECT = "removeproj";
	public static final String ADD_FUNDS_TO_GENERAL = "addgenr";
	public static final String REMOVE_FUNDS_FROM_GENERAL = "removegenr";
	@Deprecated
	public static final String REFUND_CREDIT = "refund"; //Use any of the CREDIT_REGISTER_TRANSACTION_TYPE_CODES
	public static final String CANCEL_FEE = "cancelfee";
	public static final String CANCEL_PAYMENT = "cancelpay";
	@Deprecated
	public static final String REMOVE_FROM_REGISTER_AS_CASH = "refundcash"; //Use any of the DEBIT_REGISTER_TRANSACTION_TYPE_CODES
	public static final String BUYER_PAYMENT_TERMS_COMMITMENT = "pytrmscmmt";
	public static final String BUYER_PAYMENT_TERMS_COMMITMENT_WORK_BUNDLE = "bundlePPAu";
	public static final String RESOURCE_PAYMENT_TERMS_COMMITMENT_TO_RECEIVE_PAY = "pytrmspyct";
	public static final String INVOICE_PAYMENT = "invoicePay";
	public static final String ASSIGNMENT_BATCH_PAYMENT = "batchPay";

	public static final String PAY_PAL_FEE_USA = "payPalFee";
	public static final String PAY_PAL_FEE_CANADA = "payPalFeeC";
	public static final String PAY_PAL_FEE_INTL = "payPalFeeI";
	public static final String WM_PAY_PAL_FEE_USA = "wmPPFee";
	public static final String WM_PAY_PAL_FEE_CANADA = "wmPPFeeC";
	public static final String WM_PAY_PAL_FEE_INTL = "wmPPFeeI";

	//Service Invoice Payments
	public static final String SUBSCRIPTION_INVOICE_PAYMENT = "subInvoice";
	public static final String AD_HOC_SERVICE_INVOICE_PAYMENT = "adhInvoice";

	public static final String SUBSCRIPTION_SOFTWARE_FEE_PAYMENT = "subsPeriod";
	public static final String SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT = "subsVoR";
	public static final String SUBSCRIPTION_SETUP_FEE_PAYMENT = "subsSetup";
	public static final String SUBSCRIPTION_ADD_ON_PAYMENT = "subsAddOn";
	public static final String SUBSCRIPTION_DISCOUNT = "subDisc";

	public static final String BANK_ACCOUNT_TRANSACTION = "bankAccountTransaction";
	public static final String PAYPAL_ACCOUNT_TRANSACTION = "payPalAccountTransaction";

	//CREDIT types
	public static final String CREDIT_ACH_WITHDRAWABLE_RETURN = "achWithdra";
	public static final String CREDIT_ADVANCE = "creditAdva";
	public static final String CREDIT_ASSIGNMENT_PAYMENT_REVERSAL = "creditPayR";
	public static final String CREDIT_BACKGROUND_CHECK_REFUND = "bkgrdchkRe";
	public static final String CREDIT_DRUG_TEST_REFUND = "drugTestRe";
	public static final String CREDIT_MARKETING_PAYMENT = "marketingP";
	public static final String CREDIT_RECLASS_TO_AVAILABLE_TO_WITHDRAWAL = "reclassWit";
	public static final String CREDIT_FAST_FUNDS = "fastFunCR";
	public static final String CREDIT_FAST_FUNDS_FEE_REFUND = "fastFunRfd";
	public static final String CREDIT_MISCELLANEOUS = "creditMisc";
	public static final String CREDIT_ADJUSTMENT = "creditAdj";
	public static final String CREDIT_FEE_REFUND_VOR = "feeRefVor";
	public static final String CREDIT_FEE_REFUND_NVOR = "feeRefNvor";
	public static final String CREDIT_GENERAL_REFUND = "gralRefund";
	public static final String CREDIT_WIRE_DIRECT_DEPOSIT = "directDep";
	public static final String CREDIT_CHECK_DEPOSIT = "checkDep";
	public static final String CREDIT_MEMO = "creditMemo";
	public static final String CREDIT_MEMO_ITEM = "cmItem";

	//DEBIT types
	public static final String DEBIT_ACH_DEPOSIT_RETURN = "achDeposit";
	public static final String DEBIT_ADVANCE_REPAYMENT = "debitAdvan";
	public static final String DEBIT_ASSIGNMENT_PAYMENT_REVERSAL = "debitPayRe";
	public static final String DEBIT_CREDIT_CARD_CHARGEBACK = "ccChargeBk";
	public static final String DEBIT_CREDIT_CARD_REFUND = "ccRefund";
	public static final String DEBIT_RECLASS_FROM_AVAILABLE_TO_SPEND = "reclassAva";
	public static final String DEBIT_FAST_FUNDS = "fastFunDR";
	public static final String DEBIT_MISCELLANEOUS = "debitMisc";
	public static final String DEBIT_ADJUSTMENT = "debitAdj";

	//Ad-hoc invoices fees
	public static final String SERVICE_FEE_DEPOSIT_RETURN = "depositFee";
	public static final String SERVICE_FEE_WITHDRAWAL_RETURN = "withdraFee";
	public static final String SERVICE_FEE_LATE_PAYMENT = "latePayFee";
	public static final String SERVICE_FEE_MISCELLANEOUS = "miscellFee";

	//Other
	public static final String AUTHORIZATION_TRANSACTION_TYPE = "authorization";
	public static final String CREDIT = "credit";
	public static final String DEBIT = "debit";
	public static final String BUYER_AUTHORIZATION_PAYMENT_TERMS = "pendingPytrmscmmt";
	public static final String RESOURCE_AUTHORIZATION_PAYMENT_TERMS = "pendingPytrmspyct";
	public static final String BUYER_AUTHORIZATION_IMMEDIATE = "pendingCommitment";
	public static final String RESOURCE_AUTHORIZATION_IMMEDIATE = "pendingPaycommit";

	// Fast Funds

	// Worker Transactions
	public static final String FAST_FUNDS_FEE = "fastFunFee";
	public static final String FAST_FUNDS_PAYMENT = "fastFunPyt";
	public static final String FAST_FUNDS_DEBIT = "fastFunDbt";

	//Work Bundles
	//TODO: refactor this names into something more usable
	public static final String BUYER_AUTHORIZATION_WORK_BUNDLE_PAYMENT_TERMS_VIRTUAL_CODE= "pendingBundlePytrmscmmt";
	public static final String BUYER_AUTHORIZATION_WORK_BUNDLE_IMMEDIATE_VIRTUAL_CODE = "pendingBundleCommitment";

	public static final String BUYER_AUTHORIZATION_WORK_BUNDLE_PAYMENT_TERMS = "pendingBundlePPAu";
	public static final String BUYER_AUTHORIZATION_WORK_BUNDLE_IMMEDIATE = "pendingBundleAuth";

	public static final List<String> AVAILABLE_CASH_DECREASE_REGISTER_TRANSACTION_TYPES = ImmutableList.of(
			REMOVE_FUNDS,
			REMOVE_FUNDS_PAYPAL,
			REMOVE_FUNDS_GCC,
			REMOVE_FROM_REGISTER_AS_CASH,
			BUYER_WORK_PAYMENT,
			FINISHED_WORK_FEE_LANE2,
			FINISHED_WORK_FEE_LANE3,
			NEW_WORK_LANE_2,
			NEW_WORK_LANE_3,
			CREDIT_CARD_FEE,
			AMEX_CREDIT_CARD_FEE,
			BACKGROUND_CHECK,
			BACKGROUND_CHECK_CANADA,
			BACKGROUND_CHECK_INTERNATIONAL,
			DRUG_TEST,
			CANCEL_FEE,
			PAY_PAL_FEE_USA,
			PAY_PAL_FEE_CANADA,
			PAY_PAL_FEE_INTL,

			SUBSCRIPTION_SOFTWARE_FEE_PAYMENT,
			SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT,
			SUBSCRIPTION_SETUP_FEE_PAYMENT,
			SUBSCRIPTION_ADD_ON_PAYMENT,
			SUBSCRIPTION_DISCOUNT,

			SERVICE_FEE_DEPOSIT_RETURN,
			SERVICE_FEE_WITHDRAWAL_RETURN,
			SERVICE_FEE_LATE_PAYMENT,
			SERVICE_FEE_MISCELLANEOUS,

			DEBIT_ACH_DEPOSIT_RETURN,
			DEBIT_ADVANCE_REPAYMENT,
			DEBIT_ASSIGNMENT_PAYMENT_REVERSAL,
			DEBIT_CREDIT_CARD_CHARGEBACK,
			DEBIT_CREDIT_CARD_REFUND,
			DEBIT_RECLASS_FROM_AVAILABLE_TO_SPEND,
			DEBIT_FAST_FUNDS,
			DEBIT_MISCELLANEOUS,
			DEBIT_ADJUSTMENT,
			FAST_FUNDS_FEE,
			FAST_FUNDS_DEBIT);

	public static final List<String> AVAILABLE_CASH_INCREASE_REGISTER_TRANSACTION_TYPES = ImmutableList.of(
			ADD_FUNDS,
			RESOURCE_WORK_PAYMENT,
			CANCEL_PAYMENT,
			REFUND_CREDIT,
			CREDIT_ACH_WITHDRAWABLE_RETURN,
			CREDIT_ADVANCE,
			CREDIT_ASSIGNMENT_PAYMENT_REVERSAL,
			CREDIT_BACKGROUND_CHECK_REFUND,
			CREDIT_DRUG_TEST_REFUND,
			CREDIT_MARKETING_PAYMENT,
			CREDIT_RECLASS_TO_AVAILABLE_TO_WITHDRAWAL,
			CREDIT_FAST_FUNDS,
			CREDIT_FAST_FUNDS_FEE_REFUND,
			CREDIT_MISCELLANEOUS,
			CREDIT_FEE_REFUND_VOR,
			CREDIT_FEE_REFUND_NVOR,
			CREDIT_GENERAL_REFUND,
			CREDIT_WIRE_DIRECT_DEPOSIT,
			CREDIT_CHECK_DEPOSIT,
			CREDIT_MEMO,
			CREDIT_ADJUSTMENT,
			FAST_FUNDS_PAYMENT);

	public static final List<String> PAY_PAL_FEE_REGISTER_TRANSACTION_TYPES = ImmutableList.of(
			PAY_PAL_FEE_USA,
			PAY_PAL_FEE_CANADA,
			PAY_PAL_FEE_INTL);

	public static final List<String> WORK_MARKET_FEES_PAID_TO_PAY_PAL = ImmutableList.of(
			WM_PAY_PAL_FEE_USA,
			WM_PAY_PAL_FEE_CANADA,
			WM_PAY_PAL_FEE_INTL);

	public static final Map<String, String> PAYPAL_COUNTRY_TRANSACTION_CODE_MAP = ImmutableMap.of(
			Country.USA, RegisterTransactionType.PAY_PAL_FEE_USA,
			Country.CANADA, RegisterTransactionType.PAY_PAL_FEE_CANADA,
			Country.INTERNATIONAL, RegisterTransactionType.PAY_PAL_FEE_INTL
	);

	public static final List<String> SUBSCRIPTION_SOFTWARE_FEE_TRANSACTION_CODES = ImmutableList.of(
			SUBSCRIPTION_DISCOUNT,
			SUBSCRIPTION_SETUP_FEE_PAYMENT,
			SUBSCRIPTION_SOFTWARE_FEE_PAYMENT);

	public static final List<String> SUBSCRIPTION_ALL_FEES_TRANSACTION_CODES = ImmutableList.of(
			SUBSCRIPTION_ADD_ON_PAYMENT,
			SUBSCRIPTION_DISCOUNT,
			SUBSCRIPTION_SETUP_FEE_PAYMENT,
			SUBSCRIPTION_SOFTWARE_FEE_PAYMENT,
			SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT);

	public static final List<String> DEBIT_REGISTER_TRANSACTION_TYPE_CODES = ImmutableList.of(
			DEBIT_ACH_DEPOSIT_RETURN,
			DEBIT_ADVANCE_REPAYMENT,
			DEBIT_ASSIGNMENT_PAYMENT_REVERSAL,
			DEBIT_CREDIT_CARD_CHARGEBACK,
			DEBIT_CREDIT_CARD_REFUND,
			DEBIT_RECLASS_FROM_AVAILABLE_TO_SPEND,
			DEBIT_FAST_FUNDS,
			DEBIT_ADJUSTMENT,
			DEBIT_MISCELLANEOUS);

	public static final List<String> CREDIT_REGISTER_TRANSACTION_TYPE_CODES = ImmutableList.of(
			CREDIT_ACH_WITHDRAWABLE_RETURN,
			CREDIT_ADVANCE,
			CREDIT_ASSIGNMENT_PAYMENT_REVERSAL,
			CREDIT_BACKGROUND_CHECK_REFUND,
			CREDIT_DRUG_TEST_REFUND,
			CREDIT_MARKETING_PAYMENT,
			CREDIT_RECLASS_TO_AVAILABLE_TO_WITHDRAWAL,
			CREDIT_FAST_FUNDS,
			CREDIT_FAST_FUNDS_FEE_REFUND,
			CREDIT_MISCELLANEOUS,
			CREDIT_FEE_REFUND_VOR,
			CREDIT_FEE_REFUND_NVOR,
			CREDIT_GENERAL_REFUND,
			CREDIT_WIRE_DIRECT_DEPOSIT,
			CREDIT_CHECK_DEPOSIT,
			CREDIT_ADJUSTMENT);

	public static final List<String> CREDIT_REGISTER_TRANSACTION_NOTIFY_TYPE_CODES = ImmutableList.of(
			CREDIT_CHECK_DEPOSIT,
			CREDIT_WIRE_DIRECT_DEPOSIT
	);

	public static final List<String> EARNINGS_TRANSACTION_TYPE_CODES = ImmutableList.of(
			RESOURCE_WORK_PAYMENT,
			DEBIT_ASSIGNMENT_PAYMENT_REVERSAL,
			CREDIT_MARKETING_PAYMENT);

	public static final List<String> TAX_FORM_1099_EARNINGS_NO_WORK_PAYMENTS_TRANSACTION_TYPE_CODES = ImmutableList.of(
			DEBIT_ASSIGNMENT_PAYMENT_REVERSAL,
			CREDIT_MARKETING_PAYMENT);

	public static final List<String> AD_HOC_INVOICES_FEES_TRANSACTION_TYPE_CODES = ImmutableList.of(
			SERVICE_FEE_DEPOSIT_RETURN,
			SERVICE_FEE_WITHDRAWAL_RETURN,
			SERVICE_FEE_LATE_PAYMENT,
			SERVICE_FEE_MISCELLANEOUS);

	public static final List<String> REMOVE_FUNDS_TRANSACTION_TYPE_CODES = ImmutableList.of(
			REMOVE_FUNDS,
			REMOVE_FUNDS_PAYPAL,
			REMOVE_FUNDS_GCC);

	public static final List<String> WORK_MARKET_INITIATED_TRANSACTIONS = ImmutableList.of(
		FAST_FUNDS_DEBIT,
		FAST_FUNDS_FEE,
		FAST_FUNDS_PAYMENT);

	public static Optional<String> newPaypalFeeInstanceByCountry(Country country) {
		if (country != null && country.getId() != null) {
			if (Country.USA_COUNTRY.equals(country))
				return Optional.of(PAY_PAL_FEE_USA);
			else if (Country.CANADA_COUNTRY.equals(country))
				return Optional.of(PAY_PAL_FEE_CANADA);
			else // default to international
				return Optional.of(PAY_PAL_FEE_INTL);
		}
		return Optional.absent();
	}

	public static Optional<String> newWMPaypalFeeInstanceByCountry(Country country) {
		if (country != null && country.getId() != null) {
			if (Country.USA_COUNTRY.equals(country))
				return Optional.of(WM_PAY_PAL_FEE_USA);
			else if (Country.CANADA_COUNTRY.equals(country))
				return Optional.of(WM_PAY_PAL_FEE_CANADA);
			else // default to international
				return Optional.of(WM_PAY_PAL_FEE_INTL);
		}
		return Optional.absent();
	}

	public RegisterTransactionType() {
		super();
	}

	public RegisterTransactionType(String code) {
		super(code);
	}

}
