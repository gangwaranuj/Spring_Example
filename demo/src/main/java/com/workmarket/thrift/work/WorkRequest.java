package com.workmarket.thrift.work;

import com.workmarket.domains.model.audit.ViewType;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class WorkRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private long workId;
	private String workNumber;
	private Set<WorkRequestInfo> includes;
	private ViewType viewType = ViewType.WEB;

	public WorkRequest() {
	}

	public WorkRequest(long userId, long workId, String workNumber) {
		this();
		this.userId = userId;
		this.workId = workId;
		this.workNumber = workNumber;
	}

	public WorkRequest(long userId, String workNumber, Set<WorkRequestInfo> includes) {
		this.userId = userId;
		this.workNumber = workNumber;
		this.includes = includes;
	}

	public long getUserId() {
		return this.userId;
	}

	public WorkRequest setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public boolean isSetUserId() {
		return (userId > 0L);
	}

	public long getWorkId() {
		return this.workId;
	}

	public WorkRequest setWorkId(long workId) {
		this.workId = workId;
		return this;
	}

	public boolean isSetWorkId() {
		return (workId > 0L);
	}

	public String getWorkNumber() {
		return this.workNumber;
	}

	public WorkRequest setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}

	public int getIncludesSize() {
		return (this.includes == null) ? 0 : this.includes.size();
	}

	public java.util.Iterator<WorkRequestInfo> getIncludesIterator() {
		return (this.includes == null) ? null : this.includes.iterator();
	}

	public void addToIncludes(WorkRequestInfo elem) {
		if (this.includes == null) {
			this.includes = new HashSet<WorkRequestInfo>();
		}
		this.includes.add(elem);
	}

	public Set<WorkRequestInfo> getIncludes() {
		return this.includes;
	}

	public WorkRequest setIncludes(Set<WorkRequestInfo> includes) {
		this.includes = includes;
		return this;
	}

	public boolean isSetIncludes() {
		return this.includes != null;
	}

	public ViewType getViewType() {
		return viewType;
	}

	public WorkRequest setViewType(ViewType viewType) {
		this.viewType = viewType;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkRequest)
			return this.equals((WorkRequest) that);
		return false;
	}

	private boolean equals(WorkRequest that) {
		if (that == null)
			return false;

		boolean this_present_userId = true;
		boolean that_present_userId = true;
		if (this_present_userId || that_present_userId) {
			if (!(this_present_userId && that_present_userId))
				return false;
			if (this.userId != that.userId)
				return false;
		}

		boolean this_present_workId = true;
		boolean that_present_workId = true;
		if (this_present_workId || that_present_workId) {
			if (!(this_present_workId && that_present_workId))
				return false;
			if (this.workId != that.workId)
				return false;
		}

		boolean this_present_workNumber = true && this.isSetWorkNumber();
		boolean that_present_workNumber = true && that.isSetWorkNumber();
		if (this_present_workNumber || that_present_workNumber) {
			if (!(this_present_workNumber && that_present_workNumber))
				return false;
			if (!this.workNumber.equals(that.workNumber))
				return false;
		}

		boolean this_present_includes = true && this.isSetIncludes();
		boolean that_present_includes = true && that.isSetIncludes();
		if (this_present_includes || that_present_includes) {
			if (!(this_present_includes && that_present_includes))
				return false;
			if (!this.includes.equals(that.includes))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_userId = true;
		builder.append(present_userId);
		if (present_userId)
			builder.append(userId);

		boolean present_workId = true;
		builder.append(present_workId);
		if (present_workId)
			builder.append(workId);

		boolean present_workNumber = true && (isSetWorkNumber());
		builder.append(present_workNumber);
		if (present_workNumber)
			builder.append(workNumber);

		boolean present_includes = true && (isSetIncludes());
		builder.append(present_includes);
		if (present_includes)
			builder.append(includes);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkRequest(");
		boolean first = true;

		sb.append("userId:");
		sb.append(this.userId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("workId:");
		sb.append(this.workId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("workNumber:");
		if (this.workNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.workNumber);
		}
		first = false;
		if (isSetIncludes()) {
			if (!first) sb.append(", ");
			sb.append("includes:");
			if (this.includes == null) {
				sb.append("null");
			} else {
				sb.append(this.includes);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}
