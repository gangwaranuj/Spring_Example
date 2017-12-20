package com.workmarket.web.controllers;

import com.google.common.collect.Sets;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.service.search.SearchService;
import com.workmarket.web.forms.search.UserSearchForm;
import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import static org.mockito.Matchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {

	@Mock SearchService peopleSearchService;
	@Mock SecurityContextFacade securityContextFacade;
	@Mock SecurityContext securityContext;
	@Mock ExtendedUserDetails currentUser;
	@Mock FeatureEvaluator featureEvaluator;
	@InjectMocks SearchController searchController;

	private static final Integer INSURANCE_FIGURE = 10000;
	private static final Long anyId = 12L;
	private PeopleSearchRequest request;

	@Before
	public void setUp() {
		request = new PeopleSearchRequest();
		when(securityContextFacade.getSecurityContext()).thenReturn(securityContext);
		when(securityContextFacade.getCurrentUser()).thenReturn(currentUser);
	}

	@Test
	public void testAugmentRequestAndExecuteSearch_groupMemberSearch() throws Exception {
		String searchType = SearchType.PEOPLE_SEARCH_GROUP_MEMBER.toString();
		UserSearchForm form = buildSearchForm(searchType);
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		searchController.augmentRequestAndExecuteSearch(searchType, form, request, httpServletRequest);
		assertEquals(Integer.valueOf((int)request.getWorkersCompCoverageFilter().getFrom()), INSURANCE_FIGURE);
		assertEquals(Integer.valueOf((int)request.getGeneralLiabilityCoverageFilter().getFrom()), INSURANCE_FIGURE);
		assertEquals(Integer.valueOf((int)request.getErrorsAndOmissionsCoverageFilter().getFrom()), INSURANCE_FIGURE);
		assertEquals(Integer.valueOf((int)request.getAutomobileCoverageFilter().getFrom()), INSURANCE_FIGURE);
		assertEquals(Integer.valueOf((int)request.getContractorsCoverageFilter().getFrom()), INSURANCE_FIGURE);
		assertEquals(Integer.valueOf((int)request.getBusinessLiabilityCoverageFilter().getFrom()), INSURANCE_FIGURE);
		assertEquals(Integer.valueOf((int)request.getCommercialGeneralLiabilityCoverageFilter().getFrom()), INSURANCE_FIGURE);
		assertTrue(request.isNoFacetsFlag());
	}

	@Test
	public void testAugmentRequestAndExecuteSearch_assessmentInviteSearch() throws Exception {
		String searchType = SearchType.PEOPLE_SEARCH_ASSESSMENT_INVITE.toString();
		UserSearchForm form = buildSearchForm(searchType);

		searchController.augmentRequestAndExecuteSearch(searchType, form, request, any(HttpServletRequest.class));
		assertTrue(request.getInvitedAssessmentFilter().contains(anyId));
		assertTrue(request.getNotInvitedAssessmentFilter().contains(anyId));
		assertTrue(request.getPassedAssessmentFilter().contains(anyId));
		assertTrue(request.getFailedTestFilter().contains(anyId));
	}

	private UserSearchForm buildSearchForm(String searchType) {
		UserSearchForm form = new UserSearchForm();
		if (SearchType.PEOPLE_SEARCH_GROUP_MEMBER.toString().equals(searchType)) {

			form.setGroup_id(anyId);

			form.setMember(false);
			form.setMemberoverride(false);
			form.setPending(false);
			form.setPendingoverride(false);
			form.setDeclined(false);
			form.setInvited(true);

			form.setWorkersCompCoverage(INSURANCE_FIGURE);
			form.setWorkersCompToggle(true);
			form.setGeneralLiabilityCoverage(INSURANCE_FIGURE);
			form.setGeneralLiabilityToggle(true);
			form.setErrorsAndOmissionsCoverage(INSURANCE_FIGURE);
			form.setErrorsAndOmissionsToggle(true);
			form.setAutomobileCoverage(INSURANCE_FIGURE);
			form.setAutomobileToggle(true);
			form.setContractorsCoverage(INSURANCE_FIGURE);
			form.setContractorsToggle(true);
			form.setBusinessLiabilityCoverage(INSURANCE_FIGURE);
			form.setBusinessLiabilityToggle(true);
			form.setCommercialGeneralLiabilityCoverage(INSURANCE_FIGURE);
			form.setCommercialGeneralLiabilityToggle(true);

			form.setNoFacetsFlag(true);

		} else if (SearchType.PEOPLE_SEARCH_ASSESSMENT_INVITE.toString().equals(searchType)) {
			form.setAssessmentId(anyId);
			form.setInvitedassessment(Sets.newHashSet(anyId));
			form.setNotinvitedassessment(Sets.newHashSet(anyId));
			form.setPassedassessment(Sets.newHashSet(anyId));
			form.setFailedtest(Sets.newHashSet(anyId));
		}

		return form;
	}
}