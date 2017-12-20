package com.workmarket.domains.authentication;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.workmarket.common.configuration.ConfigLoader;
import com.workmarket.common.jwt.JwtConfig;
import com.workmarket.common.jwt.JwtKeySupplier;
import com.workmarket.common.jwt.JwtValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Configuration for JWT minting and validation.
 */
@Configuration
public class JwtConfiguration {
	@Autowired
	private Config config;

	private JwtConfig getJwtConfig() throws IOException {
		Config jwtConfig = null;
		try {
			jwtConfig = this.config.getConfig("jwtConfig");
		} catch (final ConfigException.Missing e) {
			// this space intentionally left blank
		}
		if (jwtConfig == null) {
			return null;
		}
		return ConfigLoader.getConfig(JwtConfig.class, jwtConfig);
	}

	/**
	 * Get the key supplier for minting operations.
	 */
	private JwtKeySupplier getJwtKeySupplier(JwtConfig config) throws Exception {
		return new JwtKeySupplier(config);
	}

	/**
	 * Get the JWT validator.
	 *
	 * @return
	 * @throws IOException
	 */
	@Bean
	public JwtValidator getJwtValidator() throws Exception {
		final JwtConfig jwtConfig = getJwtConfig();
		final Boolean failingSignaturesAreOkYesIAmReallySure = jwtConfig == null
				? false
				: jwtConfig.getFailingSignaturesAreOkYesIAmReallySure();
		return new JwtValidator(getJwtKeySupplier(jwtConfig).getValidationKeySupplier(),
				failingSignaturesAreOkYesIAmReallySure, getJwtConfig());
	}
}
