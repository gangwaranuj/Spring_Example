package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class CustomField implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String value;
	private String defaultValue;
	private boolean visibleToResource;
	private boolean visibleToOwner;
	private boolean isRequired;
	private String type;
	private boolean readOnly;
	private boolean showOnPrintout;
	private boolean showInAssignmentHeader;
	private boolean showOnSentStatus;

	public CustomField() {
	}

	public CustomField(
			long id,
			String name,
			String value,
			String defaultValue,
			boolean visibleToResource,
			boolean visibleToOwner,
			boolean isRequired,
			String type,
			boolean readOnly,
			boolean showOnPrintout,
			boolean showInAssignmentHeader) {
		this();
		this.id = id;
		this.name = name;
		this.value = value;
		this.defaultValue = defaultValue;
		this.visibleToResource = visibleToResource;
		this.visibleToOwner = visibleToOwner;
		this.isRequired = isRequired;
		this.type = type;
		this.readOnly = readOnly;
		this.showOnPrintout = showOnPrintout;
		this.showInAssignmentHeader = showInAssignmentHeader;
		this.showOnSentStatus = showOnSentStatus;
	}

	public long getId() {
		return this.id;
	}

	public CustomField setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getName() {
		return this.name;
	}

	public CustomField setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public String getValue() {
		return this.value;
	}

	public CustomField setValue(String value) {
		this.value = value;
		return this;
	}

	public boolean isSetValue() {
		return this.value != null;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public CustomField setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public boolean isSetDefaultValue() {
		return this.defaultValue != null;
	}

	public boolean isVisibleToResource() {
		return this.visibleToResource;
	}

	public CustomField setVisibleToResource(boolean visibleToResource) {
		this.visibleToResource = visibleToResource;
		return this;
	}

	public boolean isVisibleToOwner() {
		return this.visibleToOwner;
	}

	public CustomField setVisibleToOwner(boolean visibleToOwner) {
		this.visibleToOwner = visibleToOwner;
		return this;
	}

	public boolean isIsRequired() {
		return this.isRequired;
	}

	public CustomField setIsRequired(boolean isRequired) {
		this.isRequired = isRequired;
		return this;
	}

	public String getType() {
		return this.type;
	}

	public CustomField setType(String type) {
		this.type = type;
		return this;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	public CustomField setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	public boolean isShowOnPrintout() {
		return this.showOnPrintout;
	}

	public CustomField setShowOnPrintout(boolean showOnPrintout) {
		this.showOnPrintout = showOnPrintout;
		return this;
	}

	public boolean isShowInAssignmentHeader() {
		return showInAssignmentHeader;
	}

	public CustomField setShowInAssignmentHeader(boolean showInAssignmentHeader) {
		this.showInAssignmentHeader = showInAssignmentHeader;
		return this;
	}

	public boolean isShowOnSentStatus() {
		return showOnSentStatus;
	}

	public CustomField setShowOnSentStatus(boolean showOnSentStatus) {
		this.showOnSentStatus = showOnSentStatus;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof CustomField)
			return this.equals((CustomField) that);
		return false;
	}

	public boolean equals(CustomField that) {
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

		boolean this_present_value = true && this.isSetValue();
		boolean that_present_value = true && that.isSetValue();
		if (this_present_value || that_present_value) {
			if (!(this_present_value && that_present_value))
				return false;
			if (!this.value.equals(that.value))
				return false;
		}

		boolean this_present_defaultValue = true && this.isSetDefaultValue();
		boolean that_present_defaultValue = true && that.isSetDefaultValue();
		if (this_present_defaultValue || that_present_defaultValue) {
			if (!(this_present_defaultValue && that_present_defaultValue))
				return false;
			if (!this.defaultValue.equals(that.defaultValue))
				return false;
		}

		boolean this_present_visibleToResource = true;
		boolean that_present_visibleToResource = true;
		if (this_present_visibleToResource || that_present_visibleToResource) {
			if (!(this_present_visibleToResource && that_present_visibleToResource))
				return false;
			if (this.visibleToResource != that.visibleToResource)
				return false;
		}

		boolean this_present_visibleToOwner = true;
		boolean that_present_visibleToOwner = true;
		if (this_present_visibleToOwner || that_present_visibleToOwner) {
			if (!(this_present_visibleToOwner && that_present_visibleToOwner))
				return false;
			if (this.visibleToOwner != that.visibleToOwner)
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

		boolean this_present_type = true && this.isSetType();
		boolean that_present_type = true && that.isSetType();
		if (this_present_type || that_present_type) {
			if (!(this_present_type && that_present_type))
				return false;
			if (!this.type.equals(that.type))
				return false;
		}

		boolean this_present_readOnly = true;
		boolean that_present_readOnly = true;
		if (this_present_readOnly || that_present_readOnly) {
			if (!(this_present_readOnly && that_present_readOnly))
				return false;
			if (this.readOnly != that.readOnly)
				return false;
		}

		boolean this_present_showOnPrintout = true;
		boolean that_present_showOnPrintout = true;
		if (this_present_showOnPrintout || that_present_showOnPrintout) {
			if (!(this_present_showOnPrintout && that_present_showOnPrintout))
				return false;
			if (this.showOnPrintout != that.showOnPrintout)
				return false;
		}

		if (this.showInAssignmentHeader != that.showInAssignmentHeader)
			return false;

		if (this.showOnSentStatus != that.showOnSentStatus)
			return false;

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

		boolean present_value = true && (isSetValue());
		builder.append(present_value);
		if (present_value)
			builder.append(value);

		boolean present_defaultValue = true && (isSetDefaultValue());
		builder.append(present_defaultValue);
		if (present_defaultValue)
			builder.append(defaultValue);

		boolean present_visibleToResource = true;
		builder.append(present_visibleToResource);
		if (present_visibleToResource)
			builder.append(visibleToResource);

		boolean present_visibleToOwner = true;
		builder.append(present_visibleToOwner);
		if (present_visibleToOwner)
			builder.append(visibleToOwner);

		boolean present_isRequired = true;
		builder.append(present_isRequired);
		if (present_isRequired)
			builder.append(isRequired);

		boolean present_type = true && (isSetType());
		builder.append(present_type);
		if (present_type)
			builder.append(type);

		boolean present_readOnly = true;
		builder.append(present_readOnly);
		if (present_readOnly)
			builder.append(readOnly);

		boolean present_showOnPrintout = true;
		builder.append(present_showOnPrintout);
		if (present_showOnPrintout)
			builder.append(showOnPrintout);

		builder.append(true);
		builder.append(showInAssignmentHeader);

		builder.append(true);
		builder.append(showOnSentStatus);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("CustomField(");
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
		sb.append("value:");
		if (this.value == null) {
			sb.append("null");
		} else {
			sb.append(this.value);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("defaultValue:");
		if (this.defaultValue == null) {
			sb.append("null");
		} else {
			sb.append(this.defaultValue);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("visibleToResource:");
		sb.append(this.visibleToResource);
		first = false;
		if (!first) sb.append(", ");
		sb.append("visibleToOwner:");
		sb.append(this.visibleToOwner);
		first = false;
		if (!first) sb.append(", ");
		sb.append("isRequired:");
		sb.append(this.isRequired);
		first = false;
		if (!first) sb.append(", ");
		sb.append("type:");
		if (this.type == null) {
			sb.append("null");
		} else {
			sb.append(this.type);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("readOnly:");
		sb.append(this.readOnly);
		first = false;
		if (!first) sb.append(", ");
		sb.append("showOnPrintout:");
		sb.append(this.showOnPrintout);
		first = false;
		if (!first) sb.append(", showInAssignmentHeader");
		sb.append("showInAssignmentHeader:");
		sb.append(this.showInAssignmentHeader);
		first = false;
		if (!first) sb.append(", showOnSentStatus");
		sb.append("showOnSentStatus:");
		sb.append(this.showOnSentStatus);

		sb.append(")");
		return sb.toString();
	}
}