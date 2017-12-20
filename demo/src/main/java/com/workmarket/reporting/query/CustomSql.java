package com.workmarket.reporting.query;

import com.workmarket.domains.model.reporting.Entity;


/**
 * @since 9/15/2011
 *
 */
public interface CustomSql {

	/**
	 * @return
	 */
	public String getSelectSql(String workStatusType, Entity entity);
	
	/**
	 * @return
	 */
	public String getCriteriaSql(String workStatusType, Entity entity);
	
	
	/**
	 * @param workStatusType
	 * @return
	 */
	public AbstractFilter getFilter(String workStatusType);
}
