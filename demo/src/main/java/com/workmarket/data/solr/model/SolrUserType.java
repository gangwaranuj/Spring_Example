package com.workmarket.data.solr.model;

/**
 * User type to indicate user as worker or vendor.
 */
public enum SolrUserType {
    /**
     * worker type.
     */
    WORKER(0),
    /**
     * vendor type.
     * todo: should we use 'company'?
     */
    VENDOR(1);

    private final int solrUserTypeCode;

    /**
     * Constructor.
     *
     * @param solrUserTypeCode solr user type code
     */
    SolrUserType(final int solrUserTypeCode) {
        this.solrUserTypeCode = solrUserTypeCode;
    }

    /**
     * Gets solr user type by the code.
     *
     * @param code the code value of a solr user type
     * @return SolrUserType
     */
    public static SolrUserType getSolrUserTypeByCode(final int code) {
        for (final SolrUserType type : values()) {
            if (type.getSolrUserTypeCode() == code) {
                return type;
            }
        }
        return null;
    }

    /**
     * Gets solr user type code.
     *
     * @return int
     */
    public int getSolrUserTypeCode() {
        return solrUserTypeCode;
    }
}
