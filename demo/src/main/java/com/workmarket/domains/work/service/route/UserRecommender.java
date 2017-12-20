package com.workmarket.domains.work.service.route;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.Explain;
import com.workmarket.domains.work.model.route.Recommendation;
import com.workmarket.domains.work.model.route.RecommendedResource;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class UserRecommender {
	private static final Logger logger = LoggerFactory.getLogger(UserRecommender.class);

	private static final Integer MAX_WORKER_TRAVEL_DISTANCE_MILES = 200;
	private static final String ID_FIELD = "id";
	private static final String USER_NUMBER_FIELD = "userNumber";
	private static final String USER_TYPE_FIELD = "userType";
	private static final String FL = ID_FIELD + "," + USER_NUMBER_FIELD + "," + USER_TYPE_FIELD;
	private static final String DIST_BY_DEGREES_NOTATION = "distDegrees";
	private static final String DIST_BY_DEGREES_VALUE = "$distDegrees";
	private static final String DIST_BY_MILES_NOTATION = "distMiles";
	private static final String DIST_BY_MILES_VALUE = "$distMiles";
	private static final String DEGREES_TO_MILES = "product(" + DIST_BY_DEGREES_VALUE + ",69.09341)";
	private static final String MAX_TRAVEL_DIST = "min(" + MAX_WORKER_TRAVEL_DISTANCE_MILES.toString() + ",maxTravelDistance)";
	private static final String DIST_FILTER = "{!frange l=0 u=9999999}sub(" + MAX_TRAVEL_DIST + "," + DIST_BY_MILES_VALUE + ")";

	private final String recommenderType;

	private final Histogram routingHistogram;

	/**
	 * Constructor
	 * @param recommenderType recommender type
	 * @param metricRegistry  metric registry
	 */
	public UserRecommender(final String recommenderType, final MetricRegistry metricRegistry) {
		this.recommenderType = recommenderType;
		WMMetricRegistryFacade metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "recommender.user");
		routingHistogram = metricRegistryFacade.histogram("routing." + recommenderType);
	}

	abstract Recommendation recommend(Work work, boolean includeExplain) throws SolrServerException;

	/**
	 * Convinent method to create a solr query with two parameters for recommnderType and workId
	 * @param workId the work id.
	 * @return SolrQuery
	 */
	SolrQuery createSolrQuery(final Long workId) {
		SolrQuery solrQuery = new SolrQuery();

		solrQuery.add("mRecommenderType", recommenderType);
		solrQuery.add("mWorkId", workId.toString());
		solrQuery.add("wt", "json");
		solrQuery.add("indent", "true");
		return solrQuery;
	}

	/**
	 * Sets common work resource filter query parameters.
	 *
	 * @param solrQuery solr query to be set
	 * @param work the work object
	 * @param rows number of results expected
	 */
	void setCommonResourceFilterQuery(final SolrQuery solrQuery, final Work work, final Integer rows) {
		long companyId = work.getCompany().getId();
		// NOTE: We currently don't filter by screening status
		//       We also don't filter by industries
		solrQuery.add("fq", "lane1CompanyIds:(" + companyId
			+ ") OR lane2CompanyIds:(" + companyId
			+ ") OR lane3CompanyIds:(" + companyId
			+ ") OR (-lane0CompanyIds:(" + companyId
			+ ") -lane1CompanyIds:(" + companyId
			+ ") -lane2CompanyIds:(" + companyId
			+ ") -lane3CompanyIds:(" + companyId
			+ ") AND (lane4Active:true OR (sharedGroupIds:1000_*)))");
		solrQuery.add("fq", "blockedUserIds:(-" + work.getBuyer().getId() + ")");
		solrQuery.add("fq", "blockedCompanyIds:(-" + companyId + ")");
		if (work.getAddress() != null && work.getAddress().getLongitude() != null && work.getAddress().getLatitude() != null) {
			solrQuery.add(DIST_BY_DEGREES_NOTATION, distByDegreesFrom(work.getAddress()));
			solrQuery.add(DIST_BY_MILES_NOTATION, DEGREES_TO_MILES);
			solrQuery.add("fq", DIST_FILTER);
			// Boost workers by their travel distance with plateau of 30-mile range.
			// When traveling <= 30 miles, the boost (boost=1) will not affect the query score;
			// when traveling > 30 miles, the boost causes the score to decay with distance (1/2 at 60 miles, 1/3 at 90 miles, 1/4 at 120 miles, etc.).
			// recip translates to 30/max(30,distance)
			solrQuery.add("boost", "recip(max(30," + DIST_BY_MILES_VALUE + "),1,30,0)");
		}
		solrQuery.setRows(rows);
		solrQuery.addField(FL);
	}

	/**
	 * Executes the given query on the specified server providing the explain as required.
	 * @param server The server we are executing against
	 * @param query The query we are executing
	 * @param explain The explain we are capturing
     * @return QueryResponse The result of the query
	 * @throws SolrServerException
     */
	QueryResponse executeQuery(final HttpSolrServer server, final SolrQuery query, final Explain explain)
		throws SolrServerException {

		final QueryResponse response = server.query(query);
		if (explain != null) {
			explain.add("Query: " + server.getBaseURL() + "/" + query.getRequestHandler() + "?" + query.toString());
			explain.add("# of results: " + response.getResults().size());
		}
		return response;
	}

	/**
	 * Creates our result recommendation (and does metric counting on it)
	 * @param work The work we are getting recommendations for
	 * @param recommendedResources The recommended users
	 * @param explain The explain
	 * @return Recommendation The recommendation
	 */
	Recommendation createRecommendation(final Work work, final List<RecommendedResource> recommendedResources, final Explain explain) {
		Recommendation recommendation = new Recommendation(work.getId(), recommendedResources, explain);

		if (recommendedResources != null && recommendedResources.size() > 0) {
			routingHistogram.update(recommendedResources.size());
		} else {
			logger.debug("Failed to make any " + recommenderType + " recommendations for work " + work.getId());
			routingHistogram.update(0);
		}

		return recommendation;
	}

	/**
	 * Extracts talent data from solr response.
	 *
	 * @param queryResponse solr query response
	 * @return a list of recommended talents
	 */
	List<RecommendedResource> extractResources(final QueryResponse queryResponse) {
		final List<RecommendedResource> resources = Lists.newArrayList();
		for (final SolrDocument doc : queryResponse.getResults()) {
			final Long id = new Long(doc.getFieldValue(ID_FIELD).toString());
			final String userNumber = doc.getFieldValue(USER_NUMBER_FIELD).toString();
			final String userTypeStr = doc.containsKey(USER_TYPE_FIELD) ? doc.getFieldValue(USER_TYPE_FIELD).toString() : "0";
			final SolrUserType userType = SolrUserType.getSolrUserTypeByCode(Integer.parseInt(userTypeStr));
			resources.add(new RecommendedResource(id, userNumber, userType));
		}
		return resources;
	}

	/**
	 * Generates distance function with multi-valued RTP field (locations) to calculate shortest distance.
	 * Note: locations distance unit is defined as "degree".
	 *
	 * @param address Address object
	 * @return String
	 */
	private String distByDegreesFrom(final Address address) {
		return String.format(
			"{!geofilt score=distance filter=false sfield=locations pt=%f,%f d=60}",
			address.getLatitude(), address.getLongitude());
	}
}
