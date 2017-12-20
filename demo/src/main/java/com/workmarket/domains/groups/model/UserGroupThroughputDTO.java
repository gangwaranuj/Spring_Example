package com.workmarket.domains.groups.model;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Contains the work price summation for all work routed (paid) through a UserGroup since
 * a given date (formDate field).
 *
 * User: ianha
 * Date: 11/25/13
 * Time: 3:21 PM
 */
public class UserGroupThroughputDTO {
	private Long userGroupId;
	private BigDecimal throughput;
	private Calendar fromDate; // work price sum since this date

	public Calendar getFromDate() {
		return fromDate;
	}

	public void setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
	}

	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public BigDecimal getThroughput() {
		return throughput;
	}

	public void setThroughput(BigDecimal throughput) {
		this.throughput = throughput;
	}

	@Override
	public boolean equals(Object obj) {
		return ((UserGroupThroughputDTO)obj).getUserGroupId() == getUserGroupId();
	}
}

