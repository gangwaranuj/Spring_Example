package com.workmarket.utility;

public class SqlUtilities {

	private SqlUtilities() {
	};

	public static String prepareLikeString(String s) {
		return prepareLikeString(s, false);
	}

	public static String prepareLikeString(String s, boolean stringAsPrefix) {
		if (s == null)
			return "%%";
		return (stringAsPrefix ? "" : "%" ) + s + "%";
	}
}