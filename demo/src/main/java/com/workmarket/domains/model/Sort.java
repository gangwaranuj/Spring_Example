package com.workmarket.domains.model;

public class Sort {

	private String sortColumn;
	private Pagination.SORT_DIRECTION sortDirection;

	public Sort() {}
	
	public Sort(String sortColumn, Pagination.SORT_DIRECTION sortDirection) {
		this.sortColumn = sortColumn;
		this.sortDirection = sortDirection;
	}
	
	public String getSortColumn() {
		return sortColumn;
	}

	public Sort setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
		return this;
	}

	public Pagination.SORT_DIRECTION getSortDirection() {
		return sortDirection;
	}

	public Sort setSortDirection(Pagination.SORT_DIRECTION sortDirection) {
		this.sortDirection = sortDirection;
		return this;
	}
}
