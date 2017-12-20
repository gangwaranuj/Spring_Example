package com.workmarket.data.solr.query.keyword;

import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.service.exception.search.SearchException;

import java.util.List;

public interface RelevancyKeywordService {

	String createRelevancyString(SearchUser user, PeopleSearchTransientData data) throws SearchException;

	String createAssignmentRelevancyString(String skills);

	String createGroupInNetworkRelevanceString(List<Long> networkIds);

	boolean isRequestSortedByRelevancy(PeopleSearchRequest request);
}
