package com.workmarket.service.infra.business;

import com.workmarket.auth.AuthenticationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by drew on 20/03/17.
 */
@Configuration
public class AuthenticationConfig {
	@Bean
	AuthenticationClient getAuthClient() {
		return new AuthenticationClient();
	}
}
