package com.workmarket.domains.work.service.route;

import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.route.UserRoutingStrategy;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.search.user.WorkResourceSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkRoutingVisitorTest {
	private static final Long WORK_ID = 1L;

	@Mock private WorkResourceSearchService workResourceSearchService;
	@Mock private WorkRoutingSearchRequestBuilder workRoutingSearchRequestBuilder;
	@Mock private RoutingStrategyService routingStrategyService;
	@InjectMocks private WorkRoutingVisitor workRoutingVisitor;

	@Mock AbstractRoutingStrategy routingStrategy;
	@Mock UserRoutingStrategy userRoutingStrategy;
	@Mock AssignmentResourceSearchRequest assignmentResourceSearchRequest;
	@Mock PeopleSearchRequest peopleSearchRequest;
	@Mock Pagination pagination;
	@Mock PeopleSearchResponse peopleSearchResponse;
	@Mock PeopleSearchResponse peopleSearchResponseSecond;
	@Mock PeopleSearchResult result;
	@Mock PeopleSearchResult result2;

	@Before
	public void setup() throws Exception {
		when(workRoutingSearchRequestBuilder.build(any(AbstractRoutingStrategy.class))).thenReturn(assignmentResourceSearchRequest);
		when(assignmentResourceSearchRequest.getRequest()).thenReturn(peopleSearchRequest);
		when(peopleSearchRequest.getPaginationRequest()).thenReturn(pagination);
		when(workResourceSearchService.searchWorkersForAutoRouting(assignmentResourceSearchRequest)).thenReturn(peopleSearchResponse);
		when(peopleSearchResponse.getResults()).thenReturn(Lists.newArrayList(result));
		when(peopleSearchResponseSecond.getResults()).thenReturn(Lists.<PeopleSearchResult>newArrayList());
		when(workResourceSearchService.searchWorkersForAutoRouting(assignmentResourceSearchRequest)).thenReturn(peopleSearchResponse).thenReturn(peopleSearchResponseSecond);
		when(userRoutingStrategy.getType()).thenReturn(UserRoutingStrategy.USER_ROUTING_STRATEGY);
	}

	@Test
	public void shouldReturnEmptyIfNoWorkersWithinSearchRadius() throws Exception {
		when(peopleSearchResponse.getResults()).thenReturn(Lists.<PeopleSearchResult>newArrayList());
		assertEquals(0, workRoutingVisitor.getWorkersWithinTravelDistanceForStrategy(userRoutingStrategy, WORK_ID).size());
	}

	@Test
	public void shouldReturnOnlyWorkersWithinSearchRadius() throws Exception {
		when(result.getDistance()).thenReturn(5d);
		when(result.getMaxTravelDistance()).thenReturn(10f);
		when(result.isSetLocationPoint()).thenReturn(true);
		assertEquals(1, workRoutingVisitor.getWorkersWithinTravelDistanceForStrategy(userRoutingStrategy, WORK_ID).size());
	}

	@Test
	public void shouldNotReturnWorkersOutsideSearchRadius() throws Exception {
		when(result.getDistance()).thenReturn(5d);
		when(result.getMaxTravelDistance()).thenReturn(1f);
		assertEquals(0, workRoutingVisitor.getWorkersWithinTravelDistanceForStrategy(userRoutingStrategy, WORK_ID).size());
	}

	@Test
	public void shouldPaginateSolrResults() throws Exception {
		when(result.isSetLocationPoint()).thenReturn(false);
		when(peopleSearchResponse.getTotalResultsCount()).thenReturn(2);
		workRoutingVisitor.getWorkersWithinTravelDistanceForStrategy(userRoutingStrategy, WORK_ID);
		verify(workResourceSearchService, times(2)).searchWorkersForAutoRouting(any(AssignmentResourceSearchRequest.class));
	}
}
