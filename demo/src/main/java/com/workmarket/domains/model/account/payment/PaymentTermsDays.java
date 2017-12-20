package com.workmarket.domains.model.account.payment;

public enum PaymentTermsDays {

	SEVEN(7),
	FIFTEEN(15),
	TWENTY_ONE(21),
	THIRTY(30),
	FORTY(40);

	private final int paymentDays;

	PaymentTermsDays(int paymentDays) {
		this.paymentDays = paymentDays;
	}

	public int getPaymentDays() {
		return paymentDays;
	}

	public static PaymentTermsDays getNearestPaymentTermsDays(int days) {
		PaymentTermsDays paymentTerms = PaymentTermsDays.SEVEN;
		for (PaymentTermsDays paymentTermsDays : PaymentTermsDays.values()) {
			if (days <= paymentTermsDays.getPaymentDays()) {
				return paymentTermsDays;
			} else {
				paymentTerms = paymentTermsDays;
			}
		}
		return paymentTerms;
	}
}
