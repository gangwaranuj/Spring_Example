package com.workmarket.service.infra.security;

import java.security.KeyStore;

public interface KeyStoreService {
	KeyStore getDefaultKeyStore();
	String getKeyStorePassword();
}
