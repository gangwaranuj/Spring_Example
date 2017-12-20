package com.workmarket.api.v2.employer.settings.controllers;

import com.google.common.collect.Sets;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.search.worker.model.Worker;
import com.workmarket.api.v2.employer.settings.models.CompanyWorkersDTO;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static com.workmarket.api.v2.employer.assignments.controllers.support.TypeReferences.companyWorkersType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CompanyWorkersControllerIT extends ApiV2BaseIT {
	private static final String ENDPOINT = "/employer/v2/settings/profile";

	@Before
	public void setUp() throws Exception {
		login();
	}

	@Test
	public void findCompanyWorkersWithTwoWorkersReturnTwo() throws Exception {
		final Company company = companyService.createCompany("Test", false, "unknown");
		final User user1 = newCompanyEmployeeSharedWorkerApproved(company.getId());
		final User user2 = newCompanyEmployeeSharedWorkerApproved(company.getId());

		final MvcResult mvcResult = mockMvc.perform(doGet(ENDPOINT + "/" + company.getCompanyNumber() + "/workers"))
						.andExpect(status().isOk())
						.andReturn();

		final ApiV2Response<CompanyWorkersDTO> resp = expectApiV2Response(mvcResult, companyWorkersType);
		final CompanyWorkersDTO dto = resp.getResults().get(0);
		assertTrue(dto.getWorkers().size() == 2);

		final Set<String> userNumbers = Sets.newHashSet(user1.getUserNumber(), user2.getUserNumber());
		assertTrue(userNumbers.contains(dto.getWorkers().get(0).getUserNumber()));
		assertTrue(userNumbers.contains(dto.getWorkers().get(1).getUserNumber()));
	}

	@Test
	public void findCompanyWorkersWithOneWorkerOffsetLargerThanZeroReturnZero() throws Exception {
		final Company company = companyService.createCompany("Test", false, "unknown");
		final User user = newCompanyEmployeeWorkerConfirmed(company.getId());

		final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/" + company.getCompanyNumber() + "/workers")
			.header("Authorization", "Bearer " + accessToken)
			.param("offset", "1")).andExpect(status().isOk()).andReturn();

		final ApiV2Response<CompanyWorkersDTO> resp = expectApiV2Response(mvcResult, companyWorkersType);
		final CompanyWorkersDTO dto = resp.getResults().get(0);
		assertTrue(dto.getWorkers().size() == 0);
	}

    @Test
    public void findCompanyWorkersWithTwoWorkersLimitOneReturnFirstOne() throws Exception {
        final Company company = companyService.createCompany("Test", false, "unknown");
        final User user1 = newCompanyEmployeeSharedWorkerApproved(company.getId());
        final User user2 = newCompanyEmployeeSharedWorkerApproved(company.getId());

		final MvcResult mvcResult = mockMvc.perform(
			doGet(ENDPOINT + "/" + company.getCompanyNumber() + "/workers").param("limit", "1"))
			.andExpect(status().isOk()).andReturn();

		final ApiV2Response<CompanyWorkersDTO> resp = expectApiV2Response(mvcResult, companyWorkersType);
		final CompanyWorkersDTO dto = resp.getResults().get(0);
		assertTrue(dto.getWorkers().size() == 1);

		final Worker worker = dto.getWorkers().get(0);
		assertEquals(user1.getUserNumber(), worker.getUserNumber());
	}

	@Test
	public void findCompanyWorkersWithoutWorkersReturnZero() throws Exception {
		final Company company = companyService.createCompany("Test", false, "unknown");

		final MvcResult mvcResult = mockMvc.perform(doGet(ENDPOINT + "/" + company.getCompanyNumber() + "/workers"))
			.andExpect(status().isOk())
			.andReturn();

		final ApiV2Response<CompanyWorkersDTO> resp = expectApiV2Response(mvcResult, companyWorkersType);
		final CompanyWorkersDTO dto = resp.getResults().get(0);
		assertTrue(dto.getWorkers().size() == 0);
	}
}
