package com.workmarket.api.v2.employer.search.worker.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v2.ApiV2BaseIT;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.search.common.model.LongFilter;
import com.workmarket.api.v2.employer.search.common.model.Scorecard;
import com.workmarket.api.v2.employer.search.common.model.StringFilter;
import com.workmarket.api.v2.employer.search.worker.model.ViewType;
import com.workmarket.api.v2.employer.search.worker.model.Worker;
import com.workmarket.api.v2.employer.search.worker.model.WorkerDetailsRequestDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerFilters;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRecord;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRequestDTO;
import com.workmarket.search.worker.MockFindWorkerClient;
import com.workmarket.test.IntegrationTest;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.workmarket.common.util.Util.safeToJson;

/**
 * Test cases for our worker search API.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkerSearchControllerIT extends ApiV2BaseIT {
	private static final String ENDPOINT_ROOT = "/employer/v2/search/workers";

	private static final TypeReference<ApiV2Response<WorkerSearchRecord>> workerSearchRecordBuilderType = new TypeReference<ApiV2Response<WorkerSearchRecord>>() {};
	private static final TypeReference<ApiV2Response<WorkerFilters>> workerSearchFiltersBuilderType = new TypeReference<ApiV2Response<WorkerFilters>>() {};
	private static final TypeReference<ApiV2Response<Worker>> workerBuilderType = new TypeReference<ApiV2Response<Worker>>() {};

	@Autowired MockFindWorkerClient mockFindWorkerClient;

	/**
	 * Initialize our test environment.
	 * @throws Exception Thrown if we cannot login
	 */
	@Before
	public void setUp() throws Exception {
		login();
	}


	/**
	 * Tests doing a worker search.
	 * @throws Exception Thrown if there is a failure in our requst
	 */
	@Test
	public void testSearchWorkers() throws Exception {
		WorkerSearchRequestDTO workerSearchRequestDTO = new WorkerSearchRequestDTO.Builder()
			.setKeyword("networking")
			.setOffset(0L)
			.setLimit(10L)
			.build();

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT_ROOT + "/query").header("Authorization", "Bearer " + accessToken).content(safeToJson(workerSearchRequestDTO))
		).andExpect(status().isOk()).andReturn();

		final ApiV2Response<WorkerSearchRecord> response = expectApiV2Response(mvcResult, workerSearchRecordBuilderType);

		// verify our results
		ApiJSONPayloadMap metadata = response.getMeta();
		assertNotNull(metadata.get("limit"));
		assertNotNull(metadata.get("offset"));
		assertNotNull(metadata.get("resultCount"));
		assertNotNull(metadata.get("queryTimeMillis"));
		assertNotNull(metadata.get("requestId"));

		assertTrue(response.getResults().size() > 0);

		for (WorkerSearchRecord searchRecordBuilder : response.getResults()) {
			WorkerSearchRecord searchRecord = searchRecordBuilder;

			assertNotNull(searchRecord.getUuid());
			assertNotNull(searchRecord.getScore());
			assertNull(searchRecord.getHighlights());
		}
	}

	/**
	 * Tests executing a search for workers with an error.
	 *
	 * In the current implementation of CompanyServiceService (and mimic'd by our mock)
	 * when there is an error it returns an empty response, not an error so the client
	 * will get back an empty response rather than an error condition.
	 *
	 *
	 * @throws Exception Thrown if there is a failure in our requst
	 */
	@Test
	public void testSearchWorkersWithError() throws Exception {
		WorkerSearchRequestDTO workerSearchRequestDTO = new WorkerSearchRequestDTO.Builder()
			.setKeyword("some missing keyword")
			.setOffset(0L)
			.setLimit(10L)
			.build();

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT_ROOT + "/query").header("Authorization", "Bearer " + accessToken).content(safeToJson(workerSearchRequestDTO))
		).andExpect(status().isBadRequest()).andReturn();

		final ApiV2Response<WorkerSearchRecord> response = expectApiV2Response(mvcResult, workerSearchRecordBuilderType);

		// verify our results
		ApiJSONPayloadMap metadata = response.getMeta();
		assertNotNull(metadata.get("requestId"));
		assertNotNull(metadata.get("message"));

		assertEquals(0, response.getResults().size());

	}

	/**
	 * Tests doing a worker search for filters.
	 * @throws Exception Thrown if there is a failure in our requst
	 */
	@Test
	public void testSearchWorkerFilters() throws Exception {
		WorkerSearchRequestDTO workerSearchRequestDTO = new WorkerSearchRequestDTO.Builder()
			.setKeyword("filters-networking")
			.build();

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT_ROOT + "/filters").content(safeToJson(workerSearchRequestDTO))
		).andExpect(status().isOk()).andReturn();

		final ApiV2Response<WorkerFilters> response = expectApiV2Response(mvcResult, workerSearchFiltersBuilderType);

		// verify our results
		ApiJSONPayloadMap metadata = response.getMeta();
		assertNotNull(metadata.get("resultCount"));
		assertNotNull(metadata.get("queryTimeMillis"));
		assertNotNull(metadata.get("requestId"));

		assertTrue(response.getResults().size() == 1);

		for (WorkerFilters workerFilters : response.getResults()) {
			validateLongFilter(workerFilters.getAssessments());
			validateStringFilter(workerFilters.getAvatars());
			validateLongFilter(workerFilters.getCertifications());
			validateLongFilter(workerFilters.getCompanies());
			validateStringFilter(workerFilters.getCompanyTypes());
			validateStringFilter(workerFilters.getCountries());
			validateLongFilter(workerFilters.getIndustries());
			validateStringFilter(workerFilters.getLanes());
			validateLongFilter(workerFilters.getLicenses());
			validateLongFilter(workerFilters.getGroups());
			validateLongFilter(workerFilters.getSharedGroups());
			validateStringFilter(workerFilters.getVerifications());
		}
	}

	private void validateStringFilter(final List<StringFilter> filterDetails) {
		assertNotNull(filterDetails);
		if (filterDetails.size() > 0) {
			for (StringFilter filter : filterDetails) {
				assertNotNull(filter.getCount());
				assertNotNull(filter.getId());
				assertNotNull(filter.getName());

			}
		}
	}

	private void validateLongFilter(final List<LongFilter> filterDetails) {
		assertNotNull(filterDetails);

		if (filterDetails.size() > 0) {
			for (LongFilter filter : filterDetails) {
				assertNotNull(filter.getCount());
				assertNotNull(filter.getId());
				assertNotNull(filter.getName());

			}
		}
	}

	/**
	 * Tests doing a worker search for filters where there is an error getting a response from
	 * the service.
	 *
	 * In the current implementation of CompanyServiceService (and mimic'd by our mock)
	 * when there is an error it returns an empty response, not an error so the client
	 * will get back an empty response rather than an error condition.
	 *
	 *
	 * @throws Exception Thrown if there is a failure in our requst
	 */
	@Test
	public void testSearchWorkerFiltersWithError() throws Exception {
		WorkerSearchRequestDTO workerSearchRequestDTO = new WorkerSearchRequestDTO.Builder()
			.setKeyword("invalidKeyword")
			.setOffset(0L)
			.setLimit(10L)
			.build();

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT_ROOT + "/filters").header("Authorization", "Bearer " + accessToken).content(safeToJson(workerSearchRequestDTO))
		).andExpect(status().isBadRequest()).andReturn();

		final ApiV2Response<WorkerFilters> response = expectApiV2Response(mvcResult, workerSearchFiltersBuilderType);

		// verify our results
		ApiJSONPayloadMap metadata = response.getMeta();
		assertNotNull(metadata.get("message"));
		assertNotNull(metadata.get("requestId"));

		assertEquals(0, response.getResults().size());
	}

	/**
	 * Tests retrieving our worker details.
	 *
	 * This relies on the database to retrieve the details.
	 *
	 * @throws Exception Thrown if there is a failure in our requst
	 */
	@Test
	public void testWorkerDetails() throws Exception {
		// this user is our Testing Worker qa+r@workmarket.com account
		final String workerUuid = "808a9128-713d-2931-b330-dabd3ad0b3c9";

		WorkerDetailsRequestDTO workerSearchRequestDTO = new WorkerDetailsRequestDTO.Builder()
			.addUuid(workerUuid)
			.setViewType(ViewType.search_card)
			.build();

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT_ROOT + "/view").content(safeToJson(workerSearchRequestDTO))
		).andExpect(status().isOk()).andReturn();

		final ApiV2Response<Worker> response = expectApiV2Response(mvcResult, workerBuilderType);

		// verify our results
		ApiJSONPayloadMap metadata = response.getMeta();
		assertNotNull(metadata.get("requestId"));

		assertEquals(1, response.getResults().size());

		for (Worker workerBuilder : response.getResults()) {
			Worker worker = workerBuilder;

			//assertNotNull(worker.getAvatarAssetUri());
			assertNotNull(worker.getBlocked());
			assertNotNull(worker.getCity());
			assertNotNull(worker.getCompanyName());
			assertNotNull(worker.getCompanyScorecard());
			assertNotNull(worker.getCountry());
			assertNotNull(worker.getCreatedOn());
			assertNotNull(worker.getPostalCode());
			assertNotNull(worker.getScorecard());
			assertNotNull(worker.getState());

			Scorecard companyScorecard = worker.getCompanyScorecard();
			assertNotNull(companyScorecard.getDeliverableOnTimePercentage());
			assertNotNull(companyScorecard.getOnTimePercentage());
			assertNotNull(companyScorecard.getSatisfactionRate());
			assertNotNull(companyScorecard.getWorkAbandonedCount());
			assertNotNull(companyScorecard.getWorkCancelledCount());
			assertNotNull(companyScorecard.getWorkCompletedCount());

			Scorecard scorecard = worker.getScorecard();
			assertNotNull(scorecard.getDeliverableOnTimePercentage());
			assertNotNull(scorecard.getOnTimePercentage());
			assertNotNull(scorecard.getSatisfactionRate());
			assertNotNull(scorecard.getWorkAbandonedCount());
			assertNotNull(scorecard.getWorkCancelledCount());
			assertNotNull(scorecard.getWorkCompletedCount());

		}
	}

	/**
	 * Tests retrieving our worker details there is no matching data - should return an empty response.
	 *
	 * This hits the database so tough to inject an exception in
	 *
	 * @throws Exception Thrown if there is a failure in our requst
	 */
	@Test
	public void testWorkerDetailsInvalidUserNumber() throws Exception {
		WorkerDetailsRequestDTO workerSearchRequestDTO = new WorkerDetailsRequestDTO.Builder()
			.addUuid(UUID.randomUUID().toString())
			.setViewType(ViewType.search_card)
			.build();

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT_ROOT + "/view").content(safeToJson(workerSearchRequestDTO))
		).andExpect(status().isOk()).andReturn();

		final ApiV2Response<Worker> response = expectApiV2Response(mvcResult, workerBuilderType);

		// verify our results
		ApiJSONPayloadMap metadata = response.getMeta();
		assertNotNull(metadata.get("requestId"));

		assertTrue(CollectionUtils.isEmpty(response.getResults()));
	}

	/**
	 * Tests retrieving our worker details where we have an exception.
	 *
	 * @throws Exception Thrown if there is a failure in our requst
	 */
	@Test
	public void testWorkerDetailsException() throws Exception {
		// register our exception
		mockFindWorkerClient.registerException("exception", new RuntimeException("Failed executing hystrix"));

		WorkerDetailsRequestDTO workerSearchRequestDTO = new WorkerDetailsRequestDTO.Builder()
			.setViewType(ViewType.search_card)
			.build();

		MvcResult mvcResult = mockMvc.perform(
			doPost(ENDPOINT_ROOT + "/view").header("Authorization", "Bearer " + accessToken).content(safeToJson(workerSearchRequestDTO))
		).andExpect(status().isBadRequest()).andReturn();

		final ApiV2Response<Worker> response = expectApiV2Response(mvcResult, workerBuilderType);

		// verify our results
		ApiJSONPayloadMap metadata = response.getMeta();
		assertNotNull(metadata.get("requestId"));
		assertNotNull(metadata.get("message"));

		assertTrue(CollectionUtils.isEmpty(response.getResults()));
	}

}
