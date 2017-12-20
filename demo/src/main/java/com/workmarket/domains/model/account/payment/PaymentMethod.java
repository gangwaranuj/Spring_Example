package com.workmarket.domains.model.account.payment;


public enum PaymentMethod {

	CHECK(7),
	WIRE_TRANSFER(3),
	ACH(3),
	CREDIT_CARD(1),
	PRE_FUND(0);

	private final int paymentDays;

	PaymentMethod(int paymentDays) {
		this.paymentDays = paymentDays;
	}

	public int getPaymentDays() {
		return paymentDays;
	}

	public static PaymentMethod getPaymentMethodWithGreatestProcessTime(boolean check, boolean wireTransfer, boolean ach, boolean creditCard, boolean preFund) {
		if (check) return PaymentMethod.CHECK;
		if (wireTransfer || ach) return PaymentMethod.ACH;
		if (creditCard) return CREDIT_CARD;
		if (preFund) return PRE_FUND;
		return null;
	}
}
