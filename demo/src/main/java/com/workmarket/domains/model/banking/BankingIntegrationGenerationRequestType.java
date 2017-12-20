package com.workmarket.domains.model.banking;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="banking_integration_generation_request_type")
@Table(name="banking_integration_generation_request_type")
public class BankingIntegrationGenerationRequestType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public final static String INBOUND = "inbound";
	public final static String OUTBOUND = "outbound";
	public final static String NON_USA_OUTBOUND = "nonUsaOutbound";

	public final static String ACHVERIFY = "achverify";
	public final static String PAYPAL = "paypal";
	public final static String GCC = "gcc";

	public final static String[] NACHA_TYPES = {
			INBOUND,
			OUTBOUND,
			ACHVERIFY
	};

	public BankingIntegrationGenerationRequestType(){
		super();
	}

	public BankingIntegrationGenerationRequestType(String code){
		super(code);
	}

}
