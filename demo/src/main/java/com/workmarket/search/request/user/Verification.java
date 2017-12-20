package com.workmarket.search.request.user;

import com.google.common.collect.ImmutableList;
import com.workmarket.dao.search.user.SolrUserDAOImpl;

import java.util.List;

public enum Verification {
	BACKGROUND_CHECK(1),
	@Deprecated GOVERNMENT_CLEARANCE(2),
	DRUG_TEST(3),
	FAILED_BACKGROUND_CHECK(4),
	FAILED_DRUG_TEST(5);

	private final int value;

	public static final List<Long> FAILED_SCREENING_IDS = ImmutableList.of(
		(long) FAILED_BACKGROUND_CHECK.getValue(),
		(long) FAILED_DRUG_TEST.getValue()
	);

	private Verification(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static Verification findByValue(int value) {
		switch (value) {
			case 1:
				return BACKGROUND_CHECK;
			case 2:
				return GOVERNMENT_CLEARANCE;
			case 3:
				return DRUG_TEST;
			case 4:
				return FAILED_BACKGROUND_CHECK;
			case 5:
				return FAILED_DRUG_TEST;
			default:
				return null;
		}
	}
}
