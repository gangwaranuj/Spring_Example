package com.workmarket.data.report.work;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class CustomFieldReportRow implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long fieldId;
	private String fieldName;
	private String fieldValue;
	private String fieldDefaultValue;
	private boolean showOnDashboard;
	private boolean showOnSent;
	private boolean visibleToResource;

	public CustomFieldReportRow() {}
	
	public CustomFieldReportRow(Long id, String name) {
		this.fieldId = id;
		this.fieldName = name;
	}
	
	public CustomFieldReportRow(Long id, String name, String value) {
		this.fieldId = id;
		this.fieldName = name;
		this.fieldValue = value;
	}

	public CustomFieldReportRow(Long id, String name, String value, boolean showOnDashboard) {
		this.fieldId = id;
		this.fieldName = name;
		this.fieldValue = value;
		this.showOnDashboard = showOnDashboard;
	}
	
	public Long getFieldId() {
		return fieldId;
	}
	
	public CustomFieldReportRow setFieldId(Long fieldId) {
		this.fieldId = fieldId;
		return this;
	}
	
	public String getFieldName() {
		return fieldName;
	}

	public CustomFieldReportRow setFieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public CustomFieldReportRow setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
		return this;
	}

	public String getFieldDefaultValue() {
		return fieldDefaultValue;
	}

	public CustomFieldReportRow setFieldDefaultValue(String fieldDefaultValue) {
		this.fieldDefaultValue = fieldDefaultValue;
		return this;
	}

	public boolean isShowOnDashboard() {
		return showOnDashboard;
	}

	public CustomFieldReportRow setShowOnDashboard(boolean showOnDashboard) {
		this.showOnDashboard = showOnDashboard;
		return this;
	}

	public boolean isShowOnSent() {
		return showOnSent;
	}

	public CustomFieldReportRow setShowOnSent(boolean showOnSent) {
		this.showOnSent = showOnSent;
		return this;
	}

	public boolean isVisibleToResource() {
		return visibleToResource;
	}

	public CustomFieldReportRow setVisibleToResource(boolean visibleToResource) {
		this.visibleToResource = visibleToResource;
		return this;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder("CustomFieldReportRow[");
		sb.append("fieldId:" + fieldId);
		sb.append(", fieldName" + fieldName);
		sb.append(", fieldValue:" + fieldValue);
		sb.append("]");
		return sb.toString();
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;
		CustomFieldReportRow that = (CustomFieldReportRow) obj;

		return EqualsBuilder.reflectionEquals(this, that);
	}

	public int hashcode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
