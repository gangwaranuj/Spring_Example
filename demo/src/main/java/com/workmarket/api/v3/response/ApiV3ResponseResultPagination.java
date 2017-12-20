package com.workmarket.api.v3.response;

import java.io.Serializable;

/**
 * Created by joshlevine on 12/22/16.
 */
public interface ApiV3ResponseResultPagination extends Serializable {
  Integer getOffset();
  Integer getLimit();
  Integer getResults();
}
