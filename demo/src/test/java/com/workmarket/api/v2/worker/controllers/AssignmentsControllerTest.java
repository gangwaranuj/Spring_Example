package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Pagination;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.fulfillment.AssignmentFulfillmentProcessor;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.model.AssignmentsRequestDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.WorkStatusType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class AssignmentsControllerTest extends BaseApiControllerTest {

	private static final String VALID_WORK_NUMBER = "123456";
	private static final String INVALID_WORK_NUMBER = "xxxxxx";
	private static final String TEST_ASSIGNMENT_TITLE = "This is a test assignment payload.";
	private static final String TEST_ASSIGNMENT_STATUS = "complete";

	private final static String SCHEME = "http";
	private final static String SERVER_NAME = "www.workmarket.com";
	private final static Integer PORT = 80;
	private final static String REQUEST_URI = "/worker/v2/assignments";
	private final static String URL = "http://www.workmarket.com/worker/v2/assignments";

	private final static Long PAGE = 2L;
	private final static Long PAGE_SIZE = 10L;
	private final static Long TOTAL_PAGE_COUNT = 20L;
	private final static Long TOTAL_RECORD_COUNT = 200L;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@InjectMocks private AssignmentsController controller = new AssignmentsController();
	@Mock private AssignmentFulfillmentProcessor fulfillmentProcessor;

	private FulfillmentPayloadDTO listResponse;
	private FulfillmentPayloadDTO detailsResponse;

	@Before
	public void setup() {

		request = new MockHttpServletRequest();
		request.setScheme(SCHEME);
		request.setServerName(SERVER_NAME);
		request.setServerPort(PORT);
		request.setRequestURI(REQUEST_URI);

		response = new MockHttpServletResponse();

		listResponse = new FulfillmentPayloadDTO();
		listResponse.setPayload(setupListPayload());
		listResponse.setPagination(setupTestPagination(request));

		when(fulfillmentProcessor.getListPage(any(ExtendedUserDetails.class),
																					any(AssignmentsRequestDTO.class),
																					any(HttpServletRequest.class))).thenReturn(listResponse);

		detailsResponse = new FulfillmentPayloadDTO();
		detailsResponse.addResponseResult(setupDetailsPayload());

		when(fulfillmentProcessor.getAssignmentDetails(any(ExtendedUserDetails.class), anyString())).thenReturn(
						detailsResponse);
	}

	@Test
	public void getList_withValidRequest_shouldReturnValidResponse() {

		AssignmentsRequestDTO assignmentsRequest = new AssignmentsRequestDTO.Builder()
			.withStatus(new WorkStatusType(WorkStatusType.COMPLETE))
			.build();
		//assignmentsRequest.setFields();
		//assignmentsRequest.setPage();
		//assignmentsRequest.setPageSize();

		final ApiV2Response apiResponse = controller.getWorkerAssignments(assignmentsRequest, request);

		assertEquals(HttpStatus.OK.value(), getMetaInt("code", apiResponse.getMeta()));

		final List<Map<String, Object>> results = apiResponse.getResults();
		assertEquals(1, results.size());

		final Map<String, Object> result = results.get(0);
		assertEquals(VALID_WORK_NUMBER, result.get("id"));
		assertEquals(TEST_ASSIGNMENT_TITLE, result.get("title"));
		assertEquals(TEST_ASSIGNMENT_STATUS, result.get("status"));

		final ApiV2Pagination pagination = (ApiV2Pagination) apiResponse.getPagination();
		assertEquals((Long) PAGE, pagination.getPage());
		assertEquals((Long) PAGE_SIZE, pagination.getPageSize());
		assertEquals((Long) TOTAL_PAGE_COUNT, pagination.getTotalPageCount());
		assertEquals((Long) TOTAL_RECORD_COUNT, pagination.getTotalRecordCount());

		assertEquals(toUriComponents(URL + "?page=3"), toUriComponents(pagination.getLinks().get("next")));
		assertEquals(toUriComponents(URL + "?page=1"), toUriComponents(pagination.getLinks().get("prev")));
		assertEquals(toUriComponents(URL + "?page=1"), toUriComponents(pagination.getLinks().get("first")));
		assertEquals(toUriComponents(URL + "?page=20"), toUriComponents(pagination.getLinks().get("last")));
	}

	@Test
	public void getDetails_withValidWorkNumber_shouldReturnValidResponse() {

		final ApiV2Response apiResponse = controller.getWorkerAssignmentDetails(VALID_WORK_NUMBER, request);

		assertEquals(HttpStatus.OK.value(), getMetaInt("code", apiResponse.getMeta()));

		final List<Map<String, Object>> results = apiResponse.getResults();
		assertEquals(1, results.size());

		final Map<String, Object> result = results.get(0);
		assertEquals(VALID_WORK_NUMBER, result.get("id"));
		assertEquals(TEST_ASSIGNMENT_TITLE, result.get("title"));
		assertEquals(TEST_ASSIGNMENT_STATUS, result.get("status"));

		assertNull(apiResponse.getPagination());
	}

	private List<Map<String, Object>> setupListPayload() {

		final Map<String, Object> map = new HashMap<>();
		map.put("id", VALID_WORK_NUMBER);
		map.put("title", TEST_ASSIGNMENT_TITLE);
		map.put("status", TEST_ASSIGNMENT_STATUS);

		final List<Map<String, Object>> payload = new LinkedList<>();

		payload.add(map);

		return payload;
	}

	private Map<String, Object> setupDetailsPayload() {

		final Map<String, Object> map = new HashMap<>();

		map.put("id", VALID_WORK_NUMBER);
		map.put("title", TEST_ASSIGNMENT_TITLE);
		map.put("status", TEST_ASSIGNMENT_STATUS);

		return map;
	}

	private ApiV2Pagination setupTestPagination(final HttpServletRequest request) {

		final ApiV2Pagination pagination = new ApiV2Pagination.ApiPaginationBuilder().page(PAGE)
						.pageSize(PAGE_SIZE)
						.totalPageCount(TOTAL_PAGE_COUNT)
						.totalRecordCount(TOTAL_RECORD_COUNT)
						.build(request);

		return pagination;
	}

	private UriComponents toUriComponents(final String httpUrl) {

		return UriComponentsBuilder.fromHttpUrl(httpUrl).build();
	}
}
