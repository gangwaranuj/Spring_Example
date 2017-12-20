package com.workmarket.domains.model.kpi;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Filter implements Serializable {

	private static final long serialVersionUID = 7750040893225850828L;

	private KPIReportFilter name;
	private List<String> values;

	public Filter() {}

	public Filter(KPIReportFilter name, List<String> values) {
		this.name = name;
		this.values = values;
	}

	public KPIReportFilter getName() {
		return this.name;
	}

	public Filter setName(KPIReportFilter name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public int getValuesSize() {
		return (this.values == null) ? 0 : this.values.size();
	}

	public java.util.Iterator<String> getValuesIterator() {
		return (this.values == null) ? null : this.values.iterator();
	}

	public void addToValues(String elem) {
		if (this.values == null) {
			this.values = new ArrayList<>();
		}
		this.values.add(elem);
	}

	public List<String> getValues() {
		return this.values;
	}

	public Filter setValues(List<String> values) {
		this.values = values;
		return this;
	}

	public boolean isSetValues() {
		return this.values != null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Filter(");
		boolean first = true;

		sb.append("name:");
		if (this.name == null) {
			sb.append("null");
		} else {
			sb.append(this.name);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("values:");
		if (this.values == null) {
			sb.append("null");
		} else {
			sb.append(this.values);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Filter)) {
			return false;
		}

		Filter filter = (Filter) o;
		return new EqualsBuilder()
			.append(name, filter.getName())
			.append(values, filter.getValues())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(name)
			.append(values)
			.toHashCode();
	}
}
