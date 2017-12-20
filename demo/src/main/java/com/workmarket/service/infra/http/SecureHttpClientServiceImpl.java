package com.workmarket.service.infra.http;

import com.workmarket.service.infra.security.KeyStoreService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.PrivateKeyDetails;
import org.apache.http.conn.ssl.PrivateKeyStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.ConfigurationException;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Map;

@Service
public class SecureHttpClientServiceImpl implements HttpClientService {
	private static final Log logger = LogFactory.getLog(SecureHttpClientServiceImpl.class);

	@Autowired KeyStoreService keyStoreService;

	@Value("${security.tls.version}")
	private String tlsVersion;

	@Value("${http.service.connectionTimeout}")
	private int connectionTimeout;

	@Value("${http.service.socketTimeout}")
	private int socketTimeout;

	@Value("${http.service.maxConnectionsPerRoute}")
	private int maxConnectionsPerRoute;

	@Value("${security.keystore.keyEntry}")
	private String keyEntry;

	private static CloseableHttpClient httpClient;

	@PostConstruct
	private void construct() throws ConfigurationException {
		httpClient = createClient();
	}

	@PreDestroy
	private void destroy() {
		try {
			if (httpClient != null) {
				httpClient.close();
			}
		} catch (IOException e) {
			logger.error("Error closing http client");
		}
	}

	@Override
	public CloseableHttpClient getClient() throws ConfigurationException {
		return httpClient;
	}

	private CloseableHttpClient createClient() throws ConfigurationException {
		KeyStore keyStore = keyStoreService.getDefaultKeyStore();
		SSLContext sslcontext = null;

		try {
			final PrivateKeyStrategy aliasStrategy = new PrivateKeyStrategy() {
				@Override
				public String chooseAlias(Map<String, PrivateKeyDetails> map, Socket socket) {
					Assert.isTrue(map.containsKey(keyEntry));
					return keyEntry;
				}
			};
			sslcontext = SSLContexts.custom()
				.loadKeyMaterial(keyStore, keyStoreService.getKeyStorePassword().toCharArray(), aliasStrategy)
				.build();
		} catch (Exception e) {
			logger.error(e);
			throw new ConfigurationException("Error initializing key store ssl context");
		}

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
			sslcontext,
			new String[] { "TLSv" + tlsVersion },
			null,
			SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
			.register("https", sslsf)
			.build();
		PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		pool.setDefaultMaxPerRoute(maxConnectionsPerRoute);
		return HttpClients.custom()
			.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
			.setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build())
			.setConnectionManager(pool)
			.setSSLSocketFactory(sslsf)
			.build();
	}

}
