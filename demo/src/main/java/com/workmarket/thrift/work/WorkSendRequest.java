package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkSendRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String workNumber;
	private String userNumber;
	private List<Long> groupIds;
	private boolean isAutoSend = false;
	private boolean assignToFirstToAccept;

	public WorkSendRequest() {
	}

	public String getWorkNumber() {
		return this.workNumber;
	}

	public WorkSendRequest setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public WorkSendRequest setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public int getGroupIdsSize() {
		return (this.groupIds == null) ? 0 : this.groupIds.size();
	}

	public java.util.Iterator<Long> getGroupIdsIterator() {
		return (this.groupIds == null) ? null : this.groupIds.iterator();
	}

	public void addToGroupIds(long elem) {
		if (this.groupIds == null) {
			this.groupIds = new ArrayList<Long>();
		}
		this.groupIds.add(elem);
	}

	public List<Long> getGroupIds() {
		return this.groupIds;
	}

	public WorkSendRequest setGroupIds(List<Long> groupIds) {
		this.groupIds = groupIds;
		return this;
	}

	public boolean isAutoSend() {
		return isAutoSend;
	}

	public boolean isAutoSendRequest() {
		return isAutoSend;
	}

	public WorkSendRequest setAutoSend(boolean autoSend) {
		isAutoSend = autoSend;
		return this;
	}

	public boolean isAssignToFirstToAccept() {
		return assignToFirstToAccept;
	}

	public WorkSendRequest setAssignToFirstToAccept(boolean assignToFirstToAccept) {
		this.assignToFirstToAccept = assignToFirstToAccept;
		return this;
	}

	public boolean isSetGroupIds() {
		return this.groupIds != null;
	}

	public boolean isGroupSendRequest() {
		return isSetGroupIds() && !isAutoSend;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkSendRequest)
			return this.equals((WorkSendRequest) that);
		return false;
	}

	private boolean equals(WorkSendRequest that) {
		if (that == null)
			return false;

		boolean this_present_workNumber = true && this.isSetWorkNumber();
		boolean that_present_workNumber = true && that.isSetWorkNumber();
		if (this_present_workNumber || that_present_workNumber) {
			if (!(this_present_workNumber && that_present_workNumber))
				return false;
			if (!this.workNumber.equals(that.workNumber))
				return false;
		}

		boolean this_present_userNumber = true && this.isSetUserNumber();
		boolean that_present_userNumber = true && that.isSetUserNumber();
		if (this_present_userNumber || that_present_userNumber) {
			if (!(this_present_userNumber && that_present_userNumber))
				return false;
			if (!this.userNumber.equals(that.userNumber))
				return false;
		}

		boolean this_present_groupIds = true && this.isSetGroupIds();
		boolean that_present_groupIds = true && that.isSetGroupIds();
		if (this_present_groupIds || that_present_groupIds) {
			if (!(this_present_groupIds && that_present_groupIds))
				return false;
			if (!this.groupIds.equals(that.groupIds))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_workNumber = true && (isSetWorkNumber());
		builder.append(present_workNumber);
		if (present_workNumber)
			builder.append(workNumber);

		boolean present_userNumber = true && (isSetUserNumber());
		builder.append(present_userNumber);
		if (present_userNumber)
			builder.append(userNumber);

		boolean present_groupIds = true && (isSetGroupIds());
		builder.append(present_groupIds);
		if (present_groupIds)
			builder.append(groupIds);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkSendRequest(");
		boolean first = true;

		sb.append("workNumber:");
		if (this.workNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.workNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("userNumber:");
		if (this.userNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("groupIds:");
		if (this.groupIds == null) {
			sb.append("null");
		} else {
			sb.append(this.groupIds);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}