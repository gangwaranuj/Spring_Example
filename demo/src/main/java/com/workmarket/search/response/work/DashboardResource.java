package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class DashboardResource implements Serializable {
	private static final long serialVersionUID = 1L;

	private long resourceId;
	private String resourceUserNumber;
	private String userName;
	private String resourceFirstName;
	private String resourceLastName;
	private long resourceCompanyId;
	private String resourceCompanyName;
	private String mobilePhone;
	private String workPhone;
	private boolean filteredOn = false;

	public DashboardResource() {
	}

	public long getResourceId() {
		return this.resourceId;
	}

	public DashboardResource setResourceId(long resourceId) {
		this.resourceId = resourceId;
		return this;
	}

	public boolean isSetResourceId() {
		return (resourceId > 0L);
	}

	public String getResourceUserNumber() {
		return this.resourceUserNumber;
	}

	public DashboardResource setResourceUserNumber(String resourceUserNumber) {
		this.resourceUserNumber = resourceUserNumber;
		return this;
	}

	public boolean isSetResourceUserNumber() {
		return this.resourceUserNumber != null;
	}

	public String getUserName() {
		return this.userName;
	}

	public DashboardResource setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public boolean isSetUserName() {
		return this.userName != null;
	}

	public String getResourceFirstName() {
		return this.resourceFirstName;
	}

	public DashboardResource setResourceFirstName(String resourceFirstName) {
		this.resourceFirstName = resourceFirstName;
		return this;
	}

	public boolean isSetResourceFirstName() {
		return this.resourceFirstName != null;
	}

	public String getResourceLastName() {
		return this.resourceLastName;
	}

	public DashboardResource setResourceLastName(String resourceLastName) {
		this.resourceLastName = resourceLastName;
		return this;
	}

	public boolean isSetResourceLastName() {
		return this.resourceLastName != null;
	}

	public String getResourceCompanyName() {
		return this.resourceCompanyName;
	}

	public DashboardResource setResourceCompanyName(String resourceCompanyName) {
		this.resourceCompanyName = resourceCompanyName;
		return this;
	}

	public boolean isSetResourceCompanyId() {
		return this.resourceCompanyId > 0;
	}

	public long getResourceCompanyId() {
		return this.resourceCompanyId;
	}

	public DashboardResource setResourceCompanyId(long resourceCompanyId) {
		this.resourceCompanyId = resourceCompanyId;
		return this;
	}

	public boolean isSetResourceCompanyName() {
		return this.resourceCompanyName != null;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public boolean isFilteredOn() {
		return filteredOn;
	}
	
	public void setFilteredOn(boolean filteredOn) {
		this.filteredOn = filteredOn;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		return that instanceof DashboardResource && this.equals((DashboardResource) that);
	}

	private boolean equals(DashboardResource that) {
		if (that == null)
			return false;

		if ((this.isSetResourceId() != that.isSetResourceId()) ||
			(this.isSetResourceId() && this.getResourceId() != that.getResourceId())) {
			return false;
		}

		if ((this.isSetResourceCompanyId() != that.isSetResourceCompanyId()) ||
			(this.isSetResourceCompanyId() && this.getResourceCompanyId() != that.getResourceCompanyId())) {
			return false;
		}

		if ((this.isSetResourceUserNumber() != that.isSetResourceUserNumber()) ||
			(this.isSetResourceUserNumber() && !this.getResourceUserNumber().equals(that.getResourceUserNumber()))) {
			return false;
		}

		if ((this.isSetUserName() != that.isSetUserName()) ||
			(this.isSetUserName() && !this.getUserName().equals(that.getUserName()))) {
			return false;
		}

		if ((this.isSetResourceFirstName() != that.isSetResourceFirstName()) ||
			(this.isSetResourceFirstName() && !this.getResourceFirstName().equals(that.getResourceFirstName()))) {
			return false;
		}

		if ((this.isSetResourceLastName() != that.isSetResourceLastName()) ||
			(this.isSetResourceLastName() && !this.getResourceLastName().equals(that.getResourceLastName()))) {
			return false;
		}

		if ((this.isSetResourceCompanyName() != that.isSetResourceCompanyName()) ||
			(this.isSetResourceCompanyName() && !this.getResourceCompanyName().equals(that.getResourceCompanyName()))) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(isSetResourceId());
		if (isSetResourceId())
			builder.append(resourceId);

		builder.append(isSetResourceUserNumber());
		if (isSetResourceUserNumber())
			builder.append(resourceUserNumber);

		builder.append(isSetUserName());
		if (isSetUserName())
			builder.append(userName);

		builder.append(isSetResourceFirstName());
		if (isSetResourceFirstName())
			builder.append(resourceFirstName);

		builder.append(isSetResourceLastName());
		if (isSetResourceLastName())
			builder.append(resourceLastName);

		builder.append(isSetResourceCompanyName());
		if (isSetResourceCompanyName())
			builder.append(resourceCompanyName);

		builder.append(isSetResourceCompanyId());
		if (isSetResourceCompanyId())
			builder.append(resourceCompanyId);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashboardResource(");

		if (isSetResourceId()) {
			appendStringField("resourceId", String.valueOf(resourceId), sb).append(", ");
		}
		appendStringField("resourceUserNumber", resourceUserNumber, sb).append(", ");
		appendStringField("userName", userName, sb);
		appendStringField("resourceFirstName", resourceFirstName, sb).append(", ");
		appendStringField("resourceLastName", resourceLastName, sb).append(", ");
		if (isSetResourceCompanyId()) {
			appendStringField("resourceCompanyId", String.valueOf(resourceCompanyId), sb).append(", ");
		}
		appendStringField("resourceCompanyName", resourceCompanyName, sb).append(")");
		return sb.toString();
	}

	private StringBuilder appendStringField(final String fieldName, final String fieldValue, final StringBuilder builder) {
		final String value = fieldValue == null ? "null" : fieldValue;
		builder.append(fieldName).append(":").append(value);
		return builder;
	}
}

