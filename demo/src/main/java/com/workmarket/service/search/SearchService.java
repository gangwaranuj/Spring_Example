package com.workmarket.service.search;

import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.search.gen.FeedMessages.FindWorkFeedRequest;
import com.workmarket.search.gen.FeedMessages.FindWorkFeedResponse;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolRequest;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolResponse;
import com.workmarket.search.gen.WorkMessages.FindWorkRequest;
import com.workmarket.search.gen.WorkMessages.FindWorkResponse;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.request.user.GroupPeopleSearchRequest;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.exception.search.SearchException;

public interface SearchService {

	void reindexAllData();
	void reindexAllUsers();
	void reindexAllGroups();
	void reindexAllWork();
	void reindexAllVendors();

	GroupSolrDataPagination searchAllGroups(GroupSolrDataPagination pagination) throws Exception;

	WorkSearchResponse searchAllWork(Long userId, WorkSearchRequest request);

	PeopleSearchResponse searchPeople(PeopleSearchRequest request) throws SearchException;

	PeopleSearchResponse searchAssignmentResources(AssignmentResourceSearchRequest request) throws SearchException;

	PeopleSearchResponse searchInternalAssignmentResources(AssignmentResourceSearchRequest request) throws SearchException;

	PeopleSearchResponse searchPeopleGroups(GroupPeopleSearchRequest request) throws SearchException;

	PeopleSearchResponse searchGroupMembers(GroupPeopleSearchRequest request) throws SearchException;

	PeopleSearchResponse searchPeopleForAssessment(PeopleSearchRequest request) throws SearchException;

	void exportPeopleSearch(PeopleSearchRequest request);

	FindTalentPoolResponse findTalentPools(FindTalentPoolRequest request);

	FindWorkFeedResponse findWorkFeed(FindWorkFeedRequest request);

	FindWorkResponse findWork(FindWorkRequest request);
}
