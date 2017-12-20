package com.workmarket.integration.autotask.util;

public class StringUtil {

	public static String uncap(String input) {
		if(isNullOrEmpty(input)) return input;
		StringBuilder result = new StringBuilder();
		result.append(Character.toLowerCase(input.charAt(0)));
		result.append(input.substring(1));
		return result.toString();
	}
	
	public static boolean isNullOrEmpty(String input) {
		return input == null || input.length() == 0;
	}
}
