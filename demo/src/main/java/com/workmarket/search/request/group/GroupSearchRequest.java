package com.workmarket.search.request.group;

import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.domains.model.User;
import com.workmarket.search.request.SearchRequest;
import com.workmarket.service.business.dto.GroupSearchFilterDTO;

import java.io.Serializable;

/**
 * Author: rocio
 */
public class GroupSearchRequest extends SearchRequest implements Serializable {

	private static final long serialVersionUID = 8982152011687231111L;
	private User user;
	private GroupSearchFilterDTO groupSearchFilter;
	private GroupSolrDataPagination.SEARCH_TYPE searchType;

	public GroupSearchFilterDTO getGroupSearchFilter() {
		return groupSearchFilter;
	}

	public GroupSearchRequest setGroupSearchFilter(GroupSearchFilterDTO groupSearchFilter) {
		this.groupSearchFilter = groupSearchFilter;
		return this;
	}

	public GroupSolrDataPagination.SEARCH_TYPE getSearchType() {
		return searchType;
	}

	public GroupSearchRequest setSearchType(GroupSolrDataPagination.SEARCH_TYPE searchType) {
		this.searchType = searchType;
		return this;
	}

	public User getUser() {
		return user;
	}

	public GroupSearchRequest setUser(User user) {
		this.user = user;
		return this;
	}
}
