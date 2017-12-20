package com.workmarket.api.v2.employer.search.common.model;

import static com.workmarket.common.util.Util.safeToJson;

/**
 * Base class for our data objects.
 */
public abstract class BaseDTO {

    /**
     * Constructor.
     */
    public BaseDTO() {

    }

    @Override
    public String toString() {
        return safeToJson(this);
    }

}
