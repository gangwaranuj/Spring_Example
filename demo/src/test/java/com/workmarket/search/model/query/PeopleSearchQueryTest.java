package com.workmarket.search.model.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.request.user.BackgroundScreeningChoice;
import com.workmarket.search.request.user.CompanyType;
import com.workmarket.search.request.user.NumericFilter;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class PeopleSearchQueryTest {

	private SearchUser user;
	private PeopleSearchRequest request = new PeopleSearchRequest();
	private PeopleSearchTransientData data = new PeopleSearchTransientData();

	@Mock PeopleSearchQuery mockQuery;

	@Before
	public void setup() throws Exception {
		user = new SearchUser();
		user.setCompanyId(1L);
		data.setCurrentUser(user);
		data.setOriginalRequest(request);
		mockQuery = spy(new PeopleSearchQuery(data));
	}

	@Test
	public void addBaseFacets_noFacetFlagFalse() {
		request.setNoFacetsFlag(false);
		mockQuery.addBaseFacets();
		verify(mockQuery).addUserSearchBaseFacets();
		verify(mockQuery).addLicenseFacetOptions();
		verify(mockQuery).addBackgroundSetFacet();
		verify(mockQuery).addSharedGroupFacets();
	}

	@Test
	public void addBaseFacets_noFacetFlagTrue() {
		request.setNoFacetsFlag(true);
		mockQuery.addBaseFacets();
		verify(mockQuery, never()).addUserSearchBaseFacets();
		verify(mockQuery, never()).addLicenseFacetOptions();
		verify(mockQuery, never()).addBackgroundSetFacet();
		verify(mockQuery, never()).addSharedGroupFacets();
		verify(mockQuery, never()).addFailedScreeningFilterQuery();
	}

	@Test
	public void addGroupStatusFacets_noFacetFlagTrue() {
		request.setNoFacetsFlag(true);
		data.setMemberOfGroupId(1L);
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		query.addGroupStatusFacets();
		assertFoundFacetQuery(query, UserSearchableFields.MEMBER_GROUP_IDS.getName(), false);
		assertFoundFacetQuery(query, UserSearchableFields.MEMBER_OVERRIDE_GROUP_IDS.getName(), false);
		assertFoundFacetQuery(query, UserSearchableFields.PENDING_GROUP_IDS.getName(), false);
		assertFoundFacetQuery(query, UserSearchableFields.PENDING_OVERRIDE_GROUP_IDS.getName(), false);
		assertFoundFacetQuery(query, UserSearchableFields.INVITED_GROUP_IDS.getName(), false);
		assertFoundFacetQuery(query, UserSearchableFields.DECLINED_GROUP_IDS.getName(), false);
	}

	@Test
	public void addGroupStatusFacets_noFacetFlagFalse() {
		request.setNoFacetsFlag(false);
		data.setMemberOfGroupId(1L);
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		query.addGroupStatusFacets();
		assertFoundFacetQuery(query, UserSearchableFields.MEMBER_GROUP_IDS.getName(), true);
		assertFoundFacetQuery(query, UserSearchableFields.MEMBER_OVERRIDE_GROUP_IDS.getName(), true);
		assertFoundFacetQuery(query, UserSearchableFields.PENDING_GROUP_IDS.getName(), true);
		assertFoundFacetQuery(query, UserSearchableFields.PENDING_OVERRIDE_GROUP_IDS.getName(), true);
		assertFoundFacetQuery(query, UserSearchableFields.INVITED_GROUP_IDS.getName(), true);
		assertFoundFacetQuery(query, UserSearchableFields.DECLINED_GROUP_IDS.getName(), true);
	}

	@Test
	public void peopleSearchQuery_addLaneFilterQuery_setUserCompanyId() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		query.addLaneFilterQuery();
		assertFoundFilterQuery(query, "lane0CompanyIds:(" + user.getCompanyId() + ")");
	}

	@Test
	public void peopleSearchQuery_addLaneFilterQuery_setLaneFilterCompanyId() {
		Long otherCompanyId = 2L;
		request.setLaneFilterCompanyId(otherCompanyId);
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		query.addLaneFilterQuery();
		assertFoundFilterQuery(query, "lane0CompanyIds:(" + otherCompanyId + ")");
	}

	@Test
	public void addStartPosition() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		query = query.addStartPosition();
		assertTrue(query.getStart() == 0);
	}

	@Test
	public void addStartPosition_withPagination() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		Pagination pagination = new Pagination();
		pagination.setCursorPosition(5);
		request.setPaginationRequest(pagination);
		query = query.addStartPosition();
		assertTrue(query.getStart() == 5);
	}

	@Test
	public void addRowNumberToQuery() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		query = query.addRowNumberToQuery();
		assertTrue(query.getRows() == 25);
	}

	@Test
	public void addCompanyFilterQuery() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		Set<CompanyType> companyTypeFilter = Sets.newHashSet();
		companyTypeFilter.add(CompanyType.SoleProprietor);
		request.setCompanyTypeFilter(companyTypeFilter);
		query = query.addCompanyTypeFilterQuery();
		assertFoundFilterQuery(query, "companyType");
	}

	@Test
	public void addIndustryFilterQuery() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		request.addToIndustryFilter(1000L);
		query = query.addIndustryFilterQuery();
		assertFoundFilterQuery(query, "industryId");
	}

	@Test
	public void addMinimumSatisfactionRateQuery() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		request.setSatisfactionRateFilter(new NumericFilter().setFrom(20).setTo(100));
		query = query.addMinimumSatisfactionRateQuery();
		assertFoundFilterQuery(query, "satisfactionRate");
	}

	@Test
	public void addMinimumOnTimePercentageQuery() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		request.setOnTimePercentageFilter(new NumericFilter().setFrom(20).setTo(100));
		query = query.addMinimumOnTimePercentageQuery();
		assertFoundFilterQuery(query, "onTimePercentage");
	}

	@Test
	public void addMinimumDeliverableOnTimePercentageQuery() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		request.setDeliverableOnTimePercentageFilter(new NumericFilter().setFrom(20).setTo(100));
		query = query.addMinimumDeliverableOnTimePercentageQuery();
		assertFoundFilterQuery(query, "deliverableOnTimePercentage");
	}

	@Test
	public void addAssessmentFilter() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		request.setAssessmentFilter(Sets.newHashSet(1L));
		query = query.addAssessmentFilter();
		assertFoundFilterQuery(query, "companyAssessmentIds");
	}

	@Test
	public void addInvitedToWorkFilter() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		request.setInvitedToWorkIdFilter(1000L);
		query = query.addInvitedToWorkFilter();
		assertFoundFilterQuery(query, "workInvitedIds");
	}

	@Test
	public void addBackgroundCheckFilter() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		request.setBackgroundScreeningFilter(Sets.newHashSet(BackgroundScreeningChoice.backgroundCheck));
		query = query.addBackgroundCheckFilter();
		assertFoundFilterQuery(query, "verificationIds");
	}

	@Test
	public void addLicenseFilters() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		request.setStateLicenseFilter(Sets.newHashSet("NY"));
		query = query.addLicenseFilters();
		assertFoundFilterQuery(query, "stateLicenseIds");
	}

	@Test
	public void addHasAvatarFilter() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		request.setAvatarFilter(true);
		query = query.addHasAvatarFilter();
		assertFoundFilterQuery(query, "hasAvatar");
	}

	@Test
	public void addCurrentAssessmentFacets() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		request.setCurrentAssessmentId(1L);
		query = query.addCurrentAssessmentFacets();
		assertFoundFacetQuery(query, "invitedAssessmentIds", true);
		assertFoundFacetQuery(query, "passedAssessmentIds", true);
		assertFoundFacetQuery(query, "failedTestIds", true);
	}

	@Test
	public void createNotInvitedToAssessmentQuery() {
		String facetQuery = PeopleSearchQuery.createNotInvitedToAssessmentQuery(1L);
		assertTrue(facetQuery.contains("assessmentStatus"));
	}

	@Test
	public void addInsuranceFilterQuery() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		request.setWorkersCompCoverageFilter(new NumericFilter().setFrom(20).setTo(100));
		request.setGeneralLiabilityCoverageFilter(new NumericFilter().setFrom(20).setTo(100));
		request.setErrorsAndOmissionsCoverageFilter(new NumericFilter().setFrom(20).setTo(100));
		query = query.addInsuranceTypesFilterQuery();
		assertFoundFilterQuery(query, "workersCompCoverage");
		assertFoundFilterQuery(query, "generalLiabilityCoverage");
		assertFoundFilterQuery(query, "errorsAndOmissionsCoverage");
	}

	@Test
	public void addFailedScreeningFilterQuery() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		query = query.addFailedScreeningFilterQuery();
		assertFoundFilterQuery(query, "verificationIds");
	}

	private void assertFoundFacetQuery(PeopleSearchQuery query, String fqName, boolean expectedFound) {
		boolean found = !expectedFound;
		String[] facetQueries = query.getFacetQuery();

		if (facetQueries != null) {
			for (String fq : facetQueries) {
				if (fq.contains(fqName)) {
					found = expectedFound;
					break;
				}
			}
		}

		Assert.assertTrue(found);
	}

	private void assertFoundFilterQuery(PeopleSearchQuery query, String fqName) {
		boolean found = false;
		for (String fq : query.getFilterQueries()) {
			if (fq.contains(fqName)) {
				found = true;
				break;
			}
		}
		Assert.assertTrue(found);
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildMarketplaceLaneFacet_withNullCompany_fails() {
		mockQuery.buildMarketplaceLaneFacet(null);
	}

	@Test
	public void buildMarketplaceLaneFacet_withNetworkIds_success() {
		request.setNetworkIds(Lists.newArrayList(1000L, 2000L));
		String result = mockQuery.buildMarketplaceLaneFacet(1L);

		Assert.assertTrue(result.contains("sharedGroupIds:2000_*"));
		Assert.assertTrue(result.contains("sharedGroupIds:1000_*"));
		Assert.assertTrue(result.contains("lane4Active:true OR"));
		Assert.assertTrue(result.contains("{!ex=tl key=marketplace}"));
		Assert.assertTrue(result.contains("AND -lane0CompanyIds:1 AND -lane1CompanyIds:1 AND -lane2CompanyIds:1 AND -lane3CompanyIds:1)"));
	}

	@Test
	public void buildMarketplaceLaneFacet_withNetworkIdsAndDisableMarketplace_success() {
		request.setNetworkIds(Lists.newArrayList(1000L, 2000L));
		request.setDisableMarketplace(true);
		String result = mockQuery.buildMarketplaceLaneFacet(1L);

		Assert.assertTrue(result.contains("sharedGroupIds:2000_*"));
		Assert.assertTrue(result.contains("sharedGroupIds:1000_*"));
		Assert.assertFalse(result.contains("lane4Active:true OR"));
		Assert.assertTrue(result.contains("{!ex=tl key=marketplace}"));
		Assert.assertTrue(result.contains("AND -lane0CompanyIds:1 AND -lane1CompanyIds:1 AND -lane2CompanyIds:1 AND -lane3CompanyIds:1)"));
	}

	@Test
	public void buildMarketplaceLaneFacet_withDisableMarketplace_success() {
		request.setDisableMarketplace(true);
		String result = mockQuery.buildMarketplaceLaneFacet(1L);

		Assert.assertFalse(result.contains("sharedGroupIds"));
		Assert.assertFalse(result.contains("lane4Active:true OR"));
		Assert.assertTrue(result.contains("{!ex=tl key=marketplace}"));
		Assert.assertTrue(result.contains("-lane0CompanyIds:1 AND -lane1CompanyIds:1 AND -lane2CompanyIds:1 AND -lane3CompanyIds:1)"));
	}

	@Test
	public void buildMarketplaceLaneFacet_success() {
		String result = mockQuery.buildMarketplaceLaneFacet(1L);
		Assert.assertEquals(String.format(
				"{!ex=tl key=marketplace}(lane4Active:true AND -lane0CompanyIds:%1$d AND -lane1CompanyIds:%1$d AND -lane2CompanyIds:%1$d AND -lane3CompanyIds:%1$d)", 1L), result) ;

	}

	@Test
	public void addUserTypeFilterQuery() {
		PeopleSearchQuery query = new PeopleSearchQuery(data);
		Set<SolrUserType> userTypeFilter = Sets.newHashSet(SolrUserType.WORKER);
		request.setUserTypeFilter(userTypeFilter);
		query = query.addUserTypeFilterQuery();
		assertFoundFilterQuery(query, "userType");
	}
}
