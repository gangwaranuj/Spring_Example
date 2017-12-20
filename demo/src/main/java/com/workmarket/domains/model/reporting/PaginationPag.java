/**
 * 
 */
package com.workmarket.domains.model.reporting;

import java.io.Serializable;


/**
 * @since 8/9/2011
 *
 */
public class PaginationPag implements Serializable {

	/*
	 * Instance variables
	 */
	private Integer startRow = 0;
	private Integer pageSize = 50;
	private static final long serialVersionUID = -1873296653221127410L;
	/**
	 * @return the startRow
	 */
	public Integer getStartRow() {
		return startRow;
	}
	/**
	 * @param startRow the startRow to set
	 */
	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}
	/**
	 * @return the pageSize
	 */
	public Integer getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	public String toString(){
		return new StringBuilder("PaginationPag[")
				.append("startRow:")
				.append(getStartRow())
				.append(", pageSize:")
				.append(getPageSize())
				.append("]")
				.toString();
	}

}
