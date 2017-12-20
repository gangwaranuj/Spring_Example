package com.workmarket.search.response.work;

import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class DashboardStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	private String statusId;
	private String statusName;
	private int statusCount;
	private String colorRgb;
	private String statusDescription;
	private WorkSubStatusTypeCompanySetting.DashboardDisplayType dashboardDisplayType;

	public DashboardStatus() {
	}

	public String getStatusId() {
		return this.statusId;
	}

	public DashboardStatus setStatusId(String statusId) {
		this.statusId = statusId;
		return this;
	}

	public boolean isSetStatusId() {
		return this.statusId != null;
	}

	public String getStatusName() {
		return this.statusName;
	}

	public DashboardStatus setStatusName(String statusName) {
		this.statusName = statusName;
		return this;
	}

	public boolean isSetStatusName() {
		return this.statusName != null;
	}

	public int getStatusCount() {
		return this.statusCount;
	}

	public DashboardStatus setStatusCount(int statusCount) {
		this.statusCount = statusCount;
		return this;
	}

	public boolean isSetStatusCount() {
		return (statusCount > 0);
	}

	public String getColorRgb() {
		return this.colorRgb;
	}

	public DashboardStatus setColorRgb(String colorRgb) {
		this.colorRgb = colorRgb;
		return this;
	}

	public boolean isSetColorRgb() {
		return this.colorRgb != null;
	}

	public String getStatusDescription() {
		return this.statusDescription;
	}

	public DashboardStatus setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
		return this;
	}

	public boolean isSetStatusDescription() {
		return this.statusDescription != null;
	}

	public WorkSubStatusTypeCompanySetting.DashboardDisplayType getDashboardDisplayType() {
		return dashboardDisplayType;
	}

	public DashboardStatus setDashboardDisplayType(WorkSubStatusTypeCompanySetting.DashboardDisplayType dashboardDisplayType) {
		this.dashboardDisplayType = dashboardDisplayType;
		return this;
	}

	public boolean isSetDashboardDisplayType() {
		return dashboardDisplayType != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof DashboardStatus)
			return this.equals((DashboardStatus) that);
		return false;
	}

	private boolean equals(DashboardStatus that) {
		if (that == null)
			return false;

		boolean this_present_statusId = true && this.isSetStatusId();
		boolean that_present_statusId = true && that.isSetStatusId();
		if (this_present_statusId || that_present_statusId) {
			if (!(this_present_statusId && that_present_statusId))
				return false;
			if (!this.statusId.equals(that.statusId))
				return false;
		}

		boolean this_present_statusName = true && this.isSetStatusName();
		boolean that_present_statusName = true && that.isSetStatusName();
		if (this_present_statusName || that_present_statusName) {
			if (!(this_present_statusName && that_present_statusName))
				return false;
			if (!this.statusName.equals(that.statusName))
				return false;
		}

		boolean this_present_statusCount = true;
		boolean that_present_statusCount = true;
		if (this_present_statusCount || that_present_statusCount) {
			if (!(this_present_statusCount && that_present_statusCount))
				return false;
			if (this.statusCount != that.statusCount)
				return false;
		}

		boolean this_present_colorRgb = true && this.isSetColorRgb();
		boolean that_present_colorRgb = true && that.isSetColorRgb();
		if (this_present_colorRgb || that_present_colorRgb) {
			if (!(this_present_colorRgb && that_present_colorRgb))
				return false;
			if (!this.colorRgb.equals(that.colorRgb))
				return false;
		}

		boolean this_present_statusDescription = true && this.isSetStatusDescription();
		boolean that_present_statusDescription = true && that.isSetStatusDescription();
		if (this_present_statusDescription || that_present_statusDescription) {
			if (!(this_present_statusDescription && that_present_statusDescription))
				return false;
			if (!this.statusDescription.equals(that.statusDescription))
				return false;
		}

		boolean this_present_dashboardDisplayType = true && this.isSetDashboardDisplayType();
		boolean that_present_dashboardDisplayType = true && that.isSetDashboardDisplayType();
		if (this_present_dashboardDisplayType || that_present_dashboardDisplayType) {
			if (!(this_present_dashboardDisplayType && that_present_dashboardDisplayType))
				return false;
			if (!this.dashboardDisplayType.equals(that.dashboardDisplayType))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_statusId = true && (isSetStatusId());
		builder.append(present_statusId);
		if (present_statusId)
			builder.append(statusId);

		boolean present_statusName = true && (isSetStatusName());
		builder.append(present_statusName);
		if (present_statusName)
			builder.append(statusName);

		boolean present_statusCount = true;
		builder.append(present_statusCount);
		if (present_statusCount)
			builder.append(statusCount);

		boolean present_colorRgb = true && (isSetColorRgb());
		builder.append(present_colorRgb);
		if (present_colorRgb)
			builder.append(colorRgb);

		boolean present_statusDescription = true && (isSetStatusDescription());
		builder.append(present_statusDescription);
		if (present_statusDescription)
			builder.append(statusDescription);

		boolean present_dashboardDisplayType = true && (isSetDashboardDisplayType());
		builder.append(present_dashboardDisplayType);
		if (present_dashboardDisplayType)
			builder.append(dashboardDisplayType);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashboardStatus(");
		boolean first = true;

		sb.append("statusId:");
		if (this.statusId == null) {
			sb.append("null");
		} else {
			sb.append(this.statusId);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("statusName:");
		if (this.statusName == null) {
			sb.append("null");
		} else {
			sb.append(this.statusName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("statusCount:");
		sb.append(this.statusCount);
		first = false;
		if (isSetColorRgb()) {
			if (!first) sb.append(", ");
			sb.append("colorRgb:");
			if (this.colorRgb == null) {
				sb.append("null");
			} else {
				sb.append(this.colorRgb);
			}
			first = false;
		}
		if (isSetStatusDescription()) {
			if (!first) sb.append(", ");
			sb.append("statusDescription:");
			if (this.statusDescription == null) {
				sb.append("null");
			} else {
				sb.append(this.statusDescription);
			}
			first = false;
		}
		if (isSetDashboardDisplayType()) {
			if (!first) sb.append(", ");
			sb.append("dashboardDisplayType:");
			if (this.dashboardDisplayType == null) {
				sb.append("null");
			} else {
				sb.append(this.dashboardDisplayType);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}

