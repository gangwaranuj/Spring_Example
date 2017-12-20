package com.workmarket.domains.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by ianha on 5/29/14
 */
public enum YearsOfExperienceEnum {
	ZEROTOTWO("0-2"),
	TWOTOFIVE("2-5"),
	FIVETOTEN("5-10"),
	TENPLUS("10+");

	private final String description;

	private YearsOfExperienceEnum(String description) {
		this.description = description;
	}

	public String getDescription() { return this.description; }

	public static List<String> getAllDescriptions() {
		List<String> result = Lists.newArrayList();

		for (YearsOfExperienceEnum e : YearsOfExperienceEnum.values()) {
			result.add(e.getDescription());
		}

		return result;
	}

	public static YearsOfExperienceEnum getEnumFromDescription(String description) {
		for (YearsOfExperienceEnum e : YearsOfExperienceEnum.values()) {
			if (e.description.equals(description)) {
				return e;
			}
		}

		return null;
	}
}
