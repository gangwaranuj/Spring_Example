package com.workmarket.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtilities {
	public static String digestAsHex(String algorithm, String password, String salt) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update((password + salt).getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
}
