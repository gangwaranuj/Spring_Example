package com.workmarket.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomUtilities {

	private static final Log logger = LogFactory.getLog(RandomUtilities.class);

	private RandomUtilities() {}

	public static Long nextLong() {
		SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
			return secureRandom.nextLong();
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		}
		return null;
	}

	public static int nextIntInRange(int s, int l) {
		SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
			return s + secureRandom.nextInt(l);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		}
		return 0;
	}

	public static int nextIntInRangeWithSeed(int s, int l, long seed) {
		SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(seed);
			return s + secureRandom.nextInt(l);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		}
		return 0;
	}

	public static int nextInt() {
		SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
			return secureRandom.nextInt();
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		}
		return 0;
	}

	public static String generateNumericString(int n) {
		String[] alphabet = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		Assert.isTrue(n > 0);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < n; i++) {

			int index = Math.abs(nextInt()) % 10;

			// if we picked a zero for the first number, pick again
			if ((i == 0) && (index == 0)) {
				i--;
			} else {
				sb.append(alphabet[index]);
			}
		}

		return sb.toString();
	}

	public static String generateAlphaString(int n) {
		String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h",
				"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
				"t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D",
				"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
				"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

		Assert.isTrue(n > 0);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < n; i++)
			sb.append(alphabet[Math.abs(nextInt()) % alphabet.length]);

		return sb.toString();
	}

	public static String generateAlphaNumericString(int n) {
		String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h",
				"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
				"t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D",
				"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
				"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
				"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

		Assert.isTrue(n > 0);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < n; i++)
			sb.append(alphabet[Math.abs(nextInt()) % alphabet.length]);

		return sb.toString();
	}

	public static String generateLoremIpsum(int length) {
		String[] alphabet = {"Lorem ", "ipsum ", "dolor ", "sit ", "amet ", "consectetur ", "adipiscing ", "elit ", ". "};

		Assert.isTrue(length > 0);

		StringBuilder sb = new StringBuilder();

		int len = 0;
		while (true) {
			if (len == length)
				break;
			String word = alphabet[Math.abs(nextInt()) % alphabet.length];
			if (len + word.length() > length) {
				sb.append(word.substring(0, length - len));
				len += length - len;
			} else {
				sb.append(word);
				len += word.length();
			}
		}

		return sb.toString();
	}
}