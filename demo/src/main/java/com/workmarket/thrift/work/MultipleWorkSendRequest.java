package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MultipleWorkSendRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<WorkSendRequest> requests;

	public MultipleWorkSendRequest() {
	}

	public int getRequestsSize() {
		return (this.requests == null) ? 0 : this.requests.size();
	}

	public void addToRequests(WorkSendRequest elem) {
		if (this.requests == null) {
			this.requests = new ArrayList<>();
		}
		this.requests.add(elem);
	}

	public List<WorkSendRequest> getRequests() {
		return this.requests;
	}

	public MultipleWorkSendRequest setRequests(List<WorkSendRequest> requests) {
		this.requests = requests;
		return this;
	}

	public boolean isSetRequests() {
		return this.requests != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof MultipleWorkSendRequest)
			return this.equals((MultipleWorkSendRequest) that);
		return false;
	}

	private boolean equals(MultipleWorkSendRequest that) {
		if (that == null)
			return false;

		boolean this_present_requests = true && this.isSetRequests();
		boolean that_present_requests = true && that.isSetRequests();
		if (this_present_requests || that_present_requests) {
			if (!(this_present_requests && that_present_requests))
				return false;
			if (!this.requests.equals(that.requests))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_requests = true && (isSetRequests());
		builder.append(present_requests);
		if (present_requests)
			builder.append(requests);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("MultipleWorkSendRequest(");
		boolean first = true;

		sb.append("requests:");
		if (this.requests == null) {
			sb.append("null");
		} else {
			sb.append(this.requests);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
