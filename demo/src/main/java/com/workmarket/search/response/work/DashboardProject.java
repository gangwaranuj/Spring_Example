package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class DashboardProject implements Serializable {
	private static final long serialVersionUID = 1L;

	private long projectId;
	private String projectName;

	public DashboardProject() {
	}

	public DashboardProject(long projectId, String projectName) {
		this();
		this.projectId = projectId;
		this.projectName = projectName;
	}

	public long getProjectId() {
		return this.projectId;
	}

	public DashboardProject setProjectId(long projectId) {
		this.projectId = projectId;
		return this;
	}

	public boolean isSetProjectId() {
		return (projectId > 0L);
	}

	public String getProjectName() {
		return this.projectName;
	}

	public DashboardProject setProjectName(String projectName) {
		this.projectName = projectName;
		return this;
	}

	public boolean isSetProjectName() {
		return this.projectName != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DashboardProject)
			return this.equals((DashboardProject) that);
		return false;
	}

	private boolean equals(DashboardProject that) {
		if (that == null)
			return false;

		boolean this_present_projectId = true;
		boolean that_present_projectId = true;
		if (this_present_projectId || that_present_projectId) {
			if (!(this_present_projectId && that_present_projectId))
				return false;
			if (this.projectId != that.projectId)
				return false;
		}

		boolean this_present_projectName = true && this.isSetProjectName();
		boolean that_present_projectName = true && that.isSetProjectName();
		if (this_present_projectName || that_present_projectName) {
			if (!(this_present_projectName && that_present_projectName))
				return false;
			if (!this.projectName.equals(that.projectName))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_projectId = true;
		builder.append(present_projectId);
		if (present_projectId)
			builder.append(projectId);

		boolean present_projectName = true && (isSetProjectName());
		builder.append(present_projectName);
		if (present_projectName)
			builder.append(projectName);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashboardProject(");
		boolean first = true;

		sb.append("projectId:");
		sb.append(this.projectId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("projectName:");
		if (this.projectName == null) {
			sb.append("null");
		} else {
			sb.append(this.projectName);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}

