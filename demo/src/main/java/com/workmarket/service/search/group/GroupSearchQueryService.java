package com.workmarket.service.search.group;

import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.domains.model.User;
import com.workmarket.search.model.query.GroupSearchQuery;

public interface GroupSearchQueryService {

	GroupSearchQuery build(User user, GroupSolrDataPagination pagination);
}
