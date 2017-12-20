package com.workmarket.domains.model;

/**
 * Created by ianha on 7/14/14
 */
public enum Gender {
	MALE("MALE"), FEMALE("FEMALE"), OTHER("OTHER");

	private final String code;

	private Gender(String code) {
		this.code = code;
	}

	public static Gender getEnumFromCode(String code) {
		for (Gender g : Gender.values()) {
			if (g.toString().equals(code.toUpperCase())) {
				return g;
			}
		}

		return null;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return this.code;
	}
}
