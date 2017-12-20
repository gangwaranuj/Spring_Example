package com.workmarket.domains.model.banking;


import com.workmarket.factory.BankingIntegrationGenerationRequestHeaderMapper;

public class EmptyHeaderMapper implements BankingIntegrationGenerationRequestHeaderMapper {
	/**
	 * This class is used as a place holder for a header in csv files, use it in case if csv(or other) format
	 * doesn't need to have a header.
	 *
	 * @return empty array
	 */
	@Override
	public String[] mapHeader() {
		return new String[]{};
	}
}
