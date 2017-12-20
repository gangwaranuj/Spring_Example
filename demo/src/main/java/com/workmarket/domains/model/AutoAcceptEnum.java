package com.workmarket.domains.model;

/**
 * Created by ianha on 2/19/14
 */
public enum AutoAcceptEnum {
	FIRST_TO_APPLY,
	FIND_BEST_FIT;

	public static AutoAcceptEnum getEnumByName(String name) {
		for (AutoAcceptEnum en : AutoAcceptEnum.values()) {
			if (en.name().equals(name)) {
				return en;
			}
		}

		return null;
	}
}
