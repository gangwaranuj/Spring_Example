package com.workmarket.domains.work.model.route;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.data.solr.model.SolrUserType;

import java.util.List;

/**
 * Value object holding a recommendation - a recommendation is a list of resources and an optional explain.
 */
public class Recommendation {

    private final ImmutableExplain explain;
    private final ImmutableList<RecommendedResource> recommendedResources;
    private final Long workId;

    /**
     * Constructor.
     * @param workId         The work id for recommendation
     * @param recommendedResources The set of recommended resources
     * @param explain The explain for the recommendation
     */
    public Recommendation(final Long workId, final List<RecommendedResource> recommendedResources, final Explain explain) {

        this.workId = workId;
        this.recommendedResources =
            recommendedResources != null ? ImmutableList.copyOf(recommendedResources) : ImmutableList.<RecommendedResource>of();
        this.explain = explain != null ? new ImmutableExplain(explain) : null;
    }

	/**
	 * Returns the workId of this recommendation.
     * @return Long the work id.
     */
    public Long getWorkId() {
        return workId;
    }

    /**
     * Are there recommendedResources within this Recommendation instance?
     * @return boolean
     */
    public boolean hasRecommendations() {
        return (recommendedResources != null && recommendedResources.size() > 0);
    }

    /**
     * Returns the items in the recommendation.
     * @return ImmutableList<RecommendedResource> The recommendedResources
     */
    public ImmutableList<RecommendedResource> getRecommendedResources() {
        return recommendedResources;
    }

    /**
     * Returns a list of userNumbers based on userType.
     * @param userType SolrUserType
     * @return List&lt;String&gt;
     */
    public List<String> getRecommendedResourceNumbersByUserType(final SolrUserType userType) {
        final List<String> resourceNumbers = Lists.newArrayList();
        if (hasRecommendations()) {
            for (final RecommendedResource resource : recommendedResources) {
                if (userType.equals(resource.getUserType())) {
                    resourceNumbers.add(resource.getUserNumber());
                }
            }
        }
        return resourceNumbers;
    }

    /**
     * Returns a list of userIds based on userType.
     * @param userType SolrUserType
     * @return List&lt;Long&gt;
     */
    public List<Long> getRecommendedResourceIdsByUserType(final SolrUserType userType) {
        final List<Long> resourceIds = Lists.newArrayList();
        if (hasRecommendations()) {
            for (final RecommendedResource resource : recommendedResources) {
                if (userType.equals(resource.getUserType())) {
                    resourceIds.add(resource.getId());
                }
            }
        }
        return resourceIds;
    }

    /**
     * Gets our explain.
     * @return ImmutableExplain The explain or null if there is no explain
     */
    public ImmutableExplain getExplain() {
        return explain;
    }

}
