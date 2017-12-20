package com.workmarket.data.solr.query;

import com.workmarket.data.solr.query.keyword.RelevancyKeywordService;
import com.workmarket.data.solr.query.keyword.SearchKeywordService;
import com.workmarket.data.solr.query.location.LocationQueryCreationService;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.external.GeocodingException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.gdata.util.common.base.Preconditions.checkNotNull;

/**
 * Simple factory class to output the query portion of solr queries
 * <p/>
 * It's made to create the query boosters as well as the query portion of the
 * solr query that's being created
 * <p/>
 * This is also the portion of the query that matches based on the list
 */
@Component
public class PeopleSearchSolrQueryStringImpl implements PeopleSearchSolrQueryString {

	private static final Log logger = LogFactory.getLog(PeopleSearchSolrQueryStringImpl.class);

	private final LocationQueryCreationService locationQueryService;
	private final RelevancyKeywordService relevancyQueryService;
	private final SearchKeywordService keywordQueryService;

	@Autowired
	public PeopleSearchSolrQueryStringImpl(
		final LocationQueryCreationService locationQueryService,
		final RelevancyKeywordService relevancyQueryService,
		final SearchKeywordService keywordQueryService)
	{
		checkNotNull(locationQueryService);
		checkNotNull(relevancyQueryService);
		checkNotNull(keywordQueryService);
		this.keywordQueryService = keywordQueryService;
		this.relevancyQueryService = relevancyQueryService;
		this.locationQueryService = locationQueryService;
	}

	@Override
	public void addQueryString(PeopleSearchTransientData data, SearchQuery query) throws SearchException {
		NamedList<String> list = new NamedList<>();
		if (data.isEnhancedRelevancy()) {
			list.add("qt", "/userSearchEnhanced");
		} else if (data.isTypeAhead()) {
			list.add("qt", "/userSearchSuggest");
		} else {
			list.add("qt", "/regularUserSearch");
		}

		PeopleSearchRequest request = (PeopleSearchRequest) data.getOriginalRequest();
		SearchUser currentUser = data.getCurrentUser();
		keywordQueryService.addKeywordQueryString(request.getKeyword(), query, currentUser, data.getSearchType());

		String boostFunction = relevancyQueryService.createRelevancyString(currentUser, data);
		String locationBoostFunction = addLocationQuery(data, query);

		if (relevancyQueryService.isRequestSortedByRelevancy(request) && !SearchType.PEOPLE_SEARCH_ASSIGNMENT_FULL_NAME.equals(data.getSearchType())) {
			if (data.isEnhancedRelevancy()) {
				list.add("bf", String.format("%s %s", boostFunction, locationBoostFunction));
				if (StringUtils.isNotBlank(data.getSkills())) {
					String workBoostQuery = relevancyQueryService.createAssignmentRelevancyString(data.getSkills());
					if (StringUtils.isNotBlank(workBoostQuery)) {
						list.add("bq", workBoostQuery);
					}
				}
			} else {
				if (StringUtils.isNotBlank(boostFunction)) {
					list.add("bq", boostFunction);
				}
				if (StringUtils.isNotBlank(locationBoostFunction)) {
					list.add("bf", locationBoostFunction);
				}
			}
		}

		query.add(SolrParams.toSolrParams(list));
		logger.info(query.getQuery());
	}

	@SuppressWarnings("unchecked")
	private String addLocationQuery(PeopleSearchTransientData data, SearchQuery query) {
		try {
			return locationQueryService.addLocationQuery(data, query);
		} catch (GeocodingException e) {
			locationQueryService.setGeoCodingWarning(data, e, query);
		}
		return StringUtils.EMPTY;
	}

}
