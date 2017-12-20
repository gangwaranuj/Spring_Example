package com.workmarket.domains.model.note.concern;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;


public class ConcernPagination extends AbstractPagination<Concern> implements Pagination<Concern> {
	
	public enum FILTER_KEYS { RESOLVED }
	
	public enum SORTS { TYPE, CONTENT, CREATED_ON, LAST_NAME }
}
