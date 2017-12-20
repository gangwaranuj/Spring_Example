package com.workmarket.data.solr.query;

/**
 * A collection of constants used for tracking search metrics - defined in one place to avoid duplication
 * all over the code.
 */
public final class SolrMetricConstants {

    public static final String SEARCH_TYPE = "mSearchType";
    public static final String USER = "mUser";
    public static final String COMPANY = "mCompany";
    public static final String REQUEST_SOURCE = "mRequestSource";
    public static final String PERSONA = "mPersona";
    public static final String USER_INDUSTRY = "mIndustry";
    public static final String USER_LOCATION = "mLLocation";
    public static final String USER_TRAVEL_DISTANCE = "mDistance";

    public static final String WEB_REQUEST = "web";
    public static final String MOBILE_REQUEST = "mobile";

    public static final String SYSTEM_PERSONA = "system";
    public static final String WORKER_PERSONA = "worker";
    public static final String EMPLOYER_PERSONA = "employer";


    private SolrMetricConstants() {}
}
