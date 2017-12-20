package com.workmarket.service.search.work;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.query.WorkSearchQueryCreator;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.search.SearchClient;
import com.workmarket.search.gen.Common;
import com.workmarket.search.gen.WorkMessages.FindWorkResponse;
import com.workmarket.search.gen.WorkMessages.FindWorkRequest;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.model.WorkSearchTransientData;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.analytics.Loggable;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.SearchResultHydrator;
import com.workmarket.service.search.SearchResultParser;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;

@Service
public class WorkSearchServiceImpl implements WorkSearchService {

	private static final Log logger = LogFactory.getLog(WorkSearchServiceImpl.class);

	@Autowired private WorkIndexer workIndexer;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private UserService userService;
	@Autowired private WorkSearchQueryCreator searchQueryCreator;
	@Qualifier("workSearchResultParser")
	@Autowired private SearchResultParser searchResultParser;
	@Qualifier("workSearchResultHydrator")
	@Autowired private SearchResultHydrator searchResultHydrator;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private CompanyService companyService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private SearchClient searchClient;
	@Autowired private FeatureEntitlementService featureEntitlementService;
	@Autowired private FindWorkResponseAdapter findWorkResponseAdapter;
	@Autowired private FindWorkRequestAdapter findWorkRequestAdapter;

	@Autowired
	@Qualifier("workSolrServer")
	private HttpSolrServer workSolrServer;

	@Override
	public void reindexAllWorkByCompanyAsynchronous(long companyId) {
		logger.info("Re-indexing assignments for company " + companyId + " from the queue");
		eventRouter.sendEvent(eventFactory.buildWorkUpdateSearchIndexByCompanyEvent(companyId));
	}

	@Override
	public void reindexWorkAsynchronous(Collection<Long> workIds) {
		logger.info("Re-indexing assignment: " + workIds.toString());
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workIds));
	}

	@Override
	public void reindexWorkAsynchronous(Long workId) {
		logger.info("Re-indexing assignment: " + workId);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
	}

	@Override
	@Loggable
	public WorkSearchResponse searchAllWorkByUserId(Long userId, WorkSearchRequest request) {
		User user = validateRequest(userId, request);
		WorkSearchTransientData transientData = decorateSearchRequest(user, request);
		return searchAllWorkWithFeatureToggle(request, transientData);
	}

	@Override
	@Loggable
	public WorkSearchResponse searchAllWorkByCompanyId(Long companyId, WorkSearchRequest request) {
		Long currentUserId = authenticationService.getCurrentUserId();
		WorkSearchTransientData transientData = decorateSearchRequest(validateRequest(currentUserId, request), request);

		Assert.notNull(companyService.findById(companyId));
		transientData.setCompanyId(companyId);

		return searchAllWorkWithFeatureToggle(request, transientData);
	}

	@Override
	public FindWorkResponse findWork(FindWorkRequest request) {
		final FindWorkResponse defaultResponse = FindWorkResponse.newBuilder()
			.setStatus(Common.Status.newBuilder()
				.setSuccess(false)
				.addMessages("search client failed to get response from service")
				.build())
			.build();
		return searchClient.findWork(request, webRequestContextProvider.getRequestContext())
			.toBlocking()
			.singleOrDefault(defaultResponse);
	}

	private WorkSearchResponse searchAllWork(WorkSearchRequest request, WorkSearchTransientData transientData) {
		SearchQuery searchQuery = null;

		try {
			searchQuery = searchQueryCreator.createSearchQuery(transientData, request);
			QueryResponse solrQueryResponse = workSolrServer.query(searchQuery);
			WorkSearchResponse workResponse = (WorkSearchResponse)searchResultParser.parseSolrQueryResponse(new WorkSearchResponse(), transientData, solrQueryResponse);
			workResponse.setResultsLimit(request.getPageSize());

			if (CollectionUtils.isNotEmpty(searchQuery.getSearchWarnings())) {
				workResponse.setWarnings(searchQuery.getSearchWarnings());
			}
			return (WorkSearchResponse) searchResultHydrator.hydrateSearchResult(workResponse, transientData);

		} catch (SearchException | SolrServerException | SolrException s) {
			logger.error("Error searching for work " + searchQuery, s);
		}
		return new WorkSearchResponse();
	}

	public WorkSearchTransientData decorateSearchRequest(User user, WorkSearchRequest request) {
		Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(user.getId());

		if (personaPreferenceOptional.isPresent()) {
			request.setWorkSearchRequestUserType(
				personaPreferenceOptional.get().isBuyer() ? WorkSearchRequestUserType.CLIENT : WorkSearchRequestUserType.RESOURCE
			);
		}

		boolean isManager = authenticationService.authorizeUserByAclPermission(user.getId(), Permission.VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS);
		boolean isDispatcher = personaPreferenceOptional.isPresent() && personaPreferenceOptional.get().isDispatcher();
		request.setShowAllAtCompany(isManager || isDispatcher);

		request.setDispatcher(isDispatcher);

		// If searcher is a worker, attach any blocked company ids to the request
		if (WorkSearchRequestUserType.RESOURCE.equals(request.getWorkSearchRequestUserType())) {
			request.setBlockedCompanyIds(userService.findBlockedOrBlockedByCompanyIdsByUserId(user.getId()));
		}

		SearchUser searchUser = makeSearchUser();
		searchUser.setId(user.getId());
		searchUser.setCompanyId(user.getCompany().getId());

		WorkSearchTransientData transientData = makeWorkSearchTransientData();
		transientData.setCurrentUser(searchUser);
		transientData.setOriginalRequest(request);
		transientData.setUserType(request.getWorkSearchRequestUserType());

		if (request.getLatitude() != null && request.getLongitude() != null) {
			transientData.setGeopoint(new GeoPoint(request.getLatitude(), request.getLongitude()));
		}

		return transientData;
	}

	public SearchUser makeSearchUser() {
		return new SearchUser();
	}

	public WorkSearchTransientData makeWorkSearchTransientData() {
		return new WorkSearchTransientData();
	}

	private User validateRequest(Long userId, WorkSearchRequest request) {
		Assert.notNull(userId, "User id is required");
		Assert.notNull(request, "Request is required");

		User user = userService.getUser(userId);
		Assert.notNull(user, "User not found");
		Assert.notNull(user.getCompany(), "User's company is required");
		Assert.notNull(userId, "User id is required");
		return user;
	}

	@Override
	public void workBundleUpdateSearchIndex(WorkBundleDTO workBundleDTO) {
		Assert.notNull(workBundleDTO);
		Assert.notNull(workBundleDTO.getWorkNumbers());
		workIndexer.reindexWorkByWorkNumbers(Sets.newHashSet(workBundleDTO.getWorkNumbers()));
	}

	@Override
	public void optimize() {
		workIndexer.optimize();
	}

	private WorkSearchResponse searchAllWorkWithFeatureToggle(
		final WorkSearchRequest request,
		final WorkSearchTransientData transientData
	) {
		if (featureEntitlementService.hasPercentRolloutFeatureToggle(Constants.SEARCH_SERVICE_WORK)) {
			final FindWorkRequest findWorkRequest = findWorkRequestAdapter.buildFindWorkRequest(request, transientData);
			final FindWorkResponse findWorkResponse = findWork(findWorkRequest);
			final WorkSearchResponse workResponse =
				findWorkResponseAdapter.buildWorkSearchResponse(transientData, findWorkResponse);
			return (WorkSearchResponse) searchResultHydrator.hydrateSearchResult(workResponse, transientData);
		} else {
			return searchAllWork(request, transientData);
		}
	}
}
