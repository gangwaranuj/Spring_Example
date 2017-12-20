package com.workmarket.service.search;

import com.workmarket.search.SearchClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class SearchConfiguration {

	@Bean
	protected SearchClient getSearchClient() throws IOException {
		return new SearchClient();
	}
}
