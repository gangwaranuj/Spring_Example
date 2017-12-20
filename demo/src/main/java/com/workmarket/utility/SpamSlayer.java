package com.workmarket.utility;

/**
 * User: micah
 * Date: 2/28/13
 * Time: 12:10 PM
 */
public class SpamSlayer {
	public static String slay(String stringToSlay) {
		if (stringToSlay == null) return null;
		StringBuilder slainString = new StringBuilder();
		for (char c : stringToSlay.toCharArray())
			slainString
				.append("&#")
				.append(String.format("%03d", (int)c))
				.append(";");
		return slainString.toString();
	}
}
