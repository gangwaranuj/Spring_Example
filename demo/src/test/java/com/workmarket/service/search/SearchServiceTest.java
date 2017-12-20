package com.workmarket.service.search;

import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.domains.search.group.indexer.service.GroupIndexer;
import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.search.group.GroupSearchService;
import com.workmarket.service.search.user.AssessmentInviteSearchService;
import com.workmarket.service.search.user.GroupResourceSearchService;
import com.workmarket.service.search.user.PeopleSearchService;
import com.workmarket.service.search.user.WorkResourceSearchService;
import com.workmarket.service.search.work.WorkSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceTest {

	@Mock GroupSearchService groupSearchService;
	@Mock WorkSearchService workSearchService;
	@Mock AssessmentInviteSearchService assessmentInviteSearchService;
	@Mock UserIndexer userIndexer;
	@Mock WorkIndexer workIndexer;
	@Mock WorkResourceSearchService workResourceSearchService;
	@Mock GroupResourceSearchService groupResourceSearchService;
	@Mock PeopleSearchService peopleSearchService;
	@Mock GroupIndexer groupIndexer;
	@InjectMocks SearchServiceImpl searchService;

	@Before
	public void setUp() throws Exception {
	 	when(workSearchService.searchAllWorkByUserId(anyLong(), any(WorkSearchRequest.class))).thenReturn(new WorkSearchResponse());
		when(groupSearchService.searchAllCompanyGroups(any(GroupSolrDataPagination.class))).thenReturn(new GroupSolrDataPagination());
		when(assessmentInviteSearchService.searchPeople(any(PeopleSearchRequest.class))).thenReturn(new PeopleSearchResponse());
	}

	@Test
	public void searchAllWork_WithNullUserId() throws Exception {
		assertNotNull(searchService.searchAllWork(null, null));
		verify(workSearchService, never()).searchAllWorkByUserId(anyLong(), any(WorkSearchRequest.class));
	}

	@Test
	public void searchAllWork_success() throws Exception {
		assertNotNull(searchService.searchAllWork(1L, new WorkSearchRequest()));
		verify(workSearchService, times(1)).searchAllWorkByUserId(anyLong(), any(WorkSearchRequest.class));
	}

	@Test
	public void searchAllGroups_WithNullPagination() throws Exception {
		assertNull(searchService.searchAllGroups(null));
	}

	@Test
	public void searchAllGroups() throws Exception {
		GroupSolrDataPagination pagination = new GroupSolrDataPagination();
		pagination.getSearchFilter().setUserId(1L);

		assertNotNull(searchService.searchAllGroups(pagination));
		verify(groupSearchService).searchAllCompanyGroups(any(GroupSolrDataPagination.class));
	}

	@Test
	public void searchAssessmentInvite_success() throws Exception {
		assertNotNull(searchService.searchPeopleForAssessment(new PeopleSearchRequest()));
	}

}