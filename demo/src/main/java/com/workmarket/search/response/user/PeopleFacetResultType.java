package com.workmarket.search.response.user;

import com.workmarket.thrift.EnumValue;

public enum PeopleFacetResultType implements EnumValue {
	GROUP(0),
	LANE(1),
	CERTIFICATION(2),
	LICENSE(3),
	INDUSTRY(4),
	COMPANY_TYPE(5),
	ASSESSMENT(6),
	VERIFICATION(7),
	INSURANCE(8),
	RATING(9),
	COUNTRY(10),
	AVATAR(11),
	COMPANY_ID(12),
	INVITED_ASSESSMENT(13),
	PASSED_ASSESSMENT(14),
	FAILED_TEST(15),
	NOT_INVITED_ASSESSMENT(16),
	GROUP_MEMBERS(17),
	GROUP_MEMBERS_OVERRIDE(18),
	GROUP_PENDING_MEMBERS(19),
	GROUP_PENDING_OVERRIDE_MEMBERS(20),
	GROUP_INVITED(21),
	GROUP_DECLINED(22),
	MBO(23),
	SHARED_GROUP(24);

	private final int value;

	private PeopleFacetResultType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static PeopleFacetResultType findByValue(int value) {
		switch (value) {
			case 0:
				return GROUP;
			case 1:
				return LANE;
			case 2:
				return CERTIFICATION;
			case 3:
				return LICENSE;
			case 4:
				return INDUSTRY;
			case 5:
				return COMPANY_TYPE;
			case 6:
				return ASSESSMENT;
			case 7:
				return VERIFICATION;
			case 8:
				return INSURANCE;
			case 9:
				return RATING;
			case 10:
				return COUNTRY;
			case 11:
				return AVATAR;
			case 12:
				return COMPANY_ID;
			case 23:
				return MBO;
			default:
				return null;
		}
	}
}
