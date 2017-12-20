package com.workmarket.service.search.group;

import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolResponse;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolRequest;

import java.util.List;

public interface GroupSearchService {

	void reindexAllGroups();

	void reindexGroups(List<Long> groupIds);

	void reindexGroup(Long groupId);

	GroupSolrDataPagination searchAllCompanyGroups(GroupSolrDataPagination pagination);

	void reindexGroupMembers(Long groupId);

	FindTalentPoolResponse findTalentPools(FindTalentPoolRequest request);
}
