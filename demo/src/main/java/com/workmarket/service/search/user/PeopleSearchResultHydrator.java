package com.workmarket.service.search.user;

import com.google.api.client.util.Maps;
import com.workmarket.domains.groups.model.UserGroupHydrateData;
import com.workmarket.search.model.AbstractSearchTransientData;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.search.SearchResultHydrator;
import com.workmarket.service.search.SearchResultHydratorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class PeopleSearchResultHydrator extends SearchResultHydratorImpl implements SearchResultHydrator<PeopleSearchResponse> {

	private static final Logger logger = LoggerFactory.getLogger(PeopleSearchResultHydrator.class);

	@Override
	public PeopleSearchResponse hydrateSearchResult(PeopleSearchResponse response, AbstractSearchTransientData hydrateData) {
		Assert.notNull(response);
		return hydratePeopleSearchResult(response, (PeopleSearchTransientData) hydrateData);
	}

	private PeopleSearchResponse hydratePeopleSearchResult(PeopleSearchResponse response, PeopleSearchTransientData hydrateData) {
		try {
			SearchUser currentUser = hydrateData.getCurrentUser();
			// a lot of the facet IDs have a {COMPANY_ID}_{BLAH_ID} style to it..
			// let's just get the real IDs and not what the user has
			fixGroupFacetIds(response);
			fixAssessmentFacetIds(response);
			hydrateDistances(response, hydrateData);
			hydrateContextualWorkCounts(response, hydrateData);

			// Lane 4 results need to chop off their last names - removing notion of in network
			//shortenLane4LastNames(response);

			hydrateLanes(response, hydrateData);
			hydrateLicenseData(response, hydrateData);

			//Group names
			Future<Map<Long, UserGroupHydrateData>> groupHydrateDataMap = hydrateGroupFacetData(response, hydrateData);
			// Shared group names
			Future<Map<Long, UserGroupHydrateData>> sharedGroupHydrateDataMap = hydrateSharedGroupFacetData(response, hydrateData);
			// Merge group and shared group hydrate data for results
			Map<Long, UserGroupHydrateData> allGroups = Maps.newHashMap();
			allGroups.putAll(groupHydrateDataMap.get());
			allGroups.putAll(sharedGroupHydrateDataMap.get());
			addGroupNamesToResults(response, hydrateData, allGroups);

			//Industries
			Future<Map<Long, String>> industryNames = hydrateIndustryFacets(response, hydrateData);
			hydrateIndustryData(response, hydrateData, industryNames.get());

			//Assessments
			Future<Map<Long, String>> assessments = hydrateAssessmentsFacets(response, hydrateData);
			addCompanyAssessmentNamesToResults(response, hydrateData, assessments.get());

			hydrateCertificationData(response, hydrateData);
			hydrateCompanyTypes(response, hydrateData);
			hydrateRatings(response, currentUser);
			hydrateInsurance(response, hydrateData);

			//Country names
			hydrateCountryFacets(response, hydrateData);

			hydrateCompanyData(response, hydrateData);
			sortFacets(response);
			} catch (InterruptedException | ExecutionException e) {
			 	logger.error("error hydrating results", e);
			}
		return response;
	}
}
