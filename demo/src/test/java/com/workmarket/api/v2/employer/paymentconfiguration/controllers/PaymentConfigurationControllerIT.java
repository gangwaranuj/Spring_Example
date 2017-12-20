package com.workmarket.api.v2.employer.paymentconfiguration.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.paymentconfiguration.models.PaymentConfigurationDTO;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class PaymentConfigurationControllerIT extends ApiV2BaseIT {
	private static final String ENDPOINT = "/employer/v2/payment_configuration";
	private final TypeReference<ApiV2Response<PaymentConfigurationDTO>> type = new TypeReference<ApiV2Response<PaymentConfigurationDTO>>() {};

	@Before
	public void setUp() throws Exception {
		login();
	}

	@Test
	public void getPaymentConfiguration() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT)
		).andExpect(status().isOk()).andReturn();

		PaymentConfigurationDTO result = getFirstResult(mvcResult, type);

		assertThat(result, hasProperty("workFeePercentage", equalTo(new BigDecimal("10.0000"))));
		assertThat(result, hasProperty("subscribed", is(false)));
		assertThat(result, hasProperty("assignmentPricingType", is(0)));
	}
}
