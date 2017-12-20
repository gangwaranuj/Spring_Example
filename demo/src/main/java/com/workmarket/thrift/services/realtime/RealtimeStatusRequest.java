package com.workmarket.thrift.services.realtime;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RealtimeStatusRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private RealtimePagination paginationRequest;
	private long companyId;
	private List<String> internalOwnerFilter;
	private RealtimeFilter filters;

	public RealtimeStatusRequest() {
	}

	public RealtimeStatusRequest(RealtimePagination paginationRequest, long companyId) {
		this();
		this.paginationRequest = paginationRequest;
		this.companyId = companyId;
	}

	public RealtimePagination getPaginationRequest() {
		return this.paginationRequest;
	}

	public RealtimeStatusRequest setPaginationRequest(RealtimePagination paginationRequest) {
		this.paginationRequest = paginationRequest;
		return this;
	}

	public boolean isSetPaginationRequest() {
		return this.paginationRequest != null;
	}

	public long getCompanyId() {
		return this.companyId;
	}

	public RealtimeStatusRequest setCompanyId(long companyId) {
		this.companyId = companyId;
		return this;
	}

	public boolean isSetCompanyId() {
		return (companyId > 0L);
	}

	public int getInternalOwnerFilterSize() {
		return (this.internalOwnerFilter == null) ? 0 : this.internalOwnerFilter.size();
	}

	public java.util.Iterator<String> getInternalOwnerFilterIterator() {
		return (this.internalOwnerFilter == null) ? null : this.internalOwnerFilter.iterator();
	}

	public void addToInternalOwnerFilter(String elem) {
		if (this.internalOwnerFilter == null) {
			this.internalOwnerFilter = new ArrayList<String>();
		}
		this.internalOwnerFilter.add(elem);
	}

	public List<String> getInternalOwnerFilter() {
		return this.internalOwnerFilter;
	}

	public RealtimeStatusRequest setInternalOwnerFilter(List<String> internalOwnerFilter) {
		this.internalOwnerFilter = internalOwnerFilter;
		return this;
	}

	public boolean isSetInternalOwnerFilter() {
		return this.internalOwnerFilter != null;
	}

	public RealtimeFilter getFilters() {
		return this.filters;
	}

	public RealtimeStatusRequest setFilters(RealtimeFilter filters) {
		this.filters = filters;
		return this;
	}

	public boolean isSetFilters() {
		return this.filters != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RealtimeStatusRequest)
			return this.equals((RealtimeStatusRequest) that);
		return false;
	}

	private boolean equals(RealtimeStatusRequest that) {
		if (that == null)
			return false;

		boolean this_present_paginationRequest = true && this.isSetPaginationRequest();
		boolean that_present_paginationRequest = true && that.isSetPaginationRequest();
		if (this_present_paginationRequest || that_present_paginationRequest) {
			if (!(this_present_paginationRequest && that_present_paginationRequest))
				return false;
			if (!this.paginationRequest.equals(that.paginationRequest))
				return false;
		}

		boolean this_present_companyId = true;
		boolean that_present_companyId = true;
		if (this_present_companyId || that_present_companyId) {
			if (!(this_present_companyId && that_present_companyId))
				return false;
			if (this.companyId != that.companyId)
				return false;
		}

		boolean this_present_internalOwnerFilter = true && this.isSetInternalOwnerFilter();
		boolean that_present_internalOwnerFilter = true && that.isSetInternalOwnerFilter();
		if (this_present_internalOwnerFilter || that_present_internalOwnerFilter) {
			if (!(this_present_internalOwnerFilter && that_present_internalOwnerFilter))
				return false;
			if (!this.internalOwnerFilter.equals(that.internalOwnerFilter))
				return false;
		}

		boolean this_present_filters = true && this.isSetFilters();
		boolean that_present_filters = true && that.isSetFilters();
		if (this_present_filters || that_present_filters) {
			if (!(this_present_filters && that_present_filters))
				return false;
			if (!this.filters.equals(that.filters))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_paginationRequest = true && (isSetPaginationRequest());
		builder.append(present_paginationRequest);
		if (present_paginationRequest)
			builder.append(paginationRequest);

		boolean present_companyId = true;
		builder.append(present_companyId);
		if (present_companyId)
			builder.append(companyId);

		boolean present_internalOwnerFilter = true && (isSetInternalOwnerFilter());
		builder.append(present_internalOwnerFilter);
		if (present_internalOwnerFilter)
			builder.append(internalOwnerFilter);

		boolean present_filters = true && (isSetFilters());
		builder.append(present_filters);
		if (present_filters)
			builder.append(filters);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RealtimeStatusRequest(");
		boolean first = true;

		sb.append("paginationRequest:");
		if (this.paginationRequest == null) {
			sb.append("null");
		} else {
			sb.append(this.paginationRequest);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("companyId:");
		sb.append(this.companyId);
		first = false;
		if (isSetInternalOwnerFilter()) {
			if (!first) sb.append(", ");
			sb.append("internalOwnerFilter:");
			if (this.internalOwnerFilter == null) {
				sb.append("null");
			} else {
				sb.append(this.internalOwnerFilter);
			}
			first = false;
		}
		if (isSetFilters()) {
			if (!first) sb.append(", ");
			sb.append("filters:");
			if (this.filters == null) {
				sb.append("null");
			} else {
				sb.append(this.filters);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}