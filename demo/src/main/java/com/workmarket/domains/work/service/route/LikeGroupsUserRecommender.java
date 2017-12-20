package com.workmarket.domains.work.service.route;

import com.codahale.metrics.MetricRegistry;
import com.google.api.client.util.Lists;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.Explain;
import com.workmarket.domains.work.model.route.Recommendation;
import com.workmarket.domains.work.model.route.RecommendedResource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LikeGroupsUserRecommender extends UserRecommender {
    private static Logger logger = LoggerFactory.getLogger(LikeGroupsUserRecommender.class);

    private static final String RECOMMENDER_TYPE = "likegroups";

    private static final String USER_REQUEST_HANDLER = "dismax";

    private static final Integer ACTIVE_WORKER_LIMIT = 200;
    private static final Integer ACTIVE_WORKER_MIN_ASSIGNMENTS = 2;
    private static final Integer MAX_GROUPS = 15;
    private static final Integer MIN_WORKERS_IN_GROUP = 2;
    private static final Integer MIN_GROUP_MATCH_PERCENT = 75;
    private static final Integer MAX_WORKERS = 35;
    private static final Integer MIN_WORKERS_COMPLETED_ASSIGNMENTS = 50;

    private final HttpSolrServer userSolrServer;
    private final HttpSolrServer workSolrServer;

    /**
     * Constructor.
     * @param metricRegistry The metric registry
     * @param userSolrServer The solr server for the user core
     * @param workSolrServer The solr server for the work core
     */
    @Autowired
    public LikeGroupsUserRecommender(final MetricRegistry metricRegistry,
                                     @Qualifier("userSolrServer")final HttpSolrServer userSolrServer,
                                     @Qualifier("workSolrServer") final HttpSolrServer workSolrServer) {
        super(RECOMMENDER_TYPE, metricRegistry);

        this.userSolrServer = userSolrServer;
        this.workSolrServer = workSolrServer;
    }

    /**
     * Finds the workers relevant to this work assignment based on the given routing strategy.
     * @param work The work assignment
     * @param includeExplain Set to true if we are including the explain in our result
     * @return Recommendation
     */
    public Recommendation recommend(final Work work, final boolean includeExplain) throws SolrServerException {
        Explain explain = (includeExplain ? new Explain() : null);

        if (explain != null) {
            explain.add("Explain for " + work.getId() + " from company " + work.getCompany().getId() + " " + work.getTitle() + " :" + work.getDesiredSkills());
        }

        // from the work find our company
        logger.info("Finding most active workers for company " + work.getCompany().getId());
        List<Long> resourceIds = findMostActiveResourcesForCompany(work, explain);
        logger.info("Most active resources for company " + work.getCompany().getId() + " are [" + StringUtils.join(resourceIds, " ") + "]");

        // now take the worker and find their groups
        logger.info("Finding most common groups");
        List<Long> commonGroups = findGroupsInCommon(work, resourceIds, explain);
        logger.info("Most common groups are [" + StringUtils.join(commonGroups, " ") + "]");

        // now find the workers
        logger.info("Recommending works based on groups");
        List<RecommendedResource> recommendedResources = findResources(work, commonGroups, explain);

        return createRecommendation(work, recommendedResources, explain);
    }

    /**
     * Find the most active resources for the company.
     * @param work The work we are recommending work for
     * @param explain The explain we are capturing
     * @return List&lt;Long&gt; The resource ids for the most active resources
     * @throws SolrServerException
     */
    private List<Long> findMostActiveResourcesForCompany(final Work work, final Explain explain) throws SolrServerException {
        List<Long> result = Lists.newArrayList();

        SolrQuery solrQuery = createSolrQuery(work.getId());

        solrQuery.add("q", "searchableWorkStatusTypeCode:paid AND companyId:" + work.getCompany().getId());
        solrQuery.add("fl", "assignedResourceId");
        solrQuery.add("facet", "true");
        solrQuery.add("facet.query", "*:*");
        solrQuery.add("facet.field", "assignedResourceId");
        solrQuery.add("f.assignedResourceId.facet.limit", ACTIVE_WORKER_LIMIT.toString());
        solrQuery.add("f.assignedResourceId.facet.mincount", ACTIVE_WORKER_MIN_ASSIGNMENTS.toString());


        // execute our query
        QueryResponse queryResponse = executeQuery(workSolrServer, solrQuery, explain);

        FacetField facetField = queryResponse.getFacetField("assignedResourceId");
        if (explain != null) {
            explain.add("Most active workers for company:");
        }
        for (FacetField.Count v : facetField.getValues()) {
            result.add(new Long(v.getName()));

            if (explain != null) {
                explain.add("Worker: " + v.getName() + " -> Assignments: " + v.getCount());
            }
        }

        if (explain != null) {
            explain.add("Most active resources: [" + StringUtils.join(result, " ") + "]");
        }

        return result;
    }

    /**
     * Finds the top groups these resources have in common.
     * @param work The work we are making a recommendation for
     * @param resourceIds The set of resource ids we are looking over
     * @param explain The explain we are capturing
     * @return List&lt;Long&gt; The set of common group ids
     * @throws SolrServerException
     */
    private List<Long> findGroupsInCommon(final Work work, final List<Long> resourceIds, final Explain explain) throws SolrServerException {
        List<Long> result = Lists.newArrayList();

        if (CollectionUtils.isEmpty(resourceIds)) {
            logger.info("No workers found, unable to find common groups for work " + work.getId());
            return result;
        }

        SolrQuery solrQuery = createSolrQuery(work.getId());
        solrQuery.setRequestHandler(USER_REQUEST_HANDLER);

        solrQuery.add("q", StringUtils.join(resourceIds, ' '));
        solrQuery.add("qf", "id");
        solrQuery.add("fl", "groupIds");
        solrQuery.add("fq", "userType:0");
        solrQuery.add("facet", "true");
        solrQuery.add("facet.query", "*:*");
        solrQuery.add("facet.field", "groupIds");
        solrQuery.add("f.groupIds.facet.limit", MAX_GROUPS.toString());
        solrQuery.add("f.groupIds.facet.mincount", MIN_WORKERS_IN_GROUP.toString());

        // execute our query
        QueryResponse queryResponse = executeQuery(userSolrServer, solrQuery, explain);

        FacetField facetField = queryResponse.getFacetField("groupIds");
        if (explain != null) {
            explain.add("Finding common groups:");
        }

        for (FacetField.Count v : facetField.getValues()) {
            result.add(new Long(v.getName()));

            if (explain != null) {
                explain.add("Group: " + v.getName() + " -> References: " + v.getCount());
            }
        }

        if (explain != null) {
            explain.add("Common groups: [" + StringUtils.join(result, " ") + "]");
        }


        return result;

    }

    /**
     * Finds the resources some % of groups from our top resources.
     * @param work The work we are resolving resources for
     * @param groupIds The list of group ids used as our criteria
     * @param explain The explain we are capturing
     * @return List&lt;RecommendedResource&gt; The set of resources
     * @throws SolrServerException
     */
    private List<RecommendedResource> findResources(final Work work, final List<Long> groupIds,
                                                    final Explain explain) throws SolrServerException {
        List<RecommendedResource> result = Lists.newArrayList();

        if (CollectionUtils.isEmpty(groupIds)) {
            logger.info("No common groups found - unable to recommend resources for work " + work.getId());
            return result;
        }

        SolrQuery solrQuery = createSolrQuery(work.getId());
        setCommonResourceFilterQuery(solrQuery, work, MAX_WORKERS);
        solrQuery.add("q", StringUtils.join(groupIds, ' '));
        solrQuery.add("qf", "groupIds");
        // The following fq query is malformed. But the correct format will filter out any workers with less than
        // 200 completed work count. This seems to be extremely high filtering criterion.
        // Since it is not really applied properly, we will disable it for now and investigate later.
        //solrQuery.add("fq", "workCompletedCount[" + MIN_WORKERS_COMPLETED_ASSIGNMENTS.toString() + " *]");
        solrQuery.add("mm", MIN_GROUP_MATCH_PERCENT.toString() + "%");

        // execute our query
        QueryResponse queryResponse = executeQuery(userSolrServer, solrQuery, explain);
        return extractResources(queryResponse);
    }
}
