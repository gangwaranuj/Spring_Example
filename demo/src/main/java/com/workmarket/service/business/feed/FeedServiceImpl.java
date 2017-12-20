package com.workmarket.service.business.feed;

import com.google.common.base.MoreObjects;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import com.workmarket.configuration.Constants;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisCacheFilters;
import com.workmarket.search.SearchClient;
import com.workmarket.search.gen.Common;
import com.workmarket.search.gen.FeedMessages.FindWorkFeedRequest;
import com.workmarket.search.gen.FeedMessages.FindWorkFeedResponse;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.dto.IndeedFeedDTO;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.feed.FeedRequestParams;
import com.workmarket.xml.CDATAAdapterCharacterEscape;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import static com.workmarket.service.business.feed.FindWorkFeedAdapter.DEFAULT_LIMIT;
import static com.workmarket.service.business.feed.FindWorkFeedAdapter.FEED_RETRY_INCREMENT;
import static com.workmarket.service.business.feed.FindWorkFeedAdapter.FEED_RETRY_LIMIT;
import static com.workmarket.service.business.feed.FindWorkFeedAdapter.FIRST_ROW;

@Service
public class FeedServiceImpl implements FeedService {
	public static final String DEFAULT_DISTANCE = "300.00";
	public static final String KEYWORD_QUERY =
		WorkSearchableFields.PUBLIC_TITLE.getName() + ":%s " +
			WorkSearchableFields.COMPANY_NAME.getName() + ":%s " +
			WorkSearchableFields.DESCRIPTION.getName()  + ":%s";
	public static final String PRICING_TYPE_QUERY =
		"-" + WorkSearchableFields.PRICING_TYPE.getName() + ":INTERNAL";
	public static final String VIRTUAL_QUERY =
			WorkSearchableFields.IS_OFFSITE.getName() + ":%s";
	public static final String SHOW_IN_FEED_QUERY =
		WorkSearchableFields.SHOW_IN_FEED.getName() + ":true";
	public static final String FIRST_TO_ACCEPT_QUERY =
		WorkSearchableFields.FIRST_TO_ACCEPT.getName() + ":false";
	public static final String WORK_STATUS_TYPE_CODE_QUERY =
		WorkSearchableFields.WORK_STATUS_TYPE_CODE.getName() + ":sent";
	public static final String COMPANY_ID_QUERY =
		WorkSearchableFields.COMPANY_ID.getName() + ":(%s)";
	public static final String GEO_QUERY = "{!geofilt pt=%s sfield=location d=%s}";
	public static final String SORT_BY_DISTANCE_QUERY = "geodist(location,%s,%s)";
	public static final String STATE_QUERY =
		WorkSearchableFields.STATE.getName() +":(%s)";
	public static final String INDUSTRY_ID_QUERY =
		WorkSearchableFields.INDUSTRY_ID.getName() + ":(%s)";
	public static final String WHEN_QUERY =
		WorkSearchableFields.SCHEDULE_FROM_DATE.getName() + ":[%s TO %s]";
	public static final Double MILES_TO_KILOMETERS_CONVERSION_FACTOR = 1.609344;
	public static final int OLDEST_ASSIGNMENT_AGE_IN_DAYS = 10;
	public static final String WILDCARD = "*";
	public static final int FEED_MIN_DISTANCE = 50; // miles

	@Qualifier("readOnlyWorkSolrServer")
	@Autowired private HttpSolrServer readOnlyWorkSolrServer;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private RedisAdapter redisAdapter;
	@Autowired private LaneService laneService;
	@Autowired private SearchClient searchClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private FeatureEntitlementService featureEntitlementService;
	@Autowired private FindWorkFeedAdapter findWorkFeedAdapter;

	private static final Log logger = LogFactory.getLog(FeedService.class);

	private class FeedQueryParams {
		private final Integer start;
		private final Integer limit;
		private final String companyId;
		private final String postalCode;
		private final String state;
		private final String distanceInMiles;
		private final String industryIds;
		private final String keyword;
		private final String when;
		private final String latitude;
		private final String longitude;
		private final boolean virtual;
		private final List<Long> exclusiveCompanyIds;
		private final List<Long> excludeCompanyIds;
		private final Long startDate;
		private final Long endDate;
		private final List<String> filter;

		FeedQueryParams(FeedRequestParams params) {
			this.start               = params.getStart();
			this.limit               = params.getLimit();
			this.companyId           = params.getCompanyId();
			this.postalCode          = params.getPostalCode();
			this.state               = params.getState();
			this.distanceInMiles     = params.getDistanceInMiles();
			this.keyword             = params.getKeyword();
			this.when                = params.getWhen();
			this.industryIds         = params.getIndustryId();
			this.longitude           = params.getLongitude();
			this.latitude            = params.getLatitude();
			this.virtual             = params.getVirtual();
			this.exclusiveCompanyIds = params.getExclusiveCompanyIds();
			this.excludeCompanyIds = params.getExcludeCompanyIds();
			this.startDate           = params.getStartDate();
			this.endDate             = params.getEndDate();
			this.filter              = params.getFilter();
		}

		public boolean isLatLongValid() {
			return latitude != null && !"-999".equals(latitude) && longitude != null && !"-999".equals(longitude);
		}

		public Integer getStart() {
			return start != null ? start : FIRST_ROW;
		}

		public Integer getLimit() {
			return limit != null ? limit : DEFAULT_LIMIT;
		}

		public String getCompanyIdFilterQuery() {
			return companyId == null ? null : String.format(COMPANY_ID_QUERY, companyId);
		}

		public String getExclusiveCompanyIdsFilterQuery() {
			if (exclusiveCompanyIds == null || exclusiveCompanyIds.isEmpty()) {
				return null;
			}

			StringBuilder sb = new StringBuilder("companyId:(");
			for (Long companyId : exclusiveCompanyIds) {
				sb.append(companyId + " OR ");
			}
			sb.replace(sb.length() - 4, sb.length(), ")");

			return sb.toString();
		}

		public String getExcludeCompanyIdsFilterQuery() {
			if (excludeCompanyIds == null || excludeCompanyIds.isEmpty()) {
				return null;
			}
			StringBuilder sb = new StringBuilder("-companyId:(");
			sb.append(StringUtils.join(excludeCompanyIds, " OR "));
			sb.append(")");
			return sb.toString();
		}

		public String getScheduleFromDateRangeFilterQuery() {
			if (startDate == null && endDate == null) {
				return null;
			}

			return String.format("scheduleFromDate:[%s TO %s]",
					startDate != null ? DateUtilities.formatISO8601Instant(startDate) : "*",
					endDate   != null ? DateUtilities.formatISO8601Instant(endDate)   : "*");
		}

		public String getGeoFilterQuery() {
			if (state != null) { return null; }

			if (isLatLongValid()) {
				return String.format(GEO_QUERY, latitude + "," + longitude, getDistanceInKilometers());
			}

			PostalCode pc = getPostalCode();
			if (postalCode != null && pc != null) {
				return String.format(GEO_QUERY, getPointFor(pc), getDistanceInKilometers());
			} else {
				return null;
			}
		}

		public String getSortByDistanceQuery() {
			if (isLatLongValid()) {
				return String.format(SORT_BY_DISTANCE_QUERY, latitude, longitude);
			}

			return null;
		}

		public String getVirtualFilterQuery() {
			return virtual ? String.format(VIRTUAL_QUERY, "true") : null;
		}

		public String getStateFilterQuery() {
			return state == null ? null : String.format(STATE_QUERY, state);
		}

		public String getIndustryIdFilterQuery() {
			return industryIds == null ? null : String.format(INDUSTRY_ID_QUERY,StringUtils.replace(industryIds, ",", " OR "));
		}

		private PostalCode getPostalCode() {
			if (StringUtils.isEmpty(postalCode)){return null;}
			return invariantDataService.getPostalCodeByCode(postalCode);
		}

		private String getPointFor(PostalCode postalCode) {
			return postalCode.getLatitude() + "," + postalCode.getLongitude();
		}

		private String getDistanceInMiles() {
			if (!NumberUtils.isNumber(distanceInMiles)) { return DEFAULT_DISTANCE; }
			return String.valueOf(Math.min(Double.valueOf(MoreObjects.firstNonNull(distanceInMiles, DEFAULT_DISTANCE)), Double.valueOf(DEFAULT_DISTANCE)));
		}

		private String getDistanceInKilometers() {
			return String.valueOf(Double.valueOf(getDistanceInMiles()) * MILES_TO_KILOMETERS_CONVERSION_FACTOR);
		}

		public String getKeywordQuery() {
			if (keyword == null) { return null; }
			String lowered = keyword.toLowerCase();
			return String.format(KEYWORD_QUERY, lowered, lowered, lowered);
		}

		public String getWorkQuery() {
			// IS shown in feed, NOT first to accept, IS sent, NOT internal
			return SHOW_IN_FEED_QUERY + " AND " + FIRST_TO_ACCEPT_QUERY + " AND " + WORK_STATUS_TYPE_CODE_QUERY + " AND " + PRICING_TYPE_QUERY;
		}

		public String getWhenFilterQuery() {
			if (when == null) { return null; }

			// TODO[Jim]: This would make a great service
			Parser natty = new Parser();
			List<DateGroup> dateGroup = natty.parse(when);
			if (dateGroup.size() > 0) {
				List<Date> dates = dateGroup.get(0).getDates();

				if (dates.size() > 0) {
					LocalDate localDateStart = LocalDate.fromDateFields(dates.get(0));
					DateTime start = localDateStart.toDateTimeAtStartOfDay(DateTimeZone.UTC);

					LocalDate localDateEnd = LocalDate.fromDateFields(dates.get(dates.size() - 1)).plusDays(1);
					DateTime end = localDateEnd.toDateTimeAtStartOfDay(DateTimeZone.UTC);

					return String.format(WHEN_QUERY, start, end);
				}
			}

			LocalDate localDate = LocalDate.now().minusDays(OLDEST_ASSIGNMENT_AGE_IN_DAYS);
			DateTime start = localDate.toDateTimeAtStartOfDay(DateTimeZone.UTC);

			return String.format(WHEN_QUERY, start, WILDCARD);
		}
	}


	@Override
	public Feed getFeed(FeedRequestParams params, SolrQuery query) throws SolrServerException {
		Feed result = getFeedWithFeatureToggle(params, query);
		if (params.getMinResults() != null && params.getMinResults() > 0) {
			// We want to set some hard limits internally so that people can't set externally and wreak havoc
			params.setRetryLimit(FEED_RETRY_LIMIT);
			params.setRetryDistanceIncrement(FEED_RETRY_INCREMENT);

			if (params.getDistanceInMiles() == null) {
				params.setDistanceInMiles(String.valueOf(FEED_MIN_DISTANCE));
			}

			for (int i = 0; i < params.getRetryLimit() && result.getResults().size() < params.getMinResults(); i++) {
				params.setDistanceInMiles(String.valueOf(Double.parseDouble(params.getDistanceInMiles()) + params.getRetryDistanceIncrement()));
				result = getFeedWithFeatureToggle(params, query);
			}
		}

		return result;
	}

	@Override
	public void pushFeedToRedis() {
		IndeedFeedDTO xml = getIndeedFeed();
		Marshaller m = createMarshaller();
		StringWriter writer = writeXML(xml, m);
		redisAdapter.set(RedisCacheFilters.INDEED_XML_KEY, writer.toString(), Constants.DAY_IN_SECONDS);
	}

	@Override
	public FindWorkFeedResponse findWorkFeed(final FindWorkFeedRequest request) {
		final FindWorkFeedResponse defaultResponse = FindWorkFeedResponse.newBuilder()
			.setStatus(Common.Status.newBuilder()
				.setSuccess(false)
				.addMessages("search client failed to get response from service")
				.build())
			.build();
		return searchClient.findWorkFeed(request, webRequestContextProvider.getRequestContext())
			.toBlocking()
			.singleOrDefault(defaultResponse);
	}

	private Feed requestFeed(FeedRequestParams params, SolrQuery query) throws SolrServerException {
		params.setP(StringUtilities.stripXSSAndEscapeHtml(params.getPostalCode()));
		params.setD(StringUtilities.stripXSSAndEscapeHtml(params.getDistanceInMiles()));
		params.setK(StringUtilities.stripXSSAndEscapeHtml(params.getKeyword()));
		FeedQueryParams queryParams = new FeedQueryParams(params);
		query
			.setRequestHandler("/workSearch")
			.setFacet(true)
			.setStart(queryParams.getStart())
			.setRows(queryParams.getLimit())
			.addField(WorkSearchableFields.PUBLIC_TITLE.getName())
			.addField(WorkSearchableFields.DESCRIPTION.getName())
			.addField(WorkSearchableFields.CITY.getName())
			.addField(WorkSearchableFields.STATE.getName())
			.addField(WorkSearchableFields.POSTAL_CODE.getName())
			.addField(WorkSearchableFields.COUNTRY.getName())
			.addField(WorkSearchableFields.SCHEDULE_FROM_DATE.getName())
			.addField(WorkSearchableFields.SCHEDULE_THROUGH_DATE.getName())
			.addField(WorkSearchableFields.ID.getName())
			.addField(WorkSearchableFields.WORK_NUMBER.getName())
			.addField(WorkSearchableFields.CREATED_DATE.getName())
			.addField(WorkSearchableFields.SPEND_LIMIT.getName())
			.addField(WorkSearchableFields.COMPANY_NAME.getName())
			.addField(WorkSearchableFields.LOCATION.getName())
			.addField(WorkSearchableFields.LONGITUDE.getName())
			.addField(WorkSearchableFields.LATITUDE.getName())
			.addField(WorkSearchableFields.IS_OFFSITE.getName())
			.addField(WorkSearchableFields.FIRST_TO_ACCEPT.getName())
			.addField(WorkSearchableFields.SHOW_IN_FEED.getName())
			.addField(WorkSearchableFields.PRICING_TYPE.getName())
			.addField(WorkSearchableFields.WORK_PRICE.getName())
			.setQuery(queryParams.getWorkQuery())
			.addFilterQuery(queryParams.getCompanyIdFilterQuery())
			.addFilterQuery(queryParams.getGeoFilterQuery())
			.addFilterQuery(queryParams.getStateFilterQuery())
			.addFilterQuery(queryParams.getIndustryIdFilterQuery())
			.addFilterQuery(queryParams.getWhenFilterQuery())
			.addFilterQuery(queryParams.getKeywordQuery())
			.addFilterQuery(queryParams.getVirtualFilterQuery())
			.addFilterQuery(queryParams.getExclusiveCompanyIdsFilterQuery())
			.addFilterQuery(queryParams.getExcludeCompanyIdsFilterQuery())
			.addFilterQuery(queryParams.getScheduleFromDateRangeFilterQuery());

		if (CollectionUtils.isNotEmpty(params.getFilter())) {
			addFilters(query, params.getFilter());
		}

		final boolean hasSort = CollectionUtils.isNotEmpty(params.getSort());

		if (!hasSort && queryParams.isLatLongValid()) { // default to sort-by-distance if no sort filter defined
			query.addSort(queryParams.getSortByDistanceQuery(), SolrQuery.ORDER.asc);
		}

		if (hasSort) {
			setSort(query, params.getSort(), queryParams);
		}

		// add default sorts (sorts are additive in solr)
		query
				.addSort(WorkSearchableFields.CREATED_DATE.getName(), SolrQuery.ORDER.desc)
				.addSort(WorkSearchableFields.SCHEDULE_FROM_DATE.getName(), SolrQuery.ORDER.asc);

		Feed result = extractResultsFrom(readOnlyWorkSolrServer.query(query),queryParams.getStart(),queryParams.getLimit());

		if (params.getDistanceInMiles() != null && NumberUtils.isNumber(params.getDistanceInMiles())) {
			result.setDistanceRadiusInMiles(Double.parseDouble(params.getDistanceInMiles()));
		}

		return result;
	}

	private void addFilters(final SolrQuery query, final List<String> filters) {
		for (final String f : filters) {
			String solrQuery = f.replace("=", ":");
			if (solrQuery.contains("|")) {
				solrQuery = solrQuery.replaceAll("\\|", " OR ");
				solrQuery = solrQuery.replace(":", ":(");
				solrQuery = solrQuery + ")";
			}
			query.addFilterQuery(solrQuery);
		}
	}

	private boolean hasLocationSortField(List<String> sort) {
		return CollectionUtils.isNotEmpty(sort) && (
			sort.contains(WorkSearchableFields.LOCATION.getName()) ||
			sort.contains("-" + WorkSearchableFields.LOCATION.getName()));
	}

	private void setSort(final SolrQuery query, final List<String> sortFields, final FeedQueryParams queryParams) {
		if (CollectionUtils.isEmpty(sortFields)) {
			return;
		}

		for (int i = 0; i < sortFields.size(); i++) {
			// a sort directive is a field name prefixed with an optional "-" to designate sort direction DESC; ASC if
			// prefix does not exist
			final String sortDirective = sortFields.get(i);
			final SolrQuery.ORDER order = sortDirective.startsWith("-") ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc;
			final String field = sortDirective.startsWith("-") ? sortDirective.substring(1) : sortDirective;
			if (WorkSearchableFields.LOCATION.getName().equals(field)) {
				if (queryParams.isLatLongValid()) {
					query.addField(field);
					query.addSort(queryParams.getSortByDistanceQuery(), order); // location sort requires a different sort string
				} else {
					logger.error("missing lat/long pair for location search");
				}
			} else {
				query.addField(field);
				query.addSort(field, order);
			}
		}
	}

	private Feed extractResultsFrom(QueryResponse queryResponse,int start,int limit){
		return (queryResponse == null) ? new Feed().setResults(new ArrayList<FeedItem>()) :
			new Feed()
				.setTotalCount(queryResponse.getResults().getNumFound())
				.setPageSize(limit)
				.setPage(start/limit)
				.setResults(queryResponse.getBeans(FeedItem.class));
	}


	private IndeedFeedDTO getIndeedFeed() {

		try {
			SolrQuery query = new SolrQuery();
			query.addField(WorkSearchableFields.COUNTRY.getName());
			FeedRequestParams params = new FeedRequestParams();
			Feed feed = getFeedWithFeatureToggle(params, query);
			return new IndeedFeedDTO(feed.getResults());
		}
		catch(SolrServerException e) {
			logger.error("[indeed-xml] Solr server error", e);
		}
		catch (Exception e) {
			logger.error("[indeed-xml]", e);
		}


		return null;

	}

	private Marshaller createMarshaller() {
		try {
			JAXBContext context = JAXBContext.newInstance(IndeedFeedDTO.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(CharacterEscapeHandler.class.getName() , new CDATAAdapterCharacterEscape());

			return m;
		}
		catch(JAXBException e) {
			logger.error("[indeed-xml] XML Binding error", e);
		}
		catch (Exception e) {
			logger.error("[indeed-xml]", e);
		}

		return null;
	}

	private StringWriter writeXML(IndeedFeedDTO xml, Marshaller m) {
		try {
			StringWriter writer = new StringWriter();
			m.marshal(xml, writer);

			return writer;
		}
		catch(JAXBException e) {
			logger.error("[indeed-xml] XML Binding error", e);
		}
		catch (Exception e) {
			logger.error("[indeed-xml]", e);
		}

		return null;
	}

	private Feed getFeedWithFeatureToggle(
		final FeedRequestParams params,
		final SolrQuery query
	) throws SolrServerException {
		if (featureEntitlementService.hasPercentRolloutFeatureToggle(Constants.SEARCH_SERVICE_WORKFEED)) {
			final FindWorkFeedRequest request = findWorkFeedAdapter.buildFindWorkFeedRequest(params, query);
			return findWorkFeedAdapter.extractFeed(request, findWorkFeed(request));
		} else {
			return requestFeed(params, query);
		}
	}
}
