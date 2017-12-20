package com.workmarket.domains.model.invoice;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CreditMemoReasons {
	REFUND("REFUND", 1),
	WRITE_OFF("WRITE_OFF", 2),
	OTHER("OTHER", 3);

	private final String label;
	private final Integer value;
	public static List<CreditMemoReasons> CREDITMEMO_REASONS;
	static{
		CREDITMEMO_REASONS = ImmutableList.copyOf(CreditMemoReasons.values());
	}

	// This allows us to get a CreditMemoReason by it's 'value'
	public static final Map<Integer,CreditMemoReasons> CREDIT_MEMO_REASONS_MAP;
	static {
		CREDIT_MEMO_REASONS_MAP = new HashMap<>();
		for (CreditMemoReasons v : CreditMemoReasons.values()) {
			CREDIT_MEMO_REASONS_MAP.put(v.value, v);
		}
	}

	CreditMemoReasons(String label, Integer value){
		this.label = label;
		this.value = value;
	}

	public String getLabel(){
		return this.label;
	}

	public Integer getValue(){ return this.value;}

}
