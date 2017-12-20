package com.workmarket.service.infra.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.ConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

@Service
public class KeyStoreServiceImpl implements KeyStoreService {
	private static final Log logger = LogFactory.getLog(KeyStoreServiceImpl.class);

	@Value("${javax.net.ssl.keyStore}")
	private String keyStoreSourcePath;
	@Value("${javax.net.ssl.keyStorePassword}")
	private String keyStorePassword;
	private static KeyStore defaultKeyStore;

	@PostConstruct
	private void postConstruct() throws ConfigurationException {
		try {
			File file;
			if (isDevelopment()) {
				// Get the fully qualified path if we are in development mode
				file = new File(this.getClass().getClassLoader().getResource(keyStoreSourcePath).getPath());
			} else {
				file = new File(keyStoreSourcePath);
			}
			InputStream is = new FileInputStream(file);
			defaultKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			defaultKeyStore.load(is, keyStorePassword.toCharArray());
		} catch (Exception e) {
			logger.error(e);
			throw new ConfigurationException("Error initializing default key store");
		}
	}

	private boolean isDevelopment() {
		return keyStoreSourcePath.contains("emptytruststore.jks");
	}

	@Override
	public String getKeyStorePassword() {
		return keyStorePassword;
	}


	@Override
	public KeyStore getDefaultKeyStore() {
		return defaultKeyStore;
	}
}
