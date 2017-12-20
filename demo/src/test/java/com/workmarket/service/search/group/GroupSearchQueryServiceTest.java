package com.workmarket.service.search.group;


import com.google.common.collect.ImmutableList;
import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.data.solr.query.keyword.RelevancyKeywordService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.search.model.query.GroupSearchQuery;
import com.workmarket.search.request.user.Group;
import com.workmarket.service.business.dto.GroupSearchFilterDTO;
import com.workmarket.service.network.NetworkService;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;

import java.util.List;

import static com.workmarket.data.solr.model.group.GroupSolrDataPagination.SEARCH_TYPE.SEARCH_COMPANY_GROUPS;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupSearchQueryServiceTest {

	@Mock private NetworkService networkService;
	@Mock private RelevancyKeywordService relevancyKeywordService;
	@InjectMocks private GroupSearchQueryServiceImpl groupSearchQueryService;

	@Test
	public void testBuild() {
		long companyId = 10L;
		Company company = mock(Company.class);
		when(company.getId()).thenReturn(companyId);

		long userId = 100L;
		User worker = mock(User.class);
		when(worker.getId()).thenReturn(userId);
		when(worker.getCompany()).thenReturn(company);

		GroupSearchFilterDTO.FacetFieldContainer facetFieldContainer = mock(GroupSearchFilterDTO.FacetFieldContainer.class);
		when(facetFieldContainer.getShowAll()).thenReturn(true);
		GroupSolrDataPagination pagination = mock(GroupSolrDataPagination.class);

		List<Long> blockedByCompanyIds = ImmutableList.of(101L, 102L);
		GroupSearchFilterDTO filter = mock(GroupSearchFilterDTO.class);
		when(filter.getUserId()).thenReturn(userId);
		when(filter.getIndustries()).thenReturn(facetFieldContainer);
		when(filter.getBlockedByCompanyIds()).thenReturn(blockedByCompanyIds);

		when(pagination.getSearchFilter()).thenReturn(filter);
		when(pagination.getSearchType()).thenReturn(SEARCH_COMPANY_GROUPS);
		GroupSearchQuery query = groupSearchQueryService.build(worker, pagination);
		assertTrue(query.getFilterQueries().length == 2);
		assertEquals("( ( companyId:10  AND deleted:false ) ) ", query.getFilterQueries()[0]);
		assertEquals(String.format("-companyId:(%s)", StringUtils.join(blockedByCompanyIds, " OR ")),
			query.getFilterQueries()[1]);
	}
}
