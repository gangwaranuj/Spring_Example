package com.workmarket.domains.model.request;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

/**
 * Created by nick on 4/24/13 9:36 PM
 */
public class UserGroupInvitationPagination extends AbstractPagination<UserGroupInvitation> implements Pagination<UserGroupInvitation> {
	public enum SORTS {
		GROUP_NAME,
		INVITED_ON
	}
}
