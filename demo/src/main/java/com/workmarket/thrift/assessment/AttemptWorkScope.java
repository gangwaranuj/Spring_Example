package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class AttemptWorkScope implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String workNumber;
	private String title;

	public AttemptWorkScope() {
	}

	public AttemptWorkScope(String workNumber, String title) {
		this();
		this.workNumber = workNumber;
		this.title = title;
	}

	public long getId() {
		return this.id;
	}

	public AttemptWorkScope setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getWorkNumber() {
		return this.workNumber;
	}

	public AttemptWorkScope setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}

	public String getTitle() {
		return this.title;
	}

	public AttemptWorkScope setTitle(String title) {
		this.title = title;
		return this;
	}

	public boolean isSetTitle() {
		return this.title != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof AttemptWorkScope)
			return this.equals((AttemptWorkScope) that);
		return false;
	}

	private boolean equals(AttemptWorkScope that) {
		if (that == null)
			return false;

		boolean this_present_id = true && this.isSetId();
		boolean that_present_id = true && that.isSetId();
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
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

		boolean this_present_title = true && this.isSetTitle();
		boolean that_present_title = true && that.isSetTitle();
		if (this_present_title || that_present_title) {
			if (!(this_present_title && that_present_title))
				return false;
			if (!this.title.equals(that.title))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true && (isSetId());
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_workNumber = true && (isSetWorkNumber());
		builder.append(present_workNumber);
		if (present_workNumber)
			builder.append(workNumber);

		boolean present_title = true && (isSetTitle());
		builder.append(present_title);
		if (present_title)
			builder.append(title);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AttemptWorkScope(");
		boolean first = true;

		if (isSetId()) {
			sb.append("id:");
			sb.append(this.id);
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("workNumber:");
		if (this.workNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.workNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("title:");
		if (this.title == null) {
			sb.append("null");
		} else {
			sb.append(this.title);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

