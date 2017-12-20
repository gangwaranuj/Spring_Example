package com.workmarket.domains.work.model;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class WorkUploadMappingGroupPagination extends AbstractPagination<WorkUploadMappingGroup> implements Pagination<WorkUploadMappingGroup> {
	public WorkUploadMappingGroupPagination() {}
	public WorkUploadMappingGroupPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {}
	public enum SORTS {}
}
