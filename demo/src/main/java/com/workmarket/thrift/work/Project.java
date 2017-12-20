package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Project implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String description;

	public Project() {
	}

	public Project(long id, String name, String description) {
		this();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public long getId() {
		return this.id;
	}

	public Project setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getName() {
		return this.name;
	}

	public Project setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public String getDescription() {
		return this.description;
	}

	public Project setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Project)
			return this.equals((Project) that);
		return false;
	}

	private boolean equals(Project that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_name = true && this.isSetName();
		boolean that_present_name = true && that.isSetName();
		if (this_present_name || that_present_name) {
			if (!(this_present_name && that_present_name))
				return false;
			if (!this.name.equals(that.name))
				return false;
		}

		boolean this_present_description = true && this.isSetDescription();
		boolean that_present_description = true && that.isSetDescription();
		if (this_present_description || that_present_description) {
			if (!(this_present_description && that_present_description))
				return false;
			if (!this.description.equals(that.description))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_name = true && (isSetName());
		builder.append(present_name);
		if (present_name)
			builder.append(name);

		boolean present_description = true && (isSetDescription());
		builder.append(present_description);
		if (present_description)
			builder.append(description);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Project(");
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
		sb.append(")");
		return sb.toString();
	}
}