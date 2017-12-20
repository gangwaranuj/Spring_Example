package com.workmarket.search.request.user;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class License implements Serializable {
	private static final long serialVersionUID = 1L;

	private String state;
	private long licenseId;

	public License() {
	}

	public License(String state, long licenseId) {
		this();
		this.state = state;
		this.licenseId = licenseId;
	}

	public String getState() {
		return this.state;
	}

	public License setState(String state) {
		this.state = state;
		return this;
	}

	public boolean isSetState() {
		return this.state != null;
	}

	public long getLicenseId() {
		return this.licenseId;
	}

	public License setLicenseId(long licenseId) {
		this.licenseId = licenseId;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof License)
			return this.equals((License) that);
		return false;
	}

	private boolean equals(License that) {
		if (that == null)
			return false;

		boolean this_present_state = true && this.isSetState();
		boolean that_present_state = true && that.isSetState();
		if (this_present_state || that_present_state) {
			if (!(this_present_state && that_present_state))
				return false;
			if (!this.state.equals(that.state))
				return false;
		}

		boolean this_present_licenseId = true;
		boolean that_present_licenseId = true;
		if (this_present_licenseId || that_present_licenseId) {
			if (!(this_present_licenseId && that_present_licenseId))
				return false;
			if (this.licenseId != that.licenseId)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_state = true && (isSetState());
		builder.append(present_state);
		if (present_state)
			builder.append(state);

		boolean present_licenseId = true;
		builder.append(present_licenseId);
		if (present_licenseId)
			builder.append(licenseId);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("License(");
		boolean first = true;

		sb.append("state:");
		if (this.state == null) {
			sb.append("null");
		} else {
			sb.append(this.state);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("licenseId:");
		sb.append(this.licenseId);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

