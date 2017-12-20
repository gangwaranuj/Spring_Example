package com.workmarket.domains.work.model.route;

import com.workmarket.data.solr.model.SolrUserType;

/**
 * Object to hold recommended resource, either worker type or vendor type.
 */
public class RecommendedResource {
    private final Long id;
    private final String userNumber;
    private final SolrUserType userType;

    public RecommendedResource(final Long id, final String userNumber, final SolrUserType userType) {
        this.id = id;
        this.userNumber = userNumber;
        this.userType = userType;
    }

    public Long getId() {
        return id;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public SolrUserType getUserType() {
        return userType;
    }
}
