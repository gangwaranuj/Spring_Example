package com.workmarket.service.talentpool;

import com.workmarket.business.talentpool.TalentPoolClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TalentPoolServiceConfiguration {

	private TalentPoolClient talentPoolClient = null;

	@Bean
	TalentPoolClient getTalentPoolClient() {
		if (talentPoolClient == null) {
			talentPoolClient = new TalentPoolClient();
		}
		return talentPoolClient;
	}

}
