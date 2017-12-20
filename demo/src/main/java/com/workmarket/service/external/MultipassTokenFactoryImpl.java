package com.workmarket.service.external;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Multipass SSO Token Provider
 * Used specifically for our implementation of SSO into Desk.com.
 *
 * @see http://dev.desk.com/docs/portal/multipass
 * @see https://github.com/ideascale/multipass/blob/master/java/src/com/ideascale/multipass/MultipassTokenFactoryBase.java
 *
 * For HMAC implementation example:
 * @see http://stackoverflow.com/a/3485422/80778
 */
public class MultipassTokenFactoryImpl implements MultipassTokenFactory {

	private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding"; // or AES/CBC/NOPADDING
	private static final byte[] IV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

	private String siteKey;
	private String apiKey;

	public MultipassTokenFactoryImpl(String siteKey, String apiKey) {
		this.siteKey = siteKey;
		this.apiKey = apiKey;
	}

	@Override
	public String encode(String data) {
		try {
			String salted = apiKey + siteKey;
			byte[] hash = DigestUtils.sha(salted);
			byte[] saltedHash = new byte[16];
			System.arraycopy(hash, 0, saltedHash, 0, 16);
			SecretKeySpec keySpec = new SecretKeySpec(saltedHash, "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(IV);

			Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
			byte[] payloadBytes = cipher.doFinal(data.getBytes());

			return Base64.encodeBase64String(payloadBytes)
					.replaceAll("\\s+", "")
					.replaceAll("\\=+$", "")
					.replaceAll("\\+", "-")
					.replaceAll("\\/", "_");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String sign(String data) {
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret = new SecretKeySpec(apiKey.getBytes(), mac.getAlgorithm());
			mac.init(secret);
			byte[] digest = mac.doFinal(data.getBytes());
			return Base64.encodeBase64String(digest);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
