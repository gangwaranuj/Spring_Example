package com.workmarket.domains.model.assessment;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class AssessmentUserAssociationPagination extends AbstractPagination<AssessmentUserAssociation> implements Pagination<AssessmentUserAssociation> {
	public enum FILTER_KEYS {
		COMPLETED_FLAG, PASSED_FLAG, COMPANY_ID
	}
	public enum SORTS {}
	
	public AssessmentUserAssociationPagination() {}
	public AssessmentUserAssociationPagination(boolean returnAllRows) {
		super(returnAllRows);
	}
}
