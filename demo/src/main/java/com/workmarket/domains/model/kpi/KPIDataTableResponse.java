package com.workmarket.domains.model.kpi;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KPIDataTableResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<KPIAggregateEntityTable> tabularData;

	public KPIDataTableResponse() {
	}

	public KPIDataTableResponse(List<KPIAggregateEntityTable> tabularData) {
		this();
		this.tabularData = tabularData;
	}

	public int getTabularDataSize() {
		return (this.tabularData == null) ? 0 : this.tabularData.size();
	}

	public java.util.Iterator<KPIAggregateEntityTable> getTabularDataIterator() {
		return (this.tabularData == null) ? null : this.tabularData.iterator();
	}

	public void addToTabularData(KPIAggregateEntityTable elem) {
		if (this.tabularData == null) {
			this.tabularData = new ArrayList<KPIAggregateEntityTable>();
		}
		this.tabularData.add(elem);
	}

	public List<KPIAggregateEntityTable> getTabularData() {
		return this.tabularData;
	}

	public KPIDataTableResponse setTabularData(List<KPIAggregateEntityTable> tabularData) {
		this.tabularData = tabularData;
		return this;
	}

	public boolean isSetTabularData() {
		return this.tabularData != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof KPIDataTableResponse)
			return this.equals((KPIDataTableResponse) that);
		return false;
	}

	private boolean equals(KPIDataTableResponse that) {
		if (that == null)
			return false;

		boolean this_present_tabularData = true && this.isSetTabularData();
		boolean that_present_tabularData = true && that.isSetTabularData();
		if (this_present_tabularData || that_present_tabularData) {
			if (!(this_present_tabularData && that_present_tabularData))
				return false;
			if (!this.tabularData.equals(that.tabularData))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_tabularData = true && (isSetTabularData());
		builder.append(present_tabularData);
		if (present_tabularData)
			builder.append(tabularData);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("KPIDataTableResponse(");
		boolean first = true;

		sb.append("tabularData:");
		if (this.tabularData == null) {
			sb.append("null");
		} else {
			sb.append(this.tabularData);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}