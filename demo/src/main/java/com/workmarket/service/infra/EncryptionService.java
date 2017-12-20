package com.workmarket.service.infra;


public interface EncryptionService {
	
	
    /**
     * Encrypts text
     *
     * @param plainText
     * @return
     */
    String encrypt(String plainText);

    /**
     * Decrypts text
     *
     * @param cryptText
     * @return
     */
    String decrypt(String cryptText);

    /**
     * Returns message digest using MD5 algorithm, stable and represented as hexadecimal string
     *
     * @param message
     * @return
     */
    String getMD5Digest(String message);

    /**
     * Returns message digest of a Number using MD5 algorithm, stable and represented as hexadecimal string
     *
     * @param number
     * @return
     */
    String getMD5Digest(Number number);

    /**
     * Returns a password hash. It will validate password length and legality of the password characters
     *
     * @param password
     * @return
     */
    String hashPassword(String password, String salt);

    /**
     * Validates a password against password hash
     *
     * @param password
     * @param digest
     * @return
     */
    boolean isPasswordValid(String digest, String password, String salt);

	long decryptId(String encryptedId);
}
