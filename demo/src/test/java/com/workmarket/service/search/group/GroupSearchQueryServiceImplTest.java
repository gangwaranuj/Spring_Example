package com.workmarket.service.search.group;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.Sort;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupSearchQueryServiceImplTest {

	@InjectMocks GroupSearchQueryServiceImpl toTest;

	@Test
	public void sortClause_scoreIf_emptySortsCollection() {
		final GroupSolrDataPagination pagination = mock(GroupSolrDataPagination.class);
		final SolrQuery.SortClause expected = new SolrQuery.SortClause("score", SolrQuery.ORDER.desc);

		when(pagination.getSorts()).thenReturn(Collections.<Sort>emptyList());

		final Optional<SolrQuery.SortClause> result = toTest.getSortClause(pagination);

		assertEquals(expected, result.get());
	}

	@Test
	public void sortClause_absentIf_nullSortColumn() {
		final GroupSolrDataPagination pagination = mock(GroupSolrDataPagination.class);
		final Sort sort = mock(Sort.class);

		when(sort.getSortColumn()).thenReturn(null);
		when(pagination.getSorts()).thenReturn(ImmutableList.of(sort));

		assertFalse(toTest.getSortClause(pagination).isPresent());
	}

	@Test
	public void sortClause_matches_sortColumn() {
		final GroupSolrDataPagination pagination = mock(GroupSolrDataPagination.class);
		final Sort sort = mock(Sort.class);
		final String sortColumnKey = "member_count";
		final GroupSolrDataPagination.SORTS paginationSortType = GroupSolrDataPagination.SORTS.MEMBER_COUNT;
		final SolrQuery.SortClause expected = new SolrQuery.SortClause(sortColumnKey, SolrQuery.ORDER.desc);

		when(sort.getSortColumn()).thenReturn(paginationSortType.name());
		when(pagination.getSorts()).thenReturn(ImmutableList.of(sort));
		when(pagination.getSortOrder(any(GroupSolrDataPagination.SORTS.class))).thenReturn("DESC");
		when(pagination.getSortColumn(paginationSortType)).thenReturn(sortColumnKey);

		final Optional<SolrQuery.SortClause> result = toTest.getSortClause(pagination);

		assertEquals(expected, result.get());
	}

	@Test
	public void sortClause_takesPrecedenceOverDefault_sortColumn() {
		final GroupSolrDataPagination pagination = mock(GroupSolrDataPagination.class);
		final Sort sort = mock(Sort.class);
		final String sortColumnKey = "member_count";
		final GroupSolrDataPagination.SORTS paginationSortType = GroupSolrDataPagination.SORTS.MEMBER_COUNT;
		final SolrQuery.SortClause expected = new SolrQuery.SortClause(sortColumnKey, SolrQuery.ORDER.asc);

		when(sort.getSortColumn()).thenReturn(paginationSortType.name());
		when(sort.getSortDirection()).thenReturn(Pagination.SORT_DIRECTION.ASC);
		when(pagination.getSorts()).thenReturn(ImmutableList.of(sort));
		when(pagination.getSortOrder(any(GroupSolrDataPagination.SORTS.class))).thenReturn("DESC");
		when(pagination.getSortColumn(paginationSortType)).thenReturn(sortColumnKey);

		final Optional<SolrQuery.SortClause> result = toTest.getSortClause(pagination);

		assertEquals(expected, result.get());
	}

}
