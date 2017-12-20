package com.workmarket.service.search;

import com.workmarket.data.solr.indexer.user.SolrVendorIndexer;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.data.solr.model.group.GroupSolrDataPagination;
import com.workmarket.feature.FeatureToggleClient;
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
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.feed.FeedService;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.group.GroupSearchService;
import com.workmarket.service.search.user.AssessmentInviteSearchService;
import com.workmarket.service.search.user.GroupMemberSearchService;
import com.workmarket.service.search.user.GroupResourceSearchService;
import com.workmarket.service.search.user.PeopleSearchService;
import com.workmarket.service.search.user.WorkResourceSearchService;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

	private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

	@Autowired private GroupSearchService groupSearchService;
	@Autowired private WorkSearchService workSearchService;
	@Autowired private UserIndexer userIndexer;
	@Autowired private SolrVendorIndexer vendorIndexer;
	@Autowired private WorkIndexer workIndexer;
	@Autowired private WorkResourceSearchService workResourceSearchService;
	@Autowired private GroupResourceSearchService groupResourceSearchService;
	@Autowired private GroupMemberSearchService groupMemberSearchService;
	@Autowired private PeopleSearchService peopleSearchService;
	@Autowired private AssessmentInviteSearchService assessmentInviteSearchService;
	@Autowired private FeatureToggleClient featureToggleClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private FeedService feedService;

	@Autowired private EventFactory eventFactory;
	@Autowired private EventRouter eventRouter;

	@Override
	public WorkSearchResponse searchAllWork(Long userId, WorkSearchRequest request) {
		if (userId == null || request == null) {
			return new WorkSearchResponse();
		}
		return workSearchService.searchAllWorkByUserId(userId, request);
	}

	@Override
	public GroupSolrDataPagination searchAllGroups(GroupSolrDataPagination pagination) throws Exception {
		if (pagination == null) {
			return null;
		}
		return groupSearchService.searchAllCompanyGroups(pagination);
	}

	@Override
	public void reindexAllData() {
		logger.info("Reindexing all data");
		userIndexer.reindexAll();
		groupSearchService.reindexAllGroups();
		workIndexer.reindexAll();
	}

	@Override
	public void reindexAllGroups() {
		groupSearchService.reindexAllGroups();
	}

	@Override
	public void reindexAllUsers() {
		userIndexer.reindexAll();
	}

	@Override
	public void reindexAllVendors() {
		vendorIndexer.reindexAll();
	}

	@Override
	public void reindexAllWork() {
		workIndexer.reindexAll();
	}

	@Override
	public PeopleSearchResponse searchPeople(PeopleSearchRequest request) throws SearchException {
		return peopleSearchService.searchPeople(request);
	}

	@Override
	public PeopleSearchResponse searchAssignmentResources(AssignmentResourceSearchRequest request) throws SearchException {
		return workResourceSearchService.searchAssignmentWorkers(request);
	}

	@Override
	public PeopleSearchResponse searchPeopleGroups(GroupPeopleSearchRequest request) throws SearchException {
		return groupResourceSearchService.searchPeople(request);
	}

	@Override
	public PeopleSearchResponse searchGroupMembers(GroupPeopleSearchRequest request) throws SearchException {
		return groupMemberSearchService.searchPeople(request);
	}

	@Override
	public PeopleSearchResponse searchPeopleForAssessment(PeopleSearchRequest request) throws SearchException {
		return assessmentInviteSearchService.searchPeople(request);
	}

	@Override
	public void exportPeopleSearch(PeopleSearchRequest request) {
		eventRouter.sendEvent(eventFactory.buildSearchCSVGenerateEvent(request.setExportSearch(true)));
	}

	@Override
	public FindTalentPoolResponse findTalentPools(final FindTalentPoolRequest request) {
		return groupSearchService.findTalentPools(request);
	}

	@Override
	public PeopleSearchResponse searchInternalAssignmentResources(AssignmentResourceSearchRequest request) throws SearchException {
		return workResourceSearchService.searchInternalAssignmentWorkers(request);
	}

	@Override
	public FindWorkFeedResponse findWorkFeed(FindWorkFeedRequest request) {
		return feedService.findWorkFeed(request);
	}

	@Override
	public FindWorkResponse findWork(FindWorkRequest request) {
		return workSearchService.findWork(request);
	}

}
