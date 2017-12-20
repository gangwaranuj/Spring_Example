package com.workmarket.data.solr.query;

import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.model.query.PeopleSearchQuery;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.request.user.Verification;
import com.workmarket.domains.model.requirementset.SolrQueryVisitor;
import com.workmarket.service.search.user.PeopleSearchSort;
import com.workmarket.web.controllers.BaseControllerUnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * User: alexsilva Date: 4/2/14 Time: 12:39 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class PeopleSearchQueryCreatorTest extends BaseControllerUnitTest {

	@Mock PeopleSearchSolrQueryString queryString;
	@Mock PeopleSearchSort sort;
	@Mock SolrQueryVisitor visitor;
	@Mock PeopleSearchTransientData data;
	@Mock SearchUser currentUser;
	@Mock PeopleSearchRequest request;
	@Mock PeopleSearchQuery peopleSearchQuery;
	@Mock SecurityContextFacade securityContextFacade;

	@InjectMocks PeopleSearchQueryCreator peopleSearchQueryCreator = spy(new PeopleSearchQueryCreator());

	@Before
	public void setup() throws Exception {
		init(securityContextFacade);

		when(data.getCurrentUser()).thenReturn(currentUser);
		when(data.getOriginalRequest()).thenReturn(request);

		when(peopleSearchQuery.addBaseQueryParams()).thenReturn(peopleSearchQuery);
		when(peopleSearchQuery.addBaseFilters()).thenReturn(peopleSearchQuery);
		when(peopleSearchQuery.addLaneFilterQuery()).thenReturn(peopleSearchQuery);
		when(peopleSearchQuery.addBlockedUserFilters()).thenReturn(peopleSearchQuery);
		when(peopleSearchQuery.addBaseFacets()).thenReturn(peopleSearchQuery);

		doReturn(peopleSearchQuery).when(peopleSearchQueryCreator).makePeopleSearchQuery(data);
	}

	@After
	public void teardown() {
		verify(peopleSearchQuery).addBaseQueryParams();

		switch (data.getSearchType()) {
			case PEOPLE_SEARCH_GROUP_MEMBER:
				verify(peopleSearchQuery, never()).addLaneFilterQuery();
				verify(peopleSearchQuery, never()).addBlockedUserFilters();
				break;
			default:
				verify(peopleSearchQuery).addBaseFilters();
				verify(peopleSearchQuery).addLaneFilterQuery();
				verify(peopleSearchQuery).addBlockedUserFilters();
		}
	}

	@Test
	public void testCreateSearchQuery_GroupInviteSearch() throws Exception {
		when(data.getSearchType()).thenReturn(SearchType.PEOPLE_SEARCH_GROUP);
		when(data.isSetInviteToGroupId()).thenReturn(true);
		when(peopleSearchQuery.addGroupInviteFilters()).thenReturn(peopleSearchQuery);

		peopleSearchQueryCreator.createSearchQuery(data);

		verify(peopleSearchQuery).addGroupInviteFilters();
	}

	@Test
	public void testCreateSearchQuery_GroupMemberSearch() throws Exception {
		when(data.getSearchType()).thenReturn(SearchType.PEOPLE_SEARCH_GROUP_MEMBER);
		when(data.isSetMemberOfGroupId()).thenReturn(true);
		when(peopleSearchQuery.addGroupStatusFacets()).thenReturn(peopleSearchQuery);
		when(peopleSearchQuery.addGroupStatusFilters()).thenReturn(peopleSearchQuery);
		when(peopleSearchQuery.addInsuranceTypesFilterQuery()).thenReturn(peopleSearchQuery);

		peopleSearchQueryCreator.createSearchQuery(data);

		verify(peopleSearchQuery).addGroupStatusFilters();
		verify(peopleSearchQuery).addInsuranceTypesFilterQuery();
	}

	@Test
	public void testCreateSearchQuery_AssessmentInviteSearch() throws Exception {
		when(data.getSearchType()).thenReturn(SearchType.PEOPLE_SEARCH_ASSESSMENT_INVITE);
		when(request.isSetCurrentAssessmentId()).thenReturn(true);
		when(peopleSearchQuery.addCurrentAssessmentFilters()).thenReturn(peopleSearchQuery);
		when(peopleSearchQuery.addCurrentAssessmentFacets()).thenReturn(peopleSearchQuery);

		peopleSearchQueryCreator.createSearchQuery(data);

		verify(peopleSearchQuery).addCurrentAssessmentFilters();
		verify(peopleSearchQuery).addCurrentAssessmentFacets();
	}

	@Test
	public void testCreateSearchQuery_WorkSend() throws Exception {
		when(data.getSearchType()).thenReturn(SearchType.PEOPLE_SEARCH);
		when(data.getFailedVerifications()).thenReturn(Verification.FAILED_SCREENING_IDS);

		peopleSearchQueryCreator.createSearchQuery(data);
		verify(peopleSearchQuery).addFailedScreeningFilterQuery();
	}

}
