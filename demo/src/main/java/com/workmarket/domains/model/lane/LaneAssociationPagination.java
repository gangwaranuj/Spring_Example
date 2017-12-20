package com.workmarket.domains.model.lane;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class LaneAssociationPagination extends AbstractPagination<LaneAssociation> implements Pagination<LaneAssociation> {

	public LaneAssociationPagination() {}
	public LaneAssociationPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {}
	public enum SORTS {}
}