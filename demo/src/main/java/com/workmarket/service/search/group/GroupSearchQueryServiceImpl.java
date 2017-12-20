package com.workmarket.service.search.group;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.data.solr.query.keyword.RelevancyKeywordService;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.Sort;
import com.workmarket.domains.model.User;
import com.workmarket.search.model.query.GroupSearchQuery;
import com.workmarket.service.business.dto.GroupSearchFilterDTO;
import com.workmarket.service.network.NetworkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class GroupSearchQueryServiceImpl implements GroupSearchQueryService {

	private static final Log logger = LogFactory.getLog(GroupSearchQueryServiceImpl.class);

	@Autowired private NetworkService networkService;
	@Autowired private RelevancyKeywordService relevancyKeywordService;

	@Override
	public GroupSearchQuery build(User user, GroupSolrDataPagination pagination) {
		Assert.notNull(pagination);
		Assert.notNull(pagination.getSearchFilter());
		Assert.notNull(user);
		Assert.notNull(user.getCompany());

		GroupSearchFilterDTO userSearchFilter = pagination.getSearchFilter();

		// Get user's networks
		List<Long> networkIds = networkService.findAllCompanyNetworkIds(user.getCompany().getId());

		GroupSearchQuery query = new GroupSearchQuery();
		query.setFacet(true);
		query.setFacetMissing(true);
		query.setFacetLimit(10);
		query.addFacetField("{!ex=industryIds}industryIds");
		query.addFacetQuery("*:*");
		query.add("fl", "*,score");

		final Optional<SolrQuery.SortClause> sortClause = getSortClause(pagination);
		if (sortClause.isPresent()) {
			query.addSort(sortClause.get());
		}

		//Add industry filter
		if (!userSearchFilter.getIndustries().getShowAll()) {
			StringBuilder sb = new StringBuilder();
			sb.append("{!tag=").append(userSearchFilter.getIndustries().getField()).append("}").append(userSearchFilter.getIndustries().getField()).append(":(0 ");
			for (GroupSearchFilterDTO.FacetFieldFilter filter : userSearchFilter.getIndustries().getFacetFieldFilters()) {
				if (filter.getFilterOn())
					sb.append(" OR ").append(filter.getId());
			}
			sb.append(" ) ");
			query.addFilterQuery(sb.toString());
		}

		StringBuilder queryString = new StringBuilder();

		//Add companyIds filter if user is in a private network
		if (userSearchFilter.getCompanyIdsExclusiveToUser() != null && !userSearchFilter.getCompanyIdsExclusiveToUser().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("companyId:(");
			for (Long companyId : userSearchFilter.getCompanyIdsExclusiveToUser()) {
				sb.append(companyId + "+");
			}
			sb.replace(sb.length() - 1, sb.length(), ")");
			query.addFilterQuery(sb.toString());
		}

		switch (pagination.getSearchType()) {
			case SEARCH_COMPANY_GROUPS:
				query.addFilterQuery(String.format("( ( companyId:%d  AND deleted:false ) ) ", user.getCompany().getId()));
				break;
			case SEARCH_ALL_OPEN_COMPANY_GROUPS:
				query.addFilterQuery(String.format("( ( companyId:%d AND openMembership:true AND deleted:false ) ) ", user.getCompany().getId()));
				break;
			case SEARCH_ALL_OPEN_ACTIVE_COMPANY_GROUPS:
				query.addFilterQuery(String.format("( ( companyId:%d AND openMembership:true AND deleted:false AND activeFlag:true ) ) ", user.getCompany().getId()));
				break;
			case SEARCH_ALL_OPEN_GROUPS:
				query.addFilterQuery(String.format("( ( openMembership:true AND deleted:false AND activeFlag:true AND searchable:true) ) "));
				break;
			case SEARCH_COMPANY_GROUPS_LOCATION_MANAGER:
				query.addFilterQuery(String.format("( ( companyId:%d  AND deleted:false AND activeFlag:true) ) ", user.getCompany().getId()));
				break;
			case SEARCH_COMPANY_ROUTABLE_GROUPS:
				query.addFilterQuery(String.format("( ( companyId:%d  AND deleted:false AND activeFlag:true AND memberCount:[1 TO *]) ) ", user.getCompany().getId()));
				break;
			default:
				queryString.append("( ( deleted:false AND activeFlag:true AND searchable:true ) )");
				break;
		}

		// filter out companies blocking this worker
		if(userSearchFilter.getBlockedByCompanyIds() != null && !userSearchFilter.getBlockedByCompanyIds().isEmpty()) {
			query.addFilterQuery(String.format("-companyId:(%s)",
				StringUtils.join(userSearchFilter.getBlockedByCompanyIds(), " OR ")));
		}

		//Adding keywords
		if (StringUtils.isNotBlank(userSearchFilter.getKeywords())) {
			queryString.append(userSearchFilter.getKeywords());
		}

		//Add boost if user is in a network
		String boostFunction = relevancyKeywordService.createGroupInNetworkRelevanceString(networkIds);
		if (isNotBlank(boostFunction)) {
			query.add("bf", boostFunction);
		}

		logger.debug("queryString: " + queryString.toString());
		query.setQuery(queryString.toString());
		query.setStart(pagination.getStartRow());
		query.setRows(pagination.getResultsLimit());
		return query;
	}

	@VisibleForTesting
	protected Optional<SolrQuery.SortClause> getSortClause(final GroupSolrDataPagination pagination) {
		if (CollectionUtils.isEmpty(pagination.getSorts())) {
			return Optional.of(
					new SolrQuery.SortClause("score", SolrQuery.ORDER.desc)
			);
		}
		for (final Sort sort : pagination.getSorts()) {
			if (sort.getSortColumn() == null) {
				continue;
			}
			final String sortColumnEnumName = sort.getSortColumn().toUpperCase();
			final GroupSolrDataPagination.SORTS sortColumn = GroupSolrDataPagination.SORTS.valueOf(sortColumnEnumName);
			final String getSortItem = pagination.getSortColumn(sortColumn);
			final String paginationSortOrder = pagination.getSortOrder(sortColumn);
			final SolrQuery.ORDER order = getSortOrder(sort, paginationSortOrder);

			return Optional.of(
					new SolrQuery.SortClause(getSortItem, order)
			);
		}
		return Optional.absent();
	}

	private SolrQuery.ORDER getSortOrder(final Sort sort, final String paginationSortOrder) {
		final Optional<SolrQuery.ORDER> sortOrder = getSolrSortOrder(sort);
		if (sortOrder.isPresent()) {
			return sortOrder.get();
		}

		if (Pagination.SORT_DIRECTION.ASC.equals(Pagination.SORT_DIRECTION.valueOf(paginationSortOrder))) {
			return SolrQuery.ORDER.asc;
		}

		return SolrQuery.ORDER.desc;
	}

	private Optional<SolrQuery.ORDER> getSolrSortOrder(final Sort sort) {
		if (Pagination.SORT_DIRECTION.ASC.equals(sort.getSortDirection())) {
			return Optional.of(SolrQuery.ORDER.asc);
		}

		if (Pagination.SORT_DIRECTION.DESC.equals(sort.getSortDirection())) {
			return Optional.of(SolrQuery.ORDER.desc);
		}
		return Optional.absent();
	}
}
