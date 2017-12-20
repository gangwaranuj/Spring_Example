package com.workmarket.search.request.user;

import com.workmarket.search.request.TrackableSearchRequest;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class GroupPeopleSearchRequest extends TrackableSearchRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long groupId;

	@Override
	public String getRequestType() {
		return GROUP_REQUEST;
	}

	public GroupPeopleSearchRequest() {
	}

	public long getGroupId() {
		return this.groupId;
	}

	public GroupPeopleSearchRequest setGroupId(long groupId) {
		this.groupId = groupId;
		return this;
	}

	public boolean isSetGroupId() {
		return (groupId > 0L);
	}

	public GroupPeopleSearchRequest setRequest(PeopleSearchRequest request) {
		super.setRequest(request);
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof GroupPeopleSearchRequest)
			return this.equals((GroupPeopleSearchRequest) that);
		return false;
	}

	private boolean equals(GroupPeopleSearchRequest that) {
		if (that == null)
			return false;

		boolean this_present_groupId = true;
		boolean that_present_groupId = true;
		if (this_present_groupId || that_present_groupId) {
			if (!(this_present_groupId && that_present_groupId))
				return false;
			if (this.groupId != that.groupId)
				return false;
		}

		boolean this_present_request = true && this.isSetRequest();
		boolean that_present_request = true && that.isSetRequest();
		if (this_present_request || that_present_request) {
			if (!(this_present_request && that_present_request))
				return false;
			if (!this.getRequest().equals(that.getRequest()))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_groupId = true;
		builder.append(present_groupId);
		if (present_groupId)
			builder.append(groupId);

		boolean present_request = true && (isSetRequest());
		builder.append(present_request);
		if (present_request)
			builder.append(getRequest());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("GroupPeopleSearchRequest(");
		boolean first = true;

		sb.append("groupId:");
		sb.append(this.groupId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("request:");
		if (this.getRequest() == null) {
			sb.append("null");
		} else {
			sb.append(this.getRequest());
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

