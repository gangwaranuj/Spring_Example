package com.workmarket.api.v2.worker.fulfillment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.workmarket.api.v2.worker.marshaller.AssignmentMarshaller;
import com.workmarket.api.v2.worker.model.AssignmentsRequestDTO;
import com.workmarket.api.v2.worker.service.AssignmentService;
import com.workmarket.api.v2.worker.service.XWork;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.RequestContext;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class AssignmentFulfillmentProcessorTest {

	private final WorkStatusType availableStatus = new WorkStatusType(WorkStatusType.AVAILABLE);

	private MockHttpServletRequest request;

	@InjectMocks private AssignmentFulfillmentProcessor fulfillmentProcessor = new AssignmentFulfillmentProcessor();
	@Mock private AssignmentService assignmentService;
	@Mock private XWork xWork;
	@Mock private AssignmentMarshaller assignmentMarshaller;
	@Mock private ExtendedUserDetails userDetails;

	@Before
	public void setup() {

		request = new MockHttpServletRequest();
		request.setScheme("http");
		request.setServerName("www.workmarket.com");
		request.setServerPort(80);
		request.setRequestURI("/worker/v2/assignments");
	}

	@Test
	public void getListPage_goodRequest_processed() {

		final Map<String, Object> pagination = ImmutableMap.<String, Object>of(
			"page",
			3,
			"hasMore",
			Boolean.TRUE,
			"totalPages",
			10,
			"totalResults",
			100,
			"pageSize",
			10
		);

		final Map row1 = ImmutableMap.<String, Object>of(
			"id",
			"5467382",
			"work_id",
			5467382L,
			"work_number",
			"5467382",
			"title",
			"test work"
		);

		final List rows = ImmutableList.of(row1);

		when(xWork.getWork(any(ExtendedUserDetails.class),
			anyString(),
			anySet(),
			anySet())).thenReturn(new com.workmarket.thrift.work.WorkResponse());

		when(assignmentService.getList(eq(availableStatus), (String[]) anyObject(), eq(3), eq(10), anyString())).thenReturn(
			ImmutableMap.<String, Object>of("rows", rows, "pagination", pagination));

		final AssignmentsRequestDTO assignmentsRequestDTO = new AssignmentsRequestDTO.Builder()
			.withStatus(availableStatus)
			.withPage(3)
			.withPageSize(10)
			.build();

		final FulfillmentPayloadDTO response = fulfillmentProcessor.getListPage(
			userDetails,
			assignmentsRequestDTO,
			request
		);

		verify(assignmentService, times(1)).getList(eq(availableStatus),
			(String[]) anyObject(),
			eq(3),
			eq(10),
			anyString());

		verify(assignmentMarshaller, times(1)).getListFulfillmentResponse(eq(rows),
			(Map) anyObject(),
			(FulfillmentPayloadDTO) anyObject());

		verify(assignmentMarshaller, times(1)).generatePaginationFromWorkControllerResponse(pagination, request);
	}

	@Test
	public void getFeedPage_goodRequest_processed() {

		final Map<String, Object> pagination = ImmutableMap.<String, Object>of(
			"page", 1,
			"hasMore", Boolean.TRUE,
			"totalPages", 10,
			"totalResults", 246,
			"pageSize", 25
		);

		final Map row1 = ImmutableMap.<String, Object>of(
			"id", "5467382",
			"work_id", 5467382L,
			"work_number", "5467382",
			"title", "test work"
		);

		final List rows = ImmutableList.of(row1);

		when(assignmentService.getWorkFeed(eq("keyword"),
			anyInt(),
			eq(40.8),
			eq(-73.8),
			anyDouble(),
			anyBoolean(),
			(String[]) anyObject(),
			eq(0),
			eq(1))
		).thenReturn(ImmutableMap.<String, Object>of(
			"rows", rows,
			"pagination", pagination)
		);

		final ExtendedUserDetails user = mock(ExtendedUserDetails.class);

		final FulfillmentPayloadDTO response = fulfillmentProcessor.getFeedPage(
			user,
			request,
			"keyword",
			1000,
			40.8,
			-73.8,
			10.0,
			false,
			new String[0],
			0,
			1
		);

		verify(assignmentService, times(1)).getWorkFeed(
			eq("keyword"),
			anyInt(),
			eq(40.8),
			eq(-73.8),
			anyDouble(),
			anyBoolean(),
			(String[]) anyObject(),
			eq(0),
			eq(1)
		);

		verify(assignmentMarshaller, times(1)).getListFulfillmentResponse(eq(rows),
			(Map) anyObject(),
			(FulfillmentPayloadDTO) anyObject());

		verify(assignmentMarshaller, times(1)).generatePaginationFromWorkControllerResponse(pagination, request);
	}

	@Test
	public void getAssignmentDetails_goodRequest_processed() {

		final WorkResponse workResponse = getWorkResponseNotBundle();

		final List<PartDTO> partsList = new LinkedList();

		when(assignmentService.getAssignmentDetails(anyString())).thenReturn(workResponse);

		when(assignmentService.getPartsForAssignmentDetails(workResponse)).thenReturn(partsList);

		final ExtendedUserDetails user = mock(ExtendedUserDetails.class);

		when(user.isInternal()).thenReturn(false);

		final FulfillmentPayloadDTO response = fulfillmentProcessor.getAssignmentDetails(user, "12345678");

		verify(assignmentService, times(1)).getAssignmentDetails("12345678");

		verify(assignmentService, times(1)).getPartsForAssignmentDetails(workResponse);

		verify(assignmentMarshaller, times(1)).getDetailsFulfillmentResponse(eq(workResponse),
			(List<CustomField>) anyObject(),
			eq(partsList),
			(FulfillmentPayloadDTO) anyObject(),
			any(Long.class));
	}

	@Test(expected = HttpException404.class)
	public void getAssignmentDetails_noneFound_404Error() {

		when(assignmentService.getAssignmentDetails(anyString())).thenReturn(null);

		final FulfillmentPayloadDTO response = fulfillmentProcessor.getAssignmentDetails(null, "87654321");
	}

	@Test(expected = HttpException401.class)
	public void getAssignmentBundleDetails_NotAuthorized_401Error() {

		final WorkResponse workResponse = new WorkResponse();
		workResponse.setWorkBundle(true);
		workResponse.setWork(new Work().setId(12345));

		when(assignmentService.getAssignmentDetails(anyString())).thenReturn(workResponse);

		final ExtendedUserDetails user = mock(ExtendedUserDetails.class);

		when(user.getId()).thenReturn(68123987L);

		when(assignmentService.authorizeBundleView(12345, user)).thenReturn(false);

		FulfillmentPayloadDTO response = fulfillmentProcessor.getAssignmentDetails(null, "87654321");
	}


	@Test(expected = HttpException401.class)
	public void getAssignmentBundleDetails_bundlePendingRouting_401Error() {

		final WorkResponse workResponse = new WorkResponse();
		workResponse.setWorkBundle(true);
		workResponse.setWork(new Work().setId(12345));

		when(assignmentService.getAssignmentDetails(anyString())).thenReturn(workResponse);

		final ExtendedUserDetails user = mock(ExtendedUserDetails.class);

		when(user.getId()).thenReturn(68123987L);

		when(assignmentService.authorizeBundleView(12345, user)).thenReturn(true);

		when(assignmentService.authorizeBundlePendingRouting(12345, 68123987L)).thenReturn(false);

		final FulfillmentPayloadDTO response = fulfillmentProcessor.getAssignmentDetails(null, "87654321");
	}

	private WorkResponse getWorkResponseNotBundle() {

		final Set<RequestContext> requestContexts = ImmutableSet.of(RequestContext.ACTIVE_RESOURCE);
		final Set<AuthorizationContext> authorizationContexts = ImmutableSet.of(AuthorizationContext.ACTIVE_RESOURCE);

		final WorkResponse workResponse = new WorkResponse();
		workResponse.setWorkBundle(false);
		workResponse.setRequestContexts(requestContexts);
		workResponse.setAuthorizationContexts(authorizationContexts);

		return workResponse;
	}
}
