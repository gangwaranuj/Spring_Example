/**
 * 
 */
package com.workmarket.domains.model.reporting;

import java.io.Serializable;
import java.util.List;


public class EntityResponseForReport implements Serializable {

	/**
	 * Instance variables and constants
	 */
	private List<String> headers;
	private List<List<String>>  rows;
	private String fileName;
	private Pagination pagination;

	private static final long serialVersionUID = 7375422008738797323L;

	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public List<List<String>>  getRows() {
		return rows;
	}

	public void setRows(List<List<String>>  rows) {
		this.rows = rows;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the pagination
	 */
	public Pagination getPagination() {
		return pagination;
	}

	/**
	 * @param pagination the pagination to set
	 */
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

}
