package com.workmarket.reporting.query;

import com.workmarket.domains.model.reporting.Entity;

/**
 * @since 09/15/2011
 * 
 */
public class WorkStatusCodeCustomSql implements CustomSql {

	public String getSelectSql(String workStatusType, Entity entity) {
		return "";
	}

	public String getCriteriaSql(String workStatusType, Entity entity) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.workmarket.reporting.query.CustomSql#getFilter(java.lang.String)
	 */
	public AbstractFilter getFilter(String workStatusType) {
		return new WorkStatuscodeFilterSqlImpl();
	}

}
