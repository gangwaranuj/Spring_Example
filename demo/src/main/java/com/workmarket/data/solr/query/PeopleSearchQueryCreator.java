package com.workmarket.data.solr.query;

import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.model.query.PeopleSearchQuery;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.search.request.SearchRequest;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.domains.model.requirementset.SolrQueryVisitor;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.search.user.PeopleSearchSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@SuppressWarnings("unchecked")
@Component
public class PeopleSearchQueryCreator implements SearchQueryCreator<PeopleSearchTransientData> {

	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private PeopleSearchSolrQueryString queryString;
	@Autowired private PeopleSearchSort sort;
	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private SolrQueryVisitor visitor;

	@Override
	public <S extends SearchRequest> SearchQuery createSearchQuery(PeopleSearchTransientData data, S request) throws SearchException {
		return createSearchQuery(data);
	}

	@Override
	public SearchQuery createSearchQuery(PeopleSearchTransientData data) throws SearchException {
		PeopleSearchRequest request = (PeopleSearchRequest)data.getOriginalRequest();
		PeopleSearchQuery query = makePeopleSearchQuery(data);
		SearchType searchType = data.getSearchType();

		queryString.addQueryString(data, query);

		// add the user and company to our query string for analytics
		if (data.getCurrentUser() != null) {
			if (data.getCurrentUser().getId() != null) {
				query.add(SolrMetricConstants.USER, data.getCurrentUser().getId().toString());
			}
			if (data.getCurrentUser().getCompanyId() != null) {
				query.add(SolrMetricConstants.COMPANY, data.getCurrentUser().getCompanyId().toString());
			}
		}

		// Check search type
		if (searchType != null) {
			// add some additional info to the query for metrics gathering
			query.add(SolrMetricConstants.SEARCH_TYPE, searchType.toString());
			if (searchType == SearchType.PEOPLE_SEARCH_ELIGIBILITY) {
				query.add(SolrMetricConstants.PERSONA, SolrMetricConstants.SYSTEM_PERSONA);
			} else {
				query.add(SolrMetricConstants.PERSONA, SolrMetricConstants.EMPLOYER_PERSONA);
			}

			// now enrich the query as necessary
			switch (searchType) {

				case PEOPLE_SEARCH_GROUP_MEMBER:
					// Filter to fetch the current group's members and apply special insurance filters
					query
						.addGroupStatusFilters()
						.addGroupStatusFacets()
						.addInsuranceTypesFilterQuery()

						.addBaseQueryParams()
						.addBaseFilters()
						.addBaseFacets();
					break;

				case PEOPLE_SEARCH_GROUP:
					// Filter out current members, plus invited, pending, and declined users from group invite search
					query
						.addGroupInviteFilters()

						.addBaseQueryParams()
						.addBaseFilters()
						.addLaneFilterQuery()
						.addBlockedUserFilters()
						.addBaseFacets();
					break;

				case PEOPLE_SEARCH_ASSESSMENT_INVITE:
					// Filter users by assessment status
					query
						.addCurrentAssessmentFilters()
						.addCurrentAssessmentFacets()

						.addBaseQueryParams()
						.addBaseFilters()
						.addLaneFilterQuery()
						.addBlockedUserFilters()
						.addBaseFacets();
					break;

				case PEOPLE_SEARCH_TYPE_AHEAD:
					query
						.addBaseFilters()
						.addLaneFilterQuery()
						.addBlockedUserFilters();
					if (data.getFailedVerifications() != null) {
						query.addFailedScreeningFilterQuery();
					}
					query.setRows(10);
					query.setStart(0);

					break;

				default:
					query
						.addBaseQueryParams()
						.addBaseFilters()
						.addLaneFilterQuery()
						.addBlockedUserFilters()
						.addBaseFacets()
						.addInsuranceFilterQuery();
					if (data.getFailedVerifications() != null) {
						query.addFailedScreeningFilterQuery();
					}
					break;
			}
		}

		// Filter by requirements
		if (request.getRequirements() != null) {
			for (AbstractRequirement requirement : request.getRequirements()) {
				requirement.accept(visitor, query);
			}
		}

		//Sort
		sort.addSortField(query, data);

		return query;
	}

	public PeopleSearchQuery makePeopleSearchQuery(PeopleSearchTransientData data) {
		return new PeopleSearchQuery(data);
	}
}
