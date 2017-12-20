package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class FieldType implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;
	private String description;
	private int order;

	public FieldType() {
	}

	public FieldType(String code, String description, int order) {
		this();
		this.code = code;
		this.description = description;
		this.order = order;
	}

	public String getCode() {
		return this.code;
	}

	public FieldType setCode(String code) {
		this.code = code;
		return this;
	}

	public boolean isSetCode() {
		return this.code != null;
	}

	public String getDescription() {
		return this.description;
	}

	public FieldType setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	public int getOrder() {
		return this.order;
	}

	public FieldType setOrder(int order) {
		this.order = order;
		return this;
	}

	public boolean isSetOrder() {
		return (order > 0);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof FieldType)
			return this.equals((FieldType) that);
		return false;
	}

	private boolean equals(FieldType that) {
		if (that == null)
			return false;

		boolean this_present_code = true && this.isSetCode();
		boolean that_present_code = true && that.isSetCode();
		if (this_present_code || that_present_code) {
			if (!(this_present_code && that_present_code))
				return false;
			if (!this.code.equals(that.code))
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

		boolean this_present_order = true;
		boolean that_present_order = true;
		if (this_present_order || that_present_order) {
			if (!(this_present_order && that_present_order))
				return false;
			if (this.order != that.order)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_code = true && (isSetCode());
		builder.append(present_code);
		if (present_code)
			builder.append(code);

		boolean present_description = true && (isSetDescription());
		builder.append(present_description);
		if (present_description)
			builder.append(description);

		boolean present_order = true;
		builder.append(present_order);
		if (present_order)
			builder.append(order);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("FieldType(");
		boolean first = true;

		sb.append("code:");
		if (this.code == null) {
			sb.append("null");
		} else {
			sb.append(this.code);
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
		sb.append("order:");
		sb.append(this.order);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}