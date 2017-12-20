package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class NotificationTypeConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;

	private NotificationType type;
	private int days;

	public NotificationTypeConfiguration() {
	}

	public NotificationTypeConfiguration(NotificationType type) {
		this();
		this.type = type;
	}

	public NotificationType getType() {
		return this.type;
	}

	public NotificationTypeConfiguration setType(NotificationType type) {
		this.type = type;
		return this;
	}

	public void unsetType() {
		this.type = null;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	public void setTypeIsSet(boolean value) {
		if (!value) {
			this.type = null;
		}
	}

	public int getDays() {
		return this.days;
	}

	public NotificationTypeConfiguration setDays(int days) {
		this.days = days;
		return this;
	}

	public boolean isSetDays() {
		return (days > 0);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof NotificationTypeConfiguration)
			return this.equals((NotificationTypeConfiguration) that);
		return false;
	}

	private boolean equals(NotificationTypeConfiguration that) {
		if (that == null)
			return false;

		boolean this_present_type = true && this.isSetType();
		boolean that_present_type = true && that.isSetType();
		if (this_present_type || that_present_type) {
			if (!(this_present_type && that_present_type))
				return false;
			if (!this.type.equals(that.type))
				return false;
		}

		boolean this_present_days = true && this.isSetDays();
		boolean that_present_days = true && that.isSetDays();
		if (this_present_days || that_present_days) {
			if (!(this_present_days && that_present_days))
				return false;
			if (this.days != that.days)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_type = true && (isSetType());
		builder.append(present_type);
		if (present_type)
			builder.append(type.getValue());

		boolean present_days = true && (isSetDays());
		builder.append(present_days);
		if (present_days)
			builder.append(days);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("NotificationTypeConfiguration(");
		boolean first = true;

		sb.append("type:");
		if (this.type == null) {
			sb.append("null");
		} else {
			sb.append(this.type);
		}
		first = false;
		if (isSetDays()) {
			if (!first) sb.append(", ");
			sb.append("days:");
			sb.append(this.days);
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}
