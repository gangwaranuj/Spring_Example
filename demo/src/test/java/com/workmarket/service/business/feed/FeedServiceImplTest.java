package com.workmarket.service.business.feed;

import com.google.common.collect.ImmutableList;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisCacheFilters;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.web.forms.feed.FeedRequestParams;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedServiceImplTest {
	@Mock HttpSolrServer readOnlyWorkSolrServer;
	@Mock InvariantDataService invariantDataService;
	@Mock RedisAdapter redisAdapter;
	@Mock FeatureEntitlementService featureEntitlementService;
	@InjectMocks FeedServiceImpl feedService;

	SolrQuery query;
	FeedRequestParams params;
	SolrDocumentList solrDocuments;
	List<FeedItem> results;
	QueryResponse queryResponse;

	@Before
	public void setup() throws Exception {
		query = mock(SolrQuery.class);
			when(query.setRequestHandler(any(String.class))).thenReturn(query);
			when(query.setStart(any(Integer.class))).thenReturn(query);
			when(query.setRows(any(Integer.class))).thenReturn(query);
			when(query.addSort(any(String.class), any(SolrQuery.ORDER.class))).thenReturn(query);
			when(query.setQuery(any(String.class))).thenReturn(query);
			when(query.addFilterQuery(any(String.class))).thenReturn(query);
			when(query.addField(any(String.class))).thenReturn(query);
			when(query.setFacet(true)).thenReturn(query);

		params = mock(FeedRequestParams.class);
			when(params.getLimit()).thenReturn(-9999);
			when(params.getCompanyId()).thenReturn("SOME_COMPANY_ID");
			when(params.getPostalCode()).thenReturn("SOME_POSTAL_CODE");
			when(params.getState()).thenReturn("NY");
			when(params.getDistanceInMiles()).thenReturn("1337");
			when(params.getIndustryId()).thenReturn("SOME_INDUSTRY_ID");
			when(params.getKeyword()).thenReturn("SOME_KEYWORD");
			when(params.getWhen()).thenReturn("today");
			when(params.getStart()).thenReturn(1);
			when(params.getLatitude()).thenReturn("90");
			when(params.getLongitude()).thenReturn("90");
			when(params.getVirtual()).thenReturn(false);

		solrDocuments = mock(SolrDocumentList.class);
			when(solrDocuments.getNumFound()).thenReturn(10L);

		results = mock(ArrayList.class);
		queryResponse = mock(QueryResponse.class);
			when(queryResponse.getResults()).thenReturn(solrDocuments);
			when(queryResponse.getBeans(any(Class.class))).thenReturn(results);

		PostalCode postalCode = mock(PostalCode.class);
			when(postalCode.getLatitude()).thenReturn(45.00001);
			when(postalCode.getLongitude()).thenReturn(90.00001);

		when(invariantDataService.getPostalCodeByCode(any(String.class))).thenReturn(postalCode);
		when(readOnlyWorkSolrServer.query(query)).thenReturn(queryResponse);
		when(featureEntitlementService.hasPercentRolloutFeatureToggle(anyString())).thenReturn(false);
	}

	@Test
	public void firstRow() {
		assertEquals(FindWorkFeedAdapter.FIRST_ROW, 0);
	}

	@Test
	public void defaultLimit() {
		assertEquals(FindWorkFeedAdapter.DEFAULT_LIMIT,10);
	}

	@Test
	public void defaultDistance() {
		assertEquals(FeedServiceImpl.DEFAULT_DISTANCE, "300.00");
	}

	@Test
	public void keywordQuery() {
		assertEquals(FeedServiceImpl.KEYWORD_QUERY, "publicTitle:%s companyName:%s description:%s");
	}

	@Test
	public void pricingTypeQuery() {
		assertEquals(FeedServiceImpl.PRICING_TYPE_QUERY, "-pricingType:INTERNAL");
	}

	@Test
	public void showInFeedQuery() {
		assertEquals(FeedServiceImpl.SHOW_IN_FEED_QUERY, "showInFeed:true");
	}

	@Test
	public void firstToAcceptQuery() {
		assertEquals(FeedServiceImpl.FIRST_TO_ACCEPT_QUERY, "assignToFirstResource:false");
	}

	@Test
	public void workStatusTypeCodeQuery() {
		assertEquals(FeedServiceImpl.WORK_STATUS_TYPE_CODE_QUERY, "workStatusTypeCode:sent");
	}

	@Test
	public void companyIdQuery() {
		assertEquals(FeedServiceImpl.COMPANY_ID_QUERY, "companyId:(%s)");
	}

	@Test
	public void geoQuery() {
		assertEquals(FeedServiceImpl.GEO_QUERY, "{!geofilt pt=%s sfield=location d=%s}");
	}

	@Test
	public void stateQuery() {
		assertEquals(FeedServiceImpl.STATE_QUERY, "state:(%s)");
	}

	@Test
	public void industryIDQuery() {
		assertEquals(FeedServiceImpl.INDUSTRY_ID_QUERY, "industryId:(%s)");
	}

	@Test
	public void whenQuery() {
		assertEquals(FeedServiceImpl.WHEN_QUERY, "scheduleFromDate:[%s TO %s]");
	}

	@Test
	public void milesToKilometersConversionFactor() {
		assertEquals(FeedServiceImpl.MILES_TO_KILOMETERS_CONVERSION_FACTOR, 1.609344, 0.0000001);
	}

	@Test
	public void oldestAssignmentAgeInDays() {
		assertEquals(FeedServiceImpl.OLDEST_ASSIGNMENT_AGE_IN_DAYS, 10);
	}

	@Test
	public void wildcard() {
		assertEquals(FeedServiceImpl.WILDCARD, "*");
	}

	@Test
	public void getResults_SetsStartToLimitTimesStart() throws SolrServerException {
		when(params.getStart()).thenReturn(1);
		when(params.getLimit()).thenReturn(1);

		feedService.getFeed(params, query);
			verify(query).setStart(params.getStart()*params.getLimit());
	}

	@Test
	public void getResults_WithNoLimit_SetsRowsToDefault() throws SolrServerException {
		when(params.getLimit()).thenReturn(null);

		feedService.getFeed(params, query);
			verify(query).setRows(FindWorkFeedAdapter.DEFAULT_LIMIT);
	}

	@Test
	public void getResults_WithAGivenLimit_SetsRowsToGivenLimit() throws SolrServerException {
		when(params.getLimit()).thenReturn(22);

		feedService.getFeed(params, query);
			verify(query).setRows(22);
	}

	@Test
	public void getResults_SetsSortFields() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(query).addSort(WorkSearchableFields.CREATED_DATE.getName(), SolrQuery.ORDER.desc);
			verify(query).addSort(WorkSearchableFields.SCHEDULE_FROM_DATE.getName(), SolrQuery.ORDER.asc);
	}

	@Test
	public void getResults_AddsTitleField() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(query).addField(WorkSearchableFields.PUBLIC_TITLE.getName());
	}

	@Test
	public void getResults_AddsDescriptionField() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(query).addField(WorkSearchableFields.DESCRIPTION.getName());
	}

	@Test
	public void getResults_AddsCityField() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(query).addField(WorkSearchableFields.CITY.getName());
	}

	@Test
	public void getResults_AddsStateField() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(query).addField(WorkSearchableFields.STATE.getName());
	}

	@Test
	public void getResults_AddsScheduleFromDateField() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(query).addField(WorkSearchableFields.SCHEDULE_FROM_DATE.getName());
	}

	@Test
	public void getResults_AddsWorkNumberField() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(query).addField(WorkSearchableFields.WORK_NUMBER.getName());
	}

	@Test
	public void getResults_AddsCreatedDateField() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(query).addField(WorkSearchableFields.CREATED_DATE.getName());
	}

	@Test
	public void getResults_AddsSpendLimitField() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(query).addField(WorkSearchableFields.SPEND_LIMIT.getName());
	}

	@Test
	public void getResults_WithNoCompanyId_AddsNullFilterQuery() throws SolrServerException {
		when(params.getCompanyId()).thenReturn(null);
		feedService.getFeed(params, query);
			verify(query, times(5)).addFilterQuery((String) null);
	}

	@Test
	public void getResults_WithCompanyId_AddsCompanyIdFilterQuery() throws SolrServerException {
		when(params.getCompanyId()).thenReturn("1");
		feedService.getFeed(params, query);
			verify(query).addFilterQuery(String.format(FeedServiceImpl.COMPANY_ID_QUERY, "1"));
	}

	@Test
	public void getResults_WithNullZipCode_AddsNullQuery() throws SolrServerException {
		when(params.getPostalCode()).thenReturn(null);
		when(params.getLongitude()).thenReturn(null);
		when(params.getLatitude()).thenReturn(null);
		when(params.getState()).thenReturn(null);
		feedService.getFeed(params, query);
			verify(query, times(5)).addFilterQuery((String) null);
	}

	@Test
	public void getResults_WithNullZipCode_NeverGetsThePostalCode() throws SolrServerException {
		when(params.getPostalCode()).thenReturn(null);
		when(params.getLongitude()).thenReturn(null);
		when(params.getLatitude()).thenReturn(null);
		when(params.getState()).thenReturn(null);
		feedService.getFeed(params, query);
			verify(invariantDataService, never()).getPostalCodeByCode(null);
	}

	@Test
	public void getResults_WithEmptyZipCode_AddsNullQuery() throws SolrServerException {
		when(params.getPostalCode()).thenReturn("");
		when(params.getLongitude()).thenReturn(null);
		when(params.getLatitude()).thenReturn(null);
		when(params.getState()).thenReturn(null);
		feedService.getFeed(params, query);
			verify(query, times(5)).addFilterQuery((String) null);
	}

	@Test
	public void getResults_WithEmptyZipCode_NeverGetsThePostalCode() throws SolrServerException {
		when(params.getPostalCode()).thenReturn("");
		when(params.getLongitude()).thenReturn(null);
		when(params.getLatitude()).thenReturn(null);
		when(params.getState()).thenReturn(null);
		feedService.getFeed(params, query);
			verify(invariantDataService, never()).getPostalCodeByCode("");
	}

	@Test
	public void getResults_WithState_AddsNullQuery() throws SolrServerException {
		when(params.getState()).thenReturn("NY");
		feedService.getFeed(params, query);
			verify(query, times(4)).addFilterQuery((String) null);
	}

	@Test
	public void getResults_WithState_NeverGetsThePostalCode() throws SolrServerException {
		when(params.getState()).thenReturn("NY");
		feedService.getFeed(params, query);
			verify(invariantDataService, never()).getPostalCodeByCode(null);
	}

	@Test
	public void getResults_WithZipCodeButNoDistance_AddsGeoFilterQueryWithDefaultDistance() throws SolrServerException {
		when(params.getPostalCode()).thenReturn("11787");
		when(params.getDistanceInMiles()).thenReturn(null);
		when(params.getLongitude()).thenReturn(null);
		when(params.getLatitude()).thenReturn(null);
		when(params.getState()).thenReturn(null);

		String distanceInKilometers = String.valueOf(Double.valueOf(FeedServiceImpl.DEFAULT_DISTANCE) * FeedServiceImpl.MILES_TO_KILOMETERS_CONVERSION_FACTOR);
		feedService.getFeed(params, query);
			verify(query).addFilterQuery(String.format(FeedServiceImpl.GEO_QUERY, "45.00001,90.00001", distanceInKilometers));
	}

	@Test
	public void getResults_WithZipCodeAndDistanceLessThanDefault_AddsGeoFilterQueryWithGivenDistance() throws SolrServerException {
		when(params.getPostalCode()).thenReturn("11787");
		when(params.getLongitude()).thenReturn(null);
		when(params.getLatitude()).thenReturn(null);
		when(params.getState()).thenReturn(null);

		String someDistance = "299.99";
		when(params.getDistanceInMiles()).thenReturn(someDistance);

		String distanceInKilometers = String.valueOf(Double.valueOf(someDistance) * FeedServiceImpl.MILES_TO_KILOMETERS_CONVERSION_FACTOR);
		feedService.getFeed(params, query);
			verify(query).addFilterQuery(String.format(FeedServiceImpl.GEO_QUERY, "45.00001,90.00001", distanceInKilometers));
	}

	@Test
	public void getResults_WithZipCodeAndDistanceSameAsDefault_AddsGeoFilterQueryWithGivenDistance() throws SolrServerException {
		when(params.getPostalCode()).thenReturn("11787");
		when(params.getLongitude()).thenReturn(null);
		when(params.getLatitude()).thenReturn(null);
		when(params.getState()).thenReturn(null);

		String someDistance = "300.00";
		when(params.getDistanceInMiles()).thenReturn(someDistance);

		String distanceInKilometers = String.valueOf(Double.valueOf(someDistance) * FeedServiceImpl.MILES_TO_KILOMETERS_CONVERSION_FACTOR);
		feedService.getFeed(params, query);
		verify(query).addFilterQuery(String.format(FeedServiceImpl.GEO_QUERY, "45.00001,90.00001", distanceInKilometers));
	}

	@Test
	public void getResults_WithZipCodeAndDistanceGreaterThanDefault_AddsGeoFilterQueryWithDefaultDistance() throws SolrServerException {
		when(params.getPostalCode()).thenReturn("11787");
		when(params.getLongitude()).thenReturn(null);
		when(params.getLatitude()).thenReturn(null);
		when(params.getState()).thenReturn(null);

		String someDistance = "300.01";
		when(params.getDistanceInMiles()).thenReturn(someDistance);

		String distanceInKilometers = String.valueOf(Double.valueOf(FeedServiceImpl.DEFAULT_DISTANCE) * FeedServiceImpl.MILES_TO_KILOMETERS_CONVERSION_FACTOR);
		feedService.getFeed(params, query);
		verify(query).addFilterQuery(String.format(FeedServiceImpl.GEO_QUERY, "45.00001,90.00001", distanceInKilometers));
	}

	@Test
	public void getResults_WithZipCodeAndWonkyDistance_AddsGeoFilterQueryWithDefaultDistance() throws SolrServerException {
		when(params.getPostalCode()).thenReturn("11787");
		when(params.getLongitude()).thenReturn(null);
		when(params.getLatitude()).thenReturn(null);
		when(params.getState()).thenReturn(null);

		String someDistance = "some wonky distance";
		when(params.getDistanceInMiles()).thenReturn(someDistance);

		String distanceInKilometers = String.valueOf(Double.valueOf(FeedServiceImpl.DEFAULT_DISTANCE) * FeedServiceImpl.MILES_TO_KILOMETERS_CONVERSION_FACTOR);
		feedService.getFeed(params, query);
		verify(query).addFilterQuery(String.format(FeedServiceImpl.GEO_QUERY, "45.00001,90.00001", distanceInKilometers));
	}

	@Test
	public void getResults_WithInvalidZipCode_AddsNullQuery() throws SolrServerException {
		when(params.getLongitude()).thenReturn(null);
		when(params.getLatitude()).thenReturn(null);
		when(params.getState()).thenReturn(null);
		when(params.getPostalCode()).thenReturn("invalid");
		when(params.getDistanceInMiles()).thenReturn("1337");
		when(invariantDataService.getPostalCodeByCode("invalid")).thenReturn(null);

		feedService.getFeed(params, query);
			verify(query, times(5)).addFilterQuery((String) null);
	}

	@Test
	public void getResults_WithVirtual_AddsVirtualFilterQuery() throws SolrServerException {
		when(params.getVirtual()).thenReturn(true);
		feedService.getFeed(params, query);
			verify(query).addFilterQuery(String.format(FeedServiceImpl.VIRTUAL_QUERY, "true"));
	}

	@Test
	public void getResults_WithVirtual_AddsNullFilterQuery() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(query, times(4)).addFilterQuery((String) null);
	}

	@Test
	public void getResults_WithState_AddsStateFilterQuery() throws SolrServerException {
		when(params.getState()).thenReturn("NY");
		feedService.getFeed(params, query);
			verify(query).addFilterQuery(String.format(FeedServiceImpl.STATE_QUERY, "NY"));
	}

	@Test
	public void getResults_WithNoState_AddsNullFilterQuery() throws SolrServerException {
		when(params.getState()).thenReturn(null);
		feedService.getFeed(params, query);
			verify(query, times(4)).addFilterQuery((String) null);
	}

	@Test
	public void getResults_WithNoIndustryId_AddsNullFilterQuery() throws SolrServerException {
		when(params.getIndustryId()).thenReturn(null);
		feedService.getFeed(params, query);
			verify(query, times(5)).addFilterQuery((String) null);
	}

	@Test
	public void getResults_WithIndustryId_AddsIndustryIdFilterQuery() throws SolrServerException {
		when(params.getIndustryId()).thenReturn("1002");
		feedService.getFeed(params, query);
			verify(query).addFilterQuery(String.format(FeedServiceImpl.INDUSTRY_ID_QUERY, "1002"));
	}

	@Test
	public void getResults_WithNoKeyword_AddsNullFilterQuery() throws SolrServerException {
		when(params.getKeyword()).thenReturn(null);
		feedService.getFeed(params, query);
			verify(query, times(5)).addFilterQuery((String) null);
	}

	@Test
	public void getResults_WithKeyword_AddsKeywordFilterQuery() throws SolrServerException {
		when(params.getKeyword()).thenReturn("blarg");
		feedService.getFeed(params, query);
			verify(query).addFilterQuery(String.format(FeedServiceImpl.KEYWORD_QUERY, "blarg", "blarg", "blarg"));
	}

	@Test
	public void getResults_WithNoWhen_AddsNullFilterQuery() throws SolrServerException {
		when(params.getWhen()).thenReturn(null);
		feedService.getFeed(params, query);
			verify(query, times(5)).addFilterQuery(new String[] {null});
	}

	@Test
	public void getResults_WithWhen_AddsWhenQuery() throws SolrServerException {
		LocalDate localDateStart = LocalDate.now();
		DateTime start = localDateStart.toDateTimeAtStartOfDay(DateTimeZone.UTC);

		LocalDate localDateEnd = LocalDate.now().plusDays(1);
		DateTime end = localDateEnd.toDateTimeAtStartOfDay(DateTimeZone.UTC);

		when(params.getWhen()).thenReturn("today");
		feedService.getFeed(params, query);
			verify(query).addFilterQuery(String.format(FeedServiceImpl.WHEN_QUERY, start, end));
	}

	@Test
	public void getResults_WithBadWhen_AddsOpenEndedWhenQuery() throws SolrServerException {
		LocalDate localDateEnd = LocalDate.now().minusDays(FeedServiceImpl.OLDEST_ASSIGNMENT_AGE_IN_DAYS);
		DateTime start = localDateEnd.toDateTimeAtStartOfDay(DateTimeZone.UTC);

		when(params.getWhen()).thenReturn("blargedy blarg blarg");
		feedService.getFeed(params, query);
			verify(query).addFilterQuery(String.format(FeedServiceImpl.WHEN_QUERY, start, FeedServiceImpl.WILDCARD));
	}

	@Test
	public void getResults_QueriesTheSolrServer() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(readOnlyWorkSolrServer).query(query);
	}

	@Test
	public void getResults_DoesNotRetry() throws SolrServerException {
		feedService.getFeed(params, query);
			verify(readOnlyWorkSolrServer, times(1)).query(query);
	}

	@Test
	public void getResults_DoesRetry() throws SolrServerException {
		when(params.getMinResults()).thenReturn(3);
		when(params.getRetryLimit()).thenReturn(1);
		when(params.getRetryDistanceIncrement()).thenReturn(100);
		feedService.getFeed(params, query);
			verify(readOnlyWorkSolrServer, times(2)).query(query);
	}

	@Test
	public void getResults_WhenNoResults_ReturnsEmptyList() throws SolrServerException {
		when(readOnlyWorkSolrServer.query(query)).thenReturn(null);
		assertTrue(feedService.getFeed(params, query).getResults().isEmpty());
	}

	@Test
	public void getResults_WhenResults_ReturnsFeed() throws SolrServerException {
		Feed actualResults = feedService.getFeed(params, query);
			verify(queryResponse).getBeans(FeedItem.class);

		assertEquals(actualResults.getResults(), results);
	}

	@Test
	public void pushToRedis_success() {
		feedService.pushFeedToRedis();

		verify(redisAdapter, times(1)).set(eq(RedisCacheFilters.INDEED_XML_KEY), anyString(), eq(Constants.DAY_IN_SECONDS));
	}

	@Test
	public void setSort_success() throws SolrServerException {
		when(params.getSort()).thenReturn(ImmutableList.of("-location", "workPrice", "-country"));
		feedService.getFeed(params, query);
		verify(query).addSort("geodist(location,90,90)", SolrQuery.ORDER.desc);
		verify(query).addSort("workPrice", SolrQuery.ORDER.asc);
		verify(query).addSort("country", SolrQuery.ORDER.desc);
	}

	@Test
	public void setSort_defaultToSortByDistance() throws SolrServerException {
		when(params.getSort()).thenReturn(ImmutableList.<String>of());
		feedService.getFeed(params, query);
		verify(query).addSort("geodist(location,90,90)", SolrQuery.ORDER.asc);
	}

	@Test
	public void setSort_doNotSortByDistanceIfSortDefined() throws SolrServerException {
		when(params.getSort()).thenReturn(ImmutableList.of("workPrice", "-country"));
		feedService.getFeed(params, query);
		verify(query, never()).addSort("geodist(location,90,90)", SolrQuery.ORDER.desc);
		verify(query).addSort("workPrice", SolrQuery.ORDER.asc);
		verify(query).addSort("country", SolrQuery.ORDER.desc);
	}

	@Test
	public void setFilter_success() throws SolrServerException {
		when(params.getFilter()).thenReturn(ImmutableList.of("workPrice=4", "country=USA"));
		feedService.getFeed(params, query);
		verify(query).addFilterQuery("workPrice:4");
		verify(query).addFilterQuery("country:USA");
	}

	@Test
	public void multiplePricingTypeFilters() throws Exception {
		when(params.getFilter()).thenReturn(ImmutableList.of("pricingType=PER_HOUR|BLENDED_PER_HOUR", "country=USA"));
		feedService.getFeed(params, query);
		verify(query).addFilterQuery("pricingType:(PER_HOUR OR BLENDED_PER_HOUR)");
		verify(query).addFilterQuery("country:USA");
	}

	@Test
	public void getResults_WithCompanyBlockingWorker() throws SolrServerException {
		Long blockingCompany = 1L;
		when(params.getExcludeCompanyIds()).thenReturn(ImmutableList.<Long>of(blockingCompany));
		feedService.getFeed(params, query);
		verify(query).addFilterQuery(eq(String.format("-companyId:(%d)", blockingCompany)));
	}

	@Test
	public void getResults_WithCompaniesBlockingWorker() throws SolrServerException {
		Long[] blockingCompany = {1L, 2L};
		when(params.getExcludeCompanyIds()).thenReturn(ImmutableList.<Long>of(blockingCompany[0], blockingCompany[1]));
		feedService.getFeed(params, query);
		verify(query).addFilterQuery(eq(String.format("-companyId:(%d OR %d)", blockingCompany[0], blockingCompany[1])));
	}
}
