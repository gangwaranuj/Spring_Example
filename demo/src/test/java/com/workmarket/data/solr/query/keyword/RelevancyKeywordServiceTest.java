package com.workmarket.data.solr.query.keyword;

import com.workmarket.data.solr.configuration.BoostConfiguration;
import com.workmarket.data.solr.repository.UserBoostFields;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.request.user.PeopleSearchSortByType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class RelevancyKeywordServiceTest {

	@Mock private BoostConfiguration boostConfig;
	@Mock private StopWordFilterService stopwords;
	@InjectMocks RelevancyKeywordServiceImpl relevancyKeywordService;

	private SearchUser user;
	private PeopleSearchTransientData data;

	@Before
	public void setUp() {
		data = new PeopleSearchTransientData();
		data.setEnhancedRelevancy(true);

		user = new SearchUser();
		user.setCompanyId(1L);
		user.setUserNumber("00000015");

		when(boostConfig.getCompletedWorkForCompaniesBoost()).thenReturn(2.0);
	}

	@Test
	public void createRelevancyString_withRelevancy_notEmpty() throws Exception {
		String relevancyQuery = relevancyKeywordService.createRelevancyString(user, data);
		assertTrue(StringUtils.isNotBlank(relevancyQuery));
		for (UserBoostFields userBoostField: boostConfig.BOOST_FIELDS) {
			assertTrue(relevancyQuery.contains(userBoostField.getName()));
		}
	}

	@Test
	public void createRelevancyString_withoutRelevancy_notEmpty() throws Exception {
		data.setEnhancedRelevancy(false);
		String relevancyQuery = relevancyKeywordService.createRelevancyString(user, data);
		assertTrue(StringUtils.isNotBlank(relevancyQuery));
	}

	@Test
	public void isRequestSortedByRelevancy_positive_success() throws Exception {
		PeopleSearchRequest searchRequest = new PeopleSearchRequest();
		Pagination pagination = new Pagination();
		pagination.setSortBy(PeopleSearchSortByType.RELEVANCY);
		searchRequest.setPaginationRequest(pagination);
		assertTrue(relevancyKeywordService.isRequestSortedByRelevancy(searchRequest));
	}

	@Test
	public void isRequestSortedByRelevancy_false_success() throws Exception {
		PeopleSearchRequest searchRequest = new PeopleSearchRequest();
		Pagination pagination = new Pagination();
		pagination.setSortBy(PeopleSearchSortByType.RATING);
		searchRequest.setPaginationRequest(pagination);
		assertFalse(relevancyKeywordService.isRequestSortedByRelevancy(searchRequest));
	}

	@Test
	public void isRequestSortedByRelevancy_withoutPaginationDefaultSort_success() throws Exception {
		PeopleSearchRequest searchRequest = new PeopleSearchRequest();
		assertTrue(relevancyKeywordService.isRequestSortedByRelevancy(searchRequest));
	}
}
