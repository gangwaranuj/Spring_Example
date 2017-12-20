package com.workmarket.data.solr.query.keyword;

import com.workmarket.search.model.SearchType;
import com.workmarket.search.model.SearchUser;
import org.apache.solr.client.solrj.SolrQuery;

public interface SearchKeywordService {
	
	String addKeywordQueryString(String keywordInputString, SolrQuery query, SearchUser currentUser, SearchType searchType);
}
