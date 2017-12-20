package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardResponseSidebar implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean showAllCompany;
	private Map<String, DashboardStatus> dashboardStatuses;
	private Map<String, DashboardStatus> dashboardSubStatuses;
	private Map<String, List<DashboardStatus>> dashboardClientSubStatusesByStatus;
	private Map<Long, DashboardResource> dashboardAssignedResources;

	public DashboardResponseSidebar() {
		this.showAllCompany = true;
	}

	public boolean isShowAllCompany() {
		return this.showAllCompany;
	}

	public DashboardResponseSidebar setShowAllCompany(boolean showAllCompany) {
		this.showAllCompany = showAllCompany;
		return this;
	}

	public void putToDashboardStatuses(String key, DashboardStatus val) {
		if (this.dashboardStatuses == null) {
			this.dashboardStatuses = new LinkedHashMap<String, DashboardStatus>();
		}
		this.dashboardStatuses.put(key, val);
	}

	public Map<String, DashboardStatus> getDashboardStatuses() {
		return this.dashboardStatuses;
	}

	public DashboardResponseSidebar setDashboardStatuses(Map<String, DashboardStatus> dashboardStatuses) {
		this.dashboardStatuses = dashboardStatuses;
		return this;
	}

	public boolean isSetDashboardStatuses() {
		return this.dashboardStatuses != null;
	}

	public void putToDashboardClientSubStatuses(String key, DashboardStatus val) {
		if (this.dashboardSubStatuses == null) {
			this.dashboardSubStatuses = new LinkedHashMap<String, DashboardStatus>();
		}
		this.dashboardSubStatuses.put(key, val);
	}

	public Map<String, DashboardStatus> getDashboardSubStatuses() {
		return this.dashboardSubStatuses;
	}

	public DashboardResponseSidebar setDashboardSubStatuses(Map<String, DashboardStatus> dashboardSubStatuses) {
		this.dashboardSubStatuses = dashboardSubStatuses;
		return this;
	}

	public boolean isSetDashboardClientSubStatuses() {
		return this.dashboardSubStatuses != null;
	}

	public int getDashboardAssignedResourcesSize() {
		return (this.dashboardAssignedResources == null) ? 0 : this.dashboardAssignedResources.size();
	}

	public void putToDashboardAssignedResources(Long key, DashboardResource val) {
		if (this.dashboardAssignedResources == null) {
			this.dashboardAssignedResources = new LinkedHashMap<Long, DashboardResource>();
		}
		this.dashboardAssignedResources.put(key, val);
	}

	public Map<Long, DashboardResource> getDashboardAssignedResources() {
		return this.dashboardAssignedResources;
	}

	public DashboardResponseSidebar setDashboardAssignedResources(Map<Long, DashboardResource> dashboardAssignedResources) {
		this.dashboardAssignedResources = dashboardAssignedResources;
		return this;
	}

	public boolean isSetDashboardAssignedResources() {
		return this.dashboardAssignedResources != null;
	}

	public int getDashboardClientSubStatusesByStatusSize() {
		return (this.dashboardClientSubStatusesByStatus == null) ? 0 : this.dashboardClientSubStatusesByStatus.size();
	}

	public void putToDashboardClientSubStatusesByStatus(String key, List<DashboardStatus> val) {
		if (this.dashboardClientSubStatusesByStatus == null) {
			this.dashboardClientSubStatusesByStatus = new LinkedHashMap<String, List<DashboardStatus>>();
		}
		this.dashboardClientSubStatusesByStatus.put(key, val);
	}

	public Map<String, List<DashboardStatus>> getDashboardClientSubStatusesByStatus() {
		return this.dashboardClientSubStatusesByStatus;
	}

	public DashboardResponseSidebar setDashboardClientSubStatusesByStatus(Map<String, List<DashboardStatus>> dashboardClientSubStatusesByStatus) {
		this.dashboardClientSubStatusesByStatus = dashboardClientSubStatusesByStatus;
		return this;
	}

	public boolean isSetDashboardClientSubStatusesByStatus() {
		return this.dashboardClientSubStatusesByStatus != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DashboardResponseSidebar)
			return this.equals((DashboardResponseSidebar) that);
		return false;
	}

	private boolean equals(DashboardResponseSidebar that) {
		if (that == null)
			return false;

		if (this.showAllCompany != that.showAllCompany)
			return false;

		boolean this_present_dashboardClientStatuses = true && this.isSetDashboardStatuses();
		boolean that_present_dashboardClientStatuses = true && that.isSetDashboardStatuses();
		if (this_present_dashboardClientStatuses || that_present_dashboardClientStatuses) {
			if (!(this_present_dashboardClientStatuses && that_present_dashboardClientStatuses))
				return false;
			if (!this.dashboardStatuses.equals(that.dashboardStatuses))
				return false;
		}

		boolean this_present_dashboardClientSubStatuses = true && this.isSetDashboardClientSubStatuses();
		boolean that_present_dashboardClientSubStatuses = true && that.isSetDashboardClientSubStatuses();
		if (this_present_dashboardClientSubStatuses || that_present_dashboardClientSubStatuses) {
			if (!(this_present_dashboardClientSubStatuses && that_present_dashboardClientSubStatuses))
				return false;
			if (!this.dashboardSubStatuses.equals(that.dashboardSubStatuses))
				return false;
		}

		boolean this_present_dashboardClientSubStatusesByStatus = true && this.isSetDashboardClientSubStatusesByStatus();
		boolean that_present_dashboardClientSubStatusesByStatus = true && that.isSetDashboardClientSubStatusesByStatus();
		if (this_present_dashboardClientSubStatusesByStatus || that_present_dashboardClientSubStatusesByStatus) {
			if (!(this_present_dashboardClientSubStatusesByStatus && that_present_dashboardClientSubStatusesByStatus))
				return false;
			if (!this.dashboardClientSubStatusesByStatus.equals(that.dashboardClientSubStatusesByStatus))
				return false;
		}

		boolean this_present_dashboardAssignedResources = true && this.isSetDashboardAssignedResources();
		boolean that_present_dashboardAssignedResources = true && that.isSetDashboardAssignedResources();
		if (this_present_dashboardAssignedResources || that_present_dashboardAssignedResources) {
			if (!(this_present_dashboardAssignedResources && that_present_dashboardAssignedResources))
				return false;
			if (!this.dashboardAssignedResources.equals(that.dashboardAssignedResources))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(true);
		builder.append(showAllCompany);

		boolean present_dashboardClientStatuses = true && (isSetDashboardStatuses());
		builder.append(present_dashboardClientStatuses);
		if (present_dashboardClientStatuses)
			builder.append(dashboardStatuses);

		boolean present_dashboardClientSubStatuses = true && (isSetDashboardClientSubStatuses());
		builder.append(present_dashboardClientSubStatuses);
		if (present_dashboardClientSubStatuses)
			builder.append(dashboardSubStatuses);

		boolean present_dashboardClientSubStatusesByStatus = true && (isSetDashboardClientSubStatusesByStatus());
		builder.append(present_dashboardClientSubStatusesByStatus);
		if (present_dashboardClientSubStatusesByStatus)
			builder.append(dashboardClientSubStatusesByStatus);

		boolean present_dashboardAssignedResources = true && (isSetDashboardAssignedResources());
		builder.append(present_dashboardAssignedResources);
		if (present_dashboardAssignedResources)
			builder.append(dashboardAssignedResources);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashboardResponseSidebar(");
		boolean first = true;

		sb.append("showAllCompany:");
		sb.append(this.showAllCompany);
		first = false;

		if (isSetDashboardStatuses()) {
			if (!first) sb.append(", ");
			sb.append("dashboardStatuses:");
			if (this.dashboardStatuses == null) {
				sb.append("null");
			} else {
				sb.append(this.dashboardStatuses);
			}
			first = false;
		}
		if (isSetDashboardClientSubStatuses()) {
			if (!first) sb.append(", ");
			sb.append("dashboardSubStatuses:");
			if (this.dashboardSubStatuses == null) {
				sb.append("null");
			} else {
				sb.append(this.dashboardSubStatuses);
			}
			first = false;
		}
		if (isSetDashboardClientSubStatusesByStatus()) {
			if (!first) sb.append(", ");
			sb.append("dashboardClientSubStatusesByStatus:");
			if (this.dashboardClientSubStatusesByStatus == null) {
				sb.append("null");
			} else {
				sb.append(this.dashboardClientSubStatusesByStatus);
			}
			first = false;
		}
		if (isSetDashboardAssignedResources()) {
			if (!first) sb.append(", ");
			sb.append("dashboardAssignedResources:");
			if (this.dashboardAssignedResources == null) {
				sb.append("null");
			} else {
				sb.append(this.dashboardAssignedResources);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}

