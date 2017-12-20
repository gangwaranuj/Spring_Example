package com.workmarket.thrift.services.realtime;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class RealtimeDropDownOption implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;

	public RealtimeDropDownOption() {
	}

	public RealtimeDropDownOption(String id, String name) {
		this();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return this.id;
	}

	public RealtimeDropDownOption setId(String id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return this.id != null;
	}

	public String getName() {
		return this.name;
	}

	public RealtimeDropDownOption setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RealtimeDropDownOption)
			return this.equals((RealtimeDropDownOption) that);
		return false;
	}

	private boolean equals(RealtimeDropDownOption that) {
		if (that == null)
			return false;

		boolean this_present_id = true && this.isSetId();
		boolean that_present_id = true && that.isSetId();
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (!this.id.equals(that.id))
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

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true && (isSetId());
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_name = true && (isSetName());
		builder.append(present_name);
		if (present_name)
			builder.append(name);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RealtimeDropDownOption(");
		boolean first = true;

		sb.append("id:");
		if (this.id == null) {
			sb.append("null");
		} else {
			sb.append(this.id);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("name:");
		if (this.name == null) {
			sb.append("null");
		} else {
			sb.append(this.name);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}