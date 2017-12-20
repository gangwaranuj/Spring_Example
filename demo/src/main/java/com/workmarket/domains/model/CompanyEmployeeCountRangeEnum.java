package com.workmarket.domains.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by ianha on 5/29/14
 */
public enum CompanyEmployeeCountRangeEnum {
	ZEROTOFIVE("0-5"),
	FIVETOTEN("5-10"),
	TENTOTWENTYFIVE("10-25"),
	TWENTYFIVEPLUS("25+");

	private final String description;

	private CompanyEmployeeCountRangeEnum(String description) {
		this.description = description;
	}

	public String getDescription() { return this.description; }

	public static List<String> getAllDescriptions() {
		List<String> result = Lists.newArrayList();

		for (CompanyEmployeeCountRangeEnum e : CompanyEmployeeCountRangeEnum.values()) {
			result.add(e.getDescription());
		}

		return result;
	}

	public static CompanyEmployeeCountRangeEnum getEnumFromDescription(String description) {
		for (CompanyEmployeeCountRangeEnum e : CompanyEmployeeCountRangeEnum.values()) {
			if (e.description.equals(description)) {
				return e;
			}
		}

		return CompanyEmployeeCountRangeEnum.ZEROTOFIVE;
	}
}
