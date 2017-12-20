package com.workmarket.utility;

import com.workmarket.configuration.EncryptionProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.util.Assert;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncryptionUtilities {
	private static final Log logger = LogFactory.getLog(EncryptionUtilities.class);

	private static final String LEGAL_PASSWORD_CHARACTERS = "a-zA-Z0-9_!@#$%^&";
	private static final String SALT_ALGORITHM = "SHA1PRNG";
	private static final int SALT_LENGTH = 20;
	private static final String OUTPUT_ENCODING = "hexadecimal";
	public static final String ENCRYPTION_ALGORITHM = "PBEWITHSHA256AND128BITAES-CBC-BC";

	private static String DEFAULT_SALT = EncryptionProperties.getSalt();
	private static String ENCRYPTION_SECRET = EncryptionProperties.getSecret();

	private static StandardPBEStringEncryptor standardEncryptor;
	private static StandardPBEStringEncryptor weakEncryptor;

	/**
	 * Returns new MD5 digester
	 *
	 * @return
	 */
	private static Md5PasswordEncoder newMd5PasswordEncoder() {
		return new Md5PasswordEncoder();
	}

	/**
	 * Returns new SHA-256 digester
	 *
	 * @return
	 */
	private static ShaPasswordEncoder newSha256PasswordEncoder() {
		return new ShaPasswordEncoder(256);
	}

	/**
	 * Returns message digest using MD5 algorithm, stable and represented as hexadecimal string
	 *
	 * @param message
	 * @return
	 */
	public static String getMD5Digest(String message) {
		Assert.hasText(message);
		return newMd5PasswordEncoder().encodePassword(message, DEFAULT_SALT);
	}

	/**
	 * Returns message digest of a Number using MD5 algorithm, stable and represented as hexadecimal string
	 *
	 * @param number
	 * @return
	 */
	public static String getMD5Digest(Number number) {
		Assert.notNull(number);
		return getMD5Digest(number.toString());
	}

	/**
	 * Returns message digest using SHA-256 algorithm, not stable, base64 encoded
	 *
	 * @param message
	 * @return
	 */
	public static String getSHA256Digest(String message, String salt) {
		Assert.hasText(message);
		return newSha256PasswordEncoder().encodePassword(message, salt);
	}

	/**
	 * Returns a password hash. It will validate password length and legality of the password characters
	 *
	 * @param password
	 * @return
	 */
	public static String hashPassword(String password, String salt) {

		Assert.hasText(password);
		return newSha256PasswordEncoder().encodePassword(password, salt);
	}

	/**
	 * Validates a password against password hash
	 *
	 * @param password
	 * @param digest
	 * @return
	 */
	public static boolean isPasswordValid(String digest, String password, String salt) {

		Assert.hasText(password);
		Assert.hasText(digest);
		Assert.hasText(salt);

		return newSha256PasswordEncoder().isPasswordValid(digest, password, salt);
	}

	/**
	 * Validates if password contains legal characters
	 *
	 * @param password
	 * @return
	 */
	static boolean isPasswordLegal(String password) {
		Assert.hasText(password);
		return !password.matches(".*[^" + LEGAL_PASSWORD_CHARACTERS + "]+.*");
	}

	/**
	 * Generates new random salt encoded as hex string
	 *
	 * @return
	 */
	static String newSalt() {
		SecureRandom sr = null;
		byte[] b = new byte[SALT_LENGTH];
		try {
			sr = SecureRandom.getInstance(SALT_ALGORITHM);
			sr.nextBytes(b);
			return StringUtilities.encodeHex(b);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	/**
	 * Returns new password based encryptor
	 *
	 * @return
	 */
	static StandardPBEStringEncryptor newStandardPBEStringEncryptor() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(ENCRYPTION_SECRET);
		encryptor.setStringOutputType(OUTPUT_ENCODING);
		return encryptor;
	}

	static synchronized StandardPBEStringEncryptor getEncryptor() {
		if (standardEncryptor == null) {
			standardEncryptor = newStandardPBEStringEncryptor();
			standardEncryptor.setProvider(new BouncyCastleProvider());
			standardEncryptor.setAlgorithm(ENCRYPTION_ALGORITHM);
		}

		return standardEncryptor;
	}

	static synchronized StandardPBEStringEncryptor getWeakEncryptor() {
		if (weakEncryptor == null) {
			weakEncryptor = newStandardPBEStringEncryptor();
		}

		return weakEncryptor;
	}

	/**
	 * Encrypts text
	 *
	 * @param plainText
	 * @return
	 */
	public static String encrypt(String plainText) {
		Assert.hasText(plainText);

		return getEncryptor().encrypt(plainText);
	}

	/**
	 * Encrypts a number
	 *
	 * @param number
	 * @return
	 */
	public static String encryptLong(Long number) {
		Assert.notNull(number);

		return getEncryptor().encrypt(number.toString());
	}

	/**
	 * Decrypts text
	 *
	 * @param cryptText
	 * @return
	 */
	public static String decrypt(String cryptText) {
		Assert.hasText(cryptText);

		try {
			return getEncryptor().decrypt(cryptText);
		} catch (EncryptionOperationNotPossibleException e) {
			logger.info("EncryptionUtilities#decrypt: Fallback to StandardPBEStringEncryptor with default encryption algorithm.", e);
			return getWeakEncryptor().decrypt(cryptText);
		}
	}

	/**
	 * Decrypts long
	 *
	 * @param cryptText
	 * @return
	 */
	public static Long decryptLong(String cryptText) {
		Assert.hasText(cryptText);

		try {
			return StringUtilities.parseLong(getEncryptor().decrypt(cryptText));
		} catch (EncryptionOperationNotPossibleException e) {
			logger.info("EncryptionUtilities#decryptLong: Fallback to StandardPBEStringEncryptor with default encryption algorithm.", e);
			return StringUtilities.parseLong(getWeakEncryptor().decrypt(cryptText));
		}
	}
}
