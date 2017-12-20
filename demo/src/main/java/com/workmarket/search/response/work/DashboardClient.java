package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class DashboardClient implements Serializable {
	private static final long serialVersionUID = 1L;

	private long clientId;
	private String clientName;

	public DashboardClient() {
	}

	public DashboardClient(long clientId, String clientName) {
		this();
		this.clientId = clientId;
		this.clientName = clientName;
	}

	public long getClientId() {
		return this.clientId;
	}

	public DashboardClient setClientId(long clientId) {
		this.clientId = clientId;
		return this;
	}

	public boolean isSetClientId() {
		return (clientId > 0L);
	}

	public String getClientName() {
		return this.clientName;
	}

	public DashboardClient setClientName(String clientName) {
		this.clientName = clientName;
		return this;
	}

	public boolean isSetClientName() {
		return this.clientName != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DashboardClient)
			return this.equals((DashboardClient) that);
		return false;
	}

	private boolean equals(DashboardClient that) {
		if (that == null)
			return false;

		boolean this_present_clientId = true;
		boolean that_present_clientId = true;
		if (this_present_clientId || that_present_clientId) {
			if (!(this_present_clientId && that_present_clientId))
				return false;
			if (this.clientId != that.clientId)
				return false;
		}

		boolean this_present_clientName = true && this.isSetClientName();
		boolean that_present_clientName = true && that.isSetClientName();
		if (this_present_clientName || that_present_clientName) {
			if (!(this_present_clientName && that_present_clientName))
				return false;
			if (!this.clientName.equals(that.clientName))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_clientId = true;
		builder.append(present_clientId);
		if (present_clientId)
			builder.append(clientId);

		boolean present_clientName = true && (isSetClientName());
		builder.append(present_clientName);
		if (present_clientName)
			builder.append(clientName);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashboardClient(");
		boolean first = true;

		sb.append("clientId:");
		sb.append(this.clientId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("clientName:");
		if (this.clientName == null) {
			sb.append("null");
		} else {
			sb.append(this.clientName);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}