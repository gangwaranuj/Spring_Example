package com.workmarket.service.search.group;

import com.google.common.collect.Maps;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.domains.search.group.indexer.model.GroupSolrData;
import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.domains.model.User;
import com.workmarket.search.request.group.GroupSearchRequest;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.dto.GroupSearchFilterDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.LaneContext;
import com.workmarket.utility.CollectionUtilities;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Component
public class GroupSearchResultHydrator {

	@Autowired private LaneService laneService;
	@Autowired private IndustryDAO industryDAO;
	@Autowired private AuthenticationService authenticationService;

	List<GroupSolrData> hydrateSearchResult(QueryResponse response, GroupSearchRequest groupSearchRequest) {
		Assert.notNull(response);
		Assert.notNull(groupSearchRequest.getGroupSearchFilter());

		List<GroupSolrData> groupResults;
		groupResults = response.getBeans(GroupSolrData.class);

		GroupSearchFilterDTO groupSearchFilter = groupSearchRequest.getGroupSearchFilter();
		User user = groupSearchRequest.getUser();
		Assert.notNull(user);

		if (response.getFacetFields() != null) {
			for (FacetField facetField : response.getFacetFields()) {
				if (facetField.getName().endsWith(groupSearchFilter.getIndustries().getField()))
					for (FacetField.Count facetQuery : CollectionUtilities.nullSafeCollection(facetField.getValues())) {

						GroupSearchFilterDTO.FacetFieldFilter facetFieldFilter = null;
						for (int i = 0; i < groupSearchFilter.getIndustries().getFacetFieldFilters().size(); i++)
							if (groupSearchFilter.getIndustries().getFacetFieldFilters().get(i).getId().equals(facetQuery.getName())) {
								facetFieldFilter = groupSearchFilter.getIndustries().getFacetFieldFilters().get(i);
								break;
							}
						if (facetFieldFilter == null && facetQuery.getName() != null) {
							facetFieldFilter = new GroupSearchFilterDTO.FacetFieldFilter(groupSearchFilter.getIndustries(), facetField.getName(), facetField.getName(), facetQuery.getName(), Long.valueOf(facetQuery.getCount()).intValue(), true);
							groupSearchFilter.getIndustries().getFacetFieldFilters().add(facetFieldFilter);
						}
						if (facetFieldFilter != null)
							facetFieldFilter.setCount(Long.valueOf(facetQuery.getCount()).intValue());
					}
			}
		}

		// hydrate
		Map<Long, GroupSolrData> results = Maps.newHashMap();
		for (GroupSolrData sto : groupResults) {
			results.put(sto.getId(), sto);

			if (GroupSolrDataPagination.SEARCH_TYPE.SEARCH_ALL_OPEN_GROUPS.equals(groupSearchRequest.getSearchType())) {
				if (sto.getLane1Flag()) {
					sto.setAuthorizedToJoin(true);
				} else if (sto.getLane2Flag()) {
					sto.setAuthorizedToJoin(true);
				} else if (sto.getLane3Flag()) {
					if (authenticationService.isLane3Active(user)) {
						sto.setAuthorizedToJoin(true);
					} else {
						sto.setAuthorizedToJoin(false);
					}
				} else if (!sto.getLane0Flag() && !sto.getLane1Flag() && !sto.getLane2Flag() && !sto.getLane3Flag() && !sto.getLane4Flag()) {
					if (authenticationService.isLane3Active(user)) {
						sto.setAuthorizedToJoin(true);
					} else {
						LaneContext laneContext = laneService.getLaneContextForUserAndCompany(user.getId(), sto.getCompanyId());
						if (laneContext != null && laneContext.isInWorkerPool()) {
							sto.setAuthorizedToJoin(true);
						} else {
							sto.setAuthorizedToJoin(false);
						}
					}
				}
			}
		}
		for (GroupSearchFilterDTO.FacetFieldFilter filter : groupSearchFilter.getIndustries().getFacetFieldFilters()) {
			if (filter.getId() != null) {
				filter.setLabel(industryDAO.findIndustryById(filter.getIdAsInteger().longValue()).getName());
			}
		}

		groupSearchFilter.getIndustries().sortLabels();

		return groupResults;
	}
}
