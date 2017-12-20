package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class PricingLogEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	private long timestamp;
	private PricingStrategy pricing;

	public PricingLogEntry() {
	}

	public PricingLogEntry(
			long timestamp,
			PricingStrategy pricing) {
		this();
		this.timestamp = timestamp;
		this.pricing = pricing;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public PricingLogEntry setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public boolean isSetTimestamp() {
		return (timestamp > 0L);
	}

	public PricingStrategy getPricing() {
		return this.pricing;
	}

	public PricingLogEntry setPricing(PricingStrategy pricing) {
		this.pricing = pricing;
		return this;
	}

	public boolean isSetPricing() {
		return this.pricing != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof PricingLogEntry)
			return this.equals((PricingLogEntry) that);
		return false;
	}

	private boolean equals(PricingLogEntry that) {
		if (that == null)
			return false;

		boolean this_present_timestamp = true;
		boolean that_present_timestamp = true;
		if (this_present_timestamp || that_present_timestamp) {
			if (!(this_present_timestamp && that_present_timestamp))
				return false;
			if (this.timestamp != that.timestamp)
				return false;
		}

		boolean this_present_pricing = true && this.isSetPricing();
		boolean that_present_pricing = true && that.isSetPricing();
		if (this_present_pricing || that_present_pricing) {
			if (!(this_present_pricing && that_present_pricing))
				return false;
			if (!this.pricing.equals(that.pricing))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_timestamp = true;
		builder.append(present_timestamp);
		if (present_timestamp)
			builder.append(timestamp);

		boolean present_pricing = true && (isSetPricing());
		builder.append(present_pricing);
		if (present_pricing)
			builder.append(pricing);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("PricingLogEntry(");
		boolean first = true;

		sb.append("timestamp:");
		sb.append(this.timestamp);
		first = false;
		if (!first) sb.append(", ");
		sb.append("pricing:");
		if (this.pricing == null) {
			sb.append("null");
		} else {
			sb.append(this.pricing);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}