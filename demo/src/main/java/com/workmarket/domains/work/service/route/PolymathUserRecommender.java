package com.workmarket.domains.work.service.route;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.qualification.WorkToQualification;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.Explain;
import com.workmarket.domains.work.model.route.Recommendation;
import com.workmarket.domains.work.model.route.RecommendedResource;
import com.workmarket.id.IdGenerator;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.service.business.qualification.QualificationAssociationService;
import com.workmarket.service.business.qualification.QualificationRecommender;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PolymathUserRecommender extends UserRecommender {

	private static final Logger logger = LoggerFactory.getLogger(PolymathUserRecommender.class);

	private static final String RECOMMENDER_TYPE = "polymath";

	private static final String MORE_LIKE_THIS_HANDLER = "/mlt";
	private static final Integer MAX_WORKERS = 55;
	private static final String MLT_MINIMUM_DOC_FREQUENCY = "10";
	private static final String MLT_MINIMUM_TERM_FREQUENCY = "1";
	private static final String MLT_BOOST = "true";
	private static final String WORK_FL = "title,skills";
	private static final String WORK_MLT_FL = "qualificationMatch";
	private static final String USER_SKILL_QF = "qualificationMatch";
	private static final String JOB_TITLE_QF = "jobTitle";

	private final HttpSolrServer userSolrServer;
	private final HttpSolrServer workSolrServer;
	private final IdGenerator idGenerator;
	private final QualificationAssociationService qualificationAssociationService;
	private final QualificationRecommender qualificationRecommender;

	/**
	 * Constructor
	 *
	 * @param metricRegistry  metric registry
	 */
	@Autowired
	public PolymathUserRecommender(final MetricRegistry metricRegistry,
	                               @Qualifier("userSolrServer") final HttpSolrServer userSolrServer,
	                               @Qualifier("workSolrServer") final HttpSolrServer workSolrServer,
	                               final IdGenerator idGenerator,
	                               final QualificationAssociationService qualificationAssociationService,
	                               final QualificationRecommender qualificationRecommender) {
		super(RECOMMENDER_TYPE, metricRegistry);
		this.userSolrServer = userSolrServer;
		this.workSolrServer = workSolrServer;
		this.idGenerator = idGenerator;
		this.qualificationAssociationService = qualificationAssociationService;
		this.qualificationRecommender = qualificationRecommender;
	}

	@Override
	public Recommendation recommend(final Work work, final boolean includeExplain) throws SolrServerException {
		Explain explain = (includeExplain ? new Explain() : null);

		final RequestContext requestContext = getRequestContext(work.getBuyer().getId().toString());
		final List<RecommendedResource> recommendedResources = findResources(work, requestContext, explain);
		return createRecommendation(work, recommendedResources, explain);
	}

	/**
	 * Finds a list workers based work title and desired skills.
	 * @param work    The work with title and desired skills.
	 * @param explain Explain for logging.
	 * @return        A list of worker ids.
	 * @throws SolrServerException
	 */
	private List<RecommendedResource> findResources(final Work work, final RequestContext requestContext, final Explain explain)
		throws SolrServerException {

		final List<RecommendedResource> resources = new ArrayList<>();
		final StringBuilder queryBuilder = new StringBuilder();
		final String bundledJobTitles = createJobTitleQueryString(work, requestContext);
		final String query = getInterestingTerms(work, explain);

		if (StringUtils.isNotBlank(query)) {
			queryBuilder.append(USER_SKILL_QF).append(":(").append(query).append(")");
		}

		if (StringUtils.isNotBlank(bundledJobTitles)) {
			if (queryBuilder.length() > 0) {
				queryBuilder.append(" ");
			}
			queryBuilder.append(JOB_TITLE_QF).append(":(").append(bundledJobTitles).append(")");
		}

		// no interesting terms, don't recommend anyone.
		if (queryBuilder.length() == 0) {
			return resources;
		}

		final SolrQuery solrQuery = createSolrQuery(work.getId());
		setCommonResourceFilterQuery(solrQuery, work, MAX_WORKERS);
		solrQuery.add("q", queryBuilder.toString());
		solrQuery.add("mm", "15%");
		// Boost new workers by the workers' join dates with a plateau of 30-day range.
		// When joined within 30 days, the boost (boost=1) doesn't affect the query score;
		// when joined more than 30 days ago, the boost causes the score to decay with age (1/2 at 60 days, 1/3 at 90 days, 1/4 at 120 days, etc.).
		// 1.157e-8 equals roughly to 1/86400000, which is the inverse of the milliseconds per day;
		// 2.592e+9 is milliseconds in 30 days;
		// recip translates to 30/max(30,days_since_join)
		solrQuery.add("boost", "recip(max(2.592e+9,ms(NOW/DAY,createdOn)),1.157e-8,30,0)");

		final QueryResponse queryResponse = executeQuery(userSolrServer, solrQuery, explain);
		return extractResources(queryResponse);
	}

	/**
	 * Creates stream.body for MoreLikeThis search.
	 *
	 * @param work           work object
	 * @param explain        explain
	 * @return String of stream.body
	 */
	private String createStreamBody(final Work work, final Explain explain) throws SolrServerException {
		Work updatedWork = work;
		if (StringUtilities.none(work.getTitle(), work.getDesiredSkills())) {
			updatedWork = getWorkDataFromIndex(work, explain);
		}
		final StringBuilder streamBodyBuilder = new StringBuilder();
		if (StringUtils.isNotBlank(updatedWork.getTitle())) {
			streamBodyBuilder.append(updatedWork.getTitle()).append(" ");
		}
		if (StringUtils.isNotBlank(updatedWork.getDesiredSkills())) {
			streamBodyBuilder.append(updatedWork.getDesiredSkills()).append(" ");
		}

		return streamBodyBuilder.toString();
	}

	/**
	 * Gets title and skills of work from Solr.
	 *
	 * @param work work object
	 * @return     work object with title and skills from Solr
	 * @throws SolrServerException
	 */
	private Work getWorkDataFromIndex(final Work work, final Explain explain) throws SolrServerException {
		final SolrQuery solrQuery = createSolrQuery(work.getId());
		solrQuery.add("q", "id:" + work.getId());
		solrQuery.add("fl", WORK_FL);
		final QueryResponse queryResponse = executeQuery(workSolrServer, solrQuery, explain);
		if (queryResponse.getResults().size() == 0) {
			return work;
		}
		else {
			SolrDocument doc = queryResponse.getResults().get(0);
			work.setTitle((String) doc.getFieldValue("title"));
			if (doc.getFieldValue("skills") != null) {
				work.setDesiredSkills(doc.getFieldValue("skills").toString());
			}
			return work;
		}
	}

	/**
	 * Uses Solr's MoreLikeThis to get a list of interesting terms based on work title and desired skills.
	 *
	 * @param work           work object
	 * @param explain        explain
	 * @return               A string of interesting terms.
	 * @throws SolrServerException
	 */
	private String getInterestingTerms(final Work work, final Explain explain) throws SolrServerException {
		final String streamBody = createStreamBody(work, explain);
		if (StringUtils.isBlank(streamBody)) {
			// there is no data available for MLT
			return streamBody;
		}

		final SolrQuery solrQuery = createSolrQuery(work.getId());
		solrQuery.setRequestHandler(MORE_LIKE_THIS_HANDLER);
		solrQuery.add("mlt.fl", WORK_MLT_FL);
		solrQuery.add("stream.body", streamBody);
		solrQuery.add("mlt.interestingTerms", "details");
		solrQuery.add("mlt.boost", MLT_BOOST);
		solrQuery.add("mlt.mindf", MLT_MINIMUM_DOC_FREQUENCY);
		solrQuery.add("mlt.mintf", MLT_MINIMUM_TERM_FREQUENCY);
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
	 * Creates query string for finding workers with skills.
	 * termsWithBoost is generated based on workcore fields,
	 * and they are different from the fields we will query in usercore.
	 * This method strip away the fieldName and concatenate boost back to the value.
	 *
	 * @param termsWithBoost A list of terms with boosting value.
	 * @return               A query string.
	 */
	private String createQueryString(NamedList<Object> termsWithBoost) {

		if (termsWithBoost.size() == 0) {
			return null;
		}

		List<String> queryTerms =  new ArrayList<>();
		for (int i = 0; i < termsWithBoost.size(); i++) {
			final String term = ClientUtils.escapeQueryChars(termsWithBoost.getName(i).split(":", 2)[1]);
			if (StringUtils.isNotEmpty(term)) {
				queryTerms.add(term + "^" + termsWithBoost.getVal(i).toString());
			}
		}
		return StringUtils.join(queryTerms, " ");
	}

	private String createJobTitleQueryString(final Work work, final RequestContext requestContext) {
		final ImmutableList.Builder<String> bundledJobTitles = ImmutableList.builder();
		if (work.getId() != null) {
			List<WorkToQualification> workToQualifications =
				qualificationAssociationService.findWorkQualifications(work.getId(), QualificationType.job_title, false);
			if (workToQualifications.size() > 0) {
				// we use first one only
				final String qualificationUuid = workToQualifications.get(0).getQualificationUuid();
				qualificationRecommender.searchSimilarQualifications(qualificationUuid, requestContext)
					.subscribe(
						new Action1<Qualification>() {
							@Override
							public void call(com.workmarket.search.qualification.Qualification qualification) {
								// boost original job title as is
								// boost similar job titles by 0.8
								if (qualification.getUuid().equals(qualificationUuid)) {
									bundledJobTitles.add("\"" + qualification.getName() + "\"^1.0");
								} else {
									bundledJobTitles.add("\"" + qualification.getName() + "\"^0.8");
								}
							}
						},
						new Action1<Throwable>() {
							@Override
							public void call(Throwable throwable) {
								logger.error("Failed to fetch job titles from qualification service: " + throwable);
							}
						});
			}
		}
		return StringUtils.join(bundledJobTitles.build(), " ");
	}

	private RequestContext getRequestContext(String userId) {
		final StringBuilder idGen = new StringBuilder();
		idGenerator.next().subscribe(
			new Action1<String>() {
				@Override
				public void call(String s) {
					idGen.append(s);
				}
			},
			new Action1<Throwable>() {
				@Override
				public void call(Throwable throwable) {
					logger.error("Failed to get an id from the id service", throwable);
					idGen.append(UUID.randomUUID().toString());
				}
			});

		final RequestContext requestContext = new RequestContext(idGen.toString(), "DUMMY_TENANT_ID");
		requestContext.setUserId(userId);
		return requestContext;
	}
}
