package com.workmarket.web.controllers.feed;

import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.test.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class PublicWorkControllerIT extends ApiV2BaseIT {
	private static final String SEARCH_ENDPOINT = "/work/55";
	
	@Autowired private FilterChainProxy springSecurityFilterChain;
	
	@Before
	public void setUpControllerMappingsWithSecurity() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.addFilters(this.springSecurityFilterChain)
			.build();
	}

	@Test
	public void publicWork_loginRedirect() throws Exception {
		mockMvc.perform(
			MockMvcRequestBuilders.get(SEARCH_ENDPOINT)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(redirectedUrlPattern("*/login")
		);
	}
	
	private static ResultMatcher redirectedUrlPattern(final String expectedUrlPattern) {
	    return new ResultMatcher() {
	        public void match(MvcResult result) {
	            Pattern pattern = Pattern.compile("\\A" + expectedUrlPattern + "\\z");
	            assertTrue("Redirected URL", pattern.matcher(result.getResponse().getRedirectedUrl()).find());
	        }
	    };
	}
}
