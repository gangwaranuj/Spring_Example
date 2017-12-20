package com.workmarket.factory;

import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequestType;
import com.workmarket.domains.model.banking.EmptyHeaderMapper;
import com.workmarket.domains.model.banking.GlobalCashCardHeaderMapper;
import com.workmarket.domains.model.banking.GlobalCashCardRowMapper;
import com.workmarket.domains.model.banking.NachaRowMapper;
import com.workmarket.domains.model.banking.PayPalRowMapper;
import com.workmarket.domains.model.banking.WorldLinkNachaRowMapper;
import com.workmarket.utility.CollectionUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class BankingIntegrationGenerationRequestRowMapperFactoryImpl implements BankingIntegrationGenerationRequestRowMapperFactory {

	@Value("${worldlink.debit}")
	private String WORLDLINK_DEBIT_ACCOUNT;

	@Override
	public BankingIntegrationGenerationRequestRowMapper getRowMapper(BankingIntegrationGenerationRequestType integrationType) {
		Assert.notNull(integrationType);
		String typeCode = integrationType.getCode();

		if (CollectionUtilities.containsAny(typeCode, BankingIntegrationGenerationRequestType.NACHA_TYPES)) {
			return new NachaRowMapper();
		}
		switch (typeCode) {
			case BankingIntegrationGenerationRequestType.NON_USA_OUTBOUND:
				return new WorldLinkNachaRowMapper(WORLDLINK_DEBIT_ACCOUNT);
			case BankingIntegrationGenerationRequestType.PAYPAL:
				return new PayPalRowMapper();
			case BankingIntegrationGenerationRequestType.GCC:
				return new GlobalCashCardRowMapper();
			default:
				throw new UnsupportedOperationException(String.format("Illegal BankingIntegrationGenerationRequestType: %s", typeCode));
		}
	}

	@Override
	public BankingIntegrationGenerationRequestHeaderMapper getHeaderMapper(BankingIntegrationGenerationRequestType integrationType) {
		Assert.notNull(integrationType);
		String typeCode = integrationType.getCode();

		/* NOTE: at the moment of implementation only GCC required header */
		if (BankingIntegrationGenerationRequestType.GCC.equals(typeCode)) {
			return new GlobalCashCardHeaderMapper();
		}
		return new EmptyHeaderMapper();
	}
}
