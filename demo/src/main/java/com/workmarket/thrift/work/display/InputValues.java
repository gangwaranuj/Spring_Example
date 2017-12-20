package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class InputValues implements Serializable {
	private static final long serialVersionUID = 1L;

	private String fromValue;
	private String toValue;
	private String contains;
	private List<String> fieldValue;
	private com.workmarket.thrift.work.report.RelationalOperatorThrift toOperator;
	private com.workmarket.thrift.work.report.RelationalOperatorThrift fromOperator;
	private com.workmarket.thrift.work.report.FilteringTypeThrift filteringTypeThrift;
	private com.workmarket.thrift.work.report.RelationalOperatorThrift relationalOperatorThrift;

	public InputValues() {
	}

	public InputValues(
			String fromValue,
			String toValue,
			String contains,
			List<String> fieldValue,
			com.workmarket.thrift.work.report.RelationalOperatorThrift toOperator,
			com.workmarket.thrift.work.report.RelationalOperatorThrift fromOperator,
			com.workmarket.thrift.work.report.FilteringTypeThrift filteringTypeThrift,
			com.workmarket.thrift.work.report.RelationalOperatorThrift relationalOperatorThrift) {
		this();
		this.fromValue = fromValue;
		this.toValue = toValue;
		this.contains = contains;
		this.fieldValue = fieldValue;
		this.toOperator = toOperator;
		this.fromOperator = fromOperator;
		this.filteringTypeThrift = filteringTypeThrift;
		this.relationalOperatorThrift = relationalOperatorThrift;
	}

	public String getFromValue() {
		return this.fromValue;
	}

	public InputValues setFromValue(String fromValue) {
		this.fromValue = fromValue;
		return this;
	}

	public boolean isSetFromValue() {
		return this.fromValue != null;
	}

	public String getToValue() {
		return this.toValue;
	}

	public InputValues setToValue(String toValue) {
		this.toValue = toValue;
		return this;
	}

	public boolean isSetToValue() {
		return this.toValue != null;
	}

	public String getContains() {
		return this.contains;
	}

	public InputValues setContains(String contains) {
		this.contains = contains;
		return this;
	}

	public boolean isSetContains() {
		return this.contains != null;
	}

	public List<String> getFieldValue() {
		return this.fieldValue;
	}

	public InputValues setFieldValue(List<String> fieldValue) {
		this.fieldValue = fieldValue;
		return this;
	}

	public boolean isSetFieldValue() {
		return this.fieldValue != null;
	}

	public com.workmarket.thrift.work.report.RelationalOperatorThrift getToOperator() {
		return this.toOperator;
	}

	public InputValues setToOperator(com.workmarket.thrift.work.report.RelationalOperatorThrift toOperator) {
		this.toOperator = toOperator;
		return this;
	}

	public boolean isSetToOperator() {
		return this.toOperator != null;
	}

	public com.workmarket.thrift.work.report.RelationalOperatorThrift getFromOperator() {
		return this.fromOperator;
	}

	public InputValues setFromOperator(com.workmarket.thrift.work.report.RelationalOperatorThrift fromOperator) {
		this.fromOperator = fromOperator;
		return this;
	}

	public boolean isSetFromOperator() {
		return this.fromOperator != null;
	}

	public com.workmarket.thrift.work.report.FilteringTypeThrift getFilteringTypeThrift() {
		return this.filteringTypeThrift;
	}

	public InputValues setFilteringTypeThrift(com.workmarket.thrift.work.report.FilteringTypeThrift filteringTypeThrift) {
		this.filteringTypeThrift = filteringTypeThrift;
		return this;
	}

	public boolean isSetFilteringTypeThrift() {
		return this.filteringTypeThrift != null;
	}

	public com.workmarket.thrift.work.report.RelationalOperatorThrift getRelationalOperatorThrift() {
		return this.relationalOperatorThrift;
	}

	public InputValues setRelationalOperatorThrift(com.workmarket.thrift.work.report.RelationalOperatorThrift relationalOperatorThrift) {
		this.relationalOperatorThrift = relationalOperatorThrift;
		return this;
	}

	public boolean isSetRelationalOperatorThrift() {
		return this.relationalOperatorThrift != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof InputValues)
			return this.equals((InputValues) that);
		return false;
	}

	private boolean equals(InputValues that) {
		if (that == null)
			return false;

		boolean this_present_fromValue = true && this.isSetFromValue();
		boolean that_present_fromValue = true && that.isSetFromValue();
		if (this_present_fromValue || that_present_fromValue) {
			if (!(this_present_fromValue && that_present_fromValue))
				return false;
			if (!this.fromValue.equals(that.fromValue))
				return false;
		}

		boolean this_present_toValue = true && this.isSetToValue();
		boolean that_present_toValue = true && that.isSetToValue();
		if (this_present_toValue || that_present_toValue) {
			if (!(this_present_toValue && that_present_toValue))
				return false;
			if (!this.toValue.equals(that.toValue))
				return false;
		}

		boolean this_present_contains = true && this.isSetContains();
		boolean that_present_contains = true && that.isSetContains();
		if (this_present_contains || that_present_contains) {
			if (!(this_present_contains && that_present_contains))
				return false;
			if (!this.contains.equals(that.contains))
				return false;
		}

		boolean this_present_fieldValue = true && this.isSetFieldValue();
		boolean that_present_fieldValue = true && that.isSetFieldValue();
		if (this_present_fieldValue || that_present_fieldValue) {
			if (!(this_present_fieldValue && that_present_fieldValue))
				return false;
			if (!this.fieldValue.equals(that.fieldValue))
				return false;
		}

		boolean this_present_toOperator = true && this.isSetToOperator();
		boolean that_present_toOperator = true && that.isSetToOperator();
		if (this_present_toOperator || that_present_toOperator) {
			if (!(this_present_toOperator && that_present_toOperator))
				return false;
			if (!this.toOperator.equals(that.toOperator))
				return false;
		}

		boolean this_present_fromOperator = true && this.isSetFromOperator();
		boolean that_present_fromOperator = true && that.isSetFromOperator();
		if (this_present_fromOperator || that_present_fromOperator) {
			if (!(this_present_fromOperator && that_present_fromOperator))
				return false;
			if (!this.fromOperator.equals(that.fromOperator))
				return false;
		}

		boolean this_present_filteringTypeThrift = true && this.isSetFilteringTypeThrift();
		boolean that_present_filteringTypeThrift = true && that.isSetFilteringTypeThrift();
		if (this_present_filteringTypeThrift || that_present_filteringTypeThrift) {
			if (!(this_present_filteringTypeThrift && that_present_filteringTypeThrift))
				return false;
			if (!this.filteringTypeThrift.equals(that.filteringTypeThrift))
				return false;
		}

		boolean this_present_relationalOperatorThrift = true && this.isSetRelationalOperatorThrift();
		boolean that_present_relationalOperatorThrift = true && that.isSetRelationalOperatorThrift();
		if (this_present_relationalOperatorThrift || that_present_relationalOperatorThrift) {
			if (!(this_present_relationalOperatorThrift && that_present_relationalOperatorThrift))
				return false;
			if (!this.relationalOperatorThrift.equals(that.relationalOperatorThrift))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_fromValue = true && (isSetFromValue());
		builder.append(present_fromValue);
		if (present_fromValue)
			builder.append(fromValue);

		boolean present_toValue = true && (isSetToValue());
		builder.append(present_toValue);
		if (present_toValue)
			builder.append(toValue);

		boolean present_contains = true && (isSetContains());
		builder.append(present_contains);
		if (present_contains)
			builder.append(contains);

		boolean present_fieldValue = true && (isSetFieldValue());
		builder.append(present_fieldValue);
		if (present_fieldValue)
			builder.append(fieldValue);

		boolean present_toOperator = true && (isSetToOperator());
		builder.append(present_toOperator);
		if (present_toOperator)
			builder.append(toOperator.getValue());

		boolean present_fromOperator = true && (isSetFromOperator());
		builder.append(present_fromOperator);
		if (present_fromOperator)
			builder.append(fromOperator.getValue());

		boolean present_filteringTypeThrift = true && (isSetFilteringTypeThrift());
		builder.append(present_filteringTypeThrift);
		if (present_filteringTypeThrift)
			builder.append(filteringTypeThrift.getValue());

		boolean present_relationalOperatorThrift = true && (isSetRelationalOperatorThrift());
		builder.append(present_relationalOperatorThrift);
		if (present_relationalOperatorThrift)
			builder.append(relationalOperatorThrift.getValue());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("InputValues(");
		boolean first = true;

		sb.append("fromValue:");
		if (this.fromValue == null) {
			sb.append("null");
		} else {
			sb.append(this.fromValue);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("toValue:");
		if (this.toValue == null) {
			sb.append("null");
		} else {
			sb.append(this.toValue);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("contains:");
		if (this.contains == null) {
			sb.append("null");
		} else {
			sb.append(this.contains);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("fieldValue:");
		if (this.fieldValue == null) {
			sb.append("null");
		} else {
			sb.append(this.fieldValue);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("toOperator:");
		if (this.toOperator == null) {
			sb.append("null");
		} else {
			sb.append(this.toOperator);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("fromOperator:");
		if (this.fromOperator == null) {
			sb.append("null");
		} else {
			sb.append(this.fromOperator);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("filteringTypeThrift:");
		if (this.filteringTypeThrift == null) {
			sb.append("null");
		} else {
			sb.append(this.filteringTypeThrift);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("relationalOperatorThrift:");
		if (this.relationalOperatorThrift == null) {
			sb.append("null");
		} else {
			sb.append(this.relationalOperatorThrift);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}