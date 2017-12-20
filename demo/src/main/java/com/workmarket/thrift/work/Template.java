package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Template implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String description;
	private String workNumber;

	public Template() {
	}

	public Template(long id, String name, String description) {
		this();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public long getId() {
		return this.id;
	}

	public Template setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getName() {
		return this.name;
	}

	public Template setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public String getDescription() {
		return this.description;
	}

	public Template setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public Template setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}

	@Override
	public boolean equals(Object that) {
		return that != null && that instanceof Template && this.equals((Template) that);
	}

	private boolean equals(Template that) {
		if (that == null)
			return false;

		if (this.id != that.id)
			return false;

		boolean thisPresentName = this.isSetName();
		boolean thatPresentName = that.isSetName();
		if (thisPresentName || thatPresentName) {
			if (!(thisPresentName && thatPresentName))
				return false;
			if (!this.name.equals(that.name))
				return false;
		}

		boolean thisPresentDescription = this.isSetDescription();
		boolean thatPresentDescription = that.isSetDescription();
		if (thisPresentDescription || thatPresentDescription) {
			if (!(thisPresentDescription && thatPresentDescription))
				return false;
			if (!this.description.equals(that.description))
				return false;
		}

		boolean thisWorkNumber = this.isSetWorkNumber();
		boolean thatWorkNumber = that.isSetWorkNumber();
		if (thisWorkNumber || thatWorkNumber) {
			if (!this.workNumber.equals(that.workNumber))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		builder.append(id);

		boolean present_name = (isSetName());
		builder.append(present_name);
		if (present_name)
			builder.append(name);

		boolean present_description = isSetDescription();
		builder.append(present_description);
		if (present_description)
			builder.append(description);

		boolean workNumberSet = isSetWorkNumber();
		builder.append(workNumberSet);
		if (workNumberSet)
			builder.append(workNumber);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Template(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("name:");
		if (this.name == null) {
			sb.append("null");
		} else {
			sb.append(this.name);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("description:");
		if (this.description == null) {
			sb.append("null");
		} else {
			sb.append(this.description);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("work_number:");
		if (this.workNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.workNumber);
		}
		sb.append(")");
		return sb.toString();
	}
}