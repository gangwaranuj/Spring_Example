package com.workmarket.domains.model.note;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;


public class NotePagination extends AbstractPagination<Note> implements Pagination<Note> {
	
	private boolean includePrivileged = false;
	
	public NotePagination() {}
	public NotePagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		PRIVATE
	}
	public enum SORTS {}
	
	public boolean isIncludePrivileged() {
		return includePrivileged;
	}
	public void setIncludePrivileged(boolean includePrivileged) {
		this.includePrivileged = includePrivileged;
	}
}
