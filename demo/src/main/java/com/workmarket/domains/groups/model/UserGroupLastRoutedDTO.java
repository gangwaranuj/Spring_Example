package com.workmarket.domains.groups.model;

import java.util.Calendar;

/**
 * Contains the timestamp of the latest routed work to
 * a group.
 *
 * User: ianha
 * Date: 12/1/13
 * Time: 10:41 PM
 */
public class UserGroupLastRoutedDTO {
	private Long userGroupId;
	private Calendar lastRoutedOn;

	public Calendar getLastRoutedOn() {
		return lastRoutedOn;
	}

	public void setLastRoutedOn(Calendar lastRoutedOn) {
		this.lastRoutedOn = lastRoutedOn;
	}

	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	@Override
	public boolean equals(Object obj) {
		return ((UserGroupLastRoutedDTO)obj).getUserGroupId() == getUserGroupId();
	}
}
