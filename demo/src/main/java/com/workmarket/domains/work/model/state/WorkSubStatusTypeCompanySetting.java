package com.workmarket.domains.work.model.state;

import javax.persistence.*;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "workSubStatusTypeCompanySetting")
@Table(name = "work_sub_status_type_company_setting")
@AuditChanges
public class WorkSubStatusTypeCompanySetting extends AuditedEntity {

	private static final long serialVersionUID = -1046627868978745273L;

	public enum DashboardDisplayType {
		SHOW,
		HIDE,
		SHOW_IF_ACTIVE
	}

	public static final String DEFAULT_COLOR = "C2C2C2";

	private Company company;
	private WorkSubStatusType workSubStatusType;
	private String colorRgb = DEFAULT_COLOR;
	private DashboardDisplayType dashboardDisplayType = DashboardDisplayType.SHOW;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "work_sub_status_type_id", referencedColumnName = "id", updatable = false)
	public WorkSubStatusType getWorkSubStatusType() {
		return workSubStatusType;
	}

	public void setWorkSubStatusType(WorkSubStatusType workSubStatusType) {
		this.workSubStatusType = workSubStatusType;
	}

	@Column(name ="color_rgb", nullable = false, length = 6)
	public String getColorRgb() {
		return colorRgb;
	}

	public void setColorRgb(String colorRgb) {
		this.colorRgb = colorRgb;
	}

	@Column(name = "dashboard_display_type", nullable = false)
	@Enumerated(EnumType.STRING)
	public DashboardDisplayType getDashboardDisplayType() {
		return dashboardDisplayType;
	}

	public void setDashboardDisplayType(DashboardDisplayType dashboardDisplayType) {
		this.dashboardDisplayType = dashboardDisplayType;
	}
}
