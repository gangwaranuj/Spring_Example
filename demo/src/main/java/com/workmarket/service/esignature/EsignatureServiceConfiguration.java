package com.workmarket.service.esignature;

import com.workmarket.biz.esignature.EsignatureClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsignatureServiceConfiguration {

	private EsignatureClient esignatureClient = null;

	@Bean
	EsignatureClient getEsignatureClient() {
		if (esignatureClient == null) {
			esignatureClient = new EsignatureClient();
		}
		return esignatureClient;
	}

}
