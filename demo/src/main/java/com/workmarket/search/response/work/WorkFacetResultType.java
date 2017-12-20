package com.workmarket.search.response.work;

import com.workmarket.thrift.EnumValue;

public enum WorkFacetResultType implements EnumValue {
	WORK_STATUS_TYPE_CODE(0),
	BUYER_USER_ID(1),
	WORK_SUB_STATUS_TYPE_CODE(2),
	ASSIGNED_RESOURCE(3),
	WORK_SUB_STATUS_TYPE_CODE_DRILL_DOWN(4),
	RESOURCE_WORK_STATUS_TYPE_CODE(5),
	APPLICANT_IDS(6),
	COUNTY_ID(7),
	COUNTY_NAME(8),
	EXTERNAL_UNIQUE_IDS(9);

	private final int value;

	private WorkFacetResultType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
