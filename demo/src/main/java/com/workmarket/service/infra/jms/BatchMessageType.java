package com.workmarket.service.infra.jms;

import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequestType;

public enum BatchMessageType {
	BANK_FILE_ACH("BANK_FILE_ACH"),
	BANK_FILE_OUTBOUND("BANK_FILE_OUTBOUND"),//USA Transactions
	BANK_FILE_OUTBOUND_NON_US("BANK_FILE_OUTBOUND_NON_US"),
	BANK_FILE_INBOUND("BANK_FILE_INBOUND"),
	BANK_FILE_PAYPAL("BANK_FILE_PAYPAL"),
	BANK_FILE_GCC("BANK_FILE_GCC");

	private String displayName;

	BatchMessageType(String displayName) {
		this.displayName = displayName;
	}

	public static BatchMessageType findByCode(String code) {
		switch (code) {
			case BankingIntegrationGenerationRequestType.ACHVERIFY:
				return BANK_FILE_ACH;
			case BankingIntegrationGenerationRequestType.OUTBOUND:
				return BANK_FILE_OUTBOUND;
			case BankingIntegrationGenerationRequestType.NON_USA_OUTBOUND:
				return BANK_FILE_OUTBOUND_NON_US;
			case BankingIntegrationGenerationRequestType.INBOUND:
				return BANK_FILE_INBOUND;
			case BankingIntegrationGenerationRequestType.PAYPAL:
				return BANK_FILE_PAYPAL;
			case BankingIntegrationGenerationRequestType.GCC:
				return BANK_FILE_GCC;
			default:
				return null;
		}
	}
}
