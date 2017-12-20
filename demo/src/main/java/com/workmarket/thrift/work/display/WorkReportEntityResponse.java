package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class WorkReportEntityResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String displayName;
	private String keyName;
	private com.workmarket.thrift.work.report.WorkReportColumnType workReportColumnType;
	private LocationOrderResponse locationOrderResponse;
	private boolean filterable;
	private boolean future;

	public WorkReportEntityResponse() {
	}

	public WorkReportEntityResponse(
			String displayName,
			String keyName,
			com.workmarket.thrift.work.report.WorkReportColumnType workReportColumnType,
			LocationOrderResponse locationOrderResponse,
			boolean filterable) {
		this();
		this.displayName = displayName;
		this.keyName = keyName;
		this.workReportColumnType = workReportColumnType;
		this.locationOrderResponse = locationOrderResponse;
		this.filterable = filterable;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public WorkReportEntityResponse setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public boolean isSetDisplayName() {
		return this.displayName != null;
	}

	public String getKeyName() {
		return this.keyName;
	}

	public WorkReportEntityResponse setKeyName(String keyName) {
		this.keyName = keyName;
		return this;
	}

	public boolean isSetKeyName() {
		return this.keyName != null;
	}

	public com.workmarket.thrift.work.report.WorkReportColumnType getWorkReportColumnType() {
		return this.workReportColumnType;
	}

	public WorkReportEntityResponse setWorkReportColumnType(com.workmarket.thrift.work.report.WorkReportColumnType workReportColumnType) {
		this.workReportColumnType = workReportColumnType;
		return this;
	}

	public boolean isSetWorkReportColumnType() {
		return this.workReportColumnType != null;
	}

	public LocationOrderResponse getLocationOrderResponse() {
		return this.locationOrderResponse;
	}

	public WorkReportEntityResponse setLocationOrderResponse(LocationOrderResponse locationOrderResponse) {
		this.locationOrderResponse = locationOrderResponse;
		return this;
	}

	public boolean isSetLocationOrderResponse() {
		return this.locationOrderResponse != null;
	}

	public boolean isFilterable() {
		return this.filterable;
	}

	public WorkReportEntityResponse setFilterable(boolean filterable) {
		this.filterable = filterable;
		return this;
	}

	public boolean isFuture() {
		return future;
	}

	public void setFuture(boolean future) {
		this.future = future;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkReportEntityResponse)
			return this.equals((WorkReportEntityResponse) that);
		return false;
	}

	private boolean equals(WorkReportEntityResponse that) {
		if (that == null)
			return false;

		boolean this_present_displayName = true && this.isSetDisplayName();
		boolean that_present_displayName = true && that.isSetDisplayName();
		if (this_present_displayName || that_present_displayName) {
			if (!(this_present_displayName && that_present_displayName))
				return false;
			if (!this.displayName.equals(that.displayName))
				return false;
		}

		boolean this_present_keyName = true && this.isSetKeyName();
		boolean that_present_keyName = true && that.isSetKeyName();
		if (this_present_keyName || that_present_keyName) {
			if (!(this_present_keyName && that_present_keyName))
				return false;
			if (!this.keyName.equals(that.keyName))
				return false;
		}

		boolean this_present_workReportColumnType = true && this.isSetWorkReportColumnType();
		boolean that_present_workReportColumnType = true && that.isSetWorkReportColumnType();
		if (this_present_workReportColumnType || that_present_workReportColumnType) {
			if (!(this_present_workReportColumnType && that_present_workReportColumnType))
				return false;
			if (!this.workReportColumnType.equals(that.workReportColumnType))
				return false;
		}

		boolean this_present_locationOrderResponse = true && this.isSetLocationOrderResponse();
		boolean that_present_locationOrderResponse = true && that.isSetLocationOrderResponse();
		if (this_present_locationOrderResponse || that_present_locationOrderResponse) {
			if (!(this_present_locationOrderResponse && that_present_locationOrderResponse))
				return false;
			if (!this.locationOrderResponse.equals(that.locationOrderResponse))
				return false;
		}

		if (this.filterable != that.filterable)
			return false;

		if (this.future != that.future)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_displayName = true && (isSetDisplayName());
		builder.append(present_displayName);
		if (present_displayName)
			builder.append(displayName);

		boolean present_keyName = true && (isSetKeyName());
		builder.append(present_keyName);
		if (present_keyName)
			builder.append(keyName);

		boolean present_workReportColumnType = true && (isSetWorkReportColumnType());
		builder.append(present_workReportColumnType);
		if (present_workReportColumnType)
			builder.append(workReportColumnType.getValue());

		boolean present_locationOrderResponse = true && (isSetLocationOrderResponse());
		builder.append(present_locationOrderResponse);
		if (present_locationOrderResponse)
			builder.append(locationOrderResponse);

		boolean present_filterable = true;
		builder.append(present_filterable);
		if (present_filterable)
			builder.append(filterable);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkReportEntityResponse(");
		boolean first = true;

		sb.append("displayName:");
		if (this.displayName == null) {
			sb.append("null");
		} else {
			sb.append(this.displayName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("keyName:");
		if (this.keyName == null) {
			sb.append("null");
		} else {
			sb.append(this.keyName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("workReportColumnType:");
		if (this.workReportColumnType == null) {
			sb.append("null");
		} else {
			sb.append(this.workReportColumnType);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("locationOrderResponse:");
		if (this.locationOrderResponse == null) {
			sb.append("null");
		} else {
			sb.append(this.locationOrderResponse);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("filterable:");
		sb.append(this.filterable);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}