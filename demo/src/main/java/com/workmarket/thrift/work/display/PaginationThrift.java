package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PaginationThrift implements Serializable {
	private static final long serialVersionUID = 1L;

	private int total;
	private List<PaginationPageThrift> paginationPageThrifts;

	public PaginationThrift() {
	}

	public PaginationThrift(int total, List<PaginationPageThrift> paginationPageThrifts) {
		this();
		this.total = total;
		this.paginationPageThrifts = paginationPageThrifts;
	}

	public int getTotal() {
		return this.total;
	}

	public PaginationThrift setTotal(int total) {
		this.total = total;
		return this;
	}

	public int getPaginationPageThriftsSize() {
		return (this.paginationPageThrifts == null) ? 0 : this.paginationPageThrifts.size();
	}

	public java.util.Iterator<PaginationPageThrift> getPaginationPageThriftsIterator() {
		return (this.paginationPageThrifts == null) ? null : this.paginationPageThrifts.iterator();
	}

	public void addToPaginationPageThrifts(PaginationPageThrift elem) {
		if (this.paginationPageThrifts == null) {
			this.paginationPageThrifts = new ArrayList<PaginationPageThrift>();
		}
		this.paginationPageThrifts.add(elem);
	}

	public List<PaginationPageThrift> getPaginationPageThrifts() {
		return this.paginationPageThrifts;
	}

	public PaginationThrift setPaginationPageThrifts(List<PaginationPageThrift> paginationPageThrifts) {
		this.paginationPageThrifts = paginationPageThrifts;
		return this;
	}

	public boolean isSetPaginationPageThrifts() {
		return this.paginationPageThrifts != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof PaginationThrift)
			return this.equals((PaginationThrift) that);
		return false;
	}

	private boolean equals(PaginationThrift that) {
		if (that == null)
			return false;

		boolean this_present_total = true;
		boolean that_present_total = true;
		if (this_present_total || that_present_total) {
			if (!(this_present_total && that_present_total))
				return false;
			if (this.total != that.total)
				return false;
		}

		boolean this_present_paginationPageThrifts = true && this.isSetPaginationPageThrifts();
		boolean that_present_paginationPageThrifts = true && that.isSetPaginationPageThrifts();
		if (this_present_paginationPageThrifts || that_present_paginationPageThrifts) {
			if (!(this_present_paginationPageThrifts && that_present_paginationPageThrifts))
				return false;
			if (!this.paginationPageThrifts.equals(that.paginationPageThrifts))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_total = true;
		builder.append(present_total);
		if (present_total)
			builder.append(total);

		boolean present_paginationPageThrifts = true && (isSetPaginationPageThrifts());
		builder.append(present_paginationPageThrifts);
		if (present_paginationPageThrifts)
			builder.append(paginationPageThrifts);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("PaginationThrift(");
		boolean first = true;

		sb.append("total:");
		sb.append(this.total);
		first = false;
		if (!first) sb.append(", ");
		sb.append("paginationPageThrifts:");
		if (this.paginationPageThrifts == null) {
			sb.append("null");
		} else {
			sb.append(this.paginationPageThrifts);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

