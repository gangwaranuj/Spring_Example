package com.workmarket.domains.model.assessment;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class AssessmentPagination extends AbstractPagination<AbstractAssessment> implements Pagination<AbstractAssessment> {

	public AssessmentPagination() {}
	public AssessmentPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		STATUS,
		TYPE,
		NOT_REMOVED;
	}
	public enum SORTS {
		NAME,
		DATE,
		LATEST_ACTIVITY,
		CREATED_ON,
		CREATED_BY,
		STATUS;
	}
}
