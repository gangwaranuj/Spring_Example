package com.workmarket.service.business.accountregister;

public enum CreditMemoType {
	SUBSCRIPTION_SOFTWARE_FEE_PAYMENT_CREDIT("sbSftFeeCr"),
	SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT_CREDIT("sbVoRCr"),
	SUBSCRIPTION_SETUP_FEE_PAYMENT_CREDIT("sbSetupCr"),
	SUBSCRIPTION_ADD_ON_PAYMENT_CREDIT("sbAddOnCr"),
	SUBSCRIPTION_DISCOUNT_CREDIT("sbDiscCr"),
	MISC_CREDIT("miscCr");

	private String code;
	private CreditMemoType(String code) {
		this.code = code;
	}
	public String code() {
		return this.code;
	}
}
