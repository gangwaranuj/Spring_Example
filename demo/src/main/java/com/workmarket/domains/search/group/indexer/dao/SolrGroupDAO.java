package com.workmarket.domains.search.group.indexer.dao;

import com.workmarket.dao.search.SolrDAO;
import com.workmarket.domains.search.group.indexer.model.GroupSolrData;

public interface SolrGroupDAO extends SolrDAO<GroupSolrData> {

	Integer getMaxGroupId();
}
