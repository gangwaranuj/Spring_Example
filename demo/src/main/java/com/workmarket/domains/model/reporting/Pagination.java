package com.workmarket.domains.model.reporting;

import java.io.Serializable;
import java.util.List;

public class Pagination implements Serializable {

	private Integer total;
	private List<PaginationPag> paginationPags;
	private static final long serialVersionUID = 6347202116381456868L;
	
	/**
	 * @return the total
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	/**
	 * @return the paginationPags
	 */
	public List<PaginationPag> getPaginationPags() {
		return paginationPags;
	}
	/**
	 * @param paginationPags the paginationPags to set
	 */
	public void setPaginationPags(List<PaginationPag> paginationPags) {
		this.paginationPags = paginationPags;
	}
	
}
