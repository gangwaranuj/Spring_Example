package com.workmarket.data.solr.query.keyword;

import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.model.SearchUser;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchKeywordServiceTest {

	@InjectMocks SearchKeywordServiceImpl searchKeywordService;
	private SearchUser currentUser;

	@Before
	public void setup() {
		currentUser = mock(SearchUser.class);
		when(currentUser.getId()).thenReturn(1L);
		when(currentUser.getCompanyId()).thenReturn(1L);
		Whitebox.setInternalState(searchKeywordService, "maximumKeywordLength", 200);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createKeywordQueryString_withNullUser_fail() {
		searchKeywordService.addKeywordQueryString("keyword", null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createKeywordQueryString_withNullCompanyUser_fail() {
		searchKeywordService.addKeywordQueryString("keyword", new SolrQuery(), new SearchUser(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createKeywordQueryString_withNullQuery_fail() {
		searchKeywordService.addKeywordQueryString("keyword", null, new SearchUser(), null);
	}

	@Test
	public void createKeywordQueryString_success() {
		Assert.assertEquals(searchKeywordService.addKeywordQueryString("keyword", new SolrQuery(), currentUser, null), "keyword 1_keyword");
	}

	@Test
	public void createKeywordQuery_searchForPhrase_success() {
		Assert.assertEquals(searchKeywordService.addKeywordQueryString("\"keyword\"", new SolrQuery(), currentUser, null), "\"keyword\" \"1_keyword\"");
	}

	@Test
	public void createKeywordQuery_searchForEmail_returnsNull() {
		Assert.assertNull(searchKeywordService.addKeywordQueryString("rocio@workmarket.com", new SolrQuery(), currentUser, null));
	}

	@Test
	public void createKeywordQuery_searchForUserNumber_returnsNull() {
		Assert.assertNull(searchKeywordService.addKeywordQueryString("0000001", new SolrQuery(), currentUser, null));
	}

	@Test
	public void createKeywordQuery_searchForFullName_returnsNull() {
		SolrQuery solrQuery = mock(SolrQuery.class);
		Assert.assertNull(searchKeywordService.addKeywordQueryString("FirstName LastName", solrQuery, currentUser, SearchType.PEOPLE_SEARCH_ASSIGNMENT_FULL_NAME));
		verify(solrQuery, times(1)).add(eq("qf"), eq(UserSearchableFields.FULL_NAME.getName() + SearchKeywordServiceImpl.FULL_NAME_BOOST));
	}
}
