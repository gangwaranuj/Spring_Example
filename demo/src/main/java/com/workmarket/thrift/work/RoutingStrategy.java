package com.workmarket.thrift.work;

import com.workmarket.search.request.user.PeopleSearchRequest;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Set;

public class RoutingStrategy implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private PeopleSearchRequest filter;
	private int delayMinutes;
	private com.workmarket.thrift.core.Status status;
	private long routedOn;
	private RoutingStrategySummary summary;
	private Set<String> routingUserNumbers;
	private Set<String> vendorCompanyNumbers;
	private boolean assignToFirstToAccept;

	public RoutingStrategy() {
	}

	public long getId() {
		return this.id;
	}

	public RoutingStrategy setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public PeopleSearchRequest getFilter() {
		return this.filter;
	}

	public RoutingStrategy setFilter(PeopleSearchRequest filter) {
		this.filter = filter;
		return this;
	}

	public boolean isSetFilter() {
		return this.filter != null;
	}

	public int getDelayMinutes() {
		return this.delayMinutes;
	}

	public RoutingStrategy setDelayMinutes(int delayMinutes) {
		this.delayMinutes = delayMinutes;
		return this;
	}

	public boolean isSetDelayMinutes() {
		return (delayMinutes > 0);
	}

	public com.workmarket.thrift.core.Status getStatus() {
		return this.status;
	}

	public RoutingStrategy setStatus(com.workmarket.thrift.core.Status status) {
		this.status = status;
		return this;
	}

	public boolean isSetStatus() {
		return this.status != null;
	}

	public long getRoutedOn() {
		return this.routedOn;
	}

	public RoutingStrategy setRoutedOn(long routedOn) {
		this.routedOn = routedOn;
		return this;
	}

	public boolean isSetRoutedOn() {
		return (routedOn > 0L);
	}

	public RoutingStrategySummary getSummary() {
		return this.summary;
	}

	public RoutingStrategy setSummary(RoutingStrategySummary summary) {
		this.summary = summary;
		return this;
	}

	public boolean isSetSummary() {
		return this.summary != null;
	}

	public Set<String> getRoutingUserNumbers() {
		return routingUserNumbers;
	}

	public RoutingStrategy setRoutingUserNumbers(Set<String> routingUserNumbers) {
		this.routingUserNumbers = routingUserNumbers;
		return this;
	}

	public Set<String> getVendorCompanyNumbers() {
		return vendorCompanyNumbers;
	}

	public RoutingStrategy setVendorCompanyNumbers(Set<String> vendorCompanyNumbers) {
		this.vendorCompanyNumbers = vendorCompanyNumbers;
		return this;
	}

	public boolean isAssignToFirstToAccept() {
		return assignToFirstToAccept;
	}

	public RoutingStrategy setAssignToFirstToAccept(boolean assignToFirstToAccept) {
		this.assignToFirstToAccept = assignToFirstToAccept;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RoutingStrategy)
			return this.equals((RoutingStrategy) that);
		return false;
	}

	private boolean equals(RoutingStrategy that) {
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

		boolean this_present_filter = true && this.isSetFilter();
		boolean that_present_filter = true && that.isSetFilter();
		if (this_present_filter || that_present_filter) {
			if (!(this_present_filter && that_present_filter))
				return false;
			if (!this.filter.equals(that.filter))
				return false;
		}

		boolean this_present_delayMinutes = true;
		boolean that_present_delayMinutes = true;
		if (this_present_delayMinutes || that_present_delayMinutes) {
			if (!(this_present_delayMinutes && that_present_delayMinutes))
				return false;
			if (this.delayMinutes != that.delayMinutes)
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

		boolean this_present_routedOn = true;
		boolean that_present_routedOn = true;
		if (this_present_routedOn || that_present_routedOn) {
			if (!(this_present_routedOn && that_present_routedOn))
				return false;
			if (this.routedOn != that.routedOn)
				return false;
		}

		boolean this_present_summary = true && this.isSetSummary();
		boolean that_present_summary = true && that.isSetSummary();
		if (this_present_summary || that_present_summary) {
			if (!(this_present_summary && that_present_summary))
				return false;
			if (!this.summary.equals(that.summary))
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

		boolean present_filter = true && (isSetFilter());
		builder.append(present_filter);
		if (present_filter)
			builder.append(filter);

		boolean present_delayMinutes = true;
		builder.append(present_delayMinutes);
		if (present_delayMinutes)
			builder.append(delayMinutes);

		boolean present_status = true && (isSetStatus());
		builder.append(present_status);
		if (present_status)
			builder.append(status);

		boolean present_routedOn = true;
		builder.append(present_routedOn);
		if (present_routedOn)
			builder.append(routedOn);

		boolean present_summary = true && (isSetSummary());
		builder.append(present_summary);
		if (present_summary)
			builder.append(summary);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RoutingStrategy(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("filter:");
		if (this.filter == null) {
			sb.append("null");
		} else {
			sb.append(this.filter);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("delayMinutes:");
		sb.append(this.delayMinutes);
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
		sb.append("routedOn:");
		sb.append(this.routedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("summary:");
		if (this.summary == null) {
			sb.append("null");
		} else {
			sb.append(this.summary);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}