package com.workmarket.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class EncryptionProperties {
	private static final Log logger = LogFactory.getLog(EncryptionProperties.class);

	private static String SALT;
	private static String SECRET;

	public static String getSalt() {
		return SALT;
	}

	public static void setSalt(String salt) {
		SALT = salt;
	}

	public static String getSecret() {
		return SECRET;
	}

	public static void setSecret(String secret) {
		SECRET = secret;
	}
}
