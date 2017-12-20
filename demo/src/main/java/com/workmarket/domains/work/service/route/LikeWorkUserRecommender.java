package com.workmarket.domains.work.service.route;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.Explain;
import com.workmarket.domains.work.model.route.Recommendation;
import com.workmarket.domains.work.model.route.RecommendedResource;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.util.NamedList;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.workmarket.utility.CollectionUtilities.isEmpty;

@Component
public class LikeWorkUserRecommender extends UserRecommender {

	private static final Logger logger = LoggerFactory.getLogger(LikeWorkUserRecommender.class);

	private static final String RECOMMENDER_TYPE = "likework";
	private static final String MORE_LIKE_THIS_HANDLER = "/mlt";
	private static final Integer MAX_DISTANCE_BETWEEN_WORK = 200;
	private static final Integer MAX_WORKERS = 75;
	private static final Integer MAX_WORK = 100;
	private static final String MLT_MINIMUM_DOC_FREQUENCY = "5";
	private static final String MLT_MINIMUM_TERM_FREQUENCY = "1";
	private static final String MLT_BOOST = "true";


	private static final Pattern END_WITH_PUNCTUATION = Pattern.compile("\\p{Punct}$");

	private final HttpSolrServer userSolrServer;
	private final HttpSolrServer workSolrServer;

	/**
	 * Constructor.
	 * @param metricRegistry The metric registry
	 * @param userSolrServer solr server pointing to usercore
	 * @param workSolrServer solr server pointing to workcore
	 */
	@Autowired
	public LikeWorkUserRecommender(final MetricRegistry metricRegistry,
	                               @Qualifier("userSolrServer") final HttpSolrServer userSolrServer,
	                               @Qualifier("workSolrServer") final HttpSolrServer workSolrServer) {
		super(RECOMMENDER_TYPE, metricRegistry);
		this.userSolrServer = userSolrServer;
		this.workSolrServer = workSolrServer;
	}

	/**
	 * Recommend workers based on work.
	 *
	 * @param work           Work object
	 * @param includeExplain flag to whether include explain info
	 * @return               Recommandation object with a list of worker ids
	 * @throws SolrServerException
	 */
	@Override
	public Recommendation recommend(final Work work, final boolean includeExplain) throws SolrServerException {
		Explain explain = (includeExplain ? new Explain() : null);

		List<Long> resourceIds = findResourcesBySimilarWork(work, explain);
		List<RecommendedResource> recommendedResources = filterResources(resourceIds, work, explain);

		return createRecommendation(work, recommendedResources, explain);
	}

	/**
	 * Finds resources who have done similar work.
	 *
	 * @param work Work object
	 * @return A list of resource ids
	 * @throws SolrServerException
	 */
	private List<Long> findResourcesBySimilarWork(final Work work, final Explain explain) throws SolrServerException {

		final String queryStr = getInterestingTerms(work, explain);

		if (explain != null) explain.add("work_id " + work.getId() + " query: " + queryStr);
		if (queryStr == null) {
			return new ArrayList<>();
		}

		final SolrQuery solrQuery = createSolrQuery(work.getId());

		solrQuery.add("defType", "edismax");
		solrQuery.add("stopwords", "true");
		solrQuery.add("lowercaseOperators", "true");
		solrQuery.add("fl", "assignedResourceId");
		if (work.getIndustry() != null) {
			solrQuery.add("fq", "industryId:" + work.getIndustry().getId());
		}
		solrQuery.add("fq", "searchableWorkStatusTypeCode:paid");
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String current = fmt.withZoneUTC().print(DateTime.now());
		String sixMonthsAgo = fmt.withZoneUTC().print(DateTime.now().minusMonths(6));
		solrQuery.add("fq", "completedDate:[" + sixMonthsAgo + " TO " + current + "]");
		if (work.getAddress() != null && work.getAddress().getLongitude() != null && work.getAddress().getLatitude() != null) {
			solrQuery.add("fq", "{!geofilt sfield=location pt="
					+ work.getAddress().getLatitude() + "," + work.getAddress().getLongitude()
					+ " d=" + MAX_DISTANCE_BETWEEN_WORK + "}");
		}
		solrQuery.add("q", queryStr);
		solrQuery.setRows(MAX_WORK);

		if (explain != null) {
			explain.add("Similar work query:");
		}
		final QueryResponse queryResponse = executeQuery(workSolrServer, solrQuery, explain);
		// use LinkedHashSet so we can keep the order of doc to indicate ranking
		final LinkedHashSet<Long> resourceIds = new LinkedHashSet<>();
		for (SolrDocument doc : queryResponse.getResults()) {
			resourceIds.add(new Long(doc.getFieldValue("assignedResourceId").toString()));
		}
		return new ArrayList<>(resourceIds);
	}

	/**
	 * Filters resources with a set of criteria
	 * @param resourceIds A list of resource ids
	 * @param work Work object to provide criteria
	 * @return List&lt;RecommendedResource&gt;
	 * @throws SolrServerException
	 */
	private List<RecommendedResource> filterResources(final List<Long> resourceIds, final Work work, final Explain explain)
		throws SolrServerException {

		final List<RecommendedResource> resources = Lists.newArrayList();

		if (isEmpty(resourceIds)) {
			return resources;
		}

		final SolrQuery solrQuery = createSolrQuery(work.getId());
		setCommonResourceFilterQuery(solrQuery, work, MAX_WORKERS);
		// TODO [Lu]: LikeWork algo relies on worker id matched in workcore;
		// TODO [Lu]: therefore all the ids in the following are worker ids and we need userType:0 filter
		// TODO [Lu]: consider extract worker's companyId -- "assignedResourceCompanyId" for vendors
		solrQuery.add("fq", "userType:0");
		solrQuery.add("qf", "id");
		solrQuery.add("q", StringUtils.join(resourceIds, ' '));

		final QueryResponse queryResponse = executeQuery(userSolrServer, solrQuery, explain);
		return extractResources(queryResponse);
	}

	/**
	 * Uses Solr's MoreLikeThis to get a list of interesting terms based on work title and desired skills
	 * @param work Work object.
	 * @return     A string of interesting terms.
	 * @throws SolrServerException
	 */
	private String getInterestingTerms(final Work work, final Explain explain) throws SolrServerException {
		final String streamBody = createStreamBody(work);
		final boolean workIsindexed = isIndexed(work);
		if (streamBody == null && !workIsindexed) {
			// there is no data available for MLT
			return null;
		}
		final SolrQuery solrQuery = createSolrQuery(work.getId());
		solrQuery.setRequestHandler(MORE_LIKE_THIS_HANDLER);
		solrQuery.add("mlt.interestingTerms", "details");
		solrQuery.add("mlt.fl", "title,skills");
		if (workIsindexed) {
			solrQuery.add("q", "id:" + work.getId());
		} else {
			solrQuery.add("stream.body", streamBody);
		}
		solrQuery.add("mlt.boost", MLT_BOOST);
		solrQuery.add("mlt.mindf", MLT_MINIMUM_DOC_FREQUENCY); // minimum document frequency
		solrQuery.add("mlt.mintf", MLT_MINIMUM_TERM_FREQUENCY); // minimum term frequency
		solrQuery.setRows(0);

		if (explain != null) {
			explain.add("InterestingTerm query: ");
		}
		final QueryResponse queryResponse = executeQuery(workSolrServer, solrQuery, explain);
		@SuppressWarnings("unchecked")
		final NamedList<Object> termsWithBoost = (NamedList<Object>) queryResponse.getResponse().get("interestingTerms");
		return createQueryString(termsWithBoost);
	}

	/**
	 * Creates stream.body for MoreLikeThis search
	 * @param work Work object.
	 * @return String of stream.body
	 */
	private String createStreamBody(final Work work) {
		if (StringUtilities.none(work.getTitle(), work.getDesiredSkills())) {
			return null;
		}
		return (StringUtilities.isNotEmpty(work.getDesiredSkills()) ? work.getDesiredSkills() : "")
				+ " "
				+ (StringUtilities.isNotEmpty(work.getTitle()) ? work.getTitle() : "");
	}

	/**
	 * Check if a work object is indexed.
	 * @param work Work object.
	 * @return     boolean flag.
	 * @throws SolrServerException
	 */
	private boolean isIndexed(final Work work) throws SolrServerException {
		final SolrQuery solrQuery = createSolrQuery(work.getId());
		solrQuery.add("q", "id:" + work.getId());
		QueryResponse queryResponse = workSolrServer.query(solrQuery);
		return queryResponse.getResults().size() > 0;
	}

	/**
	 * Creates query string for finding similar work.
	 * The main process is to remove ngrams and keeps the longest one.
	 * @param termsWithBoost NamedList from solr query response.
	 * @return               String of query.
	 */
	private String createQueryString(final NamedList<Object> termsWithBoost) {

		if (termsWithBoost.size() == 0) {
			return null;
		}

		final Set<Integer> indexes = new HashSet<>();
		for (int i = 0; i < termsWithBoost.size(); i++) {
			final String term = termsWithBoost.getName(i);

			if (END_WITH_PUNCTUATION.matcher(term).find()) {
				continue;
			}

			boolean matched = false;
			for (Integer index : indexes) {
				String n = termsWithBoost.getName(index);
				if (n.contains(term)) {
					matched = true;
					break;
				}
				if (term.contains(n)) {
					matched = true;
					indexes.add(i);
					indexes.remove(index);
					break;
				}
			}
			if (!matched) {
				indexes.add(i);
			}
		}

		final List<String> terms = new ArrayList<>();
		for (Integer index : indexes) {
			final String[] parts = termsWithBoost.getName(index).split(":", 2);
			terms.add(parts[0] + ":" + ClientUtils.escapeQueryChars(parts[1]) + "^" + termsWithBoost.getVal(index));
		}

		return StringUtils.join(terms, " ");
	}
}
