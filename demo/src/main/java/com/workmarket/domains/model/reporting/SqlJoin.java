/**
 * 
 */
package com.workmarket.domains.model.reporting;

import java.io.Serializable;
import java.lang.Comparable;

/**
 * @since 8/8/2011
 *
 */
public class SqlJoin implements Serializable, Comparable<SqlJoin> {
	
	/*
	 * Instance variables and constants
	 */
	private Integer sortOrder;
	private String join;
	private static final long serialVersionUID = -1842793490794168980L;

	/**
	 * @return the sortOrder
	 */
	public Integer getSortOrder() {
		return sortOrder;
	}
	/**
	 * @param sortOrder the sortOrder to set
	 */
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	/**
	 * @return the join
	 */
	public String getJoin() {
		return join;
	}
	/**
	 * @param join the join to set
	 */
	public void setJoin(String join) {
		this.join = join;
	}
	
	@Override
	public int compareTo(SqlJoin os) {
		//SqlJoin os = (SqlJoin)o;
		if(os.getSortOrder() > this.getSortOrder())
			return -1;
		if(os.getSortOrder() < this.getSortOrder())
			return 1;

		return 0;
	}

}
