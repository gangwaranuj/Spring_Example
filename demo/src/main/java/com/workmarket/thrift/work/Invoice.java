package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Invoice implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String number;
	private com.workmarket.thrift.core.Status status;
	private boolean bundled;
	private boolean editable;

	public Invoice() {
	}

	public Invoice(
			long id,
			String number,
			com.workmarket.thrift.core.Status status,
			boolean bundled,
			boolean editable) {
		this();
		this.id = id;
		this.number = number;
		this.status = status;
		this.bundled = bundled;
		this.editable = editable;
	}

	public long getId() {
		return this.id;
	}

	public Invoice setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getNumber() {
		return this.number;
	}

	public Invoice setNumber(String number) {
		this.number = number;
		return this;
	}

	public boolean isSetNumber() {
		return this.number != null;
	}

	public com.workmarket.thrift.core.Status getStatus() {
		return this.status;
	}

	public Invoice setStatus(com.workmarket.thrift.core.Status status) {
		this.status = status;
		return this;
	}

	public boolean isSetStatus() {
		return this.status != null;
	}

	public boolean isBundled() {
		return this.bundled;
	}

	public Invoice setBundled(boolean bundled) {
		this.bundled = bundled;
		return this;
	}

	public boolean isEditable() {
		return editable;
	}

	public Invoice setEditable(boolean editable) {
		this.editable = editable;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Invoice)
			return this.equals((Invoice) that);
		return false;
	}

	private boolean equals(Invoice that) {
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

		boolean this_present_number = true && this.isSetNumber();
		boolean that_present_number = true && that.isSetNumber();
		if (this_present_number || that_present_number) {
			if (!(this_present_number && that_present_number))
				return false;
			if (!this.number.equals(that.number))
				return false;
		}

		boolean this_present_status = true && this.isSetStatus();
		boolean that_present_status = true && that.isSetStatus();
		if (this_present_status || that_present_status) {
			if (!(this_present_status && that_present_status))
				return false;
			if (!this.status.equals(that.status))
				return false;
		}

		boolean this_present_bundled = true;
		boolean that_present_bundled = true;
		if (this_present_bundled || that_present_bundled) {
			if (!(this_present_bundled && that_present_bundled))
				return false;
			if (this.bundled != that.bundled)
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

		boolean present_number = true && (isSetNumber());
		builder.append(present_number);
		if (present_number)
			builder.append(number);

		boolean present_status = true && (isSetStatus());
		builder.append(present_status);
		if (present_status)
			builder.append(status);

		boolean present_bundled = true;
		builder.append(present_bundled);
		if (present_bundled)
			builder.append(bundled);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Invoice(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("number:");
		if (this.number == null) {
			sb.append("null");
		} else {
			sb.append(this.number);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("status:");
		if (this.status == null) {
			sb.append("null");
		} else {
			sb.append(this.status);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("bundled:");
		sb.append(this.bundled);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}