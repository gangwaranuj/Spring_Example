package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class DashboardBuyer implements Serializable {
	private static final long serialVersionUID = 1L;

	private String buyerFullName;
	private long buyerId;

	public DashboardBuyer() {
	}

	public DashboardBuyer(String buyerFullName, long buyerId) {
		this();
		this.buyerFullName = buyerFullName;
		this.buyerId = buyerId;
	}

	public String getBuyerFullName() {
		return this.buyerFullName;
	}

	public DashboardBuyer setBuyerFullName(String buyerFullName) {
		this.buyerFullName = buyerFullName;
		return this;
	}

	public boolean isSetBuyerFullName() {
		return this.buyerFullName != null;
	}

	public long getBuyerId() {
		return this.buyerId;
	}

	public DashboardBuyer setBuyerId(long buyerId) {
		this.buyerId = buyerId;
		return this;
	}

	public boolean isSetBuyerId() {
		return (buyerId > 0L);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DashboardBuyer)
			return this.equals((DashboardBuyer) that);
		return false;
	}

	private boolean equals(DashboardBuyer that) {
		if (that == null)
			return false;

		boolean this_present_buyerFullName = true && this.isSetBuyerFullName();
		boolean that_present_buyerFullName = true && that.isSetBuyerFullName();
		if (this_present_buyerFullName || that_present_buyerFullName) {
			if (!(this_present_buyerFullName && that_present_buyerFullName))
				return false;
			if (!this.buyerFullName.equals(that.buyerFullName))
				return false;
		}

		boolean this_present_buyerId = true;
		boolean that_present_buyerId = true;
		if (this_present_buyerId || that_present_buyerId) {
			if (!(this_present_buyerId && that_present_buyerId))
				return false;
			if (this.buyerId != that.buyerId)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_buyerFullName = true && (isSetBuyerFullName());
		builder.append(present_buyerFullName);
		if (present_buyerFullName)
			builder.append(buyerFullName);

		boolean present_buyerId = true;
		builder.append(present_buyerId);
		if (present_buyerId)
			builder.append(buyerId);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashboardBuyer(");
		boolean first = true;

		sb.append("buyerFullName:");
		if (this.buyerFullName == null) {
			sb.append("null");
		} else {
			sb.append(this.buyerFullName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("buyerId:");
		sb.append(this.buyerId);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

