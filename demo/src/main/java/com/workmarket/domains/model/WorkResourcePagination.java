package com.workmarket.domains.model;

public class WorkResourcePagination extends AbstractPagination<WorkResource> implements Pagination<WorkResource> {
	public enum FILTER_KEYS {
		WORK_RESOURCE_STATUS
	}
	public enum SORTS {}
	
	public WorkResourcePagination() {}
	public WorkResourcePagination(Boolean returnAllRows) {
		super(returnAllRows);
	}
}
