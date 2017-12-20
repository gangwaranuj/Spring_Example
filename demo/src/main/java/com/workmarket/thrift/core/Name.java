package com.workmarket.thrift.core;

import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Name implements Serializable {
	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;

	public Name() {
	}

	public Name(String firstName, String lastName) {
		this();
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public Name setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public boolean isSetFirstName() {
		return this.firstName != null;
	}

	public String getLastName() {
		return this.lastName;
	}

	public Name setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public boolean isSetLastName() {
		return this.lastName != null;
	}

	public String getFullName() {
		return StringUtilities.fullName(firstName, lastName);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Name)
			return this.equals((Name) that);
		return false;
	}

	private boolean equals(Name that) {
		if (that == null)
			return false;

		boolean this_present_firstName = true && this.isSetFirstName();
		boolean that_present_firstName = true && that.isSetFirstName();
		if (this_present_firstName || that_present_firstName) {
			if (!(this_present_firstName && that_present_firstName))
				return false;
			if (!this.firstName.equals(that.firstName))
				return false;
		}

		boolean this_present_lastName = true && this.isSetLastName();
		boolean that_present_lastName = true && that.isSetLastName();
		if (this_present_lastName || that_present_lastName) {
			if (!(this_present_lastName && that_present_lastName))
				return false;
			if (!this.lastName.equals(that.lastName))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_firstName = true && (isSetFirstName());
		builder.append(present_firstName);
		if (present_firstName)
			builder.append(firstName);

		boolean present_lastName = true && (isSetLastName());
		builder.append(present_lastName);
		if (present_lastName)
			builder.append(lastName);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Name(");
		boolean first = true;

		sb.append("firstName:");
		if (this.firstName == null) {
			sb.append("null");
		} else {
			sb.append(this.firstName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("lastName:");
		if (this.lastName == null) {
			sb.append("null");
		} else {
			sb.append(this.lastName);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

