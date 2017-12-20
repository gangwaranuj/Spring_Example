package com.workmarket.service.search.user;

import com.google.common.collect.Lists;
import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleFacetResultType;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.SolrDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * User: alexsilva Date: 12/10/13 Time: 2:26 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class PeopleSearchResultParserTest {

	private static final int NUMBER_OF_ASSESSMENT_STATUSES = 4;
	private static final int NUMBER_OF_GROUP_MEMBER_STATUSES = 6;
	private static final Integer ID1 = 1111111;
	private static final Integer ID2 = 2222222;
	private static final List<Integer> IDS_SIZE_1 = Arrays.asList(ID1);
	private static final List<Integer> IDS_SIZE_2 = Arrays.asList(ID1, ID2);
	private static final Collection<Object> INSURANCE_IDS_SIZE_1 = new HashSet<Object>(IDS_SIZE_1);
	private static final Collection<Object> INSURANCE_IDS_SIZE_2 = new HashSet<Object>(IDS_SIZE_2);

	@Mock Map<String, Integer> facetQuery;
	@Mock PeopleSearchResponse peopleSearchResponse;
	@Mock PeopleSearchTransientData transientData;
	@Mock PeopleSearchRequest originalRequest;

	@InjectMocks PeopleSearchResultParser peopleSearchResultParser = spy(new PeopleSearchResultParser());

	SolrDocument solrDocument;
	PeopleSearchResult peopleSearchResult;

	@Before
	public void setup() {
		solrDocument = mock(SolrDocument.class);
		when(solrDocument.containsKey(UserSearchableFields.INSURANCE_IDS.getName())).thenReturn(true);
		when(solrDocument.containsKey(UserSearchableFields.INSURANCE_NAMES.getName())).thenReturn(false);
		when(solrDocument.getFieldValues(UserSearchableFields.INSURANCE_IDS.getName())).thenReturn(null);

		peopleSearchResult = spy(new PeopleSearchResult());

		when(transientData.getOriginalRequest()).thenReturn(originalRequest);
	}

	@Test
	public void peopleSearchResultParser_insuranceIdCollectionNull_parseResultNull() {
		peopleSearchResultParser.parseInsurances(solrDocument, peopleSearchResult);
		assertNull(peopleSearchResult.getInsuranceIds());
	}

	@Test
	public void peopleSearchResultParser_insuranceIdCollectionEmpty_parseResultNull() {
		when(solrDocument.getFieldValues(UserSearchableFields.INSURANCE_IDS.getName())).thenReturn(Lists.newArrayList());
		peopleSearchResultParser.parseInsurances(solrDocument, peopleSearchResult);
		assertNull(peopleSearchResult.getInsuranceIds());
	}

	@Test
	public void peopleSearchResultParser_insuranceIdCollectionSingleElement_parseOneResult() {
		when(solrDocument.getFieldValues(UserSearchableFields.INSURANCE_IDS.getName())).thenReturn(INSURANCE_IDS_SIZE_1);
		peopleSearchResultParser.parseInsurances(solrDocument, peopleSearchResult);
		assertEquals((long)peopleSearchResult.getInsuranceIds().get(0), ID1.longValue());
	}

	@Test
	public void peopleSearchResultParser_insuranceIdCollectionMultiElement_parseTwoResults() {
		when(solrDocument.getFieldValues(UserSearchableFields.INSURANCE_IDS.getName())).thenReturn(INSURANCE_IDS_SIZE_2);
		peopleSearchResultParser.parseInsurances(solrDocument, peopleSearchResult);
		assertEquals(peopleSearchResult.getInsuranceIds().size(), 2);
		assertEquals((long)peopleSearchResult.getInsuranceIds().get(0), ID1.longValue());
		assertEquals((long)peopleSearchResult.getInsuranceIds().get(1), ID2.longValue());
	}

	@Test
	public void whenSearchTypeAssessmentInvite_parseAssessmentStatusFacets() {
		when(transientData.isAssessmentInviteSearch()).thenReturn(true);
		peopleSearchResultParser.parseAssessmentStatusFacets(facetQuery, peopleSearchResponse, transientData);
		verify(peopleSearchResultParser, times(NUMBER_OF_ASSESSMENT_STATUSES)).parseStatusFacet(
				anyMap(), any(PeopleSearchResponse.class), anyLong(),
				anyBoolean(), anyString(), any(PeopleFacetResultType.class)
		);
	}

	@Test
	public void whenSearchTypeNotAssessmentInvite_dontParseAssessmentStatusFacets() {
		when(transientData.isAssessmentInviteSearch()).thenReturn(false);
		peopleSearchResultParser.parseAssessmentStatusFacets(facetQuery, peopleSearchResponse, transientData);
		verify(peopleSearchResultParser, never()).parseStatusFacet(
				anyMap(), any(PeopleSearchResponse.class), anyLong(),
				anyBoolean(), anyString(), any(PeopleFacetResultType.class)
		);
	}

	@Test
	public void whenSearchTypeGroupMember_parseGroupStatusFacets() {
		when(transientData.isGroupMemberSearch()).thenReturn(true);
		peopleSearchResultParser.parseGroupStatusFacets(facetQuery, peopleSearchResponse, transientData);
		verify(peopleSearchResultParser, times(NUMBER_OF_GROUP_MEMBER_STATUSES)).parseStatusFacet(
				anyMap(), any(PeopleSearchResponse.class), anyLong(),
				anyBoolean(), anyString(), any(PeopleFacetResultType.class)
		);
	}

	@Test
	public void whenSearchTypeIsNotGroupMember_dontParseGroupStatusFacets() {
		when(transientData.isGroupMemberSearch()).thenReturn(false);
		peopleSearchResultParser.parseGroupStatusFacets(facetQuery, peopleSearchResponse, transientData);
		verify(peopleSearchResultParser, never()).parseStatusFacet(
				anyMap(), any(PeopleSearchResponse.class), anyLong(),
				anyBoolean(), anyString(), any(PeopleFacetResultType.class)
		);
	}

	@Test
	public void parseSharedGroupFacets_withoutSharedGroupFacets_success() {
		peopleSearchResultParser.parseSharedGroupFacets(Lists.<FacetField>newArrayList(), peopleSearchResponse, transientData);
		assertNull(peopleSearchResponse.getFacets().get(PeopleFacetResultType.SHARED_GROUP));
	}

	@Test
	public void parseCertifications_ArrayIndexOutOfBoundsExceptionSafe() {
		SolrDocument solrDocument = mock(SolrDocument.class);
		when(solrDocument.containsKey(UserSearchableFields.CERTIFICATION_IDS.getName())).thenReturn(true);

		Collection<Object> certificationNames = new ArrayList();
		certificationNames.add("Networking");
		certificationNames.add("Websphere");
		when(solrDocument.getFieldValues(UserSearchableFields.CERTIFICATION_NAMES.getName())).thenReturn(certificationNames);

		Collection<Object> certificationVendors = new ArrayList();
		certificationVendors.add("Cisco");
		when(solrDocument.getFieldValues(UserSearchableFields.CERTIFICATION_VENDORS.getName())).thenReturn(certificationVendors);

		PeopleSearchResult peopleSearchResult = mock(PeopleSearchResult.class);
		peopleSearchResultParser.parseCertifications(solrDocument, peopleSearchResult);
	}
}
