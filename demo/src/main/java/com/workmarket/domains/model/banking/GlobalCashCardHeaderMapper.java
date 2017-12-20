package com.workmarket.domains.model.banking;

import com.workmarket.factory.BankingIntegrationGenerationRequestHeaderMapper;
import com.workmarket.utility.CollectionUtilities;

public class GlobalCashCardHeaderMapper implements BankingIntegrationGenerationRequestHeaderMapper{
	@Override
	public String[] mapHeader() {
		return CollectionUtilities.newArray(
				"keyfield",
				"description",
				"net"
		);
	}
}
