package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Pagination;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.fulfillment.AssignmentFulfillmentProcessor;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.model.FeedRequestDTO;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.User;
import com.workmarket.domains.velvetrope.doorman.HideWorkFeedDoorman;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.web.forms.feed.FeedRequestParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedControllerTest extends BaseApiControllerTest {

	private static final String TEST_ASSIGNMENT_ID = "12345678";
	private static final String TEST_ASSIGNMENT_TITLE = "This is a test assignment payload.";

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@Mock
	private AssignmentFulfillmentProcessor fulfillmentProcessor;
	@Mock
	private ProfileService profileService;
	@Spy
	private HideWorkFeedDoorman hideWorkFeedDoorman = new HideWorkFeedDoorman();

	@InjectMocks
	private FeedController controller = new FeedController();

	private FulfillmentPayloadDTO feedResponse;
	private ArgumentCaptor<FeedRequestParams> feedRequestParamsArgumentCaptor;

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		request = new MockHttpServletRequest();
		request.setScheme("http");
		request.setServerName("www.workmarket.com");
		request.setServerPort(80);
		request.setRequestURI("/worker/v2/feed");

		response = new MockHttpServletResponse();

		feedResponse = new FulfillmentPayloadDTO();
		feedResponse.setPayload(setupPayload());
		feedResponse.setPagination(setupTestPagination(request));

		feedRequestParamsArgumentCaptor = ArgumentCaptor.forClass(FeedRequestParams.class);

		ProfileDTO profileDTO = new ProfileDTO();
		profileDTO.setPostalCode("10001");

		when(profileService.findProfileDTO(anyLong()))
			.thenReturn(profileDTO);

		when(fulfillmentProcessor.getFeedPage(any(ExtendedUserDetails.class), any(HttpServletRequest.class),
			feedRequestParamsArgumentCaptor.capture()))
			.thenReturn(feedResponse);
	}

	@Test
	public void getFeed_withValidRequest_shouldReturnValidResponse() {

		final FeedRequestDTO feedRequestDTO = new FeedRequestDTO.Builder()
			.withKeyword("keyword")
			//.withIndustryId(null)
			.withLatitude(40.8)
			.withLongitude(-72.3)
			.withRadius(null)
			.withVirtual(null)
			.withFields("")
			.withWhen("all")
			.withPage(1)
			.withPageSize(2)
			.build();

		final ApiV2Response apiResponse = controller
			.getFeed
				(
					feedRequestDTO,
					request
				);

		expectStatusCode(HttpStatus.OK.value(), apiResponse.getMeta());

		List<Map<String, Object>> results = apiResponse.getResults();
		assertEquals(1, results.size());

		Map<String, Object> result = results.get(0);
		assertEquals(TEST_ASSIGNMENT_ID, result.get("id"));
		assertEquals(TEST_ASSIGNMENT_TITLE, result.get("title"));

		ApiV2Pagination pagination = (ApiV2Pagination) apiResponse.getPagination();
		assertEquals((Long) 2L, pagination.getPage());
		assertEquals((Long) 25L, pagination.getPageSize());
		assertEquals((Long) 9L, pagination.getTotalPageCount());
		assertEquals((Long) 205L, pagination.getTotalRecordCount());

		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/feed?page=3"),
			toUriComponents(pagination.getLinks().get("next")));

		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/feed?page=1"),
			toUriComponents(pagination.getLinks().get("prev")));

		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/feed?page=1"),
			toUriComponents(pagination.getLinks().get("first")));

		assertEquals(toUriComponents("http://www.workmarket.com/worker/v2/feed?page=9"),
			toUriComponents(pagination.getLinks().get("last")));
	}

	@Test
	public void testFeedWithAppliedFilteredOut() throws Exception {
		final FeedRequestDTO feedRequestDTO = new FeedRequestDTO.Builder()
			.withKeyword("keyword")
			.withLatitude(40.8)
			.withLongitude(-72.3)
			.withRadius(null)
			.withVirtual(null)
			.withFields("")
			.withWhen("all")
			.withPage(1)
			.withPageSize(2)
			.withFilterOutApplied(true)
			.build();

		final ApiV2Response apiResponse = controller.getFeed(feedRequestDTO, request);

		FeedRequestParams requestParams = feedRequestParamsArgumentCaptor.getValue();
		assertTrue(requestParams.isFilterOutApplied());
	}

	@Test
	public void testFeedWithDefaultSort() throws Exception {
		controller.getFeed(new FeedRequestDTO.Builder().build(), request);

		final FeedRequestParams requestParams = feedRequestParamsArgumentCaptor.getValue();
		assertEquals("-" + WorkSearchableFields.SEND_DATE.getName(), requestParams.getSort().get(0));
	}

	@Test
	public void testDTOtoFeedRequestParamsTranslation() throws Exception {
		ArrayList sortArray = new ArrayList<String>();
		sortArray.add("Test");
		sortArray.add("Test2");

		ArrayList filterArray = new ArrayList<String>();
		filterArray.add("industry=1");
		filterArray.add("name=tom");

		final FeedRequestDTO feedRequestDTO = new FeedRequestDTO.Builder()
			.withKeyword("keyword")
			.withIndustryId(5)
			.withLatitude(40.8)
			.withLongitude(-72.3)
			.withRadius(5.0)
			.withVirtual(false)
			.withFields("") // This isn't used anywhere yet
			.withWhen("none")
			.withPage(2)
			.withPageSize(28)
			.withFilterOutApplied(true)
			.withSort(sortArray)
			.withStartDate(123456789L)
			.withEndDate(987654321L)
			.withFilter(filterArray)
			.build();

		final ApiV2Response apiResponse = controller.getFeed(feedRequestDTO, request);

		FeedRequestParams requestParams = feedRequestParamsArgumentCaptor.getValue();

		assertEquals(requestParams.getKeyword(), "keyword");
		assertEquals(requestParams.getIndustryId(), "5");
		assertEquals(requestParams.getLatitude(), "40.8");
		assertEquals(requestParams.getLongitude(), "-72.3");
		assertEquals(requestParams.getDistanceInMiles(), "5.0");
		assertEquals(requestParams.isVirtual(), false);
		assertEquals(requestParams.getWhen(), "none");
		assertEquals((int) requestParams.getStart(), 28); //Start is 0 indexed, while page is 1 indexed.  Page 2 should
		// start at element 28
		assertEquals((int) requestParams.getLimit(), 28);
		assertTrue(requestParams.isFilterOutApplied());
		assertEquals(requestParams.getSort().size(), 2);
		assertTrue(requestParams.getSort().contains("Test"));
		assertTrue(requestParams.getSort().contains("Test2"));
		assertEquals((long) requestParams.getStartDate(), 123456789L);
		assertEquals((long) requestParams.getEndDate(), 987654321L);

		assertEquals(requestParams.getFilter().size(), 2);
		assertTrue(requestParams.getFilter().contains("industry=1"));
		assertTrue(requestParams.getFilter().contains("name=tom"));
	}

	@Test
	public void testDTOtoFeedRequestParamsTranslationAndWorkerRole() throws Exception {

		when(userService.isEmployeeWorker(any(User.class))).thenReturn(true);
		when(userService.getUser(user.getId())).thenReturn(user);

		ArrayList sortArray = new ArrayList<String>();
		sortArray.add("Test");
		sortArray.add("Test2");

		ArrayList filterArray = new ArrayList<String>();
		filterArray.add("industry=1");
		filterArray.add("name=tom");

		final FeedRequestDTO feedRequestDTO = new FeedRequestDTO.Builder()
			.withKeyword("keyword")
			.withIndustryId(5)
			.withLatitude(40.8)
			.withLongitude(-72.3)
			.withRadius(5.0)
			.withVirtual(false)
			.withFields("") // This isn't used anywhere yet
			.withWhen("none")
			.withPage(2)
			.withPageSize(28)
			.withFilterOutApplied(true)
			.withSort(sortArray)
			.withStartDate(123456789L)
			.withEndDate(987654321L)
			.withFilter(filterArray)
			.build();

		final ApiV2Response apiResponse = controller.getFeed(feedRequestDTO, request);

		FeedRequestParams requestParams = feedRequestParamsArgumentCaptor.getValue();

		assertEquals(user.getCompany().getId(), requestParams.getExclusiveCompanyIds().get(0));
		assertEquals(requestParams.getKeyword(), "keyword");
		assertEquals(requestParams.getIndustryId(), "5");
		assertEquals(requestParams.getLatitude(), "40.8");
		assertEquals(requestParams.getLongitude(), "-72.3");
		assertEquals(requestParams.getDistanceInMiles(), "5.0");
		assertEquals(requestParams.isVirtual(), false);
		assertEquals(requestParams.getWhen(), "none");
		assertEquals((int) requestParams.getStart(), 28); //Start is 0 indexed, while page is 1 indexed.  Page 2 should
		// start at element 28
		assertEquals((int) requestParams.getLimit(), 28);
		assertTrue(requestParams.isFilterOutApplied());
		assertEquals(requestParams.getSort().size(), 2);
		assertTrue(requestParams.getSort().contains("Test"));
		assertTrue(requestParams.getSort().contains("Test2"));
		assertEquals((long) requestParams.getStartDate(), 123456789L);
		assertEquals((long) requestParams.getEndDate(), 987654321L);

		assertEquals(requestParams.getFilter().size(), 2);
		assertTrue(requestParams.getFilter().contains("industry=1"));
		assertTrue(requestParams.getFilter().contains("name=tom"));
	}


	private List<Map<String, Object>> setupPayload() {

		List<Map<String, Object>> payload = new LinkedList<>();
		Map<String, Object> itemMap = new HashMap<>();

		itemMap.put("id", TEST_ASSIGNMENT_ID);
		itemMap.put("title", TEST_ASSIGNMENT_TITLE);

		payload.add(itemMap);
		return payload;
	}

	private ApiV2Pagination setupTestPagination(final HttpServletRequest request) {

		final ApiV2Pagination pagination = new ApiV2Pagination
			.ApiPaginationBuilder()
			.page(2L)
			.pageSize(25L)
			.totalPageCount(9L)
			.totalRecordCount(205L)
			.build(request);

		return pagination;
	}

	private UriComponents toUriComponents(final String httpUrl) {

		return UriComponentsBuilder
			.fromHttpUrl(httpUrl)
			.build();
	}
}
