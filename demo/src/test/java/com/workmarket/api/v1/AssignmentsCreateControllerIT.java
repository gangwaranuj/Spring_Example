package com.workmarket.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.internal.RequestToken;
import com.workmarket.api.v1.model.ApiAssignmentCreationResponseDTO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.web.controllers.ControllerIT;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AssignmentsCreateControllerIT extends ControllerIT {

	@Autowired WorkService workService;
	private static final String ENDPOINT = "/api/v1/assignments/create";

	protected static final TypeReference<ApiV1Response<ApiAssignmentCreationResponseDTO>> assignmentCreationResponseType = new TypeReference<ApiV1Response<ApiAssignmentCreationResponseDTO>>() {
	};

	@Test
	public void createAssignment_withPaymentTerms_useArgumentValue() throws Exception {
		user = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		login(user);
		RequestToken requestToken = generateApiRequestTokenForCompany(user.getCompany().getId());
		accessToken = generateAccessToken(requestToken, user.getUuid(), user.getCompany().getUuid());

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
				.param("title", "test")
				.param("description", "test")
				.param("pricing_type", "flat")
				.param("pricing_flat_price", "1")
				.param("location_offsite", "1")
				.param("industry_id", "1000")
				.param("scheduled_start_date", String.valueOf(System.currentTimeMillis() / 1000))
				.param("payment_terms_days", "3")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		).andExpect(status().isOk()).andReturn();

		final ApiV1Response<ApiAssignmentCreationResponseDTO> response = expectApiV1Response(mvcResult, assignmentCreationResponseType);

		Work work = workService.findWorkByWorkNumber(response.getResponse().getId());
		assertEquals(200, response.getMeta().getStatusCode());
		assertEquals(3L, (work.getPaymentTermsDays()).longValue());
	}

	@Test
	public void createAssignment_withNoPaymentTerms_useDefault() throws Exception {
		user = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		login(user);
		RequestToken requestToken = generateApiRequestTokenForCompany(user.getCompany().getId());
		accessToken = generateAccessToken(requestToken, user.getUuid(), user.getCompany().getUuid());

		MvcResult mvcResult = mockMvc.perform(doPost(ENDPOINT)
				.param("title", "test")
				.param("description", "test")
				.param("pricing_type", "flat")
				.param("pricing_flat_price", "1")
				.param("location_offsite", "1")
				.param("industry_id", "1000")
				.param("scheduled_start_date", String.valueOf(System.currentTimeMillis() / 1000))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		).andExpect(status().isOk()).andReturn();

		final ApiV1Response<ApiAssignmentCreationResponseDTO> response = expectApiV1Response(mvcResult, assignmentCreationResponseType);

		Work work = workService.findWorkByWorkNumber(response.getResponse().getId());

		assertEquals(200, response.getMeta().getStatusCode());
		assertEquals(30L, (work.getPaymentTermsDays()).longValue());
	}
}

