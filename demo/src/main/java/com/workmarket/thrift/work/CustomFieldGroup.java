package com.workmarket.thrift.work;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomFieldGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private List<CustomField> fields;
	private boolean isRequired;
	private Integer position;

	public CustomFieldGroup() {
	}

	public CustomFieldGroup(long id, String name, List<CustomField> fields, boolean isRequired) {
		this();
		this.id = id;
		this.name = name;
		this.fields = fields;
		this.isRequired = isRequired;
	}

	public long getId() {
		return this.id;
	}

	public CustomFieldGroup setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getName() {
		return this.name;
	}

	public CustomFieldGroup setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public Integer getPosition() {
		return (this.position != null) ? this.position : 0;
	}

	public CustomFieldGroup setPosition(Integer position) {
		this.position = position;
		return this;
	}

	public boolean isSetPosition() {
		return this.position != null;
	}

	public int getFieldsSize() {
		return (this.fields == null) ? 0 : this.fields.size();
	}

	public java.util.Iterator<CustomField> getFieldsIterator() {
		return (this.fields == null) ? null : this.fields.iterator();
	}

	public void addToFields(CustomField elem) {
		if (this.fields == null) {
			this.fields = new ArrayList<CustomField>();
		}
		this.fields.add(elem);
	}

	public List<CustomField> getFields() {
		return this.fields;
	}

	public CustomFieldGroup setFields(List<CustomField> fields) {
		this.fields = fields;
		return this;
	}

	public boolean hasFields() {
		return CollectionUtils.isNotEmpty(fields);
	}

	public boolean isIsRequired() {
		return this.isRequired;
	}

	public CustomFieldGroup setIsRequired(boolean isRequired) {
		this.isRequired = isRequired;
		return this;
	}

	public boolean hasRequiredResourceFields() {
		return CollectionUtils.isNotEmpty(fields) && Iterables.any(fields, new Predicate<CustomField>() {
			@Override public boolean apply(CustomField field) {
				return WorkCustomFieldType.RESOURCE.equals(field.getType()) && field.isIsRequired();
			}
		});
	}

	public boolean hasBuyerFields() {
		return CollectionUtils.isNotEmpty(fields) && Iterables.any(fields, new Predicate<CustomField>() {
			@Override public boolean apply(CustomField field) {
				return WorkCustomFieldType.OWNER.equals(field.getType());
			}
		});
	}

	public boolean hasResourceFields() {
		return CollectionUtils.isNotEmpty(fields) && Iterables.any(fields, new Predicate<CustomField>() {
			@Override public boolean apply(CustomField field) {
				return WorkCustomFieldType.RESOURCE.equals(field.getType());
			}
		});
	}

	public boolean hasBuyerFieldsVisibleToResourceOnSentStatus() {
		return CollectionUtils.isNotEmpty(fields) && Iterables.any(fields, new Predicate<CustomField>() {
			@Override public boolean apply(CustomField field) {
				return field.isShowOnSentStatus() || (field.isVisibleToResource() && WorkCustomFieldType.OWNER.equals(field.getType()));
			}
		});
	}

	/**
	 * returns true if the custom field group has at least one printable field
	 * @return
	 */
	public boolean isPrintable() {
		return CollectionUtils.isNotEmpty(fields) && Iterables.any(fields, new Predicate<CustomField>() {
			@Override public boolean apply(CustomField field) {
				return field.isShowOnPrintout();
			}
		});
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof CustomFieldGroup)
			return this.equals((CustomFieldGroup) that);
		return false;
	}

	private boolean equals(CustomFieldGroup that) {
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

		boolean this_present_name = true && this.isSetName();
		boolean that_present_name = true && that.isSetName();
		if (this_present_name || that_present_name) {
			if (!(this_present_name && that_present_name))
				return false;
			if (!this.name.equals(that.name))
				return false;
		}

		boolean this_present_fields = true && this.hasFields();
		boolean that_present_fields = true && that.hasFields();
		if (this_present_fields || that_present_fields) {
			if (!(this_present_fields && that_present_fields))
				return false;
			if (!this.fields.equals(that.fields))
				return false;
		}

		boolean this_present_isRequired = true;
		boolean that_present_isRequired = true;
		if (this_present_isRequired || that_present_isRequired) {
			if (!(this_present_isRequired && that_present_isRequired))
				return false;
			if (this.isRequired != that.isRequired)
				return false;
		}

		boolean this_present_position = true && this.isSetPosition();
		boolean that_present_position = true && that.isSetPosition();
		if (this_present_name || that_present_name) {
			if (!(this_present_name && that_present_name))
				return false;
			if (!this.position.equals(that.position))
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

		boolean present_name = true && (isSetName());
		builder.append(present_name);
		if (present_name)
			builder.append(name);

		boolean present_fields = true && (hasFields());
		builder.append(present_fields);
		if (present_fields)
			builder.append(fields);

		boolean present_isRequired = true;
		builder.append(present_isRequired);
		if (present_isRequired)
			builder.append(isRequired);

		boolean present_position = true && (isSetPosition());
		builder.append(present_position);
		if (present_position)
			builder.append(position);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("CustomFieldGroup(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("name:");
		if (this.name == null) {
			sb.append("null");
		} else {
			sb.append(this.name);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("fields:");
		if (this.fields == null) {
			sb.append("null");
		} else {
			sb.append(this.fields);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("isRequired:");
		sb.append(this.isRequired);
		first = false;
		if (!first) sb.append(", ");
		sb.append("position:");
		if (this.position == null) {
			sb.append("null");
		} else {
			sb.append(this.position);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
