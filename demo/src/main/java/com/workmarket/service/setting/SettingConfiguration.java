package com.workmarket.service.setting;

import com.workmarket.setting.SettingClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class SettingConfiguration {
	/**
	 * Wire SettingClient.
	 *
	 * @return SettingClient.
	 */
	@Bean(name = "SettingClient")
	protected SettingClient getSettingClient() throws IOException {
		return new SettingClient();
	}
}
