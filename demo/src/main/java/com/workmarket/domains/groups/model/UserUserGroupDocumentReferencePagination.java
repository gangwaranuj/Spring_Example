package com.workmarket.domains.groups.model;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

/**
 * User: micah
 * Date: 12/16/13
 * Time: 4:17 PM
 */
public class UserUserGroupDocumentReferencePagination extends AbstractPagination<UserUserGroupDocumentReference> implements Pagination<UserUserGroupDocumentReference> {
	public enum FILTER_KEYS {}
	public enum SORTS {}

	public UserUserGroupDocumentReferencePagination(){}
	public UserUserGroupDocumentReferencePagination(boolean returnAllRows){
		super(returnAllRows);
	}
}
