package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.workmarket.redis.repositories.WorkSearchRequestRepositoryImpl;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.DashboardResultList;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.feed.FeedServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectServiceUnitTest {
	Log logger = LogFactory.getLog(SelectServiceUnitTest.class);

	@Mock com.workmarket.service.search.SearchService searchService;
	@Mock WorkSearchRequestRepositoryImpl workSearchRequestRepository;
	@InjectMocks SelectServiceImpl selectService;

	Optional<WorkSearchRequest> opt;
	WorkSearchRequest request;
	WorkSearchResponse response;
	DashboardResultList resultList;

	@Before
	public void  setup(){

		request = mock(WorkSearchRequest.class);

		opt = mock(Optional.class);
		when(opt.isPresent()).thenReturn(true);
		when(opt.get()).thenReturn(request);

		when(workSearchRequestRepository.get(any(Long.class))).thenReturn(opt);

		List<String> workNumbers;
		workNumbers = mock(ArrayList.class);

		resultList = mock(DashboardResultList.class);
		when(resultList.getResultIds()).thenReturn(workNumbers);

		response = mock(WorkSearchResponse.class);
		when(response.getDashboardResultList()).thenReturn(resultList);

		when(searchService.searchAllWork(any(Long.class), any(WorkSearchRequest.class))).thenReturn(response);
	}

	@Test
	public void test_fetchAllWorkBySearchFilter_get(){
		selectService.fetchAllWorkBySearchFilter(1L);
		verify(workSearchRequestRepository).get(1L);
	}


	@Test
	public void test_fetchAllWorkBySearchFilter_isPresent(){
		selectService.fetchAllWorkBySearchFilter(1L);
		verify(opt).isPresent();
	}

	@Test
	public void test_fetchAllWorkBySearchFilter_isAbsent(){
		when(opt.isPresent()).thenReturn(false);
		selectService.fetchAllWorkBySearchFilter(1L);
		verify(opt,never()).get();
	}

	@Test
	public void test_fetchAllWorkBySearchFilter_response(){
		selectService.fetchAllWorkBySearchFilter(1L);
		verify(searchService).searchAllWork(1L, opt.get());
	}

	@Test
	public void test_fetchAllWorkBySearchFilter_nullResponse(){
		when(searchService.searchAllWork(any(Long.class), any(WorkSearchRequest.class))).thenReturn(null);
		selectService.fetchAllWorkBySearchFilter(1L);
		verify(response,never()).getDashboardResultList();
	}




}
