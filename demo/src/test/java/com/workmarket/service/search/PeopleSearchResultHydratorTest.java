package com.workmarket.service.search;

import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.service.search.user.PeopleSearchResultHydrator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class PeopleSearchResultHydratorTest {

	private PeopleSearchResponse response;
	private PeopleSearchTransientData hydrateData;
	private PeopleSearchRequest request;

	@InjectMocks PeopleSearchResultHydrator userSearchResultHydrator;

	@Before
	public void setUp() throws Exception {
		response = mock(PeopleSearchResponse.class);
		hydrateData = mock(PeopleSearchTransientData.class);
		request = mock(PeopleSearchRequest.class);
		when(request.getCompanyTypeFilter()).thenReturn(Collections.EMPTY_SET);
		when(hydrateData.getOriginalRequest()).thenReturn(request);
	}

	@Test
	public void hydrateSearchResult_notNull() throws Exception {
		assertNotNull(userSearchResultHydrator.hydrateSearchResult(response, hydrateData));
	}

	@Test
	public void hydrateSearchResult_equals() throws Exception {
		assertEquals(userSearchResultHydrator.hydrateSearchResult(response, hydrateData), response);
	}
}
