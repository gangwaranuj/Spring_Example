package com.workmarket.service.search.user;

import com.workmarket.data.solr.query.SearchQueryCreator;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.search.SearchError;
import com.workmarket.search.SearchErrorType;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.search.request.LocationFilter;
import com.workmarket.search.request.user.Constants;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.validator.PeopleSearchRequestValidator;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.infra.analytics.Loggable;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.network.NetworkService;
import com.workmarket.service.search.SearchResultHydrator;
import com.workmarket.service.search.SearchResultParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class PeopleSearchServiceImpl implements PeopleSearchService {

	private static final Log logger = LogFactory.getLog(PeopleSearchServiceImpl.class);

	@Qualifier("peopleSearchResultHydrator")
	@Autowired private SearchResultHydrator searchResultHydrator;
	@Autowired private PeopleSearchRequestValidator validator;
	@Resource(name = "userSolrServer") private SolrServer userSolrServer;
	@Qualifier("peopleSearchQueryCreator")
	@Autowired private SearchQueryCreator queryGenerator;
	@Qualifier("peopleSearchResultParser")
	@Autowired private SearchResultParser searchResultParser;
	@Autowired private NetworkService networkService;
	@Autowired private UserService userService;
	@Autowired private IndustryService industryService;
	@Autowired private AuthenticationService authenticationService;

	@Override
	@Loggable
	public PeopleSearchResponse searchPeople(PeopleSearchRequest request) throws SearchException {
		Assert.notNull(request);
		PeopleSearchTransientData data = convertToTransientData(request);
		return searchPeople(data);
	}

	private PeopleSearchTransientData convertToTransientData(PeopleSearchRequest request) {
		PeopleSearchTransientData data = new PeopleSearchTransientData();
		data.setOriginalRequest(request);
		data.setSearchType(request.getSearchType());
		return data;
	}

	@Override
	public PeopleSearchResponse searchPeople(PeopleSearchTransientData transientData) throws SearchException {
		PeopleSearchTransientData data = augmentAndValidateTransientData(transientData);
		PeopleSearchResponse response = performSearch(data);
		return (PeopleSearchResponse) searchResultHydrator.hydrateSearchResult(response, transientData);
	}

	private PeopleSearchTransientData augmentAndValidateTransientData(PeopleSearchTransientData transientData) throws SearchException {
		PeopleSearchRequest request = (PeopleSearchRequest)transientData.getOriginalRequest();

		transientData.addToIgnoredLanes(LaneType.LANE_23);

		if (request.isSetLocationFilter()) {
			LocationFilter locationFilter = request.getLocationFilter();
			if (!locationFilter.isSetMaxMileFromResourceToLocation()) {
				locationFilter.setMaxMileFromResourceToLocation(Constants.MAX_MILES);
			}
		}

		validator.validate(request);

		SearchUser user = buildSearchUser(request.getUserId());

		validateUserWithRequest(request, user);

		List<Long> networkIds = networkService.findAllCompanyNetworkIds(user.getCompanyId());
		if (isNotEmpty(networkIds)) {
			request.setNetworkIds(networkIds);
		}

		// setup the location for the search
		transientData.setCurrentUser(user);
		transientData.setOriginalRequest(request);

		return transientData;
	}

	private SearchUser buildSearchUser(Long userId) {
		SearchUser user = userService.getSearchUser(userId);
		user.setBlockedUserIds(userService.findAllBlockedUserIdsByBlockingUserId(userId));
		user.setIndustries(industryService.getIndustryIdsForProfile(user.getProfileId()));
		return user;
	}

	private PeopleSearchResponse performSearch(PeopleSearchTransientData hydrateData) throws SearchException {
		SearchQuery query = queryGenerator.createSearchQuery(hydrateData);
		logger.info("request: " + hydrateData.getOriginalRequest() + "\n Query:" + query.getQuery());

		try {
			QueryResponse response = userSolrServer.query(query);
			PeopleSearchResponse psResponse = (PeopleSearchResponse) searchResultParser.parseSolrQueryResponse(new PeopleSearchResponse(), hydrateData, response);

			if (isNotEmpty(query.getSearchWarnings())) {
				psResponse.setWarnings(query.getSearchWarnings());
			}
			return psResponse;
		} catch (SolrServerException e) {
			logger.fatal(e);
			throw new SearchException("Solr error", e);
		}
	}

	private void validateUserWithRequest(PeopleSearchRequest request, SearchUser currentUser) throws SearchException {
		if (currentUser == null) {
			List<SearchError> errors = new ArrayList<>(1);
			errors.add(new SearchError(SearchErrorType.INVALID_REQUEST, "User does not exist"));
			throw new SearchException("Invalid user id " + request.getUserId() + "for requested search", errors);
		}
		Long company = currentUser.getCompanyId();
		if (company == null) {
			List<SearchError> errors = new ArrayList<>(1);
			errors.add(new SearchError(SearchErrorType.NO_COMPANY, "User does not have a company"));
			throw new SearchException("No company found in database for user " + request.getUserId(), errors);
		}
	}
}
