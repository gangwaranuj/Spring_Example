package com.workmarket.service.business.accountregister;

import com.google.common.collect.ImmutableList;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(value = "prototype")
public class CreditMemoRegisterTransaction extends CreditRegisterTransaction {

	public static final List<Integer> CREDIT_MEMO_SOFTWARE_FEE_TYPE_IDS = ImmutableList.of(
			CreditMemoType.SUBSCRIPTION_DISCOUNT_CREDIT.ordinal(),
			CreditMemoType.SUBSCRIPTION_SETUP_FEE_PAYMENT_CREDIT.ordinal(),
			CreditMemoType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT_CREDIT.ordinal());

	public static final List<Integer> CREDIT_MEMO_SUBSCRIPTION_TYPE_IDS = ImmutableList.of(
		CreditMemoType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT_CREDIT.ordinal(),
		CreditMemoType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT_CREDIT.ordinal(),
		CreditMemoType.SUBSCRIPTION_SETUP_FEE_PAYMENT_CREDIT.ordinal(),
		CreditMemoType.SUBSCRIPTION_DISCOUNT_CREDIT.ordinal());
}
