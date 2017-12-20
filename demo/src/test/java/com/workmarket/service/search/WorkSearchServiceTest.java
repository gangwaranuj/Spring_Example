package com.workmarket.service.search;

import com.google.common.base.Optional;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.query.WorkSearchQueryCreator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.search.model.AbstractSearchTransientData;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.model.WorkSearchTransientData;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.response.SearchResponse;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.work.WorkSearchServiceImpl;
import com.workmarket.utility.DateUtilities;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkSearchServiceTest {

	@Mock private WorkIndexer workIndexer;
	@Mock private EventRouter eventRouter;
	@Mock private EventFactory eventFactory;
	@Mock private UserService userService;
	@Mock private WorkSearchQueryCreator searchQueryCreator;
	@Mock private SearchResultParser searchResultParser;
	@Mock private SearchResultHydrator searchResultHydrator;
	@Mock private HttpSolrServer workSolrServer;
	@Mock private CompanyService companyService;
	@Mock private AuthenticationService authenticationService;
	@Mock private FeatureEntitlementService featureEntitlementService;

	@InjectMocks WorkSearchServiceImpl workSearchService = spy(new WorkSearchServiceImpl());

	private Long userId = 1L;
	private Long companyId = 2L;
	private WorkSearchRequest workSearchRequest;
	private WorkSearchRequest timeFilterRequest;
	private Company company;
	private User user;
	private SearchQuery searchQuery;
	private SearchQuery timeFilterQuery;
	private QueryResponse queryResponse;
	private QueryResponse timeFilterQueryResponse;
	private WorkSearchResponse workSearchResponse;
	private WorkSearchResponse timeFilterSearchResponse;
	private Optional personaPreferenceOptional;
	private PersonaPreference personaPreference;
	private WorkSearchTransientData transientData;

	@Before
	public void setup() {
		workSearchRequest = mock(WorkSearchRequest.class);
		timeFilterRequest = mock(WorkSearchRequest.class);
		when(workSearchRequest.getType()).thenReturn(SearchType.WORK_KPI.toString());
		when(timeFilterRequest.getType()).thenReturn(SearchType.WORK_KPI.toString());

		user = mock(User.class);
		when(user.getId()).thenReturn(userId);
		when(authenticationService.getCurrentUserId()).thenReturn(userId);

		company = mock(Company.class);
		when(user.getCompany()).thenReturn(company);
		when(companyService.findById(anyLong())).thenReturn(company);
		when(company.getId()).thenReturn(companyId);

		DateRange dr = new DateRange();
		dr.setFrom(DateUtilities.getMidnightToday());
		dr.setThrough(DateUtilities.getMidnightTomorrow());

		when(userService.getUser(any(Long.class))).thenReturn(user);

		searchQuery = mock(SearchQuery.class);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");

		timeFilterQuery = mock(SearchQuery.class);
		SolrQuery timeFilterSolrQuery = new SolrQuery();
		timeFilterSolrQuery.setQuery("scheduleFromDate:[ " + dr.getFrom().toString() + " TO " + dr.getThrough().toString()+ " ]");

		queryResponse = mock(QueryResponse.class);
		workSearchResponse = mock(WorkSearchResponse.class);

		timeFilterQueryResponse = mock(QueryResponse.class);
		timeFilterSearchResponse = mock(WorkSearchResponse.class);

		try {
			when(searchQueryCreator.createSearchQuery(any(WorkSearchTransientData.class), any(WorkSearchRequest.class))).thenReturn(searchQuery);
			when(workSolrServer.query(any(SolrQuery.class))).thenReturn(queryResponse);
			when(searchResultParser.parseSolrQueryResponse(any(SearchResponse.class), any(AbstractSearchTransientData.class), any(QueryResponse.class))).thenReturn(workSearchResponse);
			when(searchResultHydrator.hydrateSearchResult(any(SearchResponse.class), any(AbstractSearchTransientData.class))).thenReturn(workSearchResponse);
		} catch (SearchException | SolrServerException e) {
			e.printStackTrace();
		}

		try {
			when(searchQueryCreator.createSearchQuery(any(WorkSearchTransientData.class), any(WorkSearchRequest.class))).thenReturn(timeFilterQuery);
			when(workSolrServer.query(any(SolrQuery.class))).thenReturn(timeFilterQueryResponse);
			when(searchResultParser.parseSolrQueryResponse(any(SearchResponse.class), any(AbstractSearchTransientData.class), any(QueryResponse.class))).thenReturn(timeFilterSearchResponse);
			when(searchResultHydrator.hydrateSearchResult(any(SearchResponse.class), any(AbstractSearchTransientData.class))).thenReturn(timeFilterSearchResponse);
		} catch (SearchException | SolrServerException e) {
			e.printStackTrace();
		}

		personaPreferenceOptional = mock(Optional.class);
		personaPreference = mock(PersonaPreference.class);
		when(userService.getPersonaPreference(user.getId())).thenReturn(personaPreferenceOptional);
		when(personaPreferenceOptional.isPresent()).thenReturn(true);
		when(personaPreferenceOptional.get()).thenReturn(personaPreference);
		when(personaPreference.isBuyer()).thenReturn(true);

		transientData = mock(WorkSearchTransientData.class);
		doReturn(transientData).when(workSearchService).makeWorkSearchTransientData();

		when(featureEntitlementService.hasPercentRolloutFeatureToggle(anyString())).thenReturn(false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void searchAllWorkByUserId_withNullUser_fail() {
		workSearchService.searchAllWorkByUserId(null, workSearchRequest);
	}

	@Test(expected = IllegalArgumentException.class)
	public void searchAllWorkByUserId_withNullRequest_fail() {
		workSearchService.searchAllWorkByUserId(1L, null);
	}

	@Test
	public void searchAllWorkByUserId_success() {
		WorkSearchResponse workSearchResponse = workSearchService.searchAllWorkByUserId(1L, workSearchRequest);

		assertNotNull(workSearchResponse);
	}

	@Test
	public void searchAllWorkByUserId_withTimeFilter_success() {
		WorkSearchResponse timeFilterSearchResponse = workSearchService.searchAllWorkByUserId(1L, timeFilterRequest);

		assertEquals(0, timeFilterSearchResponse.getResultsSize());
	}

	@Test
	public void searchAllWorkByUserId_clientSearch_setWorkSearchRequestUserType() {
		workSearchService.searchAllWorkByUserId(userId, workSearchRequest);

		verify(workSearchRequest).setWorkSearchRequestUserType(eq(WorkSearchRequestUserType.CLIENT));
	}

	@Test
	public void searchAllWorkByUserId_workerSearch_setWorkSearchRequestUserType() {
		when(personaPreference.isBuyer()).thenReturn(false);

		workSearchService.searchAllWorkByUserId(userId, workSearchRequest);
		
		verify(workSearchRequest).setWorkSearchRequestUserType(eq(WorkSearchRequestUserType.RESOURCE));
	}

	@Test
	public void searchAllWorkByUserId_ifPersonaPreferenceIsPresent_setWorkSearchRequestUserTypeOnRequest() {
		workSearchService.searchAllWorkByUserId(1L, workSearchRequest);

		verify(workSearchRequest).setWorkSearchRequestUserType(WorkSearchRequestUserType.CLIENT);
	}

	@Test
	public void searchAllWorkByUserId_ifPersonaPreferenceIsNotPresent_doNotSetWorkSearchRequestUserTypeOnRequest() {
		when(personaPreferenceOptional.isPresent()).thenReturn(false);

		workSearchService.searchAllWorkByUserId(1L, workSearchRequest);

		verify(workSearchRequest, never()).setWorkSearchRequestUserType(any(WorkSearchRequestUserType.class));
	}

	@Test
	public void searchAllWorkByUserId_isManagerRole_setShowAllAtCompany() {
		when(authenticationService.authorizeUserByAclPermission(anyLong(), anyString())).thenReturn(true);

		workSearchService.searchAllWorkByUserId(userId, workSearchRequest);

		verify(workSearchRequest).setShowAllAtCompany(true);
	}

	@Test
	public void searchAllWorkByUserId_isDispatcherRole_setShowAllAtCompany() {
		when(personaPreference.isDispatcher()).thenReturn(true);

		workSearchService.searchAllWorkByUserId(userId, workSearchRequest);

		verify(workSearchRequest).setShowAllAtCompany(true);
	}

	@Test
	public void searchAllWorkByUserId_isNeitherManagerNorDispatcher_doNotSetShowAllAtCompany() {
		when(authenticationService.authorizeUserByAclPermission(anyLong(), anyString())).thenReturn(false);
		when(personaPreference.isDispatcher()).thenReturn(false);

		workSearchService.searchAllWorkByUserId(userId, workSearchRequest);

		verify(workSearchRequest).setShowAllAtCompany(false);
	}

	@Test
	public void searchAllWorkByUserId_workerSearch_setBlockedCompanyIds() {
		when(workSearchRequest.getWorkSearchRequestUserType()).thenReturn(WorkSearchRequestUserType.RESOURCE);

		workSearchService.searchAllWorkByUserId(userId, workSearchRequest);

		verify(userService).findBlockedOrBlockedByCompanyIdsByUserId(userId);
		verify(workSearchRequest).setBlockedCompanyIds(anyListOf(Long.class));
	}

	@Test
	public void searchAllWorkByUserId_setSearchUser() {
		SearchUser searchUser = mock(SearchUser.class);
		doReturn(searchUser).when(workSearchService).makeSearchUser();

		workSearchService.searchAllWorkByUserId(userId, workSearchRequest);

		verify(searchUser).setId(userId);
		verify(searchUser).setCompanyId(companyId);
	}

	@Test
	public void searchAllWorkByUserId_setTransientData() {
		workSearchService.searchAllWorkByUserId(userId, workSearchRequest);

		verify(transientData).setCurrentUser(any(SearchUser.class));
		verify(transientData).setOriginalRequest(workSearchRequest);
	}

	@Test
	public void searchAllWorkByUserId_withLatAndLong_setGeopointOnRequest() {
		double lat = 523L, lng = 21435L;
		when(workSearchRequest.getLatitude()).thenReturn(lat);
		when(workSearchRequest.getLongitude()).thenReturn(lng);

		workSearchService.searchAllWorkByUserId(userId, workSearchRequest);

		verify(transientData).setGeopoint(eq(new GeoPoint(lat, lng)));
	}

	@Test
	public void searchAllWorkByUserId_noLatAndLong_doNotSetGeopointOnRequest() {
		when(workSearchRequest.getLatitude()).thenReturn(null);
		when(workSearchRequest.getLongitude()).thenReturn(null);

		workSearchService.searchAllWorkByUserId(userId, workSearchRequest);

		verify(transientData, never()).setGeopoint(any(GeoPoint.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void searchAllWorkByCompanyId_companyDoesNotExist_throwException() {
		when(companyService.findById(anyLong())).thenReturn(null);

		workSearchService.searchAllWorkByCompanyId(1L, workSearchRequest);
	}

	@Test
	public void searchAllWorkByCompanyId_verifyCompanyIdIsSetOnTransientData() {
		doReturn(transientData).when(workSearchService).decorateSearchRequest(user, workSearchRequest);

		workSearchService.searchAllWorkByCompanyId(companyId, workSearchRequest);

		verify(transientData).setCompanyId(companyId);
	}
}
