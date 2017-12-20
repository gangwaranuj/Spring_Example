package com.workmarket.utility;

import static org.apache.commons.lang.StringUtils.contains;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailUtilities {

	/**
	 * Tests for a valid email address per the RFC.
	 * 
	 * This means that even non-domain emails can be OK emails
	 * 
	 * "dog@doggie" is valid. "@doggie.com" is not
	 * 
	 * @param aEmailAddress
	 * @return
	 */
	public static boolean isValidEmailAddress(String aEmailAddress) {
		if (aEmailAddress == null || !contains(aEmailAddress, '@')) {
			return false;
		}
		boolean result = true;
		try {
			new InternetAddress(aEmailAddress);
			if (!hasNameAndDomain(aEmailAddress)) {
				result = false;
			}
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}

	private static boolean hasNameAndDomain(String aEmailAddress) {
		String[] tokens = aEmailAddress.split("@");
		return tokens.length == 2 && isNotEmpty(tokens[0]) && isNotEmpty(tokens[1]);
	}

	public static boolean containsEmail(List<String> keywords) {
		for (String keyword : keywords) {
			if (isValidEmailAddress(keyword)) {
				return true;
			}
		}
		return false;
	}

	public static String findEmail(List<String> keywords) {
		for (String keyword : keywords) {
			if (isValidEmailAddress(keyword)) {
				return keyword;
			}
		}
		return null;
	}
}
