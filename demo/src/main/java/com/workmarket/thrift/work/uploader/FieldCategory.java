package com.workmarket.thrift.work.uploader;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FieldCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;
	private String description;
	private int order;
	private List<FieldType> fieldTypes;

	public FieldCategory() {
	}

	public FieldCategory(String code, String description, int order) {
		this();
		this.code = code;
		this.description = description;
		this.order = order;
	}

	public String getCode() {
		return this.code;
	}

	public FieldCategory setCode(String code) {
		this.code = code;
		return this;
	}

	public boolean isSetCode() {
		return this.code != null;
	}

	public String getDescription() {
		return this.description;
	}

	public FieldCategory setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	public int getOrder() {
		return this.order;
	}

	public FieldCategory setOrder(int order) {
		this.order = order;
		return this;
	}

	public int getFieldTypesSize() {
		return (this.fieldTypes == null) ? 0 : this.fieldTypes.size();
	}

	public java.util.Iterator<FieldType> getFieldTypesIterator() {
		return (this.fieldTypes == null) ? null : this.fieldTypes.iterator();
	}

	public void addToFieldTypes(FieldType elem) {
		if (this.fieldTypes == null) {
			this.fieldTypes = new ArrayList<FieldType>();
		}
		this.fieldTypes.add(elem);
	}

	public List<FieldType> getFieldTypes() {
		return this.fieldTypes;
	}

	public FieldCategory setFieldTypes(List<FieldType> fieldTypes) {
		this.fieldTypes = fieldTypes;
		return this;
	}

	public boolean isSetFieldTypes() {
		return this.fieldTypes != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof FieldCategory)
			return this.equals((FieldCategory) that);
		return false;
	}

	private boolean equals(FieldCategory that) {
		if (that == null)
			return false;

		boolean this_present_code = true && this.isSetCode();
		boolean that_present_code = true && that.isSetCode();
		if (this_present_code || that_present_code) {
			if (!(this_present_code && that_present_code))
				return false;
			if (!this.code.equals(that.code))
				return false;
		}

		boolean this_present_description = true && this.isSetDescription();
		boolean that_present_description = true && that.isSetDescription();
		if (this_present_description || that_present_description) {
			if (!(this_present_description && that_present_description))
				return false;
			if (!this.description.equals(that.description))
				return false;
		}

		boolean this_present_order = true;
		boolean that_present_order = true;
		if (this_present_order || that_present_order) {
			if (!(this_present_order && that_present_order))
				return false;
			if (this.order != that.order)
				return false;
		}

		boolean this_present_fieldTypes = true && this.isSetFieldTypes();
		boolean that_present_fieldTypes = true && that.isSetFieldTypes();
		if (this_present_fieldTypes || that_present_fieldTypes) {
			if (!(this_present_fieldTypes && that_present_fieldTypes))
				return false;
			if (!this.fieldTypes.equals(that.fieldTypes))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_code = true && (isSetCode());
		builder.append(present_code);
		if (present_code)
			builder.append(code);

		boolean present_description = true && (isSetDescription());
		builder.append(present_description);
		if (present_description)
			builder.append(description);

		boolean present_order = true;
		builder.append(present_order);
		if (present_order)
			builder.append(order);

		boolean present_fieldTypes = true && (isSetFieldTypes());
		builder.append(present_fieldTypes);
		if (present_fieldTypes)
			builder.append(fieldTypes);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("FieldCategory(");
		boolean first = true;

		sb.append("code:");
		if (this.code == null) {
			sb.append("null");
		} else {
			sb.append(this.code);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("description:");
		if (this.description == null) {
			sb.append("null");
		} else {
			sb.append(this.description);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("order:");
		sb.append(this.order);
		first = false;
		if (isSetFieldTypes()) {
			if (!first) sb.append(", ");
			sb.append("fieldTypes:");
			if (this.fieldTypes == null) {
				sb.append("null");
			} else {
				sb.append(this.fieldTypes);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}