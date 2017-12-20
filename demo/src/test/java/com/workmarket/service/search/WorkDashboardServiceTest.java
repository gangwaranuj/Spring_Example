package com.workmarket.service.search;

import com.google.common.base.Optional;
import com.workmarket.business.decision.gen.Messages.Decision;
import com.workmarket.business.decision.gen.Messages.GetDoableDecisionsRequest;
import com.workmarket.dao.UserDAO;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.work.service.dashboard.DashboardResultService;
import com.workmarket.domains.work.service.dashboard.WorkDashboardResultParser;
import com.workmarket.domains.work.service.dashboard.WorkDashboardServiceImpl;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.DashboardResponse;
import com.workmarket.search.response.work.DashboardResponseSidebar;
import com.workmarket.search.response.work.DashboardResultList;
import com.workmarket.search.response.work.DashboardStatusFilter;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.WorkAggregatesDTO;
import com.workmarket.service.decisionflow.DecisionFlowService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.search.work.WorkSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkDashboardServiceTest {

	private static final List<Decision> EMPTY_LIST_FOR_DECISIONS = Collections.EMPTY_LIST;
	private static final String USER_UUID = "user-uuid";
	private static final GetDoableDecisionsRequest REQUEST = GetDoableDecisionsRequest.newBuilder()
		.setDeciderUuid(USER_UUID)
		.build();

	@Mock private WorkSearchService workSearchService;
	@Mock private WorkDashboardResultParser workDashboardResultParser;
	@Mock private DashboardResultService dashboardResultService;
	@Mock private UserDAO userDAO;
	@Mock private UserService userService;
	@Mock private AuthenticationService authenticationService;
	@Mock private DecisionFlowService decisionFlowService;

	@InjectMocks WorkDashboardServiceImpl workDashboardService = spy(new WorkDashboardServiceImpl());

	private Long userId = 1L;
	private String userNumber = "12345";

	private WorkSearchRequest workSearchRequest;
	private WorkSearchRequest availableSearchRequest;
	private User user;
	private WorkSearchResponse workSearchResponse;
	private DashboardResponse dashboardResponse;
	private DashboardResultList dashboardResultList;
	private DashboardResponseSidebar sidebar;
	private WorkAggregatesDTO workAggregatesDTO;
	private Optional personaPreferenceOptional;
	private PersonaPreference personaPreference;

	@Before
	public void setup() {
		user = mock(User.class);
		workSearchRequest = mock(WorkSearchRequest.class);
		availableSearchRequest = mock(WorkSearchRequest.class);
		dashboardResponse = mock(DashboardResponse.class);
		workSearchResponse = mock(WorkSearchResponse.class);
		workAggregatesDTO = mock(WorkAggregatesDTO.class);
		dashboardResultList = mock(DashboardResultList.class);
		sidebar = mock(DashboardResponseSidebar.class);
		personaPreferenceOptional = mock(Optional.class);
		personaPreference = mock(PersonaPreference.class);

		when(user.getId()).thenReturn(1L);
		when(user.getUuid()).thenReturn("USER_UUID");
		when(userDAO.findUserByUserNumber(anyString(), anyBoolean())).thenReturn(user);
		when(userService.getPersonaPreference(user.getId())).thenReturn(personaPreferenceOptional);
		when(personaPreferenceOptional.isPresent()).thenReturn(true);
		when(personaPreferenceOptional.get()).thenReturn(personaPreference);
		when(personaPreference.isBuyer()).thenReturn(true);

		when(workSearchService.searchAllWorkByUserId(anyLong(), any(WorkSearchRequest.class))).thenReturn(workSearchResponse, workSearchResponse);
		when(workSearchResponse.getDashboardResultList()).thenReturn(dashboardResultList);
		when(workSearchResponse.getSidebar()).thenReturn(sidebar);
		when(workSearchResponse.getAggregates()).thenReturn(workAggregatesDTO);
		when(workDashboardResultParser.parseResult(anyCollectionOf(SolrWorkData.class), any(DashboardResultList.class))).thenReturn(dashboardResultList);
		when(workSearchRequest.getUserNumber()).thenReturn(userNumber);
		doReturn(availableSearchRequest).when(workDashboardService).copyWorkSearchRequest(workSearchRequest);
		when(decisionFlowService.getDoableDecisions(REQUEST)).thenReturn(EMPTY_LIST_FOR_DECISIONS);
	}

	@Test
	public void getDashboard_returnDashboardAndSidebar() {
		dashboardResponse = workDashboardService.getDashboard(workSearchRequest);

		assertNotNull(dashboardResponse.getDashboardResultList());
		assertNotNull(dashboardResponse.getSidebar());
	}

	@Test
	public void getDashboard_fetchMinimalUserObject() {
		workDashboardService.getDashboard(workSearchRequest);

		verify(userDAO).findUserByUserNumber(userNumber, false);
	}

	@Test
	public void getDashboard_searchAllWorkByUserId() {
		workDashboardService.getDashboard(workSearchRequest);

		verify(workSearchService, times(1)).searchAllWorkByUserId(userId, workSearchRequest);
	}

	@Test
	public void getDashboard_availableSearch() {
		when(workSearchRequest.isAvailableSearch()).thenReturn(true);

		workDashboardService.getDashboard(workSearchRequest);

		verify(workSearchRequest).setStatusFilter(eq(new DashboardStatusFilter().setStatusCode(WorkStatusType.ALL)));
		verify(workSearchService, times(1)).searchAllWorkByUserId(userId, workSearchRequest);
		verify(availableSearchRequest).setStatusFilter(eq(new DashboardStatusFilter().setStatusCode(WorkStatusType.AVAILABLE)));
		verify(workSearchService, times(1)).searchAllWorkByUserId(userId, availableSearchRequest);
		verify(workSearchResponse).setResults(anyListOf(SolrWorkData.class));
		verify(workSearchResponse).setResultsLimit(anyInt());
		verify(workSearchResponse).setTotalResultsCount(anyInt());
	}
}
