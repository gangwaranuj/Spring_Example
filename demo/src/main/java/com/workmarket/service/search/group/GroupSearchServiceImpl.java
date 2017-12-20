package com.workmarket.service.search.group;

import com.workmarket.dao.UserDAO;
import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.search.group.indexer.model.GroupSolrData;
import com.workmarket.domains.search.group.indexer.service.GroupIndexer;
import com.workmarket.search.SearchClient;
import com.workmarket.search.cache.HydratorCache;
import com.workmarket.search.gen.Common.Status;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolRequest;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolResponse;
import com.workmarket.search.model.query.GroupSearchQuery;
import com.workmarket.search.request.group.GroupSearchRequest;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.dto.GroupSearchFilterDTO;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.infra.analytics.Loggable;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Service
public class GroupSearchServiceImpl implements GroupSearchService {

	private static final Log logger = LogFactory.getLog(GroupSearchServiceImpl.class);

	@Autowired private GroupIndexer groupIndexer;
	@Autowired private GroupSearchResultHydrator groupSearchResultHydrator;
	@Autowired private UserDAO userDAO;
	@Autowired private GroupSearchQueryService groupSearchQueryService;
	@Autowired @Qualifier("groupSolrServer") private HttpSolrServer groupSolrServer;
	@Autowired UserGroupService userGroupService;
	@Autowired private EventRouter eventRouter;
	@Autowired private HydratorCache hydratorCache;
	@Autowired private SearchClient searchClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@Override
	public void reindexAllGroups() {
		logger.info("Re-indexing all groups");
		groupIndexer.reindexAll();
	}

	@Override
	public void reindexGroups(List<Long> groupIds) {
		if (CollectionUtilities.isEmpty(groupIds)) {
			return;
		}
		logger.info("Re-indexing " + groupIds.size() + " groups");
		groupIndexer.reindexById(groupIds);
	}

	@Override
	@Loggable
	public GroupSolrDataPagination searchAllCompanyGroups(GroupSolrDataPagination pagination) {
		Assert.notNull(pagination);
		Assert.notNull(pagination.getSearchFilter());
		Assert.notNull(pagination.getSearchFilter().getUserId());

		GroupSearchFilterDTO userSearchFilter = pagination.getSearchFilter();

		User user = userDAO.getUser(userSearchFilter.getUserId());
		GroupSearchQuery query = groupSearchQueryService.build(user, pagination);

		QueryResponse solrResponse = null;

		try {
			solrResponse = groupSolrServer.query(query);
		} catch (SolrServerException e) {
			logger.error("error searching for groups ", e);
		}
		GroupSearchRequest groupSearchRequest = new GroupSearchRequest()
				.setUser(user)
				.setGroupSearchFilter(userSearchFilter)
				.setSearchType(pagination.getSearchType());

		List<GroupSolrData> beans = groupSearchResultHydrator.hydrateSearchResult(solrResponse, groupSearchRequest);
		Map<String, Integer> facetQuery = solrResponse.getFacetQuery();
		pagination.setResults(beans);
		pagination.setRowCount(facetQuery != null ? MapUtils.getInteger(facetQuery, "*:*", 0) : 0);
		return pagination;
	}

	@Override
	public void reindexGroup(Long groupId) {
		Assert.notNull(groupId);
		hydratorCache.updateGroupCache(groupId);
		groupIndexer.reindexById(groupId);
	}

	@Override
	public void reindexGroupMembers(Long groupId) {
		Assert.notNull(groupId);
		List<Long> memberIds = userGroupService.getAllActiveGroupMemberIds(groupId);
		eventRouter.sendEvent(
			new UserSearchIndexEvent(memberIds));
	}

	@Override
	public FindTalentPoolResponse findTalentPools(final FindTalentPoolRequest request) {
		final FindTalentPoolResponse defaultResponse = FindTalentPoolResponse.newBuilder()
			.setStatus(Status.newBuilder()
				.setSuccess(false)
				.addMessages("search client failed to get response from service")
				.build())
			.build();
		return searchClient.findTalentPool(request, webRequestContextProvider.getRequestContext())
			.toBlocking()
			.singleOrDefault(defaultResponse);
	}
}
