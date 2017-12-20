package com.workmarket.domains.model.insurance;

import com.workmarket.thrift.EnumValue;

/**
 * User: alexsilva Date: 12/20/13 Time: 1:15 PM
 */
public enum UserInsuranceType implements EnumValue {
	WORKERS_COMPENSATION(1000),
	GENERAL_LIABILITY(1001),
	ERRORS_AND_OMISSIONS(1002),
	AUTOMOBILE(1003),
	COMMERCIAL_GENERAL_LIABILITY(1023),
	BUSINESS_LIABILITY(1024),
	CONTRACTORS(1029);

	private final int value;

	private UserInsuranceType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static UserInsuranceType findByValue(int value) {
		switch (value) {
			case 1000:
				return WORKERS_COMPENSATION;
			case 1001:
				return GENERAL_LIABILITY;
			case 1002:
				return ERRORS_AND_OMISSIONS;
			default:
				return null;
		}
	}
}
